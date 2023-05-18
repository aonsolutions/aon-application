package com.esferalia.aon.gwt.fiscal.server.fiscal.mod193;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class Mod193CertificatePDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");
	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	private static final Font CELL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font CELL_LABEL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	private static final Font CELL_LABEL_SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 6);
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	
	public void printMod193Certificate(OutputStream outputStream, Occam occam, Map<String, RetentionCertificate> certificates) throws DocumentException {
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		
		if (certificates.size()==0) {
			document.add(new Chunk(""));			
		} else {
			for (RetentionCertificate detail : certificates.values()) {
				printMod193CertificateDetail(document, writer, detail);			
			}
		}
		
		document.close();
	}
	
	// Rendimientos del capital mobiliario (IRPF) y de determinadas rentas (IS e IRNR)
	private void printMod193CertificateDetail(Document document, PdfWriter writer, RetentionCertificate detail) throws DocumentException {
		
        // Cabeceras        
        addHeaderBand1(document, "Rendimientos del capital mobiliario y de determinadas rentas", detail.getYear());
        
        // Datos del Perceptor
		addHeaderBand2(document, "Datos del Perceptor", 100, "Apellidos y Nombre", detail.getEmployeeDocument(), detail.getEmployeeName());
		
		// Datos de la persona o entidad pagadora
		addHeaderBand2(document, "Datos de la persona o entidad pagadora", 193, "Apellidos y nombre, denominación o razón social", detail.getEnterpriseDocument(), detail.getEnterpriseName());
		
		// Detalle de las percepciones y retenciones e ingresos a cuenta
		addSubTitleLine(document, "Detalle de las percepciones y de las retenciones e ingresos a cuenta", 315);	
	    addDetailBand(document, "A - Rendimientos o rentas obtenidos por la participación en los fondos propios de cualquier entidad", detail.getProf1());  // Clave A
	    addDetailBand(document, "B - Rendimientos o rentas obtenidos por la cesión a terceros de capitales propios diferentes de los consignados en la clave D", detail.getProf2());  // Clave B
	    addDetailBand(document, "C - Otros rendimientos de capital mobiliario o rentas no incluidos en las claves A, B ó D", detail.getProf3()); // Clave C
	    addDetailBand(document, "D - Rendimientos o rentas obtenidos por la cesión a terceros de capitales propios procedentes de entidades vinculadas cuando el perceptor sea contribuyente del Impuesto sobre la Renta de las Personas Físicas", detail.getProf4(), true); // Clave D
	    
	    // Fecha y Firma
		addFooterBand(document);

		document.newPage();
	}
	
	private void addHeaderBand1(Document document, String title, int year) throws DocumentException {
		PdfPTable table1 = new PdfPTable(1);
		table1.setSpacingBefore(10);
		table1.setWidthPercentage(100);
		table1.setTotalWidth(535); // Total 535
		table1.setLockedWidth(true);
 
		addTitleCell(table1, "Certificado de retenciones e ingresos a cuenta del IRPF sobre determinados rendimientos del capital mobiliario y retenciones e ingresos a cuenta del IS e IRNR (establecimientos permanentes) sobre determinadas rentas", TITLE_FONT_BOLD);
		addEmptyCell(table1);
		addTitleCell(table1, "Datos correspondientes al ejercicio " + year, SUBTITLE_FONT_BOLD);
		
        document.add(table1);		
	}
	
	private void addHeaderBand2(Document document, String title, int titleWidth, String nameHead, String nif, String name) throws DocumentException {
		
		addSubTitleLine(document, title, titleWidth);
		
		PdfPTable table1 = new PdfPTable(5);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);
		table1.setTotalWidth(new float[]{5,100,10,415,5});
		table1.setLockedWidth(true);
		
	    addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "NIF");
		addEmptyCell(table1);
		addLabelCell(table1, nameHead);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 1, Rectangle.LEFT);
		addBodyCell(table1, nif);
		addEmptyCell(table1);
		addBodyCell(table1, name);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyBottomLine(table1); 
	    
		document.add(table1);
		
	}
	
	private void addFooterBand(Document document) throws DocumentException {
		
		addSubTitleLine(document, "Fecha y firma", 90);
		
		PdfPTable table1 = new PdfPTable(11);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,15,245,80,15,20,15,90,15,30,5}); // Total 535
		table1.setLockedWidth(true);
		
		addEmptyLine(table1);
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "Para que conste y sirva de justificante al interesado, en cumplimiento de lo dispuesto en el Reglamento del Impuesto sobre la Renta de las Personas Físicas y del Reglamento del Impuesto sobre Sociedades, se expide la presente", 10, Rectangle.RIGHT);
		addEmptyLine(table1);
		
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCenterCell(table1, "En");
		addEmptyCell(table1, 2, Rectangle.BOX);
		addLabelCenterCell(table1, "a");
		addEmptyCell(table1, 1, Rectangle.BOX);
		addLabelCenterCell(table1, "de");
		addEmptyCell(table1, 1, Rectangle.BOX);
		addLabelCenterCell(table1, "de");
		addEmptyCell(table1, 1, Rectangle.BOX);
		addEmptyCell(table1, 1, Rectangle.RIGHT);
		
		addEmptyLine(table1);
		
		addEmptyCell(table1, 2, Rectangle.LEFT);
		addSmallLabelCell(table1, "Firma y sello de la empresa o entidad pagadora", 1, Rectangle.LEFT + Rectangle.TOP + Rectangle.RIGHT);
		addEmptyCell(table1, 8, Rectangle.RIGHT);
		
		for (int i = 0; i < 8; i++) {
			addEmptyCell(table1, 2, Rectangle.LEFT);
			addEmptyCell(table1, 1, Rectangle.LEFT + Rectangle.RIGHT);
			addEmptyCell(table1, 8, Rectangle.RIGHT);
		}
		
		addEmptyCell(table1, 2, Rectangle.LEFT);
		addSmallLabelCell(table1, "Fdo.: D./D.ª ____________________________________________________________", 1, Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);		
		addSmallLabelCell(table1, "    La presente certificación deberá ser firmada por el retenedor, su apoderado o su representante", 8, Rectangle.RIGHT);
		addEmptyBottomLine(table1);
				
		document.add(table1);
		
	}
	
	private void addDetailBand(Document document, String title, RetentionCertificate detail) throws DocumentException {
		addDetailBand(document, title, detail, false);
	}
	
	private void addDetailBand(Document document, String title, RetentionCertificate detail, boolean lastLine) throws DocumentException {
		
		PdfPTable table1 = new PdfPTable(8);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,5,325,5,90,10,90,5}); // Total 535
		table1.setLockedWidth(true);
		
	    addEmptyLine(table1);		
	    
		addEmptyCell(table1, 1, Rectangle.LEFT);		
		addLabelCell(table1, title, 7, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
		
		// Contraprestaciones dinerarias: base y retencion
		
		addEmptyCell(table1, 4, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Base de las retenciones");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Retenciones");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Contraprestaciones dinerarias");
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getRetention()); 
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
		// Contraprestaciones en especie: base e ingreso a cuenta
	    
		addEmptyCell(table1, 4, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Base de los ingresos a cuenta");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Ingresos a cuenta");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Contraprestaciones en especie");
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindDeposit()); 
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
		// Importe percepciones
	    
		addEmptyCell(table1, 6, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Importe");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
		
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Importe de las percepciones", 3);
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getRefundAmount());
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
		// Importe reducciones
	    
	    addEmptyLine(table1);
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Importe de las reducciones", 3);
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getRefundReduction());
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    if (lastLine) {
	    	addEmptyLine(table1);
	    	addEmptyBottomLine(table1);	    	
	    }	
	    
	    document.add(table1);

	}
	
	private void addEmptyLine(PdfPTable table) {
		addEmptyCell(table, table.getNumberOfColumns(), Rectangle.LEFT + Rectangle.RIGHT);  
	}
	
	private void addEmptyBottomLine(PdfPTable table) {
		addEmptyCell(table, table.getNumberOfColumns(), Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);  
	}
	
	private PdfPCell addTitleCell(PdfPTable table, String title, Font font) {
	    PdfPCell c1 = new PdfPCell(new Phrase(title, font));
	    c1.setBorder(Rectangle.BOX);
	    c1.setBorderColor(BaseColor.BLACK);
	    c1.setBorderWidth(1);
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setPaddingBottom(4);
	    table.addCell(c1);
	    return c1;		
	}
	
	private void addSubTitleLine(Document document, String title, int ancho) throws DocumentException {
		
		PdfPTable table3 = new PdfPTable(3);
		table3.setSpacingBefore(10);
		table3.setWidthPercentage(100);		
		table3.setTotalWidth(new float[]{5, ancho, 535-ancho-5});
		table3.setLockedWidth(true);
		
		addSubTitleCell(table3, "",  1, Rectangle.NO_BORDER);
		addSubTitleCell(table3, title,  2, Rectangle.BOX);
		addSubTitleCell(table3, "",  1, Rectangle.NO_BORDER);
		
		addSubTitleCell(table3, "",  1, Rectangle.TOP + Rectangle.LEFT);	
		addSubTitleCell(table3, "",  1, Rectangle.TOP + Rectangle.RIGHT);
		
		document.add(table3);
		
	}
	
	private PdfPCell addSubTitleCell(PdfPTable table, String title, int rowspan, int border) {
	    PdfPCell c1 = new PdfPCell(new Phrase(title, SUBTITLE_FONT_BOLD));
	    c1.setBorder(border);
	    c1.setBorderColor(BaseColor.BLACK);
	    c1.setBorderWidth(0.5f);
	    c1.setColspan(1);
	    c1.setRowspan(rowspan);
	    c1.setPadding(3);
	    c1.setPaddingLeft(6);
	    c1.setUseAscender(true);
	    table.addCell(c1);
	    return c1;		
	}
	
	private PdfPCell addEmptyCell(PdfPTable table) {
		return addEmptyCell(table, 1);		
	}
	
	private PdfPCell addEmptyCell(PdfPTable table, int colspan) {
	    return addEmptyCell(table, colspan, 0);		
	}
	
	private PdfPCell addEmptyCell(PdfPTable table, int colspan, int border) {
	    PdfPCell c1 = new PdfPCell();
	    c1.setBorder(border);
	    if (border > 0) {
		    c1.setBorderWidth(0.5f);
		    c1.setBorderColor(BaseColor.BLACK);
	    }
	    c1.setColspan(colspan);
	    table.addCell(c1);
	    return c1;		
	}
	
	private PdfPCell addDotLabelCell(PdfPTable table, String text) {		
		return addDotLabelCell(table, text, 1);
	}
	
	private PdfPCell addDotLabelCell(PdfPTable table, String text, int colspan) {		
		return addDotLabelCell(table, text, colspan, 1);
	}
	
	private PdfPCell addDotLabelCell(PdfPTable table, String text, int colspan, int rowspan) {
		
		Chunk ch1 = new Chunk(text, CELL_LABEL_FONT);
		DottedLineSeparator dls = new DottedLineSeparator();
		dls.setGap(2);
		dls.setLineWidth(0.5f);
		Chunk ch2 = new Chunk(dls);
		
		Paragraph p = new Paragraph(6);
		p.add(ch1);
		p.add(ch2);
		
		PdfPCell c1 = new PdfPCell();		
		c1.setColspan(colspan);
		c1.setRowspan(rowspan);
		c1.setBorder(Rectangle.NO_BORDER);
	    c1.setBorderWidth(0);
	    c1.setVerticalAlignment(Element.ALIGN_BOTTOM);	    
	    c1.addElement(p);	    
        table.addCell(c1);
		return c1;
		
	}
	
	private PdfPCell addSmallLabelCell(PdfPTable table, String text, int colspan, int border) {				
		return addLabelCell(table, text, colspan, border, CELL_LABEL_SMALL_FONT, Element.ALIGN_JUSTIFIED, 1);
	}
	
	private PdfPCell addSmallLabelCenterCell(PdfPTable table, String text) {
		return addLabelCell(table, text, 1, Rectangle.NO_BORDER, CELL_LABEL_SMALL_FONT, Element.ALIGN_CENTER, 1);
	}
	
	private PdfPCell addLabelCenterCell(PdfPTable table, String text) {
		return addLabelCell(table, text, 1, Rectangle.NO_BORDER, CELL_LABEL_FONT, Element.ALIGN_CENTER, 1);
	}
	
	private PdfPCell addLabelCell(PdfPTable table, String text) {
		return addLabelCell(table, text, 1, Rectangle.NO_BORDER); 
	}
		
	private PdfPCell addLabelCell(PdfPTable table, String text, int colspan, int border) {
		return addLabelCell(table, text, colspan, border, CELL_LABEL_FONT, Element.ALIGN_LEFT, 1);		
	}
	
	private PdfPCell addLabelCell(PdfPTable table , String text, int colspan, int border, Font font, int horizontalAlignment) {
		return addLabelCell(table, text, colspan, border, font, horizontalAlignment, 1);
	}
	
	private PdfPCell addLabelCell(PdfPTable table, String text, int colspan, int border, Font font, int horizontalAlignment, int rowspan) {
		PdfPCell c1 = new PdfPCell(new Phrase(text, font));		
		c1.setColspan(colspan);
		c1.setRowspan(rowspan);
		c1.setBorder(border);
	    c1.setBorderColor(BaseColor.BLACK);
	    c1.setBorderWidth(0.5f);
	    c1.setHorizontalAlignment(horizontalAlignment);
	    if (font == CELL_LABEL_SMALL_FONT) {
			c1.setPaddingTop(0);
	    }
	    c1.setVerticalAlignment(Element.ALIGN_BOTTOM);  
        table.addCell(c1);
		return c1;
	}
	
	private PdfPCell addBodyCell(PdfPTable table, String text) {
		return addBodyCell(table, text, 1, Rectangle.BOX, Element.ALIGN_LEFT);
	}
	
	private PdfPCell addNumericBodyCell(PdfPTable table, double amount) {
		return addBodyCell(table, AonMathUtils.isZero(amount)?" ":FMT.format(amount), 1, Rectangle.BOX, Element.ALIGN_RIGHT);
	}
	
	private PdfPCell addBodyCell(PdfPTable table, String text, int colspan, int border, int horizontalAlignment) {
		PdfPCell c1 = new PdfPCell(new Phrase(text, BODY_FONT));
		c1.setColspan(colspan);
		c1.setBorder(border);
		c1.setBorderColor(BaseColor.BLACK);
		c1.setBorderWidth(0.5f);
		c1.setPadding(2);
		c1.setPaddingBottom(4);
		c1.setHorizontalAlignment(horizontalAlignment);		
		table.addCell(c1);
		return c1;
	}

////// MAIN ///////
	
	public static void main(String[] args) {
				
		try {
			Map<String, RetentionCertificate> certificates = new HashMap<String, RetentionCertificate>();
			certificates.put("", getTestCertificates());
			
			File file = new File("c:\\tmp\\certificado_mod193.pdf");
			OutputStream stream = new FileOutputStream(file);
			
			Mod193CertificatePDF print = new Mod193CertificatePDF();
			print.printMod193Certificate(stream, null, certificates);
			
			stream.close();
			
		} catch (IOException | DocumentException e) {
			System.out.println(e.getMessage());
		}
		
	}

	private static RetentionCertificate getTestCertificates() {
		RetentionCertificate cert = new RetentionCertificate();
		cert.setId(null);
		cert.setName(null);
		cert.setYear(2022);
		cert.setEnterpriseName("EMPRESA DE PRUEBA, S.L.");
		cert.setEnterpriseDocument("A50111111");
		cert.setEmployeeName("PERSONA DE PRUEBA");
		cert.setEmployeeDocument("12345678Z");
		
		RetentionCertificate cert1 = new RetentionCertificate();
		cert1.setPerception(3000.0);
		cert1.setRetention(300.0);
		cert1.setInKindPerception(4000.0);
		cert1.setInKindDeposit(400.0);
		cert1.setInKindOutputDeposit(450.0);
		cert1.setRefundAmount(30);
		cert1.setRefundReduction(40);		
		cert.setProf1(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(5000.0);
		cert1.setRetention(500.0);
		cert1.setInKindPerception(6000.0);
		cert1.setInKindDeposit(600.0);
		cert1.setInKindOutputDeposit(650.0);
		cert1.setRefundAmount(50);
		cert1.setRefundReduction(60);			
		cert.setProf2(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(7000.0);
		cert1.setRetention(700.0);
		cert1.setInKindPerception(8000.0);
		cert1.setInKindDeposit(800.0);
		cert1.setInKindOutputDeposit(850.0);
		cert1.setRefundAmount(70);
		cert1.setRefundReduction(80);
		cert.setProf3(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(9000.0);
		cert1.setRetention(900.0);
		cert1.setInKindPerception(9500.0);
		cert1.setInKindDeposit(950.0);
		cert1.setInKindOutputDeposit(955.0);
		cert1.setRefundAmount(90);
		cert1.setRefundReduction(95);
		cert.setProf4(cert1);

		return cert;
	}

	
}
