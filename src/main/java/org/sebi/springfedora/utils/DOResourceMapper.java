package org.sebi.springfedora.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DOResourceMapper {

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;


  /**
   * Maps pid to fedora resource path part e.g. like o:derla.sty to /objects/derla.sty.
   * DOES NOT apply the full resource address like https://localhost:8080/rest/objects/derla.sty
   */
  public static String mapPidToResourcePath(String pid){
    return Rename.rename(pid);
  }

  /**
   * Maps given pid to full resource address like o:derla.sty to like https://localhost:8080/rest/objects/derla.sty
   * dependent on spring environment variables.
   */
  public String mapObjectResourcePath(String pid) {
    
    String mappedPid = mapPidToResourcePath(pid);
    String resourcePath = curHost + fedoraRESTEndpoint + mappedPid;

    return resourcePath;
  }
}
