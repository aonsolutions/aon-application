package com.esferalia.aon.ui.payroll.file;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.math.NumberUtils;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryExpenseController;


public class EnterpriseCostProvider implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryExpenseController.class.getName());
	
	
	private List<Integer> salaryList;
	
	private Map<String, Object> salaryConceptsMap;
	
	
	public List<Integer> getSalaryList() {
		return salaryList;
	}

	public void setSalaryList(List<Integer> salaryList) {
		this.salaryList = salaryList;
	}

	public Map<String, Object> getSalaryConceptsMap() {
		return salaryConceptsMap;
	}

	public void setSalaryConceptsMap(Map<String, Object> salaryConceptsMap) {
		this.salaryConceptsMap = salaryConceptsMap;
	}
	
	public boolean excelReport(Date startDate, Date endDate, Integer domain, List<Integer> cccList, List<Integer> workPlaceList, List<Integer> salaryList, OutputStream output) throws IOException, ReportException, AonConnectionException {
		Connection conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
		int salaryCount = initSalaryContext(conn, startDate, endDate, domain, cccList, workPlaceList, salaryList);
		if(salaryCount<=0){
			return false;
		}
		
		ExcelReportExporter exporter = new ExcelReportExporter();
		
		// MS EXCEL 97 not support more than 256 columns 
		if(salaryCount<256){
			exporter.startExport("Conceptos");
			ReportMetadata conceptColumnMetadata = getConceptColumnMetadata();
			exporter.exportHeader(conceptColumnMetadata);
			excelReportByConcept(exporter, conceptColumnMetadata);
		}

		exporter.setSheet(exporter.createSheet("Trabajadores"));
		ReportMetadata contractColumnMetadata = getContractColumnMetadata();
		exporter.exportHeader(contractColumnMetadata);
		excelReportByContract(exporter, contractColumnMetadata);
		
		exporter.endExport(output);
		output.flush();
		return true;
	}
	
	private int initSalaryContext(Connection connection, Date startDate, Date endDate, Integer domain, List<Integer> cccIds,
			List<Integer> workPlaceIds, List<Integer> salaryIds) throws AonConnectionException {
		
		if(domain==null && cccIds==null && workPlaceIds==null && salaryIds==null){
			throw new AbortProcessingException("One of 'domain' or 'cccIdList' or 'workPlaceIds' or 'salaryIdList' is expected.");
		}
		
		Result<Record> salaryRecords = (Result<Record>) getSalaryRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds);
		
		if(salaryRecords.size()>0){
//			Result<Record> salaryDeductionHeaders = (Result<Record>) getSalaryDeductionRecords(year, month, domain, null, true);
//			Result<Record> salaryCostHeaders = (Result<Record>) getSalaryCostRecords(conn, startDate, endDate, domain, null, true);
			
			Result<Record> salaryPaymentRecords = (Result<Record>) getSalaryPaymentRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds, false);
			Result<Record> salaryDeductionRecords = (Result<Record>) getSalaryDeductionRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds, false);
			Result<Record> salaryCostRecords = (Result<Record>) getSalaryCostRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds, false);
			Result<Record> salaryBonusRecords = (Result<Record>) getSalaryBonusRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds);
			Result<Record> salaryEmbargoRecords = (Result<Record>) getSalaryEmbargoRecords(connection, startDate, endDate, domain, cccIds, workPlaceIds, salaryIds);
			
			salaryList = new LinkedList<Integer>();
			salaryConceptsMap = new HashMap<String, Object>();
			
			for (Record salaryPayment : salaryPaymentRecords) {
				Integer salaryId = salaryPayment.getValue(SALARY_PAYMENT.SALARY);
				Integer type = salaryPayment.getValue(SALARY_PAYMENT.TYPE).intValue();
				Double amount = salaryPayment.getValue(SALARY_PAYMENT.AMOUNT);
				if(salaryConceptsMap.containsKey(type + "_" + salaryId)){
					salaryConceptsMap.put(type + "_" + salaryId, ((Double)salaryConceptsMap.get(type + "_" + salaryId)) + amount);
				} else {
					salaryConceptsMap.put(type + "_" + salaryId, amount);
				}
			}
			
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

			for (Record salaryBonus : salaryBonusRecords) {
				Integer salaryId = salaryBonus.getValue(SALARY_BONUS.SALARY);
				Double amount = salaryBonus.getValue(SALARY_BONUS.AMOUNT);
				if(salaryConceptsMap.containsKey(QuoteConcept.BONUS.name()+"_" + salaryId)){
					salaryConceptsMap.put(QuoteConcept.BONUS.name()+"_" + salaryId, ((Double)salaryConceptsMap.get(QuoteConcept.BONUS.name()+"_" + salaryId)) + amount);
				} else {
					salaryConceptsMap.put(QuoteConcept.BONUS.name()+"_" + salaryId, amount);
				}
			}
			
			for (Record salaryEmbargo : salaryEmbargoRecords) {
				Integer salaryId = salaryEmbargo.getValue(SALARY_EMBARGO.SALARY);
				Double amount = salaryEmbargo.getValue(SALARY_EMBARGO.AMOUNT);
				if(salaryConceptsMap.containsKey(QuoteConcept.EMBARGO.name()+"_" + salaryId)){
					salaryConceptsMap.put(QuoteConcept.EMBARGO.name()+"_" + salaryId, ((Double)salaryConceptsMap.get(QuoteConcept.EMBARGO.name()+"_" + salaryId)) + amount);
				} else {
					salaryConceptsMap.put(QuoteConcept.EMBARGO.name()+"_" + salaryId, amount);
				}
			}

			for (Record salary : salaryRecords) {
				Integer salaryId = salary.getValue(SALARY.ID);
				salaryList.add(salaryId);
				salaryConceptsMap.put(QuoteConcept.DATE.name() + "_" + salaryId,salary.getValue(SALARY.END_DATE));
				salaryConceptsMap.put(QuoteConcept.ENTERPRISE_CCC.name() + "_" + salaryId,salary.getValue(SALARY.CCC));
				salaryConceptsMap.put(QuoteConcept.EMPLOYEE_NAME.name() + "_" + salaryId,salary.getValue(SALARY.EMPLOYEE_NAME));
				salaryConceptsMap.put(QuoteConcept.REMUNERATION.name() + "_" + salaryId,salary.getValue(SALARY.REMUNERATION));
				salaryConceptsMap.put(QuoteConcept.SOCIAL_SECURITY_CONTRIBUTIONS.name() + "_" + salaryId,salary.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
				salaryConceptsMap.put(QuoteConcept.TOTAL_IRPF.name() + "_" + salaryId,salary.getValue(SALARY.TOTAL_IRPF));
				salaryConceptsMap.put(QuoteConcept.TOTAL_PAYMENT.name() + "_" + salaryId,salary.getValue(SALARY.TOTAL_PAYMENT));
				salaryConceptsMap.put(QuoteConcept.TOTAL_DEDUCTION.name() + "_" + salaryId,salary.getValue(SALARY.TOTAL_DEDUCTION));
				salaryConceptsMap.put(QuoteConcept.TOTAL_LIQUID.name() + "_" + salaryId,salary.getValue(SALARY.TOTAL_LIQUID));
				salaryConceptsMap.put(QuoteConcept.TOTAL_ENTERPRISE.name() + "_" + salaryId,salary.getValue(SALARY.TOTAL_ENTERPRISE));
				salaryConceptsMap.put(QuoteConcept.PRO_EXT_BASE.name() + "_" + salaryId,salary.getValue(SALARY.PRO_EXT_BASE));
				salaryConceptsMap.put(QuoteConcept.IT_BASE.name() + "_" + salaryId,salary.getValue(SALARY.IT_BASE));
				salaryConceptsMap.put(QuoteConcept.RAW_CGC_BASE.name() + "_" + salaryId,salary.getValue(SALARY.RAW_CGC_BASE));
				salaryConceptsMap.put(QuoteConcept.CGC_BASE.name() + "_" + salaryId,salary.getValue(SALARY.CGC_BASE));
				salaryConceptsMap.put(QuoteConcept.HEXTRA_BASE.name() + "_" + salaryId,salary.getValue(SALARY.HEXTRA_BASE));
				salaryConceptsMap.put(QuoteConcept.NON_HEXTRA_BASE.name() + "_" + salaryId,salary.getValue(SALARY.NON_HEXTRA_BASE));
				salaryConceptsMap.put(QuoteConcept.CGP_BASE.name() + "_" + salaryId,salary.getValue(SALARY.CGP_BASE));
				salaryConceptsMap.put(QuoteConcept.MONEY_IRPF_BASE.name() + "_" + salaryId,salary.getValue(SALARY.MONEY_IRPF_BASE));
				salaryConceptsMap.put(QuoteConcept.INKIND_IRPF_BASE.name() + "_" + salaryId,salary.getValue(SALARY.INKIND_IRPF_BASE));
				salaryConceptsMap.put(QuoteConcept.IRPF_BASE.name() + "_" + salaryId,salary.getValue(SALARY.IRPF_BASE));
			}
			return salaryList.size();
		}
		return 0;
	}
	
	private ReportMetadata getContractColumnMetadata() throws ReportException {
		
		ReportMetadata metadata = new ReportMetadata();
		// salary
//		metadata.getColumns().add(new ReportColumnMetadata("salary",Types.INTEGER,"cod. nomina",5));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.DATE.name(),Types.DATE,"Periodo liquidacion",15));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.ENTERPRISE_CCC.name(),Types.VARCHAR,"Cuenta de cotiz.",15));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EMPLOYEE_NAME.name(),Types.VARCHAR,"Trabajador",30));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.TOTAL_PAYMENT.name(),Types.DOUBLE,"Total devengos",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.TOTAL_DEDUCTION.name(),Types.DOUBLE,"Total deducciones",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.TOTAL_LIQUID.name(),Types.DOUBLE,"Total liquido",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.PRO_EXT_BASE.name(),Types.DOUBLE,"Base prorrateada extras",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IT_BASE.name(),Types.DOUBLE,"Base IT",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.RAW_CGC_BASE.name(),Types.DOUBLE,"Base cg.c. efectiva",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC_BASE.name(),Types.DOUBLE,"Base Cg. Comunes",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.HEXTRA_BASE.name(),Types.DOUBLE,"BAse h. extra",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.NON_HEXTRA_BASE.name(),Types.DOUBLE,"Base h. extra no estruc.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGP_BASE.name(),Types.DOUBLE,"Base Cg. Profesionales",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.MONEY_IRPF_BASE.name(),Types.DOUBLE,"Base IRPF dineraria",10));
//		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.INKIND_IRPF_BASE.name(),Types.DOUBLE,"Base IRPF especie",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IRPF_BASE.name(),Types.DOUBLE,"Base IRPF",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.REMUNERATION.name(),Types.DOUBLE,"Remuneracion",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.SOCIAL_SECURITY_CONTRIBUTIONS.name(),Types.DOUBLE,"Contribucion a la S.S.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.TOTAL_ENTERPRISE.name(),Types.DOUBLE,"Total empresa",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.TOTAL_IRPF.name(),Types.DOUBLE,"Total Irpf",10));
		// salary_bonus
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.BONUS.name(),Types.DOUBLE,"Bonificado",10));
		// salary_embargo
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EMBARGO.name(),Types.DOUBLE,"Embargado",10));
		// salary_deduction
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC.name(),Types.DOUBLE,"Cont. comunes",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FP.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FP.name(),Types.DOUBLE,"F.P.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.DESMPL.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.DESMPL.name(),Types.DOUBLE,"Desempleo",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EXTR.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EXTR.name(),Types.DOUBLE,"H. Extra",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.NEXTR.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.NEXTR.name(),Types.DOUBLE,"H. Extra no estruc.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IRPF.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IRPF.name(),Types.DOUBLE,"I.R.P.F.",10));
		// salary_cost
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC_E.name(),Types.DOUBLE,"Cont. comunes (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC_E_TEMP.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.CGC_E_TEMP.name(),Types.DOUBLE,"Inferior a 7 dias (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EXTR_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.EXTR_E.name(),Types.DOUBLE,"H.Extra (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.NEXTR_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.NEXTR_E.name(),Types.DOUBLE,"H.Extra no estruc (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IT_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IT_E.name(),Types.DOUBLE,"I.T.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IMS_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.IMS_E.name(),Types.DOUBLE,"I.M.S.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FP_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FP_E.name(),Types.DOUBLE,"F.P. (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FOGASA_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.FOGASA_E.name(),Types.DOUBLE,"FOGASA",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.DESMPL_E.name()+"_PERCENT",Types.DOUBLE,"%",4));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.DESMPL_E.name(),Types.DOUBLE,"Desempleo (empresa)",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.ATEP_E.name(),Types.DOUBLE,"IT A.T. o E.P.",10));
		metadata.getColumns().add(new ReportColumnMetadata(QuoteConcept.ECSS_E.name(),Types.DOUBLE,"IT Enfermedad comun",10));
				
		return metadata;
	}

	private ReportMetadata getConceptColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("Concepto",Types.VARCHAR,"",25));
		metadata.getColumns().add(new ReportColumnMetadata("Total",Types.DOUBLE,"Total",15));
		for(Integer salaryId: salaryList){
			metadata.getColumns().add(new ReportColumnMetadata((String) salaryConceptsMap.get("EMPLOYEE_NAME_"+salaryId),Types.DOUBLE,(String) salaryConceptsMap.get("EMPLOYEE_NAME_"+salaryId),20));
		}
		return metadata;
	}

	public void excelReportByConcept(ExcelReportExporter exporter, ReportMetadata metadata) throws IOException, ReportException, AonConnectionException {
		
		// ****************************
		// salary
		// ****************************
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.TOTAL_PAYMENT.name(),"Total devengos");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.TOTAL_DEDUCTION.name(),"Total deducciones");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.TOTAL_LIQUID.name(),"Total liquido");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.PRO_EXT_BASE.name(),"Base prorrateada extras");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.IT_BASE.name(),"Base IT");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.RAW_CGC_BASE.name(),"Base cg.c. efectiva");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.CGC_BASE.name(),"Base cg.comunes");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.HEXTRA_BASE.name(),"Base h.extra");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.NON_HEXTRA_BASE.name(),"Base h.extra no estruc.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.CGP_BASE.name(),"Base cg.profesionales");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.IRPF_BASE.name(),"Base IRPF");
//		exportSalaryAllColumns(exporter, metadata, QuoteConcept.MONEY_IRPF_BASE.name(),"Base IRPF dineraria");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.INKIND_IRPF_BASE.name(),"Base IRPF especie");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.REMUNERATION.name(),"Remuneracion");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.SOCIAL_SECURITY_CONTRIBUTIONS.name(),"Contribucion a la S.S.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.TOTAL_ENTERPRISE.name(),"Total empresa");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.TOTAL_IRPF.name(),"Total Irpf");
		
		// ****************************
		// salary_payment
		// ****************************
//		exporter.startLine();
//		exportSalaryAllColumns(exporter, metadata, "PAYMENT");
		
		// ****************************
		// salary_bonus
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.BONUS.name(), "Bonificado");
		// ****************************
		// salary_embargo
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.EMBARGO.name(), "Embargado");
		// ****************************
		// salary_deduction
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.CGC.name(), "Cont. comunes");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.FP.name(), "F.P.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.DESMPL.name(), "Desempleo");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.EXTR.name(), "H. Extra");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.NEXTR.name(), "H. Extra no estruc.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.IRPF.name(), "I.R.P.F.");
		
		// ****************************
		// salary_cost
		// ****************************
		exporter.startLine();
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.CGC_E.name(), "Cont. comunes (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.CGC_E_TEMP.name(), "Inferior a 7 dias (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.EXTR_E.name(), "H.Extra (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.NEXTR_E.name(), "H.Extra no estruc (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.IT_E.name(), "I.T.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.IMS_E.name(), "I.M.S.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.FP_E.name(), "F.P. (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.FOGASA_E.name(), "FOGASA");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.DESMPL_E.name(),  "Desempleo (empresa)");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.ATEP_E.name(), "IT A.T. o E.P.");
		exportSalaryAllColumns(exporter, metadata, QuoteConcept.ECSS_E.name(), "IT Enfermedad comun");
		
	}
	
	private void exportSalaryAllColumns(ExcelReportExporter exporter, ReportMetadata metadata, String key, String description) throws ReportException{
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
	
	public void excelReportByContract(ExcelReportExporter exporter, ReportMetadata metadata) throws IOException, ReportException, AonConnectionException {
		for (Integer salary : salaryList) {
//			Double itBase = (Double) salaryConceptsMap.get(QuoteConcept.IT_BASE.name()+"_"+ salary);
//			Double rawCgcBase = (Double) salaryConceptsMap.get(QuoteConcept.RAW_CGC_BASE.name()+"_"+ salary);
			Double cgcBase = (Double) salaryConceptsMap.get(QuoteConcept.CGC_BASE.name()+"_"+ salary);
			Double extrBase = (Double) salaryConceptsMap.get(QuoteConcept.HEXTRA_BASE.name()+"_"+ salary);
			Double nextrBase = (Double) salaryConceptsMap.get(QuoteConcept.NON_HEXTRA_BASE.name()+"_"+ salary);
			Double cgpBase = (Double) salaryConceptsMap.get(QuoteConcept.CGP_BASE.name()+"_"+ salary);
//			Double moneyIrpfBase = (Double) salaryConceptsMap.get(QuoteConcept.MONEY_IRPF_BASE.name()+"_"+ salary);
//			Double inkindIrpfBase = (Double) salaryConceptsMap.get(QuoteConcept.INKIND_IRPF_BASE.name()+"_"+ salary);
			Double irpfBase = (Double) salaryConceptsMap.get(QuoteConcept.IRPF_BASE.name()+"_"+ salary);
			
			int column = 0;
			exporter.startLine();
			// salary
//			exporter.exportColumn(metadata.getColumns().get(column++), salary);
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.DATE.name()+"_"+ salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.ENTERPRISE_CCC.name()+"_"+ salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.EMPLOYEE_NAME.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.TOTAL_PAYMENT.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.TOTAL_DEDUCTION.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.TOTAL_LIQUID.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.PRO_EXT_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.IT_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.RAW_CGC_BASE.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.CGC_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.HEXTRA_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.NON_HEXTRA_BASE.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.CGP_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.MONEY_IRPF_BASE.name()+"_" + salary));
//			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.INKIND_IRPF_BASE.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.IRPF_BASE.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.REMUNERATION.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.SOCIAL_SECURITY_CONTRIBUTIONS.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.TOTAL_ENTERPRISE.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.TOTAL_IRPF.name()+"_" + salary));
			// salary_bonus
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.BONUS.name()+"_" + salary));
			// salary_embargo
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.EMBARGO.name()+"_" + salary));
			// salary_deduction
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.CGC.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.CGC.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.FP.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.FP.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.DESMPL.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.DESMPL.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.EXTR.name()+"_" + salary), extrBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.EXTR.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.NEXTR.name()+"_" + salary), nextrBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.NEXTR.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.IRPF.name()+"_" + salary), irpfBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.IRPF.name()+"_" + salary));
			// salary_cost
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.CGC_E.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.CGC_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.CGC_E_TEMP.name()+"_" + salary), (Double) salaryConceptsMap.get("CGC_E_" + salary)));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.CGC_E_TEMP.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.EXTR_E.name()+"_" + salary), extrBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.EXTR_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.NEXTR_E.name()+"_" + salary), nextrBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.NEXTR_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.IT_E.name()+"_" + salary), cgpBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.IT_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.IMS_E.name()+"_" + salary), cgpBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.IMS_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.FP_E.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.FP_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.FOGASA_E.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.FOGASA_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), getPercent((Double) salaryConceptsMap.get(QuoteConcept.DESMPL_E.name()+"_" + salary), cgcBase));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.DESMPL_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.ATEP_E.name()+"_" + salary));
			exporter.exportColumn(metadata.getColumns().get(column++), salaryConceptsMap.get(QuoteConcept.ECSS_E.name()+"_" + salary));
		}
		exporter.startLine();
	}
	
	private Double getPercent(Double quote, Double base){
		try {
			if(base<=0){
				return 0.0;
			}
			return (new Double(quote))*100/(new Double(base));
		} catch (Exception e) {
			return null;
		}
	}

	// /////////////////////////
	// SQL
	// /////////////////////////

	public Result<Record> getSalaryRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(SALARY);
		query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
		query.addConditions(SALARY.ISSUE_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.ISSUE_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(SALARY.ISSUE_DATE.desc(), SALARY.CCC.asc(), SALARY.EMPLOYEE_NAME.asc());
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		if (workPlaceIds != null && !workPlaceIds.isEmpty()) {
			query.addConditions(CONTRACT.WORKPLACE.in(workPlaceIds));
		}
		if (salaryIds != null && !salaryIds.isEmpty()) {
			query.addConditions(SALARY.ID.in(salaryIds));
		}
		return query.fetch();
	}

	public Result<Record> getSalaryPaymentRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds, boolean onlyHeaders) {
		SelectQuery<Record> query = obtainSalaryLinesQuery(connection, startDate, endDate,
				domainId, cccIds, workPlaceIds, salaryIds, SALARY_PAYMENT, SALARY_PAYMENT.SALARY, SALARY_PAYMENT.PAYMENT_CONCEPT);
		return query.fetch();
	}

	public Result<Record> getSalaryDeductionRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds, boolean onlyHeaders) {
		SelectQuery<Record> query = obtainSalaryLinesQuery(connection, startDate, endDate,
				domainId, cccIds, workPlaceIds, salaryIds, SALARY_DEDUCTION, SALARY_DEDUCTION.SALARY, SALARY_DEDUCTION.DEDUCTION_CONCEPT);
		return query.fetch();
	}

	public Result<Record> getSalaryCostRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds, boolean onlyHeaders) {
		SelectQuery<Record> query = obtainSalaryLinesQuery(connection, startDate, endDate,
				 domainId, cccIds, workPlaceIds, salaryIds, SALARY_COST, SALARY_COST.SALARY, SALARY_COST.COST_CONCEPT);
		return query.fetch();
	}
	
	public Result<Record> getSalaryBonusRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds) {
		SelectQuery<Record> query = obtainSalaryLinesQuery(connection, startDate, endDate,
				 domainId, cccIds, workPlaceIds, salaryIds, SALARY_BONUS, SALARY_BONUS.SALARY);
		return query.fetch();
	}
	
	public Result<Record> getSalaryEmbargoRecords(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds) {
		SelectQuery<Record> query = obtainSalaryLinesQuery(connection, startDate, endDate,
				 domainId, cccIds, workPlaceIds, salaryIds, SALARY_EMBARGO, SALARY_EMBARGO.SALARY);
		return query.fetch();
	}
	
	private SelectQuery<Record> obtainSalaryLinesQuery(Connection connection, Date startDate, Date endDate,
			Integer domainId, List<Integer> cccIds, List<Integer> workPlaceIds, List<Integer> salaryIds, TableImpl<?> salaryLines, TableField<?,?>... orderBy) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		SelectQuery<Record> query = ctx.selectQuery();
		query.addFrom(salaryLines);
		query.addJoinOnKey(SALARY, JoinType.LEFT_OUTER_JOIN);
		query.addConditions(SALARY.ISSUE_DATE.greaterOrEqual(new java.sql.Date(startDate.getTime())));
		query.addConditions(SALARY.ISSUE_DATE.lessOrEqual(new java.sql.Date(endDate.getTime())));
		query.addOrderBy(orderBy);
		if (domainId != null) {
			query.addConditions(SALARY.DOMAIN.equal(domainId));
		}
		if (cccIds != null && !cccIds.isEmpty()) {
			query.addJoin(CONTRACT, JoinType.LEFT_OUTER_JOIN, SALARY.CONTRACT.equal(CONTRACT.ID));
			query.addConditions(CONTRACT.ENTERPRISE_CCC.in(cccIds));
		}
		if (workPlaceIds != null && !workPlaceIds.isEmpty()) {
			query.addConditions(CONTRACT.WORKPLACE.in(workPlaceIds));
		}
		if (salaryIds != null && !salaryIds.isEmpty()) {
			query.addConditions(SALARY.ID.in(salaryIds));
		}
		return query;
	}
	
	public Result<Record> getAllCccRecords(Connection connection, Integer domainId) {
		if(domainId!=null){
			DSLContext ctx = DSL.using(connection, getDefaultSettings());
			SelectQuery<Record> query = ctx.selectQuery();
			query.addFrom(ENTERPRISE_CCC);
			query.addOrderBy(ENTERPRISE_CCC.CCC);
			query.addConditions(ENTERPRISE_CCC.DOMAIN.equal(domainId));
			return query.fetch();
		}
		return null;
	}
	
	public List<Integer> getAllCccIds(Connection connection, Integer domainId){
		Result<Record> cccRecords = getAllCccRecords(connection, domainId);
		List<Integer> idList = new LinkedList<Integer>();
		for (Record ccc : cccRecords) {
			idList.add(ccc.getValue(ENTERPRISE_CCC.ID));
		}
		return idList;
	}
	
	private Settings getDefaultSettings() {
		Settings SETTINGS = new Settings();
		SETTINGS.setRenderSchema(false);
		return SETTINGS;
	}

	
	public enum QuoteConcept {
		
		// salary
		DATE,
		ENTERPRISE_CCC,
		EMPLOYEE_NAME,
		REMUNERATION,
		SOCIAL_SECURITY_CONTRIBUTIONS,
		TOTAL_IRPF,
		TOTAL_PAYMENT,
		TOTAL_DEDUCTION,
		TOTAL_LIQUID,
		TOTAL_ENTERPRISE,
		PRO_EXT_BASE,
		IT_BASE,
		RAW_CGC_BASE,
		CGC_BASE,
		HEXTRA_BASE,
		NON_HEXTRA_BASE,
		CGP_BASE,
		MONEY_IRPF_BASE,
		INKIND_IRPF_BASE,
		IRPF_BASE,
		
		// salary_payment
		
		// salary_deduction
		CGC,
		FP,
		DESMPL,
		EXTR,
		NEXTR,
		IRPF,
		
		// salary_cost
		CGC_E,
		CGC_E_TEMP,
		EXTR_E,
		NEXTR_E,
		IT_E,
		IMS_E,
		FP_E,
		FOGASA_E,
		DESMPL_E,
		ATEP_E,
		ECSS_E,
		
		// salary_bonus
		BONUS,
		
		// salary_embargo
		EMBARGO,
		;
	}
	
}
