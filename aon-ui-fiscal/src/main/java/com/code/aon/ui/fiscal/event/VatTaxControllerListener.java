package com.code.aon.ui.fiscal.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.ui.fiscal.controller.VatTaxController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class VatTaxControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		VatTaxController c = (VatTaxController) event.getController();		
		VatTax vatTax = (VatTax) c.getTo();
		String defYear = c.getFiscalParams().getDefaultYear();
		vatTax.setYear(StringUtils.isEmpty(defYear)?null:Integer.parseInt(defYear) );
		vatTax.setStatus( VatTaxStatus.PENDING);
		vatTax.setComplementary(false);
		vatTax.setReplacement(false);
		vatTax.setSecurityLevel(SecurityLevel.OFFICIAL);
		vatTax.setTaxRefundRegistry(c.getFiscalParams().isTaxRefundRegistry());
		vatTax.setProrata(100);
		c.setAnyPreviousAdjust(false);
		c.setScoredInvoices(false);
		c.setDeclaredPanelVisible(false);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			VatTaxController c = (VatTaxController) event.getController();
			c.initializeVatTax( true );
			c.refreshPreviousAdjustFlag();
			c.setDetailNew(false);
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
			c.setDeclaredPanelVisible(false);
			c.setDetailNew(false);
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
			c.setDetailNew(false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		VatTaxController c = (VatTaxController) event.getController();
		c.setDetailNew(false);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		VatTaxController c = (VatTaxController) event.getController();
		c.setDetailNew(false);
	}
}
