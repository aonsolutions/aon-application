package com.esferalia.aon.occam.api.json.invoice;

import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.EnterpriseDataJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class InvoiceCommunicationConfigurationJSON {

	private InvoiceCommunicationConfigurationJSON() {
		
	}
	
	public static Optional<InvoiceCommunicationConfiguration> from(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();  
		return Optional.of( 
			new InvoiceCommunicationConfiguration()
				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setAdministrationHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.ADMINISTRATION_HISTORY)))
				
				.setTbaiData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TBAI_DATA)))
				.setTbaiDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.TBAI_DATA_HISTORY)))
				
				.setLroeData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.LROE_DATA)))
				.setLroeDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.LROE_DATA_HISTORY)))
				.setLroeRegistryDate(JsonUtils.getString(json, IJsonNames.LROE_REGISTRY_DATE))
				
				.setVerifactuData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.VERIFACTU_DATA)))
				.setVerifactuDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.VERIFACTU_DATA_HISTORY)))
				
				.setNoVerifactuData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.NO_VERIFACTU_DATA)))
				.setNoVerifactuDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.NO_VERIFACTU_DATA_HISTORY)))

				.setSiiData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SII_DATA)))
				.setSiiDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.SII_DATA_HISTORY)))
				.setSiiRegistryDate(JsonUtils.getString(json, IJsonNames.SII_REGISTRY_DATE))
				
				.setSifData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SIF_DATA)))
				.setSifDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.SIF_DATA_HISTORY)))
				
				.setNoSifData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.NO_SIF_DATA)))
				.setNoSifDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.NO_SIF_DATA_HISTORY)))
				
				.setDefaultCertificate(JsonUtils.getInteger(json, IJsonNames.DEFAULT_CERTIFICATE))
		);
	}
	
	public static Optional<JSONObject> to(InvoiceCommunicationConfiguration config) {
		if (config == null) return Optional.empty();
		return Optional.of(		
			new JSONObject()
			.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
			.put(IJsonNames.ADMINISTRATION_HISTORY, EnterpriseDataJSON.toJSON(config.getAdministrationHistory()))
			
			.put(IJsonNames.TBAI_DATA, EnterpriseDataJSON.toJSON(config.getTbaiData()))
			.put(IJsonNames.TBAI_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getTbaiDataHistory()))
			
			.put(IJsonNames.LROE_DATA, EnterpriseDataJSON.toJSON(config.getLroeData()))
			.put(IJsonNames.LROE_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getLroeDataHistory()))
			.put(IJsonNames.LROE_REGISTRY_DATE, config.getLroeRegistryDate())
			
			.put(IJsonNames.VERIFACTU_DATA, EnterpriseDataJSON.toJSON(config.getVerifactuData()))
			.put(IJsonNames.VERIFACTU_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getVerifactuDataHistory()))
			
			.put(IJsonNames.NO_VERIFACTU_DATA, EnterpriseDataJSON.toJSON(config.getVerifactuData()))
			.put(IJsonNames.NO_VERIFACTU_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getVerifactuDataHistory()))
			
			.put(IJsonNames.SII_DATA, EnterpriseDataJSON.toJSON(config.getSiiData()))
			.put(IJsonNames.SII_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getSiiDataHistory()))
			.put(IJsonNames.SII_REGISTRY_DATE, config.getSiiRegistryDate())
			
			.put(IJsonNames.SIF_DATA, EnterpriseDataJSON.toJSON(config.getSifData()))
			.put(IJsonNames.SIF_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getSifDataHistory()))
			
			.put(IJsonNames.NO_SIF_DATA, EnterpriseDataJSON.toJSON(config.getNoSifData()))
			.put(IJsonNames.NO_SIF_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getNoSifDataHistory()))
			
			.put(IJsonNames.DEFAULT_CERTIFICATE, config.getDefaultCertificate())		
		);
	}
	
}
