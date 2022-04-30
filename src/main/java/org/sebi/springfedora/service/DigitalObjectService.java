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
import org.sebi.springfedora.repository.utils.FedoraMetadata;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.sebi.springfedora.utils.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  @Override
  public DigitalObject createDigitalObjectByPid(String pid, @Nullable String rdf) throws ResourceRepositoryException {
    
    String resourcePath = doResourceMapper.mapObjectResourcePath(pid); 

    // check if resource already exists
    if(digitalObjectRepository.existsById(pid)) {
      String msg = String.format("Creation of object failed. Resource already exists for object with pid: %s . Found existing resource path: %s", pid, resourcePath);
      log.error(msg);
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

  @Transactional
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

  @Transactional(rollbackFor = Exception.class)
  public DigitalObject createTrans(String pid) throws ResourceRepositoryException {

    String path = doResourceMapper.mapObjectResourcePath(pid);

    Optional<DigitalObject> dObject = digitalObjectRepository.findById(pid);

    if(dObject.isPresent()){
      String msg = String.format("Object with pid %s already exists. Found existing resource with path %s", pid, path);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }
 
    DigitalObject digitalObject = new DigitalObject(pid, path, null);
    return digitalObjectRepository.save(digitalObject);
  }

}
