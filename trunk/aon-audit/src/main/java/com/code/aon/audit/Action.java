package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ActionDB;

@Entity
@Table(name="action")
public class Action extends ActionDB {

	private static final long serialVersionUID = 1L;

	
}