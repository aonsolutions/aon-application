package com.code.aon.ui.cms.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.util.AonUtil;

public class AdminController implements ICMSConstants {
	
	private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

	private String user_ = "esferalia";

	private String passwd_ = "76a2173be6393254e72ffa4d6df13a";

	private String user = "";

	private String passwd = "";

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String loginAction() {
		CmsController cms = (CmsController)AonUtil.getRegisteredBean(CMS);
		return cms.isAdministrator() ? HOME : null;
	}

	public void onAccept(ActionEvent event) {
		String crypted = hash(passwd);
		if (user_.equals(user) && passwd_.equals(crypted)){
			DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DOMAIN_UTILS);
			domainUtilities.assignAdminProfile();
			CmsController cms = (CmsController)AonUtil.getRegisteredBean(CMS);
			cms.assignAdminProfile();
		} else {
			String message = AonUtil.getMessage("securityBundle", "aon_login_err_0", user);
			AonUtil.addErrorMessage(message);
		}
		user = "";
		passwd = "";
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
			LOGGER.log(Level.FINE, nsae.getMessage(), nsae);
		}
		return md5_passwd;
	} 
	
}
