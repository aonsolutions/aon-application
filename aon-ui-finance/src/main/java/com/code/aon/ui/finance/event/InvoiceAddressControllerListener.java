package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceAddressControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private CompanyUtil companyUtil;

	public CompanyUtil getCompanyUtil() {
		if (companyUtil == null) {
			companyUtil = new CompanyUtil();
		}
		return companyUtil;
	}

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		try {
			if (controller.getModel() != null && controller.getModel().getRowCount() > 0) {
				controller.onSelect(null);
			} else {
				controller.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)	throws ControllerListenerException {
		LinesController invoiceAddressController = (LinesController)event.getController();
		InvoiceAddress to = (InvoiceAddress)invoiceAddressController.getTo();
		to.setStreetType(StreetType.CL);

		Invoice invoice = (Invoice)invoiceAddressController.getMasterController().getTo();
		if (invoice.getPosShift() != null && invoice.getPosShift().getId() != null) {
			to.setGeozone(invoice.getPosShift().getPos().getWorkPlace().getAddress().getGeozone());
		} else {
			try {
				to.setGeozone(getCompanyUtil().getCompanyGeoZone());
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

}