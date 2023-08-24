package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.ICErrorImpl;

public class IC_ERROR {

	private IC_ERROR() {
		throw new IllegalStateException("Utility class");
	}
	
	private static IICError getICError() {
		return new ICErrorImpl();
	}
	
    
	public static void assignInvestAsset2Invoice(String domainName, Integer domainId, String login, Integer investAssetId, Invoice invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getICError().assignInvestAsset2Invoice(ctx, investAssetId, invoice);
		}
	}
	
	public static void addDocumentInvoice(String domainName, Integer domainId, String login, Invoice invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getICError().addDocumentInvoice(ctx, invoice);
		}
	}
	

}