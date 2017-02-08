package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEmployeeCalendar {

	private static Settings SETTINGS = null;

	public static EmployeeCalendarData getEmployeeHour(Connection conn, Integer contract) throws IllegalArgumentException {

		return getHoursByDay(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	private static EmployeeCalendarData getHoursByDay(DSLContext dslContext, Integer contract) throws IllegalArgumentException {

		EmployeeCalendarData employeeInfoCalendar;
		
		ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato = new ArrayList<Quartet<Date, Date, String, String>>();
		
		Result<Record> contratoInfoEmpleado = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.in(
						  ContextVariable.MONDAY_HOURS.getName()
						  ,ContextVariable.TUESDAY_HOURS.getName()
						  ,ContextVariable.WEDNESDAY_HOURS.getName()
						  ,ContextVariable.THURSDAY_HOURS.getName()
						  ,ContextVariable.FRIDAY_HOURS.getName()
						  ,ContextVariable.SATURDAY_HOURS.getName()
						  ,ContextVariable.SUNDAY_HOURS.getName()))
				  .fetch();
		
		for(Record r: contratoInfoEmpleado){
			Quartet<Date, Date, String, String> infoEmployee = new Quartet<Date, Date, String, String>();
			
			infoEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			listaHorasContrato.add(infoEmployee);
		}
		
		Result<Record> tipoDiasContratoEmpleado = dslContext
					.select()
					.from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.and(CONTRACT_DATA.NAME.in(
							ContextVariable.ERE_DAYS.getName()
							,ContextVariable.STRIKE_DAYS.getName()))
					.fetch();
		
		Result<Record> tipoDiasITContratoEmpleado = dslContext
				.select()
				.from(CONTRACT_LEAVE)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.fetch();
		
		for(Record r: tipoDiasContratoEmpleado){
			Quartet<Date, Date, String, String> infoEmployee = new Quartet<Date, Date, String, String>();
			
			infoEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			listaTipoDiasContrato.add(infoEmployee);
		}
		
		for(Record r: tipoDiasITContratoEmpleado){
			Quartet<Date, Date, String, String> infoEmployee = new Quartet<Date, Date, String, String>();
			
			infoEmployee.setStartDate(r.get(CONTRACT_LEAVE.START_DATE))
			.setEndDate(r.get(CONTRACT_LEAVE.END_DATE))
			.setName("DIAS_IT")
			.setExpression("");
			
			listaTipoDiasContrato.add(infoEmployee);
		}
		
		employeeInfoCalendar = new EmployeeCalendarData(listaHorasContrato, listaTipoDiasContrato);
		
		return employeeInfoCalendar;
	}

}
