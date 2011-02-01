package com.code.aon.ui.form.listener;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * The Class ControllerAdapter.
 * 
 * @author Esferalia Networks.
 */
public class ConfidentialityFilterListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		BasicController controller = (BasicController)event.getController();
		String alias = controller.getPojoShortName() + "_securityLevel";
		try {
			controller.getCriteria().addEqualExpression(controller.getFieldName(alias), SecurityLevel.OFFICIAL);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding confidentialityFilter",e);
		}
	}

}