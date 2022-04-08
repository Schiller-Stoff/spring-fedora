package org.sebi.springfedora.service;

import org.sebi.springfedora.model.DigitalObject;

public interface IDigitalObjectService {
  
  public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject findDigitalObjectByPid(String pid); 

}
