package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.BaseColor;
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

public class AccountLedgerReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static final DecimalFormat FMT_INT = new DecimalFormat("#,##0");
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printLedgerReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		AONContext ctx = AONContext.getAONContext(domainName, domainId, user);
		if (params.getPeriod() != null) {
			params.setSelectedPeriod(AccountPeriodDAO.getPeriod(ctx, params.getPeriod()));
		}
		if (params.getAccount() != null && params.getAccount().getId() != null) {
			params.setSelectedAccount(AccountDAO.get(ctx, params.getAccount().getId()));
		}
		if (params.getActivity() != null) {
			params.setSelectedActivity(CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()));
		}
		ctx.finalize();
		
		ReportMetadata metadata = new ReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(params))
				.setTitle("LISTADO MAYOR DE CUENTAS");
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
//		writer.setPageEvent(new ReportPageEvent(metadata));
		writer.setPageEvent(new AccountReportPdfPageEvent(metadata));
		document.open();
		
		int columns = 9; 
		PdfPTable table = new PdfPTable(columns);
		
		float[] widths = new float[]{35,45,100,65,65,65,65,60,80};
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		Paragraph journalParagrph = new Paragraph(8,"Diario",BODY_FONT_BOLD);
		journalParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell journalCell = new PdfPCell();
		journalCell.setBorder(0);
		journalCell.setBorderWidthBottom(1);
		journalCell.addElement(journalParagrph);
		table.addCell( journalCell );
		
		Paragraph dateParagrph = new Paragraph(8,"Fecha",BODY_FONT_BOLD);
		dateParagrph.setAlignment( Element.ALIGN_CENTER);
		PdfPCell dateCell = new PdfPCell();
		dateCell.setBorder(0);
		dateCell.setBorderWidthBottom(1);
		dateCell.addElement(dateParagrph);
		table.addCell( dateCell );

		Paragraph conceptParagrph = new Paragraph(8,"Concepto",BODY_FONT_BOLD);
		conceptParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell conceptCell = new PdfPCell();
		conceptCell.setBorder(0);
		conceptCell.setBorderWidthBottom(1);
		conceptCell.addElement(conceptParagrph);
		table.addCell( conceptCell );

		Paragraph debitParagrph = new Paragraph(8,"Debe",BODY_FONT_BOLD);
		debitParagrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell debitCell = new PdfPCell();
		debitCell.setBorder(0);
		debitCell.setBorderWidthBottom(1);
		debitCell.addElement(debitParagrph);
		table.addCell( debitCell );

		Paragraph creditParagrph = new Paragraph(8,"Haber",BODY_FONT_BOLD);
		creditParagrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell creditCell = new PdfPCell();
		creditCell.setBorder(0);
		creditCell.setBorderWidthBottom(1);
		creditCell.addElement(creditParagrph);
		table.addCell( creditCell );

		Paragraph debitBalanceParagrph = new Paragraph(8,"S. Deudor",BODY_FONT_BOLD);
		debitBalanceParagrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell debitBalanceCell = new PdfPCell();
		debitBalanceCell.setBorder(0);
		debitBalanceCell.setBorderWidthBottom(1);
		debitBalanceCell.addElement(debitBalanceParagrph);
		table.addCell( debitBalanceCell );

		Paragraph unpaidBalanceParagrph = new Paragraph(8,"S. Acreedor",BODY_FONT_BOLD);
		unpaidBalanceParagrph.setAlignment( Element.ALIGN_RIGHT);
		PdfPCell unpaidBalanceCell = new PdfPCell();
		unpaidBalanceCell.setBorder(0);
		unpaidBalanceCell.setBorderWidthBottom(1);
		unpaidBalanceCell.addElement(unpaidBalanceParagrph);
		table.addCell( unpaidBalanceCell );

		Paragraph balancingAccountParagrph = new Paragraph(8,"Contrapart.",BODY_FONT_BOLD);
		balancingAccountParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell balancingAccountCell = new PdfPCell();
		balancingAccountCell.setBorder(0);
		balancingAccountCell.setBorderWidthBottom(1);
		balancingAccountCell.addElement(balancingAccountParagrph);
		table.addCell( balancingAccountCell );

		Paragraph documentParagrph = new Paragraph(8,"N\u00BA Documento",BODY_FONT_BOLD);
		documentParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell documentAccountCell = new PdfPCell();
		documentAccountCell.setBorder(0);
		documentAccountCell.setBorderWidthBottom(1);
		documentAccountCell.addElement(documentParagrph);
		table.addCell( documentAccountCell );

		table.setHeaderRows(1);
	    
		Stream<FlatAccountEntryDetail> stream = ACCOUNTING.getLedgerStream(domainName, domainId, user, params,0,Integer.MAX_VALUE);
		PDFAction action = new PDFAction(table); 
		stream.forEach(action);
		stream.close();
	    
		document.add(table);
		document.close();
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

	private class PDFAction implements Consumer<FlatAccountEntryDetail>{

		private PdfPTable table;
		private Integer oldId;
		
		public PDFAction(PdfPTable table) {
			this.table = table;
		}
		
		@Override
		public void accept(FlatAccountEntryDetail entry) {
			if (!AonNumberUtils.equals(oldId, entry.getAccount())) {
				paintEntryHeader( entry );
			}
			Paragraph code = new Paragraph(8,entry.getJournal() == null || AonMathUtils.isZero(entry.getJournal())?""
					:FMT_INT.format(entry.getJournal()),BODY_FONT);
			PdfPCell codeCell = new PdfPCell();
			codeCell.addElement(code);
			codeCell.setBorder(0);
			table.addCell(codeCell);
			
			Paragraph description = new Paragraph(8,DATE_FORMATTER.format(entry.getEntryDate()),BODY_FONT);
			PdfPCell descriptionCell = new PdfPCell();
			descriptionCell.addElement(description);
			descriptionCell.setBorder(0);
			table.addCell(descriptionCell);

			String conc = entry.getConcept();
			Chunk conceptChunk = new Chunk(entry.getConcept(),BODY_FONT);
			while (conceptChunk.getWidthPoint() > 100f) {
				conc = AonStringUtils.abbreviate(entry.getConcept(),conc.length() - 10); 
				conceptChunk = new Chunk(conc,BODY_FONT);
			}
			Paragraph concept = new Paragraph(8,conceptChunk);
			PdfPCell conceptCell = new PdfPCell();
			conceptCell.addElement(concept);
			conceptCell.setBorder(0);
			table.addCell(conceptCell);
			
			String debit = AonMathUtils.isZero( entry.getDebit())?"":FMT.format(entry.getDebit());
			Paragraph debitP = new Paragraph(8,debit,BODY_FONT);
			debitP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell debitCell = new PdfPCell( );
			debitCell.addElement(debitP);
			debitCell.setBorder(0);
			table.addCell(debitCell);
			
			String credit = AonMathUtils.isZero( entry.getCredit())?"":FMT.format(entry.getCredit());
			Paragraph creditP = new Paragraph(8,credit,BODY_FONT);
			creditP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell creditCell = new PdfPCell( );
			creditCell.addElement(creditP);
			creditCell.setBorder(0);
			table.addCell(creditCell);

			double db = AonMathUtils.round( entry.getInitialDebitBalance() + entry.getDebitBalance() - entry.getUnpaidBalance()); 
			String debitBalance = AonMathUtils.isLessThanZero( db )?"":FMT.format(db);
			Paragraph debitBalanceP = new Paragraph(8,debitBalance,BODY_FONT);
			debitBalanceP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell debitBalanceCell = new PdfPCell( );
			debitBalanceCell.addElement(debitBalanceP);
			debitBalanceCell.setBorder(0);
			table.addCell(debitBalanceCell);
			
			double ub = AonMathUtils.round( entry.getInitialUnpaidBalance() + entry.getUnpaidBalance()  - entry.getDebitBalance());
			String creditBalance = AonMathUtils.isLessThanZero( ub )?"":FMT.format(ub);
			Paragraph creditBalanceP = new Paragraph(8,creditBalance,BODY_FONT);
			creditBalanceP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell creditBalanceCell = new PdfPCell( );
			creditBalanceCell.addElement(creditBalanceP);
			creditBalanceCell.setBorder(0);
			table.addCell(creditBalanceCell);

			Paragraph balancingAccount = new Paragraph(8,entry.getBalancingAccountCode(),BODY_FONT);
			balancingAccount.setAlignment( Element.ALIGN_CENTER);
			PdfPCell balancingAccountCell = new PdfPCell();
			balancingAccountCell.addElement(balancingAccount);
			balancingAccountCell.setBorder(0);
			table.addCell(balancingAccountCell);

			Paragraph documentAccount = new Paragraph(8,entry.getDocumentNumber(),BODY_FONT);
			PdfPCell documentCell = new PdfPCell();
			documentCell.addElement(documentAccount);
			documentCell.setBorder(0);
			table.addCell(documentCell);
}

		private void paintEntryHeader(FlatAccountEntryDetail entry) {
			oldId = entry.getAccount();
			String header = entry.getAccountCode() + " - " + entry.getAccountDescription();
			Paragraph headerParagrph = new Paragraph(8,header,BODY_FONT_BOLD);
			headerParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell headerCell = new PdfPCell();
			headerCell.setBorder(0);
			headerCell.setBorderWidthBottom(1);
			headerCell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
			headerCell.addElement(headerParagrph);
			headerCell.setColspan(9);
			table.addCell( headerCell );
		}
		
	}
	
}
