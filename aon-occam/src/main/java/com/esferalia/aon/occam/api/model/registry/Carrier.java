package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;

public class Carrier extends Registry implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Scope scope;
	private CarrierStatus status;

	public Carrier copy(Registry registry) {
		return super.copy( registry, this);
	}

	@Override
	public Carrier setId(Integer id) {
		super.setId(id);
		return this;
	}
	
	public Scope getScope() {
		return scope;
	}

	public Carrier setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public CarrierStatus getStatus() {
		if(status == null) {
			status = CarrierStatus.ACTIVE;
		}
		return status;
	}

	public Carrier setStatus(CarrierStatus status) {
		this.status = status;
		return this;
	}
	
}
