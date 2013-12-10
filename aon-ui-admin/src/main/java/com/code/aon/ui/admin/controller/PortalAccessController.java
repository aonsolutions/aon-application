package com.code.aon.ui.admin.controller;

import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.util.UserIdCheckUtil;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PortalAccessController implements IAdminConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PortalAccessController.class);
	
	private final static String PAYROLL_PORTAL_OPTION = "gwt_enterprise_site";
	
	private final static String PAYROLL_PORTAL_PROFILE = "Portal Laboral";
	
	private Profile payrollPortalProfile;
	
	private User user;
	
	private UserIdCheckUtil idCheck;
	
	private String newPassword;
	
	private String confirmPassword;	
	
	public PortalAccessController() {
		this.idCheck = new UserIdCheckUtil();
		try {
			this.payrollPortalProfile = getPayrollPortalProfile();
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting profile 'Portal Laboral'", e);
		}
	}
	
	private Profile getPayrollPortalProfile() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Profile.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression("Profile.domain");
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_NAME), PAYROLL_PORTAL_PROFILE);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (Profile) list.get(0);
		}
		return null;
	}
	
	private Integer getEnterpriseId() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = controller.obtainCompany();
		return company.getId();
	}
	
	private Integer getApplicationId() {
		return AonUtil.getAuthPrincipal().getApplicationId();
	}

	private User getPortalUser() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_ENTERPRISE), getEnterpriseId());
		criteria.addEqualExpression("User.initAction", PAYROLL_PORTAL_OPTION);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (User) list.get(0);
		}
		return null;
	}
	
	public void onInit( ActionEvent event ) {
		try {		
			this.user = getPortalUser();
			if ( this.user == null ) {
				resetTo();
			}
			getIdCheck().setOldValue( getUser().getLogin() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onInit",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void accept( ActionEvent event ) {
		try {
			if (! StringUtils.equals(newPassword, confirmPassword)) {
				String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.NEW_PASSWORD_ERROR);
				throw new AbortProcessingException( message );
			}		
	        this.user.setPassword( AdminUtil.encodeSHA(newPassword) );
	        this.user.setPasswordExpiration( DateUtils.addDays(new Date(), 180) );
	        updateUser();
			DomainApplication domainApplication = getDomainApplication();
			if ( domainApplication != null ) {
				ApplicationUser appUser = ensureApplicationUser(user, domainApplication);	
				ensureProfiles(appUser);
			}			
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> accept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getIdCheck().setOldValue( getUser().getLogin() );
	}
	
	private void updateUser() throws ManagerBeanException {
		this.user.setInitAction(PAYROLL_PORTAL_OPTION);
		this.user.setEnterprise(getEnterpriseId());
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		bean.insertOrUpdate(this.user);
	}
	
	public void onRemove(ActionEvent event) {
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
		try {		
			duc.removeUserReferences(user.getId());
			duc.getManagerBean().remove(user);
			onInit(event);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onRemove",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}	
	}	
	
	private void resetTo() throws ManagerBeanException {
		this.user = (User) BeanManager.getManagerBean(User.class).createNewTo();
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
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserIdCheckUtil getIdCheck() {
		return idCheck;
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		this.idCheck.idCheck( (String) value );
	}			
	
	private DomainApplication getDomainApplication() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), getApplicationId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (DomainApplication) list.get(0);
		}
		return null;
	}	
	
	private ApplicationUser ensureApplicationUser(User user, DomainApplication domainApplication) throws ManagerBeanException {
		ApplicationUser appUser = null;
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_USER_ID), user.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_DOMAIN_APPLICATION_ID), domainApplication.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			appUser = (ApplicationUser) list.get(0);
		} else {
			appUser = new ApplicationUser();
			appUser.setUser(user);
			appUser.setDomainApplication(domainApplication);
			bean.insert(appUser);
		}
		return appUser;
	}

	private void ensureProfiles(ApplicationUser appUser) throws ManagerBeanException {
		boolean profileExists = false;
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID), appUser.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			ApplicationUserProfile aup = (ApplicationUserProfile) to;
			if ( aup.getProfile().equals(payrollPortalProfile) ) {
				profileExists = true;
				break;
			}
		}
		if (! profileExists) {
			ApplicationUserProfile aup = new ApplicationUserProfile();
			aup.setApplicationUser(appUser);
			aup.setProfile(payrollPortalProfile);
			bean.insert(aup);
		}
	}

	public void onResetPassword( ActionEvent event ) {
		try {
			DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
			duc.resetPassword( user );
			updateUser();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}		
	}	
	
}