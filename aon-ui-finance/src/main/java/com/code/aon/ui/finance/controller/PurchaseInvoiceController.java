package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.ProgressionInvoicingFeedBack;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.income.IncomeInvoicingDAO;
import com.code.aon.finance.invoicing.engine.income.IncomeInvoicingEngine;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.bridge.IncomeTransferManager;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class PurchaseInvoiceController extends InvoiceController implements IFinanceConstants, IFinanceMessages {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseInvoiceController.class.getName());
	
	private RegistryValidationManager vm;
	private IncomeTransferManager incomeTransferManager;
	private boolean showIncomeTransferWindow;

	public PurchaseInvoiceController() {
		setInvoiceAddressControllerName(PURCHASE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(PURCHASE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new SupplierValidationManager(); 
		}
		return vm;
	}
	
	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			isBlocked(supplier); // Saca el mensaje de bloqueo.
			getInvoice().setRegistryName(supplier.getRegistry().getFullName());
			getInvoice().setRegistryDocument(supplier.getRegistry().getDocument());
			getInvoice().setRegistry(supplier.getRegistry());
			loadAddresses(supplier.getId());
		} else {
			setAddresses(null);	
		}
	}

	private boolean isBlocked(Supplier supplier) {
		return getRegistryValidationManager().isBlocked(supplier);
	}

	public IncomeTransferManager getIncomeTransferManager() {
		if (incomeTransferManager == null) {
			incomeTransferManager = new IncomeTransferManager(); 
		}
		return incomeTransferManager;
	}

	public void setIncomeTransferManager(IncomeTransferManager incomeTransferManager) {
		this.incomeTransferManager = incomeTransferManager;
	}

	public boolean isShowIncomeTransferWindow() {
		return showIncomeTransferWindow;
	}

	public void setShowIncomeTransferWindow(boolean value) {
		this.showIncomeTransferWindow = value;
	}
	
	public void onIncomeTransferShow(ActionEvent event) throws ManagerBeanException {
		List<ITransferObject> invoicedIncomeList = new LinkedList<ITransferObject>();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
		criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
			if (!invoicedIncomeList.contains(incomeDetail.getIncome())) {
				invoicedIncomeList.add(incomeDetail.getIncome());
				getIncomeTransferManager().setIncomeRowChecked(incomeDetail.getIncome(), true);
			}
		}

		getIncomeTransferManager().setInvoicedIncomeList(invoicedIncomeList);

		List<ITransferObject> incomeList = new LinkedList<ITransferObject>();
		incomeList.addAll(invoicedIncomeList);
		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		criteria = new Criteria();
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), getInvoice().getRegistry().getId());
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_STATUS), IncomeStatus.PENDING);
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SECURITY_LEVEL), getInvoice().getSecurityLevel());
		criteria.addOrder(incomeBean.getFieldName(IWarehouseAlias.INCOME_ISSUE_TIME));
		criteria.addOrder(incomeBean.getFieldName(IWarehouseAlias.INCOME_SERIES));
		criteria.addOrder(incomeBean.getFieldName(IWarehouseAlias.INCOME_NUMBER));
		incomeList.addAll(incomeBean.getList(criteria));

		getIncomeTransferManager().setIncomeList(incomeList);
	}

	public void onIncomeTransfer(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = getIncomeTransferManager().getInvoicedIncomeList().iterator();
		while (iterator.hasNext()) {
			Income income = (Income)iterator.next();
			if (!getIncomeTransferManager().getCheckedIncome().contains(income)) {
				removeInvoicedIncome(income);
			}
			getIncomeTransferManager().getCheckedIncome().remove(income);
		}

		InvoicingEngineFactory.register(InvoicingEngineFactory.INCOME_ENGINE_KEY, new IncomeInvoicingEngine());
		try {
			IInvoicingEngine engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.INCOME_ENGINE_KEY);
			engine.setInvoicingDAO(new IncomeInvoicingDAO());
			engine.setInvoicingFeedBack(new ProgressionInvoicingFeedBack());
			((IncomeInvoicingEngine)engine).invoiceIncomeList(getInvoice(), getIncomeTransferManager().getCheckedIncome());
		} catch (InvoicingException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}

		IController detailController = FormUtil.getController(IFinanceConstants.PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.onSearch(null);
	}

	private void removeInvoicedIncome(Income income) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
			if (invoiceDetailBean.getList(criteria).iterator().hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)invoiceDetailBean.getList(criteria).iterator().next();
				invoiceDetailBean.remove(invoiceDetail);
			}
		}
	}
	
	@Override
	public IAttachment generateReportAttachment( ITransferObject to ) {
		IManagerBean bean = getAttachmentBean();
		try {
			Criteria criteria = new Criteria();
			String invoiceAlias = bean.getFieldName(IFinanceAlias.INVOICE_ATTACHMENT_INVOICE_ID);
			criteria.addEqualExpression(invoiceAlias, ((Invoice)to).getId());
			String typeAlias;
			typeAlias = bean.getFieldName(IFinanceAlias.INVOICE_ATTACHMENT_MIME_TYPE);
			criteria.addEqualExpression(typeAlias, MimeType.MIME_PDF);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (IAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error getting invoice pdf file " + to, e );
		}
		return null;
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		IAttachment attachment = generateReportAttachment(to);
		if ( attachment == null ) {
			attachment = super.generateReportAttachment(to);
		}
		return attachment;
	}		

	@Override
	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(IFinanceConstants.PURCHASE_INVOICE_SIGNER_CONTROLLER_NAME);
	}

}