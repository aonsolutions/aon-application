package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_EMAIL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_PORT;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_SSL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_MAIL_USERNAME;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_PORT;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_SSL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_VERIFICATION;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_PROTOCOL;
import static com.code.aon.webmail.dao.IWebMailAlias.SIGNATURE_SIGNATURE;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.LdapBasicController;
import com.code.aon.ui.webmail.controller.MailAccountController;
import com.code.aon.ui.webmail.controller.SignatureController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class DomainUserController extends LdapBasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;
	
	private boolean webmail;
	
	private String selectedTab;
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DomainUser> getUsers() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}	
	
	public DomainUser getDomainUser() {
		return (DomainUser) getTo();
	}	
	
	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getUsersDN(domain);
		getLdapDAO().setBaseDN(baseDN);
	}
	
	public Signature addDefaultSignature( DomainUser user ) throws ManagerBeanException {
		Signature signature = new Signature();
		signature.setName( user.getDomain() );
		String text = getManager().getProperties().getProperty(SIGNATURE_SIGNATURE);
		String content = MessageFormat.format( text, user.getFullName() );	
		signature.setSignature(content);
		SignatureController controller = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
		controller.updateBaseDN(user.getId());
		controller.getManagerBean().insert( signature );
		return signature;
	}

	public void addDefaultMailAccount( DomainUser user, Signature signature ) throws ManagerBeanException {
		MailAccount account = new MailAccount();
		account.setName(NameResolver.DEFAULT_MAIL_ACCOUNT_NAME);
		account.setPasswordString(user.getUid());
		account.setSignature(signature);
		Properties properties = getManager().getProperties();
		String shortDomain = user.getDomain();
		if ( StringUtils.countMatches(shortDomain, ".") > 1 ) {
			shortDomain = StringUtils.substringAfter(shortDomain, ".");	
		}
		String email = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_EMAIL), user.getUid(), user.getDomain() );
		account.setEmail(email);
		String mailUsername = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_MAIL_USERNAME), user.getUid(), user.getDomain() );
		account.setMailUsername(mailUsername);
		String host = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_HOST), shortDomain );
		account.setHost(host);		
		account.setProtocol( properties.getProperty(MAIL_ACCOUNT_PROTOCOL) );
		String incomingHost = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_INCOMING_HOST), shortDomain );
		account.setIncomingHost(incomingHost);
		int incomingPort = NumberUtils.toInt(properties.getProperty(MAIL_ACCOUNT_INCOMING_PORT));
		account.setIncomingPort(incomingPort);
		boolean incomingSsl = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_INCOMING_SSL));
		account.setIncomingSsl(incomingSsl);
		String outgoingHost = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_OUTGOING_HOST), shortDomain );
		account.setOutgoingHost(outgoingHost);
		int outgoingPort = NumberUtils.toInt(properties.getProperty(MAIL_ACCOUNT_OUTGOING_PORT));
		account.setOutgoingPort(outgoingPort);
		boolean outgoingSsl = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_OUTGOING_SSL));
		account.setOutgoingSsl(outgoingSsl);
		boolean outgoingVerification = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_OUTGOING_VERIFICATION));
		account.setOutgoingVerification(outgoingVerification);
		MailAccountController controller = (MailAccountController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT);
		controller.updateBaseDN(user.getId());
		controller.getManagerBean().insert( account );				
	}
	
	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}

	private String getSHAPassword( String value ) {
		String shaPassword = null;
		try {
			byte[] hash = MessageDigest.getInstance("SHA").digest(value.getBytes());
			shaPassword = "{SHA}" + Util.encodeBase64(hash);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}        		
		return shaPassword;
	}
	
	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR );
			throw new AbortProcessingException( message );
		}		
		DomainUser user = getDomainUser();
		BasicLdap ldap = new BasicLdap();
		try {
	        String passwordHash = getSHAPassword(newPassword);
			ldap.getLdapSession().replaceAttribute(user.getId(), USER_PASSWORD_ATTRIBUTE, passwordHash);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		} finally {
			ldap.closeSession();
		}
	}
	
	public void onResetPassword( ActionEvent event ) {
		resetPassword( getDomainUser() );
	}	
	
	public void resetPassword( DomainUser user ) {
		user.setPasswordExpirationTimestamp( DateUtils.addDays(new Date(), -1) );
		String newPassword = getSHAPassword(user.getUid());
		user.setPasswordString( newPassword );
	}		
	
	public boolean isShowChangePasswordWindow() {
		return showChangePasswordWindow;
	}

	public void setShowChangePasswordWindow(boolean showChangePasswordWindow) {
		this.showChangePasswordWindow = showChangePasswordWindow;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean hasWebmail( DomainUser user ) {
		String domain = user.getDomain();
		Name dn = NameResolver.getDomainApplicationUserDN(domain, AON_WEBMAIL, user.getUid());
		BasicLdap ldap = new BasicLdap();
		return ldap.exists(dn, IAonObjectClasses.DOMAIN_APPLICATION_USER);
	}
	
	public boolean isWebmail() {
		return webmail;
	}

	public void setWebmail(boolean webmail) {
		this.webmail = webmail;
	}	

	public void registerUserInApplication( DomainUser user, String application, String profile ) throws ManagerBeanException, LdapException {
		BasicLdap ldap = new BasicLdap();
		Name applicationDN = NameResolver.getApplicationDN(application);
		if (! ldap.exists(applicationDN, IAonObjectClasses.APPLICATION) ) {
			LOGGER.error( "Application doesn't exist: " + applicationDN );
			return;
		}
		Name profileDN = NameResolver.getApplicationProfileDN(application, profile);
		if (! ldap.exists(profileDN, IAonObjectClasses.PROFILE) ) {
			LOGGER.error( "Profile doesn't exist: " + profileDN );
			return;
		}
		DomainApplicationUser dau = new DomainApplicationUser();
		dau.setCommonName( user.getUid() );
		List<Name> profiles = new LinkedList<Name>();
		profiles.add( ldap.getLdapSession().getFullDN(profileDN)  );
		dau.setProfiles( profiles );
		Name domainApplicationDN = NameResolver.getDomainApplicationDN(user.getDomain(), application);
		DomainApplicationUserController dauc = (DomainApplicationUserController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_USER_CONTROLLER_NAME);
		dauc.updateBaseDN(domainApplicationDN);
		dauc.getManagerBean().insert( dau );
	}
	
	private void registerScope( String userUid, String scopeName ) throws ManagerBeanException {
		IController scopeController = FormUtil.getController(SCOPE_CONTROLLER_NAME);
		Criteria scopeCriteria = new Criteria();
		scopeCriteria.addEqualExpression(scopeController.getFieldName(IConfigAlias.SCOPE_DESCRIPTION), scopeName);
		List<ITransferObject> scopes = scopeController.getManagerBean().getList(scopeCriteria);
		if (! scopes.isEmpty() ) {
			Scope scope = (Scope) scopes.get(0);
			User user = ensureDBUser( userUid );
			ensureDBUserScope(user, scope);
		}
	}
	
	public void registerScopeInDBs( String userUid, String scope ) throws ManagerBeanException {
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		for (DBConnnection dbc : ddbc.getDBConnnections()) {
			registerScopeInDB(dbc, userUid, scope);
		}
	}	

	public void registerScopeInDB( DBConnnection dbc, String userUid, String scope ) throws ManagerBeanException {
		getManager().changeDbConnection(dbc);
		if ( getManager().getDBManager().existsTable(dbc, "scope") ) {
			try {
				registerScope(userUid, scope);	
			} catch ( Throwable th ) {
				LOGGER.error( "Error registering " + scope + " for user " + userUid + " in " + dbc, th );
			}					
		}
	}	

	private User initDBUser( String uid ) throws ManagerBeanException {
		User user = new User();
		user.setLogin(uid);
		user.setActive(true);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("uid", uid);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (! list.isEmpty() ) {
			DomainUser domainUser = (DomainUser) list.get(0);
			user.setName( domainUser.getFullName() );
		}
		if ( StringUtils.isEmpty(user.getName()) ) {
			user.setName(uid);
		}
		return user;
	}
	
	public User ensureDBUser( String uid ) throws ManagerBeanException {
		User user = null;
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IConfigAlias.USER_LOGIN), uid);
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			user = initDBUser(uid);
			bean.insert(user);
		} else {
			user = (User) list.get(0);
			user.setActive(true);
			bean.update(user);
		}
		return user;
	}	

	public void ensureDBUserScope( User user, Scope scope ) throws ManagerBeanException {	
		IController userScopeController = FormUtil.getController(USER_SCOPE_CONTROLLER_NAME);
		IManagerBean bean = userScopeController.getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IConfigAlias.USER_SCOPE_SCOPE_ID), scope.getId());
		criteria.addEqualExpression(bean.getFieldName(IConfigAlias.USER_SCOPE_USER_ID), user.getId());
		if ( bean.getCount(criteria) == 0 ) {
			UserScope userScope = new UserScope();
			userScope.setScope(scope);
			userScope.setUser(user);
			bean.insert(userScope);
		}
	}	

	public void createMailAccount( DomainUser user ) throws ManagerBeanException {
		ManagerController manager = getManager();
		String command = null; 
		for( int i = 1; (command = manager.getProperties().getProperty(MAIL_ACCOUNT_CREATE_SCRIPT+"."+i)) != null ;i++) {
			manager.execute( new String[] {command, user.getName(), user.getDomain()} );
		}
	}	

	public void removeMailAccount( DomainUser user ) throws ManagerBeanException {
		ManagerController manager = getManager();
		String command = null; 
		for( int i = 1; (command = manager.getProperties().getProperty(MAIL_ACCOUNT_DELETE_SCRIPT+"."+i)) != null ;i++) {
			manager.execute( new String[] {command, user.getName(), user.getDomain()} );
		}
	}		

	public void deactiveDBUser( DomainUser user ) throws ManagerBeanException {
		ManagerController manager = getManager();
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		for (DBConnnection dbc : ddbc.getDBConnnections()) {
			manager.changeDbConnection(dbc);
			if ( manager.getDBManager().existsTable(dbc, "user") ) {
				try {
					deactiveDBUser(user.getUid());	
				} catch ( Throwable th ) {
					LOGGER.error( "Error deactivating user " + user.getUid() + " in " + dbc, th );
				}
			}					
		}
	}
	
	public void deactiveUsers() throws ManagerBeanException {
		for ( DomainUser user : getUsers() ) {
			if (! isAdmin(user) ) {
				user.setActive(false);
				getManagerBean().update(user);
			}
		}
	}	
	
	private void deactiveDBUser( String uid ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IConfigAlias.USER_LOGIN), uid);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			User dbUser = (User) list.get(0);
			dbUser.setActive(false);
			bean.update(dbUser);
		}
	}
	
	public void createUser( DBConnnection dbc, String uid, String name, String surname ) throws ManagerBeanException, LdapException {
		DomainUser user = new DomainUser();
		user.setUid(uid);
		user.setName(name);
		user.setSurname(surname);
		resetPassword(user);
		getManagerBean().insert(user);
		registerUserInApplication(user, AON_DESKTOP, ADMINISTRADOR_PROFILE);
		registerUserInApplication(user, AON_MANAGER, ADMINISTRADOR_PROFILE);
		registerUserInApplication(user, AON_WEBMAIL, USUARIO_PROFILE);
		registerScopeInDB( dbc, user.getUid(), GENERAL_SCOPE);
		createMailAccount(user);
		Signature signature = addDefaultSignature(user);
		addDefaultMailAccount(user, signature);		
	}
	
	private boolean isAdmin( DomainUser user ) {
		return StringUtils.equals(ADMIN_USER, user.getUid());
	}
	
	public boolean isUserRemoveable() {
		if ( getManager().isAdministrator() ) {
			return true;
		}
		return getManager().isUserManagement() && (!isAdmin(getDomainUser()));
	}

	public boolean isUserActivable() {
		if ( getManager().isAdministrator() ) {
			return true;
		}
		return !isAdmin(getDomainUser());
	}
	
}
