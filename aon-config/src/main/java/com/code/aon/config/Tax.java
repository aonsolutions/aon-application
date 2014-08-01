package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.config.enumeration.TaxType;
import com.esferalia.aon.entity.master.TaxDB;

@Entity
@Table(name="tax")
@Heritable
public class Tax extends TaxDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isVat() {
		return (getType() == TaxType.VAT);
	}

	@Transient
	public boolean isRetention() {
		return (getType() == TaxType.RETENTION);
	}

}
