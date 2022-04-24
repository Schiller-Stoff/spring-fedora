package org.sebi.springfedora.model;

public class DigitalObject extends Resource {
  
  private String pid;
  
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
