package com.code.aon.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ActionDB;

@Entity
@Table(name="action")
public class Action extends ActionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	
}