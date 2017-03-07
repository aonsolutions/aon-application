package com.esferalia.aon.ui.pms;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowStatus;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;

public class ProjectReservationConexFlow implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String CONEXFLOW_RESULT_OK = "000";

	private ProjectReservation reservation;
	private String conexflowOperation;
	private String conexflowOperationCancelation;
	private String conexflowOperationRefund;
	private Double amount;
	private Boolean showCancelationOption;
	private boolean showRefundOption = false;
	private Domain domain;

	public ProjectReservationConexFlow(ProjectReservation reservation, Domain domain) {
		setReservation(reservation);
		setDomain(domain);
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
		if(!isShowCancelation()) return false;
		if(showCancelationOption == null && !isShowPreauthorization() && !isShowCharge() && isShowCancelation())
			return true;
		if(showCancelationOption == null) setShowCancelationOption(false);
	
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
		if(isPreauthorization()){
			ConexFlow preauthorization = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.PREAUTHORIZATION);
			String amountStr = preauthorization.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else if(isCharge()){
			ConexFlow charge = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.SALE);
			String amountStr = charge.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else if(isConfirmPreauthorization()){
			ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
			String amountStr = confirmPreauthorization.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else{
			return getReservation().getPenaltyAmount();
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
		if(reservation.getToken() == null){
			return false;
		}
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
		return (confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}

	public boolean isPreauthorization() {
		ProjectReservation reservation = getReservation();
		if(reservation.getToken() == null){
			return false;
		}
		ConexFlow preauthorization = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.PREAUTHORIZATION);
		return preauthorization !=null && preauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isCharge() {
		ProjectReservation reservation = getReservation();
		if(reservation.getToken() == null){
			return false;
		}
		ConexFlow charge = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.SALE);
		return (charge != null && charge.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}
	
	public boolean isRefund() {
		ProjectReservation reservation = getReservation();
		if(reservation.getToken() == null){
			return false;
		}
		ConexFlow refund = DBConsults.getConexFlowLastStatusX(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.REFUND);
		return refund != null && refund.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isConexFlowActive(){
		return DBConsults.getConection(getDomain()).isActive();
	}
	
	public boolean isShowCharge() {
		return (!isConfirmPreauthorization() && !isCharge());
	}

	public boolean isShowCancelation() {
		return isPreauthorization() || isCharge() || isConfirmPreauthorization() || isRefund();
	}
	
	public boolean isShowConfirmPreauthorization() {
		return isPreauthorization() && !isCharge() && !isConfirmPreauthorization();
	}

	public boolean isShowPreauthorization() {
		return !isPreauthorization() && !isCharge();
	}

	public boolean isShowRefund() {
		return (isCharge() || isConfirmPreauthorization()) && !isRefund();
	}
	
	public void onRefund(){ // TODO 
		/*ProjectReservation reservation = getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastStatusX(getDomain(), AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.PREAUTHORIZATION);
		ConexFlow charge = DBConsults.getConexFlowLastStatusX(getDomain(), AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.SALE);
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastStatusX(getDomain(), AonUtil.getRemoteUser(), reservation.getId(), reservation.getToken(), ConexFlowStatus.CONFIRM_PREAUTHORIZATION);

		if(preauthorization != null && preauthorization.getRespuesta().getResultado().equals("000")){
			preauthorization.getRespuesta().setResultado("XXX");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(preauthorization),
											reservation.getProject().getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}
		if(charge != null && charge.getRespuesta().getResultado().equals("000")){
			charge.getRespuesta().setResultado("XXX");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(charge),
											reservation.getProject().getId(), ConexFlowConstant.SALE_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}
		if(confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals("000")){
			confirmPreauthorization.getRespuesta().setResultado("XXX");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(confirmPreauthorization),
											reservation.getProject().getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}*/
		showCancelationOption = null;
		setShowRefundOption(false);
	}
	
	public void onRefundCancel(){  
	/*	ProjectReservation reservation = getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		if(preauthorization != null && preauthorization.getRespuesta().getResultado().equals("XXX")){
			preauthorization.getRespuesta().setResultado("000");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(preauthorization),
											reservation.getProject().getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}
		if(charge != null && charge.getRespuesta().getResultado().equals("XXX")){
			charge.getRespuesta().setResultado("000");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(charge),
											reservation.getProject().getId(), ConexFlowConstant.SALE_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}
		if(confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals("XXX")){
			confirmPreauthorization.getRespuesta().setResultado("000");
			try {
				DBConsults.insertConexFlowOperation(getDomain(), XMLUtils.writeXml(confirmPreauthorization),
											reservation.getProject().getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	public void onCollect(){ // TODO 
		ProjectReservation reservation = getReservation();
		ConexFlow refund = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.REFUND_OP);
		if(refund != null){
			DBConsults.delete(getDomain(), reservation.getProject().getId(), ConexFlowConstant.REFUND_OP);
		}
		showCancelationOption = null;
	}
	
	public void onCancel(){
		showCancelationOption = null;
	}
}
