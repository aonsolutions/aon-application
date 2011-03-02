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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.VatTaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.vat.tax.VatTaxAmount;

@Entity
@Table(name = "fs_vat_detail")
public class VatTaxDetail implements ITransferObject {

	private static final long serialVersionUID = -4734071580890529329L;

    private Integer id;
    private VatTax vatTax;
    private VatTaxKey key;
    private double percent;
    private double taxableBase;
    private double quota;
    private double deductibleQuota;
    private double taxableBaseAdjust;
    private double quotaAdjust;
    private double deductibleQuotaAdjust;
    private double taxableBaseAccumulated;
    private double quotaAccumulated;
    private double deductibleQuotaAccumulated;
    private double taxableBaseDeclared;
    private double quotaDeclared;
    private double deductibleQuotaDeclared;
    private double taxableBaseResult;
    private double quotaResult;
    private double deductibleQuotaResult;
    private boolean descriptionDisabled;

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

    @Column(name="vat_key",nullable=false)
   	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.fiscal.enumeration.VatTaxKey") })
    public VatTaxKey getKey() {
        return key;
    }
    public void setKey(VatTaxKey key) {
        this.key = key;
    }

    @Column(nullable=true)
    public double getPercent() {
        return percent;
    }
    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Column(name="taxable_base", precision=15, scale=3)
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	
    @Column(name="quota", precision=15, scale=3)
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}

    @Column(name="deductible_quota", precision=15, scale=3)
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota= deductibleQuota;
	}

    @Column(name="adj_taxable_base", precision=15, scale=3)
	public double getTaxableBaseAdjust() {
		return taxableBaseAdjust;
	}
	public void setTaxableBaseAdjust(double taxableBaseAdjust) {
		this.taxableBaseAdjust = taxableBaseAdjust;
	}
	
    @Column(name="adj_quota", precision=15, scale=3)
	public double getQuotaAdjust() {
		return quotaAdjust;
	}
	public void setQuotaAdjust(double quotaAdjust) {
		this.quotaAdjust = quotaAdjust;
	}

    @Column(name="adj_deductible_quota", precision=15, scale=3)
	public double getDeductibleQuotaAdjust() {
		return deductibleQuotaAdjust;
	}
	public void setDeductibleQuotaAdjust(double deductibleQuotaAdjust) {
		this.deductibleQuotaAdjust= deductibleQuotaAdjust;
	}

    @Column(name="acu_taxable_base", precision=15, scale=3)
	public double getTaxableBaseAccumulated() {
		return taxableBaseAccumulated;
	}
	public void setTaxableBaseAccumulated(double taxableBaseAccumulated) {
		this.taxableBaseAccumulated= taxableBaseAccumulated;
	}
	
    @Column(name="acu_quota", precision=15, scale=3)
	public double getQuotaAccumulated() {
		return quotaAccumulated;
	}
	public void setQuotaAccumulated(double quotaAccumulated) {
		this.quotaAccumulated = quotaAccumulated;
	}

    @Column(name="acu_deductible_quota", precision=15, scale=3)
	public double getDeductibleQuotaAccumulated() {
		return deductibleQuotaAccumulated;
	}
	public void setDeductibleQuotaAccumulated(double deductibleQuotaAccumulated) {
		this.deductibleQuotaAccumulated= deductibleQuotaAccumulated;
	}
	
	
    @Column(name="dec_taxable_base", precision=15, scale=3)
	public double getTaxableBaseDeclared() {
		return taxableBaseDeclared;
	}
	public void setTaxableBaseDeclared(double taxableBaseDeclared) {
		this.taxableBaseDeclared= taxableBaseDeclared;
	}
	
    @Column(name="dec_quota", precision=15, scale=3)
	public double getQuotaDeclared() {
		return quotaDeclared;
	}
	public void setQuotaDeclared(double quotaDeclared) {
		this.quotaDeclared = quotaDeclared;
	}

    @Column(name="dec_deductible_quota", precision=15, scale=3)
	public double getDeductibleQuotaDeclared() {
		return deductibleQuotaDeclared;
	}
	public void setDeductibleQuotaDeclared(double deductibleQuotaDeclared) {
		this.deductibleQuotaDeclared = deductibleQuotaDeclared;
	}
	
    @Column(name="res_taxable_base", precision=15, scale=3)
	public double getTaxableBaseResult() {
		return taxableBaseResult;
	}
	public void setTaxableBaseResult(double taxableBaseResult) {
		this.taxableBaseResult = taxableBaseResult;
	}
	
    @Column(name="res_quota", precision=15, scale=3)
	public double getQuotaResult() {
		return quotaResult;
	}
	public void setQuotaResult(double quotaResult) {
		this.quotaResult= quotaResult;
	}

    @Column(name="res_deductible_quota", precision=15, scale=3)
	public double getDeductibleQuotaResult() {
		return deductibleQuotaResult;
	}
	public void setDeductibleQuotaResult(double deductibleQuotaResult) {
		this.deductibleQuotaResult= deductibleQuotaResult;
	}

	@Transient
	public boolean isDescriptionDisabled() {
		return descriptionDisabled;
	}
	public void setDescriptionDisabled(boolean descriptionDisabled) {
		this.descriptionDisabled = descriptionDisabled;
	}
	
	@Transient
	public void initialize() {
		setTaxableBaseAccumulated( 0.0 ); 
		setQuotaAccumulated( 0.0);
		setDeductibleQuotaAccumulated( 0.0 );
		setTaxableBaseDeclared( 0.0 ); 
		setQuotaDeclared( 0.0 );
		setDeductibleQuotaDeclared( 0.0 );
		setTaxableBaseResult( 0.0 );
		setQuotaResult( 0.0 );
		setDeductibleQuotaResult( 0.0 );
		setTaxableBaseAdjust( 0.0 );
		setQuotaAdjust( 0.0 );
		setDeductibleQuotaAdjust( 0.0 );
		setTaxableBase( 0.0 );
		setQuota( 0.0 );
		setDeductibleQuota( 0.0 );
	}
	@Transient
	public void add(VatTaxDetail toAdd) {
		setTaxableBaseAccumulated( CommonUtil.round( getTaxableBaseAccumulated() + toAdd.getTaxableBaseAccumulated())); 
		setQuotaAccumulated( CommonUtil.round( getQuotaAccumulated() + toAdd.getQuotaAccumulated()));
		setDeductibleQuotaAccumulated( CommonUtil.round( getDeductibleQuotaAccumulated() + toAdd.getDeductibleQuotaAccumulated()));
		setTaxableBaseDeclared( CommonUtil.round( getTaxableBaseDeclared() + toAdd.getTaxableBaseDeclared())); 
		setQuotaDeclared( CommonUtil.round( getQuotaDeclared() + toAdd.getQuotaDeclared()));
		setDeductibleQuotaDeclared( CommonUtil.round( getDeductibleQuotaDeclared() + toAdd.getDeductibleQuotaDeclared()));
		setTaxableBaseResult( CommonUtil.round( getTaxableBaseResult() + toAdd.getTaxableBaseResult()));
		setQuotaResult( CommonUtil.round( getQuotaResult() + toAdd.getQuotaResult()));
		setDeductibleQuotaResult( CommonUtil.round( getDeductibleQuotaResult() + toAdd.getDeductibleQuotaResult()));
		setTaxableBaseAdjust( CommonUtil.round( getTaxableBaseAdjust() + toAdd.getTaxableBaseAdjust()));
		setQuotaAdjust( CommonUtil.round( getQuotaAdjust() + toAdd.getQuotaAdjust()));
		setDeductibleQuotaAdjust( CommonUtil.round( getDeductibleQuotaAdjust() + toAdd.getDeductibleQuotaAdjust()));
		setTaxableBase( CommonUtil.round( getTaxableBase() + toAdd.getTaxableBase()));
		setQuota( CommonUtil.round( getQuota() + toAdd.getQuota()));
		setDeductibleQuota( CommonUtil.round( getQuota() + toAdd.getDeductibleQuota()));
	}
	@Transient
	public void subtract(VatTaxDetail toSubtract) {
		setTaxableBaseAccumulated( CommonUtil.round( getTaxableBaseAccumulated() - toSubtract.getTaxableBaseAccumulated())); 
		setQuotaAccumulated( CommonUtil.round( getQuotaAccumulated() - toSubtract.getQuotaAccumulated()));
		setDeductibleQuotaAccumulated( CommonUtil.round( getDeductibleQuotaAccumulated() - toSubtract.getDeductibleQuotaAccumulated()));
		setTaxableBaseDeclared( CommonUtil.round( getTaxableBaseDeclared() - toSubtract.getTaxableBaseDeclared())); 
		setQuotaDeclared( CommonUtil.round( getQuotaDeclared() - toSubtract.getQuotaDeclared()));
		setDeductibleQuotaDeclared( CommonUtil.round( getDeductibleQuotaDeclared() - toSubtract.getDeductibleQuotaDeclared()));
		setTaxableBaseResult( CommonUtil.round( getTaxableBaseResult() - toSubtract.getTaxableBaseResult()));
		setQuotaResult( CommonUtil.round( getQuotaResult() - toSubtract.getQuotaResult()));
		setDeductibleQuotaResult( CommonUtil.round( getDeductibleQuotaResult() - toSubtract.getDeductibleQuotaResult()));
		setTaxableBaseAdjust( CommonUtil.round( getTaxableBaseAdjust() - toSubtract.getTaxableBaseAdjust()));
		setQuotaAdjust( CommonUtil.round( getQuotaAdjust() - toSubtract.getQuotaAdjust()));
		setDeductibleQuotaAdjust( CommonUtil.round( getDeductibleQuotaAdjust() - toSubtract.getDeductibleQuotaAdjust()));
		setTaxableBase( CommonUtil.round( getTaxableBase() - toSubtract.getTaxableBase()));
		setQuota( CommonUtil.round( getQuota() - toSubtract.getQuota()));
		setDeductibleQuota( CommonUtil.round( getDeductibleQuota() - toSubtract.getDeductibleQuota()));
	}
	@Transient
	public void calculate() {
		setTaxableBaseResult(CommonUtil.round(getTaxableBaseAccumulated() - getTaxableBaseDeclared()));
		setTaxableBase(CommonUtil.round(getTaxableBaseResult() + getTaxableBaseAdjust()));
		setQuotaResult(CommonUtil.round(getQuotaAccumulated() - getQuotaDeclared()));
		setQuota(CommonUtil.round(getQuotaResult() + getQuotaAdjust()));
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final VatTaxDetail o = (VatTaxDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.vatTax, o.vatTax)			
				.append(this.key, o.key)
				.append(this.percent, o.percent)				
				.append(this.taxableBase, o.taxableBase)			
				.append(this.quota, o.quota)
				.append(this.deductibleQuota, o.deductibleQuota)
				.append(this.taxableBaseAdjust, o.taxableBaseAdjust)
				.append(this.quotaAdjust, o.quotaAdjust)
				.append(this.deductibleQuotaAdjust, o.deductibleQuotaAdjust)
				.append(this.taxableBaseAccumulated,o.taxableBaseAccumulated)
				.append(this.quotaAccumulated, o.quotaAccumulated)
				.append(this.deductibleQuotaAccumulated, o.deductibleQuotaAccumulated)
				.append(this.taxableBaseDeclared, o.taxableBaseDeclared)
				.append(this.quotaDeclared, o.quotaDeclared)
				.append(this.deductibleQuotaDeclared, o.deductibleQuotaDeclared)
				.append(this.taxableBaseResult, o.taxableBaseResult)
				.append(this.quotaResult, o.quotaResult)
				.append(this.deductibleQuotaResult, o.deductibleQuotaResult)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.vatTax)			
			.append(this.key)
			.append(this.percent)				
			.append(this.taxableBase)			
			.append(this.quota)
			.append(this.deductibleQuota)
			.append(this.taxableBaseAdjust)
			.append(this.quotaAdjust)
			.append(this.deductibleQuotaAdjust)
			.append(this.taxableBaseAccumulated)
			.append(this.quotaAccumulated)
			.append(this.deductibleQuotaAccumulated)
			.append(this.taxableBaseDeclared)
			.append(this.quotaDeclared)
			.append(this.deductibleQuotaDeclared)
			.append(this.taxableBaseResult)
			.append(this.quotaResult)
			.append(this.deductibleQuotaResult)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public void add(VatTaxColumn column, VatTaxAmount amount) {
		if (column == VatTaxColumn.ACUMULADO) {
			setTaxableBaseAccumulated(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseAccumulated()));
			setDeductibleQuotaAccumulated(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaAccumulated()));	
			setQuotaAccumulated(CommonUtil.round(amount.getQuota() + getQuotaAccumulated()));
		} else if (column == VatTaxColumn.DECLARADO) {
			setTaxableBaseDeclared(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseDeclared()));
			setDeductibleQuotaDeclared(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaDeclared()));	
			setQuotaDeclared(CommonUtil.round(amount.getQuota() + getQuotaDeclared()));
		} else if (column == VatTaxColumn.RESULTADO) {
			setTaxableBaseResult(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseResult()));
			setDeductibleQuotaResult(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaResult()));	
			setQuotaResult(CommonUtil.round(amount.getQuota() + getQuotaResult()));
		} else if (column == VatTaxColumn.AJUSTE) {
			setTaxableBaseAdjust(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseAdjust()));
			setDeductibleQuotaAdjust(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaAdjust()));	
			setQuotaAdjust(CommonUtil.round(amount.getQuota() + getQuotaAdjust()));
		} else if (column == VatTaxColumn.DECLARAR) {
			setTaxableBase(CommonUtil.round(amount.getTaxableBase() + getTaxableBase()));
			setDeductibleQuota(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuota()));	
			setQuota(CommonUtil.round(amount.getQuota() + getQuota()));
		}
	}
}