package com.code.aon.ui.sales.importer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.tools.csv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesImporterController;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;

public class AmazonSalesReturnHandler implements SalesImporterHandler {
	
private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<AmazonSales> importedSalesList;

	private List<AmazonSales> existingSalesList;
	
	private List<RegistryItem> nonExistentItems;

	private List<AmazonSales> nonExistentSales;
	
	private List<Sales> generatedSales;

	private List<AmazonSalesDetail> importedSalesDetailList;
	
	private Map<String, Integer> importedSalesItemMap;
	
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SALES_RETURN_INTEGRATION);
	}
	
	public List<AmazonSales> getExistingSalesList(){
		return existingSalesList;
	}

	public List<AmazonSales> getImportedSalesList(){
		return importedSalesList;
	}
	
	public List<RegistryItem> getNonExistentItems(){
		return nonExistentItems;
	}
	
	public List<AmazonSales> getNonExistentSales(){
		return nonExistentSales;
	}
	
	public List<Sales> getGeneratedSales(){
		return generatedSales;
	}
	
	public List<AmazonSales> getCancelledSales(){
		return null;
	}
	
	public boolean isValidFile(AonFile aonFile) {
		try {
			if(aonFile!=null && aonFile.getFile()!=null){
				LineNumberReader reader = new LineNumberReader(new FileReader(aonFile.getFile()));
				CSVReader csv = new CSVReader(reader,SEPARATOR_VALUE);
				List<String[]> linesList = csv.readAll();
				String[] headers = linesList.get(0);
				return ImporterUtils.validColumns(headers, "return-date", "order-id", "sku", "product-name", "quantity", "reason");
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	
	public void loadData(AonFile aonFile) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(aonFile.getFile()));
		CSVReader csv = new CSVReader(reader,SEPARATOR_VALUE);
		processImportedProducts(csv.readAll());
		
		if(nonExistentItems==null || nonExistentItems.size()==0){
			reader = new LineNumberReader(new FileReader(aonFile.getFile()));
			csv = new CSVReader(reader,SEPARATOR_VALUE);
			processImportedSalesReturn(csv.readAll());
		}
	}
	
	public void accept() throws ManagerBeanException{
		
		generatedSales = new LinkedList<Sales>();
		SalesImporterController importerController = (SalesImporterController) AonUtil.getRegisteredBean(ISalesConstants.SALES_IMPORTER_CONTROLLER_NAME);
		String serie = importerController.getSeries();
		
		SalesUtils salesUtils = new SalesUtils();
		for(AmazonSales amazonSales: importedSalesList){
			
			List<Sales> sourceSalesList = ImporterUtils.obtainSales(amazonSales.getPurchaseReference());
			Sales sourceSales = sourceSalesList.get(0);
			
			String comments = AonUtil.getMessage("sales_return_over", amazonSales.getPurchaseReference());
			Sales sales = salesUtils.createSales(serie, sourceSales.getSeller(), 
					sourceSales.getCustomer(), sourceSales.getProject(), sourceSales.getWorkPlace(), 
					DocumentType.ITEM_RETURN, sourceSales.getIssueDate(), sourceSales.getDiscountExpression(), 
					sourceSales.getNumberOfPayments(), sourceSales.getDaysToFirstPayment(), sourceSales.getDaysBetweenPayments(), 
					sourceSales.getPaymentDays(), sourceSales.getPayMethod(), sourceSales.getBankAccount(), 
					sourceSales.getBankAlias(), sourceSales.getBic(), comments, sourceSales.getRemarks(), 
					sourceSales.getCarrier(), sourceSales.getPurchaseReference(),  
					sourceSales.getShippingAlternativeAddress(), sourceSales.getShippingAlternativeAddress2(), 
					sourceSales.getShippingAlternativeZip(), sourceSales.getShippingAlternativeCity(), sourceSales.getShippingAlternativePhone(), 
					sourceSales.getShippingAlternativeRecipient(), sourceSales.getShippingContact(), sourceSales.getShippingPeriod());
			amazonSales.setSales(sales);
			generatedSales.add(sales);
			
		}
			
		for(AmazonSalesDetail amazonSalesDetail: importedSalesDetailList){
			Sales sales = amazonSalesDetail.getAmazonSales().getSales();
			if(sales!=null && sales.getId()!=null){
				Item item = ImporterUtils.obtainItem(importedSalesItemMap.get(amazonSalesDetail.getSku()));
				salesUtils.createSalesDetail(sales, item, salesUtils.calculateNextLine(sales), 
						item.getProduct().getName()+" ["+item.getDetail()+"]", 
						null, SalesDetailStatus.PENDING, new DiscountExpression(), 
						(-1)*Double.valueOf(amazonSalesDetail.getQuantity()), 
						item.getPrice(), 0.0, 0.0, 0.0);
			}
		}
	}
	
	private void processImportedProducts(List<String[]> linesList) {
		String[] headers = linesList.get(0);
		int sku_col = ImporterUtils.obtainHeaderPosition(headers, "sku");
		int productName_col = ImporterUtils.obtainHeaderPosition(headers, "product-name");
		HashMap<String, String> items = new HashMap<String, String>();
		linesList.remove(0);
		for(String[] line: linesList){
			if(line.length>=sku_col){
				String sku = line[sku_col].trim().toLowerCase();
				String productName = line[productName_col].trim().toLowerCase();
				sku = StringUtils.removeEnd(sku.toLowerCase(), "az");
				if(!items.containsKey(sku)){
					items.put(sku, productName);
				}
			}
		}
	
		importedSalesItemMap = new HashMap<String, Integer>(); 
		Result<Record2<Integer, String>> record = ImporterUtils.getItemRecords(items.keySet());
		for (Record2<Integer, String> step : record) {
			Integer id = step.value1();
			String code = step.value2().trim().toLowerCase();
			if(items.containsKey(code)){
				items.remove(code);
			}
			importedSalesItemMap.put(code, id); 
		}
		
		nonExistentItems = new LinkedList<RegistryItem>();
		
		for(String key: items.keySet()){
			items.get(key);
			Item item = new Item();
			item.setDescription(items.get(key));
			RegistryItem ritem = new RegistryItem();
			ritem.setCode(key);
			ritem.setItem(item);
			nonExistentItems.add(ritem);
		}
	}
	
	private void processImportedSalesReturn(List<String[]> linesList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
		
		String[] headers = linesList.get(0);
		
		int returnDate_col = ImporterUtils.obtainHeaderPosition(headers, "return-date");
		int orderId_col = ImporterUtils.obtainHeaderPosition(headers, "order-id");
		int sku_col = ImporterUtils.obtainHeaderPosition(headers, "sku");
		int title_col = ImporterUtils.obtainHeaderPosition(headers, "product-name");
		int quantity_col = ImporterUtils.obtainHeaderPosition(headers, "quantity");
		int reason_col = ImporterUtils.obtainHeaderPosition(headers, "reason");
		
		HashMap<String, AmazonSales> salesList = new HashMap<String, AmazonSales>();
		HashMap<String, AmazonSales> nonExistentSalesMap = new HashMap<String, AmazonSales>();
		linesList.remove(0);
		importedSalesDetailList = new LinkedList<AmazonSalesDetail>();
		for(String[] line: linesList){
			
			if ( ImporterUtils.validColumns(line,
					returnDate_col, orderId_col, sku_col, 
					title_col, quantity_col, reason_col) ){
				String amazonOrderId = line[orderId_col];
				AmazonSales amazonSales = new AmazonSales();
				amazonSales.setPurchaseReference(amazonOrderId);
				try {
					amazonSales.setIssueDate(dateFormat.parse(line[returnDate_col]));
				} catch (ParseException e) {
					LOGGER.error(e.getMessage());
				}
				List<Sales> sales = ImporterUtils.obtainSales(amazonOrderId);
				amazonSales.setRepeated(sales.size()>1);
				if(sales.size()>0){
					amazonSales.setSeller(sales.get(0).getSeller());
					amazonSales.setCustomer(sales.get(0).getCustomer());
					amazonSales.setCustomerName(sales.get(0).getCustomer().getRegistry().getFullName());
				}
				
				if(!salesList.containsKey(amazonOrderId)){
					salesList.put(amazonOrderId, amazonSales);
				}
				
				if(line.length>=sku_col  && line.length>=quantity_col ){
					AmazonSalesDetail detail = new AmazonSalesDetail(); 
					detail.setAmazonSales(amazonSales);
					detail.setPurchaseReference(amazonOrderId);
					detail.setSku(StringUtils.removeEnd(line[sku_col].toLowerCase(), "az"));
					detail.setQuantity(line[quantity_col]);
					importedSalesDetailList.add(detail);
				}
				
			}
		}
		
		// process existing return-sales
		existingSalesList = new LinkedList<AmazonSales>();
		Result<Record2<Integer, String>> returnRecord = ImporterUtils.getSalesReturnRecords(salesList.keySet());
		if(returnRecord!=null && returnRecord.size()>0){
			for (Record2<Integer, String> step : returnRecord) {
				String code = step.value2().trim();
				if(salesList.containsKey(code)){
					existingSalesList.add(salesList.remove(code));
				}
			}
		}
		
		Result<Record2<Integer, String>> salesRecords = ImporterUtils.getSalesRecords(salesList.keySet());
		if(salesRecords==null || salesRecords.size()<=0){
			nonExistentSales = new ArrayList<AmazonSales>(salesList.values());
			importedSalesList = new ArrayList<AmazonSales>();
		} else {
			
			// existing sales in data base
			HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
			for (Record2<Integer, String> step : salesRecords) {
				Integer id = step.value1();
				String code = step.value2();
				resultMap.put(code, id);
			}
			// extract non existing sales
			for (AmazonSales amazonSales : salesList.values()) {
				if(resultMap.containsKey(amazonSales.getPurchaseReference())){
					amazonSales.setSalesId(resultMap.get(amazonSales.getPurchaseReference()));
				} else {
					nonExistentSalesMap.put(amazonSales.getPurchaseReference(), salesList.get(amazonSales.getPurchaseReference()));
				}
			}
			for (String key : nonExistentSalesMap.keySet()) {
				salesList.remove(key);
			}
			nonExistentSales = new ArrayList<AmazonSales>(nonExistentSalesMap.values());
			importedSalesList = new ArrayList<AmazonSales>(salesList.values());
		}
		
	}
	
	@Override
	public void reset() {
		importedSalesList = null;
		existingSalesList = null;
		nonExistentItems = null;
		nonExistentSales = null;
	}
	
}
