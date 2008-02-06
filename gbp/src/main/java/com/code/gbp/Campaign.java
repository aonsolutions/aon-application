package com.code.gbp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.CampaignStatus;

@Entity
@Table(name="campaign")
public class Campaign implements ITransferObject {

	private Integer id;
	
	private Integer code;
	
	private String name;
	
	private CampaignStatus status;
	
	private Date startDate;
	
	private Date endDate;
	
	private Double budget;
	
	private Date offerDueDate;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false)
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Column(length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CampaignStatus getStatus() {
		return status;
	}

	public void setStatus(CampaignStatus status) {
		this.status = status;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="start_date", nullable=false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="offer_due_date")
	public Date getOfferDueDate() {
		return offerDueDate;
	}

	public void setOfferDueDate(Date offerDueDate) {
		this.offerDueDate = offerDueDate;
	}
}