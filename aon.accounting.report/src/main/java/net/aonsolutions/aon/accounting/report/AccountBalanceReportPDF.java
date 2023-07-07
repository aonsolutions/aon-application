package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountBalanceReportPDF implements IAccountReportPDF {

	private static final BaseColor SUPER_LIGHT_GRAY = new BaseColor(220, 220, 220);
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font SEPARATOR_FONT = new Font(Font.FontFamily.HELVETICA, 5);
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font ITALIC_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	
	@Override
	public void printReportPDF(OutputStream outputStream, AccountingReportParams params) {
		try {
			printBalanceReport(outputStream, params);
		} catch (DocumentException e) {
			throw new AonCoreException(e);
		}		
	}

	public void printBalanceReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		AonConfiguration config = AON.getConfiguration(params.getDomainName(), params.getDomain(), params.getUser());
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		AccountBalanceReport report = ACCOUNTING.getAccountBalanceReport(params.getDomainName(), params.getDomain(), params.getUser(), params);
		
		report.setMetadata(params.getReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(report))
			);
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		
		float pageWidth = document.getPageSize().getWidth(); 
		float w = pageWidth - document.leftMargin() - document.rightMargin();
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new AccountReportPdfPageEvent(report.getMetadata()));
		document.open();
		
		int columns = 1 + report.getPeriods().size() + (report.getParams().isBreakdownEnabled()?2:0); 
		PdfPTable table = new PdfPTable(columns);
		
		float amountCellWidth = 70;
		float conceptCellWidth = w - (amountCellWidth * (columns - 1));
		float[] widths = new float[columns];
		widths[0] = conceptCellWidth;
		int offset = 1;
		if (report.getParams().isBreakdownEnabled()) {
			widths[1] = amountCellWidth;
			widths[2] = amountCellWidth;
			offset = 3;
		}
		for (int i = 0 ; i < report.getPeriods().size(); i++) {
			widths[i+offset] = amountCellWidth;
		}
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		Paragraph separatorParagrph = new Paragraph(0," ",SEPARATOR_FONT);
		PdfPCell separatorCell = new PdfPCell(separatorParagrph);
		// PdfPCell separatorCell = new PdfPCell();
		separatorCell.setBorder(0);
		

		PdfPCell emptyCell = new PdfPCell();
		emptyCell.setBorder(0);
		table.addCell(emptyCell);
		
		if (report.getParams().isBreakdownEnabled()) {
			Paragraph debitParagrph = new Paragraph(8,"S, Deudor",BODY_FONT_BOLD);
			debitParagrph.setAlignment( Element.ALIGN_RIGHT );
			PdfPCell debitCell = new PdfPCell();
			debitCell.setBorder(0);
			debitCell.setBorderWidthBottom(1);
			debitCell.addElement(debitParagrph);
			table.addCell( debitCell );

			Paragraph creditParagrph = new Paragraph(8,"S.Acreed.",BODY_FONT_BOLD);
			creditParagrph.setAlignment( Element.ALIGN_RIGHT );
			PdfPCell creditCell = new PdfPCell();
			creditCell.setBorder(0);
			creditCell.setBorderWidthBottom(1);
			creditCell.addElement(creditParagrph);
			table.addCell( creditCell );
		}
		
		for (String period : report.getPeriods()) {
			Paragraph periodParagrph = new Paragraph(8,period,BODY_FONT_BOLD);
			periodParagrph.setAlignment( Element.ALIGN_RIGHT );
			PdfPCell periodCell = new PdfPCell();
			periodCell.setBorder(0);
			periodCell.setBorderWidthBottom(1);
			periodCell.addElement(periodParagrph);
			table.addCell( periodCell );
		}
	    table.setHeaderRows(1);
		for (BalanceLine line : report.getBalances().values() ) {
			String c = line.getPrefix() + " " + line.getDescription();
			if (line.getType() == AccountBalanceLineStyle.HEADER0) {
				table.addCell(separatorCell);
				for (int i = 0; i < (columns-1); i++) {
					table.addCell(separatorCell);
				}
			}
			boolean leaf = (line.getType() != AccountBalanceLineStyle.HEADER0 
					&& line.getType() != AccountBalanceLineStyle.TOTAL0 
					&& line.getType() != AccountBalanceLineStyle.BREAKDOWN);
			Paragraph concept = new Paragraph(8,c,leaf?BODY_FONT: line.isBreakdown()?ITALIC_FONT:BODY_FONT_BOLD);
			concept.setIndentationLeft(line.getLevel() * 10);
			PdfPCell conceptCell = new PdfPCell();
			conceptCell.addElement(concept);
			conceptCell.setBorder(0);
			conceptCell.setBorderWidthBottom(1);
			conceptCell.setBorderColor(SUPER_LIGHT_GRAY);
			table.addCell(conceptCell);
			
			if (report.getParams().isBreakdownEnabled()) {
				AccountBalance bal = line.getBreakdown();
				Double a = bal!=null && AonMathUtils.isGreatherThanZero(bal.getDebitBalance())?bal.getDebitBalance():null;
				String debit = a==null?"":FMT.format(a);
				Paragraph debitP = new Paragraph(8,debit,ITALIC_FONT);
				debitP.setAlignment( Element.ALIGN_RIGHT );
				PdfPCell debitCell = new PdfPCell( );
				debitCell.addElement(debitP);
				debitCell.setBorder(0);
				debitCell.setBorderWidthBottom((float) 0.5);
				debitCell.setBorderColor(SUPER_LIGHT_GRAY);
				table.addCell(debitCell);

				a = bal!=null && AonMathUtils.isGreatherThanZero(bal.getCreditBalance())?bal.getCreditBalance():null;
				String credit = a==null?"":FMT.format(a);
				Paragraph creditP = new Paragraph(8,credit,ITALIC_FONT);
				creditP.setAlignment( Element.ALIGN_RIGHT );
				PdfPCell creditCell = new PdfPCell( );
				creditCell.addElement(creditP);
				creditCell.setBorder(0);
				creditCell.setBorderWidthBottom((float) 0.5);
				creditCell.setBorderColor(SUPER_LIGHT_GRAY);
				table.addCell(creditCell);
			}
				
			
			for (String period : report.getPeriods()) {
				Double a = line.getAmounts().get(period);
				String amount = a==null?"":FMT.format(a);
				Paragraph amountP = new Paragraph(8,amount,leaf?BODY_FONT:BODY_FONT_BOLD);
				amountP.setAlignment( Element.ALIGN_RIGHT );
				if (line.getLevel() > 1) {
					amountP.setIndentationRight(line.getLevel() * 6);
				}
				PdfPCell amountCell = new PdfPCell( );
				amountCell.addElement(amountP);
				amountCell.setBorder(0);
				amountCell.setBorderWidthBottom((float) 0.5);
				amountCell.setBorderColor(SUPER_LIGHT_GRAY);
				if (report.getParams().isBreakdownEnabled()) {
					amountCell.setBackgroundColor(SUPER_LIGHT_GRAY);
				}
				table.addCell(amountCell);
			}
		}
		document.add(table);
		document.close();
	}

	private void concat(StringBuffer buf, String string) {
		if (buf.length() > 0) {
			buf.append(", ");
		}
		buf.append(string);
	}
	
	private String getFilterDescription(AccountBalanceReport report) {
		AccountingReportParams params = report.getParams();
		StringBuffer buf = new StringBuffer();
		if (report.getSelectedPeriod() != null) {
			concat(buf, "Ejercicio: " + report.getSelectedPeriod().getName()); 
		}
		if (params.getFromDate() != null) {
			concat(buf, "Desde: " + DATE_FORMATTER.format(params.getFromDate()) );
		}
		if (params.getToDate() != null) {
			concat(buf, "Hasta: " + DATE_FORMATTER.format(params.getToDate()) );
		}
		if (report.getSelectedActivity() != null) {
			concat(buf, "Act.: " + report.getSelectedActivity().getDescription() );
		}
		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL) {
			concat(buf, "Seg: CONFID.");	
		}
		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.OFFICIAL) {
			concat(buf, "Seg: NO CONFID.");	
		}
		return buf.toString();
	}

	
}
