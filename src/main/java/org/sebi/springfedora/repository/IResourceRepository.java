package org.sebi.springfedora.repository;

import org.sebi.springfedora.model.Resource;
import org.springframework.data.repository.CrudRepository;

/**
 * Uses fedora to retrieve an arbitrary resource.
 */
public interface IResourceRepository extends CrudRepository<Resource, String> {
  
}
