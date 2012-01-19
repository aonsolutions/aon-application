package com.code.aon.ui.document.controller;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.NEW_PASSWORD_ERROR;
import static com.code.aon.ui.document.controller.IDocumentConstants.USER_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseUserController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserController.class);

	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;
	
	public void onInit( ActionEvent event ) {
		try {
			clearCriteria();
			Criteria criteria = getCriteria();
			EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
			criteria.addEqualExpression("EnterpriseUser.enterprise.id", ec.getEnterprise().getId());
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
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
	
	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}

	public void idCheck(FacesContext context, UIComponent component, Object value) {
		idCheck( value.toString() );
	}	
	
	private boolean isValidName( String name ) {
		return name.matches("\\p{Alpha}[\\w\\_\\-]*");
	}
	
	private String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);
	}
	
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, USER_DUPLICATED, name);
	}
	
	private String getToName() {
		EnterpriseUser user = (EnterpriseUser) getTo();
		return user.getName();
	}
	
	private boolean exists( String name ) {
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		return mc.getUserManager().userExists(name);
	}	
	
	private boolean isDuplicated( String name ) {
		boolean skipCheck = false;
		if (! isNew() ) {
			skipCheck = StringUtils.equals(name, getToName());
		}
		if (! skipCheck ) {
			return exists(name);
		}
		return false;
	}		
	
	private void idCheck( String id ) {
		if (! isValidName(id) ) {
			throw new ValidatorException(new FacesMessage(getInvalidMessage(id)));
		}
		if ( isDuplicated(id) ) {
			throw new ValidatorException(new FacesMessage(getDuplicatedMessage(id)));			
		}
	}		

	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR );
			throw new AbortProcessingException( message );
		}		
		EnterpriseUser user = (EnterpriseUser) getTo();
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		try {
			mc.getUserManager().changePassword(user.getLogin(), null, newPassword);
			user.setPassword(newPassword);
			getManagerBean().update(user);
		} catch (Throwable e) {
			LOGGER.error(">>>> onChangePassword",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public boolean isAdministrator() throws ManagerBeanException {
		EnterpriseUser user = (EnterpriseUser) getTo();
		if ( user != null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			return mc.getUserManager().isAlfrescoAdministrator(user.getLogin());			
		}
		return false;
	}
	
}