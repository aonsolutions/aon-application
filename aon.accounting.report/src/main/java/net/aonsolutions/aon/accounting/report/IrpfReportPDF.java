package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
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

public class IrpfReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);

	public void printReport(OutputStream outputStream, IRPFParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		ReportMetadata metadata = new ReportMetadata()
				.setCompanyName(companyName)
				.setTitle("Listado IRPF")
				.setFilterDescription(toString(params))
			;
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		document.setMargins(30, 30, 50, 30);
		
		float[] widths = new float[]{30,70,40,60,110,40,70,180,60,30,60};
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
		table.addCell( getHeaderCell("Tipo") );
		table.addCell( getHeaderCell("Fec. IVA") );
		table.addCell( getHeaderCell("Nº Docume.") );
		table.addCell( getHeaderCell("Nº factura") );
		table.addCell( getHeaderCell("Fec. Fra.") );
		table.addCell( getHeaderCell("NIF") );
		table.addCell( getHeaderCell("Razón Social") );
		table.addCell( getHeaderCell("Base Imp.",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("Porc.",Element.ALIGN_RIGHT) );
		table.addCell( getHeaderCell("Cuota",Element.ALIGN_RIGHT) );

		table.setHeaderRows(1);
	    
		Stream<IrpfBreakdown> stream = FISCAL.getIrpfBreakdown(domainName, user, domainId, params);
		stream.forEach(action);
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

	private String toString(IRPFParams params) {
		StringBuffer buf = new StringBuffer();
		if (params.getFromDate() != null) {
			buf.append(" (Desde:");
			buf.append(DATE_FORMATTER.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(" (Hasta:");
			buf.append(DATE_FORMATTER.format(params.getToDate()));
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
			buf.append(params.getOutput() ? " (Emitidas)" : " (Recibidas)");
		}
		if (params.getWithholdingType() != null) {
			buf.append(" (");
			buf.append(params.getWithholdingType().getDescription());
			buf.append(")");
		}
		if (params.getPercent() != null) {
			buf.append(" (Porc:");
			buf.append(params.getPercent());
			buf.append(")");
		}
		if (params.getAccrualRegime() != null) {
			buf.append(params.getAccrualRegime() ? " (Crit.Caja. SI)" : " (Crit.Caja. NO)");
		}
		if (params.getInvestment() != null) {
			buf.append(params.getInvestment() ? " (Bien Inv.)" : " (Bien Corr.)");
		}
		if (params.getService() != null) {
			buf.append(params.getService() ? " (Serv. SI)" : " (Serv. NO)");
		}
		return buf.length() > 0 ? buf.insert(0, "Filtro:").toString() : "";
	}

	private class PDFAction implements Consumer<IrpfBreakdown>{

		private PdfPTable table;
		private Integer oldId;
		
		public PDFAction(PdfPTable table) {
			this.table = table;
		}
		
		public void setOldId(Integer oldId) {
			this.oldId = oldId;
		}

		@Override
		public void accept(IrpfBreakdown irpf) {
			if (!AonNumberUtils.equals(this.oldId, irpf.getInvoice())) {
				setOldId( irpf.getInvoice() );
				table.addCell(getBodyCell( irpf.isInput()?"Recb.":"Emit."));
				table.addCell(getBodyCell( irpf.getWithholdingType()==null?"":irpf.getWithholdingType().getDescription()));
				table.addCell(getBodyCell(DATE_FORMATTER.format(irpf.getTaxDate())) );
				table.addCell(getBodyCell(irpf.getDocumentNumber()));
				table.addCell(getBodyCell(irpf.getReferenceCode()));
				table.addCell(getBodyCell(DATE_FORMATTER.format(irpf.getIssueDate())) );
				table.addCell(getBodyCell(irpf.getRegistryDocument()));
				table.addCell(getBodyCell(irpf.getName()));			
			} else {
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
				table.addCell(getBodyCell(AonStringUtils.SPACE));
			}
			table.addCell(getBodyCell(FMT.format(irpf.getBase()),Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(FMT.format(irpf.getPercent()) + "%",Element.ALIGN_RIGHT));			
			table.addCell(getBodyCell(FMT.format(irpf.getQuota()),Element.ALIGN_RIGHT));
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
