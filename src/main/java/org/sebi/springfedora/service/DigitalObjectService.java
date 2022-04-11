package org.sebi.springfedora.service;

import java.util.Optional;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.utils.Rename;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DigitalObjectService implements IDigitalObjectService {
	
  private IResourceRepository resourceRepository;	
	
  public DigitalObjectService(IResourceRepository resourceRepository ) {
	  
    this.resourceRepository = resourceRepository;
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid) {
    return this.createDigitalObjectByPid(pid, "");
  }

  @Override
  public DigitalObject createDigitalObjectByPid(String pid, String rdf) {
    String mappedFedora6Path = Rename.rename(pid);
  
    String resourcePath = "http://localhost:8082/rest/" + mappedFedora6Path; 

    Resource resource = new Resource(resourcePath, rdf);

    Resource savedResource = this.resourceRepository.save(resource);

    if(savedResource != null){
      log.info("Succesfully building digital object with pid: {}, with resource with path: ", pid, resourcePath);
      return new DigitalObject(pid, resource);
    } else {
      return null;
    }
  }

  @Override
  public DigitalObject findDigitalObjectByPid(String pid) {
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
    log.error("### TRYNA FIND: " + uri);
    Optional<Resource> optional =  resourceRepository.findById(uri);
    Resource resource = optional.orElseThrow();
    DigitalObject digitalObject = new DigitalObject(pid, resource);

    return digitalObject;
  }

  @Override
  public DigitalObject deleteDigitalObjectByPid(String pid) {
    DigitalObject digitalObject = this.findDigitalObjectByPid(pid);
    this.resourceRepository.deleteById(digitalObject.getResource().getPath());

    // this will ensure that the tombstone from fedora is also deleted.
    this.resourceRepository.deleteById(digitalObject.getResource().getPath() + "/fcr:tombstone");
    
    return digitalObject;
  }

}
