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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
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
	
	public static String resetCalendar(Connection conn, Integer employeeId) {
		return resetCalendarInformation(DSL.using(conn, getDefaultSettings()), employeeId);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static String resetCalendarInformation(DSLContext dslContext, Integer contractId) {
		
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
		// DAY TYPES
		varNames.add(ContextVariable.STRIKE_DAYS.getName());
		varNames.add(ContextVariable.HOLIDAYS.getName());
		varNames.add(ContextVariable.STRIKE_FACTOR.getName());
		varNames.add("DIAS_INACTIVIDAD");
		varNames.add("NO_LABORABLE");
		varNames.add("CAUSA_INACTIVIDAD");
		varNames.add("PEONADAS");
		varNames.add("FESTIVE_WORKING");
		// FESTIVE WORKING DAYS
		varNames.add("FESTIVE_WORKING");
		// PEONADAS
		varNames.add("PEONADAS");
		varNames.add("JORNADAS_REALES");
		// CAUSA INACTIVITY
		varNames.add("CAUSA_INACTIVIDAD");
		// DROPDAY (AUSENCIA)
		varNames.add("DIAS_AUSENCIA");
		// PARTIALITY
		varNames.add("COEFICIENTE_PARCIALIDAD");
		varNames.add("DIAS_PARCIALIDAD");
		// ERE TYPES
		varNames.add(ContextVariable.ERE_DAYS.getName());
		varNames.add(ContextVariable.ERE_FACTOR.getName());
		varNames.add("DIAS_ERE_FZA");
		varNames.add("COEFICIENTE_ERE_FZA");
		varNames.add("DIAS_ERE_FZA_EXONERADO");
		varNames.add("COEFICIENTE_ERE_FZA_EXONERADO");
		
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(varNames))
			.execute();
		
		return "";
	}

	private static EmployeeCalendarData getEmployeeInformation(DSLContext dslContext, Integer contract) throws IllegalArgumentException {

		EmployeeCalendarData employeeInfoCalendar;
		
		System.out.println("Contract :"+contract);
		
		ArrayList<Quartet<Date, Date, String, String>> contractHoursList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractExtraHoursList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractDayTypesList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientEREDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientEREFzaDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientEREFzaExonDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientStrikeDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
//		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientInactivityDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractCoefficientDropDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractITDayTypeList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractInactivityDaysList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> contractPartialityDaysList = new ArrayList<Quartet<Date, Date, String, String>>();
		HashMap<java.util.Date, String> contractFestiveDaysList = new HashMap<java.util.Date, String>();
		ArrayList<Byte> contractNonWorkingDaysList = new ArrayList<Byte>();
		Boolean fullTimeJourney = false;
		Integer contractType = 0;
		String typeInactivityDays = null;
		ArrayList<Date> festiveWorkingDays = new ArrayList<Date>();
		
		// ------------------------------------------------------ JORNADA COMPLETA -------------------------------------------------------
		
		String journeyTypeEmployee = "";
		Record1<String> journeyType = dslContext
				  .select(CONTRACT_DATA.EXPRESSION)
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.equal("TIEMPO_COMPLETO"))
				  .orderBy(CONTRACT_DATA.START_DATE.desc())
				  .limit(1)
				  .fetchOne();
		
		if(journeyType != null){
			journeyTypeEmployee = journeyType.get(CONTRACT_DATA.EXPRESSION);
		}else{
			journeyTypeEmployee = dslContext
			  .select(CONTRACT_DATA.EXPRESSION)
			  .from(CONTRACT_DATA)
			  .where(CONTRACT_DATA.CONTRACT.eq(contract))
			  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
			  .orderBy(CONTRACT_DATA.START_DATE.desc())
			  .limit(1)
			  .fetchOne().get(CONTRACT_DATA.EXPRESSION);
		}
		
		fullTimeJourney = isFullTimeJourney(journeyTypeEmployee);
		
		// ---------------------------------------------- CHECK AGRARIO ---------------------------------------------------------
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		Integer contrat_ccc = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		if(null == contrat_ccc)
			contractType = -1;
		else{
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(contrat_ccc))
				.fetchOne();
			contractType = (int) enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
		}
		
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
		
		// ---------------------------------------------- HORAS EXTRAS MENSUALES ---------------------------------------------------------
		
		Result<Record> contractExtraHoursEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
				  .fetch();
		
		for(Record r: contractExtraHoursEmployeeInfo){
			Quartet<Date, Date, String, String> quarterEmployeeExtraHoursInfo = new Quartet<Date, Date, String, String>();
			
			quarterEmployeeExtraHoursInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractExtraHoursList.add(quarterEmployeeExtraHoursInfo);
		}
		
		// -------------------------------------- TIPOS DIAS NO LABORABLES SEGUN HORAS ---------------------------------------------
		
		if(!fullTimeJourney){
			
			if(contractHoursEmployeeInfo.isEmpty()){
				contractNonWorkingDaysList.add((byte) 0);
				contractNonWorkingDaysList.add((byte) 0);
				contractNonWorkingDaysList.add((byte) 0);
				contractNonWorkingDaysList.add((byte) 0);
				contractNonWorkingDaysList.add((byte) 0);
				contractNonWorkingDaysList.add((byte) 1);
				contractNonWorkingDaysList.add((byte) 1);
			}else{
			
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
					}else if (/*null == values ||*/ values.isEmpty() || values.get(0) == null){
						contractNonWorkingDaysList.add((byte) 1);
					}else{
						contractNonWorkingDaysList.add((byte) 0);
					}
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
							,"DIAS_ERE_FZA"
							,"DIAS_ERE_FZA_EXONERADO"
							,ContextVariable.STRIKE_DAYS.getName()
							,ContextVariable.HOLIDAYS.getName()
							,"NO_LABORABLE"
							,"DIAS_INACTIVIDAD"
							,"DIAS_AUSENCIA"
							,"PEONADAS"))
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
		
		// ---------------------------------------------- TIPOS DIAS (PARCIALIDAD) -------------------------------------------------
		
		Result<Record> contractDayPartialityTypesEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in("DIAS_PARCIALIDAD"))
				.fetch();
		
		if(null == contractDayPartialityTypesEmployeeInfo || contractDayPartialityTypesEmployeeInfo.isEmpty()) {
			contractDayPartialityTypesEmployeeInfo = dslContext
					.select()
					.from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.and(CONTRACT_DATA.NAME.in("COEFICIENTE_PARCIALIDAD"))
					.fetch();
		}
	
		for(Record r: contractDayPartialityTypesEmployeeInfo){
			Quartet<Date, Date, String, String> quarterDayTypeEmployee = new Quartet<Date, Date, String, String>();
			
			Date endDate = r.get(CONTRACT_DATA.END_DATE);
			if(null == endDate) {
				java.util.Date lastDayOfYear = DateUtils.getLastDayOfYear(new java.util.Date());
				endDate = new Date(DateUtils.copyDateOnly(lastDayOfYear).getTime());
			}
			
			quarterDayTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(endDate)
			.setName("DIAS_PARCIALIDAD")
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractDayTypesList.add(quarterDayTypeEmployee);
		}
	
		
		// -------------------------------------- COEFICIENTE TIPO DE DIAS ---------------------------------------------------------
		
		// ------------- ERE_FACTOR
		Result<Record> coeficientsEREEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in(
						ContextVariable.ERE_FACTOR.getName()))
				.fetch();
	
		for(Record r: coeficientsEREEmployeeInfo){
			Quartet<Date, Date, String, String> quarterCoeficcientERETypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterCoeficcientERETypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractCoefficientEREDayTypeList.add(quarterCoeficcientERETypeEmployee);
		}
		
		// ------------- COEFICIENTE_ERE_FZA
		Result<Record> coeficientsEREFzaEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in("COEFICIENTE_ERE_FZA"))
				.fetch();
	
		for(Record r: coeficientsEREFzaEmployeeInfo){
			Quartet<Date, Date, String, String> quarterCoeficcientEREFzaTypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterCoeficcientEREFzaTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractCoefficientEREFzaDayTypeList.add(quarterCoeficcientEREFzaTypeEmployee);
		}
		
		// ------------- COEFICIENTE_ERE_FZA_EXONERADO
				Result<Record> coeficientsEREFzaExoneradoEmployeeInfo = dslContext
						.select()
						.from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contract))
						.and(CONTRACT_DATA.NAME.in("COEFICIENTE_ERE_FZA_EXONERADO"))
						.fetch();
			
				for(Record r: coeficientsEREFzaExoneradoEmployeeInfo){
					Quartet<Date, Date, String, String> quarterCoeficcientEREFzaExonTypeEmployee = new Quartet<Date, Date, String, String>();
					
					quarterCoeficcientEREFzaExonTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
					.setEndDate(r.get(CONTRACT_DATA.END_DATE))
					.setName(r.get(CONTRACT_DATA.NAME))
					.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
					
					contractCoefficientEREFzaExonDayTypeList.add(quarterCoeficcientEREFzaExonTypeEmployee);
				}
		
		// ------------- STRIKE_FACTOR
		Result<Record> coeficientsStrikeEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in(
						ContextVariable.STRIKE_FACTOR.getName()))
				.fetch();
	
		for(Record r: coeficientsStrikeEmployeeInfo){
			Quartet<Date, Date, String, String> quarterCoeficcientStrikeTypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterCoeficcientStrikeTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractCoefficientStrikeDayTypeList.add(quarterCoeficcientStrikeTypeEmployee);
		}
		
		// ------------- INACTIVITY_FACTOR
//		Result<Record> coeficientsInactivityEmployeeInfo = dslContext
//				.select()
//				.from(CONTRACT_DATA)
//				.where(CONTRACT_DATA.CONTRACT.eq(contract))
//				.and(CONTRACT_DATA.NAME.in(
//						"COEFICIENTE_INACTIVIDAD"))
//				.fetch();
//	
//		for(Record r: coeficientsInactivityEmployeeInfo){
//			Quartet<Date, Date, String, String> quarterCoeficcientInactivityTypeEmployee = new Quartet<Date, Date, String, String>();
//			
//			quarterCoeficcientInactivityTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
//			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
//			.setName(r.get(CONTRACT_DATA.NAME))
//			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
//			
//			contractCoefficientInactivityDayTypeList.add(quarterCoeficcientInactivityTypeEmployee);
//		}
		
		// ------------- DROP_FACTOR
		Result<Record> coeficientsDropEmployeeInfo = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.in(
						"COEFICIENTE_AUSENCIA"))
				.fetch();
	
		for(Record r: coeficientsDropEmployeeInfo){
			Quartet<Date, Date, String, String> quarterCoeficcientDropTypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterCoeficcientDropTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractCoefficientDropDayTypeList.add(quarterCoeficcientDropTypeEmployee);
		}
		
		// ------------- COEFICIETE_PARCIALIDAD
		Result<Record> partialityTypeRecord = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD"))
				.fetch();
	
		for(Record r: partialityTypeRecord){
			Quartet<Date, Date, String, String> quarterPartialityTypeEmployee = new Quartet<Date, Date, String, String>();
			
			Date endDate = r.get(CONTRACT_DATA.END_DATE);
			if(null == endDate) {
				java.util.Date lastDayOfYear = DateUtils.getLastDayOfYear(new java.util.Date());
				endDate = new Date(DateUtils.copyDateOnly(lastDayOfYear).getTime());
			}
			
			quarterPartialityTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(endDate)
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractPartialityDaysList.add(quarterPartialityTypeEmployee);
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
				
				if(nonWorkingDays != null)
					contractNonWorkingDaysList.clear();
					
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
		
		// -------------------------------------------- DIAS INACTIVIDAD ----------------------------------------------------------
		Result<Record> inactivityTypeRecord = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.eq("CAUSA_INACTIVIDAD"))
				.fetch();
		
		for(Record r: inactivityTypeRecord){
			Quartet<Date, Date, String, String> quarterInactivityTypeEmployee = new Quartet<Date, Date, String, String>();
			
			quarterInactivityTypeEmployee.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			contractInactivityDaysList.add(quarterInactivityTypeEmployee);
		}
		
		// -------------------------------------------- DIAS FESTIVOS LABORABLES --------------------------------------------------
		Result<Record> festiveWorkingRecords = dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.eq("FESTIVE_WORKING"))
				.fetch();
		
		for(Record r: festiveWorkingRecords) {
			festiveWorkingDays.add(r.get(CONTRACT_DATA.START_DATE));
		}
		
		// ------------------------------------------------ RESULTADO -------------------------------------------------------------
		
		employeeInfoCalendar = new EmployeeCalendarData(contractHoursList, contractExtraHoursList, contractDayTypesList, contractITDayTypeList, 
				contractFestiveDaysList, contractNonWorkingDaysList, fullTimeJourney, contractCoefficientEREDayTypeList, contractCoefficientEREFzaDayTypeList, 
				contractCoefficientEREFzaExonDayTypeList, contractCoefficientStrikeDayTypeList,
				/*contractCoefficientInactivityDayTypeList,*/ contractCoefficientDropDayTypeList,
				contractInactivityDaysList, contractPartialityDaysList, contractType, festiveWorkingDays);
		
		return employeeInfoCalendar;
	}

	// -------------- AUX METHODS GET EMPLOYEE INFO ------------
	
	private static ArrayList<String> getValueDayOfWeek(Result<Record> contractHoursEmployeeInfo, String dayOfWeek) {
//		if(contractHoursEmployeeInfo.isEmpty())
//			return null;
		
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
				|| "true" == tipoJornadaInfoEmpleado)
			return true;
		else
			return false;
	}
	
	private static Integer parseContractType(String journeyTypeEmployee) {
		if(null == journeyTypeEmployee)
			return -1;
		
		if(journeyTypeEmployee.contains("\"")){
			return Integer.parseInt(journeyTypeEmployee.split("\"")[1]);
		}
		return Integer.parseInt(journeyTypeEmployee);
	}

	
	@SuppressWarnings("deprecation")
	private static void setEmployeeInformation(DSLContext dslContext, Integer contract, EmployeeCalendarUpdate updateInfo) {
		
		// -------------------------------------------------- ACTUALIZACION HORAS ------------------------------------------------------
		
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
						.from(CONTRACT)
						.where(CONTRACT.ID.eq(contract))
						.fetchOne().value1();
		
		Boolean fullTimeEmployee = updateInfo.getFullTimeEmployee();
		
		Date realEndDate = dslContext.select(CONTRACT.END_DATE)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne().value1();
	
		Date realStartDate = dslContext.select(CONTRACT.START_DATE)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne().value1();
		
		if(updateInfo.isHasChangeHours() && !fullTimeEmployee) {
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
			
			
			
			HashMap<java.util.Date, Double> updateHoursMap = updateInfo.getDaysHourMap();
			
			
			if(!fullTimeEmployee){
				if (!updateHoursMap.isEmpty()){
					
					java.util.Date startDateHour = new java.util.Date();
					java.util.Date endDateHour = new java.util.Date();
					
					for (Entry<java.util.Date, Double> entry : updateHoursMap.entrySet()) {
						if (entry.getKey().before(startDateHour))
							startDateHour = DateUtils.copyDateOnly(entry.getKey());
						
						if (entry.getKey().after(endDateHour))
							endDateHour = DateUtils.copyDateOnly(entry.getKey());
					}
					
					if(startDateHour.before(realStartDate))
						startDateHour = DateUtils.copyDateOnly(realStartDate);
					
					System.out.println("StartDate :"+startDateHour+", endDate :"+endDateHour);
					System.out.println("UPDATE HOURS MAP SIZE :"+updateHoursMap.size());
					
					for (int i=0; i<7; i++){
						java.util.Date  date = DateUtils.copyDateOnly(startDateHour);
						DateUtils.addDays2Date(date, i);
			
						String dayOfWeek = calculateDayOfWeek(date.getDay());
	//					if(dayOfWeek == "HORAS_MIERCOLES")
	//						System.out.println("HORAS_MIERCOLES");
						
						java.util.Date auxstartDateHour = DateUtils.copyDateOnly(date);
						Double startHour = updateHoursMap.get(auxstartDateHour);
						
	//					if(null == startHour){
	//						System.out.println("FALLO");
	//					}
						
						while (date.before(endDateHour) && updateHoursMap.containsKey(date)){
							Double actualDateHour = updateHoursMap.get(date);
							if((startHour == null && actualDateHour != null) || (startHour != null && actualDateHour == null)){
								
								Date sqlstartDateHour = parseStartDateForStrech(auxstartDateHour);
								Date sqlendDateHour = parseEndDateForStrech(auxstartDateHour, date);
	//							Date sqlstartDateHour = new Date(auxstartDateHour.getTime());
	//							Date sqlendDateHour = new Date(date.getTime());
								if(startHour == null)
									dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
										CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
										CONTRACT_DATA.END_DATE)
										.values(domain, dayOfWeek, contract, null, 
												sqlstartDateHour, sqlendDateHour).execute();
								else
									dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
											CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
											CONTRACT_DATA.END_DATE)
											.values(domain, dayOfWeek, contract, Double.toString(startHour), 
													sqlstartDateHour, sqlendDateHour).execute();
								
								startHour = updateHoursMap.get(date);
								auxstartDateHour = DateUtils.copyDateOnly(date);
								
								DateUtils.addDays2Date(date, 7);
								continue;
								
							}else if(startHour != null && actualDateHour != null){
								
								if(startHour.doubleValue() != actualDateHour.doubleValue()){
								
									Date sqlstartDateHour = parseStartDateForStrech(auxstartDateHour);
									Date sqlendDateHour = parseEndDateForStrech(auxstartDateHour, date);
	//								Date sqlstartDateHour = new Date(auxstartDateHour.getTime());
	//								Date sqlendDateHour = new Date(date.getTime());
									
									dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
												CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
												CONTRACT_DATA.END_DATE)
												.values(domain, dayOfWeek, contract, Double.toString(startHour), 
														sqlstartDateHour, sqlendDateHour).execute();
								
									startHour = updateHoursMap.get(date);
									auxstartDateHour = DateUtils.copyDateOnly(date);
								}
							}
							
							DateUtils.addDays2Date(date, 7);
						}
						
						
	//					Date sqlstartDateHour =  new Date(auxstartDateHour.getTime());
						Date sqlstartDateHour = parseStartDateForStrechWithOutStreecht(auxstartDateHour, realStartDate);
						Date sqlendDateHour;
						
						if(realEndDate == null)
							sqlendDateHour = null;
						else{
							sqlendDateHour = new Date(realEndDate.getTime());
						}
						if(startHour == null)
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
								CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
								CONTRACT_DATA.END_DATE)
								.values(domain, dayOfWeek, contract, null,
										sqlstartDateHour, sqlendDateHour).execute();
						else
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, dayOfWeek, contract, Double.toString(startHour),
											sqlstartDateHour, sqlendDateHour).execute();
		
					}
				}
			}
		}else if(fullTimeEmployee) {
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
		}
		
		// ----------------------------------------------- ACTUALIZACION HORAS EXTRAS MENSUALES --------------------------------------------------
		
		dslContext.delete(CONTRACT_DATA)
				   .where(CONTRACT_DATA.CONTRACT.eq(contract))
				   .and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
				   .execute();
		
		List<Quartet<Date, Date, String, String>> updateExtraHoursList = updateInfo.getMonthExtraHoursList();
		
		if(fullTimeEmployee){
			if (!updateExtraHoursList.isEmpty()){
				
				for (Quartet<Date, Date, String, String> updateExtraHoursInfo : updateExtraHoursList){
					Date sqlstartDateHour = new Date(updateExtraHoursInfo.getStartDate().getTime());
					Date sqlendDateHour = new Date(updateExtraHoursInfo.getEndDate().getTime());
					
					dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
							CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
							CONTRACT_DATA.END_DATE)
							.values(domain, updateExtraHoursInfo.getName(), contract, updateExtraHoursInfo.getExpression(), 
									sqlstartDateHour, sqlendDateHour).execute();
				}
				
			}
		}
		
		// ----------------------------------------------- ACTUALIZACION TIPO DIAS -------------------------------------------------------
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				  ContextVariable.STRIKE_DAYS.getName()
				  ,ContextVariable.HOLIDAYS.getName()
				  ,ContextVariable.ERE_FACTOR.getName()
				  ,ContextVariable.STRIKE_FACTOR.getName()
				  ,"DIAS_INACTIVIDAD"
				  ,"NO_LABORABLE"
				  ,"CAUSA_INACTIVIDAD"
				  ,"DIAS_PARCIALIDAD"
				  ,"COEFICIENTE_PARCIALIDAD"
//				  ,"COEFICIENTE_INACTIVIDAD"
				  ,"COEFICIENTE_AUSENCIA"
				  ,"DIAS_AUSENCIA"
				  ,"PEONADAS"
				  ,"FESTIVE_WORKING"
				  // ERE TYPES
				  ,ContextVariable.ERE_DAYS.getName()
				  ,ContextVariable.ERE_FACTOR.getName()
				  ,"DIAS_ERE_FZA"
				  ,"COEFICIENTE_ERE_FZA"
				  ,"DIAS_ERE_FZA_EXONERADO"
				  ,"COEFICIENTE_ERE_FZA_EXONERADO"))
		   .execute();
		
		HashMap<java.util.Date, DayType> updateDaysTypeMap = updateInfo.getDaysTypeMap();
		Map<java.util.Date, Double> strikeDaysValues = updateInfo.getMapDaysCoefficientStrike();
		Map<java.util.Date, Double> ereDaysValues = updateInfo.getMapDaysCoefficientEre();
		Map<java.util.Date, Double> ereFzaDaysValues = updateInfo.getMapDaysCoefficientEreFza();
		Map<java.util.Date, Double> ereFzaExonDaysValues = updateInfo.getMapDaysCoefficientEreFzaExon();
//		Map<java.util.Date, Double> inactivityCoeficientDaysValues = updateInfo.getMapDaysCoefficientInactivity();
		Map<java.util.Date, Double> dropDaysValues = updateInfo.getMapDaysCoefficientDrop();
		Map<java.util.Date, String> inactivityDaysValues = updateInfo.getMapInactivityDays();
		Map<java.util.Date, String> partialityDaysValues = updateInfo.getMapPartialityDays();
		
		java.util.Date startDateType = new java.util.Date();
		java.util.Date endDateType = new java.util.Date();
		
		for (Entry<java.util.Date, DayType> entry : updateDaysTypeMap.entrySet()) {
			if (entry.getKey().before(startDateType))
				startDateType = DateUtils.copyDateOnly(entry.getKey());
			
			if (entry.getKey().after(endDateType))
				endDateType = DateUtils.copyDateOnly(entry.getKey());
		}
		
		if(null != realEndDate && endDateType.after(realEndDate)) {
			endDateType = DateUtils.copyDateOnly(realEndDate);
			DateUtils.addDays2Date(endDateType, 1);
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
						
						if(dayType.equals("DIAS_HUELGA")){
							String coeficiente = strikeDaysValues.get(auxStartDateType).toString();
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
										CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
										CONTRACT_DATA.END_DATE)
										.values(domain, "COEFICIENTE_HUELGA", contract, coeficiente, 
												sqlStartDateType, sqlEndDateType).execute();
						}else if(dayType.equals("DIAS_ERE")){
							String coeficiente = ereDaysValues.get(auxStartDateType).toString();
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, "COEFICIENTE_ERE", contract, coeficiente, 
											sqlStartDateType, sqlEndDateType).execute();
						}else if(dayType.equals("DIAS_ERE_FZA")){
							String coeficiente = ereFzaDaysValues.get(auxStartDateType).toString();
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, "COEFICIENTE_ERE_FZA", contract, coeficiente, 
											sqlStartDateType, sqlEndDateType).execute();
						}else if(dayType.equals("DIAS_ERE_FZA_EXONERADO")){
							String coeficiente = ereFzaExonDaysValues.get(auxStartDateType).toString();
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, "COEFICIENTE_ERE_FZA_EXONERADO", contract, coeficiente, 
											sqlStartDateType, sqlEndDateType).execute();
						}else if(dayType.equals("DIAS_INACTIVIDAD")){
							//Tipo inactividad
							String typeInactivity = inactivityDaysValues.get(auxStartDateType);
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, 
											"CAUSA_INACTIVIDAD", 
											contract, 
											String.format("\"%s\"",typeInactivity.toUpperCase()), 
											sqlStartDateType, 
											sqlEndDateType)
											.execute();
							// Coeficiente Inactividad
//							String coeficiente = inactivityCoeficientDaysValues.get(auxStartDateType).toString();
//							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
//									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
//									CONTRACT_DATA.END_DATE)
//									.values(domain, "COEFICIENTE_INACTIVIDAD", contract, coeficiente, 
//											sqlStartDateType, sqlEndDateType).execute();
							
						}
//						else if(dayType.equals("DIAS_PARCIALIDAD")){
//							String partialityCoeficient = partialityDaysValues.get(auxStartDateType);
//							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
//									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
//									CONTRACT_DATA.END_DATE)
//									.values(domain, 
//											"COEFICIENTE_PARCIALIDAD", 
//											contract, 
//											partialityCoeficient, 
//											sqlStartDateType, 
//											sqlEndDateType)
//											.execute();
//							
//						}
						else if(dayType.equals("DIAS_AUSENCIA")){
							String coeficiente = dropDaysValues.get(auxStartDateType).toString();
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, "COEFICIENTE_AUSENCIA", contract, coeficiente, 
											sqlStartDateType, sqlEndDateType).execute();
						}
						
						if(sqlStartDateType.getMonth() == sqlEndDateType.getMonth() && !dayType.equals("DIAS_PARCIALIDAD")){
							String expression = calculateExpression(DateUtils.copyDateOnly(auxStartDateType),
									DateUtils.copyDateOnly(javaEndDateType));
							
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
									CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
									CONTRACT_DATA.END_DATE)
									.values(domain, dayType, contract, expression, 
											sqlStartDateType, sqlEndDateType).execute();
						}else{
							createAndUpdateStrech(dslContext, domain, dayType, contract, sqlStartDateType, sqlEndDateType);
						}
						
						
					}
					
					dayType = calculateDayType(updateDaysTypeMap.get(dateType));
					startDayType = updateDaysTypeMap.get(dateType);
					auxStartDateType = DateUtils.copyDateOnly(dateType);
				}
			}
			
			DateUtils.addDays2Date(dateType, 1);
		}
		
		// Last Iteration
		if(validDayType(dayType)){
			
			Date sqlStartDateType = new Date(auxStartDateType.getTime());
			java.util.Date javaEndDateType = DateUtils.copyDateOnly(dateType);
			DateUtils.addDays2Date(javaEndDateType, -1);
			Date sqlEndDateType = new Date(javaEndDateType.getTime());
			
			if(dayType.equals("DIAS_HUELGA")){
				String coeficiente = strikeDaysValues.get(auxStartDateType).toString();
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
							CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
							CONTRACT_DATA.END_DATE)
							.values(domain, "COEFICIENTE_HUELGA", contract, coeficiente, 
									sqlStartDateType, sqlEndDateType).execute();
			}else if(dayType.equals("DIAS_ERE")){
				String coeficiente = ereDaysValues.get(auxStartDateType).toString();
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, "COEFICIENTE_ERE", contract, coeficiente, 
								sqlStartDateType, sqlEndDateType).execute();
			}else if(dayType.equals("DIAS_ERE_FZA")){
				String coeficiente = ereFzaDaysValues.get(auxStartDateType).toString();
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, "COEFICIENTE_ERE_FZA", contract, coeficiente, 
								sqlStartDateType, sqlEndDateType).execute();
			}else if(dayType.equals("DIAS_ERE_FZA_EXONERADO")){
				String coeficiente = ereFzaExonDaysValues.get(auxStartDateType).toString();
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, "COEFICIENTE_ERE_FZA_EXONERADO", contract, coeficiente, 
								sqlStartDateType, sqlEndDateType).execute();
			}else if(dayType.equals("DIAS_INACTIVIDAD")){
				String typeInactivity = inactivityDaysValues.get(auxStartDateType);
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, 
								"CAUSA_INACTIVIDAD", 
								contract, 
								String.format("\"%s\"",typeInactivity.toUpperCase()), 
								sqlStartDateType, 
								sqlEndDateType)
								.execute();
			}
//			else if(dayType.equals("DIAS_PARCIALIDAD")){
//				String partialityCoeficient = partialityDaysValues.get(auxStartDateType);
//				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
//						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
//						CONTRACT_DATA.END_DATE)
//						.values(domain, 
//								"COEFICIENTE_PARCIALIDAD", 
//								contract, 
//								partialityCoeficient, 
//								sqlStartDateType, 
//								sqlEndDateType)
//								.execute();
//				
//			}
			
			if(sqlStartDateType.getMonth() == sqlEndDateType.getMonth() && !dayType.equals("DIAS_PARCIALIDAD")){
				String expression = calculateExpression(DateUtils.copyDateOnly(auxStartDateType),
						DateUtils.copyDateOnly(javaEndDateType));
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, dayType, contract, expression, 
								sqlStartDateType, sqlEndDateType).execute();
			}else{
				createAndUpdateStrech(dslContext, domain, dayType, contract, sqlStartDateType, sqlEndDateType);
			}
			
		}
		
		creteRealJourneyDB(dslContext, contract);
		
		// ------------------------------------------- ACTUALIZACION COEFICIENTE PARCIALIDAD ----------------------------------------------
		Map<java.util.Date, String> partialityDaysMap = updateInfo.getMapPartialityDays();
		
		SortedMap<java.util.Date, String> partialityDaysSorted = new TreeMap<>();
		partialityDaysSorted.putAll(partialityDaysMap);
		
		if(!partialityDaysSorted.isEmpty()) {
			
			java.util.Date[] partilityDates = partialityDaysSorted.keySet().toArray(new java.util.Date[partialityDaysSorted.size()]);
			String[] partialityValues = partialityDaysSorted.values().toArray(new String[partialityDaysSorted.size()]);
			
			Date partialityStartDate = new Date(partilityDates[0].getTime());
			Date partialityEndDate = null;
			
			String partialityValue = partialityValues[0];
			
			for(int i=0 ; i < partialityValues.length - 1; i++) {
				
				if(partialityValue.equals(partialityValues[i])) {
					continue;
				}
				
				partialityEndDate = new Date(partilityDates[i-1].getTime());
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, 
								"COEFICIENTE_PARCIALIDAD", 
								contract, 
								partialityValue, 
								partialityStartDate, 
								partialityEndDate)
								.execute();
				
				int daysBetween = DateUtils.getDaysBetween(partialityStartDate, partialityEndDate);
				daysBetween++;
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
						CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
						CONTRACT_DATA.END_DATE)
						.values(domain, 
								"DIAS_PARCIALIDAD", 
								contract, 
								daysBetween+"", 
								partialityStartDate, 
								partialityEndDate)
								.execute();
				
				partialityStartDate = new Date(partilityDates[i].getTime());
				partialityEndDate = null;
				
				partialityValue = partialityValues[i];
			}
			
			// Last iteration
			partialityEndDate = new Date(partilityDates[partilityDates.length-1].getTime());
			
			dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
					CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
					CONTRACT_DATA.END_DATE)
					.values(domain, 
							"COEFICIENTE_PARCIALIDAD", 
							contract, 
							partialityValue, 
							partialityStartDate, 
							partialityEndDate)
							.execute();
			
			int daysBetween = DateUtils.getDaysBetween(partialityStartDate, partialityEndDate);
			daysBetween++;
			
			dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
					CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
					CONTRACT_DATA.END_DATE)
					.values(domain, 
							"DIAS_PARCIALIDAD", 
							contract, 
							daysBetween+"", 
							partialityStartDate, 
							partialityEndDate)
							.execute();
			
		}
		
		// ------------------------------------------- ACTUALIZACION FESTIVOS LABORABLES --------------------------------------------------
		ArrayList<java.util.Date> festiveWorkingDays = updateInfo.getFestiveWorkingDays();
		for(java.util.Date date: festiveWorkingDays) {
			dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
					CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
					CONTRACT_DATA.END_DATE)
					.values(domain, "FESTIVE_WORKING", contract, "WORK", 
							new Date(date.getTime()), new Date(date.getTime())).execute();
		}
		
	}

	private static void creteRealJourneyDB(DSLContext dslContext, Integer contract) {
		
		Result<Record> peonadasRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.NAME.eq("PEONADAS"))
				.fetch();
		
		ArrayList<Date> months = new ArrayList<>();
		
		if(!peonadasRecords.isEmpty()) {
			Integer domain = peonadasRecords.get(0).get(CONTRACT_DATA.DOMAIN);
			
			for(Record r : peonadasRecords) {
				addDateToList(r.get(CONTRACT_DATA.START_DATE), months);
			}
			
			if(!months.isEmpty()) {
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.and(CONTRACT_DATA.NAME.eq("JORNADAS_REALES"))
					.execute();
				
				for(Date date: months) {
					java.util.Date startDate = DateUtils.getFirstDayOfMonth(new Date(date.getYear(), date.getMonth(), date.getDate()));
					java.util.Date endDate = DateUtils.getLastDayOfMonth(new Date(date.getYear(), date.getMonth(), date.getDate()));
					
					Date sqlStartDate = new Date(startDate.getTime());
					Date sqlEndDate = new Date(endDate.getTime());
					
					Result<Record> peonadasMonthRecords = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contract))
							.and(CONTRACT_DATA.NAME.eq("PEONADAS"))
							.and(CONTRACT_DATA.START_DATE.ge(sqlStartDate))
							.and(CONTRACT_DATA.END_DATE.le(sqlEndDate))
							.fetch();
					
					if(!peonadasMonthRecords.isEmpty()) {
						Integer totalJourney = 0;
						
						for(Record rMonth : peonadasMonthRecords) {
							totalJourney += Integer.parseInt(rMonth.get(CONTRACT_DATA.EXPRESSION));
						}
						
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
								CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
								CONTRACT_DATA.END_DATE)
								.values(domain, "JORNADAS_REALES", contract, totalJourney.toString(), 
										sqlStartDate, sqlEndDate).execute();
					}
				}
			}
		}	
	}

	private static void addDateToList(Date date, ArrayList<Date> months) {
		Boolean add = true;
		for(Date d : months) {
			if(d.getMonth() == date.getMonth()) {
				add = false;
				break;
			}
		}
		
		if(add)
			months.add(date);
	}

	private static void createAndUpdateStrech(DSLContext dslContext, Integer domain, String dayType, Integer contract,
			Date sqlStartDateType, Date sqlEndDateType) {
		
		java.util.Date startDate = new java.util.Date(sqlStartDateType.getTime());
		java.util.Date endDate = new java.util.Date(sqlEndDateType.getTime());
		java.util.Date iterableDate = new java.util.Date();
		iterableDate = DateUtils.copyDateOnly(startDate);
		Integer expression = 0;
		
		while(startDate.getMonth() == iterableDate.getMonth()){
			DateUtils.addDays2Date(iterableDate, 1);
			expression++;
		}
		
		//Resto un día para volver al mes anterior
		DateUtils.addDays2Date(iterableDate, -1);
		Date sqlStartDate = new Date(startDate.getTime());
		Date sqlEndDate = new Date(iterableDate.getTime());
		
		dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
				CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
				CONTRACT_DATA.END_DATE)
				.values(domain, dayType, contract, expression.toString(), 
						sqlStartDate, sqlEndDate).execute();
		
		//Sumo un día para volver al primer día del mes siguiente
		expression = 0;
		DateUtils.addDays2Date(iterableDate, 1);
		startDate = DateUtils.copyDateOnly(iterableDate);
		
		while(!iterableDate.equals(endDate)){
			DateUtils.addDays2Date(iterableDate, 1);
			expression++;
		}
		expression++;
		
		sqlStartDate = new Date(startDate.getTime());
		sqlEndDate = new Date(endDate.getTime());
		
		dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME,
				CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, 
				CONTRACT_DATA.END_DATE)
				.values(domain, dayType, contract, expression.toString(), 
						sqlStartDate, sqlEndDate).execute();
		
		
	}

	private static Date parseEndDateForStrech(java.util.Date startDateAux, java.util.Date endDateAux) {
		java.util.Date date = DateUtils.copyDateOnly(endDateAux);
		
		if(/*DateUtils.getLastDayOfMonth(date).getDate() == date.getDate() || */date.getDay() == 0){
			return new Date(date.getTime());
		}else{
			while(date.getDay() != 0)
				DateUtils.addDays2Date(date, -1);
			return new Date(date.getTime());
			
//			if((startDateAux.getMonth() != endDateAux.getMonth() && startDateAux.getYear() == endDateAux.getYear()) ||
//			   (startDateAux.getMonth() == endDateAux.getMonth() && startDateAux.getYear() != endDateAux.getYear())){ 
//				// Si la fecha de fin esta en otro mes la ficha fin sera el ultimo dia del mes anterior
//				java.util.Date date2 = null;
//				if(endDateAux.getMonth() > 0)
//					date2 = new Date(endDateAux.getYear(), endDateAux.getMonth()-1, endDateAux.getDate());
//				else
//					date2 = new Date(endDateAux.getYear()-1, 11, endDateAux.getDate());
//				return new Date(DateUtils.getLastDayOfMonth(date2).getTime());
//			}
//			
//			if(startDateAux.getMonth() != endDateAux.getMonth() && startDateAux.getYear() != endDateAux.getYear()){
//				return new Date(DateUtils.getLastDayOfYear(startDateAux).getTime());
//			}
//			
//			java.util.Date staticDate = new java.util.Date(date.getYear(), date.getMonth()-1, date.getDate());
//			staticDate = DateUtils.getLastDayOfMonth(staticDate);
//			while(date.getDay() != 0){
//				DateUtils.addDays2Date(date, -1);
//				if(date.getDate() == staticDate.getDate())
//					break;
//				
//			}
//			return new Date(date.getTime());
		}
	}

	private static Date parseStartDateForStrech(java.util.Date dateAux) {
		java.util.Date date = DateUtils.copyDateOnly(dateAux);
		
		if(date.getDate() == 1 || date.getDay() == 1){
			return new Date(date.getTime());
		}else{
			while(!(date.getDate() == 1 || date.getDay() == 1)){
				DateUtils.addDays2Date(date, -1);
			}
			return new Date(date.getTime());
		}
	}
	
	private static Date parseStartDateForStrechWithOutStreecht(java.util.Date dateAux, Date realStartDate) {
		java.util.Date date = DateUtils.copyDateOnly(dateAux);
		
		if(date.getDate() == 1){
			return new Date(date.getTime());
		}else{
			while(!(date.getDate() == 1)){
				if(date.equals(realStartDate))
					return realStartDate;
				
				DateUtils.addDays2Date(date, -1);
			}
			return new Date(date.getTime());
		}
	}

	private static String calculateExpression(java.util.Date startDate, java.util.Date endDate) {
		int expression = 0;
		
		while(!startDate.equals(endDate)){
			expression++;
			DateUtils.addDays2Date(startDate, 1);
		}
		
		expression++;
		
		return Integer.toString(expression);
	}

	// ---------- CLASS AUX METHODS ----------
	
	private static boolean validDayType(String dayType) {
		return dayType.equals("DIAS_ERE") || dayType.equals("DIAS_HUELGA") || dayType.equals("NO_LABORABLE") 
				|| dayType.equals("DIAS_VACACIONES") || dayType.equals("DIAS_INACTIVIDAD") || dayType.equals("PEONADAS")
				|| dayType.equals("DIAS_AUSENCIA") || dayType.equals("DIAS_ERE_FZA") || dayType.equals("DIAS_ERE_FZA_EXONERADO");
				//|| dayType.equals("DIAS_PARCIALIDAD");
	}

	private static String calculateDayType(DayType dayType) {
		String result = "";
		switch (dayType) {
		case EREDAY:
			result = "DIAS_ERE";
			break;
		case DROPDAY:
			result = "DIAS_AUSENCIA";
			break;
		case STRIKEDAY:
			result = "DIAS_HUELGA";
			break;
		case HOLIDAY:
			result = "DIAS_VACACIONES";
			break;
		case INACTIVITY:
			result = "DIAS_INACTIVIDAD";
			break;
		case PEONADAS:
			result = "PEONADAS";
			break;
		case NOWORKINGDAY:
			result = "NO_LABORABLE";
			break;
//		case PARTIALITY:
//			result = "DIAS_PARCIALIDAD";
//			break;
		case EREFZADAY:
			result = "DIAS_ERE_FZA";
			break;
		case EREFZAEXONDAY:
			result = "DIAS_ERE_FZA_EXONERADO";
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
