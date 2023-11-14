package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

public class DeliveryInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Integer delivery;
	private DeliveryCommunicationType type;
	private DeliveryCommunicationStatus status;

	public Integer getId() {
		return id;
	}

	public DeliveryInfo setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public DeliveryInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getDelivery() {
		return delivery;
	}

	public DeliveryInfo setDelivery(Integer delivery) {
		this.delivery = delivery;
		return this;
	}

	public DeliveryCommunicationType getType() {
		return type;
	}

	public DeliveryInfo setType(DeliveryCommunicationType type) {
		this.type = type;
		return this;
	}
	
	public DeliveryCommunicationStatus getStatus() {
		if(status == null) {
			this.status = DeliveryCommunicationStatus.PENDING;
		}
		return status;
	}

	public DeliveryInfo setStatus(DeliveryCommunicationStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getDelivery() == null
				&& getType() == null;		
	}
}