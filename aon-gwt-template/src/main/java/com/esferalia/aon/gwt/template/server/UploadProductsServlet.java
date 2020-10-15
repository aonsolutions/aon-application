package com.esferalia.aon.gwt.template.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.gwt.common.shared.Base64;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;


public class UploadProductsServlet extends UploadAction{


	  private static final long serialVersionUID = 1L;

	@Override
	public void checkRequest(HttpServletRequest request) {
		maxSize=10485760;
		super.checkRequest(request);
	}
	
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		String domainName = request.getParameter("domain_name");
		String login = request.getParameter("login");
		String hashId = Base64.encode(domainName + login);

	    for (FileItem item : sessionFiles) {
	    	System.out.println(item.isFormField());
	      if (false == item.isFormField()) {
	          String mimetype = item.getContentType();
	          Long size = item.getSize();
	          TemplatesServlet.setSize(size.intValue());
	          TemplatesServlet.addOut(hashId, item.get());
	          TemplatesServlet.setMimetype(mimetype); 
	      }
	    }
	    
	    /// Remove files from session because we have a copy of them
	    super.removeSessionFileItems(request);
	    
	    /// Send your customized message to the client.
	    return "";
	  }
	  
	 
	
	
}
