package com.esferalia.aon.gwt.fiscal.server.fiscal.mod180;

import java.io.OutputStream;
import java.text.DecimalFormat;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.Province;
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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class Mod180CertificatePDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");
	private static final Font TITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static final Font SUBTITLE_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 10);
	private static final Font CELL_LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 7);
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static final Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static final Font SMALL_BODY_FONT = new Font(Font.FontFamily.HELVETICA, 7);

	public void printMod180(OutputStream outputStream, Occam occam, Mod180 mod180) throws DocumentException {
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		for ( Mod180Detail detail : mod180.getDetails()) {
			printMod180Detail(document,writer, mod180, detail);			
		}
		document.close();
	}
	
	private void printMod180Detail(Document document, PdfWriter writer, Mod180 mod180, Mod180Detail detail) throws DocumentException {
		
		double perception  = detail.isInKind()?0:detail.getPerception();
		double retention  = detail.isInKind()?0:detail.getRetention();
		double inKindPerception  = detail.isInKind()?detail.getPerception():0;
		double inKindRetention  = detail.isInKind()?detail.getRetention():0;
		
		Paragraph p1 = new Paragraph();
		p1.setAlignment( Element.ALIGN_JUSTIFIED_ALL );
		p1.setIndentationLeft(10);
		p1.setIndentationRight(10);
		ParagraphBorder border = new ParagraphBorder();
	    writer.setPageEvent(border);
	    border.setActive(true);
		String title =  "Certificado de Retenciones e Ingresos a cuenta del Impuesto sobre la Renta de las Personas"
			+" Físicas, del Impuesto sobre Sociedades y del Impuesto sobre la Renta de no Residentes"
			+" (establecimientos permanentes y entidades en régimen de atribución de rentas constituidas en el"
			+" extranjero con presencia en territorio español).";
		p1.add(new Paragraph(title, TITLE_FONT_BOLD));
		document.add(p1);
		
		Paragraph p2 = new Paragraph();
		p2.setAlignment( Element.ALIGN_CENTER );
		p2.setSpacingBefore(20);
		p2.setIndentationLeft(10);
		p2.setIndentationRight(10);
	    border.setActive(true);
		String t21 =  "Rendimientos procedentes del arrendamiento o subarrendamiento de inmuebles urbanos";
		p2.add(new Paragraph(t21, SUBTITLE_FONT_BOLD));
		String t22 =  "Datos correspondientes al ejercicio: ";
		p2.add(new Chunk(t22, SUBTITLE_FONT));
		String t23 =  "" + mod180.getYear();
		p2.add(new Chunk(t23, SUBTITLE_FONT_BOLD));
		document.add(p2);
		
		border.setActive(false);
		
		float[] widths0 = new float[]{100,10,150,10,100,10,130};
		PdfPTable tab0 = new PdfPTable(widths0.length);
		tab0.setSpacingBefore(40);
		tab0.setTotalWidth(widths0);
		tab0.setWidthPercentage(100);
		tab0.setLockedWidth(true);

		addTitleCell(tab0, "Datos del perceptor", 2);	
		addTitleCell(tab0, "", 5);
		
		addEmptyCell(tab0, widths0.length);
		
		addLabelCell(tab0, "NIF", 1);
		addEmptyCell(tab0);
		addLabelCell(tab0, "Apellidos y Nombre o Razón Social", 5);
		
		addBodyCell(tab0, detail.getDocument(), 1);
		addEmptyCell(tab0);
		addBodyCell(tab0, detail.getName(), 5);
		document.add(tab0);
		
		float[] widths1 = new float[]{100,10,90,10,140,10,30,10,30,10,30,10,30};
		
		PdfPTable tab1 = new PdfPTable(widths1.length);
		tab1.setSpacingBefore(40);
		tab1.setTotalWidth(widths1);
		tab1.setWidthPercentage(100);
		tab1.setLockedWidth(true);

		addTitleCell(tab1, "Datos del pagador",2);	
		addTitleCell(tab1, "", 11);
		
		addEmptyCell(tab1, widths1.length);
		
		addLabelCell(tab1, "NIF", 1);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Apellidos y Nombre o Razón Social", 11);

		addBodyCell(tab1, mod180.getDocument(), 1);
		addEmptyCell(tab1);
		addBodyCell(tab1, mod180.getName(), 11);
		
		addLabelCell(tab1, "Domicilio", 5);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Número", 1);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Esc.", 1);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Piso", 1);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Prta.", 1);

		String add = AonStringUtils.join(new String[] {detail.getStreetType(),detail.getStreetName()}, " ");
		addBodyCell(tab1, add , 5);
		addEmptyCell(tab1);
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getNumber()) , 1);
		addEmptyCell(tab1);
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getStair()) , 1);
		addEmptyCell(tab1);
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getFloor()) , 1);
		addEmptyCell(tab1);
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getDoor()) , 1);
		
		addLabelCell(tab1, "Municipio", 3);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Provincia", 3);
		addEmptyCell(tab1);
		addLabelCell(tab1, "Teléfono", 3);
		addEmptyCell(tab1);
		addLabelCell(tab1, "C.P.", 1);
		
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getCity()) , 3);	
		addEmptyCell(tab1);
		addBodyCell(tab1, Province.values()[detail.getProvince()].getName()  , 3);
		addEmptyCell(tab1);
		addBodyCell(tab1, "" , 3);
		addEmptyCell(tab1);
		addBodyCell(tab1, AonStringUtils.defaultIfBlank(detail.getZip()) , 1);

		document.add(tab1);
		
		float[] widths2 = new float[]{290,10,100,10,100};
		PdfPTable tab2 = new PdfPTable(widths2.length);
		tab2.setSpacingBefore(40);
		tab2.setTotalWidth(widths2);
		tab2.setWidthPercentage(100);
		tab2.setLockedWidth(true);
		
		addTitleCell(tab2, "Rendimientos procedentes del arrendamiento o subarrendamiento de inmuebles arrendados",3);
		addTitleCell(tab2, "", 2);
		
		addEmptyCell(tab2, widths2.length);
		
		addEmptyCell(tab2, 2);
		Paragraph e0 = new Paragraph(8, "Retenciones" ,CELL_LABEL_FONT);
		e0.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab2, e0, 1);
		addEmptyCell(tab2);
		Paragraph e1 = new Paragraph(8, "Importe íntegro" ,CELL_LABEL_FONT);
		e1.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab2, e1, 1);
		
		addLabel2Cell(tab2, "Rendimientos dinerarios");
		addEmptyCell(tab2);
		addNumericBodyCell(tab2, AonMathUtils.isZero(perception)?"":FMT.format(perception));
		addEmptyCell(tab2);
		addNumericBodyCell(tab2, AonMathUtils.isZero(retention)?"":FMT.format(retention));
		
		
		addEmptyCell(tab2, 2);
		Paragraph e2 = new Paragraph(8, "Ingresos a cuenta" ,CELL_LABEL_FONT);
		e2.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab2, e2, 1);
		addEmptyCell(tab2);
		Paragraph e3 = new Paragraph(8, "Valoración" ,CELL_LABEL_FONT);
		e3.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab2, e3, 1);
		
		addLabel2Cell(tab2, "Rendimientos en especie");
		addEmptyCell(tab2);
		addNumericBodyCell(tab2, AonMathUtils.isZero(inKindPerception)?"":FMT.format(inKindPerception));
		addEmptyCell(tab2);
		addNumericBodyCell(tab2, AonMathUtils.isZero(inKindRetention)?"":FMT.format(inKindRetention));

		document.add(tab2);
		
		float[] widths3 = new float[]{20,180,20,40,20,140,20,40,30};
		PdfPTable tab3 = new PdfPTable(widths3.length);
		tab3.setSpacingBefore(100);
		tab3.setTotalWidth(widths3);
		tab3.setWidthPercentage(100);
		tab3.setLockedWidth(true);
		
		addTitleCell(tab3, "Fecha y firma",2);	
		addTitleCell(tab3, "", 7);
		
		addEmptyCell(tab3, widths3.length);

		String t = "Y para que conste y sirva de justificante al interesado, en cumplimiento"
			+ " de lo dispuesto en los Reglamentos del I.R.P.F. y del Impuesto sobre"
			+ " Sociedades, se expide el presente.;";
		Paragraph e30 = new Paragraph(8, t ,CELL_LABEL_FONT);
		e30.setAlignment( Element.ALIGN_JUSTIFIED);
		addLabelCell(tab3, e30 , widths3.length);
		
		addEmptyCell(tab3, widths3.length);
		
		Paragraph e31 = new Paragraph(8, "En" ,CELL_LABEL_FONT);
		e31.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab3, e31);
		addBodyCell(tab3, "" );
		
		Paragraph e32 = new Paragraph(8, "a" ,CELL_LABEL_FONT);
		e32.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab3, e32);
		addBodyCell(tab3, "" );
		
		Paragraph e33 = new Paragraph(8, "de" ,CELL_LABEL_FONT);
		e33.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab3, e33);
		addBodyCell(tab3, "" );
		
		Paragraph e34 = new Paragraph(8, "de" ,CELL_LABEL_FONT);
		e34.setAlignment( Element.ALIGN_CENTER & Element.ALIGN_MIDDLE);
		addLabelCell(tab3, e34);
		addBodyCell(tab3, "" );
		
		addEmptyCell(tab3);
		
		document.add(tab3);

		PdfPTable tab4 = new PdfPTable(1);
		tab4.setTotalWidth( 510 );
		tab4.setWidthPercentage(100);
		tab4.setLockedWidth(true);
		
		Paragraph p40 = new Paragraph(8,"Firma y sello de la empresa o entidad pagadora" ,BODY_FONT);
		p40.setAlignment(Element.ALIGN_CENTER);
		PdfPCell c40 = new PdfPCell();
		c40.setBorder( Rectangle.NO_BORDER);
		c40.addElement(p40);
		tab4.addCell( c40 );

		Paragraph p41 = new Paragraph(8,"Fdo.: D/Dª ________________________________________________",BODY_FONT);
		p41.setAlignment(Element.ALIGN_CENTER);
		PdfPCell c41 = new PdfPCell();
		c41.setFixedHeight(100);
		c41.setVerticalAlignment(Element.ALIGN_BOTTOM);
		c41.setBorder( Rectangle.NO_BORDER);
		c41.addElement(p41);
		tab4.addCell( c41 );
		
		Paragraph p42 = new Paragraph(8,"La presente certificación deberá ser firmada por el retenedor, su apoderado o su representante.",SMALL_BODY_FONT);
		p42.setAlignment(Element.ALIGN_CENTER);
		PdfPCell c42 = new PdfPCell();
		c42.setFixedHeight(20);
		c42.setBorder( Rectangle.NO_BORDER);
		c42.addElement(p42);
		tab4.addCell( c42 );
		
		border.setActive(true);
		Paragraph p4 = new Paragraph(8, "" ,BODY_FONT);
		p4.setSpacingBefore(10);
		p4.setIndentationLeft(10);
		p4.setIndentationRight(10);
		p4.setAlignment( Element.ALIGN_CENTER );
		p4.add( tab4 );
		document.add(p4);

		document.newPage();
	}

	private PdfPCell addTitleCell(PdfPTable table, String title, int colspan) {
		title = AonStringUtils.isBlank(title)?"":(AonStringUtils.BULLET + " " + title);
		Paragraph e0 = new Paragraph(8,title,BODY_FONT_BOLD);
		e0.setAlignment( Element.ALIGN_MIDDLE);
		PdfPCell c0 = new PdfPCell();
		c0.setColspan(colspan);
		c0.setBorder( Rectangle.BOTTOM );
		c0.setBorderColor( BaseColor.BLACK);
		c0.addElement(e0);
		table.addCell( c0 );
		return c0;
	}
	
	private PdfPCell addLabel2Cell(PdfPTable table , String text) {
		return addLabel2Cell(table , text, 1);
	}

	private PdfPCell addLabel2Cell(PdfPTable table , String text, int colspan) {
		Paragraph e0 = new Paragraph(8, text ,CELL_LABEL_FONT);
		e0.setAlignment( Element.ALIGN_MIDDLE);
		PdfPCell c0 = new PdfPCell();
		c0.setColspan(colspan);
		c0.setBorder( Rectangle.BOTTOM);
		c0.setBorderColor( BaseColor.LIGHT_GRAY);
		c0.addElement(e0);
		table.addCell( c0 );
		return c0;
	}

	private PdfPCell addLabelCell(PdfPTable table , Paragraph e0) {
		return addLabelCell(table, e0, 1);
	}
	
//	private PdfPCell addLabelCell(PdfPTable table , String text) {
//		return addLabelCell(table , text, 1);
//	}
	
	private PdfPCell addLabelCell(PdfPTable table , String text, int colspan) {
		Paragraph e0 = new Paragraph(8, text ,CELL_LABEL_FONT);
		e0.setAlignment( Element.ALIGN_MIDDLE);
		return  addLabelCell(table, e0, colspan);
	}
	
	private PdfPCell addLabelCell(PdfPTable table , Paragraph e0, int colspan) {
		PdfPCell c0 = new PdfPCell();
		c0.setColspan(colspan);
		c0.setBorder( Rectangle.NO_BORDER);
		c0.addElement(e0);
		table.addCell( c0 );
		return c0;
	}

	private PdfPCell addBodyCell(PdfPTable table , String text) {
		return addBodyCell(table , text, 1);
	}
	private PdfPCell addBodyCell(PdfPTable table , String text, int colspan) {
		Paragraph e0 = new Paragraph(8, text ,BODY_FONT);
		e0.setAlignment( Element.ALIGN_MIDDLE);
		return addBodyCell(table , e0, colspan);
	}

	private PdfPCell addNumericBodyCell(PdfPTable table , String text) {
		return addNumericBodyCell(table , text, 1);
	}

	private PdfPCell addNumericBodyCell(PdfPTable table , String text, int colspan) {
		Paragraph e0 = new Paragraph(8, text ,BODY_FONT);
		e0.setAlignment( Element.ALIGN_RIGHT);
		return addBodyCell(table , e0, colspan);
	}

	private PdfPCell addBodyCell(PdfPTable table , Paragraph e0, int colspan) {
		PdfPCell c0 = new PdfPCell();
		c0.setColspan(colspan);
		c0.setBorder( Rectangle.BOX );
		c0.setBorderColor( BaseColor.LIGHT_GRAY);
		c0.setBorderWidth( 0.5f );
		c0.setPadding( 2f );
		c0.addElement(e0);
		table.addCell( c0 );
		return c0;
	}

	private PdfPCell addEmptyCell(PdfPTable table) {
		return addEmptyCell(table, 1);
	}
	
	private PdfPCell addEmptyCell(PdfPTable table, int colspan) {
		Paragraph e1  = new Paragraph(8,"",BODY_FONT);
		PdfPCell c1 = new PdfPCell();
		c1.setColspan(colspan);
		c1.addElement(e1);
		c1.setBorder(0);
		table.addCell(c1);
		return c1;
	}

	private class ParagraphBorder extends PdfPageEventHelper {
	    
		private boolean active = false;
		private float offset = 5;
		private float startPosition;

	    public void setActive(boolean active) {
	        this.active = active;
	    }

	    @Override
	    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
	        this.startPosition = paragraphPosition;
	    }

	    @Override
	    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
	        if (active) {
	            PdfContentByte cb = writer.getDirectContentUnder();
	            cb.rectangle(document.left(), 
            		paragraphPosition - offset,
            		document.right() - document.left(), startPosition - paragraphPosition);
	            cb.stroke();
	        }
	    }
	}
	
}
