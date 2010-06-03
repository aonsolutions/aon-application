package com.code.aon.ui.commercial.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.BasicController;

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
			select( event, getPrevious() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onPreviousAction exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onNextAction( ActionEvent event ) {
		try {
			select( event, ((CommercialTracking) getTo()).getNext() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onNextAction exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void select( ActionEvent event, CommercialTracking ct ) throws ManagerBeanException {
		this.clearCriteria();
		Criteria criteria = getCriteria();
		String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_ID);
		criteria.addEqualExpression(alias, ct.getId());
		initializeModel();
		getModel().setRowIndex(0);
		onSelect(event);
	}
	
}