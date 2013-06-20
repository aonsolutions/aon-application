package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContactDataDB;

@Entity
@Table(name="contact_data")
public class ContactData extends ContactDataDB {

	private static final long serialVersionUID = 1L;

}
