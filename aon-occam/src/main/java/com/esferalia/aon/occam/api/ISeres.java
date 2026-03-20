package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

public interface ISeres {
	
	public SeresInfo getSeresInfo(AONContext ctx);
	public SeresInfo getSeresInfo(AONContext ctx, Invoice invoice);
	public SeresInfo getSeresInfo(AONContext ctx, Delivery delivery);
	
	public EdiCodes getEdiCodes(AONContext ctx, Delivery delivery);
	public EdiCodes getEdiCodes(AONContext ctx, Invoice invoice);
	
	public double getUnitPrice(AONContext ctx, Integer customer, Integer item);

}
