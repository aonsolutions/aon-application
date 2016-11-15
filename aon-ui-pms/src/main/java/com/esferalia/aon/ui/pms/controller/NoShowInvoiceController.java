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
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.invoicing.NoShowInvoiceTo;
import com.esferalia.aon.pms.invoicing.NoShowInvoicing;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.event.NoShowInvoiceSearchListener;

public class NoShowInvoiceController extends BasicController implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date noShowDate;
	private Integer noShowPenalty;
	private PayMethod noShowPayMethod;
	private RegistryBank noShowBank;
	private int noShowDaysToPayment;
	private ProjectReservation reservation;
	private boolean showInvoiceWindow;
	private boolean showConfirmWindow;
	private boolean showCreditCardWindow;

	public Date getNoShowDate() {
		return noShowDate;
	}
	public void setNoShowDate(Date noShowDate) {
		this.noShowDate = noShowDate;
	}

	public Integer getNoShowPenalty() {
		return noShowPenalty;
	}

	public void setNoShowPenalty(Integer noShowPenalty) {
		this.noShowPenalty = noShowPenalty;
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

	public ProjectReservation getReservation() {
		return reservation;
	}
	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
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

	public boolean isShowCreditCardWindow() {
		return showCreditCardWindow;
	}

	public void setShowCreditCardWindow(boolean showCreditCardWindow) {
		this.showCreditCardWindow = showCreditCardWindow;
	}

	public void onNoShowInvoiceShow(ActionEvent event) throws ManagerBeanException {
		if (obtainNoShowItem() == null) {
			setShowInvoiceWindow(false);
			String msg = "No se puede Facturar. No esta definido el Producto para No Shows.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setNoShowDate(new Date());
		setNoShowPenalty(null);
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
				noShowInvoiceTo.setPenaltyDays(getNoShowPenalty());
				noShowInvoiceTo.setPayMethod(getNoShowPayMethod());
				noShowInvoiceTo.setRegistryBank(getNoShowBank());
				noShowInvoiceTo.setFinanceDate(getNoShowPaymentDate());
				noShowInvoiceTo.setPosShift(PosUtils.getUserPosShift());
	
				NoShowInvoicing noShowInvoicing = new NoShowInvoicing();
				int count = noShowInvoicing.invoice(noShowInvoiceTo, invoiceConexflow(getCheckedReservations()));
	
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
	
	public LinkedList<Integer> invoiceConexflow(List<Integer> reservations) throws ManagerBeanException {
		try {
			LinkedList<Integer> list = new LinkedList<Integer>();
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				String token = reservation.getToken();
				if(token != null){
					Double amount = reservation.getPenaltyAmount();
					if(amount == null || amount <= 0.01){
						ReservationUtils reservationUtils = new ReservationUtils(getDomain(reservation).getId());
						amount = reservationUtils.obtainCancellationPenaltyPrice(reservation);
					}
					ConexFlow conexFlow = null;
					ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
					ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation),
							AonUtil.getRemoteUser(), reservation.getId(), "CHECK-" + ConexFlowConstant.PREAUTHORIZATION_OP);
					if(cf == null || Double.parseDouble(cf.getRespuesta().getImporte()) == 0.01
							|| Double.parseDouble(cf.getRespuesta().getImporte()) < amount)
						cf = DBConsults.getConexFlowLastOperation(getDomain(reservation),
								AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
					if(cf != null && Double.parseDouble(cf.getRespuesta().getImporte()) >= amount){
						Query confirmPreQ = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection, reservation.getCustomer().getId().toString(),
								token, amount, Double.parseDouble(cf.getRespuesta().getImporte()), cf.getRespuesta().getFecha(),
								cf.getRespuesta().getAutorizacion(),cf.getRespuesta().getFechaOriginal(), cf.getRespuesta().getOperacion());
						conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, confirmPreQ, reservation.getId(), getDomain(reservation), false);
						if(conexFlow.getRespuesta().getResultado().equals("000")){
							list.add(reservationId);
						}	
					}
					if(cf == null || (conexFlow != null && !conexFlow.getRespuesta().getResultado().equals("000"))) {
						Query saleQ = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, token, amount,
							reservation.getCustomer().getId().toString(),reservation.getCreditCardCvv());
						ConexFlow conexFlow2 = ConexFlowPost.execute(connection,ConexFlowConstant.SALE_OP, saleQ, reservation.getId(), getDomain(reservation), false);
						if(conexFlow2.getRespuesta().getResultado().equals("000"))
							list.add(reservationId);
					}
				} else list.add(reservationId);
			}
			return list;
		} catch (Exception e) {
			throw new ManagerBeanException(e.getMessage(),e);
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
					noShowInvoiceTo.setKeepAdvance(search.isGuestReservationSearch());
					noShowInvoiceTo.setPosShift(PosUtils.getUserPosShift());

					NoShowInvoicing noShowInvoicing = new NoShowInvoicing();
					noShowInvoicing.invoice(noShowInvoiceTo, reservation);
				}

				reservation.setPenaltyDays(0);
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

	/********** CREDIT CARD OPERATIONS / CONEXFLOW **********/
	
	private static final String CONEXFLOW_RESULT_OK = "000";
	
	private boolean preauthorization;
	private boolean confirmPreauthorization;
	private boolean creditCardToken;
	private boolean sale;
	private Double preauthorizationAmount;
	private Double confirmPreauthorizationAmount;

	
	//********** GETTERS && SETTERS **********//
	
	public boolean isPreauthorization() {
		return preauthorization;
	}
	
	public void setPreauthorization(boolean preauthorization) {
		this.preauthorization = preauthorization;
	}
	
	public Double getPreauthorizationAmount(){
		return preauthorizationAmount;
	}
	
	public void setPreauthorizationAmount(Double preauthorizationAmount){
		this.preauthorizationAmount = preauthorizationAmount;
	}
	
	public boolean isConfirmPreauthorization(){
		return confirmPreauthorization;
	}
	
	public void setConfirmPreauthorization(boolean confirmPreauthorization){
		this.confirmPreauthorization = confirmPreauthorization;
	}
	
	public boolean isCreditCardToken(){
		return creditCardToken;
	}
	
	public void setCreditCardToken(boolean creditCardToken){
		this.creditCardToken = creditCardToken;
	}
	
	public boolean isSale(){
		return sale;
	}
	
	public void setSale(boolean sale){
		this.sale = sale;
	}
	
	public boolean isShowSale(){
		ConexFlowConnection connection = DBConsults.getConection(getDomain(getReservation()));
		
		return connection.getActive() && (isPreauthorization()|| isCreditCardToken()) && !isConfirmPreauthorization() && !isSale();
	}
	
	public boolean isNotShowSale(){
		ConexFlowConnection connection = DBConsults.getConection(getDomain(getReservation()));
		
		return connection.getActive() && (isConfirmPreauthorization() || isSale());
	}
	
	public Double getConfirmPreauthorizationAmount(){
		return confirmPreauthorizationAmount;
	}
	
	public void setConfirmPreauthorizationAmount(Double confirmPreauthorizationAmount){
		this.confirmPreauthorizationAmount = confirmPreauthorizationAmount;
	}
	
	//********** EVENTS **********//
	
	public void onCreditCardShow(ActionEvent event) {
		setPreauthorizationAmount(null);
		setConfirmPreauthorizationAmount(null);
		ProjectReservation reservation = (ProjectReservation)model.getRowData();
		ReservationUtils reservationUtils = new ReservationUtils();
		reservationUtils.decryptReservationCreditCardData(reservation);
		setReservation(reservation);
		ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		setPreauthorization(cf != null);
		ConexFlow cf2 = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		setConfirmPreauthorization(cf2 != null);
		ConexFlow cf3 = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CREATE_TOKEN_OP);
		setCreditCardToken(cf3 != null);
		ConexFlow cf4 = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
		setSale(cf4 != null);
		if(isPreauthorization() && cf.getRespuesta().getImporte() !=  null) setPreauthorizationAmount(Double.parseDouble(cf.getRespuesta().getImporte()));
		if(isConfirmPreauthorization() && cf.getRespuesta().getImporte() != null) setConfirmPreauthorizationAmount(Double.parseDouble(cf2.getRespuesta().getImporte()));
	}
	
	public void onChargeCreditCard(ActionEvent event) {
		boolean resultOk = false;
		ProjectReservation reservation = getReservation();
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if(connection.getActive()){
			String errorMsg = null;
			if(getPreauthorizationAmount() <= reservation.getTotal()){
				ConexFlow cf1 = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CREATE_TOKEN_OP);
				if(cf1 != null){
					ConexFlow cf2 = DBConsults.getConexFlowLastOperation(getDomain(reservation),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
					if(cf2 != null){
						Double importeOriginal;
						if(cf2.getRespuesta().getImporte() != null) importeOriginal = Double.parseDouble(cf2.getRespuesta().getImporte());
						else importeOriginal = getPreauthorizationAmount();
						
						Query confirmPreauthorizationQuery = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(
							connection, reservation.getCustomer().getId().toString(), cf1.getRespuesta().getToken()
							, getPreauthorizationAmount(), importeOriginal, cf1.getRespuesta().getCF_ExpirationDate(), cf2.getRespuesta().getAutorizacion()
							, cf2.getRespuesta().getFecha(), cf2.getRespuesta().getIdOperacion());
						
						ConexFlow conexFlowConfirmPreauthorization =  ConexFlowPost.execute(connection, ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, confirmPreauthorizationQuery, reservation.getId(), getDomain(reservation), false);
						if(conexFlowConfirmPreauthorization!= null){
							resultOk = conexFlowConfirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
							if(resultOk){
								// crear factura ....	
								setShowCreditCardWindow(false);
							}
						}
						else errorMsg = "Los datos de conexión a conexFlow son incorrectos.";
					}
					if(cf2 == null || !resultOk){
						Query cardPaymentQuery = ConexFlowUtils.getConexFlowCardPaymentQuery(
						connection, cf1.getRespuesta().getToken()
							, getPreauthorizationAmount(), reservation.getCustomer().getId().toString(), reservation.getCreditCardCvv());
				
						ConexFlow conexFlowCardPayment = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, cardPaymentQuery, reservation.getId(), getDomain(reservation), false);
						if(conexFlowCardPayment != null){
							if(conexFlowCardPayment.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)){
								// crear factura ....	
								setShowCreditCardWindow(false);
							}
							else errorMsg = "Error " + conexFlowCardPayment.getRespuesta().getResultado() + ": " + conexFlowCardPayment.getRespuesta().getDesResultado()+".";	
						}
						else errorMsg = "Los datos de conexión a conexFlow son incorrectos.";	
					}
				}
				else errorMsg = "No tiene ninguna tarjeta de crédito asociada a la reserva.";
			}
			else errorMsg = "El importe a cobrar es mayor que el importe de la reserva.";
			
			if(errorMsg != null){
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
	}
	
	private Domain getDomain(ProjectReservation reservation) {
		Domain domain = new Domain();
		domain.setName(AonUtil.getDomainName());
		domain.setId(reservation.getDomain());
		return domain;
	}
}
