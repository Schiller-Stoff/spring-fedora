package org.sebi.springfedora.repository.utils;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.TransactionRequiredException;

import org.apache.commons.lang3.StringUtils;
import org.fcrepo.client.DeleteBuilder;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.PostBuilder;
import org.fcrepo.client.PutBuilder;
import org.sebi.springfedora.Common;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FedroaPlatformTransactionManager extends AbstractPlatformTransactionManager {

  @Value("${gams.curHost}")
  private String curHost;

  @Value("${gams.fedoraRESTEndpoint}")
  private String fedoraRESTEndpoint;

  private final String FEDORA_TRANSACTION_ENDPOINT = "fcr:tx";

  private String txid;

  public FedroaPlatformTransactionManager() {
    // not allowed to be null
    this.txid = "";
    log.info("Succesfully instantiated custom FedoraPlatformTransactionManager as extension of AbstractPlatformTransactionManager");
  }

  @Override
  protected String doGetTransaction() throws TransactionException {

    if(txid == null){
      throw new TransactionSystemException("Transaction id should never be null! ");
    }

    return this.txid;
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {

    FcrepoClient client = new FcrepoClient.FcrepoClientBuilder().build();

    try {
      URI uri = URI.create(curHost + fedoraRESTEndpoint + FEDORA_TRANSACTION_ENDPOINT);
      FcrepoResponse response = new PostBuilder(uri, client).perform();

      if (response.getStatusCode() == 201) {
        this.txid = StringUtils.substringAfter(response.getLocation().getPath(), FEDORA_TRANSACTION_ENDPOINT + "/");
        log.info("POST 201: Succesfully created new fedora transaction with txid: {} - from endpoint: {}", txid,
            uri.toString());
        return;
      } else {
        String msg = String.format("Status code: %s Failed POST request against fedora for txid against %s. ",
            response.getStatusCode(), uri.toString());// IMPLEMENT!!!
        log.error(msg);
        throw new TransactionSystemException(msg);
      }

    } catch (NullPointerException | IllegalArgumentException e) {
      String msg = String.format("Failed at preprocessing for requesting txid from fedora against %s",
          FEDORA_TRANSACTION_ENDPOINT);
      log.error(msg + "\n" + e);
      throw new TransactionSystemException(msg);
    } catch (FcrepoOperationFailedException e2) {
      String msg = String.format("Failed at creation of new fedora transaction via POST against %s",
          FEDORA_TRANSACTION_ENDPOINT);
      log.error(msg + "\n" + e2);
      throw new TransactionSystemException(msg);
    }

  }

  @Override
  protected void doCommit(DefaultTransactionStatus status) throws TransactionException {

    FcrepoClient client = new FcrepoClient.FcrepoClientBuilder().build();
    String curTxid = this.doGetTransaction();

    try {
      URI uri = URI.create(curHost + fedoraRESTEndpoint + FEDORA_TRANSACTION_ENDPOINT + "/" + curTxid);
      FcrepoResponse response = new PutBuilder(uri, client).perform();

      if (response.getStatusCode() == 204) {
        log.info("PUT 204: Succesfully commited fedora transaction with txid: {} - against endpoint: {}", curTxid,
            uri.toString());
        return;
      } else {
        String msg = String.format(
            "Status code: %s Failed transaction commit (= via PUT request) with txid %s against fedora endpoint %s. ",
            response.getStatusCode(), curTxid, uri.toString());// IMPLEMENT!!!
        log.error(msg);
        throw new TransactionSystemException(msg);
      }

    } catch (NullPointerException | IllegalArgumentException e) {
      String msg = String.format("Failed at preprocessing for transaction commit with txid %s from fedora", curTxid);
      log.error(msg + "\n" + e.getMessage());
      throw new TransactionSystemException(msg);
    } catch (FcrepoOperationFailedException e2) {
      String msg = String.format(
          "Fcrepo exception. Failed at commit of fedora transaction with txid %s. Got status code: %s", curTxid,
          e2.getStatusCode());
      log.error(msg + "\n" + e2.getMessage());
      throw new TransactionSystemException(msg);
    }

  }

  @Override
  protected void doRollback(DefaultTransactionStatus status) throws TransactionException {

    log.debug("Starting rollback of transaction...");

    FcrepoClient client = new FcrepoClient.FcrepoClientBuilder().build();
    String curTxid = this.doGetTransaction();

    try {
      URI uri = URI.create(curHost + fedoraRESTEndpoint + FEDORA_TRANSACTION_ENDPOINT + "/" + curTxid);
      FcrepoResponse response = new DeleteBuilder(uri, client).perform();
      log.debug("Transaction rollback status and location: {} {}", response.getStatusCode(), uri.toString());

      if (response.getStatusCode() == 204) {
        log.info(
            "DELETE 204: Succesfully rolled back and closed fedora transaction with txid: {} - against endpoint: {}",
            curTxid, uri.toString());
        return;
      } else {
        String msg = String.format(
            "Status code: %s Failed transaction rollback (= via DELETE request) with txid %s against fedora endpoint %s. ",
            response.getStatusCode(), curTxid, uri.toString());// IMPLEMENT!!!
        log.error(msg);
        throw new TransactionSystemException(msg);
      }

    } catch (NullPointerException | IllegalArgumentException e) {
      String msg = String.format("Failed at preprocessing for transaction rollback with txid %s from fedora", curTxid);
      log.error(msg + "\n" + e.getMessage());
      throw new TransactionSystemException(msg);
    } catch (FcrepoOperationFailedException e2) {
      String msg = String.format(
          "Fcrepo exception. Failed at rollback of fedora transaction with txid %s. Got status code: %s", curTxid,
          e2.getStatusCode());
      log.error(msg + "\n" + e2.getMessage());
      throw new TransactionSystemException(msg);
    }

  }

  /**
   * Returns currently active transaction from FedoraTransactionManager instance.
   * @return {String} transaction id of currently running transaction.
   * @throws ResourceRepositoryException if transaction id is null or empty string. 
   */
  public String getTransactionId() throws ResourceRepositoryException {
    if ((this.txid == null) || (this.txid == "")){
      String msg = String.format("Demanded INVALID transaction id. Transaction id (txid) is null or '' but was demanded for fedora http operation");
      log.error(msg);
      throw new ResourceRepositoryException(HttpStatus.UNPROCESSABLE_ENTITY.value(), msg);
    }

    return doGetTransaction();
  }

}
