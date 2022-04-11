package org.sebi.springfedora.service;

import org.sebi.springfedora.model.DigitalObject;

public interface IDigitalObjectService {
  
  public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject createDigitalObjectByPid(String pid, String rdf);
  public DigitalObject findDigitalObjectByPid(String pid); 
  
  public DigitalObject deleteDigitalObjectByPid(String pid); 

}
