package com.code.aon.ui.sales.importer;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Tax;
import com.code.aon.customer.Customer;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.seller.Seller;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesAppParamController;
import com.code.aon.ui.sales.controller.SalesAppParamController.AmazonSalesChannel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ImporterUtils implements Serializable {
		
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImporterUtils.class.getName());
	
	private static String CURRENCY_GBP = "GBP";
	private static String CURRENCY_EUR = "EUR";
	
	private static String CUSTOMER_NAME_UK = "CLIENTE CONTADO GRAN BRETAÑA";
	private static String CUSTOMER_NAME_DE = "CLIENTE CONTADO ALEMANIA";
	private static String CUSTOMER_NAME_FR = "CLIENTE CONTADO FRANCIA";
	private static String CUSTOMER_NAME_IT = "CLIENTE CONTADO ITALIA";
	private static String CUSTOMER_NAME_ES = "Ventas AMAZON MARKETPLACE";

	private static String FUL_FILLMENT_CHANNEL_AMAZON = "amazon";
	private static String FUL_FILLMENT_CHANNEL_MERCHANT = "merchant";
	
	private static String SALES_CHANNEL_UK = "amazon.co.uk";
	private static String SALES_CHANNEL_DE = "amazon.de";
	private static String SALES_CHANNEL_FR = "amazon.fr";
	private static String SALES_CHANNEL_IT = "amazon.it";
	private static String SALES_CHANNEL_ES = "amazon.es";
	
	private static Map<String, Customer> amazonCustomerMap = null;
	private static Map<String, Seller> amazonSellerMap = null;
	
	
	public static int obtainHeaderPosition(String[] headers, String _header) {
		int flag = -1;
		for(int i=0; i<headers.length; ++i){
		    if(StringUtils.equals(headers[i], _header)) {
		        flag = i;
		        break;
		    }
		}
		return flag;
	}
	
	public static boolean validColumns(String[] line, int... columns) {
		boolean valid = true;
		for(int column: columns){
			if(line.length < column){
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	public static boolean validColumns(String[] line, String... columns) {
		boolean valid = true;
		for(String column: columns){
			if(obtainHeaderPosition(line, column)<=-1){
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	public static boolean checkCustomer(String salesChannel){
		return StringUtils.equals(salesChannel.trim().toLowerCase(), SALES_CHANNEL_ES);
	}
	
	public static WorkPlace obtainWorkPlace() {
		CompanyCollectionsController companyCollections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		try {
			if(companyCollections.getCurrentUserWorkPlaceList().size()>1){
				AonUtil.addErrorMessage("Se ha detectado mas de un centro de trabajo.");
				AonUtil.addErrorMessage("Se asigna el primero.");
			}
			return (WorkPlace) companyCollections.getCurrentUserWorkPlaceList().get(0);
		} catch (ManagerBeanException e) {
			LOGGER.error("SalesUtils.obtainWorkPlace: " + e.getMessage());
		}
		return null;
	}
	
	public static GeoZone obtainGeozone(String code) {
		try {
			IManagerBean rMediaBean = BeanManager.getManagerBean(GeoZone.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.GEO_ZONE_CODE), code);
			List<ITransferObject> list = rMediaBean.getList(criteria);
			if (! list.isEmpty() ) {
				return (GeoZone) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining GeoZone", e);
		}			
		return null;
	}

	public static Item obtainItem(Integer id) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Item.class);
			return (Item) bean.get(id);
		} catch (ManagerBeanException e) {
			LOGGER.error("SalesUtils.obtainItem: " + e.getMessage());
		}
		return null;
	}

	public static Double obtainItemPrice(Tax vat, String price) {
		if(!NumberUtils.isNumber(price)){
			return null;
		}
		Double notVatPrice = Double.valueOf(price);
		notVatPrice /= (1 + (vat.getPercentage()/100));
		return notVatPrice;
	}
	
	public static DiscountExpression obtainItemDiscountExpression(Double itemPrice, Double amazonPrice, String currency) {
		try {
			if(itemPrice!=amazonPrice && amazonPrice!=null && itemPrice!=null){
				Double currencyFactor = 1.0;
				if(StringUtils.isNotBlank(currency) && currency.toUpperCase().equals(CURRENCY_GBP)){
					currencyFactor = 1.2674;
				} else if(StringUtils.isNotBlank(currency) && !currency.toUpperCase().equals(CURRENCY_EUR)){
					LOGGER.error("Currency not supported " + currency);
					AonUtil.addErrorMessage("Currency not supported " + currency);
				}
				double percent = (1 - ((amazonPrice * currencyFactor)/itemPrice));
				return new DiscountExpression( String.valueOf(CommonUtil.round(percent*100,4)) );
			}
		} catch (NumberFormatException e) {
			LOGGER.error("SalesUtils.obtainItemDiscountExpression: " + e.getMessage());
		}
		return new DiscountExpression("0");
	}
	
	public static String obtainCustomerName(String salesChannel) {
		String name = null;
		if(StringUtils.isNotBlank(salesChannel) && salesChannel.toLowerCase().equals(SALES_CHANNEL_UK)){
			name = CUSTOMER_NAME_UK;
		} else if(StringUtils.isNotBlank(salesChannel) && salesChannel.toLowerCase().equals(SALES_CHANNEL_DE)){
			name = CUSTOMER_NAME_DE;
		} else if(StringUtils.isNotBlank(salesChannel) && salesChannel.toLowerCase().equals(SALES_CHANNEL_FR)){
			name = CUSTOMER_NAME_FR;
		} else if(StringUtils.isNotBlank(salesChannel) && salesChannel.toLowerCase().equals(SALES_CHANNEL_IT)){
			name = CUSTOMER_NAME_IT;
		} else if(StringUtils.isNotBlank(salesChannel) && salesChannel.toLowerCase().equals(SALES_CHANNEL_ES)){
			name = CUSTOMER_NAME_ES;
		}
		return name;
	}
	public static String obtainCustomerName(String fulfillmentChannel, String salesChannel) {
		String name = null;
		if(StringUtils.isNotBlank(fulfillmentChannel) && fulfillmentChannel.toLowerCase().equals(FUL_FILLMENT_CHANNEL_AMAZON)){
			name = obtainCustomerName(salesChannel);
		} else if(StringUtils.isNotBlank(fulfillmentChannel) && fulfillmentChannel.toLowerCase().equals(FUL_FILLMENT_CHANNEL_MERCHANT)){
			name = CUSTOMER_NAME_ES;
		}
		return name;
	}
	
	public static Customer obtainCustomer(String fulfillmentChannel, String salesChannel) {
		if(amazonCustomerMap==null){
			loadCustomerMap();
		}
		String name = obtainCustomerName(fulfillmentChannel, salesChannel);
		return amazonCustomerMap.get(name);
	}
	
	private static void loadCustomerMap() {
		amazonCustomerMap = new HashMap<String, Customer>();
		String name = CUSTOMER_NAME_UK;
		amazonCustomerMap.put(name, obtainCustomerByName(name));
		name = CUSTOMER_NAME_DE;
		amazonCustomerMap.put(name, obtainCustomerByName(name));
		name = CUSTOMER_NAME_FR;
		amazonCustomerMap.put(name, obtainCustomerByName(name));
		name = CUSTOMER_NAME_IT;
		amazonCustomerMap.put(name, obtainCustomerByName(name));
		name = CUSTOMER_NAME_ES;
		amazonCustomerMap.put(name, obtainCustomerByName(name));
	}

	private static Customer obtainCustomerByName(String name) {
		Result<Record1<Integer>> records = getCustomerRecords(name);
		if(records!=null && records.size()>0){
			Integer id = records.get(0).value1();
			try {
				IManagerBean bean = BeanManager.getManagerBean(Customer.class);
				return (Customer) bean.get(id);
			} catch (ManagerBeanException e) {
				LOGGER.error("SalesUtils.obtainCustomer: " + e.getMessage());
			}
		} else {
			AonUtil.addErrorMessage("No se ha podido localizar el cliente " + name );
		}
		return null;
	}
			
	public static Seller obtainSeller(String salesChannel) {
		if(amazonSellerMap==null){
			loadAmazonSellerMap();
		}
		return amazonSellerMap.get(salesChannel.toLowerCase());
	}
	
	public static void loadAmazonSellerMap() {
		SalesAppParamController params = (SalesAppParamController) AonUtil.getRegisteredBean("salesAppParam");
		amazonSellerMap = new HashMap<String, Seller>();
		for(AmazonSalesChannel asc: params.getAmazonSalesChannels()){
			amazonSellerMap.put(asc.getName(), asc.getSeller());
		}
	}
	public static String obtainAmazonSalesProductPrefix() {
		SalesAppParamController params = (SalesAppParamController) AonUtil.getRegisteredBean("salesAppParam");
		return params.getAmazonSalesProductName().get("prefix");
	}
	public static String obtainAmazonSalesProductSuffix() {
		SalesAppParamController params = (SalesAppParamController) AonUtil.getRegisteredBean("salesAppParam");
		return params.getAmazonSalesProductName().get("suffix");
	}
		
	public static Seller obtainSellerByName(String name) {
		Result<Record1<Integer>> records = getSellerRecords(name);
		if(records!=null && records.size()>0 ){
			Integer id = records.get(0).value1();
			try {
				IManagerBean bean = BeanManager.getManagerBean(Seller.class);
				return (Seller) bean.get(id);
			} catch (ManagerBeanException e) {
				LOGGER.error("SalesUtils.obtainSeller: " + e.getMessage());
			}
		} else {
			AonUtil.addErrorMessage("No se ha podido localizar el comercial " + name);
		}
		return null;
	}

	public static List<Sales> obtainSales(String amazonOrderId) {
		Result<Record2<Integer, String>> records = getSalesRecords(Arrays.asList(amazonOrderId));
		List<Sales> salesList = new LinkedList<Sales>();
		try {
			for(Record2<Integer, String> record: records){
				Integer id = record.value1();
				salesList.add((Sales) BeanManager.getManagerBean(Sales.class).get(id));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("SalesUtils.obtainSales: " + e.getMessage());
		}
		return salesList;
	}
	
	// ////////////////
	// SQL
	// ////////////////
	public static Result<Record1<Integer>> getCustomerRecords(String name) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record1<Integer>> record = ctx
					.select(REGISTRY.ID)
					.from(REGISTRY)
					.where(REGISTRY.DOMAIN.equal(DomainManager
							.getCurrentDomain()))
					.and(REGISTRY.NAME.likeIgnoreCase("%" + name + "%")).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	public static Result<Record2<Integer, String>> getCustomerRecords(List<String> emailList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			
			Result<Record2<Integer, String>> record = ctx
					.select(CUSTOMER.REGISTRY, RMEDIA.VALUE)
					.from(CUSTOMER.leftOuterJoin(RMEDIA).on(RMEDIA.REGISTRY.equal(CUSTOMER.REGISTRY)))
					.where(CUSTOMER.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(DSL.trim(RMEDIA.VALUE).in(emailList))
					.and(RMEDIA.MEDIA.equal((byte) MediaType.EMAIL.ordinal()))
					.fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record1<Integer>> getSellerRecords(String alias) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record1<Integer>> record = ctx
					.select(REGISTRY.ID)
					.from(REGISTRY)
					.where(REGISTRY.DOMAIN.equal(DomainManager
							.getCurrentDomain()))
					.and(REGISTRY.ALIAS.likeIgnoreCase(alias)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record2<Integer, String>> getItemRecords(
			Collection<String> codeList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Integer, String>> record = ctx
					.select(RITEM.ITEM, RITEM.CODE)
					.from(RITEM)
					.where(RITEM.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(DSL.trim(RITEM.CODE).in(codeList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record1<String>> getProductRecords(
			Collection<String> codeList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record1<String>> record = ctx
					.select(PRODUCT.CODE)
					.from(PRODUCT)
					.where(PRODUCT.DOMAIN.equal(DomainManager
							.getCurrentDomain()))
					.and(DSL.trim(PRODUCT.CODE).in(codeList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record2<Integer, String>> getSalesRecords(
			Collection<String> codeList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Integer, String>> record = ctx
					.select(SALES.ID, SALES.PURCHASE_REFERENCE)
					.from(SALES)
					.where(SALES.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(SALES.DOCUMENT_TYPE.equal((byte) DocumentType.NORMAL.ordinal()))
					.and(DSL.trim(SALES.PURCHASE_REFERENCE).in(codeList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record2<Integer, String>> getSalesReturnRecords(
			Collection<String> codeList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Integer, String>> record = ctx
					.select(SALES.ID, SALES.PURCHASE_REFERENCE)
					.from(SALES)
					.where(SALES.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(SALES.DOCUMENT_TYPE.equal((byte) DocumentType.ITEM_RETURN.ordinal()))
					.and(DSL.trim(SALES.PURCHASE_REFERENCE).in(codeList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record2<Integer, String>> getShipmentDataSalesRecords(
			Collection<String> codeList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Integer, String>> record = ctx
					.select(SALES.ID, SALES.PURCHASE_REFERENCE)
					.from(SALES)
					.where(SALES.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(SALES.DOCUMENT_TYPE.equal((byte) DocumentType.NORMAL.ordinal()))
					.and( SALES.SHIPPING_ALTERNATIVE_ADDRESS.isNotNull()
							.or(SALES.SHIPPING_ALTERNATIVE_ADDRESS2.isNotNull()) )
					.and(DSL.trim(SALES.PURCHASE_REFERENCE).in(codeList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	public static Result<Record2<Double, String>>  getLastSalesDetailRecords(Integer itemId, String purchaseReference) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Double, String>> record = ctx
					.select(SALES_DETAIL.PRICE, SALES_DETAIL.DISCOUNT_EXPR)
					.from(SALES_DETAIL.leftOuterJoin(SALES).on(SALES.ID.equal(SALES_DETAIL.SALES)))
					.where(SALES_DETAIL.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(SALES_DETAIL.ITEM.equal(itemId))
					.and(DSL.trim(SALES.PURCHASE_REFERENCE).equal(purchaseReference))
					.orderBy(SALES_DETAIL.PRICE).limit(1)
					.fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
}
