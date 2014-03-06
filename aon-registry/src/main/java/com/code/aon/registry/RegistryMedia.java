package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.RegistryMediaDB;

@Entity
@Table(name="rmedia")
@Heritable
public class RegistryMedia extends RegistryMediaDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


}