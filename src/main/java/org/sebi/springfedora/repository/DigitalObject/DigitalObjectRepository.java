package org.sebi.springfedora.repository.DigitalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.NotImplementedException;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.sebi.springfedora.model.DigitalObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
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

  @Override
  public <S extends DigitalObject> S save(S entity) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public <S extends DigitalObject> Iterable<S> saveAll(Iterable<S> entities) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public Optional<DigitalObject> findById(String id) {
    throw new NotImplementedException("Method not implemented!");
  }

  @Override
  public boolean existsById(String id) {
    throw new NotImplementedException("Method not implemented!");
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
