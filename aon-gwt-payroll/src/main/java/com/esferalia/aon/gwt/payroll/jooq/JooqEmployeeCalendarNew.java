package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.CalendarDayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEmployeeCalendarNew {
	
	// ---------------------------------------------------------------------------------------------------
	//										SETTINGS
	// ---------------------------------------------------------------------------------------------------
	
	private static Settings SETTINGS = null;
	
	public static String resetEmployeeCalendar(Connection conn, Integer contractId) {
		return resetEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId);
	}

	public static EmployeeCalendarInfo getEmployeeCalendar (Connection conn, Integer contractId) throws IllegalArgumentException {
		return getEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	public static String setEmployeeCalendar (Connection conn, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo) throws IllegalArgumentException {
		return setEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId, employeeCalendarInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// ---------------------------------------------------------------------------------------------------
	//										MAIN METHODS
	// ---------------------------------------------------------------------------------------------------
	
	private static String resetEmployeeCalendarDB(DSLContext dslContext, Integer contractId) {
		
		ArrayList<String> varNames = new ArrayList<String>();
		
		// HOURS
		varNames.add(ContextVariable.MONDAY_HOURS.getName());
		varNames.add(ContextVariable.TUESDAY_HOURS.getName());
		varNames.add(ContextVariable.WEDNESDAY_HOURS.getName());
		varNames.add(ContextVariable.THURSDAY_HOURS.getName());
		varNames.add(ContextVariable.FRIDAY_HOURS.getName());
		varNames.add(ContextVariable.SATURDAY_HOURS.getName());
		varNames.add(ContextVariable.SUNDAY_HOURS.getName());
		
		// EXTRA HOURS
		varNames.add(ContextVariable.EXTRA_HOURS.getName());
		
		// COMPLEMENTARY HOURS
		varNames.add(ContextVariable.ADDITIONAL_HOURS.getName());
		
		// DAY TYPES
		varNames.add(ContextVariable.HOLIDAYS.getName());
		varNames.add("LABORABLE");
		varNames.add("NO_LABORABLE");
		
		// COEFFICIENT TYPES
		varNames.add(ContextVariable.ERE_FACTOR.getName());
		varNames.add(ContextVariable.STRIKE_FACTOR.getName());
		varNames.add("COEFICIENTE_ERE_FZA");
		varNames.add("COEFICIENTE_ERE_FZA_EXONERADO");
		varNames.add("COEFICIENTE_AUSENCIA");
		varNames.add("COEFICIENTE_PARCIALIDAD");
		varNames.add("CAUSA_INACTIVIDAD");
		
		// PEONADAS
		varNames.add("PEONADAS");
		varNames.add("JORNADAS_REALES");
		
		// LABORABLES
		varNames.add("LABORABLE_DOMINGO");
		varNames.add("LABORABLE_LUNES");
		varNames.add("LABORABLE_MARTES");
		varNames.add("LABORABLE_MIERCOLES");
		varNames.add("LABORABLE_JUEVES");
		varNames.add("LABORABLE_VIERNES");
		varNames.add("LABORABLE_SABADO");
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(varNames))
			.execute();
		
		return "El calendario ha sido reseteado.";
	}
	
	// Get employee calendar info from database
	private static EmployeeCalendarInfo getEmployeeCalendarDB(DSLContext dslContext, Integer contract) {
		
		// ----------------------------------- VARIABLES
		Boolean fullTimeJourney = false;
		Boolean agrarianContract = false;
		
		HashMap<java.util.Date, String> monthExtraHoursMap = new HashMap<java.util.Date, String>();
		
		CalendarHours calendarHours = new CalendarHours();
		
		Byte[] nonWorkingDays = new Byte[7];
		
		HashMap<java.util.Date, String> festiveDaysMap = new HashMap<java.util.Date, String>();
		
		CalendarDaysType calendarDaysType = new CalendarDaysType();
		
		CalendarDaysType partialityDaysType = new CalendarDaysType();
		
		Date contractStartDate = null;
		Date contractEndDate = null;
		
		// ----------------------------------- CONTRACT START AND END DATE
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		contractStartDate = contractRecord.get(CONTRACT.START_DATE);
		contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		// ----------------------------------- FULL TIME JOURNEY
		
		String journeyTypeEmployee = "";
		
		Result<Record> journeyTypeRecords = dslContext.select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.equal("TIEMPO_COMPLETO"))
				  .orderBy(CONTRACT_DATA.START_DATE.desc()) // If there is more than one contract
				  .fetch();
		
		if(!journeyTypeRecords.isEmpty())
			journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
		else {
			journeyTypeRecords = dslContext.select()
					  .from(CONTRACT_DATA)
					  .where(CONTRACT_DATA.CONTRACT.eq(contract))
					  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
					  .orderBy(CONTRACT_DATA.START_DATE.desc())
					  .fetch();
			
			journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
		}
		
		fullTimeJourney = isFullTimeJourney(journeyTypeEmployee);
		
		// ----------------------------------- CHECK EMPLOYEE IS AGRARIAN
		
		Integer contractCCCType = -1;
		
		Integer contratEnterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		
		if(null != contratEnterpriseCCCId) {
			Record enterpriseCCCRecord = dslContext.select()
					.from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.eq(contratEnterpriseCCCId))
					.fetchOne();
		
			contractCCCType = (int) enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
		}
		
		agrarianContract = isAgrarianContract(contractCCCType);
		
		// ----------------------------------- WEEK HOURS
		
		if(!fullTimeJourney) {
			Result<Record> contractHoursRecords = dslContext.select()
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
			
			for(Record r: contractHoursRecords){
				
				java.util.Date startDate = parseDateSqlToUtil(r.get(CONTRACT_DATA.START_DATE));
				java.util.Date endDate = parseDateSqlToUtil(r.get(CONTRACT_DATA.END_DATE));
				Double value = parseStringToDouble(r.get(CONTRACT_DATA.EXPRESSION));
				
				DayHour newDayHour = new DayHour(startDate, endDate, value);
				
				String dayName = r.get(CONTRACT_DATA.NAME);
				
				switch (dayName) {
					case "HORAS_LUNES":
						calendarHours.getDayHours()[0].addDayHour(newDayHour);
						break;
					case "HORAS_MARTES":
						calendarHours.getDayHours()[1].addDayHour(newDayHour);
						break;
					case "HORAS_MIERCOLES":
						calendarHours.getDayHours()[2].addDayHour(newDayHour);
						break;
					case "HORAS_JUEVES":
						calendarHours.getDayHours()[3].addDayHour(newDayHour);
						break;
					case "HORAS_VIERNES":
						calendarHours.getDayHours()[4].addDayHour(newDayHour);
						break;
					case "HORAS_SABADO":
						calendarHours.getDayHours()[5].addDayHour(newDayHour);
						break;
					case "HORAS_DOMINGO":
						calendarHours.getDayHours()[6].addDayHour(newDayHour);
						break;
					default:
						break;
				}
			}
		}
		
		// ----------------------------------- MONTH EXTRA HOURS
		
		Result<Record> monthExtraHoursRecords = null;
		
		if(fullTimeJourney) {
			monthExtraHoursRecords = dslContext.select()
					  .from(CONTRACT_DATA)
					  .where(CONTRACT_DATA.CONTRACT.eq(contract))
					  .and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
					  .fetch();
		} else {
			monthExtraHoursRecords = dslContext.select()
					  .from(CONTRACT_DATA)
					  .where(CONTRACT_DATA.CONTRACT.eq(contract))
					  .and(CONTRACT_DATA.NAME.eq(ContextVariable.ADDITIONAL_HOURS.getName()))
					  .fetch();
		}
		
		for(Record r: monthExtraHoursRecords){
			java.util.Date startDate = parseDateSqlToUtil(r.get(CONTRACT_DATA.START_DATE));
			
			monthExtraHoursMap.put(startDate, r.get(CONTRACT_DATA.EXPRESSION));
		}
		
		// ----------------------------------- FESTIVE DAYS AND HOURS BY CALENDAR
		
		Integer calendarId = dslContext.select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contract))
				  .fetchOne()
				  .get(CONTRACT.CALENDAR);

		if (calendarId != null){
			
			if(fullTimeJourney){
				// Si vale 0 es laborable y si vale 1 es no laborables
				Result<Record> nonWorkingDaysRecords = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(calendarId))
						.and(CONTRACT_DATA.NAME.in(
								"LABORABLE_LUNES",
								"LABORABLE_MARTES",
								"LABORABLE_MIERCOLES",
								"LABORABLE_JUEVES",
								"LABORABLE_VIERNES",
								"LABORABLE_SABADO",
								"LABORABLE_DOMINGO"
						))
						.fetch();
				
				if(nonWorkingDaysRecords.isEmpty()) {
					Result<Record> calendarRecords = dslContext.select()
							.from(CALENDAR)
							.where(CALENDAR.ID.eq(calendarId))
							.fetch();
					
					for(Record r: calendarRecords){
						nonWorkingDays[0] = r.get(CALENDAR.SUNDAY);
						nonWorkingDays[1] = r.get(CALENDAR.MONDAY);
						nonWorkingDays[2] = r.get(CALENDAR.TUESDAY);
						nonWorkingDays[3] = r.get(CALENDAR.WEDNESDAY);
						nonWorkingDays[4] = r.get(CALENDAR.THURSDAY);
						nonWorkingDays[5] = r.get(CALENDAR.FRIDAY);
						nonWorkingDays[6] = r.get(CALENDAR.SATURDAY);
					}
				} else {
					for(Record nonWorkingDaysRecord : nonWorkingDaysRecords) {
						String name = nonWorkingDaysRecord.get(CONTRACT_DATA.NAME);
						switch (name) {
							case "LABORABLE_DOMINGO":
								nonWorkingDays[0] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_LUNES":
								nonWorkingDays[1] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_MARTES":
								nonWorkingDays[2] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_MIERCOLES":
								nonWorkingDays[3] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_JUEVES":
								nonWorkingDays[4] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_VIERNES":
								nonWorkingDays[5] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							case "LABORABLE_SABADO":
								nonWorkingDays[6] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
								break;
							default:
								break;
						}
					}
				}	
			}
			
			Integer holidayId = dslContext.select(CALENDAR.HOLIDAY)
					.from(CALENDAR)
					.where(CALENDAR.ID.eq(calendarId))
					.fetchOne()
					.get(CALENDAR.HOLIDAY);
			
			ArrayList<Integer> holidays = new ArrayList<Integer>();
			
			// Get all holidays ids
			while ( holidayId != null ) {
				holidays.add(holidayId);
				
				holidayId = dslContext.select(HOLIDAY.HOLIDAY_)
						.from(HOLIDAY)
						.where(HOLIDAY.ID.eq(holidayId))
						.fetchOne()
						.get(HOLIDAY.HOLIDAY_);
			}
			
			Result<Record> holidaysRecords = dslContext.select()
					.from(HOLIDAY_DETAIL)
					.where(HOLIDAY_DETAIL.HOLIDAY.in(holidays))
					.fetch();
			
			for(Record r : holidaysRecords) {
				CalendarDayType calendarDayType = new CalendarDayType();
				
				calendarDayType.setStartDate(parseDateSqlToUtil(r.get(HOLIDAY_DETAIL.DATE)));
				calendarDayType.setEndDate(parseDateSqlToUtil(r.get(HOLIDAY_DETAIL.DATE)));
				calendarDayType.setDayType(DayType.FREEDAY);
				calendarDayType.setExpession(r.get(HOLIDAY_DETAIL.DESCRIPTION));
				
				calendarDaysType.addDayType(calendarDayType);
				
				festiveDaysMap.put(r.get(HOLIDAY_DETAIL.DATE), r.get(HOLIDAY_DETAIL.DESCRIPTION));
			}
		
		} else if(fullTimeJourney){
			// Si vale 0 es laborable y si vale 1 es no laborables
			Result<Record> nonWorkingDaysRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.and(CONTRACT_DATA.NAME.in(
							"LABORABLE_LUNES",
							"LABORABLE_MARTES",
							"LABORABLE_MIERCOLES",
							"LABORABLE_JUEVES",
							"LABORABLE_VIERNES",
							"LABORABLE_SABADO",
							"LABORABLE_DOMINGO"
					))
					.fetch();
			
			if(nonWorkingDaysRecords.isEmpty()) {
				nonWorkingDays[0] = 1;
				nonWorkingDays[1] = 0;
				nonWorkingDays[2] = 0;
				nonWorkingDays[3] = 0;
				nonWorkingDays[4] = 0;
				nonWorkingDays[5] = 0;
				nonWorkingDays[6] = 1;
			} else {
				for(Record nonWorkingDaysRecord : nonWorkingDaysRecords) {
					String name = nonWorkingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "LABORABLE_DOMINGO":
							nonWorkingDays[0] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_LUNES":
							nonWorkingDays[1] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MARTES":
							nonWorkingDays[2] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MIERCOLES":
							nonWorkingDays[3] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_JUEVES":
							nonWorkingDays[4] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_VIERNES":
							nonWorkingDays[5] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_SABADO":
							nonWorkingDays[6] = Byte.parseByte(nonWorkingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}
		}
		
		// ----------------------------------- NON WORKING DAYS BY EXISTIN HOURS
		
		if(!fullTimeJourney){
			
			if(calendarHours.isEmpty()){
				nonWorkingDays[0] = 0;
				nonWorkingDays[1] = 0;
				nonWorkingDays[2] = 0;
				nonWorkingDays[3] = 0;
				nonWorkingDays[4] = 0;
				nonWorkingDays[5] = 0;
				nonWorkingDays[6] = 0;
			} else {
			 
				String listDaysOfWeek [] = {
						"HORAS_LUNES", 
						"HORAS_MARTES", 
						"HORAS_MIERCOLES", 
						"HORAS_JUEVES", 
						"HORAS_VIERNES",
						"HORAS_SABADO",
						"HORAS_DOMINGO"
				}; 
				
				Result<Record> contractHoursRecords = dslContext.select()
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
		
				ArrayList<String> listDefinedDays = new ArrayList<String>();
				for(Record r: contractHoursRecords){
					listDefinedDays.add(r.get(CONTRACT_DATA.NAME));
				}
				
				for (int i=0; i<7; i++){
					String dayOfWeek = listDaysOfWeek[i];
					if (!listDefinedDays.contains(dayOfWeek))
						nonWorkingDays[i] = 1; // Non working day
					else {
						ArrayList<String> values = getValuesDayOfWeek(contractHoursRecords, dayOfWeek);
						if(values.isEmpty() || values.get(0) == null)
							nonWorkingDays[i] = 1; // Non working day
						else
							nonWorkingDays[i] = 0; // Working day
					}
				}
			}
		}
		
		// ----------------------------------- DAYS TYPE
		
		Result<Record> daysTypeRecords = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in(
						ContextVariable.HOLIDAYS.getName()
						,"NO_LABORABLE"
						,"PEONADAS"
						,"LABORABLE"
						//Coeficientes
						,ContextVariable.ERE_FACTOR.getName()
						,ContextVariable.STRIKE_FACTOR.getName()
						,"COEFICIENTE_ERE_FZA"
						,"COEFICIENTE_ERE_FZA_EXONERADO"
						,"COEFICIENTE_AUSENCIA"
						,"CAUSA_INACTIVIDAD"))
				.fetch();
		
		for(Record r: daysTypeRecords){
			CalendarDayType calendarDayType = new CalendarDayType();
			
			calendarDayType.setStartDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.START_DATE)));
			calendarDayType.setEndDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.END_DATE)));
			calendarDayType.setDayType(parseStringToDayType(r.get(CONTRACT_DATA.NAME)));
			calendarDayType.setExpession(r.get(CONTRACT_DATA.EXPRESSION));
			
			calendarDaysType.addDayType(calendarDayType);
		}
		
		// ----------------------------------- DAYS PARTIALITY
		
		// Se analiza por separado por que puede venir del borrrador para todo el contrato y no habría que modificarlo y se mete con el resto
		// de dias se corre el riesgo de que se sobreescriba
		
		Result<Record> daysPartialityRecords = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in("COEFICIENTE_PARCIALIDAD"))
				.fetch();
		
		for(Record r: daysPartialityRecords){
			CalendarDayType calendarDayType = new CalendarDayType();
			
			calendarDayType.setStartDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.START_DATE)));
			calendarDayType.setEndDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.END_DATE)));
			calendarDayType.setDayType(parseStringToDayType(r.get(CONTRACT_DATA.NAME)));
			calendarDayType.setExpession(r.get(CONTRACT_DATA.EXPRESSION));
			
			partialityDaysType.addDayType(calendarDayType);
		}
		
		// ----------------------------------- DAYS IT
		
		Result<Record> daysITRecords = dslContext
				.select()
				.from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(contract))
				.fetch();
		
		for(Record r: daysITRecords){
			CalendarDayType calendarDayType = new CalendarDayType();
			
			calendarDayType.setStartDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.START_DATE)));
			calendarDayType.setEndDate(parseDateSqlToUtil(r.get(CONTRACT_DATA.END_DATE)));
			calendarDayType.setDayType(parseStringToDayType("DIAS_IT"));
			calendarDayType.setExpession("");
			
			calendarDaysType.addDayType(calendarDayType);
		}
		
		// ----------------------------------- CREATE AND INIT EMPLOYEE CALENDAR INFO
		
		EmployeeCalendarInfo employeeCalendarInfo = new EmployeeCalendarInfo();
		
		calendarHours.initMapDaysHour();
		calendarDaysType.initMapDaysDayType();
		partialityDaysType.initMapDaysDayType();
		
		// Add contract dates
		
		calendarHours.setContractStartDate(contractStartDate);
		calendarHours.setContractEndDate(contractEndDate);
		
		calendarDaysType.setContractStartDate(contractStartDate);
		calendarDaysType.setContractEndDate(contractEndDate);
		
		partialityDaysType.setContractStartDate(contractStartDate);
		partialityDaysType.setContractEndDate(contractEndDate);
		
		employeeCalendarInfo
			.setFullTimeJourney(fullTimeJourney)
			.setAgrarianContract(agrarianContract)
			.setCalendarHours(calendarHours)
			.setMonthExtraHoursMap(monthExtraHoursMap)
			.setNonWorkingDays(nonWorkingDays)
			.setFestiveDaysMap(festiveDaysMap)
			.setCalendarDaysType(calendarDaysType)
			.setPartialityDaysType(partialityDaysType);
		
		return employeeCalendarInfo;
		
	}
	
	// Set employee calendar info to database
	private static String setEmployeeCalendarDB(DSLContext dslContext, Integer contract, EmployeeCalendarInfo employeeCalendarInfo) {
			
		// ----------------------------------- VARIABLES
		Boolean fullTimeJourney = employeeCalendarInfo.getFullTimeJourney();
		
		HashMap<java.util.Date, String> monthExtraHoursMap = employeeCalendarInfo.getMonthExtraHoursMap();
		
		CalendarHours calendarHours = employeeCalendarInfo.getCalendarHours();
		
		Byte[] nonWorkingDays = employeeCalendarInfo.getNonWorkingDays();
		
		CalendarDaysType calendarDaysType = employeeCalendarInfo.getCalendarDaysType();
		
		CalendarDaysType partialityDaysType = employeeCalendarInfo.getPartialityDaysType();
		
		// ----------------------------------- CONTRACT INFO
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		Integer domain = contractRecord.get(CONTRACT.DOMAIN);
		
		Date contractStartDate = contractRecord.get(CONTRACT.START_DATE);
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		// ----------------------------------- MONTH EXTRA HOURS
		
		// Borramos las horas extras existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.in(
					ContextVariable.EXTRA_HOURS.getName(),
					ContextVariable.ADDITIONAL_HOURS.getName()))
			.execute();
		
		for(Entry<java.util.Date, String> entry : monthExtraHoursMap.entrySet()) {
			String expression = entry.getValue();
			
			if(null != expression && !StringUtils.isEmpty(expression)) {
				Date startDate = parseDateUtilToSql(entry.getKey());
				Date endDate = parseDateUtilToSql(DateUtils.getLastDayOfMonth(entry.getKey()));
				
				if(fullTimeJourney)
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domain)
						.set(CONTRACT_DATA.CONTRACT, contract)
						.set(CONTRACT_DATA.NAME, ContextVariable.EXTRA_HOURS.getName())
						.set(CONTRACT_DATA.EXPRESSION, expression)
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
				else
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domain)
						.set(CONTRACT_DATA.CONTRACT, contract)
						.set(CONTRACT_DATA.NAME, ContextVariable.ADDITIONAL_HOURS.getName())
						.set(CONTRACT_DATA.EXPRESSION, expression)
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
			}
			
		}
		
		// ----------------------------------- MODIFY NON WORKING DAYS
		
		// Borramos los existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.in(
					"LABORABLE_LUNES",
					"LABORABLE_MARTES",
					"LABORABLE_MIERCOLES",
					"LABORABLE_JUEVES",
					"LABORABLE_VIERNES",
					"LABORABLE_SABADO",
					"LABORABLE_DOMINGO"))
			.execute();
		
		if(fullTimeJourney) {
			for(int day=0; day<7; day++) {
				String expression = Byte.toString(nonWorkingDays[day]);
				
				String name = "";
				switch (day) {
					case 0:
						name = "LABORABLE_DOMINGO";
						break;
					case 1:
						name = "LABORABLE_LUNES";
						break;
					case 2:
						name = "LABORABLE_MARTES";
						break;
					case 3:
						name = "LABORABLE_MIERCOLES";
						break;
					case 4:
						name = "LABORABLE_JUEVES";
						break;
					case 5:
						name = "LABORABLE_VIERNES";
						break;
					case 6:
						name = "LABORABLE_SABADO";
						break;
					default:
						break;
				}
				
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.CONTRACT, contract)
					.set(CONTRACT_DATA.NAME, name)
					.set(CONTRACT_DATA.EXPRESSION, expression)
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			}
		}
		
		
		// ----------------------------------- WEEK HOURS
		
		// Eliminamos las horas existentes
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.in(
				ContextVariable.MONDAY_HOURS.getName(),
				ContextVariable.TUESDAY_HOURS.getName(),
				ContextVariable.WEDNESDAY_HOURS.getName(),
				ContextVariable.THURSDAY_HOURS.getName(),
				ContextVariable.FRIDAY_HOURS.getName(),
				ContextVariable.SATURDAY_HOURS.getName(),
				ContextVariable.SUNDAY_HOURS.getName()
			)).execute();
		
		for(int day = 0; day < 7; day++) {
			ArrayList<DayHour> dayHours = calendarHours.getDayHours()[day].getFixUpdateList();
			
			String name = "";
			switch (day) {
				case 0:
					name = ContextVariable.SUNDAY_HOURS.getName();
					break;
				case 1:
					name = ContextVariable.MONDAY_HOURS.getName();
					break;
				case 2:
					name = ContextVariable.TUESDAY_HOURS.getName();
					break;
				case 3:
					name = ContextVariable.WEDNESDAY_HOURS.getName();
					break;
				case 4:
					name = ContextVariable.THURSDAY_HOURS.getName();
					break;
				case 5:
					name = ContextVariable.FRIDAY_HOURS.getName();
					break;
				case 6:
					name = ContextVariable.SATURDAY_HOURS.getName();
					break;
				default:
					break;
			}
			
			for(DayHour dayHour : dayHours) {
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.CONTRACT, contract)
					.set(CONTRACT_DATA.NAME, name)
					.set(CONTRACT_DATA.EXPRESSION, null == dayHour.getValue() ? null : Double.toString(dayHour.getValue()))
					.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(dayHour.getStartDate()))
					.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(dayHour.getEndDate()))
					.execute();
			}
		}
		
		// ----------------------------------- DAYS TYPE
		
		// Eliminamos los tipos de dias existentes
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.in(
					"DIAS_FESTIVOS",
					ContextVariable.HOLIDAYS.getName()
					,"NO_LABORABLE"
					,"PEONADAS"
					,ContextVariable.ERE_FACTOR.getName()
					,ContextVariable.STRIKE_FACTOR.getName()
					,"COEFICIENTE_ERE_FZA"
					,"COEFICIENTE_ERE_FZA_EXONERADO"
					,"COEFICIENTE_AUSENCIA"
					,"CAUSA_INACTIVIDAD"
					,"LABORABLE"
			)).execute();
		
		ArrayList<CalendarDayType> dayTypeFixList = calendarDaysType.getFixUpdateList();
		
		for(CalendarDayType calendarDayType : dayTypeFixList) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, getNameByDayType(calendarDayType.getDayType()))
				.set(CONTRACT_DATA.EXPRESSION, calendarDayType.getExpession())
				.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(calendarDayType.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(calendarDayType.getEndDate()))
				.execute();
		}
		
		// ----------------------------------- DAYS PARTIALITY
		
		//  Eliminamos los tipos de dias existentes
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD"))
			.execute();
		
		ArrayList<CalendarDayType> partialityDayTypeFixList = partialityDaysType.getFixUpdateList();
		
		for(CalendarDayType partialityDayType : partialityDayTypeFixList) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, "COEFICIENTE_PARCIALIDAD")
				.set(CONTRACT_DATA.EXPRESSION, partialityDayType.getExpession())
				.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(partialityDayType.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(partialityDayType.getEndDate()))
				.execute();
		}
		
		return "Cambios guardados correctamente";
		
	}
	
	

	// ---------------------------------------------------------------------------------------------------
	//										VARIABLES DAYS TYPES
	// ---------------------------------------------------------------------------------------------------

	private static final Map<DayType, String> NAME_TYPE_OF_DAY  = new HashMap<DayType, String>(){
		
		private static final long serialVersionUID = 1L;

		{
			put(DayType.BAJAIT, "DIAS_IT");
			put(DayType.HOLIDAY, "DIAS_VACACIONES");
			put(DayType.STRIKEDAY, "COEFICIENTE_HUELGA");
			put(DayType.EREDAY, "COEFICIENTE_ERE");
			put(DayType.INACTIVITY, "CAUSA_INACTIVIDAD");
			put(DayType.PEONADAS, "PEONADAS");
			put(DayType.NOWORKINGDAY, "NO_LABORABLE");
			put(DayType.DROPDAY, "COEFICIENTE_AUSENCIA");
			put(DayType.PARTIALITY, "COEFICIENTE_PARCIALIDAD");
			put(DayType.EREFZADAY, "COEFICIENTE_ERE_FZA");
			put(DayType.EREFZAEXONDAY, "COEFICIENTE_ERE_FZA_EXONERADO");
			put(DayType.FREEDAY, "DIAS_FESTIVOS");
			put(DayType.WORKINGDAY, "LABORABLE");
		}
	};
	
	private static final Map<String, DayType> TYPE_OF_DAY  = new HashMap<String, DayType>(){
		
		private static final long serialVersionUID = 1L;

		{
			put("DIAS_IT", DayType.BAJAIT);
			put("DIAS_VACACIONES", DayType.HOLIDAY);
			put("COEFICIENTE_HUELGA", DayType.STRIKEDAY);
			put("COEFICIENTE_ERE", DayType.EREDAY);
			put("CAUSA_INACTIVIDAD", DayType.INACTIVITY);
			put("PEONADAS", DayType.PEONADAS);
			put("NO_LABORABLE", DayType.NOWORKINGDAY);
			put("COEFICIENTE_AUSENCIA", DayType.DROPDAY);
			put("COEFICIENTE_PARCIALIDAD", DayType.PARTIALITY);
			put("COEFICIENTE_ERE_FZA", DayType.EREFZADAY);
			put("COEFICIENTE_ERE_FZA_EXONERADO", DayType.EREFZAEXONDAY);
			put("DIAS_FESTIVOS", DayType.FREEDAY);
			put("LABORABLE", DayType.WORKINGDAY);
		}
	};
	// ---------------------------------------------------------------------------------------------------
	//										AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------------

	// Get employee calendar info from database
	private static Boolean isFullTimeJourney(String journeyType) {
		return ('1' == journeyType.charAt(1) || '4' == journeyType.charAt(1)|| "true" == journeyType) ? true : false;
	}
	
	private static Boolean isAgrarianContract(Integer contractCCCType) {
		return (contractCCCType == 7) ? true : false;
	}
	
	private static ArrayList<String> getValuesDayOfWeek(Result<Record> contractHoursRecords, String dayOfWeek) {
		ArrayList<String> values = new ArrayList<String>();
		
		for(Record r: contractHoursRecords){
			if(dayOfWeek.equals(r.get(CONTRACT_DATA.NAME))){
				values.add(r.get(CONTRACT_DATA.EXPRESSION));
			}
		}
		
		return values;
	}
	
	private static String parseCoefficientName(String type) {
		switch (type) {
			case "COEFICIENTE_ERE":
				return "DIAS_ERE";
			case "COEFICIENTE_ERE_FZA":
				return "DIAS_ERE_FZA";
			case "COEFICIENTE_ERE_FZA_EXONERADO":
				return "DIAS_ERE_FZA_EXONERADO";
			case "COEFICIENTE_HUELGA":
				return "DIAS_HUELGA";
			case "COEFICIENTE_AUSENCIA":
				return "DIAS_AUSENCIA";
			case "COEFICIENTE_PARCIALIDAD":
				return "DIAS_PARCIALIDAD";
			case "CAUSA_INACTIVIDAD":
				return "DIAS_INACTIVIDAD";
			default:
				return "CUIDADO";
		}
	}
	
	private static java.util.Date parseDateSqlToUtil(Date date) {
		if(null == date)
			return null;
		
		java.util.Date javaDate = new java.util.Date(date.getTime());
		DateUtils.resetTime(javaDate);
		
		return javaDate;
	}
	
	private static Date parseDateUtilToSql(java.util.Date date) {
		if(null == date)
			return null;
		
		DateUtils.resetTime(date);
		
		return new Date(date.getTime());
	}
	
	private static Double parseStringToDouble(String value) {
		if(null == value)
			return null;
		
		Double result = 0.00;
		try {
			result = Double.parseDouble(value);
		}catch (NumberFormatException e) {
			result = 0.00;
		}
		
		return result;
	}
	
	private static DayType parseStringToDayType(String dayTypeName) {
		return TYPE_OF_DAY.get(dayTypeName);
	}
	
	private static String getNameByDayType(DayType dayType) {
		return NAME_TYPE_OF_DAY.get(dayType);
	}
}
