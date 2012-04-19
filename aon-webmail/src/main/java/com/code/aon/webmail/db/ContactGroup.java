package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContactGroupDB;

@Entity
@Table(name="contact_group")
public class ContactGroup extends ContactGroupDB {

	private static final long serialVersionUID = 1L;

}
