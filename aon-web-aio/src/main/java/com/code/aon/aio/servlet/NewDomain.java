package com.code.aon.aio.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

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
				if (StringUtils.isNotBlank(value)){
					buf.append(" --");	
					buf.append(name);
					buf.append("=");
					buf.append(value);
				}
			}
			String command = "newDomain.py " + buf.toString();
			System.out.println( command);
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String line = "";
			while ((line = stdInput.readLine()) != null) {
				response.getWriter().println(line);
			}
			while ((line = stdError.readLine()) != null) {
				response.getWriter().println(line);
			}
			int exitVal = p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
