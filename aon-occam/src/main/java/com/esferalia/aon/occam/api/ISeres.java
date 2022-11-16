package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

public interface ISeres {
	
	public EdiCodes getEdiCodes(AONContext ctx, Delivery delivery);
	public EdiCodes getEdiCodes(AONContext ctx, Invoice invoice);

}
