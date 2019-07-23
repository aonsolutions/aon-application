package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.COMMERCIAL_TRACKING_CONTROLLER_NAME;
import static com.code.aon.ui.groupware.controller.IGroupWareConstants.ALARM_CONTROLLER_NAME;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommercialTrackingController.class);
	
	private boolean nextAction;
	
	private CommercialTracking next;
	
	private CommercialTracking previous;
	
	private boolean offerChecked;
	
	private IControllerListener offerFilter;
	
	private boolean launchSurvey;
	
	private String surveyReturnAction;
	
	public boolean isLaunchSurvey() {
		return launchSurvey;
	}

	public void setLaunchSurvey(boolean launchSurvey) {
		this.launchSurvey = launchSurvey;
	}
	
	public String getSurveyReturnAction() {
		return surveyReturnAction;	
	}

	public void setSurveyReturnAction(String surveyReturnAction) {
		this.surveyReturnAction = surveyReturnAction;
	}

	public boolean isOfferChecked() {
		return offerChecked;
	}

	public void setOfferChecked(boolean offerChecked) {
		this.offerChecked = offerChecked;
	}

	public boolean isNextAction() {
		return nextAction;
	}

	public void setNextAction(boolean nextAction) {
		this.nextAction = nextAction;
	}

	public CommercialTracking getNext() {
		return next;
	}

	public void setNext(CommercialTracking next) {
		this.next = ( next.getId() != null ) ? next : null;
		setNextAction( this.next != null );
	}	
	
	public CommercialTracking getPrevious() {
		return previous;
	}

	public void setPrevious(CommercialTracking previous) {
		this.previous = previous;
	}

	public void onNextChanged( ActionEvent event ) {
		if ( this.nextAction ) {
			this.next = new CommercialTracking();
			this.next.setStatus(CommercialTrackingStatus.PENDING);
			CommercialTracking ct = (CommercialTracking) getTo();
			this.next.setSeller(ct.getSeller());
			this.next.setLocation(ct.getLocation());
		} else {
			this.next = null;
		}
	}
	
	public void onPreviousAction( ActionEvent event ) {
		try {
			select( event, getPrevious().getId() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onPreviousAction exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onNextAction( ActionEvent event ) {
		try {
			select( event, ((CommercialTracking) getTo()).getNext().getId() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onNextAction exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void offerChanged( ValueChangeEvent event ) {
		CommercialTracking ct = (CommercialTracking) getTo();
		Boolean value = (Boolean) event.getNewValue();
		if ( value ) {
			if ( ct.getOffer() == null ) {
				ct.setOffer( new Offer() );	
			}
		} else {
			ct.setOffer(null);
		}
	}
	
	public IControllerListener getOfferFilter() {
		if ( this.offerFilter == null ) {
			this.offerFilter = new OfferFilter();
		}
		return this.offerFilter;
	}

	public void onSelectOffer( ActionEvent event ) {
		CommercialTracking ct = (CommercialTracking) getTo();
		Offer offer = ct.getOffer();
		OfferController controller = (OfferController) AonUtil.getRegisteredBean(ICommercialConstants.OFFER_CONTROLLER_NAME);
		try {
			controller.select(event, offer.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectOffer exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		controller.setBackAction(ICommercialConstants.NAVIGATION_COMMERCIAL_TRACKING_FORM);
	}

	@Override
	public Collection<ITransferObject> getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression(getCriteria().getExpression());
		try {
			criteria.addOrder(this.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_SELLER_ID));
			criteria.addOrder(this.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getCollection exception: ", e);
			addMessage(e.getMessage());
		}
		return this.getManagerBean().getList(criteria);
	}

	public void projectChanged( LookupChangeEvent event ) throws ManagerBeanException {
		CommercialTracking ct = (CommercialTracking) getTo(); 
		if ( event.getNewValue() != null ) {
			Seller seller = ((ProjectCommercial) event.getNewValue()).getSeller();
			if ( seller!=null && seller.getId()!=null ) {
				ct.setSeller( seller );
			}
		} else {
			IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
			ct.setSeller( (Seller) sellerBean.createNewTo() );			
		}
	}

	public void onNewAlarm( ActionEvent event ) {
		AlarmController controller = (AlarmController) AonUtil.getRegisteredBean(ALARM_CONTROLLER_NAME);
		controller.setShowNewAlarmWindow(true);
		controller.onReset(event);
		CommercialTracking ct = (CommercialTracking) getTo();
		Alarm alarm = (Alarm) controller.getTo();
		alarm.setSource(AlarmSource.COMMERCIAL_TRACKING);
		alarm.setSourceId(ct.getId());
		alarm.setDescription(ct.getComments());
	}
	
	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		if ( isLaunchSurvey() ) {
			AonUtil.actionListener("#{communicationCenter.onStartSurveyFromProject}", event);
		}		
	}
	
	public void accepAndSurvey(ActionEvent event) {
		setSurveyReturnAction(formAction());
		accept(event);
	}
	
	public String saveAction() {
		boolean launchNow = isLaunchSurvey();
		setLaunchSurvey(false);		
		if ( launchNow ) {
			return ICommercialConstants.NAVIGATION_COMMUNICATION_CENTER_RESPONSE;
		}
		return getSurveyReturnAction();
	}	
	
	private static Criteria getCriteria( IManagerBean bean, CommercialTracking ct ) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ALARM_SOURCE), AlarmSource.COMMERCIAL_TRACKING);
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ALARM_SOURCE_ID), ct.getId());
    	criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.ALARM_STATUS), AlarmStatus.FINISHED);
    	User user = UserUtils.getInstance().getLoggedUser();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ALARM_USER_ID), user.getId());
    	String dateAlias = bean.getFieldName(IEntityAlias.ALARM_ALARM_DATE);
    	criteria.addLessThanOrEqualExpression(dateAlias, new Date());
       	criteria.addOrder(dateAlias, false);	
       	return criteria;
	}
	
	public static boolean isCurrentHasAlarm( CommercialTracking ct ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Alarm.class);
    	Criteria criteria = getCriteria(bean, ct);
    	return bean.getCount(criteria) > 0;
	}
	
	public boolean isCurrentHasAlarm() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			CommercialTracking ct = (CommercialTracking) getModel().getRowData();
			return isCurrentHasAlarm(ct);
		}
		return false;
	}

	public boolean isHasAlarm() throws ManagerBeanException {
		return isCurrentHasAlarm( (CommercialTracking) getTo() );
	}
	
	public void onSelectAlarm( ActionEvent event ) {
		AlarmController controller = (AlarmController) AonUtil.getRegisteredBean(ALARM_CONTROLLER_NAME);
        try {
        	CommercialTracking ct = (CommercialTracking) getSelectedTO();
        	Criteria criteria = getCriteria(controller.getManagerBean(), ct);
        	List<ITransferObject> list = controller.getManagerBean().getList(criteria);
        	if (! list.isEmpty() ) {
        		Alarm alarm = (Alarm) list.get(0);
                controller.select(event, alarm);	
                controller.setBackAction(COMMERCIAL_TRACKING_CONTROLLER_NAME + "_form");
        	}
        } catch (ManagerBeanException e) {
        	LOGGER.error( e.getMessage(), e );
            throw new AbortProcessingException(e.getMessage(), e);
        }			
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		if ( getBackAction() != null ) {
			onBackActionListener(event);
		}
	}



	private static class OfferFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			CommercialTracking ct = (CommercialTracking) FormUtil.getController(COMMERCIAL_TRACKING_CONTROLLER_NAME).getTo();
			try {					
				controller.clearCriteria();
				if ( ct.getSeller().getId() != null ) {
					String alias = controller.getFieldName(IEntityAlias.OFFER_SELLER_ID);
					controller.getCriteria().addEqualExpression(alias, ct.getSeller().getId());
				}
				if ( ct.getProject().getTarget().getId() != null ) {
					String alias = controller.getFieldName(IEntityAlias.OFFER_TARGET_ID);
					controller.getCriteria().addEqualExpression(alias, ct.getProject().getTarget().getId());
				}						
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering offer", e);
			}
		}
		
	}
	
}