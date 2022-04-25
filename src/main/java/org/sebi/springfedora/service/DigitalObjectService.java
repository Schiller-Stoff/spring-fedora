package org.sebi.springfedora.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.utils.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DigitalObjectService implements IDigitalObjectService {
	
  private IResourceRepository resourceRepository;	
  private IDigitalObjectRepository digitalObjectRepository;

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;
	
  public DigitalObjectService(IResourceRepository resourceRepository, IDigitalObjectRepository digitalObjectRepository ) {
    this.resourceRepository = resourceRepository;
    this.digitalObjectRepository = digitalObjectRepository;
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid) throws ResourceRepositoryException {
    return this.createDigitalObjectByPid(pid, "");
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid, String rdf) throws ResourceRepositoryException {
    String mappedFedora6Path = this.mapPidToResourcePath(pid);
  
    String resourcePath = "http://localhost:8082/rest/" + mappedFedora6Path; 

    // check if resource already exists
    if(resourceRepository.existsById(resourcePath)) {
      String msg = String.format("Creation of object failed. Resource already exists for object with pid: %s . Found existing resource path: %s", pid, resourcePath);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }

    Resource resource = new Resource(resourcePath, rdf);

    this.resourceRepository.save(resource);
    
    return new DigitalObject(pid, resourcePath, rdf);
  }

  @Override
  public DigitalObject findDigitalObjectByPid(String pid) throws ResourceRepositoryException {
    // TODO Auto-generated method stub

    // repository layer: request against fedora
    // getting the Resource
    // need to be returned as DigitalObject / DataStream?

    final String PROTOCOL = "http://";
    final String HOST_NAME = "localhost";
    final String PORT = "8082";
    final String FC_REPO_REST = "/rest";

    String mappedPath = Rename.rename(pid);


    String uri = PROTOCOL + HOST_NAME + ":" + PORT + FC_REPO_REST + "/" + mappedPath;
    Optional<Resource> optional =  resourceRepository.findById(uri);
    

    if(optional.isPresent()){
      Resource resource = optional.orElseThrow();
      DigitalObject digitalObject = new DigitalObject(pid, resource.getPath(), resource.getRdfXml(), resource.getChildren());
      log.info("Found digital object with pid: {}. Related resource path: {}", pid, resource.getPath());
      return digitalObject;
    } else {
      String msg = String.format("Couldn't find object with pid %s . Tried to GET from ResourceRepository path: %s", pid, uri);
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
  public DigitalObject updateDatastreamByPid(String pid, String sparql) throws ResourceRepositoryException {

    //URI uri = new URI("bla");

    DigitalObject digitalObject = this.findDigitalObjectByPid(pid);

    this.resourceRepository.updateResourceTriples(digitalObject.getPath(), sparql);

    return digitalObject;
  }

  /**
   * Checks if the digital object exists.
   * (If for the pid exists a resource with mapped resource path)
   */
  @Override
  public boolean checkIfExists(String pid){
    String resourcePath = this.mapObjectResourcePath(pid);
    return resourceRepository.existsById(resourcePath);
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

}
