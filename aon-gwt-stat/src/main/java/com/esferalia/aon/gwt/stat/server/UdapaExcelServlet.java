package com.esferalia.aon.gwt.stat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.stat.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.stat.DirectSalesChartTypeVisitor;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Udapa Stat Report (excel)", urlPatterns = { "/aon_gwt_stat/ms/UdapaStatExcel",
																"/aon_gwt_aio/ms/UdapaStatExcel" })
public class UdapaExcelServlet extends HttpServlet {
	
	private static final long serialVersionUID = -4825807448151446413L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN));
			String statParams = req.getParameter(IRequestParamsNames.STAT_PARAMS);
			StatParams params = StatParamsUtils.get(statParams);
			Map<String,UdapaStatRow> map = getMap(domainName,domainId,user,params);
			UdapaExcelAction excelAction = new UdapaExcelAction(params,map);
			excelAction.run(resp);
			resp.flushBuffer();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	@FunctionalInterface
	private interface IFiller {
		void fill (UdapaStatRow row, double value);
	}
	private Map<String, UdapaStatRow> getMap(String domainName, int domainId, String user, StatParams params) {
		final Map<String, UdapaStatRow> map = new LinkedHashMap<String, UdapaStatRow>();
		
		// DATOS DEL MES SELECCIONADO
		StatParams periodParams = StatParamsUtils.getPeriodParams(params);
		StatData<String, String, Double> periodData = AON.getStatData(domainName,domainId,user,periodParams);
		fill(map, periodData, (row, value) -> row.setMonth(value));
		
		// DATOS DEL MES ANTERIOR
		StatParams previousMonthParams = StatParamsUtils.getPreviousMonthParams(periodParams);
		StatData<String, String, Double> previousMonthData = AON.getStatData(domainName,domainId,user, previousMonthParams);
		fill(map, previousMonthData, (row, value) -> row.setPrevMonth(value));
		
		// DATOS DEL MES DEL AÑO ANTERIOR
		StatParams periodPreviousYearParams = StatParamsUtils.getPeriodPreviousYearParams(periodParams);
		StatData<String, String, Double> periodPreviousYearData = AON.getStatData(domainName,domainId,user, periodPreviousYearParams);
		fill(map, periodPreviousYearData, (row, value) -> row.setLastYearMonth(value));
		
		// DATOS DEL AÑO ANTERIOR COMPLETO
		StatParams previousYearParams = StatParamsUtils.getPreviousYearParams(periodParams);
		StatData<String, String, Double> previousYearData = AON.getStatData(domainName,domainId,user, previousYearParams);
		fill(map, previousYearData, (row, value) -> row.setLastYear(value));

		// DATOS DEL AÑO ACTUAL, DESDE 1 DE ENERO HASTA FIN PERIODO
		StatParams currentYearParams = StatParamsUtils.getCurrentYearParams(periodParams);
		StatData<String, String, Double> currentYearData = AON.getStatData(domainName,domainId,user, currentYearParams);
		fill(map, currentYearData, (row, value) -> row.setCurrentYear(value));
		return map;
	}

	private void fill(Map<String, UdapaStatRow> map, StatData<String, String, Double> data, IFiller filler) {
		for (String key : data.getMap().keySet()) {
			if (data.getMap().get( key ).containsKey(DirectSalesChartTypeVisitor.AMOUNT_LABEL)) {
				if (!map.containsKey(key)) {
					map.put(key, new UdapaStatRow() );
				}
				UdapaStatRow row = map.get(key);
				filler.fill(row, data.getMap().get(key).get(DirectSalesChartTypeVisitor.AMOUNT_LABEL));
			}
		}
	}
	
	private static class UdapaExcelAction  {
		
		private static final SimpleDateFormat MONTH_FORMATTER =  new SimpleDateFormat("MM/yyyy");
		private static final SimpleDateFormat YEAR_FORMATTER = new SimpleDateFormat("yyyy");
		private static final String DECIMAL_PATTERN = "#,##0.00";
		private static final String PERCENT_PATTERN = "##0.0000%";
		private static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,0,0));
		
		private Map<String, UdapaStatRow> map;
		private StatParams params;
		
		private XSSFWorkbook workbook;
		private XSSFSheet sheet;
		private DataFormat dataFormat;	
		private XSSFCellStyle firstColStyle;
		private XSSFCellStyle headerCellStyle;
		private XSSFCellStyle currentDecimalStyle;
		private XSSFCellStyle previousMonthDecimalStyle;
		private XSSFCellStyle previousYearDecimalStyle;
		private XSSFCellStyle currentPercentStyle;
		private XSSFCellStyle previousMonthPercentStyle;
		private XSSFCellStyle previousYearPercentStyle;
		private XSSFCellStyle totalCellStyle;
		private Font boldFont;
		
		public UdapaExcelAction(StatParams params, Map<String, UdapaStatRow> map) {
			this.params = params;
			this.map = map;
		}

		private void finalize(OutputStream out) throws IOException {
			workbook.write(out);
		}

		private void initialize(String name) {
			workbook = new XSSFWorkbook();
		    sheet = (XSSFSheet) workbook.createSheet(name);
		    dataFormat = workbook.getCreationHelper().createDataFormat();
		    
		    XSSFColor currentColor = new XSSFColor(new java.awt.Color(255, 255, 153));
		    XSSFColor previousYearColor = new XSSFColor(new java.awt.Color(220, 220, 220));
		    XSSFColor previousMonthColor = new XSSFColor(new java.awt.Color(204, 255, 204));
		    
		    boldFont= workbook.createFont();
			boldFont.setFontHeightInPoints((short) 9);
			boldFont.setBold(true);

			Font headerFont= workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor( IndexedColors.WHITE.index );

			headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
			headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerCellStyle.setFillForegroundColor(AON_BLUE);
		    headerCellStyle.setFont(headerFont);

		    firstColStyle = workbook.createCellStyle();
		    firstColStyle.setBorderTop(BorderStyle.THIN);
		    firstColStyle.setBorderLeft(BorderStyle.THIN);
		    firstColStyle.setBorderRight(BorderStyle.THIN);
		    firstColStyle.setBorderBottom(BorderStyle.THIN);
		    firstColStyle.setFont(boldFont);
		    
		    currentDecimalStyle = workbook.createCellStyle();
		    currentDecimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    currentDecimalStyle.setAlignment( HorizontalAlignment.RIGHT );
		    currentDecimalStyle.setBorderTop(BorderStyle.THIN);
		    currentDecimalStyle.setBorderLeft(BorderStyle.THIN);
		    currentDecimalStyle.setBorderRight(BorderStyle.THIN);
		    currentDecimalStyle.setBorderBottom(BorderStyle.THIN);
		    currentDecimalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    currentDecimalStyle.setFillForegroundColor(currentColor);
		    
		    previousYearDecimalStyle = workbook.createCellStyle();
		    previousYearDecimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    previousYearDecimalStyle.setAlignment( HorizontalAlignment.RIGHT );
		    previousYearDecimalStyle.setBorderTop(BorderStyle.THIN);
		    previousYearDecimalStyle.setBorderLeft(BorderStyle.THIN);
		    previousYearDecimalStyle.setBorderRight(BorderStyle.THIN);
		    previousYearDecimalStyle.setBorderBottom(BorderStyle.THIN);
		    previousYearDecimalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    previousYearDecimalStyle.setFillForegroundColor(previousYearColor);
		    
		    previousMonthDecimalStyle = workbook.createCellStyle();
		    previousMonthDecimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    previousMonthDecimalStyle.setAlignment( HorizontalAlignment.RIGHT );
		    previousMonthDecimalStyle.setBorderTop(BorderStyle.THIN);
		    previousMonthDecimalStyle.setBorderLeft(BorderStyle.THIN);
		    previousMonthDecimalStyle.setBorderRight(BorderStyle.THIN);
		    previousMonthDecimalStyle.setBorderBottom(BorderStyle.THIN);
		    previousMonthDecimalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    previousMonthDecimalStyle.setFillForegroundColor(previousMonthColor);

		    currentPercentStyle = workbook.createCellStyle();
		    currentPercentStyle.setDataFormat(dataFormat.getFormat(PERCENT_PATTERN));
		    currentPercentStyle.setAlignment( HorizontalAlignment.RIGHT );
		    currentPercentStyle.setBorderTop(BorderStyle.THIN);
		    currentPercentStyle.setBorderLeft(BorderStyle.THIN);
		    currentPercentStyle.setBorderRight(BorderStyle.THIN);
		    currentPercentStyle.setBorderBottom(BorderStyle.THIN);
		    currentPercentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    currentPercentStyle.setFillForegroundColor(currentColor);

		    previousYearPercentStyle = workbook.createCellStyle();
		    previousYearPercentStyle.setDataFormat(dataFormat.getFormat(PERCENT_PATTERN));
		    previousYearPercentStyle.setAlignment( HorizontalAlignment.RIGHT );
		    previousYearPercentStyle.setBorderTop(BorderStyle.THIN);
		    previousYearPercentStyle.setBorderLeft(BorderStyle.THIN);
		    previousYearPercentStyle.setBorderRight(BorderStyle.THIN);
		    previousYearPercentStyle.setBorderBottom(BorderStyle.THIN);
		    previousYearPercentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    previousYearPercentStyle.setFillForegroundColor(previousYearColor);

		    previousMonthPercentStyle = workbook.createCellStyle();
		    previousMonthPercentStyle.setDataFormat(dataFormat.getFormat(PERCENT_PATTERN));
		    previousMonthPercentStyle.setAlignment( HorizontalAlignment.RIGHT );
		    previousMonthPercentStyle.setBorderTop(BorderStyle.THIN);
		    previousMonthPercentStyle.setBorderLeft(BorderStyle.THIN);
		    previousMonthPercentStyle.setBorderRight(BorderStyle.THIN);
		    previousMonthPercentStyle.setBorderBottom(BorderStyle.THIN);
		    previousMonthPercentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    previousMonthPercentStyle.setFillForegroundColor(previousMonthColor);

		    totalCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			totalCellStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    totalCellStyle.setAlignment( HorizontalAlignment.RIGHT );
			totalCellStyle.setFont(boldFont);
			totalCellStyle.setBorderTop(BorderStyle.THIN);
			totalCellStyle.setBorderLeft(BorderStyle.THIN);
			totalCellStyle.setBorderRight(BorderStyle.THIN);
			totalCellStyle.setBorderBottom(BorderStyle.THIN);
			

			Row titleRow = sheet.createRow(0);
			InvoiceChartType chartType = InvoiceChartType.values()[params.getChartType()];
			CellUtil.createCell(titleRow, 0, "Datos gr\u00E1fico (" + chartType.getDescription() + ")" , headerCellStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

			Row headerRow = sheet.createRow(1);
			CellUtil.createCell(headerRow, 0, "", headerCellStyle);

			String previousMonth =  MONTH_FORMATTER.format( StatParamsUtils.getPreviousMonthParams(params).getFrom());
			String periodPreviousYearMonth =  MONTH_FORMATTER.format( StatParamsUtils.getPeriodPreviousYearParams(params).getFrom());
			String period =  MONTH_FORMATTER.format( StatParamsUtils.getPeriodParams(params).getFrom());
			String previousYear =  YEAR_FORMATTER.format( StatParamsUtils.getPreviousYearParams(params).getFrom());
			String currentYear =  YEAR_FORMATTER.format( StatParamsUtils.getCurrentYearParams(params).getFrom());
			
			CellUtil.createCell(headerRow, 1, previousMonth, headerCellStyle); // colspan 2
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
			CellUtil.createCell(headerRow, 3, periodPreviousYearMonth, headerCellStyle); // colspan 2
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
			CellUtil.createCell(headerRow, 5, period, headerCellStyle); // colspan 2
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
			CellUtil.createCell(headerRow, 7, ("%S/ " + previousMonth), headerCellStyle);
			CellUtil.createCell(headerRow, 8, ("%S/ " + periodPreviousYearMonth), headerCellStyle);
			CellUtil.createCell(headerRow, 9, previousYear, headerCellStyle); // colspan 2
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
			CellUtil.createCell(headerRow, 11, currentYear, headerCellStyle); // colspan 2
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
			CellUtil.createCell(headerRow, 13, ("%S/ " + currentYear), headerCellStyle);
			
			sheet.setColumnWidth( 0, 40*256);
			sheet.setColumnWidth( 1, 12*256);
			sheet.setColumnWidth( 2, 12*256);
			sheet.setColumnWidth( 3, 12*256);
			sheet.setColumnWidth( 4, 12*256);
			sheet.setColumnWidth( 5, 12*256);
			sheet.setColumnWidth( 6, 12*256);
			sheet.setColumnWidth( 7, 12*256);
			sheet.setColumnWidth( 8, 12*256);
			sheet.setColumnWidth( 9, 12*256);
			sheet.setColumnWidth(10, 12*256);
			sheet.setColumnWidth(11, 12*256);
			sheet.setColumnWidth(12, 12*256);
			sheet.setColumnWidth(13, 12*256);
			sheet.setColumnWidth(14, 12*256);
			
		}

		public void fillFormulas() {
			XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(workbook);
			Row totalRow = sheet.createRow(sheet.getLastRowNum() + 1);
			
			int[] colIdxs = {1,3,5,9,11};
			for ( int colIdx : colIdxs) {
				Cell cell = totalRow.createCell(colIdx);
				cell.setCellStyle(totalCellStyle);
				cell.setCellType(CellType.FORMULA);
				CellReference ref = new CellReference(cell);
				String[] parts = ref.getCellRefParts(); // Returns the three parts of the cell reference, the Sheet
															// name (or null if none supplied),
															// the 1 based row number, and the A based column letter.
															// This will not include any markers
															// for absolute references, so use formatAsString() to
															// properly turn references into strings.
				String colId = parts[2];
				String totalRef = parts[2] + parts[1];
				String formula = "SUM(" + colId + "3:" + colId + sheet.getLastRowNum() + ")";
				cell.setCellFormula(formula);
				CellValue cellValue = evaluator.evaluate(cell);
				cell.setCellValue(cellValue.getNumberValue());
		
				for (int i = 2; i < sheet.getLastRowNum(); i++) {
					Row curRow = sheet.getRow(i);
					Cell percentCell = curRow.createCell(colIdx + 1);
					if (colIdx == 1) {
						percentCell.setCellStyle(previousMonthPercentStyle);	
					} else if (colIdx == 3) {
						percentCell.setCellStyle(previousYearPercentStyle);
					} else if (colIdx == 9) {
						percentCell.setCellStyle(previousYearPercentStyle);
					} else {
						percentCell.setCellStyle(currentPercentStyle);
					}
					percentCell.setCellType(CellType.FORMULA);
					String percentFormula = colId + (i + 1) + "/" + totalRef;
					percentCell.setCellFormula(percentFormula);
					CellValue percentValue = evaluator.evaluate(percentCell);
					percentCell.setCellValue(percentValue.getNumberValue());
				}
			}
			StatParams currentYearParams = StatParamsUtils.getCurrentYearParams(params);
			int currentMonth = AonDateUtils.getMonth(currentYearParams.getTo());
			String formula = "IF({1}=0,1,(({0}-{1}) / {1}))";
			String formula2 = "IF({1}=0,1,((({0}*12/({2,number,integer}+1))-{1}) / {1}))";
			for (int i = 2; i < sheet.getLastRowNum(); i++) {
				Row curRow = sheet.getRow(i);
				
				String currentMonthId = "F" + (i + 1);
				String lastMonthId = "B" + (i + 1);
				String previousYearMonthId = "D" + (i + 1);
				String previousYearId = "J" + (i + 1);
				String currentYearId = "L" + (i + 1);
				
				
				Cell percentCell = curRow.createCell(7);
				percentCell.setCellStyle(previousMonthPercentStyle);
				percentCell.setCellType(CellType.FORMULA);
				System.out.println(MessageFormat.format(formula, currentMonthId, lastMonthId));
				percentCell.setCellFormula(MessageFormat.format(formula, currentMonthId, lastMonthId));
				CellValue percentValue = evaluator.evaluate(percentCell);
				percentCell.setCellValue(percentValue.getNumberValue());
				
				percentCell = curRow.createCell(8);
				percentCell.setCellStyle(previousYearPercentStyle);
				percentCell.setCellType(CellType.FORMULA);
				System.out.println(MessageFormat.format(formula, currentMonthId, previousYearMonthId));
				percentCell.setCellFormula(MessageFormat.format(formula, currentMonthId, previousYearMonthId));
				percentValue = evaluator.evaluate(percentCell);
				percentCell.setCellValue(percentValue.getNumberValue());
				
				percentCell = curRow.createCell(13);
				percentCell.setCellStyle(currentPercentStyle);
				percentCell.setCellType(CellType.FORMULA);
				System.out.println(MessageFormat.format(formula2, currentYearId, previousYearId,currentMonth));
				percentCell.setCellFormula(MessageFormat.format(formula2, currentYearId, previousYearId,currentMonth));
				percentValue = evaluator.evaluate(percentCell);
				percentCell.setCellValue(percentValue.getNumberValue());
			}
		}

		public void run(HttpServletResponse resp) throws IOException {
			String fileName = "Datos estad\u00EDsticos";
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xlsx\";");
			run(fileName,resp.getOutputStream());
			resp.flushBuffer();
		}
		
		public void run(String fileName, OutputStream out) throws IOException {
			initialize(fileName);
			for (String volume : map.keySet()) {
				Row colRow = sheet.createRow(sheet.getLastRowNum()+1);
				CellUtil.createCell(colRow, 0, AonStringUtils.trimToEmpty( volume ), firstColStyle);
				UdapaStatRow rowData = map.get(volume);
				addCell(colRow, 1, rowData.getPrevMonth(), previousMonthDecimalStyle );
				addCell(colRow, 3, rowData.getLastYearMonth(), previousYearDecimalStyle);
				addCell(colRow, 5, rowData.getMonth(), currentDecimalStyle);
				addCell(colRow, 9, rowData.getLastYear(), previousYearDecimalStyle);
				addCell(colRow,11, rowData.getCurrentYear(), currentDecimalStyle);
			}
			fillFormulas();
			finalize(out);
		}
		
		private Cell addCell(Row row, int colIdx, Double number, XSSFCellStyle style) {
			Cell cell = row.createCell(colIdx);
			cell.setCellStyle(style);
			cell.setCellValue(number!=null?number:0.0);
			cell.setCellType(CellType.NUMERIC);
			return cell;
		}
	}
	
	private static class UdapaStatRow {
		double prevMonth;
		double lastYearMonth;
		double month;
		double lastYear;
		double currentYear;
		
		public double getPrevMonth() {
			return prevMonth;
		}
		public UdapaStatRow setPrevMonth(double prevMonth) {
			this.prevMonth = prevMonth;
			return this;
		}
		public double getLastYearMonth() {
			return lastYearMonth;
		}
		public UdapaStatRow setLastYearMonth(double lastYearMonth) {
			this.lastYearMonth = lastYearMonth;
			return this;
		}
		public double getMonth() {
			return month;
		}
		public UdapaStatRow setMonth(double month) {
			this.month = month;
			return this;
		}
		public double getLastYear() {
			return lastYear;
		}
		public UdapaStatRow setLastYear(double lastYear) {
			this.lastYear = lastYear;
			return this;
		}
		public double getCurrentYear() {
			return currentYear;
		}
		public UdapaStatRow setCurrentYear(double currentYear) {
			this.currentYear = currentYear;
			return this;
		}
	}
}
