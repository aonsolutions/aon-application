package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.Mod349DetailDB;

@Entity
@Table(name="fs_mod349_detail")
public class Mod349Detail extends Mod349DetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
