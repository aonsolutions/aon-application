package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;

import java.sql.Date;
import java.sql.Timestamp;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailProperties;

public class PropertiesDAO {

	
	public static class CustomerPropertiesDAO implements CustomerProperties {
		protected Condition[] getConditions(CustomerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.TARIFF);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.SCOPE);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.E_INVOICE);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.INVOICING_GROUP);}
		@Override public Property<Byte> getProjectGroupedProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.PROJECT_GROUPED);}
		@Override public Property<Byte> getDeliveryGroupedProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.DELIVERY_GROUPED);}
		@Override public Property<Byte> getDeliveryValuatedProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.DELIVERY_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(CUSTOMER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CUSTOMER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(CUSTOMER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CUSTOMER.MODIFICATION_DATE);}	
	}
	
	public static class SellerPropertiesDAO implements SellerProperties {
		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(SELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(SELLER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(SELLER.SCOPE);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<Integer>(SELLER.COMMISSION_TYPE);}
	}
	
	public static class CarrierPackingPropertiesDAO implements CarrierPackingProperties {
		protected Condition[] getConditions(CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CARRIER_PACKING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CARRIER_PACKING.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(CARRIER_PACKING.STATUS);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(CARRIER_PACKING.NUMBER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(CARRIER_PACKING.TYPE);}
		@Override public Property<Timestamp> getIssueDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CARRIER_PACKING.ISSUE_DATE);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<Integer>(CARRIER_PACKING.CARRIER);}
		@Override public Property<Timestamp> getDeliveryDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CARRIER_PACKING.DELIVERY_DATE);}
		@Override public Property<String> getCarrierReferenceProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.CARRIER_REFERENCE);}
		@Override public Property<String> getNumberPlateProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.NUMBER_PLATE);}
		@Override public Property<String> getDriverNameProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.DRIVER_NAME);}
		@Override public Property<String> getDriverDocumentProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.DRIVER_DOCUMENT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CARRIER_PACKING.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(CARRIER_PACKING.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CARRIER_PACKING.MODIFICATION_DATE);}
	}
	
	public static class RegistryPropertiesDAO implements RegistryProperties {
		protected Condition[] getConditions(RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class CarrierPropertiesDAO extends RegistryPropertiesDAO implements CarrierProperties  {
		protected Condition[] getConditions(CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(CARRIER.SCOPE);}
	}
	
	protected static class PurchaseDetailPropertiesDAO implements PurchaseDetailProperties {
		protected Condition[] getConditions(PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.DOMAIN);}
		@Override public Property<Integer> getPurchaseProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PURCHASE);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PROJECT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<Short>(PURCHASE_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE_DETAIL.STATUS);}
		@Override public Property<Integer> getProposalDetailProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PROPOSAL_DETAIL);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE_DETAIL.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.SOURCE_ID);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.DELIVERED);}
	}
	
	protected static class PurchasePropertiesDAO implements PurchaseProperties {
		protected Condition[] getConditions(PurchaseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.PROJECT);}
		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.SUPPLIER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.NUMBER);}
		@Override public Property<String> getPurchaseReferenceProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.PURCHASE_REFERENCE);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.ADDRESS);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.DISCOUNT_EXPR);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<Date>(PURCHASE.ISSUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.PAY_METHOD);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE.DOCUMENT_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.WORKPLACE);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.WAREHOUSE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<Short>(PURCHASE.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<Short>(PURCHASE.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<Short>(PURCHASE.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.BIC);}
		@Override public Property<Byte> getEmailCommunicationProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE.EMAIL_COMMUNICATION);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.CARRIER);}
		@Override public Property<String> getShippingAlternativeAddressProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS);}
		@Override public Property<String> getShippingAlternativeAddress2Property() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2);}
		@Override public Property<String> getShippingAlternativeZipProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_ZIP);}
		@Override public Property<String> getShippingAlternativeCityProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_CITY);}
		@Override public Property<String> getShippingAlternativePhoneProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_PHONE);}
		@Override public Property<String> getShippingAlternativeRecipientProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT);}
		@Override public Property<String> getShippingContactProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.SHIPPING_CONTACT);}
		@Override public Property<Byte> getShippingPeriodProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE.SHIPPING_PERIOD);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PURCHASE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PURCHASE.MODIFICATION_DATE);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE.CARRIER_PACKING);}
	}
}
