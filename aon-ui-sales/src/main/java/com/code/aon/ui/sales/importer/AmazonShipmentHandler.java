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
import com.code.aon.customer.Customer;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.sales.Sales;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.util.AonUtil;
		
public class AmazonShipmentHandler implements SalesImporterHandler {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private List<AmazonSales> importedSalesList;

	private List<RegistryItem> nonExistentItems;

	private List<AmazonSales> nonExistentSales;
	
	private List<Sales> generatedSales;
	
	public String getModuleLabel(){
		return AonUtil.getMessage(ISalesConstants.AMAZON_SHIPMENT_INTEGRATION);
	}
	
	public List<AmazonSales> getExistingSalesList(){
		return null;
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
		generatedSales = new LinkedList<Sales>();
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		for(AmazonSales amazonSales: importedSalesList){
			Sales sales = (Sales) salesBean.get(amazonSales.getSalesId());
			if(sales!=null && sales.getId()!=null){
				
				if(amazonSales.isNewCustomer()){
					Registry registry = new Registry();
					registry.setName(amazonSales.getBuyerName());
					registry.setType(RegistryType.NATURAL);
					registry = (Registry) BeanManager.getManagerBean(Registry.class).insert(registry);
					
					Customer customer = new Customer();
					customer.setRegistry(registry);
					customer.setScope(sales.getWorkPlace().getScope());
					customer = (Customer) BeanManager.getManagerBean(Customer.class).insert(customer);
					sales.setCustomer(customer);
					
					RegistryAddress raddress = new RegistryAddress();
					raddress.setRegistry(customer.getRegistry());
					
					raddress.setAddress(amazonSales.getShipAddress1());
					raddress.setAddress2(amazonSales.getShipAddress2());
					raddress.setAddress3(amazonSales.getShipAddress3());
					raddress.setAddressType(AddressType.MAIN);
					raddress.setCity(amazonSales.getShipCity());
					if(StringUtils.isNotBlank(amazonSales.getShipPostalCode()) 
							&& amazonSales.getShipPostalCode().length()>2){
						raddress.setGeozone(ImporterUtils.obtainGeozone(amazonSales.getShipPostalCode().substring(0, 2)));
					}
					raddress.setProvince(amazonSales.getShipState());
					raddress.setStreetType(StreetType.CL);
					raddress.setZip(amazonSales.getShipPostalCode());
					raddress = (RegistryAddress) BeanManager.getManagerBean(RegistryAddress.class).insert(raddress);					
					
					RegistryMedia rmedia = null;
					if(StringUtils.isNotBlank(amazonSales.getBuyerPhoneNumber())){
						rmedia = new RegistryMedia();
						rmedia.setRegistry(customer.getRegistry());
						rmedia.setAddress(raddress);
						rmedia.setMediaType(MediaType.FIXED_PHONE);
						rmedia.setValue(amazonSales.getBuyerPhoneNumber());
						BeanManager.getManagerBean(RegistryMedia.class).insert(rmedia);
					}
					if(StringUtils.isNotBlank(amazonSales.getBuyerEmail())){
						rmedia = new RegistryMedia();
						rmedia.setRegistry(customer.getRegistry());
						rmedia.setAddress(raddress);
						rmedia.setMediaType(MediaType.EMAIL);
						rmedia.setValue(amazonSales.getBuyerEmail());
						BeanManager.getManagerBean(RegistryMedia.class).insert(rmedia);
					}
				}
				// shipment data
				sales.setShippingAlternativeAddress(amazonSales.getShipAddress1());
				sales.setShippingAlternativeAddress2(amazonSales.getShipAddress2()+". "+amazonSales.getShipAddress3());
				sales.setShippingAlternativeZip(amazonSales.getShipPostalCode());
				sales.setShippingAlternativeCity(amazonSales.getShipCity()+", "+amazonSales.getShipState()+" ("+amazonSales.getShipCountry()+")");
				sales.setShippingAlternativePhone(amazonSales.getBuyerPhoneNumber());
				sales.setShippingAlternativeRecipient(amazonSales.getBuyerName());

				salesBean.update(sales);
				generatedSales.add(sales);
			}
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
		
		HashMap<String, AmazonSales> salesList = new HashMap<String, AmazonSales>();
		HashMap<String, AmazonSales> nonExistentSalesMap = new HashMap<String, AmazonSales>();
		linesList.remove(0);
		List<String> customerEmailList = new LinkedList<String>();
		for(String[] line: linesList){
			if ( ImporterUtils.validColumns(line, 
					amazonOrderId_col, buyerEmail_col, buyerName_col, buyerPhoneNumber_col, shipAddress1_col, 
					shipAddress2_col, shipAddress3_col, shipCity_col, shipState_col, 
					shipPostalCode_col, shipCountry_col, shipPhoneNumber_col, 
					purchaseDate_col, fulfillmentChannel_col, salesChannel_col) ){

				String amazonOrderId = line[amazonOrderId_col];
				AmazonSales amazonSales = new AmazonSales();
				amazonSales.setPurchaseReference(line[amazonOrderId_col]);
				try {
					amazonSales.setIssueDate(dateFormat.parse(line[purchaseDate_col]));
				} catch (ParseException e) {
					LOGGER.error(e.getMessage());
					amazonSales.setIssueDate(new Date());
				}
				amazonSales.setSeller(ImporterUtils.obtainSeller(line[salesChannel_col]));
				amazonSales.setBuyerEmail(line[buyerEmail_col]);
				if(ImporterUtils.checkCustomer(line[salesChannel_col])){
					customerEmailList.add(line[buyerEmail_col]);
					amazonSales.setCustomerName(line[buyerName_col]);
				} else {
					amazonSales.setCustomerName(ImporterUtils.obtainCustomerName(line[salesChannel_col]));
				}
				amazonSales.setBuyerName(line[buyerName_col]);
				amazonSales.setBuyerPhoneNumber(line[buyerPhoneNumber_col]);
				amazonSales.setShipAddress1(line[shipAddress1_col]);
				amazonSales.setShipAddress2(line[shipAddress2_col]);
				amazonSales.setShipAddress3(line[shipAddress3_col]);
				amazonSales.setShipCity(line[shipCity_col]);
				amazonSales.setShipState(line[shipState_col]);
				amazonSales.setShipPostalCode(line[shipPostalCode_col]);
				amazonSales.setShipCountry(line[shipCountry_col]);
				amazonSales.setShipPhoneNumber(line[shipPhoneNumber_col]);
				amazonSales.setPurchaseDate(line[purchaseDate_col]);
				amazonSales.setFulfillmentChannel(line[fulfillmentChannel_col]);
				amazonSales.setSalesChannel(line[salesChannel_col]);
				salesList.put(amazonOrderId, amazonSales);
				
			}
		}
		
		
		Result<Record2<Integer, String>> customerRecords = ImporterUtils.getCustomerRecords(customerEmailList);
		
		HashMap<String, Integer> existingEmailMap = new HashMap<String, Integer>();
		for (Record2<Integer, String> step : customerRecords) {
			Integer registry = step.value1();
			String email = step.value2();
			existingEmailMap.put(email, registry);
		}
		
		Result<Record2<Integer, String>> salesRecords = ImporterUtils.getSalesRecords(salesList.keySet());
		if(salesRecords==null || salesRecords.size()<=0){
			nonExistentSales = new ArrayList<AmazonSales>(salesList.values());
			importedSalesList = new ArrayList<AmazonSales>(null);
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
			
			// check customer data
			for (AmazonSales amazonSales : salesList.values()) {
				if( ImporterUtils.checkCustomer(amazonSales.getSalesChannel())
						&& !existingEmailMap.containsKey(amazonSales.getBuyerEmail()) ){
					amazonSales.setNewCustomer(true);
				}
			}
			
			nonExistentSales = new ArrayList<AmazonSales>(nonExistentSalesMap.values());
			importedSalesList = new ArrayList<AmazonSales>(salesList.values());
		}
		
	}

	@Override
	public void reset() {
		importedSalesList = null;
		nonExistentItems = null;
		nonExistentSales = null;
	}
	
}
