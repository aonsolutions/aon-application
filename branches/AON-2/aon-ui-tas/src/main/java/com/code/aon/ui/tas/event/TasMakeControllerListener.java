package com.code.aon.ui.tas.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the TasMakeController.
 */
public class TasMakeControllerListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TasMakeControllerListener.class.getName());
	
	/**
	 * Adds sorting criteria
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ITASAlias.MAKE_NAME));
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error beforeModelInitialized", e);
		}
	}
}