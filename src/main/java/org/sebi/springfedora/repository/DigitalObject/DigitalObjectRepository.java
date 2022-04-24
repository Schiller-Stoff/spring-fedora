package org.sebi.springfedora.repository.DigitalObject;

import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.model.DigitalObject;

public class DigitalObjectRepository implements IDigitalObjectRepository  {

  @Override
  public <S extends DigitalObject> S save(S entity) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public <S extends DigitalObject> Iterable<S> saveAll(Iterable<S> entities) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public Optional<DigitalObject> findById(String id) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public boolean existsById(String id) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public Iterable<DigitalObject> findAll() {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public Iterable<DigitalObject> findAllById(Iterable<String> ids) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public long count() {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public void deleteById(String id) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void delete(DigitalObject entity) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAll(Iterable<? extends DigitalObject> entities) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAll() {
    throw new NotImplementedException("Method not implemented!");
  }
  
}
