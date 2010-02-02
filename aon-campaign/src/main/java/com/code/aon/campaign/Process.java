package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="process")
public class Process implements ITransferObject {
	
	private static final long serialVersionUID = -5491663246478443959L;

	private Integer id;
	
	private String description;

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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (id == null) {
			return super.equals(obj);
		}
		if (obj instanceof Process) {
			return (this.id.equals(((Process) obj).getId()));
		}
		return false;
	}
	
}