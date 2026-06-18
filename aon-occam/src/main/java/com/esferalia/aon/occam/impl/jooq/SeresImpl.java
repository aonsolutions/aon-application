package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISeres;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.PriceStrategyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PriceStrategyDAO.PriceStrategy;
import com.esferalia.aon.occam.impl.jooq.dao.SeresDAO;

public class SeresImpl implements ISeres {

	@Override
	public SeresInfo getSeresInfo(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> 
			SeresDAO.getSeresInfo(ctx));
	}
	
	@Override
	public SeresInfo getSeresInfo(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(configuration -> 
			SeresDAO.getSeresInfo(ctx, invoice));
	}
	
	@Override
	public SeresInfo getSeresInfo(AONContext ctx, Delivery delivery) {
		return ctx.getDslContext().transactionResult(configuration -> 
			SeresDAO.getSeresInfo(ctx, delivery));
	}
	
	@Override
	public EdiCodes getEdiCodes(AONContext ctx, Delivery delivery) {
		return ctx.getDslContext().transactionResult(configuration -> 
			SeresDAO.getEdiCodes(ctx, delivery));
	}

	@Override
    public EdiCodes getEdiCodes(AONContext ctx, Invoice invoice) {
        return ctx.getDslContext().transactionResult(configuration -> 
            SeresDAO.getEdiCodes(ctx, invoice));
    }
	
	@Override
    public PriceStrategy calculatePriceStrategy(AONContext ctx, Integer customer, Date date, Item item) {
        return ctx.getDslContext().transactionResult(configuration -> 
            PriceStrategyDAO.calculatePriceStrategy(ctx, customer, date, item));
    }
	
}
