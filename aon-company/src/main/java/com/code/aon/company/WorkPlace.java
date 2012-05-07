package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.WorkPlaceDB;
import com.code.aon.config.IScopable;

@Entity
@Table(name="workplace")
public class WorkPlace extends WorkPlaceDB implements IScopable  {

	private static final long serialVersionUID = 1L;

	public WorkPlace() {
		setActive(true);
	}

}
