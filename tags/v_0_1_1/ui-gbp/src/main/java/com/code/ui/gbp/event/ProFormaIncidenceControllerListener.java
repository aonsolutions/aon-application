package com.code.ui.gbp.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Incidence;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.IncidenceSource;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;
import com.code.ui.gbp.controller.ProFormaInvoiceController;

public class ProFormaIncidenceControllerListener extends ControllerAdapter {

	private static final String PRO_FORMA_INVOICE_CONTROLLER_NAME = "proForma";
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IGBPAlias.INCIDENCE_SOURCE), IncidenceSource.PRO_FORMA);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.INCIDENCE_INCIDENCE_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Incidence incidence = (Incidence)event.getController().getTo();
			incidence.setSource(IncidenceSource.PRO_FORMA);
			ProFormaInvoiceController proFormaController = (ProFormaInvoiceController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
			ProFormaInvoice proFormaInvoice = (ProFormaInvoice)proFormaController.getTo(); 
			updateProFormaInvoiceStatus(proFormaController, ProFormaInvoiceStatus.INCIDENCES);
			incidence.setCampaign(proFormaInvoice.getCampaign());
			incidence.setSupplier(proFormaInvoice.getSupplier());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			if(event.getController().getModel().getRowCount() == 1){
				ProFormaInvoiceController proFormaController = (ProFormaInvoiceController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
				updateProFormaInvoiceStatus(proFormaController, ProFormaInvoiceStatus.PENDING);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private void updateProFormaInvoiceStatus(ProFormaInvoiceController invoiceController, ProFormaInvoiceStatus status) throws ManagerBeanException {
		((ProFormaInvoice)invoiceController.getTo()).setStatus(status);
		invoiceController.accept(null);
	}
}