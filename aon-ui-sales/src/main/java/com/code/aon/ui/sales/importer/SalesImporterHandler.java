package com.code.aon.ui.sales.importer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.customer.Customer;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;
import com.code.aon.seller.Seller;

public interface SalesImporterHandler extends Serializable {
		
	char SEPARATOR_VALUE = '\t';
//	char SEPARATOR_VALUE = ';'; 
	
	boolean isValidFile(AonFile aonFile);
	
	void loadData(AonFile aonFile) throws IOException;
	
	void accept() throws ManagerBeanException;

	void reset();
	
	String getModuleLabel();
	
	List<AmazonSales> getExistingSalesList();
	
	List<AmazonSales> getImportedSalesList();
	
	List<RegistryItem> getNonExistentItems();

	List<AmazonSales> getNonExistentSales();
	
	List<AmazonSales> getExcludedSales();
	
	List<Sales> getGeneratedSales();
	
	
	public static class AmazonSales implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String purchaseReference;
		private Date issueDate;
		private String buyerEmail;
		private String buyerName;
		private String buyerPhoneNumber;
		private String shipAddress1;
		private String shipAddress2;
		private String shipAddress3;
		private String shipCity;
		private String shipState;
		private String shipPostalCode;
		private String shipCountry;
		private String shipPhoneNumber;
		private String purchaseDate;
		private String fulfillmentChannel;
		private String salesChannel;
		
		private Integer salesId;
		private Integer itemId;
		
		private String sellerName;
		private String customerName;
		private boolean newCustomer;
		
		private Sales sales;
		private Seller seller;
		private Customer customer;

		private boolean repeated;
		
		private String observation;
		
		public boolean isRepeated() {
			return repeated;
		}
		public void setRepeated(boolean repeated) {
			this.repeated = repeated;
		}
		public Date getIssueDate() {
			return issueDate;
		}
		public void setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
		}
		public Integer getSalesId() {
			return salesId;
		}
		public void setSalesId(Integer salesId) {
			this.salesId = salesId;
		}
		public Sales getSales() {
			return sales;
		}
		public void setSales(Sales sales) {
			this.sales = sales;
		}
		public Seller getSeller() {
			return seller;
		}
		public void setSeller(Seller seller) {
			this.seller = seller;
		}
		public Customer getCustomer() {
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public Integer getItemId() {
			return itemId;
		}
		public void setItemId(Integer itemId) {
			this.itemId = itemId;
		}
		public String getPurchaseReference() {
			return purchaseReference;
		}
		public void setPurchaseReference(String purchaseReference) {
			this.purchaseReference = purchaseReference;
		}
		public String getSellerName() {
			return sellerName;
		}
		public void setSellerName(String sellerName) {
			this.sellerName = sellerName;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public boolean isNewCustomer() {
			return newCustomer;
		}
		public void setNewCustomer(boolean newCustomer) {
			this.newCustomer = newCustomer;
		}
		public String getBuyerEmail() {
			return buyerEmail;
		}
		public void setBuyerEmail(String buyerEmail) {
			this.buyerEmail = buyerEmail;
		}
		public String getBuyerName() {
			return buyerName;
		}
		public void setBuyerName(String buyerName) {
			this.buyerName = buyerName;
		}
		public String getBuyerPhoneNumber() {
			return buyerPhoneNumber;
		}
		public void setBuyerPhoneNumber(String buyerPhoneNumber) {
			this.buyerPhoneNumber = buyerPhoneNumber;
		}
		public String getShipAddress1() {
			return shipAddress1;
		}
		public void setShipAddress1(String shipAddress1) {
			this.shipAddress1 = shipAddress1;
		}
		public String getShipAddress2() {
			return shipAddress2;
		}
		public void setShipAddress2(String shipAddress2) {
			this.shipAddress2 = shipAddress2;
		}
		public String getShipAddress3() {
			return shipAddress3;
		}
		public void setShipAddress3(String shipAddress3) {
			this.shipAddress3 = shipAddress3;
		}
		public String getShipCity() {
			return shipCity;
		}
		public void setShipCity(String shipCity) {
			this.shipCity = shipCity;
		}
		public String getShipState() {
			return shipState;
		}
		public void setShipState(String shipState) {
			this.shipState = shipState;
		}
		public String getShipPostalCode() {
			return shipPostalCode;
		}
		public void setShipPostalCode(String shipPostalCode) {
			this.shipPostalCode = shipPostalCode;
		}
		public String getShipCountry() {
			return shipCountry;
		}
		public void setShipCountry(String shipCountry) {
			this.shipCountry = shipCountry;
		}
		public String getShipPhoneNumber() {
			return shipPhoneNumber;
		}
		public void setShipPhoneNumber(String shipPhoneNumber) {
			this.shipPhoneNumber = shipPhoneNumber;
		}
		public String getPurchaseDate() {
			return purchaseDate;
		}
		public void setPurchaseDate(String purchaseDate) {
			this.purchaseDate = purchaseDate;
		}
		public String getFulfillmentChannel() {
			return fulfillmentChannel;
		}
		public void setFulfillmentChannel(String fulfillmentChannel) {
			this.fulfillmentChannel = fulfillmentChannel;
		}
		public String getSalesChannel() {
			return salesChannel;
		}
		public void setSalesChannel(String salesChannel) {
			this.salesChannel = salesChannel;
		}
		public String getObservation() {
			return observation;
		}
		public void setObservation(String observation) {
			this.observation = observation;
		}
		
	}
	
	public static class AmazonSalesDetail implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private AmazonSales amazonSales;
		private String purchaseReference;
		private String sku;
		private String price;
		private String currency;
		private String quantity;
		private String productName;
		
		public AmazonSales getAmazonSales() {
			return amazonSales;
		}
		public void setAmazonSales(AmazonSales amazonSales) {
			this.amazonSales = amazonSales;
		}
		public String getPurchaseReference() {
			return purchaseReference;
		}
		public void setPurchaseReference(String purchaseReference) {
			this.purchaseReference = purchaseReference;
		}
		public String getSku() {
			return sku;
		}
		public void setSku(String sku) {
			this.sku = sku;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
	}
	
}
