package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class RegistryJSON {

	public static Registry fromJSON(JSONObject json) {
		if(json == null) {
			return new Registry();
		}
		
		return new Registry() 
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
			.setDocument(json.optString(IJsonNames.DOCUMENT))
			.setDocumentCountry(Country.safeValueOf(json.optString(IJsonNames.DOCUMENT_COUNTRY)))
			.setDocumentType(DocumentType.safeValueOf(json.optString(IJsonNames.DOCUMENT_TYPE)))
			.setName(json.optString(IJsonNames.NAME))
			.setAlias(json.optString(IJsonNames.ALIAS))
			.setLegalPerson(json.optBoolean(IJsonNames.LEGAL_PERSON))
			.setNationality(Country.safeValueOf(json.optString(IJsonNames.NATIONALITY)))
			.setConfidential(json.optBoolean(IJsonNames.CONFIDENTIAL))
			.setGlobal(json.optBoolean(IJsonNames.GLOBAL))
			.setDirty(json.optBoolean(IJsonNames.DIRTY));
	}
	
	public static JSONObject toJSON(Registry registry) {
		if(registry == null || registry.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, registry.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(registry.getDomain()))
			.put(IJsonNames.DOCUMENT, registry.getDocument())
			.put(IJsonNames.DOCUMENT_COUNTRY, registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2(): null)
			.put(IJsonNames.DOCUMENT_TYPE, registry.getDocumentType() != null ? registry.getDocumentType().name(): null)
			.put(IJsonNames.NAME, registry.getName())
			.put(IJsonNames.ALIAS, registry.getAlias())
			.put(IJsonNames.LEGAL_PERSON, registry.isLegalPerson())
			.put(IJsonNames.NATIONALITY, registry.getNationality() != null ? registry.getNationality().getIso2() : null)
			.put(IJsonNames.CONFIDENTIAL, registry.isConfidential());
	}

	public static JSONObject toJSON(RegistryFull<?> rf) {
		return toJSON( rf.getRegistry())
			.put(IJsonNames.BANKS, RegistryBankJSON.toJSON(rf.getBanks()) )
			.put(IJsonNames.ADDRESSES, RegistryAddressJSON.toJSON(rf.getAddresses()) )
			.put(IJsonNames.MEDIAS, RegistryMediaJSON.toJSON(rf.getMedias()) )
			.put(IJsonNames.RECORD_DATAS, RecordDataJSON.toJSON(rf.getRecordDatas()) )
		;
	}
}
