package org.sebi.springfedora.model;

import org.springframework.util.MimeType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Resource in terms of LDP.
 * One of four types - here used BasicContainer / Binary
 * Dependent if binary datastream is used.
 */
@Data
public class Resource {
    // path used by fedora - abstract REST display 
    private String path;

    // RDF info from resource  
    private String rdfXml;
    
    // mimetype of contained datastream
    private MimeType mimeType;

    // optional binary described by resource
    private byte[] binary; 

    // ldp:contains of resource
    private String[] children;

    public Resource(String path, String rdfXml, MimeType mimeType){
        this.path = path;
        this.rdfXml = rdfXml;
        this.mimeType = mimeType;
    }

    public Resource(String path, String rdfXml, MimeType mimeType, byte[] binary){
        this(path, rdfXml, mimeType);
        this.binary = binary;
    }

    public Resource(String path, String rdfXml, MimeType mimeType, String[] children){
        this(path, rdfXml, mimeType);
        this.children = children;
    }

}
