package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.BatchConfigStatus;

@Entity
@Table(name="batch_config")
public class BatchConfig implements ITransferObject {

	private Integer id;
	
	private Integer code;
	
	private String description;

	private BatchConfigStatus status;

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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BatchConfigStatus getStatus() {
		return status;
	}

	public void setStatus(BatchConfigStatus status) {
		this.status = status;
	}
	
	
}