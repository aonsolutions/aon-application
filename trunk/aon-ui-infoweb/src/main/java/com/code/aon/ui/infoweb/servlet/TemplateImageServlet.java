package com.code.aon.ui.infoweb.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.ui.infoweb.util.PathUtil;

public class TemplateImageServlet extends HttpServlet {

	private static final long serialVersionUID = 481356189045635775L;
	
	private static final Logger LOGGER = Logger.getLogger(TemplateImageServlet.class.getName());
	
	/**
	 * Retrieves the required RegistryAttachment from the database
	 * 
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		String template = req.getParameter("tpl");
		boolean big = req.getParameter("big") != null;
		String subfix = "";
		if (big) subfix = "_grande";
		if (template != null) {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				File f = new File( PathUtil.getTemplatePath(template), "preview"+subfix+".jpg" );
	        	res.setContentType( "image/jpeg" );
	        	res.setHeader("Expires", "0");
	        	res.setHeader("Pragma", "no-cache");
	        	res.setHeader("Cache-Control", "no-store");
	            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
				if(!f.exists()) {
					f = new File( PathUtil.getTemplatesPath(), "preview"+subfix+".jpg" );
				}
				bis = new BufferedInputStream(new FileInputStream(f));
        		bos = new BufferedOutputStream(res.getOutputStream());
        		IOUtils.copyLarge(bis, bos);
        		bos.flush();
	            res.flushBuffer();
	        } catch (Throwable th) {
	        	LOGGER.log(Level.SEVERE, th.getMessage(), th);
	            throw new ServletException(th.getMessage(), th);
	        } finally {
	        	IOUtils.closeQuietly(bis);
	        	IOUtils.closeQuietly(bos);
	        }
		}
	}
	
}
