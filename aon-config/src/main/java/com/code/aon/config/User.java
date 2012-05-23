package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.UserDB;

@Entity
@Table(name="user")
public class User extends UserDB {

	private static final long serialVersionUID = 1L;

	private Integer enterprise;
	private Integer registry;
	
	public User() {
		setActive(true);
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
