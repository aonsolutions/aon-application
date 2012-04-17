package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.UserType;
import com.code.aon.ui.admin.util.ManagerLogger;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.esferalia.aon.entity.IEntityAlias;

public class AdminMainController implements IAdminConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminMainController.class);
	
	public static final String PROPERTIES_PATH = "/com/code/aon/ui/admin/";
	
	private static final String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.config.properties";
	
	private static final File MANAGER_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-admin/config.properties" );

	private String _user;

	private String _password;
	
	private UserType userType;
	
	private Domain currentDomain;
	
	private List<SelectItem> workGroupStatuses;
	
	private Properties properties;
	
	private Properties config;
	
	private ManagerLogger logger;
	
	private boolean termsOfServiceAccepted;
	
	public AdminMainController() {
		this.properties = PropertiesUtil.getProperties(MANAGER_PROPERTIES, DEFAULT_PROPERTIES);
		this.logger = new ManagerLogger( this.properties.getProperty(NOTIFICATION_EMAIL) );
	}
	
	public void onInit( ActionEvent event ) {
		this.currentDomain = calculateCurrentDomain();
		this.userType = calculateUserType();
		init( event );
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public Properties getConfig() {
		return config;
	}
	
	public ManagerLogger getLogger() {
		return logger;
	}

	public UserType getUserType() {
		return userType;
	}
	
	private void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public Domain getCurrentDomain() {
		return currentDomain;
	}
	
	public String getCurrentDomainTypeLabel() {
		return currentDomain.getType().getName(AonUtil.getCurrentLocale());
	}	
	
	public void setCurrentDomain(Domain currentDomain) {
		this.currentDomain = currentDomain;
	}

	public boolean isAdministrator() {
		return this.userType == UserType.ESFERALIA;
	}

	public boolean isUserManagement() {
		if ( isAdministrator() ) {
			return true;
		}
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		return dc.getDomain().isUserManagement();
	}

	public boolean isDomainManagement() {
		return isAdministrator() || getCurrentDomain().isDomainManagement();
	}
	
	public String getHomeTemplate() {
		return this.userType.getTemplate();
	}
	
	public String getUser() {
		return _user;
	}

	public void setUser(String user) {
		this._user = user;
	}

	public String getPasswd() {
		return _password;
	}

	public void setPasswd(String passwd) {
		this._password = passwd;
	}
	
	public String loginAction() {
		return isAdministrator() ? DOMAIN_LIST : null;
	}
	
	public boolean isTermsOfServiceAccepted() {
		return termsOfServiceAccepted;
	}

	public void setTermsOfServiceAccepted(boolean termsOfServiceAccepted) {
		this.termsOfServiceAccepted = termsOfServiceAccepted;
	}	

	public void resetTermsOfServiceAccepted() {
		termsOfServiceAccepted = isAdministrator();
	}
	
	public void onAccept(ActionEvent event) {
		String crypted = hash(_password);
		String amUser = getProperties().getProperty(ADVANCED_MODE_USER); 
		String amPassword = getProperties().getProperty(ADVANCED_MODE_PASSWORD);
		if (amUser.equals(_user) && amPassword.equals(crypted)) {
			setUserType(UserType.ESFERALIA);
			init(event);
		} else {
			String message = AonUtil.getMessage("securityBundle", "aon_login_err_0", _user);
			AonUtil.addErrorMessage(message);
		}
		_user = null;
		_password = null;
	}

	/**
	* Encripta un String con el algoritmo MD5.
	* @return String
	* @throws Exception
	*/
	private String hash(String passwd){
		String md5_passwd = passwd;
		byte[] defaultBytes = md5_passwd.getBytes();
		try{
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<messageDigest.length;i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			md5_passwd=hexString+"";
		}catch(NoSuchAlgorithmException nsae){
			LOGGER.debug( nsae.getMessage(), nsae);
		}
		return md5_passwd;
	} 
	
	public List<SelectItem> getWorkGroupStatuses() {
		if(workGroupStatuses == null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			workGroupStatuses = new LinkedList<SelectItem>();
			for (WorkGroupStatus status : WorkGroupStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				workGroupStatuses.add(item);
			}
		}
		return workGroupStatuses;
	}	
	
	private Domain calculateCurrentDomain() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			return (Domain) bean.get( DomainManager.getCurrentDomain() );
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	private UserType calculateUserType() {
		UserType type = UserType.NORMAL;
		DomainType dt = currentDomain.getType();
		if ( (dt != null) && ((dt == DomainType.CONSULTANCY) || (dt == DomainType.ENTERPRISE_MANAGER)) ) {
			type = UserType.PARENT;
		}
		return type;
	}
	
	@SuppressWarnings("unchecked")
	private void initEsferaliaUser( ActionEvent event ) {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		try {
			controller.setInitExpressions(Collections.EMPTY_LIST);			
			controller.clearCriteria();
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		mailConfig.setSystemAccountEditable(true);
	}

	private void initNormalUser( ActionEvent event ) {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		try {
			controller.select(event, currentDomain.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}

	private void initParentUser( ActionEvent event ) {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		try {
			List<Expression> initExpressions = new LinkedList<Expression>();
			String cn = controller.getFieldName(IEntityAlias.DOMAIN_ID);
			Expression expr1 = ExpressionUtilities.getEqualExpression(cn, currentDomain.getId());
			String parent = "Domain<parent.id";
			Expression expr2 = ExpressionUtilities.getEqualExpression(parent, currentDomain.getId());
			initExpressions.add( ExpressionUtilities.getOrExpression(expr1, expr2) );
			controller.setInitExpressions(initExpressions);
			controller.clearCriteria(); 
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}
	
	private void init( ActionEvent event ) {
		this.config = PropertiesUtil.loadProperties(this.userType.getResource());
		switch ( this.userType ) {
			case ESFERALIA:
				initEsferaliaUser(event);
				break;
			case NORMAL:
				initNormalUser(event);
				break;
			case PARENT:
				initParentUser(event);
				break;
		}
	}	
	
	public int execute( String[] commandLine ) {
		int exitVal = -1;
        try {
            Runtime rt = Runtime.getRuntime();
            LOGGER.info( "Executing: {}", StringUtils.join(commandLine, " ") );
            Process pr = rt.exec( commandLine );

            Reader reader = new InputStreamReader(pr.getInputStream());
            BufferedReader in = new BufferedReader(reader);

            String line=null;
            while((line=in.readLine()) != null) {
            	LOGGER.debug( "Output: {}", line );
            }

            exitVal = pr.waitFor();
            LOGGER.debug( "Exited with error code {}", exitVal );
        } catch(Throwable e) {
        	LOGGER.error( "Error executing command " + commandLine[0], e );
        }		
        return exitVal;
	}
	
	public static void updateController( String name, Name parent ) {
		updateController(name, parent, true);
	}

	public static void updateController( String name, Name parent, boolean force ) {
		LdapBasicController controller = (LdapBasicController) AonUtil.getRegisteredBean(name);
		LdapDAO dao = controller.getLdapDAO();
		Name oldDN = dao.getBaseDN();
		controller.updateBaseDN(parent);
		Name currentDN = dao.getBaseDN();
		if ( force || (! ObjectUtils.equals(oldDN, currentDN)) ) {
			controller.onSearch(null);	
		}				
	}

	public void flushAuthenticationCache( Domain domain, User user ) {
		try {
			IConsoleAdmin console = Utils.getSecurityConsole();
			AuthPrincipal principal = new AuthPrincipal( user.getLogin() + "@" + domain.getName() );			
			console.flushAuthenticationCache(UserManager.LDAP_SECURITY_DOMAIN, principal);
		} catch (DeploymentException e) {
			LOGGER.error( "Error flushing authenticaction cache for " + user, e );
		}
	}
	
}