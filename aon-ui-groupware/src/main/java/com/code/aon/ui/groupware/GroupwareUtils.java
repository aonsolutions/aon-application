package com.code.aon.ui.groupware;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.config.User;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;

public class GroupwareUtils {

	private TaskHolder currentTaskHolder;
	
	public TaskHolder getCurrentTaskHolder() throws ManagerBeanException {
		if (currentTaskHolder == null) {
	        AuthPrincipal principal = BasicPrincipal.getAuthPrincipal();
	        if( principal.getUserId() == null){
	        	throw new IllegalStateException("No es posible encontrar el usuario actual");
	        }
			IManagerBean bean = BeanManager.getManagerBean(TaskHolder.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_USER_ID), principal.getUserId());
			List<ITransferObject> list = bean.getList(criteria);
			if (list != null && list.size() > 0) {
				currentTaskHolder = (TaskHolder) list.get(0);
			} 
		}
		return currentTaskHolder;
	}
}
