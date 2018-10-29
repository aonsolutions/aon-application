package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.ReportMetadata;
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

public class AccountBalanceReportPDF {
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static SimpleDateFormat FORMATTER_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static Font HEADER_FONT_0 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font HEADER_FONT_1 = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static Font HEADER_FONT_2 = new Font(Font.FontFamily.HELVETICA, 6);
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printBalanceReport(OutputStream outputStream, AccountBalanceReport report)
			throws DocumentException {
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(36, 36, 50, 20);
		
		float pageWidth = document.getPageSize().getWidth(); 
		float w = pageWidth - document.leftMargin() - document.rightMargin();
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new ReportPageEvent(report.getMetadata()));
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
			
			canvas.setColorStroke(BaseColor.BLACK);
	        canvas.moveTo(document.leftMargin(), document.top() + 5);
	        canvas.lineTo(pageWidth - document.rightMargin(), document.top() + 5);
	        canvas.closePathStroke();
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte canvas = writer.getDirectContent();
			
			float pageWidth = document.getPageSize().getWidth(); 

			canvas.setColorStroke(BaseColor.BLACK);
	        canvas.moveTo(document.leftMargin(), document.bottom() - 2);
	        canvas.lineTo(pageWidth - document.rightMargin(), document.bottom() - 2);
	        canvas.closePathStroke();
	        
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT
					,new Phrase(FORMATTER_TIME.format( new Date()), HEADER_FONT_1)
					,document.leftMargin()
					,document.bottom() - 10
					, 0);
			ColumnText.showTextAligned(canvas
					, Element.ALIGN_LEFT
					,new Phrase("P\u00E1g: " + (metadata.getPageOffset() + writer.getPageNumber()), HEADER_FONT_1)
					,(document.getPageSize().getWidth() - document.rightMargin() - 40)
					,document.bottom() - 10
					, 0);
			writer.flush();
		}
	}
	
}
