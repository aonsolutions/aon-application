package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
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

public class AccountBalanceReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printBalanceReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		AonConfiguration config = AON.getConfiguration(params.getDomainName(), params.getDomain(), params.getUser());
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		AccountBalanceReport report = ACCOUNTING.getAccountBalanceReport(params.getDomainName(), params.getDomain(), params.getUser(), params);
		
		report.setMetadata(new ReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(report))
				.setTitle(params.getBalanceType().getName()));
		
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(36, 36, 50, 20);
		
		float pageWidth = document.getPageSize().getWidth(); 
		float w = pageWidth - document.leftMargin() - document.rightMargin();
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new AccountReportPdfPageEvent(report.getMetadata()));
		document.open();
		
		int columns = 1 + report.getPeriods().size(); 
		PdfPTable table = new PdfPTable(columns);
		
		float amountCellWidth = 70;
		float conceptCellWidth = w - (amountCellWidth * report.getPeriods().size());
		float[] widths = new float[report.getPeriods().size() + 1 ];
		widths[0] = conceptCellWidth;
		for (int i = 0 ; i < report.getPeriods().size(); i++) {
			widths[i+1] = amountCellWidth;
		}
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		PdfPCell emptyCell = new PdfPCell();
		emptyCell.setBorder(0);
		table.addCell(emptyCell);
		
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
			Paragraph concept = new Paragraph(8,c,line.isLeaf()?BODY_FONT:BODY_FONT_BOLD);
			concept.setIndentationLeft(line.getLevel() * 10);
			PdfPCell conceptCell = new PdfPCell();
			conceptCell.addElement(concept);
			conceptCell.setBorder(0);
			conceptCell.setBorderWidthBottom(1);
			conceptCell.setBorderColor(BaseColor.LIGHT_GRAY);
			table.addCell(conceptCell);
			for (String period : report.getPeriods()) {
				Double a = line.getAmounts().get(period);
				String amount = a==null?"":FMT.format(a);
				Paragraph amountP = new Paragraph(8,amount,line.isLeaf()?BODY_FONT:BODY_FONT_BOLD);
				amountP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell amountCell = new PdfPCell( );
				amountCell.addElement(amountP);
				amountCell.setBorder(0);
				amountCell.setBorderWidthBottom(1);
				amountCell.setBorderColor(BaseColor.LIGHT_GRAY);
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
