package com.esferalia.aon.gwt.fiscal.server.fiscal.mod184;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Certificate;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class Mod184CertificatePDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");
	private static final DecimalFormat FMT_PERCENT = new DecimalFormat("##0.0000");
	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	private static final Font CELL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font CELL_LABEL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	private static final Font CELL_LABEL_SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 6);
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static final Font BODY_FONT_NUMERIC = new Font(Font.FontFamily.HELVETICA, 7);
	
	// Descripción según clave y subclave
	public static HashMap<String,String> DESCRIPTION_MAP = new HashMap<String,String>();
	static {		    	
	    DESCRIPTION_MAP.put("A01", "Rendimientos del capital mobiliario previstos en los apartados 1, 2 y 3 del artículo 25 de la LIRPF");  // Clave A 01 
	    DESCRIPTION_MAP.put("A02", "Rendimientos del capital mobiliario previstos en el apartado 4 del artículo 25 de la LIRPF");  // Clave A 02 
	    DESCRIPTION_MAP.put("C", "Rendimientos del capital inmobiliario");  // Clave C    
	    DESCRIPTION_MAP.put("D", "Rendimientos de actividades económicas");  // Clave D    
	    DESCRIPTION_MAP.put("E", "Rentas contabilizadas de participaciones de Instituciones de Inversión Colectiva");  // Clave E    
	    DESCRIPTION_MAP.put("F01", "Ganancias patrimoniales no derivadas de transmisiones de elementos patrimoniales");  // Clave F 01 
	    DESCRIPTION_MAP.put("F02", "Pérdidas patrimoniales no derivadas de transmisiones de elementos patrimoniales");  // Clave F 02 
	    DESCRIPTION_MAP.put("G01", "Ganancias patrimoniales derivadas de transmisiones de elementos patrimoniales");  // Clave G 01 
	    DESCRIPTION_MAP.put("G02", "Pérdidas patrimoniales derivadas de transmisiones de elementos patrimoniales");  // Clave G 02 
	    DESCRIPTION_MAP.put("I01", "Por protección Patrimonio Español y Mundial");     // Clave I 01 
	    DESCRIPTION_MAP.put("I02", "Por donativos, donaciones y aportaciones a determinadas entidades");      // Clave I 02 
	    DESCRIPTION_MAP.put("I03", "Por rentas obtenidas en Ceuta y Melilla");      // Clave I 03 
	    DESCRIPTION_MAP.put("I04", "Deducciones en actividades económicas");      // Clave I 04 
	    DESCRIPTION_MAP.put("I05", "Deducción por doble imposición internacional");      // Clave I 05 
	    DESCRIPTION_MAP.put("I06", "Por inversión en empresas de nueva o reciente creación");      // Clave I 06 
	    DESCRIPTION_MAP.put("J01", "Deducciones por doble imposición internacional");    // Clave J 01 
	    DESCRIPTION_MAP.put("J02", "Deducciones con límite sobre cuota");    // Clave J 02 
	    DESCRIPTION_MAP.put("J03", "Deducción por donativos a entidades sin fines lucrativos");    // Clave J 03 
	    DESCRIPTION_MAP.put("J04", "Otras deducciones");     // Clave J 04 
	    DESCRIPTION_MAP.put("K01", "Por rendimientos del capital mobiliario");  // Clave K 01 
	    DESCRIPTION_MAP.put("K02", "Por arrendamiento de inmuebles urbanos");  // Clave K 02 
	    DESCRIPTION_MAP.put("K03", "Por rendimientos de actividades económicas");  // Clave K 03 
	    DESCRIPTION_MAP.put("K04", "Por ganancias patrimoniales");  // Clave K 04 
	    DESCRIPTION_MAP.put("K05", "Por otros conceptos");  // Clave K 05		
	}	
	
	public void printMod184Certificate(OutputStream outputStream, Occam occam, Map<String, Mod184Certificate> certificates) throws DocumentException {
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 20, 20);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		
		if (certificates.size()==0) {
			document.add(new Chunk(""));			
		} else {
			for (Mod184Certificate certi : certificates.values()) {
				printMod184Certificate1(document, writer, certi);			
			}
		}
		
		document.close();
	}
	
	// Certificado de Atribución de Rentas
	private void printMod184Certificate1(Document document, PdfWriter writer, Mod184Certificate certi) throws DocumentException {
		
	    // Cabeceras        
        addHeaderBand(document, "Notificación de imputaciones a miembros de entidades en régimen de atribución de rentas", certi.getYear());
		
		// Datos de la entidad
		addHeaderBandEntity(document, "Datos de la Entidad", 100, "Apellidos y nombre, denominación o razón social", certi.getEntityDocument(), certi.getEntityName());
				
        // Datos del Perceptor
		addHeaderBandMember(document, certi);
		
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

		// Detalles por clave y subclave
	    addDetailBand(table1, certi, "A01");      // Clave A 01 
	    addDetailBand(table1, certi, "A02");      // Clave A 02 
	    addDetailBand(table1, certi, "C"  );      // Clave C    
	    addDetailBand(table1, certi, "D"  );      // Clave D    
	    addDetailBand(table1, certi, "E"  );      // Clave E    
	    addDetailBand(table1, certi, "F01");      // Clave F 01 
	    addDetailBand(table1, certi, "F02");      // Clave F 02 
	    addDetailBand(table1, certi, "G01");      // Clave G 01 
	    addDetailBand(table1, certi, "G02");      // Clave G 02 
	    addTitleDetailBand(table1, "Deducciones Ley IRPF:"); 
	    addSubDetailBand(table1, certi, "I01");   // Clave I 01 
	    addSubDetailBand(table1, certi, "I02");   // Clave I 02 
	    addSubDetailBand(table1, certi, "I03");   // Clave I 03 
	    addSubDetailBand(table1, certi, "I04");   // Clave I 04 
	    addSubDetailBand(table1, certi, "I05");   // Clave I 05 
	    addSubDetailBand(table1, certi, "I06");   // Clave I 06 
	    addTitleDetailBand(table1, "Deducciones Ley Impuesto Sociedades:");
	    addSubDetailBand(table1, certi, "J01");   // Clave J 01 
	    addSubDetailBand(table1, certi, "J02");   // Clave J 02 
	    addSubDetailBand(table1, certi, "J03");   // Clave J 03 
	    addSubDetailBand(table1, certi, "J04");   // Clave J 04 
	    addTitleDetailBand(table1, "Retenciones e ingresos a cuenta:");
	    addSubDetailBand(table1, certi, "K01");   // Clave K 01 
	    addSubDetailBand(table1, certi, "K02");   // Clave K 02 
	    addSubDetailBand(table1, certi, "K03");   // Clave K 03 
	    addSubDetailBand(table1, certi, "K04");   // Clave K 04 
	    addSubDetailBand(table1, certi, "K05");   // Clave K 05
	    addEmptyBottomLine(table1);
	    
	    document.add(table1);    
	    
	    // Fecha y Firma
		addFooterBand(document);

		document.newPage();
	}
	
	private void addHeaderBand(Document document, String title, int year) throws DocumentException {
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
	
	private void addHeaderBandEntity(Document document, String title, int titleWidth, String nameHead, String nif, String name) throws DocumentException {
		
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
	private void addHeaderBandMember(Document document, Mod184Certificate certi) throws DocumentException {
		
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
	    addBodyCell(table1, certi.getMemberDocument());
		addEmptyCell(table1);
		addBodyCell(table1, certi.getMemberName());
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    document.add(table1);
	    
	    // Domicilio, Código Provincia, Código Pais, Clave Tipo Participe
	    
		PdfPTable table2 = new PdfPTable(9);
		table2.setSpacingBefore(0);
		table2.setWidthPercentage(100);
		table2.setTotalWidth(new float[]{5,257,5,38,5,35,5,180,5}); // 535
		table2.setLockedWidth(true);
		
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
		addBodyCell(table2, certi.getAddress());
		addEmptyCell(table2);
		addCenterBodyCell(table2, certi.getProvince() == 0 ? "" : AonStringUtils.leftPad(AonNumberUtils.emptyIfNull(certi.getProvince()), 2, '0'));
		addEmptyCell(table2);
		addCenterBodyCell(table2, certi.getCountry());		
		addEmptyCell(table2);
		
		String value = "";
		switch (certi.getPartType()) {
			case 2:			
				value = "2.- No residente sin establecimiento permanente";
				break;
			case 3:
				value = "3.- No residente con establecimiento permanente";
				break;
			default:
				value = "1.- Residente";
		}
				
		addBodyCell(table2, value);
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
		addLabelCell(table3, "Miembro a 31 de diciembre");
		addCenterBodyCell(table3, certi.isMemberEndOfYear() ? "Si" : "No");
		addEmptyCell(table3);
		addLabelCell(table3, "Número de días miembro");
		addCenterBodyCell(table3, AonNumberUtils.emptyIfNull(certi.getMemberDays()) );
		addEmptyCell(table3);
		addLabelCell(table3, "Porcentaje de Participación");
		addCenterBodyCell(table3, FMT_PERCENT.format(certi.getPartPercent())+"%");		
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
		addSmallLabelCell(table1, "", 8, Rectangle.RIGHT);
		addEmptyBottomLine(table1);                            
				
		document.add(table1);
		
	}
	
	private void addTitleDetailBand(PdfPTable table, String title) throws DocumentException {
		addEmptyLine(table);
	    addEmptyCell(table, 1, Rectangle.LEFT);		
		addLabelCell(table, title, 8, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
	}	
	
	private void addSubDetailBand(PdfPTable table, Mod184Certificate certi, String key) throws DocumentException {
		addDetailBand(table, certi, key, 2);
	}
	
	private void addDetailBand(PdfPTable table, Mod184Certificate certi, String key) throws DocumentException {
		addDetailBand(table, certi, key, 1);
	}
	
    private void addDetailBand(PdfPTable table, Mod184Certificate certi, String key, int colSpanLeft) throws DocumentException {
    	
    	String description = DESCRIPTION_MAP.get(key);
    	double amount = 0;
    	double reduction = 0;
    	if (certi.getDetail().containsKey(key)) {
    		amount = certi.getDetail().get(key)[0];
    		reduction = certi.getDetail().get(key)[1];
    	}
    	
	    addEmptyLine(table);
	    
	    // Descripción 
	    addEmptyCell(table, colSpanLeft, Rectangle.LEFT);
	    addDotLabelCell(table, description,3-colSpanLeft);
	    
	    // Importe
	    addEmptyCell(table);
		addNumericBodyCell(table, amount);
		
		// Reducción (solo claves A02, C y D)
		if (key.equals("A02") || key.equals("C") || key.equals("D")) {
			addEmptyCell(table);
	     	addNumericBodyCell(table, reduction); 
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
	
//	public static void main(String[] args) {
//				
//		try {
//			Map<String, Mod184Certificate> certificates = new HashMap<>();
//			certificates.put("1", getTestCertificate1());
//			certificates.put("2", getTestCertificate2());
//			certificates.put("3", getTestCertificate3());
//			
//			File file = new File("c:\\tmp\\certificado_mod184.pdf");
//			OutputStream stream = new FileOutputStream(file);
//			
//			Mod184CertificatePDF print = new Mod184CertificatePDF();
//			print.printMod184Certificate(stream, null, certificates);
//			
//			stream.close();
//			
//		} catch (IOException | DocumentException e) {
//			System.out.println(e.getMessage());
//		}
//		
//	}
//	
//	private static Mod184Certificate getTestCertificate1() {
//		
//		Mod184Certificate cert = new Mod184Certificate();
//		
//		cert.setYear(2023); 
//		cert.setEntityDocument("H50111111");  
//		cert.setEntityName("ENTIDAD DE PRUEBA");
//		cert.setMemberDocument("1111111H");
//		cert.setMemberName("PRUEBA PRUEBA, UNO");
//		cert.setAddress("C/ DOMICILIO FISCAL DE PRUEBA, 15, 7ºA"); 
//		cert.setProvince(50);
//		cert.setCountry(null);
//		cert.setPartType((byte) 1);
//		cert.setMemberEndOfYear(true);
//		cert.setMemberDays(366);
//		cert.setPartPercent(25.5691);		
//		cert.getDetail().put("A01", new double[]{100.02,0});	     
//	    cert.getDetail().put("A02", new double[]{200.02,10.01});  
//	    cert.getDetail().put("C"  , new double[]{300.02,20.01}); 
//	    cert.getDetail().put("D"  , new double[]{400.02,30.01}); 
//	    cert.getDetail().put("E"  , new double[]{500.02,0}); 
//	    cert.getDetail().put("F01", new double[]{600.02,0}); 
//	    cert.getDetail().put("F02", new double[]{700.02,0}); 
//	    cert.getDetail().put("G01", new double[]{800.02,0}); 
//	    cert.getDetail().put("G02", new double[]{900.02,0}); 
//	    cert.getDetail().put("I01", new double[]{100.02,0}); 
//	    cert.getDetail().put("I02", new double[]{110.02,0}); 
//	    cert.getDetail().put("I03", new double[]{120.02,0}); 
//	    cert.getDetail().put("I04", new double[]{130.02,0}); 
//	    cert.getDetail().put("I05", new double[]{140.02,0}); 
//	    cert.getDetail().put("I06", new double[]{150.02,0}); 
//	    cert.getDetail().put("J01", new double[]{160.02,0}); 
//	    cert.getDetail().put("J02", new double[]{170.02,0}); 
//	    cert.getDetail().put("J03", new double[]{180.02,0}); 
//	    cert.getDetail().put("J04", new double[]{190.02,0}); 
//	    cert.getDetail().put("K01", new double[]{200.02,0}); 
//	    cert.getDetail().put("K02", new double[]{210.02,0}); 
//	    cert.getDetail().put("K03", new double[]{220.02,0}); 
//	    cert.getDetail().put("K04", new double[]{230.02,0}); 
//	    cert.getDetail().put("K05", new double[]{240.02,0}); 
//		
//		return cert;
//	}
//
//	private static Mod184Certificate getTestCertificate2() {
//		
//		Mod184Certificate cert = new Mod184Certificate();
//		
//		cert.setYear(2023); 
//		cert.setEntityDocument("H50111111");  
//		cert.setEntityName("ENTIDAD DE PRUEBA");
//		cert.setMemberDocument("12345678Z");
//		cert.setMemberName("APELLIDOUNO APELLIDODOS, MIEMBRO");
//		cert.setAddress("C/ SAN SEBASTIAN DE LOS REYES, 1, 1ºA"); 
//		cert.setProvince(99);
//		cert.setCountry("GB");
//		cert.setPartType((byte) 3);
//		cert.setMemberEndOfYear(true);
//		cert.setMemberDays(366);
//		cert.setPartPercent(100);		
//		cert.getDetail().put("A01", new double[]{1000,0});	     
//	    cert.getDetail().put("A02", new double[]{2000,100});  
//	    cert.getDetail().put("C"  , new double[]{3000,200}); 
//	    cert.getDetail().put("D"  , new double[]{4000,300}); 
//	    cert.getDetail().put("E"  , new double[]{5000,0}); 
//	    cert.getDetail().put("F01", new double[]{6000,0}); 
//	    cert.getDetail().put("F02", new double[]{7000,0}); 
//	    cert.getDetail().put("G01", new double[]{8000,0}); 
//	    cert.getDetail().put("G02", new double[]{9000,0}); 
//	    cert.getDetail().put("I01", new double[]{1000,0}); 
//	    cert.getDetail().put("I02", new double[]{1100,0}); 
//	    cert.getDetail().put("I03", new double[]{1200,0}); 
//	    cert.getDetail().put("I04", new double[]{1300,0}); 
//	    cert.getDetail().put("I05", new double[]{1400,0}); 
//	    cert.getDetail().put("I06", new double[]{1500,0}); 
//	    cert.getDetail().put("J01", new double[]{1600,0}); 
//	    cert.getDetail().put("J02", new double[]{1700,0}); 
//	    cert.getDetail().put("J03", new double[]{1800,0}); 
//	    cert.getDetail().put("J04", new double[]{1900,0}); 
//	    cert.getDetail().put("K01", new double[]{2000,0}); 
//	    cert.getDetail().put("K02", new double[]{2100,0}); 
//	    cert.getDetail().put("K03", new double[]{2200,0}); 
//	    cert.getDetail().put("K04", new double[]{2300,0}); 
//	    cert.getDetail().put("K05", new double[]{2400,0}); 
//		
//		return cert;
//	}
//	
//	private static Mod184Certificate getTestCertificate3() {
//		
//		Mod184Certificate cert = new Mod184Certificate();
//		
//		cert.setYear(2023); 
//		cert.setEntityDocument(null);  
//		cert.setEntityName(null);
//		cert.setMemberDocument(null);
//		cert.setMemberName(null);
//		cert.setAddress(null); 
//		cert.setProvince(50);
//		cert.setCountry(null);
//		cert.setPartType((byte) 0);
//		cert.setMemberEndOfYear(false);
//		cert.setMemberDays(null);
//		cert.setPartPercent(0.0);		
//		
//		return cert;
//	}

	
}
