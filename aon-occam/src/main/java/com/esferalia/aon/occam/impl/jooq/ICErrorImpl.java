package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IICError;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.ICErrorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO;

public class ICErrorImpl implements IICError {

	@Override
	public void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice) {
		ctx.getDslContext().transaction( configuration -> 
		InvestAssetDAO.assignInvestAsset2Invoice(ctx, investAssetId, invoice));
	}
	
	@Override
	public void addDocumentInvoice(AONContext ctx, Invoice invoice) {
		ctx.getDslContext().transaction( configuration -> 
			ICErrorDAO.addDocumentInvoice(ctx, invoice));
	}
}
