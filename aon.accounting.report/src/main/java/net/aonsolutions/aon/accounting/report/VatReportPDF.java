package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class VatReportPDF implements IAccountReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	
	private final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	public void printReportPDF(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		printReport(outputStream, params);
	}

	public void printReport(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		Occam occam = new Occam()
			.setDomain(domainId)
			.setDomainName(domainName)
			.setUser(user);
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		ReportMetadata metadata = params.getReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(toString(params))
			;
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		document.setMargins(30, 30, 50, 30);
		
		float[] widths = new float[]{8,8,8,40,60,110,40,70,180,60,30,60,30,60};
		PdfPTable table = new PdfPTable(widths.length);
		table.setTotalWidth(widths);
		table.setLockedWidth(true);
		
		PDFAction action = new PDFAction(table);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		writer.setPageEvent(new AccountReportPdfPageEvent(metadata) {
			@Override
			public void onStartPage(PdfWriter writer, Document document) {
				super.onStartPage(writer, document);
				action.setOldId(null);
			}
			
		});
		document.open();
		
		
		table.addCell( getHeaderCell("T") );
		table.addCell( getHeaderCell("S") );
		table.addCell( getHeaderCell("I") );
		table.addCell( getHeaderCell("Fec. IVA") );
		table.addCell( getHeaderCell("Nº Docume.") );
		table.addCell( getHeaderCell("Nº factura") );
		table.addCell( getHeaderCell("Fec. Fra.") );
		table.addCell( getHeaderCell("NIF") );
		table.addCell( getHeaderCell("Razón Social") );
		table.addCell( getHeaderCell("Base Imp.",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("Porc.",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("Cuota",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("R.E.",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("Cuo. RE",Element.ALIGN_RIGHT) );

		table.setHeaderRows(1);
	    
		Stream<VatContext> stream = FISCAL.getVatContext(occam, params);
		stream.forEach(action);
		action.paintTotals();
		stream.close();
		
		document.add(table);
		document.close();
	}
	
	private PdfPCell getHeaderCell(String text) {
		return getHeaderCell(text,Element.ALIGN_LEFT);
	}

	private PdfPCell getHeaderCell(String text, int align) {
		Paragraph headerParagrph = new Paragraph(8,text,BODY_FONT_BOLD);
		headerParagrph.setAlignment( align );
		PdfPCell headerCell = new PdfPCell();
		headerCell.setBorder(0);
		headerCell.setBorderWidthBottom(1);
		headerCell.addElement(headerParagrph);
		return headerCell;
	}

	private String toString(AccountingReportParams params) {
		StringBuilder buf = new StringBuilder();
		if (params.getFromDate() != null) {
			buf.append(" (Desde:");
			buf.append(dateFormatter.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(" (Hasta:");
			buf.append(dateFormatter.format(params.getToDate()));
			buf.append(")");
		}
		if (params.getRegistry() != null) {
			buf.append(" (Titular:");
			buf.append(params.getRegistry());
			buf.append(")");
		}
		if (params.getActivity() != null) {
			buf.append(" (Actividad:");
			buf.append(params.getActivity());
			buf.append(")");
		}
		if (params.getOutput() != null) {
			buf.append(params.getOutput().booleanValue() ? " (Emitidas)" : " (Recibidas)");
		}
		if (params.getVatSummaryType() != null) {
			buf.append(" (");
			buf.append(params.getVatSummaryType().getDescription());
			buf.append(")");
		}
		if (params.getPercent() != null) {
			buf.append(" (Porc:");
			buf.append(params.getPercent());
			buf.append(")");
		}
		if (params.getSurcharge() != null) {
			buf.append(params.getSurcharge().booleanValue() ? " (Rec.Equiv. SI)" : " (Rec.Equiv. NO)");
		}

		if (params.getFarmerRegime() != null) {
			buf.append(params.getFarmerRegime().booleanValue() ? " (Reg.Agric. SI)" : " (Reg.Agric. NO)");
		}
		if (params.getAccrualRegime() != null) {
			buf.append(params.getAccrualRegime().booleanValue() ? " (Crit.Caja. SI)" : " (Crit.Caja. NO)");
		}
		if (params.getInvestment() != null) {
			buf.append(params.getInvestment().booleanValue() ? " (Bien Inv.)" : " (Bien Corr.)");
		}
		if (params.getService() != null) {
			buf.append(params.getService().booleanValue() ? " (Serv. SI)" : " (Serv. NO)");
		}
		return buf.length() > 0 ? buf.insert(0, "Filtro:").toString() : "";
	}

	private class PDFAction implements Consumer<VatContext>{

		private PdfPTable table;
		private Integer oldId;
		
		private double sumBase;
		private double sumQuota;
		private double sumSurcharge;
		
		public PDFAction(PdfPTable table) {
			this.table = table;
		}
		
		public void setOldId(Integer oldId) {
			this.oldId = oldId;
		}

		@Override
		public void accept(VatContext vat) {
			if (!AonNumberUtils.equals(this.oldId, vat.getInvoice())) {
				setOldId( vat.getInvoice() );
				table.addCell(getBodyCell( vat.isInput()?"S":"R"));
				table.addCell(getBodyCell( vat.isService()?"X":""));
				table.addCell(getBodyCell( vat.isInvestment()?"X":""));
				table.addCell(getBodyCell(dateFormatter.format(vat.getTaxDate())) );
				table.addCell(getBodyCell(vat.getDocumentNumber()));
				table.addCell(getBodyCell(vat.getReferenceCode()));
				table.addCell(getBodyCell(dateFormatter.format(vat.getIssueDate())) );
				table.addCell(getBodyCell(vat.getRegistryDocument()));
				table.addCell(getBodyCell(vat.getRegistryName()));			
			} else {
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
			}
			table.addCell(getBodyCell(FMT.format(vat.getBase()),Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(FMT.format(vat.getPercentage()) + "%",Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(FMT.format(vat.getQuota()),Element.ALIGN_RIGHT));
			table.addCell(getBodyCell(AonMathUtils.isZero(vat.getSurchargePercent())?"":FMT.format(vat.getSurchargePercent()) + "%",Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(AonMathUtils.isZero(vat.getSurchargeQuota())?"":FMT.format(vat.getSurchargeQuota()),Element.ALIGN_RIGHT));
			
			
			sumBase += vat.getBase(); 
			sumQuota += vat.getQuota();
			sumSurcharge += vat.getSurchargeQuota();

		}
		
		private void paintTotals() {
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getTotalCell(FMT.format(sumBase),Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getTotalCell(FMT.format(sumQuota),Element.ALIGN_RIGHT));
			table.addCell(getBodyCell(AonStringUtils.SPACE));
			table.addCell(getTotalCell(FMT.format(sumSurcharge),Element.ALIGN_RIGHT));
		}

		private PdfPCell getTotalCell(String text, int align) {
			Paragraph headerParagrph = new Paragraph(8,text,BODY_FONT_BOLD);
			headerParagrph.setAlignment( align );
			PdfPCell headerCell = new PdfPCell();
			headerCell.setBorder(0);
			headerCell.setBorderWidthTop(1);
			headerCell.addElement(headerParagrph);
			return headerCell;
		}
		
		private PdfPCell getBodyCell(String t) {
			return getBodyCell(t,Element.ALIGN_LEFT);
		}
		
		private PdfPCell getBodyCell(String text, int align) {
			Paragraph body = new Paragraph(8,text,BODY_FONT);
			body .setAlignment( align );
			PdfPCell bodyCell = new PdfPCell( );
			bodyCell.addElement(body);
			bodyCell.setBorder(0);
			return bodyCell; 
		}

	}

}
