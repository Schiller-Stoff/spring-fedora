package org.sebi.springfedora.service.utils;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility for content model operations. Like resolving pid for prototypes.
 */
@Slf4j
public class ContentModelUtils {
  
  public static String resolveModelPid(String contentModel){
    String protoPid = "";
    switch(contentModel.toUpperCase()){
      case "TEI":
        protoPid = "o:prototype.tei";
        break;
      case "GML":
        protoPid = "o:prototype.gml";
        break;
      default:
        String msg = String.format("Found no prototype for given content model %s (should be no pid!)", contentModel);
        log.error(msg);
        throw new ResourceRepositoryException(HttpStatus.UNPROCESSABLE_ENTITY.value(), msg);
    }
    return protoPid;
  }

}
