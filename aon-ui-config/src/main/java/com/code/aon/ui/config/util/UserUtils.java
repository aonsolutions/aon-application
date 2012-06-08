package com.code.aon.ui.config.util;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserUtils {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);
	
	private Boolean passwordExpired;
	
	private User loggedUser;
	
	public AuthPrincipal getPrincipal() {
		return BasicPrincipal.getAuthPrincipal();
	}

	public User getLoggedUser() {
		if (this.loggedUser == null) {
			this.loggedUser = resolveUser();
		}
		return loggedUser;
	}
	
	private User resolveUser() {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		String q = "SELECT u FROM User u  WHERE u.login = '" + getPrincipal().getShortName() + "'";
		if (getPrincipal().getDomainId() != null) {
			q = q + " AND u.domain = " + getPrincipal().getDomainId();
		}
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
		List<?> queryList = query.list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			User user = (User) iterator.next();
			return user;
		}
        return null;		
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
			IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), getLoggedUser().getId());
			criteria.addOrder(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_DESCRIPTION));
			for (ITransferObject ito : userScopeBean.getList(criteria)) {
				scopes.add(((UserScope)ito).getScope());
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

	public boolean isPasswordExpired() {
		if ( passwordExpired == null ) {
			passwordExpired = Boolean.FALSE;
			User user = getLoggedUser();
			if ( user != null ) {
				if ( user.getPasswordExpiration() != null ) {
					passwordExpired = new Date().after(user.getPasswordExpiration());
				}
			}		
		}
		return passwordExpired;
	}
	
}