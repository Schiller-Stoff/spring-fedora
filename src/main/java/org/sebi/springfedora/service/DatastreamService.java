package org.sebi.springfedora.service;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.Datastream.DatastreamRepository;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatastreamService implements IDatastreamService  {

  private DatastreamRepository datastreamRepository;
  private IDigitalObjectService digitalObjectService;
  private DOResourceMapper doResourceMapper;

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;

  public DatastreamService(DatastreamRepository datastreamRepository, IDigitalObjectService digitalObjectService, DOResourceMapper doResourceMapper){
    this.datastreamRepository = datastreamRepository;
    this.digitalObjectService = digitalObjectService;
    this.doResourceMapper = doResourceMapper;
  }

  @Override
  public Datastream findById() {
    throw new NotImplementedException("Method not implemented");
  }
  
  @Override
  public void deleteByDsid(String pid, String dsid) {
    String path = doResourceMapper.mapObjectResourcePath(pid) + "/datastream/" + dsid;
    datastreamRepository.deleteById(path);
    
  }

  public Datastream createById(String id, String mimetype, String pid, byte[] content) throws ResourceRepositoryException {
    
    String path = doResourceMapper.mapObjectResourcePath(pid) + "/datastream/" + id;

    // throw if already exists
    if(datastreamRepository.existsById(path)){
      String msg = String.format("Creation of datastream with id %s at path %s failed. Found an already existing resource. Tried mimetype: %s", id, path, mimetype);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.CONFLICT.value(), msg);
    }

    // datastreams are only allowed if object exists
    if(!digitalObjectService.checkIfExists(pid)){
      String msg = String.format("Digital object not found. Creation of datastream with id %s at path %s failed. Linked digital object with pid: %s does not exist! Tried mimetype: %s", id, path, pid, mimetype);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.NOT_FOUND.value(), msg);
    }

    Datastream datastream = new Datastream(path, "", MimeType.valueOf(mimetype), content);

    return datastreamRepository.save(datastream);
  }
  
}
