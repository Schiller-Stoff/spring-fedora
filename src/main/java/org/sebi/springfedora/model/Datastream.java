package org.sebi.springfedora.model;

import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

public class Datastream extends Resource {
  //private String dsId; 
  private byte[] content;

  public Datastream(String path, String rdfXml, MimeType mimeType, byte[] content){
    super(path, rdfXml, mimeType);
    this.content = content;
  }

  public byte[] getContent(){
    return this.content;
  }
}
