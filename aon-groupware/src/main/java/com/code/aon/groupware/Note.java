package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.NoteDB;

@Entity
@Table(name="note")
public class Note extends NoteDB {

	private static final long serialVersionUID = 1L;
	
}