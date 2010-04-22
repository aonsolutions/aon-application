package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "sport_category")
public class SportCategory implements ITransferObject {

	private Integer id;

	private String alias;
	
	private boolean default_;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(name = "default_", nullable = false)
	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}

}
