package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.invoicing.NoShowInvoiceTo;
import com.esferalia.aon.pms.invoicing.NoShowInvoicing;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.ProjectReservationConexFlow;
import com.esferalia.aon.ui.pms.event.NoShowInvoiceSearchListener;

public class NoShowInvoiceController extends BasicController implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date noShowDate;
	private String noShowPenaltyValue;
	private PayMethod noShowPayMethod;
	private RegistryBank noShowBank;
	private int noShowDaysToPayment;
	private boolean showInvoiceWindow;
	private boolean showConfirmWindow;

	public Date getNoShowDate() {
		return noShowDate;
	}
	public void setNoShowDate(Date noShowDate) {
		this.noShowDate = noShowDate;
	}

	public String getNoShowPenaltyValue() {
		return noShowPenaltyValue;
	}
	public void setNoShowPenaltyValue(String noShowPenaltyValue) {
		this.noShowPenaltyValue = noShowPenaltyValue;
	}

	public PayMethod getNoShowPayMethod() {
		return noShowPayMethod;
	}
	public void setNoShowPayMethod(PayMethod noShowPayMethod) {
		this.noShowPayMethod = noShowPayMethod;
	}

	public RegistryBank getNoShowBank() {
		return noShowBank;
	}
	public void setNoShowBank(RegistryBank noShowBank) {
		this.noShowBank = noShowBank;
	}

	public int getNoShowDaysToPayment() {
		return noShowDaysToPayment;
	}
	public void setNoShowDaysToPayment(int noShowDaysToPayment) {
		this.noShowDaysToPayment = noShowDaysToPayment;
	}

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}
	public void setShowInvoiceWindow(boolean showInvoiceWindow) {
		this.showInvoiceWindow = showInvoiceWindow;
	}

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}
	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}

	public void onNoShowInvoiceShow(ActionEvent event) throws ManagerBeanException {
		if (obtainNoShowItem() == null) {
			setShowInvoiceWindow(false);
			String msg = "No se puede Facturar. No esta definido el Producto para No Shows.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setNoShowDate(new Date());
		setNoShowPenaltyValue(null);
		setNoShowPayMethod(null);
		setNoShowBank(null);
		setNoShowDaysToPayment(0);
	}

	public boolean isBankRequired() {
		PayMethod payMethod = getNoShowPayMethod();
		return (payMethod != null && (payMethod.getType() == PayMethodType.BANK_TRANSFER || payMethod.getType() == PayMethodType.CHEQUE)); 		 
	}
	
	public Date getNoShowPaymentDate() {
		return DateUtils.addDays(getNoShowDate(), getNoShowDaysToPayment());
	}
	
	public void onNoShowInvoice(ActionEvent event) {
		try {
			if (validateNoShowInvoice()) {
				NoShowInvoiceTo noShowInvoiceTo = new NoShowInvoiceTo();
				NoShowInvoiceSearchListener search = (NoShowInvoiceSearchListener)AonUtil.getRegisteredBean(NO_SHOW_INVOICE_SEARCH_LISTENER_NAME);
				noShowInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
				noShowInvoiceTo.setIssueDate(getNoShowDate());
				noShowInvoiceTo.setItem(obtainNoShowItem());
				noShowInvoiceTo.setPenaltyValue(getNoShowPenaltyValue());
				noShowInvoiceTo.setPayMethod(getNoShowPayMethod());
				noShowInvoiceTo.setConexFlowPayMethod(obtainConexFlowPayMethod());
				noShowInvoiceTo.setRegistryBank(getNoShowBank());
				noShowInvoiceTo.setFinanceDate(getNoShowPaymentDate());
				noShowInvoiceTo.setManual(search.isManual());
				noShowInvoiceTo.setPosShift(PosUtils.getUserPosShift());
	
				NoShowInvoicing noShowInvoicing = new NoShowInvoicing();
				int count = noShowInvoicing.invoice(noShowInvoiceTo, getChargeableReservations(getCheckedReservations(), noShowInvoiceTo));
	
				clearCheckedReservations();
				onSearch(event);
				String msg = "Facturas de No Show generadas: " + count; 
				AonUtil.addInfoMessage(msg);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Se produjo un error al generar las Facturas de No Show. [" + ex.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private boolean validateNoShowInvoice() throws ManagerBeanException {
		if (isCashOrCardPayment() && !PosUtils.isUserPosShiftOpened()) {
			String msg = "No se puede Facturar en Metálico/Tarjetas. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	private boolean isCashOrCardPayment() {
		PayMethodType type = getNoShowPayMethod().getType();
		if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
			return true;
		}
		return false;
	}

	private Item obtainNoShowItem() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainNoShowItem();
	}

	private PayMethod obtainConexFlowPayMethod() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainConexFlowPayMethod();
	}

	public void onNoShowNoInvoice(ActionEvent event) {
		try {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : getCheckedReservations()) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				double advancedAmount = reservation.getAdvancedAmount();
				if (advancedAmount > 0) {
					NoShowInvoiceSearchListener search = (NoShowInvoiceSearchListener)AonUtil.getRegisteredBean(NO_SHOW_INVOICE_SEARCH_LISTENER_NAME);
					NoShowInvoiceTo noShowInvoiceTo = new NoShowInvoiceTo();
					noShowInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
					noShowInvoiceTo.setIssueDate(new Date());
					noShowInvoiceTo.setItem(obtainNoShowItem());
					noShowInvoiceTo.setKeepAdvance(search.isGuestReservationSearch());
					noShowInvoiceTo.setPosShift(PosUtils.getUserPosShift());

					NoShowInvoicing noShowInvoicing = new NoShowInvoicing();
					noShowInvoicing.invoice(noShowInvoiceTo, reservation);
				}

				reservation.setPenaltyValue("0");
				reservation.setCheckStatus(ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE);
				reservationBean.update(reservation);
			}

			int count = getCheckedCount();
			clearCheckedReservations();
			onSearch(event);
			String msg = "Reservas marcadas como No Facturables: " + count;
			AonUtil.addInfoMessage(msg);
		} catch (ManagerBeanException ex) {
			String msg = "Se produjo un error al marcar las Reservas como No Facturables. [" + ex.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getModel().getRowData();

		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
		reservationController.onLoad(event, reservation.getId(), NO_SHOW_INVOICE_LIST_NAME, NO_SHOW_INVOICE_CONTROLLER_NAME + ".onSearch");
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

	private List<Integer> getChargeableReservations(List<Integer> reservations, NoShowInvoiceTo noShowInvoiceTo) throws ManagerBeanException { 
		try {
			LinkedList<Integer> chargeableReservations = new LinkedList<Integer>();
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				Double amount = reservation.getPenaltyAmount();
				if (noShowInvoiceTo.isManual() || (amount == null || amount <= 0.01)) {
					if (noShowInvoiceTo.isManual()) {
						reservation.setPenaltyValue(noShowInvoiceTo.getPenaltyValue());
					}
					ReservationUtils reservationUtils = new ReservationUtils(reservation.getDomain());
					amount = reservationUtils.obtainNoShowPenaltyAmount(reservation);
					reservation.setPenaltyAmount(amount);
					reservation = (ProjectReservation)reservationBean.update(reservation);
				}
				amount = CommonUtil.round(amount - reservation.getAdvancedAmount());

				if (!reservation.isBlankToken()) {
					ProjectReservationConexFlow reservationConexFlow = new ProjectReservationConexFlow(reservation);
					if (reservationConexFlow.executeCancellationSale(amount)) {
						chargeableReservations.add(reservationId);
					}
				} else {
					chargeableReservations.add(reservationId);
				}
			}
			return chargeableReservations;
		} catch (Exception e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

}