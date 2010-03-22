package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;


/**
 * Listener Added to the CommercialTrackingController.
 */
public class CommercialTrackingListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {
	    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
			collections.refreshActivities();
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
	    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
			collections.refreshActivities();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		controller.setNext( ct.getNext() );
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {		
			fillNextAction(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
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
		controller.setNext( new CommercialTracking() );
	}
	
	private void updateLastValue( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		controller.setLastDate( ct.getDate() );
		controller.setLastSeller( ct.getSeller() );
	}

	private void fillNextAction( CommercialTrackingController controller ) throws ManagerBeanException {
		if ( controller.isNextAction() ) {
			CommercialTracking ct = (CommercialTracking) controller.getTo();
			CommercialTracking next = controller.getNext();
			ct.setNext( next );
			next.setSeller( ct.getSeller() );
			next.setTarget( ct.getTarget() );
			controller.getManagerBean().insert(next);
		}
	}
	
}
