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

		return getEmployeeInformation(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static void setEmployeeHour(Connection conn, Integer contract, EmployeeCalendarUpdate updateInfo){
		setEmployeeInformation(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	private static EmployeeCalendarData getEmployeeInformation(DSLContext dslContext, Integer contract) throws IllegalArgumentException {

		EmployeeCalendarData employeeInfoCalendar;
		
		ArrayList<Quartet<Date, Date, String, String>> contractHoursList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractDayTypesList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractITDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		HashMap<java.util.Date, String> contractFestiveDaysList = new HashMap<java.util.Date, String>();
		ArrayList<Byte> contractNonWorkingDaysList = new ArrayList<Byte>();
		Boolean fullTimeJourney = false;
		
		// ------------------------------------------------------ JORNADA COMPLETA -------------------------------------------------------
				 	
		String journeyTypeEmployee = dslContext
				  .select(CONTRACT_DATA.EXPRESSION)
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
				  .orderBy(CONTRACT_DATA.START_DATE.desc())
				  .limit(1)
				  .fetchOne().get(CONTRACT_DATA.EXPRESSION);
		
		if(journeyTypeEmployee == null)
			journeyTypeEmployee = dslContext
			  .select(CONTRACT_DATA.EXPRESSION)
			  .from(CONTRACT_DATA)
			  .where(CONTRACT_DATA.CONTRACT.eq(contract))
			  .and(CONTRACT_DATA.NAME.equal("TIEMPO_COMPLETO"))
			  .orderBy(CONTRACT_DATA.START_DATE.desc())
			  .limit(1)
			  .fetchOne().get(CONTRACT_DATA.EXPRESSION);
		
		fullTimeJourney = isFullTimeJourney(journeyTypeEmployee);
		
		// ---------------------------------------------- HORAS SEMANALES ---------------------------------------------------------
		
		Result<Record> contractHoursEmployeeInfo = dslContext
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
		
		for(Record r: contractHoursEmployeeInfo){
			Quartet<Date, Date, String, String> quarterEmployeeHourInfo = new Quartet<Date, Date, String, String>();
			
			quarterEmployeeHourInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractHoursList.add(quarterEmployeeHourInfo);
		}
		
		// -------------------------------------- TIPOS DIAS NO LABORABLES SEGUN HORAS ---------------------------------------------
		
		if(!fullTimeJourney){
			
			String listDaysOfWeek [] = {"HORAS_LUNES", "HORAS_MARTES", "HORAS_MIERCOLES", "HORAS_JUEVES", "HORAS_VIERNES",
					 "HORAS_SABADO", "HORAS_DOMINGO"}; 
	
			ArrayList<String> listDefinedDays = new ArrayList<String>();
			for(Record r: contractHoursEmployeeInfo){
				listDefinedDays.add(r.get(CONTRACT_DATA.NAME));
			}
			
			for (int i=0; i<7; i++){
				String dayOfWeek = listDaysOfWeek[i];
				ArrayList<String> values = getValueDayOfWeek(contractHoursEmployeeInfo, dayOfWeek);
				if (!listDefinedDays.contains(dayOfWeek)){
					contractNonWorkingDaysList.add((byte) 1);
				}else if (values.size() > 1){
					contractNonWorkingDaysList.add((byte) 0);
				}else if (null == values || values.isEmpty() || "-1.0".equals(values.get(0))){
					contractNonWorkingDaysList.add((byte) 1);
				}else{
					contractNonWorkingDaysList.add((byte) 0);
				}
			}
		}
		
		// ---------------------------------------------- TIPOS DIAS ---------------------------------------------------------
		
		Result<Record> contractDayTypesEmployeeInfo = dslContext
					.select()
					.from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.and(CONTRACT_DATA.NAME.in(
							ContextVariable.ERE_DAYS.getName()
							,ContextVariable.STRIKE_DAYS.getName()
							,ContextVariable.HOLIDAYS.getName()))
					.fetch();
		
		for(Record r: contractDayTypesEmployeeInfo){
			Quartet<Date, Date, String, String> quarterDayTypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterDayTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractDayTypesList.add(quarterDayTypeEmployee);
		}
		
		Result<Record> contractITDaysEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(contract))
				.fetch();
		
		for(Record r: contractITDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterITDayEmployee = new Quartet<Date, Date, String, String>();
			
			quarterITDayEmployee.setStartDate(r.get(CONTRACT_LEAVE.START_DATE))
			.setEndDate(r.get(CONTRACT_LEAVE.END_DATE))
			.setName("DIAS_IT")
			.setExpression("");
			
			contractITDayTypeList.add(quarterITDayEmployee);
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
			
			if(fullTimeJourney){
				Result<Record> nonWorkingDays = dslContext.select()
															.from(CALENDAR)
															.where(CALENDAR.ID.eq(calendar))
															.fetch();
				
				for(Record r: nonWorkingDays){
					contractNonWorkingDaysList.add(r.get(CALENDAR.MONDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.TUESDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.WEDNESDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.THURSDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.FRIDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.SATURDAY));
					contractNonWorkingDaysList.add(r.get(CALENDAR.SUNDAY));
				}
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
				contractFestiveDaysList.put(r.get(HOLIDAY_DETAIL.DATE), r.get(HOLIDAY_DETAIL.DESCRIPTION));
		
		}else if(fullTimeJourney && calendar == null){
			contractNonWorkingDaysList.add((byte) 0);
			contractNonWorkingDaysList.add((byte) 0);
			contractNonWorkingDaysList.add((byte) 0);
			contractNonWorkingDaysList.add((byte) 0);
			contractNonWorkingDaysList.add((byte) 0);
			contractNonWorkingDaysList.add((byte) 1);
			contractNonWorkingDaysList.add((byte) 1);
		}
		
		// ------------------------------------------------ RESULTADO -------------------------------------------------------------
		
		employeeInfoCalendar = new EmployeeCalendarData(contractHoursList, contractDayTypesList, contractITDayTypeList, 
				contractFestiveDaysList, contractNonWorkingDaysList, fullTimeJourney);
		
		return employeeInfoCalendar;
	}
	
	// -------------- AUX METHODS GET EMPLOYEE INFO ------------
	
	private static ArrayList<String> getValueDayOfWeek(Result<Record> contractHoursEmployeeInfo, String dayOfWeek) {
		ArrayList<String> result = new ArrayList<String>();
		for(Record r: contractHoursEmployeeInfo){
			if(dayOfWeek.equals(r.get(CONTRACT_DATA.NAME))){
				result.add(r.get(CONTRACT_DATA.EXPRESSION));
			}
		}
		return result;
	}

	private static Boolean isFullTimeJourney(String tipoJornadaInfoEmpleado) {
		if ('1' == tipoJornadaInfoEmpleado.charAt(1) || '4' == tipoJornadaInfoEmpleado.charAt(1)
				|| "false" == tipoJornadaInfoEmpleado)
			return true;
		else
			return false;
	}

	
	@SuppressWarnings("deprecation")
	private static void setEmployeeInformation(DSLContext dslContext, Integer contract, EmployeeCalendarUpdate updateInfo) {
		
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
		
		Date realEndDate = dslContext.select(CONTRACT.END_DATE)
						.from(CONTRACT)
						.where(CONTRACT.ID.eq(contract))
						.fetchOne().value1();
		
		HashMap<java.util.Date, Double> updateHoursMap = updateInfo.getDaysHourMap();
		
		if (!updateHoursMap.isEmpty()){
			
			java.util.Date startDateHour = new java.util.Date();
			java.util.Date endDateHour = new java.util.Date();
			
			for (Entry<java.util.Date, Double> entry : updateHoursMap.entrySet()) {
				if (entry.getKey().before(startDateHour))
					startDateHour = DateUtils.copyDateOnly(entry.getKey());
				
				if (entry.getKey().after(endDateHour))
					endDateHour = DateUtils.copyDateOnly(entry.getKey());
			}
			
			for (int i=0; i<7; i++){
				java.util.Date  date = DateUtils.copyDateOnly(startDateHour);
				DateUtils.addDays2Date(date, i);
	
				String dayOfWeek = calculateDayOfWeek(date.getDay());
				
				java.util.Date auxstartDateHour = DateUtils.copyDateOnly(date);
				Double startHour = updateHoursMap.get(auxstartDateHour);
				
				while (date.before(endDateHour) && updateHoursMap.containsKey(date)){
					if(!startHour.equals(updateHoursMap.get(date))){
						Date sqlstartDateHour = new Date(auxstartDateHour.getTime());
						Date sqlendDateHour = new Date(date.getTime());
						
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
								CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
								CONTRACT_DATA.END_DATE)
								.values(domain, dayOfWeek, contract, Double.toString(startHour), 
										sqlstartDateHour, sqlendDateHour).execute();
					
						startHour = updateHoursMap.get(date);
						auxstartDateHour = DateUtils.copyDateOnly(date);
					}
					
					DateUtils.addDays2Date(date, 7);
				}
				
				Date sqlstartDateHour = new Date(startDateHour.getTime());
				Date sqlendDateHour;
				
				if(realEndDate == null)
					sqlendDateHour = null;
				else
					sqlendDateHour = new Date(realEndDate.getTime());
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, dayOfWeek, contract, Double.toString(startHour), 
								sqlstartDateHour, sqlendDateHour).execute();

			}
		}
		
		// ----------------------------------------------- ACTUALIZACION TIPO DIAS -------------------------------------------------------
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				  ContextVariable.ERE_DAYS.getName()
				  ,ContextVariable.STRIKE_DAYS.getName()
				  ,ContextVariable.HOLIDAYS.getName()))
		   .execute();
		
		HashMap<java.util.Date, DayType> updateDaysTypeMap = updateInfo.getDaysTypeMap();
		
		java.util.Date startDateType = new java.util.Date();
		java.util.Date endDateType = new java.util.Date();
		
		for (Entry<java.util.Date, DayType> entry : updateDaysTypeMap.entrySet()) {
			if (entry.getKey().before(startDateType))
				startDateType = DateUtils.copyDateOnly(entry.getKey());
			
			if (entry.getKey().after(endDateType))
				endDateType = DateUtils.copyDateOnly(entry.getKey());
		}
		
		java.util.Date dateType = DateUtils.copyDateOnly(startDateType);
		java.util.Date auxStartDateType = DateUtils.copyDateOnly(dateType);
		String dayType = "";
		DayType startDayType = DayType.NOTYPEDAY;
		
		if (updateDaysTypeMap.get(dateType) != null){
			dayType = calculateDayType(updateDaysTypeMap.get(dateType));
			startDayType = updateDaysTypeMap.get(auxStartDateType);
		}
		
		while (dateType.before(endDateType)){
			if (updateDaysTypeMap.get(dateType) != null){
				if(!startDayType.equals(updateDaysTypeMap.get(dateType))){
					if(validDayType(dayType)){
						
						Date sqlStartDateType = new Date(auxStartDateType.getTime());
						java.util.Date javaEndDateType = DateUtils.copyDateOnly(dateType);
						DateUtils.addDays2Date(javaEndDateType, -1);
						Date sqlEndDateType = new Date(javaEndDateType.getTime());
						
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
								CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
								CONTRACT_DATA.END_DATE)
								.values(domain, dayType, contract, "", 
										sqlStartDateType, sqlEndDateType).execute();
					}
					
					dayType = calculateDayType(updateDaysTypeMap.get(dateType));
					startDayType = updateDaysTypeMap.get(dateType);
					auxStartDateType = DateUtils.copyDateOnly(dateType);
				}
			}
			
			DateUtils.addDays2Date(dateType, 1);
		}
		
	}
	
	// ---------- CLASS AUX METHODS ----------
	
	private static boolean validDayType(String dayType) {
		return dayType.equals("DIAS_ERE") || dayType.equals("DIAS_HUELGA") 
				|| dayType.equals("DIAS_VACACIONES");
	}

	private static String calculateDayType(DayType dayType) {
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

	private static String calculateDayOfWeek(int day) {
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
