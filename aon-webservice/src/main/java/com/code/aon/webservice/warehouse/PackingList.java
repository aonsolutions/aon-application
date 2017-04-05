package com.code.aon.webservice.warehouse;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.server.warehouse.XMLUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PackingList {
	
	private static final Logger LOGGER  = Logger.getLogger(PackingList.class.getName());
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	
	private PackingList() {
	    throw new IllegalAccessError("Utility class");
	}
	
	public static File createPdf(JSONObject json, byte [] image) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("packingList", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			CarrierPackingType type = CarrierPackingType.values()[json.getJSONObject(MSG.CARRIER_PACKING).getJSONObject(MSG.TYPE).getInt(MSG.ID)];
			String printType = json.getString("type");
			document.open();			
			document.add(getHeader(json, image));
			document.add(new Paragraph(" "));
			document.add(getSubHeader(json, type, printType));
			document.add(new Paragraph(" "));

			JSONArray orders  = json.getJSONArray("orders");	
			Double totalPackages = 0.0;
			Double totalWeight = 0.0;
			
			for(Integer i = 0 ; i < orders.length() ; i++){				
				document.add(order(orders.getJSONObject(i), type, printType));
				if(type.equals(CarrierPackingType.WAYBILL)){				
					if(orders.getJSONObject(i).opt("total_packages") != null){
						totalPackages = totalPackages + orders.getJSONObject(i).getDouble("total_packages");
					}
					if(orders.getJSONObject(i).opt("total_weight") != null){
						totalWeight = totalWeight + orders.getJSONObject(i).getDouble("total_weight");
					}
				}
				document.add(new Paragraph(" "));
			}
			
			if(type.equals(CarrierPackingType.WAYBILL)){	
				document.add(totalQuantity(orders, totalPackages, totalWeight));
			}

			Paragraph order = new Paragraph(" ");
			order.add(getSeparator());
			document.add(order);
			if(!"reception".equals(printType)){
				String observation  = json.getJSONObject(MSG.CARRIER_PACKING).getString("observation") != null ?
						json.getJSONObject(MSG.CARRIER_PACKING).getString("observation") : "";
				com.esferalia.aon.occam.server.warehouse.CarrierPackingParams params = XMLUtils.readXml(json.getJSONObject(MSG.CARRIER_PACKING).getString("params"));
				if(params.getParam() == null){
					params.setParam(new LinkedList<>());
				}
				document.add(new Paragraph(" "));
				document.add(getObservations(observation, type, params));		
				document.add(new Paragraph(" "));
				document.add(new Paragraph(" "));
			}
			document.add(new Paragraph(new Phrase("Firma Transportista", getFont1())));					
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}
	
	// ------------------- HEADER
	
	private static PdfPTable getHeader(JSONObject json, byte [] image) throws BadElementException, MalformedURLException, IOException{
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
		header.addCell(getHeaderCompany(json));
		header.addCell(getHeaderPackingList(json.getJSONObject("carrier_packing")));
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
	
	private static PdfPTable getHeaderCompany(JSONObject json){
		PdfPTable table = new PdfPTable(1);
		PdfPCell ca = new PdfPCell(new Phrase(json.getJSONObject("address").getString("name"),getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		table.addCell(ca);
		
		PdfPCell cX = new PdfPCell(new Phrase("NIF: " + json.getJSONObject("address").getString("document"),getFont2()));
		cX.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cX);
		
		PdfPCell cb = new PdfPCell(new Phrase(json.getJSONObject("address").getString("address"),getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cb);
		
		PdfPCell cc = new PdfPCell(new Phrase(json.getJSONObject("address").getString("zip") + " " 
				   + json.getJSONObject("address").getString("city") + " "
				   + json.getJSONObject("address").getString("province") + " "
				   + json.getJSONObject("address").getString("country"),getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cc);
		return table;
	}
	
	private static PdfPTable getHeaderPackingList(JSONObject json){
		PdfPTable header3 = new PdfPTable(2);
		PdfPCell c4 = new PdfPCell(new Phrase("Número:",getFont1()));
		c4.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c4);
			
		PdfPCell c5 = new PdfPCell(new Phrase(json.getString("series_number"),getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c5);
			
		PdfPCell c2 = new PdfPCell(new Phrase("Fecha:",getFont1()));
		c2.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c2);
			
		PdfPCell c3 = new PdfPCell(new Phrase(json.getString("issue_date"),getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c3);
			
		PdfPCell c6 = new PdfPCell(new Phrase("Su Referencia:",getFont1()));
		c6.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c6);
			
		PdfPCell c7 = new PdfPCell(new Phrase(json.getString("carrier_reference"),getFont2()));
		c7.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c7);
		
		PdfPCell c8 = new PdfPCell(new Phrase("Hoja:",getFont1()));
		c8.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c8);
			
		PdfPCell c9 = new PdfPCell(new Phrase("1",getFont2()));
		c9.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c9);
		
		return header3;
	}	
	
	// ------------------- SUB-HEADER

	private static PdfPTable getSubHeader(JSONObject json, CarrierPackingType type, String printType) throws BadElementException, MalformedURLException, IOException{
		JSONObject carrierPackingJSON = json.getJSONObject("carrier_packing");
		PdfPTable subHeader = new PdfPTable(1);
        subHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        subHeader.setWidthPercentage(100);
      
        if(type.equals(CarrierPackingType.WAYBILL)){
            subHeader.addCell(getSubHeaderBoeInfo());
        }
        subHeader.addCell(getSubHeaderPackingListType(carrierPackingJSON, printType));

		PdfPCell space = new PdfPCell(new Phrase("",getFont1()));
		space.setBorder(PdfPCell.NO_BORDER);
        subHeader.addCell(space);
        
        subHeader.addCell(getSubHeaderPackingListCarrier(carrierPackingJSON, type, printType));
		return subHeader;
	}

	private static PdfPCell getSubHeaderBoeInfo() {
		String boeInfo = "DOCUMENTO DE CONTROL orden FOM/2861/13-12 2012(BOE nº 5 de 5/01/2013)";
		Paragraph title = new Paragraph(boeInfo, getBoeInfoFont());
		title.setAlignment(Element.ALIGN_CENTER);
		title.add(getSeparator());
		title.setIndentationRight(20);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		return cell;
	}
	
	private static PdfPCell getSubHeaderPackingListType(JSONObject json, String printType) {		
		String type = json.getJSONObject("type").getString("name").toUpperCase();
		if("reception".equals(printType)){
			type = "RECEPCIÓN";
		}
		Paragraph title = new Paragraph(type, getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		return cell;
	}
	
	private static PdfPTable getSubHeaderPackingListCarrier(JSONObject json, CarrierPackingType type, String printType) {
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		PdfPTable carrier = new PdfPTable(4);
		carrier.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		float[] medidaCeldas = {1.5f, 2.5f, 1f, 1f};
		try {
			carrier.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		JSONObject address = json.getJSONObject("address");
		PdfPCell cEmpty = new PdfPCell(new Phrase("",getFont1()));
		cEmpty.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell c8 = new PdfPCell(new Phrase("Empresa de Transporte:",getFont1()));
		c8.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c8);
		
		PdfPCell c9 = new PdfPCell(new Phrase(json.getJSONObject("carrier").getString("name"),getFont3()));
		c9.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c9);
		
		PdfPCell c10 = new PdfPCell(new Phrase(type.equals(CarrierPackingType.WAYBILL) ? "Entrega:" : "Recogida:",getFont3()));
		c10.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c10);
		
		PdfPCell c11 = new PdfPCell(new Phrase(json.getString("delivery_date"),getFont3()));
		c11.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c11);
		
		carrier.addCell(cEmpty);

		PdfPCell cNif = new PdfPCell(new Phrase("NIF: " + json.getString("document"),getFont2()));
		cNif.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(cNif);
		
		carrier.addCell("");
		carrier.addCell("");
		
		carrier.addCell(cEmpty);

		PdfPCell caddress2 = new PdfPCell(new Phrase(address.getString("address"),getFont2()));
		caddress2.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(caddress2);
		
		carrier.addCell("");
		carrier.addCell("");
		
		carrier.addCell(cEmpty);
		
		PdfPCell cc = new PdfPCell(new Phrase(address.getString("zip") + " " 
				   + address.getString("city") + " "
				   + address.getString("province") + " "
				   + address.getString("country"),getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(cc);
		
		carrier.addCell("");
		carrier.addCell("");
		
		PdfPCell c14 = new PdfPCell(new Phrase("Conductor:",getFont3()));
		c14.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c14);
		
		PdfPCell c15 = new PdfPCell(new Phrase(json.getString("driver_name") +" - "
									+ json.getString("driver_document"),getFont3()));
		c15.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c15);
		
		PdfPCell c12 = new PdfPCell(new Phrase("Matricula:",getFont3()));
		c12.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c12);
		
		PdfPCell c13 = new PdfPCell(new Phrase(json.getString("number_plate"),getFont3()));
		c13.setBorder(PdfPCell.NO_BORDER);
		carrier.addCell(c13);

		table.addCell(carrier);
		
		if("reception".equals(printType)){ // TODO
			PdfPTable pesos = new PdfPTable(6);
			pesos.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			
			PdfPCell brutoLabel = new PdfPCell(new Phrase("Peso Bruto:",getFont1()));
			brutoLabel.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(brutoLabel);
			
			PdfPCell bruto = new PdfPCell(new Phrase("-",getFont1()));
			bruto.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(bruto);

			PdfPCell taraLabel = new PdfPCell(new Phrase("Tara:",getFont1()));
			taraLabel.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(taraLabel);
			
			PdfPCell tara = new PdfPCell(new Phrase("-",getFont1()));
			tara.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(tara);
			
			PdfPCell netoLabel = new PdfPCell(new Phrase("Neto:",getFont1()));
			netoLabel.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(netoLabel);
			
			PdfPCell neto = new PdfPCell(new Phrase("-",getFont1()));
			neto.setBorder(PdfPCell.NO_BORDER);
			pesos.addCell(neto);
			table.addCell(pesos);
		}

		return table;
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
	
	private static Paragraph order(JSONObject json, CarrierPackingType type, String printType){
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		JSONObject address = json.getJSONObject("address");
		PdfPTable destinatario = new PdfPTable(4);
		destinatario.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		float[] medidaCeldas = {1f, 3f, 1f, 1f};
		try {
			destinatario.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	
		PdfPCell c = new PdfPCell(new Phrase(CarrierPackingType.SHIPMENT_REQUEST.equals(type) ? "Remitente" : "Destinatario",getFont1()));
		c.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(c);

		PdfPCell ca = new PdfPCell(new Phrase(address.getString("name"),getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(ca);
		String descr = type.equals(CarrierPackingType.SHIPMENT_REQUEST) ? "Pedido:" : "Albarán:";
		String val = json.getString("series_number");
		if("reception".equals(printType)){
			descr = "Albarán";
			val = json.getString("reference_code");
		}
		destinatario.addCell(new Phrase(descr,getFont1()));
		destinatario.addCell(new Phrase(val,getFont2()));
		
		destinatario.addCell("");
		PdfPCell cbc = new PdfPCell(new Phrase("NIF: " + address.getString("document"),getFont2()));
		cbc.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cbc);
		destinatario.addCell(new Phrase("Fecha:",getFont1()));
		String dstr = "";
		try {
			Date d = dateTimeFormat.parse(json.getString("issue_date"));
			dstr = dateFormat.format(d);
		} catch (JSONException | ParseException e1) {
			e1.printStackTrace();
		}
		destinatario.addCell(new Phrase(dstr,getFont2()));
		
		destinatario.addCell("");
		PdfPCell cb = new PdfPCell(new Phrase(address.getString("address"),getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cb);
		destinatario.addCell(new Phrase("reception".equals(printType) ? "" : "Su Referencia:",getFont1()));
		String val2 = json.getString("reference");
		destinatario.addCell(new Phrase("reception".equals(printType) ? "" : json.getString("reference"),getFont2()));
		
		destinatario.addCell("");
		PdfPCell cc = new PdfPCell(new Phrase(address.getString("zip") + " " 
				   + address.getString("city") + " "
				   + address.getString("province") + " "
				   + address.getString("country"),getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cc);
		destinatario.addCell("");
		destinatario.addCell("");
		table.addCell(destinatario);

		table.addCell(getDottedSeparator());
		
		PdfPCell cell1 = new PdfPCell(new Phrase("Articulo",getFont1()));
		cell1.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell2 = new PdfPCell(new Phrase("Formato",getFont1()));
		cell2.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell3 = new PdfPCell(new Phrase("Cantidad",getFont1()));
		cell3.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable detail = new PdfPTable(3);
		float[] medidaCeldas2 = {4f, 1f, 1f};
		try {
			detail.setWidths(medidaCeldas2);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		detail.addCell(cell1);
		detail.addCell(cell2);
		detail.addCell(cell3);
		detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		JSONArray details  = json.getJSONArray("details");	
		
		HashMap<String, Double> measurementMap = new HashMap<>();
		HashMap<String, Double> formatMap = new HashMap<>();

		for(Integer i = 0 ; i < details.length() ; i++){	
			StringBuilder description = new StringBuilder();
			if(details.getJSONObject(i).opt("code") != null){
				description.append("S/Ref:");
				description.append(details.getJSONObject(i).getString("code"));
				description.append(" - ");
			}
			description.append(details.getJSONObject(i).getString("description"));
			PdfPCell c1 = new PdfPCell(new Phrase(description.toString(),getFont2()));
			c1.setBorder(PdfPCell.NO_BORDER);
	
			Double measurements = details.getJSONObject(i).getDouble("measurements");
			Double units = details.getJSONObject(i).getDouble("units");
			Double quantity = details.getJSONObject(i).getDouble("quantity");
			Double format = (quantity / (units != null && units != 0.0 ? units : 1))
					/ (measurements != null && measurements != 0.0 ? measurements : 1);
			
			String formatTag = "";
			if(details.getJSONObject(i).opt("format_tag") != null){
				formatTag = details.getJSONObject(i).getString("format_tag");
				if(formatMap.containsKey(formatTag)){
					formatMap.put(formatTag, formatMap.get(formatTag) + format);
				} else formatMap.put(formatTag, format);
			}
			String measurementTag = "";
			if(details.getJSONObject(i).opt("measurements_tag") != null){
				measurementTag = details.getJSONObject(i).getString("measurements_tag");
				if(measurementMap.containsKey(measurementTag)){
					measurementMap.put(measurementTag, measurementMap.get(measurementTag) + quantity);
				} else measurementMap.put(measurementTag, quantity);
			}
			String formatStr = AonMathUtils.round(format) + " " + formatTag;
			String quantityStr = AonMathUtils.round(quantity) + " " + measurementTag;
			if(quantityStr.equals(formatStr)){
				formatStr = "";
			}
			PdfPCell c2 = new PdfPCell(new Phrase(formatStr, getFont2()));
			c2.setBorder(PdfPCell.NO_BORDER);
			PdfPCell c3 = new PdfPCell(new Phrase(quantityStr, getFont2()));
			c3.setBorder(PdfPCell.NO_BORDER);
			detail.addCell(c1);
			detail.addCell(c2);
			detail.addCell(c3);
		}	
		
		if(type.equals(CarrierPackingType.WAYBILL)){
			LinkedList<String> formatList = new LinkedList<>(formatMap.keySet());
			Integer formatCont = 0;
			LinkedList<String> measurementList = new LinkedList<>(measurementMap.keySet());
			Integer measurementCont = 0;
			Double totalPackages = json.getDouble("total_packages");
			Double totalWeight = json.getDouble("total_weight");
			
			if(totalPackages!= null && totalPackages != 0){
				PdfPCell cz1 = new PdfPCell(new Phrase("", getFont2()));
				cz1.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cz1);

				PdfPCell cy1 = new PdfPCell(new Phrase("Bultos: "+ AonMathUtils.round(totalPackages), getFont1()));
				cy1.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cy1);
			
				String str1 = "";
				if(formatList.size() > 0){
					str1 = formatList.get(0) + " " + AonMathUtils.round(formatMap.get(formatList.get(0))); 
					formatCont++;
				} else if(measurementList.size() > 0){
					str1 = measurementList.get(0) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(0))); 
					measurementCont++;
				}
				PdfPCell cx1 = new PdfPCell(new Phrase(str1, getFont1()));
				cx1.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cx1);
			}
			
			if(totalWeight != null && totalWeight != 0.0){
				PdfPCell cz2 = new PdfPCell(new Phrase("", getFont2()));
				cz2.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cz2);
				
				PdfPCell cy2 = new PdfPCell(new Phrase("Peso: " + AonMathUtils.round(totalWeight), getFont1()));
				cy2.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cy2);
				
				String str2 = "";
				if(formatList.size() > 1){
					str2 = formatList.get(1) + " " + AonMathUtils.round(formatMap.get(formatList.get(1))); 
					formatCont++;
				} else if(measurementList.size() > measurementCont){
					str2 = measurementList.get(measurementCont) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(measurementCont))); 
					measurementCont++;
				}
				PdfPCell cx2 = new PdfPCell(new Phrase(str2, getFont1()));
				cx2.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cx2);
			}
			
			for(Integer i = formatCont; i < formatList.size(); i++){
				PdfPCell czi = new PdfPCell(new Phrase("", getFont2()));
				czi.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(czi);
				
				PdfPCell cyi = new PdfPCell(new Phrase("", getFont1()));
				cyi.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cyi);

				String stri = formatList.get(i) + " " + AonMathUtils.round(formatMap.get(formatList.get(i))); 
				PdfPCell cxi = new PdfPCell(new Phrase(stri, getFont1()));
				cxi.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cxi);
			}
			
			for(Integer j = measurementCont; j < measurementList.size(); j++){
				PdfPCell czj = new PdfPCell(new Phrase("", getFont2()));
				czj.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(czj);
				
				PdfPCell cyj = new PdfPCell(new Phrase("", getFont1()));
				cyj.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cyj);

				String strj = measurementList.get(j) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(j))); 
				PdfPCell cxj = new PdfPCell(new Phrase(strj, getFont1()));
				cxj.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(cxj);
			}		
		}
		
		PdfPTable reception = new PdfPTable(2);
		PdfPCell sign = new PdfPCell(new Phrase("Firma", getFont1()));
		sign.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(sign);

		PdfPCell date = new PdfPCell(new Phrase("Fecha", getFont1()));
		date.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(date);
		
		table.addCell(detail);
		table.addCell(reception);
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}

	public static PdfPTable getObservations(String observation, CarrierPackingType type, com.esferalia.aon.occam.server.warehouse.CarrierPackingParams params){
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		table.addCell(new Paragraph(new Phrase("Observaciones: ", getFont1())));
		table.addCell(new Paragraph(new Phrase(observation, getFont2())));
		
		if(params.getParam().size() > 0){
			PdfPTable parameters = new PdfPTable(2);
			PdfPCell cp1 = new PdfPCell(new Phrase("Parámetro",getFont1()));
			cp1.setBorder(PdfPCell.NO_BORDER);
			parameters.addCell(cp1);
		
			PdfPCell cr1 = new PdfPCell(new Phrase("Resultado",getFont1()));
			cr1.setBorder(PdfPCell.NO_BORDER);
			parameters.addCell(cr1);
			
			params.getParam().stream().forEach(p -> {
				PdfPCell cp2 = new PdfPCell(new Phrase(p.getName(),getFont2()));
				cp2.setBorder(PdfPCell.NO_BORDER);
				parameters.addCell(cp2);
			
				PdfPCell cr2 = new PdfPCell(new Phrase(p.getValue(),getFont2()));
				cr2.setBorder(PdfPCell.NO_BORDER);
				parameters.addCell(cr2);
			});
			
			table.addCell(parameters);
		}
	
		return table;
	}
	
	private static HashMap<String, Double> getMeasurementMap(JSONArray orders) {
		HashMap<String, Double> measurementMap = new HashMap<>();
		for(Integer j = 0 ; j < orders.length() ; j++){
			JSONArray details  = orders.getJSONObject(j).getJSONArray("details");	
			for(Integer i = 0 ; i < details.length() ; i++){
				Double quantity = details.getJSONObject(i).getDouble("quantity");
				String measurementTag = "";
				if(details.getJSONObject(i).opt("measurements_tag") != null){
					measurementTag = details.getJSONObject(i).getString("measurements_tag");
					if(measurementMap.containsKey(measurementTag)){
						measurementMap.put(measurementTag, measurementMap.get(measurementTag) + quantity);
					} else measurementMap.put(measurementTag, quantity);
				}
			}
		}
		return measurementMap;
	}
	 
	private static HashMap<String, Double> getFormatMap(JSONArray orders) {
		HashMap<String, Double> formatMap = new HashMap<>();
		for(Integer j = 0 ; j < orders.length() ; j++){
			JSONArray details  = orders.getJSONObject(j).getJSONArray("details");	
			for(Integer i = 0 ; i < details.length() ; i++){
				Double measurements = details.getJSONObject(i).getDouble("measurements");
				Double units = details.getJSONObject(i).getDouble("units");
				Double quantity = details.getJSONObject(i).getDouble("quantity");
				Double format = (quantity / (units != null && units != 0.0 ? units : 1))
					/ (measurements != null && measurements != 0.0 ? measurements : 1);
			
				String formatTag = "";
				if(details.getJSONObject(i).opt("format_tag") != null){
					formatTag = details.getJSONObject(i).getString("format_tag");
					if(formatMap.containsKey(formatTag)){
						formatMap.put(formatTag, formatMap.get(formatTag) + format);
					} else formatMap.put(formatTag, format);
				}
			}
		}
		return formatMap;
	}
	
	private static PdfPTable totalQuantity(JSONArray orders, Double totalPackages,Double totalWeight){
		HashMap<String, Double> formatMap = getFormatMap(orders);
		HashMap<String, Double> measurementMap = getMeasurementMap(orders);

		LinkedList<String> formatList = new LinkedList<>(formatMap.keySet());
		Integer formatCont = 0;
		LinkedList<String> measurementList = new LinkedList<>(measurementMap.keySet());
		Integer measurementCont = 0;

		PdfPTable t = new PdfPTable(3);
		float[] medidaCeldas = {4f, 1f, 1f};
		try {
			t.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		t.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		t.setWidthPercentage(100);
		
	
		if(totalPackages != null && totalPackages != 0.0){
			t.addCell("");
			t.addCell(new Phrase("Bultos: " + AonMathUtils.round(totalPackages), getFont1()));
			String str1 = "";
			if(formatList.size() > 0){
				str1 = formatList.get(0) + " " + AonMathUtils.round(formatMap.get(formatList.get(0))); 
				formatCont++;
			} else if(measurementList.size() > 0){
				str1 = measurementList.get(0) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(0))); 
				measurementCont++;
			}
			PdfPCell cx1 = new PdfPCell(new Phrase(str1, getFont1()));
			cx1.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cx1);
		}
		if(totalWeight != null && totalWeight != 0.0){
			t.addCell("");
			t.addCell(new Phrase("Peso: " +  AonMathUtils.round(totalWeight), getFont1()));
			String str2 = "";
			if(formatList.size() > 1){
				str2 = formatList.get(1) + " " + AonMathUtils.round(formatMap.get(formatList.get(1))); 
				formatCont++;
			} else if(measurementList.size() > measurementCont){
				str2 = measurementList.get(measurementCont) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(measurementCont))); 
				measurementCont++;
			}
			PdfPCell cx2 = new PdfPCell(new Phrase(str2, getFont1()));
			cx2.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cx2);
		}
		for(Integer i = formatCont; i < formatList.size(); i++){
			t.addCell("");
			t.addCell("");
			String stri = formatList.get(i) + " " + AonMathUtils.round(formatMap.get(formatList.get(i))); 
			PdfPCell cxi = new PdfPCell(new Phrase(stri, getFont1()));
			cxi.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cxi);
		}
		for(Integer j = measurementCont; j < measurementList.size(); j++){
			t.addCell("");
			t.addCell("");

			String strj = measurementList.get(j) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(j))); 
			PdfPCell cxj = new PdfPCell(new Phrase(strj, getFont1()));
			cxj.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cxj);
		}	
		return t;
	}

	// ------------------- FONTS
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static Font getBoeInfoFont(){
		Font font = new Font();
		font.setSize(13);
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
}
