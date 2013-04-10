package com.code.aon.ui.marketing.servlet;

import static com.code.aon.ui.common.ICommonConstants.SKIP_LDAP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.marketing.controller.RSSController;
import com.code.aon.ui.util.DataSourceUtil;
import com.code.aon.ui.util.DownloadUtil;

public class RSSServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RSSServlet.class.getName());
	
	private static final String HIBERNATE_CONFIGURATION_FILE = "/hibernate.rss.cfg.xml";

	private boolean skipLdap;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
		this.skipLdap = BooleanUtils.toBoolean(getServletContext().getInitParameter(SKIP_LDAP));        
    }
	
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
	
	private Properties getConnectionProperties( HttpServletRequest req ) {
		String server = req.getServerName();
		String context = req.getContextPath(); 
		return DataSourceUtil.getDBProperties(server, context, skipLdap);
	}
	
	private Configuration getConfiguration( HttpServletRequest req ) {
		AnnotationConfiguration configuration = null;
		Properties properties = getConnectionProperties(req);
		if ( (properties != null) && (!properties.isEmpty()) ) {
			configuration = new AnnotationConfiguration();
			configuration.addProperties(properties);
			configuration.configure(HIBERNATE_CONFIGURATION_FILE);			
		}
		return configuration;
	}	
	
	private byte[] getRSSData( HttpServletRequest req ) {
		byte[] data = null;
		SessionFactory factory = null;
		try {
			Configuration configuration = getConfiguration(req);
			if ( configuration != null ) {
				factory = configuration.buildSessionFactory();
				StatelessSession session = factory.openStatelessSession();
				data = RSSController.getRSS(session, getURLPreffix(req));
				session.close();				
			}				
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting rss", th );
		} finally {
			if ( factory != null ) {
				factory.close();	
			}
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