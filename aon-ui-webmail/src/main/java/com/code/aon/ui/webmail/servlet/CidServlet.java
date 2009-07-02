package com.code.aon.ui.webmail.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.util.ServleJSFtUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonMessage;

public class CidServlet extends HttpServlet {

	private static final long serialVersionUID = -2679137324617541495L;

	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
    	String cadena = request.getRequestURI();
		cadena = "cid:" + cadena.substring(cadena.lastIndexOf("/")+1,cadena.indexOf(".cid"));
		String id  = "<" + cadena.substring(4,cadena.length()) + ">";

		MessageController messageController = (MessageController) ServleJSFtUtil.getManagedBean( request, response, WebMailConstants.BEAN_MESSAGE );
    	AonMessage aonMessage = messageController.getMessage();

		try{
			MimeMessage message = ( MimeMessage ) aonMessage.getMessage();
			Object content = message.getContent();
			Part part = getPartById( content , id );
			response.setContentType( part.getContentType() );

			OutputStream out = response.getOutputStream();
			InputStream input = part.getInputStream();

			byte buff [] = new byte [ 256 ];
			int read = input.read ( buff );
			while ( read != -1  ) {
				out.write ( buff,0,read );
				read = input.read ( buff );
			}
			response.flushBuffer();
		}catch (Exception e){}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		processRequest(req,res);
	}
	
	private Part getPartById(Object mimepart , String id ) throws MessagingException, IOException {

		Part part = null;
		if (mimepart instanceof Multipart){
			Multipart multipart = (Multipart)mimepart;
			for (int i = 0 ; i < multipart.getCount() && part == null  ; i++ ){
				MimePart candidate =  ( MimePart ) multipart.getBodyPart(i);
				if ( id.equals( candidate.getContentID() ) ) {
					part = candidate;
				}
				else {
					part = getPartById( candidate.getContent() , id );
				}
			}
		}
		return part;
	}

}
