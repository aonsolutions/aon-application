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
				.setTbaiIncludeDate(JsonUtils.getDate(json, IJsonNames.TBAI_INCLUDE_DATE))
				.setTbaiRegistryDate(JsonUtils.getString(json, IJsonNames.TBAI_REGISTRY_DATE))
				.setVerifactu(JsonUtils.getboolean(json, IJsonNames.VERIFACTU))
				.setVerifactuTest(JsonUtils.getboolean(json, IJsonNames.VERIFACTU_TEST))
				.setVerifactuIncludeDate(JsonUtils.getDate(json, IJsonNames.VERIFACTU_INCLUDE_DATE))
				.setVerifactuRegistryDate(JsonUtils.getString(json, IJsonNames.VERIFACTU_REGISTRY_DATE))
				.setSii(JsonUtils.getboolean(json, IJsonNames.SII))
				.setSiiTest(JsonUtils.getboolean(json, IJsonNames.SII_TEST))
				.setSiiAutosend(JsonUtils.getboolean(json, IJsonNames.SII_AUTOSEND))
				.setSiiIncludeDate(JsonUtils.getDate(json, IJsonNames.SII_INCLUDE_DATE))
				.setSiiRegistryDate(JsonUtils.getString(json, IJsonNames.SII_REGISTRY_DATE))
		);
	}
	
	public static Optional<JSONObject> to(InvoiceCommunicationConfiguration config) {
		if (config == null) return Optional.empty();
		return Optional.of(		
			new JSONObject()
				.put(IJsonNames.TBAI, config.isTbai())
				.put(IJsonNames.TBAI_TEST, config.isTbaiTest())
				.put(IJsonNames.TBAI_INCLUDE_DATE, AonDateUtils.format(config.getTbaiIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.TBAI_REGISTRY_DATE, config.getTbaiRegistryDate())
				.put(IJsonNames.VERIFACTU, config.isVerifactu())
				.put(IJsonNames.VERIFACTU_TEST, config.isVerifactuTest())
				.put(IJsonNames.VERIFACTU_INCLUDE_DATE, AonDateUtils.format(config.getVerifactuIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.VERIFACTU_REGISTRY_DATE, config.getVerifactuRegistryDate())
				.put(IJsonNames.SII, config.isSii())
				.put(IJsonNames.SII_TEST, config.isSiiTest())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.SII_AUTOSEND, config.isSiiAutosend())
				.put(IJsonNames.SII_INCLUDE_DATE, AonDateUtils.format(config.getSiiIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.SII_REGISTRY_DATE, config.getSiiRegistryDate())
			);
	}
	
}
