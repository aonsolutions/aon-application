package com.code.aon.webservice.documental;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;

@WebServlet(name = "ImageServletXXX", urlPatterns = { "/aon_gwt_aio/image_servlet/*" })
public class ImageServlet  extends HttpServlet {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String attach_type = request.getParameter("type");
		String attach_id = request.getParameter("id");
		String domainName = request.getParameter("domain");
		String login = request.getParameter("login");
		Integer domainId = 1;
		Domain domain = AON.getDomain(domainName, domainId, login, f -> f
				.getNameProperty().eq(domainName));
		
		Integer id = Integer.parseInt(attach_id);
		AttachType attachType = AttachType.getAttachType(attach_type);
		
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id), attachType);
		if ( attach != null && attach.getData() != null) {			
			Integer length = attach.getData().length;
			ByteArrayInputStream bais = new ByteArrayInputStream(attach.getData());
		       
			response.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() +"\"");
		    //p_response.setContentType("application/octet-stream");
			response.setContentType(attach.getMimeType().getName());

		    if (length > 0 && length <= Integer.MAX_VALUE)	
		    	response.setContentLength((int)length);
		    
	        ServletOutputStream out = response.getOutputStream();
	        response.setBufferSize(32768);
	        int bufSize = response.getBufferSize();
	        byte[] buffer = new byte[bufSize];
	        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
	        int bytes;
	        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
	        	out.write(buffer, 0, bytes);
		        	
		        
	        bis.close();
	        bais.close();
	        out.flush();
	        out.close();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
	
	}

}