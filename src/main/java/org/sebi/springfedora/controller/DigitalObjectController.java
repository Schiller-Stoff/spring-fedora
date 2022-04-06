package org.sebi.springfedora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DigitalObjectController {
  

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping({
    "/{pid}", 
    "/objects/{pid}"
  })
  @ResponseBody
  public String getDO(@PathVariable("pid") String pid){
    return pid;
  }

}
