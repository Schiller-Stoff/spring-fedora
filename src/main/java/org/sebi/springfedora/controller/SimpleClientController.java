package org.sebi.springfedora.controller;

import java.io.IOException;

import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.service.IDatastreamService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/client/")
@Controller
public class SimpleClientController {

  IDigitalObjectService digitalObjectService;
  IDatastreamService datastreamService;


  public SimpleClientController(IDigitalObjectService digitalObjectService, IDatastreamService datastreamService){
    this.digitalObjectService = digitalObjectService;
    this.datastreamService = datastreamService;
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
    model.addAttribute("do", new DigitalObject("testpid", new Resource("", "",MimeType.valueOf("text/turtle"))));
    return "digitalObject/create";
  }

  @PostMapping("/createDatastream")
  public String createDatastream(@RequestParam String pid, @RequestParam String dsid, @RequestParam MultipartFile file, Model model) throws IOException {

    byte[] fileAsBytes = file.getBytes();
    String mimetype = file.getContentType();

    Datastream datastream = datastreamService.createById(dsid, mimetype, pid, fileAsBytes);
    DigitalObject digitalObject = digitalObjectService.findDigitalObjectByPid(pid);

    model.addAttribute("do", digitalObject);
    model.addAttribute("datastream", datastream);

    return "redirect:/client/" + digitalObject.getPid();
  }

}
