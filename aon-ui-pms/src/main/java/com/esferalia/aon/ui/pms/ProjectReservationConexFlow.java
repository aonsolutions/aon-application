package com.esferalia.aon.ui.pms;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.jooq.DBConsults;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationConexFlow implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String CONEXFLOW_RESULT_OK = "000";
	
	
	private ProjectReservation reservation;
	private ProjectReservationPermission reservationPermission;
	private String conexflowOperation;
	private String conexflowOperationCancelation;
	private String conexflowOperationRefund;
	private Double amount;
	private Boolean showCancelationOption;
	private boolean showRefundOption = false;
	private Domain domain;
	
	
	public ProjectReservationConexFlow(ProjectReservation reservation, Domain domain, ProjectReservationPermission reservationPermission) {
		setReservation(reservation);
		setDomain(domain);
		setReservationPermission(reservationPermission);
	}
	
	public ProjectReservationPermission getReservationPermission(){
		return reservationPermission;
	}
	
	public void setReservationPermission(ProjectReservationPermission reservationPermission){
		this.reservationPermission = reservationPermission;
	}
	
	public Domain getDomain(){
		return domain;
	}
	
	public void setDomain(Domain domain){
		this.domain = domain;
	}
	
	public ProjectReservation getReservation() {
		return reservation;
	}

	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
	}

	public boolean isShowCancelationOption() {
		if(showCancelationOption == null) setShowCancelationOption(false);
		if(showCancelationOption == null && !isShowPreauthorization() && !isShowCharge() && isShowCancelation())
			setShowCancelationOption(true);
		return showCancelationOption;
	}
	
	public void setShowCancelationOption(boolean value){
		showCancelationOption = value;
	}
	
	public boolean isShowRefundOption() {
		if(!isShowPreauthorization() && !isShowCharge() && !isShowCancelation()
				&& !isShowConfirmPreauthorization() && isShowRefund())
			setShowRefundOption(true);
		return showRefundOption;
	}
	
	public void setShowRefundOption(boolean value){
		showRefundOption = value;
	}
	
	public Double getAmount() {
		if(amount != null) return amount;
		ProjectReservation reservation = getReservation();
		ReservationCheckStatus rcs = reservation.getCheckStatus();
		if(rcs.equals(ReservationCheckStatus.CANCEL_INVOICEABLE) || rcs.equals(ReservationCheckStatus.NO_SHOW) || reservation.getAdvance() == 0.0){
			ReservationUtils reservationUtils = new ReservationUtils(getDomain().getId());
			try {
				return (Double) reservationUtils.obtainCancellationPenaltyPrice(getReservation());
			} catch (ManagerBeanException e) {
				e.printStackTrace();
				return 0.0;
			}
		} else{
			return reservation.getAdvance();
		}
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getConexflowOperation() {
		return conexflowOperation;
	}

	public void setConexflowOperation(String conexflowOperation) {
		this.conexflowOperation = conexflowOperation;
	}
	
	public String getConexflowOperationCancelation() {
		return conexflowOperationCancelation;
	}

	public void setConexflowOperationCancelation(String conexflowOperationCancelation) {
		this.conexflowOperationCancelation = conexflowOperationCancelation;
	}
	public String getConexflowOperationRefund() {
		return conexflowOperationRefund;
	}

	public void setConexflowOperationRefund(String conexflowOperationRefund) {
		this.conexflowOperationRefund = conexflowOperationRefund;
	}
	
	public boolean isConfirmPreauthorization() {
		ProjectReservation reservation = getReservation();
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		return (confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}

	public boolean isPreauthorization() {
		ProjectReservation reservation = getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		return preauthorization !=null && preauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isRefund() {
		ProjectReservation reservation = getReservation();
		ConexFlow refund = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.REFUND_OP);
		return refund != null && refund.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isConexFlowConnection(){
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		boolean b= false;
		try {
			b = getReservationPermission().isCreditCardEditable();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return !b && connection.getActive();
	}
	
	public boolean isShowCharge() {
		ProjectReservation reservation =  getReservation();
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.SALE_OP);
		return (charge == null || !charge.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}

	public boolean isShowCancelation() {
		return isPreauthorization() || isCharge() || isConfirmPreauthorization() || isRefund();
	}
	
	public boolean isShowConfirmPreauthorization() {
		ProjectReservation reservation =  getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		return (preauthorization != null && preauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK))
				&& (confirmPreauthorization == null || !confirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}

	public boolean isShowPreauthorization() {
		ProjectReservation reservation =  getReservation();
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		
		return confirmPreauthorization==null || !confirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isShowRefund() {
		ProjectReservation reservation =  getReservation();
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.SALE_OP);
		
		return (confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals("000"))
				|| (charge !=null && charge.getRespuesta().getResultado().equals("000"));
	}
	
	public boolean isCharge() {
		ProjectReservation reservation = getReservation();
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(), reservation.getId(), ConexFlowConstant.SALE_OP);
		return (charge != null && charge.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}


}
