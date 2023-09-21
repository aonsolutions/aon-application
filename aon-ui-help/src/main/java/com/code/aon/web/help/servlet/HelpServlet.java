package com.code.aon.web.help.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.ui.help.pdf.PdfSearcher;
import com.code.aon.ui.help.pdf.IndexPDFFiles.PdfIndexProperties;


@WebServlet("/help/*")
public class HelpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private HashMap<String, String> files;
	
	
	public HelpServlet() {
		super();
		this.files = new HashMap<>();
		
		files.put("LABORAL Manual de USUARIO", "payroll_names.pdf");
		files.put("CONTABILIDAD Manual de USUARIO", "account_names.pdf");
		files.put("FISCAL Manual de USUARIO", "fiscal_names.pdf");
		files.put("CONFIGURACION Manual de USUARIO", "config_names.pdf");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/pdf;charset=UTF-8");
        response.addHeader("Content-Disposition", "inline; filename=" + request.getPathInfo());
        
        ServletOutputStream output = response.getOutputStream();
        String filename = this.files.get(request.getPathInfo().substring(1).replaceAll("\\+" , " ").replace(".pdf",""));        

        if(filename == null) {
        	response.sendError(404);
        	return;
        }
        
        try ( InputStream input = PdfSearcher.class.getResourceAsStream(filename)) {
        	
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
