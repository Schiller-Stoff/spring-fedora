package org.sebi.springfedora.controller;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/objects/")
@RestController
public class SimpleDOController {
  
  private IDigitalObjectService digitalObjectService;
  
  public SimpleDOController(IDigitalObjectService digitalObjectService) {
	  this.digitalObjectService = digitalObjectService;
  }
	

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping("{pid}")
  public DigitalObject getDO(@PathVariable("pid") String pid){
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


}
