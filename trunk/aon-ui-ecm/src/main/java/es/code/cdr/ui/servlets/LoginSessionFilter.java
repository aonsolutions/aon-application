/**
 * 
 */
package es.code.cdr.ui.servlets;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.SessionManager;
import es.code.repository.IProvider;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class LoginSessionFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(true);
//	************************************************************************************************
/*	TODO. Conseguir la clave con la que se validó el usuario. Codigo original.
		if ( httpRequest.getUserPrincipal() != null
				&& SessionManager.getInstance().get( session.getId() ) == null ) {
			try {
				SimpleCredentials sc = new SimpleCredentials( httpRequest.getUserPrincipal().getName(), "".toCharArray() );
 */
		System.out.println( "LoginSessionFilter:" + httpRequest.getRequestURI() );
		if ( (httpRequest.getUserPrincipal() != null || httpRequest.getServletPath().indexOf( "index.jsp" ) > -1) 
				&& SessionManager.getInstance().get( session.getId() ) == null ) {
			try {
				SimpleCredentials sc;
				if (httpRequest.getUserPrincipal() != null) 
					sc = new SimpleCredentials( httpRequest.getUserPrincipal().getName(), "".toCharArray() );
				else
					sc = new SimpleCredentials( ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER ), ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ).toCharArray() );
//	************************************************************************************************
				Session jcrSession = ContentRepository.getSessionInstance( sc );
				SessionManager.getInstance().put( session.getId(), jcrSession );
			} catch (LoginException e) {
				throw new ServletException(e);
			} catch (NoSuchWorkspaceException e) {
				throw new ServletException(e);
			} catch (RepositoryException e) {
				throw new ServletException(e);
			}
		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
