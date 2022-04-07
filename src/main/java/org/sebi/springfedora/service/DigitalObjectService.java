package org.sebi.springfedora.service;

import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.IDigitalObjectRepository;
import org.springframework.stereotype.Service;

@Service
public class DigitalObjectService implements IDigitalObjectService {
	
  private IDigitalObjectRepository digitalObjectRepository;	
	
  public DigitalObjectService(IDigitalObjectRepository digitalObjectRepository) {
	  this.digitalObjectRepository = digitalObjectRepository;
  }

  @Override
  public DigitalObject createDigitalObject(String pid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DigitalObject findDigitalObjectByPid(String pid) {
    // TODO Auto-generated method stub

    // repository layer: request against fedora
    // getting the Resource
    // need to be returned as DigitalObject / DataStream?

	  this.digitalObjectRepository.findById(pid);
    return null;
  }

}
