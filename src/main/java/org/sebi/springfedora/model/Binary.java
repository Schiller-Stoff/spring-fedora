package org.sebi.springfedora.model;

import org.springframework.util.MimeType;

public class Binary extends Resource {
  
  // mimetype of contained datastream
  private MimeType mimeType;

  // optional binary described by resource
  private byte[] binary; 

  public Binary(String path, String rdfXml, MimeType mimeType, byte[] binary){
    super(path, rdfXml, mimeType);
    this.binary = binary;
}

}
