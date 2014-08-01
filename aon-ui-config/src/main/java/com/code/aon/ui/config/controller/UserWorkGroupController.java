package com.code.aon.ui.config.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class UserWorkGroupController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public void onEmployeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean userBean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			String identifier = userBean.getFieldName(IEntityAlias.USER_ID);
			criteria.addEqualExpression( identifier, event.getNewValue());
			Iterator<ITransferObject> iter = userBean.getList(criteria).iterator();
			if(iter.hasNext()){
				User user = (User)iter.next();
				((UserWorkGroup)this.getTo()).setUser(user);
			}
		}
	}
}