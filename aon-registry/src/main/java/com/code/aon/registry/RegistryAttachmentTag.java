package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.RegistryAttachmentTagDB;

@Entity
@Table(name="rattach_tag")
@Heritable(force=true)
public class RegistryAttachmentTag extends RegistryAttachmentTagDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}