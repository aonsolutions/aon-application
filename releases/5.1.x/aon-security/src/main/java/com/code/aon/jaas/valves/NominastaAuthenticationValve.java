/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.IUser;

/**
 * Registra las llamadas al SERVLET de cosmos para validar al usuario como invitado y de esta forma
 * habilitar la auditoria.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 22/10/2007
 */
public class NominastaAuthenticationValve extends ValveBase implements IConstants {

	/** CosmosAuthenticationValve Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(NominastaAuthenticationValve.class);

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		String contextPath = request.getContextPath();
		boolean isRequestURI = request.getRequestURI().startsWith( contextPath + "/view/cosmos/" );
		if ( isRequestURI ) {
			IUser user = getUser( contextPath, request.getHost().getName(), request.getParameter( "usuario" ) );
			String username = IConstants.IDENTITY_SEPARATOR + request.getHost().getName() + contextPath;
			String password = null;
			if ( user != null ) {
				username = user.getId() + username;
				password = getPassword( user, Integer.parseInt( request.getParameter( "empresa" ) ) );
			} else {
				username = "invitado" + username;
				password = "demo";
				LOGGER.warn( "Using invitado to log in the application" );
			}
			List<String> roles = getRoles( request.getContextPath() );
			register( request, new AonGenericPrincipal( request, username, password, roles ), AUTH_TYPE );
		}
		try {
			getNext().invoke( request, response );// Perform the request
			if ( isRequestURI )
				return;
		} finally {
			request.removeNote( AUTH_TYPE );
			request.removeNote( AUTH_USERNAME_NOTE );
			request.removeNote( AUTH_PASSWORD_NOTE );
			request.removeAttribute( AUTH_PASSWORD_NOTE );
		}
	}

	/**
	 * Register the principal with the request, session etc just the way AuthenticatorBase does.
	 * 
	 * @param request Catalina Request
	 * @param principal <code>AonGenericPrincipal</code> generated via authentication
     * @param authType The authentication type to be registered
	 */
	private void register(Request request, AonGenericPrincipal principal, String authType) {
		request.setAuthType( authType );
		request.setUserPrincipal( principal ); 
		if ( principal != null ) {
			request.setNote( AUTH_USERNAME_NOTE, principal.getUserPrincipal() );
			request.setNote( AUTH_PASSWORD_NOTE, principal.getCredentials() );
		}
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( true );
		if(session != null) {
			session.setAuthType( authType );
			session.setPrincipal( principal );
		}
		flushSessionPrincipal( session.getId(), principal );
	}

	/**
	 * Flush principal in the AonSessionManager MBean.
	 * 
	 * @param sessionId
	 * @param principal
	 */
	private void flushSessionPrincipal(String sessionId, AonGenericPrincipal principal) {
		try {
			LOGGER.debug( "Flushing LOCAL session: {} principal: {}", sessionId, principal.getUserPrincipal() );
			Object[] params = { sessionId, principal };
			String[] sig = { String.class.getName(), Object.class.getName() };
			mserver.invoke( new ObjectName( "jboss.admin:service=AonSessionManager" ), "flushSSOPrincipal", params, sig );
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/**
	 * Return the list of application roles.
	 * 
	 * @param ctx
	 * @return
	 */
	private List<String> getRoles(String ctx) {
		try {
			List<String> roles = new ArrayList<String>();
			Object[] params = { ctx };
			String[] sig = { String.class.getName() };
			IApplication app = 
				(IApplication) mserver.invoke( new ObjectName( "jboss.admin:service=AonLdap" ), "getApplication4Ctx", params, sig );
			Iterator<IRole> iter = app.roles().iterator(); 
			while ( iter.hasNext() ) {
				IRole role = iter.next();
				roles.add( role.getId() );
			}
			return roles;
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}

	/**
	 * Return the list of application roles.
	 * 
	 * @param ctx
	 * @param domain
	 * @param username
	 * @return
	 */
	private IUser getUser(String ctx, String domain, String username) {
		try {
			Object[] params = { ctx, domain };
			String[] sig = { String.class.getName(), String.class.getName() };
			IDomain d = 
				(IDomain) mserver.invoke( new ObjectName( "jboss.admin:service=AonLdap" ), "getDomain", params, sig );
			return d.standaloneUsers().get( username ); 
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}

	/**
	 * Return authenticated user password asking to application database.
	 * 
	 * @param user
	 * @param empresa
	 * @return
	 */
	private String getPassword(IUser user, Integer empresa) {
		String password = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			Context ctx = new InitialContext();
			Context envContext  = (Context)ctx.lookup("java:/comp/env");
			DataSource datasource =  (DataSource)envContext.lookup("AonPayrollDS");
			if ( datasource != null ) {
				conn = datasource.getConnection();
				stmt = conn.createStatement();
				String select = "SELECT clave FROM usuario"
							+ " WHERE cdg=" + user.getId()
							+ " AND empresa=" + empresa;
				ResultSet rs = stmt.executeQuery( select );
				password = rs.getString( 1 );
			}
		} catch(NamingException e) {
			 LOGGER.error( "JNDI could not create InitalContext", e );
		} catch (SQLException e) {
			 LOGGER.error( "Unable to obtain authentication info", e );
		} finally {
			if ( stmt != null )
				try {
					stmt.close();
				} catch (SQLException e) {
					 LOGGER.error( "Unable to close statement", e );
				}
			if ( conn != null )
				try {
					conn.close();
				} catch (SQLException e) {
					 LOGGER.error( "Unable to close connection", e );
				}
		}
		return password;
	}
}