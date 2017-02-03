package com.code.aon.webservice.warehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

public class PackingList {
	
	public static File createPdf(JSONObject json) {
		File archivoPDF = new File("aaa" + ".pdf");
		if(archivoPDF.exists()) archivoPDF.delete();
		try {
			archivoPDF.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
		
			document.open();
		
			Paragraph separator = new Paragraph();
			LineSeparator line = new LineSeparator();
	        line.setOffset(-2);
	        separator.add(line);
	        
			Paragraph title = new Paragraph("Packing List",getTitleFont());
			title.add(separator);
			document.add(title);
			document.add(new Paragraph(" "));
				
			document.add(carrierPacking(json.getJSONObject("carrier_packing")));
			document.add(new Paragraph(" "));

			JSONArray orders  = json.getJSONArray("orders");	
			for(Integer i = 0 ; i < orders.length() ; i++){
				document.add(order(orders.getJSONObject(i)));
				document.add(new Paragraph(" "));
			}
			
			Paragraph order = new Paragraph(" ");
			order.add(separator);
			document.add(order);
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
		document.close();
		return archivoPDF;
	}
	
	private static Paragraph carrierPacking(JSONObject json){
		Paragraph paragraph = new Paragraph();
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		
		PdfPTable table = new PdfPTable(4);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		PdfPCell c = new PdfPCell(new Phrase("Tipo",getFont1()));
		c.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c);
		
		PdfPCell c1 = new PdfPCell(new Phrase(json.getJSONObject("type").getString("name"),getFont2()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase("Fecha de emision",getFont1()));
		c2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c2);
		
		PdfPCell c3 = new PdfPCell(new Phrase(json.getString("issue_date"),getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c3);
		
		PdfPCell c4 = new PdfPCell(new Phrase("Serie/Numero",getFont1()));
		c4.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c4);
		
		PdfPCell c5 = new PdfPCell(new Phrase(json.getString("series") 
									 + "/" + json.getString("number"),getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c5);
		
		PdfPCell c6 = new PdfPCell(new Phrase("Referencia",getFont1()));
		c6.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c6);
		
		PdfPCell c7 = new PdfPCell(new Phrase(json.getString("carrier_reference"),getFont2()));
		c7.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c7);
		
		PdfPCell c8 = new PdfPCell(new Phrase("Empresa de Transporte",getFont1()));
		c8.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c8);
		
		PdfPCell c9 = new PdfPCell(new Phrase(json.getJSONObject("carrier").getString("name"),getFont2()));
		c9.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c9);
		
		PdfPCell c10 = new PdfPCell(new Phrase("Fecha de Entrega",getFont1()));
		c10.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c10);
		
		PdfPCell c11 = new PdfPCell(new Phrase(json.getString("delivery_date"),getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c11);
		
		PdfPCell c12 = new PdfPCell(new Phrase("Matricula",getFont1()));
		c12.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c12);
		
		PdfPCell c13 = new PdfPCell(new Phrase(json.getString("number_plate"),getFont2()));
		c13.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c13);
		
		PdfPCell c14 = new PdfPCell(new Phrase("Conductor",getFont1()));
		c14.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c14);
		
		PdfPCell c15 = new PdfPCell(new Phrase(json.getString("driver_name") +" - "
									+ json.getString("driver_document"),getFont2()));
		c15.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c15);
		
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}
	
	private static Paragraph order(JSONObject json){
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		JSONObject address = json.getJSONObject("address");
		PdfPTable destinatario = new PdfPTable(2);
		destinatario.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		float[] medidaCeldas = {1f, 5f};
		try {
			destinatario.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	
		PdfPCell c = new PdfPCell(new Phrase("Destinatario",getFont1()));
		c.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(c);
		

		PdfPCell ca = new PdfPCell(new Phrase(address.getString("name"),getFont2()));
		ca.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(ca);
		destinatario.addCell("");
		
		PdfPCell cb = new PdfPCell(new Phrase(address.getString("address"),getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		
		destinatario.addCell(cb);
		destinatario.addCell("");
		
		PdfPCell cc = new PdfPCell(new Phrase(address.getString("zip") + " " 
				   + address.getString("city") + " "
				   + address.getString("province") + " "
				   + address.getString("country"),getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		
		destinatario.addCell(cc);
		
		table.addCell(destinatario);

		PdfPCell cell1 = new PdfPCell(new Phrase("Articulo",getFont1()));
		cell1.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell2 = new PdfPCell(new Phrase("Cantidad",getFont1()));
		cell2.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable detail = new PdfPTable(2);
		detail.addCell(cell1);
		detail.addCell(cell2);
		detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		JSONArray details  = json.getJSONArray("details");	
		for(Integer i = 0 ; i < details.length() ; i++){
			PdfPCell c1 = new PdfPCell(new Phrase(details.getJSONObject(i).getString("product_code") + "-" +
					details.getJSONObject(i).getString("product_name"),getFont2()));
			c1.setBorder(PdfPCell.NO_BORDER);
			PdfPCell c2 = new PdfPCell(new Phrase(details.getJSONObject(i).getString("quantity"),getFont2()));
			c2.setBorder(PdfPCell.NO_BORDER);
			detail.addCell(c1);
			detail.addCell(c2);
		}	
		table.addCell(detail);
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}

	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static Font getFont1(){
		Font font1 = new Font();
		font1.setSize(10);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(10);
		return font2;
	}
	
}
