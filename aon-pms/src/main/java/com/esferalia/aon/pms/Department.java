package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.DepartmentDB;

@Entity
@Table(name="department")
public class Department extends DepartmentDB {

	private static final long serialVersionUID = 1L;

}
