package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.AreaStatus;

@Entity
@Table(name="area")
public class Area implements ITransferObject {

	private Integer id;
	
	private String description;

	private AreaGroup areaGroup;
	
	private AreaStatus status;
	
	private Double budget;
	
	private Double estimate;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false, length=50)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn( name="area_group", nullable=false )
	public AreaGroup getAreaGroup() {
		return areaGroup;
	}

	public void setAreaGroup(AreaGroup areaGroup) {
		this.areaGroup = areaGroup;
	}

	public AreaStatus getStatus() {
		return status;
	}

	public void setStatus(AreaStatus status) {
		this.status = status;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Double getEstimate() {
		return estimate;
	}

	public void setEstimate(Double estimate) {
		this.estimate = estimate;
	}
	
	
}