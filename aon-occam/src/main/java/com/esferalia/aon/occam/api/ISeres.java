package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

public interface ISeres {
	
	public SeresInfo getSeresInfo(AONContext ctx);
	public EdiCodes getEdiCodes(AONContext ctx, Delivery delivery);
	public EdiCodes getEdiCodes(AONContext ctx, Invoice invoice);

}
