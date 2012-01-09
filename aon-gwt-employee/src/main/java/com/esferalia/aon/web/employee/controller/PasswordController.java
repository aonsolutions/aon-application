package com.esferalia.aon.web.employee.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.ui.util.AonUtil;

public class PasswordController {

	private final static Logger LOGGER = LoggerFactory.getLogger(PasswordController.class);
	
	private String password;
	
	private String newPassword;
	
	private String confirmPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public void onInit( ActionEvent event ) {
		setPassword(null);
		setNewPassword(null);
		setConfirmPassword(null);
	}
	
	public void acceptPassword( ActionEvent event ) {
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(ManagerController.CONTROLLER_NAME);
		EnterpriseUser user = manager.getLoggedUser();
		if (! StringUtils.equals(password, user.getPassword()) ) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_passwd_error");
			throw new AbortProcessingException( message );			
		}
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_new_passwd_error");
			throw new AbortProcessingException( message );
		}
		try {
            IManagerBean bean = BeanManager.getManagerBean(EnterpriseUser.class);
            user.setPassword(newPassword);
            bean.update(user);
			AonUtil.addInfoMessageFromBundle( "securityBundle", "aon_security_password_changed");
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error updating user password: " + user, e);
        }		
	}
	
}