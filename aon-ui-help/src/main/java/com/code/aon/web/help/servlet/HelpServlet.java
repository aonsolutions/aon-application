package com.code.aon.web.help.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.ui.help.pdf.PdfIndexer;
import com.code.aon.web.help.service.drive.DriveService;
import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.SearchFiles;


@WebServlet("/help/*")
public class HelpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public HelpServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/pdf;charset=UTF-8");

        response.addHeader("Content-Disposition", "inline; filename=" + request.getPathInfo());

        ServletOutputStream output = response.getOutputStream();
        
        
        try ( InputStream input = PdfIndexer.class.getResourceAsStream("payroll.pdf") ) {
	        
	        int length;
	        byte[] bytes = new byte[1024];
	
	        // copy data from input stream to output stream
	        while ((length = input.read(bytes)) != -1) {
	            output.write(bytes, 0, length);
	        }
        } 
        
        
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
	
	
	


}
