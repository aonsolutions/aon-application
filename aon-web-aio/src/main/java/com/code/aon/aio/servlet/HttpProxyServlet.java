package com.code.aon.aio.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import jakarta.mail.internet.ContentType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "HttpProxyServlet", urlPatterns = { "/proxy" })
public class HttpProxyServlet extends HttpServlet {

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		URI uri = URI.create(req.getParameter("url"));
		URLConnection connection = uri.toURL().openConnection();
		resp.setContentType(connection.getContentType());
		resp.setContentLength(connection.getContentLength());
		//resp.setHeader("Content-Security-Policy"," frame-ancestors 'self' http://payroll-test.aonsolutions.org:8080/ ;");
		try ( InputStream is = connection.getInputStream() ) {
			byte[] content = is.readAllBytes();
			
			resp.getOutputStream().write(content);
		}
	}
}
