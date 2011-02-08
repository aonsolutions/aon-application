package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "contract_batch")
public class ContractBatch implements ITransferObject {
	
	private static final long serialVersionUID = -7639337685373561673L;

	private Integer id; 
	private Date date; 
	private Date redNotifyDate;
	private Integer redNotifyId;
	private Date redResponseDate;
	private Integer redResponseId;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=4)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "red_notify_date")
	public Date getRedNotifyDate() {
		return redNotifyDate;
	}
	public void setRedNotifyDate(Date redNotifyDate) {
		this.redNotifyDate = redNotifyDate;
	}
	
	@Column(name = "red_notify_id")
	public Integer getRedNotifyId() {
		return redNotifyId;
	}
	public void setRedNotifyId(Integer redNotifyId) {
		this.redNotifyId = redNotifyId;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "red_response_date")
	public Date getRedResponseDate() {
		return redResponseDate;
	}
	public void setRedResponseDate(Date redResponseDate) {
		this.redResponseDate = redResponseDate;
	}
	
	@Column(name = "red_response_id")
	public Integer getRedResponseId() {
		return redResponseId;
	}
	public void setRedResponseId(Integer redResponseId) {
		this.redResponseId = redResponseId;
	}
	
	
	
}

