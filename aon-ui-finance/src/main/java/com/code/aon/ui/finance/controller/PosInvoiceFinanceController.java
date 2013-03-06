package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.util.AonUtil;

public class PosInvoiceFinanceController extends InvoiceFinanceController {

	private List<Finance> finances;

	public List<Finance> getFinances() {
		return finances;
	}

	public void setFinances(List<Finance> finances) {
		this.finances = finances;
	}

	public void resetFinances() {
		setFinances(new LinkedList<Finance>());
	}

	public int getFinancesCount() {
		return getFinances().size();
	}

	public Finance getFirstFinance() {
		return getFinances().get(0);
	}

	public Finance getLastFinance() {
		return getFinances().get(getFinancesCount()-1);
	}

	public void onNewFinance(ActionEvent event) {
		InvoiceController invoiceController = (InvoiceController)getMasterController();
		Invoice invoice = (Invoice)invoiceController.getTo();

		Finance finance = new Finance();
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			try {
				RegistryPayMethod payMethod = invoice.getRegistry().getPayMethod();
				if (payMethod != null) {
					finance.setPayMethod(payMethod.getPayment());
				}
			} catch (ManagerBeanException ex) {
				String msg = "Error al obtener la Forma de Pago del Cliente";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		finance.setAmount(CommonUtil.round(invoiceController.getPendingAmount() - getFinancesAmount()));
		getFinances().add(finance);
	}

	public void onRemoveFinance(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int financeIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("posFinanceIndex"));
        getFinances().remove(financeIndex);
	}

	public boolean isFinancesOk() {
		return (isFinancesPayMethodOk()) ? isFinancesCardOk() : false;
	}

	public boolean isFinancesPayMethodOk() {
		for (Finance finance : getFinances()) {
			PayMethod payMethod = finance.getPayMethod();
			if (payMethod == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isFinancesCardOk() {
		InvoiceController invoiceController = (InvoiceController)getMasterController();
		return (invoiceController.getPendingAmount() >= getFinancesCardAmount());
	}

	public boolean isFinancesCashAlready() {
		if (getFinances().size() > 1) {
			for (Finance finance : getFinances().subList(0, getFinances().size()-1)) {
				if (finance.getPayMethod() != null && finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
					return true;
				}
			}
		}
		return false;
	}

	public double getFinancesAmount() {
		double amount = 0;
		for (Finance finance : getFinances()) {
			amount += CommonUtil.round(finance.getAmount());
		}
		return CommonUtil.round(amount);
	}

	public double getFinancesCardAmount() {
		double amount = 0;
		for (Finance finance : getFinances()) {
			PayMethod payMethod = finance.getPayMethod();
			if (payMethod != null && (payMethod.getType() == PayMethodType.CREDIT_CARD || payMethod.getType() == PayMethodType.DEBIT_CARD)) {
				amount += CommonUtil.round(finance.getAmount());
			}
		}
		return CommonUtil.round(amount);
	}

	public double getFinancesCashAmount() {
		double amount = 0;
		for (Finance finance : getFinances()) {
			PayMethod payMethod = finance.getPayMethod();
			if (payMethod != null && payMethod.getType() == PayMethodType.CASH_BASIS) {
				amount += CommonUtil.round(finance.getAmount());
			}
		}
		return CommonUtil.round(amount);
	}

	public double getFinancesCashChange() {
		InvoiceController invoiceController = (InvoiceController)getMasterController();
		double cashAmount = getFinancesCashAmount();
		if (cashAmount > 0) {
			double change = CommonUtil.round(getFinancesAmount() - invoiceController.getPendingAmount());
			return (change > 0) ? change : 0;
		}
		return 0;
	}

}
