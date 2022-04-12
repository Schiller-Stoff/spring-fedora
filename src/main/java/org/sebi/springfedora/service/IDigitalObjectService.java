package org.sebi.springfedora.service;

import java.net.URISyntaxException;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public interface IDigitalObjectService {
  
  public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject createDigitalObjectByPid(String pid, String rdf);
  public DigitalObject findDigitalObjectByPid(String pid) throws ResourceRepositoryException; 
  
  public DigitalObject deleteDigitalObjectByPid(String pid); 

  public DigitalObject updateDatastreamByPid(String pid, String sparql) throws FcrepoOperationFailedException;

}
