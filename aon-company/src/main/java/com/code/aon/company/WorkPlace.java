package com.code.aon.company;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.esferalia.aon.entity.master.WorkPlaceDB;
import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;

@Entity
@Table(name="workplace")
public class WorkPlace extends WorkPlaceDB implements IScopable  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public WorkPlace() {
		setActive(true);
	}

}
