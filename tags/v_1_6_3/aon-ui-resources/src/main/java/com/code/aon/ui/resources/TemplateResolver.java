package com.code.aon.ui.resources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import com.code.aon.common.net.DummyHandler;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;

/**
 * This facelets resource resolver allows us to put facelet files in jars 
 * on the classpath, as well as the context root of the webapp.
 *
 * @author roger
 */
public class TemplateResolver extends DefaultResourceResolver 
        implements ResourceResolver {
	
	private static final Logger LOGGER = Logger.getLogger(TemplateResolver.class
			.getName());
    
    /** first check the context root, then the classpath */
    public URL resolveUrl(String path) {
        LOGGER.fine("Resolving URL " + path);
        URL url = super.resolveUrl(path);
        if (url == null) {
            
            /* classpath resources don't start with / */
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            url = Thread.currentThread().getContextClassLoader().
                    getResource(path);
    		try {
    			url = new URL(null, url.toExternalForm(), new DummyHandler() );
    		} catch (MalformedURLException e) {
    			LOGGER.severe( e.getMessage() );
    		}           
        }
        return url;
    }
}