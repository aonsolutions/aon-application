package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.master.UserDB;

@Entity
@Table(name="user")
public class EnterpriseUser extends UserDB implements IRegistry{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private Enterprise enterprise; 	
    private Registry registry; 	
	
	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

	@Override
	@OneToOne
    @JoinColumn(name="registry", updatable = false )
	public Registry getRegistry() {
		return this.registry;
	}

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
}