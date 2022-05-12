package org.sebi.springfedora.repository.Datastream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.fcrepo.client.PutBuilder;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.ResourceRepository;
import org.sebi.springfedora.repository.utils.FedroaPlatformTransactionManager;
import org.sebi.springfedora.repository.utils.RepositoryUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.MimeType;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DatastreamRepository implements IDatastreamRepository {

  private ResourceRepository resourceRepository;
  private FedroaPlatformTransactionManager fedroaPlatformTransactionManager;

  public DatastreamRepository(ResourceRepository resourceRepository, PlatformTransactionManager platformTransactionManager){
    this.resourceRepository = resourceRepository;
    this.fedroaPlatformTransactionManager = (FedroaPlatformTransactionManager) platformTransactionManager;
  }

  @Override
  public <S extends Datastream> S save(S datastream) throws ResourceRepositoryException {

    verifyExpecetedMimetype(datastream);

    URI uri = RepositoryUtils.parseToURI(datastream.getPath()); 
    String curMimetype = datastream.getMimetype().toString();

    String triples = datastream.getRdfXml();

    InputStream content = new ByteArrayInputStream(datastream.getContent()); 

    log.info("Initiating PUT request for datastream: {}. With rdf: {}", datastream.getPath(), triples);

    String txid = fedroaPlatformTransactionManager.getTransactionId();

    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new PutBuilder(uri, client)
          .body( content, curMimetype)
          .addHeader("Atomic-ID", txid)
          .perform()
    ) {

      if(response.getStatusCode() == 201 ){
        log.info("Succesfully saved resource with path: {} ", datastream.getPath());
        return datastream;
      } else if (response.getStatusCode() == 204){
        log.info("Succesfully saved resource with path: {}. Without content", datastream.getPath());
        return datastream;
      } else {
        String msg = String.format("Failed to save datastream with path: %s. Original message: %s", datastream.getPath(), resourceRepository.retrieveFedoraErrBodyMsg(response));
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }
      
    } catch (IOException e) {
      String msg = String.format("Failed to save resource with path: %s. Original message: %s", datastream.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e1) {
      String msg = String.format("Failed to save datastream with path: %s. Original message: %s", datastream.getPath(), e1.getMessage());
      log.error(msg + "\n" + e1);
      throw new ResourceRepositoryException(e1.getStatusCode(), msg);
    } catch (Exception e){
      // pass through thrown if status codes are 400+ from above
      if(e instanceof ResourceRepositoryException) throw e;

      String msg = String.format("Failed to save datastream at path %s. Original message: ", datastream.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    
  }

  @Override
  public <S extends Datastream> Iterable<S> saveAll(Iterable<S> entities) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public Optional<Datastream> findById(String id) {
    URI uri = RepositoryUtils.parseToURI(id);

    try (
        final FcrepoClient client = FcrepoClient.client().build();
        FcrepoResponse response = new GetBuilder(uri, client)
            .perform()
    ) {

      //String turtleContent = IOUtils.toString(response.getBody(), "UTF-8");
      MimeType mimeType = MimeType.valueOf(response.getContentType());
      byte[] content = IOUtils.toByteArray(response.getBody());

      Datastream datastream = new Datastream(id, "", mimeType, content);

      if(response.getStatusCode() == 200){
        log.info("Found resource with uri {} inside fedora", datastream.getPath());
        return Optional.of(datastream);
      } else if(response.getStatusCode() == 404) {
        log.info("GET request for resource {} succesfull. Found no resource at given path. ", datastream.getPath());
        return Optional.empty();
      } else {
        String msg = String.format("Failed to GET resource from fedora at uri: %s. Original resource response body: %s", datastream.getPath(), resourceRepository.retrieveFedoraErrBodyMsg(response));
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
  public boolean existsById(String id) {
    return !this.findById(id).isEmpty();
  }

  @Override
  public Iterable<Datastream> findAll() {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public Iterable<Datastream> findAllById(Iterable<String> ids) {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public long count() {
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public void deleteById(String id) {
    resourceRepository.deleteById(id);
    resourceRepository.deleteById(id + "/fcr:tombstone");
    //throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void delete(Datastream entity) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAll(Iterable<? extends Datastream> entities) {
    throw new NotImplementedException("Method not implemented yet");
    
  }

  @Override
  public void deleteAll() {
    throw new NotImplementedException();
    
  }
  

  /**
   * Will throw if mimetype corresponds to fedora supported rdf formats.
   * Only if not rdf will the resource be saved as binary.
   * 
   * See https://wiki.lyrasis.org/display/FEDORAM6M1P0/REST+API+Specification
   * @param datastream
   * @throws ResourceRepositoryException
   */
  private void verifyExpecetedMimetype(Datastream datastream) throws ResourceRepositoryException{
    
    String rdfFormats = "text/turtle, text/rdf+n3, application/n3, text/n3, application/rdf+xml, application/n-triples, application/ld+json";
    String mimetype = datastream.getMimetype().toString();
     

    if(rdfFormats.contains(mimetype)){
      String msg = String.format("Mimetype of given datastream should not allow RDF / SPARQL. At datastream with path: %s. Got mimetype %s. Forbidden mimetypes for datastream creation: %s", mimetype, datastream.getPath(), rdfFormats);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), msg);
    }
    
  }


}
