package com.code.aon.ui.cms.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;

public class AdminController implements ICMSConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
	
	private final static String USER = "esferalia";

	private final static String PASSWORD = "76a2173be6393254e72ffa4d6df13a";
	
	private final static String PASSWORD_FM = "c289aadb6a5972df641c617ba481e766";

	private String _user;

	private String _password;

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
		CmsController cms = (CmsController)AonUtil.getRegisteredBean(CMS);
		return cms.isAdministrator() ? HOME : null;
	}
	
	private void setAdministrator(boolean fileManager) {
		DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DOMAIN_UTILS);
		domainUtilities.assignAdminProfile();
		CmsController cms = (CmsController)AonUtil.getRegisteredBean(CMS);
		cms.assignAdminProfile( fileManager );		
	}

	public void onAccept(ActionEvent event) {
		String crypted = hash(_password);
		if (USER.equals(_user) && PASSWORD.equals(crypted)) {
			setAdministrator(false);
		} else if (USER.equals(_user) && PASSWORD_FM.equals(crypted)) {
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
	
}
