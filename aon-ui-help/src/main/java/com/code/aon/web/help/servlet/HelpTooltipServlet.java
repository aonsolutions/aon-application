package com.code.aon.web.help.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.ui.help.pdf.PdfExtractor;
import com.code.aon.ui.help.pdf.PdfImageExtractor;
import com.code.aon.ui.help.pdf.PdfSearcher;


@WebServlet("/Tooltip/*")
public class HelpTooltipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private HashMap<String, String> files;
	
	
	public HelpTooltipServlet() {
		super();
		this.files = new HashMap<>();
		
		files.put("LABORAL Manual de USUARIO", "payroll_names.pdf");
		files.put("CONTABILIDAD Manual de USUARIO", "account_names.pdf");
		files.put("FISCAL Manual de USUARIO", "fiscal_names.pdf");
		files.put("CONFIGURACION Manual de USUARIO", "config_names.pdf");

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if((request.getParameter("name") == null && request.getParameter("page") == null) || request.getParameter("filename") == null) {
			response.sendError(400);
			return;
		}		
		
		String name = request.getParameter("name");		
        String filename = this.files.get(request.getParameter("filename"));
		
        if(filename == null) {
        	response.sendError(404);
        	return;
        }
        
        
        response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "inline; filename=tooltip-" + name + ".jpg" );
        ServletOutputStream output = response.getOutputStream();

        
        try ( InputStream input = PdfSearcher.class.getResourceAsStream(filename)) {
	        
        	InputStream image;
        	
        	if(request.getParameter("page") != null) {
        		image = PdfExtractor.fromPage(input, 0); 		
        	} else {
            	image = PdfExtractor.fromDestinationName(input, name); 		
        	}
        	
 	        if(image == null) {
	        	response.sendError(404);
	        	return;
	        }
        	
        	int length;
 	        byte[] bytes = new byte[1024];
 	
 	        // copy data from input stream to output stream
 	        while ((length = image.read(bytes)) != -1) {
 	            output.write(bytes, 0, length);
 	        }
 	       
 	        image.close();

        }
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
	
	
	


}
