package com.esferalia.aon.occam.api.json;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.invoice.InvofoxConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceCommunicationConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.PrintInvoiceConfigurationJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.ApiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class ApiConfigurationJSON {
	
	private ApiConfigurationJSON() {
		
	}
	
	// ******************************************************
	// ******************************************* [FROM] ***
	// ******************************************************
	
	public static Optional<Account> from(JSONObject json) {
		return from(json, Account::new );
	}
	public static Optional<Account> from(JSONObject json, Supplier<Account> account) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			account.get()
				.setId(JsonUtils.getInteger( json, IJsonNames.ID ))
				.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
				.setCode(JsonUtils.getString(json,IJsonNames.CODE))
				.setDescription(JsonUtils.getString(json,IJsonNames.DESCRIPTION))
				.setAlias(JsonUtils.getString(json,IJsonNames.ALIAS))
				.setEntryEnabled(JsonUtils.getboolean(json, IJsonNames.ENTRY_ENABLED))
				.setLevel(JsonUtils.getbyte(json,IJsonNames.LEVEL))
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
				.setCostCenter(JsonUtils.getString(json,IJsonNames.COST_CENTER))
		);
	}
	
	// ****************************************************
	// ******************************************* [TO] ***
	// ****************************************************
	public static JSONObject to(ApiConfiguration conf) {
		if (conf == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.COMPANY, CompanyJSON.toJSON(conf.getCompany()))
			.put(IJsonNames.ADMINISTRATION, Administration.name( conf.getAdministration()).orElse(Administration.UNKNOWN.name()))
			.put(IJsonNames.DEFAULT_RETENTION_TAX, conf.getDefaultRetention())
			.put(IJsonNames.DEFAULT_VAT_TAX, conf.getDefaultVat())
			.put(IJsonNames.DEFAULT_SERIES, conf.getDefaultSeries())
			.put(IJsonNames.TAXES, TaxJSON.toJSON(conf.getTaxes()))
			.put(IJsonNames.SERIES, SeriesJSON.to(conf.getSeries()))
			.put(IJsonNames.WORKPLACES, WorkplaceJSON.toJSON(conf.getWorkplaces()))
			.put(IJsonNames.PRINT, PrintInvoiceConfigurationJSON.to(conf.getPrintConfiguration()).orElse(null))
			.put(IJsonNames.COMMUNICATION, InvoiceCommunicationConfigurationJSON.to(conf.getCommunicationConfiguration()).orElse(null))
			.put(IJsonNames.INVOFOX, InvofoxConfigurationJSON.to(conf.getInvofoxConfiguration()).orElse(null))		
		;
	}

}


	
	
	
