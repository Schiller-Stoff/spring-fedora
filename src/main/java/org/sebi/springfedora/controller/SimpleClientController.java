package org.sebi.springfedora.controller;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/client/")
@Controller
public class SimpleClientController {

  IDigitalObjectService digitalObjectService;


  public SimpleClientController(IDigitalObjectService digitalObjectService){
    this.digitalObjectService = digitalObjectService;
  }
  

  @GetMapping("{pid}")
  public String getObject(@PathVariable String pid, Model model ){

    DigitalObject digitalObject = digitalObjectService.findDigitalObjectByPid(pid);

    model.addAttribute("do", digitalObject);

    return "digitalObject/show";
  }

}
