package com.code.aon.commercial;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.enumeration.OfferDetailCommissionStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="offer_detail_commission")
public class OfferDetailCommission implements ITransferObject {

	private static final long serialVersionUID = -6691654954195445777L;

	private Integer id;
	private OfferDetail offerDetail;
	private double commission;
	private double amount;
	private OfferDetailCommissionStatus status;
	private Date payDate;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="offer_detail", nullable=false)
    @ForeignKey(name = "FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL")
    @Index(name = "IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL")
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}

	public void setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
	}

	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission= commission;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public OfferDetailCommissionStatus getStatus() {
		return status;
	}
	public void setStatus(OfferDetailCommissionStatus status) {
		this.status = status;
	}

	@Column(name="pay_date")
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	
	@Transient
	public Offer getOffer() {
		return (getOfferDetail()!=null?getOfferDetail().getOffer():null);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final OfferDetailCommission o = (OfferDetailCommission) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.offerDetail, o.offerDetail)
				.append(this.commission, o.commission)
				.append(this.amount, o.amount)
				.append(this.status, o.status)
				.append(this.payDate, o.payDate)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(offerDetail)
			.append(commission)
			.append(amount)
			.append(status)
			.append(payDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
