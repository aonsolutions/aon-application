package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IManagement;
import com.esferalia.aon.occam.api.model.Options;
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
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IncomeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PurchaseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;

public class ManagementImpl implements IManagement {

	// ------------------ OFFER
	
	@Override
	public Offer getOffer(AONContext ctx, OfferFilter filter) {
		return OfferDAO.getOffer(ctx, filter);
	}
	
	@Override
	public Stream<Offer> getOfferStream(AONContext ctx, OfferFilter filter) {
		return OfferDAO.getOfferStream(ctx, filter);
	}

	
	@Override
	public Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter) {
		return OfferDAO.getOfferDetails(ctx, filter);
	}
	
	@Override
	public Offer updateOffer(AONContext ctx, Offer offer) {
		return ctx.getDslContext().transactionResult(
				configuration -> OfferDAO.updateOffer(ctx, offer));
	}
	
	@Override
	public Offer insertOffer(AONContext ctx, Offer offer) {
		return ctx.getDslContext().transactionResult(
				configuration -> OfferDAO.insertOffer(ctx, offer));
	}

	@Override
	public OfferDetail insertOfferDetail(AONContext ctx, OfferDetail offerDetail) {
		return ctx.getDslContext().transactionResult(
				configuration -> OfferDAO.insertOfferDetail(ctx, offerDetail));
	}

	// ------------------ SALES
	@Override
	public Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options) {
		return ctx.getDslContext().transactionResult(
				configuration -> SalesDAO.getStream(ctx, filter, options));
	}
	
	@Override
	public Sales getSales(AONContext ctx, SalesFilter filter, Options... options) {
		return ctx.getDslContext().transactionResult(
				configuration -> SalesDAO.get(ctx, filter, options));
	}
	
	@Override
	public Sales saveSales(AONContext ctx, Sales sales) {
		return ctx.getDslContext().transactionResult(
			configuration -> SalesDAO.save(ctx, sales));
	}
	
	@Override
	public void deleteSales(AONContext ctx, Integer salesId) {
		ctx.getDslContext().transaction(
			configuration -> SalesDAO.delete(ctx, salesId));
	}
	
	// ------------------ SALES DETAIL
	
	@Override
	public Stream<SalesDetail> getSalesDetailStream(AONContext ctx, SalesDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> SalesDAO.getSalesDetailStream(ctx, filter));
	}
	
	@Override
	public Stream<SalesDetail> getSalesDetails(AONContext ctx, SalesFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> SalesDAO.getSalesDetails(ctx, filter));
	}

	@Override
	public void updateSalesDetail(AONContext ctx, SalesDetail salesDetail) {
		ctx.getDslContext().transaction(
				configuration -> SalesDAO.updateSalesDetail(ctx, salesDetail));
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
	public Stream<PurchaseDetail> getPurchaseDetails(AONContext ctx, PurchaseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> PurchaseDAO.getPurchaseDetails(ctx, filter));
	}
	
	@Override
	public Stream<PurchaseDetail> getPurchaseDetailStream(AONContext ctx, PurchaseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
			configuration -> PurchaseDAO.getPurchaseDetailStream(ctx, filter));
	}
	
	@Override
	public Integer insertPurchaseDetail(AONContext ctx, PurchaseDetail purchaseDetail) {
		return ctx.getDslContext().transactionResult(
				configuration -> PurchaseDAO.insertPurchaseDetail(ctx, purchaseDetail));
	}

	@Override
	public PurchaseDetail updatePurchaseDetail(AONContext ctx, PurchaseDetail purchaseDetail,
			PurchaseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> PurchaseDAO.updatePurchaseDetail(ctx, purchaseDetail, filter));
	}

	@Override
	public void deletePurchaseDetail(AONContext ctx, PurchaseDetailFilter filter) {
		ctx.getDslContext().transaction(
					configuration -> PurchaseDAO.deletePurchaseDetail(ctx, filter));		
	}
	
	// ------------------ DELIVERY
	
	@Override
	public Stream<Delivery> getDeliveryStream(AONContext ctx, DeliveryFilter filter, Options...options) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.getStream(ctx, filter, options));
	}
	
	@Override
	public Delivery getDelivery(AONContext ctx, DeliveryFilter filter, Options...options) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.get(ctx, filter, options));
	}

	@Override
	public Delivery saveDelivery(AONContext ctx, Delivery delivery) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.save(ctx, delivery));
	}
	
	@Override
	@Deprecated(forRemoval = true)
	public Delivery insertDelivery(AONContext ctx, Delivery delivery) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.insertDelivery(ctx, delivery));
	}

	@Override
	@Deprecated(forRemoval = true)
	public Delivery updateDelivery(AONContext ctx, Delivery delivery, DeliveryFilter filter) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.updateDelivery(ctx, delivery, filter));
	}

	@Override
	public void deleteDelivery(AONContext ctx, Integer id) {
		 ctx.getDslContext().transaction(
			configuration -> DeliveryDAO.delete(ctx, id));		
	}
	
	@Override
	public void deleteDelivery(AONContext ctx, DeliveryFilter filter) {
		 ctx.getDslContext().transaction(
			configuration -> DeliveryDAO.deleteDelivery(ctx, filter));		
	}
	
	// ------------------ DELIVERY DETAIL
	
	@Override
	public Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.getDeliveryDetailStream(ctx, filter));
	}

	@Override
	public Stream<DeliveryDetail> getDeliveryDetails(AONContext ctx, DeliveryFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DeliveryDAO.getDeliveryDetails(ctx, filter));
	}
	
	@Override
	public DeliveryDetail insertDeliveryDetail(AONContext ctx, DeliveryDetail deliveryDetail) {
		return ctx.getDslContext().transactionResult(
			configuration -> DeliveryDAO.insertDeliveryDetail(ctx, deliveryDetail));
	}

	// ------------------ INCOME DETAIL
	
	@Override
	public Stream<IncomeDetail> getIncomeDetails(AONContext ctx, IncomeFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> IncomeDAO.getIncomeDetails(ctx, filter));
	}

}
