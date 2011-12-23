package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RegistryAddInfoDB;

@Entity
@Table(name="raddinfo")
public class RegistryAddInfo extends RegistryAddInfoDB {
	
	private static final long serialVersionUID = 1L;
	
	public RegistryAddInfo() {
		setValueDate( new Date());
	}
	

}