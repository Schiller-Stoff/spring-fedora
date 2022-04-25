package org.sebi.springfedora.bootstrap;

import java.util.Arrays;
import java.util.List;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.service.IDatastreamService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

// @Profile("none")
@Slf4j
@Component // tells spring to be a spring bean.
public class DigitalObjectInitializer implements CommandLineRunner {

  private final IDigitalObjectService DOService;
  private final IResourceRepository resourceRepository;
  private final IDatastreamService datastreamService;

  public DigitalObjectInitializer(IDigitalObjectService DOService, IResourceRepository resourceRepository, IDatastreamService datastreamService) {
    this.DOService = DOService;
    this.resourceRepository = resourceRepository;
    this.datastreamService = datastreamService;
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
      // new Resource("http://localhost:8082/rest/cm4f/defaults/sampleText9", "", MimeType.valueOf("text/plain"), "".getBytes())
    );
    
    resourceList.forEach(resource -> {
      
      if(resourceRepository.existsById(resource.getPath())){
        log.debug("Bootstrap: Skipping resource creation because already existing at uri: {}" , resource.getPath()); 
      } else {
        resourceRepository.save(resource);
      }
    });

    // (at johannes code: loop through folder in apache and create objects accordingly)

    // creation of prototypes
    // (without looping through apache folder)
    List<String> prototypes = Arrays.asList(
      "o:prototype.latex",
      "o:prototype.lido",
      "o:prototype.gml",
      "o:prototype.mei",
      "o:prototype.context",
      "o:prototype.tei",
      "o:prototype.query"
    );

    // creation of system objects (cirilo properties / initializer etc.)
    List<String> sysObjs = Arrays.asList(
      "cirilo:Backbone",
      "cirilo:ontology",
      "cirilo:TEI",
      "cirilo:MEI"
    );

    // create some dummy digital objects
    // with initializer for these
    List<String> digitalObjectPids = Arrays.asList(
      "cirilo:derla",
      "cirilo:gml.derla",
      "cirilo:tei.derla",
      "cirilo:cantus",
      "cirilo:TEI.cantus",
      "o:cantus.regensburg",
      "o:derla.sty",
      "context:derla",
      "o:derla.sty3",
      "query:derla.all",
      "corpus:derla.words"
    );

    prototypes.forEach(pid -> this.createDigitalObject(pid));
    sysObjs.forEach(pid -> this.createDigitalObject(pid));
    digitalObjectPids.forEach(pid -> this.createDigitalObject(pid));


    /**
     * Creation of datastreams
     */
    this.createDatastream("o:derla.sty", "SOME_TEXT");
    this.createDatastream("o:derla.sty", "DEMO_TEXT");

  }

  private void createDigitalObject(String pid){
    try {
      this.DOService.createDigitalObjectByPid(pid);
    } catch ( ResourceRepositoryException e){
      //skip already existing
    }
  }

  private void createDatastream(String pid, String dsid){
    try {
      this.datastreamService.createById(dsid, "text/plain", pid, "demo text".getBytes());
    } catch ( ResourceRepositoryException e){
      //skip already existing
    }
  }

}
