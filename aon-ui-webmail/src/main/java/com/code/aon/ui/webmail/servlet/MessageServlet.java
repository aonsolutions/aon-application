package com.code.aon.ui.webmail.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.util.ServleJSFtUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;

public class MessageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 8241784167281247172L;
	
	private static final Logger LOGGER = Logger.getLogger(MessageServlet.class.getName());

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {

		MessageController messageController = (MessageController) ServleJSFtUtil.getManagedBean( request, response, WebMailConstants.BEAN_MESSAGE );
		try {	
            response.setContentType( MimeType.MIME_HTML.getName() );
           	response.setCharacterEncoding( CharEncoding.UTF_8 );
            Writer writer = new OutputStreamWriter( response.getOutputStream(), CharEncoding.UTF_8 );
            writer.write( "<html><body>");
            String context = messageController.getMessageContent();
            writer.write( context );
            writer.write( "</body></html>");
            writer.flush();
            response.flushBuffer();
		}catch (Throwable th){
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		processRequest(req,res);
	}

}
