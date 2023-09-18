package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CNAEDB;

@Entity
@Table(name="cnae")
public class CNAE extends CNAEDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
