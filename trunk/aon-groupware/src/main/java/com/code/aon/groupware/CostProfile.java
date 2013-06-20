package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CostProfileDB;

@Entity
@Table(name="cost_profile")
public class CostProfile extends CostProfileDB {
	
	private static final long serialVersionUID = 1L;
	
}