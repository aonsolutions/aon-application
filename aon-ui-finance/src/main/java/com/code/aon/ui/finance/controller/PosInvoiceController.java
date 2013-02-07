package com.code.aon.ui.finance.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {

	public PosInvoiceController() {
		setInvoiceAddressControllerName(POS_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(POS_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(POS_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	public void generateCashFinance(ActionEvent event) {
		generatePosFinance(PayMethodType.CASH_BASIS);
	}

	public void generateCardFinance(ActionEvent event) {
		generatePosFinance(PayMethodType.CREDIT_CARD);
	}

	private void generatePosFinance(PayMethodType payMethodType) {
		Invoice invoice = getInvoice();
		try {
			double totalAmount = CommonUtil.round(getToInvoiceTotalPrice() - getToInvoiceFinanceTotal());
			if (totalAmount != 0) {
				PayMethod payMethod = null;
				IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), payMethodType);
				for (ITransferObject ito : payMethodBean.getList(criteria)) {
					payMethod = (PayMethod)ito;
					break;
				}

				Finance finance = getFinanceGenerator().createFinance(invoice, invoice.getIssueDate(), payMethod, totalAmount, null, null);
				BeanManager.getManagerBean(Finance.class).insert(finance);
			}

			IController invoiceFinanceController = FormUtil.getController(getInvoiceFinanceControllerName());
			invoiceFinanceController.onSearch(null);
		} catch (ManagerBeanException ex) {
			String msg = AonUtil.getMessage(BUNDLE_KEY, GENERATE_FINANCES_ERROR_KEY) + ". " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

}