package com.code.aon.ui.commercial.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommercialTrackingController.class);
	
	private Date lastDate;
	
	private Seller lastSeller;
	
	private boolean nextAction;
	
	private CommercialTracking next;
	
	private CommercialTracking previous;
	
	private boolean offerChecked;
	
	private IControllerListener offerFilter;
	
	public boolean isOfferChecked() {
		return offerChecked;
	}

	public void setOfferChecked(boolean offerChecked) {
		this.offerChecked = offerChecked;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Seller getLastSeller() {
		return lastSeller;
	}

	public void setLastSeller(Seller lastSeller) {
		this.lastSeller = lastSeller;
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

	public void nextActionChanged( ValueChangeEvent event ) {
		Boolean value = (Boolean) event.getNewValue();
		if ( value ) {
			this.next = new CommercialTracking();
			if ( getLastDate() != null ) {
				this.next.setDate( getLastDate() );	
			}
			this.next.setStatus(CommercialTrackingStatus.PENDING);
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
			this.offerFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					CommercialTracking ct = (CommercialTracking) getTo();
					try {					
						if ( ct.getSeller().getId() != null ) {
							String alias = controller.getFieldName(ICommercialAlias.OFFER_SELLER_ID);
							controller.getCriteria().addEqualExpression(alias, ct.getSeller().getId());
						}
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering offer", e);
					}
				}
			};
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
			criteria.addOrder(this.getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_SELLER_ID));
			criteria.addOrder(this.getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_DATE));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getCollection exception: ", e);
			addMessage(e.getMessage());
		}
		return this.getManagerBean().getList(criteria);
	}
	
}