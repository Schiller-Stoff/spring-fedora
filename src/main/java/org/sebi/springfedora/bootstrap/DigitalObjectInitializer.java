package org.sebi.springfedora.bootstrap;

import org.sebi.springfedora.repository.IDigitalObjectRepository;
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


  public DigitalObjectInitializer(IDigitalObjectService DOService, IDigitalObjectRepository dObjectRepository) {
     this.DOService = DOService;
     this.dObjectRepository = dObjectRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    log.debug("Bootstraping geo data...");
    //rdf4JService.startServer();
    //rdf4JService.loadTestData();
  }

  private void initConnections(){
    
  }

}
