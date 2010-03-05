package com.code.aon.ui.tas.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * A listener for controller with a Model POJO.
 * 
 * @author Consulting & Development.
 * @since 1.0
 */
public class TASModelMakeListener extends ControllerAdapter {

	/** LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TASModelMakeListener.class.getName());
	
	/**
	 * Recover the entire Make object
	 * and assigns to the Model.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Model model = (Model) event.getController().getTo();
		try {
			Make make = (Make) BeanManager.getManagerBean(Make.class).get(model.getMake().getId());
			model.setMake( make );
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, "Error updating Make " + model.getMake(), e );
		}
	}
	
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
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ITASAlias.MODEL_MAKE_NAME));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ITASAlias.MODEL_NAME));
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error beforeModelInitialized", e);
		}
	}
}
