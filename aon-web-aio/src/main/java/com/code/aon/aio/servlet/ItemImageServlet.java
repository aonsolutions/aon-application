package com.code.aon.aio.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;

@WebServlet(name = "ItemImage", urlPatterns = { "/aonItemImage/*" })
public class ItemImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Attach getAttachment(HttpServletRequest req){
		String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
		String value = StringUtils.substringAfterLast(uri, "/");
		Integer pos = value.indexOf(".");
		Integer id = Integer.valueOf(value.substring(0,pos));
		Connection connection = null;
		try {
			String domainName = AonUtil.getServerName();
		 	connection = DatabaseUtil.getConnection(domainName);
		 	if ( connection != null ) {
		 		Integer domainId = DatabaseUtil.getDomain(connection, domainName);
				return AON.getAttach(domainName, domainId, "", f -> f.getIdProperty().eq(id), AttachType.ITEM);
		 	}
		} catch ( Throwable th ) {
			System.out.println("Error getting Domain"+ th );
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	 	return null;
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		Attach attach = getAttachment(req);
		if ( attach != null ) {			
			Integer length = attach.getData().length;
			ByteArrayInputStream bais = new ByteArrayInputStream(attach.getData());
		       
		    res.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() +"\"");
		    //p_response.setContentType("application/octet-stream");
		    res.setContentType(attach.getMimeType().getName());

		    if (length > 0 && length <= Integer.MAX_VALUE)	
		    	res.setContentLength((int)length);
		    
	        ServletOutputStream out = res.getOutputStream();
	        res.setBufferSize(32768);
	        int bufSize = res.getBufferSize();
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
}
