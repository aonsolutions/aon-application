package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;

public interface IManagement {
	
	
	// 	***********************************************
	// 	************************************* OFFER ***
	// 	***********************************************
	Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter);

	// -------------------- PURCHASE 
	Stream<Purchase> getPurchaseStream(AONContext ctx, PurchaseFilter filter);
	Purchase insertPurchase(AONContext ctx, Purchase purchase);
	Purchase updatePurchase(AONContext ctx, Purchase purchase, PurchaseFilter filter);
	void deletePurchase(AONContext ctx, PurchaseFilter filter);
	
	// -------------------- PURCHASE DETAIL
	Stream<PurchaseDetail> getPurchaseDetailStream(AONContext ctx, PurchaseDetailFilter filter);
	
}
