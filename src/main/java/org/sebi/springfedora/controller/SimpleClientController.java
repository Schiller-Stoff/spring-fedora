package org.sebi.springfedora.controller;

import java.io.IOException;

import javax.validation.constraints.Pattern;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.service.ContentModelService;
import org.sebi.springfedora.service.IDatastreamService;
import org.sebi.springfedora.service.IDigitalObjectService;
import org.sebi.springfedora.utils.ValidationCommon;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/client/")
@Controller
@Validated
public class SimpleClientController {

  IDigitalObjectService digitalObjectService;
  IDatastreamService datastreamService;
  ContentModelService contentModelService;


  public SimpleClientController(IDigitalObjectService digitalObjectService, IDatastreamService datastreamService, ContentModelService contentModelService){
    this.digitalObjectService = digitalObjectService;
    this.datastreamService = datastreamService;
    this.contentModelService = contentModelService;
  }
  

  @GetMapping("/objects")
  public String showAllDigitalObjects(Model model) throws ResourceRepositoryException {

    DigitalObject[] digitalObjects = digitalObjectService.findAll();

    model.addAttribute("dos", digitalObjects);

    return "digitalObject/show_all";
  }

  @GetMapping("{pid}")
  public String getObject(@PathVariable String pid, Model model ){

    DigitalObject digitalObject = digitalObjectService.findDigitalObjectByPid(pid);

    model.addAttribute("do", digitalObject);
    
    return "digitalObject/show";
  }

  @PostMapping("/createObject")
  @Validated
  public String postObject( 
    @RequestParam @Pattern(regexp = ValidationCommon.VALID_PID_REGEX, message = ValidationCommon.INVALID_PID_MESSAGE) String pid, 
    Model model
  ){
    DigitalObject digitalObject = digitalObjectService.createDigitalObjectByPid(pid, null);
    model.addAttribute("do", digitalObject);
    return "redirect:/client/" + digitalObject.getPid();
  }

  @GetMapping
  public String getIndex(){
    return "index";
  }

  @GetMapping("/createObject")
  public String createObject(Model model){
    model.addAttribute("do", new DigitalObject("testpid", "", ""));
    return "digitalObject/create";
  }

  @PostMapping("/createDatastream")
  public String createDatastream(
    @RequestParam @Pattern(regexp = ValidationCommon.VALID_PID_REGEX, message = ValidationCommon.INVALID_PID_MESSAGE) String pid, 
    @RequestParam @Pattern(regexp = ValidationCommon.VALID_DATASTREAM_ID, message = ValidationCommon.INVALID_DATASTREAM_ID_MESSAGE) String dsid, 
    @RequestParam MultipartFile file, 
    Model model
  ) throws IOException {

    byte[] fileAsBytes = file.getBytes();
    String mimetype = file.getContentType();

    Datastream datastream = datastreamService.createById(dsid, mimetype, pid, fileAsBytes);
    DigitalObject digitalObject = digitalObjectService.findDigitalObjectByPid(pid);

    model.addAttribute("do", digitalObject);
    model.addAttribute("datastream", datastream);

    return "redirect:/client/" + digitalObject.getPid();
  }

  @DeleteMapping("/deleteDatastream")
  public String deleteDatastream(@RequestParam String dsid, @RequestParam String pid){

    datastreamService.deleteByDsid(pid, dsid);

    return "redirect:/client/" + pid;
  }

  @DeleteMapping("/deleteObject")
  public String deleteObject(@RequestParam String pid, Model model){
    this.digitalObjectService.deleteDigitalObjectByPid(pid);
    return "redirect:/client/objects";
  } 

  @PostMapping("/createObjectFromPrototype")
  public String createObjectFromPrototype(
    @RequestParam @Pattern(regexp = ValidationCommon.VALID_PID_REGEX, message = ValidationCommon.INVALID_PID_MESSAGE) String pid, 
    @RequestParam String contentModel
  ){
    DigitalObject digitalObject = contentModelService.createFromPrototypeByModel(pid, contentModel);
    return "redirect:/client/" + digitalObject.getPid();
  }

  @GetMapping("/createObjectFromPrototype")
  public String createObjectFromPrototype(){
    return "digitalObject/create_from_prototype";
  }

}
