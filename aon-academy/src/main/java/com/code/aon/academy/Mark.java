package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MarkDB;

@Entity
@Table(name="mark")
public class Mark extends MarkDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}

