package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

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
			.setActivityDescription(JsonUtils.optString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setEpigraph(JsonUtils.optString(json, IJsonNames.EPIGRAPH))
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
			.setWithholdingType( WithholdingType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.WITHHOLDING_TYPE)))
			.setIRPFRegime(IRPFRegime.safeValueOf(JsonUtils.optInteger(json,IJsonNames.IRPF_REGIME)))
			.setInKind( json.optBoolean(IJsonNames.IN_KIND))
			.setBase( json.optDouble(IJsonNames.BASE))
			.setPercent( json.optDouble(IJsonNames.PERCENT))
			.setQuota( json.optDouble(IJsonNames.QUOTA))
			.setDeductiblePercent( json.optDouble(IJsonNames.DEDUCTIBLE_PERCENT))
			.setDeductibleQuota( json.optDouble(IJsonNames.DEDUCTIBLE_QUOTA))
			.setGroupByNif(JsonUtils.getInteger(json, IJsonNames.GROUP_BY_NIF))
			.setZip(JsonUtils.optString(json, IJsonNames.ZIP))
			.setCity(JsonUtils.optString(json, IJsonNames.CITY))
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
				.put(IJsonNames.ACTIVITY, irpf.getActivity())
				.put(IJsonNames.ACTIVITY_DESCRIPTION, irpf.getActivityDescription())
				.put(IJsonNames.EPIGRAPH, irpf.getEpigraph() )
				.put(IJsonNames.REGISTRY_DOCUMENT, irpf.getRegistryDocument() )
				.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, irpf.getRegistryDocumentType()==null?null:irpf.getRegistryDocumentType().ordinal() )
				.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, irpf.getRegistryDocumentCountry()==null?null:irpf.getRegistryDocumentCountry().getIso2() )
				.put(IJsonNames.REGISTRY_NAME, irpf.getName() )
				.put(IJsonNames.ISSUE_DATE, AonNumberUtils.toString(irpf.getIssueDate().getTime()))
				.put(IJsonNames.FROM_SALARY, irpf.isFromSalary() )
				.put(IJsonNames.INSIDE_PERIOD, irpf.isInsidePeriod() )
				.put(IJsonNames.INVOICE_TYPE, irpf.getInvoiceType()==null?null:irpf.getInvoiceType().ordinal() )
				.put(IJsonNames.INVOICE, irpf.getInvoice())				
				.put(IJsonNames.SERIES, irpf.getSeries())				
				.put(IJsonNames.NUMBER, irpf.getNumber())				
				.put(IJsonNames.REFERENCE_CODE, irpf.getReferenceCode() )
				.put(IJsonNames.TAX_DATE, AonNumberUtils.toString(irpf.getTaxDate().getTime()))
				.put(IJsonNames.WITHHOLDING_TYPE, irpf.getWithholdingType()==null?null:irpf.getWithholdingType().ordinal() )
				.put(IJsonNames.IRPF_REGIME, irpf.getIRPFRegime()==null?null:irpf.getIRPFRegime().ordinal() )
				.put(IJsonNames.IN_KIND, irpf.isInKind() )
				.put(IJsonNames.BASE, irpf.getBase() )
				.put(IJsonNames.PERCENT, irpf.getPercent() )
				.put(IJsonNames.QUOTA, irpf.getQuota() )
				.put(IJsonNames.DEDUCTIBLE_PERCENT, irpf.getDeductiblePercent() )
				.put(IJsonNames.DEDUCTIBLE_QUOTA, irpf.getDeductibleQuota() )
				.put(IJsonNames.GROUP_BY_NIF, irpf.getGroupByNif() )
				.put(IJsonNames.ZIP, irpf.getZip() )
				.put(IJsonNames.CITY, irpf.getCity() )
			;
	}

}
