package com.esferalia.aon.payroll;


import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.IPersona;



@Entity
@Table(name="persona")
public class Persona implements ITransferObject, IPersona  {

	private static final long serialVersionUID = 492566375940771381L;
	
	
	private Integer id;
    private Registry registry;
    private String name;
    private String surname;
    private String lastName;
    private String numSS;
    private String fullName;

	@Id     
    @Column(name="cdg", unique=true, nullable=false, length=4)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
	@Column(name="descripcion", nullable=false, length=35)
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
		this.fullName = null;
	}

	@Override
	@Column(name="nombre", length=25)
	public String getSurname() {
		return surname;
	}
	@Override
	public void setSurname(String surname) {
		this.surname = surname;
		this.fullName = null;
	}

	@Override
	@Column(name="apellido2", length=25)
	public String getLastName() {
		return lastName;
	}
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
		this.fullName = null;
	}

	@Column(name="numss", length=12)
	public String getNumSS() {
		return numSS;
	}

	public void setNumSS(String numSS) {
		this.numSS = numSS;
	}

	@Override
	@Transient
	public String getFullName() {
		if (this.fullName == null) {
			this.fullName = getSurname()==null?"":getSurname();
			String b = getLastName()==null?"":getLastName();
			if (!StringUtils.isBlank(this.fullName)) {
				if (!StringUtils.isBlank(b)) {
					this.fullName = this.fullName + " " + b;
				}
			} else {
				if (!StringUtils.isBlank(b)) {
					this.fullName = b;
				}
			}
			if (!StringUtils.isBlank(this.fullName) && !StringUtils.isBlank(getName())) {
				this.fullName = this.fullName + ", " + getName();
			}
		}
		return this.fullName;
	}

}


