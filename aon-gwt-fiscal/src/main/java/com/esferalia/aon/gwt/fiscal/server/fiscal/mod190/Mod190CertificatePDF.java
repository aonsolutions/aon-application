package com.esferalia.aon.gwt.fiscal.server.fiscal.mod190;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Occam;
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

public class Mod190CertificatePDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");	
	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	private static final Font CELL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font CELL_LABEL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	private static final Font CELL_LABEL_SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 6);
	private static final Font CELL_LABEL_SMALL_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	
	public void printMod190Certificate(OutputStream outputStream, Occam occam, Map<String, RetentionCertificate> employeeCertificates, Map<String, RetentionCertificate> professionalCertificates) throws DocumentException {
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		for (RetentionCertificate detail : employeeCertificates.values()) {
			printMod190Certificate1(document, writer, detail);			
		}
		for (RetentionCertificate detail : professionalCertificates.values()) {
			printMod190Certificate2(document, writer, detail);			
		}
		document.close();
	}
	
	// Certificado de Retenciones: Rendimientos del trabajo, dietas exceptuadas de gravamen y rentas exentas
	private void printMod190Certificate1(Document document, PdfWriter writer, RetentionCertificate detail) throws DocumentException {

        addHeaderBand1(document, "Rendimientos del trabajo, dietas exceptuadas de gravamen y rentas exentas", detail.getYear());
        
        // Datos del Perceptor
		addHeaderBand2(document, "Datos del Perceptor", 100, "Apellidos y Nombre", detail.getEmployeeDocument(), detail.getEmployeeName());
		
		// Datos de la persona o entidad pagadora
		addHeaderBand2(document, "Datos de la persona o entidad pagadora", 190, "Apellidos y nombre, denominación o razón social", detail.getEnterpriseDocument(), detail.getEnterpriseName());
		
		// Rendimientos del trabajo: detalle de las percepciones y de las retenciones e ingresos a cuenta
		addSubTitleLine(document, "Rendimientos del trabajo: detalle de las percepciones y de las retenciones e ingresos a cuenta", 420);
		
		PdfPTable table1 = new PdfPTable(10);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,5,225,5,90,10,90,10,90,5}); // Total 535
		table1.setLockedWidth(true);
		
	    addEmptyLine(table1);
		
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "Rendimientos correspondientes al ejercicio.", 5, Rectangle.NO_BORDER, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
		addSmallLabelCenterCell(table1, "Importe íntegro satisfecho");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Retenciones practicadas");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Retribuciones dinerarias", 3);
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getRetention()); 
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
		
		addEmptyCell(table1, 4, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Valoración");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Ingresos a cuenta efectuados");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Ingresos a cuenta repercutidos");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
		
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Retribuciones en especie");
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindDeposit());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindOutputDeposit()); 
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addLine(table1, "Importe imputado al perceptor", "Contribuciones empresariales a planes de pensiones, planes de previsión social "
	    		+ "empresarial y mutualidades de previsión social, así como aportaciones a estos sistemas de previsión social que deriven "
	    		+ "de una decisión del trabajador, que reduzcan la base imponible del IRPF (excepto a seguros colectivos de dependencia)", detail.getForecastPlanContributions(), true);
	    addLine(table1, "Importe imputado al perceptor", "Contribuciones empresariales a seguros colectivos de dependencia", detail.getDependencyContributions());
	    addLine(table1, "Importe de las reducciones", "Reducciones a que se refieren el artículo 18, apartados 2 y 3, y/o las disposiciones transitorias 11.ª y 12.ª de la Ley del Impuesto", detail.getApplicableReduction());
	    addLine(table1, "Importe de los gastos", "Gastos fiscalmente deducibles a que se refiere el artículo 19.2 de la Ley del Impuesto", detail.getDeducibleExpense());
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addSmallLabelCell(table1, "(Cotizaciones a la Seguridad Social o a mutualidades generales obligatorias de funcionarios, detracciones por derechos pasivos y cotizaciones a Colegios de Huérfanos o entidades similares)", 5);
	    addEmptyCell(table1, 3, Rectangle.RIGHT);
	    
	    addEmptyLine(table1);
	    
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "Rendimientos satisfechos en el ejercicio correspondientes a ejercicios anteriores (atrasos).", 9, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addSmallLabelCell(table1, "Se hace constar asimismo que, con independencia de las retribuciones anteriormente detalladas, en el ejercicio a que "
	    		+ "este certificado se refiere le han sido satisfechas al perceptor que figura en el encabezamiento otras cantidades en concepto de atrasos "
	    		+ "correspondientes a ejercicios anteriores cuyos datos, a efectos de lo dispuesto en el artículo 14.2.b) de la Ley del Impuesto, se desglosan "
	    		+ "como sigue:", 7);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    PdfPTable table2 = new PdfPTable(9);
		table2.setSpacingBefore(0);
		table2.setWidthPercentage(90);		
		table2.setTotalWidth(new float[]{50,5,80,5,80,5,80,5,80}); 
		table2.setLockedWidth(true);
		table2.setHorizontalAlignment(Element.ALIGN_CENTER);		
		
		addSmallLabelCenterCell(table2, "Ejercicio de devengo");
		addEmptyCell(table2);		
		addSmallLabelCenterCell(table2, "Importe íntegro satisfecho");
		addEmptyCell(table2);
		addSmallLabelCenterCell(table2, "Retenciones practicadas");
		addEmptyCell(table2);
		addSmallLabelCenterCell(table2, "Reducciones (art. 18, 2 y 3, y DT 11.ª y 12.ª de la Ley del Impuesto)");
		addEmptyCell(table2);
		addSmallLabelCenterCell(table2, "Gastos deducibles (art. 19.2 [letras a), b) y c)] de la Ley del Impuesto)");
		
		// Atrasos linea 1
		addCenterBodyCell(table2, detail.getDelay1().getYear() == 0 ? " " : Integer.toString(detail.getDelay1().getYear()));  // Ejercicio de devengo
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay1().getPerception());  // Importe integro satisfecho
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay1().getRetention());  // Retenciones practicadas
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay1().getApplicableReduction());  // Reducciones 
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay1().getDeducibleExpense());	// Gastos
		
		// Atrasos linea 2
		addCenterBodyCell(table2, detail.getDelay2().getYear() == 0 ? " " : Integer.toString(detail.getDelay2().getYear()));  // Ejercicio de devengo
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay2().getPerception());  // Importe integro satisfecho
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay2().getRetention());  // Retenciones practicadas
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay2().getApplicableReduction());  // Reducciones 
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay2().getDeducibleExpense());	// Gastos
		
		// Atrasos linea 3
		addCenterBodyCell(table2, detail.getDelay3().getYear() == 0 ? " " : Integer.toString(detail.getDelay3().getYear()));  // Ejercicio de devengo
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay3().getPerception());  // Importe integro satisfecho
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay3().getRetention());  // Retenciones practicadas
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay3().getApplicableReduction());  // Reducciones 
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay3().getDeducibleExpense());	// Gastos
		
		// Atrasos linea 4
		addCenterBodyCell(table2, detail.getDelay4().getYear() == 0 ? " " : Integer.toString(detail.getDelay4().getYear()));  // Ejercicio de devengo
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay4().getPerception());  // Importe integro satisfecho
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay4().getRetention());  // Retenciones practicadas
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay4().getApplicableReduction());  // Reducciones 
		addEmptyCell(table2);
		addNumericBodyCell(table2, detail.getDelay4().getDeducibleExpense());	// Gastos
		
		PdfPCell c1 = new PdfPCell();
		c1.setColspan(10);
		c1.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
		c1.setPaddingTop(0);		
		c1.addElement(table2);
		table1.addCell(c1);
		
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addSmallLabelCell(table1, "Información de interés para el perceptor.- ","La percepción de cantidades en concepto de atrasos de "
	    		+ "rendimientos del trabajo dará lugar a la presentación de una declaración complementaria del IRPF por cada uno de "
	    		+ "los ejercicios a los que dichas cantidades se refieran, sin que estas declaraciones complementarias comporten la "
	    		+ "exigencia de intereses de demora ni recargo alguno.", 7);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyLine(table1);
	    
		addEmptyCell(table1, 1, Rectangle.LEFT);
		addLabelCell(table1, "Cantidades reintegradas por el perceptor en el ejercicio por haber sido indebida o excesivamente percibidas en ejercicios anteriores (reintegros).", 9, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
		
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addSmallLabelCell(table1, "Se hace constar también que, con independencia de los rendimientos anteriormente detallados, el perceptor que figura en el "
	    	    + "encabezamiento ha reintegrado en el ejercicio a que este certificado se refiere las cantidades que a continuación se detallan, "
	    	    + "que fueron indebida o excesivamente percibidas en cada uno de los ejercicios que se indican. Asimismo, se hace constar el importe "
	    	    + "de las reducciones que, en su caso, correspondieron a dichas cantidades a efectos de determinar el tipo de retención en los respectivos ejercicios.", 7);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    table2 = new PdfPTable(5);
		table2.setSpacingBefore(0);
		table2.setWidthPercentage(90);		
		table2.setTotalWidth(new float[]{70,10,100,10,100}); 
		table2.setLockedWidth(true);
		table2.setHorizontalAlignment(Element.ALIGN_CENTER);		
		
		addSmallLabelCenterCell(table2, "Ejercicio de percepción");
		addEmptyCell(table2);		
		addSmallLabelCenterCell(table2, "Importe íntegro reintegrado");
		addEmptyCell(table2);
		addSmallLabelCenterCell(table2, "Reducciones que correspondieron");
		
		for (int i = 0; i < 3; i++) {
			// Estos importes no se calculan actualmente en el certificado, por lo tanto se imprime directamente cero
			addNumericBodyCell(table2, 0); 
			addEmptyCell(table2);
			addNumericBodyCell(table2, 0);
			addEmptyCell(table2);
			addNumericBodyCell(table2, 0);
		}
		
		c1 = new PdfPCell();
		c1.setColspan(10);
		c1.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
		c1.setPaddingTop(0);		
		c1.addElement(table2);
		table1.addCell(c1);	    

	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addSmallLabelCell(table1, "Información de interés para el perceptor.- ", "El reintegro de cantidades incluidas en declaraciones del IRPF ya presentadas por el "
	    	    + "contribuyente, dará derecho a éste a solicitar de la Administración tributaria la rectificación de dichas declaraciones y, en "
	    	    + "su caso, la devolución de los ingresos indebidamente realizados en el Tesoro por esta causa, con arreglo a lo dispuesto en los "
	    	    + "artículos 120.3 y 221.4 de la Ley 58/2003, de 17 de diciembre, General Tributaria.", 7);
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addLine(table1, "Dietas exceptuadas de gravamen y rentas exentas del Impuesto.", "Importe satisfecho", "Dietas y asignaciones para gastos de viaje, en las cuantías exceptuadas de gravamen del IRPF", detail.getJourneyDiet());  
	    addLine(table1, "", "Rentas exentas del IRPF incluidas por la empresa o entidad pagadora en el resumen anual de retenciones e ingresos a cuenta (mod.190)", detail.getIncomeExemption(), true);
	    
	    addEmptyBottomLine(table1);
	    
	    document.add(table1);
	    
	    // Fecha y Firma
	    addFooterBand(document);

		document.newPage();
	}
	
	// Rendimientos de actividades económicas
	private void printMod190Certificate2(Document document, PdfWriter writer, RetentionCertificate detail) throws DocumentException {
		
        // Cabeceras        
        addHeaderBand1(document, "Rendimientos de actividades económicas", detail.getYear());
        
        // Datos del Perceptor
		addHeaderBand2(document, "Datos del Perceptor", 100, "Apellidos y Nombre", detail.getEmployeeDocument(), detail.getEmployeeName());
		
		// Datos de la persona o entidad pagadora
		addHeaderBand2(document, "Datos de la persona o entidad pagadora", 190, "Apellidos y nombre, denominación o razón social", detail.getEnterpriseDocument(), detail.getEnterpriseName());
		
		// Rendimientos de actividades profesionales
		addSubTitleLine(document, "Detalle de las percepciones y de las retenciones e ingresos a cuenta", 315);	
		
	    addDetailBand(document, "Rendimientos de actividades profesionales", detail);  // Clave G
	    addDetailBand(document, "Rendimientos de actividades agrícolas o ganaderas", detail.getProf1()); // Claves H01 y H02
	    addDetailBand(document, "Rendimientos de actividades forestales", detail.getProf2()); // Clave H03
	    addDetailBand(document, "Rendimientos de las actividades empresariales en estimación objetiva previstas en el art.º 95.6 del Reglamento del IRPF", detail.getProf3()); // Clave H04
	    addDetailBand(document, "Rendimientos a que se refiere el artículo 75.2.b) del Reglamento del IRPF, que deban calificarse como rendimientos de actividades económicas", detail.getProf4(), true); // Clave I
	    
	    // Fecha y Firma
		addFooterBand(document);

		document.newPage();
	}
	
	private void addHeaderBand1(Document document, String title, int year) throws DocumentException {
		PdfPTable table1 = new PdfPTable(3);
		table1.setSpacingBefore(10);
		table1.setWidthPercentage(100);
		table1.setTotalWidth(new float[]{345,5,185}); // Total 535
		table1.setLockedWidth(true);

		addTitleCell(table1, "Certificado de retenciones e ingresos a cuenta del Impuesto sobre la Renta de las Personas Físicas", 3, TITLE_FONT_BOLD);
		addEmptyCell(table1, 3);
		addTitleCell(table1, title, 1, SUBTITLE_FONT_BOLD);
		addEmptyCell(table1);
		addTitleCell(table1, "Datos correspondientes al ejercicio " + year, 1, SUBTITLE_FONT_BOLD);
		
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
		addLabelCell(table1, "Para que conste y sirva de justificante al interesado, en cumplimiento de lo dispuesto en el Reglamento del Impuesto sobre la Renta de las Personas Físicas, se expide la presente", 10, Rectangle.RIGHT);
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
		
		PdfPTable table1 = new PdfPTable(10);
		table1.setSpacingBefore(0);
		table1.setWidthPercentage(100);		
		table1.setTotalWidth(new float[]{5,5,225,5,90,10,90,10,90,5}); // Total 535
		table1.setLockedWidth(true);
		
	    addEmptyLine(table1);		
	    
		addEmptyCell(table1, 1, Rectangle.LEFT);		
		addLabelCell(table1, title, 9, Rectangle.RIGHT, CELL_LABEL_FONT_BOLD, Element.ALIGN_LEFT);
		
		addEmptyCell(table1, 6, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Importe íntegro satisfecho");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Retenciones practicadas");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
	    
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Contraprestaciones dinerarias", 3);
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getRetention()); 
	    addEmptyCell(table1, 1, Rectangle.RIGHT);
		
		addEmptyCell(table1, 4, Rectangle.LEFT);
		addSmallLabelCenterCell(table1, "Valoración");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Ingresos a cuenta efectuados");
		addEmptyCell(table1);
		addSmallLabelCenterCell(table1, "Ingresos a cuenta repercutidos");
		addEmptyCell(table1, 1, Rectangle.RIGHT);
		
	    addEmptyCell(table1, 2, Rectangle.LEFT);
	    addDotLabelCell(table1, "Contraprestaciones en especie");
	    addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindPerception());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindDeposit());
		addEmptyCell(table1);
		addNumericBodyCell(table1, detail.getInKindOutputDeposit()); 
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
	    c1.setVerticalAlignment(Element.ALIGN_BOTTOM);  
        table.addCell(c1);
		return c1;
	}
	
	private PdfPCell addBodyCell(PdfPTable table, String text) {
		return addBodyCell(table, text, 1, Rectangle.BOX, Element.ALIGN_LEFT);
	}
	
	private PdfPCell addCenterBodyCell(PdfPTable table, String text) {
		return addBodyCell(table, text, 1, Rectangle.BOX, Element.ALIGN_CENTER);
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
	
//	public static void main(String[] args) {
//				
//		try {
//			Map<String, RetentionCertificate> employeeCertificates = new HashMap<String, RetentionCertificate>();
//			employeeCertificates.put("", getTestEmployeeCertificates());
//			
//			Map<String, RetentionCertificate> professionalCertificates = new HashMap<String, RetentionCertificate>();
//			professionalCertificates.put("", getTestProfessionalCertificates());
//			
//			File file = new File("c:\\tmp\\certificado_mod190.pdf");
//			OutputStream stream = new FileOutputStream(file);
//			
//			Mod190CertificatePDF print = new Mod190CertificatePDF();
//			print.printMod190(stream, null, employeeCertificates, professionalCertificates);
//			
//			stream.close();
//			
//		} catch (IOException | DocumentException e) {
//			System.out.println(e.getMessage());
//		}
//		
//	}
//
//	private static RetentionCertificate getTestEmployeeCertificates() {
//		RetentionCertificate cert = new RetentionCertificate();
//		cert.setId(null);
//		cert.setName(null);
//		cert.setYear(2023);
//		cert.setEnterpriseName("EMPRESA DE PRUEBA, S.L.");
//		cert.setEnterpriseDocument("B50111111");
//		cert.setEmployeeName("PRUEBA PRUEBA, NOMBRE");
//		cert.setEmployeeDocument("11111111H");
//		
//		cert.setPerception(1225375.43);
//		cert.setRetention(10.0);
//		cert.setInKindPerception(200.0);
//		cert.setInKindDeposit(20.0);
//		cert.setInKindOutputDeposit(15.0);
//		
//		cert.setForecastPlanContributions(300);
//		cert.setDependencyContributions(400);
//			
//		cert.setApplicableReduction(500.0);
//		cert.setDeducibleExpense(600.0);
//			
//		cert.setDelay1(new RetentionCertificate());
//		cert.setDelay2(new RetentionCertificate());
//		cert.setDelay3(new RetentionCertificate());
//		cert.setDelay4(new RetentionCertificate());
//		
//		cert.setRefund1(new RetentionCertificate());
//		cert.setRefund2(new RetentionCertificate());
//		cert.setRefund3(new RetentionCertificate());
//			
//		cert.setJourneyDiet(700.0);
//		cert.setIncomeExemption(800.0);
//		
//		return cert;
//	}
//
//	private static RetentionCertificate getTestProfessionalCertificates() {
//		RetentionCertificate cert = new RetentionCertificate();
//		cert.setId(null);
//		cert.setName(null);
//		cert.setYear(2022);
//		cert.setEnterpriseName("EMPRESA DE PRUEBA PROFESIONALES, S.L.");
//		cert.setEnterpriseDocument("A50111111");
//		cert.setEmployeeName("PROFESIONAL DE PRUEBA CLAVE G");
//		cert.setEmployeeDocument("12345678Z");
//		
//		cert.setPerception(1000.0);
//		cert.setRetention(100.0);
//		cert.setInKindPerception(2000.0);
//		cert.setInKindDeposit(200.0);
//		cert.setInKindOutputDeposit(150.0);
//		
//		
//		RetentionCertificate cert1 = new RetentionCertificate();
//		cert1.setPerception(3000.0);
//		cert1.setRetention(300.0);
//		cert1.setInKindPerception(4000.0);
//		cert1.setInKindDeposit(400.0);
//		cert1.setInKindOutputDeposit(450.0);
//		cert.setProf1(cert1);
//		
//		cert1 = new RetentionCertificate();
//		cert1.setPerception(5000.0);
//		cert1.setRetention(500.0);
//		cert1.setInKindPerception(6000.0);
//		cert1.setInKindDeposit(600.0);
//		cert1.setInKindOutputDeposit(650.0);
//		cert.setProf2(cert1);
//		
//		cert1 = new RetentionCertificate();
//		cert1.setPerception(7000.0);
//		cert1.setRetention(700.0);
//		cert1.setInKindPerception(8000.0);
//		cert1.setInKindDeposit(800.0);
//		cert1.setInKindOutputDeposit(850.0);
//		cert.setProf3(cert1);
//		
//		cert1 = new RetentionCertificate();
//		cert1.setPerception(9000.0);
//		cert1.setRetention(900.0);
//		cert1.setInKindPerception(9500.0);
//		cert1.setInKindDeposit(950.0);
//		cert1.setInKindOutputDeposit(955.0);
//		cert.setProf4(cert1);
//
//		return cert;
//	}

	
}
