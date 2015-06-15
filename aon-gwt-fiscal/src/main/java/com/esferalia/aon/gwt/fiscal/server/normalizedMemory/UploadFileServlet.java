package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;


public class UploadFileServlet extends UploadAction{


	  private static final long serialVersionUID = 1L;

	@Override
	public void checkRequest(HttpServletRequest request) {
		maxSize=10485760;
		super.checkRequest(request);
	}
	
	
	  @Override
	  public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
	    System.out.println("aqaa");
		  String response = "";
	    for (FileItem item : sessionFiles) {
	    	System.out.println(item.isFormField());
	      if (false == item.isFormField()) {
	        
	          
	          String mimetype = item.getContentType();
	          Long size = item.getSize();
	        
	        
	          byte[] b = item.get();
	          request.getSession().putValue("importedData", b);
	        
	      }
	    }
	    
	    /// Remove files from session because we have a copy of them
	    super.removeSessionFileItems(request);
	    
	    /// Send your customized message to the client.
	    return response;
	  }
	  
	 
	
	
}
