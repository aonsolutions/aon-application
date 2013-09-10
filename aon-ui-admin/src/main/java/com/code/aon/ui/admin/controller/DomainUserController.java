package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ACTIVE_USERS;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.MAXIMUM_NUMBER_USERS;
import static com.code.aon.ui.admin.controller.IAdminConstants.USER_DUPLICATED;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.NEW_PASSWORD_ERROR;
import static com.esferalia.aon.entity.IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.UserApplicationInfo;
import com.code.aon.ui.admin.util.IdCheckUtil;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainUserController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;	

	private String selectedTab;
	
	private IdCheckUtil idCheck;
	
	private List<UserApplicationInfo> applicationInfos;
	
	private List<SelectItem> actionList;
	
	public DomainUserController() {
		this.idCheck = new IdCheckUtil(this, IEntityAlias.USER_LOGIN, USER_DUPLICATED);
		this.idCheck.setDomainAlias(IEntityAlias.USER_DOMAIN);
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public IdCheckUtil getIdCheck() {
		return idCheck;
	}

	public void setIdCheck(IdCheckUtil idCheck) {
		this.idCheck = idCheck;
	}

	public void idCheck(FacesContext context, UIComponent component, Object value) {
		this.idCheck.idCheck( (String) value );
	}		

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User> getUsers() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
		return (List) getManagerBean().getList(criteria);
	}	
	
	public User getDomainUser() {
		return (User) getTo();
	}	

	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}
	
	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle(NEW_PASSWORD_ERROR);
			throw new AbortProcessingException( message );
		}		
		User user = getDomainUser();
		try {
	        user.setPassword( AdminUtil.encodeSHA(newPassword) );
	        user.setPasswordExpiration( DateUtils.addDays(new Date(), 180) );
	        getManagerBean().update(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}
	}
	
	public void onResetPassword( ActionEvent event ) {
		User user = getDomainUser();
		try {
			resetPassword( user );
	        getManagerBean().update(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}		
	}	
	
	public void resetPassword( User user ) {
		user.setPasswordExpiration( DateUtils.addDays(new Date(), -1) );
		String newPassword = AdminUtil.encodeSHA(user.getLogin());
		user.setPassword( newPassword );
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

	public void registerScope( User user, String scopeName ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Scope.class);
		Criteria scopeCriteria = new Criteria();
		scopeCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION), scopeName);
		List<ITransferObject> scopes = bean.getList(scopeCriteria);
		if (! scopes.isEmpty() ) {
			Scope scope = (Scope) scopes.get(0);
			ensureDBUserScope(user, scope);
		}
	}

	public void registerWorkGroup( User user, String workGroupName ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkGroup.class);
		Criteria wgCriteria = new Criteria();
		wgCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION), workGroupName);
		List<ITransferObject> wgs = bean.getList(wgCriteria);
		WorkGroup workGroup = null;
		if (! wgs.isEmpty() ) {
			workGroup = (WorkGroup) wgs.get(0);
		} else {
			workGroup = new WorkGroup();
			workGroup.setStatus(WorkGroupStatus.ACTIVE);
			workGroup.setDescription(workGroupName);
			bean.insert(workGroup);
		}
		ensureDBUserWorkGroup(user, workGroup);
	}	
	
	private void ensureDBUserScope( User user, Scope scope ) throws ManagerBeanException {	
		IManagerBean bean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_ID), scope.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), user.getId());
		if ( bean.getCount(criteria) == 0 ) {
			UserScope userScope = new UserScope();
			userScope.setScope(scope);
			userScope.setUser(user);
			bean.insert(userScope);
		}
	}	

	public void ensureDBUserWorkGroup( User user, WorkGroup workGroup ) throws ManagerBeanException {	
		IManagerBean bean = BeanManager.getManagerBean(UserWorkGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroup.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_WORK_GROUP_USER_ID), user.getId());
		if ( bean.getCount(criteria) == 0 ) {
			UserWorkGroup uwg = new UserWorkGroup();
			uwg.setWorkGroup(workGroup);
			uwg.setUser(user);
			bean.insert(uwg);
		}
	}		

	public int getNumberOfActiveUsers() {
        try {
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.USER_ACTIVE);
			criteria.addEqualExpression(alias, Boolean.TRUE);
			return bean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return 0;
	}

	public String getActiveUsersMessage() {
		return AonUtil.getMessage(BUNDLE_NAME, ACTIVE_USERS, getNumberOfActiveUsers());		
	}

	public String getDetailMessage() {
		String message = getActiveUsersMessage();
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		if ( dc.getDomain().getMaxDefinedUsers() != null ) {
			message += ", " + AonUtil.getMessage(BUNDLE_NAME, MAXIMUM_NUMBER_USERS, dc.getDomain().getMaxDefinedUsers());
		}
		return message;
	}
	
	public int getMinimumUserNumber() {
		return Math.max(0, getNumberOfActiveUsers());
	}
	
	public boolean isSkipUserReset() {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		Domain domain = dc.getDomain();
		if ( domain.getMaxDefinedUsers() != null ) {
			return getNumberOfActiveUsers() >= domain.getMaxDefinedUsers();
		}
		return true;
	}
	
	public boolean isUserActivable() {
		if ( AonUtil.getRoleManager().isSysAdmin() ) {
			return true;
		}
		return getDomainUser().isActive() || (!isSkipUserReset());
	}	

	public void initApplicationInfos( User user ) throws ManagerBeanException {
		this.applicationInfos = UserApplicationInfo.getApplicationInfos(user);
	}
	
	public List<UserApplicationInfo> getApplicationInfos() {
		return applicationInfos;
	}
	
	public int getApplicationsInfoSize() {
		return applicationInfos.size();
	}

	public void registerAllApplications() throws ManagerBeanException {
		for( UserApplicationInfo uai : this.applicationInfos ) {
			uai.setChecked(true);
			uai.register();
		}
	}	
	
	public void onSaveApplications( ActionEvent event ) throws ManagerBeanException {
		boolean changed = false;
		for( UserApplicationInfo uai : this.applicationInfos ) {
			if ( uai.isChecked() ) {
				changed |= uai.register();
			} else {
				changed |= uai.unregister();
			}
		}
		if ( changed ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			adc.initEdit( getDomainUser() );
			User user = UserUtils.getInstance().getLoggedUser();
			if ( ObjectUtils.equals(user, getDomainUser()) ) {
				adc.initCurrentUser();
			}
		}
	}	

	public void updateActionList( List<ApplicationOption> options ) {
		actionList = new LinkedList<SelectItem>();
		for( ApplicationOption option : options ) {
			String name = StringUtils.abbreviate(option.getDescription(), 60) + " (" + option.getGroup().getCategory().getName() + ")";
			SelectItem item = new SelectItem(option.getAction(), name);
			actionList.add(item);
		}
		AonUtil.sortSelectItems(actionList);
	}
	
	public List<SelectItem> getActionList() {
        return actionList;
	}	

	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			User user = (User) getModel().getRowData();
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			Integer applicationUser = AdminUtil.getApplicationUser(user.getDomain(), user.getId(), appId);
			if ( applicationUser != null ) {
				IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(APPLICATION_USER_PROFILE_APPLICATION_USER_ID), applicationUser);
				List<ITransferObject> list = bean.getList(criteria);
				List<String> profiles = new LinkedList<String>();
				for( ITransferObject to : list ) {
					profiles.add( ((ApplicationUserProfile)to).getProfile().getName() );
				}
				return StringUtils.join(profiles, ", ");
			}
		}
		return null;
	}	
	
}