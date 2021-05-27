package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
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
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Finance Report Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/FinanceReportExcelPrint" })
public class FinanceReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2852761111430416931L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String financeParams = req.getParameter( IRequestParamsNames.FINANCE_PARAMS );
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			
			
			FinanceParams params = JsonParser.parseFinanceParams(financeParams);
			ExcelAction action = new ExcelAction( );
			String name = "Cartera de pagos y cobros";
			action.initialize(name);
			Stream<Finance> stream =  AON.getFinancesStream(domainName,domainId,user, params,0,Integer.MAX_VALUE);
			stream.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+name+"."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			stream.close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

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
				addEmptyCell();
				addEmptyCell();
			} else {
				addCell(finance.getInvoice().getReferenceCode());
				addCell(finance.getInvoice().getIssueDate());
				addCell(finance.getInvoice().getRegistryDocument());
				addCell(finance.getInvoice().getRegistryName());
			}
			addCell(finance.getPayMethodName());
			addCell(finance.getAmount());
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
}
