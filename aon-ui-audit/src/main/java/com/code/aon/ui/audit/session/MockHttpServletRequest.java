package com.code.aon.ui.audit.session;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.jaas.auth.AuthPrincipal;

public class MockHttpServletRequest implements HttpServletRequest {
	
	private static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
	
	private static final String DEFAULT_REMOTE_HOST = "localhost";
	
	private ServletContext servletContext;
	
	private AuthPrincipal userPrincipal;
	
	private HttpSession httpSession;
	
	public MockHttpServletRequest(ServletContext servletContext) {
		this.servletContext = servletContext;
	}	
	
	public Object getAttribute(String name) {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getAttributeNames() {
		return Collections.enumeration(Collections.EMPTY_LIST);
	}

	public String getCharacterEncoding() {
		return null;
	}

	public void setCharacterEncoding(String characterEncoding) {
	}

	public int getContentLength() {
		return -1;
	}

	public String getContentType() {
		return null;
	}

	public ServletInputStream getInputStream() {
		return null;
	}

	public String getParameter(String name) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return null;
	}

	public String[] getParameterValues(String name) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		return null;
	}

	public String getProtocol() {
		return null;
	}

	public String getScheme() {
		return null;
	}

	public String getServerName() {
		return this.userPrincipal.getDomain();
	}

	public int getServerPort() {
		return -1;
	}

	public BufferedReader getReader() throws UnsupportedEncodingException {
		return null;
	}

	public String getRemoteAddr() {
		return DEFAULT_REMOTE_ADDR;
	}

	public String getRemoteHost() {
		return DEFAULT_REMOTE_HOST;
	}

	public void setAttribute(String name, Object value) {
	}

	public void removeAttribute(String name) {
	}

	public Locale getLocale() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getLocales() {
		return Collections.enumeration(Collections.EMPTY_LIST);
	}

	public boolean isSecure() {
		return false;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	public String getRealPath(String path) {
		return null;
	}

	public int getRemotePort() {
		return -1;
	}

	public String getLocalName() {
		return null;
	}

	public String getLocalAddr() {
		return null;
	}

	public int getLocalPort() {
		return -1;
	}

	public String getAuthType() {
		return null;
	}

	public Cookie[] getCookies() {
		return null;
	}

	public long getDateHeader(String name) {
		return -1L;
	}

	public String getHeader(String name) {
		return null;		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getHeaders(String name) {
		return Collections.enumeration(Collections.EMPTY_LIST);		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getHeaderNames() {
		return Collections.enumeration(Collections.EMPTY_LIST);
	}

	public int getIntHeader(String name) {
		return -1;
	}

	public String getMethod() {
		return null;
	}

	public String getPathInfo() {
		return null;
	}

	public String getPathTranslated() {
		return null;
	}

	public String getContextPath() {
		return null;
	}

	public String getQueryString() {
		return null;
	}

	public String getRemoteUser() {
		return null;
	}

	public boolean isUserInRole(String role) {
		return false;
	}
	
	public void setUserPrincipal(Principal userPrincipal) {
		this.userPrincipal = (AuthPrincipal) userPrincipal;
	}	

	public Principal getUserPrincipal() {
		return this.userPrincipal;
	}

	public String getRequestedSessionId() {
		return null;
	}

	public String getRequestURI() {
		return null;
	}

	public StringBuffer getRequestURL() {
		return null;
	}

	public String getServletPath() {
		return null;
	}

	public HttpSession getSession(boolean create) {
		if (this.httpSession == null ) {
			this.httpSession = new MockHttpSession(servletContext);
		}
		return this.httpSession;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public boolean isRequestedSessionIdValid() {
		return false;
	}

	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

}
