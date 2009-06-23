package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.enumeration.TaxType;

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
	 * Gets the percentage.
	 * 
	 * @return the percentage
	 */
	@Column(name="percentage")
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
	@Column(name="surcharge")
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
		return 0;
	}

}