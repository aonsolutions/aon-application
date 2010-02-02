package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.finance.controller.SaleInvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			invoiceDetail.setLine(calculateNextLine((Invoice)controller.getMasterController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(event, invoiceDetail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(event, invoiceDetail);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		controller.initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();

		controller.setLongDescription((invoiceDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void obtainTaxableBase(ControllerEvent event, InvoiceDetail invoiceDetail) {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
	}

}