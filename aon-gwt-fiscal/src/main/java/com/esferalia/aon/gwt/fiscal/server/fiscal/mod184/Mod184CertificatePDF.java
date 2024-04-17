package com.esferalia.aon.gwt.fiscal.server.fiscal.mod184;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class Mod184CertificatePDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");	
//	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
//	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static final Font CELL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font CELL_LABEL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	private static final Font CELL_LABEL_SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 6);
	private static final Font CELL_LABEL_SMALL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static final Font BODY_FONT_NUMERIC = new Font(Font.FontFamily.HELVETICA, 7);
	
	public void printMod184Certificate(OutputStream outputStream, Occam occam, Map<String, RetentionCertificate> certificates) throws DocumentException {
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 20, 20);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		for (RetentionCertificate detail : certificates.values()) {
			printMod184Certificate1(document, writer, detail);			
		}
		document.close();
	}
	
	// Certificado de Atribución de Rentas
	private void printMod184Certificate1(Document document, PdfWriter writer, RetentionCertificate detail) throws DocumentException {
		
        // Cabeceras        
        addHeaderBand1(document, "Notificación de imputaciones a socios de entidades en régimen de atribución de rentas", detail.getYear());
		
		// Datos de la entidad
		addHeaderBand2(document, "Datos de la Entidad", 100, "Apellidos y nombre, denominación o razón social", detail.getEnterpriseDocument(), detail.getEnterpriseName());
		
        // Datos del Perceptor
		addHeaderBand22(document);
		
		// FALTA - RESTO DE DATOS DEL PERCEPTOR
		
		addSubTitleLine(document, "Rendimiento, Deducción y Retención Atribuible", 220);
		
		PdfPTable table1 = new PdfPTable(8);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{10,5,315,5,90,10,90,10}); // Total 535
		table1.setLockedWidth(true);
		
		// Cabeceras		
		addEmptyCell(table1, 4, Rectangle.LEFT);
		addLabelCenterCell(table1, "Importe");
		addEmptyCell(table1);		 
		addLabelCenterCell(table1, "Reducción");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
		
	    addDetailBand(table1, "Rendimientos del capital mobiliario previstos en los apartados 1, 2 y 3 del artículo 25 de la LIRPF", "A01");  // Clave A 01 
	    addDetailBand(table1, "Rendimientos del capital mobiliario previstos en el apartado 4 del artículo 25 de la LIRPF", "A02");  // Clave A 02 
	    addDetailBand(table1, "Rendimientos del capital inmobiliario", "C");  // Clave C    
	    addDetailBand(table1, "Rendimientos de actividades económicas", "D");  // Clave D    
	    addDetailBand(table1, "Rentas contabilizadas de participaciones de Instituciones de Inversión Colectiva", "E");  // Clave E    
	    addDetailBand(table1, "Ganancias patrimoniales no derivadas de transmisiones de elementos patrimoniales", "F01");  // Clave F 01 
	    addDetailBand(table1, "Pérdidas patrimoniales no derivadas de transmisiones de elementos patrimoniales", "F02");  // Clave F 02 
	    addDetailBand(table1, "Ganancias patrimoniales derivadas de transmisiones de elementos patrimoniales", "G01");  // Clave G 01 
	    addDetailBand(table1, "Pérdidas patrimoniales derivadas de transmisiones de elementos patrimoniales", "G02");  // Clave G 02 
	                  
	    //addDetailBand(table1, "Deducciones Ley IRPF:");
	    addEmptyLine(table1);
	    addEmptyCell(table1, 1, Rectangle.LEFT);		
    	addLabelCell(table1, "Deducciones Ley IRPF:", 8, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
    	
	    addSubDetailBand(table1, "Por protección Patrimonio Español y Mundial", "I01");     // Clave I 01 
	    addSubDetailBand(table1, "Por donativos, donaciones y aportaciones a determinadas entidades", "I02");      // Clave I 02 
	    addSubDetailBand(table1, "Por rentas obtenidas en Ceuta y Melilla", "I03");      // Clave I 03 
	    addSubDetailBand(table1, "Deducciones en actividades económicas", "I04");      // Clave I 04 
	    addSubDetailBand(table1, "Deducción por doble imposición internacional", "I05");      // Clave I 05 
	    addSubDetailBand(table1, "Por inversión en empresas de nueva o reciente creación", "I06");      // Clave I 06 
                      
//	    addDetailBand(table1, "Deducciones Ley Impuesto Sociedades:");
	    addEmptyLine(table1);
	    addEmptyCell(table1, 1, Rectangle.LEFT);		
    	addLabelCell(table1, "Deducciones Ley Impuesto Sociedades:", 8, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
    	
	    addSubDetailBand(table1, "Deducciones por doble imposición internacional", "J01");    // Clave J 01 
	    addSubDetailBand(table1, "Deducciones con límite sobre cuota", "J02");    // Clave J 02 
	    addSubDetailBand(table1, "Deducción por donativos a entidades sin fines lucrativos", "J03");    // Clave J 03 
	    addSubDetailBand(table1, "Otras deducciones", "J04");     // Clave J 04 
                      
//	    addDetailBand(table1, "Retenciones e ingresos a cuenta:");
	    addEmptyLine(table1);
	    addEmptyCell(table1, 1, Rectangle.LEFT);		
    	addLabelCell(table1, "Retenciones e ingresos a cuenta:", 8, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
    	
	    addSubDetailBand(table1, "Por rendimientos del capital mobiliario", "K01");  // Clave K 01 
	    addSubDetailBand(table1, "Por arrendamiento de inmuebles urbanos", "K02");  // Clave K 02 
	    addSubDetailBand(table1, "Por rendimientos de actividades económicas", "K03");  // Clave K 03 
	    addSubDetailBand(table1, "Por ganancias patrimoniales", "K04");  // Clave K 04 
	    addSubDetailBand(table1, "Por otros conceptos", "K05");  // Clave K 05
	    
	    addEmptyBottomLine(table1);
	    
	    document.add(table1);    
	    
	    // Fecha y Firma
		addFooterBand(document);

		document.newPage();
	}
	
	private void addHeaderBand1(Document document, String title, int year) throws DocumentException {
		PdfPTable table1 = new PdfPTable(3);
		table1.setSpacingBefore(10);
		table1.setWidthPercentage(100);
		table1.setTotalWidth(new float[]{400,5,130}); // Total 535
		table1.setLockedWidth(true);

		addTitleCell(table1, "Impuesto sobre la Renta de las Personas Físicas, Impuesto sobre Sociedades, Impuesto sobre la Renta de No Residentes", 3, TITLE_FONT_BOLD);
		addEmptyCell(table1, 3);
		addTitleCell(table1, title, 1, SUBTITLE_FONT_BOLD);
		addEmptyCell(table1);
		addTitleCell(table1, "Ejercicio " + year, 1, SUBTITLE_FONT_BOLD);
		
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
	
	// Datos del Socio, Heredero, Comunero o Participe
	private void addHeaderBand22(Document document) throws DocumentException {
		
		// , Mod184Partner mod184partner
		
		//addHeaderBand22(document, "Datos del Socio, Heredero, Comunero o Partícipe", 225, "Apellidos y nombre, denominación o razón social", detail.getEmployeeDocument(), detail.getEmployeeName());
		
		addSubTitleLine(document, "Datos del Socio, Heredero, Comunero o Partícipe", 225);
		
		// NIF, Nombre
		
		PdfPTable table1 = new PdfPTable(5);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);
		table1.setTotalWidth(new float[]{5,100,10,415,5});  // 535
		table1.setLockedWidth(true);
		
	    addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "NIF");
		addEmptyCell(table1);
		addLabelCell(table1, "Apellidos y nombre, denominación o razón social");
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 1, Rectangle.LEFT);
		//addBodyCell(table1, mod184partner.getDocument());
	    addBodyCell(table1, "12345678Z");  // PRUEBA
		addEmptyCell(table1);
		addBodyCell(table1, "APELLIDOS Y NOMBRE DEL SOCIO");
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    
	    
	    document.add(table1);
	    
	    // Domicilio, Código Provincia, Código Pais, Clave Tipo Participe
	    
		PdfPTable table2 = new PdfPTable(9);
		table2.setSpacingBefore(0);
		table2.setWidthPercentage(100);
		table2.setTotalWidth(new float[]{5,257,5,38,5,35,5,180,5}); // 535
		table2.setLockedWidth(true);
		
		//addEmptyLine(table2);
		
		addEmptyCell(table2, 1, Rectangle.LEFT);
		addLabelCell(table2, "Domicilio Fiscal");
		addEmptyCell(table2);
		addLabelCell(table2, "Cod. Prov.");
		addEmptyCell(table2);
		addLabelCell(table2, "Cod. País");
		addEmptyCell(table2);
		addLabelCell(table2, "Clave Tipo de Partícipe");
	    addEmptyCell(table2, 1, Rectangle.RIGHT);
		
		addEmptyCell(table2, 1, Rectangle.LEFT);
		addBodyCell(table2, "domicilio");
		addEmptyCell(table2);
		addCenterBodyCell(table2, "50");
		addEmptyCell(table2);
		addCenterBodyCell(table2, "ES");		
		addEmptyCell(table2);
		addBodyCell(table2, "3 - No residente con establecimiento permanente");
	    addEmptyCell(table2, 1, Rectangle.RIGHT);
		
		document.add(table2);
		
	    // Miembro a 31 diciembre, Número de días miembro, Porcentaje Participación		
		
		PdfPTable table3 = new PdfPTable(10);
		table3.setSpacingBefore(0);
		table3.setWidthPercentage(100);
		table3.setTotalWidth(new float[]{45,90,25,40,85,25,40,90,50,45}); // 535
		table3.setLockedWidth(true);
		
		addEmptyLine(table3);
		
		addEmptyCell(table3, 1, Rectangle.LEFT);
		addLabelCell(table3, "Miembro a 31 de diciembre").setVerticalAlignment(Element.ALIGN_TOP);
		addCenterBodyCell(table3, "No");
		addEmptyCell(table3);
		addLabelCell(table3, "Número de días miembro").setVerticalAlignment(Element.ALIGN_MIDDLE);
		addCenterBodyCell(table3, "365");
		addEmptyCell(table3);
		addLabelCell(table3, "Porcentaje de Participación").setVerticalAlignment(Element.ALIGN_MIDDLE);
		addCenterBodyCell(table3, "99,9999%");		
	    addEmptyCell(table3, 1, Rectangle.RIGHT);
	    
	    addEmptyBottomLine(table3);
		
		document.add(table3);
		
	}
	
	
	private void addFooterBand(Document document) throws DocumentException {
		
		addSubTitleLine(document, "Fecha y Firma", 75);
		
		PdfPTable table1 = new PdfPTable(11);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,15,245,80,15,20,15,90,15,30,5}); // Total 535
		table1.setLockedWidth(true);
		
		addEmptyLine(table1);
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addSmallLabelCell(table1, "Los datos expresados figuran en la Declaración Informativa Anual (Modelo 184), presentada por la Entidad. Y para que conste y sirva de justificante al interesado, se expide la presente", 10, Rectangle.RIGHT);
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
		addSmallLabelCell(table1, "Firma y sello de la entidad", 1, Rectangle.LEFT + Rectangle.TOP + Rectangle.RIGHT);
		addEmptyCell(table1, 8, Rectangle.RIGHT);
		
		for (int i = 0; i < 8; i++) {
			addEmptyCell(table1, 2, Rectangle.LEFT);
			addEmptyCell(table1, 1, Rectangle.LEFT + Rectangle.RIGHT);
			addEmptyCell(table1, 8, Rectangle.RIGHT);
		}
		
		addEmptyCell(table1, 2, Rectangle.LEFT);
		addSmallLabelCell(table1, "Fdo.: D./D.ª ____________________________________________________________", 1, Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);		
		//addSmallLabelCell(table1, "    La presente certificación deberá ser firmada por el retenedor, su apoderado o su representante", 8, Rectangle.RIGHT);
		addSmallLabelCell(table1, "", 8, Rectangle.RIGHT);
		addEmptyBottomLine(table1);                            // FALTA - RETENEDOR ???
				
		document.add(table1);
		
	}
	
	private void addDetailBand(Document document, String title, String head1, String head2, RetentionCertificate detail) throws DocumentException {
		addDetailBand(document, title, head1, head2, detail, false);
	}
	
	private void addDetailBand(Document document, String title, String head1, String head2, RetentionCertificate detail, boolean lastLine) throws DocumentException {
		
		PdfPTable table1 = new PdfPTable(10);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,5,225,5,90,10,90,10,90,5}); // Total 535
		table1.setLockedWidth(true);
		
	    addEmptyLine(table1);		
	    
	    if (detail == null) {
	    	addEmptyCell(table1, 2, Rectangle.LEFT);		
	    	addLabelCell(table1, title, 9, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);	    	
	    } else {
			// FALTA - DEPENDE DE LA CLAVE LA CABECERA DE LAS CASILLAS ES UNA U OTRA O NINGUNA
	    	if (head1 != null) {
				addEmptyCell(table1, 6, Rectangle.LEFT);
				addSmallLabelCenterCell(table1, head1);
				addEmptyCell(table1);
				if (head2 == null)
					addEmptyCell(table1);
				else 
				    addSmallLabelCenterCell(table1, head2);
				addEmptyCell(table1, 1, Rectangle.RIGHT);
	    	}
		    
		    addEmptyCell(table1, 2, Rectangle.LEFT);
		    addDotLabelCell(table1, title, 3);
		    addEmptyCell(table1);
			addNumericBodyCell(table1, detail.getPerception());
			addEmptyCell(table1);
			if (head2 == null)
				addEmptyCell(table1);
			else 
				addNumericBodyCell(table1, detail.getRetention()); 
		    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    }
	    
	    if (lastLine) {
	    	addEmptyLine(table1);
	    	addEmptyBottomLine(table1);	    	
	    }	
	    
	    document.add(table1);

	}
	
	private void addSubDetailBand(PdfPTable table, String description, String key) throws DocumentException {
		addDetailBand(table, description, key, 2);
	}
	
	private void addDetailBand(PdfPTable table, String description, String key) throws DocumentException {
		addDetailBand(table, description, key, 1);
	}
	
    private void addDetailBand(PdfPTable table, String description, String key, int colSpanLeft) throws DocumentException {
    	
	    addEmptyLine(table);
	    
	    // Descripción 
	    addEmptyCell(table, colSpanLeft, Rectangle.LEFT);
	    addDotLabelCell(table, description,3-colSpanLeft);
	    
	    // Importe
	    addEmptyCell(table);
		addNumericBodyCell(table, 1200.25);
		
		// Reducción (solo claves A02, C y D)
		if (key.equals("A02") || key.equals("C") || key.equals("D")) {
			addEmptyCell(table);
	     	addNumericBodyCell(table, 205.65); 
		} else {
			addEmptyCell(table,2);
		}
	    addEmptyCell(table, 1, Rectangle.RIGHT);
	    
	}
	
	private void addEmptyLine(PdfPTable table) {
		addEmptyCell(table, table.getNumberOfColumns(), Rectangle.LEFT + Rectangle.RIGHT);  
	}
	
	private void addEmptyBottomLine(PdfPTable table) {
		addEmptyCell(table, table.getNumberOfColumns(), Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);  
	}
	
	private void addLine(PdfPTable table, String columnHeadText, String lineText, double amount) {
		addLine(table, columnHeadText, lineText, amount, false);
	}
	
	private void addLine(PdfPTable table, String columnHeadText, String lineText, double amount, boolean doubleLine) {
		addLine(table, "", columnHeadText, lineText, amount, doubleLine);
	}	
	
	private void addLine(PdfPTable table, String sectionTitle, String columnHeadText, String lineText, double amount) {
		addLine(table, sectionTitle, columnHeadText, lineText, amount, false);
	}
	
	private void addLine(PdfPTable table, String sectionTitle, String columnHeadText, String lineText, double amount, boolean doubleLine) {
		
		if (AonStringUtils.isNotEmpty(sectionTitle))
			addEmptyLine(table);
		
		if (doubleLine) {
			addEmptyCell(table, 2, Rectangle.LEFT);		
			addDotLabelCell(table, lineText, 5, 2);
		    addEmptyCell(table);
			addSmallLabelCenterCell(table, columnHeadText);
			addEmptyCell(table, 1, Rectangle.RIGHT);
		} else {
			addEmptyCell(table, 1, Rectangle.LEFT);
			addLabelCell(table, sectionTitle, 7, Rectangle.NO_BORDER, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);					
			addSmallLabelCenterCell(table, columnHeadText);
			addEmptyCell(table, 1, Rectangle.RIGHT);
		}
		
		if (doubleLine) {
		    addEmptyCell(table, 2, Rectangle.LEFT);		    
		} else {
		    addEmptyCell(table, 2, Rectangle.LEFT);
		    addDotLabelCell(table, lineText, 5);
		}
		addEmptyCell(table);
		addNumericBodyCell(table, amount); 
	    addEmptyCell(table, 1, Rectangle.RIGHT);
		
	}
	
	private PdfPCell addTitleCell(PdfPTable table, String title, int colspan, Font font) {
	    PdfPCell c1 = new PdfPCell(new Phrase(title, font));
	    c1.setBorder(Rectangle.BOX);
	    c1.setBorderColor(BaseColor.BLACK);
	    c1.setBorderWidth(1);
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setPaddingBottom(4);
	    c1.setColspan(colspan);
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
	
	private PdfPCell addSmallLabelCell(PdfPTable table, String text, int colspan) {				
		return addLabelCell(table, text, colspan, Rectangle.NO_BORDER, CELL_LABEL_SMALL_FONT, Element.ALIGN_JUSTIFIED, 1);
	}
	
	private PdfPCell addSmallLabelCell(PdfPTable table, String text, int colspan, int border) {				
		return addLabelCell(table, text, colspan, border, CELL_LABEL_SMALL_FONT, Element.ALIGN_JUSTIFIED, 1);
	}
	
	private PdfPCell addSmallLabelCell(PdfPTable table, String boldText, String text, int colspan) {
		
		Chunk ch1 = new Chunk(boldText, CELL_LABEL_SMALL_FONT_BOLD);
		Chunk ch2 = new Chunk(text, CELL_LABEL_SMALL_FONT);
		
		Paragraph p = new Paragraph(6);
		p.add(ch1);
		p.add(ch2);
		p.setAlignment(Element.ALIGN_JUSTIFIED);
				
		PdfPCell c1 = new PdfPCell();
		c1.setColspan(colspan);
		c1.setBorder(Rectangle.NO_BORDER);
		c1.setPaddingTop(0);
	    c1.addElement(p);
		table.addCell(c1);
		return c1;
		
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
	    // FALTA
	    //c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
	    //c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(c1);
		return c1;
	}
	
	private PdfPCell addBodyCell(PdfPTable table, String text) {
		return addBodyCell(table, text, 1, Rectangle.BOX, Element.ALIGN_LEFT, BODY_FONT);
	}
	
	private PdfPCell addCenterBodyCell(PdfPTable table, String text) {
		return addBodyCell(table, text, 1, Rectangle.BOX, Element.ALIGN_CENTER, BODY_FONT);
	}
	
	private PdfPCell addNumericBodyCell(PdfPTable table, double amount) {
		return addBodyCell(table, AonMathUtils.isZero(amount)?" ":FMT.format(amount), 1, Rectangle.BOX, Element.ALIGN_RIGHT, BODY_FONT_NUMERIC);
	}
	
	private PdfPCell addBodyCell(PdfPTable table, String text, int colspan, int border, int horizontalAlignment, Font font) {
		//PdfPCell c1 = new PdfPCell(new Phrase(text, BODY_FONT));
		PdfPCell c1 = new PdfPCell(new Phrase(text, font));
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
			Map<String, RetentionCertificate> professionalCertificates = new HashMap<String, RetentionCertificate>();
			professionalCertificates.put("", getTestProfessionalCertificates());
			
			File file = new File("c:\\tmp\\certificado_mod184.pdf");
			OutputStream stream = new FileOutputStream(file);
			
			Mod184CertificatePDF print = new Mod184CertificatePDF();
			print.printMod184Certificate(stream, null, professionalCertificates);
			
			stream.close();
			
		} catch (IOException | DocumentException e) {
			System.out.println(e.getMessage());
		}
		
	}

	private static RetentionCertificate getTestProfessionalCertificates() {
		RetentionCertificate cert = new RetentionCertificate();
		cert.setId(null);
		cert.setName(null);
		cert.setYear(2022);
		cert.setEnterpriseName("ENTIDAD DE PRUEBA");
		cert.setEnterpriseDocument("H50111111");
		cert.setEmployeeName("PROFESIONAL DE PRUEBA CLAVE G");
		cert.setEmployeeDocument("12345678Z");
		
		cert.setPerception(1000.0);
		cert.setRetention(100.0);
		cert.setInKindPerception(2000.0);
		cert.setInKindDeposit(200.0);
		cert.setInKindOutputDeposit(150.0);
		
		
		RetentionCertificate cert1 = new RetentionCertificate();
		cert1.setPerception(3000.0);
		cert1.setRetention(300.0);
		cert1.setInKindPerception(4000.0);
		cert1.setInKindDeposit(400.0);
		cert1.setInKindOutputDeposit(450.0);
		cert.setProf1(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(5000.0);
		cert1.setRetention(500.0);
		cert1.setInKindPerception(6000.0);
		cert1.setInKindDeposit(600.0);
		cert1.setInKindOutputDeposit(650.0);
		cert.setProf2(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(7000.0);
		cert1.setRetention(700.0);
		cert1.setInKindPerception(8000.0);
		cert1.setInKindDeposit(800.0);
		cert1.setInKindOutputDeposit(850.0);
		cert.setProf3(cert1);
		
		cert1 = new RetentionCertificate();
		cert1.setPerception(9000.0);
		cert1.setRetention(900.0);
		cert1.setInKindPerception(9500.0);
		cert1.setInKindDeposit(950.0);
		cert1.setInKindOutputDeposit(955.0);
		cert.setProf4(cert1);

		return cert;
	}

	
}
