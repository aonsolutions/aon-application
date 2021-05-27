package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_ALREADY_RECORDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_DETAIL_NO_WORKPLACE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.ITEM_SERIALIZABLE_WILDCARD_ERROR;

import java.util.List;

import javax.faces.event.AbortProcessingException;
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
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceController invoiceController = (InvoiceController)controller.getMasterController();
		Invoice invoice = controller.getInvoice();
		if (invoice.isRecorded() || invoiceController.checkRecorded(invoice)) {
			invoiceController.refreshEntireInvoice();
			throw new ControllerListenerException(AonUtil.getMessage(FINANCE_INVOICE_ALREADY_RECORDED_ERROR));
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		Invoice invoice = controller.getInvoice();

		controller.setLongDescription(false);
		try {
			invoiceDetail.setProject((invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null);
			invoiceDetail.setSeller((invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null);
			invoiceDetail.setInvestAsset((invoice.getInvestAsset() != null && invoice.getInvestAsset().getId() != null) ? invoice.getInvestAsset() : null);
			invoiceDetail.setLine(calculateNextLine(invoice));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			fillWorkPlace(event, invoiceDetail);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceController invoiceController = (InvoiceController)controller.getMasterController();
		Invoice invoice = controller.getInvoice();
		if (!invoice.isRecorded() && invoiceController.checkRecorded(invoice)) {
			invoiceController.refreshEntireInvoice();
			throw new ControllerListenerException(AonUtil.getMessage(FINANCE_INVOICE_ALREADY_RECORDED_ERROR));
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
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		Invoice invoice = controller.getInvoice();
		try {
			checkSerializable(invoiceDetail);

			invoiceDetail.setProject((invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null);
			invoiceDetail.setSeller((invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null);
			invoiceDetail.setInvestAsset((invoice.getInvestAsset() != null && invoice.getInvestAsset().getId() != null) ? invoice.getInvestAsset() : null);
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			if (invoiceDetail.getInvoice().isSales() || invoiceDetail.getInvoice().isPurchase()) {
				invoiceDetail.setTaxableBase(controller.getTaxableBase());
			}
			if (invoiceDetail.getWorkPlace() == null || invoiceDetail.getWorkPlace().getId() == null) {
				fillWorkPlace(event, invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController detailController = (InvoiceDetailController)event.getController();
		refreshInvoiceData(detailController.getInvoice(), (InvoiceDetail)detailController.getTo());

		InvoiceController invoiceController = (InvoiceController)detailController.getMasterController();
		invoiceController.autoGenerateIncreases();
		invoiceController.autoGenerateFinances();
		invoiceController.resetListTotals();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		try {
			checkSerializable(invoiceDetail);

			invoiceDetail.getInvoice().setStatus(controller.getInvoice().getStatus());
			if (invoiceDetail.getInvoice().isSales() || invoiceDetail.getInvoice().isPurchase()) {
				invoiceDetail.setTaxableBase(controller.getTaxableBase());
			}
			if (invoiceDetail.getWorkPlace() == null || invoiceDetail.getWorkPlace().getId() == null) {
				fillWorkPlace(event, invoiceDetail);
			}
			if (!controller.isEditable(invoiceDetail) && invoiceDetail.getSourceId() < 0) {
				invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
				invoiceDetail.setSourceId(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController detailController = (InvoiceDetailController)event.getController();
		detailController.initializeModel();
		refreshInvoiceData(detailController.getInvoice(), (InvoiceDetail)detailController.getTo());

		InvoiceController invoiceController = (InvoiceController)detailController.getMasterController();
		invoiceController.autoGenerateIncreases();
		invoiceController.autoGenerateFinances();
		invoiceController.resetListTotals();
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNevv()) {
			event.getController().initializeModel();
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.getInvoice().setStatus(controller.getInvoice().getStatus());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)	throws ControllerListenerException {
		InvoiceDetailController detailController = (InvoiceDetailController)event.getController();
		refreshInvoiceData(detailController.getInvoice(), (InvoiceDetail)detailController.getTo());

		InvoiceController invoiceController = (InvoiceController)detailController.getMasterController();
		invoiceController.autoGenerateIncreases();
		invoiceController.autoGenerateFinances();
		invoiceController.resetListTotals();
	}

	private	Integer calculateNextLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void checkSerializable(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getProduct().isSerializable()) {
			if (invoiceDetail.getItem().isWildCard()) {
				throw new AbortProcessingException(AonUtil.getMessage(ITEM_SERIALIZABLE_WILDCARD_ERROR));
			} else if (!invoiceDetail.getItem().getProduct().isLotable() && Math.abs(invoiceDetail.getQuantity()) != 1) {
				invoiceDetail.setQuantity(1);
			}
		}
	}

	private void fillWorkPlace(ControllerEvent event, InvoiceDetail invoiceDetail) throws ManagerBeanException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		Invoice invoice = (Invoice)controller.getInvoice();
		if (invoice.getPosShift() != null && invoice.getPosShift().getId() != null) {
			invoiceDetail.setWorkPlace(invoice.getPosShift().getPos().getWorkPlace());
		} else if (invoiceDetail.getLine() != 1) {
			invoiceDetail.setWorkPlace(obtainPreviousWorkPlace(invoice));
		} else {
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			List<SelectItem> workPlaces = companyCollections.getCurrentUserWorkPlaces();
			if (workPlaces.size() > 0) {
				invoiceDetail.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else {
				throw new AbortProcessingException(AonUtil.getMessage(FINANCE_INVOICE_DETAIL_NO_WORKPLACE_ERROR));
			}
		}
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

	private void refreshInvoiceData(Invoice invoice, InvoiceDetail invoiceDetail) {
		invoice.setTaxableBase(invoiceDetail.getInvoice().getTaxableBase());
		invoice.setVatQuota(invoiceDetail.getInvoice().getVatQuota());
		invoice.setRetentionQuota(invoiceDetail.getInvoice().getRetentionQuota());
		invoice.setTotal(invoiceDetail.getInvoice().getTotal());
		invoice.setService(invoiceDetail.getInvoice().isService());
	}

}
