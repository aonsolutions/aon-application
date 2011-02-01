package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;

/**
 * Entity class for representing an account.
 * 
 * @author Consulting & Development. ecastellano - 22/01/2007
 * 
 */
@Entity
@Table(name = "amortization_detail")
public class AmortizationDetail implements ITransferObject {

	private static final long serialVersionUID = -7469783739232294839L;

	private Integer id;
	private Amortization amortization;
    private Date fromDate;
    private Date toDate;
    private Double coefficient;
    private Double allocation;
    private Double fiscalAllocation;
    private AmortizationDetailStatus status;
    private AccountEntry accountEntry;
    private Double accumulated;
    private Double pending;
    private Double fiscalAccumulated;
	private Double fiscalPending;
	private boolean checked;

	@Id
    @GeneratedValue	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="amortization", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_DETAIL_AMORTIZATION")
	@Index(name = "IDX_AMORTIZATION_DETAIL_AMORTIZATION")		
	public Amortization getAmortization() {
		return amortization;
	}

	public void setAmortization(Amortization amortization) {
		this.amortization = amortization;
	}

	@Column(name = "from_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@Column(name = "to_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Column(nullable = false, precision=15, scale=3)
    public Double getAllocation() {
        return allocation;
    }

    public void setAllocation(Double allocation) {
        this.allocation = allocation;
    }
	
	@Column(name = "fiscal_allocation", nullable = false, precision=15, scale=3)
    public Double getFiscalAllocation() {
        return fiscalAllocation;
    }

    public void setFiscalAllocation(Double fiscalAllocation) {
        this.fiscalAllocation = fiscalAllocation;
    }

    @Column(nullable = false, precision=15, scale=3)
    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Column(name = "status", nullable = false)
    public AmortizationDetailStatus getStatus() {
        return status;
    }

    public void setStatus(AmortizationDetailStatus status) {
        this.status = status;
    }
	
	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account_entry")
	@ForeignKey(name = "FK_AMORTIZATION_DETAIL_ACCOUNT_ENTRY")
	@Index(name = "IDX_AMORTIZATION_DETAIL_ACCOUNT_ENTRY")			
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AmortizationDetail) {
			AmortizationDetail account = (AmortizationDetail) obj;
			if (ObjectUtils.equals(getId(), account.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}