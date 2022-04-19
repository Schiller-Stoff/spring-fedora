package org.sebi.springfedora.controller;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

  @PostMapping("/createObject")
  public String postObject(@RequestParam String pid, Model model){

    DigitalObject digitalObject = digitalObjectService.createDigitalObjectByPid(pid);
    model.addAttribute("do", digitalObject);
    return "redirect:/client/" + digitalObject.getPid();
  }

  @GetMapping
  public String getIndex(){
    return "index";
  }

  @GetMapping("/createObject")
  public String createObject(Model model){
    model.addAttribute("do", new DigitalObject("testpid", new Resource("", "")));
    return "digitalObject/create";
  }

}
