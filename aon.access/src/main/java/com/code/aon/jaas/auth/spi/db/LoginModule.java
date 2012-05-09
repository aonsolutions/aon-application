package com.code.aon.jaas.auth.spi.db;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.AuthRolePrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;

public class LoginModule implements javax.security.auth.spi.LoginModule {

    private CallbackHandler callbackHandler;
    private Subject subject;
    private Map sharedState;
    private Map options;
    
    private boolean debug = false;
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    private String login;
    private char[] password;
    private AuthPrincipal userPrincipal;    

    private Util dbUtil;
	private String dataBaseName;
	private BasicInfo appplicationUser;
 

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    	this.subject = subject;
    	this.callbackHandler = callbackHandler;
    	this.sharedState = sharedState;
    	this.options = options;
    	debug = "true".equalsIgnoreCase((String)options.get("debug"));
   		initConnection();
    }
 
    /**
     * This method checks whether the name and the password are the same.
     *
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
    	if (callbackHandler == null) {
    	    throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
    	}
    	Callback[] callbacks = new Callback[2];
    	callbacks[0] = new NameCallback("user name: ");
    	callbacks[1] = new PasswordCallback("password: ", false);
     
    	try {
    	    callbackHandler.handle(callbacks);
    	    login = ((NameCallback)callbacks[0]).getName();
    	    userPrincipal = new AuthPrincipal( login );
    	    password = ((PasswordCallback)callbacks[1]).getPassword();
    	} catch (java.io.IOException ioe) {
    	    throw new LoginException(ioe.toString());
    	} catch (UnsupportedCallbackException uce) {
    	    throw new LoginException("Error: " + uce.getCallback().toString() +
    		" not available to garner authentication information " +
    		"from the user");
    	}
    	
		Connection mainConnection = null;
		Connection connection = null;
		try {
			mainConnection = dbUtil.createConnection(null);
			String domainName = userPrincipal.getDomain();
			Domain domain = dbUtil.getDomain(domainName);
			if ( domain == null ) {
				throw new AuthenticationLoginException( "aon_login_err_2", domainName );
			}
			if (! domain.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_domain_inactive", domain.getName() );	
			}
			userPrincipal.setDomainId(domain.getId());
			this.dataBaseName = domain.getDataBaseName();
			connection = dbUtil.createConnection(this.dataBaseName);
			dbUtil.setConnection(connection);
			String applicationName = StringUtils.substringAfter( userPrincipal.getContext(), "/" );
			Integer applicationId = dbUtil.getApplicationId(applicationName);
			if ( applicationId == null ) {
				throw new AuthenticationLoginException( "aon_login_application_not_found", applicationName );
			}
			userPrincipal.setApplicationId(applicationId);
			User user = dbUtil.getUser(domain.getId(), userPrincipal.getShortName());
			if ( user == null ) {
				throw new AuthenticationLoginException( "aon_login_err_1", userPrincipal.getShortName() );
			}
			if (! user.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_user_inactive", userPrincipal.getShortName() );	
			}
			userPrincipal.setUserId(user.getId());
			DomainApplication da = dbUtil.getDomainApplication(user.getDomain(), applicationId );
			if ( da == null ) {
				throw new AuthenticationLoginException( "aon_login_application_not_registered", applicationName );
			}
			if (! da.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_application_inactive", applicationName );	
			}
			this.appplicationUser = dbUtil.getApplicationUser(user.getId(), da.getId());
			if ( this.appplicationUser == null ) {
				throw new AuthenticationLoginException( "aon_login_application_user_not_registered", new Object[]{userPrincipal.getShortName(), applicationName} );
			}
			if (! this.appplicationUser.isActive() ) {
				throw new AuthenticationLoginException( "aon_login_application_user_inactive", new Object[]{userPrincipal.getShortName(), applicationName} );
			}
			String correctPassword = user.getPassword();
            byte[] hash = MessageDigest.getInstance("SHA-1").digest(new String(password).getBytes());
            String loginPassword = new String( Base64.encodeBase64(hash) );
            
			if (!StringUtils.equals(correctPassword, loginPassword)) {
				throw new AuthenticationLoginException( "aon_login_err_0", userPrincipal.getShortName() );
			}
			succeeded = true;
			return true;
		} catch (SQLException ex) {
			LoginException le = new LoginException("Query failed");
			le.initCause(ex);
			throw le;
		} catch (ClassNotFoundException e) {
			LoginException le = new LoginException("JDBC driver not found");
			le.initCause(e);
			throw le;
		} catch (NoSuchAlgorithmException e) {
			LoginException le = new LoginException("Password Encoding error");
			le.initCause(e);
			throw le;
		} finally {
			DbUtils.closeQuietly(mainConnection);
			DbUtils.closeQuietly(connection);
		}
    }

    @Override
    public boolean commit() throws LoginException {
    	if (succeeded == false) {
    	    return false;
    	} else {
    	    if (!subject.getPrincipals().contains(userPrincipal))
    		subject.getPrincipals().add(userPrincipal);
    	    subject.getPrincipals().addAll(getRoleSets());
    	    if (debug) {
    	    	System.out.println("\t\t[SampleLoginModule] " +
    	    			"added SamplePrincipal to Subject");
    	    }
    	    // in any case, clean out state
    	    login = null;
    	    for (int i = 0; i < password.length; i++) {
    	    	password[i] = ' ';	
    	    }
    	    password = null;
    	    commitSucceeded = true;
    	    return true;
    	}
    }
    
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			login = null;
			if (password != null) {
				for (int i = 0; i < password.length; i++)
					password[i] = ' ';
				password = null;
			}
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}
    
	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		login = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
		}
		userPrincipal = null;
		return true;
	}
	
	protected Collection<Principal> getRoleSets() throws LoginException {
		Connection connection = null;
		Collection<Principal> retRoles = new LinkedList<Principal>();
		try {
			connection = dbUtil.createConnection(this.dataBaseName);
			List<Integer> profiles = dbUtil.getProfiles(this.appplicationUser.getId());
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				Set<String> roles = new HashSet<String>();
				for( Integer profile : profiles ) {
					List<String> list = dbUtil.getRoles(profile);
					if ( (list != null) && (!list.isEmpty()) ) {
						roles.addAll(list);
					}
				}
				for( String role : roles ) {
					try {
						AuthRolePrincipal p = new AuthRolePrincipal(role);
						if( debug ) {
							System.out.println("Assign user to role " + role);
						}
						retRoles.add(p);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			return retRoles;
		} catch (SQLException ex) {
			LoginException le = new LoginException("Query failed");
			le.initCause(ex);
			throw le;
		} catch (ClassNotFoundException e) {
			LoginException le = new LoginException("JDBC driver not found");
			le.initCause(e);
			throw le;
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	
	private void initConnection() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("/usr/share/tomcat6/conf/deployed.properties"));
			this.dbUtil = new Util(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void settingFailedLoginException(AuthenticationLoginException e) {
    	try {
//    		ObjectName name = new ObjectName(this.sessionManagerObjectName);
//    		getMBeanServer().invoke( name, "fillLastLoginException", new Object[] { e }, new String[] { AuthenticationLoginException.class.getName() } );
    		
    	} catch (Throwable th) {
    		th.printStackTrace();
        }
	}	
}
