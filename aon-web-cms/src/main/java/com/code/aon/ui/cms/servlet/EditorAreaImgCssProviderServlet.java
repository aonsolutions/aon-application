package com.code.aon.ui.cms.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.cms.Config;
import com.code.aon.ui.cms.Constants;


/**
 * @author srecinto
 *
 */
public class EditorAreaImgCssProviderServlet extends HttpServlet implements Constants{

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
        String uri = req.getRequestURI();
        System.out.println(">>>>>>>>>>> Reading IMG CSS file: " + uri);
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			String path = getCurrentImgCssPath(session);
			String img = path + "/imagen.jpg";
			System.out.println(">>>>>>>>>>> Reading IMG CSS file: " + img);
			File f = new File(img);
	        if (uri.endsWith(".gif")) res.setContentType("image/gif;");
	        if (uri.endsWith(".jpg")) res.setContentType("image/jpeg;");
	        if (uri.endsWith(".png")) res.setContentType("image/png;");
        	res.setHeader("Expires", "0");
        	res.setHeader("Pragma", "no-cache");
        	res.setHeader("Cache-Control", "no-store");
            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
			if(f.exists()){
        		is = new FileInputStream(f);
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
			}
            res.flushBuffer();
        } 
		catch (Throwable th) {
            th.printStackTrace();
            throw new ServletException(th.getMessage(), th);
        }finally{
    		try{bis.close();}catch(Exception e){}
    		try{is.close();}catch(Exception e){}
    		try{bos.close();}catch(Exception e){}
    		try{os.close();}catch(Exception e){}
    		bis = null;
    		is = null;
    		bos = null;
    		os = null;
        }
    }

	public static String getCurrentImgCssPath(HttpSession session) {
		Config config = (Config)session.getAttribute(SESSION_CONFIG);
		String path = DOMAINS_PATH + "/" + config.getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME +
									"/" + TEMPLATE_PATH +
									"/" + config.getTemplate() +
									"/" + CSS_PATH +
									"/" + IMG_PATH;
		return path;
	}	
}
