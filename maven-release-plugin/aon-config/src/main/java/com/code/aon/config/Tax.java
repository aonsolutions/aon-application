package com.code.aon.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;

@Entity
@Table(name="tax")
public class Tax implements ITransferObject{

	private static final long serialVersionUID = -513647182781441011L;

    private Integer id;
    private String name;
    private TaxType type;
    private double percentage;
    private double surcharge;
    private Date startDate;
    private VatDeductionType vatDeductionType;
    private WithholdingType withholdingType;
	private Account salesAccount;
    private Account purchaseAccount;

    public Tax() {
    }

    public Tax(Integer pk) {
        this.id = pk;
    }

    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length=30, nullable=false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name="tax_type", nullable=false)
    public TaxType getType() {
        return type;
    }
    public void setType(TaxType type) {
        this.type = type;
    }
 
    @Column(nullable = false, precision = 15, scale = 3)
    public double getPercentage() {
        return percentage;
    }
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Column(precision = 15, scale = 3)
    public double getSurcharge() {
        return surcharge;
    }
    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    @Column(name="start_date")
    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name="vat_deduction_type")
    public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}

    @Column(name="withholding_type")
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}

	@Transient
	public boolean isVat() {
		return (getType() == TaxType.VAT);
	}
	
	@Transient
	public boolean isRetention() {
		return (getType() == TaxType.RETENTION);
	}

	@Transient
	public Account getSalesAccount() {
		return salesAccount;
	}
	@Transient
	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}

	@Transient
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	@Transient
	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Tax o = (Tax) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)			
				.append(this.percentage, o.percentage)
				.append(this.purchaseAccount, o.purchaseAccount)				
				.append(this.salesAccount, o.salesAccount)			
				.append(this.startDate, o.startDate)
				.append(this.surcharge, o.surcharge)				
				.append(this.type, o.type)
				.append(this.vatDeductionType, o.vatDeductionType)
				.append(this.withholdingType, o.withholdingType)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(name)
			.append(percentage)		
			.append(purchaseAccount)
			.append(salesAccount)		
			.append(startDate)
			.append(surcharge)		
			.append(type)
			.append(vatDeductionType)
			.append(withholdingType)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}