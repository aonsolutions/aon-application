package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenBankAccount implements Serializable {

	private static final long serialVersionUID = 1175684863512830855L;

	private RegistryBank rbank;
	private RegistryAddInfo raddInfo;
	private NordigenAccountMetadata metadata;
	private NordigenAccountDetails details;
	private List<NordigenAccountBalance> balances;
	private NordigenRequisition requisition;
	private NordigenInstitution institution;
	
	private boolean isLinked;
	private String iban;
	private String bankAlias;
	private Date lastMovementDate;
	private Set<String> logs;
	private String requisitionId;
	
	private List<NordigenBankStatement> notInsertedMovements;
	
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
	public NordigenAccountDetails getDetails() {
		return details;
	}
	public NordigenBankAccount setDetails(NordigenAccountDetails details) {
		this.details = details;
		return this;
	}
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
	public List<NordigenAccountBalance> getBalances() {
		if (balances == null)
			return Collections.emptyList();
		return balances;
	}
	public NordigenBankAccount setBalances(List<NordigenAccountBalance> balances) {
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
	public Set<String> getLogs() {
		if (logs == null) {
			logs = new LinkedHashSet<>();
		}
		return logs;
	}
	public Set<String> addLog(String log) {
		if (logs == null) {
			logs = new LinkedHashSet<>();
		}
		if (AonStringUtils.isNotBlank(log)) {
			logs.add(log);
		}
		return logs;
	}
	public NordigenBankAccount setLogs(Set<String> logs) {
		if (logs != null) {			
			this.logs = logs;
		}
		return this;
	}
	public List<NordigenBankStatement> getNotInsertedMovements() {
		return notInsertedMovements;
	}
	public NordigenBankAccount setNotInsertedMovements(List<NordigenBankStatement> notInsertedMovements) {
		this.notInsertedMovements = notInsertedMovements;
		return this;
	}

	
	
}
