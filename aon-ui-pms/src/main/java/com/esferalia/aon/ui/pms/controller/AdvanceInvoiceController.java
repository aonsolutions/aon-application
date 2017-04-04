package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.invoicing.AdvanceInvoiceTo;
import com.esferalia.aon.pms.invoicing.AdvanceInvoicing;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.event.AdvanceInvoiceSearchListener;

public class AdvanceInvoiceController extends BasicController{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date advanceDate;
	private boolean percent;
	private double advancePercent;
	private double advanceAmount;
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

	public boolean getPercent() {
		return percent;
	}
	public void setPercent(boolean percent) {
		this.percent = percent;
	}
	
	public double getAdvancePercent() {
		return advancePercent;
	}
	public void setAdvancePercent(double advancePercent) {
		this.advancePercent = advancePercent;
	}
	
	public double getAdvanceAmount() {
		return advanceAmount;
	}
	public void setAdvanceAmount(double advanceAmount) {
		this.advanceAmount = advanceAmount;
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
		if (obtainAdvanceItem() == null) {
			setShowInvoiceWindow(false);
			String msg = "No se puede Facturar. No esta definido el Producto para Anticipos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setAdvanceDate(new Date());
		setPercent(false);
		setAdvancePercent(0);
		setAdvanceAmount(0);
		setAdvancePayMethod(null);
		setAdvanceBank(null);
		setAdvanceDaysToPayment(0);
	}

	public void onAdvancePercentMode(ActionEvent event) {
		setPercent(true);
		setAdvanceAmount(0);
	}

	public void onAdvanceAmountMode(ActionEvent event) {
		setPercent(false);
		setAdvancePercent(0);
	}

	public boolean isBankRequired() {
		return (advancePayMethod != null && (advancePayMethod.getType() == PayMethodType.BANK_TRANSFER || advancePayMethod.getType() == PayMethodType.CHEQUE)); 		 
	}
	
	public Date getAdvancePaymentDate() {
		return DateUtils.addDays(getAdvanceDate(), getAdvanceDaysToPayment());
	}
	
	public void onAdvanceInvoice(ActionEvent event) {
		try {
			if (validateAdvanceInvoice()) {
				AdvanceInvoiceTo advanceInvoiceTo = new AdvanceInvoiceTo();
				AdvanceInvoiceSearchListener search = (AdvanceInvoiceSearchListener) AonUtil.getRegisteredBean(IPmsConstants.ADVANCE_INVOICE_SEARCH_LISTENER_NAME);
				advanceInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
				advanceInvoiceTo.setIssueDate(getAdvanceDate());
				advanceInvoiceTo.setItem(obtainAdvanceItem());
				advanceInvoiceTo.setPercent(getAdvancePercent());
				advanceInvoiceTo.setAmount(getAdvanceAmount());
				advanceInvoiceTo.setPayMethod(getAdvancePayMethod());
				advanceInvoiceTo.setConexFlowPayMethod(obtainConexFlowPayMethod());
				advanceInvoiceTo.setRegistryBank(getAdvanceBank());
				advanceInvoiceTo.setFinanceDate(getAdvancePaymentDate());
				advanceInvoiceTo.setPosShift(PosUtils.getUserPosShift());

				AdvanceInvoicing advanceInvoicing = new AdvanceInvoicing();
				int count = advanceInvoicing.invoice(advanceInvoiceTo, getCheckedReservations());

				clearCheckedReservations();
				onSearch(event);
				String msg = "Facturas de Anticipos generadas: " + count; 
				AonUtil.addInfoMessage(msg);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Se produjo un error al generar las Facturas de Anticipos. [" + ex.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private boolean validateAdvanceInvoice() throws ManagerBeanException {
		if (isCashOrCardPayment() && !PosUtils.isUserPosShiftOpened()) {
			String msg = "No se puede Facturar en Metálico/Tarjetas. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	private boolean isCashOrCardPayment() {
		PayMethodType type = getAdvancePayMethod().getType();
		if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
			return true;
		}
		return false;
	}

	private Item obtainAdvanceItem() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainAdvanceItem();
	}

	private PayMethod obtainConexFlowPayMethod() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainConexFlowPayMethod();
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