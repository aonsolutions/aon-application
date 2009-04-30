package com.code.aon.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.project.enumeration.DossierStatus;

@Entity
@Table(name="dossier")
public class Dossier implements ITransferObject {
	
	private static final long serialVersionUID = -8950442685548951095L;

	private Integer id;
	
	private Customer customer;
	
	private DossierType dossierType;
	
	private String number;
	
	private String location;
	
	private DossierStatus status;

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
	@JoinColumn( name="customer",nullable=false )
	@ForeignKey(name = "FK_DOSSIER_CUSTOMER")
	@Index(name = "IDX_DOSSIER_CUSTOMER")						            	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne
	@JoinColumn(name="dossier_type", nullable=false)
	@ForeignKey(name = "FK_DOSSIER_DOSSIER_TYPE")
	@Index(name = "IDX_DOSSIER_DOSSIER_TYPE")						            		
	public DossierType getDossierType() {
		return dossierType;
	}

	public void setDossierType(DossierType dossierType) {
		this.dossierType = dossierType;
	}

	@Column(length=16, nullable=false)
	@Index(name = "IDX_DOSSIER_NUMBER")	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(length=20)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(nullable=false)
	public DossierStatus getStatus() {
		return status;
	}

	public void setStatus(DossierStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (id == null) {
			return super.equals(obj);
		}
		if (obj instanceof Dossier) {
			return (this.id.equals(((Dossier) obj).getId()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(customer).append(dossierType).
			append(id).append(location).
			append(number).append(status).
			toHashCode();
	}
	
}