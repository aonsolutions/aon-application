package com.code.aon.ui.config.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the ItemController.
 */
public class ScopeVetoListener extends ControllerAdapter {

	/**
	 * Sets a default productType to the current Item
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			List<ITransferObject> scopeLst = scopeBean.getList(null);
			if (scopeLst.size() > 0) {
				Scope scope = (Scope)scopeLst.get(0);
				IScopable scopable = (IScopable)event.getController().getTo();
				scopable.setScope(scope);
			}
			else throw new ControllerListenerException("No scope defined.");

		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
			throw new ControllerListenerException(mbe.getMessage());
		}
	}
}