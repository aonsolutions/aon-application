package com.code.aon.ui.cms.servlet;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import com.code.aon.cms.Config;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ImageUtil;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;

/**
 *  
 * @author Consulting & Development. Joseba Urkiri - 02-nov-2006
 * @since 1.0
 *
 */

public class ThumbnailServlet extends HttpServlet implements Constants{
	
	private static final Logger LOGGER = Logger.getLogger(ThumbnailServlet.class.getName());
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		int maxDim = 100;
		if (req.getParameter("width") != null) maxDim = Integer.parseInt(req.getParameter("width").toString());
		try {
			String servlet = req.getServletPath();
			String basePath = getImagesPath(session);
			String path = basePath + servlet;
			String file = path.replaceAll(".thumbnail", "");
			File f = new File(file);
        	res.setContentType( "image/jpeg" );
        	res.setHeader("Expires", "0");
        	res.setHeader("Pragma", "no-cache");
        	res.setHeader("Cache-Control", "no-store");
            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
			if(f.exists()){
                ImageUtil.resize(file, res.getOutputStream(), maxDim);
            }
            else {
        		InputStream is = ThumbnailServlet.class.getResourceAsStream(BLANK_IMAGE);
        		BufferedInputStream bis = new BufferedInputStream(is);
                OutputStream os = res.getOutputStream();
        		BufferedOutputStream bos = new BufferedOutputStream(os);
        		byte[] input = new byte[1024];
        		boolean eof = false;
        		while (!eof) {
        			int length = bis.read(input);
        			if (length == -1) {
        				eof = true;
        			}
        			else {
        				bos.write(input, 0, length);
        			}
        		}
        		bos.flush();
        		bis.close();
        	}
            res.flushBuffer();
        } 
		catch (Throwable th) {
            th.printStackTrace();
            throw new ServletException(th.getMessage(), th);
        }
    }
	

	
	public static String getImagesPath(HttpSession session) {
		Config config = (Config)session.getAttribute(SESSION_CONFIG);
		String path = DOMAINS_PATH + "/" + config.getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + config.getPreviewUrl() +
									"/" + IMAGES_PATH;
		return path;
	}	

}
