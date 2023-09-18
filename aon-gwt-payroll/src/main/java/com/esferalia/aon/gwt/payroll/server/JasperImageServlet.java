package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections.LRUMap;

public class JasperImageServlet extends HttpServlet {
	
	private static final int DEFAULT_MAX_SIZE = 50;
	
	private static Map<Object, Object> IMAGES_MAP = 
			Collections.synchronizedMap(new LRUMap(DEFAULT_MAX_SIZE));
	
	
	protected static void saveImage(String name , byte data []) {
		IMAGES_MAP.put(name, data);
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		String servletPath = req.getServletPath();
		String imageName = requestURI.substring(requestURI.indexOf(servletPath)+1); 
		
		byte data [] = ( byte [] ) IMAGES_MAP.get(imageName);
		
		if ( data != null ) {
			OutputStream os = resp.getOutputStream();
			os.write(data);
			os.flush();
		}
	}
	
}
