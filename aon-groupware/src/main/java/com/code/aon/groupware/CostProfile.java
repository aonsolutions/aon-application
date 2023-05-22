package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CostProfileDB;

@Entity
@Table(name="cost_profile")
public class CostProfile extends CostProfileDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}