package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Finance Report Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/FinanceReportExcelPrint/*" })
public class FinanceReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2852761111430416931L;
	private NumberFormat euroFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String path = req.getPathInfo();
			
			switch (path) {
			case "/payments":
				payments(req, resp);
				break;
				
			case "/print":
				print(req, resp);
				break;

			default:
				throw new ServletException("Path not found");
			}
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private void print(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String financeParams = req.getParameter( IRequestParamsNames.FINANCE_PARAMS );
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String user = req.getParameter(IRequestParamsNames.USER);
		int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
		
		
		FinanceParams params = JsonParser.parseFinanceParams(financeParams);
		ExcelAction action = new ExcelAction( );
		String name = params.isPayroll() ? "Vencimientos n\u00f3minas" : "Cartera de pagos y cobros";
		action.initialize(name);
		Stream<Finance> stream =  AON.getFinancesStream(domainName,domainId,user, params,0,Integer.MAX_VALUE);
		stream.forEach(action);
		resp.setContentType(MimeType.MS_EXCEL.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\""+name+"."+ MimeType.MS_EXCEL.getExtension()+ "\";");
		action.finalize(resp.getOutputStream());
		
		resp.flushBuffer();
		stream.close();
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<Finance>{
		private XSSFCellStyle entryHeaderStyle;
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Listado diario de movimientos ");
			footer.setRight("P\u00E1g: &P/&N");
			
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerStyle.setFont(journalHeaderFont);
		    headerStyle.setBorderTop(BorderStyle.THIN);
		    headerStyle.setBorderRight(BorderStyle.THIN);
		    headerStyle.setBorderLeft(BorderStyle.THIN);
		    headerStyle.setBorderBottom(BorderStyle.THIN);
		    
		    
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment( HorizontalAlignment.CENTER );
			entryHeaderStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);

			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "TIPO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 6 * 256);

			CellUtil.createCell(row, cellCount, "FECHA VTO.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "N. FACTURA.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "FECHA FRA.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "TITULAR", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 30 * 256);

			CellUtil.createCell(row, cellCount, "F.PAGO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "IMPORTE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "GASTOS", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "ESTADO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			
		}

		@Override
		public void accept(Finance finance) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			addCell(finance.isPayment()?"PAGO":"COBRO");
			addCell(finance.getDueDate());
			if (finance.getInvoice() == null) {
				addEmptyCell();
				addEmptyCell();
				if(finance.isPayroll()) addCell(finance.getRegistry().getDocument()); else addEmptyCell();
				if(finance.isPayroll()) addCell(finance.getRegistry().getName()); else addEmptyCell();
			} else {
				addCell(finance.getInvoice().getReferenceCode());
				addCell(finance.getInvoice().getIssueDate());
				addCell(finance.getInvoice().getRegistryDocument());
				addCell(finance.getInvoice().getRegistryName());
			}
			addCell(finance.getPayMethodName());
			
			Cell amountCell = addCell(euroFormat.format(finance.getAmount()));
			CellStyle amountStyle = workbook.createCellStyle();
			amountStyle.setAlignment( HorizontalAlignment.RIGHT );
			amountCell.setCellStyle(amountStyle);
			
			Cell expensesCell = addCell(euroFormat.format(finance.getExpenses()));
			CellStyle expensesStyle = workbook.createCellStyle();
			expensesStyle.setAlignment( HorizontalAlignment.RIGHT );
			expensesCell.setCellStyle(expensesStyle);
			
			if (finance.getFinanceStatus() == null) {
				addEmptyCell();
			} else {
				addCell(finance.getFinanceStatus().getDescription());
			}

			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
		
	}
	
	private void payments(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String financeParams = req.getParameter( IRequestParamsNames.FINANCE_PARAMS );
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String user = req.getParameter(IRequestParamsNames.USER);
		int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
		
		
		FinanceParams params = JsonParser.parseFinanceParams(financeParams);
		ExcelPaymentsAction action = new ExcelPaymentsAction( );
		String name = "Informe de plazos de pago";
		action.initialize(name);
		Stream<Finance> stream =  AON.getFinancesStream(domainName,domainId,user, params,0,Integer.MAX_VALUE);
		
		stream.map(finance -> {
			LinkedList<FinanceTracking> trackings = AON.getFinanceTracking(domainName, domainId, user, finance.getId());
			Optional<FinanceTracking> paidTracking = trackings.stream().sorted((t1, t2) -> t2.getTrackingDate().compareTo(t1.getTrackingDate())).filter(tracking -> tracking.getType().equals(FinanceTrackingType.PAID)).findFirst();
			if(paidTracking.isPresent()) finance.setPaidDate(paidTracking.get().getTrackingDate());
			return finance;
		}).forEach(action);
				
		resp.setContentType(MimeType.MS_EXCEL.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\""+name+"."+ MimeType.MS_EXCEL.getExtension()+ "\";");
		action.finalize(resp.getOutputStream());
		
		resp.flushBuffer();
		stream.close();
	}
	
	private class ExcelPaymentsAction extends AbsExcelAction implements Consumer<Finance>{
		private XSSFCellStyle entryHeaderStyle;
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Listado diario de movimientos ");
			footer.setRight("P\u00E1g: &P/&N");
			
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerStyle.setFont(journalHeaderFont);
		    headerStyle.setBorderTop(BorderStyle.THIN);
		    headerStyle.setBorderRight(BorderStyle.THIN);
		    headerStyle.setBorderLeft(BorderStyle.THIN);
		    headerStyle.setBorderBottom(BorderStyle.THIN);
		    
		    
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment( HorizontalAlignment.CENTER );
			entryHeaderStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);

			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "N. FACTURA.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 16 * 256);
			
			CellUtil.createCell(row, cellCount, "FECHA FRA.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);

			CellUtil.createCell(row, cellCount, "TITULAR", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 42 * 256);
			
			CellUtil.createCell(row, cellCount, "F.PAGO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 18 * 256);

			CellUtil.createCell(row, cellCount, "FECHA VTO.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "IMPORTE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "GASTOS", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "ESTADO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "FECHA PAGO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "PLAZO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);
	
		}

		@Override
		public void accept(Finance finance) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			if (finance.getInvoice() == null) {
				addEmptyCell();
				addEmptyCell();
				addEmptyCell();
				addEmptyCell();
			} else {
				addCell(finance.getInvoice().getReferenceCode());
				addCell(finance.getInvoice().getIssueDate());
				addCell(finance.getInvoice().getRegistryDocument());
				addCell(finance.getInvoice().getRegistryName());
				
			}
			addCell(finance.getPayMethodName());
			addCell(finance.getInvoice().getIssueDate());
			
			Cell amountCell = addCell(euroFormat.format(finance.getAmount()));
			CellStyle amountStyle = workbook.createCellStyle();
			amountStyle.setAlignment( HorizontalAlignment.RIGHT );
			amountCell.setCellStyle(amountStyle);
			
			Cell expensesCell = addCell(euroFormat.format(finance.getExpenses()));
			CellStyle expensesStyle = workbook.createCellStyle();
			expensesStyle.setAlignment( HorizontalAlignment.RIGHT );
			expensesCell.setCellStyle(expensesStyle);
			
			if (finance.getFinanceStatus() == null) {
				addEmptyCell();
			} else {
				Cell statusCell = addCell(finance.getFinanceStatus().getDescription());
				CellStyle statusStyle = workbook.createCellStyle();
				statusStyle.setAlignment( HorizontalAlignment.CENTER );
				statusCell.setCellStyle(statusStyle);
			}
			// Fecha pago
			if(finance.getPaidDate() == null) {
				Cell paidCell = addCell("Pendiente");
				CellStyle paidStyle = workbook.createCellStyle();
				paidStyle.setAlignment( HorizontalAlignment.CENTER );
				paidCell.setCellStyle(paidStyle);
			} else addCell(finance.getPaidDate());
			
			// Plazo
			if(finance.getPaidDate() == null && finance.getInvoice() == null) {
				addEmptyCell();
			} else if(finance.getPaidDate() == null) {
				Cell cell = addCell(getDaysBetween(new Date(), finance.getInvoice().getIssueDate()) + " día(s)" );
				CellStyle periodStyle = workbook.createCellStyle();
				periodStyle.setAlignment( HorizontalAlignment.RIGHT );
				cell.setCellStyle(entryHeaderStyle);
			} else {
				Cell cell = addCell(getDaysBetween(finance.getPaidDate(), finance.getInvoice().getIssueDate()) + " día(s)" );
				CellStyle periodStyle = workbook.createCellStyle();
				periodStyle.setAlignment( HorizontalAlignment.RIGHT );
				cell.setCellStyle(entryHeaderStyle);
			}
			
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}

		private String getDaysBetween(Date paidDate, Date issueDate) {
			 long differenceInMilliseconds = paidDate.getTime() - issueDate.getTime();
		     long daysBetween = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
		     return  daysBetween + "";  
		}
		
	}
}
