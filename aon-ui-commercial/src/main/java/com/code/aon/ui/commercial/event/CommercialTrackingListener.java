package com.code.aon.ui.commercial.event;

import java.util.List;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
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
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Listener Added to the CommercialTrackingController.
 */
public class CommercialTrackingListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean closed;
	
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
			boolean check = ct.getOffer().getId()!=null; 
			controller.setOfferChecked(check);
			controller.setLaunchSurvey(false);
			updateClosed(controller);
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
			updateProbability(controller);
			controller.setLaunchSurvey(isLaunchSurvey(controller));
			updateClosed(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTrackingController controller = (CommercialTrackingController) event.getController();
		try {		
			updateNextAction(controller);
			if (! this.closed ) {
				controller.setLaunchSurvey(isLaunchSurvey(controller));
			}
			updateClosed(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	private void init( CommercialTrackingController controller ) {
		controller.setNext( new CommercialTracking() );
		controller.setOfferChecked(false);
	}

	private void updateNextAction( CommercialTrackingController controller ) throws ManagerBeanException {
		CommercialTracking ct = (CommercialTracking) controller.getTo();		
		if ( controller.isNextAction() ) {
			CommercialTracking next = controller.getNext();
			ct.setNext( next );
			next.setSeller( ct.getSeller() );	
			next.setProject( ct.getProject() );
			controller.getManagerBean().insertOrUpdate(next);
		} else {
			ct.setNext(null);
		}	
	}

	private void updateProbability( CommercialTrackingController controller ) throws ManagerBeanException {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		Integer probability = ct.getActivity().getProbability();
		if ( probability!=null && probability>0 ) {
			ct.getProject().setProbability(probability);
			BeanManager.getManagerBean(ProjectCommercial.class).update(ct.getProject());
		}
	}
	
	private void updatePreviousAction( CommercialTrackingController controller, CommercialTracking ct ) throws ManagerBeanException {
		CommercialTracking previous = null;
		Criteria criteria = new Criteria();
		String alias = controller.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_NEXT_ID);
		criteria.addEqualExpression(alias, ct.getId());
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		if (! list.isEmpty()) {
			previous = (CommercialTracking) list.get(0);
		}
		controller.setPrevious( previous );
	}
	
	private void updateClosed( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		this.closed = ct.getStatus() == CommercialTrackingStatus.CLOSED; 
	}
	
	private boolean isLaunchSurvey( CommercialTrackingController controller ) {
		CommercialTracking ct = (CommercialTracking) controller.getTo();
		return ct.getStatus()==CommercialTrackingStatus.CLOSED &&
			ct.getActivity().getSurvey()!=null && ct.getActivity().getSurvey().getId()!=null;		
	}
	
	
}
