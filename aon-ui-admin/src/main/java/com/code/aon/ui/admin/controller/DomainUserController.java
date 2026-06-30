package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.Session;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Favorite;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.UserApplicationInfo;
import com.code.aon.ui.admin.util.UserIdCheckUtil;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.db.Contact;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.db.Signature;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class DomainUserController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	public final static String FAVORITES = "[FAVORITES]";
	
	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;	

	private String selectedTab;
		
	private UserIdCheckUtil idCheck;
	
	private List<UserApplicationInfo> applicationInfos;
	
	private boolean skipDomain;
	
	private boolean showFavorites;
	
	private boolean portal = false;
	
	public DomainUserController() {
		this.idCheck = new UserIdCheckUtil();
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public UserIdCheckUtil getIdCheck() {
		return idCheck;
	}

	public void setIdCheck(UserIdCheckUtil idCheck) {
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
	
	@Override
	public void onSearch(ActionEvent event) {
		if(!portal) {
			try {
				getCriteria().addNullExpression(getFieldName(IEntityAlias.USER_ENTERPRISE));
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		} else portal = false;
		
		super.onSearch(event);
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
			String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.NEW_PASSWORD_ERROR);
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
			userScope.setUserDBByUserId(user);
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

	public static int getNumberOfActiveUsers( Integer domainId) {
        try {
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			if ( domainId != null ) {
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_DOMAIN), domainId);
			}
			String alias = bean.getFieldName(IEntityAlias.USER_ACTIVE);
			criteria.addEqualExpression(alias, Boolean.TRUE);
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.USER_ENTERPRISE));
			return bean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return 0;
	}
	
	public int getNumberOfActiveUsers() {
		return getNumberOfActiveUsers(null);
	}

	public String getActiveUsersMessage() {
		return AonUtil.getMessage(ICommonMessages.ACTIVE_USERS, getNumberOfActiveUsers());		
	}
	
	public Domain getUserDomain() {
		User user = getDomainUser();
		if (user != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Domain.class);
				Domain domain = (Domain) bean.get( user.getDomain() );
				return domain;
			} catch (ManagerBeanException e) {
				return null;
			}
		}
		return null;
	}
	
	public Integer getLoggedUserDomain() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		return (principal != null) ? principal.getDomainId() : null;
	}
	
	public boolean isImpersonateUserEnabled() {
		if (!isNevv()) {
			String dn1 = getCurrentURLDomain();
			String dn2 = getUserDomain().getName();
			return !StringUtils.equals( dn1 , dn2 );
		}
		return false;	
	}
	
	public String getImpersonateUserURL() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		StringBuilder url = new StringBuilder();
		
		if (request.getServerPort() != 80 && request.getServerPort() != 443)  {
			url.append( (AonStringUtils.isBlank(request.getScheme())? "http" : request.getScheme()) )
				.append("://")
				.append( getUserDomain().getName() )
				.append(":" + request.getServerPort())
			;	
		} else {
			url.append( "https://")
				.append( getUserDomain().getName() );
		}
		url
			.append(request.getContextPath())
			.append("/impuser/home.jsf")
		;
		return url.toString();
	}


	public String getCurrentURLDomain() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return request.getServerName();
	}
	
	private Domain getDomain() {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		return dc.getDomain();
	}

	public String getDetailMessage() {
		String message = getActiveUsersMessage();
		Domain domain = getDomain();
		if ( domain.getMaxDefinedUsers() != null ) {
			message += ", " + AonUtil.getMessage(ICommonMessages.MAXIMUM_NUMBER_USERS, domain.getMaxDefinedUsers());
		}
		return message;
	}
	
	public boolean isSkipUserReset() {
		Domain domain = getDomain();
		if ( domain.getMaxDefinedUsers() != null ) {
			return getNumberOfActiveUsers() >= domain.getMaxDefinedUsers();
		}
		return true;
	}
	
	public boolean isUserActivable() {
		if ( AonUtil.getRoleManager().isSysAdmin() ) {
			return true;
		}
		return getDomainUser().isActive() || !isSkipUserReset();
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

	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			User user = (User) getModel().getRowData();
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			Integer applicationUser = AdminUtil.getApplicationUser(user.getDomain(), user.getId(), appId);
			if ( applicationUser != null ) {
				IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID), applicationUser);
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
	
	public void removeUserReferences( Serializable id ) throws ManagerBeanException {
		DomainApplicationUserController dausc = (DomainApplicationUserController) AonUtil.getRegisteredBean(IAdminConstants.APPLICATION_USER_CONTROLLER_NAME);
		dausc.removeApplicationUsers( IEntityAlias.APPLICATION_USER_USER_ID, id );
		FormUtil.remove(UserScope.class, id, true, IEntityAlias.USER_SCOPE_USER_ID);
		FormUtil.remove(UserWorkGroup.class, id, true, IEntityAlias.USER_WORK_GROUP_USER_ID);
		FormUtil.remove(ActionDenied.class, id, true, IEntityAlias.ACTION_DENIED_USER_ID);
		FormUtil.remove(ActionFavorite.class, id, true, IEntityAlias.ACTION_FAVORITE_USER_ID);
		FormUtil.remove(Contact.class, id, true, IEntityAlias.CONTACT_USER_ID);
		FormUtil.remove(MailAccount.class, id, true, IEntityAlias.MAIL_ACCOUNT_USER_ID);
		FormUtil.remove(Signature.class, id, true, IEntityAlias.SIGNATURE_USER_ID);
		FormUtil.remove(ActionEntry.class, id, true, IEntityAlias.ACTION_ENTRY_SESSION_USER_ID);
		FormUtil.remove(Session.class, id, true, IEntityAlias.SESSION_USER_ID);
		FormUtil.remove(Alarm.class, id, true, IEntityAlias.ALARM_USER_ID);
		FormUtil.remove(Favorite.class, id, true, IEntityAlias.FAVORITE_USER_ID);
		FormUtil.remove(FavoriteCategory.class, id, true, IEntityAlias.FAVORITE_CATEGORY_USER_ID);
		FormUtil.remove(Note.class, id, true, IEntityAlias.NOTE_OWNER_ID);
		FormUtil.remove(Notice.class, id, true, IEntityAlias.NOTICE_SENDER_ID, IEntityAlias.NOTICE_RECIPIENT_ID);
		resetTaskHolder(id);		
	}

	private void resetTaskHolder( Serializable id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(TaskHolder.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_USER_ID), id);
		for( ITransferObject to : bean.getList(criteria) ) {
			((TaskHolder) to).setUser(null);
			bean.update(to);
		}
	}
	
	public void addPortalExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String value = event.getNewValue().toString();
			if (! StringUtils.isBlank(value) && "true".equalsIgnoreCase(value)) {
				this.portal = true;
				IManagerBean bean = BeanManager.getManagerBean(User.class);
				getCriteria().addEqualExpression(bean.getFieldName(IEntityAlias.USER_ENTERPRISE), getEnterpriseId());
			} else this.portal = false;
		}
	} 
	
	@Override
	protected void accept() {
		User user2 = (User) getTo();
		System.out.println( user2.getEnterprise());
		super.accept();
	}
	
	private Integer getEnterpriseId() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = controller.obtainCompany();
		return company.getId();
	}
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		getCriteria().setSkipDomainFilter(this.skipDomain);
	}	
	
	public void onInitUserProfile( ActionEvent event ) throws ManagerBeanException {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		this.skipDomain = true;
		select(event, principal.getUserId());
	}

	public boolean isSkipDomain() {
		return skipDomain;
	}

	public void setSkipDomain(boolean skipDomain) {
		this.skipDomain = skipDomain;
	}

	public boolean isShowFavorites() {
		return showFavorites;
	}

	public void setShowFavorites(boolean showFavorites) {
		this.showFavorites = showFavorites;
	}
	
	public boolean isShowFavoritesEnabled() throws ManagerBeanException {
		if ( ! showFavorites ) {
			User user = (User) getTo();
			return StringUtils.isBlank(user.getInitAction());			
		}
		return true;
	}
	
}