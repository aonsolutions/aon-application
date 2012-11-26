package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.MarkDB;

@Entity
@Table(name="mark")
public class Mark extends MarkDB {

	private static final long serialVersionUID = 1L;
	
}

