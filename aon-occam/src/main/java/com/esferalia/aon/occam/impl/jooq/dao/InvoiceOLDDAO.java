package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ProductPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;;

@Deprecated
public class InvoiceOLDDAO {

	private  InvoiceOLDDAO() {
		
	}
	
	private static final String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy}. ";
	static final Date VAT_ACCRUAL_START_DATE = AonDateUtils.getDate(2014, 0, 1);
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();
	
	@Deprecated	
	private static class InvoiceDetailFiller extends Filler implements Function<Record, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return new InvoiceDetail().setInvoice(new Invoice()
						.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
						.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
						.setSeries(r.getValue(INVOICE.SERIES))
						.setNumber(r.getValue(INVOICE.NUMBER))
						.setRegistry(r.getValue(INVOICE.REGISTRY))
						.setRegistryName(r.getValue(INVOICE.RNAME))
						)
					.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
					.setId(r.getValue(INVOICE_DETAIL.ID))
					.setItem(checkField(r, ITEM.ID)
							? ItemFiller.build(r)
							: new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
					.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR) != null
							? r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR) : "0.0")
					.setQuantity(
							r.getValue(INVOICE_DETAIL.QUANTITY) != null ? r.getValue(INVOICE_DETAIL.QUANTITY) : 0.0);
		}

	}

	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	// *************************************************************************************
	@Deprecated
	public static InvoiceDetail getLastInvoiceDetail(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId){
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select()
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId()))
				.and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.orderBy(INVOICE.ISSUE_DATE.desc())
				.limit(1).fetch().stream().map(new InvoiceDetailFiller())
				.findFirst().orElse(new InvoiceDetail());
	}
	@Deprecated
	public static InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date){
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);

		return ctx.getDslContext()
				.select()
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId()))
				.and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.orderBy(INVOICE.ISSUE_DATE.desc())
				.limit(1).fetch().stream().map(new InvoiceDetailFiller())
				.findFirst().orElse(new InvoiceDetail());
	}
	@Deprecated
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
						INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate)))
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
								INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())
						.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate))))
				
				.orderBy(INVOICE.ISSUE_DATE.desc())// ,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Deprecated
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
						INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate)))
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
								INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())
						.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
						.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate))))
						
				
				.orderBy(INVOICE.ISSUE_DATE.desc())//,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Deprecated
	public static LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);

		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
						INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
								INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull()))
				
				
				.orderBy(INVOICE.ISSUE_DATE.desc())//,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Deprecated
	public static LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);

		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
						INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REGISTRY, INVOICE.RNAME,
								INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull()))
				
				
				.orderBy(INVOICE.ISSUE_DATE.desc()) //,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Deprecated
	public static Stream<OldProduct> getInvoiceProducts(AONContext ctx, ProductFilter filter) {
		return 	ctx.getDslContext().select(PRODUCT.ID,PRODUCT.NAME,PRODUCT.CODE)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
			.join(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.join(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.where(PRODUCT_PROPERTIES.getConditions(filter))
			.and(INVOICE.DOMAIN.eq(ctx.getDomainId())
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),INVOICE.SCOPE)))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), INVOICE.SECURITY_LEVEL))
			.groupBy(INVOICE.TYPE,PRODUCT.ID)
			.orderBy(PRODUCT.NAME)
			.limit(30)
			.fetch()
			.stream()
			.map(rec -> new OldProduct()
					.setId( rec.getValue(PRODUCT.ID) )
					.setCode( rec.getValue(PRODUCT.CODE) )
					.setName(rec.getValue(PRODUCT.NAME))
					);			
	}
	
	
}
