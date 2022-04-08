package org.sebi.springfedora.model;

import lombok.AllArgsConstructor;
import lombok.Data;

// lombok provides getters + setters
@Data
@AllArgsConstructor
public class DigitalObject {
  
  private String pid;
  private Resource resource;
  

}
