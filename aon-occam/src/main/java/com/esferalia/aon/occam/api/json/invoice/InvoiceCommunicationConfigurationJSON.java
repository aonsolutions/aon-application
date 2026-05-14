package com.esferalia.aon.occam.api.json.invoice;

import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.EnterpriseDataJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

public class InvoiceCommunicationConfigurationJSON {

	private InvoiceCommunicationConfigurationJSON() {
		
	}
	
	public static Optional<InvoiceCommunicationConfiguration> from(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();  
		return Optional.of( 
			new InvoiceCommunicationConfiguration()
//				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
//				.setAdministrationHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.ADMINISTRATION_HISTORY)))
				
//				.setTbaiData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TBAI_DATA)))
//				.setTbaiDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.TBAI_DATA_HISTORY)))
//				.setTbaiInvoice(JsonUtils.getboolean(json, IJsonNames.TBAI_INVOICE))
				
//				.setLroeData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.LROE_DATA)))
//				.setLroeDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.LROE_DATA_HISTORY)))
				.setLroeRegistryDate(JsonUtils.getString(json, IJsonNames.LROE_REGISTRY_DATE))
//				.setLroeInvoice(JsonUtils.getboolean(json, IJsonNames.LROE_INVOICE))
				
//				.setVerifactuData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.VERIFACTU_DATA)))
//				.setVerifactuDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.VERIFACTU_DATA_HISTORY)))
//				.setVerifactuInvoice(JsonUtils.getboolean(json, IJsonNames.VERIFACTU_INVOICE))
				
//				.setNoVerifactuData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.NO_VERIFACTU_DATA)))
//				.setNoVerifactuDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.NO_VERIFACTU_DATA_HISTORY)))
//				.setNoVerifactuInvoice(JsonUtils.getboolean(json, IJsonNames.NO_VERIFACTU_INVOICE))

//				.setSiiData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SII_DATA)))
//				.setSiiDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.SII_DATA_HISTORY)))
				.setSiiRegistryDate(JsonUtils.getString(json, IJsonNames.SII_REGISTRY_DATE))
//				.setSiiInvoice(JsonUtils.getboolean(json, IJsonNames.SII_INVOICE))
				
//				.setSifData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SIF_DATA)))
//				.setSifDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.SIF_DATA_HISTORY)))
//				.setSifInvoice(JsonUtils.getboolean(json, IJsonNames.SIF_INVOICE))
				
//				.setNoSifData(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.NO_SIF_DATA)))
//				.setNoSifDataHistory(EnterpriseDataJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.NO_SIF_DATA_HISTORY)))
				
				.setDefaultCertificate(JsonUtils.getInteger(json, IJsonNames.DEFAULT_CERTIFICATE))
		);
	}
	
	public static Optional<JSONObject> to(InvoiceCommunicationConfiguration config) {
		if (config == null) return Optional.empty();
		return Optional.of(		
			new JSONObject()
			.put(IJsonNames.DATA, EnterpriseDataJSON.toJSON(config.dataStream()))
			
//			.put(IJsonNames.ADMINISTRATION, config.getAdministration().map( Enum::name ).orElse(null))
//			.put(IJsonNames.ADMINISTRATION_HISTORY, EnterpriseDataJSON.toJSON(config.getAdministrationStream()))
//
//			.put(IJsonNames.TBAI_DATA, EnterpriseDataJSON.toJSON(config.getTbaiData().orElse(null)))
//			.put(IJsonNames.TBAI_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getTbaiStream()))
//			.put(IJsonNames.TBAI_INVOICE, false )
//			
//			.put(IJsonNames.LROE_DATA, EnterpriseDataJSON.toJSON(config.getLroeData().orElse(null)))
//			.put(IJsonNames.LROE_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getLroeStream()))
			.put(IJsonNames.LROE_REGISTRY_DATE, config.getLroeRegistryDate())
//			.put(IJsonNames.LROE_INVOICE, false )
//			
//			.put(IJsonNames.VERIFACTU_DATA, EnterpriseDataJSON.toJSON(config.getVerifactuData().orElse(null)))
//			.put(IJsonNames.VERIFACTU_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getVerifactuStream()))
//			.put(IJsonNames.VERIFACTU_INVOICE, false )
//			
//			.put(IJsonNames.NO_VERIFACTU_DATA, EnterpriseDataJSON.toJSON(config.getNoVerifactuData().orElse(null)))
//			.put(IJsonNames.NO_VERIFACTU_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getNoVerifactuStream()))
//			.put(IJsonNames.NO_VERIFACTU_INVOICE,false )
//			
//			.put(IJsonNames.SII_DATA, EnterpriseDataJSON.toJSON(config.getSiiData().orElse(null)))
//			.put(IJsonNames.SII_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getSiiStream()))
			.put(IJsonNames.SII_REGISTRY_DATE, config.getSiiRegistryDate())
//			.put(IJsonNames.SII_INVOICE, false)
//			
//			.put(IJsonNames.SIF_DATA, EnterpriseDataJSON.toJSON(config.getSifData().orElse(null)))
//			.put(IJsonNames.SIF_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getSifStream()))
//			.put(IJsonNames.SIF_INVOICE, false)
//			
//			.put(IJsonNames.NO_SIF_DATA, EnterpriseDataJSON.toJSON(config.getNoSifData().orElse(null)))
//			.put(IJsonNames.NO_SIF_DATA_HISTORY, EnterpriseDataJSON.toJSON(config.getNoSifStream()))
			
			.put(IJsonNames.DEFAULT_CERTIFICATE, config.getDefaultCertificate())
			
//			.put(IJsonNames.TBAI_INVOICE, config.hasTbaiInvoice())
//			.put(IJsonNames.LROE_INVOICE,  config.hasLroeInvoice())
//			.put(IJsonNames.VERIFACTU_INVOICE, config.hasVerifactuInvoice())
//			.put(IJsonNames.NO_VERIFACTU_INVOICE, config.hasNoVerifactuInvoice())
//			.put(IJsonNames.SII_INVOICE, config.hasSiiInvoice())
//			.put(IJsonNames.SIF_INVOICE, config.hasSifInvoice())
		);
	}
	
}
