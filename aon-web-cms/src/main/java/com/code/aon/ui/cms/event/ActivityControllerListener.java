package com.code.aon.ui.cms.event;

import com.code.aon.cms.Activity;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ActivityControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Activity to = (Activity)event.getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ACTIVITY_ALIAS),to.getAlias());
			if (!bean.getList(criteria).isEmpty()){
				throw new ControllerListenerException("ALIAS DUPLICATED");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}		
		
	}
}
