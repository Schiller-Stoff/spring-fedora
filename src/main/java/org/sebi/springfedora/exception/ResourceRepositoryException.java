package org.sebi.springfedora.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Example for dynamic exception according to resource layer
 */
public class ResourceRepositoryException extends ResponseStatusException {
  
  public ResourceRepositoryException(int statusCode, String message ) {
    super(HttpStatus.valueOf(statusCode), message);
  }

  // public ResourceRepositoryException(String message){
  //   super(HttpStatus., message);
  // }
}
