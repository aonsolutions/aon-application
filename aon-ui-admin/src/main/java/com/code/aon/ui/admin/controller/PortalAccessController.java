package com.code.aon.ui.admin.controller;

import java.io.Serializable;
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

import com.code.aon.AonVersion;
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
import com.code.aon.ui.admin.PortalInfo;
import com.code.aon.ui.admin.util.UserIdCheckUtil;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PortalAccessController implements IAdminConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PortalAccessController.class);
	
	private final static String PAYROLL_PORTAL_OPTION = "gwt_enterprise_site";
	
	private final static String PAYROLL_PORTAL_PROFILE = "Portal Laboral";

	private final static String DOCUMENTAL_PORTAL_PROFILE = "Portal Documental";

	private final static String FINANCE_PORTAL_PROFILE = "Portal Gestion";
	
	private Profile payrollPortalProfile;
	
	private Profile documentalPortalProfile;
	
	private Profile financePortalProfile;
	
	private User user;
	
	private UserIdCheckUtil idCheck;
	
	private String newPassword;
	
	private String confirmPassword;	
	
	private PortalInfo info;
	
	private boolean showChangePasswordWindow;
	
	public PortalAccessController() {
		this.idCheck = new UserIdCheckUtil();
		try {
			this.payrollPortalProfile = getPortalProfile(PAYROLL_PORTAL_PROFILE);
			this.documentalPortalProfile = getPortalProfile(DOCUMENTAL_PORTAL_PROFILE);
			this.financePortalProfile = getPortalProfile(FINANCE_PORTAL_PROFILE);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting profile 'Portal Laboral'", e);
		}
	}
	
	private Profile getPortalProfile(String profileName) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Profile.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression("Profile.domain");
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_NAME), profileName);
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
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (User) list.get(0);
		}
		return null;
	}
	
	public void onInit( ActionEvent event ) {
		try {		
			this.info = new PortalInfo();
			this.user = getPortalUser();
			if ( this.user == null ) {
				resetTo();
			} else {
				updateScopes();
				if ( StringUtils.isNotEmpty(this.user.getInitAction()) ) {
					this.info.setPayrollPortal(true);
				}
			}
			this.info.init();
			getIdCheck().setOldValue( getUser().getLogin() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onInit",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.NEW_PASSWORD_ERROR);
			throw new AbortProcessingException( message );
		}		
		try {
	        this.user.setPassword( AdminUtil.encodeSHA(newPassword) );
	        this.user.setPasswordExpiration( DateUtils.addDays(new Date(), 180) );
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			bean.insertOrUpdate(this.user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}
	}
	
	public void accept( ActionEvent event ) {
		try {			
			if ( info.isActive() ) {
		        updateUser();	
			} else {
				if (this.user.getId() != null) {
					disableUser();
				}
			}
			info.update();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> accept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void updateUser() throws ManagerBeanException {
		boolean isNevv = ( this.user.getId() == null );
		if ( this.info.isPayrollPortal() ) {
			this.user.setInitAction(PAYROLL_PORTAL_OPTION);	
		} else { 
			this.user.setInitAction(null);
		}
		this.user.setEnterprise(getEnterpriseId());
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
		if ( isNevv ) {
			duc.resetPassword( user );
		}
		user.setActive(true);
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		bean.insertOrUpdate(this.user);
		if ( isNevv ) {
			duc.registerScope(user, GENERAL_SCOPE);
			updateScopes();
		} else {
			saveScopes();
		}
		DomainApplication domainApplication = getDomainApplication();
		if ( domainApplication != null ) {
			ApplicationUser appUser = ensureApplicationUser(user, domainApplication);
			ensureProfiles(appUser);
		}			
		getIdCheck().setOldValue( getUser().getLogin() );
	}
	
	private void disableUser() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		user.setActive(false);
		bean.update(user);
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
		boolean payrollProfileExists = false;
		boolean documentalProfileExists = false;
		boolean financeProfileExists = false;
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID), appUser.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for(ITransferObject to : list) {
			ApplicationUserProfile aup = (ApplicationUserProfile) to;
			if (aup.getProfile().equals(payrollPortalProfile)) {
				payrollProfileExists = true;
				if (!(this.info.isPayrollPortal() || this.info.isPayrollInfo())) {
					bean.remove(aup);
				}
			}
			if (aup.getProfile().equals(documentalPortalProfile)) {
				documentalProfileExists = true;
				if (!this.info.isDocumentalManagement()) {
					bean.remove(aup);
				}
			}
			if (aup.getProfile().equals(financePortalProfile)) {
				financeProfileExists = true;
				if (!this.info.isFinanceManagement()) {
					bean.remove(aup);
				}
			}
		}
		if (!payrollProfileExists && (this.info.isPayrollPortal() || this.info.isPayrollInfo())) {
			ApplicationUserProfile aup = new ApplicationUserProfile();
			aup.setApplicationUser(appUser);
			aup.setProfile(payrollPortalProfile);
			bean.insert(aup);
		}
		if (!documentalProfileExists && this.info.isDocumentalManagement()) {
			ApplicationUserProfile aup = new ApplicationUserProfile();
			aup.setApplicationUser(appUser);
			aup.setProfile(documentalPortalProfile);
			bean.insert(aup);
		}
		if (!financeProfileExists && this.info.isFinanceManagement()) {
			ApplicationUserProfile aup = new ApplicationUserProfile();
			aup.setApplicationUser(appUser);
			aup.setProfile(financePortalProfile);
			bean.insert(aup);
		}
	}
	
	public void onPayrollPortalChanged( ActionEvent event ) {
		if ( this.info.isPayrollPortal() && this.info.isShowInfo() ) {
			this.info.setFiscalInfo(false);
			this.info.setDocumentalInfo(false);
			this.info.setPayrollInfo(false);
			this.info.setAccountingInfo(false);
		}
	}

	private void saveScopes() {
		UserScopeController usc = (UserScopeController) AonUtil.getRegisteredBean(IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME);
		usc.accept(null);
	}	
	
	private void updateScopes() {
		UserScopeController usc = (UserScopeController) AonUtil.getRegisteredBean(IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME);
		usc.init(user);
	}	

	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}

	public boolean isShowChangePasswordWindow() {
		return showChangePasswordWindow;
	}

	public void setShowChangePasswordWindow(boolean showChangePasswordWindow) {
		this.showChangePasswordWindow = showChangePasswordWindow;
	}

	public PortalInfo getInfo() {
		return info;
	}
	
}