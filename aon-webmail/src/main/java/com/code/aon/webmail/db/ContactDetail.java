package com.code.aon.webmail.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContactDetailDB;

@Entity
@Table(name="contact_detail")
public class ContactDetail extends ContactDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
