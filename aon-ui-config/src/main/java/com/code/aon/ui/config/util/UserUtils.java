package com.code.aon.ui.config.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.enumeration.Toolbar;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);

	private Boolean passwordExpired;
	private User loggedUser;

	public boolean isPasswordExpired() {
		if ( passwordExpired == null ) {
			passwordExpired = Boolean.FALSE;
			User user = getLoggedUser();
			if ( user!=null && user.getPasswordExpiration()!=null ) {
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

	private void updateUser( User user ) {
		if ( user.getToolbar() == Toolbar.ESFERALIA_WEBMAIL ) {
			user.setToolbar(Toolbar.ACENS);
			try {
				IManagerBean bean = BeanManager.getManagerBean(User.class);
				bean.update(user);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}

	private User resolveUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery("SELECT u FROM User u  WHERE u.id = ?");
		query.setInteger(0, principal.getUserId());
		return (User) query.uniqueResult();
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
            LOGGER.error( "Error obtaining the employee groups related with the logged in user", e);
        }
        return expression;
    }
	
	public List<Scope> getCurrentUserScopes() {
		List<Scope> scopes = new LinkedList<Scope>();
		try {
			// Si el usuario pertenece a un dominio padre, pero el dominio activo es hijo,
			// se habilitan todos los scopes del hijo.
			if (DomainManager.isParentDomainUserInChildDomain()) {
				IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
				Criteria criteria = new Criteria();
				for (ITransferObject ito : scopeBean.getList(criteria)) {
					scopes.add((Scope) ito);
				}
			} else {
				IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), getLoggedUser().getId());
				criteria.addOrder(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_DESCRIPTION));
				for (ITransferObject ito : userScopeBean.getList(criteria)) {
					scopes.add(((UserScope)ito).getScope());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error scopes related with the user" + getLoggedUser().getLogin(), e);
		}
		return scopes;
	}

	public boolean isScopeInUserScopes(Scope scope) {
		return getCurrentUserScopes().contains(scope);
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getCurrentUserScopeIds(User user) {
		List<Integer> scopes = null;
		try {		
			if (DomainManager.isParentDomainUserInChildDomain()) {
				IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
				Criteria criteria = new Criteria();
				Projection projection = Projection.property(scopeBean.getFieldName(IEntityAlias.SCOPE_ID));
				scopes = scopeBean.getList(new ProjectionList(projection), criteria);
			} else {	
				IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), user.getId());
				Projection projection = Projection.property(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_ID));
				scopes = userScopeBean.getList(new ProjectionList(projection), criteria);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error scopes related with the user" + getLoggedUser().getLogin(), e);
		}		
		return scopes;
	}

	public void addScopeFilterToCriteria(Criteria criteria, String alias) throws ManagerBeanException {
		Expression scopeExpression = null;
		for(Scope scope : getCurrentUserScopes()) {
			if (scopeExpression == null) {
				scopeExpression = ExpressionUtilities.getEqualExpression(alias, scope.getId());				
			} else {
				Expression expression = ExpressionUtilities.getEqualExpression(alias, scope.getId());
				scopeExpression = ExpressionUtilities.getOrExpression(scopeExpression, expression);
			}
		}
		if (scopeExpression != null) {
			criteria.addExpression(scopeExpression);
		}
	}	

	public Expression getNullableScopeExpression( String resolvedAlias ) {
		User user = UserUtils.getInstance().getLoggedUser();
		String nullAlias = StringUtils.substringBeforeLast(resolvedAlias, ".");
		Expression exp = ExpressionUtilities.getNullExpression(nullAlias);
		if (user != null) {
			List<Integer> list = getCurrentUserScopeIds(user);
			if (list!= null && !list.isEmpty() ) {
				String ljAlias = getLeftJoinAlias(resolvedAlias);
				Expression scopeExp = ExpressionUtilities.getInExpression(ljAlias, list);
				exp = ExpressionUtilities.getOrExpression(exp, scopeExp);					
			}
		}
		return exp;
	}

	private String getLeftJoinAlias( String alias ) {
		String ljAlias = alias;
		int index = StringUtils.lastIndexOf(alias, '.');
		if ( index != -1 ) {
			ljAlias = StringUtils.substring(alias, 0, index) + "<" + StringUtils.substring(alias, index+1); 
		}
		return ljAlias;
	}

}