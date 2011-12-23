package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RegistryDirStaffDB;

@Entity
@Table(name="rdir_staff")
public class RegistryDirStaff extends RegistryDirStaffDB {

	private static final long serialVersionUID = 1L;
}
