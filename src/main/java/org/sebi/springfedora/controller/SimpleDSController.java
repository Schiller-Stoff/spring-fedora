package org.sebi.springfedora.controller;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.service.IDatastreamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/datastreams/")
@RestController
public class SimpleDSController {
  
  private IDatastreamService datastreamService;

  public SimpleDSController(IDatastreamService datastreamService){
    this.datastreamService = datastreamService;
  }

  @GetMapping("{pid}/{dsid}")
  public Datastream createSampleDatastream(@PathVariable String pid, @PathVariable String dsid) throws ResourceRepositoryException {
    String textFile = "Textfile with pid and dsid: " + pid + " - " + dsid;
    return this.datastreamService.createById(dsid, "text/plain", pid, textFile.getBytes());
  }

}
