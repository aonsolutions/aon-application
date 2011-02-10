package com.code.aon.ui.manager.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.common.AonException;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.dao.IManagerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.manager.UserType;
import com.code.aon.ui.manager.util.DBManager;
import com.code.aon.ui.manager.util.ManagerLogger;
import com.code.aon.ui.manager.util.PropertiesUtil;
import com.code.aon.ui.util.AonUtil;

public class ManagerController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	public static final String PROPERTIES_PATH = "/com/code/aon/ui/manager/";
	
	private static final String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.config.properties";
	
	private static final File MANAGER_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-manager/config.properties" );
	
	private final static String HOME = "home";
	
	private final static String USER = "esferalia";

	private final static String PASSWORD = "113e2f4682d921b8a33eff77b489409d";

	private String _user;

	private String _password;
	
	private UserType userType;
	
	private Domain currentDomain;
	
	private String homeTemplate;
	
	private List<SelectItem> workGroupStatuses;
	
	private DBManager dbManager;
	
	private DBConnnection dbConnection;
	
	private SessionFactory sessionFactory;
	
	private Properties properties;
	
	private Properties config;
	
	private ManagerLogger logger;
	
	private boolean termsOfServiceAccepted;
	
	public ManagerController() {
		this.dbManager = new DBManager();
		this.properties = PropertiesUtil.getProperties(MANAGER_PROPERTIES, DEFAULT_PROPERTIES);
		this.currentDomain = calculateCurrentDomain();
		this.logger = new ManagerLogger( this.properties.getProperty(NOTIFICATION_EMAIL) );
		if ( this.logger.isConfigured() ) {
			this.userType = calculateUserType();	
		} else {
			this.userType = UserType.NORMAL;
		}
		init( this.userType );
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

	public DBManager getDBManager() {
		return dbManager;
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

	public boolean isAdministrator() {
		return this.userType == UserType.ESFERALIA;
	}

	public boolean isUserManagement() {
		return isAdministrator() || getCurrentDomain().getUserManagement();
	}

	public boolean isDomainManagement() {
		return isAdministrator() || getCurrentDomain().getDomainManagement();
	}
	
	public String getHomeTemplate() {
		return homeTemplate;
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
		return isAdministrator() ? HOME : null;
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
		if (USER.equals(_user) && PASSWORD.equals(crypted)) {
			setUserType(UserType.ESFERALIA);
			init(userType);
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
	
	public void createDB( DBConnnection dbConnection ) {
		if (! getDBManager().exists(dbConnection) ) {
			try {
				getDBManager().createDB(dbConnection);
			} catch (AonException e) {
				LOGGER.error(e.getMessage(), e);
				try {
					removeDB(dbConnection);
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
				throw new AbortProcessingException( e.getMessage(), e );
			}
		} else {
			AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DB_DUPLICATED, dbConnection.getDBName());
		}
	}
	
	public void removeDB( DBConnnection dbConnection ) throws SQLException {
		if ( getDBManager().exists(dbConnection) ) {
			getDBManager().dropDB(dbConnection);
		}
	}		
	
	public void insertDefaults( DBConnnection dbConnection, String application ) throws AonException {
		if ( getDBManager().exists(dbConnection) ) {
			getDBManager().insertDefaults(dbConnection, application);
		} else {
			AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DB_NOT_EXIST, dbConnection.getDBName());
		}
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
	
	public boolean changeDbConnection(DBConnnection dbc) {
		if (! ObjectUtils.equals(dbConnection, dbc) ) {
			this.dbConnection = dbc;	
			if ( this.sessionFactory != null ) {
				this.sessionFactory.close();
				this.sessionFactory = null;				
			}
			return true;
		}
		return false;
	}

	public SessionFactory getSessionFactory() {
		if ( sessionFactory == null )  {
			AnnotationConfiguration configuration = new AnnotationConfiguration();
			dbConnection.configure(configuration);
	   		configuration.buildMappings();
			sessionFactory = configuration.buildSessionFactory();
            DAOConstantsResolver resolver = new DAOConstantsResolver(configuration);
            resolver.createDAOConstants();			
		}
		return sessionFactory;
	}
	
	private Domain calculateCurrentDomain() {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(IManagerConstants.DOMAIN_CONTROLLER_NAME);
		DomainResolver domainResolver = (DomainResolver) AonUtil.getRegisteredBean(DomainResolver.CONTROLLER_NAME);
		String name = domainResolver.getDomain();		
		try {
			IManagerBean bean = controller.getManagerBean();
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IManagerAlias.DOMAIN_COMMON_NAME);
			criteria.addEqualExpression(alias, name);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (Domain) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	private UserType calculateUserType() {
		UserType type = UserType.PARENT;
		if ( currentDomain.getParentDomain() != null ) {
			type = UserType.NORMAL;
		}
		return type;
	}
	
	@SuppressWarnings("unchecked")
	private void initEsferaliaUser() {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(IManagerConstants.DOMAIN_CONTROLLER_NAME);
		try {
			controller.setInitExpressions(Collections.EMPTY_LIST);			
			controller.clearCriteria();
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}

	private void initNormalUser() {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(IManagerConstants.DOMAIN_CONTROLLER_NAME);
		try {
			Criteria criteria = controller.getCriteria();
			String alias = controller.getFieldName(IManagerAlias.DOMAIN_COMMON_NAME);
			criteria.addEqualExpression(alias, currentDomain.getCommonName());
			controller.initializeModel();
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);					
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}

	private void initParentUser() {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(IManagerConstants.DOMAIN_CONTROLLER_NAME);
		try {
			List<Expression> initExpressions = new LinkedList<Expression>();
			String cn = controller.getFieldName(IManagerAlias.DOMAIN_COMMON_NAME);
			Expression expr1 = ExpressionUtilities.getEqualExpression(cn, currentDomain.getCommonName());
			String parent = controller.getFieldName(IManagerAlias.DOMAIN_PARENT_DOMAIN);
			Expression expr2 = ExpressionUtilities.getEqualExpression(parent, currentDomain.getId());
			initExpressions.add( ExpressionUtilities.getOrExpression(expr1, expr2) );
			controller.setInitExpressions(initExpressions);
			controller.clearCriteria(); 
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}
	
	private void init( UserType type ) {
		this.homeTemplate = type.getTemplate();
		this.config = PropertiesUtil.loadProperties(type.getResource());
		switch ( type ) {
			case ESFERALIA:
				initEsferaliaUser();
				break;
			case NORMAL:
				initNormalUser();
				break;
			case PARENT:
				initParentUser();
				break;
		}
	}	
	
	public int execute( String[] commandLine ) {
		int exitVal = -1;
        try {
            Runtime rt = Runtime.getRuntime();
            LOGGER.info( "Executing: {}", StringUtils.join(commandLine, " ") );
            Process pr = rt.exec( commandLine );

            StringBuffer result = new StringBuffer();
            
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
	
}
