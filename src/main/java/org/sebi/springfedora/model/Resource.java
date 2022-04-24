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

    // ldp:contains of resource
    private String[] children;

    public Resource(String path, String rdfXml){
        this.path = path;
        this.rdfXml = rdfXml;
    }

    public Resource(String path, String rdfXml, String[] children){
        this(path, rdfXml);
        this.children = children;
    }

}
