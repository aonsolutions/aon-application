package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IAEDB;

@Entity
@Table(name="iae")
public class IAE extends IAEDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
