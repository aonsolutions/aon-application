package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.commons.fileupload.FileItem;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;


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
	    String domain = AonUtil.getDomainName();
	    String domain_id = request.getParameter("domain_id");
	    String cif = request.getParameter("cif");
		Integer domainId = Integer.parseInt(domain_id);
		
	    String response = "";
	    for (FileItem item : sessionFiles) {
	    	System.out.println(item.isFormField());
	      if (false == item.isFormField()) {
	        
	          
	          String mimetype = item.getContentType();
	          Long size = item.getSize();
	        
	        
	          byte[] b = item.get();
	          
	          try {
				Esquema schema = Utils.readXml(b);
				if(schema.getCabecera().getCIF().equals(cif)){
					//DBConsults.insertDeposit(domain, b, domainId);
					request.getSession().removeAttribute("ModifyD2DepositSchema"+cif);
					request.getSession().putValue("d2DepositSchema"+cif, schema);
				}
	          } catch (JAXBException e) {
				e.printStackTrace();
	          }
	      }
	    }
	    
	    /// Remove files from session because we have a copy of them
	    super.removeSessionFileItems(request);
	    
	    /// Send your customized message to the client.
	    return response;
	  }
	  
	 
	
	
}
