package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * TransferObject that represents a Balance.
 */
@Entity
@Table(name = "balance")
public class Balance implements ITransferObject {
		
	private Integer id;
	private String name;
	private Boolean removable;
	private Integer type;


	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="name", length=64, nullable=false)
    public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@Column(name="removable")
	public Boolean getRemovable() {
		return removable;
	}


	public void setRemovable(Boolean removable) {
		this.removable = removable;
	}

	@Column(name="type")
	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}



}