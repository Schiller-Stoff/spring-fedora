package org.sebi.springfedora.service;

import java.io.IOException;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.repository.utils.FedoraMetadata;
import org.sebi.springfedora.service.utils.ContentModelUtils;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles base content model related operations.
 * Like creating a digital object from a content model. 
 */
@Slf4j
@Service
public class ContentModelService {

  private DOResourceMapper doResourceMapper;
  private IDigitalObjectRepository digitalObjectRepository;

  public ContentModelService(DOResourceMapper doResourceMapper, IDigitalObjectRepository digitalObjectRepository){
    this.doResourceMapper = doResourceMapper;
    this.digitalObjectRepository = digitalObjectRepository;
  }
  
  @Transactional
  public DigitalObject createFromPrototypeByModel(String pid, String contentModel) throws ResourceRepositoryException {

    String protoPid = ContentModelUtils.resolveModelPid(contentModel);

    Optional<DigitalObject> prototypeProto = digitalObjectRepository.findById(protoPid);
    
    if(!prototypeProto.isPresent()){
      String msg = String.format("Couldn't find prototype with pid %s needed to create digital object for content model: %s", protoPid, contentModel);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.NOT_FOUND.value(), msg);
    }

    if(digitalObjectRepository.existsById(pid)){
      String msg = String.format("Object with pid %s to be created from prototype %s for content model %s already exists.", pid, protoPid, contentModel);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }

    DigitalObject prototype = prototypeProto.get();

    // replace pid in rdf
    String clonedRdf = prototype.getRdfXml();
    String mappedDOPath = doResourceMapper.mapObjectResourcePath(pid);

    log.info("Request against protoype {} succesfully for creation of object with pid {}", protoPid, mappedDOPath);
    
    /**
     * From here processing + building of xml based RDF.
     * (remove system triples)
     */
    try {
      FedoraMetadata newMetadata = new FedoraMetadata(clonedRdf)
        .removeFedoraSystemTriples()
        .replaceResourcePath(mappedDOPath);

      String newRdf = newMetadata.serializeToString();

      DigitalObject newDigitalObject = new DigitalObject(pid, mappedDOPath, newRdf);

      return digitalObjectRepository.save(newDigitalObject);

    } catch(SAXException | IOException | ParserConfigurationException e){
      String msg = String.format("Failed to process xml from prototype %s for digital object %s. Starting from prototype rdf metadata: %s", protoPid, pid, clonedRdf);
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
  }

}
