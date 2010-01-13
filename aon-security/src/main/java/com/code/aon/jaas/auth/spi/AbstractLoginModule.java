package com.code.aon.jaas.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.IConstants;

/**
 * Esta clase implementa la funcionalidad requerida por la JAAS.
 * Deriva de esta clase y crea tu propio LoginModule sobreescribiendo 
 * los métodos login(), getRoleSets() y getIdentity().
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 */
@SuppressWarnings( "unchecked" )
public abstract class AbstractLoginModule implements LoginModule, IConstants {

    /** Obtiene un logger apropiado. */
	protected static final Log LOGGER = LogFactory.getLog( AbstractLoginModule.class.getName() );

    /** El Subject a tener en cuenta despues de una identificación positiva. */
	protected Subject subject;

    /**
     * Atributo de retrollamada con la información de la identidad del usuario y sus credenciales 
     * entre otras. 
     */
	protected CallbackHandler callbackHandler;

    /**
     * Mapa donde se almacenan el usuario y la clave en el caso de ser verdadero 
     * el flag useFirstPass.
     */
	protected Map sharedState;

    /** Opciones utilizadas en la política de seguridad. */
	protected Map options;

    /** Flag que indica si la credencial compartida debe ser usada. */
	protected boolean useFirstPass = false;

    /**
     * Flag que indica si la fase de login tuvo éxito. Las subclases que
     * sobreescriban el método login deberán asignar true si el login se realizó
     * con éxito.
     */
	protected boolean loginOk;

	/** An optional custom Principal class implementation */
	protected String principalClassName;

	/** the principal to use when a null username and password are seen */
	protected Principal unauthenticatedIdentity;

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     *      javax.security.auth.callback.CallbackHandler, java.util.Map,
     *      java.util.Map)
     */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
		LOGGER.debug( INITIALIZE_STATE );
		/*
		 * Check for password sharing options. Any non-null value for
		 * password_stacking sets useFirstPass as this module has no way to
		 * validate any shared password.
		 */
		String passwordStacking = (String) options.get("password-stacking"); //$NON-NLS-1$
		if (passwordStacking != null && passwordStacking.equalsIgnoreCase("useFirstPass")) { //$NON-NLS-1$
			useFirstPass = true;
		}
		// Check for a custom Principal implementation
		principalClassName = (String) options.get("principalClass");

		// Check for unauthenticatedIdentity option.
		String name = (String) options.get("unauthenticatedIdentity");
		if( name != null ) {
			try {
				unauthenticatedIdentity = createIdentity( name );
				LOGGER.debug( "Saw unauthenticatedIdentity=" + name );
	         } catch(Exception e) {
				LOGGER.warn( "Failed to create principal" );
			}
		}
	}

    /**
     * Looks for javax.security.auth.login.name and javax.security.auth.login.password 
     * values in the sharedState map if the useFirstPass option was true and returns
     * true if they exist. If they do not or are null this method returns false.
     * 
     * Note that subclasses that override the login method must set the loginOk
     * ivar to true if the login succeeds in order for the commit phase to
     * populate the Subject. This implementation sets loginOk to true if the 
     * login() method returns true, otherwise, it sets loginOk to false.
     */
	public boolean login() throws LoginException {
		LOGGER.debug( LOGIN_STATE );
		loginOk = false;
//	If useFirstPass is true, look for the shared password
		if (useFirstPass) {
			try {
				Object identity = sharedState.get( "javax.security.auth.login.name");
				Object credential = sharedState.get( "javax.security.auth.login.password" );
				if (identity != null && credential != null) {
					loginOk = true;
					return true;
				}
				//	Else, fall through and perform the login
			} catch (Exception e) { // $codepro.audit.disable caughtExceptions
				// Dump the exception and continue
				LOGGER.fatal( "login failed:" + e.getMessage() );
			}
		}
		return false;
	}

    /**
     * Method to commit the authentication process (phase 2). If the login 
     * method completed successfully as indicated by loginOk == true, this 
     * method adds the getIdentity() value to the subject getPrincipals() Set.
     * It also adds the members of each Group returned by getRoleSets()
     * to the subject getPrincipals() Set.
     * 
     * @see javax.security.auth.Subject;
     * @see java.security.acl.Group;
     * @return true always. 
     */
	public boolean commit() throws LoginException {
		if (!loginOk) {
			return false;
		}
		Set<Principal> principals = subject.getPrincipals();
		Principal identity = getIdentity();
		principals.add(identity);
		roles4Subject(principals);
		LOGGER.debug( "commit, loginOk=" + loginOk + " " + subject.getPrincipals() );
		return true;
	}

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.spi.LoginModule#abort()
     */
	public boolean abort() throws LoginException {
		LOGGER.debug( ABORT_STATE );
		return true;
	}

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.spi.LoginModule#logout()
     */
	public boolean logout() throws LoginException {
		LOGGER.debug( LOGOUT_STATE );
//	Remove the user identity
		Principal identity = getIdentity();
		Set principals = subject.getPrincipals();
		principals.remove(identity);
//	Remove any added Groups...
		return true;
	}

    /**
     * Informa sobre el uso que se va ha hacer de la credencial. true 
     * si la credencial va a ser compartida o false en caso contrario. 
     * 
     * @return boolean
     */
	protected boolean getUseFirstPass() {
		return useFirstPass;
	}

    /**
     * Devuelve la Identidad que no necesita de autentificación.
     * 
     * @return Principal
     */
    protected Principal getUnauthenticatedIdentity() {
        return unauthenticatedIdentity;
    }

    /**
     * Deberá implementarse en las subclases para que devuelvan el
     * <code>Principal</code> que corresponda a la identidad primaria.
     * 
     * @return Principal La identidad primaria.
     */
	protected abstract Principal getIdentity();

    /**
     * Overriden by subclasses to return the Groups that correspond to the
     * to the role sets assigned to the user. Subclasses should create at
     * least a Group named "Roles" that contains the roles assigned to the user.
     * A second common group is "CallerPrincipal" that provides the application
     * identity of the user rather than the security domain identity.
     * 
     * @return Group[] containing the sets of roles
     * @throws LoginException
     */
	protected abstract Group[] getRoleSets() throws LoginException;

	/**
	 * Utility method to create a Principal for the given username. This
	 * creates an instance of the principalClassName type if this option was
	 * specified using the class constructor matching: ctor(String). If
	 * principalClassName was not specified, a SimplePrincipal is created.
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	protected abstract Principal createIdentity(String username) throws Exception;

	/**
	 * 
	 * @param principals
	 * @throws LoginException
	 */
	protected abstract void roles4Subject(Set<Principal> principals) throws LoginException;

	/**
	 * Return the number of active users.
	 * 
	 * @param host
	 * @param context
	 * @return
	 */
    protected abstract Integer getActiveUsers(String host, String context) throws LoginException;

    /**
     * Returns <code>MBeanServer</code> current instance.
     * 
     * @return
     */
    protected abstract MBeanServer getMBeanServer();

}