package org.sebi.springfedora;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


/**
* Container class for heavily used constants. Like SPARQL query-templates, location of fcrepo/rest interface;
* ports and addresses; file locations; prefixes; login ids; schema locations and definitions et.c
* 
* Contains often needed SPARQLS (like ALL query - to find all digital objects) and 
* template SPARQLs (where values are being filled in by replacing expected $ signs).
* 
* Contains addresses to all internal and external requested container-applications like to blazegraph etc.
* OR cab webservice under https://www.deutschestextarchiv.de/demo/cab/query
* @author Johannes Stigler
*/

public class Common {

 
 public static final String VERSION = "FCGate Version 0.0.3-SNAPSHOT (c) by ZIM, Austria";
 public static final String FCREPO_HOST ="fcrepo4:8080";
 
 /**
  * fcrepo/rest endpoint in fedora container
  */
 public static final String FCREPO_SERVICE = "/fcrepo/rest";
 public static final String FCREPO_REST ="http://"+FCREPO_HOST+FCREPO_SERVICE+"/";
   public static final String TRIPLESTORE = "http://triplestore:8080/bigdata";
   public static final String BLAZEGRAPH = "http://blazegraph:8080/bigdata";
   public static final String TREETAGGER = "http://treetagger:8080/treetagger";
   public static final String NEXUSCUBE = "http://nexuscube:8080/nexuscube";
   public static final String ARCHIVER = "http://archiver:8080/archiver/archive";
   public static final String RELIGHT = "http://relight:8080/relight-cli";
   public static final String VEROVIO = "http://verovio:8080/verovio";
 public static final String CM4F_HOST ="http://cm4f.org";
   public static final String FCREPO_APIXURI = "http://apix:8080/services";	
   public static final String CAB_WEBSERVICE = "https://www.deutschestextarchiv.de/demo/cab/query";
   
   /**
    * URL of apache root. (Protocol and port)
    */
   public static final String APACHE = "http://apache:82/";
   public static final String APACHE_ROOT = "/var/apache/";
   /**
    * Prototype base data files (rdf / dublin core / htmlstream / thubmnail) 
    * as content models stored in apache/www/models.
    * https://gedra.uni-graz.at/svn/denv/trunk/fcrepo4/master/apache/www/models/
    * 
    */
   public static final String CONTENTMODELS = APACHE + "models";
   public static final String CIRILO_PROPERTIES = APACHE +"cirilo.properties";
   public static final String CIRILO_PID = "o:cirilo.properties";
   /**
    * url to apache config
    */
   public static final String CONFIG = APACHE + "config";
   public static final String LORIS_HOST = "loris:5000";
   /**
    * Set mimetype length in GAMS
    */
   public static final int    MIMETYPE_LENGTH = 120;
   public static final String ACCESS_TOKEN = "access_token";
   public static final String LOGID = "logid";
   
   /**
    * info:fedora/ statement
    */
   public static final String INFO_FEDORA = "info:fedora/";
   public static final String DC_DEFAULT_TITLE = "Unknown";
   /**
    * Datastream REST url component used in GAMS
    */
   public static final String DATASTREAMS = "datastream"; 
   public static final String X_FORWARDED_HOST = "x-forwarded-host";
   public static final String TXID = "txid";
   public static final String FITS_SERVICE = "http://fits:8082/fits/examine";
   public static final String HDL_PREFIX  = "hdl:";
   public static final Pattern VALID_PID_PATTERN = Pattern.compile("(o|context|query|corpus|cirilo):([a-zA-Z0-9])([-._a-zA-Z0-9]){1,}");
   public static final int    MAXREPEATS = 20;
   
   public static final String SUPERUSER = "superuser";
    
 public static enum Schemes {XMLSchema, RelaxNG, RelaxNGCompact};

   
   public static final String SERVICE_NOT_FOUND = "No Fedora 4.x service found on ";
   public static final String FITS_SERVICE_NOT_FOUND = "FITS service not found";
   public static final String SOFTWARE_AGENT = "Created by Cirilo 6.0";
   
   public static final String CHAMOIS        = "chamois";

   /**
    * SPARQL string template.
    * Used as wrapper for a DELETE SPARQL filled in with parameters.
    * $1 -> body of DELETE SPARQL
    */
   public static final String DELRDFPROPERTY =  "PREFIX rel: <info:fedora/fedora-system:def/relations-external#> "+
                            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                         "PREFIX dc: <http://purl.org/dc/elements/1.1/> "+
                         "PREFIX xsl: <http://cm4f.org/xsl/> "+
                         "PREFIX model: <info:fedora/fedora-system:def/model#> "+
                         "PREFIX premis: <http://www.loc.gov/premis/rdf/v1#> "+
                         "PREFIX dcterms: <http://purl.org/dc/terms/> "+
                         "PREFIX cm4f: <http://cm4f.org/> "+
                         "PREFIX edm: <http://www.europeana.eu/schemas/edm/> "+	
                         "PREFIX fedora: <http://fedora.info/definitions/v4/repository#> "+
                          "DELETE { <> $1 } where {}";
   
   /**
    * SPARQL INSERT query-string template. 
    * Replace content via replacing $1 in string template.
    * Provides necessary PREFIXEs. 
    */
   public static final String ADDRDFPROPERTY =  "PREFIX rel: <info:fedora/fedora-system:def/relations-external#> "+
                          "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                         "PREFIX dc: <http://purl.org/dc/elements/1.1/> "+
                         "PREFIX xsl: <http://cm4f.org/xsl/> "+
                         "PREFIX model: <info:fedora/fedora-system:def/model#> "+
                         "PREFIX premis: <http://www.loc.gov/premis/rdf/v1#> "+
                         "PREFIX dcterms: <http://purl.org/dc/terms/> "+
                         "PREFIX cm4f: <http://cm4f.org/> "+
                         "PREFIX edm: <http://www.europeana.eu/schemas/edm/> "+
                         "PREFIX fedora: <http://fedora.info/definitions/v4/repository#> "+
                         "INSERT { <> $1 } where {}";
   
   /**
    * SPARQL string templaate
    * $pid -> needs to be string-replaced via pid of digital object.
    * Retrieves pid and title according to rel:isMemberOf.
    */
   public static final String ISMEMBEROF =      "PREFIX dc: <http://purl.org/dc/elements/1.1/> "+
                            "PREFIX dcterms: <http://purl.org/dc/terms/> "+
                         "PREFIX rel: <info:fedora/fedora-system:def/relations-external#> "+
                         "select ?pid ?title where { $pid rel:isMemberOf ?pid . "+                                                   
                                                " ?oid dcterms:identifier ?pid. " +
                         " ?oid dc:title ?title . }";
   
   /**
    * SPARQL string template.
    * $pid needs to be string replaced. 
    * Retrieves pipelines for object with given pid. 
    * Filters for http://cm4f.org/hasXsltPipelines and http://cm4f.org/xsl 
    */
   public static final String PIPELINES =       "select * where {$pid ?pipeline ?url . " +  
                          "filter(regex(str(?pipeline), '^http://cm4f.org/xsl') || regex(str(?pipeline), '^http://cm4f.org/hasXsltPipelines'))}";
   
   /**
    * SPARQL string template for SETOWNER operation for fedora.
    * $1 Replace through the owner to be deleted
    * $2 Insert new owner.
    */
   public static final String SETOWNER =        "PREFIX cm4f: <http://cm4f.org/> "+
                                          "DELETE { <> cm4f:owner '$1' } INSERT { <> cm4f:owner '$2' } where {}";

   /**
    * SPARQL string template.
    * $model / $owner / $handle / $filter -> need to be string replaced 
    * Retrieves all digital objects pid / model / owner / handle / modified / derived for given parameters 
    */
   public static final String OBJECTS =         "select distinct ?pid (group_concat(?xtitle; separator='; ') as ?title) ?model ?owner ?handle ?modified ?derived where {"+
                          "?oid <info:fedora/fedora-system:def/model#hasModel> ?model ; "+
                          "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cm4f.org/Object> ; " + 
                          "<http://purl.org/dc/elements/1.1/title> ?xtitle ; "+
                          "<http://purl.org/dc/elements/1.1/identifier> ?pid ; "+
                            " $model $owner "+ 
                          " $handle "+
                          " optional { ?oid <http://cm4f.org/lastModified> ?modified } . " +
                          " optional { ?oid <http://cm4f.org/isDerivedFrom> ?derived } . " +
                           " $filter } group by ?pid ?model ?owner ?handle ?modified ?derived order by ?pid $limit";
   
   /**
    * SPARQL string template.
    * $model
    * $owner
    * $handle
    * $filter
    * $limit
    * Used to retrieve object counts as ?n with string replaced constraints.
    */
   public static final String N =        	     "select (count(distinct ?oid) as ?n) where {"+
                          "?oid <info:fedora/fedora-system:def/model#hasModel> ?model ; "+
                          "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cm4f.org/Object> ; " + 
                           "<http://purl.org/dc/elements/1.1/identifier> ?pid ; "+
                           "<http://purl.org/dc/elements/1.1/title> ?title. "+
                           " $model $owner "+ 
                          " $handle "+
                             " $filter } $limit";
   
   /**
    * SPARQL string template.
    * $modified -> fill with rdf:type <http://cm4f.org/hasBeenModified>; to retrieve all objects that have been modified.
    * Retrieves pids of objects. If $modified is set to above it will return only modified objects
    */
   public static final String MODIFIED =        "select ?pid where {"+
                         "?oid <info:fedora/fedora-system:def/model#hasModel> ?model ; "+
                         "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cm4f.org/Object> ; " + 
                         "$modified "+
                         "<http://purl.org/dc/elements/1.1/identifier> ?pid }";
   
   public static final String ARCHIVED =        "select ?path where {?path <http://purl.org/dc/elements/1.1/identifier> '$pid' }"; 

   /**
    * Just contains 'rdf:type cm4f:hasBeenModified'
    * Used to update has been modified for a digital object in fedora.
    */
   public static final String HasBeenMODIFIED = "rdf:type cm4f:hasBeenModified";
   
   /**
    * SPARQL query string template. 
    * Retrieves all prototypes via <http://cm4f.org/Prototype> with pid / (content-)model and title.
    * Filters via regex for "g_" in prototype pids to filter superprototypes from result.
    * Group statements '[$]groups' need to string replaced.
    */
   public static final String PROTOTYPES =      "select distinct ?pid ?model ?title where {" + 
                          "?pre  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cm4f.org/Prototype>. " + 
                          "?pre  <http://purl.org/dc/elements/1.1/identifier> ?pid. " + 
                          "?pre  <http://purl.org/dc/elements/1.1/title> ?title. " + 
                          "?pre <info:fedora/fedora-system:def/model#hasModel> ?model. " + 
                          "filter (regex(str(?pid), \"[a-z]+:prototype.g_$groups.*\"))} order by ?model";
   
   /**
    * SPARQL query. 
    * Retrieves all superprototypes via <http://cm4f.org/Prototype> with pid / (content-)model and title.
    * Filters via regex for "g_" not in prototype pids to filter only for group prototypes from result.
    */
   public static final String SUPERPROTOTYPES = "select distinct ?pid ?model ?title where {" + 
                         "?pre  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cm4f.org/Prototype>. " + 
                         "?pre  <http://purl.org/dc/elements/1.1/identifier> ?pid. " + 
                         "?pre  <http://purl.org/dc/elements/1.1/title> ?title. " + 
                         "?pre <info:fedora/fedora-system:def/model#hasModel> ?model. " + 
                         "filter (regex(str(?pid), \"[a-z]+:prototype.[a-z]+$\"))} order by ?model";

   /**
    * SPARQL query. Finds all digital objects via
    * search for rdf:type cm4f:Object.
    * Gives access to found pids. 
    */
   public static final String ALL         =     "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                           "prefix cm4f: <http://cm4f.org/> "+ 
                             "prefix dc: <http://purl.org/dc/elements/1.1/> "+
                             "prefix model: <info:fedora/fedora-system:def/model#> "+
                        "select ?pid where { ?s rdf:type cm4f:Object; "+
                        "   dc:identifier ?pid; "+											 
                            "   model:hasModel ?model "+
                            "	 values (?model) {(cm4f:TEI) (cm4f:MEI) (cm4f:LIDO) (cm4f:CUBE) (cm4f:GML) (cm4f:RDF) (cm4f:SKOS) (cm4f:Ontology) } " +

                        "}";
  
   /** 
    * XML string template.
    * GAMS TEI-P-IDNO pid convention.
    * TEI paragraph with nested idno for defining pid of TEI_SOURCE.
    * $recplacement needs to be string replaced through
    * currently processed pid. 
    *
    */
   public static String TEI_P_IDNO          = "<p xmlns=\"http://www.tei-c.org/ns/1.0\"><idno type=\"PID\">$replacement</idno></p>";
   
   /**
    * XML string template.
    * GAMS TEI-IDNO pid convention.
    * TEI <idno> with $replacement meant to be string replaced with 
    * currently processed pid.
    */
   public static String TEI_IDNO            = "<idno xmlns=\"http://www.tei-c.org/ns/1.0\" type=\"PID\">$replacement</idno>";
   
   /** 
    * XML string template.
    * GAMS MEI-IDNO pid convention.
    * MEI <identifier> with $replacement meant to be string replaced
    * with currently processed pid.
    */
   public static String MEI_IDNO            = "<identifier xmlns=\"http://www.music-encoding.org/ns/mei\" type=\"PID\">$replacement</identifier>";
   
   /**
    * XML string template. 
    * GAMS LIDO RECID pid convention.
    * LIDO <lidoRecID> with $replacement, meant to be string replaced
    * with currently processed pid.
    */
   public static String RECID               = "<lido:lidoRecID lido:type=\"PID\">$replacement</lido:lidoRecID>";

   /**
    * Available mimetypes at GAMS mapped to file endings / extensions.
    */
   public final static HashMap<String, String> extensions = new HashMap<String, String>();
   static {
       extensions.put("application/gzip", ".gz");
      extensions.put("application/json", ".json");
      extensions.put("application/pdf", ".pdf");
     extensions.put("application/xml", ".xml");
       extensions.put("application/zip", ".zip");
       extensions.put("application/x-ipynb+json", ".ipynb");    	
       extensions.put("application/tei+xml", ".xml");    	
       extensions.put("application/rdf+xml", ".rdf");
       extensions.put("application/x-turtle", ".ttl");
       extensions.put("application/javascript", ".js");
       extensions.put("text/xml", ".xml");
       extensions.put("text/html", ".html");
        extensions.put("text/css", ".css");
        extensions.put("text/markdown", ".md");
     extensions.put("text/plain", ".txt");
     extensions.put("image/jpeg", ".jpg");
     extensions.put("image/tiff", ".tif");
     extensions.put("image/png", ".png");
     extensions.put("image/svg+xml", ".svg");
    }
 
   /**
    * SPARQL query. 
    * Retrieves all cm4f:owner ordered alphabetically. 
    */
   public static final String OWNERS       =    "select distinct ?owner where {?pid <http://cm4f.org/owner> ?owner} order by ?owner";
  
   public static enum Activities { CREATE, UPDATE, DELETE, INGEST, SYSOP, PID }; 

   public static enum Modes {ADD, UPDATE, DELETE}

   public static enum RequestMethods {Get, Post, Patch, Put, PutLenient, Delete}
 
   /**
    * Ongoing transaction prefix for fedora operations.
    * Might be contained by pid.
    */
   public static String TX_PREFIX = "fcr:tx/";

   /**
    * Returns System.getenv("PUBLIC_HOST") or localhost if null or 
    * first entry in string list separated via ",".
    * @param host {String=} string where host should be extract-splitted.
    * @return {String} system's hostname 
    */
   public static String getHost (String host) {
    
    if (host == null) {
      host = System.getenv("PUBLIC_HOST");
    }
    if (host == null) {
      host = "localhost";
    }
    if (host.contains(",")) {
      host = host.split("[,]")[0].trim();
    }
    return host;
  }

   /**
    * Normalizes given pid (like o:derla1 OR info:fedora/o:derla1 --> to info:fedora/o:derla1)
    * Builds info:fedora/{pid} no matter if given with or without info:fedora statement.
    * @param pid {String} pid to be normalized
    * @return {String} normalized pid
    */
 public static String getNormalizedPid(String pid) {
   
   pid  = Common.INFO_FEDORA + (pid.contains("/") ? StringUtils.substringAfterLast(pid, "/") : pid);
   return  pid;
 }
 
 /**
  * Sets given response to empty html page and it's status code to
  * service unavailable.
  * @param response {HttpServletResponse}
  */
 public static void setServiceUnavailable (HttpServletResponse response) {
   try {
     response.addHeader("Content-Disposition", "inline;"); 
     response.setContentType("text/html; charset=UTF-8");
     response.addHeader("Content-Length","7");   
     IOUtils.write("<html/>", response.getOutputStream(), "UTF-8");
     response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
     response.flushBuffer();
   } catch (Exception e) {
   }
 }

}
