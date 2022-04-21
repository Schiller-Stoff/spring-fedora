package org.sebi.springfedora.controller;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.Datastream.DatastreamRepository;
import org.sebi.springfedora.service.IDatastreamService;
import org.springframework.util.MimeType;
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

  @GetMapping("{path}")
  public Datastream createSampleDatastream(@PathVariable String path) throws ResourceRepositoryException {
    return this.datastreamService.createById(path, "text/plain");
  }

}
