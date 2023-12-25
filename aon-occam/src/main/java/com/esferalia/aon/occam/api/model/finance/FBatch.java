package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class FBatch implements Serializable, HasAudit {
	
	private static final long serialVersionUID = 1362357187101112851L;

	private boolean selected;
	private boolean removed;
	
	private Integer id;
	private Integer domain;
	private String description;
	private Date issueDate;
	private Byte type;
	private FBatchStatus status;
	private RegistryBank rbank;
	private Integer bankStatementLink;
	private Byte payment;
	private SecurityLevel securityLevel;
	private Integer rattach;
	
	private List<FBatchDetail> batchDetails;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}

	public FBatch setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}

	public FBatch setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public FBatch setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public FBatch setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public Byte getType() {
		return type;
	}

	public FBatch setType(Byte type) {
		this.type = type;
		return this;
	}

	public FBatchStatus getStatus() {
		if(status == null) {
			status = FBatchStatus.UNKNOWN;
		}
		return status;
	}

	public FBatch setStatus(FBatchStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isUnknown() {
		return FBatchStatus.UNKNOWN == getStatus();
	}
	
	public boolean isPending() {
		return FBatchStatus.PENDING == getStatus();
	}
	
	public boolean isGenerated() {
		return FBatchStatus.GENERATED == getStatus();
	}
	
	public boolean isAccounted() {
		return FBatchStatus.ACCOUNTED == getStatus();
	}

	public RegistryBank getRbank() {
		return rbank;
	}

	public FBatch setRbank(RegistryBank rbank) {
		this.rbank = rbank;
		return this;
	}

	public Integer getBankStatementLink() {
		return bankStatementLink;
	}

	public FBatch setBankStatementLink(Integer bankStatementLink) {
		this.bankStatementLink = bankStatementLink;
		return this;
	}

	public Byte getPayment() {
		return payment;
	}

	public FBatch setPayment(Byte payment) {
		this.payment = payment;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public FBatch setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	
	public FBatch setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}

	public Integer getRattach() {
		return rattach;
	}

	public FBatch setRattach(Integer rattach) {
		this.rattach = rattach;
		return this;
	}
	
	public List<FBatchDetail> getBatchDetails() {
		return batchDetails;
	}

	public FBatch setBatchDetails(List<FBatchDetail> batchDetails) {
		this.batchDetails = batchDetails;
		return this;
	}
	
	public void addBatchDetail(FBatchDetail fbatchDetail) {
		if(null == getBatchDetails()) batchDetails = new ArrayList<>();
		Optional<FBatchDetail> fbatchDetailOpt = batchDetails.stream().filter(fbatchDetial -> null != fbatchDetial.getFinance() && fbatchDetial.getFinance().getId() == fbatchDetail.getFinance().getId()).findFirst();
		if(!fbatchDetailOpt.isPresent()) batchDetails.add(fbatchDetail);
	}

	// ---------------------------------------------------------- AUDIT

	public String getCreationUser() {
		return creationUser;
	}

	public FBatch setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public FBatch setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public FBatch setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public FBatch setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	// ---------------------------------------------------------- 
	
	public boolean isRemoved() {
		return removed;
	}
	
	public FBatch setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public FBatch setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

}
