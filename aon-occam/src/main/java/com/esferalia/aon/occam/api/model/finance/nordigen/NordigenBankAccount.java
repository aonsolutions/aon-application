package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenBankAccount implements Serializable {

	private static final long serialVersionUID = 1175684863512830855L;

	private RegistryBank rbank;
	private RegistryAddInfo raddInfo;
	private NordigenAccountMetadata metadata;
//	private NordigenAccountDetail detail;
	private LinkedList<NordigenAccountBalance> balances;
	private NordigenRequisition requisition;
	private NordigenInstitution institution;
	
	private boolean isLinked;
	private String iban;
	private String bankAlias;
	private Date lastMovementDate;
	private LinkedHashSet<String> logs;
	private String requisitionId;
	
	private LinkedList<NordigenBankStatement> notInsertedMovements;
	
	public RegistryBank getRbank() {
		return rbank;
	}
	public NordigenBankAccount setRbank(RegistryBank rbank) {
		this.rbank = rbank;
		return this;
	}
	public NordigenAccountMetadata getMetadata() {
		return metadata;
	}
	public NordigenBankAccount setMetadata(NordigenAccountMetadata metadata) {
		this.metadata = metadata;
		return this;
	}
//	public NordigenAccountDetail getDetail() {
//		return detail;
//	}
//	public NordigenBankAccount setDetail(NordigenAccountDetail detail) {
//		this.detail = detail;
//		return this;
//	}
	public NordigenRequisition getRequisition() {
		return requisition;
	}
	public NordigenBankAccount setRequisition(NordigenRequisition requisition) {
		this.requisition = requisition;
		return this;
	}
	public NordigenInstitution getInstitution() {
		return institution;
	}
	public NordigenBankAccount setInstitution(NordigenInstitution institution) {
		this.institution = institution;
		return this;
	}
	public boolean isLinked() {
		return isLinked;
	}
	public NordigenBankAccount setLinked(boolean isLinked) {
		this.isLinked = isLinked;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public NordigenBankAccount setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getRequisitionId() {
		return requisitionId;
	}
	public NordigenBankAccount setRequisitionId(String requisitionId) {
		this.requisitionId = requisitionId;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public NordigenBankAccount setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public LinkedList<NordigenAccountBalance> getBalances() {
		if (balances == null) return new LinkedList<>();
		return balances;
	}
	public NordigenBankAccount setBalances(LinkedList<NordigenAccountBalance> balances) {
		this.balances = balances;
		return this;
	}
	public RegistryAddInfo getRaddInfo() {
		return raddInfo;
	}
	public NordigenBankAccount setRaddInfo(RegistryAddInfo raddInfo) {
		this.raddInfo = raddInfo;
		return this;
	}
	public Date getLastMovementDate() {
		return lastMovementDate;
	}
	public NordigenBankAccount setLastMovementDate(Date lastMovementDate) {
		this.lastMovementDate = lastMovementDate;
		return this;
	}
	public LinkedHashSet<String> getLogs() {
		if (logs == null) {
			logs = new LinkedHashSet<>();
		}
		return logs;
	}
	public LinkedHashSet<String> addLog(String log) {
		if (logs == null) {
			logs = new LinkedHashSet<>();
		}
		if (AonStringUtils.isNotBlank(log)) {
			logs.add(log);
		}
		return logs;
	}
	public NordigenBankAccount setLogs(LinkedHashSet<String> logs) {
		if (logs != null) {		
			this.logs = logs;
		}
		return this;
	}
	public LinkedList<NordigenBankStatement> getNotInsertedMovements() {
		return notInsertedMovements;
	}
	public NordigenBankAccount setNotInsertedMovements(LinkedList<NordigenBankStatement> notInsertedMovements) {
		this.notInsertedMovements = notInsertedMovements;
		return this;
	}

	
	public static NordigenBankStatement  toBankStatement(NordigenBankAccount account, NordigenAccountTransaction transaction) {
		NordigenBankStatement statement = new NordigenBankStatement();
		double amount = NordigenAccountAmount.getAmount( transaction.getTransactionAmount());
		boolean bpayment = AonMathUtils.isLessThanZero( amount );
		String description = "";
		if (AonStringUtils.isNotBlank(transaction.getRemittanceInformationUnstructured())) {
			description = transaction.getRemittanceInformationUnstructured();
		} else if (AonStringUtils.isNotBlank(transaction.getRemittanceInformationStructured())) {				
			description = transaction.getRemittanceInformationStructured();
		}
		description = AonStringUtils.substring(description, 0, 80);
		
		StringBuilder sb = new StringBuilder();
		if (AonStringUtils.isNotBlank(transaction.getTransactionId())) {
			sb.append(transaction.getTransactionId());
		} else if (AonStringUtils.isNotBlank(transaction.getInternalTransactionId())) {
			sb.append(transaction.getInternalTransactionId());				
		}
		statement
			.setDomain(account.getRbank().getDomain())
			.setRegistryBank(account.getRbank())
			.setOperationDate(transaction.getBookingDate() != null ? transaction.getBookingDate() : new Date())
			.setCommonConcept(StatementConcept.UNKNOWN)
			.setPayment(bpayment)
			.setAmount(Math.abs(amount))
			.setDescription(description)
			.setStatus(StatementStatus.PENDING)
			.setReference1("NORDIGEN")
			.setReference2(AonStringUtils.trimToNull(AonStringUtils.substring(sb.toString(), 0, 64)));
		
		String id = null;
		if (AonStringUtils.isNotBlank(transaction.getTransactionId())) {
			id = transaction.getTransactionId();
		} else if (AonStringUtils.isNotBlank(transaction.getInternalTransactionId())) {
			id = transaction.getInternalTransactionId();
		}
		statement.setNordigenMovementId(id);
		return statement;
	}
	
}
