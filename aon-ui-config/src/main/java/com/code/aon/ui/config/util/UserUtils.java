package com.code.aon.ui.config.util;

import java.security.Principal;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;

public class UserUtils {
	
	private static final Logger LOGGER = Logger.getLogger(UserUtils.class.getName());

	@SuppressWarnings("unchecked")
	public static User getLoggedUser(){
		try {
			String name = null;
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
    		if ( principal != null && principal instanceof AuthPrincipal ) {
    			name = ( (AuthPrincipal) principal ).getShortName(); 
    		} else if ( principal != null ) {
    			name = new AuthPrincipal( principal.getName() ).getShortName(); 
    		}
            IManagerBean bean = BeanManager.getManagerBean(User.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression( bean.getFieldName(IConfigAlias.USER_LOGIN), name );
            Iterator iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                return ((User)iterator.next());
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining the USER related with the logged in user", e);
        }
        return null;
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
            LOGGER.log(Level.SEVERE, "Error obtaining the employee groups related with the logged in user", e);
        }
        return expression;
    }
}
