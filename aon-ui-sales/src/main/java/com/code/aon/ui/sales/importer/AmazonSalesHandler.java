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
import com.code.aon.ui.sales.controller.SalesImporterController;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;

public class AmazonSalesHandler implements SalesImporterHandler {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<AmazonSales> importedSalesList;

	private List<AmazonSalesDetail> importedSalesDetailList;
	
	private List<AmazonSales> existingSalesList;

	private List<AmazonSales> excludedSales;
	
	private List<RegistryItem> nonExistentItems;
	
	private Map<String, Integer> importedSalesItemMap;

	private List<Sales> generatedSales;
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SALES_INTEGRATION);
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
		return null;
	}
	
	public List<AmazonSales> getExcludedSales(){
		return excludedSales;
	}
		
	public List<Sales> getGeneratedSales(){
		return generatedSales;
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
		generatedSales = new LinkedList<Sales>();
		SalesUtils salesUtils = new SalesUtils();
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		SalesImporterController importerController = (SalesImporterController) AonUtil.getRegisteredBean(ISalesConstants.SALES_IMPORTER_CONTROLLER_NAME);
		String serie = importerController.getSeries();
		for(AmazonSales amazonSales: importedSalesList){
			Sales sales = new Sales();
			try {
				sales.setPurchaseReference(amazonSales.getPurchaseReference());
				sales.setStatus(SalesStatus.PENDING);
				sales.setIssueDate(amazonSales.getIssueDate());
				
				sales.setSeller(amazonSales.getSeller());
				sales.setCustomer(amazonSales.getCustomer());
				
				sales.setPaymentDays("0");
				sales.setWorkPlace(ImporterUtils.obtainWorkPlace());
				
				sales.setSeries( serie );
				sales.setNumber( salesUtils.obtainSeriesMaxNumber(serie) );
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
			}
			sales = (Sales) salesBean.insert(sales);
			amazonSales.setSales(sales);
			generatedSales.add(sales);
		}
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		for(AmazonSalesDetail amazonSalesDetail: importedSalesDetailList){
			if(amazonSalesDetail.getAmazonSales().getSales()!=null && amazonSalesDetail.getAmazonSales().getSales().getId()!=null){
				SalesDetail salesDetail = new SalesDetail(); 
				salesDetail.setSales(amazonSalesDetail.getAmazonSales().getSales());
				salesDetail.setStatus(SalesDetailStatus.PENDING);
				
				Item item = ImporterUtils.obtainItem(importedSalesItemMap.get(amazonSalesDetail.getSku()));
				salesDetail.setItem(item);
				salesDetail.setDescription(item.getProduct().getName()+" ["+item.getDetail()+"]");
				
				Double amazonPrice = ImporterUtils.obtainItemPrice(salesDetail.getItem().getVat(), amazonSalesDetail.getPrice());
				salesDetail.setPrice(salesDetail.getItem().getPrice());
				salesDetail.setDiscountExpression(ImporterUtils.obtainItemDiscountExpression(salesDetail.getItem().getPrice(), amazonPrice, amazonSalesDetail.getCurrency()));
				salesDetail.setQuantity(Double.valueOf(amazonSalesDetail.getQuantity()));
				
				salesDetail.setLine(salesUtils.calculateNextLine(salesDetail.getSales()));
				salesDetailBean.insert(salesDetail);
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

	private void processImportedSales(List<String[]> linesList) {
		final String STATUS_CANCELLED = "cancelled";
		final String SALES_CHANNEL_NON_AMAZON = "non-amazon";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
		
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
		
		
		HashMap<String, AmazonSales> salesList = new HashMap<String, AmazonSales>();
		importedSalesDetailList = new LinkedList<AmazonSalesDetail>();
		excludedSales  = new LinkedList<AmazonSales>();
		linesList.remove(0);
		ImporterUtils.loadAmazonSellerMap();
		for(String[] line: linesList){
			if ( ImporterUtils.validColumns(line, 
					sku_col, amazonOrderId_col, purchaseDate_col, orderStatus_col, 
					fulfillmentChannel_col, salesChannel_col, quantity_col, 
					itemPrice_col, currency_col, itemPromotionDiscount_col) ){
				
				String amazonOrderId = line[amazonOrderId_col];
				if(!salesList.containsKey(amazonOrderId)){
					AmazonSales amazonSales = new AmazonSales();
					amazonSales.setPurchaseReference(amazonOrderId);
					try {
						amazonSales.setIssueDate(dateFormat.parse(line[purchaseDate_col]));
					} catch (ParseException e) {
						LOGGER.error(e.getMessage());
						amazonSales.setIssueDate(new Date());
					}
					amazonSales.setSeller(ImporterUtils.obtainSeller(line[salesChannel_col]));
					amazonSales.setCustomer(ImporterUtils.obtainCustomer(line[fulfillmentChannel_col], line[salesChannel_col]));
					amazonSales.setCustomerName(ImporterUtils.obtainCustomerName(line[fulfillmentChannel_col], line[salesChannel_col]));
					if(STATUS_CANCELLED.equals(line[orderStatus_col].trim().toLowerCase())){
						amazonSales.setObservation("Cancelado");
						excludedSales.add(amazonSales);
					} else if(SALES_CHANNEL_NON_AMAZON.equals(line[salesChannel_col].trim().toLowerCase())){
						amazonSales.setObservation(SALES_CHANNEL_NON_AMAZON);
						excludedSales.add(amazonSales);
					} else {
						salesList.put(amazonOrderId, amazonSales);
					}
				}
				if(line.length>=sku_col  && line.length>=quantity_col
						&& line.length>=itemPrice_col && line.length>=itemPromotionDiscount_col ){
					AmazonSales amazonSales = salesList.get(amazonOrderId);
					if(amazonSales!=null){
						AmazonSalesDetail detail = new AmazonSalesDetail(); 
						detail.setAmazonSales(amazonSales);
						detail.setPurchaseReference(amazonOrderId);
						detail.setSku(StringUtils.removeEnd(line[sku_col].toLowerCase(), "az"));
						detail.setPrice(line[itemPrice_col]);
						detail.setCurrency(line[currency_col]);
						detail.setQuantity(line[quantity_col]);
						importedSalesDetailList.add(detail);
					}
				}
				
			}
		}
		
		existingSalesList = new LinkedList<AmazonSales>();
		Result<Record2<Integer, String>> record = ImporterUtils.getSalesRecords(salesList.keySet());
		for (Record2<Integer, String> step : record) {
			String code = step.value2().trim();
			if(salesList.containsKey(code)){
				existingSalesList.add(salesList.remove(code));
			}
		}
		importedSalesList = new ArrayList<AmazonSales>(salesList.values());
	}

	@Override
	public void reset() {
		importedSalesList = null;
		importedSalesDetailList = null;
		importedSalesItemMap = null;
		existingSalesList = null;
		excludedSales = null;
		nonExistentItems = null;
	}
}

