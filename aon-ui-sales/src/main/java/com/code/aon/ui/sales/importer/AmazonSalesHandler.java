package com.code.aon.ui.sales.importer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;

public class AmazonSalesHandler implements SalesImporterHandler {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<Sales> importedSalesList;

	private List<SalesDetail> importedSalesDetailList;
	
	private List<Sales> existingSalesList;
	
	private List<RegistryItem> nonExistentItems;
	
	private Map<String, Integer> importedSalesItemMap;
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SALES_INTEGRATION);
	}
	
	public List<Sales> getExistingSalesList(){
		return existingSalesList;
	}

	public List<Sales> getImportedSalesList(){
		return importedSalesList;
	}
	
	public List<RegistryItem> getNonExistentItems(){
		return nonExistentItems;
	}
	
	public List<Sales> getNonExistentSales(){
		return null;
	}
	
	public boolean isValidFile(AonFile aonFile) {
		try {
			if(aonFile!=null && aonFile.getFile()!=null){
				LineNumberReader reader = new LineNumberReader(new FileReader(aonFile.getFile()));
				CSVReader csv = new CSVReader(reader,SEPARATOR_VALUE);
				List<String[]> linesList = csv.readAll();
				String[] headers = linesList.get(0);
				return ImporterUtils.validColumns(headers, 
						"sku", "amazon-order-id", "purchase-date", 
						"product-name", "fulfillment-channel", "sales-channel", 
						"quantity", "item-price", "currency", "item-promotion-discount");
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
			processImportedSales(csv.readAll());
		}
	}
		
	public void accept() throws ManagerBeanException{
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		for(Sales sales: importedSalesList){
			salesBean.insert(sales);
		}
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		for(SalesDetail detail: importedSalesDetailList){
			if(detail.getSales()!=null && detail.getSales().getId()!=null){
				salesDetailBean.insert(detail);
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
				String sku = line[sku_col];
				String productName = line[productName_col];
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
			String code = step.value2();
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

	private void processImportedSales(List<String[]> linesList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
		SalesUtils salesUtils = new SalesUtils();
		
		String[] headers = linesList.get(0);
		int sku_col = ImporterUtils.obtainHeaderPosition(headers, "sku");
		int amazonOrderId_col = ImporterUtils.obtainHeaderPosition(headers, "amazon-order-id");
		int purchaseDate_col = ImporterUtils.obtainHeaderPosition(headers, "purchase-date");
		int orderStatus_col = ImporterUtils.obtainHeaderPosition(headers, "order-status");
		int fulfillmentChannel_col = ImporterUtils.obtainHeaderPosition(headers, "fulfillment-channel");
		int salesChannel_col = ImporterUtils.obtainHeaderPosition(headers, "sales-channel");
		int quantity_col = ImporterUtils.obtainHeaderPosition(headers, "quantity");
		int itemPrice_col = ImporterUtils.obtainHeaderPosition(headers, "item-price");
		int currency_col = ImporterUtils.obtainHeaderPosition(headers, "currency");
		int itemPromotionDiscount_col = ImporterUtils.obtainHeaderPosition(headers, "item-promotion-discount");
		
		
		HashMap<String, Sales> salesList = new HashMap<String, Sales>();
		importedSalesDetailList = new LinkedList<SalesDetail>();
		linesList.remove(0);
		int detailLine=0;
		for(String[] line: linesList){
			if ( ImporterUtils.validColumns(line, 
					sku_col, amazonOrderId_col, purchaseDate_col, orderStatus_col, 
					fulfillmentChannel_col, salesChannel_col, quantity_col, 
					itemPrice_col, currency_col, itemPromotionDiscount_col) ){
				
				String amazonOrderId = line[amazonOrderId_col];
				if(!salesList.containsKey(amazonOrderId)){
					Sales sales = new Sales();
					sales.setPurchaseReference(amazonOrderId);
					sales.setStatus(SalesStatus.PENDING);
					try {
						sales.setIssueDate(dateFormat.parse(line[purchaseDate_col]));
					} catch (ParseException e) {
						LOGGER.error(e.getMessage());
						sales.setIssueDate(new Date());
					}
					sales.setSeller(ImporterUtils.obtainSeller(line[fulfillmentChannel_col], line[salesChannel_col]));
					sales.setCustomer(ImporterUtils.obtainCustomer(line[fulfillmentChannel_col], line[salesChannel_col]));
					sales.setPaymentDays("0");
					sales.setWorkPlace(ImporterUtils.obtainWorkPlace());
					try {
						sales.setSeries( salesUtils.obtainWorkPlaceSerie(sales.getWorkPlace()) );
						sales.setNumber( salesUtils.obtainSeriesMaxNumber(sales.getSeries()) );
					} catch (ManagerBeanException e) {
						LOGGER.error(e.getMessage());
					}
					salesList.put(amazonOrderId, sales);
				}
				if(line.length>=sku_col  && line.length>=quantity_col
						&& line.length>=itemPrice_col && line.length>=itemPromotionDiscount_col ){
					Sales sales = salesList.get(amazonOrderId);
					SalesDetail detail = new SalesDetail(); 
					detail.setSales(sales);
					detail.setStatus(SalesDetailStatus.PENDING);
					detail.setItem(ImporterUtils.obtainItem(importedSalesItemMap.get(StringUtils.removeEnd(line[sku_col].toLowerCase(), "az"))));
					detail.setDescription(detail.getItem().getProduct().getName());
					detail.setPrice(detail.getItem().getPrice());
					detail.setDiscountExpression(ImporterUtils.obtainItemDiscountExpression(detail.getItem(), line[itemPrice_col], line[currency_col]));
					detail.setQuantity(Double.valueOf(line[quantity_col]));
					
					detail.setLine(detailLine++);
					importedSalesDetailList.add(detail);
				}
			}
		}
		
		existingSalesList = new LinkedList<Sales>();
		Result<Record2<Integer, String>> record = ImporterUtils.getSalesRecords(salesList.keySet());
		for (Record2<Integer, String> step : record) {
			String code = step.value2();
			if(salesList.containsKey(code)){
				existingSalesList.add(salesList.remove(code));
			}
		}
		importedSalesList = new ArrayList<Sales>(salesList.values());
	}

	@Override
	public void reset() {
		importedSalesList = null;
		importedSalesDetailList = null;
		importedSalesItemMap = null;
		existingSalesList = null;
		nonExistentItems = null;
	}
}

