package com.code.aon.ui.config.controller;

import static com.code.aon.bridge.controller.ISecurityBridgeConstants.BUNDLE_NAME;
import static com.code.aon.bridge.controller.ISecurityBridgeConstants.NEW_PASSWORD_ERROR;
import static com.code.aon.bridge.controller.ISecurityBridgeConstants.PASSWORD_ERROR;
import static com.code.aon.common.util.BeanServerUtil.AON_SECURITY_DOMAIN;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.BeanServerUtil;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public abstract class BasicChangePasswordController {
	
	private AuthPrincipal principal;
	
	private User to;
	
	private boolean showPasswordChangedWindow;
	
	private String password;
	
	private String newPassword;
	
	private String confirmPassword;
	
	public BasicChangePasswordController() {
		this.principal = AonUtil.getAuthPrincipal();
		this.to = UserUtils.getInstance().getLoggedUser();
	}
	
	public User getTo() {
		return to;
	}
	
	public void onInit(ActionEvent event) throws DeploymentException {
		setShowPasswordChangedWindow(false);
		setPassword(null);
		setNewPassword(null);
		setConfirmPassword(null);
	}	
	
	protected abstract void updatePassword( String newPassword );

	protected abstract boolean isCorrectPassword();
	
	public void acceptPassword(ActionEvent event) {
		if (! isCorrectPassword() ) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, PASSWORD_ERROR);
			throw new AbortProcessingException( message );			
		}
		if (! StringUtils.equals(newPassword, confirmPassword) ) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR);
			throw new AbortProcessingException( message );
		}
		updatePassword( newPassword );			
		BeanServerUtil.flushAuthenticationCache(AON_SECURITY_DOMAIN);
		setShowPasswordChangedWindow(true);
	}

	public boolean isShowPasswordChangedWindow() {
		return showPasswordChangedWindow;
	}

	public void setShowPasswordChangedWindow(boolean showPasswordChangedWindow) {
		this.showPasswordChangedWindow = showPasswordChangedWindow;
	}

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

	protected AuthPrincipal getPrincipal() {
		return principal;
	}
	
}
