package net.aonsolutions.aon.gwt.udapa.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import net.aonsolutions.aon.gwt.udapa.shared.quality.Clean;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CleanAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CulinaryAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Defects;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Plague;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetConstants;

public class printQuality {
	
	private static final Logger LOGGER  = Logger.getLogger(printQuality.class.getName());
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	
	private printQuality() {
	    throw new IllegalAccessError("Utility class");
	}
	
	public static File createPdf(HashMap<String, String> map, byte[] logo,  LinkedList<byte[]> images) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("quality", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			document.open();			
			document.add(getHeader(map, logo));
			document.add(new Paragraph(" "));
			document.add(getSubHeader(map));
			document.add(new Paragraph(" "));

			document.add(transportTable(map));
			document.add(new Paragraph(" "));
			document.add(productTable(map));
			document.add(new Paragraph(" "));
			document.add(qualityTable(map));
			document.add(new Paragraph(" "));
			document.add(caliberTable(map));
			document.add(new Paragraph(" "));
			document.add(observationTable(map));
			document.add(new Paragraph(" "));

			document.add(imageTable(images));

		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}
	
	// ------------------- HEADER
	
	private static PdfPTable getHeader(HashMap<String, String> map, byte [] image) throws BadElementException, MalformedURLException, IOException{
        PdfPTable header = new PdfPTable(3);
        float[] medidaCeldas = {0.75f, 1.25f, 1f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.setWidthPercentage(100);
      
        header.addCell(getHeaderLogo(image));
        
		header.addCell(getHeaderCompany(map));
		header.addCell(getHeaderRmedia(map));
		return header;
	}
	
	private static PdfPCell getHeaderLogo(byte [] image) throws BadElementException, MalformedURLException, IOException{
		Image i1 = Image.getInstance(image);
		
		float percentage = 0;
		if(i1.getWidth() > i1.getHeight()){
			percentage = 100 / i1.getWidth();
		} else percentage = 100 / i1.getHeight();
		
		Float width = i1.getWidth() * percentage;
		Float height = i1.getHeight() * percentage;
		
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
		
		Image logo = Image.getInstance(img, null);
		logo.scaleAbsolute(width, height);
		PdfPCell headerLogo = new PdfPCell(logo, false);
		headerLogo.setBorder(PdfPCell.NO_BORDER);
		return headerLogo;
	}
	
	private static PdfPTable getHeaderCompany(HashMap<String, String> map){
		PdfPTable table = new PdfPTable(1);
		PdfPCell ca = new PdfPCell(new Phrase(map.get("registry_name"),getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		table.addCell(ca);
		
		PdfPCell cX = new PdfPCell(new Phrase("NIF: " + map.get("registry_document"),getFont2()));
		cX.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cX);
		
		PdfPCell cb = new PdfPCell(new Phrase(map.get("registry_full_address"),getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cb);
		
		PdfPCell cc = new PdfPCell(new Phrase(map.get("registry_end_address"),getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cc);
		return table;
	}
	
	private static PdfPTable getHeaderRmedia(HashMap<String, String> rmedia){
		PdfPTable header3 = new PdfPTable(2);
		float[] medidaCeldas = {1f, 2f};
		try {
			header3.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		PdfPCell c4 = new PdfPCell(new Phrase("Teléfono:",getFont2()));
		c4.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c4);
			
		PdfPCell c5 = new PdfPCell(new Phrase(rmedia.get("phone"),getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c5);
			
		PdfPCell c2 = new PdfPCell(new Phrase("Fax:",getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c2);
			
		PdfPCell c3 = new PdfPCell(new Phrase(rmedia.get("fax"),getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c3);
			
		PdfPCell c6 = new PdfPCell(new Phrase("Correo:",getFont2()));
		c6.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c6);
			
		PdfPCell c7 = new PdfPCell(new Phrase(rmedia.get("mail"),getFont2()));
		c7.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c7);
		
		PdfPCell c8 = new PdfPCell(new Phrase("Web:",getFont2()));
		c8.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c8);
			
		PdfPCell c9 = new PdfPCell(new Phrase(rmedia.get("web"),getFont2()));
		c9.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c9);
		
		return header3;
	}	
	
	private static PdfPTable getHeaderWarehouse(HashMap<String, String> ware) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String date = "-";
		String hour = "-";
		String transportDeliveryDate = ware.get("transport_delivery_date");
		if(transportDeliveryDate != null && !"-".equals(transportDeliveryDate)){
			try {
				Date deliveryDate = dateTimeFormat.parse(transportDeliveryDate);
				date = dateFormat.format(deliveryDate);
				hour = timeFormat.format(deliveryDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		PdfPTable header3 = new PdfPTable(2);
		float[] medidaCeldas = {1f, 2f};
		try {
			header3.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		PdfPCell c4 = new PdfPCell(new Phrase("Almacén:",getFont1()));
		c4.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c4);
			
		PdfPCell c5 = new PdfPCell(new Phrase(ware.get("warehouse"),getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c5);
			
		PdfPCell c2 = new PdfPCell(new Phrase("Número:",getFont1()));
		c2.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c2);
			
		PdfPCell c3 = new PdfPCell(new Phrase(ware.get("number"),getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c3);
			
		PdfPCell c6 = new PdfPCell(new Phrase("Fecha:",getFont1()));
		c6.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c6);
			
		PdfPCell c7 = new PdfPCell(new Phrase(date,getFont2()));
		c7.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c7);
		
		PdfPCell c8 = new PdfPCell(new Phrase("Hora:",getFont1()));
		c8.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c8);
			
		PdfPCell c9 = new PdfPCell(new Phrase(hour,getFont2()));
		c9.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c9);
		
		return header3;
	}	
	
	// ------------------- SUB-HEADER

	private static PdfPTable getSubHeader(HashMap<String, String> map) {
		PdfPTable subHeader = new PdfPTable(2);
		float[] medidaCeldas = {2f, 1f};
		try {
			subHeader.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        subHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        subHeader.setWidthPercentage(100);
     
        subHeader.addCell(getSubHeaderTitle("FICHA DE CALIDAD"));
        subHeader.addCell(getHeaderWarehouse(map));

		return subHeader;
	}
	
	private static PdfPCell getSubHeaderTitle(String titleStr) {		
		Paragraph title = new Paragraph(titleStr, getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		return cell;
	}
	

	static class Title implements PdfPCellEvent {
        protected String title;
 
        public Title(String title) {
            this.title = title;
        }
 
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            Chunk c = new Chunk(title);
            c.setBackground(BaseColor.WHITE);
            c.setFont(getTableTitleFont());
            PdfContentByte canvas = canvases[PdfPTable.TEXTCANVAS];
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, 
                new Phrase(c), position.getLeft(5), position.getTop(3), 0);
            
            PdfContentByte canvas2 = canvases[PdfPTable.LINECANVAS];
            canvas2.setLineCap(PdfContentByte.LINE_CAP_ROUND);
            canvas2.setLineDash(0, 1.5f, 0.5f);
            canvas2.rectangle(position.getLeft(), position.getBottom(),
                position.getWidth(), position.getHeight());
            canvas2.stroke();
        }
    }
    
    public static PdfPCell getCell(PdfPTable content, String title) {
        PdfPCell cell = new PdfPCell(content);
        cell.setCellEvent(new Title(title));
        cell.setPadding(5);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPTable transportTable(HashMap<String, String> map){
    	PdfPTable content = new PdfPTable(4);
    	
    	float[] medidaCeldas = {1f, 3f, 1f, 1F};
		try {
			content.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
    	
    	PdfPCell c1 = new PdfPCell(new Phrase("Agencia:",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(map.get("transport_carrier"),getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2);

	   	PdfPCell c5 = new PdfPCell(new Phrase("Bruto:",getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c5);
			
		PdfPCell c6 = new PdfPCell(new Phrase(map.get("bruto"),getFont2()));
		c6.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c6);
		
    	
    	PdfPCell c11 = new PdfPCell(new Phrase("Conductor:",getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c11);
		
		PdfPCell c22 = new PdfPCell(new Phrase(map.get("transport_driver_name"),getFont2()));
		c22.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c22);

	   	PdfPCell c55 = new PdfPCell(new Phrase("Tara:",getFont2()));
		c55.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c55);
			
		PdfPCell c66 = new PdfPCell(new Phrase(map.get("tara"),getFont2()));
		c66.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c66);
		
    	
    	PdfPCell c111 = new PdfPCell(new Phrase("DNI:",getFont2()));
		c111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c111);
		
		PdfPTable tAux = new PdfPTable(3);
		PdfPCell c222 = new PdfPCell(new Phrase(map.get("transport_driver_document"),getFont2()));
		c222.setBorder(PdfPCell.NO_BORDER);
		tAux.addCell(c222);

		PdfPCell c333 = new PdfPCell(new Phrase("Matrícula:",getFont2()));
		c333.setBorder(PdfPCell.NO_BORDER);
		tAux.addCell(c333);

		PdfPCell c444 = new PdfPCell(new Phrase(map.get("transport_number_plate"),getFont2()));
		c444.setBorder(PdfPCell.NO_BORDER);
		tAux.addCell(c444);
		
		PdfPCell cell = new PdfPCell(tAux);
		cell.setBorder(PdfPCell.NO_BORDER);
		content.addCell(cell);

		PdfPCell c555 = new PdfPCell(new Phrase("Tara Adicional:",getFont2()));
		c555.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c555);
			
		PdfPCell c666 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQDT1.getName()),getFont2()));
		c666.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c666);

	   	PdfPCell c1111 = new PdfPCell(new Phrase("",getFont1()));
	   	c1111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1111);
			
		PdfPCell c2222 = new PdfPCell(new Phrase("",getFont1()));
		c2222.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2222);
		
	   	PdfPCell c5555 = new PdfPCell(new Phrase("Peso Neto:",getFont1()));
	   	c5555.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c5555);
			
		PdfPCell c6666 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQDT2.getName()),getFont1()));
		c6666.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c6666);
		
    	PdfPTable table = new PdfPTable(1);
    	table.setWidthPercentage(90);
    	table.addCell(getCell(content, " Datos Transporte "));
    	return table;
    }
    
    public static PdfPTable productTable(HashMap<String, String> map){
    	PdfPTable content = new PdfPTable(4);
    	
    	float[] medidaCeldas = {1f, 3f, 1f, 1f};
		try {
			content.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
    	
    	PdfPCell c1 = new PdfPCell(new Phrase("Producto:",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(map.get("product_description"),getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2);

	   	PdfPCell c5 = new PdfPCell(new Phrase("Cantidad:",getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c5);
			
		PdfPCell c6 = new PdfPCell(new Phrase(map.get("product_quantity"),getFont2()));
		c6.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c6);
		
		
    	PdfPCell c11 = new PdfPCell(new Phrase("Proveedor:",getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c11);
		
		PdfPCell c22 = new PdfPCell(new Phrase(map.get("product_supplier"),getFont2()));
		c22.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c22);

	   	PdfPCell c55 = new PdfPCell(new Phrase("Destino:",getFont2()));
		c55.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c55);
			
		Double index = Double.parseDouble(map.get(QualitySheetCode.UFQDP1.getName())) - 1;
		String str = (index >= 0.0) ? Destiny.values()[index.intValue()].getName() : "-";
		PdfPCell c66 = new PdfPCell(new Phrase(str,getFont2()));
		c66.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c66);
		
    	PdfPCell c111 = new PdfPCell(new Phrase("Origen:",getFont2()));
		c111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c111);
		
		PdfPCell c222 = new PdfPCell(new Phrase(map.get("full_address"),getFont2()));
		c222.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c222);

	   	PdfPCell c555 = new PdfPCell(new Phrase("Rechazado:",getFont2()));
		c555.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c555);
			
		PdfPCell c666 = new PdfPCell(new Phrase( "0".equals(map.get(QualitySheetCode.UFQDP2.getName()))
				|| "0".equals(map.get(QualitySheetCode.UFQDP2.getName())) ? "No" : "Si", getFont2()));
		c666.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c666);
		
		PdfPCell c1111 = new PdfPCell(new Phrase("",getFont2()));
		c1111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1111);
		
		PdfPCell c2222 = new PdfPCell(new Phrase(map.get("end_address"),getFont2()));
		c2222.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2222);
		
		PdfPCell c5555 = new PdfPCell(new Phrase("",getFont2()));
		c5555.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c5555);
		
		PdfPCell c6666 = new PdfPCell(new Phrase("",getFont2()));
		c6666.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c6666);
		
    	PdfPTable table = new PdfPTable(1);
    	table.setWidthPercentage(90);
    	table.addCell(getCell(content, " Datos Producto "));
    	return table;
    }
    
    public static PdfPTable qualityTable(HashMap<String, String> map){   
    	PdfPTable content = new PdfPTable(4);
    	
    	PdfPCell c1 = new PdfPCell(new Phrase("Temperatura:",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQAC1.getName()),getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2);

		PdfPCell c3 = new PdfPCell(new Phrase("Aptitud Culinaria para Fritura",getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c3);

		Double index = Double.parseDouble(map.get(QualitySheetCode.UFQAC2.getName())) - 1;
		String str1 = (index >= 0.0) ? CulinaryAptitude.values()[index.intValue()].getName() : "-";
		PdfPCell c4 = new PdfPCell(new Phrase(str1,getFont2()));
		c4.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c4);

	   	PdfPCell c11 = new PdfPCell(new Phrase("Plaga:",getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c11);
			
		index = Double.parseDouble(map.get(QualitySheetCode.UFQAC5.getName())) - 1;
		String str2 = (index >= 0.0) ? Plague.values()[index.intValue()].getName() : "-";
		PdfPCell c22 = new PdfPCell(new Phrase(str2,getFont2()));
		c22.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c22);
		
		PdfPCell c33 = new PdfPCell(new Phrase("Aptitud Culinaria para Cocido",getFont2()));
		c33.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c33);

		index = Double.parseDouble(map.get(QualitySheetCode.UFQAC4.getName())) - 1;
		String str3 = (index >= 0.0) ? CulinaryAptitude.values()[index.intValue()].getName() : "-";
		PdfPCell c44 = new PdfPCell(new Phrase(str3,getFont2()));
		c44.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c44);
		
	   	PdfPCell c111 = new PdfPCell(new Phrase("Limpieza:",getFont2()));
		c111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c111);
			
		index = Double.parseDouble(map.get(QualitySheetCode.UFQAC3.getName())) - 1;
		String str4 = (index >= 0.0) ? Clean.values()[index.intValue()].getName() : "-";
		PdfPCell c222 = new PdfPCell(new Phrase(str4,getFont2()));
		c222.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c222);
		
		PdfPCell c333 = new PdfPCell(new Phrase("Aptitud Lavado",getFont2()));
		c333.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c333);

		index = Double.parseDouble(map.get(QualitySheetCode.UFQAC6.getName())) - 1;
		String str5 = (index >= 0.0) ? CleanAptitude.values()[index.intValue()].getName() : "-";
		PdfPCell c444 = new PdfPCell(new Phrase(str5,getFont2()));
		c444.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c444);
		
	   	PdfPCell c1111 = new PdfPCell(new Phrase("Materia Seca:",getFont2()));
		c1111.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1111);
			
		PdfPCell c2222 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQAC7.getName()),getFont2()));
		c2222.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2222);
		
		PdfPCell c3333 = new PdfPCell(new Phrase(isPropaco(map) ? "Color" : "",getFont2()));
		c3333.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c3333);

		String color = "0".equals(map.get(QualitySheetCode.UFQAC8.getName())) ? "No" : "Si";
		PdfPCell c4444 = new PdfPCell(new Phrase(isPropaco(map) ? color : "",getFont2()));
		c4444.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c4444);
		
    	PdfPTable table = new PdfPTable(1);
    	table.setWidthPercentage(90);
    	table.addCell(getCell(content, " Análisis Calidad "));
    	return table;
    }
    
    public static PdfPTable caliberTable(HashMap<String, String> map){
    	PdfPTable content = new PdfPTable(2);
    	
    	Double index = Double.parseDouble(map.get(QualitySheetCode.UFQDP1.getName())) - 1;
		String str = (index >= 0.0) ? Destiny.values()[index.intValue()].getName() : "-";
		if(Destiny.SIEMBRA.getName().equalsIgnoreCase(str)){
    		PdfPCell c1 = new PdfPCell(caliberSiembraTable(map));
    		c1.setBorder(PdfPCell.NO_BORDER);
    		content.addCell(c1);
    	} else {
    		PdfPCell c1 = new PdfPCell(caliberConsumoTable(map));
    		c1.setBorder(PdfPCell.NO_BORDER);
    		content.addCell(c1);
    	}
		PdfPCell c2 = new PdfPCell(defectTable(map));
		c2.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2);

    	PdfPTable table = new PdfPTable(1);
    	table.setWidthPercentage(90);
    	table.addCell(getCell(content, " Control Calibres y Defectos "));
    	return table;
    }
    
    public static PdfPTable caliberSiembraTable(HashMap<String, String> map){
		String[] calibresSiembra ={
				"Calibre 25-40:",
				"Calibre 28-35:",
				"Calibre 35-45:",
				"Calibre 40-50:",
				"Calibre 45-50:",
				"Calibre 45-55:",
				"Calibre 50-55:",
				"Calibre > 55:",
				"Sin Calibrar:",
				"Tierras Piedra:"
		};
		
    	PdfPTable table = new PdfPTable(3);
    	float[] medidaCeldas = {1f, 1f, 2f};
		try {
			table.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
    	
    	PdfPCell c1 = new PdfPCell(new Phrase("Peso Muestra:",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC01.getName()),getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c2);

		PdfPCell c3 = new PdfPCell(new Phrase("",getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c3);
		
    	PdfPCell c11 = new PdfPCell(new Phrase("",getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c11);
		
		PdfPCell c22 = new PdfPCell(new Phrase("Peso",getFont1()));
		c22.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c22);

		PdfPCell c33 = new PdfPCell(new Phrase("%",getFont1()));
		c33.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c33);
		

		for(Integer i = 0 ; i < QualitySheetConstants.CALIBER_SIEMBRA.length ; i = i + 2){
			Integer c = i/2;
			
			PdfPCell ci = new PdfPCell(new Phrase(calibresSiembra[c],getFont2()));
			ci.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ci);
			
			PdfPCell cii = new PdfPCell(new Phrase(map.get(QualitySheetConstants.CALIBER_SIEMBRA[i].getName()),getFont2()));
			cii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cii);
			
			PdfPCell ciii = new PdfPCell(new Phrase(map.get(QualitySheetConstants.CALIBER_SIEMBRA[i + 1].getName()),getFont2()));
			ciii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ciii);
		}
		
		PdfPCell c111 = new PdfPCell(new Phrase("Total",getFont1()));
		c111.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c111);
		
		PdfPCell c222 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC15.getName()),getFont1()));
		c222.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c222);
		
		PdfPCell c333 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC151.getName()),getFont1()));
		c333.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c333);
		
    	return table;
    }
    
    public static PdfPTable caliberConsumoTable(HashMap<String, String> map){
    	String[] calibresConsumo ={
				"Calibre < 45:",
				"Calibre 45-50:",
				"Calibre > 80:",
				"Sin Calibrar:",
				"Tierras Piedra:"
		};
    	
    	PdfPTable table = new PdfPTable(3);

    	PdfPCell c1 = new PdfPCell(new Phrase("Peso Muestra:",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC01.getName()),getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c2);

		PdfPCell c3 = new PdfPCell(new Phrase("",getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c3);
		
    	PdfPCell c11 = new PdfPCell(new Phrase("",getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c11);
		
		PdfPCell c22 = new PdfPCell(new Phrase("Peso",getFont1()));
		c22.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c22);

		PdfPCell c33 = new PdfPCell(new Phrase("%",getFont1()));
		c33.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c33);
		
		QualitySheetCode[] qsc = isPropaco(map) ? QualitySheetConstants.CALIBER_PROPACO : QualitySheetConstants.CALIBER;
		for(Integer i = 0 ; i < qsc.length ; i = i + 2){
			Integer c = i/2;
			
			PdfPCell ci = new PdfPCell(new Phrase(calibresConsumo[c],getFont2()));
			ci.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ci);
			
			PdfPCell cii = new PdfPCell(new Phrase(map.get(qsc[i].getName()),getFont2()));
			cii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cii);
			
			PdfPCell ciii = new PdfPCell(new Phrase(map.get(qsc[i + 1].getName()),getFont2()));
			ciii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ciii);
		}
		
		PdfPCell c111 = new PdfPCell(new Phrase("Total",getFont1()));
		c111.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c111);
		
		PdfPCell c222 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC14.getName()),getFont1()));
		c222.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c222);
		
		PdfPCell c333 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCC141.getName()),getFont1()));
		c333.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c333);
		
    	return table;
    }
 
 	public static PdfPTable defectTable(HashMap<String, String> map){
    	PdfPTable table = new PdfPTable(3);

    	PdfPCell c1 = new PdfPCell(new Phrase(" ",getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase(" ",getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c2);

		PdfPCell c3 = new PdfPCell(new Phrase(" ",getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c3);
		
    	PdfPCell c11 = new PdfPCell(new Phrase("Defectos",getFont1()));
		c11.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c11);
		
		PdfPCell c22 = new PdfPCell(new Phrase("Peso",getFont1()));
		c22.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c22);

		PdfPCell c33 = new PdfPCell(new Phrase("%",getFont1()));
		c33.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c33);
		
		QualitySheetCode[] qsc = isPropaco(map) ? QualitySheetConstants.DEFECTS_PROPACO : QualitySheetConstants.DEFECTS;
		for(Integer i = 0; i < qsc.length; i = i + 2){
			Integer c = i/2;
			
			PdfPCell ci = new PdfPCell(new Phrase(Defects.values()[c].getName(),getFont2()));
			ci.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ci);
			
			PdfPCell cii = new PdfPCell(new Phrase(map.get(qsc[i].getName()),getFont2()));
			cii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cii);
			
			PdfPCell ciii = new PdfPCell(new Phrase(map.get(qsc[i+1].getName()),getFont2()));
			ciii.setBorder(PdfPCell.NO_BORDER);
			table.addCell(ciii);
		}
		
		PdfPCell c111 = new PdfPCell(new Phrase("Total",getFont1()));
		c111.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c111);
		
		PdfPCell c222 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCD11.getName()),getFont1()));
		c222.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c222);
		
		PdfPCell c333 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQCD111.getName()),getFont1()));
		c333.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c333);
		
    	return table;
 	}
    
    public static PdfPTable observationTable(HashMap<String, String> map){
    	PdfPTable content = new PdfPTable(1);
    	
    	PdfPCell c1 = new PdfPCell(new Phrase(map.get(QualitySheetCode.UFQO.getName()),getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c1);
		
    	PdfPCell c2 = new PdfPCell(new Phrase(" ",getFont2()));
		c2.setBorder(PdfPCell.NO_BORDER);
		content.addCell(c2);
		
    	PdfPTable table = new PdfPTable(1);
    	table.setWidthPercentage(90);
    	table.addCell(getCell(content, " Observaciones "));
    	return table;
    }

    public static PdfPTable imageTable(LinkedList<byte[]> images){
    	PdfPTable content = new PdfPTable(4);
    	
    	for(Integer i = 0; i < images.size(); i++){
    		try {
				content.addCell(getHeaderLogo(images.get(i)));
			} catch (BadElementException | IOException e) {
				e.printStackTrace();
			}  
    	}
    	Integer j = 4 - (images.size() % 4);
    	for(Integer i = 0 ; i < j; i++){
    		PdfPCell cell = new PdfPCell();
    		cell.setBorder(PdfPCell.NO_BORDER);
    		content.addCell(cell);
    	}
    	
    	return content;
    }
    
	private static Paragraph getSeparator(){
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
        line.setOffset(-2);
        separator.add(line);
        return separator;
	}
	private static Paragraph getDottedSeparator(){
		Paragraph p = new Paragraph();
	    DottedLineSeparator dottedline = new DottedLineSeparator();
	    dottedline.setOffset(5);
	    dottedline.setGap(2f);
	    p.add(dottedline);
        return p;
	}

	// ------------------- FONTS
	private static Font getTableTitleFont(){
		Font font = new Font();
		font.setSize(12);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static Font getFont1(){
		Font font1 = new Font();
		font1.setSize(8);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(8);
		return font2;
	}

	private static Font getFont3(){
		Font font1 = new Font();
		font1.setSize(12);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	
	private static Boolean isPropaco(Map<String, String> map) {
		return map.containsKey(QualitySheetCode.UFQDP1.getName()) && 
			(map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.BASERRI.ordinal() + 1))
			|| map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.EUSKOLABEL.ordinal() + 1)));
	}

}
