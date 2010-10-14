package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.enumeration.Model303Key;

@Entity
@Table(name = "fs_vat_detail")
public class VatTaxDetail implements ITransferObject {

	private static final long serialVersionUID = -4734071580890529329L;

    private Integer id;
    private VatTax vatTax;
    private Model303Key key;
    private double percent;
    private double taxableBase;
    private double quota;
    private double deductibleQuota;
    private double taxableBaseAdjust;
    private double quotaAdjust;
    private double deductibleQuotaAdjust;

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

    @Column(nullable=false)
    public Model303Key getKey() {
        return key;
    }
    public void setKey(Model303Key key) {
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
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}