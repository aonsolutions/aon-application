package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.AmortizationDetailDB;

@Entity
@Table(name="amortization_detail")
public class AmortizationDetail extends AmortizationDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Double accumulated;
	private Double pending;
	private Double fiscalAccumulated;
	private Double fiscalPending;
	private boolean checked;
	
	@Transient
    public Double getAccumulated() {
		return accumulated;
	}
	public void setAccumulated(Double accumulated) {
		this.accumulated = accumulated;
	}

	@Transient
	public Double getPending() {
		return pending;
	}
	public void setPending(Double pending) {
		this.pending = pending;
	}

	@Transient
	public Double getFiscalAccumulated() {
		return fiscalAccumulated;
	}
	public void setFiscalAccumulated(Double fiscalAccumulated) {
		this.fiscalAccumulated = fiscalAccumulated;
	}

	@Transient
	public Double getFiscalPending() {
		return fiscalPending;
	}
	public void setFiscalPending(Double fiscalPending) {
		this.fiscalPending = fiscalPending;
	}

	@Transient
	public double getTaxAdjust() {
		double a = getAllocation()==null?0.0:getAllocation();
		double b = getFiscalAllocation()==null?0.0:getFiscalAllocation();
		return CommonUtil.round(a-b);
	}
	
	@Transient
	public boolean isUpdatable() {
		return getStatus() == AmortizationDetailStatus.PENDING;
	}
	@Transient
	public boolean isBlocked() {
		return getStatus() == AmortizationDetailStatus.BLOCKED;
	}
	@Transient
	public boolean isScored() {
		return getStatus() == AmortizationDetailStatus.SCORED;
	}

	@Transient
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
