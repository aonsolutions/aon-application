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
import net.aonsolutions.occam.api.invoicing.InvoiceBreakdown;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceBreakdownJSON {
	
	private InvoiceBreakdownJSON() {
	}
	
	public static List<InvoiceBreakdown> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(InvoiceBreakdownJSON::from)
			.toList();		
	}
	
	public static InvoiceBreakdown from(JSONObject json) {
		if (json == null) return null; 
		return new InvoiceBreakdown()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setTaxType( TaxType.safeValueOf(AonJSONUtils.getString(json, AonNames.TYPE)).orElse(null) )
			.setBase(AonJSONUtils.getDouble(json, AonNames.BASE))
			.setPercent(AonJSONUtils.getDouble(json, AonNames.PERCENT))
			.setQuota(AonJSONUtils.getDouble(json, AonNames.QUOTA))
			.setSurchargePercent(AonJSONUtils.getDouble(json, AonNames.SURCHARGE_PERCENT))
			.setSurchargeQuota(AonJSONUtils.getDouble(json, AonNames.SURCHARGE_QUOTA))
			.setDeductiblePercent(AonJSONUtils.getDouble(json, AonNames.DEDUCTIBLE_PERCENT))
			.setDeductibleQuota(AonJSONUtils.getDouble(json, AonNames.DEDUCTIBLE_QUOTA))
			.setVatDeductionType( VatDeductionType.safeValueOf(AonJSONUtils.getString(json, AonNames.VAT_DEDUCTION_TYPE)).orElse(null) )
			.setWithholdingType( WithholdingType.safeValueOf(AonJSONUtils.getString(json, AonNames.WITHHOLDING_TYPE)).orElse(null) )
		;
	}
	
	public static JSONArray to(List<InvoiceBreakdown> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<InvoiceBreakdown> stream) {
		return stream
			.map(InvoiceBreakdownJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(InvoiceBreakdown tax) {
		if (tax == null) return null;
		return new JSONObject()
			.put(AonNames.ID, tax.getId())
			.putOpt(AonNames.TYPE, AonObjectUtils.ifNotNullDo(tax.getTaxType(), Object::toString ))
			.putOpt(AonNames.BASE, tax.getBase())
			.putOpt(AonNames.PERCENT, tax.getPercent())
			.putOpt(AonNames.QUOTA, tax.getQuota())
			.putOpt(AonNames.SURCHARGE_PERCENT, tax.getSurchargePercent())
			.putOpt(AonNames.SURCHARGE_QUOTA, tax.getSurchargeQuota())
			.putOpt(AonNames.DEDUCTIBLE_PERCENT, tax.getDeductiblePercent())
			.putOpt(AonNames.DEDUCTIBLE_QUOTA, tax.getDeductibleQuota())
			.putOpt(AonNames.VAT_DEDUCTION_TYPE, AonObjectUtils.ifNotNullDo(tax.getVatDeductionType(), Object::toString ))
			.putOpt(AonNames.WITHHOLDING_TYPE, AonObjectUtils.ifNotNullDo(tax.getWithholdingType(), Object::toString ))
			;
	}
}
