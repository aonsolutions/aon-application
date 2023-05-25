package com.code.aon.ui.common.session;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.code.aon.jaas.auth.AuthPrincipal;

public class MockHttpServletRequest implements HttpServletRequest {
	
	private static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
	
	private static final String DEFAULT_REMOTE_HOST = "localhost";
	
	private ServletContext servletContext;
	
	private AuthPrincipal userPrincipal;
	
	private HttpSession httpSession;

	public MockHttpServletRequest( AuthPrincipal userPrincipal ) {
		this.userPrincipal = userPrincipal;
	}	
	
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

  // --------------------------------------------------------------------------
  // Since Servlet API 3.1.0

  public long getContentLengthLong(){
    return -1L;  
  }

  public ServletContext getServletContext(){
    return null;
  }

  public AsyncContext startAsync() throws IllegalStateException{
    throw new IllegalStateException();
  }

  public AsyncContext startAsync(ServletRequest servletRequest,
                                   ServletResponse servletResponse)
            throws IllegalStateException{
    throw new IllegalStateException();
  }

  public boolean isAsyncStarted(){
    return false;
  }

  public boolean isAsyncSupported(){
    return false;
  }

  public AsyncContext getAsyncContext(){
    return null;
  }
  
  public DispatcherType getDispatcherType() {
    return null;
  }

  public String changeSessionId(){
    throw new IllegalStateException();
  }

  public boolean authenticate(HttpServletResponse response) 
	throws IOException,ServletException{
    throw new ServletException();
  }

  public void logout() throws ServletException{
    throw new ServletException();
  }

  public void login(String username, String password) 
	throws ServletException{
    throw new ServletException();
  }

  public Part getPart(String name) throws IOException, ServletException{
    throw new ServletException();
  }

  public Collection<Part> getParts() throws IOException, ServletException{
    throw new ServletException();
  }

  public <T extends HttpUpgradeHandler> T  upgrade(Class<T> handlerClass)
        throws IOException, ServletException {
    throw new ServletException();
  }
  //  Jakarta EE

  public String getRequestId() {
    return null;
  }

  public String getProtocolRequestId() {
    return null;
  }

  public ServletConnection getServletConnection() {
    return null;
  }

}
