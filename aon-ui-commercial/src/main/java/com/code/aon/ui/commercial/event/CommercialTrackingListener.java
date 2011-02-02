package com.code.aon.ui.commercial.event;

import java.util.List;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
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
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		try {
	    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
			collections.refreshActivities();
			updatePreviousAction( controller, ct );
			controller.setNext( ct.getNext() );
			boolean check = (ct.getOffer().getId() !=null); 
			controller.setOfferChecked(check);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {		
			updateNextAction(controller);
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
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {		
			updateNextAction(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
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
		controller.setOfferChecked(false);
	}
	
	private void updateLastValue( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		controller.setLastDate( ct.getDate() );
		controller.setLastSeller( ct.getSeller() );
	}

	private void updateNextAction( CommercialTrackingController controller ) throws ManagerBeanException {
		if ( controller.isNextAction() ) {
			CommercialTracking ct = (CommercialTracking) controller.getTo();
			CommercialTracking next = controller.getNext();
			ct.setNext( next );
			next.setSeller( ct.getSeller() );	
			next.setTarget( ct.getTarget() );
			controller.getManagerBean().insertOrUpdate(next);
		} else {
			CommercialTracking ct = (CommercialTracking) controller.getTo();
			ct.setNext(null);
		}
	}
	
	private void updatePreviousAction( CommercialTrackingController controller, CommercialTracking ct ) throws ManagerBeanException {
		CommercialTracking previous = null;
		Criteria criteria = new Criteria();
		String alias = controller.getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_NEXT_ID);
		criteria.addEqualExpression(alias, ct.getId());
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		if (! list.isEmpty()) {
			previous = (CommercialTracking) list.get(0);
		}
		controller.setPrevious( previous );
	}
	
}
