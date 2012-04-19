package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContactDB;

@Entity
@Table(name="contact")
public class Contact extends ContactDB {

	private static final long serialVersionUID = 1L;

}
