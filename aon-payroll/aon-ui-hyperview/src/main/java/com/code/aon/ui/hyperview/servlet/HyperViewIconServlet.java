package com.code.aon.ui.hyperview.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet destinado a servir las imágenes de los productos.
 * 
 * @author ecastellano
 * @created 19 de junio de 2002
 */

public class HyperViewIconServlet extends HttpServlet {

	/** Se obtiene el Logger adecuado */
	private static final Logger LOGGER = Logger.getLogger(HyperViewIconServlet.class.getName());

	private final static String JOKER_IMAGE_NAME = "/images/tree/missing.gif";

	private static final int buffSize = 1024;

	public static final String RESOURCE_DIR_ATTR = "aon.hyperview.resource.dir";

	private static final String PATH_PARAMETER = "image-path";

	private String imagePath;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		imagePath = config.getInitParameter(PATH_PARAMETER);
	}

	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
		sendFile(req, res);
	}

	private synchronized void sendFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			java.io.IOException {
		OutputStream out = resp.getOutputStream();
		InputStream input = getResourceAsStream(req);
		byte buff[] = new byte[buffSize];
		int read = input.read(buff);
		while (read != -1) {
			out.write(buff, 0, read);
			read = input.read(buff);
		}
		input.close();
		resp.flushBuffer();
	}

	private InputStream getResourceAsStream(HttpServletRequest req) throws FileNotFoundException {
		ServletContext ctx = req.getSession().getServletContext();
		HttpSession session = req.getSession();
		String path = (String) session.getAttribute(RESOURCE_DIR_ATTR);
		String imageName = getImageName(req);
		try {
			URL url = new URL( imageName );
			return url.openStream();
		} catch (MalformedURLException e) {
			// Nothing, it is suposed is a relative path.
		} catch (IOException e) {
			return ctx.getResourceAsStream(imagePath + JOKER_IMAGE_NAME);
		}
		InputStream in = null;
		if (path != null) {
			LOGGER.fine( "RESOURCE DIRECTORY ..: " + path );		
			File file = new File(path, imageName);
			LOGGER.fine( "Attemp to find resource:  " + file );
			if (file.exists() && file.canRead()) {
				in = new FileInputStream(file);
				LOGGER.fine( "Resource found!" );
			} else {
				LOGGER.fine( "File not found or can not be read: "  + file);
			} 
		} else {
			LOGGER.fine( "RESOURCE DIRECTORY NOT SET USING DEFAULT ..: " + imagePath  );
			path = imagePath;
			LOGGER.fine( "Attemp to search resource in default resources: "  + imagePath + imageName);
			in = ctx.getResourceAsStream(imagePath + imageName);
			if (in == null) {
				LOGGER.fine( "File not found or can not be read: "  + imagePath + imageName);
			}
		}
		if (in == null) {
			LOGGER.fine( "Default image sent (?) !");
			in = ctx.getResourceAsStream(imagePath + JOKER_IMAGE_NAME);
		}
		return in;
	}

	private String getImageName(HttpServletRequest req) {
		String path = req.getServletPath();
		int period = path.lastIndexOf(".");
		if (period > 0)
			path = path.substring(0,period);
		return path;
	}

}
