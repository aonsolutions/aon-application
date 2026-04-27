package com.esferalia.aon.occam.api.json;

import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.DomainInvoiceStat;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class DomainInvoiceStatJSON {
	
	private DomainInvoiceStatJSON() {
	}
	
	public static Optional<DomainInvoiceStat> fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json) ) return Optional.empty();
		return Optional.of(new DomainInvoiceStat()
			.setId(JsonUtils.getInteger(json,IJsonNames.ID))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setCompanyDocument(JsonUtils.getString(json, IJsonNames.COMPANY_DOCUMENT))
			.setCompanyName(JsonUtils.getString(json, IJsonNames.COMPANY_NAME))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setExpired(JsonUtils.getboolean(json, IJsonNames.EXPIRED))
			.setNational(JsonUtils.getInt(json, IJsonNames.NATIONAL))
			.setIntracommunity(JsonUtils.getInt(json, IJsonNames.INTRACOMMUNITY))
			.setExtracommunity(JsonUtils.getInt(json, IJsonNames.EXTRACOMMUNITY))
			.setCanCeuMel(JsonUtils.getInt(json, IJsonNames.CAN_CEU_MEL))
			.setOtherISP(JsonUtils.getInt(json, IJsonNames.OTHER_ISP))
			.setWithholding(JsonUtils.getInt(json, IJsonNames.WITHHOLDING))
			.setUndeclaredIssued(JsonUtils.getInt(json, IJsonNames.UNDECLARED_ISSUED))
			.setUndeclaredReceived(JsonUtils.getInt(json, IJsonNames.UNDECLARED_RECEIVED))
			.setUndeclaredSimplified(JsonUtils.getInt(json, IJsonNames.UNDECLARED_SIMPLIFIED))
			.setProformas(JsonUtils.getInt(json, IJsonNames.PROFORMAS))
			.setUnrecordedIssued(JsonUtils.getInt(json, IJsonNames.UNRECORDED_ISSUED))
			.setUnrecordedReceived(JsonUtils.getInt(json, IJsonNames.UNRECORDED_RECEIVED))
			.setUnrecordedSimplified(JsonUtils.getInt(json, IJsonNames.UNRECORDED_SIMPLIFIED))
			.setDraft(JsonUtils.getInt(json, IJsonNames.DRAFT))
			.setInProcess(JsonUtils.getInt(json, IJsonNames.IN_PROCESS))
			.setReview(JsonUtils.getInt(json, IJsonNames.REVIEW))
			.setTrash(JsonUtils.getInt(json, IJsonNames.TRASH))
		);
	}
	
	public static Optional<JSONObject> toJSON(DomainInvoiceStat dis) {
		if (dis == null) return Optional.empty();
		return Optional.of(new JSONObject()
			.putOpt(IJsonNames.ID, dis.getId())
			.putOpt(IJsonNames.NAME, dis.getName())
			.putOpt(IJsonNames.DESCRIPTION, dis.getDescription())
			.putOpt(IJsonNames.COMPANY_DOCUMENT, dis.getCompanyDocument())
			.putOpt(IJsonNames.COMPANY_NAME, dis.getCompanyName())
			.put(IJsonNames.ACTIVE, dis.isActive())
			.put(IJsonNames.EXPIRED, dis.isExpired())
			.put(IJsonNames.NATIONAL, dis.getNational())
			.put(IJsonNames.INTRACOMMUNITY, dis.getIntracommunity())
			.put(IJsonNames.EXTRACOMMUNITY, dis.getExtracommunity())
			.put(IJsonNames.CAN_CEU_MEL, dis.getCanCeuMel())
			.put(IJsonNames.OTHER_ISP, dis.getOtherISP())
			.put(IJsonNames.WITHHOLDING, dis.getWithholding())
			.put(IJsonNames.UNDECLARED_ISSUED, dis.getUndeclaredIssued())
			.put(IJsonNames.UNDECLARED_RECEIVED, dis.getUndeclaredReceived())
			.put(IJsonNames.UNDECLARED_SIMPLIFIED, dis.getUndeclaredSimplified())
			.put(IJsonNames.PROFORMAS, dis.getProformas())
			.put(IJsonNames.UNRECORDED_ISSUED, dis.getUnrecordedIssued())
			.put(IJsonNames.UNRECORDED_RECEIVED, dis.getUnrecordedReceived())
			.put(IJsonNames.UNRECORDED_SIMPLIFIED, dis.getUnrecordedSimplified())
			.put(IJsonNames.DRAFT, dis.getDraft())
			.put(IJsonNames.IN_PROCESS, dis.getInProcess())
			.put(IJsonNames.REVIEW, dis.getReview())
			.put(IJsonNames.TRASH, dis.getTrash())
		);		
	}

}
