package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import com.code.aon.commercial.Offer;
import com.code.aon.common.ITransferObject;
import com.code.aon.ebackoffice.enumeration.PaymentStatus;


/**
 * Transfer Object that represents a eCommerce Offer payment Info.
 * 
 * @author Esferalia Networks. David Uriarte - 3/09/2009
 */
@Entity
@Table(name="ec_offer_pay_info")
public class EcOfferPayment implements ITransferObject {
	
	
	private Integer id;	
	private Offer offer;	
	private PaymentStatus status;
	private Integer authorizationNumber;
	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="offer", nullable=false)
	@ForeignKey(name = "FK_EC_OFFER_PAY_INFO_OFFER")
	@Index(name = "IDX_EC_OFFER_PAY_INFO_OFFER")
	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	
	@Column(name="authorization_number")
	public Integer getAuthorizationNumber() {
		return authorizationNumber;
	}
	public void setAuthorizationNumber(Integer authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}
	
	@Column(name="payment_status")
	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	

	