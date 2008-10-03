package com.code.aon.tasDelivery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.commercial.Offer;
import com.code.aon.common.ITransferObject;
import com.code.aon.tas.SupportOrder;
import com.code.aon.warehouse.Delivery;

/**
 * Transfer Object that represents an entity of a Tas Delivery.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name="tas_delivery")
public class TasDelivery implements ITransferObject {

	/**
	 * Unique key
	 */
	private Integer Id;
	
	/**
	 * Support order linked to the offer
	 */
	private SupportOrder supportOrder;
	
    /**
     * Delivery linked to the Support order 
     */
    private Delivery delivery;

    /**
     * Delivery linked to the Support order 
     */
    private Offer offer;

	/**
	 * Returns the unique key.
	 * 
	 * @return unique key.
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return Id;
	}

	/**
	 * Assigns the unique key.
	 * 
	 * @param id
	 *            unique key.
	 */
	public void setId(Integer id) {
		Id = id;
	}

	/**
	 * Returns the support order
	 * 
	 * @return support order
	 */
	@ManyToOne
	@JoinColumn( name="support_order", nullable = false)
	public SupportOrder getSupportOrder() {
		return supportOrder;
	}

	/**
	 * Assigns the support order
	 * 
	 * @param supportOrder
	 */
	public void setSupportOrder(SupportOrder supportOrder) {
		this.supportOrder = supportOrder;
	}

	/**
	 * Returns the delivery
	 * 
	 * @return delivery
	 */
	@ManyToOne
	@JoinColumn( name="delivery", nullable = false)
	public Delivery getDelivery() {
		return delivery;
	}

	/**
	 * Assigns the delivery
	 * 
	 * @param delivery
	 */
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

    /**
     * Returns the offer
     * 
     * @return offer
     */
    @ManyToOne
    @JoinColumn( name="offer")
    public Offer getOffer() {
        return offer;
    }

    /**
     * Assigns the offer
     * 
     * @param offer
     */
    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}
