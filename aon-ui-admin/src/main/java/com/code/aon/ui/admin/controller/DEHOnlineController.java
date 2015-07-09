package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.common.ICommonMessages.NEW_PASSWORD_ERROR;

import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.util.AonUtil;

public class DEHOnlineController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String user;
	
	private String password;
	
	private String confirmPassword;	

	public boolean isShowConfiguration() {
		int value = AppParamUtil.getValueAsInt(AppParam.AON_EXTERNAL_APPLICATIONS);
		return (value & ICommonConstants.DEH_ONLINE_EXTERNAL_APP) != 0;
	}	
	
	private void reset() {
		this.user = null;
		this.password = null;
		this.confirmPassword = null;		
	}
	
	public void onInit(ActionEvent event) {
		reset();
		this.user = getStoredUser();
	}

	public void onRemove(ActionEvent event) {
		AppParamUtil.removeParameter(AppParam.AON_DEH_ONLINE_USER);
		AppParamUtil.removeParameter(AppParam.AON_DEH_ONLINE_PASSWORD);
		reset();
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
	
	public String getStoredUser() {
		return AppParamUtil.getValue(AppParam.AON_DEH_ONLINE_USER);
	}

	public boolean isConfigured() {
		return (!StringUtils.isEmpty(getStoredUser())) && (!StringUtils.isEmpty(getStoredPassword()));
	}
	
}