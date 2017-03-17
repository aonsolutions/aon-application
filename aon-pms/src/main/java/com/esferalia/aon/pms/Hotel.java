package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.customer.Customer;
import com.esferalia.aon.entity.master.HotelDB;
import com.esferalia.aon.pms.sql.ISQLConstants;

@Entity
@Table(name="hotel")
public class Hotel extends HotelDB implements IScopable, ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getAlias() {
		String alias = getWorkPlace().getDescription();
		alias = alias.replaceAll("(?i)" + PLAYASOL, "");
		alias = alias.replaceAll("(?i)" + APARTAMENTOS, "");
		alias = alias.replaceAll("(?i)" + APTOS, "");
		alias = alias.replaceAll("(?i)" + APTS, "");
		alias = alias.replaceAll("(?i)" + APTHOTEL, "");
		alias = alias.replaceAll("(?i)" + HOTEL, "");
		return alias.trim();
	}

	@Transient
	public Customer getCustomer() {
		return getWorkPlace().getCustomer();
	}

}
