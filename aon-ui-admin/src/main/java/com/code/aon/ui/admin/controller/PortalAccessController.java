package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

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
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.util.UserIdCheckUtil;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PortalAccessController implements IAdminConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PortalAccessController.class);
	
	private final static String PAYROLL_PORTAL_OPTION = "gwt_enterprise_site";
	
	private final static String PAYROLL_PORTAL_PROFILE = "Portal Laboral";
	
	private Profile payrollPortalProfile;
	
	private User user;
	
	private UserIdCheckUtil idCheck;
	
	private String newPassword;
	
	private String confirmPassword;	
	
	private boolean active;
	
	private int portalValue;
	
	private boolean showFiscalInfo;
	
	private boolean showPayrollInfo;
	
	private boolean showDocumentalInfo;
	
	private boolean showPayrollPortal;
	
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
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (User) list.get(0);
		}
		return null;
	}
	
	public void onInit( ActionEvent event ) {
		try {		
			setActive(false);
			Integer value = AppParamUtil.getValueAsInteger(AppParam.AON_PORTAL);
			this.portalValue = (value != null) ? value : 0;
			this.user = getPortalUser();
			if ( this.user == null ) {
				resetTo();
			} else {
				updateScopes();
				if ( StringUtils.isNotEmpty(this.user.getInitAction()) ) {
					setPayrollPortal(true);
				}
			}
			setActive(this.portalValue != 0);
			calculateAvalilableOptions();
			getIdCheck().setOldValue( getUser().getLogin() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onInit",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void calculateAvalilableOptions() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer parentDomainId = ds.getParentDomainId();
		Integer domainId = DomainManager.getCurrentDomain();
		Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
		try {
			this.showFiscalInfo = AuditManager.hasModule(parentDomainId, appId, Module.FISCAL);
			this.showPayrollInfo = AuditManager.hasModule(parentDomainId, appId, Module.PAYROLL);
			this.showDocumentalInfo = AuditManager.hasModule(parentDomainId, appId, Module.DOCUMENT);
			this.showPayrollPortal = AuditManager.hasModule(parentDomainId, appId, Module.PAYROLL_PORTAL) ||
					AuditManager.hasModule(domainId, appId, Module.PAYROLL_PORTAL);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}							
	}
	
	public void onSaveUser(ActionEvent event) {	
		try {
			if (! StringUtils.equals(newPassword, confirmPassword)) {
				String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.NEW_PASSWORD_ERROR);
				throw new AbortProcessingException( message );
			}		
	        this.user.setPassword( AdminUtil.encodeSHA(newPassword) );
	        this.user.setPasswordExpiration( DateUtils.addDays(new Date(), 180) );					
			updateUser();
			AppParamUtil.insertParameter(AppParam.AON_PORTAL, portalValue);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> accept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
		getIdCheck().setOldValue( getUser().getLogin() );
	}

	public void accept( ActionEvent event ) {
		try {
			if ( isActive() ) {
				AppParamUtil.insertParameter(AppParam.AON_PORTAL, portalValue);
				if (this.user.getId() != null) {
			        updateUser();	
				}
			} else {
				AppParamUtil.removeParameter(AppParam.AON_PORTAL);
				if (this.user.getId() != null) {
					onRemoveUser(event);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> accept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void updateUser() throws ManagerBeanException {
		boolean isNevv = ( this.user.getId() == null );
		if ( isPayrollPortal() ) {
			this.user.setInitAction(PAYROLL_PORTAL_OPTION);	
		} else { 
			this.user.setInitAction(null);
		}
		this.user.setEnterprise(getEnterpriseId());
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		bean.insertOrUpdate(this.user);
		if ( isNevv ) {
			DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
			duc.registerScope(user, GENERAL_SCOPE);
			updateScopes();
		}
		DomainApplication domainApplication = getDomainApplication();
		if ( domainApplication != null ) {
			ApplicationUser appUser = ensureApplicationUser(user, domainApplication);
			ensureProfiles(appUser);
		}			
	}
	
	public void onRemoveUser(ActionEvent event) {
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
		try {		
			duc.removeUserReferences(user.getId());
			duc.getManagerBean().remove(user);
			resetTo();
			getIdCheck().setOldValue( getUser().getLogin() );
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
		List<ITransferObject> list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			ApplicationUserProfile aup = (ApplicationUserProfile) to;
			if ( aup.getProfile().equals(payrollPortalProfile) ) {
				profileExists = true;
				if (! (isPayrollPortal() || isPayrollInfo()) ) {
					bean.remove(aup);
					return;
				}
				break;
			}
		}
		if (! profileExists && (isPayrollPortal() || isPayrollInfo())) {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private boolean getPortalValue(int bitwise) {
		return (portalValue & bitwise) != 0;
	}

	private void setPortalValue(boolean value, int bitwise) {
		if ( value ) {
			this.portalValue |= bitwise;	
		} else {
			this.portalValue &= (~bitwise);
		}
	}
	
	public boolean isFiscalInfo() {
		return getPortalValue(IAdminConstants.FISCAL_INFO_PORTAL);
	}
		
	public void setFiscalInfo(boolean fiscalInfo) {
		setPortalValue(fiscalInfo, IAdminConstants.FISCAL_INFO_PORTAL);
	}

	public boolean isShowFiscalInfo() {
		return this.showFiscalInfo;
	}
	
	public boolean isPayrollInfo() {
		return getPortalValue(IAdminConstants.PAYROLL_INFO_PORTAL);
	}

	public void setPayrollInfo(boolean payrollInfo) {
		setPortalValue(payrollInfo, IAdminConstants.PAYROLL_INFO_PORTAL);
	}
	
	public boolean isShowPayrollInfo() {
		return this.showPayrollInfo;
	}	

	public boolean isDocumentalInfo() {
		return getPortalValue(IAdminConstants.DOCUMENTAL_INFO_PORTAL);
	}

	public void setDocumentalInfo(boolean documentalInfo) {
		setPortalValue(documentalInfo, IAdminConstants.DOCUMENTAL_INFO_PORTAL);
	}

	public boolean isShowDocumentalInfo() {
		return this.showDocumentalInfo;
	}	

	public boolean isShowInfo() {
		return isShowDocumentalInfo() || isShowFiscalInfo() || isShowPayrollInfo();
	}
	
	public boolean isInfoEnabled() {
		return isDocumentalInfo() || isFiscalInfo() || isPayrollInfo();
	}
	
	public boolean isPayrollPortal() {
		return getPortalValue(IAdminConstants.PAYROLL_PORTAL);
	}

	public void setPayrollPortal(boolean payrollPortal) {
		setPortalValue(payrollPortal, IAdminConstants.PAYROLL_PORTAL);
	}

	public boolean isShowPayrollPortal() {
		return this.showPayrollPortal;
	}
	
	public void onInfoChanged( ActionEvent event ) {
		if ( isInfoEnabled() && isShowPayrollPortal() ) {
			setPayrollPortal(false);
		}
	}	
	
	public void onPayrollPortalChanged( ActionEvent event ) {
		if ( isPayrollPortal() && isShowInfo() ) {
			setFiscalInfo(false);
			setDocumentalInfo(false);
			setPayrollInfo(false);
		}
	}

	private void updateScopes() {
		UserScopeController usc = (UserScopeController) AonUtil.getRegisteredBean(IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME);
		usc.init(user);
	}	
	
}