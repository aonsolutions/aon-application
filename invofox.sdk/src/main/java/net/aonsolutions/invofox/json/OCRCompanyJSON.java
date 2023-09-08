package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRCompany;

public class OCRCompanyJSON {
	
	private OCRCompanyJSON() {
	}
	
	public static List<OCRCompany> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRCompanyJSON::from)
			.toList();		
	}
	
	public static OCRCompany from(JSONObject json) {
		if (json == null) return null; 
		return new OCRCompany()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setAccount(OCRJSONUtils.getString(json, OCRNames.ACCOUNT))
			.setEnvironment(OCRJSONUtils.getString(json, OCRNames.ENVIRONMENT))
			.setName(OCRJSONUtils.getString(json, OCRNames.NAME))
			.setCountryCode(OCRJSONUtils.getString(json, OCRNames.COUNTRY_CODE))
			.setTaxId(OCRJSONUtils.getString(json, OCRNames.TAX_ID))
			.setAccountingPeriodLength(OCRJSONUtils.getInteger(json, OCRNames.ACCOUNTING_PERIOD_LENGTH))
			.setCreator(OCRJSONUtils.getString(json, OCRNames.CREATOR))
			.setCreation(OCRJSONUtils.getString(json, OCRNames.CREATION))
		;
	}
	
	public static JSONArray to(List<OCRCompany> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRCompany> stream) {
		return stream
			.map(OCRCompanyJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRCompany company) {
		if (company == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.ID, company.getId())
			.putOpt(OCRNames.ACCOUNT, company.getAccount().orElse(null))
			.putOpt(OCRNames.ENVIRONMENT, company.getEnvironment().orElse(null))
			.putOpt(OCRNames.NAME, company.getName().orElse(null))
			.putOpt(OCRNames.COUNTRY_CODE, company.getCountryCode().orElse(null))
			.putOpt(OCRNames.TAX_ID, company.getTaxId().orElse(null))
			.putOpt(OCRNames.ACCOUNTING_PERIOD_LENGTH, company.getAccountingPeriodLength().orElse(null))
			.putOpt(OCRNames.CREATOR, company.getCreator().orElse(null))
			.putOpt(OCRNames.CREATION, company.getCreation().orElse(null))
		;
	}
}
