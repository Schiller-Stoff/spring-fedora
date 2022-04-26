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

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for Fedora. Allows to process specific fedora responses etc.
 */
@Slf4j
public class FedoraUtils {
  
  /**
   * Clones given resource metadata as string AND removes system specific statements like fedora: namespace stuff
   * (creation date via fedora) 
   * Does not apply new data from to be cloned object.
   * @param sourceRdfXml {String} resource's rdf metadata as xml
   * @return {String} new resource metadata rdf xml as string.
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public static String cloneResourceMetadata(String sourceRdfXml) throws SAXException, IOException, ParserConfigurationException{
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(IOUtils.toInputStream(sourceRdfXml, "utf-8"));

      doc.getDocumentElement().normalize();

      doc = removeFedoraSystemTriples(doc);

      // add given triples?
      return documentToString(doc);
  }


  /**
   * 
   * @return
   */
  private static Document removeFedoraSystemTriples(Document metadataRdfXml){

    Node rdfDescription = metadataRdfXml.getElementsByTagName("rdf:Description").item(0);

      NodeList children = rdfDescription.getChildNodes();


      String[] forbiddenURIs = {
        "http://fedora.info/definitions/v4/repository#Resource", 
        "http://www.w3.org/ns/ldp#BasicContainer", 
        "http://www.w3.org/ns/ldp#Resource", 
        "http://www.w3.org/ns/ldp#RDFSource",
        "http://www.w3.org/ns/ldp#Container",
        "http://fedora.info/definitions/v4/repository#Container"
      };

      for (int i = 0; i < children.getLength(); i++){
        Node child = children.item(i);
        
        if(child.getNodeName() == "#text")continue;

        log.debug(child.getNodeName());
        if(child.getNodeName().contains("fedora:")) {
          rdfDescription.removeChild(child);
          continue;
        };

        if(child.getNodeName() == "rdf:type"){

          String curVal = child.getAttributes().getNamedItem("rdf:resource").getNodeValue();

         if(Arrays.asList(forbiddenURIs).contains(curVal)) rdfDescription.removeChild(child);


        }
        
      }

      return metadataRdfXml;

  }



  /**
   * Outputs given w3.Document as string.
   * @param doc {Document} w3c Document to output as string
   * @return Document as String
   */
  private static String documentToString(Document doc) {
    try {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    } catch (Exception ex) {
        throw new RuntimeException("Error converting to String", ex);
    }
}

}
