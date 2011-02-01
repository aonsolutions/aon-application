package com.code.aon.ui.resources;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TemplateResolver.class);
    
    /** first check the context root, then the classpath */
    public URL resolveUrl(String path) {
        LOGGER.debug("Resolving URL {}", path);
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
    			LOGGER.error( "Error resolving url " + url, e );
    		}           
        }
        return url;
    }
}