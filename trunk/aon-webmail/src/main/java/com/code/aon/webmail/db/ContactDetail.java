package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContactDetailDB;

@Entity
@Table(name="contact_detail")
public class ContactDetail extends ContactDetailDB {

	private static final long serialVersionUID = 1L;

}
