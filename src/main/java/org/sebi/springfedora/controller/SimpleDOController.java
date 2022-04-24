package org.sebi.springfedora.controller;

import java.net.URISyntaxException;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.Common;
import org.sebi.springfedora.exception.ResourceNotFoundException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/objects/")
@RestController
public class SimpleDOController {
  
  private IDigitalObjectService digitalObjectService;
  private IDigitalObjectRepository digitalObjectRepository;
  
  public SimpleDOController(IDigitalObjectService digitalObjectService, IDigitalObjectRepository digitalObjectRepository) {
	  this.digitalObjectService = digitalObjectService;
    this.digitalObjectRepository = digitalObjectRepository;
  }
	

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping("{pid}")
  public DigitalObject getDO(@PathVariable("pid") String pid) throws ResourceRepositoryException {
	  return this.digitalObjectService.findDigitalObjectByPid(pid);
    // return pid;
  }

  @GetMapping("{pid}/delete")
  public DigitalObject deleteById(@PathVariable("pid") String pid){
    return this.digitalObjectService.deleteDigitalObjectByPid(pid);
  }

  @GetMapping("{pid}/create")
  public DigitalObject creaDigitalObject(@PathVariable("pid") String pid){
    return this.digitalObjectService.createDigitalObjectByPid(pid);
  }


  @GetMapping("{pid}/updateDcTitle/{dc}")
  public DigitalObject updateDOTriples(@PathVariable("pid") String pid, @PathVariable("dc") String dcTitle) throws URISyntaxException, ResourceRepositoryException {
    String sparql = Common.ADDRDFPROPERTY.replace("$1", "dc:title \""+ dcTitle + "\"");
    return this.digitalObjectService.updateDatastreamByPid(pid, sparql);
  }

  @GetMapping("")
  public Iterable<DigitalObject> getAllDOs(){
    return digitalObjectRepository.findAll();
  }

}
