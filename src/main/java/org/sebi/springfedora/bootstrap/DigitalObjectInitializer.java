package org.sebi.springfedora.bootstrap;

import java.util.Arrays;
import java.util.List;

import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IDigitalObjectRepository;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// @Profile("none")
@Slf4j
@Component // tells spring to be a spring bean.
public class DigitalObjectInitializer implements CommandLineRunner {

  private final IDigitalObjectRepository dObjectRepository;
  private final IDigitalObjectService DOService;
  private final IResourceRepository resourceRepository;

  public DigitalObjectInitializer(IDigitalObjectService DOService, IDigitalObjectRepository dObjectRepository, IResourceRepository resourceRepository) {
    this.DOService = DOService;
    this.dObjectRepository = dObjectRepository;
    this.resourceRepository = resourceRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    log.debug("Bootstraping fedora spring stuff...");
    
    setupFedoraPrototypes();
  }

  private void initConnections() {

    

  }

  private void setupFedoraPrototypes(){

    // create basic resources
    List<Resource> resourceList = Arrays.asList(
      new Resource("http://localhost:8082/rest/objects", ""),
      new Resource("http://localhost:8082/rest/aggregations", ""),
      new Resource("http://localhost:8082/rest/aggregations/context", ""),
      new Resource("http://localhost:8082/rest/aggregations/corpus", ""),
      new Resource("http://localhost:8082/rest/aggregations/query", ""),
      new Resource("http://localhost:8082/rest/cm4f", ""),
      new Resource("http://localhost:8082/rest/cm4f/defaults", "")
    );
    
    resourceList.forEach(resource -> {
      if(resourceRepository.existsById(resource.getPath())){
        log.debug("Bootstrap: Skipping resource creation because already existing at uri: {}" , resource.getPath()); 
      } else {
        resourceRepository.save(resource);
      }
    });

    // (at johannes code: loop through folder in apache and create objects accordingly)

    // create digital objects
    List<String> digitalObjectPids = Arrays.asList(
      "o:derla.sty",
      "o:cantus.regensburg"
    );

    digitalObjectPids.forEach(pid -> DOService.createDigitalObjectByPid(pid));


  }

}
