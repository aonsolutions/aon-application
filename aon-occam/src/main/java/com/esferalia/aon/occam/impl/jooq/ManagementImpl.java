package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IManagement;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PurchaseDAO;

public class ManagementImpl implements IManagement {

	@Override
	public Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter) {
		return OfferDAO.getOfferDetails(ctx, filter);
	}

	// ------------------ PURCHASE
	
	@Override
	public Stream<Purchase> getPurchaseStream(AONContext ctx, PurchaseFilter filter) {
		return ctx.getDslContext().transactionResult(
			configuration -> PurchaseDAO.getPurchaseStream(ctx, filter));
	}

	@Override
	public Purchase insertPurchase(AONContext ctx, Purchase purchase) {
		return ctx.getDslContext().transactionResult(
				configuration -> PurchaseDAO.insertPurchase3(ctx, purchase));
	}

	@Override
	public Purchase updatePurchase(AONContext ctx, Purchase purchase, PurchaseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> PurchaseDAO.updatePurchase(ctx, purchase, filter));

	}

	@Override
	public void deletePurchase(AONContext ctx, PurchaseFilter filter) {
		 ctx.getDslContext().transaction(
				configuration -> PurchaseDAO.deletePurchase(ctx, filter));		
	}

	// ------------------ PURCHASE DETAIL
	
	@Override
	public Stream<PurchaseDetail> getPurchaseDetailStream(AONContext ctx, PurchaseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
			configuration -> PurchaseDAO.getPurchaseDetailStream(ctx, filter));
	}
}
