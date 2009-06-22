/*
 * Created on 04-oct-2006
 *
 */
package com.code.aon.bridge.plugin;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.jmx.mbean.Messages;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.MaximumLoginException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Clase responsable de las modificaciones a realizar sobre la información del usuario, 
 * en la política de acceso a la aplicación, de la sesión en curso.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 04-oct-2006
 * @since 1.0
 *
 */

public class UserManager implements Serializable {

	private static final long serialVersionUID = 5967483672644450772L;

	public static final String USER_DESCRIPTION = "Usuario Generado"; 
	public static final String SECURITY_CONTEXT_NAME = "/aon-security";
	public static final String LDAP_SECURITY_DOMAIN = "aon-ldap"; 
	/** UserManager Logger instance. */
	private static final Logger LOGGER = Logger.getLogger( UserManager.class.getName() );

	transient ResourceBundle bundle;
    transient IConsoleAdmin console;
	/** Application domain user. */
    IUser user;
    /** User profiles in the application domain. */
    IRelation relation;
	/** Tell if the user comes from available user list. */
	private boolean isNew;
	/** Tell if the user comes from available user list. */
	private boolean availableUser;
	/** Current password. */
	private String password;
	/** Allow password update. */
	private boolean changePassword;
	/** New password. */
	private String newPassword;
	/** Confirm password. */
	private String confirmPassword;

	/**
	 * Constructor 
	 */
	public UserManager() {
		try {
			this.user = new User();
			setDescription( UserManager.USER_DESCRIPTION ); 
			setNew( true );
			setAvailableUser( false );
			setChangePassword( false );
			FacesContext ctx = FacesContext.getCurrentInstance();
			if ( ctx != null )
				bundle = ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			console = Utils.getSecurityConsole();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * @return Returns the user.
	 */
	public IUser getUser() {
		return user;
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(IUser user) {
	    this.user = user;
	}

	/**
	 * @return the relation
	 */
	public IRelation getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(IRelation relation) {
		this.relation = relation;
	}

	/**
	 * @return
	 */
	public String getId() {
		return this.user.getId();
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		( (User) this.user ).setId( id );
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		( (User) this.user ).setName( name );
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		( (User) this.user ).setDescription( description );
	}

	/**
	 * @return the isNew
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * @param isNew the isNew to set
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * @return the availableUser
	 */
	public boolean isAvailableUser() {
		return availableUser;
	}

	/**
	 * @param availableUser the availableUser to set
	 */
	public void setAvailableUser(boolean availableUser) {
		this.availableUser = availableUser;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {	
		this.password = password;
	}

	/**
	 * @return the changePassword
	 */
	public boolean isChangePassword() {
		return changePassword;
	}

	/**
	 * @param changePassword the changePassword to set
	 */
	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	/**
	 * @return Returns the newPassword.
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword The newPassword to set.
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return Returns the confirmPassword.
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @param confirmPassword The confirmPassword to set.
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * Check if user password and typed password are equals.
	 *  
	 * @param app
	 * @return
	 */
	public boolean isCorrectPassword(IApplication app) {
		return this.user.getPasswd().equals( encryptPassword( app ) );    	
	}

	/**
	 * Check if new and confirm passwords are equals.
	 * 
	 * @return
	 */
	public boolean areEqualPasswords() {
		return newPassword.equals( confirmPassword );    	
	}

	/**
     * Selected user profiles.
	 * 
	 * @return
	 */
    public String[] getRelations() { 
		String[] list = new String[0];
    	if ( this.relation != null ) {
    		List<String> relations = this.relation.relations();
    		list = new String[ relations.size() ];
	        Iterator<String> iter = relations.iterator();
	        int i = 0;
	        while (iter.hasNext()){
	            String role = (String) iter.next();
	            list[i++] = role;
	        }
    	}
        return list;
    }

    public void setRelations(String[] relations) {
        ( (Relation) this.relation ).setRelations( Arrays.asList(relations) );
    }

    /**
	 * @return Returns the principal user.
	 */
	public IUser getUserFromPrincipal() {
		User _user = new User();
		_user.setDescription( USER_DESCRIPTION ); 
		try {
			Principal principal = 
				(Principal) FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
			if ( principal != null && console != null ) {
				AuthPrincipal aPrincipal = 
					( principal instanceof AuthPrincipal )? (AuthPrincipal) principal: new AuthPrincipal( principal.getName() );
				Object[] params = {aPrincipal};
				String[] sig = {Principal.class.getName()};
				String oname = console.getAonSecurityName();
				_user = (User)console.invoke( oname, IOperation.GET_USER, params, sig );
			}
		} catch (DeploymentException e) {
			LOGGER.severe(e.getMessage());
		}
		return _user;
	}

	/**
	 * Returns a list of domain users.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	public Collection<IUser> getUsers() throws DeploymentException {
		return getDomain().standaloneUsers().values();
	}

	/**
	 * Returns a list of domain users.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	@SuppressWarnings("unchecked")
	public List getUserApplications() throws DeploymentException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String domainId = IDomain.DEFAULT_DOMAIN_NAME;
		Principal principal = (Principal) ec.getUserPrincipal();
		if ( principal != null && principal instanceof AuthPrincipal ) {
			AuthPrincipal authPrincipal = ( (AuthPrincipal) principal );
			domainId = authPrincipal.getDomain();
		} else if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal( principal.getName() );
			domainId = authPrincipal.getDomain();
		}

		Object[] params = { domainId, this.user.getId() };
		String[] sig = { String.class.getName(), String.class.getName() };
		String oname = console.getAonSecurityName();
		return (List) console.invoke( oname, IOperation.USER_APPLICATIONS_LIST, params, sig );
	}

	/**
	 * Returns the list of application domains this context belongs to.
	 * 
	 * @param context
	 * @return
	 * @throws DeploymentException
	 */
	public List<String> getApplicationDomains(String context) throws DeploymentException {
		List<String> l = new ArrayList<String>(); 
		Object[] params = { context };
		String[] sig = { String.class.getName() };
		String oname = console.getAonSecurityName();
		IApplication app = 
			(IApplication) console.invoke( oname, IOperation.GET_APPLICATION4CTX, params, sig );
		if ( app != null ) {
			Iterator<IDomain> it = app.domains().iterator();
			while (it.hasNext()) {
				IDomain domain = it.next();
				l.add( domain.getId() );
			}
		}
		return l;
	}

	/**
	 * Available profiles list defined, 
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	public List<SelectItem> getProfiles() {
		try {
			return getAvailableProfiles( getApplication().getId(), getDomain() );
		} catch (DeploymentException e) {
			LOGGER.severe(e.getMessage());
		}
		return new ArrayList<SelectItem>();
	}
    /**
     * Available profiles list defined.
     * 
     * @return
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableProfiles(String appId, IDomain domain) {
        List<SelectItem> list = new ArrayList<SelectItem>();
    	Collection<IRelation> c = domain.getDomainApplication( appId ).profiles();
        Iterator<IRelation> iter = c.iterator();
        while (iter.hasNext()) {
            IRelation r = iter.next();
            SelectItem item = new SelectItem( r.getId(), r.getId() );
            list.add(item);
        }
        return list;
    }

	/**
	 * Return security application context.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	public String getSecurityApplicationCtx() {
		return SECURITY_CONTEXT_NAME;
	}

	/**
	 * Find <code>IUser</code> and its relations and assigns them, new user will be created 
	 * if no one is found.
	 * 
	 * @param username
	 */
	public void findUser(String username) {
		try {
			IApplication app = getApplication();
			IDomain domain = getDomain();
			this.user = domain.getStandaloneUser( username );
			this.relation = domain.getDomainApplication( app.getId() ).getUser( username );
			if ( this.user == null ) {
				setNew( true ); 
				this.user = new User();
				setId( username );
				setDescription( UserManager.USER_DESCRIPTION ); 
			} else {
				setNew( false ); 
			}
			if ( this.relation == null )
				this.relation = new Relation( this.user.getId() );
		} catch (DeploymentException e) {
			LOGGER.severe(e.getMessage());
			setNew( true ); 
			this.user = new User();
			setDescription( UserManager.USER_DESCRIPTION );
			this.relation = new Relation( this.user.getId() );
		}
		setAvailableUser( this.relation.getId() == null );
		setChangePassword( false );
		setPassword( null );
		setNewPassword( null );
		setConfirmPassword( null );
	}

	/**
	 * Accept user, in the application domain given by the <code>Principal</code>, 
	 * if this one is not found looks for it in the given request. 
	 * 
	 * @return
	 * @throws MaximumLoginException 
	 */
	public String accept(String oldUserId) throws MaximumLoginException {
		try {
			IApplication app = getApplication();
			IDomain domain = getDomain();
			int maxUsers = domain.getDomainApplication( app.getId() ).users().size();
			int maxDefinedUsers = domain.getAccessPolicy().getMaxDefinedUsers();
			boolean isAcceptEnabled = 
				maxDefinedUsers == -1 || maxDefinedUsers > maxUsers || !isNew();
			if ( isAcceptEnabled ) {
				return accept( app, domain, oldUserId );
			} else {
				throw new MaximumLoginException( "aon_login_err_4", Integer.toString( maxUsers ) );
			}
		} catch (DeploymentException e) {
			LOGGER.severe( e.getMessage() );
		}
		return null;
	}

	/**
	 * Accept user, in the application domain. 
	 * 
	 * @param app
	 * @param domain
	 * @return
	 * @throws DeploymentException
	 */
	public String accept(IApplication app, IDomain domain, String oldUserId) 
			throws DeploymentException {
		if ( isChangePassword() ) {
			if ( bundle == null ) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				bundle = ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			}
			if ( ( isNew() && !isAvailableUser() ) || isCorrectPassword( app ) ) { 
				if ( areEqualPasswords() ) {
					setPassword( getNewPassword() );
				} else {
					Messages.addErrorMessage( bundle.getString("aon_security_new_passwd_error") );
					return null;
				}
			} else {
				Messages.addErrorMessage( bundle.getString("aon_security_passwd_error") );
				return null;
			}
			this.user.changePasswd( encryptPassword( app ) );
		}
		saveUser( app, domain.getId(), oldUserId );
		setChangePassword( false );
		return updateRelation( app, domain.getId() );
	}

	/**
	 * Accept user, in the application domain. 
	 * 
	 * @param app
	 * @param domain
	 * @return
	 * @throws DeploymentException
	 */
	public void savePassword() {
		try {
			if ( areEqualPasswords() ) {
				setPassword( getNewPassword() );
			} else {
				Messages.addErrorMessage( bundle.getString("aon_security_new_passwd_error") );
				return;
			}
			IApplication app = getApplication();
			this.user.changePasswd( encryptPassword( app ) );
			IDomain domain = getDomain();
			saveUser( app, domain.getId(), null );
		} catch (DeploymentException e) {
			LOGGER.severe( e.getMessage() );
		}	
	}

	/**
	 * Remove user from application domain.
	 */
	public void remove() {
		try {
			IApplication app = getApplication();
			IDomain domain = getDomain();
			remove( app, domain.getId() );
		} catch (DeploymentException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	/**
	 * Remove user from application domain.
	 * 
	 * @param app
	 * @param domainId
	 */
	public void remove(IApplication app, String domainId) {
		Object[] params = { app.getId(), domainId, this.getUser() };
		String[] sig = {String.class.getName(), String.class.getName(), IUser.class.getName()};
		try {
			if (console != null) {
				String oname = console.getAonSecurityName();
				console.invoke( oname, IOperation.REMOVE_USER, params, sig );
			}
		} catch (Exception e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	/**
	 * Add or update user, in the application domain.
	 * 
	 * @param app
	 * @param domainId
	 * @param oldUserId
	 * @throws DeploymentException 
	 */
	private void saveUser(IApplication app, String domainId, String oldUserId) throws DeploymentException {
		if (console != null) {
			Object[] params = { app.getId(), domainId, user, oldUserId };
			String[] sig = {String.class.getName(), String.class.getName(), IUser.class.getName(), String.class.getName()};
			String oname = console.getAonSecurityName();
			if ( isNew() && !isAvailableUser() )
				console.invoke( oname, IOperation.ADD_USER, params, sig );
			else
				console.invoke( oname, IOperation.UPDATE_USER, params, sig );
		}
		setNew( false );
		setAvailableUser( false );
	}

	/**
	 * Add or update user realtion, in the application domain. This method only takes place if 
	 * it is a new <code>User</code> with relation or a <code>User</code> relation update. 
	 * 
	 * @param app
	 * @param domainId
	 * @return
	 * @throws DeploymentException 
	 */
	private String updateRelation(IApplication app, String domainId) throws DeploymentException {
		boolean hasRelations = this.relation.relations().size() > 0;
		if ( (isNew() && hasRelations) || !isNew() ) {
			( (Relation) this.relation ).setId( this.user.getId() );
			Object[] params = { app.getId(), domainId, relation };
			String[] sig = {String.class.getName(), String.class.getName(), IRelation.class.getName()};
			String operation = (hasRelations)? IOperation.UPDATE_RELATION: IOperation.REMOVE_RELATION;
			if (console != null) {
				String oname = console.getAonSecurityName();
				console.invoke( oname, operation, params, sig );
			}
			return (relation.relations().size() > 0)? operation: IOperation.REMOVE_USER;
		}
		return null;
	}

	/**
	 * Encrypt user password.
	 * 
	 * @param app
	 * @return
	 */
	private String encryptPassword(IApplication app) {
		if ( this.password != null ) {
			String passwordHash = this.password;
			if ( app.getHashAlgorithm() != null ) {
				String userId = user.getId();
				String hashAlgorithm = app.getHashAlgorithm();
				String hashEncoding = app.getHashEncoding();
				passwordHash = 
					Util.createPasswordHash( hashAlgorithm, hashEncoding, "", userId, password );
			}
			return passwordHash;
		}
		return this.password;
	}

	/**
	 * Return an <code>IApplication</code> instance given by authenticated <code>Principal</code>, 
	 * if no <code>Principal</code> was authenticated then application is look for requesting
	 * context.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	private IApplication getApplication() throws DeploymentException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String appContext = ec.getRequestContextPath();
		Principal principal = (Principal) ec.getUserPrincipal();
		if ( principal != null && principal instanceof AuthPrincipal ) {
			appContext = ( (AuthPrincipal) principal ).getContext(); 
		} else if ( principal != null ) {
			appContext = new AuthPrincipal( principal.getName() ).getContext(); 
		}
		Object[] params = { appContext };
		String[] sig = { String.class.getName() };
		String oname = console.getAonSecurityName();
		return (IApplication) console.invoke( oname, IOperation.GET_APPLICATION4CTX, params, sig );
	}

	/**
	 * Return an <code>IApplication</code> instance given by authenticated <code>Principal</code>, 
	 * if no <code>Principal</code> was authenticated then application is look for requesting
	 * context and default domain '<code>IDomain.DEFAULT_DOMAIN_NAME</code>'.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	private IDomain getDomain() throws DeploymentException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String appContext = ec.getRequestContextPath(), domainId = IDomain.DEFAULT_DOMAIN_NAME;
		Principal principal = (Principal) ec.getUserPrincipal();
		if ( principal != null && principal instanceof AuthPrincipal ) {
			AuthPrincipal authPrincipal = ( (AuthPrincipal) principal );
			appContext = authPrincipal.getContext(); 
			domainId = authPrincipal.getDomain();
		} else if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal( principal.getName() );
			appContext = authPrincipal.getContext(); 
			domainId = authPrincipal.getDomain(); 
		}
		Object[] params = { appContext, domainId };
		String[] sig = { String.class.getName(), String.class.getName() };
		String oname = console.getAonSecurityName();
		return (IDomain) console.invoke( oname, IOperation.GET_DOMAIN, params, sig );
	}
}
