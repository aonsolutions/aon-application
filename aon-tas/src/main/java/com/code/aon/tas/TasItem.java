package com.code.aon.tas;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

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
