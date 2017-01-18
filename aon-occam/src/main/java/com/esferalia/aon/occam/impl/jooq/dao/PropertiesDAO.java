package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.sql.Timestamp;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;

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
}
