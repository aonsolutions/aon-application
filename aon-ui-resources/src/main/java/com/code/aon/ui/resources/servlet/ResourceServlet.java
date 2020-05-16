package com.code.aon.ui.resources.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.faces.component.util.DownloadUtil;

/**
 * Servlet class invoked whenever a field form needs a Resource.
 * 
 * @author Consulting & Development. Aimar Tellitu - 22-abril-2007
 * @since 1.0
 */
@WebServlet(name = "ResourceServlet", urlPatterns = { "/aonResource/*"})
public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/**
	 * Logger initialization
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceServlet.class);

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

	private String getMimeType(String resource, byte[] data)
			throws ServletException {
		MimeType mt = MimeResolver.getMimeTypeByExtension(resource);
		if ( mt == null ) {
			mt = MimeResolver.getMimeType(data);
		}
		return (mt != null) ? mt.getName() : null;
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
		if ( !debug && resource.isCacheable() ) {
			DownloadUtil.setCacheable(response);
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
		InputStream in = null;
		try {
			String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
			ResourceURI resource = new ResourceURI(uri, req.getContextPath(), DEFAULT_PATTERN);
			in = resource.getInputStream(getServletContext(), basePath);
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
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
