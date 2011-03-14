package com.code.aon.ui.fiscal.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.ui.fiscal.controller.VatTaxController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class VatTaxControllerListener extends ControllerAdapter {
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		VatTaxController c = (VatTaxController) event.getController();		
		VatTax vatTax = (VatTax) c.getTo();
		String defYear = c.getFiscalParams().getDefaultYear();
		vatTax.setYear( defYear==null?null:Integer.parseInt(defYear) );
		vatTax.setStatus( VatTaxStatus.PENDING);
		vatTax.setComplementary(false);
		vatTax.setReplacement(false);
		vatTax.setSecurityLevel(SecurityLevel.OFFICIAL);
		vatTax.setTaxRefundRegistry(c.getFiscalParams().isTaxRefundRegistry());
		c.setAnyPreviousAdjust(false);
		c.setScoredInvoices(false);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			VatTaxController c = (VatTaxController) event.getController();
			c.initializeVatTax( true );
			c.refreshPreviousAdjustFlag();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			VatTaxController c = (VatTaxController) event.getController();
			VatTax vatTax = (VatTax) c.getTo();
			c.initializeVatTax( false );
			c.setSelectedTab(vatTax.isFinished()?VatTaxController.PAY_TAB:VatTaxController.DETAIL_TAB);
			c.refreshPreviousAdjustFlag();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			VatTaxController c = (VatTaxController) event.getController();
			VatTax vatTax = (VatTax) event.getController().getTo();
			if (!vatTax.isFinished()) {
				c.saveVatTax();	
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
}
