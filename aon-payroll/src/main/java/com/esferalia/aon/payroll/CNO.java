package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mvel2.MVEL;

import com.esferalia.aon.entity.master.CNODB;

@Entity
@Table(name = "cno")
public class CNO extends CNODB {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getFullDescription() {
		return getId() != null ? ("(" + getCode() + ") " + getTitle()) : "";
	}

}
