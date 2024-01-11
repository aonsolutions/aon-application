package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;

public class FBatchDetail implements Serializable, HasAudit {
	
	private static final long serialVersionUID = 1181685270744293278L;

	private boolean removed;
	
	private Integer id;
	private Integer domain;
	private Integer fbatch;
	private Finance finance;
	private Double amount;
	private Byte status;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}

	public FBatchDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}

	public FBatchDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getFbatch() {
		return fbatch;
	}

	public FBatchDetail setFbatch(Integer fbatch) {
		this.fbatch = fbatch;
		return this;
	}

	public Finance getFinance() {
		return finance;
	}

	public FBatchDetail setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}

	public Double getAmount() {
		return amount;
	}

	public FBatchDetail setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public FBatchDetail setStatus(Byte status) {
		this.status = status;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	
	public String getCreationUser() {
		return creationUser;
	}

	public FBatchDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public FBatchDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public FBatchDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public FBatchDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	// ---------------------------------------------------------- 
	
	public boolean isRemoved() {
		return removed;
	}
	
	public FBatchDetail setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}

}
