package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class InvoiceMinJSON {
	
	private InvoiceMinJSON() {
	
	}
	
	public static JSONObject toJSON(Invoice inv) {
		return new JSONObject()
			.put(IJsonNames.ID, inv.getId())				
			.put(IJsonNames.ACTIVITY, inv.getActivity()==null?null:inv.getActivity().getId())
			.putOpt(IJsonNames.ACTIVITY_DESCRIPTION, inv.getActivity()==null?null:inv.getActivity().getDescription())
			.putOpt(IJsonNames.EPIGRAPH, inv.getActivity()==null?null:inv.getActivity().getEpigraph())
			.put(IJsonNames.INVOICE_TYPE, inv.getType()==null?null:inv.getType().ordinal() )
			.put(IJsonNames.DOCUMENT_NUMBER, inv.getDocumentNumber() )
			.put(IJsonNames.REFERENCE_CODE, inv.getReferenceCode() )
			.put(IJsonNames.REGISTRY_DOCUMENT, inv.getRegistryDocument() )
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, inv.getRegistryDocumentType()==null?null:inv.getRegistryDocumentType().ordinal() )
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, inv.getRegistryDocumentCountry()==null?null:inv.getRegistryDocumentCountry().getIso2() )
			.put(IJsonNames.REGISTRY_ID, inv.getRegistry() )
			.put(IJsonNames.REGISTRY_NAME, inv.getRegistryName() )
			.put(IJsonNames.ISSUE_DATE, inv.getIssueDate() == null? null : AonNumberUtils.toString(inv.getIssueDate().getTime()) )
			.put(IJsonNames.TAX_DATE, inv.getTaxDate() == null? null : AonNumberUtils.toString(inv.getTaxDate().getTime()) )
			.put(IJsonNames.TOTAL, inv.getTotal())
		;
	}
	
	
}
