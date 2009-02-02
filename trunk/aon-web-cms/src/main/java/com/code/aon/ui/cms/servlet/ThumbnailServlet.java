package com.code.aon.ui.cms.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.cms.Config;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ImageUtil;

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
		
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			String servlet = req.getServletPath();
			String basePath = getImagesPath(session);
			String path = basePath + servlet;
			String file = path.replaceAll(".thumbnail", "");
			boolean document = false;
			//Comprobamos la extension...
			String ext = file.substring(file.lastIndexOf(".")+1);
			if (ext != null && !ext.trim().equals("")) {
				if (ext.equals("doc") || ext.equals("docx") || ext.equals("wps")) {
					ext = "word.png";
					document = true;
				}
				else {
					if (ext.equals("xls") || ext.equals("xlsx")) {
						ext = "excel.png";
						document = true;
					}
					else {
						if (ext.equals("pps") || ext.equals("pptx") || ext.equals("ppt")) {
							ext = "powerpoint.png";
							document = true;
						}
						else {
							if (ext.equals("wmv") || ext.equals("avi") || ext.equals("mov")) {
								ext = "video.png";
								document = true;
							}
							else {
								if (ext.equals("wma") || ext.equals("mp3") || ext.equals("rm")) {
									ext = "audio.png";
									document = true;
								}
								else {
									if (ext.equals("html") || ext.equals("htm")) {
										ext = "html.png";
										document = true;
									}
									else {
										if (ext.equals("pdf")) {
											ext = "pdf.png";
											document = true;
										}

									}
								}
							}
						}
					}
				}
			}
			File f = new File(file);
        	res.setContentType( "image/jpeg" );
        	res.setHeader("Expires", "0");
        	res.setHeader("Pragma", "no-cache");
        	res.setHeader("Cache-Control", "no-store");
            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
			if (!document && f.exists() && f.isFile()) {
				ImageUtil.resize(file, res.getOutputStream(), maxDim);
            }
            else {
            	String image = OTHER_IMAGE;
            	if (!f.exists() || !f.isFile()) {
            		image = BLANK_IMAGE;
            	}
            	res.setContentType( "image/png" );
            	if (document) image = ext;
        		is = ThumbnailServlet.class.getResourceAsStream(image);
        		bis = new BufferedInputStream(is);
                os = res.getOutputStream();
        		bos = new BufferedOutputStream(os);
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
        }finally{
        	try {bis.close();} catch (Exception e) {}
        	try {bos.close();} catch (Exception e) {}
        	try {is.close();} catch (Exception e) {}
        	try {os.close();} catch (Exception e) {}
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
