package org.sebi.springfedora.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Rename {
  /**
     * Needed for creation of fedora6 paths from pids etc. Like context:cantus -> needs to be under aggregations/context/cantus!
     * or o:cantus.regensburg -> /objects/cantus.regensburg.
     * 
     * Translates various internally used notation patterns e.g. dependent on pid notation / 'cirilo:' etc.
     * Like mapping from backbone object transformation from GAMS3 to prototype based transformation on GAMS4.
     * @param s {String=} to be refactored.
     * @return {String} translated string. (if conditions were met like starstwith('http'))
     * TODO translateLegacyDefaults? translateLegacy?
     */
	public static String rename (String s) {

		//TODO logging conditionals
		String txid = null;
				
		if (s == null) return new String();
		
		if (s.startsWith("http")) return s;
		
		//if (s.contains(Common.INFO_FEDORA)) s = StringUtils.remove(s, Common.INFO_FEDORA);
		
		// if (s.startsWith(Common.TX_PREFIX)) {
		// 	txid = StringUtils.substringBefore(s, "/");
		// 	s = StringUtils.substringAfter(s, "/");
		// }
		
		// pattern how to split according to content model definitions in pid
		// also for backbone / prototypes  
		if (s.contains(":")) {
			s = s.toLowerCase().replaceAll("_", "@");
			if (s.matches("^(query:|corpus:|context:).*") ) { 
				Matcher m = Pattern.compile("(query|corpus|context)[:](.*)").matcher(s);
				if (m.find()) s = "aggregations/" + m.group(1) + "/" +  m.group(2);
			} else if (s.matches("(^o:prototype).*") ) { 	
				s = "cm4f/" + StringUtils.substringAfter(s, ":");
			} else if (s.matches("^cirilo:(.*)") ) {
				s = "cm4f/defaults/" + StringUtils.substringAfter(s.replaceAll("backbone","settings"), ":");
			} else if (s.matches("^o:.*") ) {
				s = "objects/" + StringUtils.substringAfter(s, ":");
			}  		
		}
					
		s = (txid != null ? txid + "/" : "") + s;

		return s;	
	 }
}
