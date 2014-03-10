package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TagDB;

@Entity
@Table(name="tag")
@Heritable
public class Tag extends TagDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getShortName() {
		return (getName().length() > 16) ? StringUtils.substring(getName(), 0, 16) : getName();
	}

}