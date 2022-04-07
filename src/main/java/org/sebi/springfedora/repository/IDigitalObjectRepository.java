package org.sebi.springfedora.repository;

import org.sebi.springfedora.model.DigitalObject;
import org.springframework.data.repository.CrudRepository;

public interface IDigitalObjectRepository extends CrudRepository<DigitalObject, String> {
  
}
