package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_USER;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.NEW_PASSWORD_ERROR;

import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainUserController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	private static final String SHA_ALGORITHM = "SHA";
	
	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;	

	private String selectedTab;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
	}		

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User> getUsers() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
		return (List) getManagerBean().getList(criteria);
	}	

	private boolean isAdmin( User user ) {
		return StringUtils.equals(ADMIN_USER, user.getLogin());
	}
	
	public void deactiveUsers() throws ManagerBeanException {
		for ( User user : getUsers() ) {
			if (! isAdmin(user) ) {
				user.setActive(false);
				getManagerBean().update(user);
			}
		}
	}	
	
	public User getDomainUser() {
		return (User) getTo();
	}	
	
	public boolean isUserRemoveable() {
		if ( getAdmin().isAdministrator() ) {
			return true;
		}
		return getAdmin().isUserManagement() && (!isAdmin(getDomainUser()));
	}

	public boolean isUserActivable() {
		if ( getAdmin().isAdministrator() ) {
			return true;
		}
		return getAdmin().isUserManagement() && !isAdmin(getDomainUser());
	}	

	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}
	
	private String encodeSHA( String value ) {
		String shaPassword = null;
		try {
			byte[] hash = MessageDigest.getInstance(SHA_ALGORITHM).digest(value.getBytes());
			shaPassword = new String( Base64.encodeBase64(hash) );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}        		
		return shaPassword;
	}	
	
	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR );
			throw new AbortProcessingException( message );
		}		
		User user = getDomainUser();
		try {
	        user.setPassword( encodeSHA(newPassword) );
	        getManagerBean().update(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}
		flushPasswordCache();
	}
	
	public void onResetPassword( ActionEvent event ) {
		resetPassword( getDomainUser() );
		flushPasswordCache();
	}	
	
	public void resetPassword( User user ) {
		user.setPasswordExpiration( DateUtils.addDays(new Date(), -1) );
		String newPassword = encodeSHA(user.getLogin());
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

	private void flushPasswordCache() {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		getAdmin().flushAuthenticationCache(dc.getDomain(), getDomainUser());
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
		
}