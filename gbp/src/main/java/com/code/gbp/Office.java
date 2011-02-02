package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="office")
public class Office implements ITransferObject {

	private Integer id;
	
	private Integer code;
	
	private String name;

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

	@Column(nullable=false, length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}