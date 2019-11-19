package com.esferalia.aon.gwt.template.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Utils {

	@Deprecated
	public static TemplateInfo readxml(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();
	 	 
		Element root = doc.getDocumentElement();
		String type = root.getAttribute("type");
		
		NodeList nList = doc.getElementsByTagName("column");
	 
		TemplateInfo ti = new TemplateInfo();
		ti.sethasWarehouse(false);
		LinkedList<String> columns = new LinkedList<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String column = nList.item(temp).getTextContent();
			if(column.equals("Almac\u00e9n Destino"))
				ti.sethasWarehouse(true);
			columns.add(column);
		}
		
		ti.setType(type);
		ti.setColumns(columns);
		
		return ti;
	}
	
	public static TemplateInfo readxml(InputStream fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();
	 	 
		Element root = doc.getDocumentElement();
		String type = root.getAttribute("type");
		
		NodeList nList = doc.getElementsByTagName("column");
	 
		TemplateInfo ti = new TemplateInfo();
		ti.sethasWarehouse(false);
		LinkedList<String> columns = new LinkedList<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String column = nList.item(temp).getTextContent();
			if(column.equals("Almac\u00e9n Destino"))
				ti.sethasWarehouse(true);
			columns.add(column);
		}
		
		ti.setType(type);
		ti.setColumns(columns);
		
		return ti;
	}
	
	@Deprecated
	public static TemplateInfo readxmlWithVersion(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		doc.getDocumentElement().normalize();
	 	 
		Element root = doc.getDocumentElement();
		String type = root.getAttribute("type");
		String version = root.getAttribute("version");
		
		NodeList nList = doc.getElementsByTagName("column");
	 
		TemplateInfo ti = new TemplateInfo();
		ti.sethasWarehouse(false);
		LinkedList<String> columns = new LinkedList<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String column = nList.item(temp).getTextContent();
			if(column.equals("Almac\u00e9n Destino"))
				ti.sethasWarehouse(true);
			columns.add(column);
		}
		ti.setVersion(version);
		ti.setType(type);
		ti.setColumns(columns);
		
		return ti;
	}
	
	public static TemplateInfo readxmlWithVersion(InputStream fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		doc.getDocumentElement().normalize();
	 	 
		Element root = doc.getDocumentElement();
		String type = root.getAttribute("type");
		String version = root.getAttribute("version");
		
		NodeList nList = doc.getElementsByTagName("column");
	 
		TemplateInfo ti = new TemplateInfo();
		ti.sethasWarehouse(false);
		LinkedList<String> columns = new LinkedList<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String column = nList.item(temp).getTextContent();
			if(column.equals("Almac\u00e9n Destino"))
				ti.sethasWarehouse(true);
			columns.add(column);
		}
		ti.setVersion(version);
		ti.setType(type);
		ti.setColumns(columns);
		
		return ti;
	}
	
	public static byte[] newXmlFile(TemplateInfo ti) {
		/*<?xml version="1.0" encoding="UTF-8" standalone="no"?>
		<template name="lalala" type="product" >
			<columns>
				<column id = 1>

				</column>
				.....
				<column id = N>

				</column>
			</columns>
		</template>*/

		byte[] b = null;
		  try {
	 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("template");
			doc.appendChild(rootElement);
	 
			// set attribute to staff element
			Attr attr = doc.createAttribute("name");
			attr.setValue(ti.getName());
			
			Attr attr2 = doc.createAttribute("type");
			attr2.setValue(ti.getType());
			
			rootElement.setAttributeNode(attr);
			rootElement.setAttributeNode(attr2);
	 
			// shorten way
			// staff.setAttribute("id", "1");
	 
			// firstname elements
			Element columns = doc.createElement("columns");
			rootElement.appendChild(columns);
	 
			for (String value : ti.getColumns()) {
				Element column = doc.createElement("column");
				columns.appendChild(column);
				column.appendChild(doc.createTextNode(value));
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
				
			
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);
	 
			transformer.transform(source, result);
			
			b = bos.toByteArray();
			
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		  return b;
	}
		
	public static Date stringToDate(String s){
		DateFormat df = DateFormat.getInstance();
		Date date = null;
		try {
			date = df.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return  date;
	}
	
	public static Date stringToDateBilling(String s){
		//junio/2015
		//junio/15
		//10/2015
		//10/15
		//junio-2015
		//junio-15
		//10-2015
		//10-15
			
		String mes = null;
		String anho = null;
		Date d = null;
		if(s.contains("/")){
			Integer pos = s.indexOf("/"); 
			mes = s.substring(0,pos);
			anho = s.substring(pos+1);
		}
		else if(s.contains("-")){
			Integer pos = s.indexOf("-"); 
			mes = s.substring(0,pos);
			anho = s.substring(pos+1);
		}
		if(mes != null && anho != null){
			Integer day = 1;
			Integer month = getMes(mes);
			Integer year = getAnho(anho);
			
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day);
			d = cal.getTime();
		}
		if(d == null){
			d = stringToDate(s);
		}
		return d;
	}
	
	
	private static Integer getMes(String mes) {
		if(mes.equalsIgnoreCase("enero") || mes.equals("1") || mes.equals("01")) return 0;
		if(mes.equalsIgnoreCase("febrero") || mes.equals("2") || mes.equals("02")) return 1;
		if(mes.equalsIgnoreCase("marzo") || mes.equals("3") || mes.equals("03")) return 2;
		if(mes.equalsIgnoreCase("abril") || mes.equals("4") || mes.equals("04")) return 3;
		if(mes.equalsIgnoreCase("mayo") || mes.equals("5") || mes.equals("05")) return 4;
		if(mes.equalsIgnoreCase("junio") || mes.equals("6") || mes.equals("06")) return 5;
		if(mes.equalsIgnoreCase("julio") || mes.equals("7") || mes.equals("07")) return 6;
		if(mes.equalsIgnoreCase("agosto") || mes.equals("8") || mes.equals("08")) return 7;
		if(mes.equalsIgnoreCase("septiembre") || mes.equals("9") || mes.equals("09")) return 8;
		if(mes.equalsIgnoreCase("octubre") || mes.equals("10") || mes.equals("10")) return 9;
		if(mes.equalsIgnoreCase("noviembre") || mes.equals("11") || mes.equals("11")) return 10;
		if(mes.equalsIgnoreCase("diciembre") || mes.equals("12") || mes.equals("12")) return 11;
			
		return null;
	}
	
	private static Integer getAnho(String anho){
		if(anho.length() == 2 || anho.length() == 4)
			return Integer.parseInt(anho);		
		return null;
	}
	
	public static String getColumn(Integer integer){
		switch (integer) {
		case 0: return "A";
		case 1: return "B";
		case 2: return "C";
		case 3: return "D";
		case 4: return "E";
		case 5: return "F";
		case 6: return "G";
		case 7: return "H";
		case 8: return "I";
		case 9: return "J";
		case 10: return "K";
		case 11: return "L";
		case 12: return "M";
		case 13: return "N";
		case 14: return "O";
		case 15: return "P";
		case 16: return "Q";
		case 17: return "R";
		case 18: return "S";
		case 19: return "T";
		case 20: return "U";
		case 21: return "V";
		case 22: return "W";
		case 23: return "X";
		case 24: return "Y";
		case 25: return "Z";
		default:
			return integer.toString();
		}
		
	}
	
	public static String getColumn(Short s){
		switch (s) {
		case 0: return "A";
		case 1: return "B";
		case 2: return "C";
		case 3: return "D";
		case 4: return "E";
		case 5: return "F";
		case 6: return "G";
		case 7: return "H";
		case 8: return "I";
		case 9: return "J";
		case 10: return "K";
		case 11: return "L";
		case 12: return "M";
		case 13: return "N";
		case 14: return "O";
		case 15: return "P";
		case 16: return "Q";
		case 17: return "R";
		case 18: return "S";
		case 19: return "T";
		case 20: return "U";
		case 21: return "V";
		case 22: return "W";
		case 23: return "X";
		case 24: return "Y";
		case 25: return "Z";
		default:
			return s.toString();
		}
		
	}
	
	public static Boolean isExcel(String mimetype) {
	
		switch (mimetype) {
		case "application/vnd.ms-excel":
		case "application/msexcel":
		case "application/x-msexcel":
		case "application/x-ms-excel":
		case "application/x-excel":
		case "application/x-dos_ms_excel":
		case "application/xls":
		case "application/x-xls":
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
	//	case "application/vnd.oasis.opendocument.spreadsheet":
			return true;
		default:
			return false;
		}
		
	}
	
	public static Double getValCost(String domainName, Double quantity, Item item, String login, Integer workplaceId, Integer warehouseId, Date inventoryDate, ApplicationParameter ap){
		if(ap != null && ap.getValue() != null && !"0".equals(ap.getValue())) {
			if("1".equals(ap.getValue())) return getLastPurchasePrice(domainName, item, quantity, login, workplaceId, warehouseId, inventoryDate);
			else if("2".equals(ap.getValue())) return getAveragePurchasePrice(domainName, item, quantity, login, workplaceId, warehouseId, inventoryDate);
			else if("3".equals(ap.getValue())) return getFifoPrice(domainName, item, quantity, login, workplaceId, warehouseId, inventoryDate);
		} 
		return quantity != 0 ? item.getPurchasePrice() : 0.0;
	}
	
	public static Double getLastPurchasePrice(String domainName, Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0;
		if(item.getProduct().isInventoriable() && item.getProduct().isManufactured()) 
			return item.getPurchasePrice();
		
		// COMPRAS (Albaranes)
		IncomeDetail incomeDetail = AON.getLastIncomeDetailUntilDate(domainName, item.getDomain(), user, item, workplaceId, warehouseId, inventoryDate);
	
		Double price1 = 0.0;
		Date date1 = new Date();
		if(incomeDetail.getId() != null){
			if(incomeDetail.getIncome().getIssueDate() != null) date1 = incomeDetail.getIncome().getIssueDate();
			if(incomeDetail.getPrice() != null) price1 = incomeDetail.getPrice();
		}
			
		// COMPRAS (Facturas)
		InvoiceDetail invoiceDetail = AON.getLastInvoiceDetailUntilDate(domainName, item.getDomain(), user, item, workplaceId, warehouseId, inventoryDate);
		Double price2 = 0.0;
		Date date2 = new Date();
		if(invoiceDetail.getId() != null){
			if(invoiceDetail.getInvoice().getIssueDate() != null) 
				date2 = invoiceDetail.getInvoice().getIssueDate();
			if(invoiceDetail.getPrice() != null) 
				price2 = invoiceDetail.getPrice();
		}
			
		if(incomeDetail.getId() == null && invoiceDetail.getId() == null) return item.getPurchasePrice();
		else if(incomeDetail.getId() == null) return price2;
		else if(invoiceDetail.getId() == null) return price1;
		else return date1.compareTo(date2) < 0 ? price1 : price2;
	}
	
	public static Double getAveragePurchasePrice(String domainName, Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0;
		if(item.getProduct().isInventoriable() && item.getProduct().isManufactured()) 
			return item.getPurchasePrice();
					
		Integer domainId = item.getDomain();
		ApplicationParameter ap = AON.getApplicationParameter(domainName, domainId, user, AppParam.AON_PRODUCT_AVERAGE_MONTHS);
		LinkedList<InvoiceDetail> invoiceList = AON.getLastInvoiceDetailListUntilDate(domainName, domainId, user, item, ap.getValue(), workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getLastIncomeDetailListUntilDate(domainName, domainId, user, item, ap.getValue(), workplaceId, warehouseId, inventoryDate);
		
		Double invoiceSum = invoiceList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double incomeSum = incomeList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double sum = invoiceSum + incomeSum;
		Double invoiceQuantity = invoiceList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double incomeQuantity = incomeList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double totalQuantity = invoiceQuantity + incomeQuantity;
		
		if(totalQuantity == 0) return item.getPurchasePrice();
		return  sum / totalQuantity;
	}

	public static Double getFifoPrice(String domainName, Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0; 
		if((item.getProduct().isInventoriable() && item.getProduct().isManufactured())) 
			return item.getPurchasePrice();
		
		Integer domainId = item.getDomain();
		LinkedList<InvoiceDetail> invoiceList = AON.getInvoiceDetailListUntilDate(domainName, domainId, user, item, workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getIncomeDetailListUntilDate(domainName, domainId, user, item, workplaceId, warehouseId, inventoryDate);
		
		LinkedList<Fifo> fifoList = new LinkedList<Fifo>();

		Double q = 0.0;
		Double qError = 0.0;
		Integer i = 0;
		Integer j = 0;
		Double quantity2 = Math.abs(quantity) ;
		while(q < quantity2 &&  qError == 0.0){
			
			InvoiceDetail invoiceDetail = invoiceList.size() > i  ? invoiceList.get(i) : null;
			IncomeDetail incomeDetail = incomeList.size() > j ? incomeList.get(j) : null;
			
			if((invoiceDetail!= null && incomeDetail == null) ||(invoiceDetail!= null &&
					invoiceDetail.getInvoice().getIssueDate().compareTo(incomeDetail.getIncome().getIssueDate())>= 0)){
				if(invoiceDetail.getQuantity() > 0){
					q = q + invoiceDetail.getQuantity(); 
					Fifo fifo = new Fifo(invoiceDetail.getPrice(), invoiceDetail.getQuantity(), Double.parseDouble(invoiceDetail.getDiscountExpression()));
					fifoList.add(fifo);
				}
				i++;	
			}
			else if(incomeDetail != null){
				if(incomeDetail.getQuantity() > 0){
					q = q + incomeDetail.getQuantity(); 
					Fifo fifo = new Fifo(incomeDetail.getPrice(), incomeDetail.getQuantity(), Double.parseDouble(incomeDetail.getDiscountExpression()));
					fifoList.add(fifo);
				}
				j++;
			}
			else qError = quantity2;
		}
		if(qError != 0.0) return item.getPurchasePrice();
		if(q == quantity2){
			Double fifoPrice = fifoList.stream().mapToDouble(x -> x.getPrice() * x.getQuantity()).sum();
			return fifoPrice / quantity2;
		}
		else{
			Double fifoPrice = fifoList.stream().limit(fifoList.size()-1).mapToDouble(x -> x.getPrice() * (1 -(x.getDiscount()/100.0)) * x.getQuantity()).sum();
			Double lastFifoPrice = fifoList.getLast().getPrice() * (1 - (fifoList.getLast().getDiscount()/100.0)) * (fifoList.getLast().getQuantity() - (q-quantity2));
			return (fifoPrice + lastFifoPrice) / quantity2;
		}
	}
	
	public static class Fifo {
		private Double price;
		private Double quantity;
		private Double discount;
	
		public Fifo(Double price, Double quantity, Double discount) {
			this.price = price;
			this.quantity = quantity;
			this.discount = discount;
		}
		
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Double getDiscount() {
			return discount;
		}

		public void setDiscount(Double discount) {
			this.discount = discount;
		}
	}
	
	public static String getDateStr(Date date){
		Integer day = AonDateUtils.getDay(date);
		Integer month = (AonDateUtils.getMonth(date) +1);
		Integer year = AonDateUtils.getYear(date);

		String mes = month.toString();
		if(month< 10) mes = "0"+ month;
		String dia = day.toString();
		if(day < 10) dia = "0" + day;
		return year + "-" + mes + "-" + dia;
	}
}
