package org.sebi.springfedora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({
  "objects/{pid}/datastreams/{dsid}/content",
  "get/{pid}/{dsid}",
  "{pid}/{dsid}",
  "objects/{dsid}/methods/sdef:{model}"
})
public class DatastreamController {
  
}
