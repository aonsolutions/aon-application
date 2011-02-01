package com.code.aon.ui.fiscal.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.ui.fiscal.controller.RentingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RentingControllerListener extends ControllerAdapter {
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();		
		Renting renting = (Renting) c.getTo();
		String defYear = c.getFiscalParams().getDefaultYear();
		renting.setYear( defYear==null?null:Integer.parseInt(defYear) );
		renting.setAdministration( c.getFiscalParams().getDefaultAdministration());
		renting.setStatus( RentingStatus.PENDING);
		renting.setComplementary(false);
		renting.setReplacement(false);
		renting.setSecurityLevel(SecurityLevel.OFFICIAL);
		c.setFileOutput(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			RentingController c = (RentingController) event.getController();
			c.initializeRenting( );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			RentingController c = (RentingController) event.getController();
			c.initializeRentingDetail( );
			c.setFileOutput(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		RentingController c = (RentingController) event.getController();
		c.setFileOutput(null);
	}
}
