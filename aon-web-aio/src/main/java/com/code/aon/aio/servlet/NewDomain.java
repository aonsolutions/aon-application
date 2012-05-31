package com.code.aon.aio.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class NewDomain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public NewDomain() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Enumeration<?> parameters = request.getParameterNames();
			StringBuilder buf = new StringBuilder(); 
			while (parameters.hasMoreElements()) {
				String name = (String) parameters.nextElement();
				String value = request.getParameter(name);
				System.out.println( "Parameter: " + name +"="+ value+"#" );
				if (StringUtils.isNotBlank(value)){
					buf.append(" --");	
					buf.append(name);
					if (!StringUtils.equals(name, "verbose")) {
						buf.append("=");
						buf.append(value);
					}
				}
			}
			String command = "new_domain.py " + buf.toString();
			System.out.println( command);
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			response.setContentType("text/html");
			response.getWriter().println("<html><head><head><body>");
			String line = "";
			while ((line = stdInput.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			while ((line = stdError.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			int exitVal = p.waitFor();
			response.getWriter().println("</body></html>");
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String parseLine(String line) {
		line = StringUtils.replace(line, "[91m", "<p style='color: red;'>");
		line = StringUtils.replace(line, "[32m", "<p style='color: green;'>");
		line = StringUtils.replace(line, "[1m", "<p style='font-weight: bold;'>");
		line = StringUtils.replace(line, "[0m", "</p>");
		return line;
	}
}
