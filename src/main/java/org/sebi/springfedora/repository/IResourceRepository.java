package org.sebi.springfedora.repository;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Resource;
import org.springframework.data.repository.CrudRepository;

/**
 * Uses fedora to retrieve an arbitrary resource.
 */
public interface IResourceRepository<T> extends CrudRepository<T, String> {
  
  public Resource updateResourceTriples(String url, String sparql) throws ResourceRepositoryException;


}
