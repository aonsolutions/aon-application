package com.code.aon.ui.form.listener;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * LinesControllerListener is a listener that must be registered in <code>faces-bean-config.xml</code> asociated
 * with the master Controller.
 * It handles the relation between master and child controllers.
 */
public class LinesControllerListener extends MasterControllerListener {
	
	/**
	 * Gets the lines(child) controller.
	 * 
	 * @return the lines controller
	 */
	protected LinesController getLinesController() {
		return (LinesController) getDetailController();
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.listener.MasterControllerListener#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			updateDetailCriteria( event.getController(), false );
			getLinesController().saveModel( event.getController().getTo() );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.listener.MasterControllerListener#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		getLinesController().initModel();
		super.afterBeanCreated(event);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.listener.MasterControllerListener#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		getLinesController().initModel();
		super.afterBeanSelected(event);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {		
			getLinesController().deleteOrphans();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
