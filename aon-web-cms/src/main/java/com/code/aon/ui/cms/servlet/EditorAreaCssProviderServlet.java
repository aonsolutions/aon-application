package com.code.aon.ui.cms.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.cms.Config;
import com.code.aon.faces.component.richfaces.inputRichText.InputRichTextServlet;
import com.code.aon.faces.component.richfaces.inputRichText.InputRichTextUtil;
import com.code.aon.ui.cms.Constants;

/**
 *  
 * @author Consulting & Development. Joseba Urkiri - 02-nov-2006
 * @since 1.0
 *
 */

public class EditorAreaCssProviderServlet extends HttpServlet implements Constants{
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			String css = getCurrentCss(session);
			File f = new File(css);
        	res.setContentType( "text/css" );
        	res.setHeader("Expires", "0");
        	res.setHeader("Pragma", "no-cache");
        	res.setHeader("Cache-Control", "no-store");
            res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
			if(f.exists()){
				System.out.println(">>>>>>>>>>>>>>>> CSS: " + f.getAbsolutePath());
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
			else {
		        String uri = req.getRequestURI();
		        String path = "/" + uri.substring(uri.indexOf(InputRichTextUtil.FCK_FACES_RESOURCE_PREFIX)+InputRichTextUtil.FCK_FACES_RESOURCE_PREFIX.length()+1);
	        	//this.getServletContext().getRequestDispatcher(new InputRichTextServlet().getCustomResourcePath() + path).forward(req,res);
	        	//this.getServletContext().getRequestDispatcher(getCustomResourcePath() + path).forward(request,response);
				System.out.println(">>>>>>>>>>>>>>>> innerCSS: " + path);
				//ClassLoader cl = this.getClass().getClassLoader();
	            ClassLoader cl = InputRichTextServlet.class.getClassLoader();

	            is = cl.getResourceAsStream(path);
		        // if no resource found in classloader return nothing
		        if (is==null) return;
		        // resource found, copying on output stream
		        os = res.getOutputStream();
		        byte[] buffer = new byte[1024];
		        bis = new BufferedInputStream(is);
		        int read = 0;
		        read = bis.read(buffer);
		        while (read!=-1) {
		            os.write(buffer,0,read);
		            read = bis.read(buffer);
		        }
		        os.flush();

//				FCKeditor/editor/css/fck_editorarea.css
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
	
	public static String getCurrentCss(HttpSession session) {
		Config config = (Config)session.getAttribute(SESSION_CONFIG);
		String path = DOMAINS_PATH + "/" + config.getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME +
									"/" + TEMPLATE_PATH +
									"/" + config.getTemplate() +
									"/" + CSS_PATH +
									"/" + CSS_FILE;
		return path;
	}	

}
