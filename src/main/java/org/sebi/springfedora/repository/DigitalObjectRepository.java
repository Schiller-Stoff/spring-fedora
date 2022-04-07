package org.sebi.springfedora.repository;

import java.util.Optional;

import org.sebi.springfedora.model.DigitalObject;
import org.springframework.data.repository.CrudRepository;

public class DigitalObjectRepository implements IDigitalObjectRepository, CrudRepository<DigitalObject, String> {

  @Override
  public <S extends DigitalObject> S save(S entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S extends DigitalObject> Iterable<S> saveAll(Iterable<S> entities) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<DigitalObject> findById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean existsById(String id) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterable<DigitalObject> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<DigitalObject> findAllById(Iterable<String> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long count() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void deleteById(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void delete(DigitalObject entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAll(Iterable<? extends DigitalObject> entities) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAll() {
    // TODO Auto-generated method stub
    
  }
  
  

}
