package com.zim.geosparql.bootstrap;

import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // tells spring to be a spring bean.
public class DigitalObjectInitializer implements CommandLineRunner {
  
  //private final IGEOSPARQLService rdf4JService;
  private final IDigitalObjectService DOService;


  public DigitalObjectInitializer(IDigitalObjectService DOService) {
     this.DOService = DOService;
  }

  @Override
  public void run(String... args) throws Exception {
    log.debug("Bootstraping geo data...");
    //rdf4JService.startServer();
    //rdf4JService.loadTestData();
  }

}
