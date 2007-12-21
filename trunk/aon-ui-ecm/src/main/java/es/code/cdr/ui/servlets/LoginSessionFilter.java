/**
 * 
 */
package es.code.cdr.ui.servlets;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.bridge.session.BackDoorAuthenticationFilter;
import com.code.aon.jaas.valves.BackDoorPrincipal;

import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.SessionManager;
import es.code.repository.IProvider;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class LoginSessionFilter extends BackDoorAuthenticationFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
System.out.println( "service LoginSessionFilter:" + httpRequest.getRequestURI() );
		HttpSession session = httpRequest.getSession( true );
		if ( (httpRequest.getUserPrincipal() != null || httpRequest.getServletPath().indexOf( "index.jsp" ) > -1) 
				&& SessionManager.getInstance().getHierarchyManager( session.getId() ) == null ) {
			BackDoorPrincipal bdp = null;
			String workspace = ContentRepository.DEFAULT_WORKSPACE;
			if (httpRequest.getUserPrincipal() != null) { 
				bdp = deserialize( session.getId() );
				workspace = bdp.getPrincipal().getDomain();
			} else {
				Properties props = ContentRepository.getProvider().getRi().getProps();
				bdp = 
					new BackDoorPrincipal( props.getProperty( IProvider.REPOSITORY_CONNECTION_USER ), props.getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ) );
			}
			SessionManager.getInstance().getHierarchyManager( session.getId(), bdp, workspace );
		}
		chain.doFilter(request, response);
	}

}
