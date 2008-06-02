package com.code.aon.faces.component.richfaces.inputRichText;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;


/**
 * @author srecinto
 *
 */
public class InputRichTextServlet extends HttpServlet {

	private static final long serialVersionUID = 7260045528613530636L;

	/**
	 * One week in milliseconds.
	 */
	private static final long ONE_WEEK_MILLIS = 604800000L;
	
	private static final String modify=calcModify();
	
	private volatile String customResourcePath;
	
	private static final String calcModify() {
		Date mod = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(mod);
	}
	
	public void init(ServletConfig config) throws ServletException { 
		super.init(config); 
		setCustomResourcePath(config.getInitParameter("customResourcePath"));
	} 

	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // search the resource in classloader
        ClassLoader cl = this.getClass().getClassLoader();
        String uri = request.getRequestURI();
        String path = uri.substring(uri.indexOf(InputRichTextUtil.FCK_FACES_RESOURCE_PREFIX)+InputRichTextUtil.FCK_FACES_RESOURCE_PREFIX.length()+1);
        
        InputStream is = cl.getResourceAsStream(path);
        // if no resource found in classloader return nothing
        if (is==null) return;
        // resource found, copying on output stream
		byte[] data = IOUtils.toByteArray(is);
		response.setContentLength(data.length);
        
        if(getCustomResourcePath() != null) { //Use custom path to FCKeditor
        	this.getServletContext().getRequestDispatcher(getCustomResourcePath() + path).forward(request,response);
        } else {  //Use default FCKeditor bundled up in the jar
        	if (uri.endsWith(".jsf") || uri.endsWith(".html")) {
	        	response.setContentType("text/html;charset=UTF-8");
	        } else {
	            response.setHeader("Cache-Control", "public");
	            response.setHeader("Last-Modified", modify);
				long currentTime = System.currentTimeMillis();
				response.setDateHeader("Expires", currentTime + ONE_WEEK_MILLIS);            
	        }
	        if (uri.endsWith(".css")) {
	        	response.setContentType("text/css;charset=UTF-8");
	        } else if (uri.endsWith(".js")) {
	        	response.setContentType("text/javascript;charset=UTF-8");
	        } else if (uri.endsWith(".gif")) {
	        	response.setContentType("image/gif;");
	        } else if (uri.endsWith(".xml")) {
	        	response.setContentType("text/xml;charset=UTF-8");
	        } 
	        
			response.getOutputStream().write(data);
			response.flushBuffer();
			response.getOutputStream().close();
        }
    }

	public String getCustomResourcePath() {
		return customResourcePath;
	}

	public void setCustomResourcePath(String customResourcePath) {
		synchronized (this) {
			this.customResourcePath = customResourcePath;
		}
	}

}
