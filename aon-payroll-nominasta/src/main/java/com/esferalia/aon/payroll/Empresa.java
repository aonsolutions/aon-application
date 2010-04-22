package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.IEmpresa;

@Entity
@Table(name = "emprnif")
public class Empresa implements ITransferObject, IEmpresa {

	private static final long serialVersionUID = -3266513951564213596L;
	
	private Integer id;
    private Registry registry;
	private String name;
	

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	@Column(name = "descripcion", nullable = false, length = 60)
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
    @Embedded
	public Registry getRegistry() {
		return registry;
	}
	@Override
	public void setRegistry(IRegistry registry) {
		this.registry = (Registry) registry;
	}
	
	@Override
	@Transient
	public boolean isActive() {
		return true;
	}

}
