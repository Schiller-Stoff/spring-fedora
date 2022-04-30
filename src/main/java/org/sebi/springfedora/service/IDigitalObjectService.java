package org.sebi.springfedora.service;

import java.net.URISyntaxException;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public interface IDigitalObjectService {
  
  // public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject createDigitalObjectByPid(String pid, String rdf);
  public DigitalObject findDigitalObjectByPid(String pid) throws ResourceRepositoryException; 
  
  public DigitalObject deleteDigitalObjectByPid(String pid); 

  public DigitalObject updateMetadataByPid(String pid, String sparql) throws ResourceRepositoryException;

  public DigitalObject[] findAll() throws ResourceRepositoryException;

  public DigitalObject createFromPrototypeByPid(String pid, String protoPid);
  public DigitalObject createFromPrototypeByModel(String pid, String model);

  public boolean checkIfExists(String pid);
  
  public String mapPidToResourcePath(String pid);
  public String mapObjectResourcePath(String pid);

  public DigitalObject createTrans(String pid) throws ResourceRepositoryException;
}
