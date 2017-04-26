package com.esferalia.aon.ui.pms;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowStatus;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.config.PayMethod;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationConexFlow implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String CONEXFLOW_RESULT_OK = "000";
	private static final String CONEXFLOW_PREFIX = "CONEXFLOW_";

	private ProjectReservation reservation;
	private String login;
	private Domain domain;
	private String conexflowOperation;
	private String conexflowOperationCancelation;
	private String conexflowOperationRefund;
	private Double amount;
	private boolean showCancelationOption;
	private boolean showRefundOption;
	private boolean showNotifyWindow;

	public ProjectReservationConexFlow(ProjectReservation reservation) {
		setReservation(reservation);
		setLogin(UserUtils.getInstance().getLoggedUser().getLogin());
		setAmount(null);
	}

	public ProjectReservation getReservation() {
		return reservation;
	}
	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
		setDomain(getDomain(reservation));
	}

	public String getLogin() {
		return login;
	}
	public void setLogin(String login){
		this.login = login;
	}

	public Domain getDomain(){
		return domain;
	}
	public void setDomain(Domain domain){
		this.domain = domain;
	}
	private Domain getDomain(ProjectReservation reservation) {
		Domain domain = new Domain();
		domain.setName(AonUtil.getDomainName());
		domain.setId(reservation.getDomain());
		return domain;
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

	public Double getAmount() {
		if (amount == null) {
			ConexFlowStatus status = null;
			if (isPreauthorization()) {
				status = ConexFlowStatus.PREAUTHORIZATION;
			} else if (isSale()) {
				status = ConexFlowStatus.SALE;
			} else if (isConfirmPreauthorization()) {
				status = ConexFlowStatus.CONFIRM_PREAUTHORIZATION;
			}

			if (status != null) {
				ConexFlow cf = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), getReservation().getToken(), status);
				amount = Double.parseDouble(cf.getRespuesta().getImporte());
			} else {
				amount = getReservation().getPenaltyAmount();
			}
		}
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public boolean isShowCancelationOption() {
		return showCancelationOption;
	}

	public void setShowCancelationOption(boolean value){
		showCancelationOption = value;
	}

	public boolean isShowRefundOption() {
		return showRefundOption;
	}
	public void setShowRefundOption(boolean value){
		showRefundOption = value;
	}

	public boolean isShowNotifyWindow() {
		return showNotifyWindow;
	}
	public void setShowNotifyWindow(boolean showNotifyWindow) {
		this.showNotifyWindow = showNotifyWindow;
	}

	public boolean isPreauthorization() {
		return isOperation(ConexFlowStatus.PREAUTHORIZATION);
	}

	public boolean isConfirmPreauthorization() {
		return isOperation(ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
	}

	public boolean isSale() {
		return isOperation(ConexFlowStatus.SALE);
	}
	
	public boolean isRefund() {
		return isOperation(ConexFlowStatus.REFUND);
	}
	
	public boolean isOperation(ConexFlowStatus status){
		if (getReservation().getToken() != null) {
			ConexFlow cfP = DBConsults.getConexFlowLastStatusWD(getDomain(), getLogin(), 
					getReservation().getId(), getReservation().getToken(), status);
			return cfP !=null;
		} else return false;
	}

	public boolean isConexFlowActive() {
		return DBConsults.getConection(getDomain()).isActive();
	}
	
	public boolean isShowPreauthorization() {
		return (!isPreauthorization() && !isSale());
	}

	public boolean isShowSale() {
		return (!isConfirmPreauthorization() && !isSale());
	}

	public boolean isShowConfirmPreauthorization() {
		return (isPreauthorization() && !isSale() && !isConfirmPreauthorization());
	}

	public boolean isShowCancelation() {
		return (isPreauthorization() || isSale() || isConfirmPreauthorization() || isRefund());
	}

	public boolean isShowRefund() {
		return (isSale() || isConfirmPreauthorization()) && !isRefund();
	}
	

	public String onCreateToken() {
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		String code = getReservation().getCode();
		String token = null;
		if (connection.isActive()) {
			Query createTokenQuery = ConexFlowUtils.getConexFlowCreateTokenQuery(getReservation().getHrCreditCardNumber(), connection, 
					getReservation().getHrCreditCardExpirationMonth() + getReservation().getHrCreditCardExpirationYear(), code);
			ConexFlow cfT = ConexFlowPost.execute(connection, ConexFlowConstant.CREATE_TOKEN_OP, createTokenQuery);
			if (cfT == null) {
				conexFlowError("Error al crear el Token.");
			} else {
				token = cfT.getRespuesta().getToken();
				Boolean conexFlowOk = cfT.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
				if (!conexFlowOk) {
					conexFlowError("Error " + cfT.getRespuesta().getResultado() + ": " + cfT.getRespuesta().getDesResultado() + ".");
				} else {
					Double amount = 0.01;
					if (isAmex(getReservation().getHrCreditCardNumber())) {
						Query query = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, token, (Double) 0.01, code, getReservation().getId());
						ConexFlow cfV = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, query);
						conexFlowOk = cfV.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
						if (!conexFlowOk) {
							conexFlowError("Error " + cfV.getRespuesta().getResultado() + ": " + cfV.getRespuesta().getDesResultado() + ".");
						} else {
							// ACTUALIZA VALOR DEL TOKEN EN PROJECT_RESERVATION E INSERTA XML - CREATE TOKEN EN PROJECT ATTACH.
							cfT.setStatus(ConexFlowStatus.CREATE_TOKEN);
							String description = getConexFlowDescription(cfT, token, cfT.getStatus().getName(), null);
							DBConsults.insertConexFlow(getDomain(), getLogin(), cfT, getReservation().getId(), description);

							// INSERTA EL CHECKEO!
							cfV.setStatus(ConexFlowStatus.SALE_CHECK);
							description = getConexFlowDescription(cfV, token, cfV.getStatus().getName());
							DBConsults.insertConexFlow(getDomain(), getLogin(), cfV, getReservation().getId(), description);

							// REEMBOLSAR CARGO DE CHECKEO
							query = ConexFlowUtils.getConexFlowRefundQuery(connection, token, amount.toString(), code, getReservation().getId());
							ConexFlowPost.execute(connection, ConexFlowConstant.REFUND_OP, query);
							// NO SE INSERTA EN LA BASE DE DATOS
						}
					}
					else {
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection, code, token, (Double) 0.01, getReservation().getId());
						ConexFlow cfP = ConexFlowPost.execute(connection, ConexFlowConstant.PREAUTHORIZATION_OP, query);
						conexFlowOk = cfP.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
						
						if(!conexFlowOk){
							query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection, code, token, (Double) 1.00, getReservation().getId());
							cfP = ConexFlowPost.execute(connection, ConexFlowConstant.PREAUTHORIZATION_OP, query);
							conexFlowOk = cfP.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
						}
						
						if (!conexFlowOk) {
							conexFlowError("Error " + cfP.getRespuesta().getResultado() + ": " + cfP.getRespuesta().getDesResultado() + ".");
						} else {
							// ACTUALIZA VALOR DEL TOKEN EN PROJECT_RESERVATION E INSERTA XML - CREATE TOKEN EN PROJECT ATTACH.
							cfT.setStatus(ConexFlowStatus.CREATE_TOKEN);
							String description = getConexFlowDescription(cfT, token, cfT.getStatus().getName(), null);
							DBConsults.insertConexFlow(getDomain(), getLogin(), cfT, getReservation().getId(), description);

							// INSERTA EL CHECKEO
							cfP.setStatus(ConexFlowStatus.PREAUTHORIZATION_CHECK);
							description = getConexFlowDescription(cfP, token, cfP.getStatus().getName());
							DBConsults.insertConexFlow(getDomain(), getLogin(), cfP, getReservation().getId(), description);

							// CANCELAR PREAUTHORIZACION DE CHECKEO.
							query = ConexFlowUtils.getConexFlowCancelationQuery(connection, ConexFlowConstant.PREAUTHORIZATION_OP, (Double) 0.01
									, (Double) 0.01, cfP.getRespuesta().getAutorizacion(), cfP.getRespuesta().getRefClient()
									, cfP.getRespuesta().getIdOperacion(), cfP.getRespuesta().getFecha(), getReservation().getId());
							ConexFlowPost.execute(connection, ConexFlowConstant.CANCELATION_OP, query);
							// NO SE INSERTA EN LA BASE DE DATOS
						}
					}
				}
			}
		}
		return token;
	}

	public void resetConexflowOperation() {
		setConexflowOperation(null);
		setShowCancelationOption(false);
		setShowRefundOption(false);
	}
	
	public void onConexflowOperationChange(ActionEvent event) {
		if (getConexflowOperation().equals(ConexFlowConstant.CANCELATION_OP)) {
			setShowCancelationOption(true);
			setShowRefundOption(false);
		}
		else if (getConexflowOperation().equals(ConexFlowConstant.REFUND_OP)) {
			setShowCancelationOption(false);
			setShowRefundOption(true);
		}
		else {
			setShowCancelationOption(false);
			setShowRefundOption(false);
		}
	}
	
	public void onConexflowOperation(ActionEvent event) {
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		if (connection.isActive()) {
			String operation = getConexflowOperation();
			switch (operation) {
				case ConexFlowConstant.PREAUTHORIZATION_OP:
					preauthorizationOperation(connection);
					break;
				case ConexFlowConstant.SALE_OP:
					saleOperation(connection);
					break;
				case ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP:
					confirmPreauthorizationOperation(connection);
					break;
				case ConexFlowConstant.CANCELATION_OP:
					cancelOperation(connection);
					break;
				case ConexFlowConstant.REFUND_OP:
					refundOperation(connection);
					break;
				default:
					break;
			}
		}
		setShowNotifyWindow(true);
	}

	private void preauthorizationOperation(ConexFlowConnection connection) {
		String code = getReservation().getCode();
		String token = getReservation().getToken();
		Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection, code, token, getAmount(), getReservation().getId());
		ConexFlow cfP = ConexFlowPost.execute(connection, ConexFlowConstant.PREAUTHORIZATION_OP, query); 
		if (cfP == null) {
			conexFlowError("Error al realizar la operación.");
		} else {
			Boolean ok = cfP.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
			cfP.setStatus(ok ? ConexFlowStatus.PREAUTHORIZATION : ConexFlowStatus.PREAUTHORIZATION_FAIL);
			String description = getConexFlowDescription(cfP, token, cfP.getStatus().getName());
			DBConsults.insertConexFlow(getDomain(), getLogin(), cfP, getReservation().getId(), description);
			if (!ok) {
				conexFlowError("Error " + cfP.getRespuesta().getResultado() + ": " + cfP.getRespuesta().getDesResultado() + ".");
			}
		}
		resetConexflowOperation();
	}

	private void saleOperation(ConexFlowConnection connection) {
		String code = getReservation().getCode();
		String token = getReservation().getToken();
		Query query = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, token, getAmount(), code, getReservation().getId());
		ConexFlow cfV = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, query);
		if (cfV == null) {
			conexFlowError("Error al realizar la operación.");
		} else {
			Boolean ok = cfV.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
			cfV.setStatus(ok ? ConexFlowStatus.SALE : ConexFlowStatus.SALE_FAIL);
			String description = getConexFlowDescription(cfV, token, cfV.getStatus().getName());
			cfV = DBConsults.insertConexFlow(getDomain(), getLogin(), cfV, getReservation().getId(), description);
			if (!ok) {
				conexFlowError("Error " + cfV.getRespuesta().getResultado() + ": " + cfV.getRespuesta().getDesResultado() + ".");
			} else {
				ConexFlowUtils.setVoucher(getDomain(), getLogin(), getReservation().getProject().getId(), cfV);
			}
		}
		resetConexflowOperation();
	}

	private void confirmPreauthorizationOperation(ConexFlowConnection connection) {
		String code = getReservation().getCode();
		String token = getReservation().getToken();
		ConexFlow cfP = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, ConexFlowStatus.PREAUTHORIZATION);
		
		Query query = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection, code, token, getAmount()
				, Double.parseDouble(cfP.getRespuesta().getImporte()), cfP.getRespuesta().getCF_ExpirationDate()
				, cfP.getRespuesta().getAutorizacion(), cfP.getRespuesta().getFecha(), cfP.getRespuesta().getIdOperacion(), getReservation().getId());
		ConexFlow cfC = ConexFlowPost.execute(connection, ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, query);
		if (cfC == null) {
			conexFlowError("Error al realizar la operación.");
		} else {
			Boolean ok = cfC.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
			cfC.setStatus(ok ? ConexFlowStatus.CONFIRM_PREAUTHORIZATION : ConexFlowStatus.CONFIRM_PREAUTHORIZATION_FAIL);
			String description = getConexFlowDescription(cfC, token, cfC.getStatus().getName());
			cfC = DBConsults.insertConexFlow(getDomain(), getLogin(), cfC, getReservation().getId(), description);
			if (!ok) {
				conexFlowError("Error " + cfC.getRespuesta().getResultado() + ": " + cfC.getRespuesta().getDesResultado() + ".");
			} else{
				description = getConexFlowDescription(cfP, token, ConexFlowStatus.PREAUTHORIZATION_PAID.getName());
				DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfP.getId(), description);		
				ConexFlowUtils.setVoucher(getDomain(), getLogin(), getReservation().getId(), cfC);
			}
		}
		resetConexflowOperation();
	}

	private void cancelOperation(ConexFlowConnection connection) {
		String code = getReservation().getCode();
		String token = getReservation().getToken();
		ConexFlowStatus subStatus = ConexFlowStatus.valueOfName(getConexflowOperationCancelation());
		ConexFlow cf = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, subStatus);
		if (cf == null) {
			conexFlowError("Error al realizar la operación, No existe operación cancelable");
		} else {
			Query query = ConexFlowUtils.getConexFlowCancelationQuery(connection, cf.getRespuesta().getOperacion(), getAmount()
					, Double.parseDouble(cf.getRespuesta().getImporte()), cf.getRespuesta().getAutorizacion(), code
					, cf.getRespuesta().getIdOperacion(), cf.getRespuesta().getFecha(), getReservation().getId());
			ConexFlow cfA = ConexFlowPost.execute(connection, ConexFlowStatus.CANCEL.getName(), query);
			if (cfA == null) {
				conexFlowError("Error al realizar la operación.");
			} else {
				Boolean ok = cfA.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
				cfA.setStatus(ok ? ConexFlowStatus.CANCEL : ConexFlowStatus.CANCEL_FAIL);
				String description = getConexFlowDescription(cfA, token, cfA.getStatus().getName());
				DBConsults.insertConexFlow(getDomain(), getLogin(), cfA, getReservation().getId(), description);
				if (!ok) {
					conexFlowError("Error " + cfA.getRespuesta().getResultado() + ": " + cfA.getRespuesta().getDesResultado() + ".");
				} else {
					description = getConexFlowDescription(cf, token, cf.getStatus().cancel().getName());
					DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cf.getId(), description);
					if (ConexFlowStatus.REFUND.equals(subStatus)) {
						ConexFlow cfV = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, 
								ConexFlowStatus.SALE_REFUND);
						ConexFlow cfC = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, 
								ConexFlowStatus.CONFIRM_PREAUTHORIZATION_REFUND);
						if (cfV != null && (cfC == null || cfV.getDate().compareTo(cfC.getDate()) > 0)) {
							description = getConexFlowDescription(cfV, token, ConexFlowStatus.SALE.getName());
							DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfV.getId(), description);	
							// PASAR PAYSLIP A ESTADO ANTERIOR
							String filter = getConexFlowDescription(cfV, token, ConexFlowStatus.PAYSLIP_REFUND.getName() + "#" + cfV.getId());
							description = getConexFlowDescription(cfV, token, ConexFlowStatus.PAYSLIP.getName() + "#" + cfV.getId());
							DBConsults.updateConexFlowPayslipDescription(getDomain(), getLogin(), filter, description);
						} else if (cfC != null) {
							description = getConexFlowDescription(cfC, token, ConexFlowStatus.CONFIRM_PREAUTHORIZATION.getName());
							DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfC.getId(), description);	 
							ConexFlow cfP = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, 
									ConexFlowStatus.PREAUTHORIZATION);
							description = getConexFlowDescription(cfP, token, ConexFlowStatus.PREAUTHORIZATION_PAID.getName());
							DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfC.getId(), description);	 
							// PASAR PAYSLIP A ESTADO ANTERIOR
							String filter = getConexFlowDescription(cfC, token, ConexFlowStatus.PAYSLIP_REFUND.getName() + "#" + cfC.getId());
							description = getConexFlowDescription(cfC, token, ConexFlowStatus.PAYSLIP.getName() + "#" + cfC.getId());
							DBConsults.updateConexFlowPayslipDescription(getDomain(), getLogin(), filter, description);
						}
					} else if(ConexFlowStatus.CONFIRM_PREAUTHORIZATION.equals(subStatus) || ConexFlowStatus.SALE.equals(subStatus)){
						// PASAR PAYSLIP A ESTADO CANCELADO
						String filter = getConexFlowDescription(cf, token, ConexFlowStatus.PAYSLIP.getName() + "#" + cf.getId());
						description = getConexFlowDescription(cf, token, ConexFlowStatus.PAYSLIP_CANCEL.getName() + "#" + cf.getId());
						DBConsults.updateConexFlowPayslipDescription(getDomain(), getLogin(), filter, description);	 
					}
				}
			}
		}
		resetConexflowOperation();
	}

	private void refundOperation(ConexFlowConnection connection) {
		//TODO TENER ENCUENTA EL CARGO O LA CONFIRM PREAUTHO..
		String code = getReservation().getCode();
		String token = getReservation().getToken();
		Query query = ConexFlowUtils.getConexFlowRefundQuery(connection, token, getAmount().toString(), code, getReservation().getId());
		ConexFlow cfD = ConexFlowPost.execute(connection, ConexFlowConstant.REFUND_OP, query);
		if (cfD == null) {
			conexFlowError("Error al realizar la operación.");
		} else {
			Boolean ok = cfD.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
			cfD.setStatus(ok ? ConexFlowStatus.REFUND : ConexFlowStatus.REFUND_FAIL);
			String description = getConexFlowDescription(cfD, token, cfD.getStatus().getName());
			DBConsults.insertConexFlow(getDomain(), getLogin(), cfD, getReservation().getId(), description);
			if (!ok) {
				conexFlowError("Error " + cfD.getRespuesta().getResultado() + ": " + cfD.getRespuesta().getDesResultado() + ".");
			} else {
				ConexFlow cfV = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, ConexFlowStatus.SALE);
				ConexFlow cfC = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, 
						ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
				if (cfV != null && (cfC == null || cfV.getDate().compareTo(cfC.getDate()) > 0)) {
					description = getConexFlowDescription(cfV, token, ConexFlowStatus.SALE_REFUND.getName());
					DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfV.getId(), description);		
					// PASAR PAYSLIP A ESTADO REEMBOLSADO.
					String filter = getConexFlowDescription(cfV, token, ConexFlowStatus.PAYSLIP.getName() + "#" + cfV.getId());
					description = getConexFlowDescription(cfV, token, ConexFlowStatus.PAYSLIP_REFUND.getName() + "#" + cfV.getId());
					DBConsults.updateConexFlowPayslipDescription(getDomain(), getLogin(), filter, description);	 
				} else if (cfC != null) {
					description = getConexFlowDescription(cfC, token, ConexFlowStatus.CONFIRM_PREAUTHORIZATION_REFUND.getName());
					DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfC.getId(), description);	 
					ConexFlow cfP = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, 
							ConexFlowStatus.PREAUTHORIZATION_PAID);
					description = getConexFlowDescription(cfP, token, ConexFlowStatus.PREAUTHORIZATION.getName());
					DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfC.getId(), description);	 
					// PASAR PAYSLIP A ESTADO REEMBOLSADO.
					String filter = getConexFlowDescription(cfC, token, ConexFlowStatus.PAYSLIP.getName() + "#" + cfC.getId());
					description = getConexFlowDescription(cfC, token, ConexFlowStatus.PAYSLIP_REFUND.getName() + "#" + cfC.getId());
					DBConsults.updateConexFlowPayslipDescription(getDomain(), getLogin(), filter, description);	
				}
			}
		}
		resetConexflowOperation();
	}

	public void checkPreauthorization() {
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		if (connection.isActive()) {
			String code = getReservation().getCode();
			String token = getReservation().getToken();
			ConexFlow cfP = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, ConexFlowStatus.PREAUTHORIZATION);
			if (cfP != null) { // CANCEL
				Query query = ConexFlowUtils.getConexFlowCancelationQuery(connection, cfP.getRespuesta().getOperacion(), getAmount()
					, Double.parseDouble(cfP.getRespuesta().getImporte()), cfP.getRespuesta().getAutorizacion(), code
					, cfP.getRespuesta().getIdOperacion(), cfP.getRespuesta().getFecha(), getReservation().getId());
				ConexFlow cfA = ConexFlowPost.execute(connection, ConexFlowStatus.CANCEL.getName(), query);
				if (cfA != null) {
					Boolean ok = cfA.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
					cfA.setStatus(ok ? ConexFlowStatus.CANCEL : ConexFlowStatus.CANCEL_FAIL);
					String description = getConexFlowDescription(cfA, token, cfA.getStatus().getName());
					DBConsults.insertConexFlow(getDomain(), getLogin(), cfA, getReservation().getId(), description);
					if (ok) {
						description = getConexFlowDescription(cfP, token, cfP.getStatus().cancel().getName());
						DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfP.getId(), description);
					}
				}
			}
			Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection, getReservation().getHotelReservation().getId().toString(), 
					token, getReservation().getPenaltyAmount(), getReservation().getId()); 
 			cfP = ConexFlowPost.execute(connection, ConexFlowConstant.PREAUTHORIZATION_OP, query);
 			if (cfP != null) {
 				Boolean ok = cfP.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK);
				cfP.setStatus(ok ? ConexFlowStatus.PREAUTHORIZATION : ConexFlowStatus.PREAUTHORIZATION_FAIL);
				String description = getConexFlowDescription(cfP, token, cfP.getStatus().getName());
				DBConsults.insertConexFlow(getDomain(), getLogin(), cfP, getReservation().getId(), description);
 			}
		}
	}

	public void cancelPreauthorization(ActionEvent event) {
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		setConexflowOperationCancelation(ConexFlowStatus.PREAUTHORIZATION.getName());
		cancelOperation(connection);
	}

	public boolean executeCancellationSale(Double penaltyAmount) {
		ConexFlowConnection connection = DBConsults.getConection(getDomain());
		if (connection.isActive()) {
			String code = getReservation().getCode();
			String token = getReservation().getToken();
			ConexFlow cfC = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), reservation.getId(), token, ConexFlowStatus.CONFIRM_PREAUTHORIZATION);
			ConexFlow cfV = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), reservation.getId(), token, ConexFlowStatus.SALE);
			if ((cfC != null && Double.parseDouble(cfC.getRespuesta().getImporte()) >= penaltyAmount) || 
				(cfV != null && Double.parseDouble(cfV.getRespuesta().getImporte()) >= penaltyAmount)) {
				return true;
			} else {
				cfC = null;
				ConexFlow cfP = DBConsults.getConexFlowLastStatusX(getDomain(), getLogin(), getReservation().getId(), token, ConexFlowStatus.PREAUTHORIZATION);
				if (cfP != null && Double.parseDouble(cfP.getRespuesta().getImporte()) >= penaltyAmount) {
					Query query = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection, code, token, penaltyAmount, 
							Double.parseDouble(cfP.getRespuesta().getImporte()), cfP.getRespuesta().getFecha(),
							cfP.getRespuesta().getAutorizacion(), cfP.getRespuesta().getFechaOriginal(), cfP.getRespuesta().getOperacion(), getReservation().getId());
					cfC = ConexFlowPost.execute(connection, ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, query);

					Boolean ok = CONEXFLOW_RESULT_OK.equals(cfC.getRespuesta().getResultado());
					cfC.setStatus(ok ? ConexFlowStatus.CONFIRM_PREAUTHORIZATION : ConexFlowStatus.CONFIRM_PREAUTHORIZATION_FAIL);
					String description = getConexFlowDescription(cfC, token, cfC.getStatus().getName());
					DBConsults.insertConexFlow(getDomain(), getLogin(), cfC, reservation.getId(), description);
					if (ok) {
						description = getConexFlowDescription(cfP, token, ConexFlowStatus.PREAUTHORIZATION_PAID.getName());
						DBConsults.updateConexFlowDescription(getDomain(), getLogin(), cfP.getId(), description);	
						// CREACION  DEL PAYSLIP PARA LA CONFIRMACION DE PREAUTHORIZACION
						ConexFlowUtils.setVoucher(getDomain(), getLogin(), getReservation().getId(), cfC);
						return true;
					}
				}

				if (cfC == null || (!CONEXFLOW_RESULT_OK.equals(cfC.getRespuesta().getResultado()))) {
					Query query = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, token, penaltyAmount, code, getReservation().getId());
					cfV = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, query);
					
					Boolean ok = CONEXFLOW_RESULT_OK.equals(cfV.getRespuesta().getResultado());
					cfV.setStatus(ok ? ConexFlowStatus.SALE : ConexFlowStatus.SALE_FAIL);
					String description = getConexFlowDescription(cfV, token, cfV.getStatus().getName());
					DBConsults.insertConexFlow(getDomain(), getLogin(), cfV, reservation.getId(), description);
					if (ok) {
						// CREACION  DEL PAYSLIP PARA LA CARGO DIRECTO
						ConexFlowUtils.setVoucher(getDomain(), getLogin(), getReservation().getId(), cfV);
						return true;
					}
				}
			}
		}
		return false;
	}


	private Boolean isAmex(String creditCard) {
		return creditCard.substring(0, 2).equals("37") || creditCard.substring(0, 2).equals("34");
	}

	private String getConexFlowDescription(ConexFlow cf, String token, String status) {
		return getConexFlowDescription(cf, token, status, cf.getRespuesta().getImporte());
	}

	private String getConexFlowDescription(ConexFlow cf, String token, String status, String amount) {
		StringBuffer description = new StringBuffer();
		description.append(CONEXFLOW_PREFIX);
		description.append("(");
		description.append(token.substring(token.length()-5));
		description.append(")_");
		description.append(status);
		description.append("#");
		if (amount != null) {
			description.append(amount);
		}
		if (cf.isAntTnr()) {
			description.append("_ANT_TNR");
		}
		return description.toString();
	}

	private void conexFlowError(String error){
		AonUtil.addErrorMessage(error);
		throw new AbortProcessingException(error);
	}

	public PayMethod getConexFlowDefaultPayMethod() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain().getId());
		return reservationUtils.obtainConexFlowPayMethod();
	}

	public String getPrintConexFlowPayslip() {
		String str = "domain_name="+ getDomain().getName() + "&domain_id="+ getDomain().getId()+ "&login="+ getLogin() + "&project=" + getReservation().getId();
		String base = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		return "/print_conexflowpayslip/"+ base;
	}
	
	public String getPrintConexFlowLog() {
		String str = "domain_name="+ getDomain().getName() + "&domain_id="+ getDomain().getId()+ "&login="+ getLogin() + "&project=" + getReservation().getId();
		String base = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		return "/print_conexflow_log/"+ base;
	}

}
