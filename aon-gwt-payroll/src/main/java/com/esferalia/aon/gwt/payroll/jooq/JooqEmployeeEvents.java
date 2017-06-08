package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEmployeeEvents {

	private static Settings SETTINGS = null;

	public static EmployeeEventsData getEmployeeEvents(Connection conn, Integer contract) throws IllegalArgumentException {
		return getEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static void setEmployeeEvents(Connection conn, Integer contract, EmployeeEventsUpdate updateInfo){
		setEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static EmployeeEventsData getEmployeeEventsInformation(DSLContext dslContext, Integer contract) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		
		Map<String,ArrayList<Quartet<Date, Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<Date, Date, String, String>>>(); 
		ArrayList<Quartet<Date, Date, String, String>> workedDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> completedDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> ereDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> strikeDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> absenceDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> workedHoursVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> additionalHoursVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> pecnortaDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> maintenanceDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> pecnortaForeignDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> maintenanceForeignDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> kmsVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> holidayDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> realWorkDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> extraHoursVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> extraHoursFZAVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		
		
		
		// ---------------------------------------------- DIAS TRABAJADOS ---------------------------------------------------------
		
		Result<Record> contractWorkedDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.WORKED_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractWorkedDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterWorkedDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterWorkedDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			workedDaysVaribaleList.add(quarterWorkedDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_TRABAJADAS", workedDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS EFECTIVOS ---------------------------------------------------------
		
		Result<Record> contractCompletedDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_EFECTIVOS"))
				  .fetch();
		
		for(Record r: contractCompletedDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterCompletedDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterCompletedDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			completedDaysVaribaleList.add(quarterCompletedDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_EFECTIVOS", completedDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS ERE ---------------------------------------------------------
		
		Result<Record> contractEREDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.ERE_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractEREDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterEREDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterEREDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			ereDaysVaribaleList.add(quarterEREDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_ERE", ereDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS HUELGA ---------------------------------------------------------
		
		Result<Record> contractStrikeDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.STRIKE_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractStrikeDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterStrikeDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterStrikeDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			strikeDaysVaribaleList.add(quarterStrikeDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_HUELGA", strikeDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS AUSENCIA ---------------------------------------------------------
		
		Result<Record> contractAbsenceDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.LEAVE_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractAbsenceDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterAbsenceDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterAbsenceDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			absenceDaysVaribaleList.add(quarterAbsenceDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_AUSENCIA", absenceDaysVaribaleList);
		
		
		// ---------------------------------------------- HORAS TRABAJADAS ---------------------------------------------------------
		
		Result<Record> contractWorkedHoursEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.WORKED_HOURS.getName()))
				  .fetch();
		
		for(Record r: contractWorkedHoursEmployeeInfo){
			Quartet<Date, Date, String, String> quarterWorkedHoursEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterWorkedHoursEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			workedHoursVaribaleList.add(quarterWorkedHoursEmployeeInfo);
		}
		
		employeeVariablesEvents.put("HORAS_TRABAJADAS", workedHoursVaribaleList);
		
		
		// ---------------------------------------------- HORAS COMPLEMENTARIAS ---------------------------------------------------------
		
		Result<Record> contractAdditionalHoursEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("HORAS_COMPLEMENTARIAS"))
				  .fetch();
		
		for(Record r: contractAdditionalHoursEmployeeInfo){
			Quartet<Date, Date, String, String> quarterAdditionalHoursEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterAdditionalHoursEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			additionalHoursVaribaleList.add(quarterAdditionalHoursEmployeeInfo);
		}
		
		employeeVariablesEvents.put("HORAS_COMPLEMENTARIAS", additionalHoursVaribaleList);
		
		
		// ---------------------------------------------- DIAS PECNORTA ---------------------------------------------------------
		
		Result<Record> contractPecnortaDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA"))
				  .fetch();
		
		for(Record r: contractPecnortaDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterPecnortaDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterPecnortaDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			pecnortaDaysVaribaleList.add(quarterPecnortaDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_PECNORTA", pecnortaDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS MANUTENCION ---------------------------------------------------------
		
		Result<Record> contractMaintenanceDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION"))
				  .fetch();
		
		for(Record r: contractMaintenanceDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterMaintenanceDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterMaintenanceDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			maintenanceDaysVaribaleList.add(quarterMaintenanceDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_MANUTENCION", maintenanceDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS PECNORTA EXTRANJERO ---------------------------------------------------------
		
		Result<Record> contractPecnortaForeignDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA_EXTRANJERO"))
				  .fetch();
		
		for(Record r: contractPecnortaForeignDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterPecnortaForeignDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterPecnortaForeignDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			pecnortaForeignDaysVaribaleList.add(quarterPecnortaForeignDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_PECNORTA_EXTRANJERO", pecnortaForeignDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS MANUTENCION EXTRANJERO ---------------------------------------------------------
		
		Result<Record> contractMaintenanceForeignDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION_EXTRANJERO"))
				  .fetch();
		
		for(Record r: contractMaintenanceForeignDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterMaintenanceForeignDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterMaintenanceForeignDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			maintenanceForeignDaysVaribaleList.add(quarterMaintenanceForeignDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_MANUTENCION_EXTRANJERO", maintenanceForeignDaysVaribaleList);
		
		
		// ------------------------------------------------- KMS ----------------------------------------------------------
		
		Result<Record> kmsEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("KMS"))
				  .fetch();
		
		for(Record r: kmsEmployeeInfo){
			Quartet<Date, Date, String, String> quarterKmsEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterKmsEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			kmsVaribaleList.add(quarterKmsEmployeeInfo);
		}
		
		employeeVariablesEvents.put("KMS", kmsVaribaleList);
		
		
		// ---------------------------------------------- DIAS VACACIONES ------------------------------------------------------
		
		Result<Record> contractHolidaysDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
				  .fetch();
		
		for(Record r: contractHolidaysDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterHolidaysDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterHolidaysDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			holidayDaysVaribaleList.add(quarterHolidaysDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_VACACIONES", holidayDaysVaribaleList);
		
		
		// ---------------------------------------------- JORNADAS REALES ------------------------------------------------------
		
		Result<Record> contractRealWorkDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.REAL_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractRealWorkDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterRealWorkDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterRealWorkDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			realWorkDaysVaribaleList.add(quarterRealWorkDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("JORNADAS_REALES", realWorkDaysVaribaleList);
		
		
		// ---------------------------------------------- HORAS EXTRAS ------------------------------------------------------
		
		Result<Record> contractExtraHoursEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.EXTRA_HOURS.getName()))
				  .fetch();
		
		for(Record r: contractExtraHoursEmployeeInfo){
			Quartet<Date, Date, String, String> quarterExtraHoursEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterExtraHoursEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			extraHoursVaribaleList.add(quarterExtraHoursEmployeeInfo);
		}
		
		employeeVariablesEvents.put("HORAS_EXTRAS", extraHoursVaribaleList);
		
		
		// ---------------------------------------------- HORAS EXTRAS FZA ------------------------------------------------------
		
		Result<Record> contractExtraHoursFZAEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS_FZA"))
				  .fetch();
		
		for(Record r: contractExtraHoursFZAEmployeeInfo){
			Quartet<Date, Date, String, String> quarterExtraHoursFZAEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterExtraHoursFZAEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			extraHoursFZAVaribaleList.add(quarterExtraHoursFZAEmployeeInfo);
		}
		
		employeeVariablesEvents.put("HORAS_EXTRAS_FZA", extraHoursFZAVaribaleList);
		

		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		
		return employeeInfoVariablesEvents;
	
	}
	
	private static void setEmployeeEventsInformation(DSLContext dslContext, Integer contract,
			EmployeeEventsUpdate updateInfo) {
		
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne().value1();
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				   ContextVariable.WORKED_DAYS.getName()
				  ,ContextVariable.ERE_DAYS.getName()
				  ,ContextVariable.STRIKE_DAYS.getName()
				  ,ContextVariable.LEAVE_DAYS.getName()
				  ,ContextVariable.WORKED_HOURS.getName()
				  ,ContextVariable.REAL_DAYS.getName()
				  ,ContextVariable.HOLIDAYS.getName()
				  ,ContextVariable.EXTRA_HOURS.getName())
				.or(CONTRACT_DATA.NAME.eq("DIAS_EFECTIVOS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_COMPLEMENTARIAS"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA_EXTRANJERO"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION_EXTRANJERO"))
				.or(CONTRACT_DATA.NAME.eq("KMS"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS_FZA"))
				)
		   .execute();
		
		List<Quartet<Date, Date, String, String>> updateList = updateInfo.getVariableEventsList();
		
		for (Quartet<Date, Date, String, String> quartet : updateList){
			
			dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
					CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
					.values(domain, quartet.getName(), contract, quartet.getExpression(), 
							quartet.getStartDate(), quartet.getEndDate())
					.execute();
		}
		
	}

}
