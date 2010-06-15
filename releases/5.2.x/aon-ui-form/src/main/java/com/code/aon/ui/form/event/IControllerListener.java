/*
 * Created on 07-abr-2005
 *
 */
package com.code.aon.ui.form.event;

import com.code.aon.ui.form.IController;

/**
 * The Interface IControllerListener.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-abr-2005
 * @since 1.0
 */
public interface IControllerListener {

	/**
	 * Sets the controller.
	 * 
	 * @param controller the controller
	 */
	void setController( IController controller );
	
	/**
	 * Gets the controller.
	 * 
	 * @return the controller
	 */
	IController getController();
	
	/**
	 * Método invocado cuando se añade un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanAdded(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado cuando se añade un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado cuando se elimina un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se elimina un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se modifica un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se modifica un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se cree un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanCreated(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se crea un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se inicialize un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanReset(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se selecciona un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanReset(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se selecciona un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanSelected(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se selecciona un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se cancela la edición de un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado cuando se cancela la edición de un elemento.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeBeanCanceled(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado cuando se inicializa el modelode datos.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterModelInitialized(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado cuando se inicializa el modelode datos.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se procede a la búsqueda.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterModelSearched(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado después de realizar una búsqueda
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeModelSearched(ControllerEvent event) throws ControllerListenerException;

	/**
	 * Método invocado cuando se procede a la edición de la búsqueda.
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void afterEditSearch(ControllerEvent event) throws ControllerListenerException;
	
	/**
	 * Método invocado después de realizar la edición de la búsqueda
	 * 
	 * @param event ChangeEvent Evento propagado.
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	void beforeEditSearch(ControllerEvent event) throws ControllerListenerException;
}
