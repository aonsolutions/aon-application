package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProcessDB;

@Entity
@Table(name="process")
public class Process extends ProcessDB {
	
	private static final long serialVersionUID = 1L;
	
    public Process() {
		setActive(true);
	}

}