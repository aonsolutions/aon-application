package com.code.aon.ui.fiscal.event;


import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.ui.fiscal.controller.VatTaxController;
import com.code.aon.ui.fiscal.controller.VatTaxDeclarationController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class VatTaxDeclarationControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		VatTaxController master = (VatTaxController) c.getMasterController();
		VatTax vatTax = (VatTax) master.getTo();
		VatTaxDeclaration to = (VatTaxDeclaration) c.getTo();
		if (vatTax.isTaxRefundRegistry()) {
			to.setCompensable(false);
		}
		to.setPercent( getPercent(c) );
		c.calculate(to);
		c.setFileOutput(null);
	}

	private double getPercent(VatTaxDeclarationController master) {
		List<ITransferObject> list = master.getWrappedList();
		double percent = 100;
		for (ITransferObject to:list) {
			VatTaxDeclaration d = (VatTaxDeclaration) to;
			percent = CommonUtil.round(percent - d.getPercent());
		}
		return percent;
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		c.setFileOutput(null);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		if (c.isAdditionalDataDefined()) {
			c.onRemoveAdditionalData(null);
		}
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		c.setFileOutput(null);
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		VatTaxDeclarationController c = (VatTaxDeclarationController) event.getController();
		c.setFileOutput(null);
	}

}
