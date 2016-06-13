package com.esferalia.aon.gwt.issues.server;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@MultipartConfig

@WebServlet(name = "Issues Servlet", urlPatterns = {"/aon_gwt_issues/*" })
public class IssuesServlet extends HttpServlet {

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

}