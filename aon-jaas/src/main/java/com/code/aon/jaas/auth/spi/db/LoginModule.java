package com.code.aon.jaas.auth.spi.db;

import static com.code.aon.jaas.auth.IConstants.DEFAULT_CONTEXT_PATH;

import java.net.IDN;
import java.security.Principal;
import com.code.aon.jaas.security.acl.Group;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.SimpleGroup;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.spi.UsernamePasswordLoginModule;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class LoginModule extends UsernamePasswordLoginModule {

	private Util dbUtil;
	private String dataBaseName;
	private BasicInfo appplicationUser;
	
	private String domainName;
	private String contextPath;

	/**
	 * Initialize this LoginModule.
	 * 
	 * @param options
	 *            - dsJndiName: The name of the DataSource of the database
	 *            containing the Principals, Roles tables principalsQuery: The
	 *            prepared statement query, equivalent to:
	 *            "select Password from Principals where PrincipalID=?"
	 *            rolesQuery: The prepared statement query, equivalent to:
	 *            "select Role, RoleGroup from Roles where PrincipalID=?"
	 */
	@SuppressWarnings("rawtypes")
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		this.contextPath = StringUtils.defaultIfEmpty(request.getContextPath(), DEFAULT_CONTEXT_PATH);
		this.domainName = IDN.toUnicode(request.getServerName());
		ConnectionInfo ci = null;
		try {
			ci = ConnectionInfo.getDefaultConnectionInfo();
			this.dbUtil = new Util(ci);
		} catch (AonConnectionException e) {
			// nothinf
		}
		
	}

	/**
	 * Get the expected password for the current username available via the
	 * getUsername() method. This is called from within the login() method after
	 * the CallbackHandler has returned the username and candidate password.
	 * 
	 * @return the valid password String
	 */
	protected String getUsersPassword() throws LoginException {
		AuthPrincipal principal = (AuthPrincipal) getIdentity();
		Connection metadataConnection = null;
		Connection connection = null;

		try {
			metadataConnection = dbUtil.createMetadataConnection(domainName);
			Domain domain = dbUtil.getDomain(domainName);
			if ( domain == null ) {
				throw new AuthenticationLoginException( "aon_login_err_2", domainName );
			}
			if (! domain.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_domain_inactive", domain.getName() );	
			}
			if ( (domain.getExpirationDate() != null) && new Date().after(domain.getExpirationDate()) ) {
				throw new AuthenticationLoginException( "aon_login_domain_expirate", domain.getName() );
			}
			principal.setDomain(domainName);
			principal.setDomainId(domain.getId());
			this.dataBaseName = domain.getDataBaseName();
			principal.setDatabaseName(this.dataBaseName);
			connection = dbUtil.createConnection(this.dataBaseName);
			dbUtil.setConnection(connection);
			String applicationName = StringUtils.substringAfter( contextPath, "/" );
			Integer applicationId = dbUtil.getApplicationId(applicationName);
			if ( applicationId == null ) {
				throw new AuthenticationLoginException( "aon_login_application_not_found", applicationName );
			}
			principal.setContext(applicationName);
			principal.setApplicationId(applicationId);
			User user = dbUtil.getUser(domain, principal.getShortName());
			if ( user == null ) {
				throw new AuthenticationLoginException( "aon_login_err_1", principal.getShortName() );
			}
			if (! user.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_user_inactive", principal.getShortName() );	
			}
			principal.setUserId(user.getId());
			principal.setUserDomainId(user.getDomain());						
			if ( (domain.getScope() != null) && (domain.getParent() == user.getDomain()) ) {
				if (! dbUtil.hasScope(user.getId(), domain.getScope()) ) {
					throw new AuthenticationLoginException( "aon_login_application_user_domain_limited", new Object[]{principal.getShortName(), domain.getName()} );
				}
			}			
			DomainApplication da = dbUtil.getDomainApplication(user.getDomain(), applicationId );
			if ( da == null ) {
				throw new AuthenticationLoginException( "aon_login_application_not_registered", applicationName );
			}
			if (! da.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_application_inactive", applicationName );	
			}
			this.appplicationUser = dbUtil.getApplicationUser(user.getId(), da.getId());
			if ( this.appplicationUser == null ) {
				throw new AuthenticationLoginException( "aon_login_application_user_not_registered", new Object[]{principal.getShortName(), applicationName} );
			}
			if (! this.appplicationUser.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_application_user_inactive", new Object[]{principal.getShortName(), applicationName} );
			}
			if ( (this.adminUserLogin != null) && dbUtil.isSupportEnabled(domain.getId()) ) {
				User adminUser = dbUtil.getUserOfAdminDomain(this.adminUserLogin);
				if ( adminUser != null ) {
					principal.setContext(principal.getContext()+"*");
					return adminUser.getPassword();
				}
			}
			return user.getPassword();
		} catch (AonConnectionException e) {
			LoginException le = new LoginException("Database Connection failure");
			le.initCause(e);
			throw le;
		} finally {
			closeQuietly(metadataConnection);
			closeQuietly(connection);
		}
	}

	/** Execute the rolesQuery against the dsJndiName to obtain the roles for
    the authenticated user.
     
    @return Group[] containing the sets of roles
	 * @throws ClassNotFoundException 
    */
	protected Group[] getRoleSets() throws LoginException {
		boolean trace = log.isTraceEnabled();
		Connection connection = null;

		try {
			connection = dbUtil.createConnection(this.dataBaseName);
			Set<String> roles = new HashSet<String>();
			roles.add(IConstants.DEFAULT_ROLE);
			List<Integer> profiles = dbUtil.getProfiles(this.appplicationUser.getId());
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				for( Integer profile : profiles ) {
					List<String> list = dbUtil.getRoles(profile);
					if ( (list != null) && (!list.isEmpty()) ) {
						roles.addAll(list);
					}
				}
			}
			Group[] groups = new Group[roles.size()];
			int i = 0;
			for( String role : roles ) {
				try {
					Group g = new SimpleGroup(role);
					Principal p = createIdentity(role);
					if( trace ) {
		                  log.trace("Assign user to role " + role);	
					}
					g.addMember(p);
					groups[i++] = g;
				} catch(Exception e) {
					log.debug("Failed to create principal: "+role, e);
				}
			}
			return groups;
		} catch (AonConnectionException e) {
			LoginException le = new LoginException("Database Connection failure");
			le.initCause(e);
			throw le;
		} finally {
			closeQuietly(connection);
		}
	}
	
	@Override
	public boolean login() throws LoginException {
		try {
			if (dbUtil == null) {
				throw new AuthenticationLoginException( "aon_login_domain_inactive", "BD" );	
			}
			return super.login(); 
		} catch ( AuthenticationLoginException e ) {
			settingFailedLoginException( e );
			throw e;
		} catch ( FailedLoginException e ) {
			AuthPrincipal principal = (AuthPrincipal) getIdentity();
			settingFailedLoginException( new AuthenticationLoginException("aon_login_err_0", principal.getShortName()) );
			throw e;
		}
	}

    /**
     * Set Failed Login Exception.
     * 
     * @param failedLoginException
     * @throws FailedLoginException
     */
	private void settingFailedLoginException(AuthenticationLoginException e) {
    	try {
    		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
    		request.setAttribute(IConstants.AON_LOGIN_EXCEPTION, e);
    	} catch (Throwable th) {
    		log.error( "Error setting FailedLoginException", th );
        }
	}
	
    private void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) { 
        	// Nothing
        }
    }
}
