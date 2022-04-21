package org.sebi.springfedora.model;

import org.springframework.util.MimeType;



public class Datastream extends Resource {
  //private String dsId; 

  public Datastream(String path, String rdfXml, MimeType mimeType){
    super(path, rdfXml, mimeType);
  }
}
