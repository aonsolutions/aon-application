package com.esferalia.aon.occam.api.json;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class DomainInvoiceStatParamsJSON {
	
	private DomainInvoiceStatParamsJSON() {
		
	}

	public static Optional<DomainInvoiceStatParams> from(JSONObject json) {
		return from(json, DomainInvoiceStatParams::new );
	}
	public static Optional<DomainInvoiceStatParams> from(JSONObject json, Supplier<DomainInvoiceStatParams> supp) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			supp.get()
				.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
				.setFromDate(JsonUtils.getDate(json, IJsonNames.FROM_DATE))
				.setToDate(JsonUtils.getDate(json, IJsonNames.TO_DATE))
				.setActive(JsonUtils.getInteger(json, IJsonNames.ACTIVE))
				.setQuery(JsonUtils.getString(json, IJsonNames.QUERY))
				.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
				.setFiscalModelType( FiscalModelType.safeValueByName( JsonUtils.getString(json, IJsonNames.FISCAL_MODEL_TYPE)))
				.setInvoices(JsonUtils.getBoolean(json, IJsonNames.INVOICES))
				.setAlcatraz(JsonUtils.getBoolean(json, IJsonNames.ALCATRAZ))
				.setRawdoc(JsonUtils.getBoolean(json, IJsonNames.RAWDOC))
				.setLimit(JsonUtils.getInteger(json, IJsonNames.LIMIT))
				.setOffset(JsonUtils.getInteger(json, IJsonNames.OFFSET))
		);
	}
	
	public static Optional<JSONObject> to(Optional<DomainInvoiceStatParams> account) {
		return account.flatMap( a -> to( a) );
	}
	public static Optional<JSONObject> to(DomainInvoiceStatParams account) {
		if (account == null) return Optional.empty();
		return Optional.of(
			new JSONObject()
				.put(IJsonNames.DOMAIN, account.getDomain())
				.put(IJsonNames.FROM_DATE, account.getFromDate())
				.put(IJsonNames.TO_DATE, account.getToDate())
				.put(IJsonNames.ACTIVE, account.getActive())
				.put(IJsonNames.QUERY, account.getQuery())
				.put(IJsonNames.SCOPE, account.getScope())
				.put(IJsonNames.FISCAL_MODEL_TYPE, account.getFiscalModelType())
				.put(IJsonNames.INVOICES, account.getInvoices())
				.put(IJsonNames.ALCATRAZ, account.getAlcatraz())
				.put(IJsonNames.RAWDOC, account.getRawdoc())
				.put(IJsonNames.LIMIT, account.getLimit())
				.put(IJsonNames.OFFSET, account.getOffset())
		);
	}

}


	
	
	
