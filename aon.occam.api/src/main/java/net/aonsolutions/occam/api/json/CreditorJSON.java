package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

public class CreditorJSON {
	
	private CreditorJSON() {
	}
	
	public static Creditor fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return  
			RegistryJSON.fromJSON(json, Creditor::new )
			.setStatus(RegistryStatus.value(JsonUtils.getString(json, IJsonNames.STATUS)).orElse(null))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT))
			.setTransaction(InvoiceTransactionType.value(JsonUtils.getString(json, IJsonNames.TRANSACTION)).orElse(null))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setAccount(AccountJSON.fromJSON( JsonUtils.getJSONObject(json, IJsonNames.ACCOUNT)))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.parseDateTime(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.parseDateTime(json, IJsonNames.MODIFICATION_DATE))
		;
	}

	public static JSONObject toJSON(Creditor creditor) {
		if (creditor == null) return null;
		return RegistryJSON.toJSON(creditor)
			.put(IJsonNames.STATUS, RegistryStatus.name(creditor.getStatus()))
			.put(IJsonNames.WITHHOLDING, creditor.isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, creditor.isVatAccrualPayment())
			.put(IJsonNames.TRANSACTION, InvoiceTransactionType.name(creditor.getTransaction()))
			.put(IJsonNames.SCOPE, creditor.getScope())
			.put(IJsonNames.ACCOUNT, AccountJSON.toJSON(creditor.getAccount().orElse(null)))
			.put(IJsonNames.CREATION_USER, creditor.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.formatDateTime( creditor.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, creditor.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.formatDateTime( creditor.getModificationDate()))
		;
	}
}
