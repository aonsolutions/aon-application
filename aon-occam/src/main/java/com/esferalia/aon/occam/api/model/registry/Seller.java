package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.SellerStatus;

public class Seller extends Registry implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private CommissionType commissionType;
	private Scope scope;
	private SellerStatus status;
	private TaskHolder taskHolder;
	
	public Seller copy(Registry registry) {
		return super.copy( registry, this);
	}

	public Seller setId(Integer id) {
		super.setId(id);
		return this;
	}
	
	public Seller setDomain(Integer id) {
		super.setDomain(new Domain().setId(id));
		return this;
	}
	
	public CommissionType getCommissionType() {
		if(commissionType == null) {
			commissionType = new CommissionType();
		}
		return commissionType;
	}
	
	public Seller setCommissionType(CommissionType commissionType) {
		this.commissionType = commissionType;
		return this;
	}

	public Scope getScope() {
		if(scope == null) {
			scope = new Scope();
		}
		return scope;
	}
	
	public Seller setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public SellerStatus getStatus() {
		return status;
	}
	
	public Seller setStatus(SellerStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isActive() {
		return SellerStatus.ACTIVE == getStatus();
	}
	
	public Seller setActive(boolean active) {
		this.status = active ? SellerStatus.ACTIVE : SellerStatus.INACTIVE;
		return this;
	}
	
	@Override
	public boolean isEmpty() {
		return super.isEmpty() 
			&& (getScope() == null || getScope().isEmpty())
			&& getStatus() == null
			&& (getCommissionType() == null || getCommissionType().isEmpty());
	}

	public TaskHolder getTaskHolder() {
		if(taskHolder == null) {
			taskHolder = new TaskHolder();
		}
		return taskHolder;
	}
	
	public Seller setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
}
