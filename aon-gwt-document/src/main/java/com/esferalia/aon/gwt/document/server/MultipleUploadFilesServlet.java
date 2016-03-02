package com.esferalia.aon.gwt.document.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.gwt.document.shared.FileInfo;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;


public class MultipleUploadFilesServlet extends  UploadAction{


	  private static final long serialVersionUID = 1L;

	@Override
	public void checkRequest(HttpServletRequest request) {
		maxSize=10485760;
		super.checkRequest(request);
	}
	
	
	  @Override
	  public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
	    for (FileItem item : sessionFiles) {
	    	
	    	System.out.println(item.isFormField());
	      if (false == item.isFormField()) {
	    	  String dialogCode =  item.getFieldName().substring(0, 10);
	    	  String name = item.getName();
	    	  if(name.contains(".")){
	    		  Integer pos = name.lastIndexOf(".");
	    		  name = name.substring(0,pos);
	    	  }
	    	  FileInfo fi = new FileInfo();
	    	  fi.setTitle(name);
	    	  Long size = item.getSize();
	          fi.setSize(size.intValue());
	    	  fi.setData(item.get());
	    	  fi.setMimeString(item.getContentType());
	    	  DocumentsServlet.addOuts(dialogCode, fi, request);
	      }
	    }
	    
	    /// Remove files from session because we have a copy of them
	    super.removeSessionFileItems(request);

	    /// Send your customized message to the client.
	    return response;
	  }
}
