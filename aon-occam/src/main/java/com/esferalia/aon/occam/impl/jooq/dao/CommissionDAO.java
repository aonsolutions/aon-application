package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Commission.COMMISSION;
import static com.esferalia.aon.jooq.tables.CommissionType.COMMISSION_TYPE;
import static com.esferalia.aon.jooq.tables.CommissionCategory.COMMISSION_CATEGORY;
import static com.esferalia.aon.jooq.tables.CommissionItem.COMMISSION_ITEM;
import static com.esferalia.aon.jooq.tables.CommissionTypeCommission.COMMISSION_TYPE_COMMISSION;
import static com.esferalia.aon.jooq.tables.OfferDetailCommission.OFFER_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailCommission.INVOICE_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.CommissionCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionItemFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.OfferDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.commission.Commission;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CommissionCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CommissionFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CommissionItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CommissionTypeCommissionFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CommissionTypeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.InvoiceDetailCommissionFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.OfferDetailCommissionFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CommissionCategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CommissionItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CommissionPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CommissionTypeCommissionPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CommissionTypePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoiceDetailCommissionPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.OfferDetailCommissionPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class CommissionDAO {
	
	private CommissionDAO() {
	
	}
	
	private static final OfferDetailCommissionPropertiesDAO OFFER_DETAIL_COMMISSION_PROPERTIES = new OfferDetailCommissionPropertiesDAO();
	private static final InvoiceDetailCommissionPropertiesDAO INVOICE_DETAIL_COMMISSION_PROPERTIES = new InvoiceDetailCommissionPropertiesDAO();
	private static final CommissionPropertiesDAO COMMISSION_PROPERTIES = new CommissionPropertiesDAO();
	private static final CommissionTypePropertiesDAO COMMISSION_TYPE_PROPERTIES = new CommissionTypePropertiesDAO();
	private static final CommissionItemPropertiesDAO COMMISSION_ITEM_PROPERTIES = new CommissionItemPropertiesDAO();
	private static final CommissionCategoryPropertiesDAO COMMISSION_CATEGORY_PROPERTIES = new CommissionCategoryPropertiesDAO();
	private static final CommissionTypeCommissionPropertiesDAO COMMISSION_TYPE_COMMISSION_PROPERTIES = new CommissionTypeCommissionPropertiesDAO();
	
	public static Stream<Commission> getCommissionStream(AONContext ctx, CommissionFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(COMMISSION)
				.where(COMMISSION_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CommissionFiller());
	}
	
	public static Stream<CommissionType> getCommissionTypeStream(AONContext ctx, CommissionTypeFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(COMMISSION_TYPE)
				.where(COMMISSION_TYPE_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CommissionTypeFiller());
	}
	
	public static Stream<CommissionItem> getCommissionItemStream(AONContext ctx, CommissionItemFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(COMMISSION_ITEM).join(COMMISSION).on(COMMISSION.ID.eq(COMMISSION_ITEM.COMMISSION))
				.where(COMMISSION_ITEM_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CommissionItemFiller());
	}
	
	public static Stream<CommissionCategory> getCommissionCategoryStream(AONContext ctx, CommissionCategoryFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(COMMISSION_CATEGORY).join(COMMISSION).on(COMMISSION.ID.eq(COMMISSION_CATEGORY.COMMISSION))
				.where(COMMISSION_CATEGORY_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CommissionCategoryFiller());
	}

	public static Stream<CommissionTypeCommission> getCommissionTypeCommissionStream(AONContext ctx, CommissionTypeCommissionFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(COMMISSION_TYPE_COMMISSION).join(COMMISSION).on(COMMISSION.ID.eq(COMMISSION_TYPE_COMMISSION.COMMISSION))
				.where(COMMISSION_TYPE_COMMISSION_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CommissionTypeCommissionFiller());
	}

	/*
	 *		OFFER DETAIL COMMISSION
	 */
	
	public static Stream<OfferDetailCommission> getOfferDetailCommissionStream(AONContext ctx, OfferDetailCommissionFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(OFFER_DETAIL_COMMISSION)
				.join(OFFER_DETAIL).on(OFFER_DETAIL_COMMISSION.OFFER_DETAIL.eq(OFFER_DETAIL.ID))
				.join(OFFER).on(OFFER_DETAIL.OFFER.eq(OFFER.ID))
				.leftOuterJoin(SELLER).on(OFFER.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(OFFER.SELLER.eq(SELLER_ALIAS.ID))
				.where(OFFER_DETAIL_COMMISSION_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new OfferDetailCommissionFiller());
	}
	
	public static OfferDetailCommission insertOfferDetailCommission(AONContext ctx, OfferDetailCommission odc) {
		return ctx.getDslContext().insertInto(OFFER_DETAIL_COMMISSION)
				.set(OFFER_DETAIL_COMMISSION.DOMAIN, odc.getDomain())
				.set(OFFER_DETAIL_COMMISSION.OFFER_DETAIL, odc.getOfferDetail().getId())
				.set(OFFER_DETAIL_COMMISSION.AMOUNT, odc.getAmount())
				.set(OFFER_DETAIL_COMMISSION.COMMISSION, odc.getCommission())
				.set(OFFER_DETAIL_COMMISSION.PAY_DATE, AonDateUtils.toSql(odc.getPayDate()))
				.set(OFFER_DETAIL_COMMISSION.STATUS, odc.getStatus().value())
				.returning().fetch().stream().map(new OfferDetailCommissionFiller())
				.findFirst().orElse(null);
	}
	
	public static OfferDetailCommission updateOfferDetailCommission(AONContext ctx, OfferDetailCommission odc) {
		return ctx.getDslContext().update(OFFER_DETAIL_COMMISSION)
				.set(OFFER_DETAIL_COMMISSION.DOMAIN, odc.getDomain())
				.set(OFFER_DETAIL_COMMISSION.OFFER_DETAIL, odc.getOfferDetail().getId())
				.set(OFFER_DETAIL_COMMISSION.AMOUNT, odc.getAmount())
				.set(OFFER_DETAIL_COMMISSION.COMMISSION, odc.getCommission())
				.set(OFFER_DETAIL_COMMISSION.PAY_DATE, AonDateUtils.toSql(odc.getPayDate()))
				.set(OFFER_DETAIL_COMMISSION.STATUS, odc.getStatus().value())
				.where(OFFER_DETAIL_COMMISSION.ID.eq(odc.getId()))
				.returning().fetch().stream().map(new OfferDetailCommissionFiller())
				.findFirst().orElse(null);
	}
	
	public static void deleteOfferDetailCommission(AONContext ctx, Integer id) {
		ctx.getDslContext()
			.delete(OFFER_DETAIL_COMMISSION)
			.where(OFFER_DETAIL_COMMISSION.ID.eq(id))
			.execute();
	}
	
	
	/*
	 *		INVOICE DETAIL COMMISSION
	 */
	
	public static Stream<InvoiceDetailCommission> getInvoiceDetailCommissionStream(AONContext ctx, InvoiceDetailCommissionFilter filter) {
		ctx.checkRead();
		return INVOICE_DETAIL_COMMISSION_PROPERTIES.build(ctx.getDslContext().select()
				.from(INVOICE_DETAIL_COMMISSION)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL_COMMISSION.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
				.leftOuterJoin(REGISTRY).on(INVOICE.SELLER.eq(REGISTRY.ID)), filter)
				.fetch().stream().map(new InvoiceDetailCommissionFiller());
	}
	
	public static InvoiceDetailCommission insertInvoiceDetailCommission(AONContext ctx, InvoiceDetailCommission idc) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL_COMMISSION)
				.set(INVOICE_DETAIL_COMMISSION.DOMAIN, idc.getDomain())
				.set(INVOICE_DETAIL_COMMISSION.INVOICE_DETAIL, idc.getInvoiceFlat().getId())
				.set(INVOICE_DETAIL_COMMISSION.AMOUNT, idc.getAmount())
				.set(INVOICE_DETAIL_COMMISSION.COMMISSION, idc.getCommission())
				.set(INVOICE_DETAIL_COMMISSION.PAY_DATE, idc.getPayDate() != null ? AonDateUtils.toSql(idc.getPayDate()) : null)
				.set(INVOICE_DETAIL_COMMISSION.STATUS, idc.getStatus().value())
				.execute();
				
		return getInvoiceDetailCommissionStream(ctx, f -> f.getIdProperty().eq(id)).findFirst().orElse(new InvoiceDetailCommission());
	}
	
	public static InvoiceDetailCommission updateInvoiceDetailCommission(AONContext ctx, InvoiceDetailCommission idc) {
		return ctx.getDslContext().update(INVOICE_DETAIL_COMMISSION)
				.set(INVOICE_DETAIL_COMMISSION.DOMAIN, idc.getDomain())
				.set(INVOICE_DETAIL_COMMISSION.INVOICE_DETAIL, idc.getInvoiceFlat().getId())
				.set(INVOICE_DETAIL_COMMISSION.AMOUNT, idc.getAmount())
				.set(INVOICE_DETAIL_COMMISSION.COMMISSION, idc.getCommission())
				.set(INVOICE_DETAIL_COMMISSION.PAY_DATE, idc.getPayDate() != null ? AonDateUtils.toSql(idc.getPayDate()) : null)
				.set(INVOICE_DETAIL_COMMISSION.STATUS, idc.getStatus().value())
				.where(INVOICE_DETAIL_COMMISSION.ID.eq(idc.getId()))
				.returning().fetch().stream().map(new InvoiceDetailCommissionFiller())
				.findFirst().orElse(null);
	}
	
	public static void deleteInvoiceDetailCommission(AONContext ctx, Integer id) {
		deleteInvoiceDetailCommission(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void deleteInvoiceDetailCommission(AONContext ctx, InvoiceDetailCommissionFilter filter) {
		ctx.getDslContext()
			.delete(INVOICE_DETAIL_COMMISSION)
			.where(INVOICE_DETAIL_COMMISSION_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	
}




