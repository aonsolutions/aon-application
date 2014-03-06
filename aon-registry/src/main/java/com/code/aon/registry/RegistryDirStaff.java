package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.RegistryDirStaffDB;

@Entity
@Table(name="rdir_staff")
@Heritable
public class RegistryDirStaff extends RegistryDirStaffDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
}
