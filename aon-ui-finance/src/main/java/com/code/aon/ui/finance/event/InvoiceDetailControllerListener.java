package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		Invoice invoice = (Invoice)controller.getMasterController().getTo();

		controller.setLongDescription(false);
		try {
			invoiceDetail.setProject((invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null);
			//invoiceDetail.setSeller((invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null);
			invoiceDetail.setLine(calculateNextLine(invoice));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);

			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			if (companyCollections.getCurrentUserWorkPlacesCount() == 1) {
				invoiceDetail.setWorkPlace((WorkPlace)companyCollections.getCurrentUserWorkPlaces().get(0).getValue());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();

		controller.setLongDescription((invoiceDetail.getDescription().length() > 64) ? true : false);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		obtainTaxableBase(event, invoiceDetail);
		obtainWorkPlace(event, invoiceDetail);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		refreshInvoiceData((InvoiceDetailController)event.getController());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(event, invoiceDetail);
		obtainWorkPlace(event, invoiceDetail);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
		refreshInvoiceData((InvoiceDetailController)event.getController());
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNew()) {
			event.getController().initializeModel();
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)	throws ControllerListenerException {
		refreshInvoiceData((InvoiceDetailController)event.getController());
	}

	private	Integer calculateNextLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void obtainTaxableBase(ControllerEvent event, InvoiceDetail invoiceDetail) {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
	}

	private void obtainWorkPlace(ControllerEvent event, InvoiceDetail invoiceDetail) {
		if (invoiceDetail.getWorkPlace() == null || invoiceDetail.getWorkPlace().getId() == null) {
			InvoiceDetailController controller = (InvoiceDetailController)event.getController();
			InvoiceController invoiceController = (InvoiceController)controller.getMasterController();
			Invoice invoice = (Invoice)invoiceController.getTo();
			if (invoice.getPos() != null && invoice.getPos().getId() != null) {
				invoiceDetail.setWorkPlace(invoice.getPos().getWorkPlace());
			}
		}
	}

	private void refreshInvoiceData(InvoiceDetailController controller) {
		InvoiceController invoiceController = (InvoiceController)controller.getMasterController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		invoice.setTaxableBase(((InvoiceDetail)controller.getTo()).getInvoice().getTaxableBase());
		invoice.setVatQuota(((InvoiceDetail)controller.getTo()).getInvoice().getVatQuota());
		invoice.setRetentionQuota(((InvoiceDetail)controller.getTo()).getInvoice().getRetentionQuota());
		invoice.setTotal(((InvoiceDetail)controller.getTo()).getInvoice().getTotal());
		invoice.setService(((InvoiceDetail)controller.getTo()).getInvoice().isService());
	}

}
