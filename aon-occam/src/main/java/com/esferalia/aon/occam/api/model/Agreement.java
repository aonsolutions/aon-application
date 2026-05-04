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

	public Agreement setId(Integer id) {
		this.id = id;
		return this;
	}

	@Override
	public Integer getDomain() {
		return domain;
	}

	public Agreement setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Agreement setDescription(String name) {
		this.description = name;
		return this;
	}
	
	public Collection<Payment> getPayments() {
		return Collections.unmodifiableCollection(payments);
	}
	
	public void addPayment(Payment payment) {
		payments.add(payment);
	}
	

}
