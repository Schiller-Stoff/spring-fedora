package org.sebi.springfedora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({
  "/{pid}", 
  "objects/{pid}"
})
public class DigitalObjectController {
  

  /**
   * Default getters for GAMS objects
   * @param pid
   * @return
   */
  @GetMapping
  @ResponseBody
  public String getDO(@PathVariable("pid") String pid){
    return pid;
  }

}
