package org.sebi.springfedora.repository;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.sebi.springfedora.model.DigitalObject;
import org.springframework.stereotype.Repository;

@Repository
public class DigitalObjectRepository implements IDigitalObjectRepository {

  @Override
  public <S extends DigitalObject> S save(S entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S extends DigitalObject> Iterable<S> saveAll(Iterable<S> entities) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<DigitalObject> findById(String id) {

    URI uri = null;
    try {
      uri = new URI("http://localhost:8082/rest/image-collection");
    } catch (Exception e){
      return null;
    }
    
    //FcrepoClient client = FcrepoClient.client().build();

    try (
        final FcrepoClient client = FcrepoClient.client().build();
        FcrepoResponse response = new GetBuilder(uri, client)
          .accept("application/rdf+xml")
          .perform()
    ) {

      String turtleContent = IOUtils.toString(response.getBody(), "UTF-8");
      System.out.println(turtleContent);
      
    } catch (IOException e) {
		
		e.printStackTrace();
	} catch (FcrepoOperationFailedException e1) {
		
		e1.printStackTrace();
	}

    return null;
  }

  @Override
  public boolean existsById(String id) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterable<DigitalObject> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<DigitalObject> findAllById(Iterable<String> ids) {
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
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(DigitalObject entity) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteAllById(Iterable<? extends String> ids) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteAll(Iterable<? extends DigitalObject> entities) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteAll() {
    // TODO Auto-generated method stub

  }

}
