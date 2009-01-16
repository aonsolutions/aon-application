package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(CommercialTrackingController.class.getName());
	
	private Date lastDate;
	
	private Seller lastSeller;
	
	private boolean nextAction;
	
	private CommercialTracking next;
	
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
	
}