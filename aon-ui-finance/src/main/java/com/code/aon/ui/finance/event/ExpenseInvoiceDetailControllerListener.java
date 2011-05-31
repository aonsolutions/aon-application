package com.code.aon.ui.finance.event;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.ExpenseInvoiceDetailController;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class ExpenseInvoiceDetailControllerListener extends InvoiceDetailControllerListener {

	@Override
	@SuppressWarnings("unchecked")
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceDetailController controller = (InvoiceDetailController)event.getController();
			Iterator<ITransferObject> iterator = ((List<ITransferObject>)controller.getModel().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
				fillTaxDataInDetail(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setTaxDataInDetail(true);
		try {
			controller.loadExpenseItems();
			if (controller.getExpenseItems().size() > 0) {
				SelectItem selectItem = (SelectItem)controller.getExpenseItems().get(0);
				Item item = (Item)selectItem.getValue();
				controller.itemChanged(item);
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);

		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setTaxDataInDetail(true);
		fillTaxDataInDetail(invoiceDetail);
		try {
			controller.loadExpenseItems();
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		beforeSaveExpenseDetail(invoiceDetail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		beforeSaveExpenseDetail(invoiceDetail);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		afterSaveExpenseDetail(invoiceDetail, controller.getVatQuota(invoiceDetail), controller.getRetentionQuota(invoiceDetail));
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		afterSaveExpenseDetail(invoiceDetail, controller.getVatQuota(invoiceDetail), controller.getRetentionQuota(invoiceDetail));
	}

	private void beforeSaveExpenseDetail(InvoiceDetail invoiceDetail) {
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
	}

	private void afterSaveExpenseDetail(InvoiceDetail invoiceDetail, double calculatedVatQuota, double calculatedRetentionQuota) {
		if ((invoiceDetail.getVatQuota() != calculatedVatQuota) || (invoiceDetail.getRetentionQuota() != calculatedRetentionQuota)) {
			String bundle = IFinanceMessages.BUNDLE_KEY;
			String msg = IFinanceMessages.FINANCE_EXPENSE_INVOICE_QUOTA_WARNING;
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			if (invoiceDetail.getVatQuota() != calculatedVatQuota) {
				String taxType = TaxType.VAT.getName(AonUtil.getCurrentLocale());
				String quotaFormatted = formatter.format(calculatedVatQuota);
				AonUtil.addWarningMessage(AonUtil.getMessage(bundle, msg, taxType, quotaFormatted));
			}
			if (invoiceDetail.getRetentionQuota() != calculatedRetentionQuota) {
				String taxType = TaxType.RETENTION.getName(AonUtil.getCurrentLocale());
				String quotaFormatted = formatter.format(calculatedRetentionQuota);
				AonUtil.addWarningMessage(AonUtil.getMessage(bundle, msg, taxType, quotaFormatted));
			}
			AonUtil.addWarningMessageFromBundle(bundle, IFinanceMessages.FINANCE_EXPENSE_INVOICE_CHECK_WARNING);
		}
	}

	private void fillTaxDataInDetail(InvoiceDetail invoiceDetail) throws ControllerListenerException {
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
			Iterator<?> iterator = invoiceTaxBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				InvoiceTax invoiceTax = (InvoiceTax)iterator.next();
				if (TaxType.VAT == invoiceTax.getTaxType()) {
					invoiceDetail.setVatPercent(invoiceTax.getPercentage());
					invoiceDetail.setVatQuota(invoiceTax.getQuota());
				} else if (TaxType.RETENTION == invoiceTax.getTaxType()) {
					invoiceDetail.setRetentionPercent(invoiceTax.getPercentage());
					invoiceDetail.setRetentionQuota(invoiceTax.getQuota());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
