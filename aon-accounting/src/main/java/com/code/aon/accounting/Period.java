package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.PeriodDB;

@Entity
@Table(name="account_period", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class Period extends PeriodDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
