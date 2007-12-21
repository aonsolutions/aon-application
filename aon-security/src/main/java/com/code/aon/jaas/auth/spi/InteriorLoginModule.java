/**
 * 
 */
package com.code.aon.jaas.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import oracle.jdbc.driver.OracleTypes;

import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/02/2007
 *
 */
@SuppressWarnings("unchecked")
public class InteriorLoginModule extends AbstractLoginModule {

    /** Default identity to use in case of user and password are null */
	protected Principal unauthenticatedIdentity;

	/** Identity */
    private Principal identity;

    /** The credential bound to defined Identity */
    private char[] credential;

    /** Class name of Oracle JDBC driver */
    private String driver = "oracle.jdbc.driver.OracleDriver";

    /** Initial url fragment */
    protected String url;

    /** Initial user fragment */
    protected String user;

    /** Initial password fragment */
    protected String password;

    /** Initial application name fragment */
    protected String applicationName;

    /** Connection to database */
    private Connection conn = null;

    /**
     *  JDBC style of calling a package function with 3 arguments,
     *  P_USUARIO IN VARCHAR2, P_PASSWORD IN VARCHAR2, P_APLICACION IN VARCHAR2.
     */
    private String query = "{? = call PAQ_ACCESO_EXTERNO.TIENE_ACCESO(?,?,?)}";

    /**
     * Inicializa el LoginModule.
     * 
     * @param subject, mapa de opciones del módule.
     * @param callbackHandler, <code>CallbackHandler</code>
     * @param sharedState, <code>Map</code>
     * @param options, <code>Map</code>
     * 
     * @see javax.security.auth.spi.LoginModule#initialize(Subject, CallbackHandler, Map, Map)
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        // Check for unauthenticatedIdentity option.
        String name = (String) options.get( IConstants.UNAUTHENTICATED_IDENTITY );
        if (name != null) {
            unauthenticatedIdentity = new AuthPrincipal(name);
            LOGGER.debug("Saw unauthenticatedIdentity=" + name);
        }
        this.url = (String) options.get("url");
        this.user = (String) options.get("user");
        this.password = (String) options.get("password");
        this.applicationName = (String) options.get("applicationName");
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#login()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean login() throws LoginException {
        //	See if shared credentials exist
        if (super.login()) {
            // Setup our view of the user
            Object username = sharedState.get("javax.security.auth.login.name"); //$NON-NLS-1$
            if (username instanceof Principal) {
                identity = (Principal) username;
            } else {
                String name = username.toString();
                identity = new AuthPrincipal(name);
                
            }
            Object password = sharedState.get("javax.security.auth.login.password"); //$NON-NLS-1$
            if (password instanceof char[]) {
                credential = (char[]) password;
            } else if (password != null) {
                String tmp = password.toString();
                credential = tmp.toCharArray();
            }
            return true;
        }

        super.loginOk = false;
        String[] info = getUsernameAndPassword();
        String username = info[0];
        String passwd = info[1];
        if (username == null && passwd == null) {
            identity = unauthenticatedIdentity;
            LOGGER.debug("Authenticating as unauthenticatedIdentity=" + identity); //$NON-NLS-1$
        }

        if (identity == null) {
        	identity = new AuthPrincipal(username);
            // load the Oracle driver and establish a connection
            try {
                Class.forName( driver );
                conn = DriverManager.getConnection( url, user, this.password );
                int number = execute( username, passwd, applicationName );
                super.loginOk = (number > 0);
            } catch (ClassNotFoundException ex) {
            	ex.printStackTrace();
                throw new FailedLoginException("Failed to find driver class: " + driver);
            } catch (SQLException ex) {
            	ex.printStackTrace();
            	LOGGER.debug("SQLException '" + identity + " " + url  +":"+  user +","+ this.password );//$NON-NLS-1$ //$NON-NLS-2$
                throw new FailedLoginException("Failed to establish a connection to: " + url);
            }
        }

        if (getUseFirstPass()) {
            //	Add the username and password to the shared state map
            sharedState.put("javax.security.auth.login.name", username); //$NON-NLS-1$
            sharedState.put("javax.security.auth.login.password", credential); //$NON-NLS-1$
        }
        LOGGER.debug("User '" + identity + "' authenticated, loginOk=" + loginOk);//$NON-NLS-1$ //$NON-NLS-2$
        return true;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getIdentity()
	 */
	@Override
	protected Principal getIdentity() {
		return identity;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getRoleSets()
	 */
	@Override
	protected Group[] getRoleSets() throws LoginException {
        AuthGroup rolesGroup = new AuthGroup(ROLES_GROUP_NAME);
        List<Principal> groups = new LinkedList<Principal>();
        groups.add(rolesGroup);
        AuthPrincipal p = new AuthPrincipal("Manager");
        rolesGroup.addMember(p);
        Group[] roleSets = new Group[groups.size()];
        groups.toArray(roleSets);
        return roleSets;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#roles4Subject(java.util.Set)
	 */
	@Override
	protected void roles4Subject(Set<Principal> principals) throws LoginException {
		Group[] roleSets = getRoleSets();
		for (int g = 0; g < roleSets.length; g++) {
			Group group = roleSets[g];
            // Copy the group members to the principals
			Enumeration members = group.members();
			while (members.hasMoreElements()) {
				Principal role = (Principal) members.nextElement();
				principals.add( new AuthGroup( role.getName() ) );
			}
		}
	}

    /**
     * This method gets called for retrieving user and password values.  
     * 
     * @return String[], [0] = username, [1] = password
     * @exception LoginException, thrown if CallbackHandler is not set or fails.
     */
    protected String[] getUsernameAndPassword() throws LoginException {
        String[] info = { null, null };
        //	prompt for a username and password
        if (callbackHandler == null) {
            throw new LoginException(
                    "Error: no CallbackHandler available to collect authentication information"); //$NON-NLS-1$
        }
        NameCallback nc = new NameCallback("User name: ", "guest"); //$NON-NLS-1$ //$NON-NLS-2$
        PasswordCallback pc = new PasswordCallback("Password: ", false); //$NON-NLS-1$
        Callback[] callbacks = { nc, pc };
        String username = null;
        String password = null;
        try {
            callbackHandler.handle(callbacks);
            username = nc.getName();
            char[] tmpPassword = pc.getPassword();
            if (tmpPassword != null) {
                credential = new char[tmpPassword.length];
                System.arraycopy(tmpPassword, 0, credential, 0, tmpPassword.length);
                pc.clearPassword();
                password = new String(credential);
            }
        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("CallbackHandler does not support: " //$NON-NLS-1$
                    + uce.getCallback());
        }
        info[0] = username;
        info[1] = password;
        return info;
    }

    /**
     *  Execute the stored procedure
     *
     * @param user
     * @param password
     * @param application
     * @return
     * @throws SQLException
     */
    protected int execute(String user, String password, String application) 
    			throws SQLException {
        CallableStatement stmt = conn.prepareCall(query);
        // register the type of the out param - an Oracle specific type
        stmt.registerOutParameter(1, OracleTypes.NUMBER);
        // set the in parameters
        stmt.setString( 2, user );
        stmt.setString( 3, password );
        stmt.setString( 4, application );
        // execute and retrieve the int value
        stmt.execute();
        return stmt.getInt(1);
    }

	@Override
	protected Principal createIdentity(String username) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Integer getActiveUsers(String host, String context) throws LoginException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getMBeanServer()
	 */
	@Override
	protected MBeanServer getMBeanServer() {
		// TODO Auto-generated method stub
		return null;
	}

}
