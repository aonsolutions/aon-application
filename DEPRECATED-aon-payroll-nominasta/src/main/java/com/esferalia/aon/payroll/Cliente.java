package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.ICliente;

@Entity
@Table(name = "cliente")
public class Cliente implements ITransferObject, ICliente {

	private static final long serialVersionUID = 1195024884016075841L;
	
	private Integer id;
	private String inactivoBD;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Column(name = "inactivo", length = 1)
	public String getInactivoBD() {
		return inactivoBD;
	}
	@Override
	public void setInactivoBD(String inactivoBD) {
		this.inactivoBD = inactivoBD;
	}
	
	@Override
	@Transient
	public boolean isInactivo() {
		return ("S".equals(getInactivoBD()));
	}
	@Override
	public void setInactivo(boolean inactivo) {
		setInactivoBD((inactivo)?"S":"N");
	}	
	
}
