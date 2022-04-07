package org.sebi.springfedora.controller;

import org.sebi.springfedora.service.DigitalObjectService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({
  "{pid}", 
  "objects/{pid}",

  "objects/{pid}/methods/sdef:{model}/{method}",
  "get/{pid}/bdef:{model}/{method}",
  "get/{pid}/sdef:{model}/{method}",
  "{pid}/sdef:{model}/{method}",
  "objects/{pid}/sdef:{model}/{method}"

})
public class DigitalObjectController {
  
  private IDigitalObjectService digitalObjectService;
  
  public DigitalObjectController(IDigitalObjectService digitalObjectService) {
	  this.digitalObjectService = digitalObjectService;
  }
	

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping
  @ResponseBody
  public String getDO(@PathVariable("pid") String pid){
	this.digitalObjectService.getDigitalObject(pid);
    return pid;
  }

}
