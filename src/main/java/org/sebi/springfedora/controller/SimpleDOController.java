package org.sebi.springfedora.controller;

import java.net.URISyntaxException;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.Common;
import org.sebi.springfedora.exception.ResourceNotFoundException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.DigitalObject.IDigitalObjectRepository;
import org.sebi.springfedora.service.ContentModelService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.sebi.springfedora.service.utils.ContentModelUtils;
import org.sebi.springfedora.utils.ValidationCommon;
import org.springframework.validation.BindingResult;
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
  public DigitalObject getDO(@PathVariable("pid") @Pattern(regexp = ValidationCommon.VALID_PID_REGEX) String pid) throws ResourceRepositoryException {
    
	  return this.digitalObjectService.findDigitalObjectByPid(pid);
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
