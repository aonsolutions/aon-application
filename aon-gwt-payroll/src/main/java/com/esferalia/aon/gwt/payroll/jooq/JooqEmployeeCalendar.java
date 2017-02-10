package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
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
		ArrayList<java.util.Date> listaFestivosContrato = new ArrayList<java.util.Date>();
		ArrayList<Byte> listaNoLaborablesContrato = new ArrayList<Byte>();
		
		// ---------------------------------------------- HORAS SEMANALES ---------------------------------------------------------
		
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
		
		// ---------------------------------------------- TIPOS DIAS ---------------------------------------------------------
		
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
				.where(CONTRACT_LEAVE.CONTRACT.eq(contract))
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
		
		// -------------------------------------- DIAS NO LABRABLES / FESTIVOS ---------------------------------------------------------
		
		 Integer calendar = dslContext.select(CONTRACT.CALENDAR)
							 		  .from(CONTRACT)
							 		  .where(CONTRACT.ID.eq(contract))
							 		  .fetchOne()
							 		  .get(CONTRACT.CALENDAR);
		 
		Result<Record> diasNoLaborables = dslContext.select()
													.from(CALENDAR)
													.where(CALENDAR.ID.eq(calendar))
													.fetch();
		
		for(Record r: diasNoLaborables){
			listaNoLaborablesContrato.add(r.get(CALENDAR.MONDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.TUESDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.WEDNESDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.THURSDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.FRIDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.SATURDAY));
			listaNoLaborablesContrato.add(r.get(CALENDAR.SUNDAY));
		}
		
		Integer holiday = dslContext.select(CALENDAR.HOLIDAY)
									.from(CALENDAR)
									.where(CALENDAR.ID.eq(calendar))
									.fetchOne()
									.get(CALENDAR.HOLIDAY);
		
		ArrayList<Integer> holidays = new ArrayList<Integer>();
		
		while ( holiday != null ) {
			holidays.add(holiday);
			
			holiday = dslContext.select(HOLIDAY.HOLIDAY_)
								.from(HOLIDAY)
								.where(HOLIDAY.ID.eq(holiday))
								.fetchOne()
								.get(HOLIDAY.HOLIDAY_);
		}
		
		Result<Record> countryHolidays = dslContext.select()
													.from(HOLIDAY_DETAIL)
													.where(HOLIDAY_DETAIL.HOLIDAY.in(holidays))
													.fetch();
		
		for(Record r : countryHolidays)
			listaFestivosContrato.add(r.get(HOLIDAY_DETAIL.DATE));
		
		employeeInfoCalendar = new EmployeeCalendarData(listaHorasContrato, listaTipoDiasContrato, listaFestivosContrato, listaNoLaborablesContrato);
		
		return employeeInfoCalendar;
	}

}
