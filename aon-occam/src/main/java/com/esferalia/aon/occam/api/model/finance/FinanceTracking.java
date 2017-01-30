// AON-ENTITY Nov 6, 2012 12:29:06 PM - 3.2.2.GA
package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;

public class FinanceTracking implements Serializable, HasAudit  {

	private static final long serialVersionUID = -5057462585779434987L;
	
	private boolean deleted;
	private boolean checked;
	
	private boolean lastTracking;
	
	private Integer id;
	private Integer registryBank;
	private Integer bankStatementLink;
	private Finance finance;
	private Integer payMethodTypeDetail;
	private Integer domain;
	private Date trackingDate;
	private FinanceTrackingType type;
	private String description;
	private double amount;
	private boolean recorded;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Integer getId() {
		return this.id;
	}
	public FinanceTracking setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getRegistryBank() {
		return this.registryBank;
	}
	public FinanceTracking setRegistryBank(Integer registryBank) {
		this.registryBank = registryBank;
		return this;
	}

	public Integer getBankStatementLink() {
		return this.bankStatementLink;
	}
	public FinanceTracking setBankStatementLink(Integer bankStatementLink) {
		this.bankStatementLink = bankStatementLink;
		return this;
	}

	public Finance getFinance() {
		return this.finance;
	}
	public FinanceTracking setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}

	public Integer getPayMethodTypeDetail() {
		return this.payMethodTypeDetail;
	}
	public FinanceTracking setPayMethodTypeDetail(Integer payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public FinanceTracking setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Date getTrackingDate() {
		return this.trackingDate;
	}
	public FinanceTracking setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
		return this;
	}

	public FinanceTrackingType getType() {
		return this.type;
	}
	public FinanceTracking setType(FinanceTrackingType type) {
		this.type = type;
		return this;
	}

	public String getDescription() {
		return this.description;
	}
	public FinanceTracking setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getAmount() {
		return this.amount;
	}
	public FinanceTracking setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public boolean isRecorded() {
		return this.recorded;
	}
	public FinanceTracking setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}

	public String getCreationUser() {
		return this.creationUser;
	}
	public FinanceTracking setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}
	public FinanceTracking setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return this.modificationUser;
	}
	public FinanceTracking setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return this.modificationDate;
	}
	public FinanceTracking setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public boolean isLastTracking() {
		return lastTracking;
	}
	public FinanceTracking setLastTracking(boolean lastTracking) {
		this.lastTracking = lastTracking;
		return this;
	}

	public boolean isChecked() {
		return checked;
	}
	public FinanceTracking setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public FinanceTracking setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
}

