package org.sebi.springfedora.controller;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.Datastream.DatastreamRepository;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/datastreams/")
@RestController
public class SimpleDSController {
  
  private DatastreamRepository datastreamRepository;

  public SimpleDSController(DatastreamRepository datastreamRepository){
    this.datastreamRepository = datastreamRepository;
  }

  @GetMapping("{path}")
  public Datastream createSampleDatastream(@PathVariable String path) throws ResourceRepositoryException {
    Datastream ds = new Datastream("http://localhost:8082/rest/" + path, "", MimeType.valueOf("text/plain"));
    this.datastreamRepository.save(ds);
    return ds;
  }

}
