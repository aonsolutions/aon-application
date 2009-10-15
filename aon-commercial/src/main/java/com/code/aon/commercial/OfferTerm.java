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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a OfferTerm.
 */
@Entity
@Table(name="offer_term")
public class OfferTerm implements ITransferObject {
	
	private static final long serialVersionUID = 4210659065078151277L;

	/** The id. */
	private Integer id;

    /** The name. */
    private String name;
	
	/** The offer. */
	private Offer offer;
	
    /** The description. */
    private String description;

    /** If the OfferTerm is particular. */
    private boolean particular;    
    
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
	 * Gets the offer.
	 * 
	 * @return the offer
	 */
	@ManyToOne
	@JoinColumn( name="offer", nullable=false , updatable=false)
    @ForeignKey(name = "FK_OFFER_TERM_OFFER")
    @Index(name = "IDX_OFFER_TERM_OFFER")	
	public Offer getOffer() {
		return offer;
	}

	/**
	 * Sets the offer.
	 * 
	 * @param offer the offer
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	
    /**
     * Gets the name.
     * 
     * @return the name
     */
    @Column(length=32, nullable = false)
	public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }	

    /**
     * Gets the description.
     * 
     * @return the description
     */
	@Lob
	@Type(type="stringClob")	
    public String getDescription() {
        return description;
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
	 * Checks if is particular.
	 * 
	 * @return true, if is particular
	 */
	@Column(nullable = false)
	public boolean isParticular() {
		return particular;
	}

	/**
	 * Sets the particular.
	 * 
	 * @param signed the new particular
	 */
	public void setParticular(boolean particular) {
		this.particular = particular;
	}	
    
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final OfferTerm o = (OfferTerm) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)				
				.append(this.particular, o.particular)
				.append(this.name, o.name)				
				.append(this.offer, o.offer)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)	
			.append(particular)			
			.append(id)			
			.append(name)
			.append(offer)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("description", StringUtils.abbreviate(description, 64)).
			append("particular", particular).
			append("id", id).
			append("name", name).
			append("offer", offer.getId()).
			toString();
	}	
}