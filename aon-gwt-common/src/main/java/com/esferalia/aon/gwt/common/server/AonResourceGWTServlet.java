package com.esferalia.aon.gwt.common.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
		name = "AonResourceGWTServlet", 
		urlPatterns = { 
				"/aon_gwt_aio/*", 
				"/aon_gwt_payroll/*", 
				"/aon_gwt_fiscal/*",
				"/aon_gwt_mod200/*",
				"/aon_gwt_stat/*",
				"/aon_gwt_dump/*",
				"/aon_gwt_connect/*",
				"/aon_gwt_document/*",
				"/aon_gwt_template/*",
				"/aon_gwt_issues/*",
				"/aon_gwt_commercial/*"
		})
public class AonResourceGWTServlet extends HttpServlet {

	private static final long serialVersionUID = 8491390024984300156L;

	// One year in milliseconds. (Actually, just short of on year, since
	// RFC 2616 says Expires should not be more than one year out, so
	// cutting back just to be safe.)
	public static final long ONE_YEAR_MILLIS = 31363200000L;

	/**
	 * ContextDescriptor parameter for activating debug mode, which will disable caching.
	 */
	public static final String DEBUG_INIT_PARAM = "debug";
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		_initDebug(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ClassLoader loader = _getResourceLoader(req);
		String resourcePath = getResourcePath(req);
		URL url = loader.getResource(resourcePath);
		
		// Make sure the resource is available
		if (url == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Stream the resource contents to the servlet response
		URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(false);

		_setHeaders(connection, resp);

		InputStream in = connection.getInputStream();
		OutputStream out = resp.getOutputStream();
		byte[] buffer = new byte[_BUFFER_SIZE];

		try {
			_pipeBytes(in, out, buffer);
		} finally {
			try {
				in.close();
			} finally {
				out.close();
			}
		}

	}
	

	protected String getResourcePath(HttpServletRequest req) {
		String path = req.getServletPath();
		String info = req.getPathInfo();
		if ( path.startsWith("/")) {
			path = path.substring(1);
		}
		return path + info;
	}

	private ClassLoader _getResourceLoader(HttpServletRequest req) {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Initialize whether resource debug mode is enabled.
	 */
	private void _initDebug(ServletConfig config) {
		String debug = config.getInitParameter(DEBUG_INIT_PARAM);
		// --------------------------------
		// Este parametro estaba definido en el web.xml con valor a true.
		// Se define aquí despuñes de haber eliminado la definición del WEB.xml
		debug = "true"; 
		// --------------------------------
		if (debug == null) {
			// Check for a context init parameter if servlet init
			// parameter isn't set
			debug = config.getServletContext().getInitParameter(
					DEBUG_INIT_PARAM);
		}

		_debug = "true".equalsIgnoreCase(debug);
		if (_debug) {
			_LOGGER.info("RESOURCESERVLET_IN_DEBUG_MODE", DEBUG_INIT_PARAM);
		}
	}

	/**
	 * Reads the specified input stream into the provided byte array storage and
	 * writes it to the output stream.
	 */
	private static void _pipeBytes(InputStream in, OutputStream out,
			byte[] buffer) throws IOException {
		int length;

		while ((length = (in.read(buffer))) >= 0) {
			out.write(buffer, 0, length);
		}
	}

	/**
	 * Sets HTTP headers on the response which tell the browser to cache the
	 * resource indefinitely.
	 */
	private void _setHeaders(URLConnection connection, HttpServletResponse resp) {
		// -------------------------------
		URL url = connection.getURL();
		String resourcePath = url.getPath();
		String contentType = URLConnection.guessContentTypeFromName(resourcePath);
		if (contentType == null || "content/unknown".equals(contentType)) {
			if (resourcePath.endsWith(".css"))
				contentType = "text/css";
			else
				contentType = getServletContext().getMimeType(resourcePath);
		}
		resp.setContentType(contentType);

		int contentLength = connection.getContentLength();
		if (contentLength >= 0)
			resp.setContentLength(contentLength);

		long lastModified = connection.getLastModified();
		if (lastModified > 0)
			resp.setDateHeader("Last-Modified", lastModified);

		// If we're not in debug mode, set cache headers
		if (!_debug) {
			// We set two headers: Cache-Control and Expires.
			// This combination lets browsers know that it is
			// okay to cache the resource indefinitely.

			// Set Cache-Control to "Public".
			resp.setHeader("Cache-Control", "Public");

			// Set Expires to current time + one year.
			long currentTime = System.currentTimeMillis();

			resp.setDateHeader("Expires", currentTime + ONE_YEAR_MILLIS);
		} else {
			// Set Cache-Control to "Public".
			resp.setHeader("Cache-Control", "no-cache");
		}
	}

	private boolean _debug = true;


	// Size of buffer used to read in resource contents
	private static final int _BUFFER_SIZE = 2048;


    /**
     * Logger initialization
     */
	private final static Logger _LOGGER = LoggerFactory.getLogger(AonResourceGWTServlet.class);

}
