package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.code.aon.person.Person;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Bonus;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterprisePayrollExcel {

	private static final List<String> IMPORTANT_CELLS;
	private static final LinkedHashMap<String, String> DEFAULT_HEADER;
	private static final LinkedHashMap<String, String> DEFAULT_HEADER_SUMMARY;
	private static final LinkedHashMap<String, String> DEFAULT_HEADER_NO_TYPE;
	private static final LinkedHashMap<String, String> DEFAULT_HEADER_NO_TYPE_SUMMARY;
	private static final LinkedHashMap<String, String> COMPLETE_HEADER;
	private static final LinkedHashMap<String, String> COMPLETE_TOTALS_HEADER;
	private static final String FUNDAE = "BONIFICACION_FORMACION_CONTINUA";

	static {
		
		//-------------------------IMPORTANT------------------------
			IMPORTANT_CELLS = new LinkedList<>();
			IMPORTANT_CELLS.add("raw");
			IMPORTANT_CELLS.add("totalCost");
			IMPORTANT_CELLS.add("liquid");
			IMPORTANT_CELLS.add("totalSS");
		
		//-------------------------COMPLETE NORMAL-------------------------
		
			DEFAULT_HEADER = new LinkedHashMap<>();
			DEFAULT_HEADER.put("employee", "NOMBRE");
			DEFAULT_HEADER.put("type", "TIPO");
			DEFAULT_HEADER.put("raw", "BRUTO");
			DEFAULT_HEADER.put("enterpriseSS", "COTIZAC.");
			DEFAULT_HEADER.put("totalCost", "COSTE TOTAL");
			
			DEFAULT_HEADER.put("joint1", "");
			
			DEFAULT_HEADER.put("employeeSS", "COTIZAC.");
			DEFAULT_HEADER.put("irpf", "IRPF");
			DEFAULT_HEADER.put("other", "DEDUC.");
			DEFAULT_HEADER.put("liquid", "LÍQUIDO");
			
			DEFAULT_HEADER.put("joint2", "");
			
			DEFAULT_HEADER.put("totalSS", "TOTAL S.S.");
			
			DEFAULT_HEADER.put("joint3", "");
			
			DEFAULT_HEADER.put("cgcEnterprise", "C. COMUN.");
			DEFAULT_HEADER.put("cgpEnterprise", "C. PROFES.");
			DEFAULT_HEADER.put("unemploymentEnterprise", "DESEMP.");
			DEFAULT_HEADER.put("jobTrainingEnterprise", "F.P.");
			DEFAULT_HEADER.put("fogasaEnterprise", "FOGASA");
			DEFAULT_HEADER.put("extraHEnterprise", "H. EXTRAS");
			DEFAULT_HEADER.put("bonuses", "BONIF.");
			
			DEFAULT_HEADER.put("joint4", "");
			
			DEFAULT_HEADER.put("cgc", "C. COMUN.");
			DEFAULT_HEADER.put("cgp", "C. PROFES.");
			DEFAULT_HEADER.put("unemployment", "DESEMP.");
			DEFAULT_HEADER.put("jobTraining", "F.P.");
			DEFAULT_HEADER.put("extraH", "H. EXTRAS");
			
			DEFAULT_HEADER.put("advancedPayments", "ANTICIPOS");
			DEFAULT_HEADER.put("embargos", "EMBARGOS");
			DEFAULT_HEADER.put("otherDeductions", "OTR. DEDUC.");
			
			DEFAULT_HEADER.put("joint5", "");
			
			DEFAULT_HEADER.put("cgcBase", "BASE C.C.");
			DEFAULT_HEADER.put("irpfBase", "BASE IRPF");
			DEFAULT_HEADER.put("joint6", "");
			DEFAULT_HEADER.put("moneyIrpfBase", "IRPF MON.");
			DEFAULT_HEADER.put("inKindIrpfBase", "IRPF ESP.");
		//---------------------------------------------------------
		
		//-------------------------COMPLETE------------------------
			COMPLETE_HEADER = new LinkedHashMap<>();
			COMPLETE_HEADER.put("employee", "PERÍODO");
			COMPLETE_HEADER.put("joint0", "");
			COMPLETE_HEADER.put("type", "TIPO");
			COMPLETE_HEADER.put("raw", "BRUTO");
			COMPLETE_HEADER.put("enterpriseSS", "COTIZAC.");
			COMPLETE_HEADER.put("totalCost", "COSTE TOTAL");
			
			COMPLETE_HEADER.put("joint1", "");
			
			COMPLETE_HEADER.put("employeeSS", "COTIZAC.");
			COMPLETE_HEADER.put("irpf", "IRPF");
			COMPLETE_HEADER.put("other", "DEDUC.");
			COMPLETE_HEADER.put("liquid", "LÍQUIDO");
			
			COMPLETE_HEADER.put("joint2", "");
			
			COMPLETE_HEADER.put("totalSS", "TOTAL S.S.");
			
			COMPLETE_HEADER.put("joint3", "");
			
			COMPLETE_HEADER.put("cgcEnterprise", "C. COMUN.");
			COMPLETE_HEADER.put("cgpEnterprise", "C. PROFES.");
			COMPLETE_HEADER.put("unemploymentEnterprise", "DESEMP.");
			COMPLETE_HEADER.put("jobTrainingEnterprise", "F.P.");
			COMPLETE_HEADER.put("fogasaEnterprise", "FOGASA");
			COMPLETE_HEADER.put("extraHEnterprise", "H. EXTRAS");
			COMPLETE_HEADER.put("bonuses", "BONIF.");
			COMPLETE_HEADER.put(FUNDAE, "FUNDAE");
			
			COMPLETE_HEADER.put("joint4", "");
			
			COMPLETE_HEADER.put("cgc", "C. COMUN.");
			COMPLETE_HEADER.put("cgp", "C. PROFES.");
			COMPLETE_HEADER.put("unemployment", "DESEMP.");
			COMPLETE_HEADER.put("jobTraining", "F.P.");
			COMPLETE_HEADER.put("extraH", "H. EXTRAS");
			
			COMPLETE_HEADER.put("advancedPayments", "ANTICIPOS");
			COMPLETE_HEADER.put("embargos", "EMBARGOS");
			COMPLETE_HEADER.put("otherDeductions", "OTR. DEDUC.");
			
			COMPLETE_HEADER.put("joint5", "");
			
			COMPLETE_HEADER.put("cgcBase", "BASE C.C.");
			COMPLETE_HEADER.put("irpfBase", "BASE IRPF");
			COMPLETE_HEADER.put("joint6", "");
			COMPLETE_HEADER.put("moneyIrpfBase", "IRPF MON.");
			COMPLETE_HEADER.put("inKindIrpfBase", "IRPF ESP.");
			//---------------------------------------------------------
			
			
		//-------------------------COMPLETE TOTALS-----------------
			
			COMPLETE_TOTALS_HEADER = new LinkedHashMap<>();
			COMPLETE_TOTALS_HEADER.put("employee", "PERÍODO");
			
			COMPLETE_TOTALS_HEADER.put("joint0", "");
			
			COMPLETE_TOTALS_HEADER.put("workplace", "CENTRO DE TRABAJO");
			COMPLETE_TOTALS_HEADER.put("raw", "BRUTO");
			COMPLETE_TOTALS_HEADER.put("enterpriseSS", "COTIZAC.");
			COMPLETE_TOTALS_HEADER.put("totalCost", "COSTE TOTAL");
			
			COMPLETE_TOTALS_HEADER.put("joint1", "");
			
			COMPLETE_TOTALS_HEADER.put("employeeSS", "COTIZAC.");
			COMPLETE_TOTALS_HEADER.put("irpf", "IRPF");
			COMPLETE_TOTALS_HEADER.put("other", "DEDUC.");
			COMPLETE_TOTALS_HEADER.put("liquid", "LÍQUIDO");
			
			COMPLETE_TOTALS_HEADER.put("joint2", "");
			
			COMPLETE_TOTALS_HEADER.put("totalSS", "TOTAL S.S.");
			
			COMPLETE_TOTALS_HEADER.put("joint3", "");
			
			COMPLETE_TOTALS_HEADER.put("cgcEnterprise", "C. COMUN.");
			COMPLETE_TOTALS_HEADER.put("cgpEnterprise", "C. PROFES.");
			COMPLETE_TOTALS_HEADER.put("unemploymentEnterprise", "DESEMP.");
			COMPLETE_TOTALS_HEADER.put("jobTrainingEnterprise", "F.P.");
			COMPLETE_TOTALS_HEADER.put("fogasaEnterprise", "FOGASA");
			COMPLETE_TOTALS_HEADER.put("extraHEnterprise", "H. EXTRAS");
			COMPLETE_TOTALS_HEADER.put("bonuses", "BONIF.");
			COMPLETE_TOTALS_HEADER.put(FUNDAE, "FUNDAE");
			
			COMPLETE_TOTALS_HEADER.put("joint4", "");
			
			COMPLETE_TOTALS_HEADER.put("cgc", "C. COMUN.");
			COMPLETE_TOTALS_HEADER.put("cgp", "C. PROFES.");
			COMPLETE_TOTALS_HEADER.put("unemployment", "DESEMP.");
			COMPLETE_TOTALS_HEADER.put("jobTraining", "F.P.");
			COMPLETE_TOTALS_HEADER.put("extraH", "H. EXTRAS");
			
			COMPLETE_TOTALS_HEADER.put("advancedPayments", "ANTICIPOS");
			COMPLETE_TOTALS_HEADER.put("embargos", "EMBARGOS");
			COMPLETE_TOTALS_HEADER.put("otherDeductions", "OTR. DEDUC.");
			
			COMPLETE_TOTALS_HEADER.put("joint5", "");
			
			COMPLETE_TOTALS_HEADER.put("cgcBase", "BASE C.C.");
			COMPLETE_TOTALS_HEADER.put("irpfBase", "BASE IRPF");
			COMPLETE_TOTALS_HEADER.put("joint6", "");
			COMPLETE_TOTALS_HEADER.put("moneyIrpfBase", "IRPF MON.");
			COMPLETE_TOTALS_HEADER.put("inKindIrpfBase", "IRPF ESP.");
		//---------------------------------------------------------
			
			
		
		//-------------------------SUMMARY-------------------------
		
			DEFAULT_HEADER_SUMMARY = new LinkedHashMap<>();
			DEFAULT_HEADER_SUMMARY.put("employee", "NOMBRE");
			DEFAULT_HEADER_SUMMARY.put("type", "TIPO");
			DEFAULT_HEADER_SUMMARY.put("raw", "BRUTO");
			DEFAULT_HEADER_SUMMARY.put("enterpriseSS", "COTIZAC.");
			DEFAULT_HEADER_SUMMARY.put("totalCost", "COSTE TOTAL");
			
			DEFAULT_HEADER_SUMMARY.put("joint1", "");
			
			DEFAULT_HEADER_SUMMARY.put("employeeSS", "COTIZAC.");
			DEFAULT_HEADER_SUMMARY.put("irpf", "IRPF");
			DEFAULT_HEADER_SUMMARY.put("other", "DEDUC.");
			DEFAULT_HEADER_SUMMARY.put("liquid", "LÍQUIDO");
			
			DEFAULT_HEADER_SUMMARY.put("joint2", "");
			
			DEFAULT_HEADER_SUMMARY.put("totalSS", "TOTAL S.S.");
			
		//---------------------------------------------------------
		//--------------------EMPLOYEE_SUMMARY---------------------
			
			DEFAULT_HEADER_NO_TYPE_SUMMARY = new LinkedHashMap<>();
			DEFAULT_HEADER_NO_TYPE_SUMMARY.putAll(DEFAULT_HEADER_SUMMARY);
			DEFAULT_HEADER_NO_TYPE_SUMMARY.remove("type");
			
		//---------------------------------------------------------
		//------------------------EMPLOYEE-------------------------
		
		DEFAULT_HEADER_NO_TYPE = new LinkedHashMap<>();
		DEFAULT_HEADER_NO_TYPE.putAll(DEFAULT_HEADER);
		DEFAULT_HEADER_NO_TYPE.remove("type");
		
		//---------------------------------------------------------
			
	}
	
	public static void enterprisePayrollGeneratorByEmployee (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date startDate, Date endDate, ExcelType excelType, SalaryType[] salaryFilter, Person ...persons) {
		List<String> filteredNafs = new LinkedList<>();
		List<SalaryType> typesList = Arrays.asList(salaryFilter != null ? salaryFilter : new SalaryType[0]);
		
		if (persons != null) {
			for (Person person : persons) {
				if (person != null && person.getSocialSecurityNumber() != null)
					filteredNafs.add(person.getSocialSecurityNumber());
			}
		}
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, w -> w.getIdProperty().eq(atomicWorkplace.get()))
					.getEnterprise();
			
			List<IEnterprisePayroll> payrolls = new LinkedList<>();
			
			getEnterprisePayrolls(aonContext, startDate, endDate, eId, wId)
				.filter(p -> filteredNafs.isEmpty() || filteredNafs.contains(p.getEmployeeNaf()))
				.filter(p -> (p.getSalaryType() == null || typesList.contains(p.getSalaryType())) && p.getEmployeeNaf() != null)
				.forEach(p -> {
					Optional<IEnterprisePayroll> optPayroll = payrolls.stream().filter(pa -> pa.getEmployeeNaf().equals(p.getEmployeeNaf())).findFirst();
					EnterprisePayroll enterprisePayroll = null;
					if (optPayroll.isPresent()) {
						enterprisePayroll = (EnterprisePayroll) optPayroll.get();
						
						
						EnterprisePayrollExcelUtils.sumPayrolls(enterprisePayroll, p);
						
						
					} else {
						p.salaryType = null;
						p.startDate = null;
						p.endDate = null;
						payrolls.add(p);
					}
				});
					
					
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, startDate
					, endDate
					, excelType);	
		} catch (IOException e) {}
		
	}
	
	public static void enterprisePayrollGeneratorByPeriod (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date startDate, Date endDate, ExcelType excelType, SalaryType[] salaryFilter, Person ...persons) {
		
		List<String> filteredNafs = new LinkedList<>();
		List<SalaryType> typesList = Arrays.asList(salaryFilter != null ? salaryFilter : new SalaryType[0]);
		
		if (persons != null) {
			for (Person person : persons) {
				if (person != null && person.getSocialSecurityNumber() != null)
					filteredNafs.add(person.getSocialSecurityNumber());
			}
		}
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, w -> w.getIdProperty().eq(atomicWorkplace.get()))
					.getEnterprise();
			
			List<IEnterprisePayroll> payrolls = new LinkedList<>();
			
			getEnterprisePayrolls(aonContext, startDate, endDate, eId, wId)
				.filter(p -> filteredNafs.isEmpty() || filteredNafs.contains(p.getEmployeeNaf()))
				.filter(p -> (p.getSalaryType() == null || typesList.contains(p.getSalaryType())) && p.getEmployeeNaf() != null)
				.forEach(p -> {
					Optional<IEnterprisePayroll> optPayroll = payrolls.stream().filter(pa -> {
						String nameKey = EnterprisePayrollExcelUtils.getMonthYearName(p.getEndDate());
						return (pa.getEmployee().equalsIgnoreCase(nameKey));
					}).findFirst();
					EnterprisePayroll enterprisePayroll = null;
					if (optPayroll.isPresent()) {
						enterprisePayroll = (EnterprisePayroll) optPayroll.get();
						
						EnterprisePayrollExcelUtils.sumPayrolls(enterprisePayroll, p);
						
						
					} else {
						
						if (p.endDate != null) {							
							Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
							cal.setTime(p.endDate);
							EnterprisePayrollExcelUtils.clearCalendar(cal);
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
							p.endDate = cal.getTime();
							cal.set(Calendar.DAY_OF_MONTH, 1);							
							p.startDate = cal.getTime();
						}
						p.salaryType = null;
						p.employee = EnterprisePayrollExcelUtils.getMonthYearName(p.getEndDate());
						payrolls.add(p);
					}
				});
					
					
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, startDate
					, endDate
					, excelType);	
		} catch (IOException e) {}
		
	}
	
	public static void completeEnterprisePayrollGenerator (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date startDate, Date endDate, SalaryType[] salaryFilter, Person ...persons) {
		
		List<String> filteredNafs = new LinkedList<>();
		List<SalaryType> typesList = Arrays.asList(salaryFilter != null ? salaryFilter : new SalaryType[0]);
		
		if (persons != null) {
			for (Person person : persons) {
				if (person != null && person.getSocialSecurityNumber() != null)
					filteredNafs.add(person.getSocialSecurityNumber());
			}
		}
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
						, aonContext.getDomainId()
						, aonContext.getUser()
						, w -> w.getIdProperty().eq(atomicWorkplace.get()))
				.getEnterprise();
			
			
			Map<String, Map<String, Map<SalaryType, IEnterprisePayroll>>> completeWorkplaceData = new LinkedHashMap<>();
			Map<String, Map<String, Map<SalaryType, IEnterprisePayroll>>> completeEmployeeData = new LinkedHashMap<>();
			
			getEnterprisePayrolls(aonContext, startDate, endDate, eId, wId)
			.filter(p -> filteredNafs.isEmpty() || filteredNafs.contains(p.getEmployeeNaf()))
			.filter(p -> (p.getSalaryType() == null || typesList.contains(p.getSalaryType())) && p.getEmployeeNaf() != null)
			.forEach(p -> {
				
				String dateKey = EnterprisePayrollExcelUtils.getMonthYearName(p.getEndDate());
				
				/**
				 * employees 
				 */
				
				/*if (completeEmployeeData.containsKey(p.getEmployeeNaf())) {
					Map<String, Map<SalaryType, IEnterprisePayroll>> workplaceData = completeEmployeeData.get(p.getEmployeeNaf());
					
					if (workplaceData.containsKey(dateKey)) {
						Map<SalaryType, IEnterprisePayroll> dateData = workplaceData.get(dateKey);
						
						if (dateData.containsKey(p.getSalaryType())) {
							EnterprisePayroll prl = (EnterprisePayroll) dateData.get(p.getSalaryType());
							EnterprisePayrollExcelUtils.sumPayrolls(prl, p);
						} else {
							dateData.put(p.getSalaryType(), p);
						}
						
					} else {
						LinkedHashMap<SalaryType, IEnterprisePayroll> dateData = new LinkedHashMap<>();
						dateData.put(p.getSalaryType(), p);
						workplaceData.put(dateKey, dateData);
					}
				} else {
					Map<String, Map<SalaryType, IEnterprisePayroll>> workplaceData = new LinkedHashMap<>();
					LinkedHashMap<SalaryType, IEnterprisePayroll> dateData = new LinkedHashMap<>();
					dateData.put(p.getSalaryType(), p);
					workplaceData.put(dateKey, dateData);
					completeEmployeeData.put(p.getEmployeeNaf(), workplaceData);
				}*/
				
				
				/**
				 * workplaces
				 */
				if (completeWorkplaceData.containsKey(p.getWorkplace())) {
					Map<String, Map<SalaryType, IEnterprisePayroll>> workplaceData = completeWorkplaceData.get(p.getWorkplace());
					
					if (workplaceData.containsKey(dateKey)) {
						Map<SalaryType, IEnterprisePayroll> dateData = workplaceData.get(dateKey);
						
						if (dateData.containsKey(p.getSalaryType())) {
							
							EnterprisePayroll prl = (EnterprisePayroll) dateData.get(p.getSalaryType());
							
							EnterprisePayrollExcelUtils.sumPayrolls(prl, p);
							
						} else {
							dateData.put(p.getSalaryType(), p);
						}
						
					} else {
						LinkedHashMap<SalaryType, IEnterprisePayroll> dateData = new LinkedHashMap<>();
						dateData.put(p.getSalaryType(), p);
						workplaceData.put(dateKey, dateData);
					}
				} else {
					Map<String, Map<SalaryType, IEnterprisePayroll>> workplaceData = new LinkedHashMap<>();
					LinkedHashMap<SalaryType, IEnterprisePayroll> dateData = new LinkedHashMap<>();
					dateData.put(p.getSalaryType(), p);
					workplaceData.put(dateKey, dateData);
					completeWorkplaceData.put(p.getWorkplace(), workplaceData);
				}
			});
			
			Map<String, Map<String, List<ContractData>>> contractDataByWorkplace = getContractDataByWorkplace(aonContext, startDate, endDate, eId, wId);
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			writeComplete(outputStream
					, completeWorkplaceData
					/*, completeEmployeeData*/
					, Optional.empty()
					, enterpriseName
					, startDate
					, endDate
					, Optional.ofNullable(contractDataByWorkplace));	
		} catch (IOException e) {}
		
	}
	
	
	
	public static void simpleEnterprisePayrollGenerator (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date startDate, Date endDate, ExcelType excelType) {
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, w -> w.getIdProperty().eq(atomicWorkplace.get()))
					.getEnterprise();
			
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, startDate, endDate, eId, wId)
					.filter(p -> p.getSalaryType() == null || p.getSalaryType().ordinal()< SalaryType.L00.ordinal())
					.collect(Collectors.toList());
			
			Map<String, Map<String, List<ContractData>>> contractDataByWorkplace = getContractDataByWorkplace(aonContext, startDate, endDate, eId, wId);
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, startDate
					, endDate
					, excelType);	
		} catch (IOException e) {}
		
	}
	
	public static void simpleEnterprisePayrollGenerator (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date date, ExcelType excelType) {
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Integer month = c.get(Calendar.MONTH)+1;
		Integer year = c.get(Calendar.YEAR);
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, w -> w.getIdProperty().eq(atomicWorkplace.get()))
					.getEnterprise();
			
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, month, year, eId, wId)
					.filter(p -> p.getSalaryType() == null || p.getSalaryType().ordinal()< SalaryType.L00.ordinal())
					.collect(Collectors.toList());
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, EnterprisePayrollExcelUtils.getDateString(month, year)
					, excelType);
		} catch (IOException e) {}
	}
	
	public static void simpleEnterprisePayrollGenerator (String domainName, String user, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date date, ExcelType excelType, Collection<Integer> types) {
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Integer month = c.get(Calendar.MONTH)+1;
		Integer year = c.get(Calendar.YEAR);
		
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
			if (eId == null || eId == 0)
				eId = AON.getWorkplace(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, w -> w.getIdProperty().eq(atomicWorkplace.get()))
					.getEnterprise();
			
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, month, year, eId, wId)
					.filter(p -> p.getSalaryType() == null || types.contains(p.getSalaryType().ordinal()))
					.sorted(Comparator.comparing(IEnterprisePayroll::getEmployee))
					.collect(Collectors.toList());
			
			
			String enterpriseName = getEnterpriseName(aonContext, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, EnterprisePayrollExcelUtils.getDateString(month, year)
					, excelType);	
		} catch (IOException e) {}
	}
	
	
	public static void writeComplete(OutputStream outputStream, Map<String, Map<String, Map<SalaryType, IEnterprisePayroll>>> completeWorkplaceData,
			/*Map<String, Map<String, Map<SalaryType, IEnterprisePayroll>>> completeEmployeeData,*/ Optional<LinkedHashMap<String, String>> header,
			String enterpriseName, Date startDate, Date endDate, Optional<Map<String, Map<String, List<ContractData>>>> optContractData)
			throws IOException {
		
		
		ExcelType excelType = ExcelType.COMPLETE;
		
		Workbook wb = new XSSFWorkbook();

		DataFormat format = wb.createDataFormat();
		
		String dateString = EnterprisePayrollExcelUtils.getAppropiatePeriodString(startDate, endDate);

		Map<PayrollCellStyle, CellStyle> stylesMap = PayrollCellStyle.getStyles(wb, format);
		
		final int completeLength = 35;
		final int summaryLength = 11;

		Row row = null;

		Sheet totals = null;
		
		List<String> keys = completeWorkplaceData.keySet().stream().collect(Collectors.toList());
		
		totals = initializeCompleteTotalsSheet(enterpriseName, dateString, excelType, wb, stylesMap, completeLength,
				summaryLength, totals, keys);
		
		List<String> orderedMonths = EnterprisePayrollExcelUtils.getInnerPeriodStrings(startDate, endDate);
		
		Map<String, List<TotalsReferences>> totalsSchema = new LinkedHashMap<>();

		Map<String, Map<String, Map<String, Double>>> contractDataMap = getContractData4Complete(optContractData, startDate, endDate);
		
		
		for(String sheetKey : keys) {
			try {
				
				int numberOfColumns = 0;
				
				List<IEnterprisePayroll> allPayrolls = new LinkedList<>();
				
				
				completeWorkplaceData.get(sheetKey).forEach((k2, v2) -> v2.forEach((k3, v3) -> allPayrolls.add(v3)));
				
				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(sheetKey));
				
				sheet.createFreezePane(1, 3);
				
				LinkedHashMap<String, String> finalHeader = new LinkedHashMap<>();

				if (header.isPresent()) {
					LinkedHashMap<String, String> customHeader = header.get();
					finalHeader.putAll(customHeader);
				} else {
						finalHeader.putAll(COMPLETE_HEADER);
				}

				String rawColumn = null;
				String enterpriseSSColumn = null;
				String employeeSSColumn = null;
				String otherDecutionsColumn = null;
				String advancedPaymentsColumn = null;
				
				ArrayList<Integer> joints = new ArrayList<>(5);
				ArrayList<Integer> importantCells = new ArrayList<>(3);
				
				Map<String, Map<String, Double>> workplaceContractDataMap = contractDataMap.containsKey(sheetKey) ? contractDataMap.get(sheetKey) : Collections.emptyMap();
				
				EnterprisePayrollExcelChecks checks = new EnterprisePayrollExcelChecks(allPayrolls, workplaceContractDataMap);
				
				int empFirstCell = 4;
				int entFirstCell = 3;
				int tgssCell = 5;
				int entQuoteFirstCell;
				int empQuoteFirstCell;
				int fundaeCell = 0;
				

					if (!checks.isRaw())
						finalHeader.remove("raw");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!checks.isEnterpriseSS())
						finalHeader.remove("enterpriseSS");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!checks.isTotalCost())
						finalHeader.remove("totalCost");
					else {
						empFirstCell++;
						tgssCell++;
					}
					
					if (!checks.isEmployeeSS())
						finalHeader.remove("employeeSS");
					else
						tgssCell++;
					if (!checks.isIrpf())
						finalHeader.remove("irpf");
					else
						tgssCell++;
					if (!checks.isOther())
						finalHeader.remove("other");
					else
						tgssCell++;
					if (!checks.isLiquid())
						finalHeader.remove("liquid");
					else
						tgssCell++;
					
					if (!checks.isTotalSS())
						finalHeader.remove("totalSS");
					
					entQuoteFirstCell= tgssCell+2;
					empQuoteFirstCell = entQuoteFirstCell+1;
					if (excelType.isComplete()) {
						if (!checks.isCgcEnterprise())
						finalHeader.remove("cgcEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isCgpEnterprise())
							finalHeader.remove("cgpEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isUnemploymentEnterprise())
							finalHeader.remove("unemploymentEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isJobTrainingEnterprise())
							finalHeader.remove("jobTrainingEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isFogasaEnterprise())
							finalHeader.remove("fogasaEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isExtraHEnterprise())
							finalHeader.remove("extraHEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isBonuses())
							finalHeader.remove("bonuses");
						else
							empQuoteFirstCell++;
						if (!checks.isFundae())
							finalHeader.remove(FUNDAE);
						else
							empQuoteFirstCell++;
						
						
						if (!checks.isCgcBase())
							finalHeader.remove("cgcBase");
						if (!checks.isIrpfBase())
							finalHeader.remove("irpfBase");
						if (!checks.isMoneyIrpfBase())
							finalHeader.remove("moneyIrpfBase");
						if (!checks.isInKindIrpfBase())
							finalHeader.remove("inKindIrpfBase");
						
						
						
						if (!checks.isCgc())
							finalHeader.remove("cgc");
	
						if (!checks.isCgp())
							finalHeader.remove("cgp");
	
						if (!checks.isUnemployment())
							finalHeader.remove("unemployment");
	
						if (!checks.isJobTraining())
							finalHeader.remove("jobTraining");
	
						if (!checks.isAdvancedPayment())
							finalHeader.remove("advancedPayments");
	
						if (!checks.isOtherDeductions())
							finalHeader.remove("otherDeductions");
						if (!checks.isExtraH())
							finalHeader.remove("extraH");
						
						
						if (!checks.isEmbargos())
							finalHeader.remove("embargos");
						
					}
					

					row = sheet.createRow(2);

					Iterator<String> headersIt = finalHeader.keySet().iterator();

					int c = 0;

					while (headersIt.hasNext()) {
						Cell cell = row.createCell(c);
						String key = headersIt.next();
						String cellValue = finalHeader.get(key);

						if (key.equals("raw"))
							rawColumn = CellReference.convertNumToColString(c);
						else if (key.equals("enterpriseSS"))
							enterpriseSSColumn = CellReference.convertNumToColString(c);
//						else if (key.equals("bonuses"))
//							bonusColumn = CellReference.convertNumToColString(c);
						else if (key.equals("employeeSS"))
							employeeSSColumn = CellReference.convertNumToColString(c);
						else if (key.equals("otherDeductions"))
							otherDecutionsColumn = CellReference.convertNumToColString(c); 
						else if (key.equals("advancedPayments"))
							advancedPaymentsColumn= CellReference.convertNumToColString(c);
						else if (key.equals(FUNDAE))
							fundaeCell = c;

						cell.setCellValue(cellValue);
						if (AonStringUtils.containsIgnoreCase(key, "joint")) {
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
							joints.add(c);
						}
						else if (key.equals("totalCost") || key.equals("liquid") || key.equals("totalSS")) {
							importantCells.add(c);
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						}
						else
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						
						c++;
					}

				

				LinkedList<Integer> normalRows = new LinkedList<>();
				LinkedList<Integer> totalRows = new LinkedList<>();
				HashSet<Integer> ssCols = new HashSet<>();
				
				Map<String, Map<SalaryType, IEnterprisePayroll>> dataByPeriod = completeWorkplaceData.get(sheetKey);
				
				
				for (String month : orderedMonths) {
					List<Integer> rowsForMonthTotal = new LinkedList<>();
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					int monthRowInd = row.getRowNum();
					Cell nameCell = row.createCell(0);
					nameCell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE));
					nameCell.setCellValue(month);
					
					if (dataByPeriod.containsKey(month)) {
						
						Map<SalaryType, IEnterprisePayroll> dataByType = dataByPeriod.get(month);
						
						for (SalaryType salaryType : dataByType.keySet()) {
							IEnterprisePayroll payroll = dataByType.get(salaryType);
							row = sheet.createRow(sheet.getLastRowNum() + 1);
							rowsForMonthTotal.add(row.getRowNum() + 1);
							Cell leftCell = row.createCell(0);
							leftCell.setCellValue(month);
							leftCell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE_WHITE_BACK));
							
							leftCell = row.createCell(1);
							leftCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
							
							
							Cell typeCell = row.createCell(2);
							typeCell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
							typeCell.setCellValue(getSalaryTypeName(salaryType));
							
							int column = 0;
							
							normalRows.add(row.getRowNum() + 1);
							//EMPLOYEE NAME
//							writeEmployee(stylesMap, row, column++, payroll, excelType);
							column++;
							column++;
							column++;
							//SALARY TYPE
							
							PayrollCellStyle style = PayrollCellStyle.DOUBLE_CELL_STYLE;
							if (checks.isRaw()) {
								createDoubleCell(row, column++, payroll.getRaw(), stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
									
							}
							
							if (checks.isEnterpriseSS()) {
								CellStyle sti = stylesMap.get(style);
								ssCols.add(column);
								createDoubleCell(row, column++, payroll.getEnterpriseSS(), sti);
							
								
							}

							if (checks.isTotalCost()) {
								int rowNum = row.getRowNum() + 1;
								Cell totalCostCell = row.createCell(column++);
								totalCostCell.setCellType(CellType.FORMULA);
								if (!checks.isRaw())
									totalCostCell.setCellFormula(enterpriseSSColumn + rowNum);
								else if (!checks.isEnterpriseSS())
									totalCostCell.setCellFormula(rawColumn + rowNum);
								else
									totalCostCell.setCellFormula(rawColumn + rowNum + "+" + enterpriseSSColumn + rowNum);
								totalCostCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
							}
							
							writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
							
							if (checks.isEmployeeSS()) {
								
								CellStyle sti = stylesMap.get(style);
								ssCols.add(column);
								if (column == entFirstCell - 1) {
									style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
									sti = stylesMap.get(style);
								}
									createDoubleCell(row, column++, payroll.getEmployeeSS(), sti);
							}
							
							if (checks.isIrpf()) {
								if (column == entFirstCell - 1)
									style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
								createDoubleCell(row, column++, payroll.getIrpf(), stylesMap.get(style));
							}
							
							if (checks.isOther()) {
								int rowNum = row.getRowNum() + 1;
								Cell otherCell = row.createCell(column++, CellType.FORMULA);
								String formula = "";
								if (excelType.isComplete())
									formula = (advancedPaymentsColumn!=null?advancedPaymentsColumn+rowNum+"+":"")
										+(otherDecutionsColumn!=null?otherDecutionsColumn+rowNum:"0");
								else if (!excelType.isComplete())
									formula = (payroll.getAdvancedPayment()!=null?payroll.getAdvancedPayment()+"+":"")
									+(payroll.getOtherDeductions()!=null?payroll.getOtherDeductions():"0");
								otherCell.setCellFormula(formula);
								otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
							}
							
							if (checks.isLiquid()) {
								if (column == entFirstCell - 1)
									style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
								createDoubleCell(row, column++, payroll.getLiquid(), stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
									
							}
							
							//JOINT
							writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
							
							if (checks.isTotalSS()) {
								
								int rowNum = row.getRowNum() + 1;
								Cell totalCostCell;
								ssCols.add(column);
								
								totalCostCell = row.createCell(column++, CellType.FORMULA);
								String formula = "";
								if (checks.isEnterpriseSS() && !checks.isEmployeeSS())
									formula += enterpriseSSColumn + rowNum;
								else if (!checks.isEnterpriseSS() && checks.isEmployeeSS())
									formula += employeeSSColumn + rowNum;
								else if (checks.isEnterpriseSS() && checks.isEmployeeSS())
									formula += employeeSSColumn + rowNum + "+" + enterpriseSSColumn + rowNum;
								
								totalCostCell.setCellFormula(formula);
								
								totalCostCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));

							}
							if (excelType.isComplete()) {
								writeCompleteDetails(stylesMap, row, checks, payroll, workplaceContractDataMap, column, false, style, ssCols);
							}
							
							numberOfColumns = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
							
						}
						
						
						
					}
					
					if (!rowsForMonthTotal.isEmpty()) {
						row = sheet.getRow(monthRowInd);
						LinkedHashMap<Integer, Double> fixedCells = new LinkedHashMap<>();
						if (fundaeCell > 0) {
							
							Map<String, Double> contractDatas = workplaceContractDataMap.containsKey(month) ? workplaceContractDataMap.get(month) : Collections.emptyMap();
							if (contractDatas.containsKey(FUNDAE)) {
								Double value = contractDatas.get(FUNDAE);
								fixedCells.put(fundaeCell, value);
							} else {
								fixedCells.put(fundaeCell, 0d);
							}
						}
						int totRow = writeMonthTotals(stylesMap, sheet, joints, importantCells, rowsForMonthTotal, row, month, fixedCells);
						totalRows.add(totRow + 1);
					} else {
						row = sheet.getRow(monthRowInd);
						
						Cell otherCell = row.createCell(1);
						otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
						
						otherCell = row.createCell(2);
						otherCell.setCellType(CellType.BLANK);
						otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE));
						
						int cellInd = 3;
						
						for (; cellInd<finalHeader.size(); cellInd++) {
							List<String> hList = new LinkedList<>();
							hList.addAll(finalHeader.keySet());
							
							if (!hList.get(cellInd).contains("join")) {
								Cell totCell = row.createCell(cellInd);
								totCell.setCellType(CellType.FORMULA);
								totCell.setCellFormula("0");
								if (!IMPORTANT_CELLS.contains(hList.get(cellInd)) || hList.get(cellInd).equals("raw"))
									totCell.setCellStyle(stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE));
								else
									totCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE));
									
							} else {
								Cell totCell = row.createCell(cellInd);
								totCell.setCellType(CellType.BLANK);
								totCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
							}
						}
						otherCell = row.createCell(cellInd);
						otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_LEFT_CELL_STYLE));
						
						
					}
					
					
					
					TotalsReferences refs = new TotalsReferences(checks, sheetKey, (dataByPeriod.containsKey(month)) ? row.getRowNum() : null);
					
					if (totalsSchema.containsKey(month)) {
						List<TotalsReferences> referenceList = totalsSchema.get(month);
						referenceList.add(refs);
					} else {
						List<TotalsReferences> referenceList = new LinkedList<>();
						referenceList.add(refs);						
						totalsSchema.put(month, referenceList);
					}
					
				}
				
				
				Integer lastCell = numberOfColumns - 1;

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCell));
				row = sheet.createRow(0);
				Cell enterpriseCell = row.createCell(0);
				enterpriseCell.setCellType(CellType.STRING);
				enterpriseCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				enterpriseCell.setCellValue(enterpriseName + " - " + dateString);
				//WORKPLACE'S 2ND HEADER ROW 
				
				row = sheet.createRow(1);
//				sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, entFirstCell-1));
				sheet.addMergedRegion(new CellRangeAddress(1, 1, entFirstCell, empFirstCell - 2));
				sheet.addMergedRegion(new CellRangeAddress(1, 1, empFirstCell, tgssCell - 2));
				
				
				Cell entCell = row.createCell(entFirstCell, CellType.STRING);
				entCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				entCell.setCellValue("EMPRESA");
				
				Cell jointCell = row.createCell(empFirstCell-1, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				Cell epCell = row.createCell(empFirstCell, CellType.STRING);
				epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				epCell.setCellValue("EMPLEADO");
				
				jointCell = row.createCell(tgssCell-1, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				if (checks.isTotalSS()) {
					Cell tgCell = row.createCell(tgssCell, CellType.STRING);
					tgCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
					tgCell .setCellValue("TGSS");
				}
				
				if (excelType.isComplete()) {
					jointCell = row.createCell(empFirstCell-1, CellType.STRING);
					jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
					
					if(empQuoteFirstCell-2 > entQuoteFirstCell) {
						sheet.addMergedRegion(new CellRangeAddress(1, 1, entQuoteFirstCell, empQuoteFirstCell-2));
						Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
						entQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
					} else if (empQuoteFirstCell-2 == entQuoteFirstCell) {
						Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
						entQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
					}
					
					jointCell = row.createCell(empQuoteFirstCell-1, CellType.STRING);
					jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
					
					sheet.addMergedRegion(new CellRangeAddress(1, 1, empQuoteFirstCell, lastCell));
					
					Cell empQuoteCell = row.createCell(empQuoteFirstCell, CellType.STRING);
					empQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
					empQuoteCell.setCellValue("COTIZACIÓN EMPLEADO");
				}
					
					LinkedHashMap<Integer, Double> fixedCells = new LinkedHashMap<>(); 
					
//					if (fundaeCell > 0 && optWorkplaceContractData.isPresent()) {
//						Map<String, Double> workplaceContractData = optWorkplaceContractData.get();
//						if (workplaceContractData.containsKey(FUNDAE)) {
//							double fundaeValue = workplaceContractData.get(FUNDAE) != null ? workplaceContractData.get(FUNDAE) : 0;
//							fixedCells.put(fundaeCell, fundaeValue);
//						}
//					}
				
				
				
				//WORKPLACE'S TOTALS
				writeWorkplaceTotals(stylesMap, sheet, joints, importantCells, totalRows, excelType, true, fixedCells);

				for (int i = 0; i < finalHeader.size(); i++) {
					sheet.autoSizeColumn(i);
				}
				
				//WORKPLACE'S FINAL BORDER
				for (int i = 0; i<=sheet.getLastRowNum(); i++) {
					Row r = sheet.getRow(i);
					Cell borderCell = r.createCell(finalHeader.size());
					borderCell.setCellStyle(wb.createCellStyle());
					borderCell.getCellStyle().setBorderLeft(BorderStyle.THIN);
				}
				
			} catch (java.lang.IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		completeTotals(wb, stylesMap, totals, excelType, totalsSchema, completeLength, summaryLength);
		
		wb.write(outputStream);
		outputStream.close();
		wb.close();
	}	
	
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<LinkedHashMap<String, String>> header, String enterpriseName, Date startDate, Date endDate, ExcelType excelType)
			throws IOException {
		
		String dateString = EnterprisePayrollExcelUtils.getAppropiatePeriodString(startDate, endDate);
		write(outputStream, payrolls, header, enterpriseName, dateString, excelType);
	}

	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<LinkedHashMap<String, String>> header, String enterpriseName, String dateString, ExcelType excelType)
			throws IOException {
		
		Workbook wb = new XSSFWorkbook();

		DataFormat format = wb.createDataFormat();

		Map<PayrollCellStyle, CellStyle> stylesMap = PayrollCellStyle.getStyles(wb, format);
		HashSet<Integer> totalsSSRows = new HashSet<>();
		
		final int completeLength = 34;
		final int summaryLength = 11;

		Row row = null;

		Sheet totals = null;
		
		
		List<IEnterprisePayroll> normal = payrolls.stream().filter(s -> s.getSalaryType() == null || s.getSalaryType().ordinal() <= SalaryType.DELAY.ordinal()).collect(Collectors.toList());
		List<IEnterprisePayroll> diff = payrolls.stream().filter(s -> s.getSalaryType() != null && s.getSalaryType().ordinal() > SalaryType.DELAY.ordinal()).collect(Collectors.toList());
		
		
		List<String> workplaces = normal.stream().map(IEnterprisePayroll::getWorkplace).distinct().collect(Collectors.toList());
		
		totals = initializeTotalsSheet(header, enterpriseName, dateString, excelType, wb, stylesMap, completeLength,
				summaryLength, totals, workplaces);

		Iterator<String> it = payrolls.stream().map(IEnterprisePayroll::getWorkplace).distinct().sorted((w1, w2) -> {
			String str1 = w1 != null ? w1 : "";
			String str2 = w2 != null ? w2 : "";
			return str1.compareTo(str2);
		}).iterator();

		while (it.hasNext()) {
			try {
				String workplace = it.next();
				LinkedList<IEnterprisePayroll> ordered = new LinkedList<>();
				List<IEnterprisePayroll> spare = diff.stream()
					.filter(p -> p.getWorkplace().equals(workplace))
					.filter(p -> !(normal.stream().anyMatch(np ->(np.getStartDate() != null && np.getStartDate().equals(p.getStartDate()) 
						&& (np.getEndDate() != null && np.getEndDate().equals(p.getEndDate())) 
						&& (np.getEmployeeNaf() != null && np.getEmployeeNaf().equals(p.getEmployeeNaf()))
						&& (p.getCcc() != null && p.getCcc().equals(np.getCcc()))
						&& isRelatedSalaryType(np.getSalaryType(), p.getSalaryType()))
					)
					)).collect(Collectors.toList());
				
				normal.stream()
						.filter(payroll -> payroll.getWorkplace().equals(workplace))
						.forEach(pay -> {
							ordered.add(pay);
							diff.stream().filter(p -> 
							(p.getStartDate() != null && p.getStartDate().equals(pay.getStartDate()) 
							&& (p.getEndDate() != null && p.getEndDate().equals(pay.getEndDate())) 
							&& (p.getEmployeeNaf() != null && p.getEmployeeNaf().equals(pay.getEmployeeNaf())))
							&& (p.getCcc() != null && p.getCcc().equals(pay.getCcc()))
							&& isRelatedSalaryType(pay.getSalaryType(), p.getSalaryType()))
							.forEach(p -> ordered.add(p.setOriginalPayroll(pay)));
							
						});

				
				ordered.addAll(spare);
				
				
				if (excelType.equals(ExcelType.EMPLOYEE_COMPLETE) || excelType.equals(ExcelType.EMPLOYEE_SUMMARY)) {					
					ordered.sort((e1, e2) -> e1.getEmployee().compareTo(e2.getEmployee()));
				} else if (excelType.equals(ExcelType.PERIOD_COMPLETE) || excelType.equals(ExcelType.PERIOD_SUMMARY) ) {
					ordered.sort((e1, e2) -> {
						Date d1 = e1.getEndDate();
						Date d2 = e2.getEndDate();
						if (d1 == null && d2 == null)
							return 0;
						else if (d1 == null)
							return -1;
						else if (d2 == null)
							return 1;
						else {
							return d1.compareTo(d2);
						}
					});					
				}
				
				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(workplace));
				EnterprisePayrollExcelChecks checks = new EnterprisePayrollExcelChecks(ordered);
				
				LinkedHashMap<String, String> finalHeader = new LinkedHashMap<>();

				if (header.isPresent()) {
					LinkedHashMap<String, String> customHeader = header.get();
					finalHeader.putAll(customHeader);
				} else {
					if (excelType == ExcelType.COMPLETE)
						finalHeader.putAll(DEFAULT_HEADER);
					else if (excelType == ExcelType.SUMMARY)
						finalHeader.putAll(DEFAULT_HEADER_SUMMARY);
					else if (excelType == ExcelType.EMPLOYEE_SUMMARY || excelType == ExcelType.PERIOD_SUMMARY)
						finalHeader.putAll(DEFAULT_HEADER_NO_TYPE_SUMMARY);
					else if (excelType == ExcelType.EMPLOYEE_COMPLETE || excelType == ExcelType.PERIOD_COMPLETE)
						finalHeader.putAll(DEFAULT_HEADER_NO_TYPE);
				}

				String rawColumn = null;
				String enterpriseSSColumn = null;
				String employeeSSColumn = null;
//				String bonusColumn = null;
				String otherDecutionsColumn = null;
				String advancedPaymentsColumn = null;
				
				ArrayList<Integer> joints = new ArrayList<>(5);
				ArrayList<Integer> importantCells = new ArrayList<>(3);
				
				
				int empFirstCell = excelType.isWithType() ? 3 : 2;
				int entFirstCell = excelType.isWithType() ? 2 : 1;
				int tgssCell = excelType.isWithType() ? 4 : 3;
				int entQuoteFirstCell;
				int empQuoteFirstCell;
				

					if (!checks.isRaw())
						finalHeader.remove("raw");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!checks.isEnterpriseSS())
						finalHeader.remove("enterpriseSS");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!checks.isTotalCost())
						finalHeader.remove("totalCost");
					else {
						empFirstCell++;
						tgssCell++;
					}
					
					if (!checks.isEmployeeSS())
						finalHeader.remove("employeeSS");
					else
						tgssCell++;
					if (!checks.isIrpf())
						finalHeader.remove("irpf");
					else
						tgssCell++;
					if (!checks.isOther())
						finalHeader.remove("other");
					else
						tgssCell++;
					if (!checks.isLiquid())
						finalHeader.remove("liquid");
					else
						tgssCell++;
					
					if (!checks.isTotalSS())
						finalHeader.remove("totalSS");
					
					entQuoteFirstCell= tgssCell+2;
					empQuoteFirstCell = entQuoteFirstCell+1;
					if (excelType.isComplete()) {
						if (!checks.isCgcEnterprise())
						finalHeader.remove("cgcEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isCgpEnterprise())
							finalHeader.remove("cgpEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isUnemploymentEnterprise())
							finalHeader.remove("unemploymentEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isJobTrainingEnterprise())
							finalHeader.remove("jobTrainingEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isFogasaEnterprise())
							finalHeader.remove("fogasaEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isExtraHEnterprise())
							finalHeader.remove("extraHEnterprise");
						else
							empQuoteFirstCell++;
						if (!checks.isBonuses())
							finalHeader.remove("bonuses");
						else
							empQuoteFirstCell++;
						
						
						if (!checks.isCgcBase())
							finalHeader.remove("cgcBase");
						if (!checks.isIrpfBase())
							finalHeader.remove("irpfBase");
						if (!checks.isMoneyIrpfBase())
							finalHeader.remove("moneyIrpfBase");
						if (!checks.isInKindIrpfBase())
							finalHeader.remove("inKindIrpfBase");
						
						
						
						if (!checks.isCgc())
							finalHeader.remove("cgc");
	
						if (!checks.isCgp())
							finalHeader.remove("cgp");
	
						if (!checks.isUnemployment())
							finalHeader.remove("unemployment");
	
						if (!checks.isJobTraining())
							finalHeader.remove("jobTraining");
	
						if (!checks.isAdvancedPayment())
							finalHeader.remove("advancedPayments");
	
						if (!checks.isOtherDeductions())
							finalHeader.remove("otherDeductions");
						if (!checks.isExtraH())
							finalHeader.remove("extraH");
						
						
						if (!checks.isEmbargos())
							finalHeader.remove("embargos");
					}
					

					row = sheet.createRow(2);

					Iterator<String> headersIt = finalHeader.keySet().iterator();

					int c = 0;

					while (headersIt.hasNext()) {
						Cell cell = row.createCell(c);
						String key = headersIt.next();
						String cellValue = finalHeader.get(key);

						if (key.equals("raw"))
							rawColumn = CellReference.convertNumToColString(c);
						else if (key.equals("enterpriseSS"))
							enterpriseSSColumn = CellReference.convertNumToColString(c);
//						else if (key.equals("bonuses"))
//							bonusColumn = CellReference.convertNumToColString(c);
						else if (key.equals("employeeSS"))
							employeeSSColumn = CellReference.convertNumToColString(c);
						else if (key.equals("otherDeductions"))
							otherDecutionsColumn = CellReference.convertNumToColString(c); 
						else if (key.equals("advancedPayments"))
							advancedPaymentsColumn= CellReference.convertNumToColString(c);

						cell.setCellValue(cellValue);
						if (AonStringUtils.containsIgnoreCase(key, "joint")) {
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
							joints.add(c);
						}
						else if (key.equals("totalCost") || key.equals("liquid") || key.equals("totalSS")) {
							importantCells.add(c);
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						}
						else
							cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						
						c++;
					}

				

				LinkedList<Integer> normalRows = new LinkedList<>();
				LinkedList<Integer> ssRows = new LinkedList<>();
				HashSet<Integer> ssCols = new HashSet<>();
				
				
				
				int lastNoDiffRowNum = 0;
				for (IEnterprisePayroll payroll : ordered) {
					int column = 0;
					
					boolean isDiff = payroll != null 
							&& payroll.getSalaryType() != null  
							&& payroll.getSalaryType().ordinal() > SalaryType.DELAY.ordinal();		
					
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					if (!isDiff) {
						lastNoDiffRowNum = row.getRowNum();
						normalRows.add(row.getRowNum() + 1);
					} else {
						ssRows.add(row.getRowNum() + 1);						
					}
					//EMPLOYEE NAME
					writeEmployee(stylesMap, row, column++, payroll, excelType);
					//SALARY TYPE
					if (excelType.isWithType()) {						
						writeSalaryType(
								isDiff
								, stylesMap
								, row
								, column++
								, payroll);
					}
					
					PayrollCellStyle style = PayrollCellStyle.DOUBLE_CELL_STYLE;
					if (checks.isRaw()) {
						if (!isDiff)
							createDoubleCell(row, column++, payroll.getRaw(), stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
						else {
							createDoubleCell(row, column++, null, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
						}
							
					}
					//TODO:NOT SURE
					if (checks.isEnterpriseSS()) {
//						Cell fCell = row.createCell(column++, CellType.FORMULA);
//						String formula = ""+payroll.getEnterpriseSS()!=null?""+payroll.getEnterpriseSS():"0";
//						if (thereIsBonuses && excelType == ExcelType.COMPLETE)
//							formula+="+"+bonusColumn+(row.getRowNum()+1);
//						else if (thereIsBonuses && excelType == ExcelType.SUMMARY)
//							formula+="-"+payroll.getBonuses();
//						fCell.setCellFormula(formula);
						CellStyle sti = stylesMap.get(style);
						ssCols.add(column);
						if (payroll.getOriginalPayroll() != null){
							double difference = Math.abs(
							AonNumberUtils.zeroIfNull(payroll.getOriginalPayroll().getEnterpriseSS())
							- AonNumberUtils.zeroIfNull(payroll.getEnterpriseSS())
							);
							sti = selectColor(difference
									, stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE)
									, stylesMap.get(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE)
									, stylesMap.get(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE));
								
						} else if (isDiff && payroll.getOriginalPayroll() == null)
							sti = stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE);
						createDoubleCell(row, column++, payroll.getEnterpriseSS(), sti);
					
						
					}

					if (checks.isTotalCost()) {
						int rowNum = row.getRowNum() + 1;
						Cell totalCostCell = row.createCell(column++);
						if (!isDiff) {
							totalCostCell.setCellType(CellType.FORMULA);
							if (!checks.isRaw())
								totalCostCell.setCellFormula(enterpriseSSColumn + rowNum);
							else if (!checks.isEnterpriseSS())
								totalCostCell.setCellFormula(rawColumn + rowNum);
							else
								totalCostCell.setCellFormula(rawColumn + rowNum + "+" + enterpriseSSColumn + rowNum);
							
						}
						totalCostCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
					}
					
					writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
					
					if (checks.isEmployeeSS()) {
						
						CellStyle sti = stylesMap.get(style);
						ssCols.add(column);
						if (payroll.getOriginalPayroll() != null){
							double difference = Math.abs(
							AonNumberUtils.zeroIfNull(payroll.getOriginalPayroll().getEmployeeSS())
							- AonNumberUtils.zeroIfNull(payroll.getEmployeeSS())
							);
							if (column == entFirstCell - 1) {
								sti = selectColor(difference
										, stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_RED)
										, stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_ORANGE)
										, stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_GREEN));								
							} else {
								sti = selectColor(difference
										, stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE));								
							}
								
						} else if (column == entFirstCell - 1) {
							style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
							sti = stylesMap.get(isDiff ? PayrollCellStyle.BOUND_CELL_STYLE_PREV_RED : style);
						} else if (isDiff) {
							sti = stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE);
						}
							createDoubleCell(row, column++, payroll.getEmployeeSS(), sti);
					}
					
					if (checks.isIrpf()) {
						if (column == entFirstCell - 1)
							style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
						if (!isDiff)
							createDoubleCell(row, column++, payroll.getIrpf(), stylesMap.get(style));
						else
							createDoubleCell(row, column++, null, stylesMap.get(style));
					}
					
					if (checks.isOther()) {
						int rowNum = row.getRowNum() + 1;
						Cell otherCell = row.createCell(column++, CellType.FORMULA);
						String formula = "";
						if (excelType.isComplete())
							formula = (advancedPaymentsColumn!=null?advancedPaymentsColumn+rowNum+"+":"")
								+(otherDecutionsColumn!=null?otherDecutionsColumn+rowNum:"0");
						else if (!excelType.isComplete())
							formula = (payroll.getAdvancedPayment()!=null?payroll.getAdvancedPayment()+"+":"")
							+(payroll.getOtherDeductions()!=null?payroll.getOtherDeductions():"0");
						otherCell.setCellFormula(formula);
						otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
					}
					
					if (checks.isLiquid()) {
						if (column == entFirstCell - 1)
							style = PayrollCellStyle.BOUND_CELL_STYLE_PREV;
						if (!isDiff)
							createDoubleCell(row, column++, payroll.getLiquid(), stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
						else
							createDoubleCell(row, column++, null, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
							
					}
					
					//JOINT
					writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
					
					if (checks.isTotalSS()) {
						
						int rowNum = row.getRowNum() + 1;
						Cell totalCostCell;
						ssCols.add(column);
						if (isDiff) {
							double diffAmount = AonNumberUtils.zeroIfNull(payroll.getEmployeeSS()) + AonNumberUtils.zeroIfNull(payroll.getEnterpriseSS());
							CellStyle sti = null;
							Double originAmount = null;
							if (payroll.getOriginalPayroll() != null) {
								IEnterprisePayroll origin = payroll.getOriginalPayroll();
								originAmount = AonNumberUtils.zeroIfNull(origin.getEmployeeSS()) + AonNumberUtils.zeroIfNull(origin.getEnterpriseSS());
								double difference = Math.abs(diffAmount - originAmount);
								sti = selectColor(difference, stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE), stylesMap.get(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE),
										stylesMap.get(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE));
								if (difference > 0.01) {
									sheet.getRow(lastNoDiffRowNum).getCell(0).setCellStyle(stylesMap.get(PayrollCellStyle.RED_STRING_CELL_STYLE));
								}
							} else {
								sti = stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE);
							}
							createDoubleCell(row, column++, diffAmount, sti);
							
						} else {							
							totalCostCell = row.createCell(column++, CellType.FORMULA);
							String formula = "";
							if (checks.isEnterpriseSS() && !checks.isEmployeeSS())
								formula += enterpriseSSColumn + rowNum;
							else if (!checks.isEnterpriseSS() && checks.isEmployeeSS())
								formula += employeeSSColumn + rowNum;
							else if (checks.isEnterpriseSS() && checks.isEmployeeSS())
								formula += employeeSSColumn + rowNum + "+" + enterpriseSSColumn + rowNum;
							
							totalCostCell.setCellFormula(formula);
							
							totalCostCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE));
						}

					}
					if (excelType.isComplete()) {
						writeCompleteDetails(stylesMap, row, checks, payroll, Collections.emptyMap(), column, isDiff, style, ssCols);
					}
					
				}

				Integer lastCell = row.getLastCellNum() - 1;

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCell));
				row = sheet.createRow(0);
				Cell enterpriseCell = row.createCell(0);
				enterpriseCell.setCellType(CellType.STRING);
				enterpriseCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				enterpriseCell.setCellValue(enterpriseName + " - " + dateString);
				//WORKPLACE'S 2ND HEADER ROW 
				
				row = sheet.createRow(1);
//				sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, entFirstCell-1));
				sheet.addMergedRegion(new CellRangeAddress(1, 1, entFirstCell, empFirstCell - 2));
				sheet.addMergedRegion(new CellRangeAddress(1, 1, empFirstCell, tgssCell-2));
				
				
				Cell entCell = row.createCell(entFirstCell, CellType.STRING);
				entCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				entCell.setCellValue("EMPRESA");
				
				Cell jointCell = row.createCell(empFirstCell-1, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				Cell epCell = row.createCell(empFirstCell, CellType.STRING);
				epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				epCell.setCellValue("EMPLEADO");
				
				jointCell = row.createCell(tgssCell-1, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				if (checks.isTotalSS()) {
					Cell tgCell = row.createCell(tgssCell, CellType.STRING);
					tgCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
					tgCell .setCellValue("TGSS");
				}
				
				if (excelType.isComplete()) {
					jointCell = row.createCell(empFirstCell-1, CellType.STRING);
					jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
					
					if(empQuoteFirstCell-2 > entQuoteFirstCell) {
						sheet.addMergedRegion(new CellRangeAddress(1, 1, entQuoteFirstCell, empQuoteFirstCell-2));
						Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
						entQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
					} else if (empQuoteFirstCell-2 == entQuoteFirstCell) {
						Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
						entQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
						entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
					}
					
					jointCell = row.createCell(empQuoteFirstCell-1, CellType.STRING);
					jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
					
					sheet.addMergedRegion(new CellRangeAddress(1, 1, empQuoteFirstCell, lastCell));
					
					Cell empQuoteCell = row.createCell(empQuoteFirstCell, CellType.STRING);
					empQuoteCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
					empQuoteCell.setCellValue("COTIZACIÓN EMPLEADO");
				}
					

					

				
				//WORKPLACE'S TOTALS
				int totalsRow = writeWorkplaceTotals(stylesMap, sheet, joints, importantCells, normalRows, excelType);

				//WORKPLACE'S SS TOTALS
				if (ssRows != null && !ssRows.isEmpty())
					writeWorkplaceSSTotals(wb, stylesMap, sheet, joints, importantCells, ssRows, ssCols);
				
				

				// TOTALS (IN THE FIRST SHEET)
				totalsSSRows.add(writeTotals(excelType, wb, stylesMap, totals, workplace, checks, totalsRow, ssRows != null && !ssRows.isEmpty()));

				for (int i = 0; i < finalHeader.size(); i++) {
					sheet.autoSizeColumn(i);
				}
				
				//TOTALS' FINAL BORDER
				if (totals != null) {
					for (int i = 0; i<=totals.getLastRowNum(); i++) {
//						if (!totalsSSRows.contains(i)) {
							Row r = totals.getRow(i);
							int lCell = excelType.isComplete() ? completeLength + 1 : summaryLength +1;
							Cell borderCell = r.createCell(lCell);
							borderCell.setCellStyle(wb.createCellStyle());
							borderCell.getCellStyle().setBorderLeft(BorderStyle.THIN);							
//						}
					}
					
				}
				//WORKPLACE'S FINAL BORDER
				for (int i = 0; i<=sheet.getLastRowNum(); i++) {
					Row r = sheet.getRow(i);
					Cell borderCell = r.createCell(finalHeader.size());
					borderCell.setCellStyle(wb.createCellStyle());
					borderCell.getCellStyle().setBorderLeft(BorderStyle.THIN);
				}
				
			} catch (java.lang.IllegalArgumentException e) {}
		}

		if (totals != null) {
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			evaluator.evaluateAll();
			Sheet totSheet = totals;
			totalsSSRows.stream().filter(r -> r > 0).forEach(r -> {	
				Cell closeFirst = totSheet.getRow(r).createCell(1);
				CellStyle rightBorder = wb.createCellStyle();
				rightBorder.setBorderRight(BorderStyle.THIN);
				closeFirst.setCellStyle(rightBorder);
			});
			
			int length = -1;
			if (excelType.isComplete())
				length = completeLength;
			else
				length = summaryLength;
			
			for (int i = 0; i <= length; i++) {
				int col = i;
				totalsSSRows.stream().filter(r -> r > 0).forEach(r -> {						
					if (totSheet.getRow(r).getCell(col) == null) {
						Cell c = totSheet.getRow(r).createCell(col);
						c.setCellStyle(stylesMap.get(PayrollCellStyle.BLANK_DIFF_CELL_STYLE));
					}
				});
				totals.autoSizeColumn(i);
				if (totals.getColumnWidth(i) > 256)
					totals.setColumnWidth(i, totals.getColumnWidth(i) + 256);
			}

			
			Row prev = totals.getRow(totals.getLastRowNum());
			Row finale = totals.createRow(totals.getLastRowNum() + 1);
			CellStyle closingStyle = wb.createCellStyle();
			closingStyle.setBorderTop(BorderStyle.THIN);
			for (int i = 2; i <= (!excelType.isComplete() ? summaryLength : completeLength); i++) {
				if (prev.getCell(i) == null || (prev.getCell(i) != null && !prev.getCell(i).getCellStyle().equals(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE))))
					finale.createCell(i).setCellStyle(closingStyle);
			}
			
		}
		wb.write(outputStream);
		outputStream.close();
		wb.close();
	}

	
	public static Map<String, Map<String, List<ContractData>>> getContractDataByWorkplace(AONContext aonContext, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId) {
		
		if (startDate == null || endDate == null)
			return null;
		
		java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
		
		SelectConditionStep<Record> query = aonContext.getDslContext()
		.select(WORKPLACE.DESCRIPTION, CONTRACT_DATA.asterisk())
		.from(CONTRACT)
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE.REGISTRY))
		.leftJoin(CONTRACT_DATA).on(CONTRACT.ID.eq(CONTRACT_DATA.CONTRACT))
			.and(CONTRACT_DATA.END_DATE.ge(sqlStartDate))
			.and(CONTRACT_DATA.END_DATE.le(sqlEndDate))
			.and(CONTRACT_DATA.NAME.eq(FUNDAE))
		.where(CONTRACT.START_DATE.ge(sqlStartDate))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.le(sqlEndDate)))
		.and(ENTERPRISE.REGISTRY.eq(enterpriseId))
		;
		
		if (workplaceId != null && workplaceId > 0) {
			query = query.and(WORKPLACE.ID.eq(workplaceId));
		}
		
		LinkedHashMap<String, Map<String, List<ContractData>>> contractDataMap = new LinkedHashMap<>();
		
		query.fetchStream()
		.filter(Objects::nonNull)
		.forEach(res -> {
			String name = res.get(CONTRACT_DATA.NAME);
			String workplace = res.get(WORKPLACE.DESCRIPTION);
			Double value = null;
			if (res.get(CONTRACT_DATA.EXPRESSION) != null) {
				try {
					value = Double.parseDouble(res.get(CONTRACT_DATA.EXPRESSION));
				} catch (NumberFormatException e) {
					value  = 0d;
				}
			}
			
			
			ContractData cd = new ContractData()
				.setDomain(res.get(CONTRACT_DATA.DOMAIN))
				.setContract(res.get(CONTRACT_DATA.CONTRACT))
				.setEndDate(res.get(CONTRACT_DATA.END_DATE))
				.setStartDate(res.get(CONTRACT_DATA.START_DATE))
				.setExpression(res.get(CONTRACT_DATA.EXPRESSION))
				.setId(res.get(CONTRACT_DATA.ID))
				.setName(res.get(CONTRACT_DATA.NAME));
			
			
			if (res.get(CONTRACT_DATA.NAME) != null) {
				if (contractDataMap.containsKey(workplace)) {
					Map<String, List<ContractData>> contractData = contractDataMap.get(workplace);
					if (contractData.containsKey(name)) {
						contractData.get(name).add(cd);
					} else {
						LinkedList<ContractData> cdList = new LinkedList<>();
						cdList.add(cd);
						contractData.put(name, cdList);
					}
				} else {
					LinkedHashMap<String, List<ContractData>> contractData = new LinkedHashMap<>();
					LinkedList<ContractData> cdList = new LinkedList<>();
					cdList.add(cd);
					contractData.put(name, cdList);
					contractDataMap.put(workplace, contractData);
				}
			}
		});
		
		return contractDataMap;
	}



	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId) {

		Condition condition = SALARY.ISSUE_DATE.ge(new java.sql.Date(startDate.getTime()))
				.and(SALARY.ISSUE_DATE.le(new java.sql.Date(endDate.getTime())))
				.and(ENTERPRISE.REGISTRY.eq(enterpriseId));
		if (workplaceId != null && workplaceId > 0)
			condition = condition.and(WORKPLACE.ID.eq(workplaceId));

		Map<Integer, String> workplaces = aonContext.getDslContext().select(SALARY.ID, WORKPLACE.DESCRIPTION)
				.from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey()
				.where(condition).fetchStream().collect(HashMap::new,
						(m, v) -> m.put(v.get(SALARY.ID), v.get(WORKPLACE.DESCRIPTION)), HashMap::putAll);
		
		Collection<Integer> ids = new LinkedList<>();
		Collection<Integer> contractIds = new LinkedList<>();
		aonContext.getDslContext()
			.select(SALARY.ID, WORKPLACE.DESCRIPTION)
			.from(SALARY)
			.innerJoin(CONTRACT).onKey()
			.innerJoin(WORKPLACE).onKey()
			.innerJoin(ENTERPRISE).onKey()
			.where(condition)
			.fetchStreamInto(SALARY).forEach(sr -> {
				if (sr != null && sr.getId() != null && sr.getId() > 0) {
					ids.add(sr.getId());
				}
				if (sr != null && sr.getContract() != null && sr.getContract() > 0) {
					contractIds.add(sr.getContract());			
				}
			});

		Stream<Salary> salaries = AON.getSalaries(aonContext,
				s -> s.getIdProperty().in(ids.toArray(new Integer[ids.size()])));
		
		return salaries.filter(s -> s.getSalaryType() != null).map(s -> {
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
			
			enterprisePayroll.startDate = s.getStartDate();
			enterprisePayroll.endDate = s.getEndDate();
			
			enterprisePayroll.employee = s.getEmployeeName();
			enterprisePayroll.employeeNaf = s.getEmployeeSSNumber();
			enterprisePayroll.ccc = s.getEnterpriseCCC();
			enterprisePayroll.workplace = workplaces.get(s.getId());
			
			enterprisePayroll.salaryType = s.getSalaryType();

			enterprisePayroll.irpf = s.getTotalIrpf();

			enterprisePayroll.cgcBase = s.getCommonContingenciesBase();
			enterprisePayroll.irpfBase = s.getIrpfBase();
			enterprisePayroll.inKindIrpfBase = s.getInkindIrpfBase();
			enterprisePayroll.moneyIrpfBase = s.getMoneyIrpfBase();

			enterprisePayroll.raw = s.getTotalPayment();
			enterprisePayroll.liquid = s.getTotalLiquid();
			
			Double dedBonus = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() == null && d.getAmount() < 0)
					.mapToDouble(Deduction::getAmount).sum();
					
			
			enterprisePayroll.employeeSS = s.getTotalSSContributions();
			
			if (s.getTotalSSContributions() != null && dedBonus != null)
				enterprisePayroll.employeeSS += dedBonus;
			else if (dedBonus != null)
				enterprisePayroll.employeeSS = dedBonus;
			
			enterprisePayroll.enterpriseSS = s.getTotalEnterprise();
//			enterprisePayroll.enterpriseSS = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();

			enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
			enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS /*+ enterprisePayroll.irpf*/
					+ enterprisePayroll.raw;
			
			
			
			enterprisePayroll.bonuses = s.getBonuses().stream().mapToDouble(Bonus::getAmount).sum();
			// PICKING UP DEDUCTIONS
			Double cgc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double cgp = s.getDeductions().stream()
					.filter(d -> (d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.IT.ordinal())
							|| (d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.IMS.ordinal()))
					.mapToDouble(Deduction::getAmount).sum();
			Double unemployment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double jobTraining = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double advancedPayment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.ADVANCE_PAYMENT.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double otherDeductions = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.OTHER.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double estruc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double noEstruct = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() != null && d.getDeductionType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Deduction::getAmount).sum();
			Double embargos = s.getEmbargos().stream()
					.mapToDouble(Embargo::getAmount).sum();
			// PICKING UP COSTS
			Double cgcEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double cgpEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.IT.ordinal()
					|| c.getCostType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			
			Double unemploymentEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double jobTrainingEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double fogasaEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double estrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Cost::getAmount).sum();
			Double noEstrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(Cost::getAmount).sum();

			// DEDUCTIONS
			enterprisePayroll.cgc = cgc;
			enterprisePayroll.cgp = cgp;
			enterprisePayroll.unemployment = unemployment;
			enterprisePayroll.jobTraining = jobTraining;
			enterprisePayroll.advancedPayment = advancedPayment;
			enterprisePayroll.otherDeductions = otherDeductions;
			
			enterprisePayroll.estruc = estruc;
			enterprisePayroll.noEstruct = noEstruct;
			// COSTS
			enterprisePayroll.cgcEnterprise = cgcEnterprise;
			enterprisePayroll.cgpEnterprise = cgpEnterprise;
			enterprisePayroll.unemploymentEnterprise = unemploymentEnterprise;
			enterprisePayroll.jobTrainingEnterprise = jobTrainingEnterprise;
			enterprisePayroll.fogasaEnterprise = fogasaEnterprise;
			enterprisePayroll.estrucEnterprise = estrucEnterprise;
			enterprisePayroll.noEstructEnterprise = noEstrucEnterprise;
			enterprisePayroll.embargos = embargos;

			return enterprisePayroll;
		});
	}
	
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, final int month,
			final int year, Integer enterpriseId, Integer workplaceId) throws IOException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.YEAR, year);
		Date startDate = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		return getEnterprisePayrolls(aonContext, startDate, endDate, enterpriseId, workplaceId);
	}
	
	
	
	private static Sheet initializeTotalsSheet(Optional<LinkedHashMap<String, String>> header, String enterpriseName,
			String dateString, ExcelType excelType, Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap,
			final int completeLength, final int summaryLength, Sheet totals, List<String> workplaces) {
		Row row;
		if (workplaces.size() > 1) {
			totals = wb.createSheet(WorkbookUtil.createSafeSheetName("TOTALES"));
			// Sheet of total amounts by workplace

			LinkedHashMap<String, String> finalHeader = new LinkedHashMap<>();

			if (header.isPresent()) {
				LinkedHashMap<String, String> customHeader = header.get();
				finalHeader.putAll(customHeader);
			} else {
				if (excelType.isComplete())
					finalHeader.putAll(DEFAULT_HEADER);
				else
					finalHeader.putAll(DEFAULT_HEADER_SUMMARY);
			}

			finalHeader.put("employee", "CENTRO DE TRABAJO");

			Iterator<String> itHead = finalHeader.keySet().iterator();
			
			int lCell = excelType.isComplete() ? completeLength : summaryLength;
			
			totals.addMergedRegion(new CellRangeAddress(0, 0, 0, lCell));
			row = totals.createRow(0);
			Cell enterpriseCell = row.createCell(0);
			enterpriseCell.setCellType(CellType.STRING);
			enterpriseCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			enterpriseCell.setCellValue(enterpriseName + " - " + dateString);

			row = totals.createRow(2);

			int cellCount = 0;

			while (itHead.hasNext()) {
				Cell cell = row.createCell(cellCount++);
				cell.setCellType(CellType.STRING);
				String value = itHead.next();
				if (AonStringUtils.containsIgnoreCase(value, "join") || AonStringUtils.containsIgnoreCase(value, "type"))
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				else {
					cell.setCellValue(finalHeader.get(value));
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				}
			}

			
			int lastCell = row.getLastCellNum() - 1;
			row = totals.createRow(1);
			totals.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
			totals.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));
			Cell epCell = row.createCell(2, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("EMPRESA");
			
			Cell jointCell = row.createCell(5, CellType.STRING);
			jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			totals.addMergedRegion(new CellRangeAddress(1, 1, 6, 9));
			epCell = row.createCell(6, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("EMPLEADO");
			
			jointCell = row.createCell(10, CellType.STRING);
			jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			epCell = row.createCell(summaryLength, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("TGSS");
			
			if (excelType.isComplete()) {
				jointCell = row.createCell(12, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				totals.addMergedRegion(new CellRangeAddress(1, 1, 13, 19));
				Cell entCell = row.createCell(13, CellType.STRING);
				entCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				entCell.setCellValue("COTIZACIÓN EMPRESA");
				
				jointCell = row.createCell(20, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				totals.addMergedRegion(new CellRangeAddress(1, 1, 21, lastCell));
				epCell= row.createCell(21, CellType.STRING);
				epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				epCell.setCellValue("COTIZACIÓN EMPLEADO");
			}
				
				
			

		}
		return totals;
	}
	
	private static Sheet initializeCompleteTotalsSheet(String enterpriseName,
			String dateString, ExcelType excelType, Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap,
			final int completeLength, final int summaryLength, Sheet totals, List<String> workplaces) {
		Row row;
		if (workplaces.size() > 1) {
			totals = wb.createSheet(WorkbookUtil.createSafeSheetName("TOTALES"));
			// Sheet of total amounts by workplace
			
			LinkedHashMap<String, String> finalHeader = new LinkedHashMap<>();
			
			finalHeader = COMPLETE_TOTALS_HEADER;
			
			
			Iterator<String> itHead = finalHeader.keySet().iterator();
			
			int lCell = excelType.isComplete() ? completeLength + 1 : summaryLength + 1;
			
			totals.addMergedRegion(new CellRangeAddress(0, 0, 0, lCell));
			row = totals.createRow(0);
			Cell enterpriseCell = row.createCell(0);
			enterpriseCell.setCellType(CellType.STRING);
			enterpriseCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			enterpriseCell.setCellValue(enterpriseName + " - " + dateString);
			
			row = totals.createRow(2);
			
			int cellCount = 0;
			
			while (itHead.hasNext()) {
				Cell cell = row.createCell(cellCount++);
				cell.setCellType(CellType.STRING);
				String value = itHead.next();
				if (AonStringUtils.containsIgnoreCase(value, "join"))
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				else {
					cell.setCellValue(finalHeader.get(value));
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				}
			}
			
			
			int lastCell = row.getLastCellNum() - 1;
			row = totals.createRow(1);
			totals.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
			totals.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));
			Cell epCell = row.createCell(3, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("EMPRESA");
			
			Cell jointCell = row.createCell(6, CellType.STRING);
			jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			totals.addMergedRegion(new CellRangeAddress(1, 1, 7, 10));
			epCell = row.createCell(7, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("EMPLEADO");
			
			jointCell = row.createCell(11, CellType.STRING);
			jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			epCell = row.createCell(summaryLength + 1, CellType.STRING);
			epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			epCell.setCellValue("TGSS");
			
			Cell closingCell = totals.getRow(0).createCell(lCell + 1);
			closingCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_LEFT_CELL_STYLE));
			closingCell = totals.getRow(1).createCell(lCell + 1);
			closingCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_LEFT_CELL_STYLE));
			
			
			if (excelType.isComplete()) {
				jointCell = row.createCell(13, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				totals.addMergedRegion(new CellRangeAddress(1, 1, 14, 21));
				Cell entCell = row.createCell(14, CellType.STRING);
				entCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				entCell.setCellValue("COTIZACIÓN EMPRESA");
				
				jointCell = row.createCell(21, CellType.STRING);
				jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				totals.addMergedRegion(new CellRangeAddress(1, 1, 23, lastCell));
				epCell= row.createCell(23, CellType.STRING);
				epCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
				epCell.setCellValue("COTIZACIÓN EMPLEADO");
			}
			
			
			totals.createFreezePane(1, 3);

			
		}
		return totals;
	}

	private static boolean isRelatedSalaryType(SalaryType original, SalaryType ss) {
		SalaryType.TypeVisitor<Boolean> visitor = new SalaryType.TypeVisitor<Boolean>() {

			@Override
			public Boolean visitSalary(SalaryType type) {
				return ss != null && ss.equals(SalaryType.L00);
			}

			@Override
			public Boolean visitExtra(SalaryType type) {
				return false;
			}

			@Override
			public Boolean visitSettle(SalaryType type) {
				return ss != null && ss.equals(SalaryType.L13);
			}

			@Override
			public Boolean visitDelay(SalaryType type) {
				return ss != null && ss.equals(SalaryType.L03);
			}
			
			@Override
			public Boolean visitM190(SalaryType type) {
				return ss != null && ss.equals(SalaryType.M190);
			}
		};
		
		return original.accept(visitor);
	}
	
	private static void writeCompleteDetails(Map<PayrollCellStyle, CellStyle> stylesMap, Row row,
			EnterprisePayrollExcelChecks checks, IEnterprisePayroll payroll, Map<String, Map<String, Double>> workplaceContractDataMap, int column, boolean isDiff, PayrollCellStyle style, HashSet<Integer> ssCols) {
		writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
		
		ssCols.add(column);
		writeDetail(payroll.getCgcEnterprise(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getCgcEnterprise() : null
				, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isCgcEnterprise(), checks.isCgcEnterprise() ? column++ : column, isDiff);
			
		ssCols.add(column);
		writeDetail(payroll.getCgpEnterprise(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getCgpEnterprise() : null
				, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isCgpEnterprise(), checks.isCgpEnterprise() ? column++ : column, isDiff);
		
		ssCols.add(column);
		writeDetail(payroll.getUnemploymentEnterprise(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getUnemploymentEnterprise() : null
				, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isUnemploymentEnterprise(), checks.isUnemploymentEnterprise() ? column++ : column, isDiff);

		ssCols.add(column);
		writeDetail(payroll.getJobTrainingEnterprise(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getJobTrainingEnterprise() : null
				, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isJobTrainingEnterprise(), checks.isJobTrainingEnterprise() ? column++ : column, isDiff);

		ssCols.add(column);
		writeDetail(payroll.getFogasaEnterprise(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getFogasaEnterprise() : null
				, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isFogasaEnterprise(), checks.isFogasaEnterprise() ? column++ : column, isDiff);
		
		Double extraHEntAmount = null;
		Double extraHEntAmountDiff = null;
		if (payroll.getEstrucEnterprise() == null && payroll.getNoEstructEnterprise() == null)
			extraHEntAmount = (payroll.getEstrucEnterprise()!=null?payroll.getEstrucEnterprise():0d)
					+
					(payroll.getNoEstructEnterprise()!=null?payroll.getNoEstructEnterprise():0d); 
		if (payroll.getOriginalPayroll() != null && payroll.getOriginalPayroll().getEstrucEnterprise() == null && payroll.getOriginalPayroll().getNoEstructEnterprise() == null)
			extraHEntAmount = (payroll.getEstrucEnterprise()!=null?payroll.getEstrucEnterprise():0d)
			+
			(payroll.getOriginalPayroll().getNoEstructEnterprise()!=null?payroll.getOriginalPayroll().getNoEstructEnterprise():0d); 
		
		ssCols.add(column);
		writeDetail(extraHEntAmount, extraHEntAmountDiff, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isExtraH()
			, checks.isExtraH() ? column++ : column, isDiff);
		
		
		
		ssCols.add(column);
		writeDetail(-payroll.getBonuses(), payroll.getOriginalPayroll() != null ? -payroll.getOriginalPayroll().getBonuses() : null
			, stylesMap, PayrollCellStyle.DOUBLE_CELL_STYLE, row, checks.isBonuses(), checks.isBonuses() ? column++ : column, isDiff);
		
		
		if (checks.isFundae()) {
			Cell cell = row.createCell(column++);
			cell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
			cell.setCellType(CellType.BLANK);
		}
		
		
		writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
		
		ssCols.add(column);
		writeDetail(payroll.getCgc(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getCgc() : null
				, stylesMap, style, row, checks.isCgc(), checks.isCgc() ? column++ : column, isDiff);
		
		ssCols.add(column);
		writeDetail(payroll.getCgp(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getCgp() : null
				, stylesMap, style, row, checks.isCgp(), checks.isCgp() ? column++ : column, isDiff);

		ssCols.add(column);
		writeDetail(payroll.getUnemployment(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getUnemployment() : null
				, stylesMap, style, row, checks.isUnemployment(), checks.isUnemployment() ? column++ : column, isDiff);
		
		ssCols.add(column);
		writeDetail(payroll.getJobTraining(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getJobTraining() : null
				, stylesMap, style, row, checks.isJobTraining(), checks.isJobTraining() ? column++ : column, isDiff);

		
		Double extraHAmount = null;
		Double extraHAmountDiff = null;
		if (!(payroll.getEstruc() == null && payroll.getNoEstruct()==null))
			extraHAmount = (payroll.getEstruc()!=null?payroll.getEstruc():0d) + (payroll.getNoEstruct()!=null?payroll.getNoEstruct():0d);
		if (payroll.getOriginalPayroll() != null && !(payroll.getEstruc() == null && payroll.getNoEstruct()==null))
			extraHAmountDiff = (payroll.getOriginalPayroll().getEstruc()!=null?payroll.getOriginalPayroll().getEstruc():0d) + (payroll.getNoEstruct()!=null?payroll.getNoEstruct():0d);
		
		ssCols.add(column);
		writeDetail(extraHAmount, extraHAmountDiff, stylesMap, style, row, checks.isExtraH()
				, checks.isExtraH() ? column++ : column, isDiff);
			
		ssCols.add(column);
		writeDetail(payroll.getAdvancedPayment(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getAdvancedPayment() : null
				, stylesMap, style, row, checks.isAdvancedPayment(), checks.isAdvancedPayment() ? column++ : column, isDiff);
		
		ssCols.add(column);
		writeDetail(payroll.getEmbargos(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getEmbargos() : null
				, stylesMap, style, row, checks.isEmbargos(), checks.isEmbargos() ? column++ : column, isDiff);
		
		ssCols.add(column);
		writeDetail(payroll.getOtherDeductions(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getOtherDeductions() : null
				, stylesMap, style, row, checks.isOtherDeductions(), checks.isOtherDeductions() ? column++ : column, isDiff);
		
		
		writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
		
		
		ssCols.add(column);
		writeDetail(payroll.getCgcBase(), payroll.getOriginalPayroll() != null ? payroll.getOriginalPayroll().getCgcBase() : null
				, stylesMap, style, row, checks.isCgcBase(), checks.isCgcBase() ? column++ : column, isDiff);
		
		if (checks.isIrpfBase()) {
			if (!isDiff)
				createDoubleCell(row, column++, payroll.getIrpfBase(), stylesMap.get(style));
			else
				createDoubleCell(row, column++, null, stylesMap.get(style));
		}
		
		writeJoint(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE), row, column++);
		
		if (checks.isMoneyIrpfBase()) {
			if (!isDiff)
				createDoubleCell(row, column++, payroll.getMoneyIrpfBase(), stylesMap.get(style));
			else
				createDoubleCell(row, column++, null, stylesMap.get(style));
		}
		if (checks.isInKindIrpfBase()) {
			if (!isDiff)
				createDoubleCell(row, column, payroll.getInkindIrpfBase(), stylesMap.get(style));
			else
				createDoubleCell(row, column, null, stylesMap.get(style));
		}
	}
	
	
	private static int writeMonthTotals(Map<PayrollCellStyle, CellStyle> stylesMap, Sheet sheet, ArrayList<Integer> joints,
			List<Integer> importantCells, List<Integer> rowsForFormula, Row row, String month, Map<Integer, Double> fixedCells) {
		
		fixedCells = fixedCells != null ? fixedCells : Collections.emptyMap();
		
//		int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
		
		int[] numberOfColumns = new int[1];
		
		sheet.rowIterator().forEachRemaining(r -> {
			if (r.getLastCellNum() > numberOfColumns[0]){
				numberOfColumns[0] = r.getLastCellNum();
			}
		});
		
		int lastColumn = numberOfColumns[0];
		
		int ind = 3;
		
		Cell firstTotalsCell = row.createCell(1);
		firstTotalsCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
		
		firstTotalsCell = row.createCell(2);
		firstTotalsCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE));

		for (int i = ind; i < lastColumn; i++) {
			
			CellStyle style = stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE);
			StringBuilder fsb = new StringBuilder("0");
			for (Integer r : rowsForFormula) {
				fsb.append("+" + CellReference.convertNumToColString(i) + r);
				
			}
			Cell cell = row.createCell(i);
			if (joints.contains(i))
				style = stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE);
			else if (importantCells.contains(i)) {
				style = stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE);
				cell.setCellFormula(fsb.toString());
				cell.setCellType(CellType.FORMULA);
			} else {
				
				if (fixedCells.containsKey(cell.getColumnIndex())) {
					
					Double value = fixedCells.get(cell.getColumnIndex());
					if (value == null)
						cell.setCellType(CellType.BLANK);
					else
						cell.setCellValue(value);
				} else {					
					cell.setCellFormula(fsb.toString());
					cell.setCellType(CellType.FORMULA);
				}
				
			}
				
			cell.setCellStyle(style);
		}
		return row.getRowNum();
		
	}

	private static int writeWorkplaceTotals(Map<PayrollCellStyle, CellStyle> stylesMap, Sheet sheet, ArrayList<Integer> joints,
			ArrayList<Integer> importantCells, LinkedList<Integer> normalRows, ExcelType excelType) {
		return writeWorkplaceTotals(stylesMap, sheet, joints, importantCells, normalRows, excelType, false);
	}
	
	
	private static int writeWorkplaceTotals(Map<PayrollCellStyle, CellStyle> stylesMap, Sheet sheet, ArrayList<Integer> joints,
			ArrayList<Integer> importantCells, LinkedList<Integer> normalRows, ExcelType excelType, boolean completeWorkplace) {
		return writeWorkplaceTotals(stylesMap, sheet, joints, importantCells, normalRows, excelType, completeWorkplace, Collections.emptyMap());
	}
	
	private static int writeWorkplaceTotals(Map<PayrollCellStyle, CellStyle> stylesMap, Sheet sheet, ArrayList<Integer> joints,
			ArrayList<Integer> importantCells, LinkedList<Integer> normalRows, ExcelType excelType, boolean completeWorkplace, Map<Integer, Double> fixedValues) {
		Row row;
		
		fixedValues = fixedValues != null ? fixedValues : Collections.emptyMap();
//		int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
		
		int[] numberOfColumns = new int[1];
		
		sheet.rowIterator().forEachRemaining(r -> {
			if (r.getLastCellNum() > numberOfColumns[0]){
				numberOfColumns[0] = r.getLastCellNum();
			}
		});
		
		int lastColumn = numberOfColumns[0];
		
		row = sheet.createRow(sheet.getLastRowNum() + 1);
		
		int ind = 1;
		
		if (completeWorkplace) {
			Cell totalCell = row.createCell(2);
			totalCell.setCellType(CellType.STRING);
			totalCell.setCellValue("TOTALES:");
			totalCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			ind = 3;
		} else if (excelType.isWithType()) {			
			Cell totalCell = row.createCell(1);
			totalCell.setCellType(CellType.STRING);
			totalCell.setCellValue("TOTALES:");
			totalCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			ind = 2;
		} else {
			Cell firstTotalsCell = row.createCell(0);
			firstTotalsCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_RIGHT_CELL_STYLE));
		}

		for (int i = ind; i < lastColumn; i++) {
			
			CellStyle style = stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE);
			StringBuilder fsb = new StringBuilder("0");
			for (Integer r : normalRows) {
				fsb.append("+" + CellReference.convertNumToColString(i) + r);
				
			}
			Cell cell = row.createCell(i);
			if (joints.contains(i))
				style = stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE);
			else if (importantCells.contains(i)) {
				style = stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE);
				cell.setCellFormula(fsb.toString());
				cell.setCellType(CellType.FORMULA);
			} else {
				
				if (fixedValues.containsKey(cell.getColumnIndex())) {
					Double value = fixedValues.get(cell.getColumnIndex());
					if (value == null)
						cell.setCellType(CellType.BLANK);
					else
						cell.setCellValue(value);
				} else {
					cell.setCellFormula(fsb.toString());
					cell.setCellType(CellType.FORMULA);
				}
			}
				
			cell.setCellStyle(style);
		}
		return row.getRowNum();
		
	}
	
	private static void writeWorkplaceSSTotals(Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap, Sheet sheet, ArrayList<Integer> joints,
			ArrayList<Integer> importantCells, LinkedList<Integer> ssRows, HashSet<Integer> ssCols) {
		Row row;
		
		
		int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
		row = sheet.createRow(sheet.getLastRowNum() + 1);
		
		Cell totalCell = row.createCell(1);
		totalCell.setCellType(CellType.STRING);
		totalCell.setCellValue("SUBTOTAL S.S.:");
		totalCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
		
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		for (int i = 2; i < lastColumn; i++) {
				CellStyle style = stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE);
				StringBuilder fsb = new StringBuilder("0");
				for (Integer r : ssRows) {
					fsb.append("+" + CellReference.convertNumToColString(i) + r);
					
				}
				Cell cell = row.createCell(i);
				if (joints.contains(i))
					style = stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE);
				else {
					if (ssCols.contains(i)) {						
						cell.setCellType(CellType.FORMULA);
						cell.setCellFormula(fsb.toString());
						
						Cell c = sheet.getRow(sheet.getLastRowNum() -1).getCell(i);
						if (evaluator.evaluateFormulaCellEnum(cell) == CellType.NUMERIC && evaluator.evaluateFormulaCellEnum(c) == CellType.NUMERIC) {
							double difference = Math.abs(AonNumberUtils.zeroIfNull(c.getNumericCellValue()) - AonNumberUtils.zeroIfNull(cell.getNumericCellValue()));
							
							
							if (importantCells.contains(i)) {
								style = selectColor(difference
										, stylesMap.get(PayrollCellStyle.RED_IMPORTANT_TOTAL_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.ORANGE_IMPORTANT_TOTAL_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.GREEN_IMPORTANT_TOTAL_CELL_STYLE)
										);
							} else {
								style = selectColor(difference
										, stylesMap.get(PayrollCellStyle.RED_FORMULA_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.ORANGE_FORMULA_CELL_STYLE)
										, stylesMap.get(PayrollCellStyle.GREEN_FORMULA_CELL_STYLE)
										);								
							}	
						}
					}
				}	
				cell.setCellStyle(style);
		}		
	}

	private static int writeTotals(ExcelType excelType, Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap, Sheet totals,
			String workplace, EnterprisePayrollExcelChecks checks, int totalsRow, boolean diffs) {
		
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		Row row;
		Row diffRow = null;
		if (totals != null) {
			row = totals.createRow(totals.getLastRowNum() + 1);
			if (diffs) {
				diffRow = totals.createRow(row.getRowNum() + 1);
			}

			Cell tCell = row.createCell(0);
			tCell.setCellValue(workplace);
			tCell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE));

			
			int column = excelType.isWithType() ? 2 : 1;
			int cell = 2;
			
			Cell jCell = row.createCell(1, CellType.STRING);
			jCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			
			//------EMPRESA------
			column = addTotalsFormulaCell(checks.isRaw(), workplace, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isEnterpriseSS(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
			column = addTotalsFormulaCell(checks.isTotalCost(), workplace, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), row, cell++, totalsRow, column);
			//-------------------			
			
			//JOINT
			drawTotalsJoint(stylesMap, row, diffRow, cell);
			cell++;
			column++;
			
			//------EMPLEADO------
			column = addTotalsFormulaCell(checks.isEmployeeSS(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
			column = addTotalsFormulaCell(checks.isIrpf(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isOther(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isLiquid(), workplace, stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV), row, cell++, totalsRow, column);
			//--------------------
			
			//JOINT
			drawTotalsJoint(stylesMap, row, diffRow, cell);
			cell++;
			column++;
			
			//------TGSS------
			column = addTotalsFormulaCell(checks.isTotalSS(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), PayrollCellStyle.IMPORTANT_CELL_STYLE, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
			//----------------
			
			if (excelType.isComplete()) {
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				//------COTIZACIÓN EMPRESA------
				column = addTotalsFormulaCell(checks.isCgcEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isCgpEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isUnemploymentEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isJobTrainingEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isFogasaEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isExtraHEnterprise(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isBonuses(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				//------------------------------
				
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;

				//------COTIZACIÓN EMPLEADO------
				column = addTotalsFormulaCell(checks.isCgc(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isCgp(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isUnemployment(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isJobTraining(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isExtraH(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isAdvancedPayment(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isEmbargos(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isOtherDeductions(), workplace, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), row, cell++, totalsRow, column);
				
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				column = addTotalsFormulaCell(checks.isCgcBase(), workplace, stylesMap,  stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), null, evaluator,row, diffRow, cell++, totalsRow, column, diffs);
				column = addTotalsFormulaCell(checks.isIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.FINAL_CELL_STYLE), row, cell++, totalsRow, column);
				
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				
				column = addTotalsFormulaCell(checks.isMoneyIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				addTotalsFormulaCell(checks.isInKindIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.FINAL_CELL_STYLE), row, cell++, totalsRow, column);			
				//-------------------------------
			}					

		}
		if (diffRow != null)
			return diffRow.getRowNum();
		else return -1;
	}

	
	private static Integer writeCompleteTotals(ExcelType excelType, Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap, Sheet totals,
			String workplace, EnterprisePayrollExcelChecks checks, int totalsRow, String month) {
		Row row;
		Row diffRow = null;
		if (totals != null) {
			row = totals.createRow(totals.getLastRowNum() + 1);

			Cell tCell = row.createCell(0);			
			tCell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE_WHITE_BACK));
			tCell.setCellValue(month);
			
			
			int column = 2;
			int cell = 2;
			
			Cell jCell = row.createCell(1, CellType.STRING);
			jCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
			
			tCell = row.createCell(cell++);
			tCell.setCellType(CellType.STRING);
			tCell.setCellValue(workplace);
			tCell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
			column++;
			
			//------EMPRESA------
			column = addTotalsFormulaCell(checks.isRaw(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isEnterpriseSS(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isTotalCost(), workplace, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), row, cell++, totalsRow, column);
			//-------------------
			
			//JOINT
			drawTotalsJoint(stylesMap, row, diffRow, cell);
			cell++;
			column++;
			
			
			//------EMPLEADO------
			column = addTotalsFormulaCell(checks.isEmployeeSS(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isIrpf(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isOther(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
			column = addTotalsFormulaCell(checks.isLiquid(), workplace, stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV), row, cell++, totalsRow, column);
			//--------------------
			
			//JOINT
			drawTotalsJoint(stylesMap, row, diffRow, cell);
			cell++;
			column++;
			
			//------TGSS------			
			column = addTotalsFormulaCell(checks.isTotalSS(), workplace, stylesMap.get(PayrollCellStyle.IMPORTANT_CELL_STYLE), row, cell++, totalsRow, column);
			//----------------
			
			if (excelType.isComplete()) {
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				//------COTIZACIÓN EMPRESA------
				column = addTotalsFormulaCell(checks.isCgcEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isCgpEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isUnemploymentEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isJobTrainingEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isFogasaEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isExtraHEnterprise(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isBonuses(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				//------------------------------
				
				//------FUNDAE------
				tCell = row.createCell(cell++);
				tCell.setCellStyle(stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE));
				if (checks.isFundae()) {	
					tCell.setCellType(CellType.FORMULA);
					tCell.setCellFormula("'" + workplace + "'!" + CellReference.convertNumToColString(column) + (totalsRow+1));
				} else {
					tCell.setCellType(CellType.BLANK);
				}
				column++;				
				//------------------				
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;

				//------COTIZACIÓN EMPLEADO------
				column = addTotalsFormulaCell(checks.isCgc(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isCgp(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isUnemployment(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isJobTraining(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isExtraH(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isAdvancedPayment(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isEmbargos(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isOtherDeductions(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				column = addTotalsFormulaCell(checks.isCgcBase(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				column = addTotalsFormulaCell(checks.isIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.FINAL_CELL_STYLE), row, cell++, totalsRow, column);
								
				//JOINT
				drawTotalsJoint(stylesMap, row, diffRow, cell);
				cell++;
				column++;
				
				column = addTotalsFormulaCell(checks.isMoneyIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE), row, cell++, totalsRow, column);
				addTotalsFormulaCell(checks.isInKindIrpfBase(), workplace, stylesMap.get(PayrollCellStyle.FINAL_CELL_STYLE), row, cell++, totalsRow, column);
				//-------------------------------
				
			}					
			return row.getRowNum();
		}
		return null;
	}
	
	private static int addTotalsFormulaCell(boolean check, String workplace, CellStyle style, Row row, int cellNum, int totalsRow, int column ) {
		Cell tCell = row.createCell(cellNum);
		tCell.setCellType(CellType.FORMULA);
		tCell.setCellStyle(style);
		if (check) {
			tCell.setCellFormula("'" + workplace + "'!" + CellReference.convertNumToColString(column) + (totalsRow+1));
			column++;
		}
		return column;
	}
	
	private static int addTotalsFormulaCell(boolean check, String workplace, Map<PayrollCellStyle, CellStyle> stylesMap,  CellStyle style, PayrollCellStyle differenceStyle, FormulaEvaluator evaluator,Row row, Row diffRow, int cellNum, int totalsRow, int column, boolean diffs) {
		Cell tCell = row.createCell(cellNum);
		tCell.setCellType(CellType.FORMULA);
		tCell.setCellStyle(style);
		if (check) {
			tCell.setCellFormula("'" + workplace + "'!" + CellReference.convertNumToColString(column) + (totalsRow+1));
			if (diffs) {
				Cell dCell = diffRow.createCell(cellNum);
				dCell.setCellType(CellType.FORMULA);
				dCell.setCellFormula("'" + workplace + "'!" + CellReference.convertNumToColString(column) + (totalsRow+2));
				setDCellStyle(evaluator, stylesMap, tCell, dCell, differenceStyle);
			}
			column++;
		}
		return column;
	}
	
	
	private static void drawTotalsJoint(Map<PayrollCellStyle, CellStyle> stylesMap, Row row, Row diffRow, int cell) {
		Cell jointCell;
		jointCell = row.createCell(cell, CellType.STRING);
		jointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
		if (diffRow != null) {
			Cell diffJointCell = diffRow.createCell(cell, CellType.STRING);
			diffJointCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
			
		}
	}

	private static void setDCellStyle(FormulaEvaluator evaluator, Map<PayrollCellStyle, CellStyle> stylesMap,
			Cell tCell, Cell dCell, PayrollCellStyle baseStyle) {
		//DEFAULT STYLES
		PayrollCellStyle red = PayrollCellStyle.RED_DOUBLE_CELL_STYLE_NO_BORDERS;
		PayrollCellStyle orange = PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS;
		PayrollCellStyle green= PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE_NO_BORDERS;
		
		if (PayrollCellStyle.IMPORTANT_CELL_STYLE == baseStyle) {
			red = PayrollCellStyle.RED_IMPORTANT_CELL_STYLE_NO_BORDERS;
			orange = PayrollCellStyle.ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS;
			green= PayrollCellStyle.GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS;
		}
		
		
		if (evaluator.evaluateFormulaCellEnum(tCell) == CellType.NUMERIC && evaluator.evaluateFormulaCellEnum(dCell) == CellType.NUMERIC) {
			double difference = Math.abs(AonNumberUtils.zeroIfNull(dCell.getNumericCellValue()) - AonNumberUtils.zeroIfNull(tCell.getNumericCellValue()));
			dCell.setCellStyle(selectColor(
					difference
					, stylesMap.get(red)
					, stylesMap.get(orange)
					, stylesMap.get(green)
					)
				);
		}
	}
	
	private static void completeTotals(Workbook wb, Map<PayrollCellStyle, CellStyle> stylesMap, Sheet totals,
			ExcelType excelType, Map<String, List<TotalsReferences>> totalsSchema, final int completeLength,
			final int summaryLength) {
		Row row;
		if (totals != null) {
			
			LinkedList<Integer> totalRows = new LinkedList<>();
			for (String month : totalsSchema.keySet()) {
				
				List<Integer> totalOfTotals = new LinkedList<>();
				
				row = totals.createRow(totals.getLastRowNum() + 1);
				totalRows.add(row.getRowNum());
				
				Cell cell = row.createCell(0);
				cell.setCellValue(month);
				cell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE));
				
				cell = row.createCell(1);
				CellStyle leftBorder = wb.createCellStyle();
				leftBorder.setBorderLeft(BorderStyle.THIN);
				cell.setCellStyle(leftBorder);
				
				
				List<TotalsReferences> references = totalsSchema.get(month);
				for (TotalsReferences ref : references) {
					if (ref.getRowNum() != null)
						totalOfTotals.add(writeCompleteTotals(excelType, wb, stylesMap, totals, ref.getWorkplace(), ref.getChecks(), ref.getRowNum(), month) + 1);					
				}
				
			
				Cell otherCell = row.createCell(1);
				otherCell.setCellType(CellType.BLANK);
				otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				
				
				otherCell = row.createCell(2);
				otherCell.setCellType(CellType.BLANK);
				otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE));
				
				int cellInd = 3;
				for (; cellInd<COMPLETE_TOTALS_HEADER.size(); cellInd++) {
					List<String> hList = new LinkedList<>();
					hList.addAll(COMPLETE_TOTALS_HEADER.keySet());
					
					
					if (!hList.get(cellInd).contains("join")) {
						Cell totCell = row.createCell(cellInd);
						totCell.setCellType(CellType.FORMULA);
						
						String colLetter = CellReference.convertNumToColString(cellInd);
						String formula = "0";
						for (Integer t : totalOfTotals) {
							formula += "+" + colLetter + t;
						}
						
						totCell.setCellFormula(formula);
						if(!IMPORTANT_CELLS.contains(hList.get(cellInd))) {							
							totCell.setCellStyle(stylesMap.get(PayrollCellStyle.FORMULA_CELL_STYLE));
						} else {							
							totCell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE));
						}
					} else {
						Cell totCell = row.createCell(cellInd);
						totCell.setCellType(CellType.BLANK);
						totCell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
					}
				}
				
				otherCell = row.createCell(cellInd);
				otherCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_LEFT_CELL_STYLE));
			}
			
			
			//-------------TOTAL OF TOTALS XD--------------
			AtomicInteger numberOfColumns = new AtomicInteger(0);
			
			totals.rowIterator().forEachRemaining(r -> {
				if (r.getLastCellNum() > numberOfColumns.get()){
					numberOfColumns.set(r.getLastCellNum());
				}
			});
			
			int lastColumn = numberOfColumns.get();
			
			row = totals.createRow(totals.getLastRowNum() + 1);
			
			int ind = 2;
			
			Cell totalCell = row.createCell(ind);
			totalCell.setCellType(CellType.STRING);
			totalCell.setCellValue("TOTALES:");
			totalCell.setCellStyle(stylesMap.get(PayrollCellStyle.HEADER_CELL_STYLE));
			ind = 3;
			
			
			ArrayList<Integer> joints = new ArrayList<>();
			ArrayList<Integer> importantCells = new ArrayList<>();
			
			AtomicInteger headInd = new AtomicInteger(0);
			COMPLETE_TOTALS_HEADER.forEach((k, v) -> {
				if (k.contains("joint")) {
					joints.add(headInd.get());
				}
				headInd.getAndAdd(1);
			});
			

			for (int i = ind; i < lastColumn - 1; i++) {
				
				StringBuilder fsb = new StringBuilder("0");
				int elPutoIndiceJoder = i;
				totalRows.forEach(rn -> fsb.append( "+" + CellReference.convertNumToColString(elPutoIndiceJoder) + (rn+1)));
				
				Cell cell = row.createCell(i);
				if (joints.contains(i))
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE));
				else {
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE));
					cell.setCellFormula(fsb.toString());
					cell.setCellType(CellType.FORMULA);
				}
			}
			Cell closingCell = row.createCell(row.getLastCellNum());
			closingCell.setCellStyle(stylesMap.get(PayrollCellStyle.BORDER_LEFT_CELL_STYLE));
			
			//---------------------------
			
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			evaluator.evaluateAll();
			
			int length = -1;
			if (excelType.isComplete())
				length = completeLength;
			else
				length = summaryLength;
			
			for (int i = 0; i <= length; i++) {
				totals.autoSizeColumn(i);
				if (totals.getColumnWidth(i) > 256)
					totals.setColumnWidth(i, totals.getColumnWidth(i) + 256);
			}

			
			Row prev = totals.getRow(totals.getLastRowNum());
			Row finale = totals.createRow(totals.getLastRowNum() + 1);
			CellStyle closingStyle = wb.createCellStyle();
			closingStyle.setBorderTop(BorderStyle.THIN);
			for (int i = 2; i <= (!excelType.isComplete() ? summaryLength : completeLength); i++) {
				if (prev.getCell(i) == null || (prev.getCell(i) != null && !prev.getCell(i).getCellStyle().equals(stylesMap.get(PayrollCellStyle.JOINT_CELL_STYLE))))
					finale.createCell(i).setCellStyle(closingStyle);
			}
			
		}
	}	
	
	private static void writeDetail(Double original, Double diff, Map<PayrollCellStyle, CellStyle> stylesMap, PayrollCellStyle cellStyle, Row row, boolean thereIsCgcEnterprise, int column, boolean isDiff) {
		//DEFAULT
		CellStyle normal = stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE);
		CellStyle red = stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE);
		CellStyle green = stylesMap.get(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE);
		CellStyle orange = stylesMap.get(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE);
		
		if (cellStyle.equals(PayrollCellStyle.BOUND_CELL_STYLE_PREV)) {
			normal = stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV);
			red = stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_RED);
			green = stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_GREEN);
			orange = stylesMap.get(PayrollCellStyle.BOUND_CELL_STYLE_PREV_ORANGE);			
		}
		
		
		if (thereIsCgcEnterprise) {
			CellStyle sti = normal;
			if (isDiff && diff != null) {
				double difference = Math.abs(
						AonNumberUtils.zeroIfNull(original
								- diff));
				sti = selectColor(difference, red, orange, green);
			} else if (isDiff) {
				sti = red;
			}
			createDoubleCell(row, column, original, sti);
		}
	}

	private static CellStyle selectColor(double difference, CellStyle redCellStyle, CellStyle orangeCellStyle,
			CellStyle greenCellStyle) {
		CellStyle sti;
		if (difference > 0 && difference <= 0.01) {
			sti = orangeCellStyle;
		} else if (difference > 0.01) {
			sti = redCellStyle;
		} else {
			sti = greenCellStyle;
		}
		return sti;
	}

	private static void writeJoint(CellStyle jointCellStyle, Row row, int column) {
		Cell jointCell = row.createCell(column, CellType.STRING);
		jointCell.setCellStyle(jointCellStyle);
	}
	
	private static String getSalaryTypeName(SalaryType salaryType) {
		if (salaryType != null) {
			return salaryType.accept(new SalaryType.TypeVisitor<String>() {

				@Override
				public String visitSalary(SalaryType type) {
					return "NÓMINA";
				}

				@Override
				public String visitExtra(SalaryType type) {
					return "EXTRA";
				}

				@Override
				public String visitSettle(SalaryType type) {
					return "FINIQUITO";
				}

				@Override
				public String visitDelay(SalaryType type) {
					return "ATRASOS";
				}

				@Override
				public String visitL00(SalaryType type) {
					return "L00";
				}

				@Override
				public String visitL03(SalaryType type) {
					return "L03";
				}

				@Override
				public String visitL13(SalaryType type) {
					return "L13";
				}

				@Override
				public String visitM190(SalaryType type) {
					return "M190";
				}
				
				
			});
		} else {
			return "DESCONOCIDO";
		}
	}

	private static void writeSalaryType(boolean isDiff, Map<PayrollCellStyle, CellStyle> stylesMap, Row row, int column, IEnterprisePayroll payroll) {
			CellStyle sti = (isDiff && payroll.getOriginalPayroll() == null) ? stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE) : stylesMap.get(PayrollCellStyle.DOUBLE_CELL_STYLE);
			Cell cell = row.createCell(column);
			String name;
			if (payroll.getSalaryType() != null) {
				name = payroll.getSalaryType().accept(new SalaryType.TypeVisitor<String>() {

					@Override
					public String visitSalary(SalaryType type) {
						return "NÓMINA";
					}

					@Override
					public String visitExtra(SalaryType type) {
						return "EXTRA";
					}

					@Override
					public String visitSettle(SalaryType type) {
						return "FINIQUITO";
					}

					@Override
					public String visitDelay(SalaryType type) {
						return "ATRASOS";
					}

					@Override
					public String visitL00(SalaryType type) {
						return "L00";
					}

					@Override
					public String visitL03(SalaryType type) {
						return "L03";
					}

					@Override
					public String visitL13(SalaryType type) {
						return "L13";
					}
					
					@Override
					public String visitM190(SalaryType type) {
						return "M190";
					}
					
				});
			} else {
				name = "DESCONOCIDO";
				sti = stylesMap.get(PayrollCellStyle.RED_DOUBLE_CELL_STYLE);
			}
			cell.setCellValue(name);
			cell.setCellStyle(sti);
	}

	private static void writeEmployee(Map<PayrollCellStyle, CellStyle> stylesMap, Row row, int column, IEnterprisePayroll payroll, ExcelType excelType) {
		Cell cell = row.createCell(column);
		if(payroll.getOriginalPayroll() == null) {			
			cell.setCellValue(payroll.getEmployee());
			if (payroll.getSalaryType() == null || payroll.getSalaryType().ordinal() > SalaryType.DELAY.ordinal())
				
				if (payroll.getSalaryType() == null && !excelType.isWithType()) {
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE));					
				} else {					
					cell.setCellStyle(stylesMap.get(PayrollCellStyle.RED_STRING_CELL_STYLE));
				}
				
			else
				cell.setCellStyle(stylesMap.get(PayrollCellStyle.STRING_CELL_STYLE));
				
		}
	}

	private static void createDoubleCell(Row row, int column, Double value, CellStyle doubleCellStyle) {
		Cell cell = row.createCell(column);
		if (value != null)
			cell.setCellValue(value);
		cell.setCellStyle(doubleCellStyle);
		cell.setCellType(CellType.NUMERIC);
	}
	
	private static String getEnterpriseName(AONContext aonContext, Integer enterpriseId, Integer workplaceId) {
			AtomicInteger eId = new AtomicInteger(enterpriseId);
			if (enterpriseId == null || enterpriseId == 0)
				eId.set(AON.getWorkplace(aonContext.getDomainName(), aonContext.getDomainId(), "",
						w -> w.getIdProperty().eq(workplaceId)).getEnterprise());
			return AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					r -> r.getIdProperty().eq(eId.get())).getName();
	}
	
	private enum PayrollCellStyle {
		HEADER_CELL_STYLE,
		STRING_CELL_STYLE,
		STRING_CELL_STYLE_WHITE_BACK,
		BLANK_DIFF_CELL_STYLE,
		RED_STRING_CELL_STYLE,
		DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE,
		ORANGE_DOUBLE_CELL_STYLE,
		GREEN_DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE_NO_BORDERS,
		ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS,
		GREEN_DOUBLE_CELL_STYLE_NO_BORDERS,
		IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE,
		ORANGE_IMPORTANT_CELL_STYLE,
		GREEN_IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE_NO_BORDERS,
		ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS,
		GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS,
		IMPORTANT_TOTAL_CELL_STYLE,
		RED_IMPORTANT_TOTAL_CELL_STYLE,
		GREEN_IMPORTANT_TOTAL_CELL_STYLE,
		ORANGE_IMPORTANT_TOTAL_CELL_STYLE,
		BOUND_CELL_STYLE_PREV,
		BOUND_CELL_STYLE_PREV_GREEN,		
		BOUND_CELL_STYLE_PREV_ORANGE,		
		BOUND_CELL_STYLE_PREV_RED,		
		FORMULA_CELL_STYLE,
		RED_FORMULA_CELL_STYLE,
		GREEN_FORMULA_CELL_STYLE,
		ORANGE_FORMULA_CELL_STYLE,		
		JOINT_CELL_STYLE,
		BORDER_RIGHT_CELL_STYLE,
		BORDER_LEFT_CELL_STYLE,
		FINAL_CELL_STYLE;
		
		private static final String DATA_FORMAT = "#,###,##0.#0";

		private static Map<PayrollCellStyle, CellStyle> getStyles(Workbook wb, DataFormat format) {
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			Font headerRedFont = wb.createFont();
			headerRedFont.setBold(true);
			headerRedFont.setColor(IndexedColors.RED.getIndex());
			
			Font headerGreenFont = wb.createFont();
			headerGreenFont.setBold(true);
			headerGreenFont.setColor(IndexedColors.GREEN.getIndex());
			
			Font headerOrangeFont = wb.createFont();
			headerOrangeFont.setBold(true);
			headerOrangeFont.setColor(IndexedColors.ORANGE.getIndex());

			Font wrongFont = wb.createFont();
			wrongFont.setColor(IndexedColors.RED.getIndex());
			wrongFont.setFontHeightInPoints((short) 10);
			
			Font warningFont = wb.createFont();
			warningFont.setColor(IndexedColors.ORANGE.getIndex());
			warningFont.setFontHeightInPoints((short) 10);

			Font okFont = wb.createFont();
			okFont.setColor(IndexedColors.GREEN.getIndex());
			okFont.setFontHeightInPoints((short) 10);
			
			
			//Declaring different cell styles
			Map<PayrollCellStyle, CellStyle> stylesMap = new EnumMap<>(PayrollCellStyle.class);
			
			
			CellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(PayrollCellStyle.HEADER_CELL_STYLE, headerCellStyle);
			
			CellStyle stringCellStyle = wb.createCellStyle();
			stringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stringCellStyle.setBorderBottom(BorderStyle.THIN);
			stringCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.STRING_CELL_STYLE, stringCellStyle);
			
			CellStyle stringCellStyleWhiteBack = wb.createCellStyle();
			stringCellStyleWhiteBack.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			stringCellStyleWhiteBack.setFillPattern(FillPatternType.FINE_DOTS);
			stringCellStyleWhiteBack.setBorderBottom(BorderStyle.THIN);
			stringCellStyleWhiteBack.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.STRING_CELL_STYLE_WHITE_BACK, stringCellStyleWhiteBack);
			
			CellStyle redStringCellStyle = wb.createCellStyle();
			redStringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redStringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redStringCellStyle.setBorderBottom(BorderStyle.THIN);
			redStringCellStyle.setBorderTop(BorderStyle.THIN);
			redStringCellStyle.setFont(wrongFont);
			stylesMap.put(PayrollCellStyle.RED_STRING_CELL_STYLE, redStringCellStyle);
			
			CellStyle blankDiffCellStyle = wb.createCellStyle();
			blankDiffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			blankDiffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(PayrollCellStyle.BLANK_DIFF_CELL_STYLE, blankDiffCellStyle);
			
			CellStyle doubleCellStyle = wb.createCellStyle();
			doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			doubleCellStyle.setBorderBottom(BorderStyle.THIN);
			doubleCellStyle.setBorderTop(BorderStyle.THIN);
			doubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.DOUBLE_CELL_STYLE, doubleCellStyle);
			
			CellStyle redDoubleCellStyle = wb.createCellStyle();
			redDoubleCellStyle.setFont(wrongFont);
			redDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			redDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			redDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_DOUBLE_CELL_STYLE, redDoubleCellStyle);
			
			CellStyle orangeDoubleCellStyle = wb.createCellStyle();
			orangeDoubleCellStyle.setFont(warningFont);
			orangeDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			orangeDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE, orangeDoubleCellStyle);
			
			CellStyle greenDoubleCellStyle = wb.createCellStyle();
			greenDoubleCellStyle.setFont(okFont);
			greenDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			greenDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			greenDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE, greenDoubleCellStyle);
			
			CellStyle redDoubleCellStyleNoBorders = wb.createCellStyle();
			redDoubleCellStyleNoBorders.setFont(wrongFont);
			redDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_DOUBLE_CELL_STYLE_NO_BORDERS, redDoubleCellStyleNoBorders);
			
			
			CellStyle greenDoubleCellStyleNoBorders = wb.createCellStyle();
			greenDoubleCellStyleNoBorders.setFont(okFont);
			greenDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE_NO_BORDERS, greenDoubleCellStyleNoBorders);
			
			
			CellStyle orangeDoubleCellStyleNoBorders = wb.createCellStyle();
			orangeDoubleCellStyleNoBorders.setFont(warningFont);
			orangeDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS, orangeDoubleCellStyleNoBorders);
			
			CellStyle importantCellStyle = wb.createCellStyle();
			importantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			importantCellStyle.setFont(headerFont);
			importantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantCellStyle.setBorderBottom(BorderStyle.THIN);
			importantCellStyle.setBorderTop(BorderStyle.THIN);
			importantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.IMPORTANT_CELL_STYLE, importantCellStyle);
			
			CellStyle redImportantCellStyle = wb.createCellStyle();
			redImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyle.setFont(headerRedFont);
			redImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_CELL_STYLE, redImportantCellStyle);
			
			CellStyle orangeImportantCellStyle = wb.createCellStyle();
			orangeImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyle.setFont(headerOrangeFont);
			orangeImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_CELL_STYLE, orangeImportantCellStyle);
			
			CellStyle greenImportantCellStyle = wb.createCellStyle();
			greenImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyle.setFont(headerGreenFont);
			greenImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_CELL_STYLE, greenImportantCellStyle);
			
			CellStyle greenImportantCellStyleNoBorders = wb.createCellStyle();
			greenImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyleNoBorders.setFont(headerGreenFont);
			greenImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
//			greenImportantCellStyleNoBorders.setBorderBottom(BorderStyle.THIN);
//			greenImportantCellStyleNoBorders.setBorderTop(BorderStyle.THIN);
			greenImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS, greenImportantCellStyleNoBorders);
			
			CellStyle redImportantCellStyleNoBorders = wb.createCellStyle();
			redImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyleNoBorders.setFont(headerRedFont);
			redImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
//			redImportantCellStyleNoBorders.setBorderBottom(BorderStyle.THIN);
//			redImportantCellStyleNoBorders.setBorderTop(BorderStyle.THIN);
			redImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_CELL_STYLE_NO_BORDERS, redImportantCellStyleNoBorders);
			
			CellStyle orangeImportantCellStyleNoBorders = wb.createCellStyle();
			orangeImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyleNoBorders.setFont(headerOrangeFont);
			orangeImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
//			orangeImportantCellStyleNoBorders.setBorderBottom(BorderStyle.THIN);
//			orangeImportantCellStyleNoBorders.setBorderTop(BorderStyle.THIN);
			orangeImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS, orangeImportantCellStyleNoBorders);
			
			CellStyle importantTotalCellStyle = wb.createCellStyle();
			importantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			importantTotalCellStyle.setFont(headerFont);
			importantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			importantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			importantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE, importantTotalCellStyle);
			
			CellStyle redImportantTotalCellStyle = wb.createCellStyle();
			redImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redImportantTotalCellStyle.setFont(headerRedFont);
			redImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_TOTAL_CELL_STYLE, redImportantTotalCellStyle);
			
			CellStyle greenImportantTotalCellStyle = wb.createCellStyle();
			greenImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenImportantTotalCellStyle.setFont(headerGreenFont);
			greenImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_TOTAL_CELL_STYLE, greenImportantTotalCellStyle);
			
			CellStyle orangeImportantTotalCellStyle = wb.createCellStyle();
			orangeImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			orangeImportantTotalCellStyle.setFont(headerOrangeFont);
			orangeImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_TOTAL_CELL_STYLE, orangeImportantTotalCellStyle);

			CellStyle boundCellStylePrev = wb.createCellStyle();
			boundCellStylePrev.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrev.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrev.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrev.setBorderTop(BorderStyle.THIN);
			boundCellStylePrev.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV, boundCellStylePrev);

			CellStyle boundCellStylePrevGreen = wb.createCellStyle();
			boundCellStylePrevGreen.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevGreen.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevGreen.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_GREEN, boundCellStylePrevGreen);		
			
			CellStyle boundCellStylePrevOrange = wb.createCellStyle();
			boundCellStylePrevOrange.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevOrange.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevOrange.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_ORANGE, boundCellStylePrevOrange);		
			
			CellStyle boundCellStylePrevRed = wb.createCellStyle();
			boundCellStylePrevRed.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevRed.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevRed.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_RED, boundCellStylePrevRed);		

			CellStyle formulaCellStyle = wb.createCellStyle();
			formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			formulaCellStyle.setBorderBottom(BorderStyle.THIN);
			formulaCellStyle.setBorderTop(BorderStyle.THIN);
			formulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.FORMULA_CELL_STYLE, formulaCellStyle);
			
			CellStyle redFormulaCellStyle = wb.createCellStyle();
			redFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			redFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			redFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			redFormulaCellStyle.setFont(wrongFont);
			stylesMap.put(PayrollCellStyle.RED_FORMULA_CELL_STYLE, redFormulaCellStyle);
			
			CellStyle greenFormulaCellStyle = wb.createCellStyle();
			greenFormulaCellStyle.setFont(okFont);
			greenFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			greenFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			greenFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_FORMULA_CELL_STYLE, greenFormulaCellStyle);

			CellStyle yellowFormulaCellStyle = wb.createCellStyle();
			yellowFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			yellowFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			yellowFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			yellowFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			yellowFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			yellowFormulaCellStyle.setFont(warningFont);
			stylesMap.put(PayrollCellStyle.ORANGE_FORMULA_CELL_STYLE, yellowFormulaCellStyle);
			
			CellStyle jointCellStyle = wb.createCellStyle();
			jointCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			jointCellStyle.setBorderLeft(BorderStyle.THIN);
			jointCellStyle.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.JOINT_CELL_STYLE, jointCellStyle);
			
			CellStyle borderRightCellStyle = wb.createCellStyle();
			borderRightCellStyle.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BORDER_RIGHT_CELL_STYLE, borderRightCellStyle);
			
			CellStyle borderLeftCellStyle = wb.createCellStyle();
			borderLeftCellStyle.setBorderLeft(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BORDER_LEFT_CELL_STYLE, borderLeftCellStyle);
			
			CellStyle finalCellStyle = wb.createCellStyle();
			finalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			finalCellStyle.setBorderRight(BorderStyle.THIN);
			finalCellStyle.setBorderBottom(BorderStyle.THIN);
			finalCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.FINAL_CELL_STYLE, finalCellStyle);
			
			
			return stylesMap;
		}
	}
	
	public static class TotalsReferences {
		private EnterprisePayrollExcelChecks checks;
		private String workplace;
		private Integer rowNum;
				
		public TotalsReferences(EnterprisePayrollExcelChecks checks, String workplace, Integer rowNum) {
			super();
			this.checks = checks;
			this.workplace = workplace;
			this.rowNum = rowNum;
		}
		
		public EnterprisePayrollExcelChecks getChecks() {
			return checks;
		}
		public TotalsReferences setChecks(EnterprisePayrollExcelChecks checks) {
			this.checks = checks;
			return this;
		}
		public String getWorkplace() {
			return workplace;
		}
		public TotalsReferences setWorkplace(String workplace) {
			this.workplace = workplace;
			return this;
		}
		public Integer getRowNum() {
			return rowNum;
		}
		public TotalsReferences setRowNum(Integer rowNum) {
			this.rowNum = rowNum;
			return this;
		}
			
	}
	
	private static List<String> periodsBetween(Date startDate, Date endDate) {
		if (startDate == null || endDate == null || startDate.compareTo(endDate) > 0)
			return Collections.emptyList();
		
		LinkedList<String> months = new LinkedList<>();
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		calendar.setTime(startDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		EnterprisePayrollExcelUtils.clearCalendar(calendar);
		
		while (calendar.getTime().compareTo(endDate) <= 0) {
			months.add(EnterprisePayrollExcelUtils.getMonthYearName(calendar.getTime()));
			calendar.add(Calendar.MONTH, 1);
		}
		return months;
	}
	
	private static Map<String/*workplace*/, Map<String/*period name*/, Map<String/*type*/, Double/*amount*/>>> getContractData4Complete(Optional<Map<String, Map<String, List<ContractData>>>> optContractData, Date startDate, Date endDate) {
		if (optContractData.isPresent() && startDate != null &&endDate != null) {
			Map<String, Map<String, List<ContractData>>> contractData = optContractData.get();
			
			Map<String, Map<String, Map<String, Double>>> returnedMap = new LinkedHashMap<>();
			
			contractData.forEach((k, v) -> {
				if (k != null) {
					String workplaceName = k;
					LinkedHashMap<String, Map<String, Double>> periodMap = new LinkedHashMap<>();
					v.forEach((k1, v1) -> {
						if (k1 != null) {
							String typeName = k1;
							v1.stream()
							.filter(cd -> cd != null && cd.getExpression() != null)
							.forEach(cd -> {
								try {
									Double value = Double.parseDouble(cd.getExpression());
									Date start = cd.getStartDate().compareTo(startDate) > 0  ? cd.getEndDate() : startDate;//not null field
									Date end = cd.getEndDate() != null ? cd.getEndDate() : endDate;
									
									periodsBetween(start, end).forEach(dateStr -> {
										if (periodMap.containsKey(dateStr)) {
											Map<String, Double> typeMap = periodMap.get(dateStr);
											if (typeMap.containsKey(typeName)) {												
												typeMap.put(typeName, EnterprisePayrollExcelUtils.sumThings(typeMap.get(typeName), value));
											} else {
												typeMap.put(typeName, value);
											}
										} else {
											LinkedHashMap<String, Double> typeMap = new LinkedHashMap<>();
											typeMap.put(typeName, value);
											periodMap.put(dateStr, typeMap);
										}
									});
									
								} catch (NumberFormatException e) {}
							});
						}
					});
					returnedMap.put(workplaceName, periodMap);
				}
			});
			return returnedMap;
		} else {
			return Collections.emptyMap();
		}
	}
	
	public static class EnterprisePayroll implements IEnterprisePayroll, Cloneable {
		
		protected Date startDate;
		protected Date endDate;
		
		protected String employee;
		protected String employeeNaf;
		protected String ccc;
		protected String workplace;
		protected SalaryType salaryType;

		protected Double raw;
		protected Double employeeSS;
		protected Double irpf;
		protected Double liquid;
		protected Double enterpriseSS;
		protected Double totalCost;
		protected Double totalSS;
		protected Double bonuses;

		protected Double cgcBase;
		protected Double irpfBase;
		protected Double moneyIrpfBase;
		protected Double inKindIrpfBase;

		protected Double cgc;
		protected Double cgp;
		protected Double unemployment;
		protected Double jobTraining;
		protected Double advancedPayment;
		protected Double otherDeductions;
		protected Double estruc;
		protected Double noEstruct;
		protected Double embargos;

		protected Double cgcEnterprise;
		protected Double cgpEnterprise;
		protected Double unemploymentEnterprise;
		protected Double jobTrainingEnterprise;
		protected Double fogasaEnterprise;
		protected Double estrucEnterprise;
		protected Double noEstructEnterprise;
		
		private IEnterprisePayroll originalPayroll;

		@Override
		public String getEmployee() {
			return employee;
		}

		@Override
		public String getWorkplace() {
			return workplace;
		}
		
		@Override
		public SalaryType getSalaryType() {
			return salaryType;
		}

		@Override
		public Double getRaw() {
			return raw;
		}

		@Override
		public Double getEmployeeSS() {
			return employeeSS;
		}

		@Override
		public Double getIrpf() {
			return irpf;
		}

		@Override
		public Double getLiquid() {
			return liquid;
		}

		@Override
		public Double getEnterpriseSS() {
			return enterpriseSS;
		}

		@Override
		public Double getTotalCost() {
			return totalCost;
		}

		@Override
		public Double getTotalSS() {
			return totalSS;
		}

		@Override
		public Double getBonuses() {
			return bonuses;
		}

		@Override
		public Double getCgcBase() {
			return cgcBase;
		}

		@Override
		public Double getIrpfBase() {
			return irpfBase;
		}

		@Override
		public Double getCgc() {
			return cgc;
		}

		@Override
		public Double getCgp() {
			return cgp;
		}

		@Override
		public Double getUnemployment() {
			return unemployment;
		}

		@Override
		public Double getJobTraining() {
			return jobTraining;
		}

		@Override
		public Double getAdvancedPayment() {
			return advancedPayment;
		}

		@Override
		public Double getOtherDeductions() {
			return otherDeductions;
		}

		@Override
		public Double getCgcEnterprise() {
			return cgcEnterprise;
		}

		@Override
		public Double getCgpEnterprise() {
			return cgpEnterprise;
		}

		@Override
		public Double getUnemploymentEnterprise() {
			return unemploymentEnterprise;
		}

		@Override
		public Double getJobTrainingEnterprise() {
			return jobTrainingEnterprise;
		}

		@Override
		public Double getFogasaEnterprise() {
			return fogasaEnterprise;
		}

		@Override
		public Double getEstrucEnterprise() {
			return estrucEnterprise;
		}

		@Override
		public Double getNoEstructEnterprise() {
			return noEstructEnterprise;
		}

		@Override
		public Double getEstruc() {
			return estruc;
		}

		@Override
		public Double getNoEstruct() {
			return noEstruct;
		}

		@Override
		public Double getEmbargos() {
			return embargos;
		}

		@Override
		public Double getInkindIrpfBase() {
			return inKindIrpfBase;
		}

		@Override
		public Double getMoneyIrpfBase() {
			return moneyIrpfBase;
		}

		@Override
		public Date getStartDate() {
			return startDate;
		}

		@Override
		public Date getEndDate() {
			return endDate;
		}

		@Override
		public IEnterprisePayroll getOriginalPayroll() {
			return originalPayroll;
		}

		@Override
		public IEnterprisePayroll setOriginalPayroll(IEnterprisePayroll originalPayroll) {
			this.originalPayroll = originalPayroll;
			return this;
		}

		@Override
		public String getEmployeeNaf() {
			return employeeNaf;
		}

		@Override
		public String getCcc() {
			return ccc;
		}
		
		@Override
		protected EnterprisePayroll clone() throws CloneNotSupportedException {
			EnterprisePayroll cloned = new EnterprisePayroll();
			cloned.startDate = this.startDate;
			cloned.endDate = this.endDate;
			
			cloned.employee = this.employee;
			cloned.employeeNaf = this.employeeNaf;
			cloned.ccc = this.ccc;
			cloned.workplace = this.workplace;
			cloned.salaryType = this.salaryType;

			cloned.raw = this.raw;
			cloned.employeeSS = this.employeeSS;
			cloned.irpf = this.irpf;
			cloned.liquid = this.liquid;
			cloned.enterpriseSS = this.enterpriseSS;
			cloned.totalCost = this.totalCost;
			cloned.totalSS = this.totalSS;
			cloned.bonuses = this.bonuses;

			cloned.cgcBase = this.cgcBase;
			cloned.irpfBase = this.irpfBase;
			cloned.moneyIrpfBase = this.moneyIrpfBase;
			cloned.inKindIrpfBase = this.inKindIrpfBase;

			cloned.cgc = this.cgc;
			cloned.cgp = this.cgp;
			cloned.unemployment = this.cgp;
			cloned.jobTraining = this.jobTraining;
			cloned.advancedPayment = this.advancedPayment;
			cloned.otherDeductions = this.otherDeductions;
			cloned.estruc = this.estruc;
			cloned.noEstruct = this.noEstruct;
			cloned.embargos = this.embargos;

			cloned.cgcEnterprise = cgcEnterprise;
			cloned.cgpEnterprise = this.cgpEnterprise;
			cloned.unemploymentEnterprise = this.unemploymentEnterprise;
			cloned.jobTrainingEnterprise = this.jobTrainingEnterprise;
			cloned.fogasaEnterprise = this.fogasaEnterprise;
			cloned.estrucEnterprise = this.estrucEnterprise;
			cloned.noEstructEnterprise = this.noEstructEnterprise;
			
			return cloned;
		}		

	}
	
	private static class EnterprisePayrollExcelChecks {
		
		private boolean raw;
		private boolean employeeSS;
		private boolean irpf;
		private boolean liquid;
		private boolean enterpriseSS;
		private boolean bonuses;
		private boolean totalCost;
		private boolean cgcBase;
		private boolean irpfBase;
		private boolean inKindIrpfBase;
		private boolean moneyIrpfBase;
		private boolean cgc;
		private boolean cgp;
		private boolean unemployment;
		private boolean jobTraining;
		private boolean advancedPayment;
		private boolean otherDeductions;
		private boolean cgcEnterprise;
		private boolean cgpEnterprise;
		private boolean unemploymentEnterprise;
		private boolean jobTrainingEnterprise;
		private boolean fogasaEnterprise;
		private boolean noEstructEnterprise;
		private boolean other;
		private boolean estrucEnterprise;
		private boolean estruc;
		private boolean noEstruct;
		private boolean extraH;
		private boolean extraHEnterprise;
		private boolean embargos;
		private boolean totalSS;
		
		private boolean fundae;
		
		private EnterprisePayrollExcelChecks (List<IEnterprisePayroll> payrolls) {
			this(payrolls, Collections.emptyMap());
		}
		
		private EnterprisePayrollExcelChecks (List<IEnterprisePayroll> payrolls, Map<String, Map<String, Double>> workplaceContractDataMap) {
			this.raw = !payrolls.stream().allMatch(p -> p.getRaw() == null);
			this.employeeSS = !payrolls.stream()
					.allMatch(p -> p.getEmployeeSS() == null);
			this.irpf = !payrolls.stream().allMatch(p -> p.getIrpf() == null);
			this.liquid = !payrolls.stream().allMatch(p -> p.getLiquid() == null);
			this.enterpriseSS = !payrolls.stream()
					.allMatch(p -> p.getEnterpriseSS() == null);

			this.bonuses = !payrolls.stream()
					.allMatch(p -> p.getBonuses() == null
					|| p.getBonuses() == 0d);
			
			this.totalCost = (raw || enterpriseSS || bonuses);
			this.cgcBase = !payrolls.stream()
					.allMatch(p -> p.getCgcBase() == null);
			this.irpfBase = !payrolls.stream()
					.allMatch(p -> p.getIrpfBase() == null);
			this.inKindIrpfBase = !payrolls.stream()
					.allMatch(p -> p.getInkindIrpfBase() == null);
			this.moneyIrpfBase = !payrolls.stream()
					.allMatch(p -> p.getMoneyIrpfBase() == null);
			this.cgc = !payrolls.stream().allMatch(
					p -> p.getCgc() == null || p.getCgc() == 0);
			this.cgp = !payrolls.stream().allMatch(
					p -> p.getCgp() == null || p.getCgp() == 0);
			this.unemployment = !payrolls.stream()
					.allMatch(p -> p.getUnemployment() == null);
			this.jobTraining = !payrolls.stream()
					.allMatch(p -> p.getJobTraining() == null);
			this.advancedPayment = !payrolls.stream()
					.allMatch(p -> p.getAdvancedPayment() == null
							|| p.getAdvancedPayment() == 0d);
			this.otherDeductions = !payrolls.stream()
					.allMatch(p -> p.getOtherDeductions() == null
							|| p.getOtherDeductions() == 0d);
			this.cgcEnterprise = !payrolls.stream()
					.allMatch(p -> p.getCgcEnterprise() == null
							|| p.getCgcEnterprise() == 0d);
			this.cgpEnterprise = !payrolls.stream()
					.allMatch(p -> p.getCgpEnterprise() == null
							|| p.getCgpEnterprise() == 0d);
			this.unemploymentEnterprise = !payrolls.stream()
					.allMatch(p -> p.getUnemploymentEnterprise() == null
							|| p.getUnemploymentEnterprise() == 0d);
			this.jobTrainingEnterprise = !payrolls.stream()
					.allMatch(p -> p.getJobTrainingEnterprise() == null
							|| p.getUnemploymentEnterprise() == 0d);
			this.fogasaEnterprise = !payrolls.stream()
					.allMatch(p -> p.getFogasaEnterprise() == null
							|| p.getFogasaEnterprise() == 0d);
			this.estrucEnterprise = !payrolls.stream()
					.allMatch(p -> p.getEstrucEnterprise() == null
							|| p.getEstrucEnterprise() == 0d);
			this.noEstructEnterprise = !payrolls.stream()
					.allMatch(p -> p.getNoEstructEnterprise() == null
							|| p.getNoEstructEnterprise() == 0d);
			
			this.other = otherDeductions || advancedPayment;
			
			this.estruc = !payrolls.stream()
					.allMatch(p -> p.getEstruc() == null
					|| p.getEstruc() == 0d);
			
			this.noEstruct = !payrolls.stream()
					.allMatch(p -> p.getNoEstruct() == null
					|| p.getNoEstruct() == 0d);
			this.extraH = estruc || noEstruct;
			this.extraHEnterprise = estrucEnterprise || noEstructEnterprise;
			this.embargos = !payrolls.stream()
					.allMatch(p -> p.getEmbargos() == null
					|| p.getEmbargos() == 0d);
			
			this.totalSS = (employeeSS || enterpriseSS || bonuses);
			
			this.fundae = checkContractDataField(workplaceContractDataMap, FUNDAE);
			
		}
		
		private boolean checkContractDataField(Map<String, Map<String, Double>> workplaceContractDataMap, String fieldKey) {
			if (workplaceContractDataMap != null && !workplaceContractDataMap.isEmpty()) {
				for(Entry<String, Map<String, Double>> entry : workplaceContractDataMap.entrySet()) {
					Map<String, Double> valueMap = entry.getValue();
					if (valueMap.containsKey(fieldKey) && valueMap.get(fieldKey) != null && valueMap.get(fieldKey) > 0) {
						return true;
					}
				}	
			}
			return false;
		}

		public boolean isRaw() {
			return raw;
		}

		public boolean isEmployeeSS() {
			return employeeSS;
		}

		public boolean isIrpf() {
			return irpf;
		}

		public boolean isLiquid() {
			return liquid;
		}

		public boolean isEnterpriseSS() {
			return enterpriseSS;
		}

		public boolean isBonuses() {
			return bonuses;
		}

		public boolean isTotalCost() {
			return totalCost;
		}

		public boolean isCgcBase() {
			return cgcBase;
		}

		public boolean isIrpfBase() {
			return irpfBase;
		}

		public boolean isInKindIrpfBase() {
			return inKindIrpfBase;
		}

		public boolean isMoneyIrpfBase() {
			return moneyIrpfBase;
		}

		public boolean isCgc() {
			return cgc;
		}

		public boolean isCgp() {
			return cgp;
		}

		public boolean isUnemployment() {
			return unemployment;
		}

		public boolean isJobTraining() {
			return jobTraining;
		}

		public boolean isAdvancedPayment() {
			return advancedPayment;
		}

		public boolean isOtherDeductions() {
			return otherDeductions;
		}

		public boolean isCgcEnterprise() {
			return cgcEnterprise;
		}

		public boolean isCgpEnterprise() {
			return cgpEnterprise;
		}

		public boolean isUnemploymentEnterprise() {
			return unemploymentEnterprise;
		}

		public boolean isJobTrainingEnterprise() {
			return jobTrainingEnterprise;
		}

		public boolean isFogasaEnterprise() {
			return fogasaEnterprise;
		}

		public boolean isOther() {
			return other;
		}

		public boolean isExtraH() {
			return extraH;
		}

		public boolean isExtraHEnterprise() {
			return extraHEnterprise;
		}

		public boolean isEmbargos() {
			return embargos;
		}

		public boolean isTotalSS() {
			return totalSS;
		}
		
		public boolean isFundae() {
			return fundae;
		}

	}
}
