package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public interface IICError {

	public void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice);
	public void addDocumentInvoice(AONContext ctx, Invoice invoice);

}
