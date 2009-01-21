package com.code.aon.ui.config.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.UserScope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class UserScopeController extends LinesController {

	@SuppressWarnings("unchecked")
	public void onScopeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(scopeBean.getFieldName(IConfigAlias.SCOPE_ID), event.getNewValue());
			Iterator iter = scopeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Scope scope = (Scope)iter.next();
				((UserScope)this.getTo()).setScope(scope);
			}
		}
	}
}