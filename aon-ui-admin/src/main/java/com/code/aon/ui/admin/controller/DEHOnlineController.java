package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.common.ICommonMessages.NEW_PASSWORD_ERROR;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.User;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class DEHOnlineController {
	
	private String user;
	
	private String password;
	
	private String confirmPassword;	

	public boolean isShowConfiguration() {
		User user = UserUtils.getInstance().getLoggedUser();
		Integer value = AppParamUtil.getValueAsInteger(AppParam.AON_EXTERNAL_APPLICATIONS, user.getDomain());
		return (value != null) && ((value & IAdminConstants.DEH_ONLINE_EXTERNAL_APP) != 0 );
	}	
	
	public void onInit(ActionEvent event) {
		this.user = AppParamUtil.getValue(AppParam.AON_DEH_ONLINE_USER);
	}

	public void acceptPassword(ActionEvent event) {
		if (! StringUtils.equals(password, confirmPassword) ) {
			String message = AonUtil.addErrorMessageFromBundle(NEW_PASSWORD_ERROR);
			throw new AbortProcessingException( message );
		}
		AppParamUtil.insertParameter(AppParam.AON_DEH_ONLINE_USER, user);
		AppParamUtil.insertParameter(AppParam.AON_DEH_ONLINE_PASSWORD, password);
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}	

	public String getStoredPassword() {
		return AppParamUtil.getValue(AppParam.AON_DEH_ONLINE_PASSWORD);
	}
	
}