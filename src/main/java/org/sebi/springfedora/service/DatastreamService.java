package org.sebi.springfedora.service;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.Datastream.DatastreamRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatastreamService implements IDatastreamService  {

  private DatastreamRepository datastreamRepository;

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;

  public DatastreamService(DatastreamRepository datastreamRepository){
    this.datastreamRepository = datastreamRepository;
  }

  @Override
  public Datastream findById() {
    throw new NotImplementedException("Method not implemented");
  }
  

  public Datastream createById(String id, String mimetype) throws ResourceRepositoryException {
    
    String path = curHost + fedoraRESTEndpoint + id;

    // throw if already exists
    if(datastreamRepository.existsById(path)){
      String msg = String.format("Creation of datastream with id %s at path %s failed. Found and already existing resource. Tried mimetype: %s", id, path, mimetype);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }

    Datastream datastream = new Datastream(path, "", MimeType.valueOf(mimetype));

    return datastreamRepository.save(datastream);
  }
  
}
