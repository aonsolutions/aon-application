package com.esferalia.aon.gwt.marketing.server;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

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

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.gwt.marketing.shared.IRequestParamsNames;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "QuestionReport Excel Print", urlPatterns = { "/aon_gwt_marketing/roms/QuestionReportExcelPrint"})
public class QuestionReportExcelPrint extends HttpServlet {
	
	private static final long serialVersionUID = 2828445311492662344L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try  {
			ExcelAction action = new ExcelAction();
			action.initialize("Preguntas");
			
			QuestionParams questionParams = new QuestionParams()
					.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)))
					.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME))
					.setUser(req.getParameter(IRequestParamsNames.USER))
					;
					
			List<Question> list = AON.getQuestionList(questionParams);
			list.stream().forEach(action);

			resp.setContentType(MimeType.MS_EXCEL.getName());
			String balName = "Preguntas";
			resp.setHeader("Content-disposition",
					"attachment; filename=\"" + balName + "." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			list.stream().close();
		} catch (RuntimeException e) {
			throw new ServletException(e);
		}
	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<Question> {
		private XSSFCellStyle entryHeaderStyle;
	
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3);
			sheet.setMargin(Sheet.RightMargin, 0.3);
			sheet.setMargin(Sheet.TopMargin, 0.3);
			Footer footer = sheet.getFooter();
			footer.setLeft("Preguntas ");
			footer.setRight("P\u00E1g: &P/&N");
	
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
	
			decimalStyle.setFont(smallFont);
	
			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
	
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
			headerStyle.setFont(journalHeaderFont);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderBottom(BorderStyle.THIN);
	
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
			entryHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);
	
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
	
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			// cells
			CellUtil.createCell(row, cellCount, "ALIAS", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);
	
			CellUtil.createCell(row, cellCount, "DESCRIPCI\u00D3N", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 70 * 256);
	
			CellUtil.createCell(row, cellCount, "TIPO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
	
			CellUtil.createCell(row, cellCount, "ACTIVA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
	
		}
	
		@Override
		public void accept(Question question) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			addCell(question.getAlias());
			addCell(question.getText());
			addCell(question.getType());
			addCell(question.isActive());
			
			try {
				if (rowCount % 100 == 0)
					sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException(e);
			}
		}
	}
}
