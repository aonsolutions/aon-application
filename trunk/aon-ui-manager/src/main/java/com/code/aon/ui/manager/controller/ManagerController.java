package com.code.aon.ui.manager.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.manager.DBConnnection;
import com.code.aon.ui.manager.util.DBManager;
import com.code.aon.ui.util.AonUtil;

public class ManagerController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	private final static String HOME = "home";
	
	private final static String USER = "esferalia";

	private final static String PASSWORD = "113e2f4682d921b8a33eff77b489409d";

	private String _user;

	private String _password;
	
	private boolean administrator;
	
	private List<SelectItem> workGroupStatuses;
	
	private DBManager dbManager;
	
	private DBConnnection dbConnection;
	
	private SessionFactory sessionFactory;
	
	public ManagerController() {
		this.dbManager = new DBManager();
	}
	
	public DBManager getDBManager() {
		return dbManager;
	}

	public boolean isAdministrator() {
		return administrator;
	}
	
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
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
	
	public void onAccept(ActionEvent event) {
		String crypted = hash(_password);
		if (USER.equals(_user) && PASSWORD.equals(crypted)) {
			setAdministrator(true);
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
	
	public void createDB( DBConnnection dbConnection ) throws AonException {
		if (! getDBManager().exists(dbConnection) ) {
			getDBManager().createDB(dbConnection);
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
	
	public void changeDbConnection(DBConnnection dbc) {
		if (! ObjectUtils.equals(dbConnection, dbc) ) {
			this.dbConnection = dbc;	
			if ( this.sessionFactory != null ) {
				this.sessionFactory.close();
				this.sessionFactory = null;				
			}
		}
	}

	public SessionFactory getSessionFactory() {
		if ( sessionFactory == null )  {
			AnnotationConfiguration configuration = new AnnotationConfiguration();
			dbConnection.configure(configuration);
	   		configuration.buildMappings();
			sessionFactory = configuration.buildSessionFactory();
		}
		return sessionFactory;
	}
	
}
