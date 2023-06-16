package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.invoicing.InvoiceTax;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceTaxJSON {
	
	private InvoiceTaxJSON() {
	}
	
	public static List<InvoiceTax> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(InvoiceTaxJSON::from)
			.toList();		
	}
	
	public static InvoiceTax from(JSONObject json) {
		if (json == null) return null; 
		return new InvoiceTax()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setTaxType( TaxType.safeValueOf(AonJSONUtils.getString(json, AonNames.TAX_TYPE)).orElse(null) )
			.setPercent(AonJSONUtils.getDouble(json, AonNames.PERCENT))
			.setSurchargePercent(AonJSONUtils.getDouble(json, AonNames.SURCHARGE_PERCENT))
			.setDeductiblePercent(AonJSONUtils.getDouble(json, AonNames.DEDUCTIBLE_PERCENT))
			.setVatDeductionType( VatDeductionType.safeValueOf(AonJSONUtils.getString(json, AonNames.VAT_DEDUCTION_TYPE)).orElse(null) )
			.setWithholdingType( WithholdingType.safeValueOf(AonJSONUtils.getString(json, AonNames.WITHHOLDING_TYPE)).orElse(null) )
		;
	}
	
	public static JSONArray to(List<InvoiceTax> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<InvoiceTax> stream) {
		return stream
			.map(InvoiceTaxJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(InvoiceTax tax) {
		if (tax == null) return null;
		return new JSONObject()
			.put(AonNames.ID, tax.getId())
			.putOpt(AonNames.TAX_TYPE, AonObjectUtils.ifNotNullGet(tax.getTaxType(), Object::toString ))
			.putOpt(AonNames.PERCENT, tax.getPercent())
			.putOpt(AonNames.SURCHARGE_PERCENT, tax.getSurchargePercent())
			.putOpt(AonNames.DEDUCTIBLE_PERCENT, tax.getDeductiblePercent())
			.putOpt(AonNames.VAT_DEDUCTION_TYPE, AonObjectUtils.ifNotNullGet(tax.getVatDeductionType(), Object::toString ))
			.putOpt(AonNames.WITHHOLDING_TYPE, AonObjectUtils.ifNotNullGet(tax.getWithholdingType(), Object::toString ))
			;
	}
}
