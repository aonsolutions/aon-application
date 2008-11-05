package com.code.aon.ui.commercial.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;


/**
 * Listener Added to the CommercialTrackingController.
 */
public class CommercialTrackingListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(CommercialTrackingListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {
			controller.completeCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {
			controller.refreshActivities();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
		init(controller);		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {
			controller.refreshActivities();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		fillNextAction(controller);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		updateLastValue(controller);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		updateLastValue(controller);
	}

	private void init( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		if ( controller.getLastDate() != null ) {
			ct.setDate( controller.getLastDate() );
		}
		if ( controller.getLastSeller() != null ) {
			ct.setSeller( controller.getLastSeller() );
		}
	}
	
	private void updateLastValue( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		controller.setLastDate( ct.getDate() );
		controller.setLastSeller( ct.getSeller() );
	}

	private void fillNextAction( CommercialTrackingController controller ) {
		if ( controller.isNextAction() ) {
			CommercialTracking ct = (CommercialTracking) controller.getTo();
			CommercialTracking next = ct.getNext();
			next.setSeller( ct.getSeller() );
			next.setTarget( ct.getTarget() );
		}
	}
	
}
