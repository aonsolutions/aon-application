package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.AddInfoType;

@Entity
@Table(name="supplier_add_info")
public class SupplierAddInfo implements ITransferObject {

	private Integer id;
	
	private Supplier supplier;
	
	private AddInfoType type;
	
	private String value;

	
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

	@Column(nullable=false)
	public AddInfoType getType() {
		return type;
	}

	public void setType(AddInfoType type) {
		this.type = type;
	}

	@Column(length=64)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}