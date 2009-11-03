package com.code.aon.ui.config.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

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
			Expression exp = getExpression(event.getController());
			event.getController().getCriteria().addExpression(exp);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding scopeFilter",e);
		}
	}

	private List<ITransferObject> obtainUserScopeList(User user) throws ManagerBeanException {
		IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userScopeBean.getFieldName(IConfigAlias.USER_SCOPE_USER_ID), user.getId());
		return userScopeBean.getList(criteria);
	}
	
	private String getLeftJoinAlias( String alias ) {
		String ljAlias = alias;
		int index = StringUtils.lastIndexOf(alias, '.');
		if ( index != -1 ) {
			ljAlias = StringUtils.substring(alias, 0, index) + "<" + StringUtils.substring(alias, index+1); 
		}
		return ljAlias;
	}
	
	private Expression getExpression( IController controller ) throws ManagerBeanException {
		User user = UserUtils.getInstance().getLoggedUser();
		String alias = controller.getFieldName(this.aliasName);
		String nullAlias = StringUtils.substringBeforeLast(alias, ".");
		Expression exp = ExpressionUtilities.getNullExpression(nullAlias);
		if (user != null) {
			List<ITransferObject> list = obtainUserScopeList(user);
			if (! list.isEmpty() ) {
				String ljAlias = getLeftJoinAlias(alias);
				for( ITransferObject to : list ) {
					UserScope userScope = (UserScope) to;
					Expression scopeExp = ExpressionUtilities.getEqualExpression(ljAlias, userScope.getScope().getId());
					exp = ExpressionUtilities.getOrExpression(exp, scopeExp);					
				}
			}
		}
		return exp;
	}

}