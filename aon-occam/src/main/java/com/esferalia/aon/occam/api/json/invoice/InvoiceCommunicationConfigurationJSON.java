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
	
	public static Optional<InvoiceCommunicationConfiguration> from(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();  
		return Optional.of( 
			new InvoiceCommunicationConfiguration()
				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setTbai(JsonUtils.getboolean(json, IJsonNames.TBAI))
				.setTbaiTest(JsonUtils.getboolean(json, IJsonNames.TBAI_TEST))
				.setVerifactu(JsonUtils.getboolean(json, IJsonNames.VERIFACTU))
				.setVerifactuTest(JsonUtils.getboolean(json, IJsonNames.VERIFACTU_TEST))
				.setSii(JsonUtils.getboolean(json, IJsonNames.SII))
				.setSiiTest(JsonUtils.getboolean(json, IJsonNames.SII_TEST))
				.setAutosend(JsonUtils.getboolean(json, IJsonNames.AUTOSEND))
				.setIncludeDate(JsonUtils.getDate(json, IJsonNames.INCLUDE_DATE))
				.setRegistryDate(JsonUtils.getString(json, IJsonNames.REGISTRY_DATE))
		);
	}
	
	public static Optional<JSONObject> to(InvoiceCommunicationConfiguration config) {
		if (config == null) return Optional.empty();
		return Optional.of(		
			new JSONObject()
				.put(IJsonNames.TBAI, config.isTbai())
				.put(IJsonNames.TBAI_TEST, config.isTbaiTest())
				.put(IJsonNames.VERIFACTU, config.isVerifactu())
				.put(IJsonNames.VERIFACTU_TEST, config.isVerifactuTest())
				.put(IJsonNames.SII, config.isSii())
				.put(IJsonNames.SII_TEST, config.isSiiTest())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.AUTOSEND, config.isAutosend())
				.put(IJsonNames.INCLUDE_DATE, AonDateUtils.format(config.getIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.REGISTRY_DATE, config.getRegistryDate())
			);
	}
	
}
