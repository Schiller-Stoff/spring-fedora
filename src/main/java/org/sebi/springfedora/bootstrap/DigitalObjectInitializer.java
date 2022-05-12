package org.sebi.springfedora.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.sebi.springfedora.Common;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.service.IDatastreamService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.sebi.springfedora.service.ISetupService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// @Profile("none")
@Slf4j
@Component // tells spring to be a spring bean.
public class DigitalObjectInitializer implements CommandLineRunner {

  private final IDigitalObjectService DOService;
  private final IResourceRepository resourceRepository;
  private final IDatastreamService datastreamService;
  private final ISetupService setupService;

  public DigitalObjectInitializer(IDigitalObjectService DOService, IResourceRepository resourceRepository, IDatastreamService datastreamService, ISetupService setupService) {
    this.DOService = DOService;
    this.resourceRepository = resourceRepository;
    this.datastreamService = datastreamService;
    this.setupService = setupService;
  }

  @Override
  public void run(String... args) throws Exception {
    log.debug("Bootstraping fedora spring stuff...");
    setupService.createBaseResources();
    setupFedoraPrototypes();
  }

  private void setupFedoraPrototypes(){

    // (at johannes code: loop through folder in apache and create objects accordingly)

    // creation of prototypes
    // (without looping through apache folder)
    List<String> prototypes = Arrays.asList(
      "o:prototype.latex",
      "o:prototype.lido",
      "o:prototype.gml",
      "o:prototype.mei",
      "o:prototype.context",
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
    this.createDatastream("SOME_TEXT", "text/plain", "o:derla.sty", "example text".getBytes());
    this.createDatastream("DEMO_TEXT", "text/plain", "o:derla.sty", "demo text".getBytes());
    

    /**
     * Creation of prototypes
     * (in case of TEI)
     */

    this.createDigitalObject("o:prototype.tei");

    String propertiesToBeAdded = "cm4f:owner 'sysop'; cm4f:created 'now'";
    String sparqlUpdate = Common.ADDRDFPROPERTY.replace("$1", propertiesToBeAdded);

    this.DOService.updateMetadataByPid("o:prototype.tei", sparqlUpdate);

    String teiBasePath = "C:\\Users\\stoffse\\Documents\\programming\\java\\spring-fedora\\data\\models\\tei\\";

    //TEI SOURCE
    createDatastreamFromFile(
      "TEI_SOURCE", 
      "application/xml", 
      "o:prototype.tei", 
      teiBasePath + "TEI_SOURCE.xml"
    );

    // Dublin Core
    createDatastreamFromFile(
      "DC", 
      "application/xml", 
      "o:prototype.tei", 
      teiBasePath + "DC.xml"
    );

  }

  private void createDigitalObject(String pid, String rdfXml){
    try {
      this.DOService.createDigitalObjectByPid(pid, rdfXml);
    } catch ( ResourceRepositoryException e){
      //skip already existing
    }
  }

  private void createDigitalObject(String pid){
    try {
      this.DOService.createDigitalObjectByPid(pid, null);
    } catch ( ResourceRepositoryException e){
      //skip already existing
    }
  }

  private void createDatastream(String dsid, String mimetype, String pid, byte[] content){
    try {
      this.datastreamService.createById(dsid, mimetype, pid, content);
    } catch ( ResourceRepositoryException e){
      //skip already existing
    }
  }

  private void createDatastreamFromFile(String dsid, String mimetype, String pid, String filePath){
    try {
      byte[] content = FileUtils.readFileToByteArray(new File(filePath));
      this.createDatastream(dsid, mimetype, pid, content);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
