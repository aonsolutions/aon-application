package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;
import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEmployeeCalendar {

	private static Settings SETTINGS = null;

	public static EmployeeCalendarData getEmployeeHour(Connection conn, Integer contract) throws IllegalArgumentException {

		return getHoursByDay(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static void setEmployeeHour(Connection conn, Integer contract, EmployeeCalendarUpdate updateInfo){
		setHoursByDay(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
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
		Boolean jornadaCompleta = false;
		
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
							,ContextVariable.STRIKE_DAYS.getName()
							,ContextVariable.HOLIDAYS.getName()))
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
		
		Integer calendar = dslContext.select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
							  .from(CONTRACT)
							  .innerJoin(PAYROLL_WORKPLACE)
							  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
							  .where(CONTRACT.ID.eq(contract))
							  .fetchOne()
							  .get(CONTRACT.CALENDAR);
		
		if (calendar != null){
		 
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
		}
		
		// ------------------------------------------------------ JORNADA COMPLETA -------------------------------------------------------
		String tipoJornadaInfoEmpleado = dslContext
				  .select(CONTRACT_DATA.EXPRESSION)
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
				  .fetchOne().get(CONTRACT_DATA.EXPRESSION);
		
		jornadaCompleta = comprobarTipoJornada(tipoJornadaInfoEmpleado);
		
		employeeInfoCalendar = new EmployeeCalendarData(listaHorasContrato, listaTipoDiasContrato, listaFestivosContrato, 
				listaNoLaborablesContrato, jornadaCompleta);
		
		return employeeInfoCalendar;
	}
	
	private static Boolean comprobarTipoJornada(String tipoJornadaInfoEmpleado) {
		if ('1' == tipoJornadaInfoEmpleado.charAt(1) || '4' == tipoJornadaInfoEmpleado.charAt(1))
			return true;
		else
			return false;
	}

	private static void setHoursByDay(DSLContext dslContext, Integer contract, EmployeeCalendarUpdate updateInfo) {
		
		// -------------------------------------------------- ACTUALIZACION HORAS ------------------------------------------------------
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
						.from(CONTRACT)
						.where(CONTRACT.ID.eq(contract))
						.fetchOne().value1();
		
		dslContext.delete(CONTRACT_DATA)
				   .where(CONTRACT_DATA.CONTRACT.eq(contract))
				   .and(CONTRACT_DATA.NAME.in(
						  ContextVariable.MONDAY_HOURS.getName()
						  ,ContextVariable.TUESDAY_HOURS.getName()
						  ,ContextVariable.WEDNESDAY_HOURS.getName()
						  ,ContextVariable.THURSDAY_HOURS.getName()
						  ,ContextVariable.FRIDAY_HOURS.getName()
						  ,ContextVariable.SATURDAY_HOURS.getName()
						  ,ContextVariable.SUNDAY_HOURS.getName()))
				   .execute();
		
		HashMap<java.util.Date, Double> mapaHorasUpdate = updateInfo.getMapaHorasDias();
		
		java.util.Date startDate = new java.util.Date();
		java.util.Date endDate = new java.util.Date();
		
		for (Entry<java.util.Date, Double> entry : mapaHorasUpdate.entrySet()) {
			if (entry.getKey().before(startDate))
				startDate = DateUtils.copyDateOnly(entry.getKey());
			
			if (entry.getKey().after(endDate))
				endDate = DateUtils.copyDateOnly(entry.getKey());
		}
		
		
		for (int i=0; i<7; i++){
			java.util.Date  date = DateUtils.copyDateOnly(startDate);
			DateUtils.addDays2Date(date, i);

			@SuppressWarnings("deprecation")
			String diaSemana = calcularDiaSemana(date.getDay());
			
			java.util.Date auxStartDate = DateUtils.copyDateOnly(date);
			Double horasStart = mapaHorasUpdate.get(auxStartDate);
			
			while (date.before(endDate)){
				if(!horasStart.equals(mapaHorasUpdate.get(date))){
					Date sqlStartDate = new Date(auxStartDate.getTime());
					Date sqlEndDate = new Date(date.getTime());
					dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
							CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
							CONTRACT_DATA.END_DATE)
							.values(domain, diaSemana, contract, Double.toString(horasStart), 
									sqlStartDate, sqlEndDate).execute();
				
					horasStart = mapaHorasUpdate.get(date);
					auxStartDate = DateUtils.copyDateOnly(date);
				}
				
				DateUtils.addDays2Date(date, 7);
			}
			
			Date sqlStartDate = new Date(auxStartDate.getTime());
			Date sqlEndDate = new Date(endDate.getTime());
			dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
					CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
					CONTRACT_DATA.END_DATE)
					.values(domain, diaSemana, contract, Double.toString(horasStart), 
							sqlStartDate, sqlEndDate).execute();
			
		}
		
		// ----------------------------------------------- ACTUALIZACION TIPO DIAS -------------------------------------------------------
		HashMap<java.util.Date, DayType> mapaTiposUpdate = updateInfo.getMapaTipoDias();
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				  ContextVariable.ERE_DAYS.getName()
				  ,ContextVariable.STRIKE_DAYS.getName()
				  ,ContextVariable.HOLIDAYS.getName()))
		   .execute();
		
		java.util.Date startDateTipo = new java.util.Date();
		java.util.Date endDateTipo = new java.util.Date();
		
		for (Entry<java.util.Date, DayType> entry : mapaTiposUpdate.entrySet()) {
			if (entry.getKey().before(startDateTipo))
				startDateTipo = DateUtils.copyDateOnly(entry.getKey());
			
			if (entry.getKey().after(endDateTipo))
				endDateTipo = DateUtils.copyDateOnly(entry.getKey());
		}
		
		
		java.util.Date  dateTipo = DateUtils.copyDateOnly(startDateTipo);
		java.util.Date auxStartDateTipo = DateUtils.copyDateOnly(dateTipo);
		String tipoDia = "";
		DayType tipoStart = DayType.NOTYPEDAY;
		
		if (mapaTiposUpdate.get(dateTipo) != null){
			tipoDia = calcularDiaTipo(mapaTiposUpdate.get(dateTipo));
			tipoStart = mapaTiposUpdate.get(auxStartDateTipo);
		}
		
		while (dateTipo.before(endDateTipo)){
			if (mapaTiposUpdate.get(dateTipo) != null){
				if(!tipoStart.equals(mapaTiposUpdate.get(dateTipo))){
					if(tipoDiaValido(tipoDia)){
						Date sqlStartDate = new Date(auxStartDateTipo.getTime());
						java.util.Date javaEndDate = DateUtils.copyDateOnly(dateTipo);
						DateUtils.addDays2Date(javaEndDate, -1);
						Date sqlEndDate = new Date(javaEndDate.getTime());
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
								CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
								CONTRACT_DATA.END_DATE)
								.values(domain, tipoDia, contract, "", 
										sqlStartDate, sqlEndDate).execute();
					}
					tipoDia = calcularDiaTipo(mapaTiposUpdate.get(dateTipo));
					tipoStart = mapaTiposUpdate.get(dateTipo);
					auxStartDateTipo = DateUtils.copyDateOnly(dateTipo);
				}
			}
			
			DateUtils.addDays2Date(dateTipo, 1);
		}
		
	}
	
	private static boolean tipoDiaValido(String dayType) {
		return dayType.equals("DIAS_ERE") || dayType.equals("DIAS_HUELGA") 
				|| dayType.equals("DIAS_VACACIONES");
	}

	private static String calcularDiaTipo(DayType dayType) {
		String result = "";
		switch (dayType) {
		case EREDAY:
			result = "DIAS_ERE";
			break;
		case STRIKEDAY:
			result = "DIAS_HUELGA";
			break;
		case HOLIDAY:
			result = "DIAS_VACACIONES";
			break;
		case FREEDAY:
			result = "";
			break;
		default:
			result = "";
			break;
		}
		
		return result;
	}

	private static String calcularDiaSemana(int day) {
		String result = "";
		switch (day) {
		case 0:
			result = "HORAS_DOMINGO";
			break;
		case 1:
			result = "HORAS_LUNES";
			break;
		case 2:
			result = "HORAS_MARTES";
			break;
		case 3:
			result = "HORAS_MIERCOLES";
			break;
		case 4:
			result = "HORAS_JUEVES";
			break;
		case 5:
			result = "HORAS_VIERNES";
			break;
		default:
			result = "HORAS_SABADO";
			break;
		}
		
		return result;
	}

}
