package org.sebi.springfedora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DigitalObjectController {
  
  @GetMapping("/{pid}")
  @ResponseBody
  public String getDO(@PathVariable("pid") String pid){
    return pid;
  }

}
