package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;

@SuppressWarnings("serial")
@Entity
@Table(name="series")
public class Series implements ITransferObject, IConfidentialable {

	private String id;
	private String description;
	private boolean offer;
	private boolean sales;
	private boolean delivery;
	private boolean invoice;
	private boolean rectification;
	private SecurityLevel securityLevel;
	private boolean active;

	@Id
	@Column(nullable=false, length=5)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length=32, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isOffer() {
		return offer;
	}

	public void setOffer(boolean offer) {
		this.offer = offer;
	}

	public boolean isSales() {
		return sales;
	}

	public void setSales(boolean sales) {
		this.sales = sales;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public boolean isInvoice() {
		return invoice;
	}

	public void setInvoice(boolean invoice) {
		this.invoice = invoice;
	}

	public boolean isRectification() {
		return rectification;
	}

	public void setRectification(boolean rectification) {
		this.rectification = rectification;
	}

	@Column(name="security_level", nullable = false)
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}

	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Series o = (Series) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)			
				.append(this.delivery, o.delivery)
				.append(this.description, o.description)
				.append(this.invoice, o.invoice)
				.append(this.offer, o.offer)
				.append(this.rectification, o.rectification)
				.append(this.sales, o.sales)
				.append(this.securityLevel, o.securityLevel)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(delivery)		
			.append(description)		
			.append(invoice)		
			.append(offer)		
			.append(rectification)		
			.append(sales)		
			.append(id)
			.append(securityLevel)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}