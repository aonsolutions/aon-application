package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryExpirationUtils;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class CustomerJSON {
	
	private CustomerJSON() {
	
	}
	
	public static List<Customer> fromJSON(JSONArray json) {
		LinkedList<Customer> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Optional<Customer> from(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of(fromJSON(json));
	}
	
	public static Customer fromJSON(JSONObject json) {
		return new Customer()
			.copy(RegistryJSON.fromJSON(json))
			.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
			.setDeliveryGrouped(JsonUtils.getboolean(json, IJsonNames.DELIVERY_GROUPED))
			.setDeliveryValuated(JsonUtils.getboolean(json, IJsonNames.DELIVERY_VALUATED))
			.setEInvoice(JsonUtils.getboolean(json, IJsonNames.E_INVOICE))
			.setInvoicingGroup(JsonUtils.getInteger(json, IJsonNames.INVOICING_GROUP))
			.setProjectGrouped(JsonUtils.getboolean(json, IJsonNames.PROJECT_GROUPED))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setTariff(JsonUtils.getInteger(json, IJsonNames.TARIFF))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json, IJsonNames.TRANSACTION)))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setStatus(RegistryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setExpirationDate(AonDateUtils.simpleParse(JsonUtils.getString(json, IJsonNames.EXPIRATION_DATE)));
	}
	

	public static JSONArray toJSON(List<Customer> customers) {
		return toJSON(customers.stream());
	}
	
	public static JSONArray toJSON(Stream<Customer> customers) {
		JSONArray array = new JSONArray();
		customers.forEach(customer -> array.put(toJSON(customer)));
		return array;
	}
	
	public static JSONObject toJSON(Customer customer) {
		if(customer == null) return null;
		return RegistryJSON.toJSON(customer)
			.put(IJsonNames.SURCHARGE, customer.isSurcharge())
			.put(IJsonNames.WITHHOLDING, customer.isWithholding())
			.put(IJsonNames.ACCOUNT, customer.getAccount())
			.put(IJsonNames.DELIVERY_GROUPED, customer.isDeliveryGrouped())
			.put(IJsonNames.DELIVERY_VALUATED, customer.isDeliveryValuated())
			.put(IJsonNames.E_INVOICE, customer.isEInvoice())
			.put(IJsonNames.INVOICING_GROUP, customer.getInvoicingGroup())
			.put(IJsonNames.PROJECT_GROUPED, customer.isProjectGrouped())
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(customer.getScope()))
			.put(IJsonNames.TARIFF, customer.getTariff())
			.put(IJsonNames.TRANSACTION, customer.getTransaction().getTediName())
			.put(IJsonNames.STATUS, customer.getStatus() != null
					? customer.getStatus().name()
					: RegistryStatus.ACTIVE.name())
		
			// Fecha de expiracion (solo con status = BLOCKED)
			.put(IJsonNames.EXPIRATION_DATE, customer.getExpirationDate() != null
					? AonDateUtils.format(customer.getExpirationDate(), AonDateUtils.SIMPLE_DATE_FORMAT4)
					: JSONObject.NULL)
		
			// Estado EFECTIVO: un BLOCKED con fecha futura sale como ACTIVE.
			// 'status' se deja intacto porque es lo que edita la ficha.
			.put("effectiveStatus", RegistryExpirationUtils
					.effective(customer.getStatus(), customer.getExpirationDate())
					.name())
			.put(IJsonNames.CREATION_USER, customer.getCreationUser())
			.put(IJsonNames.CREATION_DATE, customer.getCreationDate()!=null ? customer.getCreationDate().getTime() : null)
			.put(IJsonNames.MODIFICATION_USER, customer.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, customer.getModificationDate()!=null ? customer.getModificationDate().getTime() : null)
			.put("isRelationship", customer.isRelationship())
			;
	}
}
