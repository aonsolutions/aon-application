package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;

public class TargetJSON {
	
	private TargetJSON() {
	
	}
	
	public static List<Target> fromJSON(JSONArray json) {
		LinkedList<Target> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Target fromJSON(JSONObject json) {
		return new Target()
			.copy(RegistryJSON.fromJSON(json))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.ID))
			.setTariff(TariffJSON.fromJSON(json.optJSONObject(IJsonNames.TARIFF)))
			.setAdvertising(Advertising.safeValueOf(JsonUtils.getString(json, IJsonNames.ADVERTISING)))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setStatus(TargetStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json, IJsonNames.TRANSACTION)))
			;
	}

	public static JSONArray toJSON(List<Target> targets) {
		return toJSON(targets.stream());
	}
	
	public static JSONArray toJSON(Stream<Target> targets) {
		JSONArray array = new JSONArray();
		targets.forEach(target -> array.put(toJSON(target)));
		return array;
	}
	
	public static JSONObject toJSON(Target target) {
		return RegistryJSON.toJSON(target)
			.put(IJsonNames.REGISTRY, target.getId())
			.put(IJsonNames.TARIFF, target.getTariff().getId()!=null ? TariffJSON.toJSON(target.getTariff()) : null)
			.put(IJsonNames.ADVERTISING, target.getAdvertising() != null ? target.getAdvertising().name() : null)
			.put(IJsonNames.SURCHARGE, target.isSurcharge())
			.put(IJsonNames.WITHHOLDING, target.isWithholding())
			.put(IJsonNames.STATUS, target.getStatus() != null ? target.getStatus().name() : TargetStatus.ACTIVE.name())
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(target.getScope()))
			.put(IJsonNames.TRANSACTION,  target.getTransaction() != null ? target.getTransaction().name() : null)
			.put(IJsonNames.CREATION_USER, target.getCreationUser())
			.put(IJsonNames.CREATION_DATE, target.getCreationDate()!=null ? target.getCreationDate().getTime() : null)
			.put(IJsonNames.MODIFICATION_USER, target.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, target.getModificationDate()!=null ? target.getModificationDate().getTime() : null)
			;
	}
}
