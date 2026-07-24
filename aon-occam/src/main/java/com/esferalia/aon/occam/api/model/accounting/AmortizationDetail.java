package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasId;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.watson.util.AonUtils;

public class AmortizationDetail implements Serializable, HasId {

	private static final long serialVersionUID = -2788114698849263524L;

	private boolean dirty;
	private boolean selected;
	private boolean deleted;
	
	private Integer id;
	private Integer domain;
	private Integer amortization;
	private Integer accountEntry;
	private Date fromDate;
	private Date toDate;
	private double coefficient;
	private double allocation;
	private AmortizationDetailStatus status;
	private double fiscalAllocation;

	private double accumulated;
	private double pending;
	private double fiscalAccumulated;
	private double fiscalPending;
	
	public boolean isSelected() {
		return selected;
	}
	public AmortizationDetail setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public boolean isNotDeleted() {
		return !isDeleted();
	}
	public AmortizationDetail setDeleted(boolean deleted) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.deleted, deleted) );
		this.deleted = deleted;
		return this;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	public AmortizationDetail setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id, id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public AmortizationDetail setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain, domain) );
		this.domain = domain;
		return this;
	}
	
	public Integer getAmortization() {
		return amortization;
	}
	public AmortizationDetail setAmortization(Integer amortization) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.amortization, amortization) );
		this.amortization = amortization;
		return this;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}
	public AmortizationDetail setAccountEntry(Integer accountEntry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.accountEntry, accountEntry) );
		this.accountEntry = accountEntry;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public AmortizationDetail setFromDate(Date fromDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.fromDate, fromDate) );
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public AmortizationDetail setToDate(Date toDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.toDate, toDate) );
		this.toDate = toDate;
		return this;
	}

	public double getCoefficient() {
		return coefficient;
	}
	public AmortizationDetail setCoefficient(double coefficient) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.coefficient, coefficient) );
		this.coefficient = coefficient;
		return this;
	}

	public double getAllocation() {
		return allocation;
	}
	public AmortizationDetail setAllocation(double allocation) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.allocation, allocation) );
		this.allocation = allocation;
		return this;
	}

	public AmortizationDetailStatus getStatus() {
		return status;
	}
	public AmortizationDetail setStatus(AmortizationDetailStatus status) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.status, status) );
		this.status = status;
		return this;
	}
	public boolean isPending() 		{ return getStatus() == AmortizationDetailStatus.PENDING;}
	public boolean isBlocked() 		{ return getStatus() == AmortizationDetailStatus.BLOCKED;}
	public boolean isScored() 		{ return getStatus() == AmortizationDetailStatus.SCORED;}
	public boolean isNotScored() 	{ return !isScored();}

	public double getFiscalAllocation() {
		return fiscalAllocation;
	}
	public AmortizationDetail setFiscalAllocation(double fiscalAllocation) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.fiscalAllocation, fiscalAllocation) );
		this.fiscalAllocation = fiscalAllocation;
		return this;
	}

	public double getAccumulated() {
		return accumulated;
	}
	public AmortizationDetail setAccumulated(double accumulated) {
		this.accumulated = accumulated;
		return this;
	}

	public double getPending() {
		return pending;
	}
	public AmortizationDetail setPending(double pending) {
		this.pending = pending;
		return this;
	}

	public double getFiscalAccumulated() {
		return fiscalAccumulated;
	}
	public AmortizationDetail setFiscalAccumulated(double fiscalAccumulated) {
		this.fiscalAccumulated = fiscalAccumulated;
		return this;
	}

	public double getFiscalPending() {
		return fiscalPending;
	}
	public AmortizationDetail setFiscalPending(double fiscalPending) {
		this.fiscalPending = fiscalPending;
		return this;
	}
	
	// ---------------------------------------------------------- DIRTY
	public boolean isDirty() {
		return dirty;
	}
	public AmortizationDetail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
}
