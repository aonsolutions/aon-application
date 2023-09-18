package com.code.aon.aio.servlet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;

public class NewDomain extends HttpServlet {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public NewDomain() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			DomainServletUtil util = new DomainServletUtil(request);
			String[] command = util.getNewDomainCommand();
			System.out.print( StringUtils.join(command, " ") );
			
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			int exitVal = p.waitFor();
			
			util.doResponse(response, stdInput, stdError, exitVal );
			
		} catch (Throwable e) {
			
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("text/html");
			response.getWriter().print("<html><head><head><body>");
			response.getWriter().print("Se ha producido un error interno. [" + e.getMessage()+ "]");
			response.getWriter().print("</body></html>");
			response.getWriter().flush();
			
			response.getWriter().flush();
			e.printStackTrace();
		}

	}
	
}
