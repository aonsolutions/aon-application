package com.esferalia.aon.ui.pms;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.JAXBException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.XMLUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;

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
			ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
			String amountStr = preauthorization.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else if(isCharge()){
			ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
			String amountStr = charge.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else if(isConfirmPreauthorization()){
			ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
			String amountStr = confirmPreauthorization.getRespuesta().getImporte();
			return Double.parseDouble(amountStr);
		}else{
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
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		return (confirmPreauthorization != null && confirmPreauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}

	public boolean isPreauthorization() {
		ProjectReservation reservation = getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		return preauthorization !=null && preauthorization.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isCharge() {
		ProjectReservation reservation = getReservation();
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
		return (charge != null && charge.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK));
	}
	
	public boolean isRefund() {
		ProjectReservation reservation = getReservation();
		ConexFlow refund = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.REFUND_OP);
		return refund != null && refund.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
	}

	public boolean isConexFlowActive(){
		return DBConsults.getConection(getDomain()).getActive();
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
	
	public void onRefund(){
		ProjectReservation reservation = getReservation();
		ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		ConexFlow charge = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
		ConexFlow confirmPreauthorization = DBConsults.getConexFlowLastOperation(getDomain(),AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
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
		}
		showCancelationOption = null;
		setShowRefundOption(false);
	}
	
	public void onRefundCancel(){
		ProjectReservation reservation = getReservation();
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
	}
	
	public void onCollect(){
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
