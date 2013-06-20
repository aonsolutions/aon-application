package com.code.aon.ui.company.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicAttachment;
import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ui.company.controller.CompanyDisplay;
import com.code.aon.ui.util.DownloadUtil;

public class CompanyDocumentServlet extends HttpServlet {

	private static final long serialVersionUID = -7223720894588071587L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDocumentServlet.class.getName());
	
	private static final String COMPANY_LOGO = "company.logo";
	
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
	
	private String getName( IAttachment attachment, MimeType type ) {
		String name = attachment.getDescription();
		if ( StringUtils.isEmpty(name) ) {
			name = "image-" + attachment.getId();
		}
		return DownloadUtil.getFileName(name, type);
	}
	
	private Integer getCompanyId( Connection connection, Integer domainId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection, 
				    "SELECT registry FROM company WHERE domain=? LIMIT 1", h, domainId); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}
	
	public static BasicAttachment getAttachment( Connection connection, Integer id ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object[]> h = new ArrayHandler();
			Object[] values = run.query( connection, 
				    "SELECT id, description, mimeType, data FROM rattach WHERE id = ? and data is not null LIMIT 1",
				    h, id);
			return CompanyDisplay.convert(values);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}			
	
	private IAttachment getAttachment( HttpServletRequest req ) {
		BasicAttachment attachment = null;
		boolean companyLogo = false;
		Integer attachmentId = null;
		String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
		String value = StringUtils.substringAfterLast(uri, "/");
		if ( StringUtils.equals(COMPANY_LOGO, value) ) {
			companyLogo = true;
		} else {
			String idValue = StringUtils.substringBefore(value, "-");
			if ( NumberUtils.isNumber(idValue) ) {
				attachmentId = NumberUtils.toInt(idValue);
			}
		}
		if ( companyLogo || (attachmentId != null) ) {		
			Connection connection = null;
			try {
				connection = DatabaseUtil.getConnection(req.getServerName());
				if ( connection != null ) {
					if ( companyLogo ) {
						Integer domainId = DatabaseUtil.getDomain(connection, req.getServerName());
						Integer companyId = getCompanyId(connection, domainId);
						attachment = CompanyDisplay.getLogo(connection, domainId, companyId);
					} else {
						attachment = getAttachment(connection, attachmentId);
					}
					if ( (attachmentId != null) && (attachment != null) ) {
						String md5Value = StringUtils.substringAfter(value, "-");
						if (! StringUtils.equals(attachment.getMD5(), md5Value) ) {
							attachment = null;
						}
					}			
				}				
			} catch ( Throwable th ) {
				LOGGER.error( "Error getting company name and logo", th );
			} finally {
				DatabaseUtil.closeQuietly(connection);
			}
		}
		return attachment;
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
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		OutputStream out = null;
		try {
			IAttachment attachment = getAttachment(req);
			if ( attachment != null ) {
				MimeType type = DownloadUtil.resolveMimeType(attachment);
				String name = getName(attachment, type);
				out = DownloadUtil.initDownload(res, name, type, attachment.getSize());
				DownloadUtil.setCacheable(res);
				InputStream in = new ByteArrayInputStream(attachment.getData());
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