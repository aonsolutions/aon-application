package com.code.aon.ui.fiscal.event;

import java.util.Date;
import java.util.List;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.ui.fiscal.controller.batch.Batchable;
import com.code.aon.ui.fiscal.controller.batch.FiscalBatchController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FiscalBatchControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FiscalBatchController c = (FiscalBatchController) event.getController();
		FiscalBatch fiscalBatch = (FiscalBatch) c.getTo();
		fiscalBatch.setDate( new Date() );
		fiscalBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FiscalBatchController c = (FiscalBatchController) event.getController();
		c.initialize();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FiscalBatchController c = (FiscalBatchController) event.getController();
		c.initialize();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		FiscalBatchController c = (FiscalBatchController) event.getController();
		List<Batchable> detailsChecked = (List<Batchable>) c.getDetails().getWrappedData();
		c.setDetailsChecked(detailsChecked);
		c.onRemoveSelected(null);
	}
}
