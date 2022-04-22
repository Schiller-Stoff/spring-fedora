package org.sebi.springfedora.service;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;

public interface IDatastreamService {
  public Datastream findById();
  public Datastream createById(String id, String mimetype, String pid, byte[] content) throws ResourceRepositoryException;
}
