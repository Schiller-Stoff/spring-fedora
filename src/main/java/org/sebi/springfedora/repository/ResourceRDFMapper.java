package org.sebi.springfedora.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.sebi.springfedora.exception.ResourceRepositoryException;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResourceRDFMapper {

  public static String[] parseRDFChildren(String fedoraResourceXml) {

    List<String> ldpChildren = new ArrayList<>();

    try {
      // TOO NAMESPACES
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      // Document doc = builder.parse(rdfXml);

      InputStream rdfInputStream = IOUtils.toInputStream(fedoraResourceXml, "utf-8");
      Document doc = builder.parse(rdfInputStream);
      doc.getDocumentElement().normalize();

      NodeList ldpContains = doc.getElementsByTagName("ldp:contains");

      // loop through stuff and return things
      for (int i = 0; i < ldpContains.getLength(); i++) {

        // get resource elem
        Node ldpContainsElem = ldpContains.item(i);
        // read out rdf:resource attribute
        String datastreamURI = ldpContainsElem.getAttributes().getNamedItem("rdf:resource").getTextContent();
        // add to array
        ldpChildren.add(datastreamURI);
      }

      return ldpChildren.toArray(new String[ldpChildren.size()]);

    } catch (IOException | ParserConfigurationException | SAXException | IllegalArgumentException e) {
      String msg = String.format("Parsing of rdf ldp:contains failed. Original message: %s. At xml: %s --- Stacktrace: %s", e.getMessage(), fedoraResourceXml, e);
      throw new ResourceRepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

  }

}
