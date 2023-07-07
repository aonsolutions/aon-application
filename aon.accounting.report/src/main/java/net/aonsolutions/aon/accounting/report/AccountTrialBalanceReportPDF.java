package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountTrialBalanceReportPDF implements IAccountReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00"); //;(#,##0.00)
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	
	@Override
	public void printReportPDF(OutputStream outputStream, AccountingReportParams params) {
		try {
			trialBalanceReportReport(outputStream, params);
		} catch (DocumentException e) {
			throw new AonCoreException(e);
		}
	}

	public void trialBalanceReportReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(domainName, domainId, user, params );
		
		ReportMetadata metadata = params.getReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(params))
			;
		
		int columns = 2; 
		if (report.hasBeforePeriodAmounts()) {
			columns = columns + 2;
		}
		if (report.hasOpeningAmounts()) {
			columns = columns + 2;
		}
		if (report.hasInPeriodPreviousAmounts()) {
			columns = columns + 2;
		}
		columns = columns + 2;
		columns = columns + 2;

		Document document = new Document();
		// document.setPageSize(PageSize.A4.rotate());
		document.setPageSize(columns<9?PageSize.A4:PageSize.A4.rotate());
		document.setMargins(30, 30, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new AccountReportPdfPageEvent(metadata));
		document.open();
		
		PdfPTable table = new PdfPTable(columns);
		
		float[] widths = new float[columns];
		widths[0] = 45f;
		widths[1] = getDescriptionColumnWidth(columns);
		for (int i = 2; i < columns; i++) {
			widths[i] = 60f;
		}
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		Paragraph emptyParagrph = new Paragraph(8,"",BODY_FONT_BOLD);
		emptyParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell emptyCell = new PdfPCell();
		emptyCell.setBorder(0);
		emptyCell.setBorderWidthTop(1);
		emptyCell.addElement(emptyParagrph);
		emptyCell.setColspan(2);
		table.addCell( emptyCell );
		
		if (report.hasBeforePeriodAmounts()) {
			String msg = report.getParams().getSelectedPeriod() != null
					?"Saldos anter. al " + DATE_FORMATTER.format(report.getParams().getSelectedPeriod().getInitiationDate())
					:"Saldos anteriores";
			Paragraph debitParagrph = new Paragraph(8, msg, BODY_FONT_BOLD);
			debitParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell debitCell = new PdfPCell();
			debitCell.setBorder(0);
			debitCell.setBorderWidthTop(1);
			debitCell.addElement(debitParagrph);
			debitCell.setColspan(2);
			table.addCell( debitCell );
		}
		if (report.hasOpeningAmounts()) {
			Paragraph debitParagrph = new Paragraph(8, "Saldo apertura",BODY_FONT_BOLD);
			debitParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell debitCell = new PdfPCell();
			debitCell.setBorder(0);
			debitCell.setBorderWidthTop(1);
			debitCell.addElement(debitParagrph);
			debitCell.setColspan(2);
			table.addCell( debitCell );
		}
		// Saldo previo
		if (report.hasInPeriodPreviousAmounts()) {
			String msg = "Saldo hasta " + DATE_FORMATTER.format(report.getParams().getFromDate());
			Paragraph debitParagrph = new Paragraph(8, msg,BODY_FONT_BOLD);
			debitParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell debitCell = new PdfPCell();
			debitCell.setBorder(0);
			debitCell.setBorderWidthTop(1);
			debitCell.addElement(debitParagrph);
			debitCell.setColspan(2);
			table.addCell( debitCell );
		}

		
		Paragraph debitParagrph = new Paragraph(8, "Sumas periodo",BODY_FONT_BOLD);
		debitParagrph.setAlignment( Element.ALIGN_CENTER);
		PdfPCell debitCell = new PdfPCell();
		debitCell.setBorder(0);
		debitCell.setBorderWidthTop(1);
		debitCell.addElement(debitParagrph);
		debitCell.setColspan(2);
		table.addCell( debitCell );

		Paragraph endParagrph = new Paragraph(8, "Saldos finales",BODY_FONT_BOLD);
		endParagrph.setAlignment( Element.ALIGN_CENTER);
		PdfPCell endCell = new PdfPCell();
		endCell.setBorder(0);
		endCell.setBorderWidthTop(1);
		endCell.addElement(endParagrph);
		endCell.setColspan(2);
		table.addCell( endCell );


		Paragraph descriptionParagrph = new Paragraph(8,"Cuenta contable",BODY_FONT_BOLD);
		descriptionParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell descripCell = new PdfPCell();
		descripCell.setBorder(0);
		descripCell.setBorderWidthBottom(1);
		descripCell.addElement(descriptionParagrph);
		descripCell.setColspan(2);
		table.addCell( descripCell );

		
		//************************************************************
		String sd = "S. Deudor";
		String sa = "S. Acreed.";
		if (report.hasBeforePeriodAmounts()) {
			Paragraph col1Paragrph = new Paragraph(8, sd, BODY_FONT_BOLD);
			col1Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col1Cell = new PdfPCell();
			col1Cell.setBorder(0);
			col1Cell.setBorderWidthBottom(1);
			col1Cell.addElement(col1Paragrph);
			table.addCell( col1Cell );
			
			Paragraph col2Paragrph = new Paragraph(8, sa, BODY_FONT_BOLD);
			col2Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col2Cell = new PdfPCell();
			col2Cell.setBorder(0);
			col2Cell.setBorderWidthBottom(1);
			col2Cell.addElement(col2Paragrph);
			table.addCell( col2Cell );
		}
		if (report.hasOpeningAmounts()) {
			Paragraph col1Paragrph = new Paragraph(8, sd, BODY_FONT_BOLD);
			col1Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col1Cell = new PdfPCell();
			col1Cell.setBorder(0);
			col1Cell.setBorderWidthBottom(1);
			col1Cell.addElement(col1Paragrph);
			table.addCell( col1Cell );
			
			Paragraph col2Paragrph = new Paragraph(8, sa, BODY_FONT_BOLD);
			col2Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col2Cell = new PdfPCell();
			col2Cell.setBorder(0);
			col2Cell.setBorderWidthBottom(1);
			col2Cell.addElement(col2Paragrph);
			table.addCell( col2Cell );
		}
		// Saldo previo
		if (report.hasInPeriodPreviousAmounts()) {
			Paragraph col1Paragrph = new Paragraph(8, sd, BODY_FONT_BOLD);
			col1Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col1Cell = new PdfPCell();
			col1Cell.setBorder(0);
			col1Cell.setBorderWidthBottom(1);
			col1Cell.addElement(col1Paragrph);
			table.addCell( col1Cell );
			
			Paragraph col2Paragrph = new Paragraph(8, sa, BODY_FONT_BOLD);
			col2Paragrph.setAlignment( Element.ALIGN_RIGHT);
			PdfPCell col2Cell = new PdfPCell();
			col2Cell.setBorder(0);
			col2Cell.setBorderWidthBottom(1);
			col2Cell.addElement(col2Paragrph);
			table.addCell( col2Cell );
		}

		
		Paragraph col1Paragrph = new Paragraph(8, "Debe", BODY_FONT_BOLD);
		col1Paragrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell col1Cell = new PdfPCell();
		col1Cell.setBorder(0);
		col1Cell.setBorderWidthBottom(1);
		col1Cell.addElement(col1Paragrph);
		table.addCell( col1Cell );
		
		Paragraph col2Paragrph = new Paragraph(8, "Haber", BODY_FONT_BOLD);
		col2Paragrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell col2Cell = new PdfPCell();
		col2Cell.setBorder(0);
		col2Cell.setBorderWidthBottom(1);
		col2Cell.addElement(col2Paragrph);
		table.addCell( col2Cell );

		Paragraph col3Paragrph = new Paragraph(8, sd, BODY_FONT_BOLD);
		col3Paragrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell col3Cell = new PdfPCell();
		col3Cell.setBorder(0);
		col3Cell.setBorderWidthBottom(1);
		col3Cell.addElement(col3Paragrph);
		table.addCell( col3Cell );
		
		Paragraph col4Paragrph = new Paragraph(8, sa, BODY_FONT_BOLD);
		col4Paragrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell col4Cell = new PdfPCell();
		col4Cell.setBorder(0);
		col4Cell.setBorderWidthBottom(1);
		col4Cell.addElement(col4Paragrph);
		table.addCell( col4Cell );

		//************************************************************
		
		table.setHeaderRows(2);
	    
		PDFAction action = new PDFAction(report,table,columns);
		report.getBalances().values()
			.forEach(action);		
		AccountTrialBalance totals = report.getTotalBalance();
		if (totals != null) {
			PDFAction totalAction = new PDFAction(report,table,columns);
			totals.setDescription("Totales");
			totalAction.accept(report.getTotalBalance());
		}
		document.add(table);

		document.close();

	}

	private float getDescriptionColumnWidth(int columns) {
		return (columns<9?150f:250f);
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

	private class PDFAction implements Consumer<AccountTrialBalance>{
		
		private AccountTrialBalanceReport report;
		private int columns;
		private PdfPTable table;
		
		public PDFAction(AccountTrialBalanceReport report, PdfPTable table, int columns) {
			this.report = report;
			this.table = table;
		}
		
		@Override
		public void accept(AccountTrialBalance account) {
			 
			boolean title = account.getId() == null;
			boolean bold = account.getId() == null || report.getParams().getLevel() > AonStringUtils.length( account.getCode());
			
			String accountCode = AonStringUtils.defaultIfBlank(account.getCode(),"");
			Paragraph code = new Paragraph(8,accountCode,bold?BODY_FONT_BOLD:BODY_FONT);
			PdfPCell codeCell = new PdfPCell();
			codeCell.addElement(code);
			codeCell.setBorder(0);
			table.addCell(codeCell);
			
			String description = AonStringUtils.defaultIfBlank(account.getDescription(),"");
			
			Chunk descChunk = new Chunk(description,bold?BODY_FONT_BOLD:BODY_FONT);
			while (descChunk.getWidthPoint() > getDescriptionColumnWidth(this.columns)) {
				description = AonStringUtils.abbreviate(account.getDescription(),description.length() - 10); 
				descChunk = new Chunk(description,bold?BODY_FONT_BOLD:BODY_FONT);
			}
			Paragraph descriptionParagraph = new Paragraph(8,descChunk);
			if (title) descriptionParagraph.setAlignment( Element.ALIGN_RIGHT );
			PdfPCell descriptionCell = new PdfPCell();
			descriptionCell.addElement(descriptionParagraph);
			descriptionCell.setBorder(0);
			table.addCell(descriptionCell);
			
			
			if (report.hasBeforePeriodAmounts()) {
				String bpd = AonMathUtils.isZero( account.getBeforePeriodDebitBalance() )?"":FMT.format(account.getBeforePeriodDebitBalance());
				Paragraph bpdP = new Paragraph(8,bpd,bold?BODY_FONT_BOLD:BODY_FONT);
				bpdP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell bpdCell = new PdfPCell( );
				bpdCell.addElement(bpdP);
				bpdCell.setBorder(0);
				table.addCell(bpdCell);
				
				String bpc = AonMathUtils.isZero( account.getBeforePeriodUnpaidBalance() )?"":FMT.format(account.getBeforePeriodUnpaidBalance());
				Paragraph bpcP = new Paragraph(8,bpc,bold?BODY_FONT_BOLD:BODY_FONT);
				bpcP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell bpcCell = new PdfPCell( );
				bpcCell.addElement(bpcP);
				bpcCell.setBorder(0);
				table.addCell(bpcCell);
			}
			if (report.hasOpeningAmounts()) {
				String ipod = AonMathUtils.isZero( account.getInPeriodOpeningDebitBalance() )?"":FMT.format(account.getInPeriodOpeningDebitBalance());
				Paragraph ipodP = new Paragraph(8,ipod,bold?BODY_FONT_BOLD:BODY_FONT);
				ipodP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell ipodCell = new PdfPCell( );
				ipodCell.addElement(ipodP);
				ipodCell.setBorder(0);
				table.addCell(ipodCell);
				
				String ipoc = AonMathUtils.isZero( account.getInPeriodOpeningUnpaidBalance() )?"":FMT.format(account.getInPeriodOpeningUnpaidBalance());
				Paragraph ipocP = new Paragraph(8,ipoc,bold?BODY_FONT_BOLD:BODY_FONT);
				ipocP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell ipocCell = new PdfPCell( );
				ipocCell.addElement(ipocP);
				ipocCell.setBorder(0);
				table.addCell(ipocCell);
			}
			// Saldo previo
			if (report.hasInPeriodPreviousAmounts()) {
				String ipbd = AonMathUtils.isZero( account.getInPeriodBeforeDebitBalance() )?"":FMT.format(account.getInPeriodBeforeDebitBalance());
				Paragraph ipbdP = new Paragraph(8,ipbd,bold?BODY_FONT_BOLD:BODY_FONT);
				ipbdP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell ipbdCell = new PdfPCell( );
				ipbdCell.addElement(ipbdP);
				ipbdCell.setBorder(0);
				table.addCell(ipbdCell);
				
				String ipbc = AonMathUtils.isZero( account.getInPeriodUnpaidBalance() )?"":FMT.format(account.getInPeriodBeforeUnpaidBalance());
				Paragraph ipbcP = new Paragraph(8,ipbc,bold?BODY_FONT_BOLD:BODY_FONT);
				ipbcP .setAlignment( Element.ALIGN_RIGHT );
				PdfPCell ipbcCell = new PdfPCell( );
				ipbcCell.addElement(ipbcP);
				ipbcCell.setBorder(0);
				table.addCell(ipbcCell);
			}
			
			String ipd = AonMathUtils.isZero( account.getInPeriodDebit() )?"":FMT.format(account.getInPeriodDebit());
			Paragraph ipdP = new Paragraph(8,ipd,bold?BODY_FONT_BOLD:BODY_FONT);
			ipdP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell ipdCell = new PdfPCell( );
			ipdCell.addElement(ipdP);
			ipdCell.setBorder(0);
			table.addCell(ipdCell);
			
			String ipc = AonMathUtils.isZero( account.getInPeriodCredit() )?"":FMT.format(account.getInPeriodCredit());
			Paragraph ipcP = new Paragraph(8,ipc,bold?BODY_FONT_BOLD:BODY_FONT);
			ipcP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell ipcCell = new PdfPCell( );
			ipcCell.addElement(ipcP);
			ipcCell.setBorder(0);
			table.addCell(ipcCell);

			
			String apd = AonMathUtils.isZero( account.getAfterPeriodDebitBalance() )?"":FMT.format(account.getAfterPeriodDebitBalance());
			Paragraph apdP = new Paragraph(8,apd,bold?BODY_FONT_BOLD:BODY_FONT);
			apdP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell apdCell = new PdfPCell( );
			apdCell.addElement(apdP);
			apdCell.setBorder(0);
			table.addCell(apdCell);
			
			String apc = AonMathUtils.isZero( account.getAfterPeriodUnpaidBalance() )?"":FMT.format(account.getAfterPeriodUnpaidBalance());
			Paragraph apcP = new Paragraph(8,apc,bold?BODY_FONT_BOLD:BODY_FONT);
			apcP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell apcCell = new PdfPCell( );
			apcCell.addElement(apcP);
			apcCell.setBorder(0);
			table.addCell(apcCell);
		}
	}

	
}
