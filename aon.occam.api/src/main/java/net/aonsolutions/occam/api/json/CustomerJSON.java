package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Customer;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

public class CustomerJSON {
	
	private CustomerJSON() {
	}
	
	public static Customer fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return  
			RegistryJSON.fromJSON(json, Customer::new )
			.setTariff(JsonUtils.getInteger(json, IJsonNames.TARIFF))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setTransaction(InvoiceTransactionType.value(JsonUtils.getString(json, IJsonNames.TRANSACTION)).orElse(null))
			.setStatus(RegistryStatus.value(JsonUtils.getString(json, IJsonNames.STATUS)).orElse(null))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setEInvoice(JsonUtils.getboolean(json, IJsonNames.E_INVOICE))
			.setInvoicingGroup(JsonUtils.getInteger(json, IJsonNames.INVOICING_GROUP))
			.setProjectGrouped(JsonUtils.getboolean(json, IJsonNames.PROJECT_GROUPED))
			.setDeliveryGrouped(JsonUtils.getboolean(json, IJsonNames.DELIVERY_GROUPED))
			.setDeliveryValuated(JsonUtils.getboolean(json, IJsonNames.DELIVERY_VALUATED))
			.setAccount(AccountJSON.fromJSON( JsonUtils.getJSONObject(json, IJsonNames.ACCOUNT)))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.parseDateTime(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.parseDateTime(json, IJsonNames.MODIFICATION_DATE))
		;
	}
	
	public static JSONObject toJSON(Customer creditor) {
		if (creditor == null) return null;
		return RegistryJSON.toJSON(creditor)
			.put(IJsonNames.TARIFF, creditor.getTariff())
			.put(IJsonNames.SURCHARGE, creditor.isSurcharge())
			.put(IJsonNames.WITHHOLDING, creditor.isWithholding())
			.put(IJsonNames.TRANSACTION, InvoiceTransactionType.name(creditor.getTransaction()))
			.put(IJsonNames.STATUS, RegistryStatus.name(creditor.getStatus()))
			.put(IJsonNames.SCOPE, creditor.getScope())
			.put(IJsonNames.E_INVOICE, creditor.isEInvoice())
			.put(IJsonNames.INVOICING_GROUP, creditor.getInvoicingGroup())
			.put(IJsonNames.PROJECT_GROUPED, creditor.isProjectGrouped())
			.put(IJsonNames.DELIVERY_GROUPED, creditor.isDeliveryGrouped())
			.put(IJsonNames.DELIVERY_VALUATED, creditor.isDeliveryValuated())
			.put(IJsonNames.ACCOUNT, AccountJSON.toJSON(creditor.getAccount().orElse(null)))
			.put(IJsonNames.CREATION_USER, creditor.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.formatDateTime( creditor.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, creditor.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.formatDateTime( creditor.getModificationDate()))
		;
	}
}
