package com.code.aon.ui.finance.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		Invoice invoice = controller.getInvoice();

		controller.setLongDescription(false);
		try {
			invoiceDetail.setProject((invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null);
			invoiceDetail.setSeller((invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null);
			invoiceDetail.setLine(calculateNextLine(invoice));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			if (invoiceDetail.getLine() == 1) {
				CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
				List<SelectItem> workPlaces = companyCollections.getCurrentUserWorkPlaces();
				if (workPlaces.size() > 0) {
					invoiceDetail.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
				}
			} else {
				invoiceDetail.setWorkPlace(obtainPreviousWorkPlace(invoice));
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
		invoiceDetail.setTaxableBase(obtainTaxableBase(event, invoiceDetail));
		if (invoiceDetail.getWorkPlace() == null || invoiceDetail.getWorkPlace().getId() == null) {
			fillPosWorkPlace(event, invoiceDetail);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		refreshInvoiceData((InvoiceDetailController)event.getController());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setInvoice(controller.getInvoice());
		invoiceDetail.setTaxableBase(obtainTaxableBase(event, invoiceDetail));
		if (invoiceDetail.getWorkPlace() == null || invoiceDetail.getWorkPlace().getId() == null) {
			fillPosWorkPlace(event, invoiceDetail);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		try {
			//En el before se añade el TO de invoice al invoiceDetail, para que al hacer el updateTotals en el BeanListener no se modifique ninguna otra propiedad.
			//Luego se vuelve a inicializar ese pojo para evitar subpojos nulos.
			controller.getMasterController().getManagerBean().initializePOJO(invoiceDetail.getInvoice());
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage());
		}
		controller.initializeModel();
		refreshInvoiceData(controller);
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNew()) {
			event.getController().initializeModel();
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setInvoice(controller.getInvoice());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)	throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		try {
			//En el before se añade el TO de invoice al invoiceDetail, para que al hacer el updateTotals en el BeanListener no se modifique ninguna otra propiedad.
			//Luego se vuelve a inicializar ese pojo para evitar subpojos nulos.
			controller.getMasterController().getManagerBean().initializePOJO(invoiceDetail.getInvoice());
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage());
		}
		refreshInvoiceData(controller);
	}

	private	Integer calculateNextLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private WorkPlace obtainPreviousWorkPlace(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), false);
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			return ((InvoiceDetail)ito).getWorkPlace();
		}
		return null;
	}

	private double obtainTaxableBase(ControllerEvent event, InvoiceDetail invoiceDetail) {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		return controller.getPriceStrategy().getBasePrice(invoiceDetail);
	}

	private void fillPosWorkPlace(ControllerEvent event, InvoiceDetail invoiceDetail) {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		Invoice invoice = (Invoice)controller.getInvoice();
		if (invoice.getPosShift() != null && invoice.getPosShift().getId() != null) {
			invoiceDetail.setWorkPlace(invoice.getPosShift().getPos().getWorkPlace());
		}
	}

	private void refreshInvoiceData(InvoiceDetailController controller) {
		Invoice invoice = (Invoice)controller.getInvoice();
		invoice.setTaxableBase(((InvoiceDetail)controller.getTo()).getInvoice().getTaxableBase());
		invoice.setVatQuota(((InvoiceDetail)controller.getTo()).getInvoice().getVatQuota());
		invoice.setRetentionQuota(((InvoiceDetail)controller.getTo()).getInvoice().getRetentionQuota());
		invoice.setTotal(((InvoiceDetail)controller.getTo()).getInvoice().getTotal());
		invoice.setService(((InvoiceDetail)controller.getTo()).getInvoice().isService());
	}

}
