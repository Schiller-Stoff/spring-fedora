package org.sebi.springfedora.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SetupService implements ISetupService {
  
  private IResourceRepository<Resource> resourceRepository;

  public SetupService(IResourceRepository<Resource> resourceRepository){
    this.resourceRepository = resourceRepository;
  }
  
  @Transactional
  @Override
  public void createBaseResources() {

    List<Resource> resourceList = Arrays.asList(
      new Resource("http://localhost:8082/rest/objects", ""),
      new Resource("http://localhost:8082/rest/aggregations", ""),
      new Resource("http://localhost:8082/rest/aggregations/context", ""),
      new Resource("http://localhost:8082/rest/aggregations/corpus", ""),
      new Resource("http://localhost:8082/rest/aggregations/query", ""),
      new Resource("http://localhost:8082/rest/cm4f", ""),
      new Resource("http://localhost:8082/rest/cm4f/defaults", "")
      // new Resource("http://localhost:8082/rest/cm4f/defaults/sampleText9", "", MimeType.valueOf("text/plain"), "".getBytes())
    );

    resourceList.forEach(resource -> {
      if(resourceRepository.existsById(resource.getPath())){
        log.debug("Bootstrap: Skipping resource creation because already existing at uri: {}" , resource.getPath()); 
      } else {
        resourceRepository.save(resource);
      }
    });

  }

  @Override
  public void createPrototypes() {
    
    throw new NotImplementedException("METHOD NOT IMPLEMENTED YET");
    
  }

}
