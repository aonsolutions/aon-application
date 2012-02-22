package com.code.aon.accounting;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;

@Entity
@Table(name = "amortization")
public class Amortization implements ITransferObject,IConfidentialable {

	private static final long serialVersionUID = 7370145682918674752L;

	private Integer id;
	private String description;
    private AmortizationType amortizationType;
    private Date initialDate;
    private Date deadline;
    private Double amount;
    private AmortizationPeriod feePeriod;
    private Double saleAmount;
	private String comments;
    private Account fixedAssetAccount;
    private Account accumulatedAccount;
    private Account allocationAccount;
    private double percentage;
    private SecurityLevel securityLevel;
	
	private List<AmortizationDetail> details;
	
	@Transient
	private boolean detailsInitialized;

	@Id
    @GeneratedValue	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable = false, length = 64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="amortization_type", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_AMORTIZATION_TYPE")
	@Index(name = "IDX_AMORTIZATION_AMORTIZATION_TYPE")	
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}

	@Column(name = "initial_date", nullable = false)
	@Temporal(TemporalType.DATE)	
	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(nullable = false)
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
	
	@Column(name = "fee_period", nullable = false)
    public AmortizationPeriod getFeePeriod() {
        return feePeriod;
    }

    public void setFeePeriod(AmortizationPeriod feePeriod) {
        this.feePeriod = feePeriod;
    }
	
	@Column(name = "sale_amount")
    public Double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        this.saleAmount = saleAmount;
    }
	
	
	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="fixed_asset_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_FIXED_ASSET_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT")							
	public Account getFixedAssetAccount() {
		return fixedAssetAccount;
	}

	public void setFixedAssetAccount(Account fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="accumulated_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_ACCUMULATED_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_ACCUMULATED_ACCOUNT")						
	public Account getAccumulatedAccount() {
		return accumulatedAccount;
	}

	public void setAccumulatedAccount(Account accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="allocation_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_ALLOCATION_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_ALLOCATION_ACCOUNT")					
	public Account getAllocationAccount() {
		return allocationAccount;
	}

	public void setAllocationAccount(Account allocationAccount) {
		this.allocationAccount = allocationAccount;
	}

	@Column(nullable=true)
    public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

    @Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    
	@Transient
	@Override
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	@Override
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Transient
    public int getYears() {
		if (percentage!= 0) {
			return (int) CommonUtil.round( 100 / percentage,0);	
		}
		return 0;
	}

	public void setYears(int years) {
		if (years != 0) {
			setPercentage(CommonUtil.round( 100.0 / years));
		} else {
			setPercentage(0);	
		}
	}

	@OneToMany(mappedBy = "amortization", cascade={CascadeType.REMOVE})
	public List<AmortizationDetail> getDetails() {
		if (!isDetailsInitialized()) {
			calculateTotals(details);
			setDetailsInitialized(true);
		}
		return details;
	}

	public void setDetails(List<AmortizationDetail> details) {
		this.details = details;
	}

	
	@Transient
	public boolean isDetailsInitialized() {
		return detailsInitialized;
	}

	public void setDetailsInitialized(boolean detailsInitialized) {
		this.detailsInitialized = detailsInitialized;
	}

	@Transient
	public void calculateTotals(List<AmortizationDetail> list)  {
		if (list != null) {
			double accumulated = 0.0;
			double pending = 0.0;
			double fiscalAccumulated = 0.0;
			double fiscalPending = 0.0;
			boolean first = true;
			for (AmortizationDetail detail: list) {
				if (first) {
					pending = detail.getAmortization().getAmount();
					fiscalPending = detail.getAmortization().getAmount();
					first = false;
				}
				accumulated = CommonUtil.round(accumulated + detail.getAllocation());
				fiscalAccumulated = CommonUtil.round(fiscalAccumulated + detail.getFiscalAllocation());
	
				pending = CommonUtil.round(pending - detail.getAllocation());
				fiscalPending = CommonUtil.round(fiscalPending - detail.getFiscalAllocation());
	
				detail.setAccumulated(accumulated);
				detail.setFiscalAccumulated(fiscalAccumulated);
				detail.setPending(pending);
				detail.setFiscalPending(fiscalPending);
			}
		}
	}

	@Transient
	public boolean isYearly()  {
		return (getFeePeriod() == AmortizationPeriod.YEARLY);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Amortization o = (Amortization) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getAmortizationType(), o.getAmortizationType())
			.append(this.getAmount(), o.getAmount())
			.append(this.getDescription(), o.getDescription())
			.append(this.getInitialDate(), o.getInitialDate())
			.append(this.getDeadline(), o.getDeadline())
			.append(this.getFeePeriod(), o.getFeePeriod())
			.append(this.getSaleAmount(), o.getSaleAmount())
			.append(this.getComments(), o.getComments())
			.append(this.getFixedAssetAccount(), o.getFixedAssetAccount())
			.append(this.getAccumulatedAccount(), o.getAccumulatedAccount())
			.append(this.getAllocationAccount(), o.getAllocationAccount())
			.append(this.getPercentage(), o.getPercentage())
			.append(this.getSecurityLevel(), o.getSecurityLevel())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getAmortizationType())
			.append(this.getAmount())
			.append(this.getDescription())
			.append(this.getInitialDate())
			.append(this.getDeadline())
			.append(this.getFeePeriod())
			.append(this.getSaleAmount())
			.append(this.getComments())
			.append(this.getFixedAssetAccount())
			.append(this.getAccumulatedAccount())
			.append(this.getAllocationAccount())
			.append(this.getPercentage())
			.append(this.getSecurityLevel())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}