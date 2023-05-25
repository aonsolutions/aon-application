package com.code.aon.aio.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;

@WebServlet(name = "Sonar", urlPatterns = { "/sonar" })
public class SonarServlet extends HttpServlet {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SonarServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try  ( PrintStream printStream = new  PrintStream(resp.getOutputStream(), true, Charset.defaultCharset()) ) {
			String message = "[SONAR]: JSSESSION_ID :" + req.getSession().getId();

			resp.setContentType("text/plain");
			resp.setContentLength(message.length());
			resp.setCharacterEncoding(Charset.defaultCharset().name());

			printStream.println(message);
			
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch ( Exception e  ) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}


}
