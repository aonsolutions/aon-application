package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ContractLeaveDB;

@Entity
@Table(name="contract_leave")
public class ContractLeave extends ContractLeaveDB {
	
	private static final long serialVersionUID = 1L;

	private Date occupationalDiseaseDate;

	@Transient
	public Date getOccupationalDiseaseDate() {
		return occupationalDiseaseDate;
	}
	public void setOccupationalDiseaseDate(Date occupationalDiseaseDate) {
		this.occupationalDiseaseDate = occupationalDiseaseDate;
	}
	
}

