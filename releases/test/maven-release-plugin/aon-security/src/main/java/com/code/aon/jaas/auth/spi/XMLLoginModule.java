package com.code.aon.jaas.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthInfo;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IAuthInfo;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IRelation;

/**
 * Esta clase es la encargada de autentificar al usuario que está intentando 
 * acceder a la aplicación. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 *  
 */
public abstract class XMLLoginModule extends AbstractLoginModule {

	private final static Logger LOGGER = LoggerFactory.getLogger(XMLLoginModule.class);
	
	/** The login identity */
	private Principal identity;
	/** The proof of login identity */
	private char[] credential;
	/** the message digest algorithm used to hash passwords. If null then plain passwords will be used. */
	private String hashAlgorithm = null;
	/** 
	 * The name of the charset/encoding to use when converting the password String to a byte array. 
	 * Default is the platform's default encoding.
	 */
	private String hashCharset = null;
	/** the string encoding format to use. Defaults to base64. */
	private String hashEncoding = null;
	/** A flag indicating if the password comparison should ignore case */
	private boolean ignorePasswordCase;
	/** A flag indicating if the store password should be hashed using the hashAlgorithm  */
	private boolean hashStorePassword;
	/** A flag indicating if the user inputted password should be hashed using the hashAlgorithm */
	private boolean hashUserPassword = true;

	/** Tells security domain.. */
	protected String securityDomain = null;
	/** Tells the MBean ObjectName where applications and users are defined. */
	protected String objectName = null;
	/** Tells the MBean  SessionManager ObjectName . */
	protected String sessionManagerObjectName = null;

	/** Tells users data like passwords and roles, that this instance used to authenticate a user. */
	protected IAuthInfo authInfo;

	/** 
     * Override the superclass method to look for the following options after 
     * first invoking the super version.
     * @param options :
    	option: hashAlgorithm - the message digest algorithm used to hash passwords. 
    							If null then plain passwords will be used.
    	option: hashCharset - the name of the charset/encoding to use when converting 
    						the password String to a byte array. Default is the platform's default encoding.
    	option: hashEncoding - the string encoding format to use. Defaults to base64.
    	option: ignorePasswordCase: A flag indicating if the password comparison should ignore case.
    	option: digestCallback - The class name of the DigestCallback {@link org.jboss.crypto.digest.DigestCallback} 
    							implementation that includes pre/post digest content like salts for 
    							hashing the input password. Only used if hashAlgorithm has been specified.
    	option: hashStorePassword - A flag indicating if the store password returned from #getUsersPassword() 
    								should be hashed .
    	option: hashUserPassword - A flag indicating if the user entered password should be hashed.
    	option: storeDigestCallback - The class name of the DigestCallback {@link org.jboss.crypto.digest.DigestCallback} 
    								implementation that includes pre/post digest content like salts for 
    								hashing the store/expected password. Only used if hashStorePassword 
    								or hashUserPassword is true and hashAlgorithm has been specified.
     * @param subject, mapa de opciones del módule.
     * @param callbackHandler, <code>CallbackHandler</code>
     * @param sharedState, <code>Map</code>
     * @param options, <code>Map</code>
     * 
	 */
	@SuppressWarnings( "unchecked" )
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		// Check to see if password hashing has been enabled.
		// If an algorithm is set, check for a format and charset.
		hashAlgorithm = (String) options.get( IConstants.ALGORITHM );
		if( hashAlgorithm != null ) {
			hashEncoding = (String) options.get( IConstants.ENCODING );
			if( hashEncoding == null )
				hashEncoding = Util.BASE64_ENCODING;
			hashCharset = (String) options.get( IConstants.CHARSET );
			if ( LOGGER.isDebugEnabled() ) {
				LOGGER.debug("Password hashing activated: algorithm = " + hashAlgorithm
						+ ", encoding = " + hashEncoding
						+ ", charset = " + (hashCharset == null ? "{default}" : hashCharset)
						+ ", callbackHandler = " + callbackHandler
						+ ", callback = " + options.get("digestCallback")
						+ ", storeCallback = " + options.get("storeDigestCallback")
						);
			}
        }
		String flag = (String) options.get("ignorePasswordCase");
		ignorePasswordCase = Boolean.valueOf(flag).booleanValue();
		flag = (String) options.get("hashStorePassword");
		hashStorePassword = Boolean.valueOf(flag).booleanValue();
		flag = (String) options.get("hashUserPassword");
		if( flag != null )
			hashUserPassword = Boolean.valueOf(flag).booleanValue();
		// XMLLoginModule Domain establishing.
		securityDomain = (String) options.get( IConstants.SECURITY_DOMAIN );
		objectName = (String) options.get( IConstants.DEPLOYER_OBJECT_NAME );
		sessionManagerObjectName = (String) options.get( IConstants.SESSION_MANAGER_OBJECT_NAME );
	}

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
	@Override
	@SuppressWarnings("unchecked")
	public boolean login() throws LoginException {
		//	See if shared credentials exist
		if (super.login()) {
			// Setup our view of the user
			Object username = sharedState.get("javax.security.auth.login.name");
			if (username instanceof Principal) {
				identity = (Principal) username;
			} else {
				String name = username.toString();
				try {
					identity = createIdentity(name);
				} catch(Exception e) {
					LOGGER.warn( "Failed to create principal", e );
					throw new LoginException("Failed to create principal: "+ e.getMessage());
				}
			}
			Object password = sharedState.get("javax.security.auth.login.password");
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
		String password = info[1];
		if (username == null && password == null) {
			identity = unauthenticatedIdentity;
			LOGGER.debug( "Authenticating as unauthenticatedIdentity={}", identity );
		}
		if (identity == null) {
			try {
				identity = createIdentity(username);
				LOGGER.debug( "identity[{}]", identity );
			} catch(Exception e) {
				LOGGER.warn( "Failed to create principal", e );
				throw new LoginException("Failed to create principal: "+ e.getMessage());
			}
			AuthPrincipal authPrincipal = (AuthPrincipal) identity;
			load( authPrincipal.getDomain() );

//	Checks if the user is defined by the AuthInfo users list
			boolean hasUser = false;
			try {
				hasUser = this.authInfo.hasUser( authPrincipal.getDomain(), authPrincipal.getContext(), authPrincipal.getShortName() );
			} catch (AuthenticationLoginException e) {
				settingFailedLoginException( e.getMessage(), e.getArg() );
				throw new FailedLoginException( "Unreachable domain:" + username );
			} 
			if ( !hasUser ) {
				settingFailedLoginException( "aon_login_err_1", authPrincipal.getShortName() );
				throw new FailedLoginException( "Incorrect user:" + username + " for the domain" );
			}
// Hash the user entered password if password hashing is in use
			if( hashAlgorithm != null && hashUserPassword == true )
				password = createPasswordHash( authPrincipal.getShortName(), password, "digestCallback" );
			String expectedPassword = getUsersPassword();
// Allow the storeDigestCallback to hash the expected password
			if( hashAlgorithm != null && hashStorePassword == true )
				expectedPassword = createPasswordHash( authPrincipal.getShortName(), expectedPassword, "storeDigestCallback" );
//	Validate the password supplied by the subclass
			if ( !validatePassword( password, expectedPassword ) ) {
				settingFailedLoginException( "aon_login_err_0", authPrincipal.getShortName() );
				throw new FailedLoginException( "Password Incorrect/Password Required, username: " + username );
			}
//	Checks if validated user has another restrictions
			validateLoggedUsers();
		}

		if (getUseFirstPass()) {
			//	Add the username and password to the shared state map
			sharedState.put( "javax.security.auth.login.name", username );
			sharedState.put( "javax.security.auth.login.password", credential );
		}
		super.loginOk = true;
		LOGGER.debug("User '{}' authenticated, loginOk={}", identity, loginOk );
		return true;
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getIdentity()
	 */
	protected Principal getIdentity() {
		return identity;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
        AuthGroup rolesGroup = new AuthGroup(ROLES_GROUP_NAME);
        List<Principal> groups = new LinkedList<Principal>();
        groups.add(rolesGroup);
    	AuthPrincipal authPrincipal = (AuthPrincipal) getIdentity();
        String domainName = authPrincipal.getDomain();
        String context = authPrincipal.getContext();
        IRelation relation = 
        	this.authInfo.getUserRelation( domainName, context, authPrincipal.getShortName() );
        if (relation != null) {
            parseGroupMembers( rolesGroup, domainName, context, relation.relations(), true );
        }
        Group[] roleSets = new Group[groups.size()];
        groups.toArray(roleSets);
        return roleSets;
	}

	/**
	 * Returns Identity Credentials. 
	 * 
	 * @return
	 */
	protected Object getCredentials() {
		return credential;
	}

	/**
	 * Returns the user name.
	 * 
	 * @return
	 */
	protected String getUsername() {
		String username = null;
		if( getIdentity() != null )
			username = getIdentity().getName();
		return username;
	}

	/**
	 * Returns User and Password required for authentication. No validation is done in this method.
	 * 
	 * @return String[], [0] = username, [1] = password
	 * @exception LoginException, thrown if CallbackHandler is not set or fails.
	 */
	protected String[] getUsernameAndPassword() throws LoginException {
		String[] info = { null, null };
		//	prompt for a username and password
		if (callbackHandler == null) {
			throw new LoginException( "Error: no CallbackHandler available to collect authentication information" );
		}
		NameCallback nc = new NameCallback( "User name: ", "guest" );
		PasswordCallback pc = new PasswordCallback( "Password: ", false );
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
			throw new LoginException( "CallbackHandler does not support: " + uce.getCallback() );
		}
		info[0] = username;
		info[1] = password;
		return info;
	}

    /**
     * Si está habilitado <code>hashAlgorithm</code>, este método es llamado por 
     * <code>login()</code> antes de validar las claves.
     * <p>
     * Retorna null si falla el algoritmo,lo que causará que falle la validación.
     * 
   * <p>
   * Subclasses may override it to provide customized password hashing,
   * for example by adding user-specific information or salting.
   * <p>
   * The default version calculates the hash based on the following options:
   * <ul>
   * <li><em>hashAlgorithm</em>: The digest algorithm to use.
   * <li><em>hashEncoding</em>: The format used to store the hashes (base64 or hex)
   * <li><em>hashCharset</em>: The encoding used to convert the password to bytes
   * for hashing.
   * <li><em>digestCallback</em>: The class name of the
   * org.jboss.security.auth.spi.DigestCallback implementation that includes
   * pre/post digest content like salts.
   * </ul>
   * It will return null if the hash fails for any reason, which will in turn
   * cause <code>validatePassword()</code> to fail.
   * 
	 * @param username ignored in default version
	 * @param password the password string to be hashed
	 * @param digestOption - the login module option name of the DigestCallback
	 * @throws SecurityException - thrown if there is a failure to load the
	 *  digestOption DigestCallback
	 * @return String
	 */
	protected String createPasswordHash(String username, String password, String digestOption) {
//        DigestCallback callback = null;
//        String callbackClassName = (String) options.get(digestOption);
//        if( callbackClassName != null )
//        {
//           try
//           {
//              ClassLoader loader = Thread.currentThread().getContextClassLoader();
//              Class callbackClass = loader.loadClass(callbackClassName);
//              callback = (DigestCallback) callbackClass.newInstance();
//              if( log.isTraceEnabled() )
//                 log.trace("Created DigestCallback: "+callback);            
//           }
//           catch (Exception e)
//           {
//              if( log.isTraceEnabled() )
//                 log.trace("Failed to load DigestCallback", e);
//              SecurityException ex = new SecurityException("Failed to load DigestCallback");
//              ex.initCause(e);
//              throw ex;
//           }
//           Map tmp = new HashMap();
//           tmp.putAll(options);
//           tmp.put("javax.security.auth.login.name", username);
//           tmp.put("javax.security.auth.login.password", password);
//       
//           callback.init(tmp);
//        }
		String passwordHash = 
			Util.createPasswordHash( hashAlgorithm, hashEncoding, hashCharset, username, password );
		return passwordHash;
	}

	/**
	 * A hook that allows subclasses to change the validation of the input 
	 * password against the expected password. This version checks that
	 * neither inputPassword or expectedPassword are null that that
	 * inputPassword.equals(expectedPassword) is true;
	 * 
	 * @param inputPassword
	 * @param expectedPassword
	 * @return true if the inputPassword is valid, false otherwise.
	 */
	protected boolean validatePassword(String inputPassword, String expectedPassword) {
		if (inputPassword == null || expectedPassword == null) {
			return false;
		}
		boolean valid = false;
		if( ignorePasswordCase == true )
			valid = inputPassword.equalsIgnoreCase(expectedPassword);
		else
			valid = inputPassword.equals(expectedPassword);
		return valid;
	}

	/**
	 * Checks if current Identity is valid. 
	 * 
	 * @throws LoginException 
	 * @throws NullPointerException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws MalformedObjectNameException 
	 * @throws InstanceNotFoundException 
	 */
	protected void validateLoggedUsers() throws LoginException {
		AuthPrincipal principal = (AuthPrincipal) this.identity;
		String domain = principal.getDomain();
		String context = principal.getContext();
		IAccessPolicy access = this.authInfo.getAccessPolicy(domain);
		try {
			getMBeanServer().invoke( new ObjectName(this.sessionManagerObjectName), "authenticate",
					new Object[] { this.identity, getActiveUsers(domain, context), access },
					new String[] { Principal.class.getName(), Integer.class.getName(), IAccessPolicy.class.getName() } );
		} catch (InstanceNotFoundException e) {
			LOGGER.debug( domain + " " + context + " " + access, e );
			throw new LoginException( e.getMessage() );
		} catch (MalformedObjectNameException e) {
			LOGGER.debug( domain + " " + context + " " + access, e );
			throw new LoginException( e.getMessage() );
		} catch (MBeanException e) {
			LOGGER.debug( domain + " " + context + " " + access, e );
			throw new LoginException( e.getMessage() );
		} catch (ReflectionException e) {
			LOGGER.debug( domain + " " + context + " " + access, e );
			throw new LoginException( e.getMessage() );
		} catch (NullPointerException e) {
			LOGGER.debug( domain + " " + context + " " + access, e );
			throw new LoginException( e.getMessage() );
		}
	}

	/**
	 * Returns user password.
	 * 
	 * @return
	 * @throws LoginException
	 */
    protected String getUsersPassword() throws LoginException {
    	AuthPrincipal authPrincipal = (AuthPrincipal) this.identity;
        return this.authInfo.getUserPassword( authPrincipal.getDomain(), authPrincipal.getShortName() ); 
    }

	/**
     * Load Domain applications and users. 
	 * 
	 * @param domain
	 */
	@SuppressWarnings("unchecked")
	protected void load(String domain) {
    	try {
    		ObjectName name = new ObjectName(this.objectName);
    		Collection apps = 
    			(Collection)getMBeanServer().invoke( name, "getSDApplications",
    						new Object[] { this.securityDomain },
							new String[] { String.class.getName() } );
	        this.authInfo = new AuthInfo( apps );
    	} catch (Throwable th) {
    		LOGGER.error( "Error loading Host[" + domain + "]", th );
        }
	}

	/**
     * Traverses user relations and generates a comma separated list of roles the user has.
     * 
     * @param group
     * @param domainName
     * @param context
     * @param profiles
     * @param flag
     */
    private void parseGroupMembers(Group group, String domainName, String context, List<String> profiles, boolean flag) 
    		throws LoginException {
        Iterator<String> iter = profiles.iterator();
        while (iter.hasNext()) {
            String element = iter.next();
            /*
             * TODO Bug. A Profile and a Role with the same name, the Role will
             * never recognized. It means the Role will be consider as a
             * Profile. <profiles> <relation> <id>Consulta </id> <list> <id>Echo
             * </id> -->> There is a Role and a Profile called Coder. The source
             * code will treat this node as if it was a Profile instead of a
             * Role. <id>Coder </id> </list> </relation> <relation> <id>Coder
             * </id> <list> <id>Echo </id> </list> </relation> ...
             */
            IRelation profile = this.authInfo.getProfileRelation( domainName, context, element );
            if (profile == null || !flag) {
                AuthPrincipal p = new AuthPrincipal(element);
                group.addMember(p);
            } else {
                parseGroupMembers(group, domainName, context, profile.relations(), false);
            }
        }
    }

    /**
     * Set Failed Login Exception.
     * 
     * @param failedLoginException
     * @throws FailedLoginException
     */
	private void settingFailedLoginException(String message, Object obj) {
    	try {
    		ObjectName name = new ObjectName(this.sessionManagerObjectName);
    		AuthenticationLoginException e = new AuthenticationLoginException( message, obj); 
    		getMBeanServer().invoke( name, "fillLastLoginException", new Object[] { e }, new String[] { AuthenticationLoginException.class.getName() } );
    	} catch (Throwable th) {
    		LOGGER.error( "Error setting FailedLoginException", th );
        }
	}

}