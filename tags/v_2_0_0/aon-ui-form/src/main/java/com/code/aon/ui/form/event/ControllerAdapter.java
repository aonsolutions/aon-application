package com.code.aon.ui.form.event;

import com.code.aon.ui.form.IController;

/**
 * The Class ControllerAdapter.
 * 
 * @author Consulting & Development. ecastellano - 06/10/2006
 */
public class ControllerAdapter implements IControllerListener {

	/** The controller. */
	private IController controller;
	
	/**
	 * Gets the controller.
	 * 
	 * @return the controller
	 */
	public IController getController() {
		return controller;
	}

	/**
	 * Sets the controller.
	 * 
	 * @param controller the controller
	 */
	public void setController(IController controller) {
		this.controller = controller;
	}

	/**
	 * After bean added.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean canceled.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanCanceled(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean created.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean removed.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean reset.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanReset(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean selected.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After bean updated.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean added.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean canceled.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanCanceled(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanCanceled(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean created.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean removed.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean reset.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanReset(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean selected.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before bean updated.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * After model initialized.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#afterModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
	}

	/**
	 * Before model initialized.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.IControllerListener#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
	}

}
