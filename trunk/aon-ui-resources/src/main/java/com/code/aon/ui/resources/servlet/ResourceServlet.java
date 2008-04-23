package com.code.aon.ui.resources.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.mime.Magic;
import com.code.aon.common.mime.MagicMatch;

/**
 * Servlet class invoked whenever a field form needs a Resource.
 * 
 * @author Consulting & Development. Aimar Tellitu - 22-abril-2007
 * @since 1.0
 */

public class ResourceServlet extends HttpServlet {

	/** The Constant PATTERN_INIT_PARAMETER. */
	private static final String PATTERN_INIT_PARAMETER = "pattern";

	private static final String DEFAULT_PATTERN = "/aonResource";
	
	/** The Constant BASE_PATH_INIT_PARAMETER. */
	private static final String BASE_PATH_INIT_PARAMETER = "basePath";

	private static final String DEFAULT_BASE_PATH = "/richCss";
	
	private String pattern;
	
	private String basePath;
	
    /**
     * Initializes the servlet
     * 
     * @param config The <code>ServletConfig</code> object that contains the information
     * to configure this servlet.
     * 
     * @throws ServletException If occurs any exception which interrupts the execution of
     * the servlet.
     * 
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
       	this.pattern = StringUtils.trimToNull(config.getInitParameter(PATTERN_INIT_PARAMETER));
       	if ( this.pattern == null ) {
       		this.pattern = DEFAULT_PATTERN;
       	}
       	this.basePath = StringUtils.trimToNull(config.getInitParameter(BASE_PATH_INIT_PARAMETER));
       	if ( this.basePath == null ) {
       		this.basePath = DEFAULT_BASE_PATH;
       	}
    }
    
    private String getResource( HttpServletRequest req ) {
    	String uri = req.getRequestURI();
    	String context = req.getContextPath();
    	if ( uri.startsWith(context) ) {
    		uri = uri.substring( context.length() );
    	}
    	if ( uri.startsWith(pattern) ) {
    		uri = uri.substring(pattern.length());
    	}
    	if ( basePath != null ) {
    		uri = basePath + uri;
    	}
    	return uri;
    }
    
    private String getMimeType( String resource, byte[] data ) throws ServletException {
    	String result = null;
    	int pos = resource.lastIndexOf('.');
    	if ( pos != -1 ) {
    		String extension = resource.substring(pos+1);
    		result = MimeType.getByExtension(extension).getName();
    	}
    	if ( result == null ) {
    		try {
				MagicMatch match = Magic.getMagicMatch(data, true);
				if ( match != null ) {
					result = match.getMimeType();
				}
			} catch (Exception e) {
				throw new ServletException(e.getMessage(), e);				
			}
    	}
    	return result;
    }
	
    /**
     * Retrieves the resource.
     * 
     * @param req the req
     * @param res the res
     * 
     * @throws IOException the IO exception
     * @throws ServletException the servlet exception
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String resource = getResource( req );
			InputStream in = getClass().getResourceAsStream(resource);
			if ( in == null ) {
				throw new ServletException( resource + " resource not found.");
			}
			byte[] data = IOUtils.toByteArray( in );
			res.setContentType( getMimeType(resource, data) );
			res.getOutputStream().write( data );
			res.flushBuffer();
        } catch (Throwable th) {
            throw new ServletException(th.getMessage(), th);
        }
    }

}
