package com.code.aon.webmail.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContactDataDB;

@Entity
@Table(name="contact_data")
public class ContactData extends ContactDataDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
