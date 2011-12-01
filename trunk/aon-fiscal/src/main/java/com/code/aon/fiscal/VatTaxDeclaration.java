package com.code.aon.fiscal;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "fs_vat_declaration")
public class VatTaxDeclaration implements ITransferObject {

	private static final long serialVersionUID = 6874750855856177012L;
	
	private Integer id;
	private VatTax vatTax;
	private boolean withoutActivity;
	private Administration administration;
	private double percent;
	private double operationsVolume;
	private double quota;
	private double previousYearCompensateQuota;
	private double doneDeposits;
	private double doneRefunds;
	private double extraCharge;
	private double delayInterest;
	private double compensate;
	private double payBack;
	private double deposit;
	private double previousDeposit;
	private double previousPayBack;
	private double totalTaxDebt;
	private RegistryBank registryBank;
	private boolean compensable;
	private VatTaxDeclarationStatus status;
	
	public VatTaxDeclaration() {
		setCompensable(true);		
	}
	
	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fs_vat", nullable = false)
    public VatTax getVatTax() {
        return vatTax;
    }
    public void setVatTax(VatTax vatTax) {
        this.vatTax = vatTax;
    }

	@Column(name="administration")
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	@Column(name="without_activity")
	public boolean isWithoutActivity() {
		return withoutActivity;
	}
	public void setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
	}

    @Column(name="percent",nullable=true)
    public double getPercent() {
        return percent;
    }
    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Column(name="operations_volume",nullable=true)
    public double getOperationsVolume() {
        return operationsVolume;
    }
    public void setOperationsVolume(double operationsVolume) {
        this.operationsVolume = operationsVolume;
    }

    @Column(name="quota",nullable=true)
    public double getQuota() {
        return quota;
    }
    public void setQuota(double quota) {
        this.quota = quota;
    }

    @Column(name="prev_year_compensate_quota",nullable=true)
    public double getPreviousYearCompensateQuota() {
        return previousYearCompensateQuota;
    }
    public void setPreviousYearCompensateQuota(double previousYearCompensateQuota) {
        this.previousYearCompensateQuota = previousYearCompensateQuota;
    }
    
    @Column(name="done_deposits",nullable=true)
    public double getDoneDeposits() {
        return doneDeposits;
    }
    public void setDoneDeposits(double doneDeposits) {
        this.doneDeposits = doneDeposits;
    }

    @Column(name="done_refunds",nullable=true)
    public double getDoneRefunds() {
        return doneRefunds;
    }
    public void setDoneRefunds(double doneRefunds) {
        this.doneRefunds = doneRefunds;
    }

    @Column(name="extra_charge",nullable=true)
    public double getExtraCharge() {
        return extraCharge;
    }
    public void setExtraCharge(double extraCharge) {
        this.extraCharge = extraCharge;
    }

    @Column(name="delay_interest",nullable=true)
    public double getDelayInterest() {
        return delayInterest;
    }
    public void setDelayInterest(double delayInterest) {
        this.delayInterest = delayInterest;
    }

    @Column(name="compensate",nullable=true)
    public double getCompensate() {
        return compensate;
    }
    public void setCompensate(double compensate) {
        this.compensate = compensate;
    }

    @Column(name="pay_back",nullable=true)
    public double getPayBack() {
        return payBack;
    }
    public void setPayBack(double payBack) {
        this.payBack = payBack;
    }

    @Column(name="deposit",nullable=true)
    public double getDeposit() {
        return deposit;
    }
    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

	@Column(name="compensable",nullable=true)
    public boolean isCompensable() {
		return compensable;
	}
	public void setCompensable(boolean compensable) {
		this.compensable = compensable;
	}

	@Column(name="prev_deposit",nullable=true)
    public double getPreviousDeposit() {
        return previousDeposit;
    }
    public void setPreviousDeposit(double previousDeposit) {
        this.previousDeposit = previousDeposit;
    }

    @Column(name="prev_pay_back",nullable=true)
    public double getPreviousPayBack() {
        return previousPayBack;
    }
    public void setPreviousPayBack(double previousPayBack) {
        this.previousPayBack= previousPayBack;
    }

    @Column(name="total_tax_debt",nullable=true)
    public double getTotalTaxDebt() {
        return totalTaxDebt;
    }
    public void setTotalTaxDebt(double totalTaxDebt) {
        this.totalTaxDebt= totalTaxDebt;
    }

    @ManyToOne
    @JoinColumn(name="rbank")
    @ForeignKey(name="FK_VATTAXDECL_RBANK")
    @Index(name="IDX_VATTAXDECL_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	@Column(name="status")
    public VatTaxDeclarationStatus getStatus() {
        return status;
    }
    public void setStatus(VatTaxDeclarationStatus status) {
        this.status = status;
    }

    @Transient
    public boolean isGenerated() {
    	return (this.status == VatTaxDeclarationStatus.GENERATED);
    }
    @Transient
	public double getDifference() {
		return CommonUtil.round( getQuota() - getPreviousYearCompensateQuota() );
	}

	@Transient
	public double getResult() {
		return CommonUtil.round( getDifference() - getDoneDeposits() + getDoneRefunds() );
	}

	@Transient
	public boolean isCompensateEnabled() {
		return getDeposit() == 0 && isCompensable() && !isDepositEnabled();
	}
	@Transient
	public boolean isPayBackEnabled() {
		return getDeposit() == 0 && !isCompensable();
	}
	@Transient
	public boolean isDepositEnabled() {
		return getDeposit() > 0 || (getDeposit() == 0 && getPayBack() == 0 && getCompensate() == 0);
	}

	@Transient
	public VatTaxDeclaration getDeclaration() {
		return this;
	}

	@Transient
	public String getBankAccount() {
		if (getRegistryBank() != null && getRegistryBank().getBankAccount() != null ) {
			return getRegistryBank().getBankAccount().getMaskedBankAccount();
		}
		return null;
	}
	
	@Transient
	public String getBank() {
		if (getRegistryBank() != null && getRegistryBank().getBank() != null ) {
			return StringUtils.abbreviate(getRegistryBank().getBank().getName(), 50);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final VatTaxDeclaration o = (VatTaxDeclaration) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.vatTax, o.vatTax)			
				.append(this.withoutActivity, o.withoutActivity)
				.append(this.administration, o.administration)			
				.append(this.percent, o.percent)				
				.append(this.operationsVolume, o.operationsVolume)
				.append(this.quota, o.quota)
				.append(this.previousYearCompensateQuota, o.previousYearCompensateQuota)
				.append(this.doneDeposits, o.doneDeposits)
				.append(this.doneRefunds, o.doneRefunds)
				.append(this.extraCharge, o.extraCharge)
				.append(this.delayInterest, o.delayInterest)
				.append(this.compensate, o.compensate)
				.append(this.payBack, o.payBack)
				.append(this.deposit, o.deposit)
				.append(this.previousDeposit, o.previousDeposit)
				.append(this.previousPayBack, o.previousPayBack)
				.append(this.totalTaxDebt, o.totalTaxDebt)
				.append(this.registryBank, o.registryBank)
				.append(this.status, o.status)
				.append(this.compensable, o.compensable)
				.isEquals();
	}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.vatTax)			
			.append(this.withoutActivity)
			.append(this.administration)			
			.append(this.percent)				
			.append(this.operationsVolume)
			.append(this.quota)
			.append(this.previousYearCompensateQuota)
			.append(this.doneDeposits)
			.append(this.doneRefunds)
			.append(this.extraCharge)
			.append(this.delayInterest)
			.append(this.compensate)
			.append(this.payBack)
			.append(this.deposit)
			.append(this.previousDeposit)
			.append(this.previousPayBack)
			.append(this.totalTaxDebt)
			.append(this.registryBank)
			.append(this.status)
			.append(this.compensable)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
