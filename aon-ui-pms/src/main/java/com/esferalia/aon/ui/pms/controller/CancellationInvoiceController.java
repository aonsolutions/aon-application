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
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowStatus;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.invoicing.CancellationInvoiceTo;
import com.esferalia.aon.pms.invoicing.CancellationInvoicing;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.event.CancellationInvoiceSearchListener;

public class CancellationInvoiceController extends BasicController implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date cancellationDate;
	private Integer cancellationPenalty;
	private PayMethod cancellationPayMethod;
	private RegistryBank cancellationBank;
	private int cancellationDaysToPayment;
	private boolean showInvoiceWindow;
	private boolean showConfirmWindow;

	public Date getCancellationDate() {
		return cancellationDate;
	}
	public void setCancellationDate(Date cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public Integer getCancellationPenalty() {
		return cancellationPenalty;
	}
	public void setCancellationPenalty(Integer cancellationPenalty) {
		this.cancellationPenalty = cancellationPenalty;
	}

	public PayMethod getCancellationPayMethod() {
		return cancellationPayMethod;
	}
	public void setCancellationPayMethod(PayMethod cancellationPayMethod) {
		this.cancellationPayMethod = cancellationPayMethod;
	}

	public RegistryBank getCancellationBank() {
		return cancellationBank;
	}
	public void setCancellationBank(RegistryBank cancellationBank) {
		this.cancellationBank = cancellationBank;
	}

	public int getCancellationDaysToPayment() {
		return cancellationDaysToPayment;
	}
	public void setCancellationDaysToPayment(int cancellationDaysToPayment) {
		this.cancellationDaysToPayment = cancellationDaysToPayment;
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

	public void onCancellationInvoiceShow(ActionEvent event) throws ManagerBeanException {
		if (obtainCancellationItem() == null) {
			setShowInvoiceWindow(false);
			String msg = "No se puede Facturar. No esta definido el Producto para Cancelaciones.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setCancellationDate(new Date());
		setCancellationPenalty(null);
		setCancellationPayMethod(null);
		setCancellationBank(null);
		setCancellationDaysToPayment(0);
	}

	public boolean isBankRequired() {
		PayMethod payMethod = getCancellationPayMethod();
		return (payMethod != null && (payMethod.getType() == PayMethodType.BANK_TRANSFER || payMethod.getType() == PayMethodType.CHEQUE)); 		 
	}
	
	public Date getCancellationPaymentDate() {
		return DateUtils.addDays(getCancellationDate(), getCancellationDaysToPayment());
	}
	
	public void onCancellationInvoice(ActionEvent event) {
		try {
			if (validateCancellationInvoice()) {
				CancellationInvoiceTo cancellationInvoiceTo = new CancellationInvoiceTo();
				CancellationInvoiceSearchListener search = (CancellationInvoiceSearchListener)AonUtil.getRegisteredBean(CANCELLATION_INVOICE_SEARCH_LISTENER_NAME);
				cancellationInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
				cancellationInvoiceTo.setIssueDate(getCancellationDate());
				cancellationInvoiceTo.setItem(obtainCancellationItem());
				cancellationInvoiceTo.setPenaltyDays(getCancellationPenalty());
				cancellationInvoiceTo.setPayMethod(getCancellationPayMethod());
				cancellationInvoiceTo.setConexFlowPayMethod(obtainConexFlowPayMethod());
				cancellationInvoiceTo.setRegistryBank(getCancellationBank());
				cancellationInvoiceTo.setFinanceDate(getCancellationPaymentDate());
				cancellationInvoiceTo.setManual(search.isManual());
				cancellationInvoiceTo.setPosShift(PosUtils.getUserPosShift());
	
				CancellationInvoicing cancellationInvoicing = new CancellationInvoicing();
				int count = cancellationInvoicing.invoice(cancellationInvoiceTo, getChargeableReservations(getCheckedReservations(), cancellationInvoiceTo));
	
				clearCheckedReservations();
				onSearch(event);
				String msg = "Facturas de Cancelacion generadas: " + count; 
				AonUtil.addInfoMessage(msg);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Se produjo un error al generar las Facturas de Cancelacion. [" + ex.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	private boolean validateCancellationInvoice() throws ManagerBeanException {
		if (isCashOrCardPayment() && !PosUtils.isUserPosShiftOpened()) {
			String msg = "No se puede Facturar en Metálico/Tarjetas. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	private boolean isCashOrCardPayment() {
		PayMethodType type = getCancellationPayMethod().getType();
		if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
			return true;
		}
		return false;
	}

	private Item obtainCancellationItem() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainCancellationItem();
	}

	private PayMethod obtainConexFlowPayMethod() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		return reservationUtils.obtainConexFlowPayMethod();
	}

	public void onCancellationNoInvoice(ActionEvent event) {
		try {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : getCheckedReservations()) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				double advancedAmount = reservation.getAdvancedAmount();
				if (advancedAmount > 0) {
					CancellationInvoiceSearchListener search = (CancellationInvoiceSearchListener)AonUtil.getRegisteredBean(CANCELLATION_INVOICE_SEARCH_LISTENER_NAME);
					CancellationInvoiceTo cancellationInvoiceTo = new CancellationInvoiceTo();
					cancellationInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
					cancellationInvoiceTo.setIssueDate(new Date());
					cancellationInvoiceTo.setItem(obtainCancellationItem());
					cancellationInvoiceTo.setKeepAdvance(search.isGuestReservationSearch());
					cancellationInvoiceTo.setPosShift(PosUtils.getUserPosShift());

					CancellationInvoicing cancellationInvoicing = new CancellationInvoicing();
					cancellationInvoicing.invoice(cancellationInvoiceTo, reservation);
				}

				reservation.setPenaltyValue("0");
				reservation.setCheckStatus(ReservationCheckStatus.CANCEL_NO_INVOICEABLE);
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

	private List<Integer> getChargeableReservations(List<Integer> reservations, CancellationInvoiceTo cancellationInvoiceTo) throws ManagerBeanException {
		Domain domain = getDomain();
		try {
			LinkedList<Integer> chargeableReservations = new LinkedList<Integer>();
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				Double amount = reservation.getPenaltyAmount();
				if (cancellationInvoiceTo.isManual() || (amount == null || amount <= 0.01)) {
					if (cancellationInvoiceTo.isManual()) {
						reservation.setPenaltyValue(cancellationInvoiceTo.getPenaltyDays().toString());
					}
					ReservationUtils reservationUtils = new ReservationUtils(domain.getId());
					amount = reservationUtils.obtainCancellationPenaltyAmount(reservation);
					reservation.setPenaltyAmount(amount);
					reservation = (ProjectReservation)reservationBean.update(reservation);
				}
				amount = CommonUtil.round(amount - reservation.getAdvancedAmount());

				String token = reservation.getToken();
				if (token != null) {
					String customerId = reservation.getCustomer().getId().toString();
					String user = getLogin();
					ConexFlowConnection connection = DBConsults.getConection(domain);
					ConexFlow cp = DBConsults.getConexFlowLastStatusX(domain, user, reservation.getId(), token, ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
					ConexFlow s = DBConsults.getConexFlowLastStatusX(domain, user, reservation.getId(), token, ConexFlowStatus.SALE);
					
					if((cp != null && amount <= Double.parseDouble(cp.getRespuesta().getImporte()))
							|| (s != null && amount <= Double.parseDouble(s.getRespuesta().getImporte()))){
						chargeableReservations.add(reservationId);
					}else {
						ConexFlow confirmPreauthorization = null;
						ConexFlow preauthorization = DBConsults.getConexFlowLastStatusX(domain, user, reservationId, token, ConexFlowStatus.PREAUTHORIZATION);
						if(preauthorization != null && Double.parseDouble(preauthorization.getRespuesta().getImporte()) >= amount){
							Query confirmPreQ = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection, customerId, token, amount, 
									Double.parseDouble(preauthorization.getRespuesta().getImporte()), preauthorization.getRespuesta().getFecha(),
									preauthorization.getRespuesta().getAutorizacion(), preauthorization.getRespuesta().getFechaOriginal(), preauthorization.getRespuesta().getOperacion());
							confirmPreauthorization = ConexFlowPost.execute(connection,ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, confirmPreQ);
								
							Boolean ok = "000".equals(confirmPreauthorization.getRespuesta().getResultado());
							confirmPreauthorization.setStatus(ok ? ConexFlowStatus.CONFIRM_PREAUTHORIZATION : ConexFlowStatus.CONFIRM_PREAUTHORIZATION_FAIL);
							String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_"
									+ confirmPreauthorization.getStatus().getName() + "#" + confirmPreauthorization.getRespuesta().getImporte();
							DBConsults.insertConexFlow(domain, user, confirmPreauthorization, reservation.getId(), description);
							if(ok){
								String description2 = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_"
										+ ConexFlowStatus.PREAUTHORIZATION_PAID.getName() + "#" + preauthorization.getRespuesta().getImporte();
								DBConsults.updateConexFlowDescription(domain, user, preauthorization.getId(), description2);	
								chargeableReservations.add(reservationId);
								// CREACION  DEL PAYSLIP PARA LA CONFIRMACION DE PREAUTHORIZACION
								ConexFlowUtils.setVoucher(getDomain(), getLogin(), reservationId, confirmPreauthorization);
							}			
						}
					
						if(confirmPreauthorization == null || (confirmPreauthorization != null 
								&& !"000".equals(confirmPreauthorization.getRespuesta().getResultado()))){
							Query saleQ = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, token, amount, customerId, null);
							ConexFlow conexFlow2 = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, saleQ);
							
							Boolean ok = "000".equals(conexFlow2.getRespuesta().getResultado());
							conexFlow2.setStatus(ok ? ConexFlowStatus.SALE : ConexFlowStatus.SALE_FAIL);
							String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_"
									+ conexFlow2.getStatus().getName() + "#" + conexFlow2.getRespuesta().getImporte();
							DBConsults.insertConexFlow(domain, user, conexFlow2, reservation.getId(), description);
								
							if (ok) {
								chargeableReservations.add(reservationId);
								// CREACION  DEL PAYSLIP PARA LA CARGO DIRECTO
								ConexFlowUtils.setVoucher(getDomain(), getLogin(), reservationId, conexFlow2);
							}
						}
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
	
	private Domain getDomain() {
		Domain domain = new Domain();
		domain.setId(DomainManager.getCurrentDomain());
		domain.setName(AonUtil.getDomainName());
		return domain;
	}
	
	private String getLogin() {
		return UserUtils.getInstance().getLoggedUser().getLogin();
	}

}