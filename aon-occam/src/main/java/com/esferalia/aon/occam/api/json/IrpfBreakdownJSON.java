package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class IrpfBreakdownJSON {
	
	private IrpfBreakdownJSON() {
		
	}
	public static List<IrpfBreakdown> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<IrpfBreakdown> fromJSON(JSONArray json) {
		LinkedList<IrpfBreakdown> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static IrpfBreakdown fromJSON(JSONObject json) {
		return new IrpfBreakdown()
			.setActivity(JsonUtils.optInteger(json, IJsonNames.ACTIVITY))
			.setActivityDescription(JsonUtils.getString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setEpigraph( JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setRegistryDocument(JsonUtils.optString(json, IJsonNames.REGISTRY_DOCUMENT))
			.setRegistryDocumentType(DocumentType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.REGISTRY_DOCUMENT_TYPE)))
			.setRegistryDocumentCountry(Country.safeValueOf(JsonUtils.optString(json, IJsonNames.REGISTRY_DOCUMENT_COUNTRY)))
			.setName(JsonUtils.optString(json, IJsonNames.REGISTRY_NAME))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setFromSalary( json.optBoolean(IJsonNames.FROM_SALARY))
			.setInsidePeriod( json.optBoolean(IJsonNames.INSIDE_PERIOD))
			.setInvoiceType(InvoiceType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.INVOICE_TYPE)))
			.setInvoice(JsonUtils.getInteger(json, IJsonNames.INVOICE))
			.setSeries(JsonUtils.optString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInteger(json, IJsonNames.NUMBER))
			.setReferenceCode(JsonUtils.optString(json, IJsonNames.REFERENCE_CODE))
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE))
			.setChargeDate(JsonUtils.getDate(json, IJsonNames.CHARGE_DATE))
			.setWithholdingType( WithholdingType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.WITHHOLDING_TYPE)))
			.setIRPFRegime(IRPFRegime.safeValueOf(JsonUtils.optInteger(json,IJsonNames.IRPF_REGIME)))
			.setInKind( json.optBoolean(IJsonNames.IN_KIND))
			.setBase( json.optDouble(IJsonNames.BASE))
			.setPercent( json.optDouble(IJsonNames.PERCENT))
			.setQuota( json.optDouble(IJsonNames.QUOTA))
			.setDeductiblePercent( json.optDouble(IJsonNames.DEDUCTIBLE_PERCENT))
			.setParticipationPercent( json.optDouble(IJsonNames.PARTICIPATION_PERCENT))
			.setParticipationQuota( json.optDouble(IJsonNames.PARTICIPATION_QUOTA))
			.setDeductibleQuota( json.optDouble(IJsonNames.DEDUCTIBLE_QUOTA))
			.setGroupedBy(JsonUtils.getInteger(json, IJsonNames.GROUPED_BY))
			.setZip(JsonUtils.getString(json, IJsonNames.ZIP))
			.setCity(JsonUtils.getString(json, IJsonNames.CITY))
			;
	}
	
	public static JSONArray toJSON(List<IrpfBreakdown> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(Stream<IrpfBreakdown> vatContexts) {
		JSONArray array = new JSONArray();
		vatContexts.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(IrpfBreakdown irpf) {
		return new JSONObject()
				.putOpt(IJsonNames.ACTIVITY, irpf.getActivity())
				.putOpt(IJsonNames.ACTIVITY_DESCRIPTION, irpf.getActivityDescription())
				.putOpt(IJsonNames.EPIGRAPH, irpf.getEpigraph())
				.putOpt(IJsonNames.REGISTRY_DOCUMENT, irpf.getRegistryDocument() )
				.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, irpf.getRegistryDocumentType()==null?null:irpf.getRegistryDocumentType().ordinal() )
				.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, irpf.getRegistryDocumentCountry()==null?null:irpf.getRegistryDocumentCountry().getIso2() )
				.putOpt(IJsonNames.REGISTRY_NAME, irpf.getName() )
				.put(IJsonNames.ISSUE_DATE, irpf.getIssueDate() ==null?null:AonNumberUtils.toString(irpf.getIssueDate().getTime()))
				.put(IJsonNames.FROM_SALARY, irpf.isFromSalary() )
				.put(IJsonNames.INSIDE_PERIOD, irpf.isInsidePeriod() )
				.put(IJsonNames.INVOICE_TYPE, irpf.getInvoiceType()==null?null:irpf.getInvoiceType().ordinal() )
				.put(IJsonNames.INVOICE, irpf.getInvoice())				
				.putOpt(IJsonNames.SERIES, irpf.getSeries())				
				.putOpt(IJsonNames.NUMBER, irpf.getNumber())				
				.putOpt(IJsonNames.REFERENCE_CODE, irpf.getReferenceCode() )
				.put(IJsonNames.TAX_DATE, irpf.getTaxDate()==null?null:AonNumberUtils.toString(irpf.getTaxDate().getTime()))
				.put(IJsonNames.CHARGE_DATE, irpf.getChargeDate()==null?null:AonNumberUtils.toString(irpf.getChargeDate().getTime()))
				.put(IJsonNames.WITHHOLDING_TYPE, irpf.getWithholdingType()==null?null:irpf.getWithholdingType().ordinal() )
				.put(IJsonNames.IRPF_REGIME, irpf.getIRPFRegime()==null?null:irpf.getIRPFRegime().ordinal() )
				.putOpt(IJsonNames.IN_KIND, irpf.isInKind() )
				.putOpt(IJsonNames.BASE, irpf.getBase() )
				.putOpt(IJsonNames.PERCENT, irpf.getPercent() )
				.putOpt(IJsonNames.QUOTA, irpf.getQuota() )
				.putOpt(IJsonNames.PARTICIPATION_PERCENT, irpf.getParticipationPercent() )
				.putOpt(IJsonNames.PARTICIPATION_QUOTA, irpf.getParticipationQuota() )
				.putOpt(IJsonNames.DEDUCTIBLE_PERCENT, irpf.getDeductiblePercent() )
				.putOpt(IJsonNames.DEDUCTIBLE_QUOTA, irpf.getDeductibleQuota() )
				.putOpt(IJsonNames.GROUPED_BY, irpf.getGroupedBy() )
				.putOpt(IJsonNames.ZIP, irpf.getZip() )
				.putOpt(IJsonNames.CITY, irpf.getCity() )
			;
	}

}
