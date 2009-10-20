package com.code.aon.commercial;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.MimeType;

@Entity
@Table(name="offer_attach")
public class OfferAttachment implements ITransferObject {

	private static final long serialVersionUID = -6572435143671792940L;

	/** The id. */
    private Integer id;

    /** The offer. */
    private Offer offer;

    /** The mime type. */
    private MimeType mimeType;

    /** The data (binary). */
    private byte[] data;

    /** The description. */
    private String description;
    
    /** The size in bytes. */
    private Integer size;
	    
    /**
     * The empty constructor.
     */
    public OfferAttachment() {
    }

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
     * @param primaryKey the primary key
     * @param id the id
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    /**
     * Gets the offer.
     * 
     * @return the offer
     */
    @ManyToOne
    @JoinColumn(name="offer", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_OFFER_ATTACH_OFFER")
    @Index(name = "IDX_OFFER_ATTACH_OFFER")
	public Offer getOffer() {
        return this.offer;
    }

    /**
     * Sets the offer.
     * 
     * @param invoice the offer
     */
    public void setOffer(Offer offer) {
        this.offer = offer;
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
     * @return the data
     */
    @Lob
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the data.
     * 
     * @param data the data
     */
    public void setData(byte[] data) {
        this.data = data;
    }
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64)
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * 
	 * @param size the size
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
	
	/**
	 * Clones the OfferAttachment.
	 * 
	 * @return the object
	 * 
	 * @throws CloneNotSupportedException the clone not supported exception
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final OfferAttachment o = (OfferAttachment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.data, o.data)				
				.append(this.description, o.description)
				.append(this.mimeType, o.mimeType)				
				.append(this.offer, o.offer)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(data)
			.append(description)	
			.append(id)			
			.append(mimeType)
			.append(offer)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("description", description).
			append("id", id).
			append("mimeType", mimeType).
			append("offer", offer.getId()).
			toString();
	}

}

