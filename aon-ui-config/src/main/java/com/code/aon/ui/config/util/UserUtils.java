package com.code.aon.ui.config.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.enumeration.Toolbar;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserUtils implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);

	private Boolean passwordExpired;
	private User loggedUser;
	private List<Integer> userScopeIds;
	private boolean addScopeExpression;
	
	public UserUtils() {
		this.userScopeIds = Collections.emptyList();
	}


	public boolean isWithOutTopSearch() {
		return getLoggedUser().getToolbar() == Toolbar.ACENS;
	}

	public boolean isNewAONTheme() {
		return getLoggedUser().getToolbar() == Toolbar.AON_SOLUTIONS;
	}
	
	public boolean isAonNewSuite() {
		return getLoggedUser().getToolbar() == Toolbar.ARSYS;
	}

	public boolean isPasswordExpired() {
		if (passwordExpired == null) {
			passwordExpired = Boolean.FALSE;
			User user = getLoggedUser();
			if (user!=null && user.getPasswordExpiration()!=null) {
				passwordExpired = new Date().after(user.getPasswordExpiration());
			}		
		}
		return passwordExpired;
	}

	public User getLoggedUser() {
		if (this.loggedUser == null) {
			this.loggedUser = resolveUser();
			updateUser(this.loggedUser);
		}
		return loggedUser;
	}

	private void updateUser(User user) {
		if (user.getToolbar() == Toolbar.ESFERALIA_WEBMAIL) {
			user.setToolbar(Toolbar.ACENS);
			try {
				IManagerBean bean = BeanManager.getManagerBean(User.class);
				bean.update(user);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
		this.userScopeIds = getUserScopeIds(false);
	}

	private User resolveUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery("SELECT u FROM User u  WHERE u.id = ?");
		query.setInteger(0, principal.getUserId());
		User user = (User) query.uniqueResult();
		HibernateUtil.closeSession(sessionFactoryName, false);
		return user;
	}

	public static UserUtils getInstance() {
		UserUtils bean = (UserUtils) AonUtil.getRegisteredBean(ConfigConstants.USER_UTILS);
		return bean;
	}
	
    public static Expression obtainUserWorkGroupsExpr(User user, String alias) {
        Expression expression = null;
        try {
            IManagerBean employeeWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(employeeWorkGroupBean.getFieldName(IEntityAlias.USER_WORK_GROUP_USER_ID), user.getId());
            for (ITransferObject ito : employeeWorkGroupBean.getList(criteria)) {
            	UserWorkGroup userWorkGroup = (UserWorkGroup)ito;
                expression = ExpressionUtilities.getOrExpression(expression, ExpressionUtilities.getEqualExpression(alias, userWorkGroup.getWorkGroup().getId()));
            }
        } catch (ManagerBeanException e) {
            LOGGER.error("Error obtaining the employee groups related with the logged in user", e);
        }
        return expression;
    }
	
	public List<Scope> getCurrentUserScopes() {
		return getCurrentUserScopes(false);
	}


	@SuppressWarnings("unchecked")
	private List<Integer> getScopeIds( Integer[] domains ) throws ManagerBeanException {
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addInExpression(scopeBean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domains);
		Projection projection = Projection.property(scopeBean.getFieldName(IEntityAlias.SCOPE_ID));
		return scopeBean.getList(new ProjectionList(projection), criteria);																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Scope> getScopes( Integer[] domains ) throws ManagerBeanException {
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addInExpression(scopeBean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domains);
		criteria.addOrder(scopeBean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
		return (List) scopeBean.getList(criteria);																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getUserScopeIds( Integer[] domains ) throws ManagerBeanException {
		IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression("UserScope.domain", loggedUser.getDomain());
		criteria.addInExpression("UserScope.scope.domain", domains);
		criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), loggedUser.getId());
		Projection projection = Projection.property(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_ID));
		return userScopeBean.getList(new ProjectionList(projection), criteria);
	}
	
	private List<Scope> getUserScopes( Integer[] domains ) throws ManagerBeanException {
		List<Scope> scopes = new LinkedList<Scope>();
		IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression("UserScope.domain", loggedUser.getDomain());
		criteria.addInExpression("UserScope.scope.domain", domains);
		criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), loggedUser.getId());
		criteria.addOrder(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_DESCRIPTION));
		scopes = new LinkedList<Scope>();
		for (ITransferObject ito : userScopeBean.getList(criteria)) {
			scopes.add(((UserScope)ito).getScope());	
		}
		return scopes;
	}
	
	public List<Scope> getCurrentUserScopes( boolean forceHeredity ) {
		List<Scope> scopes = null;
		try {
			if ( isAdminUser() ) {
				scopes = getScopes(getDomains(true));
			} else if (DomainManager.isParentDomainUserInChildDomain()) {
				scopes = getScopes(new Integer[]{DomainManager.getCurrentDomain()});
				if ( isAddParentScope(forceHeredity) ) {
					scopes.addAll(getUserScopes(new Integer[]{loggedUser.getDomain()}));	
				}
			} else {
				scopes = getUserScopes(getDomains(forceHeredity));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error scopes related with the user" + getLoggedUser().getLogin(), e);
		}
		return scopes;
	}

	private List<Integer> getUserScopeIds( boolean forceHeredity ) {
		List<Integer> scopes = null;
		try {		
			if ( isAdminUser() ) {
				scopes = getScopeIds(getDomains(true));
			} else if (DomainManager.isParentDomainUserInChildDomain()) {
				this.addScopeExpression = isAddParentScope(false);			
				scopes = getScopeIds(new Integer[]{DomainManager.getCurrentDomain()});
				if ( this.addScopeExpression ) {
					scopes.addAll(getUserScopeIds(new Integer[]{loggedUser.getDomain()}));	
				}
			} else {	
				this.addScopeExpression = true;
				scopes = getUserScopeIds(getDomains(forceHeredity));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error scopes related with the user" + loggedUser.getLogin(), e);
		}		
		return scopes;
	}
	
	public boolean isScopeInUserScopes(Scope scope) {
		return getCurrentUserScopes().contains(scope);
	}
	
	public List<Integer> getCurrentUserScopeIds() {
		return getCurrentUserScopeIds(false);
	}

	public List<Integer> getCurrentUserScopeIds( boolean forceHeredity ) {
		if ( forceHeredity ) {
			return getUserScopeIds(forceHeredity);
		}
		return this.userScopeIds;
	}
	
	public void addScopeFilterToCriteria(Criteria criteria, String alias) throws ManagerBeanException {
		if ( this.addScopeExpression ) {
			List<Integer> list = getCurrentUserScopeIds();
			if (! list.isEmpty() ) {
				if (list.size() == 1) {
					criteria.addEqualExpression(alias, list.get(0));
				} else {
					criteria.addInExpression(alias, list);
				}			
			}			
		}
	}	

	public Expression getNullableScopeExpression(String resolvedAlias, boolean forceHeredity) {
		String nullAlias = StringUtils.substringBeforeLast(resolvedAlias, "<");
		if (nullAlias.equals(resolvedAlias)) {
			nullAlias = StringUtils.substringBeforeLast(resolvedAlias, ".");
		}
		Expression exp = ExpressionUtilities.getNullExpression(nullAlias);
		List<Integer> list = getCurrentUserScopeIds(forceHeredity);
		if (list!= null && !list.isEmpty()) {
			String ljAlias = getLeftJoinAlias(resolvedAlias);
			Expression scopeExp = null;
			if (list.size() == 1) {
				scopeExp = ExpressionUtilities.getEqualExpression(ljAlias, list.get(0));
			} else {
				scopeExp = ExpressionUtilities.getInExpression(ljAlias, list);
			}
			exp = ExpressionUtilities.getOrExpression(exp, scopeExp);					
		}
		return exp;
	}
	
	public Expression getNullableScopeExpression(String resolvedAlias) {
		return getNullableScopeExpression(resolvedAlias, false);
	}
	
	public void addNullableScopeExpression(Criteria criteria, String resolvedAlias, boolean forceHeredity ) {
		if ( this.addScopeExpression ) {
			criteria.addExpression( getNullableScopeExpression(resolvedAlias, forceHeredity) );
		}
	}

	public void addNullableScopeExpression(Criteria criteria, String resolvedAlias) {
		addNullableScopeExpression(criteria, resolvedAlias, false);
	}
	
	private String getLeftJoinAlias(String alias) {
		String ljAlias = alias;
		if (!StringUtils.contains(ljAlias, "<")) {
			int index = StringUtils.lastIndexOf(alias, ".");
			if (index != -1) {
				ljAlias = StringUtils.substring(alias, 0, index) + "<" + StringUtils.substring(alias, index+1); 
			}
		}
		return ljAlias;
	}
	
	private boolean isAddParentScope( boolean forceHeredity ) {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return ( ds.isChildDomain() && (ds.isEnableHeredity() || forceHeredity) );
	}

	private Integer[] getDomains( boolean forceHeredity ) {
		Integer[] domains = null;
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		if ( ds.isChildDomain() && (ds.isEnableHeredity() || forceHeredity) ) {
			domains = new Integer[]{ds.getParentDomainId(), ds.getDomainId()};
		} else {
			domains = new Integer[]{ds.getDomainId()};
		}
		return domains;
	}
	
	public void addForceHeredityDomainCondition( Criteria criteria, String alias ) {
		if (! criteria.isSkipDomainFilter() ) {
			criteria.setSkipDomainFilter(true);
			criteria.addInExpression(alias, getDomains(true));
		}
	}
	
	private boolean isAdminUser() {
		Integer type = AdminUtil.getDomainType(loggedUser.getDomain());
		return (type != null) && (type == DomainType.ADMIN.ordinal());		
	}
	
	public boolean isAddScopeExpression() {
		return this.addScopeExpression;
	}	
	
}