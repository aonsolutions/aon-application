package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.campaign.enumeration.DateReference;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.WorkGroup;

@Entity
@Table(name="process_detail")
public class ProcessDetail implements ITransferObject {

	private static final long serialVersionUID = 1030141795904320316L;

	private Integer id;
	
	private Process process;
	
	private String description;
	
	private int position;
	
	private DateReference dateReference;
	
	private int days;
	
	private int alertDays;
	
	private WorkGroup workgroup;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name="process",nullable=false )
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	@Column(name="date_reference")
	public DateReference getDateReference() {
		return dateReference;
	}

	public void setDateReference(DateReference dateReference) {
		this.dateReference = dateReference;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Column(name="alert_days")
	public int getAlertDays() {
		return alertDays;
	}

	public void setAlertDays(int alertDays) {
		this.alertDays = alertDays;
	}

	@ManyToOne
	@JoinColumn( name="workgroup" )
	public WorkGroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(WorkGroup workgroup) {
		this.workgroup = workgroup;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ProcessDetail) {
			ProcessDetail dt = (ProcessDetail) obj;
			if (!ObjectUtils.equals(getId(), dt.getId())) {
				return false;
			}
			return true;
		}
		return false;
	}

}