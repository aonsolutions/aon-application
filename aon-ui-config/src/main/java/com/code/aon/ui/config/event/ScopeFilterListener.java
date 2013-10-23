package com.code.aon.ui.config.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ScopeFilterListener extends ControllerAdapter {

	private String aliasName;

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			if (!DomainManager.isParentDomainUserInChildDomain()) {
				Expression exp = getExpression( event.getController().resolveAlias(this.aliasName) );
				event.getController().getCriteria().addExpression(exp);
			} 
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding scopeFilter",e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Integer> obtainUserScopeList(User user) throws ManagerBeanException {
		IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), user.getId());
		Projection projection = Projection.property(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_ID));
		return userScopeBean.getList(new ProjectionList(projection), criteria);
	}
	
	private String getLeftJoinAlias( String alias ) {
		String ljAlias = alias;
		int index = StringUtils.lastIndexOf(alias, '.');
		if ( index != -1 ) {
			ljAlias = StringUtils.substring(alias, 0, index) + "<" + StringUtils.substring(alias, index+1); 
		}
		return ljAlias;
	}
	
	private Expression getExpression( String resolvedAlias ) throws ManagerBeanException {
		User user = UserUtils.getInstance().getLoggedUser();
		String nullAlias = StringUtils.substringBeforeLast(resolvedAlias, ".");
		Expression exp = ExpressionUtilities.getNullExpression(nullAlias);
		if (user != null) {
			List<Integer> list = obtainUserScopeList(user);
			if (! list.isEmpty() ) {
				String ljAlias = getLeftJoinAlias(resolvedAlias);
				Expression scopeExp = ExpressionUtilities.getInExpression(ljAlias, list);
				exp = ExpressionUtilities.getOrExpression(exp, scopeExp);					
			}
		}
		return exp;
	}

}