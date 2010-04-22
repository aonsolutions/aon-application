package com.code.aon.salesPurchase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.sales.SalesDetail;

/**
 * Transfer Object that represents a union between a SalesDetail and a PurchaseDetail.
 */
@Entity
@Table(name="sales_purchase")
public class SalesPurchase implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The sales detail. */
	private SalesDetail salesDetail;
	
	/** The purchase detail. */
	private PurchaseDetail purchaseDetail;
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
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
	 * Gets the purchase detail.
	 * 
	 * @return the purchase detail
	 */
	@ManyToOne
	@JoinColumn( name="purchase_detail", nullable = false, updatable=false)
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	/**
	 * Sets the purchase detail.
	 * 
	 * @param purchaseDetail the purchase detail
	 */
	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	/**
	 * Gets the sales detail.
	 * 
	 * @return the sales detail
	 */
	@ManyToOne
	@JoinColumn( name="sales_detail", nullable = false, updatable=false)
	public SalesDetail getSalesDetail() {
		return salesDetail;
	}

	/**
	 * Sets the sales detail.
	 * 
	 * @param salesDetail the sales detail
	 */
	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}
}