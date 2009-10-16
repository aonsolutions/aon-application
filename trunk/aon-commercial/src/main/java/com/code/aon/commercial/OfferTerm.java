package com.code.aon.commercial;

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

/**
 * Transfer Object that represents a OfferTerm.
 */
@Entity
@Table(name="offer_term")
public class OfferTerm implements ITransferObject {
	
	private static final long serialVersionUID = 4210659065078151277L;

	/** The id. */
	private Integer id;

	/** The offer. */
	private Offer offer;
	
	/** The offer. */
	private CommercialTerm term;
    
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
	 * Gets the term.
	 * 
	 * @return the term
	 */
	@ManyToOne
	@JoinColumn( name="term", nullable=false , updatable=false)
    @ForeignKey(name = "FK_OFFER_TERM_TERM")
    @Index(name = "IDX_OFFER_TERM_TERM")		
	public CommercialTerm getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 * 
	 * @param term the new term
	 */
	public void setTerm(CommercialTerm term) {
		this.term = term;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final OfferTerm o = (OfferTerm) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.offer, o.offer)			
				.append(this.term, o.term)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(offer)
			.append(term)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
}