package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.config.enumeration.TagType;
import com.esferalia.aon.entity.master.TagDB;

@Entity
@Table(name="tag")
@Heritable(force=true)
public class Tag extends TagDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getShortName() {
		return (getName().length() > 16) ? StringUtils.substring(getName(), 0, 16) : getName();
	}

	@Transient
	public boolean isPacking() {
		return getType() == TagType.PACKING;
	}

}