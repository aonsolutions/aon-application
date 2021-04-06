package com.code.aon.ui.warehouse.rpm;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class CalculatePurchasePrice {
	
	//private static final Logger LOGGER = LoggerFactory
	//		.getLogger(CalculatePurchasePrice.class.getName());
	
	private static Double getCost(Domain domain, InventoryDetail inventoryDetail, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		OldItem item = AON.getItem(domain.getName(), domain.getId(), login, inventoryDetail.getItem().getId());

		switch (type) {
			case "cost": return inventoryDetail.getRealQuantity() != 0 ? item.getPurchasePrice() : 0.0;
			case "last": return getLastPurchasePrice(domain, item, inventoryDetail.getRealQuantity(), login, workplaceId,warehouseId, inventoryDate);
			case "avg": return getAveragePurchasePrice(domain, item, inventoryDetail.getRealQuantity(), login, workplaceId,warehouseId, inventoryDate);
			case "fifo": return getFifoPrice(domain, item, inventoryDetail.getRealQuantity(), login, workplaceId,warehouseId, inventoryDate);	
			default : return item.getPurchasePrice(); 
		}
	}
	
	public static Double getAveragePurchasePrice(Domain domain, OldItem item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0;
		OldProduct product =  AON.getProduct(domain.getName(), domain.getId(), login, item.getProductId());
		if(product.isInventoriable() && product.isManufactured()) 
			return item.getPurchasePrice();
							
		LinkedList<InvoiceDetail> invoiceList = AON.getLastInvoiceDetailListUntilDate(domain.getName(), domain.getId(), user, item, months, workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getLastIncomeDetailListUntilDate(domain.getName(), domain.getId(), user, item, months, workplaceId, warehouseId, inventoryDate);
		
		Double invoiceSum = invoiceList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double incomeSum = incomeList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double sum = invoiceSum + incomeSum;
		Double invoiceQuantity = invoiceList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double incomeQuantity = incomeList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double totalQuantity = invoiceQuantity + incomeQuantity;
		
		if(totalQuantity == 0) return item.getPurchasePrice();
		return  sum / totalQuantity;	
	}
	
	public static Double getFifoPrice(Domain domain, OldItem item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		
		if(quantity == 0) return 0.0; 
		OldProduct product =  AON.getProduct(domain.getName(), domain.getId(), login, item.getProductId());
		if((product.isInventoriable() && product.isManufactured())) 
			return item.getPurchasePrice();
		LinkedList<InvoiceDetail> invoiceList = AON.getInvoiceDetailListUntilDate(domain.getName(), domain.getId(), user, item, workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getIncomeDetailListUntilDate(domain.getName(), domain.getId(), user, item, workplaceId, warehouseId, inventoryDate);
		
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

	public static Double getLastPurchasePrice(Domain domain, OldItem item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0;
		OldProduct product =  AON.getProduct(domain.getName(), domain.getId(), login, item.getProductId());
		if(product.isInventoriable() && product.isManufactured()) 
			return item.getPurchasePrice();

		// COMPRAS (Albaranes)
		IncomeDetail incomeDetail = AON.getLastIncomeDetailUntilDate(domain.getName(), item.getDomain(), user, item, workplaceId, warehouseId, inventoryDate);
		Double price1 = 0.0;
		Date date1 = new Date();
		if(incomeDetail.getId() != null){
			if(incomeDetail.getIncome().getIssueDate() != null) date1 = incomeDetail.getIncome().getIssueDate();
			if(incomeDetail.getPrice() != null) price1 = incomeDetail.getPrice();
		}
			
		// COMPRAS (Facturas)
		InvoiceDetail invoiceDetail = AON.getLastInvoiceDetailUntilDate(domain.getName(), item.getDomain(), user, item, workplaceId, warehouseId, inventoryDate);
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
	
	
	public static Integer getDay(String date){
		return Integer.parseInt(date.substring(0,2));
	}
	
	public static Integer getMonth(String date){
		return Integer.parseInt(date.substring(3,5)) -1;
	}
	
	public static Integer getYear(String date){
		return Integer.parseInt(date.substring(6));
	}
	
	public static Map<String, String> getDomains() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getDomains();
	}
	
	public static Map<String, Integer> getDomainMap() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getDomainMap();
	}
	
	public static Map<String, String> initializeDomains(){
		Map<String, String> map  = new HashMap<String, String>();
		try {
			map =  getDomains();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String, Integer> initializeDomainMap(){
		Map<String, Integer> map  = new HashMap<String, Integer>();
		try {
			map =  getDomainMap();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
		parse(args);
		Map<String, Integer> domainMap = initializeDomainMap();
		if (domains == null || domains.length == 0 || domains[0].equals("ALL")) {
			// Obtiene todos los dominios de la BD.
			Map<String, String> allDomains = initializeDomains();
			// Ordena los dominios por orden alfabetico.
			List<String> list = new ArrayList<String>(allDomains.keySet());
			Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
			domains = (String[]) list.toArray();
		}
		for (String domainName : domains){
			Domain domain = AON.getDomain(domainName, domainMap.get(domainName), login);
			//LOGGER.info("DOMAIN: " + domainName + " - ID: " + domainMap.get(domainName));
			System.out.println("DOMAIN: " + domainName + " - ID: " + domainMap.get(domainName));
			AON.updateZeroInventoryDetail(domainName, domain.getId(), login);
			Date start = AonDateUtils.getDate(getYear(startDate), getMonth(startDate), getDay(startDate));
			Date end = AonDateUtils.getDate(getYear(endDate), getMonth(endDate), getDay(endDate));
			LinkedList<Inventory> inventoryList = AON.getInventoryList(domain.getName(), domain.getId(), login, start, end);
			Collections.sort(inventoryList, (Inventory s1, Inventory s2) -> s1.getInventoryDate().compareTo(s1.getInventoryDate()));
			for (Inventory inventory : inventoryList) {
				Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), login,
						f -> f.getIdProperty().eq(inventory.getWarehouse()));
				//LOGGER.info("WAREHOUSE: "+ warehouse.getName());
				System.out.println("WAREHOUSE: "+ warehouse.getName());System.out.println();
				//LOGGER.info(inventory.getInventoryDate() + " - INVENTORY: " + inventory.getDescription());
				System.out.println(inventory.getInventoryDate() + " - INVENTORY: " + inventory.getDescription());System.out.println();
				LinkedList<InventoryDetail> inventoryDetailList = AON.getInventoryDetailList(domain.getName(), domain.getId(), login, inventory.getId());
				
				for (InventoryDetail inventoryDetail : inventoryDetailList) {
					//LOGGER.info("DETAIL : " + inventoryDetail.getId() + " - COSTE ACTUAL: " + inventoryDetail.getCost());
					System.out.println("DETAIL : " + inventoryDetail.getId() + " - COSTE ACTUAL: " + inventoryDetail.getCost());
					Double cost = getCost(domain, inventoryDetail, warehouse.getWorkplace(), warehouse.getId(), inventory.getInventoryDate());
					//LOGGER.info("NUEVO COSTE: " + cost);
					if(!inventoryDetail.equals(round(cost,3))){
						inventoryDetail.setCost(round(cost,3));
						if(!dryRun)
							AON.updateInventoryDetail(domain.getName(), domain.getId(), login, inventoryDetail);
					}
					System.out.println("NUEVO COSTE: " + round(cost,3));System.out.println();
				}
			}
		}
	}
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	private static String domains[];
	private static String login;
	private static String startDate;
	private static String endDate;
	private static boolean dryRun;
	private static String type;
	private static String months;
	
	private static boolean parse(String args[]) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Username of application");
		OptionBuilder.withLongOpt("username");
		Option loginOption = OptionBuilder.create('u');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("specify a type, e.g., \"FIFO, MEDIA, ... \"");
		OptionBuilder.withLongOpt("type");
		Option typeOption = OptionBuilder.create('t');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a domain list, consisting of domain names separated by commas, e.g., \"aon.esferalia.net,sig.aonsolutions.org\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("domains");
		Option domainOption = OptionBuilder.create('d');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Start Date");
		OptionBuilder.withLongOpt("start");
		Option startDateOption = OptionBuilder.create("start");
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("End Date");
		OptionBuilder.withLongOpt("end");
		Option endDateOption = OptionBuilder.create("end");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("only for avg type, number of months");
		OptionBuilder.withLongOpt("months");
		Option monthsOption = OptionBuilder.create('m');

		options.addOption(loginOption);
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(dryOption);
		options.addOption(startDateOption);
		options.addOption(endDateOption);
		options.addOption(monthsOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			
			login = line.getOptionValue(loginOption.getOpt());
			
			
			dryRun = line.hasOption(dryOption.getOpt());
			if (dryRun)
				//LOGGER.info("DryRun ON: Perform a trial run with no changes made.");
				System.out.println("DryRun ON: Perform a trial run with no changes made.");
			type = line.getOptionValue(typeOption.getOpt());
			
			startDate = line.getOptionValue(startDateOption.getOpt());
			
			endDate = line.getOptionValue(endDateOption.getOpt());

			domains = line.getOptionValues(domainOption.getOpt());
			if (domains == null)
				domains = new String[] {};

			months =  line.getOptionValue(monthsOption.getOpt());
		} catch (ParseException e) {
			//LOGGER.error(e.getMessage());
			System.out.println(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
