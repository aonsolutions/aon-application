package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Controller used in the RegistryMedia maintenance
 */
public class RegistryMediaControllerListener extends ControllerAdapter {

	/** ADDRESS_CONTROLLER_NAME. */
	private static final String MEDIA_CONTROLLER_NAME = "media";
	
	/**
	 * Gets the detail controller.
	 * 
	 * @return the detail controller
	 */
	public IController getDetailController() {
		return FormUtil.getController(MEDIA_CONTROLLER_NAME);
	}

	/**
	 * After bean selected. Reloads the model with the medias related with the current registry
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			IController master = event.getController();
			IController detail = getDetailController();
			Registry to = (Registry) master.getTo();
			String reg = detail
					.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reg, to.getId());
			detail.setCriteria(criteria);
			detail.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}