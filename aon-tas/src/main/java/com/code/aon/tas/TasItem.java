package com.code.aon.tas;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.TasItemDB;

@Entity
@Table(name="tas_item", uniqueConstraints = {@UniqueConstraint(columnNames="publicCode"), @UniqueConstraint(columnNames="privateCode")})
public class TasItem extends TasItemDB  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public String getFullName() {
		return ((getPublicCode() != null) ? getPublicCode() + " " : "") + ((getModel() != null && getModel().getId() != null) ? "(" + getModel().getFullName() + ")" : "");
	}
	public void setFullName(String value) {
	}

}
