package com.esferalia.aon.carrier;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.registry.IRegistry;
import com.esferalia.aon.entity.master.CarrierDB;

@Entity
@Table(name="carrier")
@PrimaryKeyJoinColumn(name="registry")
public class Carrier extends CarrierDB implements IRegistry, IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
