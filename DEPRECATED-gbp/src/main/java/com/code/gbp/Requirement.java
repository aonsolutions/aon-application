package com.code.gbp;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.DocumentType;

@Entity
@Table(name="requirement")
public class Requirement implements ITransferObject {

	private Integer id;

	private Date applicationDate;
	
	private DocumentType documentType;
	
	private BigDecimal amount;
	
	private int managerNumber;
	
	private int supervisorNumber;
	
	private boolean system;

	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="application_date", nullable=false)
	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	@Column(name="document_type", nullable=false)
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name="manager_number")
	public int getManagerNumber() {
		return managerNumber;
	}

	public void setManagerNumber(int managerNumber) {
		this.managerNumber = managerNumber;
	}

	@Column(name="supervisor_number")
	public int getSupervisorNumber() {
		return supervisorNumber;
	}

	public void setSupervisorNumber(int supervisorNumber) {
		this.supervisorNumber = supervisorNumber;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}
}