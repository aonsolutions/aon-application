package com.code.gbp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="supplier_observation")
public class SupplierObservation implements ITransferObject {

	private Integer id;
	
	private Supplier supplier;
	
	private Date observationDate;
	
	private String observation;

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
	@JoinColumn( name="supplier", nullable=false )
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Column(name="observation_date", nullable=false)
	@Temporal(value=TemporalType.DATE)
	public Date getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(Date observationDate) {
		this.observationDate = observationDate;
	}
	
	@Column(length=65535)
	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	@Transient
	public String getShortObservation() {
		if (observation != null &&
				observation.length()>SHORT_DESC_LENGTH)
			return observation.substring(0,SHORT_DESC_LENGTH)+"...";
		return observation;
	}

	private int SHORT_DESC_LENGTH = 80; 
}