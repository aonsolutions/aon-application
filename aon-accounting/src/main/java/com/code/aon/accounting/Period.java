package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.PeriodDB;

@Entity
@Table(name="account_period", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class Period extends PeriodDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
