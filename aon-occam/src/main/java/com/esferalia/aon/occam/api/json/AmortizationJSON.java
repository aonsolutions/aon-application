package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AmortizationJSON {

	private AmortizationJSON() {

	}

	public static List<Amortization> fromJSON(JSONArray json) {
		LinkedList<Amortization> list = new LinkedList<>();
		for (Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
		return list;
	}

	public static Amortization fromJSON(JSONObject json) {
		if (json == null) return new Amortization();
		Amortization amortization = new Amortization()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setInvestAsset(InvestAssetJSON.from(JsonUtils.getJSONObject(json, IJsonNames.INVEST_ASSET)).orElse(null))
				.setAllocationAccount(AccountJSON.from(JsonUtils.getJSONObject(json, "allocationAccount")).orElse(null))
				.setAccumulatedAccount(AccountJSON.from(JsonUtils.getJSONObject(json, "accumulatedAccount")).orElse(null))
				.setFixedAssetAccount(AccountJSON.from(JsonUtils.getJSONObject(json, "fixedAssetAccount")).orElse(null))
				.setDescription(JsonUtils.optString(json, IJsonNames.DESCRIPTION))
				.setInitialDate(JsonUtils.getDate(json, "initialDate"))
				.setDeadline(JsonUtils.getDate(json, IJsonNames.DEADLINE))
				.setAmount(json.isNull(IJsonNames.AMOUNT) ? null : json.getDouble(IJsonNames.AMOUNT))
				.setFeePeriod(JsonUtils.optString(json, "feePeriod") != null
						? AmortizationPeriod.safeValueOf(JsonUtils.getInteger(json, "feePeriod")).orElse(null)
						: null)
				.setSaleAmount(json.isNull("saleAmount") ? null : json.getDouble("saleAmount"))
				.setComments(JsonUtils.optString(json, IJsonNames.COMMENTS))
				.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
				.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL))
				.setAmortizationType(AmortizationTypeJSON.fromJSON(JsonUtils.getJSONObject(json, "amortizationType")));

		JSONArray details = json.optJSONArray("details");
		if (details != null) {
			for (int i = 0; i < details.length(); i++) {
				amortization.addDetail(detailFromJSON(details.getJSONObject(i)));
			}
		}
		return amortization;
	}

	public static JSONArray toJSON(List<Amortization> list) {
		return toJSON(list.stream());
	}

	public static JSONArray toJSON(Stream<Amortization> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(a -> array.put(toJSON(a)));
		return array;
	}

	public static JSONObject toJSON(Amortization a) {
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, a.getId())
				.put(IJsonNames.DOMAIN, a.getDomain())
				.put(IJsonNames.INVEST_ASSET, a.getInvestAsset() != null && a.getInvestAsset().getId() != null
					? InvestAssetJSON.toJSON(a.getInvestAsset()) : JSONObject.NULL)
				.put("allocationAccount", AccountJSON.to(a.getAllocationAccount()).orElse(null))
				.put("accumulatedAccount", AccountJSON.to(a.getAccumulatedAccount()).orElse(null))
				.put("fixedAssetAccount", AccountJSON.to(a.getFixedAssetAccount()).orElse(null))
				.put(IJsonNames.DESCRIPTION, a.getDescription())
				.put("initialDate", AonDateUtils.format(a.getInitialDate(), "yyyy-MM-dd"))
				.put(IJsonNames.DEADLINE, AonDateUtils.format(a.getDeadline(), "yyyy-MM-dd"))
				.put(IJsonNames.AMOUNT, a.getAmount())
				.put("feePeriod", a.getFeePeriod() != null ? a.getFeePeriod().name() : JSONObject.NULL)
				.put("saleAmount", a.getSaleAmount())
				.put(IJsonNames.COMMENTS, a.getComments())
				.put(IJsonNames.PERCENTAGE, a.getPercentage())
				.put(IJsonNames.CONFIDENTIAL, a.isConfidential())
				.put("amortizationType", a.getAmortizationType() != null ? AmortizationTypeJSON.toJSON(a.getAmortizationType()) : JSONObject.NULL);

		JSONArray details = new JSONArray();
		a.detailStream().forEach(d -> details.put(detailToJSON(d)));
		json.put("details", details);

		return json;
	}

	public static AmortizationDetail detailFromJSON(JSONObject json) {
		if (json == null) return new AmortizationDetail();
		return new AmortizationDetail()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setAmortization(JsonUtils.getInteger(json, "amortization"))
				.setAccountEntry(JsonUtils.getInteger(json, "accountEntry"))
				.setFromDate(JsonUtils.getDateFormat(json, "fromDate", "yyyy-MM-dd"))
				.setToDate(JsonUtils.getDateFormat(json, "toDate", "yyyy-MM-dd"))
				.setCoefficient(JsonUtils.getdouble(json, "coefficient"))
				.setAllocation(JsonUtils.getdouble(json, "allocation"))
				.setStatus(AmortizationDetailStatus.safeValueOf(JsonUtils.getInteger(json, IJsonNames.STATUS)).orElse(null))
				.setFiscalAllocation(JsonUtils.getdouble(json, "fiscalAllocation"))
				.setAccumulated(JsonUtils.getdouble(json, "accumulated"))
				.setPending(JsonUtils.getdouble(json, "pending"))
				.setFiscalAccumulated(JsonUtils.getdouble(json, "fiscalAccumulated"))
				.setFiscalPending(JsonUtils.getdouble(json, "fiscalPending"))
				.setSelected(json.optBoolean("selected"));
	}

	public static JSONObject detailToJSON(AmortizationDetail d) {
		return new JSONObject()
				.put(IJsonNames.ID, d.getId())
				.put(IJsonNames.DOMAIN, d.getDomain())
				.put("amortization", d.getAmortization())
				.put("accountEntry", d.getAccountEntry())
				.put("fromDate", AonDateUtils.format(d.getFromDate(), "yyyy-MM-dd"))
				.put("toDate", AonDateUtils.format(d.getToDate(), "yyyy-MM-dd"))
				.put("coefficient", d.getCoefficient())
				.put("allocation", d.getAllocation())
				.put(IJsonNames.STATUS, d.getStatus() != null ? d.getStatus().ordinal() : JSONObject.NULL)
				.put("fiscalAllocation", d.getFiscalAllocation())
				.put("accumulated", d.getAccumulated())
				.put("pending", d.getPending())
				.put("fiscalAccumulated", d.getFiscalAccumulated())
				.put("fiscalPending", d.getFiscalPending())
				.put("selected", d.isSelected());
	}
}
