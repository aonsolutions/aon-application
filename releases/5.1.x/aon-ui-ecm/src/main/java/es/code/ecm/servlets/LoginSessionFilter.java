/**
 * 
 */
package es.code.ecm.servlets;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.valves.BackDoorPrincipal;

import es.code.ecm.ContentRepository;
import es.code.ecm.SessionManager;

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
			String workspace = ContentRepository.DEFAULT_WORKSPACE;
			AonGenericPrincipal agp = null;
			try {
				agp = Utils.getSSOPrincipal( httpRequest.getSession().getId() );
			} catch (Exception e) {
				LOGGER.error( e.getMessage() );
			}
			BackDoorPrincipal bdp = null;
			if ( agp == null ) {
				String password = (String) httpRequest.getAttribute( IConstants.AUTH_PASSWORD_NOTE );
				bdp = new BackDoorPrincipal( principal.getName(), password );
			} else {
				bdp = new BackDoorPrincipal( agp.getName(), (String) agp.getCredentials() );
			}
			if ( bdp == null )
				bdp = new BackDoorPrincipal( principal.getName(), null );

			workspace = bdp.getPrincipal().getDomain();
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( "Accessing workspace: " + workspace );

			SessionManager.getInstance().getHierarchyManager( session.getId(), bdp, workspace );
		}
		chain.doFilter(request, response);
	}

}
