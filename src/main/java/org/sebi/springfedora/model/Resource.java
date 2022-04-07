package org.sebi.springfedora.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resource {
    private String uri;
    private String rdfXml;
}
