package com.code.aon.ui.config.util;

import java.security.Principal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;

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
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.util.AonUtil;

public class UserUtils {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);
	
	private AuthPrincipal principal;
	
	private User loggedUser;
	
	public UserUtils() {
		this.principal = resolvePrincipal();
		this.loggedUser = resolveUser();
	}
	
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	private AuthPrincipal resolvePrincipal() {
		AuthPrincipal user = null;
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} else {
			user = new AuthPrincipal( principal.getName() );
		}
		return user;
	}
	
	private User resolveUser() {
		try {
            IManagerBean bean = BeanManager.getManagerBean(User.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression( bean.getFieldName(IConfigAlias.USER_LOGIN), getPrincipal().getShortName() );
            List<ITransferObject> list = bean.getList(criteria);
            if ( (list!=null) && (list.size() == 1) ) {
                return (User) list.get(0);
            }
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error obtaining the USER related with the logged user: " + getPrincipal(), e);
        }
        return null;		
	}

	public static UserUtils getInstance() {
		UserUtils bean = (UserUtils) AonUtil.getRegisteredBean(ConfigConstants.USER_UTILS);
		return bean;
	}
	
	@SuppressWarnings("unchecked")
    public static Expression obtainUserWorkGroupsExpr(User user, String alias) {
        Expression expression = null;
        try {
            IManagerBean employeeWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(employeeWorkGroupBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_ID), user.getId());
            Iterator iter = employeeWorkGroupBean.getList(criteria).iterator();
            while(iter.hasNext()) {
            	UserWorkGroup userWorkGroup = (UserWorkGroup)iter.next();
                expression = ExpressionUtilities.getOrExpression(expression, ExpressionUtilities.getEqualExpression(alias, userWorkGroup.getWorkGroup().getId()));
            }
        } catch (ManagerBeanException e) {
            LOGGER.error( "Error obtaining the employee groups related with the logged in user", e);
        }
        return expression;
    }
	
	@SuppressWarnings("unchecked")
	public List<Scope> getCurrentUserScopes(){
		List<Scope> scopes = new LinkedList<Scope>();
		try {
			IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(userScopeBean.getFieldName(IConfigAlias.USER_SCOPE_USER_ID), getLoggedUser().getId());
			Iterator iter = userScopeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				scopes.add(((UserScope)iter.next()).getScope());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error scopes related with the user" + getLoggedUser().getLogin(), e);
		}
		return scopes;
	}
}