package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PosInvoiceDetailControllerListener extends InvoiceDetailControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@SuppressWarnings("unchecked")
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceDetailController controller = (InvoiceDetailController)event.getController();
			Iterator<ITransferObject> iterator = ((List<ITransferObject>)controller.getModel().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
				invoiceDetail.fillTaxDataInDetail();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		// Redefinido para que no salte el de la clase InvoiceDetailControllerListener 
		// y no se ejecute el refreshEntireInvoice si se recupera un Ticket contabilizado.
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setTaxDataInDetail(true);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.fillTaxDataInDetail();
		invoiceDetail.setTaxDataInDetail(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);
		
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setTaxableBase(CommonUtil.round(invoiceDetail.getTaxableBase()));
	}

}
