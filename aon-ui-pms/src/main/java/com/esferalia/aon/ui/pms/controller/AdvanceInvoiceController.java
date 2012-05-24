package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.invoicing.AdvanceInvoiceTo;
import com.esferalia.aon.pms.invoicing.AdvanceInvoicing;
import com.esferalia.aon.ui.pms.event.AdvanceInvoiceSearchListener;

public class AdvanceInvoiceController extends BasicController{

	private Date advanceDate;
	private double advancePercent;
	private PayMethod advancePayMethod;
	private RegistryBank advanceBank;
	private int advanceDaysToPayment;
	private boolean showInvoiceWindow;

	public Date getAdvanceDate() {
		return advanceDate;
	}
	public void setAdvanceDate(Date advanceDate) {
		this.advanceDate = advanceDate;
	}

	public double getAdvancePercent() {
		return advancePercent;
	}
	public void setAdvancePercent(double advancePercent) {
		this.advancePercent = advancePercent;
	}
	
	public PayMethod getAdvancePayMethod() {
		return advancePayMethod;
	}
	public void setAdvancePayMethod(PayMethod advancePayMethod) {
		this.advancePayMethod = advancePayMethod;
	}

	public RegistryBank getAdvanceBank() {
		return advanceBank;
	}
	public void setAdvanceBank(RegistryBank advanceBank) {
		this.advanceBank = advanceBank;
	}

	public int getAdvanceDaysToPayment() {
		return advanceDaysToPayment;
	}
	public void setAdvanceDaysToPayment(int advanceDaysToPayment) {
		this.advanceDaysToPayment = advanceDaysToPayment;
	}

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}

	public void setShowInvoiceWindow(boolean showInvoiceWindow) {
		this.showInvoiceWindow = showInvoiceWindow;
	}

	public void onAdvanceInvoiceShow(ActionEvent event) throws ManagerBeanException {
		setAdvanceDate(new Date());
		setAdvancePercent(0);
		setAdvancePayMethod(null);
		setAdvanceBank(null);
		setAdvanceDaysToPayment(0);
	}
	
	public List<SelectItem> getAgencyPayMethods() throws ManagerBeanException{
		List<PayMethodType> directPayMethods = new LinkedList<PayMethodType>();
		directPayMethods.add(PayMethodType.CASH_BASIS);
		directPayMethods.add(PayMethodType.DEBIT_CARD);
		directPayMethods.add(PayMethodType.CREDIT_CARD);
		directPayMethods.add(PayMethodType.BANK_TRANSFER);
		directPayMethods.add(PayMethodType.CHEQUE);

		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getInExpression(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), directPayMethods));
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod payMethod = (PayMethod)ito;
			SelectItem item = new SelectItem(payMethod, payMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}

	public boolean isBankRequired() {
		return (advancePayMethod != null && (advancePayMethod.getType() == PayMethodType.BANK_TRANSFER || advancePayMethod.getType() == PayMethodType.CHEQUE)); 		 
	}
	
	public Date getAdvancePaymentDate() {
		return DateUtils.addDays(getAdvanceDate(), getAdvanceDaysToPayment());
	}
	
	public void onAdvanceInvoice(ActionEvent event) {
		try {
			AdvanceInvoiceTo advanceInvoiceTo = new AdvanceInvoiceTo();
			AdvanceInvoiceSearchListener search = (AdvanceInvoiceSearchListener) AonUtil.getRegisteredBean(IPmsConstants.ADVANCE_INVOICE_CONTROLLER_SEARCH_LISTENER);
			advanceInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
			advanceInvoiceTo.setIssueDate(getAdvanceDate());
			advanceInvoiceTo.setPercent(getAdvancePercent());
			advanceInvoiceTo.setPayMethod(getAdvancePayMethod());
			advanceInvoiceTo.setRegistryBank(getAdvanceBank());
			advanceInvoiceTo.setFinanceDate(getAdvancePaymentDate());

			AdvanceInvoicing advanceInvoicing = new AdvanceInvoicing();
			int count = advanceInvoicing.invoice(advanceInvoiceTo, getCheckedReservations());

			clearCheckedReservations();
			onSearch(event);
			String msg = "Facturas de Anticipos generadas: " + count; 
			AonUtil.addInfoMessage(msg);
		} catch (ManagerBeanException ex) {
			String msg = "Se produjo un error al generar las Facturas de Anticipos. [" + ex.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	

	private ArrayList<Integer> checks = new ArrayList<Integer>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		ProjectReservation to = (ProjectReservation)model.getRowData();
		return checks.contains(to.getId());
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			ProjectReservation to = (ProjectReservation)model.getRowData();
			if (!checks.contains(to.getId())) {
				checks.add(to.getId());
			}
		} else {
			ProjectReservation to = (ProjectReservation)model.getRowData();
			if (checks.contains(to.getId())) {
				checks.remove(to.getId());
			}
		}
	}

	public ArrayList<Integer> getCheckedReservations() {
		return checks;
	}

	public void clearCheckedReservations() {
		checks = new ArrayList<Integer>();
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			ProjectReservation reservation = (ProjectReservation)ito;
			if (!checks.contains(reservation.getId())) {
				checks.add(reservation.getId());
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedReservations();
	}

	public int getCheckedCount() {
		return getCheckedReservations().size();
	}

}