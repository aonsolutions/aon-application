/**
 * 
 */
package es.code.cdr.ui.servlets;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.valves.BackDoorAuthenticationValve;
import com.code.aon.jaas.valves.BackDoorPrincipal;

import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.SessionManager;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class LoginSessionFilter implements Filter {

	/** LoginSessionFilter class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( LoginSessionFilter.class.getName() );

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
				throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession( true );
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug( "Login user:" + httpRequest.getUserPrincipal() + " " + session.getId() );

		Principal principal = httpRequest.getUserPrincipal();
		if ( (principal != null || httpRequest.getServletPath().indexOf( "index.jsp" ) > -1) 
				&& SessionManager.getInstance().getHierarchyManager( session.getId() ) == null ) {
			BackDoorPrincipal bdp = null;
			String workspace = ContentRepository.DEFAULT_WORKSPACE;
			String requestId = session.getId() + httpRequest.getContextPath();
			Request activeRequest = BackDoorAuthenticationValve.getActiveRequest( requestId );
			String username = principal.getName();
			String authType = httpRequest.getAuthType();
			if ( authType.equals( BackDoorAuthenticationValve.AUTH_TYPE ) ) {
				AuthPrincipal auth = new AuthPrincipal( principal.getName() );
				username = auth.getShortName() + IConstants.IDENTITY_SEPARATOR + auth.getDomain() 
									+ activeRequest.getContextPath();
			}
			if ( activeRequest != null ) {
				String password = (String) activeRequest.getNote( BackDoorAuthenticationValve.AUTH_PASSWORD_NOTE );
				bdp = new BackDoorPrincipal( username, password );
			} else {
				throw new ServletException( "Unable to access Repository[" + username + ", " + session.getId() + "]" );
//				Properties props = ContentRepository.getProvider().getRi().getProps();
//				username = props.getProperty( IProvider.REPOSITORY_CONNECTION_USER ) + props.getProperty( IProvider.REPOSITORY_CONNECTION_CONTEXT );
//				String password = props.getProperty( IProvider.REPOSITORY_CONNECTION_PSWD );
//				bdp = new BackDoorPrincipal( username, password );
			}
			workspace = bdp.getPrincipal().getDomain();
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( "Accessing workspace: " + workspace );

			SessionManager.getInstance().getHierarchyManager( session.getId(), bdp, workspace );
		}
		chain.doFilter(request, response);
	}

}
