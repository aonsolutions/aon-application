package com.code.aon.registry;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistrySellerDB;

@Entity
@Table(name="rseller")
public class RegistrySeller extends RegistrySellerDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}