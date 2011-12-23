package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.WorkPlaceDB;

@Entity
@Table(name="workplace")
public class WorkPlace extends WorkPlaceDB  {

	private static final long serialVersionUID = 1L;

}
