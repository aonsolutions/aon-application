package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractCalendarEventDB;

@Entity
@Table(name="contract_calendar_event")
public class ContractCalendarEvent extends  ContractCalendarEventDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}