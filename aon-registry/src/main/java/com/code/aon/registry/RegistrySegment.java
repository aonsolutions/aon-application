package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistrySegmentDB;

@Entity
@Table(name="rsegment")
public class RegistrySegment extends RegistrySegmentDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}