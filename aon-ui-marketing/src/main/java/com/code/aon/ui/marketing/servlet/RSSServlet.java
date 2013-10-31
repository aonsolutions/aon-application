package com.code.aon.ui.marketing.servlet;

import static com.code.aon.ui.marketing.controller.RSSController.RSS_PREFFIX;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.marketing.News;
import com.code.aon.ui.marketing.controller.NewsController;
import com.code.aon.ui.marketing.controller.RSSController;
import com.code.aon.ui.util.DownloadUtil;

public class RSSServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RSSServlet.class.getName());
	
	public static final String SERVLET_PATH = "/aonFeed/";

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
	
	private Integer getChannelId( String value ) {
		if (! StringUtils.isEmpty(value) ) {
			Pattern pattern = Pattern.compile(RSSController.RSS_REGEX);
			Matcher matcher = pattern.matcher(value);
			if ( matcher.find() ) {
				String idValue = matcher.group(1);
				if ( NumberUtils.isNumber(idValue) ) {
					return NumberUtils.toInt(idValue);
				}
			}			
		}
		return null;
	}
	
	private byte[] getRSSData( HttpServletRequest req, String value ) {
		byte[] data = null;
		String sessionName = HibernateUtil.getSessionFactoryName(News.class.getName());
		Connection c = null;
		try {
			Session session = HibernateUtil.getSession(sessionName);
			String domainName = req.getServerName(); 
			c =  DatabaseUtil.getConnection(domainName);
			Integer domainId = DatabaseUtil.getDomain(c,domainName);
			Integer channelId = getChannelId(value);
			data = RSSController.getRSS(session, domainId, channelId, getURLPreffix(req));
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting rss", th );
		} finally {
			DatabaseUtil.closeQuietly(c);
			HibernateUtil.closeSession(sessionName);
		}
		return data;
	}

	private News getNews( Session session, Integer domainId, Integer newsId ) {
		Criteria criteria = session.createCriteria(News.class);
		criteria.add(Restrictions.eq("domain", domainId));
		criteria.add(Restrictions.eq("active", Boolean.TRUE));
		criteria.add(Restrictions.eq("id", newsId));
		return (News) criteria.uniqueResult();		
	}
	
	private void writeNewsHtml( HttpServletRequest req, HttpServletResponse res, Integer newsId ) {
		String sessionName = HibernateUtil.getSessionFactoryName(News.class.getName());
		Connection c = null;
		try {
			Session session = HibernateUtil.getSession(sessionName);
			String domainName = req.getServerName(); 
			c =  DatabaseUtil.getConnection(domainName);
			Integer domainId = DatabaseUtil.getDomain(c,domainName);
			News news = getNews(session, domainId, newsId);
			if ( news != null ) {
				res.setContentType( MimeType.MIME_HTML.getName() );
				PrintWriter writer = res.getWriter();
				NewsController.writeHtml(news, getURLPreffix(req), writer);
				writer.flush();
				writer.close();					
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting rss", th );
		} finally {
			DatabaseUtil.closeQuietly(c);
			HibernateUtil.closeSession(sessionName);
		}
	}
	
	private Integer getNewsId( String value ) {
		if (! StringUtils.isEmpty(value) ) {
			Pattern pattern = Pattern.compile(NewsController.NEWS_REGEX);
			Matcher matcher = pattern.matcher(value);
			if ( matcher.find() ) {
				String idValue = matcher.group(1);
				if ( NumberUtils.isNumber(idValue) ) {
					return NumberUtils.toInt(idValue);
				}
			}
		}
		return null;
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
			String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
			String value = StringUtils.substringAfterLast(uri, "/");
			if ( StringUtils.startsWith(value, RSS_PREFFIX) ) {
				byte[] data = getRSSData(req, value);
				if (! ArrayUtils.isEmpty(data) ) {
					out = DownloadUtil.initDownload(res, value, MimeType.MIME_RSS, data.length);
					InputStream in = new ByteArrayInputStream(data);
					IOUtils.copyLarge(in, out);
				}
			} else {
				Integer newsId = getNewsId(value);
				if ( newsId != null ) {
					writeNewsHtml( req, res, newsId );
				}
			}
		} catch (Throwable th) {
			LOGGER.error( th.getMessage(), th );
			throw new ServletException(th.getMessage(), th);
		} finally {
			finishDownload(res, out);
		}
	}

}