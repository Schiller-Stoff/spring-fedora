package org.sebi.springfedora.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Example for a specific eror message
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could'nt find resource in resource repository")
public class ResourceNotFoundException extends ResponseStatusException {

  public ResourceNotFoundException(int statusCode, String message ) {
    super(HttpStatus.NOT_FOUND, "Request rejected by ResourceRepository with statuscode: " + statusCode + ". Original cause: " + message);
  }

}
