package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public class CommissionTypeCommission implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private Integer commissionType;
	private Integer commission;
	
	public Integer getId() {
		return id;
	}
	public CommissionTypeCommission setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CommissionTypeCommission setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getCommissionType() {
		return commissionType;
	}
	public CommissionTypeCommission setCommissionType(Integer commissionType) {
		this.commissionType = commissionType;
		return this;
	}
	public Integer getCommission() {
		return commission;
	}
	public CommissionTypeCommission setCommission(Integer commission) {
		this.commission = commission;
		return this;
	}
}
