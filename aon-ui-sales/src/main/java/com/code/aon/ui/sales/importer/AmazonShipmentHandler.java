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
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.util.AonUtil;
		
public class AmazonShipmentHandler implements SalesImporterHandler {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<Sales> importedSalesList;

	private List<Sales> existingSalesList;
	
	private List<RegistryItem> nonExistentItems;

	private List<Sales> nonExistentSales;
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SHIPMENT_INTEGRATION);
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
				return ImporterUtils.validColumns(headers, 
						"amazon-order-id", "buyer-email", "buyer-name", "buyer-phone-number", "ship-address-1",
						"ship-address-2", "ship-address-3", "ship-city", "ship-state",
						"ship-postal-code", "ship-country", "ship-phone-number",
						"purchase-date", "fulfillment-channel", "sales-channel" );
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
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		for(Sales sales: importedSalesList){
			salesBean.update(sales);
		}
	}
	
	private void processImportedSalesShipment(List<String[]> linesList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
		
		String[] headers = linesList.get(0);
		int amazonOrderId_col = ImporterUtils.obtainHeaderPosition(headers, "amazon-order-id");
		int buyerEmail_col = ImporterUtils.obtainHeaderPosition(headers, "buyer-email");
		int buyerName_col = ImporterUtils.obtainHeaderPosition(headers, "buyer-name");
		int buyerPhoneNumber_col = ImporterUtils.obtainHeaderPosition(headers, "buyer-phone-number");
		int shipAddress1_col = ImporterUtils.obtainHeaderPosition(headers, "ship-address-1");
		int shipAddress2_col = ImporterUtils.obtainHeaderPosition(headers, "ship-address-2");
		int shipAddress3_col = ImporterUtils.obtainHeaderPosition(headers, "ship-address-3");
		int shipCity_col = ImporterUtils.obtainHeaderPosition(headers, "ship-city");
		int shipState_col = ImporterUtils.obtainHeaderPosition(headers, "ship-state");
		int shipPostalCode_col = ImporterUtils.obtainHeaderPosition(headers, "ship-postal-code");
		int shipCountry_col = ImporterUtils.obtainHeaderPosition(headers, "ship-country");
		int shipPhoneNumber_col = ImporterUtils.obtainHeaderPosition(headers, "ship-phone-number");
		int purchaseDate_col = ImporterUtils.obtainHeaderPosition(headers, "purchase-date");
		int fulfillmentChannel_col = ImporterUtils.obtainHeaderPosition(headers, "fulfillment-channel");
		int salesChannel_col = ImporterUtils.obtainHeaderPosition(headers, "sales-channel");
		
		HashMap<String, Sales> salesList = new HashMap<String, Sales>();
		HashMap<String, Sales> nonExistentSalesMap = new HashMap<String, Sales>();
		linesList.remove(0);
		for(String[] line: linesList){
			if ( ImporterUtils.validColumns(line, 
					amazonOrderId_col, buyerEmail_col, buyerName_col, buyerPhoneNumber_col, shipAddress1_col, 
					shipAddress2_col, shipAddress3_col, shipCity_col, shipState_col, 
					shipPostalCode_col, shipCountry_col, shipPhoneNumber_col, 
					purchaseDate_col, fulfillmentChannel_col, salesChannel_col) ){

				String amazonOrderId = line[amazonOrderId_col];
				
//				ShipmentLine shipmentLine = new ShipmentLine();
//				shipmentLine.amazonOrderId = line[amazonOrderId_col];
//				shipmentLine.buyerEmail = line[buyerEmail_col];
//				shipmentLine.buyerName = line[buyerName_col];
//				shipmentLine.buyerPhoneNumber = line[buyerPhoneNumber_col];
//				shipmentLine.shipAddress1 = line[shipAddress1_col];
//				shipmentLine.shipAddress2 = line[shipAddress2_col];
//				shipmentLine.shipAddress3 = line[shipAddress3_col];
//				shipmentLine.shipCity = line[shipCity_col];
//				shipmentLine.shipState = line[shipState_col];
//				shipmentLine.shipPostalCode = line[shipPostalCode_col];
//				shipmentLine.shipCountry = line[shipCountry_col];
//				shipmentLine.shipPhoneNumber = line[shipPhoneNumber_col];
//				shipmentLine.purchaseDate = line[purchaseDate_col];
//				shipmentLine.fulfillmentChannel = line[fulfillmentChannel_col];
//				shipmentLine.salesChannel = line[salesChannel_col];
//				salesList.put(amazonOrderId, shipmentLine);
				
				
				if(!salesList.containsKey(amazonOrderId)){
					Sales sales = ImporterUtils.obtainSales(amazonOrderId);
					if(sales!=null && sales.getId()!=null){
						sales.setShippingAlternativeAddress(line[shipAddress1_col]);
						sales.setShippingAlternativeAddress2(line[shipAddress2_col]+". "+line[shipAddress3_col]);
						sales.setShippingAlternativeZip(line[shipPostalCode_col]);
						sales.setShippingAlternativeCity(line[shipCity_col]+", "+line[shipState_col]+" ("+line[shipCountry_col]+")");
						sales.setShippingAlternativePhone(line[buyerPhoneNumber_col]);
						sales.setShippingAlternativeRecipient(line[buyerName_col]);
						sales.setShippingContact(null);
						sales.setShippingPeriod(null);
						salesList.put(amazonOrderId, sales);
					} else {
						sales = new Sales();
						sales.setPurchaseReference(amazonOrderId);
						sales.setStatus(SalesStatus.PENDING);
						try {
							sales.setIssueDate(dateFormat.parse(line[purchaseDate_col]));
						} catch (ParseException e) {
							LOGGER.error(e.getMessage());
							sales.setIssueDate(new Date());
						}
						if(!nonExistentSalesMap.containsKey(amazonOrderId)){
							nonExistentSalesMap.put(amazonOrderId, sales);
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
