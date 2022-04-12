package org.sebi.springfedora.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.fcrepo.client.PatchBuilder;
import org.fcrepo.client.PutBuilder;
import org.fcrepo.client.DeleteBuilder;
import org.sebi.springfedora.Common;
import org.sebi.springfedora.model.Resource;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ResourceRepository implements IResourceRepository {

  @Override
  public <S extends Resource> S save(S resource) {

    URI uri = null;
    try {
      uri = new URI(resource.getPath());
    } catch (URISyntaxException e) {
      System.out.println("Malformed URI!");
    }

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

      int statusCode = response.getStatusCode(); 

      if(statusCode == 201 ){
        log.info("Succesfully saved resource with path: {} ", resource.getPath());
        return resource;
      } else if (statusCode == 204){
        log.info("Succesfully saved resource with path: {}. Without content", resource.getPath());
        return resource;
      } else {
        log.error("Failed to save resource with path: {}. Status code given from fedora: {}", resource.getPath(), response.getStatusCode());
        
        String bodyAString =  IOUtils.toString(response.getBody(), "utf-8");
        log.error("Response body: {}", bodyAString);
        
        return null;
      }
      
      //URI location = response.getLocation();
      //log.info("POST request against {} at location: {}", uri.toString(), location.toString());
    } catch (IOException e) {
      e.printStackTrace();
      log.error("IOException!");
      return null;
    } catch (FcrepoOperationFailedException e1) {
      // e1.printStackTrace();
      log.error("Failed to create fedora resource for uri: {}", resource.getPath());
      return null;
    } catch (Exception e){
      log.error("Uknown error at POST against fedora! For uri: {}", resource.getPath());
      log.error(e.getMessage());
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public <S extends Resource> Iterable<S> saveAll(Iterable<S> entities) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Resource> findById(String id) {
    URI uri = null;
    try {
      uri = new URI(id);
    } catch (Exception e) {
      return Optional.empty();
    }

    try (
        final FcrepoClient client = FcrepoClient.client().build();
        FcrepoResponse response = new GetBuilder(uri, client)
            .accept("application/rdf+xml")
            .perform()) {

      String turtleContent = IOUtils.toString(response.getBody(), "UTF-8");

      Resource resource = new Resource(id, turtleContent);

      if(response.getStatusCode() == 200){
        log.info("Found resource with uri {} inside fedora", resource.getPath());
        return Optional.of(resource);
      } else {
        log.debug("Failed to GET resource from fedora at uri: {}", resource.getPath());
        return Optional.empty();  
      }


    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    } catch (FcrepoOperationFailedException e1) {
      log.debug("Fedora GET request returned no result for uri: {}. Original message: {}", uri, e1.getMessage());
      return Optional.empty();
    }

  }

  @Override
  public boolean existsById(String id) {
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
  public void deleteById(String id) {
    
    URI uri = null;
    try {
      uri = new URI(id);
    } catch(URISyntaxException e) {
      log.error("Malformed uri at deteleById operation: for given id {} ", id);
    }
    
    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new DeleteBuilder(uri, client).perform();
    ) {

      log.debug("Resource deletion status: {}", response.getStatusCode());

      if(response.getStatusCode() == 204){
        log.info("Deletion of resource with uri {} was succesful. Got status code: ", id, response.getStatusCode());
      } else {
        log.error("Failed to delete resource with uri {} because of failed status code. Got status code: {} ", id, response.getStatusCode());
      }

    } catch(IOException e){
      log.error("Failed to delete resource with uri {} because if IO ", id);
    } catch (FcrepoOperationFailedException e2) {
      log.error("Failed to delete resource with uri {} from fedora. Because of failed fedora request", id);
    } catch (NullPointerException e3){
      log.error("Failed to delete resource with uri {} from fedora. Unkown error.", id);
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
  public Resource updateResourceTriples(String url, String sparql) throws FcrepoOperationFailedException{

    Optional<Resource> optional = this.findById(url);

    if(!optional.isPresent()) return null;
    
    Resource curResource = optional.get();


    URI uri;
    try {
      uri = new URI(url);
    } catch(URISyntaxException e){
      log.error("URI syntax exception for resource with path: {}", url);
      return null;
    }

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
        throw new FcrepoOperationFailedException(uri, response.getStatusCode(), "Failed to PATCH / update fedora resource " + uri + " with SPARQL:\n " + sparql + "\n");
        //return curResource;
      } else {
        log.error("{} Failed to PATCH / update fedora resource {} with SPARQL: {}", response.getStatusCode(), curResource.getPath(), sparql);
        throw new FcrepoOperationFailedException(uri, response.getStatusCode(), "Failed to PATCH / update fedora resource " + uri + " with SPARQL:\n " + sparql + "\n");
        //return curResource;   
      }

      

    } catch (FcrepoOperationFailedException e){
      log.error("Fedora operation failed. Failed to create fedora resource for path: {}. Fedora message is: ", curResource.getPath(), e.getMessage());
      throw e;
    } catch (IOException e2){
      log.error("IOException: Failed to create fedora resource for uri: {}. {}", curResource.getPath(), e2.getMessage());
    }


    return null;
  }

}
