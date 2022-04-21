package org.sebi.springfedora.repository.Datastream;

import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.ResourceRepository;

public class DatastreamRepository implements IDatastreamRepository {

  private ResourceRepository resourceRepository;

  public DatastreamRepository(ResourceRepository resourceRepository){
    this.resourceRepository = resourceRepository;
  }

  @Override
  public <S extends Datastream> S save(S entity) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public <S extends Datastream> Iterable<S> saveAll(Iterable<S> entities) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public Optional<Datastream> findById(String id) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public boolean existsById(String id) {
    return resourceRepository.existsById(id);
  }

  @Override
  public Iterable<Datastream> findAll() {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public Iterable<Datastream> findAllById(Iterable<String> ids) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public long count() {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public void deleteById(String id) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void delete(Datastream entity) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAll(Iterable<? extends Datastream> entities) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAll() {
    // TODO Auto-generated method stub
    
  }
  


}
