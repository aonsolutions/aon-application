package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SessionDB;

@Entity
@Table(name = "session")
public class Session extends SessionDB {

	private static final long serialVersionUID = 1L;

	
}