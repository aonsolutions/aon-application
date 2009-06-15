package com.code.aon.ui.cms.servlet;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.cms.Config;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ImageUtil;

/**
 *  
 * @author Consulting & Development. Joseba Urkiri - 02-nov-2006
 * @since 1.0
 *
 */

public class ThumbnailServlet extends HttpServlet implements Constants{
	
	private static final String WIDTH = "width";
	
	private static final String LAST_MODIFIED = "lm";

	private static final long serialVersionUID = 7628878719631691763L;
	
	private static final long EXPIRED = 8640000L;

	private static final Logger LOGGER = Logger.getLogger(ThumbnailServlet.class.getName());
	
	private void setNoCacheControl( HttpServletResponse res ) {
    	res.setHeader("Expires", "0");
    	res.setHeader("Pragma", "no-cache");
    	res.setHeader("Cache-Control", "no-store");								
	}

	private void setCacheableControl( HttpServletResponse res ) {
        res.setDateHeader("Expires", System.currentTimeMillis() + (EXPIRED * 1000L));
        res.setHeader("Cache-control", "max-age=" + EXPIRED);
	}
	
	private void setCacheControl( HttpServletRequest req, HttpServletResponse res ) {
		if (req.getParameter(LAST_MODIFIED) != null) {
			setCacheableControl(res);
		} else {
			setNoCacheControl(res);
		}
	}
	
	/*
	private long getLastModified( File file ) {
		if ( file.exists() && file.isFile() && file.canRead() ) {
			long value = file.lastModified();
			if ( value != 0 ) {
				return value / 1000 * 1000;
			}
		}
		return -1;
	}
	
	@Override
	protected long getLastModified(HttpServletRequest req) {
		File file = new File( getFile(req) );
		long value = getLastModified(file);
		if ( value != -1 ) {
			return value;
		}
		return super.getLastModified(req);
	}
	*/
	
	private String getFile( HttpServletRequest req ) {
		String servlet = req.getServletPath();
		String basePath = getImagesPath(req.getSession());
		String path = basePath + servlet;
		String file = path.replaceAll(".thumbnail", "");
		return file;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		int maxDim = 100;
		if (req.getParameter(WIDTH) != null) {
			maxDim = Integer.parseInt(req.getParameter(WIDTH).toString());
		}
		
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			String file = getFile(req);
			//Comprobamos la extension...
			String ext = FilenameUtils.getExtension(file);
			String resource = null;
			if (! StringUtils.isBlank(ext) ) {
				if (ext.equals("doc") || ext.equals("docx") || ext.equals("wps")) {
					resource = "word.png";
				} else if (ext.equals("xls") || ext.equals("xlsx")) {
					resource = "excel.png";
				} else if (ext.equals("pps") || ext.equals("pptx") || ext.equals("ppt")) {
					resource = "powerpoint.png";
				} else if (ext.equals("wmv") || ext.equals("avi") || ext.equals("mov")) {
					resource = "video.png";
				} else if (ext.equals("wma") || ext.equals("mp3") || ext.equals("rm")) {
					resource = "audio.png";
				} else if (ext.equals("html") || ext.equals("htm")) {
					resource = "html.png";
				} else if (ext.equals("pdf")) {
					resource = "pdf.png";
				}
			}
			File f = new File(file);
			if ( (resource == null) && f.exists() && f.isFile()) {
				setCacheControl(req, res);
	        	res.setContentType( "image/jpeg" );
				ImageUtil.resize(f, res.getOutputStream(), maxDim);
            } else {
            	String image = OTHER_IMAGE;
            	if ( resource != null ) {
            		image = resource;
            	} else if (!f.exists() || !f.isFile()) {
            		image = BLANK_IMAGE;
            	}
            	setCacheableControl(res);
            	res.setContentType( "image/png" );
        		is = ThumbnailServlet.class.getResourceAsStream(image);
        		bis = new BufferedInputStream(is);
                os = res.getOutputStream();
        		bos = new BufferedOutputStream(os);
        		IOUtils.copyLarge(bis, bos);
        		bis.close();
        	}
            res.flushBuffer();
        } catch (Throwable th) {
        	LOGGER.log(Level.SEVERE, th.getMessage(), th);
            throw new ServletException(th.getMessage(), th);
        } finally {
        	IOUtils.closeQuietly(bis);
        	IOUtils.closeQuietly(bos);
        	IOUtils.closeQuietly(is);
        	IOUtils.closeQuietly(os);
    		is = null;
    		bis = null;
            os = null;
    		bos = null;
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
