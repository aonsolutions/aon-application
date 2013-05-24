package com.code.aon.ui.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.marketing.News;
import com.code.aon.ui.marketing.controller.RSSController;
import com.code.aon.ui.util.DownloadUtil;

public class RSSServlet extends HttpServlet {

	private static final String CHANNEL_PARAMETER = "channel";

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RSSServlet.class.getName());

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

	private String getURLPreffix( HttpServletRequest req ) {
		String server = req.getServerName();
		String context = req.getContextPath(); 
		return "http://" + server + context;
	}
	
	private Integer getChannelId( HttpServletRequest req ) {
		String value = req.getParameter(CHANNEL_PARAMETER);
		if (! StringUtils.isEmpty(value) ) {
			if ( NumberUtils.isNumber(value) ) {
				return NumberUtils.toInt(value);
			}
		}
		return null;
	}
	
	private byte[] getRSSData( HttpServletRequest req ) {
		byte[] data = null;
		String sessionName = HibernateUtil.getSessionFactoryName(News.class.getName());
		Connection c = null;
		try {
			Session session = HibernateUtil.getSession(sessionName);
			String domainName = req.getServerName(); 
			c =  DatabaseUtil.getConnection(domainName);
			Integer domainId = DatabaseUtil.getDomain(c,domainName);
			Integer channelId = getChannelId(req);
			data = RSSController.getRSS(session, domainId, channelId, getURLPreffix(req));
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting rss", th );
		} finally {
			DatabaseUtil.closeQuietly(c);
			HibernateUtil.closeSession(sessionName);
		}
		return data;
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
			byte[] data = getRSSData(req);
			if (! ArrayUtils.isEmpty(data) ) {
				out = DownloadUtil.initDownload(res, RSSController.RSS_FILE, MimeType.MIME_RSS, data.length);
				InputStream in = new ByteArrayInputStream(data);
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