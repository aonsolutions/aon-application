package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContractCalendarEventDB;

@Entity
@Table(name="contract_calendar_event")
public class ContractCalendarEvent extends  ContractCalendarEventDB {

	private static final long serialVersionUID = 1L;

}