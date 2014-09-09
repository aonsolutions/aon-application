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
import java.util.List;

import org.jooq.tools.csv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.util.AonUtil;

public class AmazonSalesReturnHandler implements SalesImporterHandler {
	
private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<Sales> importedSalesList;

	private List<Sales> existingSalesList;
	
	private List<RegistryItem> nonExistentItems;

	private List<Sales> nonExistentSales;
	
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SALES_RETURN_INTEGRATION);
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
		return nonExistentSales;
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
		processImportedSalesShipment(csv.readAll());
	}
	public void accept() throws ManagerBeanException{
		// TODO
		AonUtil.addInfoMessage("sin implementar");
//			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
//			for(Sales sales: importedSalesList){
//				salesBean.update(sales);
//			}
	}
	
	private void processImportedSalesShipment(List<String[]> linesList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
		
		String[] headers = linesList.get(0);
		
		int returnDate_col = ImporterUtils.obtainHeaderPosition(headers, "return-date");
		int orderId_col = ImporterUtils.obtainHeaderPosition(headers, "order-id");
		int merchantSku_col = ImporterUtils.obtainHeaderPosition(headers, "sku");
		int title_col = ImporterUtils.obtainHeaderPosition(headers, "product-name");
		int quantity_col = ImporterUtils.obtainHeaderPosition(headers, "quantity");
		int reason_col = ImporterUtils.obtainHeaderPosition(headers, "reason");
		
		HashMap<String, Sales> salesList = new HashMap<String, Sales>();
		HashMap<String, Sales> nonExistentSalesMap = new HashMap<String, Sales>();
		linesList.remove(0);
		for(String[] line: linesList){
			if ( ImporterUtils.validColumns(line, returnDate_col, orderId_col, merchantSku_col, title_col, quantity_col, reason_col) ) {

				String orderId = line[orderId_col];
				if(!salesList.containsKey(orderId)){
					Sales sales = ImporterUtils.obtainSales(orderId);
					if(sales!=null && sales.getId()!=null){
						salesList.put(orderId, sales);
					} else {
						sales = new Sales();
						sales.setPurchaseReference(orderId);
						sales.setStatus(SalesStatus.PENDING);
						try {
							sales.setIssueDate(dateFormat.parse(line[returnDate_col]));
						} catch (ParseException e) {
							LOGGER.error(e.getMessage());
							sales.setIssueDate(new Date());
						}
						sales.setSeller(null);
						sales.setCustomer(null);
						if(!nonExistentSalesMap.containsKey(orderId)){
							nonExistentSalesMap.put(orderId, sales);
						}
					}
				}
			}
		}
		nonExistentSales = new ArrayList<Sales>(nonExistentSalesMap.values());
		importedSalesList = new ArrayList<Sales>(salesList.values());
	}
	
	@Override
	public void reset() {
		importedSalesList = null;
		existingSalesList = null;
		nonExistentItems = null;
		nonExistentSales = null;
	}
	
}
