package org.sebi.springfedora.repository.DigitalObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.fcrepo.client.PutBuilder;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.sebi.springfedora.repository.IResourceRepository;
import org.sebi.springfedora.repository.ResourceRDFMapper;
import org.sebi.springfedora.repository.utils.FedroaPlatformTransactionManager;
import org.sebi.springfedora.repository.utils.RepositoryUtils;
import org.sebi.springfedora.utils.DOResourceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Repository
@Slf4j
public class DigitalObjectRepository implements IDigitalObjectRepository  {

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;

  private FedroaPlatformTransactionManager transactionManager;

  private IResourceRepository resourceRepository;

  private DOResourceMapper doResourceMapper;

  public DigitalObjectRepository(IResourceRepository resourceRepository, DOResourceMapper doResourceMapper, PlatformTransactionManager fedroaPlatformTransactionManager){
    this.resourceRepository = resourceRepository;
    this.doResourceMapper = doResourceMapper;
    this.transactionManager = (FedroaPlatformTransactionManager) fedroaPlatformTransactionManager;
  }

  @Override
  public <S extends DigitalObject> S save(S digitalObject) throws ResourceRepositoryException {
    URI uri =  RepositoryUtils.parseToURI(digitalObject.getPath());

    String txid = transactionManager.getTransactionId();

    // rdf might be null
    // need to use mimetype text/turtle if just the resource should be created
    String triples = digitalObject.getRdfXml() != null ? digitalObject.getRdfXml() : "";
    String curMimetype =  digitalObject.getRdfXml() != null ? "application/rdf+xml" : "text/turtle";

    InputStream triplesIStream = IOUtils.toInputStream(triples, "utf-8");

    log.debug("Initiating PUT request for: {}. With txid {}", digitalObject.getPath(), txid);

    try (
      final FcrepoClient client = FcrepoClient.client().build();
      FcrepoResponse response = new PutBuilder(uri, client)
          .body( triplesIStream, curMimetype)
          //.slug(uri.toString())
          .addHeader("Atomic-ID", txid)
          .perform()
    ) {

      if(response.getStatusCode() == 201 ){
        log.info("Succesfully saved digital object {} with path: {}. With txid {} ", digitalObject.getPid(), digitalObject.getPath(), txid);
        return digitalObject;
      } else if (response.getStatusCode() == 204){
        log.info("Succesfully saved digital object {} with path: {}. Without content. With txid: {}", digitalObject.getPid(), digitalObject.getPath(), txid);
        return digitalObject;
      } else {
        String msg = String.format("Failed to save digital object %s with path: %s. Original message: %s. Txid: %s",digitalObject.getPid(), digitalObject.getPath(), resourceRepository.retrieveFedoraErrBodyMsg(response), txid);
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }
      
    } catch (IOException e) {
      String msg = String.format("Failed to save digital object %s with path: %s. Original message: %s", digitalObject.getPid(), digitalObject.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e1) {
      String msg = String.format("Failed to save digital object %s with path: %s. Against url: %s Original message: %s. With txid: %s For RDF: %s", digitalObject.getPid(), digitalObject.getPath(), uri.toString(), e1.getMessage(),txid, digitalObject.getRdfXml());
      log.error(msg + "\n" + e1);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (Exception e){
      // pass through thrown if status codes are 400+ from above
      if(e instanceof ResourceRepositoryException) throw e;
      
      String msg = String.format("Failed to save digital object %s with path %s. Original message: ", digitalObject.getPid(), digitalObject.getPath(), e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
  }

  @Override
  public <S extends DigitalObject> Iterable<S> saveAll(Iterable<S> entities) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public Optional<DigitalObject> findById(String pid) {
    
    String mappedResourcePath = doResourceMapper.mapObjectResourcePath(pid);
    URI uri = RepositoryUtils.parseToURI(mappedResourcePath);

    try (
        final FcrepoClient client = FcrepoClient.client().build();
        FcrepoResponse response = new GetBuilder(uri, client)
            .accept("application/rdf+xml")
            .perform()
        ) {

      if(response.getStatusCode() == 200){
        String turtleContent = IOUtils.toString(response.getBody(), "UTF-8");
        String[] datastreams = ResourceRDFMapper.parseRDFChildren(turtleContent);
        DigitalObject digitalObject = new DigitalObject(pid, mappedResourcePath,turtleContent, datastreams);
        log.info("Found digital object with pid {} and path {} inside fedora",digitalObject.getPid(), digitalObject.getPath());
        return Optional.of(digitalObject);
      } else if(response.getStatusCode() == 404) {
        log.info("GET request for digital object {} succesfull. Found no resource at given path {}. ",pid, uri.toString());
        return Optional.empty();
      } else {
        String msg = String.format("Failed to GET digital object with pid {} from fedora at uri: %s. Original resource response body: %s", pid, uri.toString(), resourceRepository.retrieveFedoraErrBodyMsg(response));
        log.error(msg);
        throw new ResourceRepositoryException(response.getStatusCode(), msg);
      }

    } catch (IOException e) {
      String msg = String.format("GET request for digital object {} with path: %s failed. Original err msg: %s",pid , mappedResourcePath, e.getMessage());
      log.error(msg + "\n" + e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    } catch (FcrepoOperationFailedException e1) {
      String msg = String.format("GET request for digital object {} with path: %s failed. Original err msg: %s",pid , mappedResourcePath, e1.getMessage());
      log.error(msg + "\n" + e1);
      throw new ResourceRepositoryException(e1.getStatusCode(), msg);
    }
  }

  @Override
  public boolean existsById(String pid) throws ResourceRepositoryException {
    return !this.findById(pid).isEmpty();
  }

  @Override
  public Iterable<DigitalObject> findAll() throws ResourceRepositoryException {
    //throw new NotImplementedException("Method not implemented!");

    RestTemplate restTemplate = new RestTemplate();
    String simpleSearchEndpoint = String.format("%s%sfcr:search?condition=rdf_type=*BasicContainer*", this.curHost, this.fedoraRESTEndpoint);
      // "http://localhost:8082/rest/fcr:search?condition=rdf_type=*BasicContainer*";
    ResponseEntity<String> response
      = restTemplate.getForEntity(simpleSearchEndpoint, String.class);

    if(response.getStatusCode() != HttpStatus.OK){
      String msg = String.format("Failed to get all objects from simple search api via url: %s. Got status code from endpoint: %s", simpleSearchEndpoint, response.getStatusCode());
      log.error(msg);
      throw new ResourceRepositoryException(response.getStatusCode().value(), msg);
    }

    log.info("GET requested all digital objects from fedora6 via url: {}", simpleSearchEndpoint);

    byte[] responseBody = response.getBody().getBytes();

    // from here parsing of response body and return as model = DigitalObject
    List<DigitalObject> digitalObjects = new ArrayList<>();

    JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    try {
      JSONObject root = (JSONObject) jsonParser.parse(responseBody);
      JSONArray items = (JSONArray) root.get("items");

      items.forEach(itemObj -> {

        JSONObject curitem = (JSONObject) itemObj;
        String fedoraId = (String) curitem.get("fedora_id");
        
        // needed to correctly construct pid out of fedora6 path
        if(fedoraId.contains("/objects/")){
          String pid = String.format("o:%s", fedoraId.split("/objects/")[1]);
          digitalObjects.add(new DigitalObject(pid, fedoraId, ""));
        } else if(fedoraId.contains("/aggregations/context/")){
          String pid = String.format("context:%s", fedoraId.split("/aggregations/context/")[1]);
          digitalObjects.add(new DigitalObject(pid, fedoraId, ""));
        } else if(fedoraId.contains("/aggregations/query/")){
          String pid = String.format("query:%s", fedoraId.split("/aggregations/query/")[1]);
          digitalObjects.add(new DigitalObject(pid, fedoraId, ""));
        } else if(fedoraId.contains("/aggregations/corpus/")){
          String pid = String.format("corpus:%s", fedoraId.split("/aggregations/corpus/")[1]);
          digitalObjects.add(new DigitalObject(pid, fedoraId, ""));
        }

        
      });

    } catch (ParseException e) {
      e.printStackTrace();
      String msg = String.format("Failed to parse json returned from fedora at {}. Got response body: {} ", simpleSearchEndpoint, response.getBody());
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.UNPROCESSABLE_ENTITY.value(), msg);
    }

    return digitalObjects;

  }

  @Override
  public Iterable<DigitalObject> findAllById(Iterable<String> ids) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public long count() {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public void deleteById(String id) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void delete(DigitalObject entity) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAll(Iterable<? extends DigitalObject> entities) {
    throw new NotImplementedException("Method not implemented!");
    
  }

  @Override
  public void deleteAll() {
    throw new NotImplementedException("Method not implemented!");
  }
  
}
