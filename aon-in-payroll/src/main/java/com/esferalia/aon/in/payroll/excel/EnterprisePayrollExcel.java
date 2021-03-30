package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.DSLContext;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Bonus;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterprisePayrollExcel {

	private static final LinkedHashMap<String, String> DEFAULT_HEADER;
	private static final LinkedHashMap<String, String> DEFAULT_HEADER_SUMMARY;

	static {
		//COMPLETE
		{
			DEFAULT_HEADER = new LinkedHashMap<String, String>();
			DEFAULT_HEADER.put("employee", "NOMBRE");
			DEFAULT_HEADER.put("type", "TIPO");
			DEFAULT_HEADER.put("raw", "BRUTO");
			DEFAULT_HEADER.put("enterpriseSS", "SEG. SOCIAL");
			DEFAULT_HEADER.put("totalCost", "COSTE TOTAL");
			
			DEFAULT_HEADER.put("joint1", "");
			
			DEFAULT_HEADER.put("employeeSS", "SEG. SOCIAL");
			DEFAULT_HEADER.put("irpf", "IRPF");
			DEFAULT_HEADER.put("other", "OTR. DEDUC.");
			DEFAULT_HEADER.put("liquid", "LÍQUIDO");
			
			DEFAULT_HEADER.put("joint2", "");
			
			DEFAULT_HEADER.put("totalSS", "S.S. TOTAL");
			
			DEFAULT_HEADER.put("joint3", "");
			
			DEFAULT_HEADER.put("cgcEnterprise", "CONT. COM.");
			DEFAULT_HEADER.put("cgpEnterprise", "CONT. PROF.");
			DEFAULT_HEADER.put("unemploymentEnterprise", "DESEMPL.");
			DEFAULT_HEADER.put("jobTrainingEnterprise", "FORM. PROF");
			DEFAULT_HEADER.put("fogasaEnterprise", "FOGASA");
			DEFAULT_HEADER.put("extraHEnterprise", "H. EXTRAS");
			DEFAULT_HEADER.put("bonuses", "BONIF.");
			
			DEFAULT_HEADER.put("joint4", "");
			
			DEFAULT_HEADER.put("cgc", "CONT. COMUNES");
			DEFAULT_HEADER.put("cgp", "CONT. PROF.");
			DEFAULT_HEADER.put("unemployment", "DESEMPLEO");
			DEFAULT_HEADER.put("jobTraining", "FORM. PROF.");
			DEFAULT_HEADER.put("extraH", "H. EXTRAS");
			
			DEFAULT_HEADER.put("advancedPayments", "ANTICIPOS");
			DEFAULT_HEADER.put("embargos", "EMBARGOS");
			DEFAULT_HEADER.put("otherDeductions", "OTR. DEDUC.");
			
			DEFAULT_HEADER.put("joint5", "");
			
			DEFAULT_HEADER.put("cgcBase", "BASE C.C.");
			DEFAULT_HEADER.put("irpfBase", "BASE IRPF");
		}
		//SUMMARY
		{
			DEFAULT_HEADER_SUMMARY = new LinkedHashMap<String, String>();
			DEFAULT_HEADER_SUMMARY.put("employee", "NOMBRE");
			DEFAULT_HEADER_SUMMARY.put("type", "TIPO");
			DEFAULT_HEADER_SUMMARY.put("raw", "BRUTO");
			DEFAULT_HEADER_SUMMARY.put("enterpriseSS", "SEG. SOCIAL");
			DEFAULT_HEADER_SUMMARY.put("totalCost", "COSTE TOTAL");
			
			DEFAULT_HEADER_SUMMARY.put("joint1", "");
			
			DEFAULT_HEADER_SUMMARY.put("employeeSS", "SEG. SOCIAL");
			DEFAULT_HEADER_SUMMARY.put("irpf", "IRPF");
			DEFAULT_HEADER_SUMMARY.put("other", "OTRAS DEDUCCIONES");
			DEFAULT_HEADER_SUMMARY.put("liquid", "LÍQUIDO");
			
			DEFAULT_HEADER_SUMMARY.put("joint2", "");
			
			DEFAULT_HEADER_SUMMARY.put("totalSS", "S.S. TOTAL");
		}
	}
	
	public static void simpleEnterprisePayrollGenerator (String domainName, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date startDate, Date endDate, ExcelType excelType) {
		
		AONContext aonContext = AONContext.getAONContext(domainName, "");
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
		if (eId == null || eId == 0)
			eId = AON.getWorkplace(aonContext.getDomainName()
				, aonContext.getDomainId()
				, aonContext.getUser()
				, w -> w.getIdProperty().eq(atomicWorkplace.get()))
				.getEnterprise();
		
		
		try {
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, startDate, endDate, eId, wId)
					.collect(Collectors.toList());
			
			String enterpriseName = getEnterpriseName(domainName, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, startDate
					, endDate
					, excelType);	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void simpleEnterprisePayrollGenerator (String domainName, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date date, ExcelType excelType) {
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Integer month = c.get(Calendar.MONTH)+1;
		Integer year = c.get(Calendar.YEAR);
		
		AONContext aonContext = AONContext.getAONContext(domainName, "");
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
		if (eId == null || eId == 0)
			eId = AON.getWorkplace(aonContext.getDomainName()
				, aonContext.getDomainId()
				, aonContext.getUser()
				, w -> w.getIdProperty().eq(atomicWorkplace.get()))
				.getEnterprise();
		
		
		try {
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, month, year, eId, wId)
					.collect(Collectors.toList());
			
			String enterpriseName = getEnterpriseName(domainName, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, getDateString(month, year)
					, excelType);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void simpleEnterprisePayrollGenerator (String domainName, OutputStream outputStream, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Date date, ExcelType excelType, Collection<Integer> types) {
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Integer month = c.get(Calendar.MONTH)+1;
		Integer year = c.get(Calendar.YEAR);
		
		AONContext aonContext = AONContext.getAONContext(domainName, "");
		
		Integer wId = null;
		Integer eId = null;
		if (workplaceId.isPresent())
			wId = workplaceId.get();
		if (enterpriseId.isPresent())
			eId = enterpriseId.get();
		
		AtomicInteger atomicWorkplace = new AtomicInteger(wId != null ? wId : 0);
		if (eId == null || eId == 0)
			eId = AON.getWorkplace(aonContext.getDomainName()
				, aonContext.getDomainId()
				, aonContext.getUser()
				, w -> w.getIdProperty().eq(atomicWorkplace.get()))
				.getEnterprise();
		
		
		try {
			Collection<IEnterprisePayroll> payrolls =
					getEnterprisePayrolls(aonContext, month, year, eId, wId)
					.filter(p -> types.contains(p.getSalaryType().ordinal()))
					.sorted(Comparator.comparing(IEnterprisePayroll::getEmployee))
					.collect(Collectors.toList());
			
			String enterpriseName = getEnterpriseName(domainName, eId, wId);
			write(outputStream
					, payrolls
					, Optional.empty()
					, enterpriseName
					, getDateString(month, year)
					, excelType);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<LinkedHashMap<String, String>> header, String enterpriseName, Date startDate, Date endDate, ExcelType excelType)
			throws IOException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
		String strStartDate = df.format(startDate);
		String strEndDate = df.format(endDate);
		
		String dateString = "del " + strStartDate + " al " + strEndDate;
		
		write(outputStream, payrolls, header, enterpriseName, dateString, excelType);
	}

	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<LinkedHashMap<String, String>> header, String enterpriseName, String dateString, ExcelType excelType)
			throws IOException {
		Workbook wb = new XSSFWorkbook();

		Font headerFont = wb.createFont();
		headerFont.setBold(true);

		Font wrongFont = wb.createFont();
		wrongFont.setColor(IndexedColors.RED.getIndex());

		Font okFont = wb.createFont();
		okFont.setColor(IndexedColors.GREEN.getIndex());
		
		DataFormat format = wb.createDataFormat();
		

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

		CellStyle stringCellStyle = wb.createCellStyle();
		stringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		stringCellStyle.setBorderBottom(BorderStyle.THIN);
		stringCellStyle.setBorderTop(BorderStyle.THIN);

		CellStyle doubleCellStyle = wb.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

		CellStyle importantCellStyle = wb.createCellStyle();
		importantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		importantCellStyle.setFont(headerFont);
		importantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		importantCellStyle.setBorderBottom(BorderStyle.THIN);
		importantCellStyle.setBorderTop(BorderStyle.THIN);
		importantCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
		
		CellStyle importantTotalCellStyle = wb.createCellStyle();
		importantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		importantTotalCellStyle.setFont(headerFont);
		importantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		importantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
		importantTotalCellStyle.setBorderTop(BorderStyle.THIN);
		importantTotalCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

		CellStyle boundCellStylePrev = wb.createCellStyle();
		boundCellStylePrev.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		boundCellStylePrev.setFillPattern(FillPatternType.FINE_DOTS);
		boundCellStylePrev.setBorderBottom(BorderStyle.THIN);
		boundCellStylePrev.setBorderTop(BorderStyle.THIN);
		boundCellStylePrev.setBorderRight(BorderStyle.THIN);

		CellStyle boundCellStylePost = wb.createCellStyle();
		boundCellStylePost.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		boundCellStylePost.setFillPattern(FillPatternType.FINE_DOTS);
		boundCellStylePost.setBorderBottom(BorderStyle.THIN);
		boundCellStylePost.setBorderTop(BorderStyle.THIN);

		CellStyle noDiffCellStyle = wb.createCellStyle();
		noDiffCellStyle.setFont(okFont);
		noDiffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		noDiffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		noDiffCellStyle.setBorderBottom(BorderStyle.THIN);
		noDiffCellStyle.setBorderTop(BorderStyle.THIN);
		noDiffCellStyle.setBorderLeft(BorderStyle.THIN);
		noDiffCellStyle.setBorderRight(BorderStyle.THIN);
		noDiffCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

		CellStyle diffCellStyle = wb.createCellStyle();
		diffCellStyle.setFont(wrongFont);
		diffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		diffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		diffCellStyle.setBorderBottom(BorderStyle.THIN);
		diffCellStyle.setBorderTop(BorderStyle.THIN);
		diffCellStyle.setBorderLeft(BorderStyle.THIN);
		diffCellStyle.setBorderRight(BorderStyle.THIN);
		diffCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

		CellStyle formulaCellStyle = wb.createCellStyle();
		formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
		formulaCellStyle.setBorderTop(BorderStyle.THIN);
		formulaCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

		CellStyle formulaCellStyleBound = wb.createCellStyle();
		formulaCellStyleBound.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyleBound.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyleBound.setBorderBottom(BorderStyle.THIN);
		formulaCellStyleBound.setBorderTop(BorderStyle.THIN);
		formulaCellStyleBound.setBorderRight(BorderStyle.THIN);
		
		CellStyle jointCellStyle = wb.createCellStyle();
		jointCellStyle.setBorderLeft(BorderStyle.THIN);
		jointCellStyle.setBorderRight(BorderStyle.THIN);

		Row row = null;

		Sheet totals = null;
		if (payrolls.stream().map(IEnterprisePayroll::getWorkplace).distinct().count() > 1) {
			totals = wb.createSheet(WorkbookUtil.createSafeSheetName("TOTALES"));
			// Sheet of total amounts by workplace

			LinkedHashMap<String, String> finalHeader = new LinkedHashMap<String, String>();

			if (header.isPresent()) {
				LinkedHashMap<String, String> customHeader = header.get();
				finalHeader.putAll(customHeader);
			} else {
				if (excelType == ExcelType.COMPLETE)
					finalHeader.putAll(DEFAULT_HEADER);
				else if (excelType == ExcelType.SUMMARY)
					finalHeader.putAll(DEFAULT_HEADER_SUMMARY);
			}

			finalHeader.put("employee", "CENTRO DE TRABAJO");

			Iterator<String> itHead = finalHeader.keySet().iterator();
			
			int lCell = excelType == ExcelType.COMPLETE ? 31 : 11;
			
			totals.addMergedRegion(new CellRangeAddress(0, 0, 0, lCell));
			row = totals.createRow(0);
			Cell enterpriseCell = row.createCell(0);
			enterpriseCell.setCellType(CellType.STRING);
			enterpriseCell.setCellStyle(headerCellStyle);
			enterpriseCell.setCellValue(enterpriseName + " - " + dateString);

			row = totals.createRow(2);

			int cellCount = 0;

			while (itHead.hasNext()) {
				Cell cell = row.createCell(cellCount++);
				cell.setCellType(CellType.STRING);
				String value = itHead.next();
				if (AonStringUtils.containsIgnoreCase(value, "join") || AonStringUtils.containsIgnoreCase(value, "type"))
					cell.setCellStyle(jointCellStyle);
				else {
					cell.setCellValue(finalHeader.get(value));
					cell.setCellStyle(headerCellStyle);
				}
			}

			{
				int lastCell = row.getLastCellNum() - 1;
				row = totals.createRow(1);
				totals.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
				totals.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));
				Cell epCell = row.createCell(2, CellType.STRING);
				epCell.setCellStyle(headerCellStyle);
				epCell.setCellValue("EMPRESA");
				
				Cell jointCell = row.createCell(5, CellType.STRING);
				jointCell.setCellStyle(jointCellStyle);
				
				totals.addMergedRegion(new CellRangeAddress(1, 1, 6, 9));
				epCell = row.createCell(6, CellType.STRING);
				epCell.setCellStyle(headerCellStyle);
				epCell.setCellValue("EMPLEADO");
				
				jointCell = row.createCell(10, CellType.STRING);
				jointCell.setCellStyle(jointCellStyle);
				
				epCell = row.createCell(11, CellType.STRING);
				epCell.setCellStyle(headerCellStyle);
				epCell.setCellValue("TGSS");
				if (excelType == ExcelType.COMPLETE) {
					jointCell = row.createCell(12, CellType.STRING);
					jointCell.setCellStyle(jointCellStyle);
					
					totals.addMergedRegion(new CellRangeAddress(1, 1, 13, 19));
					Cell entCell = row.createCell(13, CellType.STRING);
					entCell.setCellStyle(headerCellStyle);
					entCell.setCellValue("COTIZACIÓN EMPRESA");
					
					jointCell = row.createCell(20, CellType.STRING);
					jointCell.setCellStyle(jointCellStyle);
					
					totals.addMergedRegion(new CellRangeAddress(1, 1, 21, lastCell));
					epCell= row.createCell(21, CellType.STRING);
					epCell.setCellStyle(headerCellStyle);
					epCell.setCellValue("COTIZACIÓN EMPLEADO");
				}
				
				
			}

		}

		Iterator<String> it = payrolls.stream().map(payroll -> payroll.getWorkplace()).distinct().iterator();

		while (it.hasNext()) {
			try {
				String workplace = it.next();

				Object[] arr = payrolls.stream().filter(payroll -> payroll.getWorkplace().equals(workplace)).toArray();



				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(workplace));

				boolean thereIsRaw = !Arrays.stream(arr).allMatch(p -> ((IEnterprisePayroll) p).getRaw() == null);
				boolean thereIsEmployeeSS = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEmployeeSS() == null);
				boolean thereIsIrpf = !Arrays.stream(arr).allMatch(p -> ((IEnterprisePayroll) p).getIrpf() == null);
				boolean thereIsLiquid = !Arrays.stream(arr).allMatch(p -> ((IEnterprisePayroll) p).getLiquid() == null);
				boolean thereIsEnterpriseSS = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEnterpriseSS() == null);

				boolean thereIsBonuses = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getBonuses() == null
						|| ((IEnterprisePayroll) p).getBonuses() == 0d);
				/*
				 * !Arrays.stream(arr) .allMatch(p -> ((IEnterprisePayroll) p).getTotalSS() ==
				 * null);
				 */
				boolean thereIsTotalCost = (thereIsRaw || thereIsEnterpriseSS || thereIsBonuses);
				boolean thereIsCgcBase = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getCgcBase() == null);
				boolean thereIsIrpfBase = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getIrpfBase() == null);
				boolean thereIsCgc = !Arrays.stream(arr).allMatch(
						p -> ((IEnterprisePayroll) p).getCgc() == null || ((IEnterprisePayroll) p).getCgc() == 0);
				boolean thereIsCgp = !Arrays.stream(arr).allMatch(
						p -> ((IEnterprisePayroll) p).getCgp() == null || ((IEnterprisePayroll) p).getCgp() == 0);
				boolean thereIsUnemployment = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getUnemployment() == null);
				boolean thereIsJobTraining = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getJobTraining() == null);
				boolean thereIsAdvancedPayment = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getAdvancedPayment() == null
								|| ((IEnterprisePayroll) p).getAdvancedPayment() == 0d);
				boolean thereIsOtherDeductions = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getOtherDeductions() == null
								|| ((IEnterprisePayroll) p).getOtherDeductions() == 0d);
				boolean thereIsCgcEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getCgcEnterprise() == null
								|| ((IEnterprisePayroll) p).getCgcEnterprise() == 0d);
				boolean thereIsCgpEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getCgpEnterprise() == null
								|| ((IEnterprisePayroll) p).getCgpEnterprise() == 0d);
				boolean thereIsUnemploymentEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getUnemploymentEnterprise() == null
								|| ((IEnterprisePayroll) p).getUnemploymentEnterprise() == 0d);
				boolean thereIsJobTrainingEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getJobTrainingEnterprise() == null
								|| ((IEnterprisePayroll) p).getUnemploymentEnterprise() == 0d);
				boolean thereIsFogasaEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getFogasaEnterprise() == null
								|| ((IEnterprisePayroll) p).getFogasaEnterprise() == 0d);
				boolean thereIsEstrucEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEstrucEnterprise() == null
								|| ((IEnterprisePayroll) p).getEstrucEnterprise() == 0d);
				boolean thereIsNoEstructEnterprise = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getNoEstructEnterprise() == null
								|| ((IEnterprisePayroll) p).getNoEstructEnterprise() == 0d);
				
				boolean thereIsOther = thereIsOtherDeductions || thereIsAdvancedPayment;
				
				boolean thereIsEstruc = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEstruc() == null
						|| ((IEnterprisePayroll) p).getEstruc() == 0d);
				
				boolean thereIsNoEstruct = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getNoEstruct() == null
						|| ((IEnterprisePayroll) p).getNoEstruct() == 0d);
				boolean thereIsExtraH = thereIsEstruc || thereIsNoEstruct;
				boolean thereIsExtraHEnterprise = thereIsEstrucEnterprise || thereIsNoEstructEnterprise;
				boolean thereIsEmbargos = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEmbargos() == null
						|| ((IEnterprisePayroll) p).getEmbargos() == 0d);
				
				boolean thereIsTotalSS = (thereIsEmployeeSS || thereIsEnterpriseSS || thereIsBonuses);
				
				LinkedHashMap<String, String> finalHeader = new LinkedHashMap<String, String>();

				if (header.isPresent()) {
					LinkedHashMap<String, String> customHeader = header.get();
					finalHeader.putAll(customHeader);
				} else {
					if (excelType == ExcelType.COMPLETE)
						finalHeader.putAll(DEFAULT_HEADER);
					else if (excelType == ExcelType.SUMMARY)
						finalHeader.putAll(DEFAULT_HEADER_SUMMARY);
				}

				String rawColumn = null;
				String enterpriseSSColumn = null;
				String employeeSSColumn = null;
				String bonusColumn = null;
				String otherDecutionsColumn = null;
				String advancedPaymentsColumn = null;
				
				ArrayList<Integer> joints = new ArrayList<Integer>(5);
				ArrayList<Integer> importantCells = new ArrayList<Integer>(3);
				
				
				int empFirstCell = 3;
				int entFirstCell = 2;
				int tgssCell = 4;
				int entQuoteFirstCell;
				int empQuoteFirstCell;
				{

					if (!thereIsRaw)
						finalHeader.remove("raw");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!thereIsEnterpriseSS)
						finalHeader.remove("enterpriseSS");
					else {
						empFirstCell++;
						tgssCell++;
					}
					if (!thereIsTotalCost)
						finalHeader.remove("totalCost");
					else {
						empFirstCell++;
						tgssCell++;
					}
					
					if (!thereIsEmployeeSS)
						finalHeader.remove("employeeSS");
					else
						tgssCell++;
					if (!thereIsIrpf)
						finalHeader.remove("irpf");
					else
						tgssCell++;
					if (!thereIsOther)
						finalHeader.remove("other");
					else
						tgssCell++;
					if (!thereIsLiquid)
						finalHeader.remove("liquid");
					else
						tgssCell++;
					
					if (!thereIsTotalSS)
						finalHeader.remove("totalSS");
					
					entQuoteFirstCell= tgssCell+2;
					empQuoteFirstCell = entQuoteFirstCell+1;
					if (excelType == ExcelType.COMPLETE) {
						if (!thereIsCgcEnterprise)
						finalHeader.remove("cgcEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsCgpEnterprise)
							finalHeader.remove("cgpEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsUnemploymentEnterprise)
							finalHeader.remove("unemploymentEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsJobTrainingEnterprise)
							finalHeader.remove("jobTrainingEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsFogasaEnterprise)
							finalHeader.remove("fogasaEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsExtraHEnterprise)
							finalHeader.remove("extraHEnterprise");
						else
							empQuoteFirstCell++;
						if (!thereIsBonuses)
							finalHeader.remove("bonuses");
						else
							empQuoteFirstCell++;
						
						
						if (!thereIsCgcBase)
							finalHeader.remove("cgcBase");
						
						if (!thereIsIrpfBase)
							finalHeader.remove("irpfBase");
						
						
						
						if (!thereIsCgc)
							finalHeader.remove("cgc");
	
						if (!thereIsCgp)
							finalHeader.remove("cgp");
	
						if (!thereIsUnemployment)
							finalHeader.remove("unemployment");
	
						if (!thereIsJobTraining)
							finalHeader.remove("jobTraining");
	
						if (!thereIsAdvancedPayment)
							finalHeader.remove("advancedPayments");
	
						if (!thereIsOtherDeductions)
							finalHeader.remove("otherDeductions");
						if (!thereIsExtraH)
							finalHeader.remove("extraH");
						
						
						if (!thereIsEmbargos)
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
						else if (key.equals("bonuses"))
							bonusColumn = CellReference.convertNumToColString(c);
						else if (key.equals("employeeSS"))
							employeeSSColumn = CellReference.convertNumToColString(c);
						else if (key.equals("otherDeductions"))
							otherDecutionsColumn = CellReference.convertNumToColString(c); 
						else if (key.equals("advancedPayments"))
							advancedPaymentsColumn= CellReference.convertNumToColString(c);

						cell.setCellValue(cellValue);
						if (AonStringUtils.containsIgnoreCase(key, "joint")) {
							cell.setCellStyle(jointCellStyle);
							joints.add(c);
						}
						else if (key.equals("totalCost") || key.equals("liquid") || key.equals("totalSS")) {
							importantCells.add(c);
							cell.setCellStyle(headerCellStyle);
						}
						else
							cell.setCellStyle(headerCellStyle);
						
						c++;
					}

				}


				for (int i = 0, column = 0; i < arr.length; i++) {

					IEnterprisePayroll payroll = (IEnterprisePayroll) arr[i];
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					//EMPLOYEE NAME
					{
						Cell cell = row.createCell(column++);
						cell.setCellValue(payroll.getEmployee());
						cell.setCellStyle(stringCellStyle);
					}
					//SALARY TYPE
					{
						Cell cell = row.createCell(column++);
						if (payroll.getSalaryType() != null)
							switch (payroll.getSalaryType().ordinal()) {
								case 0:
									cell.setCellValue("NÓMINA");
									break;
								case 1:
									cell.setCellValue("EXTRA");
									break;
								case 2:
									cell.setCellValue("FINIQUITO");
									break;
								case 3:
									cell.setCellValue("ATRASOS");
									break;
								default:
									cell.setCellValue("NÓMINA");
							}
						
						cell.setCellStyle(doubleCellStyle);
					}
					
					CellStyle style = doubleCellStyle;
					if (thereIsRaw) {
						createDoubleCell(row, column++, payroll.getRaw(), doubleCellStyle);
					}
					
					if (thereIsEnterpriseSS) {
						Cell fCell = row.createCell(column++, CellType.FORMULA);
						String formula = ""+payroll.getEnterpriseSS()!=null?""+payroll.getEnterpriseSS():"0";
						if (thereIsBonuses && excelType == ExcelType.COMPLETE)
							formula+="+"+bonusColumn+(row.getRowNum()+1);
						else if (thereIsBonuses && excelType == ExcelType.SUMMARY)
							formula+="-"+payroll.getBonuses();
						fCell.setCellFormula(formula);
						if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getEnterpriseSS() == null || payroll.getEnterpriseSS() == 0d)) {
							fCell.setCellStyle(noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							fCell.setCellStyle(diffCellStyle);
						} else
							fCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsTotalCost) {
						int rowNum = row.getRowNum() + 1;

						Cell totalCostCell = row.createCell(column++);
						totalCostCell.setCellType(CellType.FORMULA);
						if (!thereIsRaw)
							totalCostCell.setCellFormula(enterpriseSSColumn + rowNum);
						else if (!thereIsEnterpriseSS)
							totalCostCell.setCellFormula(rawColumn + rowNum);
						else
							totalCostCell.setCellFormula(rawColumn + rowNum + "+" + enterpriseSSColumn + rowNum);
						
						totalCostCell.setCellStyle(importantCellStyle);
					}
					
					//JOINT
					{
						Cell jointCell = row.createCell(column++, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
					}
					
					if (thereIsEmployeeSS) {
						if (column == entFirstCell - 1)
							style = boundCellStylePrev;
						if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getEmployeeSS() == null || payroll.getEmployeeSS() == 0d)) {
							createDoubleCell(row, column++, payroll.getEmployeeSS(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getEmployeeSS(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getEmployeeSS(), style);
					}
					
					if (thereIsIrpf) {
						if (column == entFirstCell - 1)
							style = boundCellStylePrev;
						createDoubleCell(row, column++, payroll.getIrpf(), style);
					}
					
					if (thereIsOther) {
						int rowNum = row.getRowNum() + 1;
						Cell otherCell = row.createCell(column++, CellType.FORMULA);
						String formula = "";
						if (excelType == ExcelType.COMPLETE)
							formula = (advancedPaymentsColumn!=null?advancedPaymentsColumn+rowNum+"+":"")
								+(otherDecutionsColumn!=null?otherDecutionsColumn+rowNum:"0");
						else if (excelType == ExcelType.SUMMARY)
							formula = (payroll.getAdvancedPayment()!=null?payroll.getAdvancedPayment()+"+":"")
							+(payroll.getOtherDeductions()!=null?payroll.getOtherDeductions():"0");
						otherCell.setCellFormula(formula);
						otherCell.setCellStyle(doubleCellStyle);
					}
					
					if (thereIsLiquid) {
						if (column == entFirstCell - 1)
							style = boundCellStylePrev;
						createDoubleCell(row, column++, payroll.getLiquid(), importantCellStyle);
					}
					
					//JOINT
					{
						Cell jointCell = row.createCell(column++, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
					}
					
					if (thereIsTotalSS) {

						int rowNum = row.getRowNum() + 1;
						Cell totalCostCell = row.createCell(column++, CellType.FORMULA);
						String formula = "";
						if (thereIsEnterpriseSS && !thereIsEmployeeSS)
							formula += enterpriseSSColumn + rowNum;
						else if (!thereIsEnterpriseSS && thereIsEmployeeSS)
							formula += employeeSSColumn + rowNum;
						else if (thereIsEnterpriseSS && thereIsEmployeeSS)
							formula += employeeSSColumn + rowNum + "+" + enterpriseSSColumn + rowNum;
						
						totalCostCell.setCellFormula(formula);
						
						totalCostCell.setCellStyle(importantCellStyle);
					}
					if (excelType == ExcelType.COMPLETE) {
						//JOINT
						{
							Cell jointCell = row.createCell(column++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}
						
						if (thereIsCgcEnterprise) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getCgcEnterprise() == null || payroll.getCgcEnterprise() == 0d)) {
								createDoubleCell(row, column++, payroll.getCgcEnterprise(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getCgcEnterprise(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getCgcEnterprise(), doubleCellStyle);
						}

						if (thereIsCgpEnterprise) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getCgpEnterprise() == null || payroll.getCgpEnterprise() == 0d)) {
								createDoubleCell(row, column++, payroll.getCgpEnterprise(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getCgpEnterprise(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getCgpEnterprise(), doubleCellStyle);
						}

						if (thereIsUnemploymentEnterprise) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getUnemploymentEnterprise() == null
											|| payroll.getUnemploymentEnterprise() == 0d)) {
								createDoubleCell(row, column++, payroll.getUnemploymentEnterprise(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getUnemploymentEnterprise(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getUnemploymentEnterprise(), doubleCellStyle);
						}

						if (thereIsJobTrainingEnterprise) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getJobTrainingEnterprise() == null
											|| payroll.getJobTrainingEnterprise() == 0d)) {
								createDoubleCell(row, column++, payroll.getJobTrainingEnterprise(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getJobTrainingEnterprise(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getJobTrainingEnterprise(), doubleCellStyle);
						}

						if (thereIsFogasaEnterprise) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getFogasaEnterprise() == null || payroll.getFogasaEnterprise() == 0d)) {
								createDoubleCell(row, column++, payroll.getFogasaEnterprise(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getFogasaEnterprise(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getFogasaEnterprise(), doubleCellStyle);
						}
						
						if (thereIsExtraH) {
							Double amount = null;
							if (payroll.getEstrucEnterprise() == null && payroll.getNoEstructEnterprise() == null)
								amount = (payroll.getEstrucEnterprise()!=null?payroll.getEstrucEnterprise():0d)
										+
										(payroll.getNoEstructEnterprise()!=null?payroll.getNoEstructEnterprise():0d); 
								
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (amount == null || amount == 0d)) {
								createDoubleCell(row, column++, amount, noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, amount, diffCellStyle);
							} else
								createDoubleCell(row, column++, amount, doubleCellStyle);
						}

						if (thereIsBonuses) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getBonuses() == null || payroll.getBonuses() == 0d)) {
								createDoubleCell(row, column++, -payroll.getBonuses(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, -payroll.getBonuses(), diffCellStyle);
							} else
								createDoubleCell(row, column++, -payroll.getBonuses(), doubleCellStyle);
						}
						
						{
							Cell jointCell = row.createCell(column++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}
						
						if (thereIsCgc) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getCgc() == null || payroll.getCgc() == 0d)) {
								createDoubleCell(row, column++, payroll.getCgc(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, payroll.getCgc(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getCgc(), style);
						}
						
						if (thereIsCgp) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getCgp() == null || payroll.getCgp() == 0d)) {
								createDoubleCell(row, column++, payroll.getCgp(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, payroll.getCgp(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getCgp(), style);
						}

						if (thereIsUnemployment) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getUnemployment() == null || payroll.getUnemployment() == 0d)) {
								createDoubleCell(row, column++, payroll.getUnemployment(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, payroll.getUnemployment(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getUnemployment(), style);
						}

						if (thereIsJobTraining) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getJobTraining() == null || payroll.getJobTraining() == 0d)) {
								createDoubleCell(row, column++, payroll.getJobTraining(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, payroll.getJobTraining(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getJobTraining(), style);
						}
						
						if (thereIsExtraH) {
							Double amount = null;
							if (!(payroll.getEstruc() == null && payroll.getNoEstruct()==null))
								amount = (payroll.getEstruc()!=null?payroll.getEstruc():0d) + (payroll.getNoEstruct()!=null?payroll.getNoEstruct():0d);
							
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (amount == null || amount == 0d)) {
								createDoubleCell(row, column++, amount, noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, amount, diffCellStyle);
							} else
								createDoubleCell(row, column++, amount, style);
						}

						if (thereIsAdvancedPayment) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getAdvancedPayment() == null || payroll.getAdvancedPayment() == 0d)) {
								createDoubleCell(row, column++, payroll.getAdvancedPayment(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getAdvancedPayment(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getAdvancedPayment(), style);
						}
						
						if (thereIsEmbargos) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getEmbargos() == null || payroll.getEmbargos() == 0d)) {
								createDoubleCell(row, column++, payroll.getEmbargos(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getEmbargos(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getEmbargos(), style);
						}
						
						if (thereIsOtherDeductions) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")
									&& (payroll.getOtherDeductions() == null || payroll.getOtherDeductions() == 0d)) {
								createDoubleCell(row, column++, payroll.getOtherDeductions(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg.social")) {
								createDoubleCell(row, column++, payroll.getOtherDeductions(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getOtherDeductions(), style);
						}
						
						//JOINT
						{
							Cell jointCell = row.createCell(column++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}
						
						if (thereIsCgcBase) {
							if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")
									&& (payroll.getCgcBase() == null || payroll.getCgcBase() == 0d)) {
								createDoubleCell(row, column++, payroll.getCgcBase(), noDiffCellStyle);
							} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
								createDoubleCell(row, column++, payroll.getCgcBase(), diffCellStyle);
							} else
								createDoubleCell(row, column++, payroll.getCgcBase(), style);
						}

						if (thereIsIrpfBase) {
							createDoubleCell(row, column++, payroll.getIrpfBase(), style);
						}
					}
					

					column = 0;
				}

				Integer lastCell = row.getLastCellNum() - 1;

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCell));
				row = sheet.createRow(0);
				Cell enterpriseCell = row.createCell(0);
				enterpriseCell.setCellType(CellType.STRING);
				enterpriseCell.setCellStyle(headerCellStyle);
				enterpriseCell.setCellValue(enterpriseName + " - " + dateString);
				//WORKPLACE'S 2ND HEADER ROW 
				{
					row = sheet.createRow(1);
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, entFirstCell-1));
					sheet.addMergedRegion(new CellRangeAddress(1, 1, entFirstCell, empFirstCell - 2));
					sheet.addMergedRegion(new CellRangeAddress(1, 1, empFirstCell, tgssCell-2));
					
					
					Cell entCell = row.createCell(2, CellType.STRING);
					entCell.setCellStyle(headerCellStyle);
					entCell.setCellValue("EMPRESA");
					
					Cell jointCell = row.createCell(empFirstCell-1, CellType.STRING);
					jointCell.setCellStyle(jointCellStyle);
					
					Cell epCell = row.createCell(empFirstCell, CellType.STRING);
					epCell.setCellStyle(headerCellStyle);
					epCell.setCellValue("EMPLEADO");
					
					jointCell = row.createCell(tgssCell-1, CellType.STRING);
					jointCell.setCellStyle(jointCellStyle);
					
					if (thereIsTotalSS) {
						Cell tgCell = row.createCell(tgssCell, CellType.STRING);
						tgCell.setCellStyle(headerCellStyle);
						tgCell .setCellValue("TGSS");
					}
					
					if (excelType == ExcelType.COMPLETE) {
						jointCell = row.createCell(empFirstCell-1, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
						
						if(empQuoteFirstCell-2 > entQuoteFirstCell) {
							sheet.addMergedRegion(new CellRangeAddress(1, 1, entQuoteFirstCell, empQuoteFirstCell-2));
							Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
							entQuoteCell.setCellStyle(headerCellStyle);
							entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
						} else if (empQuoteFirstCell-2 == entQuoteFirstCell) {
							Cell entQuoteCell = row.createCell(entQuoteFirstCell, CellType.STRING);
							entQuoteCell.setCellStyle(headerCellStyle);
							entQuoteCell.setCellValue("COTIZACIÓN EMPRESA");
						}
						
						jointCell = row.createCell(empQuoteFirstCell-1, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
						
						sheet.addMergedRegion(new CellRangeAddress(1, 1, empQuoteFirstCell, lastCell));
						
						Cell empQuoteCell = row.createCell(empQuoteFirstCell, CellType.STRING);
						empQuoteCell.setCellStyle(headerCellStyle);
						empQuoteCell.setCellValue("COTIZACIÓN EMPLEADO");
					}
					

					

				}
				//WORKPLACE'S TOTALS
				{
					int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
					row = sheet.createRow(sheet.getLastRowNum() + 1);

					Cell totalCell = row.createCell(1);
					totalCell.setCellType(CellType.STRING);
					totalCell.setCellValue("TOTALES:");
					totalCell.setCellStyle(headerCellStyle);

					for (int i = 2; i < lastColumn; i++) {
						
						CellStyle style = formulaCellStyle;
						
						
							
						
						Cell cell = row.createCell(i);
						if (joints.contains(i))
							style = jointCellStyle;
						else if (importantCells.contains(i)) {
							style = importantTotalCellStyle;
							cell.setCellFormula("sum(" + CellReference.convertNumToColString(i) + 1 + ":"
									+ CellReference.convertNumToColString(i) + row.getRowNum() + ")");
							cell.setCellType(CellType.FORMULA);
						} else {
							cell.setCellFormula("sum(" + CellReference.convertNumToColString(i) + 1 + ":"
									+ CellReference.convertNumToColString(i) + row.getRowNum() + ")");
							cell.setCellType(CellType.FORMULA);
						}
							
						cell.setCellStyle(style);
					}
				}

				// TOTALS (IN THE FIRST SHEET)
				if (totals != null) {
					row = totals.createRow(totals.getLastRowNum() + 1);

					Cell tCell = row.createCell(0);
					tCell.setCellValue(workplace);
					tCell.setCellStyle(stringCellStyle);

					int column = 2;
					int last = sheet.getLastRowNum();
					int cell = 2;
					
					Cell jCell = row.createCell(1, CellType.STRING);
					jCell.setCellStyle(jointCellStyle);
					
					if (thereIsRaw) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "2:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(importantCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(importantCellStyle);
					}
					
					if (thereIsEnterpriseSS) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsTotalCost) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(importantCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(importantCellStyle);
					}
					
					//JOINT
					{
						column++;
						Cell jointCell = row.createCell(cell++, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
					}
					
					if (thereIsEmployeeSS) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}
					
					if (thereIsIrpf) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}
					
					if (thereIsOther) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}
					
					if (thereIsLiquid) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(boundCellStylePrev);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(boundCellStylePrev);
					}
					
					//JOINT
					{
						column++;
						Cell jointCell = row.createCell(cell++, CellType.STRING);
						jointCell.setCellStyle(jointCellStyle);
					}
					
					if (thereIsTotalSS) {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
						tCell.setCellStyle(importantCellStyle);
						column++;
					} else {
						tCell = row.createCell(cell++);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(importantCellStyle);
					}
					
					if (excelType == ExcelType.COMPLETE) {
						//JOINT
						{
							column++;
							Cell jointCell = row.createCell(cell++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}
						
						if (thereIsCgcEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsCgpEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsUnemploymentEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsJobTrainingEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsFogasaEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						if (thereIsExtraHEnterprise) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						if (thereIsBonuses) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						//JOINT
						{
							column++;
							Cell jointCell = row.createCell(cell++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}

						if (thereIsCgc) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						if (thereIsCgp) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsUnemployment) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsJobTraining) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						if (thereIsExtraH) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsAdvancedPayment) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						if (thereIsEmbargos) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsOtherDeductions) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}
						
						//JOINT
						{
							column++;
							Cell jointCell = row.createCell(cell++, CellType.STRING);
							jointCell.setCellStyle(jointCellStyle);
						}
						
						if (thereIsCgcBase) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							tCell.setCellStyle(doubleCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellStyle(doubleCellStyle);
						}

						if (thereIsIrpfBase) {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							tCell.setCellFormula("sum('" + workplace + "'!" + CellReference.convertNumToColString(column) + "1:" + CellReference.convertNumToColString(column) + last + ")");
							CellStyle finalCellStyle = wb.createCellStyle();
							finalCellStyle.setBorderRight(BorderStyle.THIN);
							finalCellStyle.setBorderBottom(BorderStyle.THIN);
							tCell.setCellStyle(finalCellStyle);
							column++;
						} else {
							tCell = row.createCell(cell++);
							tCell.setCellType(CellType.FORMULA);
							CellStyle finalCellStyle = wb.createCellStyle();
							finalCellStyle.setBorderRight(BorderStyle.THIN);
							finalCellStyle.setBorderBottom(BorderStyle.THIN);
							tCell.setCellStyle(finalCellStyle);
						}
					}					

				}

				for (int i = 0; i < finalHeader.size(); i++) {
					sheet.autoSizeColumn(i);
				}
				
				//TOTALS' FINAL BORDER
				if (totals != null)
					for (int i = 0; i<=totals.getLastRowNum(); i++) {
						Row r = totals.getRow(i);
						int lCell = (excelType == ExcelType.COMPLETE) ? 32 : 12;
						Cell borderCell = r.createCell(lCell);
						borderCell.setCellStyle(wb.createCellStyle());
						borderCell.getCellStyle().setBorderLeft(BorderStyle.THIN);
					}
				//WORKPLACE'S FINAL BORDER
				for (int i = 0; i<=sheet.getLastRowNum(); i++) {
					Row r = sheet.getRow(i);
					Cell borderCell = r.createCell(finalHeader.size());
					borderCell.setCellStyle(wb.createCellStyle());
					borderCell.getCellStyle().setBorderLeft(BorderStyle.THIN);
				}
				
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
		}

		if (totals != null) {
			if (excelType == ExcelType.COMPLETE)
				for (int i = 0; i <= 31; i++) {
					totals.autoSizeColumn(i);
				}
			else if (excelType == ExcelType.SUMMARY)
				for (int i = 0; i <= 11; i++) {
					totals.autoSizeColumn(i);
				}
		}

		wb.write(outputStream);
		outputStream.close();
		wb.close();
	}



	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId) throws IOException {

		Condition condition = SALARY.ISSUE_DATE.ge(new java.sql.Date(startDate.getTime())).and(SALARY.ISSUE_DATE.le(new java.sql.Date(endDate.getTime())))
				.and(ENTERPRISE.REGISTRY.eq(enterpriseId));
		if (workplaceId != null)
			condition.and(WORKPLACE.ID.eq(workplaceId));

		Map<Integer, String> workplaces = aonContext.getDslContext().select(SALARY.ID, WORKPLACE.DESCRIPTION)
				.from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey()
				.where(condition).fetchStream().collect(HashMap::new,
						(m, v) -> m.put(v.get(SALARY.ID), v.get(WORKPLACE.DESCRIPTION)), HashMap::putAll);

		Collection<Integer> ids = aonContext.getDslContext().select(SALARY.ID, WORKPLACE.DESCRIPTION).from(SALARY)
				.innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey().where(condition)
				.fetchStreamInto(SALARY).map(SalaryRecord::getId).collect(Collectors.toList());

		Stream<Salary> salaries = AON.getSalaries(aonContext,
				s -> s.getIdProperty().in(ids.toArray(new Integer[ids.size()])));
		return salaries.map(s -> {

			EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
			enterprisePayroll.employee = s.getEmployeeName();
			enterprisePayroll.workplace = workplaces.get(s.getId());
			
			enterprisePayroll.salaryType = s.getSalaryType();


			enterprisePayroll.irpf = s.getTotalIrpf();

			enterprisePayroll.cgcBase = s.getCommonContingenciesBase();
			enterprisePayroll.irpfBase = s.getIrpfBase();

			enterprisePayroll.raw = s.getTotalPayment();
			enterprisePayroll.liquid = s.getTotalLiquid();
			
			Double dedBonus = s.getDeductions().stream()
					.filter(d -> d.getDeductionType() == null && d.getAmount() < 0)
					.mapToDouble(d -> d.getAmount()).sum();
					
			
			enterprisePayroll.employeeSS = s.getTotalSSContributions();
			
			if (s.getTotalSSContributions() != null && dedBonus != null)
				enterprisePayroll.employeeSS += dedBonus;
			else if (dedBonus != null)
				enterprisePayroll.employeeSS = dedBonus;
			
			
			enterprisePayroll.enterpriseSS = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();

			enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
			enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS + enterprisePayroll.irpf
					+ enterprisePayroll.raw;
			
			

			enterprisePayroll.bonuses = s.getBonuses().stream().mapToDouble(Bonus::getAmount).sum();
			// PICKING UP DEDUCTIONS
			Double cgc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double cgp = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.IT.ordinal()
							|| d.getDeductionType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double unemployment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double jobTraining = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double advancedPayment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.ADVANCE_PAYMENT.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double otherDeductions = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.OTHER.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double estruc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double noEstruct = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(d -> d.getAmount()).sum();
			Double embargos = s.getEmbargos().stream()
					.mapToDouble(Embargo::getAmount).sum();
			// PICKING UP COSTS
			Double cgcEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double cgpEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.IT.ordinal()
					|| c.getCostType().ordinal() == DeductionType.IMS.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			
			Double unemploymentEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double jobTrainingEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double fogasaEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double estrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();
			Double noEstrucEnterprise = s.getCosts().stream()
					.filter(c -> c.getCostType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
					.mapToDouble(c -> c.getAmount()).sum();

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
	
	
	@Deprecated
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {

//		EnterprisePayroll pruebaSegSocial = new EnterprisePayroll();
//		pruebaSegSocial.workplace = "PRINCIPAL - SEG. SOCIAL";
//		pruebaSegSocial.employee = "DAVID CASTAÑO, SANCHEZ";
//		pruebaSegSocial.employeeSS = 3d;
//		pruebaSegSocial.enterpriseSS = 0d;

		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition)
				.fetchStreamInto(SALARY_BONUS)
				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));


		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_DEDUCTION)
				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT),
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<String, Double> map = new HashMap<String, Double>();
						map.put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});

		/* Stream<EnterprisePayroll> ret = */return ctx.select().from(SALARY).innerJoin(CONTRACT).onKey()
				.innerJoin(WORKPLACE).onKey().where(condition).fetchStream().map(record -> {
					EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
					enterprisePayroll.employee = record.get(SALARY.EMPLOYEE_NAME);
					enterprisePayroll.workplace = record.get(WORKPLACE.DESCRIPTION);

					enterprisePayroll.irpf = record.get(SALARY.TOTAL_IRPF);

					enterprisePayroll.cgcBase = record.get(SALARY.CGC_BASE);
					enterprisePayroll.irpfBase = record.get(SALARY.IRPF_BASE);

					enterprisePayroll.raw = record.get(SALARY.TOTAL_PAYMENT);
					enterprisePayroll.liquid = record.get(SALARY.TOTAL_LIQUID);
					enterprisePayroll.employeeSS = record.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
//					enterprisePayroll.enterpriseSS = record.get(SALARY.TOTAL_ENTERPRISE);

					enterprisePayroll.enterpriseSS = ctx.select().from(SALARY_COST)
							.where(SALARY_COST.SALARY.eq(record.get(SALARY.ID))).fetchStreamInto(SALARY_COST)
							.mapToDouble(SalaryCostRecord::getAmount).sum();

					enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
					enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS + enterprisePayroll.raw;

					enterprisePayroll.bonuses = bonusesMap.get(record.get(SALARY.ID));

					Map<String, Double> map = deductions.get(record.get(SALARY.ID));
					if (map != null) {

						enterprisePayroll.cgc = map.get("CGC");
						enterprisePayroll.unemployment = map.get("DESMPL");
						enterprisePayroll.jobTraining = map.get("FP");
					}

					return enterprisePayroll;
				});
//		return Stream.concat(ret, Stream.of(pruebaSegSocial));
	}

	private static void createDoubleCell(Row row, int column, Double value, CellStyle doubleCellStyle) {
		Cell cell = row.createCell(column);
		if (value != null)
			cell.setCellValue(value);
		cell.setCellStyle(doubleCellStyle);
		cell.setCellType(CellType.NUMERIC);
	}

	private static void createDiffCell(Sheet sheet, Row row, int column, CellStyle ssCellStyle) {
		Cell cell = row.createCell(column);
		cell.setCellType(CellType.FORMULA);
		cell.setCellFormula(CellReference.convertNumToColString(column - 2) + (sheet.getLastRowNum() + 1) + "-"
				+ CellReference.convertNumToColString(column - 1) + (sheet.getLastRowNum() + 1));
		cell.setCellStyle(ssCellStyle);
	}

	public static String getEnterpriseName(String domainName, Integer enterpriseId, Integer workplaceId) {

		try (AONContext aonContext = AONContext.getAONContext(domainName, "")) {
			AtomicInteger eId = new AtomicInteger(enterpriseId);
			if (enterpriseId == null || enterpriseId == 0)
				eId.set(AON.getWorkplace(aonContext.getDomainName(), aonContext.getDomainId(), "",
						w -> w.getIdProperty().eq(workplaceId)).getEnterprise());
			return AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					r -> r.getIdProperty().eq(eId.get())).getName();
		}
	}
	
	public static class EnterprisePayroll implements IEnterprisePayroll {
		private String employee;
		private String workplace;
		private SalaryType salaryType;

		private Double raw;
		private Double employeeSS;
		private Double irpf;
		private Double liquid;
		private Double enterpriseSS;
		private Double totalCost;
		private Double totalSS;
		private Double bonuses;

		private Double cgcBase;
		private Double irpfBase;

		private Double cgc;
		private Double cgp;
		private Double unemployment;
		private Double jobTraining;
		private Double advancedPayment;
		private Double otherDeductions;
		private Double estruc;
		private Double noEstruct;
		private Double embargos;

		private Double cgcEnterprise;
		private Double cgpEnterprise;
		private Double unemploymentEnterprise;
		private Double jobTrainingEnterprise;
		private Double fogasaEnterprise;
		private Double estrucEnterprise;
		private Double noEstructEnterprise;

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

	}
	
	
	private static String getDateString (Integer month, Integer year) {
		if (month == null || year == null)
			return "";
		else {
			switch (month) {
			case 1:
				return "Enero de "+year;
			case 2:
				return "Febrero de "+year;
			case 3:
				return "Marzo de "+year;
			case 4:
				return "Abril de "+year;
			case 5:
				return "Mayo de "+year;
			case 6:
				return "Junio de "+year;
			case 7:
				return "Julio de "+year;
			case 8:
				return "Agosto de "+year;
			case 9:
				return "Septiembre de "+year;
			case 10:
				return "Octubre de "+year;
			case 11:
				return "Noviembre de "+year;
			case 12:
				return "Diciembre de "+year;
			default:
				return "";
			}
		}
	}

	
}
