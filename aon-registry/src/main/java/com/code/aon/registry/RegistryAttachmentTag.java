package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistryAttachmentTagDB;

@Entity
@Table(name="rattach_tag")
public class RegistryAttachmentTag extends RegistryAttachmentTagDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}