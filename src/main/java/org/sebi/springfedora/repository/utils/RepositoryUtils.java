package org.sebi.springfedora.repository.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for repository layer (used in different repositories)
 */
@Slf4j
public class RepositoryUtils {
  
  /**
   * Parses given uri as string to URI type.
   * @param uri {String} to be parsed to URI
   * @return parsed URI
   * @throws ResourceRepositoryException unparsable URI.
   */
  public static URI parseToURI(String uri) throws ResourceRepositoryException{
    URI uriParsed = null;
    try {
      uriParsed = new URI(uri);
      return uriParsed;
    } catch (URISyntaxException e) {
      String msg = String.format("Failed to parse URI (out of path) for string: %s. Applying status code: %d", uri, HttpStatus.INTERNAL_SERVER_ERROR.value());
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
  }


}
