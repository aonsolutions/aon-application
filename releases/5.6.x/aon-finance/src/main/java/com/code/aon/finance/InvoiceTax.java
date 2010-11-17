package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;

@Entity
@Table(name="invoice_tax")
public class InvoiceTax implements ITransferObject {

	private static final long serialVersionUID = -4275174280038370912L;

	private Integer id;
	private InvoiceDetail invoiceDetail;
	private TaxType taxType;
	private double percentage;
	private double surcharge;
	private double quota;
	private double surchargeQuota;
    private VatDeductionType vatDeductionType;
    private WithholdingType withholdingType;
	private double deductibleQuota;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn(name="invoice_detail", nullable = false)
    @ForeignKey(name="FK_INVOICE_TAX_INVOICE_DETAIL")
    @Index(name="IDX_INVOICE_TAX_INVOICE_DETAIL")                                        
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}
	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}
	
	@Column(name="tax_type")
	public TaxType getTaxType() {
		return taxType;
	}
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	@Column(name="percentage", precision=15, scale=3)
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	@Column(name="surcharge", precision=15, scale=3)
	public double getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

    @Column(nullable=true)
    public double getQuota() {
        return quota;
    }
    public void setQuota(double quota) {
        this.quota = quota;
    }

    @Column(name="surcharge_quota", nullable=true)
    public double getSurchargeQuota() {
        return surchargeQuota;
    }
    public void setSurchargeQuota(double surchargeQuota) {
        this.surchargeQuota = surchargeQuota;
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

    @Column(name="deductible_quota", nullable=true)
    public double getDeductibleQuota() {
        return deductibleQuota;
    }
    public void setDeductibleQuota(double deductibleQuota) {
        this.deductibleQuota = deductibleQuota;
    }

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoiceTax o = (InvoiceTax) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.invoiceDetail,o.invoiceDetail)
			.append(this.deductibleQuota,o.deductibleQuota)
			.append(this.quota,o.quota)
			.append(this.percentage,o.percentage)
			.append(this.surcharge,o.surcharge)
			.append(this.surchargeQuota,o.surchargeQuota)
			.append(this.taxType,o.taxType)
			.append(this.vatDeductionType,o.vatDeductionType)
			.append(this.withholdingType,o.withholdingType)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.invoiceDetail)
			.append(this.deductibleQuota)
			.append(this.quota)
			.append(this.percentage)
			.append(this.surcharge)
			.append(this.surchargeQuota)
			.append(this.taxType)
			.append(this.vatDeductionType)
			.append(this.withholdingType)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}