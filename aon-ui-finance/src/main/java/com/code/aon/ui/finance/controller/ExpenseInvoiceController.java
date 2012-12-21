package com.code.aon.ui.finance.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.util.CreditorValidationManager;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ExpenseInvoiceController extends InvoiceController implements IFinanceConstants, IFinanceMessages {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseInvoiceController.class.getName());
	
	private RegistryValidationManager vm;

	public ExpenseInvoiceController() {
		setInvoiceAddressControllerName(EXPENSE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(EXPENSE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(EXPENSE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new CreditorValidationManager(); 
		}
		return vm;
	}

	public void onCreditorChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Creditor creditor = (Creditor)event.getNewValue();
			creditorChanged(creditor);
		} else {
			setAddresses(null);	
		}
	}

	public void creditorChanged(Creditor creditor) throws ManagerBeanException {
		isBlocked(creditor);
		getInvoice().setRegistryName(creditor.getRegistry().getFullName());
		getInvoice().setRegistryDocument(creditor.getRegistry().getDocument());
		getInvoice().setRegistryDocumentType(creditor.getRegistry().getDocumentType());
		getInvoice().setRegistryDocumentCountry(creditor.getRegistry().getDocumentCountry());
		getInvoice().setRegistry(creditor.getRegistry());
		loadAddresses(creditor.getId());

		if (isNew()) {
			ExpenseInvoiceDetailController detailController = (ExpenseInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			InvoiceDetail invoiceDetail = (InvoiceDetail)detailController.getTo();
			if (invoiceDetail.getItem() == null || invoiceDetail.getItem().getId() == null) {
				Item item = obtainCreditorLastExpense(1);
				if (item != null) {
					detailController.itemChanged(item);
				}
			}
		}
	}

	private boolean isBlocked(Creditor creditor) {
		return getRegistryValidationManager().isBlocked(creditor);
	}

	public Item obtainCreditorLastExpense(int line) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_REGISTRY_ID), invoice.getRegistry().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_TYPE), InvoiceType.EXPENSES);
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), line);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ISSUE_DATE), false);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), false);
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			return ((InvoiceDetail)ito).getItem();
		}
		return null;
	}

	public boolean isCreditorWithholding() throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			Creditor creditor = (Creditor)BeanManager.getManagerBean(Creditor.class).get(invoice.getRegistry().getId());
			return (creditor != null) ? creditor.isWithholding() : false;
		}
		return false;
	}

	@Override
	public IAttachment generateReportAttachment( ITransferObject to ) {
		IManagerBean bean = getAttachmentBean();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_INVOICE_ID), ((Invoice)to).getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_MIME_TYPE), MimeType.MIME_PDF);
			List<ITransferObject> list = bean.getList(criteria);
			if (!list.isEmpty()) {
				return (IAttachment)list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error getting invoice pdf file " + to, e);
		}
		return null;
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		IAttachment attachment = generateReportAttachment(to);
		if (attachment == null) {
			attachment = super.generateReportAttachment(to);
		}
		return attachment;
	}		

	@Override
	public SignerController getSignerController() {
		return (SignerController)AonUtil.getRegisteredBean(IFinanceConstants.EXPENSE_INVOICE_SIGNER_CONTROLLER_NAME);
	}

}