package org.sebi.springfedora.model;

import org.springframework.util.MimeType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resource {
    // path used by fedora - abstract REST display 
    private String path;

    // RDF info from resource  
    private String rdfXml;
    
    // mimetype of contained datastream
    private MimeType mimeType;
}
