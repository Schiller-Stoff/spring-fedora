package org.sebi.springfedora.repository.DigitalObject;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.IResourceRepository;
import org.springframework.data.repository.CrudRepository;

public interface IDigitalObjectRepository extends CrudRepository<DigitalObject, String> {
  
}
