package org.sebi.springfedora.controller;

import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;

@Controller
// @RequestMapping({
//   "{pid}", 
//   "objects/{pid}",

//   "objects/{pid}/methods/sdef:{model}/{method}",
//   "get/{pid}/bdef:{model}/{method}",
//   "get/{pid}/sdef:{model}/{method}",
//   "{pid}/sdef:{model}/{method}",
//   "objects/{pid}/sdef:{model}/{method}"

// })
public class DigitalObjectController {
  
  private IDigitalObjectService digitalObjectService;
  
  public DigitalObjectController(IDigitalObjectService digitalObjectService) {
	  this.digitalObjectService = digitalObjectService;
  }
	

  

}
