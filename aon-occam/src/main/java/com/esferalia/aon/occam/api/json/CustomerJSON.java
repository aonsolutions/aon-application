package com.esferalia.aon.occam.api.json;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;

public class CustomerJSON {
	
	private CustomerJSON() {
	
	}
	
	public static Customer fromJSON(JSONObject json) {
		return new Customer()
			.copy(RegistryJSON.fromJSON(json))
			.setAccount(json.optInt(IJsonNames.ACCOUNT))
			.setDeliveryGrouped(JsonUtils.getboolean(json, IJsonNames.DELIVERY_GROUPED))
			.setDeliveryValuated(JsonUtils.getboolean(json, IJsonNames.DELIVERY_VALUATED))
			.setEInvoice(JsonUtils.getboolean(json, IJsonNames.E_INVOICE))
			.setInvoicingGroup(JsonUtils.getInteger(json, IJsonNames.INVOICING_GROUP))
			.setProjectGrouped(JsonUtils.getboolean(json, IJsonNames.PROJECT_GROUPED))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setTariff(JsonUtils.getInteger(json, IJsonNames.TARIFF))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json, IJsonNames.TRANSACTION)))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setStatus(RegistryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)));
	}
	
	public static JSONObject toJSON(Customer customer) {
		return RegistryJSON.toJSON(customer)
			.put(IJsonNames.SURCHARGE, customer.isSurcharge())
			.put(IJsonNames.WITHHOLDING, customer.isWithholding())
			.put(IJsonNames.ACCOUNT, customer.getAccount())
			.put(IJsonNames.DELIVERY_GROUPED, customer.isDeliveryGrouped())
			.put(IJsonNames.DELIVERY_VALUATED, customer.isDeliveryValuated())
			.put(IJsonNames.E_INVOICE, customer.isEInvoice())
			.put(IJsonNames.INVOICING_GROUP, customer.getInvoicingGroup())
			.put(IJsonNames.PROJECT_GROUPED, customer.isProjectGrouped())
			.put(IJsonNames.SCOPE, customer.getScope())
			.put(IJsonNames.TARIFF, customer.getTariff())
			.put(IJsonNames.TRANSACTION, customer.getTransaction().name())
			.put(IJsonNames.STATUS, customer.getStatus().name());
	}
}
