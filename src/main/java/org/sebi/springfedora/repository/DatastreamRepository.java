package org.sebi.springfedora.repository;

import java.util.Optional;

import org.sebi.springfedora.model.Datastream;

public class DatastreamRepository implements IDatastreamRepository {

  @Override
  public <S extends Datastream> S save(S entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S extends Datastream> Iterable<S> saveAll(Iterable<S> entities) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Datastream> findById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean existsById(String id) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterable<Datastream> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Datastream> findAllById(Iterable<String> ids) {
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
  public void delete(Datastream entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAll(Iterable<? extends Datastream> entities) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteAll() {
    // TODO Auto-generated method stub
    
  }
  


}
