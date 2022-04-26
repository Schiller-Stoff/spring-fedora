package org.sebi.springfedora.repository.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Wrapper class for rdf metadata returned from fedora 6.1.
 * Parses given metadata rdfXml as w3c.Document and provides basic functionality 
 * to update associated RDF.
 */
public class FedoraMetadata {

  private Document doc;
  private Node rdfDescription;

  public FedoraMetadata(String metadataRdfXml) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    this.doc = builder.parse(IOUtils.toInputStream(metadataRdfXml, "utf-8"));
    this.doc.getDocumentElement().normalize();

    rdfDescription = this.doc.getElementsByTagName("rdf:Description").item(0);
  }

  /**
   * 
   * @return
   */
  public FedoraMetadata removeFedoraSystemTriples() {

    //Node rdfDescription = this.doc.getElementsByTagName("rdf:Description").item(0);

    NodeList children = this.rdfDescription.getChildNodes();

    String[] forbiddenURIs = {
        "http://fedora.info/definitions/v4/repository#Resource",
        "http://www.w3.org/ns/ldp#BasicContainer",
        "http://www.w3.org/ns/ldp#Resource",
        "http://www.w3.org/ns/ldp#RDFSource",
        "http://www.w3.org/ns/ldp#Container",
        "http://fedora.info/definitions/v4/repository#Container"
    };

    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);

      if (child.getNodeName() == "#text")
        continue;

      if (child.getNodeName().contains("fedora:")) {
        rdfDescription.removeChild(child);
        continue;
      }
      ;

      if (child.getNodeName() == "rdf:type") {

        String curVal = child.getAttributes().getNamedItem("rdf:resource").getNodeValue();

        if (Arrays.asList(forbiddenURIs).contains(curVal))
          rdfDescription.removeChild(child);

      }

    }

    return this;

  }

  /**
   * Replaces pid in root rdf:description about attribute through given pid.
   * @param pid {String} to be filled in to rdf:description about=
   * @return {FedoraMetadataDocument} current instance of class
   */
  public FedoraMetadata replaceResourcePath(String path){
    this.rdfDescription.getAttributes().getNamedItem("rdf:about").setTextContent(path);
    return this;
  }

  /**
   * Outputs given w3.Document as string.
   * 
   * @param doc {Document} w3c Document to output as string
   * @return Document as String
   */
  public String serializeToString() {
    try {
      StringWriter sw = new StringWriter();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      transformer.transform(new DOMSource(this.doc), new StreamResult(sw));
      return sw.toString();
    } catch (Exception ex) {
      throw new RuntimeException("Error converting to String", ex);
    }
  }

}
