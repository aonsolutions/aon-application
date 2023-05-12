package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProcessTransitionTypeDB;

@Entity
@Table(name="process_transition_type")
public class ProcessTransitionType extends ProcessTransitionTypeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}