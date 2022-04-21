package org.sebi.springfedora.repository.Datastream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoLink;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.PostBuilder;
import org.fcrepo.client.PutBuilder;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.Datastream;
import org.sebi.springfedora.repository.ResourceRepository;
import org.sebi.springfedora.repository.utils.RepositoryUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DatastreamRepository implements IDatastreamRepository {

  private ResourceRepository resourceRepository;

  public DatastreamRepository(ResourceRepository resourceRepository){
    this.resourceRepository = resourceRepository;
  }

  @Override
  public <S extends Datastream> S save(S datastream) throws ResourceRepositoryException {

    verifyExpecetedMimetype(datastream);

    URI uri = RepositoryUtils.parseToURI(datastream.getPath()); 
    String curMimetype = datastream.getMimeType().toString();


    // String triples = resource.getRdfXml() != "" ? Common.ADDRDFPROPERTY.replace("$1", resource.getRdfXml()) : "";
    String triples = datastream.getRdfXml();
    InputStream triplesIStream = IOUtils.toInputStream(datastream.getRdfXml(), "utf-8");

    log.info("Initiating PUT request for datastream: {}. With rdf: {}", datastream.getPath(), triples);

    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new PutBuilder(uri, client)
          .body( triplesIStream, curMimetype)
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
    throw new NotImplementedException("Method not implemented yet");
  }

  @Override
  public boolean existsById(String id) {
    return resourceRepository.existsById(id);
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
    throw new NotImplementedException("Method not implemented yet");
    
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
    // TODO Auto-generated method stub
    
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
    String mimetype = datastream.getMimeType().toString();
     

    if(rdfFormats.contains(mimetype)){
      String msg = String.format("Mimetype of given datastream should not allow RDF / SPARQL. At datastream with path: %s. Got mimetype %s. Forbidden mimetypes for datastream creation: %s", mimetype, datastream.getPath(), rdfFormats);
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), msg);
    }
    
  }


}
