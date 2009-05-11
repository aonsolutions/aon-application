package com.code.aon.sales;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.MimeType;

/**
 * Transfer Object that represents an attachment of a sale.
 * 
 * @author Consulting & Development. Joseba Urkiri - 16-feb-2006
 * @since 1.0
 */
@Entity
@Table(name="sales_attach")
public class SalesAttachment implements ITransferObject {
	 
	private static final long serialVersionUID = 363976096333530545L;

	/** The id. */
	private Integer id;
	
	/** The sales. */
	private Sales sales;
	
	/** The mime type. */
	private MimeType mimeType;
	
	/** The data(binary). */
	private byte[] data;

	
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
	 * Gets the sale.
	 * 
	 * @return the sale
	 */
	@ManyToOne
	@JoinColumn(name="sales", nullable = false, updatable = false)
	public Sales getSales() {
		return sales;
	}
	
	/**
	 * Sets the sale.
	 * 
	 * @param sales the sale
	 */
	public void setSales(Sales sales) {
		this.sales = sales;
	}
	
	/**
	 * Gets the mime type.
	 * 
	 * @return the mime type
	 */
	public MimeType getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the mime type.
	 * 
	 * @param mimeType the mime type
	 */
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data(binary)
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Sets the data.
	 * 
	 * @param data the data(binary)
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
}