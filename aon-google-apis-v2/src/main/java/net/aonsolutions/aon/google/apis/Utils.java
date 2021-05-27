package net.aonsolutions.aon.google.apis;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Utils {

	/******************************* CHECK GMAIL *******************************/
	
	public static Boolean isGmail(String email) {
		try {
			Integer pos= email.indexOf("@");
			String hostname = email.substring(pos+1);
			Attribute attr = doLookup(hostname);
			int i=0;
			if (attr!=null){
				while(i<attr.size()){
					String a = (String) attr.get(i);
					if(AonStringUtils.containsIgnoreCase(a, "google.com") || AonStringUtils.containsIgnoreCase(a, "googlemail.com")){
						return true;
					}
					i++;
				}
			} 	
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return false;	  
	}
	  
	private static Attribute doLookup( String hostName ) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext( env );
		Attributes attrs = ictx.getAttributes( hostName, new String[] { "MX" });
		Attribute attr = attrs.get( "MX" );
		return attr;
	}
	

}
