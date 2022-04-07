package org.sebi.springfedora.service;

import org.sebi.springfedora.model.DigitalObject;

public interface IDigitalObjectService {
  
  public DigitalObject createDigitalObject(String pid);
  public DigitalObject findDigitalObjectByPid(String pid); 

}
