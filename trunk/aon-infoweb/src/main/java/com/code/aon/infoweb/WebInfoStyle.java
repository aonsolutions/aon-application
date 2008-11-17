package com.code.aon.infoweb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="web_info_style")
public class WebInfoStyle implements ITransferObject {
	
	private Integer id;
	
	private String variable;
		
	private String value;
	
	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column( name="variable",length=128,nullable=false )
	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	@Column(name="value",length=255)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}