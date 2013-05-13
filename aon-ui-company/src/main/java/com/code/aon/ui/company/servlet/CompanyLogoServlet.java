package com.code.aon.ui.company.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.ui.company.controller.CompanyDisplay;
import com.code.aon.ui.util.DownloadUtil;

/**
 * Servlet class invoked whenever a field form needs a Registry Attachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 01-feb-2011
 * @since 1.0
 */
public class CompanyLogoServlet extends HttpServlet {

	private static final long serialVersionUID = 5043406396881442075L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyLogoServlet.class.getName());
	
	/**
	 * Finish download.
	 *
	 * @param response the response
	 * @param out the out
	 */
	private void finishDownload( HttpServletResponse response, OutputStream out ) {
		IOUtils.closeQuietly(out);
		if ( response != null ) {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
			}	
		}
	}
	
	private MimeType getMimeType( IAttachment attachment ) {
		MimeType mt = MimeResolver.getMimeTypeByExtension(attachment.getDescription());
		if ( mt == null ) {
			mt =  MimeResolver.getMimeType(attachment.getData());
		}
		return mt;		
	}
	
	/**
	 * Retrieves the required RegistryAttachment from the database
	 * 
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		OutputStream out = null;
		try {
			String server = req.getServerName();
			String context = req.getContextPath();
			CompanyDisplay companyDisplay = new CompanyDisplay();
			companyDisplay.init(server, context);
			if ( companyDisplay.isLogoDefined() ) {
				IAttachment logo = companyDisplay.getLogo();
				MimeType type = getMimeType(logo);
				out = DownloadUtil.initDownload(res, null, type, logo.getSize());
				InputStream in = new ByteArrayInputStream(companyDisplay.getCompanyLogo());
				IOUtils.copyLarge(in, out);
			}				
		} catch (Throwable th) {
			LOGGER.error( th.getMessage(), th );
			throw new ServletException(th.getMessage(), th);
		} finally {
			finishDownload(res, out);
		}
	}


}
