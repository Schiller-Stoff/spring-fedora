package org.sebi.springfedora.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.fcrepo.client.DeleteBuilder;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.fcrepo.client.PatchBuilder;
import org.fcrepo.client.PutBuilder;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Resource;
import org.sebi.springfedora.repository.utils.RepositoryUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.MimeType;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ResourceRepository implements IResourceRepository<Resource> {

  @Override
  public <S extends Resource> S save(S resource) throws ResourceRepositoryException {

    URI uri = retrieveResourceURI(resource);

    // TODO add more
    // POST given resource to Fedora
    // String rdf = resource.getRdfXml();

    // String triples = resource.getRdfXml() != "" ? Common.ADDRDFPROPERTY.replace("$1", resource.getRdfXml()) : "";
    String triples = resource.getRdfXml();
    InputStream triplesIStream = IOUtils.toInputStream(resource.getRdfXml(), "utf-8");

    log.info("Initiating post request for: {}. With rdf: {}", resource.getPath(), triples);

    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new PutBuilder(uri, client)
          .body( triplesIStream, "text/turtle")
          //.slug(uri.toString())
          .perform()
    ) {

      if(response.getStatusCode() == 201 ){
        log.info("Succesfully saved resource with path: {} ", resource.getPath());
        return resource;
      } else if (response.getStatusCode() == 204){
        log.info("Succesfully saved resource with path: {}. Without content", resource.getPath());
        return resource;
      } else {
        String msg = String.format("Failed to save resource with path: %s. Original message: %s", resource.getPath(), retrieveFedoraErrBodyMsg(response));
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }
      
    } catch (IOException e) {
      String msg = String.format("Failed to save resource with path: %s. Original message: %s", resource.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e1) {
      String msg = String.format("Failed to save resource with path: %s. Original message: %s. For RDF: %s", resource.getPath(), e1.getMessage(), resource.getRdfXml());
      log.error(msg + "\n" + e1);
      throw new ResourceRepositoryException(e1.getStatusCode(), msg);
    } catch (Exception e){
      // pass through thrown if status codes are 400+ from above
      if(e instanceof ResourceRepositoryException) throw e;
      
      String msg = String.format("Failed to save resource with path %s. Original message: ", resource.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

  }

  @Override
  public <S extends Resource> Iterable<S> saveAll(Iterable<S> entities) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Resource> findById(String id) throws ResourceRepositoryException {
    URI uri = RepositoryUtils.parseToURI(id);

    try (
        final FcrepoClient client = FcrepoClient.client().build();
        FcrepoResponse response = new GetBuilder(uri, client)
            .accept("application/rdf+xml")
            .perform()
        ) {

      if(response.getStatusCode() == 200){
        String turtleContent = IOUtils.toString(response.getBody(), "UTF-8");
        String[] datastreams = ResourceRDFMapper.parseRDFChildren(turtleContent);
        Resource resource = new Resource(id, turtleContent, datastreams);
        log.info("Found resource with uri {} inside fedora", resource.getPath());
        return Optional.of(resource);
      } else if(response.getStatusCode() == 404) {
        log.info("GET request for resource {} succesfull. Found no resource at given path. ", uri.toString());
        return Optional.empty();
      } else {
        String msg = String.format("Status: %s - Failed to GET resource from fedora at uri: %s. Original resource response body: %s",response.getStatusCode(), uri.toString(), retrieveFedoraErrBodyMsg(response));
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }

    } catch (IOException e) {
      String msg = String.format("GET request for resource with path: %s failed. Original err msg: %s", id, e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e1) {
      String msg = String.format("GET request for resource with path: %s failed. Original err msg: %s", id, e1.getMessage());
      log.error(msg + "\n" + e1);
      throw new ResourceRepositoryException(e1.getStatusCode(), msg);
    }

  }

  @Override
  public boolean existsById(String id) throws ResourceRepositoryException {
      return !this.findById(id).isEmpty();
  }

  @Override
  public Iterable<Resource> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Resource> findAllById(Iterable<String> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long count() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void deleteById(String id) throws ResourceRepositoryException {
    
    URI uri = RepositoryUtils.parseToURI(id);
    
    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new DeleteBuilder(uri, client).perform();
    ) {

      log.debug("Resource deletion status: {}", response.getStatusCode());

      if(response.getStatusCode() == 204){
        log.info("Deletion of resource with uri {} was succesful. Got status code: ", id, response.getStatusCode());
      } else {
        String msg = String.format("Failed to delete resource with uri %s because of status code: %s. Fedora error message: ", retrieveFedoraErrBodyMsg(response));
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }

    } catch(IOException e){
      String msg = String.format("Failed to delete resource with uri %d because if IO. Original message: %s", id, e.getMessage());
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e2) {
      String msg = String.format("Failed to delete resource with uri %s from fedora. Because of status code from fedora: %d. Original message: %s", id, e2.getStatusCode(), e2.getMessage());
      log.error(msg);
      throw new ResourceRepositoryException(e2.getStatusCode(), msg);
    } catch (NullPointerException e3){
      String msg = String.format("Failed to delete resource with uri %s from fedora. Original error: %s", id, e3.getMessage());
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
  }

  @Override
  public void delete(Resource entity) {
    this.deleteById(entity.getPath());
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteAll(Iterable<? extends Resource> entities) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteAll() {
    // TODO Auto-generated method stub

  }



  @Override
  public Resource updateResourceTriples(String url, String sparql) throws ResourceRepositoryException {

    Optional<Resource> optional = this.findById(url);

    if(!optional.isPresent()) return null;
    
    Resource curResource = optional.get();


    URI uri = RepositoryUtils.parseToURI(url);

    InputStream triplesIStream = IOUtils.toInputStream(sparql, "utf-8");

    
    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new PatchBuilder(uri, client)
        .body(triplesIStream,"application/sparql-update")
        .perform()
    ) {


      if(response.getStatusCode() == 204){
        log.info("Succesfully updated triples for resource: {}. With sparql: {}", response.getStatusCode(), sparql);
        return curResource;
      } else if(response.getStatusCode() == 412){
        log.error("{} Failed to PATCH / update fedora resource {} with SPARQL: {}", response.getStatusCode(),  curResource.getPath(), sparql);
        throw new ResourceRepositoryException(response.getStatusCode(), "Failed to PATCH / update fedora resource " + uri + " with SPARQL:\n " + sparql + "\n. Original mesage was: " + retrieveFedoraErrBodyMsg(response));
      } else {
        log.error("{} Failed to PATCH / update fedora resource {} with SPARQL: {}", response.getStatusCode(), curResource.getPath(), sparql);
        throw new ResourceRepositoryException(response.getStatusCode(), "Failed to PATCH / update fedora resource " + uri + " with SPARQL:\n " + sparql + "\n Original mesage was: " + retrieveFedoraErrBodyMsg(response));
      }

      

    } catch (FcrepoOperationFailedException e){
      log.error("Fedora operation failed. Failed to create fedora resource for path: {}. Fedora message is: ", curResource.getPath(), e.getMessage());
      throw new ResourceRepositoryException( e.getStatusCode(), e.getMessage());
    } catch (IOException e2){
      log.error("IOException: Failed to create fedora resource for uri: {}. {}", curResource.getPath(), e2.getMessage());
      throw new ResourceRepositoryException( HttpStatus.INTERNAL_SERVER_ERROR.value(), e2.getMessage());
    } catch (NullPointerException e3){
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e3.getMessage());
    } 
  }



  /**
   * Checks if fcrepoResponse body response being null. 
   * Returns the send fedora error message 
   * @param fcrepoResponse
   * @return
   * @throws IOException
   */
  public String retrieveFedoraErrBodyMsg(FcrepoResponse fcrepoResponse) throws IOException {
    
    if(fcrepoResponse.getBody() != null){
      try {
        return IOUtils.toString(fcrepoResponse.getBody(), "utf-8");
      } catch (IOException e){
        log.error("Failed to parse body from request against url: {}", fcrepoResponse.getUrl());
        return "(No message from resource repository.)";
      }
      
    }
    
    return "(No message from resource repository)";
  }

  /**
   * Extracts uri from resource object parsed as URI.
   * @param resource {Resource} from which the URI should be extracted.
   * @return parsed resource's URI.
   * @throws ResourceRepositoryException
   */
  public URI retrieveResourceURI(Resource resource) throws ResourceRepositoryException {
    URI uri = null;
    try {
      uri = new URI(resource.getPath());
      return uri;
    } catch (URISyntaxException e) {
      String msg = String.format("Failed to parse URI (out of path) for resource with path: %s. Applying status code: %d", resource.getPath(), HttpStatus.INTERNAL_SERVER_ERROR.value());
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
  }

}
