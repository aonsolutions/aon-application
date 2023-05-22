package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.NoteDB;

@Entity
@Table(name="note")
public class Note extends NoteDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}