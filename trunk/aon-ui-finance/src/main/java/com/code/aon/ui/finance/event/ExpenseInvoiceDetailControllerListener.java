package com.code.aon.ui.finance.event;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.ExpenseInvoiceController;
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
				invoiceDetail.fillTaxDataInDetail();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		controller.setTotalChanged(0);
		try {
			Item item = ((ExpenseInvoiceController)controller.getMasterController()).obtainCreditorLastExpense(((InvoiceDetail)controller.getTo()).getLine());
			if (item != null) {
				controller.itemChanged(item);
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setTaxDataInDetail(true);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);

		ExpenseInvoiceDetailController controller = (ExpenseInvoiceDetailController)event.getController();
		controller.setTotalChanged(0);

		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		invoiceDetail.setTaxDataInDetail(true);
		invoiceDetail.fillTaxDataInDetail();
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		beforeSaveExpenseDetail((ExpenseInvoiceDetailController)event.getController());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		afterSaveExpenseDetail((ExpenseInvoiceDetailController)event.getController());
		super.afterBeanAdded(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeSaveExpenseDetail((ExpenseInvoiceDetailController)event.getController());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		afterSaveExpenseDetail((ExpenseInvoiceDetailController)event.getController());
		super.afterBeanUpdated(event);
	}

	private void beforeSaveExpenseDetail(ExpenseInvoiceDetailController controller) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		if (invoiceDetail.getTaxableBase() != 0 && invoiceDetail.getVatQuota() == 0) {
			controller.taxableBaseChanged(invoiceDetail);
		} else if (invoiceDetail.getTaxableBase() == 0 && controller.getTotalChanged() != 0) {
			controller.totalChanged(invoiceDetail);
		}
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		if (invoiceDetail.getVatQuota() == 0) {
			invoiceDetail.setVatPercent(0);
		}
		if (invoiceDetail.getRetentionQuota() == 0) {
			invoiceDetail.setRetentionPercent(0);
		}
	}

	private void afterSaveExpenseDetail(ExpenseInvoiceDetailController controller) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		double calculatedVatQuota = controller.getVatQuota(invoiceDetail);
		double calculatedRetentionQuota = controller.getRetentionQuota(invoiceDetail);
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

}
