package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Agreement implements Serializable, HasId, HasDomain {

	private Integer id;
	private Integer domain;
	private String description;
	
	private List<Payment> payments; 

	public Agreement() {
		payments = new LinkedList<Payment>(); // TODO: Concurrent
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String name) {
		this.description = name;
	}
	
	public Collection<Payment> getPayments() {
		return Collections.unmodifiableCollection(payments);
	}
	
	public void addPayment(Payment payment) {
		payments.add(payment);
	}
	

}
