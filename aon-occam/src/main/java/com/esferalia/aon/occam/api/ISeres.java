package com.esferalia.aon.occam.api;

import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.PriceStrategyDAO.PriceStrategy;

public interface ISeres {
	
	public SeresInfo getSeresInfo(AONContext ctx);
	public SeresInfo getSeresInfo(AONContext ctx, Invoice invoice);
	public SeresInfo getSeresInfo(AONContext ctx, Delivery delivery);
	
	public EdiCodes getEdiCodes(AONContext ctx, Delivery delivery);
	public EdiCodes getEdiCodes(AONContext ctx, Invoice invoice);
	
	public PriceStrategy calculatePriceStrategy(AONContext ctx, Integer customer, Date date, Item item);
	public double getUnitPrice(AONContext ctx, Integer customer, Date date, Item item);

}
