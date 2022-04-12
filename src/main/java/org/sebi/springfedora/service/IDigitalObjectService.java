package org.sebi.springfedora.service;

import java.net.URISyntaxException;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.model.DigitalObject;

public interface IDigitalObjectService {
  
  public DigitalObject createDigitalObjectByPid(String pid);
  public DigitalObject createDigitalObjectByPid(String pid, String rdf);
  public DigitalObject findDigitalObjectByPid(String pid); 
  
  public DigitalObject deleteDigitalObjectByPid(String pid); 

  public DigitalObject updateDatastreamByPid(String pid, String sparql) throws FcrepoOperationFailedException;

}
