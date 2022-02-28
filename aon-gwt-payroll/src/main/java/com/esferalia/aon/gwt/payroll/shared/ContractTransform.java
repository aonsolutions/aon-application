package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ContractTransform implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer domainId;
	private Integer contractId;
	private String tc2;
	private Date contractStartDate;
	private Boolean discontinuosInd;
	private String cno;
	private String sepeId;
	
	private String signBasicCopy;
	private String basicCopy;
	
	public ContractTransform() {
		super();
	}

	public Integer getDomainId() {
		return domainId;
	}

	public ContractTransform setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public Integer getContractId() {
		return contractId;
	}

	public ContractTransform setContractId(Integer contractId) {
		this.contractId = contractId;
		return this;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public ContractTransform setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
		return this;
	}

	public String getSepeId() {
		return sepeId;
	}

	public ContractTransform setSepeId(String sepeId) {
		this.sepeId = sepeId;
		return this;
	}

	public String getTc2() {
		return tc2;
	}

	public ContractTransform setTc2(String tc2) {
		this.tc2 = tc2;
		return this;
	}

	public Boolean getDiscontinuosInd() {
		return discontinuosInd;
	}

	public ContractTransform setDiscontinuosInd(Boolean discontinuosInd) {
		this.discontinuosInd = discontinuosInd;
		return this;
	}

	public String getCno() {
		return cno;
	}

	public ContractTransform setCno(String cno) {
		this.cno = cno;
		return this;
	}

	public String getSignBasicCopy() {
		return signBasicCopy;
	}

	public ContractTransform setSignBasicCopy(String signBasicCopy) {
		this.signBasicCopy = signBasicCopy;
		return this;
	}

	public String getBasicCopy() {
		return basicCopy;
	}

	public ContractTransform setBasicCopy(String basicCopy) {
		this.basicCopy = basicCopy;
		return this;
	}
	
	
	
}
