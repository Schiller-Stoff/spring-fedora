package org.sebi.springfedora.service;

import java.util.ArrayList;
import java.util.Optional;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
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

  @Override
  public DigitalObject[] findAll() throws ResourceRepositoryException {

    ArrayList<DigitalObject> result = new ArrayList<DigitalObject>();
    this.digitalObjectRepository.findAll().forEach(result::add);
    return result.toArray(new DigitalObject[result.size()]);

  }

}
