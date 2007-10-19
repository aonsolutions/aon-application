package com.code.aon.ui.config.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ScopeControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getFieldName(IConfigAlias.SCOPE_DESCRIPTION));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining scope model");
		}
	}
}