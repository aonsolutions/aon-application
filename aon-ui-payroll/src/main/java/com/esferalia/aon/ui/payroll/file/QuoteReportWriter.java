package com.esferalia.aon.ui.payroll.file;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryExpenseController;


public class QuoteReportWriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryExpenseController.class.getName());
	
	private static List<Integer> salaryList;
	
	private static Map<String, Object> salaryConceptsMap;
	
	
	public void buildFANReport(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month month) throws ManagerBeanException {
		
	}
	
	public void buildFANReport(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		
	}
	
	private ReportMetadata getConceptColumnMetadata() throws ReportException {
		
		ReportMetadata metadata = new ReportMetadata();
		// salary
		metadata.getColumns().add(new ReportColumnMetadata("salary",Types.INTEGER,"cod. nomina",5));
		metadata.getColumns().add(new ReportColumnMetadata("ccc",Types.INTEGER,"cod ccc",5));
		metadata.getColumns().add(new ReportColumnMetadata("employee",Types.VARCHAR,"Trabajador",40));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"Remuneracion",10));
		// salary_deduction
		metadata.getColumns().add(new ReportColumnMetadata("CGC",Types.DOUBLE,"Cont. comunes",10));
		metadata.getColumns().add(new ReportColumnMetadata("DESMPL",Types.DOUBLE,"Desempleo",10));
		metadata.getColumns().add(new ReportColumnMetadata("FP",Types.DOUBLE,"F.P.",10));
		metadata.getColumns().add(new ReportColumnMetadata("EXTR",Types.DOUBLE,"H. Extra",10));
		metadata.getColumns().add(new ReportColumnMetadata("NEXTR",Types.DOUBLE,"H. Extra no estruc.",10));
		metadata.getColumns().add(new ReportColumnMetadata("IRPF",Types.DOUBLE,"I.R.P.F.",10));
		// salary_cost
		metadata.getColumns().add(new ReportColumnMetadata("CGC_E",Types.DOUBLE,"Cont. comunes (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("CGC_E_TEMP",Types.DOUBLE,"Inferior a 7 dias (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("EXTR_E",Types.DOUBLE,"H.Extra (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("NEXTR_E",Types.DOUBLE,"H.Extra no estruc (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("IT_E",Types.DOUBLE,"I.T.",10));
		metadata.getColumns().add(new ReportColumnMetadata("IMS_E",Types.DOUBLE,"I.M.S.",10));
		metadata.getColumns().add(new ReportColumnMetadata("FP_E",Types.DOUBLE,"F.P. (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("FOGASA_E",Types.DOUBLE,"FOGASA",10));
		metadata.getColumns().add(new ReportColumnMetadata("DESMPL_E",Types.DOUBLE,"Desempleo (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata("ATEP_E",Types.DOUBLE,"IT A.T. o E.P.",10));
		metadata.getColumns().add(new ReportColumnMetadata("ECSS_E",Types.DOUBLE,"IT Enfermedad comun",10));
				
		return metadata;
	}

	private ReportMetadata getContractColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("",Types.VARCHAR,"",20));
		metadata.getColumns().add(new ReportColumnMetadata("Total",Types.DOUBLE,"Total",15));
		for(Integer salaryId: salaryList){
			metadata.getColumns().add(new ReportColumnMetadata((String) salaryConceptsMap.get("EMPLOYEE_NAME_"+salaryId),Types.DOUBLE,(String) salaryConceptsMap.get("EMPLOYEE_NAME_"+salaryId),20));
		}
		return metadata;
	}
	
	public void excelReport(Integer year, Month month, Integer domain, List<Integer> ccc, OutputStream output) throws IOException, ReportException, AonConnectionException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			initSalaryContext(conn, year, month, domain, ccc);
			
			CustomExcelReportExporter exporter = new CustomExcelReportExporter();
			
			exporter.startExport("Conceptos");
			ReportMetadata contractColumnMetadata = getContractColumnMetadata();
			exporter.exportHeader(contractColumnMetadata);
			excelReportByContract(exporter, contractColumnMetadata);	
	
			exporter.startSheet("Trabajadores");
			ReportMetadata conceptColumnMetadata = getConceptColumnMetadata();
			exporter.exportHeader(conceptColumnMetadata);
			excelReportByConcept(exporter, conceptColumnMetadata);	
			
			exporter.endExport(output);
			output.flush();
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}

	}
	
	private static void initSalaryContext(Connection conn, Integer year, Month month, Integer domain, List<Integer> cccIds) throws AonConnectionException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month.getValue());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = cal.getTime();
		
		Result<Record> salaryRecords = (Result<Record>) getSalaryRecords(conn, startDate, endDate, domain, cccIds);

//		Result<Record> salaryDeductionHeaders = (Result<Record>) getSalaryDeductionRecords(year, month, domain, null, true);
//		Result<Record> salaryCostHeaders = (Result<Record>) getSalaryCostRecords(conn, startDate, endDate, domain, null, true);

//		Result<Record> salaryPaymentRecords = (Result<Record>) getSalaryPaymentRecords(conn, startDate, endDate, domain, cccIds, false);
		Result<Record> salaryDeductionRecords = (Result<Record>) getSalaryDeductionRecords(conn, startDate, endDate, domain, cccIds, false);
		Result<Record> salaryCostRecords = (Result<Record>) getSalaryCostRecords(conn, startDate, endDate, domain, cccIds, false);
//		Result<Record> salaryBonusRecords = (Result<Record>)
//		Result<Record> salaryEmbargoRecords = (Result<Record>)
		
		salaryList = new LinkedList<Integer>();
		salaryConceptsMap = new HashMap<String, Object>();
	
		for (Record salaryDeduction : salaryDeductionRecords) {
			Integer salaryId = salaryDeduction.getValue(SALARY_DEDUCTION.SALARY);
			String concept = salaryDeduction.getValue(SALARY_DEDUCTION.DEDUCTION_CONCEPT);
			Double amount = salaryDeduction.getValue(SALARY_DEDUCTION.AMOUNT);
			salaryConceptsMap.put(concept + "_" + salaryId, amount);
		}

		for (Record salaryCost : salaryCostRecords) {
			Integer salaryId = salaryCost.getValue(SALARY_COST.SALARY);
			String concept = salaryCost.getValue(SALARY_COST.COST_CONCEPT);
			Double amount = salaryCost.getValue(SALARY_COST.AMOUNT);
			salaryConceptsMap.put(concept + "_" + salaryId, amount);
		}

		for (Record salary : salaryRecords) {
			Integer salaryId = salary.getValue(SALARY.ID);
			salaryList.add(salaryId);
			salaryConceptsMap.put("ENTERPRISE_CCC_" + salaryId,salary.getValue(CONTRACT.ENTERPRISE_CCC));
			salaryConceptsMap.put("EMPLOYEE_NAME_" + salaryId,salary.getValue(SALARY.EMPLOYEE_NAME));
			salaryConceptsMap.put("REMUNERATION_" + salaryId,salary.getValue(SALARY.REMUNERATION));
			salaryConceptsMap.put("SOCIAL_SECURITY_CONTRIBUTIONS_" + salaryId,salary.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
			salaryConceptsMap.put("TOTAL_IRPF_" + salaryId,salary.getValue(SALARY.TOTAL_IRPF));
			salaryConceptsMap.put("TOTAL_PAYMENT_" + salaryId,salary.getValue(SALARY.TOTAL_PAYMENT));
			salaryConceptsMap.put("TOTAL_DEDUCTION_" + salaryId,salary.getValue(SALARY.TOTAL_DEDUCTION));
			salaryConceptsMap.put("TOTAL_LIQUID_" + salaryId,salary.getValue(SALARY.TOTAL_LIQUID));
			salaryConceptsMap.put("TOTAL_ENTERPRISE_" + salaryId,salary.getValue(SALARY.TOTAL_ENTERPRISE));
			salaryConceptsMap.put("PRO_EXT_BASE_" + salaryId,salary.getValue(SALARY.PRO_EXT_BASE));
			salaryConceptsMap.put("IT_BASE_" + salaryId,salary.getValue(SALARY.IT_BASE));
			salaryConceptsMap.put("RAW_CGC_BASE_" + salaryId,salary.getValue(SALARY.RAW_CGC_BASE));
			salaryConceptsMap.put("CGC_BASE_" + salaryId,salary.getValue(SALARY.CGC_BASE));
			salaryConceptsMap.put("HEXTRA_BASE_" + salaryId,salary.getValue(SALARY.HEXTRA_BASE));
			salaryConceptsMap.put("NON_HEXTRA_BASE_" + salaryId,salary.getValue(SALARY.NON_HEXTRA_BASE));
			salaryConceptsMap.put("CGP_BASE_" + salaryId,salary.getValue(SALARY.CGP_BASE));
			salaryConceptsMap.put("MONEY_IRPF_BASE_" + salaryId,salary.getValue(SALARY.MONEY_IRPF_BASE));
			salaryConceptsMap.put("INKIND_IRPF_BASE_" + salaryId,salary.getValue(SALARY.INKIND_IRPF_BASE));
			salaryConceptsMap.put("IRPF_BASE_" + salaryId,salary.getValue(SALARY.IRPF_BASE));
		}
		
	}

	public void excelReportByContract(CustomExcelReportExporter exporter, ReportMetadata metadata) throws IOException, ReportException, AonConnectionException {
		
		// ****************************
		// salary_payment
		// ****************************
//		exporter.startLine();
//		exportSalaryAllColumns(exporter, metadata, "PAYMENT");
		
		// ****************************
		// salary_deduction
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, "CGC", "Cont. comunes");
		exportSalaryAllColumns(exporter, metadata, "DESMPL", "Desempleo");
		exportSalaryAllColumns(exporter, metadata, "FP", "F.P.");
		exportSalaryAllColumns(exporter, metadata, "EXTR", "H. Extra");
		exportSalaryAllColumns(exporter, metadata, "NEXTR", "H. Extra no estruc.");
		exportSalaryAllColumns(exporter, metadata, "IRPF", "I.R.P.F.");
		
		// ****************************
		// salary_cost
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, "CGC_E", "Cont. comunes (empresa)");
		exportSalaryAllColumns(exporter, metadata, "CGC_E_TEMP", "Inferior a 7 dias (empresa)");
		exportSalaryAllColumns(exporter, metadata, "EXTR_E", "H.Extra (empresa)");
		exportSalaryAllColumns(exporter, metadata, "NEXTR_E", "H.Extra no estruc (empresa)");
		exportSalaryAllColumns(exporter, metadata, "IT_E", "I.T.");
		exportSalaryAllColumns(exporter, metadata, "IMS_E", "I.M.S.");
		exportSalaryAllColumns(exporter, metadata, "FP_E", "F.P. (empresa)");
		exportSalaryAllColumns(exporter, metadata, "FOGASA_E", "FOGASA");
		exportSalaryAllColumns(exporter, metadata, "DESMPL_E",  "Desempleo (empresa)");
		exportSalaryAllColumns(exporter, metadata, "ATEP_E", "IT A.T. o E.P.");
		exportSalaryAllColumns(exporter, metadata, "ECSS_E", "IT Enfermedad comun");
		
		// ****************************
		// salary_bonus
		// ****************************
//		exporter.startLine();
//		exportSalaryAllColumns(exporter, metadata, "BONUS");
		
		// ****************************
		// salary_embargo
		// ****************************
//		exporter.startLine();
//		exportSalaryAllColumns(exporter, metadata, "EMBARGO");
		
	}
	
	private void exportSalaryAllColumns(CustomExcelReportExporter exporter, ReportMetadata metadata, String key, String description) throws ReportException{
		Double total = 0.0;
		int column = 0;
		exporter.startLine();
		exporter.exportColumn(metadata.getColumns().get(column++), description);
		for(Integer salaryId: salaryList){
			try {
				Object value = salaryConceptsMap.get(key+"_"+salaryId);
				if(value!=null && NumberUtils.isNumber(value.toString())){
					total += (Double) value;
				}
			} catch (Exception e) {
				LOGGER.error("error al totalizar el valor de " + key+"_"+salaryId);
			}
		}
		exporter.exportColumn(metadata.getColumns().get(column++), total);
		for(Integer salaryId: salaryList){
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(key+"_"+salaryId));
		}
	}
	
	public void excelReportByConcept(CustomExcelReportExporter exporter, ReportMetadata metadata) throws IOException, ReportException, AonConnectionException {
		for (Integer salary : salaryList) {
			int column = 0;
			exporter.startLine();
			// salary
			exporter.exportColumn(metadata.getColumns().get(column++), salary);
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("ENTERPRISE_CCC_"+ salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("EMPLOYEE_NAME_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("REMUNERATION_" + salary));
			// salary_deduction
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("CGC_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("DESMPL_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("FP_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("EXTR_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("NEXTR_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("IRPF_" + salary));
			// salary_cost
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("CGC_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("CGC_E_TEMP_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("EXTR_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("NEXTR_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("IT_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("IMS_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("FP_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("FOGASA_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("DESMPL_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("ATEP_E_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get("ECSS_E_" + salary));
		}
		exporter.startLine();
	}

	// /////////////////////////
	// SQL
	// /////////////////////////

	public static Result<?> getSalaryRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> ccc) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY);
		query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY.CCC, SALARY.EMPLOYEE_NAME);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (ccc != null && !ccc.isEmpty()) {
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(ccc));
		}
		return query.fetch();
	}

	public static Result<?> getSalaryPaymentRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, boolean onlyHeaders) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY_PAYMENT);
		query.addJoin(SALARY, JoinType.LEFT_OUTER_JOIN, SALARY_PAYMENT.SALARY.equal(SALARY.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY_PAYMENT.SALARY, SALARY_PAYMENT.PAYMENT_CONCEPT);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		if (onlyHeaders) {
			query.addSelect(SALARY_PAYMENT.PAYMENT_CONCEPT);
			query.addGroupBy(SALARY_PAYMENT.PAYMENT_CONCEPT);
		}
		return query.fetch();
	}

	public static Result<?> getSalaryDeductionRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, boolean onlyHeaders) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY_DEDUCTION);
		query.addJoin(SALARY, JoinType.LEFT_OUTER_JOIN, SALARY_DEDUCTION.SALARY.equal(SALARY.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY_DEDUCTION.SALARY, SALARY_DEDUCTION.DEDUCTION_CONCEPT);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		if (onlyHeaders) {
			query.addSelect(SALARY_DEDUCTION.DEDUCTION_CONCEPT);
			query.addGroupBy(SALARY_DEDUCTION.DEDUCTION_CONCEPT);
		}
		return query.fetch();
	}

	public static Result<?> getSalaryCostRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, boolean onlyHeaders) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY_COST);
		query.addJoin(SALARY, JoinType.LEFT_OUTER_JOIN, SALARY_COST.SALARY.equal(SALARY.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY_COST.SALARY, SALARY_COST.COST_CONCEPT);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		if (onlyHeaders) {
			query.addSelect(SALARY_COST.COST_CONCEPT);
			query.addGroupBy(SALARY_COST.COST_CONCEPT);
		}
		return query.fetch();
	}
	
	public Result<?> getSalaryBonusRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY_BONUS);
		query.addJoin(SALARY, JoinType.LEFT_OUTER_JOIN, SALARY_BONUS.SALARY.equal(SALARY.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY_BONUS.SALARY);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		return query.fetch();
	}
	
	public Result<?> getSalaryEmbargoRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, Integer enterpriseCCCId) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY_EMBARGO);
		query.addJoin(SALARY, JoinType.LEFT_OUTER_JOIN, SALARY_EMBARGO.SALARY.equal(SALARY.ID));
		query.addConditions(SALARY.END_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.END_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY_EMBARGO.SALARY);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (enterpriseCCCId != null) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.equal(enterpriseCCCId));
		}
		return query.fetch();
	}
	
	private static Settings getDefaultSettings() {
		Settings SETTINGS = new Settings();
		SETTINGS.setRenderSchema(false);
		return SETTINGS;
	}

	public static void main(String[] args) {
		int year = 2014;
		int month = Calendar.AUGUST;
		// int domain = 1051;
		int domain = 1662;
		int ccc = 174;
//		String domainName = AonUtil.getDomainName();
//		String domainName = "equipu.esferalia.net";
		String domainName = "escolaglobal-equipu.esferalia.net";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = cal.getTime();
		
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(domainName);
		
//			Result<Record> salaryRecords = (Result<Record>)
//			getSalaryRecords(year, month, domain, 174);
			Result<Record> salaryRecords = (Result<Record>) getSalaryRecords(connection, startDate, endDate, domain, null);

//			Result<Record> salaryDeductionHeaders = (Result<Record>) getSalaryDeductionRecords(startDate, endDate, domain, null, true);
//			Result<Record> salaryCostHeaders = (Result<Record>) getSalaryCostRecords(startDate, endDate, domain, null, true);

//			Result<Record> salaryPaymentRecords = (Result<Record>)
//			getSalaryPaymentRecords(year, month, domain, 174);
			Result<Record> salaryDeductionRecords = (Result<Record>) getSalaryDeductionRecords(connection, startDate, endDate, domain, null, false);
			Result<Record> salaryCostRecords = (Result<Record>) getSalaryCostRecords(connection, startDate, endDate, domain, null, false);
//			Result<Record> salaryBonusRecords = (Result<Record>)
//			getSalaryCostRecords(year, month, domain, 174);
//			Result<Record> salaryEmbargoRecords = (Result<Record>)
//			getSalaryCostRecords(year, month, domain, 174);

			salaryList = new LinkedList<Integer>();
			salaryConceptsMap = new HashMap<String, Object>();
			
			System.out.println(" *** Inicio de proceso");
		
			initSalaryContext(connection, year, Month.getMonthByValue(month), domain, null);

			// salary
			System.out.print(completeLenght("salary", 10) + " | ");
			System.out.print(completeLenght("ccc", 5) + " | ");
			System.out.print(completeLenght("employee", 50) + " | ");
			System.out.print(completeLenght("remuneration", 20) + " | ");
			// salary_deduction
			System.out.print(completeLenght("CGC", 10) + " | ");
			System.out.print(completeLenght("DESMPL", 10) + " | ");
			System.out.print(completeLenght("FP", 10) + " | ");
			System.out.print(completeLenght("EXTR", 10) + " | ");
			System.out.print(completeLenght("NEXTR", 10) + " | ");
			System.out.print(completeLenght("IRPF", 10) + " | ");
			// salary_cost
			System.out.print(completeLenght("ATEP_E", 10) + " | ");
			System.out.print(completeLenght("CGC_E", 10) + " | ");
			System.out.print(completeLenght("CGC_E_TEMP", 10) + " | ");
			System.out.print(completeLenght("DESMPL_E", 10) + " | ");
			System.out.print(completeLenght("ECSS_E", 10) + " | ");
			System.out.print(completeLenght("EXTR_E", 10) + " | ");
			System.out.print(completeLenght("FOGASA_E", 10) + " | ");
			System.out.print(completeLenght("FP_E", 10) + " | ");
			System.out.print(completeLenght("IMS_E", 10) + " | ");
			System.out.print(completeLenght("IT_E", 10) + " | ");
			System.out.print(completeLenght("NEXTR_E", 10) + " | ");
			System.out.println("");
	
			for (Integer salary : salaryList) {
	
				// salary
				System.out.print(completeLenght(salary, 10) + " | ");
				System.out.print(completeLenght((Integer) salaryConceptsMap.get("ENTERPRISE_CCC_"+ salary), 5)+ " | ");
				System.out.print(completeLenght((String) salaryConceptsMap.get("EMPLOYEE_NAME_" + salary),50) + " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("REMUNERATION_" + salary),20) + " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("CGC_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("DESMPL_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("FP_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("EXTR_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("NEXTR_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("IRPF_" + salary), 10)+ " | ");
				// salary_cost
				System.out.print(completeLenght((Double) salaryConceptsMap.get("ATEP_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("CGC_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("CGC_E_TEMP_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("DESMPL_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("ECSS_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("EXTR_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("FOGASA_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("FP_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("IMS_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("IT_E_" + salary), 10)+ " | ");
				System.out.print(completeLenght((Double) salaryConceptsMap.get("NEXTR_E_" + salary), 10)+ " | ");
	
				System.out.println("");
	
			}
	
			System.out.println("");
			System.out.println(" *** FIN de proceso");
		
			} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	private static String completeLenght(Double value, int lenght) {
		return completeLenght(value != null ? value.toString() : null, lenght);
	}

	private static String completeLenght(Integer value, int lenght) {
		return completeLenght(value != null ? value.toString() : null, lenght);
	}

	private static String completeLenght(String value, int lenght) {
		value = value == null ? "" : value;
		while (value.length() < lenght) {
			value = " " + value;
		}
		return value;
	}
	
	public class CustomExcelReportExporter extends ExcelReportExporter {
		public void startSheet(String name){
			Field sheetField = null;
			Field workbookField = null;
			Field columnCountField = null;
			Field rowCountField = null;
			Field cellCountField = null;
			try {
				// create new sheet in the workbook
				workbookField = ExcelReportExporter.class.getDeclaredField("workbook");
				workbookField.setAccessible(true);
				HSSFWorkbook workbook = (HSSFWorkbook) workbookField.get(this);
				HSSFSheet sheet = workbook.createSheet(name);
				
				// init sheet and context
				sheetField = ExcelReportExporter.class.getDeclaredField("sheet");
				sheetField.setAccessible(true);
				sheetField.set(this, sheet);
				columnCountField = ExcelReportExporter.class.getDeclaredField("columnCount");
				columnCountField.setAccessible(true);
				columnCountField.set(this, 0);
			    rowCountField = ExcelReportExporter.class.getDeclaredField("rowCount");
			    rowCountField.setAccessible(true);
			    rowCountField.set(this, 0);
			    cellCountField = ExcelReportExporter.class.getDeclaredField("cellCount");
			    cellCountField.setAccessible(true);
			    cellCountField.set(this, 0);
				
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage());
			} catch (NoSuchFieldException e) {
				LOGGER.error(e.getMessage());
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}
	
}
