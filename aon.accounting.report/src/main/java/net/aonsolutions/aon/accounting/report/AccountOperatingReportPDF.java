package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountOperatingReportPDF {
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00"); //;(#,##0.00)
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static Font HEADER_FONT_0 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font HEADER_FONT_1 = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static Font HEADER_FONT_2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static Font BODY_RED_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.RED);
	private static Font BODY_RED_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.RED);

	public void printOperatingReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		AccountOperatingReport report = ACCOUNTING.getAccountOperatingReport(domainName, user, domainId, params );
		
		ReportMetadata metadata = new ReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(report,params))
				.setTitle("CUENTA DE EXPLOTACI\u00D3N");
		
		Document document = new Document();
		// document.setPageSize(PageSize.A4.rotate());
		document.setPageSize(report.getIntervals().size()<3?PageSize.A4:PageSize.A4.rotate());
		document.setMargins(36, 36, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new ReportPageEvent(metadata));
		document.open();
		
		int columns = 0;
		if (params.isByMonth()) {
			columns = 2 + report.getIntervals().size();
		} else {
			if (report.showRatios()) {
				columns = 7;
			} else {
				if (report.showIncreasePercent()) {
					columns = 2 + 2 + ((report.getIntervals().size() -1) * 3); 
				} else {
					columns = 2 + (report.getIntervals().size() * 2); 
				}
			}
		}

		PdfPTable table = new PdfPTable(columns);
		
		float[] widths = new float[columns];
		widths[0] = 45f;
		widths[1] = getDescriptionColumnWidth(report);
		if (report.showRatios()) {
			widths[2] = 60f;
			widths[3] = 60f;
			widths[4] = 60f;
			widths[5] = 60f;
			widths[6] = 60f;
		} else {
			MutableInt col = new MutableInt(2);
			MutableBoolean first = new MutableBoolean(true);
			report.getIntervals().forEach(inter -> {
				if (params.isByMonth()) {
					widths[col.getValue()] = 58f;
					col.add(1);
				} else {
					widths[col.getValue()] = 60f;
					col.add(1);
					widths[col.getValue()] = 60f;
					col.add(1);
					if (!first.getValue() && report.showIncreasePercent()) {
						widths[col.getValue()] = 60f;
						col.add(1);
					}
					first.setValue(false);
				}
			});
		}
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		
		if (!params.isByMonth()) {
			Paragraph voidParagrph = new Paragraph(8,"",BODY_FONT_BOLD);
			PdfPCell voidCell = new PdfPCell();
			voidCell.setBorder(0);
			voidCell.addElement(voidParagrph);
			voidCell.setColspan(2);
			table.addCell( voidCell );

			for (DateInterval interval : report.getIntervals()) {
				Paragraph nameParagrph = new Paragraph(8, interval.getName(),BODY_FONT_BOLD);
				nameParagrph.setAlignment( Element.ALIGN_CENTER);
				PdfPCell nameCell = new PdfPCell();
				nameCell.setColspan(report.showRatios()?5: report.showIncreasePercent()?3:2);
				nameCell.setBorder(0);
				nameCell.setBorderWidthTop(1);
				nameCell.addElement(nameParagrph);
				table.addCell( nameCell );
			}
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
		for ( DateInterval interval : report.getIntervals()) {
			if (params.isByMonth()) {
				Paragraph debitParagrph = new Paragraph(8, interval.getName(),BODY_FONT_BOLD);
				debitParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell debitCell = new PdfPCell();
				debitCell.setBorder(0);
				debitCell.setBorderWidthBottom(1);
				debitCell.addElement(debitParagrph);
				table.addCell( debitCell );
			} else {
				Paragraph debitParagrph = new Paragraph(8,"S.Deudor",BODY_FONT_BOLD);
				debitParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell debitCell = new PdfPCell();
				debitCell.setBorder(0);
				debitCell.setBorderWidthBottom(1);
				debitCell.addElement(debitParagrph);
				table.addCell( debitCell );
				
				Paragraph creditParagrph = new Paragraph(8,"S.Acreed.",BODY_FONT_BOLD);
				creditParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell creditCell = new PdfPCell();
				creditCell.setBorder(0);
				creditCell.setBorderWidthBottom(1);
				creditCell.addElement(creditParagrph);
				table.addCell( creditCell );
			}
			
			
			if (report.showRatios()) {
				Paragraph sVtaParagrph = new Paragraph(8,"% S/Vta.",BODY_FONT_BOLD);
				sVtaParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell sVtaCell = new PdfPCell();
				sVtaCell.setBorder(0);
				sVtaCell.setBorderWidthBottom(1);
				sVtaCell.addElement(sVtaParagrph);
				table.addCell( sVtaCell );
	
				Paragraph sComParagrph = new Paragraph(8,"% S/Com.",BODY_FONT_BOLD);
				sComParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell sComCell = new PdfPCell();
				sComCell.setBorder(0);
				sComCell.setBorderWidthBottom(1);
				sComCell.addElement(sComParagrph);
				table.addCell( sComCell );
	
				Paragraph sGstParagrph = new Paragraph(8,"% S/Gst.",BODY_FONT_BOLD);
				sGstParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell sGstCell = new PdfPCell();
				sGstCell.setBorder(0);
				sGstCell.setBorderWidthBottom(1);
				sGstCell.addElement(sGstParagrph);
				table.addCell( sGstCell );
			}
			if (report.showIncreasePercent() && i > 0) {
				Paragraph incrmParagrph = new Paragraph(8,"% Incrm.",BODY_FONT_BOLD);
				incrmParagrph.setAlignment( Element.ALIGN_RIGHT);
				PdfPCell incrmCell = new PdfPCell();
				incrmCell.setBorder(0);
				incrmCell.setBorderWidthBottom(1);
				incrmCell.addElement(incrmParagrph);
				table.addCell( incrmCell );
			}
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

	private float getDescriptionColumnWidth(AccountOperatingReport report) {
		if (report.getParams().isByMonth()) {
			return 20f;	
		}
		if (report.showRatios()) {
			return 200f;	
		}
		int size = report.getIntervals().size();
		return (size<3?500f:750f) - 120 - ((size-1) * (report.showIncreasePercent()?3:2) * 60);
	}

	private void concat(StringBuffer buf, String string) {
		if (buf.length() > 0) {
			buf.append(", ");
		}
		buf.append(string);
	}
	
	private String getFilterDescription(AccountOperatingReport report, AccountingReportParams params) {
		StringBuffer buf = new StringBuffer();
		if (report.getSelectedPeriod() != null) {
			concat(buf, "Ejr.: " + report.getSelectedPeriod().getName() );
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

	
	private class PDFAction implements Consumer<AccountOperatingAccount>{
		
		private AccountOperatingReport report;
		private PdfPTable table;
		public PDFAction(AccountOperatingReport report, PdfPTable table) {
			this.report = report;
			this.table = table;
		}
		
		@Override
		public void accept(AccountOperatingAccount account) {
			boolean title = account.getId() == null;
			
			if (report.getParams().isByMonth()) {
				String description =  title || AonStringUtils.isBlank( account.getCode() ) 
						?account.getDescription()
						:account.getCode() + " - " + account.getDescription(); 
				Chunk descChunk = new Chunk(description,title?BODY_FONT_BOLD:BODY_FONT);
				while (descChunk.getWidthPoint() > 60f) {
					description = AonStringUtils.abbreviate(description,description.length() - 1);
					descChunk = new Chunk(description,title?BODY_FONT_BOLD:BODY_FONT);
				}
				Paragraph descriptionParagraph = new Paragraph(8,descChunk);
				if (title) descriptionParagraph.setAlignment( Element.ALIGN_RIGHT );
				PdfPCell descriptionCell = new PdfPCell();
				descriptionCell.addElement(descriptionParagraph);
				descriptionCell.setBorder(0);
				descriptionCell.setColspan(2);
				descriptionCell.setNoWrap(true);
				table.addCell(descriptionCell);
			} else {
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
			}
			GrayColor gray = new GrayColor(0.95f);
			
			int i = 0;
			for (DateInterval interval : report.getIntervals()) {
				AccountOperatingStatement data = report.get(account.getCode(), interval);
				if (!report.getParams().isByMonth()) {
					String debit = (data == null || AonMathUtils.isZero( data.getDebitBalance()))?"":FMT.format(data.getDebitBalance());
					Paragraph debitP = new Paragraph(8,debit,title?BODY_FONT_BOLD:BODY_FONT);
					debitP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell debitCell = new PdfPCell( );
					debitCell.addElement(debitP);
					debitCell.setBorder(0);
					table.addCell(debitCell);
					
					String credit = (data == null || AonMathUtils.isZero( data.getUnpaidBalance()))?"":FMT.format(data.getUnpaidBalance());
					Paragraph creditP = new Paragraph(8,credit,title?BODY_FONT_BOLD:BODY_FONT);
					creditP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell creditCell = new PdfPCell( );
					creditCell.addElement(creditP);
					creditCell.setBorder(0);
					table.addCell(creditCell);
				} else {
					double saldo = data == null? 0 : AonMathUtils.round( data.getUnpaidBalance() - data.getDebitBalance() );
					String saldoStr = AonMathUtils.isZero( saldo )?"":FMT.format(saldo);
					Paragraph saldoP = new Paragraph(8,saldoStr,title?BODY_FONT_BOLD:BODY_FONT);
					saldoP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell saldoCell = new PdfPCell( );
					saldoCell.addElement(saldoP);
					saldoCell.setBorder(0);
					table.addCell(saldoCell);
				}
				
				if (report.showRatios()) {
					String salesRatio = (data == null || AonMathUtils.isZero( data.getSalesRatio()))?"":FMT.format(data.getSalesRatio());
					String salesRatioText = AonStringUtils.isNotBlank(salesRatio)?salesRatio + AonStringUtils.PERCENT:AonStringUtils.SPACE;
					Paragraph salesRatioP = new Paragraph(8,salesRatioText,title?BODY_FONT_BOLD:BODY_FONT);
					salesRatioP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell salesRatioCell = new PdfPCell( );
					salesRatioCell.addElement(salesRatioP);
					salesRatioCell.setBorder(0);
					salesRatioCell.setBackgroundColor(gray);
					table.addCell(salesRatioCell);
					
					String purchaseRatio = (data == null || AonMathUtils.isZero( data.getPurchasesRatio()))?"":FMT.format(data.getPurchasesRatio());
					String purchaseRatioText = AonStringUtils.isNotBlank(purchaseRatio)?purchaseRatio + AonStringUtils.PERCENT:AonStringUtils.SPACE;
					Paragraph purchaseRatioP = new Paragraph(8,purchaseRatioText,title?BODY_FONT_BOLD:BODY_FONT);
					purchaseRatioP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell purchaseRatioCell = new PdfPCell( );
					purchaseRatioCell.addElement(purchaseRatioP);
					purchaseRatioCell.setBorder(0);
					purchaseRatioCell.setBackgroundColor(gray);
					table.addCell(purchaseRatioCell);

					String expensesRatio = (data == null || AonMathUtils.isZero( data.getExpensesRatio()))?"":FMT.format(data.getExpensesRatio());
					String expensesRatioText = AonStringUtils.isNotBlank(expensesRatio)?expensesRatio + AonStringUtils.PERCENT:AonStringUtils.SPACE;
					Paragraph expensesRatioP = new Paragraph(8,expensesRatioText,title?BODY_FONT_BOLD:BODY_FONT);
					expensesRatioP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell expensesRatioCell = new PdfPCell( );
					expensesRatioCell.addElement(expensesRatioP);
					expensesRatioCell.setBorder(0);
					expensesRatioCell.setBackgroundColor(gray);
					table.addCell(expensesRatioCell);
				}
				if (report.showIncreasePercent() && i > 0) {
					String incrmRatio = (data == null || AonMathUtils.isZero( data.getIncreasePercent()))?"":FMT.format(data.getIncreasePercent());
					String incrmRatioText = AonStringUtils.isNotBlank(incrmRatio)?incrmRatio + AonStringUtils.PERCENT:AonStringUtils.SPACE;
					Font font = null;
					if ( data != null && data.getIncreasePercent() < 0 ) {
						font = title?BODY_RED_FONT_BOLD:BODY_RED_FONT;
					} else {
						font = title?BODY_FONT_BOLD:BODY_FONT;
					}
					Paragraph incrmRatioP = new Paragraph(8,incrmRatioText, font);
					incrmRatioP .setAlignment( Element.ALIGN_RIGHT );
					PdfPCell incrmRatioCell = new PdfPCell( );
					incrmRatioCell.addElement(incrmRatioP);
					incrmRatioCell.setBorder(0);
					incrmRatioCell.setBackgroundColor(gray);
					table.addCell(incrmRatioCell);
				}
				i++;
			}
		}

		
	}
	
}
