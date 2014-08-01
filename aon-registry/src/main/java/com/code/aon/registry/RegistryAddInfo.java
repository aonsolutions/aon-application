package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistryAddInfoDB;

@Entity
@Table(name="raddinfo")
public class RegistryAddInfo extends RegistryAddInfoDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public RegistryAddInfo() {
		setValueDate( new Date());
	}
	

}