package com.code.aon.ui.config.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class UserWorkGroupController extends LinesController {
	
	@SuppressWarnings("unchecked")
	public void onEmployeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean userBean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			String identifier = userBean.getFieldName(IConfigAlias.USER_ID);
			criteria.addEqualExpression( identifier, event.getNewValue());
			Iterator iter = userBean.getList(criteria).iterator();
			if(iter.hasNext()){
				User user = (User)iter.next();
				((UserWorkGroup)this.getTo()).setUser(user);
			}
		}
	}
}