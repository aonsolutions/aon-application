package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Supplier;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

public class SupplierJSON {
	
	private SupplierJSON() {
	}
	
	public static Supplier fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return  
			RegistryJSON.fromJSON(json, Supplier::new )
			.setTariff(JsonUtils.getInteger(json, IJsonNames.TARIFF))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setWithholdingFarmer(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING_FARMER))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT))
			.setTransaction(InvoiceTransactionType.value(JsonUtils.getString(json, IJsonNames.TRANSACTION)).orElse(null))
			.setStatus(RegistryStatus.value(JsonUtils.getString(json, IJsonNames.STATUS)).orElse(null))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setPurchaseValuated(JsonUtils.getboolean(json, IJsonNames.PURCHASE_VALUATED))
			.setAccount(AccountJSON.fromJSON( JsonUtils.getJSONObject(json, IJsonNames.ACCOUNT)))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.parseDateTime(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.parseDateTime(json, IJsonNames.MODIFICATION_DATE))
		;
	}

	public static JSONObject toJSON(Supplier creditor) {
		if (creditor == null) return null;
		return RegistryJSON.toJSON(creditor)
			.put(IJsonNames.TARIFF, creditor.getTariff())
			.put(IJsonNames.WITHHOLDING, creditor.isWithholding())
			.put(IJsonNames.WITHHOLDING_FARMER, creditor.isWithholdingFarmer())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, creditor.isVatAccrualPayment())
			.put(IJsonNames.TRANSACTION, InvoiceTransactionType.name(creditor.getTransaction()))
			.put(IJsonNames.STATUS, RegistryStatus.name(creditor.getStatus()))
			.put(IJsonNames.SCOPE, creditor.getScope())
			.put(IJsonNames.PURCHASE_VALUATED, creditor.isPurchaseValuated())
			.put(IJsonNames.ACCOUNT, AccountJSON.toJSON(creditor.getAccount().orElse(null)))
			.put(IJsonNames.CREATION_USER, creditor.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.formatDateTime( creditor.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, creditor.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.formatDateTime( creditor.getModificationDate()))
		;
	}
}
