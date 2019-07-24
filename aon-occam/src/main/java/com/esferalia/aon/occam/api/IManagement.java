package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;

public interface IManagement {
	
	
	// 	***********************************************
	// 	************************************* OFFER ***
	// 	***********************************************
	Offer getOffer(AONContext ctx, OfferFilter filter);
	Stream<Offer> getOfferStream(AONContext ctx, OfferFilter filter);
	Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter);
	Offer updateOffer(AONContext ctx, Offer offer);

	// -------------------- SALES 
	Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter);
	void updateSales(AONContext ctx, Sales sales);
	
	// -------------------- SALES DETAIL
	Stream<SalesDetail> getSalesDetailStream(AONContext ctx, SalesDetailFilter filter);
	Stream<SalesDetail> getSalesDetails(AONContext ctx, SalesFilter filter);
	void updateSalesDetail(AONContext ctx, SalesDetail salesDetail);
	
	// -------------------- PURCHASE 
	Stream<Purchase> getPurchaseStream(AONContext ctx, PurchaseFilter filter);
	Purchase insertPurchase(AONContext ctx, Purchase purchase);
	Purchase updatePurchase(AONContext ctx, Purchase purchase, PurchaseFilter filter);
	void deletePurchase(AONContext ctx, PurchaseFilter filter);
	
	
	Stream<PurchaseDetail> getPurchaseDetails(AONContext ctx, PurchaseFilter filter);

	// -------------------- PURCHASE DETAIL
	Stream<PurchaseDetail> getPurchaseDetailStream(AONContext ctx, PurchaseDetailFilter filter);
	Integer insertPurchaseDetail(AONContext ctx, PurchaseDetail purchaseDetail);
	PurchaseDetail updatePurchaseDetail(AONContext ctx, PurchaseDetail purchaseDetail, PurchaseDetailFilter filter);
	void deletePurchaseDetail(AONContext ctx, PurchaseDetailFilter filter);
	
	// -------------------- DELIVERY 
	Stream<Delivery> getDeliveryStream(AONContext ctx, DeliveryFilter filter);
	Delivery insertDelivery(AONContext ctx, Delivery delivery);
	Delivery updateDelivery(AONContext ctx, Delivery delivery, DeliveryFilter filter);
	void deleteDelivery(AONContext ctx, DeliveryFilter filter);
	
	Stream<DeliveryDetail> getDeliveryDetails(AONContext ctx, DeliveryFilter filter);
	
	// -------------------- DELIVERY DETAIL
	Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryDetailFilter filter);
	DeliveryDetail insertDeliveryDetail(AONContext ctx, DeliveryDetail deliveryDetail);

	// -------------------- INCOME DETAIL
	Stream<IncomeDetail> getIncomeDetails(AONContext ctx, IncomeFilter filter);
}
