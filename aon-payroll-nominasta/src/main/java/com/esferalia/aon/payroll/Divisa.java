package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IDivisa;

@Entity
@Table(name = "divisa")
public class Divisa implements ITransferObject, IDivisa {

	private static final long serialVersionUID = 2634931829089059279L;

	private String cdg;
	private String description;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 3)
	@Override
	public String getCdg() {
		return this.cdg;
	}
	@Override
	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name = "descripcion", length = 25)
	@Override
	public String getDescription() {
		return this.description;
	}
	@Override
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

}
