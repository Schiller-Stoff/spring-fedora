package org.sebi.springfedora.model;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.sebi.springfedora.utils.ValidationCommon;
import org.springframework.validation.annotation.Validated;

@Validated
public class DigitalObject extends Resource {
  
  /**
   * PID of a digital object as defined in OAIS.
   */
  @NotBlank
  @Pattern(
    regexp = ValidationCommon.VALID_PID_REGEX, 
    message = ValidationCommon.INVALID_DATASTREAM_ID_MESSAGE)
  private String pid;

  public DigitalObject(){
    super(null, "");
  }

  public DigitalObject(String pid){
    super(null, "");
  }
  
  public DigitalObject(String pid, String path, String rdfXml){
    super(path, rdfXml);
    this.pid = pid;
  }

  public DigitalObject(String pid, String path, String rdfXml, String[] children){
    super(path, rdfXml, children);
    this.pid = pid;
  }

  public String getPid(){
    return this.pid; 
  }

  public void setPid(String pid){
    this.pid = pid;
  }

}
