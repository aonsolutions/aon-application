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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.CalendarDayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarExtraHours;
import com.esferalia.aon.gwt.payroll.shared.CalendarExtraHours.DayHourExtra;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeeCalendarNew {
	
	// ------------------------------------------------ Constructor
	
	private JooqEmployeeCalendarNew() {
		super();
	}
	
	// ------------------------------------------------ Variables (Settings)
	
	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ------------------------------------------------ Reset Calendar
	
	public static void resetEmployeeCalendar(Connection conn, Integer contractId) {
		resetEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	private static void resetEmployeeCalendarDB(DSLContext dslContext, Integer contractId) {
		
		ArrayList<String> varNames = new ArrayList<>();
		
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
		varNames.add("DIAS_EFECTIVOS");
		
		// COEFFICIENT TYPES
		varNames.add(ContextVariable.ERE_FACTOR.getName());
		varNames.add(ContextVariable.STRIKE_FACTOR.getName());
		varNames.add("COEFICIENTE_ERE_FZA");
		varNames.add("COEFICIENTE_ERE_FZA_EXONERADO");
		varNames.add("COEF_ERE_FZA_EXON_PARCIAL");
		varNames.add("FIN_ERE_FZA_EXONERADO");
		varNames.add("COEFICIENTE_AUSENCIA");
		varNames.add("COEFICIENTE_PARCIALIDAD");
		varNames.add("CAUSA_INACTIVIDAD");
		varNames.add("DIAS_INACTIVIDAD");
		
		// JORNADAS
		varNames.add("JORNADAS_REALES");
		varNames.add("JORNADAS_TEORICAS");
		
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
	}

	// ------------------------------------------------ Get Calendar
	
	public static EmployeeCalendarInfo getEmployeeCalendar (Connection conn, Integer contractId) throws IllegalArgumentException {
		return getEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	private static EmployeeCalendarInfo getEmployeeCalendarDB(DSLContext dslContext, Integer contractId) {
		
		// Contract Dates
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date contractStartDate = contractRecord.get(CONTRACT.START_DATE);
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		// isFullTime
		
		Boolean isFullTime = getFullTimeJourney(dslContext, contractId);
		
		// isAgrarian
		
		Boolean isAgrarian = getAgrarianJourney(dslContext, contractRecord.get(CONTRACT.ENTERPRISE_CCC));
		
		// Calendar Hours if not isFullTime
		
		CalendarHours calendarHours = getCalendarHours(dslContext, contractId, isFullTime);
		
		// Calendar Extra Hours if isFullTime
		
		CalendarExtraHours extraHours = getExtraHours(dslContext, contractId, isFullTime);
		
		// Calendar Complementary Hours if not isFullTime
		
		Map<java.util.Date, Double> complementaryHours = getComplementaryHours(dslContext, contractId, isFullTime);
		
		// Calendar Working Days
		
		Byte[] workingDays = getWorkingDays(dslContext, contractId);
		
		// Contract Working Days if not isFullTime
		
		getWorkingDayByContractHour(dslContext, contractId, isFullTime, workingDays);
		
		// Contract Festives and DaysType
		
		Map<java.util.Date, String> festiveDays = new TreeMap<>();
		
		CalendarDaysType calendarDaysType = new CalendarDaysType();
		
		getFestivesAndDaysType(dslContext, contractId, festiveDays, calendarDaysType);
		
		getDaysType(dslContext, contractId, calendarDaysType);
		
		// Contract Patiality DaysType
		
		CalendarDaysType partialityDaysType = getPartialityDaysType(dslContext, contractId);
		
		// Contract IT DaysType
		
		getITDaysType(dslContext, contractId, calendarDaysType);
		
		// EmployeeCalendarInfo
		
		EmployeeCalendarInfo employeeCalendarInfo = new EmployeeCalendarInfo();
		
		employeeCalendarInfo.setContractStartDate(contractStartDate);
		employeeCalendarInfo.setContractEndDate(contractEndDate);
		
		calendarHours.initMapDaysHour();
		extraHours.initExtraHoursMap();
		calendarDaysType.initMapDaysDayType();
		partialityDaysType.initMapDaysDayType();
		
		// Add contract dates
		
		calendarHours.setContractStartDate(contractStartDate);
		calendarHours.setContractEndDate(contractEndDate);
		
		extraHours.setContractStartDate(contractStartDate);
		extraHours.setContractEndDate(contractEndDate);
		
		calendarDaysType.setContractStartDate(contractStartDate);
		calendarDaysType.setContractEndDate(contractEndDate);
		
		partialityDaysType.setContractStartDate(contractStartDate);
		partialityDaysType.setContractEndDate(contractEndDate);
		
		employeeCalendarInfo
			.setFullTime(isFullTime)
			.setAgrarian(isAgrarian)
			.setCalendarHours(calendarHours)
			.setCalendarExtraHours(extraHours)
			.setCalendarComplementaryHours(complementaryHours)
			.setWorkingDays(workingDays)
			.setFestiveDays(festiveDays)
			.setCalendarDaysType(calendarDaysType)
			.setPartialityDaysType(partialityDaysType);
		
		return employeeCalendarInfo;
		
	}

	private static Boolean getFullTimeJourney(DSLContext dslContext, Integer contractId) {
		String journeyTypeEmployee = dslContext.select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
				  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
				  .orderBy(CONTRACT_DATA.START_DATE.desc())
				  .fetchStreamInto(CONTRACT_DATA)
				  .map( data -> data.getExpression())
				  .filter(tc2 -> AonStringUtils.isNotBlank(tc2))
				  .findFirst()
				  .orElse("true");
		
		return isFullTimeJourney(journeyTypeEmployee);
	}
	
	private static Boolean getAgrarianJourney(DSLContext dslContext, Integer cccId) {
		Integer cccType = null;
		
		if(null != cccId)
			cccType = dslContext.select(ENTERPRISE_CCC.TYPE)
					.from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.eq(cccId))
					.fetchOne(ENTERPRISE_CCC.TYPE).intValue();
		
		return isAgrarianJourney(cccType);
	}
	
	
	private static CalendarHours getCalendarHours(DSLContext dslContext, Integer contractId, Boolean isFullTime) {
		
		CalendarHours calendarHours = new CalendarHours();
		
		if(Boolean.FALSE.equals(isFullTime)) {
			Result<Record> contractHoursRecords = dslContext.select()
					  .from(CONTRACT_DATA)
					  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
					  .and(CONTRACT_DATA.NAME.in(
							  ContextVariable.MONDAY_HOURS.getName()
							  ,ContextVariable.TUESDAY_HOURS.getName()
							  ,ContextVariable.WEDNESDAY_HOURS.getName()
							  ,ContextVariable.THURSDAY_HOURS.getName()
							  ,ContextVariable.FRIDAY_HOURS.getName()
							  ,ContextVariable.SATURDAY_HOURS.getName()
							  ,ContextVariable.SUNDAY_HOURS.getName()))
					  .fetch();
			
			for(Record contractHoursRecord: contractHoursRecords){
				
				java.util.Date startDate = parseDateSqlToUtil(contractHoursRecord.get(CONTRACT_DATA.START_DATE));
				java.util.Date endDate = parseDateSqlToUtil(contractHoursRecord.get(CONTRACT_DATA.END_DATE));
				
				String dayName = contractHoursRecord.get(CONTRACT_DATA.NAME);
				Double value = parseStringToDouble(contractHoursRecord.get(CONTRACT_DATA.EXPRESSION));
				
				DayHour newDayHour = new DayHour(startDate, endDate, value);
				
				switch (dayName) {
					case "HORAS_LUNES":
						calendarHours.getDayHours()[1].addDayHour(newDayHour);
						break;
					case "HORAS_MARTES":
						calendarHours.getDayHours()[2].addDayHour(newDayHour);
						break;
					case "HORAS_MIERCOLES":
						calendarHours.getDayHours()[3].addDayHour(newDayHour);
						break;
					case "HORAS_JUEVES":
						calendarHours.getDayHours()[4].addDayHour(newDayHour);
						break;
					case "HORAS_VIERNES":
						calendarHours.getDayHours()[5].addDayHour(newDayHour);
						break;
					case "HORAS_SABADO":
						calendarHours.getDayHours()[6].addDayHour(newDayHour);
						break;
					case "HORAS_DOMINGO":
						calendarHours.getDayHours()[0].addDayHour(newDayHour);
						break;
					default:
						break;
				}
			}
		}
		
		return calendarHours;
	}

	
	private static CalendarExtraHours getExtraHours(DSLContext dslContext, Integer contractId, Boolean isFullTime) {
		CalendarExtraHours calendarExtraHours = new CalendarExtraHours();
		List<DayHourExtra> dayHoursComplementary = new ArrayList<>();
		
		if(Boolean.TRUE.equals(isFullTime)) {
			Result<Record> contractDataRecords = dslContext.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
				.orderBy(CONTRACT_DATA.START_DATE)
				.fetch();
			
			for(Record contractDataRecord: contractDataRecords){
				
				java.util.Date startDate = parseDateSqlToUtil(contractDataRecord.get(CONTRACT_DATA.START_DATE));
				java.util.Date endDate = parseDateSqlToUtil(contractDataRecord.get(CONTRACT_DATA.END_DATE));
				Double expression = null;
				
				try {
					expression = Double.parseDouble(contractDataRecord.get(CONTRACT_DATA.EXPRESSION));
				} catch (Exception e) {
					expression = null;
				}
				
				dayHoursComplementary.add(new DayHourExtra(startDate, endDate, expression));
			}
			
			calendarExtraHours.setDayHoursComplementary(dayHoursComplementary);
		}
		
		return calendarExtraHours;
	}
	
	
	private static Map<java.util.Date, Double> getComplementaryHours(DSLContext dslContext, Integer contractId, Boolean isFullTime) {
		Map<java.util.Date, Double> calendarComplementaryHours = new TreeMap<>();
		
		if(Boolean.FALSE.equals(isFullTime)) {
			Result<Record> contractDataRecords = dslContext.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(ContextVariable.ADDITIONAL_HOURS.getName()))
				.orderBy(CONTRACT_DATA.START_DATE)
				.fetch();
			
			for(Record contractDataRecord: contractDataRecords){
				
				java.util.Date startDate = parseDateSqlToUtil(contractDataRecord.get(CONTRACT_DATA.START_DATE));
				Double expression = null;
				
				try {
					expression = Double.parseDouble(contractDataRecord.get(CONTRACT_DATA.EXPRESSION));
				} catch (Exception e) {
					expression = null;
				}
				
				calendarComplementaryHours.put(startDate, expression);
			}
		}
		
		return calendarComplementaryHours;
	}

	
	private static Byte[] getWorkingDays(DSLContext dslContext, Integer contractId) {
		Byte[] workingDays = new Byte[7];
		
		Record contractCalendarRecord = dslContext.select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contractId))
				  .fetchOne();
		
		if (null != contractCalendarRecord){
				
			// Si vale 0 es laborable y si vale 1 es no laborables
			
			Result<Record> workingDaysRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"LABORABLE_LUNES",
							"LABORABLE_MARTES",
							"LABORABLE_MIERCOLES",
							"LABORABLE_JUEVES",
							"LABORABLE_VIERNES",
							"LABORABLE_SABADO",
							"LABORABLE_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				// Default calendar working days
				
				Integer calendarId = contractCalendarRecord.get(CONTRACT.CALENDAR);
				
				Result<Record> calendarRecords = dslContext.select().from(CALENDAR)
						.where(CALENDAR.ID.eq(calendarId))
						.fetch();
				
				for(Record calendarRecord : calendarRecords){
					workingDays[0] = calendarRecord.get(CALENDAR.SUNDAY);
					workingDays[1] = calendarRecord.get(CALENDAR.MONDAY);
					workingDays[2] = calendarRecord.get(CALENDAR.TUESDAY);
					workingDays[3] = calendarRecord.get(CALENDAR.WEDNESDAY);
					workingDays[4] = calendarRecord.get(CALENDAR.THURSDAY);
					workingDays[5] = calendarRecord.get(CALENDAR.FRIDAY);
					workingDays[6] = calendarRecord.get(CALENDAR.SATURDAY);
				}
				
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "LABORABLE_DOMINGO":
							workingDays[0] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_LUNES":
							workingDays[1] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MARTES":
							workingDays[2] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MIERCOLES":
							workingDays[3] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_JUEVES":
							workingDays[4] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_VIERNES":
							workingDays[5] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_SABADO":
							workingDays[6] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}	
		
		} else {
			
			// Si vale 0 es laborable y si vale 1 es no laborables
			Result<Record> workingDaysRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"LABORABLE_LUNES",
							"LABORABLE_MARTES",
							"LABORABLE_MIERCOLES",
							"LABORABLE_JUEVES",
							"LABORABLE_VIERNES",
							"LABORABLE_SABADO",
							"LABORABLE_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				workingDays[0] = 1;
				workingDays[1] = 0;
				workingDays[2] = 0;
				workingDays[3] = 0;
				workingDays[4] = 0;
				workingDays[5] = 0;
				workingDays[6] = 1;
			
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "LABORABLE_DOMINGO":
							workingDays[0] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_LUNES":
							workingDays[1] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MARTES":
							workingDays[2] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MIERCOLES":
							workingDays[3] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_JUEVES":
							workingDays[4] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_VIERNES":
							workingDays[5] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_SABADO":
							workingDays[6] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}
		}
		
		return workingDays;
	}
	
	
	private static void getWorkingDayByContractHour(DSLContext dslContext, Integer contractId, Boolean isFullTime, Byte[] workingDays) {
		
		if(Boolean.FALSE.equals(isFullTime)){
			
			String[] listDaysOfWeek = {
					"HORAS_LUNES", 
					"HORAS_MARTES", 
					"HORAS_MIERCOLES", 
					"HORAS_JUEVES", 
					"HORAS_VIERNES",
					"HORAS_SABADO",
					"HORAS_DOMINGO"
			}; 
			
			Result<Record> contractHoursRecords = dslContext.select().from(CONTRACT_DATA)
					  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
					  .and(CONTRACT_DATA.NAME.in(
							  ContextVariable.MONDAY_HOURS.getName()
							  ,ContextVariable.TUESDAY_HOURS.getName()
							  ,ContextVariable.WEDNESDAY_HOURS.getName()
							  ,ContextVariable.THURSDAY_HOURS.getName()
							  ,ContextVariable.FRIDAY_HOURS.getName()
							  ,ContextVariable.SATURDAY_HOURS.getName()
							  ,ContextVariable.SUNDAY_HOURS.getName()))
					  .fetch();
	
			ArrayList<String> listDefinedDays = new ArrayList<>();
			for(Record contractHoursRecord : contractHoursRecords)
				listDefinedDays.add(contractHoursRecord.get(CONTRACT_DATA.NAME));
			
			for (int i=0; i<7; i++){
				String dayOfWeek = listDaysOfWeek[i];
				if (!listDefinedDays.contains(dayOfWeek))
					workingDays[i] = 1; // Non working day
				else {
					ArrayList<String> values = getValuesDayOfWeek(contractHoursRecords, dayOfWeek);
					workingDays[i] = values.isEmpty() || values.get(0) == null ? (byte)1 : (byte)0;
				}
			}
		}
	}

	private static void getFestivesAndDaysType(DSLContext dslContext, Integer contractId, Map<java.util.Date, String> festiveDays, CalendarDaysType calendarDaysType) {
		Record contractCalendarRecord = dslContext.select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contractId))
				  .fetchOne();
		
		Integer calendarId = null == contractCalendarRecord ? null : contractCalendarRecord.get(CONTRACT.CALENDAR);
		
		if(null != calendarId) {
		
			Integer holidayId = dslContext.select(CALENDAR.HOLIDAY).from(CALENDAR)
					.where(CALENDAR.ID.eq(calendarId))
					.fetchOne(CALENDAR.HOLIDAY);
			
			ArrayList<Integer> holidays = new ArrayList<>();
			
			// Get all holidays ids
			while ( holidayId != null ) {
				holidays.add(holidayId);
				
				holidayId = dslContext.select(HOLIDAY.HOLIDAY_)
						.from(HOLIDAY)
						.where(HOLIDAY.ID.eq(holidayId))
						.fetchOne()
						.get(HOLIDAY.HOLIDAY_);
			}
			
			Result<Record> holidayRecords = dslContext.select()
					.from(HOLIDAY_DETAIL)
					.where(HOLIDAY_DETAIL.HOLIDAY.in(holidays))
					.fetch();
			
			for(Record holidayRecord : holidayRecords) {
				CalendarDayType calendarDayType = new CalendarDayType();
				
				calendarDayType.setStartDate(parseDateSqlToUtil(holidayRecord.get(HOLIDAY_DETAIL.DATE)));
				calendarDayType.setEndDate(parseDateSqlToUtil(holidayRecord.get(HOLIDAY_DETAIL.DATE)));
				calendarDayType.setDayType(DayType.FREEDAY);
				calendarDayType.setExpession(holidayRecord.get(HOLIDAY_DETAIL.DESCRIPTION));
				
				// Add Calendar Day Type
				
				calendarDaysType.addDayType(calendarDayType);
				
				// Add Calendar Festive
				
				festiveDays.put(parseDateSqlToUtil(holidayRecord.get(HOLIDAY_DETAIL.DATE)), holidayRecord.get(HOLIDAY_DETAIL.DESCRIPTION));
			}
		}
		
	}
	
	private static void getDaysType(DSLContext dslContext, Integer contractId, CalendarDaysType calendarDaysType) {
		
		Result<Record> daysTypeRecords = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.in(
						ContextVariable.HOLIDAYS.getName()
						,"NO_LABORABLE"
						,"DIAS_EFECTIVOS"
						,"JORNADAS_REALES"
						,"JORNADAS_TEORICAS"
						,"LABORABLE"
						//Coeficientes
						,ContextVariable.ERE_FACTOR.getName()
						,ContextVariable.STRIKE_FACTOR.getName()
						,"COEFICIENTE_ERE_FZA"
						,"COEFICIENTE_ERE_FZA_EXONERADO"
						//,"FIN_ERE_FZA_EXONERADO"
						,"COEFICIENTE_AUSENCIA"
						,"CAUSA_INACTIVIDAD"))
				.fetch();
		
		for(Record daysTypeRecord : daysTypeRecords){
			CalendarDayType calendarDayType = new CalendarDayType();
			
			calendarDayType.setStartDate(parseDateSqlToUtil(daysTypeRecord.get(CONTRACT_DATA.START_DATE)));
			calendarDayType.setEndDate(parseDateSqlToUtil(daysTypeRecord.get(CONTRACT_DATA.END_DATE)));
			calendarDayType.setDayType(parseStringToDayType(daysTypeRecord.get(CONTRACT_DATA.NAME)));
			calendarDayType.setExpession(daysTypeRecord.get(CONTRACT_DATA.EXPRESSION));
			
			calendarDaysType.addDayType(calendarDayType);
		}
	}

	private static CalendarDaysType getPartialityDaysType(DSLContext dslContext, Integer contractId) {
		CalendarDaysType partialityDaysType = new CalendarDaysType();
		
		Result<Record> daysPartialityRecords = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
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
		
		return partialityDaysType;
	}
	
	private static void getITDaysType(DSLContext dslContext, Integer contractId, CalendarDaysType calendarDaysType) {
		Result<Record> itDaysRecords = dslContext.select().from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(contractId))
				.fetch();
		
		for(Record itDaysRecord : itDaysRecords){
			CalendarDayType calendarDayType = new CalendarDayType();
			
			calendarDayType.setStartDate(parseDateSqlToUtil(itDaysRecord.get(CONTRACT_DATA.START_DATE)));
			calendarDayType.setEndDate(parseDateSqlToUtil(itDaysRecord.get(CONTRACT_DATA.END_DATE)));
			calendarDayType.setDayType(parseStringToDayType("DIAS_IT"));
			calendarDayType.setExpession("");
			
			calendarDaysType.addDayType(calendarDayType);
		}
	}
	
	// ------------------------------------------------ Set Calendar
	
	public static void setEmployeeCalendar (Connection conn, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo) throws IllegalArgumentException {
		setEmployeeCalendarDB(DSL.using(conn, getDefaultSettings()), contractId, employeeCalendarInfo);
	}
	
	private static void setEmployeeCalendarDB(DSLContext dslContext, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo) {
			
		// Contract
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		
		Date contractStartDate = contractRecord.get(CONTRACT.START_DATE);
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		// isFullTime
		
		Boolean isFullTime = employeeCalendarInfo.isFullTime();
		
		// Calendar Hours
		
		updateCalendarHours(dslContext, domainId, contractId, employeeCalendarInfo.getCalendarHours());
		
		// Extra Hours
		
		updateExtraHours(dslContext, domainId, contractId, employeeCalendarInfo.getCalendarExtraHours());
		
		// Complementary Hours
		
		updateComplementaryHours(dslContext, domainId, contractId, employeeCalendarInfo.getCalendarComplementaryHours());
		
		// Working Days
		
		updateWorkingDays(dslContext, domainId, contractId, contractStartDate, contractEndDate, isFullTime, employeeCalendarInfo.getWorkingDays());
		
		// Contract Days Type
		
		updateDaysType(dslContext, domainId, contractId, employeeCalendarInfo.getCalendarDaysType());
		
		// Contract Partiality Days Type
		
		updatePartialityDaysType(dslContext, domainId, contractId, employeeCalendarInfo.getPartialityDaysType());
		
	}

	private static void updateCalendarHours(DSLContext dslContext, Integer domainId, Integer contractId, CalendarHours calendarHours) {
		// Eliminamos las horas existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
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
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, name)
					.set(CONTRACT_DATA.EXPRESSION, null == dayHour.getValue() ? null : Double.toString(dayHour.getValue()))
					.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(dayHour.getStartDate()))
					.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(dayHour.getEndDate()))
					.execute();
			}
		}
	}

	private static void updateExtraHours(DSLContext dslContext, Integer domainId, Integer contractId, CalendarExtraHours calendarExtraHours) {
		// Borramos las horas extras existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
			.execute();
		
		String variableName =  ContextVariable.EXTRA_HOURS.getName();
		
		for( DayHourExtra extraHour : calendarExtraHours.getExtraHours()) {
			Double expression = extraHour.getValue();
			Date startDate = parseDateUtilToSql(extraHour.getStartDate());
			Date endDate = parseDateUtilToSql(extraHour.getEndDate());
			
			if(null != expression) {
				
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, variableName)
					.set(CONTRACT_DATA.EXPRESSION, expression.toString())
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.execute();
			}
		}
	}
	
	private static void updateComplementaryHours(DSLContext dslContext, Integer domainId, Integer contractId, Map<java.util.Date, Double> calendarComplementaryHours) {
		// Borramos las horas extras existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq(ContextVariable.ADDITIONAL_HOURS.getName()))
			.execute();
		
		String variableName =  ContextVariable.ADDITIONAL_HOURS.getName();
		
		for(Entry<java.util.Date, Double> complementaryHourEntry : calendarComplementaryHours.entrySet()) {
			Date date = parseDateUtilToSql(complementaryHourEntry.getKey());
			Double expression = complementaryHourEntry.getValue();
			
			if(null != expression) {
				
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, variableName)
					.set(CONTRACT_DATA.EXPRESSION, expression.toString())
					.set(CONTRACT_DATA.START_DATE, date)
					.set(CONTRACT_DATA.END_DATE, date)
					.execute();
			}
		}
	}

	private static void updateWorkingDays(DSLContext dslContext, Integer domainId, Integer contractId, Date contractStartDate, Date contractEndDate, Boolean isFullTime, Byte[] workingDays) {
		// Borramos los existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(
					"LABORABLE_LUNES",
					"LABORABLE_MARTES",
					"LABORABLE_MIERCOLES",
					"LABORABLE_JUEVES",
					"LABORABLE_VIERNES",
					"LABORABLE_SABADO",
					"LABORABLE_DOMINGO"))
			.execute();
		
		if(Boolean.TRUE.equals(isFullTime)) {
			for(int day=0; day<7; day++) {
				if(null == workingDays[day])
					continue;
				
				String expression = Byte.toString(workingDays[day]);
				
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
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, name)
					.set(CONTRACT_DATA.EXPRESSION, expression)
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			}
		}
	}
	
	private static void updateDaysType(DSLContext dslContext, Integer domainId, Integer contractId, CalendarDaysType calendarDaysType) {
		// Eliminamos los tipos de dias existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(
					"DIAS_FESTIVOS",
					ContextVariable.HOLIDAYS.getName()
					,"NO_LABORABLE"
					,"DIAS_EFECTIVOS"
					,"JORNADAS_REALES"
					,"JORNADAS_TEORICAS"
					,ContextVariable.ERE_FACTOR.getName()
					,ContextVariable.STRIKE_FACTOR.getName()
					,"COEFICIENTE_ERE_FZA"
					,"COEFICIENTE_ERE_FZA_EXONERADO"
					,"FIN_ERE_FZA_EXONERADO"
					,"COEFICIENTE_AUSENCIA"
					,"DIAS_INACTIVIDAD"
					,"CAUSA_INACTIVIDAD"
					,"LABORABLE"
			)).execute();
		
		ArrayList<CalendarDayType> dayTypeFixList = calendarDaysType.getFixUpdateList();
		
		for(CalendarDayType calendarDayType : dayTypeFixList) {
			if(calendarDayType.getDayType() == DayType.INACTIVITY) {
				Integer dayBetween = DateUtils.getDaysBetween(calendarDayType.getStartDate(), calendarDayType.getEndDate()) + 1;
				
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, "DIAS_INACTIVIDAD")
					.set(CONTRACT_DATA.EXPRESSION, dayBetween.toString())
					.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(calendarDayType.getStartDate()))
					.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(calendarDayType.getEndDate()))
					.execute();
			}
			
			Date startDate = parseDateUtilToSql(calendarDayType.getStartDate());
			Date endDate = parseDateUtilToSql(calendarDayType.getEndDate());
			
			if(calendarDayType.getDayType() == DayType.EREFZAEXONENDDAY) {
				java.util.Date newStartDate = DateUtils.copyDateOnly(calendarDayType.getStartDate());
				newStartDate = DateUtils.deleteDays2Date(newStartDate, 1);
				
				startDate = parseDateUtilToSql(newStartDate);
				endDate = parseDateUtilToSql(newStartDate);
			}
			
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.NAME, getNameByDayType(calendarDayType.getDayType()))
				.set(CONTRACT_DATA.EXPRESSION, calendarDayType.getExpession())
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updatePartialityDaysType(DSLContext dslContext, Integer domainId, Integer contractId, CalendarDaysType partialityDaysType) {
		//  Eliminamos los tipos de dias existentes
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD"))
			.execute();
		
		ArrayList<CalendarDayType> partialityDayTypeFixList = partialityDaysType.getFixUpdateList();
		
		for(CalendarDayType partialityDayType : partialityDayTypeFixList) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.NAME, "COEFICIENTE_PARCIALIDAD")
				.set(CONTRACT_DATA.EXPRESSION, partialityDayType.getExpession())
				.set(CONTRACT_DATA.START_DATE, parseDateUtilToSql(partialityDayType.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, parseDateUtilToSql(partialityDayType.getEndDate()))
				.execute();
		}
	}
	
	// ------------------------------------------------ Auxiliar Methods

	private static Boolean isFullTimeJourney(String tc2) {
		if(AonStringUtils.isBlank(tc2))
			return false;
		
		parseContractData(tc2);
		
		try {
			Integer contractType = Integer.parseInt(tc2);
			return !AonNumberUtils.between(contractType, 200, 300) && !AonNumberUtils.between(contractType, 500, 599) && !AonNumberUtils.equals(contractType, 0);
		} catch (NumberFormatException e) {
			return '1' == tc2.charAt(1) || '4' == tc2.charAt(1)|| "true".equals(tc2);
		}
	}
	
	private static Boolean isAgrarianJourney(Integer cccType) {
		return null != cccType && cccType == 7;
	}
	
	private static String parseContractData(String contractType) {
		if(AonStringUtils.isNotBlank(contractType) && contractType.contains("\""))
			try {
				contractType = contractType.split("\"")[1];
				return contractType;
			} catch (IndexOutOfBoundsException e) {
				return contractType;
			}	
		else
			return contractType;
	}
	
	private static ArrayList<String> getValuesDayOfWeek(Result<Record> contractHoursRecords, String dayOfWeek) {
		ArrayList<String> values = new ArrayList<>();
		
		for(Record r: contractHoursRecords)
			if(dayOfWeek.equals(r.get(CONTRACT_DATA.NAME)))
				values.add(r.get(CONTRACT_DATA.EXPRESSION));
		
		return values;
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
		if(AonStringUtils.isBlank(value))
			return null;
		
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0.00;
		}
	}
	
	private static DayType parseStringToDayType(String dayTypeName) {
		return TYPE_OF_DAY.get(dayTypeName);
	}
	
	private static String getNameByDayType(DayType dayType) {
		return NAME_TYPE_OF_DAY.get(dayType);
	}
	
	// ------------------------------------------------ Name Type Day
	
	private static final Map<DayType, String> NAME_TYPE_OF_DAY;
	static {
		Map<DayType, String> map = new HashMap<>();
		map.put(DayType.BAJAIT, "DIAS_IT");
		map.put(DayType.HOLIDAY, "DIAS_VACACIONES");
		map.put(DayType.EFFECTIVE, "DIAS_EFECTIVOS");
		map.put(DayType.STRIKEDAY, "COEFICIENTE_HUELGA");
		map.put(DayType.EREDAY, "COEFICIENTE_ERE");
		map.put(DayType.INACTIVITY, "CAUSA_INACTIVIDAD");
		map.put(DayType.REAL_DAYS, "JORNADAS_REALES");
		map.put(DayType.IF_DAYS, "JORNADAS_TEORICAS");
		map.put(DayType.NOWORKINGDAY, "NO_LABORABLE");
		map.put(DayType.DROPDAY, "COEFICIENTE_AUSENCIA");
		map.put(DayType.PARTIALITY, "COEFICIENTE_PARCIALIDAD");
		map.put(DayType.EREFZADAY, "COEFICIENTE_ERE_FZA");
		map.put(DayType.EREFZAEXONDAY, "COEFICIENTE_ERE_FZA_EXONERADO");
		map.put(DayType.EREFZAEXONENDDAY,"FIN_ERE_FZA_EXONERADO");
		map.put(DayType.FREEDAY, "DIAS_FESTIVOS");
		map.put(DayType.WORKINGDAY, "LABORABLE");
		NAME_TYPE_OF_DAY = Collections.unmodifiableMap(map);
	}
	
	private static final Map<String, DayType> TYPE_OF_DAY;
	static {
		Map<String, DayType> map = new HashMap<>();
		map.put("DIAS_IT", DayType.BAJAIT);
		map.put("DIAS_VACACIONES", DayType.HOLIDAY);
		map.put("COEFICIENTE_HUELGA", DayType.STRIKEDAY);
		map.put("COEFICIENTE_ERE", DayType.EREDAY);
		map.put("CAUSA_INACTIVIDAD", DayType.INACTIVITY);
		map.put("JORNADAS_REALES", DayType.REAL_DAYS);
		map.put("JORNADAS_TEORICAS", DayType.IF_DAYS);
		map.put("NO_LABORABLE", DayType.NOWORKINGDAY);
		map.put("COEFICIENTE_AUSENCIA", DayType.DROPDAY);
		map.put("COEFICIENTE_PARCIALIDAD", DayType.PARTIALITY);
		map.put("COEFICIENTE_ERE_FZA", DayType.EREFZADAY);
		map.put("COEFICIENTE_ERE_FZA_EXONERADO", DayType.EREFZAEXONDAY);
		map.put("COEF_ERE_FZA_EXON_PARCIAL", DayType.EREFZAEXONPARTIALDAY);
		map.put("FIN_ERE_FZA_EXONERADO", DayType.EREFZAEXONENDDAY);
		map.put("DIAS_FESTIVOS", DayType.FREEDAY);
		map.put("LABORABLE", DayType.WORKINGDAY);
		map.put("DIAS_EFECTIVOS", DayType.EFFECTIVE);
		TYPE_OF_DAY = Collections.unmodifiableMap(map);
	}
	
}
