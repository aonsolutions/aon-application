package com.esferalia.aon.gwt.document.server;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;

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
	    	  Attach attach = new Attach();
	    	  attach.setDescription(name);
	    	  Long size = item.getSize();
	    	  attach.setDparentId(Integer.toString(size.intValue()));
	    	  attach.setData(item.get());
	    	  attach.setMimeType(MimeType.get(item.getContentType()));
	    	  attach.setDomain(new Domain().setName(AonUtil.getServerName()).setId(0));
	    	  attach.setAttachType(AttachType.REGISTRY);
	    	  attach.setDate(new Date());
	    	  attach.setConfidential(false);
	    	  attach.setType((byte) 3);
	    	  Company c = AON.getCompany(AonUtil.getDomainName(), 0, "", f -> f.getDomainProperty().eq(0));
	    	  attach.setAttachModule(c.getId());
	    	  DocumentsServlet.addOuts(dialogCode, attach, request);
	      }
	    }
	    
	    /// Remove files from session because we have a copy of them
	    super.removeSessionFileItems(request);

	    /// Send your customized message to the client.
	    return response;
	  }
}
