package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;

@Entity
@Table(name="invoice_attach")
public class InvoiceAttachment implements IAttachment {

	private static final long serialVersionUID = -6774043069274297973L;

    private Integer id;
    private Invoice invoice;
    private MimeType mimeType;
    private byte[] data;
    private String description;
    private Integer size;
    
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    @ManyToOne
    @JoinColumn(name="invoice", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_INVOICE_ATTACH_INVOICE")
    @Index(name = "IDX_INVOICE_ATTACH_INVOICE")
	public Invoice getInvoice() {
        return this.invoice;
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

	public MimeType getMimeType() {
        return mimeType;
    }
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @Lob
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
	
	@Column(length=64)
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoiceAttachment o = (InvoiceAttachment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.data, o.data)				
				.append(this.description, o.description)
				.append(this.mimeType, o.mimeType)				
				.append(this.invoice, o.invoice)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(data)
			.append(description)	
			.append(mimeType)
			.append(invoice)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("id", id).
			append("description", description).
			append("mimeType", mimeType).
			append("invoice", invoice.getId()).
			toString();
	}

}