package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="process_transition_type")
public class ProcessTransitionType implements ITransferObject {

	private static final long serialVersionUID = 1030141795904320316L;

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

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ProcessTransitionType) {
			ProcessTransitionType dt = (ProcessTransitionType) obj;
			if (!ObjectUtils.equals(getId(), dt.getId())) {
				return false;
			}
			return true;
		}
		return false;
	}

}