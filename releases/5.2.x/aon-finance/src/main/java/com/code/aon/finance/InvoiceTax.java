package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.enumeration.TaxType;

/**
 * Transfer Object that represents an InvoiceTax.
 */
@Entity
@Table(name="invoice_tax")
public class InvoiceTax implements ITransferObject {

	private static final long serialVersionUID = -4275174280038370912L;

	/** The id. */
	private Integer id;
	
	/** The invoice detail. */
	private InvoiceDetail invoiceDetail;
	
	/** The tax type. */
	private TaxType taxType;
	
	/** The percentage. */
	private double percentage;
	
	/** The surcharge. */
	private double surcharge;

	/** The quota. */
	private double quota;

	/** The surcharge quota. */
	private double surchargeQuota;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the invoice detail.
	 * 
	 * @return the invoice detail
	 */
	@ManyToOne
    @JoinColumn(name="invoice_detail", nullable = false)
    @ForeignKey(name="FK_INVOICE_TAX_INVOICE_DETAIL")
    @Index(name="IDX_INVOICE_TAX_INVOICE_DETAIL")                                        
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	/**
	 * Sets the invoice detail.
	 * 
	 * @param invoiceDetail the invoice detail
	 */
	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}
	
	/**
	 * Gets the tax type.
	 * 
	 * @return the tax type
	 */
	@Column(name="tax_type")
	public TaxType getTaxType() {
		return taxType;
	}

	/**
	 * Sets the tax type.
	 * 
	 * @param taxType the tax type
	 */
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	/**
	 * Gets the percentage.
	 * 
	 * @return the percentage
	 */
	@Column(name="percentage", precision=15, scale=3)
	public double getPercentage() {
		return percentage;
	}

	/**
	 * Sets the percentage.
	 * 
	 * @param percentage the percentage
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Gets the surcharge.
	 * 
	 * @return the surcharge
	 */
	@Column(name="surcharge", precision=15, scale=3)
	public double getSurcharge() {
		return surcharge;
	}

	/**
	 * Sets the surcharge.
	 * 
	 * @param surcharge the surcharge
	 */
	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

    /**
     * Gets the quota.
     * 
     * @return the quota
     */
    @Column(nullable=true)
    public double getQuota() {
        return quota;
    }

    /**
     * Sets the quota.
     * 
     * @param quota the quota
     */
    public void setQuota(double quota) {
        this.quota = quota;
    }

    /**
     * Gets the surcharge quota.
     * 
     * @return the surcharge quota
     */
    @Column(name="surcharge_quota", nullable=true)
    public double getSurchargeQuota() {
        return surchargeQuota;
    }

    /**
     * Sets the surcharge quota.
     * 
     * @param surchargeQuota the surcharge quota
     */
    public void setSurchargeQuota(double surchargeQuota) {
        this.surchargeQuota = surchargeQuota;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof InvoiceTax) {
			InvoiceTax o = (InvoiceTax) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}