package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContactGroupDetailDB;

@Entity
@Table(name="contact_group_detail")
public class ContactGroupDetail extends ContactGroupDetailDB {

	private static final long serialVersionUID = 1L;

}
