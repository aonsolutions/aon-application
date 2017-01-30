package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.sql.Date;
import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailProperties;

public class PropertiesDAO {
	
	private PropertiesDAO() {
		throw new IllegalAccessError("DAO class");
	}
	
	public static class CustomerPropertiesDAO implements CustomerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CustomerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CustomerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TARIFF);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SCOPE);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.E_INVOICE);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.INVOICING_GROUP);}
		@Override public Property<Byte> getProjectGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.PROJECT_GROUPED);}
		@Override public Property<Byte> getDeliveryGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_GROUPED);}
		@Override public Property<Byte> getDeliveryValuatedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_DATE);}	
	}
	
	public static class SellerPropertiesDAO implements SellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SELLER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);}
	}
	
	public static class CarrierPackingPropertiesDAO implements CarrierPackingProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.STATUS);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.TYPE);}
		@Override public Property<Timestamp> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ISSUE_DATE);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER);}
		@Override public Property<Timestamp> getDeliveryDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DELIVERY_DATE);}
		@Override public Property<String> getCarrierReferenceProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER_REFERENCE);}
		@Override public Property<String> getNumberPlateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER_PLATE);}
		@Override public Property<String> getDriverNameProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_NAME);}
		@Override public Property<String> getDriverDocumentProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_DOCUMENT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_DATE);}
	}
	
	public static class RegistryPropertiesDAO implements RegistryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class CarrierPropertiesDAO extends RegistryPropertiesDAO implements CarrierProperties  {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.SCOPE);}
	}
	
	protected static class PurchaseDetailPropertiesDAO implements PurchaseDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DOMAIN);}
		@Override public Property<Integer> getPurchaseProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PURCHASE);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PROJECT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.STATUS);}
		@Override public Property<Integer> getProposalDetailProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PROPOSAL_DETAIL);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.SOURCE_ID);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DELIVERED);}
	}
	
	protected static class PurchasePropertiesDAO implements PurchaseProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,PurchaseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(PurchaseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PROJECT);}
		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SUPPLIER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.NUMBER);}
		@Override public Property<String> getPurchaseReferenceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PURCHASE_REFERENCE);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ADDRESS);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DISCOUNT_EXPR);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ISSUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PAY_METHOD);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DOCUMENT_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.WORKPLACE);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.WAREHOUSE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BIC);}
		@Override public Property<Byte> getEmailCommunicationProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.EMAIL_COMMUNICATION);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CARRIER);}
		@Override public Property<String> getShippingAlternativeAddressProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS);}
		@Override public Property<String> getShippingAlternativeAddress2Property() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2);}
		@Override public Property<String> getShippingAlternativeZipProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ZIP);}
		@Override public Property<String> getShippingAlternativeCityProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_CITY);}
		@Override public Property<String> getShippingAlternativePhoneProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_PHONE);}
		@Override public Property<String> getShippingAlternativeRecipientProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT);}
		@Override public Property<String> getShippingContactProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_CONTACT);}
		@Override public Property<Byte> getShippingPeriodProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_PERIOD);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_DATE);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CARRIER_PACKING);}
	}
	
	protected static class DeliveryPropertiesDAO implements DeliveryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PROJECT);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CUSTOMER);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ADDRESS);}
		@Override public Property<Timestamp> getIssueTimeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ISSUE_TIME);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PAY_METHOD);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BIC);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_USER);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER_PACKING);}
	}
	
	protected static class DeliveryDetailPropertiesDAO implements DeliveryDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DOMAIN);}
		@Override public Property<Integer> getDelivery() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DELIVERY);}
		@Override public Property<Short> getLine() {return new FilterDAO.PropertyDAO<Short>(DELIVERY_DETAIL.LINE);}
		@Override public Property<Integer> getItem() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DESCRIPTION);}
		@Override public Property<Integer> getWarehouse() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.WAREHOUSE);}
		@Override public Property<Double> getQuantity() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.QUANTITY);}
		@Override public Property<Double> getPrice() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Integer> getSalesDetail() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.SALES_DETAIL);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_USER);}
	}
}
