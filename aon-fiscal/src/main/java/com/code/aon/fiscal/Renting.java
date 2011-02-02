package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.Administration;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "fs_renting")
public class Renting implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = 5692053383866684819L;

    private Integer id;
	private Integer year;
	private Period period;
	private Administration administration;
	private String comments;
	private RentingStatus status;
	private SecurityLevel securityLevel;
	private boolean complementary;
	private boolean replacement;
	private double lessorCountAccumulated;
	private double lessorCountDeclared;
	private double lessorCountResult;
	private double lessorCountAdjust;
	private double lessorCount;
	private double rentingAmountAccumulated;
	private double rentingAmountDeclared;
	private double rentingAmountResult;
	private double rentingAmountAdjust;
	private double rentingAmount;
	private double retentionAccumulated;
	private double retentionDeclared;
	private double retentionResult;
	private double retentionAdjust;
	private double retention;
	private double lessorCountInKindAccumulated;
	private double lessorCountInKindDeclared;
	private double lessorCountInKindResult;
	private double lessorCountInKindAdjust;
	private double lessorCountInKind;
	private double remunerationInKindAccumulated;
	private double remunerationInKindDeclared;
	private double remunerationInKindResult;
	private double remunerationInKindAdjust;
	private double remunerationInKind;
	private double accountDepositAccumulated;
	private double accountDepositDeclared;
	private double accountDepositResult;
	private double accountDepositAdjust;
	private double accountDeposit;
	private double extraCharge;
	private double delayInterest;
	private double totalTaxDebt;
	private RegistryBank registryBank;
	
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    @Transient
    public boolean isAnual() {
    	return (getPeriod() == Period.YEAR);
    }
    
    @Column(name = "period")
    public Period getPeriod() {
        return period;
    }
    public void setPeriod(Period period) {
        this.period = period;
    }

	@Column(name="administration")
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name="status")
    public RentingStatus getStatus() {
        return status;
    }
    public void setStatus(RentingStatus status) {
        this.status = status;
    }

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Column(name="complementary")
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}

	@Column(name="replacement")
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

    @Transient
	public boolean isFinished() {
		return getStatus() == RentingStatus.FINISHED;
	}

    @Column(name="lessor_count_accumulated")
	public double getLessorCountAccumulated() {
		return lessorCountAccumulated;
	}
	public void setLessorCountAccumulated(double lessorCountAccumulated) {
		this.lessorCountAccumulated = lessorCountAccumulated;
	}

    @Column(name="lessor_count_declared")
	public double getLessorCountDeclared() {
		return lessorCountDeclared;
	}
	public void setLessorCountDeclared(double lessorCountDeclared) {
		this.lessorCountDeclared= lessorCountDeclared;
	}

    @Column(name="lessor_count_result")
	public double getLessorCountResult() {
		return lessorCountResult;
	}
	public void setLessorCountResult(double lessorCountResult) {
		this.lessorCountResult= lessorCountResult;
	}

    @Column(name="lessor_count_adjust")
	public double getLessorCountAdjust() {
		return lessorCountAdjust;
	}
	public void setLessorCountAdjust(double lessorCountAdjust) {
		this.lessorCountAdjust = lessorCountAdjust;
	}

    @Column(name="lessor_count")
	public double getLessorCount() {
		return lessorCount;
	}
	public void setLessorCount(double lessorCount) {
		this.lessorCount = lessorCount;
	}

    @Column(name="renting_amount_accumulated")
	public double getRentingAmountAccumulated() {
		return rentingAmountAccumulated;
	}
	public void setRentingAmountAccumulated(double rentingAmountAccumulated) {
		this.rentingAmountAccumulated = rentingAmountAccumulated;
	}

    @Column(name="renting_amount_declared")
	public double getRentingAmountDeclared() {
		return rentingAmountDeclared;
	}
	public void setRentingAmountDeclared(double rentingAmountDeclared) {
		this.rentingAmountDeclared= rentingAmountDeclared;
	}

    @Column(name="renting_amount_result")
	public double getRentingAmountResult() {
		return rentingAmountResult;
	}
	public void setRentingAmountResult(double rentingAmountResult) {
		this.rentingAmountResult= rentingAmountResult;
	}

    @Column(name="renting_amount_adjust")
	public double getRentingAmountAdjust() {
		return rentingAmountAdjust;
	}
	public void setRentingAmountAdjust(double rentingAmountAdjust) {
		this.rentingAmountAdjust = rentingAmountAdjust;
	}

    @Column(name="renting_amount")
	public double getRentingAmount() {
		return rentingAmount;
	}
	public void setRentingAmount(double rentingAmount) {
		this.rentingAmount = rentingAmount;
	}

    @Column(name="retention_accumulated")
	public double getRetentionAccumulated() {
		return retentionAccumulated;
	}
	public void setRetentionAccumulated(double retentionAccumulated) {
		this.retentionAccumulated = retentionAccumulated;
	}

    @Column(name="retention_declared")
	public double getRetentionDeclared() {
		return retentionDeclared;
	}
	public void setRetentionDeclared(double retentionDeclared) {
		this.retentionDeclared= retentionDeclared;
	}

    @Column(name="retention_result")
	public double getRetentionResult() {
		return retentionResult;
	}
	public void setRetentionResult(double retentionResult) {
		this.retentionResult= retentionResult;
	}

	@Column(name="retention_adjust")
	public double getRetentionAdjust() {
		return retentionAdjust;
	}
	public void setRetentionAdjust(double retentionAdjust) {
		this.retentionAdjust = retentionAdjust;
	}

    @Column(name="retention")
	public double getRetention() {
		return retention;
	}
	public void setRetention(double retention) {
		this.retention = retention;
	}

    @Column(name="lessor_count_in_kind_accumulated")
	public double getLessorCountInKindAccumulated() {
		return lessorCountInKindAccumulated;
	}
	public void setLessorCountInKindAccumulated(double lessorCountInKindAccumulated) {
		this.lessorCountInKindAccumulated = lessorCountInKindAccumulated;
	}

    @Column(name="lessor_count_in_kind_declared")
	public double getLessorCountInKindDeclared() {
		return lessorCountInKindDeclared;
	}
	public void setLessorCountInKindDeclared(double lessorCountInKindDeclared) {
		this.lessorCountInKindDeclared = lessorCountInKindDeclared;
	}

    @Column(name="lessor_count_in_kind_result")
	public double getLessorCountInKindResult() {
		return lessorCountInKindResult;
	}
	public void setLessorCountInKindResult(double lessorCountInKindResult) {
		this.lessorCountInKindResult= lessorCountInKindResult;
	}

	@Column(name="lessor_count_in_kind_adjust")
	public double getLessorCountInKindAdjust() {
		return lessorCountInKindAdjust;
	}
	public void setLessorCountInKindAdjust(double lessorCountInKindAdjust) {
		this.lessorCountInKindAdjust = lessorCountInKindAdjust;
	}

    @Column(name="lessor_count_in_kind")
	public double getLessorCountInKind() {
		return lessorCountInKind;
	}
	public void setLessorCountInKind(double lessorCountInKind) {
		this.lessorCountInKind = lessorCountInKind;
	}

    @Column(name="remuneration_in_kind_accumulated")
	public double getRemunerationInKindAccumulated() {
		return remunerationInKindAccumulated;
	}
	public void setRemunerationInKindAccumulated(double remunerationInKindAccumulated) {
		this.remunerationInKindAccumulated = remunerationInKindAccumulated;
	}

    @Column(name="remuneration_in_kind_declared")
	public double getRemunerationInKindDeclared() {
		return remunerationInKindDeclared;
	}
	public void setRemunerationInKindDeclared(double remunerationInKindDeclared) {
		this.remunerationInKindDeclared= remunerationInKindDeclared;
	}

    @Column(name="remuneration_in_kind_result")
	public double getRemunerationInKindResult() {
		return remunerationInKindResult;
	}
	public void setRemunerationInKindResult(double remunerationInKindResult) {
		this.remunerationInKindResult= remunerationInKindResult;
	}

	@Column(name="remuneration_in_kind_adjust")
	public double getRemunerationInKindAdjust() {
		return remunerationInKindAdjust;
	}
	public void setRemunerationInKindAdjust(double remunerationInKindAdjust) {
		this.remunerationInKindAdjust = remunerationInKindAdjust;
	}

    @Column(name="remuneration_in_kind")
	public double getRemunerationInKind() {
		return remunerationInKind;
	}
	public void setRemunerationInKind(double remunerationInKind) {
		this.remunerationInKind = remunerationInKind;
	}

    @Column(name="account_deposit_accumulated")
	public double getAccountDepositAccumulated() {
		return accountDepositAccumulated;
	}
	public void setAccountDepositAccumulated(double accountDepositAccumulated) {
		this.accountDepositAccumulated = accountDepositAccumulated;
	}

    @Column(name="account_deposit_declared")
	public double getAccountDepositDeclared() {
		return accountDepositDeclared;
	}
	public void setAccountDepositDeclared(double accountDepositDeclared) {
		this.accountDepositDeclared = accountDepositDeclared;
	}

    @Column(name="account_deposit_result")
	public double getAccountDepositResult() {
		return accountDepositResult;
	}
	public void setAccountDepositResult(double accountDepositResult) {
		this.accountDepositResult= accountDepositResult;
	}

	@Column(name="account_deposit_adjust")
	public double getAccountDepositAdjust() {
		return accountDepositAdjust;
	}
	public void setAccountDepositAdjust(double accountDepositAdjust) {
		this.accountDepositAdjust = accountDepositAdjust;
	}

    @Column(name="account_deposit")
	public double getAccountDeposit() {
		return accountDeposit;
	}
	public void setAccountDeposit(double accountDeposit) {
		this.accountDeposit = accountDeposit;
	}

    @Column(name="extra_charge")
	public double getExtraCharge() {
		return extraCharge;
	}
	public void setExtraCharge(double extraCharge) {
		this.extraCharge = extraCharge;
	}

    @Column(name="delay_interest")
	public double getDelayInterest() {
		return delayInterest;
	}
	public void setDelayInterest(double delayInterest) {
		this.delayInterest = delayInterest;
	}

    @Column(name="total_tax_debt")
	public double getTotalTaxDebt() {
		return totalTaxDebt;
	}
	public void setTotalTaxDebt(double totalTaxDebt) {
		this.totalTaxDebt = totalTaxDebt;
	}

    @ManyToOne
    @JoinColumn(name="rbank")
    @ForeignKey(name="FK_RENTING_RBANK")
    @Index(name="IDX_RENTING_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	@Transient
	public Renting getRenting() {
		return this;
	}
	
	public void initializeAccumulatedAmounts() {
		setLessorCountAccumulated(0);
		setRentingAmountAccumulated(0);
		setRetentionAccumulated(0);
		setLessorCountInKindAccumulated(0);
		setRemunerationInKindAccumulated(0);
		setAccountDepositAccumulated(0);
	}

	public void calculate() {
		setLessorCountResult( CommonUtil.round(getLessorCountAccumulated() - getLessorCountDeclared()));
		setRentingAmountResult(CommonUtil.round( getRentingAmountAccumulated() - getRentingAmountDeclared()));
		setRetentionResult( CommonUtil.round( getRetentionAccumulated() - getRetentionDeclared()));
		setLessorCountInKindResult( CommonUtil.round( getLessorCountInKindAccumulated() - getLessorCountInKindDeclared()));
		setRemunerationInKindResult( CommonUtil.round( getRemunerationInKindAccumulated() - getRemunerationInKindDeclared()));
		setAccountDepositResult( CommonUtil.round( getAccountDepositAccumulated() - getAccountDepositDeclared()));
		
		setLessorCount( CommonUtil.round(getLessorCountResult() + getLessorCountAdjust()));
		setRentingAmount(CommonUtil.round( getRentingAmountResult() + getRentingAmountAdjust()));
		setRetention( CommonUtil.round( getRetentionResult() + getRetentionAdjust()));
		setLessorCountInKind( CommonUtil.round( getLessorCountInKindResult() + getLessorCountInKindAdjust()));
		setRemunerationInKind( CommonUtil.round( getRemunerationInKindResult() + getRemunerationInKindAdjust()));
		setAccountDeposit( CommonUtil.round( getAccountDepositResult() + getAccountDepositAdjust()));
		
		setTotalTaxDebt( CommonUtil.round( getDeposit() + getExtraCharge() + getDelayInterest()) );
	}

	@Transient
	public double getDeposit() {
		return CommonUtil.round( getRetention() + getAccountDeposit() );
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Renting o = (Renting) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.year, o.year)			
				.append(this.period, o.period)
				.append(this.comments, o.comments)			
				.append(this.status, o.status)
				.append(this.securityLevel, o.securityLevel)
				.append(this.complementary, o.complementary)
				.append(this.replacement, o.replacement)
				.append(this.lessorCountAccumulated, o.lessorCountAccumulated) 
				.append(this.lessorCountDeclared, o.lessorCountDeclared) 
				.append(this.lessorCountResult, o.lessorCountResult) 
				.append(this.lessorCountAdjust, o.lessorCountAdjust) 
				.append(this.lessorCount, o.lessorCount) 
				.append(this.rentingAmountAccumulated, o.rentingAmountAccumulated) 
				.append(this.rentingAmountDeclared, o.rentingAmountDeclared) 
				.append(this.rentingAmountResult, o.rentingAmountResult) 
				.append(this.rentingAmountAdjust, o.rentingAmountAdjust) 
				.append(this.rentingAmount, o.rentingAmount) 
				.append(this.retentionAccumulated, o.retentionAccumulated)
				.append(this.retentionDeclared, o.retentionDeclared)
				.append(this.retentionResult, o.retentionResult)
				.append(this.retentionAdjust, o.retentionAdjust)
				.append(this.retention, o.retention)
				.append(this.lessorCountInKindAccumulated, o.lessorCountInKindAccumulated)
				.append(this.lessorCountInKindDeclared, o.lessorCountInKindDeclared)
				.append(this.lessorCountInKindResult, o.lessorCountInKindResult)
				.append(this.lessorCountInKindAdjust, o.lessorCountInKindAdjust)
				.append(this.lessorCountInKind, o.lessorCountInKind)
				.append(this.remunerationInKindAccumulated, o.remunerationInKindAccumulated)
				.append(this.remunerationInKindDeclared, o.remunerationInKindDeclared)
				.append(this.remunerationInKindResult, o.remunerationInKindResult)
				.append(this.remunerationInKindAdjust, o.remunerationInKindAdjust)
				.append(this.remunerationInKind, o.remunerationInKind)
				.append(this.accountDepositAccumulated, o.accountDepositAccumulated)
				.append(this.accountDepositDeclared, o.accountDepositDeclared)
				.append(this.accountDepositResult, o.accountDepositResult)
				.append(this.accountDepositAdjust, o.accountDepositAdjust)
				.append(this.accountDeposit, o.accountDeposit)
				.append(this.extraCharge, o.extraCharge)
				.append(this.delayInterest, o.delayInterest)
				.append(this.totalTaxDebt, o.totalTaxDebt)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.year)			
			.append(this.period)
			.append(this.comments)			
			.append(this.status)
			.append(this.securityLevel)
			.append(this.complementary)
			.append(this.replacement)
			.append(this.lessorCountAccumulated)
			.append(this.lessorCountDeclared)
			.append(this.lessorCountResult)
			.append(this.lessorCountAdjust)
			.append(this.lessorCount)
			.append(this.rentingAmountAccumulated)
			.append(this.rentingAmountDeclared)
			.append(this.rentingAmountResult)
			.append(this.rentingAmountAdjust)
			.append(this.rentingAmount)
			.append(this.retentionAccumulated)
			.append(this.retentionDeclared)
			.append(this.retentionResult)
			.append(this.retentionAdjust)
			.append(this.retention)
			.append(this.lessorCountInKindAccumulated)
			.append(this.lessorCountInKindDeclared)
			.append(this.lessorCountInKindResult)
			.append(this.lessorCountInKindAdjust)
			.append(this.lessorCountInKind)
			.append(this.remunerationInKindAccumulated)
			.append(this.remunerationInKindDeclared)
			.append(this.remunerationInKindResult)
			.append(this.remunerationInKindAdjust)
			.append(this.remunerationInKind)
			.append(this.accountDepositAccumulated)
			.append(this.accountDepositDeclared)
			.append(this.accountDepositResult)
			.append(this.accountDepositAdjust)
			.append(this.accountDeposit)
			.append(this.extraCharge)
			.append(this.delayInterest)
			.append(this.totalTaxDebt)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
