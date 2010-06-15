package com.code.aon.ui.resources.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;

/**
 * Servlet class invoked whenever a field form needs a Resource.
 * 
 * @author Consulting & Development. Aimar Tellitu - 22-abril-2007
 * @since 1.0
 */

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -2158184452139430686L;
	
	/**
	 * Logger initialization
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceServlet.class);

	/**
	 * One week in milliseconds.
	 */
	public static final long ONE_HUNDRED_DAYS_MILLIS = 8640000000L;

	private static final int LAST_MODIFIED_YEAR = 2008;
	
	private static final int LAST_MODIFIED_MOTH = 7;
	
	private static final int LAST_MODIFIED_DAY = 14;
	
	private static final String MODIFY = calcModify();

	/** The Constant PATTERN_INIT_PARAMETER. */
	private static final String PATTERN_INIT_PARAMETER = "pattern";

	/** The Constant DEFAULT_PATTERN. */
	public static final String DEFAULT_PATTERN = "/aonResource";

	/** The Constant BASE_PATH_INIT_PARAMETER. */
	private static final String BASE_PATH_INIT_PARAMETER = "basePath";

	private static final String DEFAULT_BASE_PATH = "/richCss";

	/**
	 * Context parameter for activating debug mode, which will disable caching.
	 */
	public static final String DEBUG_INIT_PARAM = "debug";

	private String pattern;

	private String basePath;

	private boolean debug;

	/**
	 * Initializes the servlet
	 * 
	 * @param config
	 *            The <code>ServletConfig</code> object that contains the
	 *            information to configure this servlet.
	 * 
	 * @throws ServletException
	 *             If occurs any exception which interrupts the execution of the
	 *             servlet.
	 * 
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.pattern = StringUtils.trimToNull(config
				.getInitParameter(PATTERN_INIT_PARAMETER));
		if (this.pattern == null) {
			this.pattern = DEFAULT_PATTERN;
		}
		this.basePath = StringUtils.trimToNull(config
				.getInitParameter(BASE_PATH_INIT_PARAMETER));
		if (this.basePath == null) {
			this.basePath = DEFAULT_BASE_PATH;
		}
	    String debugValue = StringUtils.trimToNull(config.getInitParameter(DEBUG_INIT_PARAM));
	    if (debugValue != null) {
	    	debug = Boolean.valueOf(debugValue);
	    }
	}
	
	private static final String calcModify() {
		Date date = new GregorianCalendar( LAST_MODIFIED_YEAR, LAST_MODIFIED_MOTH, LAST_MODIFIED_DAY ).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}	

	private String getMimeType(String resource, byte[] data)
			throws ServletException {
		String result = null;
		String extension = FilenameUtils.getExtension(resource);
		if (! StringUtils.isEmpty(extension)) {
			MimeType mimeType = MimeType.getByExtension(extension);
			if ( mimeType != null ) {
				result = mimeType.getName();	
			}
		}
		if (result == null) {
			try {
				MagicMatch match = Magic.getMagicMatch(data, true);
				if (match != null) {
					result = match.getMimeType();
				}
			} catch (Exception e) {
				throw new ServletException(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * Sets HTTP headers on the response which tell the browser to cache the
	 * resource indefinitely.
	 * @throws ServletException 
	 */
	private void setHeaders(HttpServletResponse response, ResourceURI resource,
			byte[] data) throws ServletException {
		response.setContentType(getMimeType(resource.getPath(), data));
		response.setContentLength(data.length);

		// If we're not in debug mode, set cache headers
		if ( (!debug) && resource.isCacheable() ) {
			// We set two headers: Cache-Control and Expires.
			// This combination lets browsers know that it is
			// okay to cache the resource indefinitely.

			// Set Cache-Control to "Public".
			response.setHeader("Cache-Control", "Public");

			response.setHeader("Last-Modified", MODIFY);

			// Set Expires to current time + one year.
			long currentTime = System.currentTimeMillis();

			response.setDateHeader("Expires", currentTime + ONE_HUNDRED_DAYS_MILLIS);
		}
	}

	/**
	 * Retrieves the resource.
	 * 
	 * @param req
	 *            the req
	 * @param res
	 *            the res
	 * 
	 * @throws IOException
	 *             the IO exception
	 * @throws ServletException
	 *             the servlet exception
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			ResourceURI resource = new ResourceURI(req.getRequestURI(), req.getContextPath(), DEFAULT_PATTERN);
			InputStream in = resource.getInputStream(basePath);
			if (in == null) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			byte[] data = IOUtils.toByteArray(in);
			setHeaders(res, resource, data);
			res.getOutputStream().write(data);
			res.flushBuffer();
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			throw new ServletException(th.getMessage(), th);
		}
	}

}
