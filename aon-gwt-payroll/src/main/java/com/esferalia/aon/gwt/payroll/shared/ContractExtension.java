package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ContractExtension implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer domainId;
	private Integer contractId;
	private Date contractStartDate;
	private Date newContractStartDate;
	private Date newContractEndDate;
	
	private String sepeId;
	private Boolean discontinuosInd;
	private Boolean enterpriseInd;
	private String freeEnterprise;
	
	public ContractExtension() {
		super();
	}

	public Integer getDomainId() {
		return domainId;
	}

	public ContractExtension setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public Integer getContractId() {
		return contractId;
	}

	public ContractExtension setContractId(Integer contractId) {
		this.contractId = contractId;
		return this;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public ContractExtension setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
		return this;
	}

	public Date getNewContractStartDate() {
		return newContractStartDate;
	}

	public ContractExtension setNewContractStartDate(Date newContractStartDate) {
		this.newContractStartDate = newContractStartDate;
		return this;
	}

	public Date getNewContractEndDate() {
		return newContractEndDate;
	}

	public ContractExtension setNewContractEndDate(Date newContractEndDate) {
		this.newContractEndDate = newContractEndDate;
		return this;
	}

	public String getSepeId() {
		return sepeId;
	}

	public ContractExtension setSepeId(String sepeId) {
		this.sepeId = sepeId;
		return this;
	}

	public Boolean getDiscontinuosInd() {
		return discontinuosInd;
	}

	public ContractExtension setDiscontinuosInd(Boolean discontinuosInd) {
		this.discontinuosInd = discontinuosInd;
		return this;
	}

	public Boolean getEnterpriseInd() {
		return enterpriseInd;
	}

	public ContractExtension setEnterpriseInd(Boolean enterpriseInd) {
		this.enterpriseInd = enterpriseInd;
		return this;
	}

	public String getFreeEnterprise() {
		return freeEnterprise;
	}

	public ContractExtension setFreeEnterprise(String freeEnterprise) {
		this.freeEnterprise = freeEnterprise;
		return this;
	}
	
}
