package com.code.aon.tas;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.MakeDB;

@Entity
@Table(name="make")
public class Make extends MakeDB {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getFullName() {
		return (getName() == null) ? "" : getName();
	}
	
}
