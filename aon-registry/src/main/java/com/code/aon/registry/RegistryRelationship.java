package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistryRelationshipDB;

@Entity
@Table(name="rrelationship")
public class RegistryRelationship extends RegistryRelationshipDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
   
}