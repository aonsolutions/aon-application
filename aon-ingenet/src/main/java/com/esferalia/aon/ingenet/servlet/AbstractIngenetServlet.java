package com.esferalia.aon.ingenet.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

public abstract class AbstractIngenetServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String user;
	private String domain;
	private Integer domainId;

	protected static final String PARAM_USERNAME = "username";
	protected static final String PARAM_PASSWORD = "password";
	
	private SimpleDateFormat dateFormatter;
	
	protected String getUser() {
		return user;
	}

	protected String getDomain() {
		return domain;
	}
	
	protected Integer getDomainId() {
		return domainId;
	}


	protected abstract void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException;
	
	
	protected SimpleDateFormat getDateFormatter() {
		if(dateFormatter==null){
			dateFormatter = new SimpleDateFormat("yyyyMMdd");
		}
		return dateFormatter;
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
//		TODO do not allow GET method
//		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void process(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {

		String _domainName = httpRequest.getServerName();
		httpRequest.getContextPath();
		httpRequest.getAuthType();
		httpRequest.getPathInfo();
		httpRequest.getRemoteAddr();
		httpRequest.getServerName();
		httpRequest.getRemoteHost();
		String _username = httpRequest.getParameter(PARAM_USERNAME);
		String _password = httpRequest.getParameter(PARAM_PASSWORD);
		
		if(doLogin(_domainName, _username, _password)){
			user = _username;
			domain = _domainName;
			domainId = searchDomainId(_domainName);
			processRequest(httpRequest, httpResponse);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}
	
	private Integer searchDomainId(String _domainName) {
		AONContext ctx = AONContext.getAONContext(getDomain(), -1, getUser());
		Domain domain = DomainDAO.getDomain(ctx, p -> {
			return p.getNameProperty().eq(_domainName);
		});
		return domain.getId();
	}

	protected boolean doLogin(String domainName, String username, String password) {
		System.out.println("INGENET LOGIN: " + domainName + "@" + username + " (using password "
				+ (password != null ? "YES" : "NO") + ")");
		if (domainName != null && username != null && password != null && domainName.matches("^udapa\\..*")) {
			return "ingenet".equals(username) && "1ng3n3t".equals(password);
		}
		return false;
	}
	
	
}
