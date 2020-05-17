package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqCost;
import com.esferalia.aon.gwt.payroll.shared.MainCost;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-Excel", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_excel/*" ,
				"/aon_gwt_payroll/cost_excel/*" 
		}
)
public class CostExcelServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String _domainName = request.getServerName();
		String _month = request.getParameter("month");
		String _year = request.getParameter("year");
		String _enterpriseId = request.getParameter("enterpriseId");
		String _workplaceId = request.getParameter("workplaceId");
		String _salary = request.getParameter("salary");
		String _extra = request.getParameter("extra");
		String _settle = request.getParameter("settle");
		String _delay = request.getParameter("delay");
		
		System.out.println(
				"DomainName : " + _domainName + "\n" +
				"Month : " + _month + "\n" +
				"Year : " + _year + "\n" +
				"EnterpriseId : " + _enterpriseId + "\n" +
				"WorkplaceId : " + _workplaceId + "\n" 
		);
		
		Date startDate = new Date();
		startDate.setYear(Integer.parseInt(_year));
		startDate.setMonth(Integer.parseInt(_month));
		startDate.setDate(1);
		
		Date endDate = DateUtils.getLastDayOfMonth(startDate);
		
		try {
			// Print salary types
			List<Byte> _types = new ArrayList<Byte>();
			_types.add((_salary.equals("1")) ? (byte) 0 : (byte) -1);
			_types.add((_extra.equals("1")) ? (byte) 1 : (byte) -1);
			_types.add((_settle.equals("1")) ? (byte) 2 : (byte) -1);
			_types.add((_delay.equals("1")) ? (byte) 3 : (byte) -1);
			
			
			ExcelAction action = new ExcelAction(startDate, endDate);
			action.initialize("Listado de costes");
			action.paintRows(JooqCost.getSalaries(_domainName, _enterpriseId, _workplaceId, _month, _year, _types));
			
			// Otra forma de hacerlo con el evento 'accept' haciendo un foreach
			// JooqCost.getSalaries(_domainName, _enterpriseId, _workplaceId, _month, _year, _types).forEach(action);
			
			response.setContentType(MimeType.MS_EXCEL.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(response.getOutputStream());
			response.flushBuffer();
		
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
	
	
	private class ExcelAction extends AbsExcelAction implements Consumer<MainCost>{
		private XSSFCellStyle entryHeaderStyle;
		private XSSFCellStyle footerStyle;
		private XSSFCellStyle footerDecimalStyle;
		private Date startDate;
		private Date endDate;
		
		public ExcelAction(Date startDate, Date endDate) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Costes");
			footer.setRight("P\u00E1g: &P/&N");
			
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			decimalStyle.setFont(smallFont);
			
			
			// ------------------------------------------------ TITLE STYLE
			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setColor(IndexedColors.WHITE.index);
			titleFont.setFontHeightInPoints((short) 18);

			XSSFCellStyle titleCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
			titleCellStyle.setWrapText(true);
			titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
			titleCellStyle.setFont(titleFont);
			
			// ------------------------------------------------ TITLE STYLE
			Font subtitleFont = workbook.createFont();
			subtitleFont.setBold(true);
			subtitleFont.setColor(IndexedColors.WHITE.index);
			subtitleFont.setFontHeightInPoints((short) 11);

			XSSFCellStyle subtitleCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			subtitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
			subtitleCellStyle.setWrapText(true);
			subtitleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			subtitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			subtitleCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
			subtitleCellStyle.setFont(subtitleFont);
			
			// ------------------------------------------------ BLANK STYLE
			XSSFCellStyle blanckStyle = (XSSFCellStyle) workbook.createCellStyle();
			blanckStyle.setAlignment( HorizontalAlignment.CENTER );
			blanckStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			blanckStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			blanckStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		    
		    // ------------------------------------------------ HEADER STYLE
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.index);
			headerFont.setFontHeightInPoints((short) 9);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		    headerStyle.setFont(headerFont);
		    
		    // ------------------------------------------------ ENTRY STYLE
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderTop(BorderStyle.THICK);
//			entryHeaderStyle.setBorderColor(BorderSide.TOP, new XSSFColor(Color.RED));
			entryHeaderStyle.setFont(defaulFont);
			
			// ------------------------------------------------ FOOTER STYLE
			Font footerFont = workbook.createFont();
			footerFont.setBold(true);
			footerFont.setColor(IndexedColors.BLACK.index);
			footerFont.setFontHeightInPoints((short) 9);
			
			footerStyle = (XSSFCellStyle) workbook.createCellStyle();
			footerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			footerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			footerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			footerStyle.setFont(footerFont);
			
			footerDecimalStyle = (XSSFCellStyle) workbook.createCellStyle();
			footerDecimalStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			footerDecimalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			footerDecimalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			footerDecimalStyle.setFont(footerFont);
			footerDecimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
			footerDecimalStyle.setAlignment( HorizontalAlignment.RIGHT );
			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			
			// ----------------------------------------------- ROW 1 - Title
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, 10);
			CellUtil.createCell(row, 0, "Tabla de costes", titleCellStyle);
			row.setHeight((short) 500);
			++rowCount;
			
			// ----------------------------------------------- ROW 2 - Subtitle
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, 10);
			CellUtil.createCell(row, 0, 
					"Desde " + this.startDate.getDate() + "/" + (this.startDate.getMonth() + 1) + "/" + (this.startDate.getYear()) +
					" hasta " + this.endDate.getDate() + "/" + (this.endDate.getMonth() + 1) + "/" + (this.endDate.getYear()), 
					subtitleCellStyle);
			row.setHeight((short) 500);
			++rowCount;
			
			// ----------------------------------------------- ROW 3 - Blank Space
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, 10);
			CellUtil.createCell(row, 0, "", blanckStyle);
			row.setHeight((short) 200);
			++rowCount;

			// ----------------------------------------------- ROW 4 - Header
			row = sheet.createRow(rowCount);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "Trabajador", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "Centro Trabajo", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);

			CellUtil.createCell(row, cellCount, "Tipo", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "Devengado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "S.S. Trabajador", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "I.R.P.F.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "Deducciones", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "Liquido", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "S.S. Empresa", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "Coste Total", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
			
			CellUtil.createCell(row, cellCount, "Total S.S.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
			++rowCount;
			
			// ----------------------------------------------- ROW 5 - Blank Space
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, 10);
			CellUtil.createCell(row, 0, "", blanckStyle);
			row.setHeight((short) 100);
			++rowCount;

			
		}
		
		private CellRangeAddress mergeHeaderRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
			CellRangeAddress range = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(range);
			return range;
		}
		
		public void paintRows(Stream<MainCost> costs) {
			Cell cell;
			MainCost[] costsArr = costs.toArray(MainCost[]::new);
			for( int i=0; i < costsArr.length - 1; i++ ) {
				row = sheet.createRow(rowCount++);
				cellCount = 0;

				cell = addCell(costsArr[i].getEmployeeName());
				cell = addCell(costsArr[i].getWorkplaceName());
				cell = addCell(costsArr[i].getSalaryType());
				cell = addCell(costsArr[i].getTotalPayment());
				cell = addCell(costsArr[i].getEmployeeSS());
				cell = addCell(costsArr[i].getTotalIRPF());
				cell = addCell(costsArr[i].getTotalDeductions());
				cell = addCell(costsArr[i].getTotalLiquid());
				cell = addCell(costsArr[i].getEnterpriseSS());
				cell = addCell(costsArr[i].getTotalCost());
				cell = addCell(costsArr[i].getTotalSS());
				
				try {
					if (rowCount % 100 == 0) sheet.flushRows();
				} catch (IOException e) {
					throw new AonCoreException( e );
				}
			}
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			Integer i = costsArr.length-1;
			cell = addCell(costsArr[i].getEmployeeName());
			cell.setCellStyle(footerStyle);
			cell = addCell(costsArr[i].getWorkplaceName());
			cell.setCellStyle(footerStyle);
			cell = addCell(costsArr[i].getSalaryType());
			cell.setCellStyle(footerStyle);
			cell = addCell(costsArr[i].getTotalPayment());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getEmployeeSS());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getTotalIRPF());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getTotalDeductions());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getTotalLiquid());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getEnterpriseSS());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getTotalCost());
			cell.setCellStyle(footerDecimalStyle);
			cell = addCell(costsArr[i].getTotalSS());
			cell.setCellStyle(footerDecimalStyle);
			
		}

		@Override
		public void accept(MainCost entry) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			addCell(entry.getEmployeeName());
			addCell(entry.getWorkplaceName());
			addCell(entry.getSalaryType());
			addCell(entry.getTotalPayment());
			addCell(entry.getEmployeeSS());
			addCell(entry.getTotalIRPF());
			addCell(entry.getTotalDeductions());
			addCell(entry.getTotalLiquid());
			addCell(entry.getEnterpriseSS());
			addCell(entry.getTotalCost());
			addCell(entry.getTotalSS());
			
//			addCell(AonMathUtils.round( entry.getDebitBalance()  + entry.getInitialDebitBalance()  - entry.getInitialUnpaidBalance()));
			
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
		
	}
	
}