package org.sebi.springfedora.service;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;

public interface IDigitalObjectService {
  
  // public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject createDigitalObjectByPid(String pid, String rdf);
  public DigitalObject findDigitalObjectByPid(String pid) throws ResourceRepositoryException; 
  
  public DigitalObject deleteDigitalObjectByPid(String pid); 

  public DigitalObject updateMetadataByPid(String pid, String sparql) throws ResourceRepositoryException;

  public DigitalObject[] findAll() throws ResourceRepositoryException;

  public boolean checkIfExists(String pid);
}
