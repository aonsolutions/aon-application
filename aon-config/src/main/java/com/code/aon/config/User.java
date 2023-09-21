package com.code.aon.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.Toolbar;
import com.esferalia.aon.entity.master.UserDB;

@Entity
@Table(name="user")
public class User extends UserDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer enterprise;
	private Integer registry;
	
	public User() {
		setActive(true);
		setToolbar(Toolbar.GOOGLE);
	}	
	
    @Column(name="enterprise")
	public Integer getEnterprise() {
		return this.enterprise;
	}
	public void setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
	}

    @Column(name="registry")
	public Integer getRegistry() {
		return this.registry;
	}
	public void setRegistry(Integer registry) {
		this.registry = registry;
	}
	
	
}