package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountJournalReportPDF {
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static Font HEADER_FONT_0 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font HEADER_FONT_1 = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static Font HEADER_FONT_2 = new Font(Font.FontFamily.HELVETICA, 6);
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printBalanceReport(OutputStream outputStream, AccountEntryParams params) throws DocumentException {
		
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
		if (params.getAccount() != null) {
			params.setSelectedAccount(AccountDAO.get(ctx, params.getAccount()));
		}
		if (params.getActivity() != null) {
			params.setSelectedActivity(CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()));
		}
		ctx.finalize();
		
		ReportMetadata metadata = new ReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(params))
				.setTitle("LISTADO DIARIO DE MOVIMIENTOS");
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(36, 36, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new ReportPageEvent(metadata));
		document.open();
		
		int columns = 5; 
		PdfPTable table = new PdfPTable(columns);
		
		float[] widths = new float[]{45,175,160,70,70};
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		Paragraph accountParagrph = new Paragraph(8,"Cuenta",BODY_FONT_BOLD);
		accountParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell accountCell = new PdfPCell();
		accountCell.setBorder(0);
		accountCell.setBorderWidthBottom(1);
		accountCell.addElement(accountParagrph);
		table.addCell( accountCell );
		
		Paragraph descriptionParagrph = new Paragraph(8,"Descripci\u00F3n",BODY_FONT_BOLD);
		descriptionParagrph.setAlignment( Element.ALIGN_LEFT);
		PdfPCell descripCell = new PdfPCell();
		descripCell.setBorder(0);
		descripCell.setBorderWidthBottom(1);
		descripCell.addElement(descriptionParagrph);
		table.addCell( descripCell );

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

		table.setHeaderRows(1);
	    
		Stream<FlatAccountEntryDetail> stream = ACCOUNTING.getFlatAccountEntries(domainName, domainId, user, params,0,Integer.MAX_VALUE);
		PDFAction action = new PDFAction(table); 
		stream.forEach(action);
		if (action.hasPendingTotals()) {
			action.paintEntryTotals();
		}
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
	
	private String getFilterDescription(AccountEntryParams params) {
		StringBuffer buf = new StringBuffer();
		if (params.getSelectedPeriod() != null) {
			concat(buf, "Ejr.: " + params.getSelectedPeriod().getName() );
		}
		if (params.getFrom() != null) {
			concat(buf, "Desde: " + DATE_FORMATTER.format(params.getFrom()) );
		}
		if (params.getTo() != null) {
			concat(buf, "Hasta: " + DATE_FORMATTER.format(params.getTo()) );
		}
		if (params.getSelectedActivity() != null) {
			concat(buf, "Act.: " + params.getSelectedActivity().getDescription() );
		}
		if (params.getJournal() != null) {
			concat(buf, "Diario: " + params.getJournal());
		}
		if (params.getType() != null) {
			concat(buf, "Tipo: " + params.getType().getDescription());
		}
		if (params.isConfidential()) {
			concat(buf, "Seg: CONFID.");	
		}
		if (params.getSelectedAccount() != null) {
			concat(buf, "Cta.: " + params.getSelectedAccount().getCode());
		}
		if (params.getDebit() != null) {
			concat(buf, "Debe: " + FMT.format(params.getDebit()));
		}
		if (params.getCredit() != null) {
			concat(buf, "Haber: " + FMT.format(params.getCredit()));
		}
		return buf.toString();
	}

	private class ReportPageEvent extends PdfPageEventHelper {
		
		private ReportMetadata metadata;
		
		private ReportPageEvent(ReportMetadata metadata) {
			this.metadata = metadata;
		}
		
		public void onStartPage(PdfWriter writer, Document document) {
			PdfContentByte canvas = writer.getDirectContent();
			
			float pageWidth = document.getPageSize().getWidth(); 
			float pageHeight = document.getPageSize().getHeight();
			float w = pageWidth - document.leftMargin() - document.rightMargin();
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT
					,new Phrase(this.metadata.getCompanyName(), HEADER_FONT_1)
					,document.leftMargin()
					,pageHeight - 15
					,0);
			
			Rectangle rect = new Rectangle(
					 (w / 2)
					,pageHeight - 10
					,w + document.leftMargin()  
					,pageHeight - 30
			);

			canvas.rectangle(rect);
			Paragraph p = new Paragraph(this.metadata.getFilterDescription(),HEADER_FONT_2);
			ColumnText ct = new ColumnText(canvas);
			ct.setAlignment(Element.ALIGN_RIGHT);
			ct.setLeading(8);
			ct.setSimpleColumn(rect);
			ct.setUseAscender(true);
			ct.addText(p);
	        try {
	            ct.go();
	        } catch (DocumentException e) {
	            throw new ExceptionConverter(e);
	        }
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER
					,new Phrase(this.metadata.getTitle(), HEADER_FONT_0)
					,(pageWidth/2) 
					,pageHeight - 40
					,0);
			
//			canvas.setColorStroke(BaseColor.BLACK);
//	        canvas.moveTo(document.leftMargin(), document.top() + 5);
//	        canvas.lineTo(pageWidth - document.rightMargin(), document.top() + 5);
//	        canvas.closePathStroke();
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte canvas = writer.getDirectContent();
			
			float pageWidth = document.getPageSize().getWidth(); 

			canvas.setColorStroke(BaseColor.BLACK);
	        canvas.moveTo(document.leftMargin(), document.bottom() - 10);
	        canvas.lineTo(pageWidth - document.rightMargin(), document.bottom() - 10);
	        canvas.closePathStroke();
	        
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT
					,new Phrase(TIME_FORMATTER.format( new Date()), HEADER_FONT_1)
					,document.leftMargin()
					,document.bottom() - 20
					, 0);
			ColumnText.showTextAligned(canvas
					, Element.ALIGN_LEFT
					,new Phrase("P\u00E1g: " + (metadata.getPageOffset() + writer.getPageNumber()), HEADER_FONT_1)
					,(document.getPageSize().getWidth() - document.rightMargin() - 40)
					,document.bottom() - 20
					, 0);
			writer.flush();
		}
	}

	
	private class PDFAction implements Consumer<FlatAccountEntryDetail>{

		private PdfPTable table;
		private Integer oldId;
		private double sumDebit = 0.0;
		private double sumCredit = 0.0;
		
		public PDFAction(PdfPTable table) {
			this.table = table;
		}
		
		public boolean hasPendingTotals() {
			return oldId != null;
		}

		@Override
		public void accept(FlatAccountEntryDetail entry) {
			if (!AonNumberUtils.equals(oldId, entry.getEntryId())) {
				if (oldId != null) {
					paintEntryTotals();
				}
				paintEntryHeader( entry );
			}
			sumDebit = AonMathUtils.round(sumDebit + entry.getDebit());
			sumCredit= AonMathUtils.round(sumCredit + entry.getCredit());

			Paragraph code = new Paragraph(8,entry.getAccountCode(),BODY_FONT);
			PdfPCell codeCell = new PdfPCell();
			codeCell.addElement(code);
			codeCell.setBorder(0);
			table.addCell(codeCell);
			
			Paragraph description = new Paragraph(8,entry.getAccountDescription(),BODY_FONT);
			PdfPCell descriptionCell = new PdfPCell();
			descriptionCell.addElement(description);
			descriptionCell.setBorder(0);
			table.addCell(descriptionCell);
			
			Paragraph concept = new Paragraph(8,entry.getConcept(),BODY_FONT);
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
		}

		private void paintEntryHeader(FlatAccountEntryDetail entry) {
			oldId = entry.getEntryId();
			sumDebit = 0.0;
			sumCredit= 0.0;
			
			String header = "Fecha: " + DATE_FORMATTER.format(entry.getEntryDate()) + " N\u00BA diario: " +  entry.getJournal();
			Paragraph headerParagrph = new Paragraph(8,header,BODY_FONT_BOLD);
			headerParagrph.setAlignment( Element.ALIGN_CENTER);
			PdfPCell headerCell = new PdfPCell();
			headerCell.setBorder(0);
			headerCell.setBorderWidthBottom(1);
			headerCell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
			headerCell.addElement(headerParagrph);
			headerCell.setColspan(5);
			table.addCell( headerCell );
		}
		
		private void paintEntryTotals() {
			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(0);
			emptyCell.setBorderWidthTop(1);
			emptyCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
			emptyCell.setColspan(3);
			table.addCell( emptyCell );
			
			String debit = AonMathUtils.isZero( sumDebit)?"":FMT.format(sumDebit);
			Paragraph debitP = new Paragraph(8,debit,BODY_FONT_BOLD);
			debitP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell debitCell = new PdfPCell( );
			debitCell.addElement(debitP);
			debitCell.setBorder(0);
			debitCell.setBorderWidthTop(1);
			debitCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
			table.addCell(debitCell);
			
			String credit = AonMathUtils.isZero( sumCredit)?"":FMT.format(sumCredit);
			Paragraph creditP = new Paragraph(8,credit,BODY_FONT_BOLD);
			creditP .setAlignment( Element.ALIGN_RIGHT );
			PdfPCell creditCell = new PdfPCell( );
			creditCell.addElement(creditP);
			creditCell.setBorder(0);
			creditCell.setBorderWidthTop(1);
			creditCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
			table.addCell(creditCell);
			
		}
		
	}
	
}
