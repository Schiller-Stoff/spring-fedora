package org.sebi.springfedora.controller;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.sebi.springfedora.Common;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.service.ContentModelService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/objects/")
@RestController
@Validated
public class SimpleDOController {
  
  private IDigitalObjectService digitalObjectService;
  private ContentModelService contentModelService;
  
  public SimpleDOController(IDigitalObjectService digitalObjectService, ContentModelService contentModelService) {
	  this.digitalObjectService = digitalObjectService;
    this.contentModelService = contentModelService;
  }
	

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping("{pid}")
  public DigitalObject getDO(@Valid DigitalObject digitalObject) throws ResourceRepositoryException {
    
	  return this.digitalObjectService.findDigitalObjectByPid(digitalObject.getPid());
    // return pid;
  }

  @GetMapping("{pid}/delete")
  public DigitalObject deleteById(@PathVariable("pid") String pid){
    return this.digitalObjectService.deleteDigitalObjectByPid(pid);
  }

  @GetMapping("{pid}/updateDcTitle/{dc}")
  public DigitalObject updateDOTriples(@PathVariable("pid") String pid, @PathVariable("dc") String dcTitle) throws URISyntaxException, ResourceRepositoryException {
    String sparql = Common.ADDRDFPROPERTY.replace("$1", "dc:title \""+ dcTitle + "\"");
    return this.digitalObjectService.updateMetadataByPid(pid, sparql);
  }

  @GetMapping("")
  public DigitalObject[] getAllDOs(){
    return digitalObjectService.findAll();
  }

}
