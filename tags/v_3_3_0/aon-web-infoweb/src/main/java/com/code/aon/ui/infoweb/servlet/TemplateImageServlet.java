package com.code.aon.ui.infoweb.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.infoweb.velocity.VelocityConstants;

public class TemplateImageServlet extends HttpServlet {

	/**
	 * Retrieves the required RegistryAttachment from the database
	 * 
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		String template = req.getParameter("tpl");
		if (template != null) {
			InputStream is = null;
			BufferedInputStream bis = null;
	        OutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				String path = VelocityConstants.TEMPLATE_PATH;
				String file = path + "/" + template + "/preview.jpg";
				File f = new File(file);
	        	res.setContentType( "image/jpeg" );
	        	res.setHeader("Expires", "0");
	        	res.setHeader("Pragma", "no-cache");
	        	res.setHeader("Cache-Control", "no-store");
	            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
				if(!f.exists()) f = new File(path + "/preview.jpg");
				is = new FileInputStream(f);
        		bis = new BufferedInputStream(is);
                os = res.getOutputStream();
        		bos = new BufferedOutputStream(os);
        		byte[] input = new byte[1024];
        		boolean eof = false;
        		while (!eof) {
        			int length = bis.read(input);
        			if (length == -1) {
        				eof = true;
        			}
        			else {
        				bos.write(input, 0, length);
        			}
        		}
        		bos.flush();
        		bis.close();
	            res.flushBuffer();
	        } 
			catch (Throwable th) {
	            th.printStackTrace();
	            throw new ServletException(th.getMessage(), th);
	        }finally{
	        	try {bis.close();} catch (Exception e) {}
	        	try {bos.close();} catch (Exception e) {}
	        	try {is.close();} catch (Exception e) {}
	        	try {os.close();} catch (Exception e) {}
	    		is = null;
	    		bis = null;
	            os = null;
	    		bos = null;
	        }
		}
	}
	
}
