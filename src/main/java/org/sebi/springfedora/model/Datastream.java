package org.sebi.springfedora.model;

import javax.validation.constraints.NotBlank;

import org.springframework.util.MimeType;

public class Datastream extends Resource {

  //private String dsId; 
  
  @NotBlank
  private byte[] content;

  // mimetype of datastream
  @NotBlank
  private MimeType mimeType;

  public Datastream(String path, String rdfXml, MimeType mimeType, byte[] content){
    super(path, rdfXml);
    this.content = content;
    this.mimeType = mimeType;
  }

  public byte[] getContent(){
    return this.content;
  }

  public void setContent(byte[] content){
    this.content = content;
  }

  public MimeType getMimetype(){
    return this.mimeType;
  }

  public void setMimetype(MimeType mimeType){
    this.mimeType = mimeType;
  }
}
