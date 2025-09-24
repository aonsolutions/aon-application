package com.esferalia.aon.occam.api.json.invoice;

import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceCommunicationConfigurationJSON {

	private InvoiceCommunicationConfigurationJSON() {
	}
	
	public static Optional<InvoiceCommunicationConfiguration> fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();  
		return Optional.of( 
			new InvoiceCommunicationConfiguration()
				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setTbai(JsonUtils.getboolean(json, IJsonNames.TBAI))
				.setSii(JsonUtils.getboolean(json, IJsonNames.SII))
				.setVerifactu(JsonUtils.getboolean(json, IJsonNames.VERIFACTU))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST))
				
				.setAutosend(JsonUtils.getboolean(json, IJsonNames.AUTOSEND))
				.setIncludeDate(JsonUtils.getDate(json, IJsonNames.INCLUDE_DATE))
				.setRegistryDate(JsonUtils.getString(json, IJsonNames.REGISTRY_DATE))
		);
	}
	
	public static Optional<JSONObject> toJSON(InvoiceCommunicationConfiguration config) {
		if (config == null) return Optional.empty();
		return Optional.of(		
			new JSONObject()
				.put(IJsonNames.TBAI, config.isTbai())
				.put(IJsonNames.VERIFACTU, config.isVerifactu())
				.put(IJsonNames.SII, config.isSii())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.TEST, config.isTest())
				.put(IJsonNames.AUTOSEND, config.isAutosend())
				.put(IJsonNames.INCLUDE_DATE, AonDateUtils.format(config.getIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.REGISTRY_DATE, config.getRegistryDate())
			);
	}
	
}
