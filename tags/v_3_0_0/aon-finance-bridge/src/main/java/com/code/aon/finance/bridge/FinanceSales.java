package com.code.aon.finance.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Finance;
import com.code.aon.finance.bridge.invoicing.remover.DeliveryInvoiceDetailRemover;
import com.code.aon.finance.bridge.invoicing.remover.DirectInvoiceDetailRemover;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.sales.Sales;

/**
 * Transfer Object that represents a union between a Finance and a sales.
 */
@Entity
@Table(name = "finance_sales")
public class FinanceSales implements ITransferObject {

	private static final long serialVersionUID = -8297977676119686982L;

	/** The id. */
	private Integer id;
	
	/** The finance. */
	private Finance finance;
	
	/** The sales. */
	private Sales sales;
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
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
	 * Gets the finance.
	 * 
	 * @return the finance
	 */
	@ManyToOne
	@JoinColumn( name="finance", nullable = false)
	public Finance getFinance() {
		return finance;
	}

	/**
	 * Sets the finance.
	 * 
	 * @param finance the finance
	 */
	public void setFinance(Finance finance) {
		this.finance = finance;
	}

	/**
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	@ManyToOne
	@JoinColumn( name="sales", nullable = false)
	public Sales getSales() {
		return sales;
	}

	/**
	 * Sets the sales.
	 * 
	 * @param sales the sales
	 */
	public void setSales(Sales sales) {
		this.sales = sales;
	}
	
	static {
		try {
			InvoiceRemoverFactory.register(DeliveryInvoiceDetailRemover.class);
			InvoiceRemoverFactory.register(DirectInvoiceDetailRemover.class);
		} catch (InvoicingException e) {
			e.printStackTrace();
		}
	}
	
}