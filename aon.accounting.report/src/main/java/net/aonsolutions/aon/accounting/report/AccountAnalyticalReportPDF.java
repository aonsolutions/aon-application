package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountAnalyticalReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00"); //;(#,##0.00)
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static GrayColor GRAY = new GrayColor(0.95f);
	public void printAnalyticalReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		AccountingAnalyticalReport report = ACCOUNTING.getAccountAnalyticalReport(domainName, user, domainId, params );
		
		ReportMetadata metadata = params.getReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(params))
			;
		
		Document document = new Document();
		document.setPageSize(report.getColumns().size()<3?PageSize.A4:PageSize.A4.rotate());
		document.setMargins(30, 30, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new AccountReportPdfPageEvent(metadata));
		document.open();
		
		int columnsPerColumn = 2;
		int columns = 1 + (report.getColumns().size() * columnsPerColumn);
		PdfPTable table = new PdfPTable(columns);
		
		float[] widths = new float[columns];
		widths[0] = 45f;
		widths[1] = getDescriptionColumnWidth(report);

		MutableInt col = new MutableInt(2);
		report.getColumns().forEach(c -> {
				if (!c.isTotalColumn()) {
					widths[col.getValue()] = 30f;
					col.add(1);
				}
				widths[col.getValue()] = 60f;
				col.add(1);
		});
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		Paragraph voidParagrph = new Paragraph(8,"",BODY_FONT_BOLD);
		PdfPCell voidCell = new PdfPCell();
		voidCell.setBorder(0);
		voidCell.addElement(voidParagrph);
		voidCell.setColspan(2);
		table.addCell( voidCell );

		for (AccountingAnalyticalColumn column : report.getColumns()) {
			Paragraph nameParagrph = new Paragraph(8, column.getName(),BODY_FONT_BOLD);
			nameParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell nameCell = new PdfPCell();
			nameCell.setColspan( 2 );
			nameCell.setBorder(0);
			nameCell.setBorderWidthTop(1);
			nameCell.addElement(nameParagrph);
			table.addCell( nameCell );
		}
		
		Paragraph descriptionParagrph = new Paragraph(8,"Cuenta contable",BODY_FONT_BOLD);
		descriptionParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell descripCell = new PdfPCell();
		descripCell.setBorder(0);
		descripCell.setBorderWidthBottom(1);
		descripCell.addElement(descriptionParagrph);
		descripCell.setColspan(2);
		table.addCell( descripCell );
		int i = 0;
		for ( AccountingAnalyticalColumn column : report.getColumns()) {
			boolean grayPainted = (!column.isTotalColumn() && (i-1)%2 == 0);
			if (!column.isTotalColumn()) {
				Paragraph percentParagrph = new Paragraph(8,"%",BODY_FONT_BOLD);
				percentParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell percentCell = new PdfPCell();
				percentCell.setBorder(0);
				percentCell.setBorderWidthBottom(1);
				percentCell.addElement(percentParagrph);
				if (grayPainted) {
					percentCell.setBackgroundColor(GRAY);	
				}
				table.addCell( percentCell );
			}
			Paragraph balanceParagrph = new Paragraph(8,"Saldo.",BODY_FONT_BOLD);
			balanceParagrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell balanceCell = new PdfPCell();
			balanceCell.setBorder(0);
			balanceCell.setBorderWidthBottom(1);
			balanceCell.addElement(balanceParagrph);
			if (grayPainted) {
				balanceCell.setBackgroundColor(GRAY);	
			}
			table.addCell( balanceCell );
			i++;
		}
		table.setHeaderRows(params.isByMonth()?1:2);
	    
		PDFAction action = new PDFAction(report,table);
		report
			.getAccounts()
			.forEach(action);		
	    
		document.add(table);
		document.close();
	}

	private float getDescriptionColumnWidth(AccountingAnalyticalReport report) {
		int size = report.getColumns().size();
		return (size<3?500f:750f) - 120 - ((size-1) * 2 * 60);
	}

	private void concat(StringBuffer buf, String string) {
		if (buf.length() > 0) {
			buf.append(", ");
		}
		buf.append(string);
	}
	
	private String getFilterDescription(AccountingReportParams params) {
		StringBuffer buf = new StringBuffer();
		if (params.getSelectedPeriod() != null) {
			concat(buf, "Ejr.: " + params.getSelectedPeriod().getName() );
		}
		if (params.getFromDate() != null) {
			concat(buf, "Desde: " + DATE_FORMATTER.format(params.getFromDate()) );
		}
		if (params.getToDate() != null) {
			concat(buf, "Hasta: " + DATE_FORMATTER.format(params.getToDate()) );
		}
		if (params.getSelectedActivity() != null) {
			concat(buf, "Act.: " + params.getSelectedActivity().getDescription() );
		}
		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL) {
			concat(buf, "Seg: CONFID.");	
		}
		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.OFFICIAL) {
			concat(buf, "Seg: NO CONFID.");	
		}
		return buf.toString();
	}

	private class PDFAction implements Consumer<AccountOperatingAccount>{
		
		private AccountingAnalyticalReport report;
		private PdfPTable table;
		public PDFAction(AccountingAnalyticalReport report, PdfPTable table) {
			this.report = report;
			this.table = table;
		}
		
		@Override
		public void accept(AccountOperatingAccount account) {
			boolean title = account.getId() == null;
			Paragraph code = new Paragraph(8,title?"":account.getCode(),title?BODY_FONT_BOLD:BODY_FONT);
			PdfPCell codeCell = new PdfPCell();
			codeCell.addElement(code);
			codeCell.setBorder(0);
			table.addCell(codeCell);
			
			String description = account.getDescription();
			Chunk descChunk = new Chunk(description,title?BODY_FONT_BOLD:BODY_FONT);
			while (descChunk.getWidthPoint() > getDescriptionColumnWidth(report)) {
				description = AonStringUtils.abbreviate(account.getDescription(),description.length() - 10); 
				descChunk = new Chunk(description,title?BODY_FONT_BOLD:BODY_FONT);
			}
			Paragraph descriptionParagraph = new Paragraph(8,descChunk);
			if (title) descriptionParagraph.setAlignment( Element.ALIGN_RIGHT );
			PdfPCell descriptionCell = new PdfPCell();
			descriptionCell.addElement(descriptionParagraph);
			descriptionCell.setBorder(0);
			table.addCell(descriptionCell);
			int i = 0;
			for (AccountingAnalyticalColumn column : report.getColumns()) {
				boolean grayPainted = (!column.isTotalColumn() && (i-1)%2 == 0); 
				AccountingAnalyticalStatement aos = report.get(account.getCode(), column);
				
				Double balance = (aos != null)?aos.getBalance() : 0.0;
				if (!column.isTotalColumn()) {
					Double percent = (aos != null)?aos.getPercent() : 0.0;
					String percentStr = AonMathUtils.isZero( percent )?"":FMT.format(percent);	
					if (!account.getType().isCalculated()) {
						balance = (aos != null)?aos.getAmount() : 0.0;
					} else {
						percentStr = "";
					}
					Paragraph percentP = new Paragraph(8,percentStr,title?BODY_FONT_BOLD:BODY_FONT);
					percentP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell percentCell = new PdfPCell( );
					percentCell.addElement(percentP);
					percentCell.setBorder(0);
					if (grayPainted) {
						percentCell.setBackgroundColor(GRAY);	
					}
					table.addCell(percentCell);
				}
				String balanceStr = AonMathUtils.isZero( balance )?"":FMT.format(balance);
				Paragraph saldoP = new Paragraph(8,balanceStr,title?BODY_FONT_BOLD:BODY_FONT);
				saldoP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell saldoCell = new PdfPCell( );
				saldoCell.addElement(saldoP);
				saldoCell.setBorder(0);
				if (grayPainted) {
					saldoCell.setBackgroundColor(GRAY);
				}
				table.addCell(saldoCell);
				i++;
			}
		}

		
	}
	
}
