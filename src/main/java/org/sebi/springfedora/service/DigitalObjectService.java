package org.sebi.springfedora.service;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.sebi.springfedora.utils.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DigitalObjectService implements IDigitalObjectService {
	
  private IResourceRepository resourceRepository;	
  private IDigitalObjectRepository digitalObjectRepository;

  private DOResourceMapper doResourceMapper;

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;
	
  public DigitalObjectService(IResourceRepository resourceRepository, IDigitalObjectRepository digitalObjectRepository, DOResourceMapper doResourceMapper ) {
    this.resourceRepository = resourceRepository;
    this.digitalObjectRepository = digitalObjectRepository;
    this.doResourceMapper = doResourceMapper;
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid) throws ResourceRepositoryException {
    return this.createDigitalObjectByPid(pid, null);
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid, @Nullable String rdf) throws ResourceRepositoryException {
    
    String resourcePath = doResourceMapper.mapObjectResourcePath(pid); 

    // check if resource already exists
    if(this.checkIfExists(pid)) {
      String msg = String.format("Creation of object failed. Resource already exists for object with pid: %s . Found existing resource path: %s", pid, resourcePath);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }

    DigitalObject digitalObject = new DigitalObject(pid, resourcePath, rdf);

    log.info("Trying to save now digital object with pid: {} - at path: {} - with rdf: {}", pid, resourcePath, rdf);
    this.digitalObjectRepository.save(digitalObject);
    return digitalObject;
  }

  @Override
  public DigitalObject findDigitalObjectByPid(String pid) throws ResourceRepositoryException {
    // TODO Auto-generated method stub

    // repository layer: request against fedora
    // getting the Resource
    // need to be returned as DigitalObject / DataStream?


    Optional<DigitalObject> optional =  digitalObjectRepository.findById(pid);
    
    if(optional.isPresent()){
      log.info("Found digital object with pid: {}", pid);
      return optional.get();
    } else {
      String msg = String.format("Couldn't find object with pid %s - should be at path: %s", pid, doResourceMapper.mapObjectResourcePath(pid));
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.NOT_FOUND.value(), msg);
    }

  }

  @Override
  public DigitalObject deleteDigitalObjectByPid(String pid) throws ResourceRepositoryException {
    DigitalObject digitalObject = this.findDigitalObjectByPid(pid);
    this.resourceRepository.deleteById(digitalObject.getPath());

    // this will ensure that the tombstone from fedora is also deleted.
    this.resourceRepository.deleteById(digitalObject.getPath() + "/fcr:tombstone");
    
    return digitalObject;
  }

  @Override
  public DigitalObject updateMetadataByPid(String pid, String sparql) throws ResourceRepositoryException {

    DigitalObject digitalObject = this.findDigitalObjectByPid(pid);

    this.resourceRepository.updateResourceTriples(digitalObject.getPath(), sparql);

    return digitalObject;
  }

  /**
   * Checks if the digital object exists.
   * (If for the pid exists a resource with mapped resource path)
   */
  @Override
  public boolean checkIfExists(String pid) throws ResourceRepositoryException {
    return digitalObjectRepository.existsById(pid);
  }

  /**
   * Maps pid to fedora resource path part e.g. like o:derla.sty to /objects/derla.sty.
   * DOES NOT apply the full resource address like https://localhost:8080/rest/objects/derla.sty
   */
  @Override
  public String mapPidToResourcePath(String pid){
    return Rename.rename(pid);
  }

  /**
   * Maps given pid to full resource address like o:derla.sty to like https://localhost:8080/rest/objects/derla.sty
   * dependent on spring environment variables.
   */
  @Override
  public String mapObjectResourcePath(String pid) {
    
    String mappedPid = mapPidToResourcePath(pid);
    String resourcePath = curHost + fedoraRESTEndpoint + mappedPid;

    return resourcePath;
  }

  @Override
  public DigitalObject[] findAll() throws ResourceRepositoryException {

    ArrayList<DigitalObject> result = new ArrayList<DigitalObject>();
    this.digitalObjectRepository.findAll().forEach(result::add);
    return result.toArray(new DigitalObject[result.size()]);

  }

  @Override
  public DigitalObject createFromPrototypeByPid(String pid, String protoPid) throws ResourceRepositoryException {

    DigitalObject prototype = this.findDigitalObjectByPid(protoPid);

    String clonedRdf = prototype.getRdfXml();


    String mappedProtoPath = doResourceMapper.mapObjectResourcePath(protoPid);
    String mappedDOPath = doResourceMapper.mapObjectResourcePath(pid);

    log.info("Request against protoype {} succesfully. Replacing now {} through {}", protoPid, mappedProtoPath, mappedDOPath);

    // replacements for prototype
    clonedRdf = clonedRdf.replaceAll(mappedProtoPath, mappedDOPath);
    

    /**
     * From here processing + building of xml based RDF.
     */

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(IOUtils.toInputStream(clonedRdf, "utf-8"));

      doc.getDocumentElement().normalize();

      Node rdfDescription = doc.getElementsByTagName("rdf:Description").item(0);

      NodeList children = rdfDescription.getChildNodes();


      String[] forbiddenURIs = {"http://fedora.info/definitions/v4/repository#Resource", "http://www.w3.org/ns/ldp#BasicContainer", "http://www.w3.org/ns/ldp#Resource", "http://www.w3.org/ns/ldp#RDFSource","http://www.w3.org/ns/ldp#Container","http://fedora.info/definitions/v4/repository#Container"};

      for (int i = 0; i < children.getLength(); i++){
        Node child = children.item(i);
        
        if(child.getNodeName() == "#text")continue;

        log.debug(child.getNodeName());
        if(child.getNodeName().contains("fedora:")) {
          rdfDescription.removeChild(child);
          continue;
        };

        if(child.getNodeName() == "rdf:type"){

          String curVal = child.getAttributes().getNamedItem("rdf:resource").getNodeValue();

         if(Arrays.asList(forbiddenURIs).contains(curVal)) rdfDescription.removeChild(child);


        }
        
      }

      log.info("Succesfully removed system properties from requested prototype digital object {} for {} Trying to send xml: {}", protoPid, pid, documentToString(doc));
      return this.createDigitalObjectByPid(pid, documentToString(doc));

    } catch (SAXException | IOException | ParserConfigurationException e) {
      String msg = String.format("Failed to process xml from prototype %s for digital object %s. Starting from prototype rdf metadata: %s", protoPid, pid, clonedRdf);
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } 
    


    

    //return this.createDigitalObjectByPid(pid, clonedRdf);
    
  }


  public static String documentToString(Document doc) {
    try {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    } catch (Exception ex) {
        throw new RuntimeException("Error converting to String", ex);
    }
}

}
