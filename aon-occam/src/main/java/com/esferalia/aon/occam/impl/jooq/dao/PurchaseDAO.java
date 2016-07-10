package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.sql.Timestamp;
import java.util.Date;

import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.type.PurchaseType;


public class PurchaseDAO {
	

	public static int insertManufacturePurchase(AONContext ctx, Purchase purchase){
		Integer enterpriseId = obtainEnterpriseId(ctx, purchase.getDomain());
		Integer enterpriseScope = obtainEnterpriseScope(ctx, purchase.getDomain());
		Integer supplier = obtainSupplier(ctx, enterpriseId);
		if(supplier==null){
			supplier = createCompanySupplier(ctx, purchase.getDomain(), enterpriseId, enterpriseScope);
		}
		purchase.setSupplier(supplier);
		purchase.setDocumentType(PurchaseType.MANUFACTURE);
		return insertPurchase(ctx, purchase);
	}
	
	public static int insertPurchase(AONContext ctx, Purchase purchase) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		if(purchase.getNumber()==null || purchase.getNumber()==0){
			Integer number = obtainManufactureMaxNumber(ctx, purchase.getDomain(), purchase.getSeries());
			purchase.setNumber(number!=null?++number:1);
		}
		return ctx
				.getDslContext()
				.insertInto(PURCHASE, PURCHASE.DOMAIN, PURCHASE.PROJECT,
						PURCHASE.SUPPLIER, PURCHASE.SERIES, PURCHASE.NUMBER,
						PURCHASE.PURCHASE_REFERENCE, PURCHASE.ADDRESS,
						PURCHASE.DISCOUNT_EXPR, PURCHASE.ISSUE_DATE,
						PURCHASE.PAY_METHOD, PURCHASE.DOCUMENT_TYPE,
						PURCHASE.SECURITY_LEVEL, PURCHASE.STATUS,
						PURCHASE.COMMENTS, PURCHASE.REMARKS,
						PURCHASE.WORKPLACE, PURCHASE.WAREHOUSE, PURCHASE.SCOPE,
						PURCHASE.NUMBER_OF_PYMNTS,
						PURCHASE.DAYS_TO_FIRST_PYMNT,
						PURCHASE.DAYS_BETWEEN_PYMNTS, PURCHASE.PYMNT_DAYS,
						PURCHASE.BANK_ACCOUNT, PURCHASE.BANK_ALIAS,
						PURCHASE.BIC, PURCHASE.EMAIL_COMMUNICATION,
						PURCHASE.CARRIER,
						PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS,
						PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2,
						PURCHASE.SHIPPING_ALTERNATIVE_ZIP,
						PURCHASE.SHIPPING_ALTERNATIVE_CITY,
						PURCHASE.SHIPPING_ALTERNATIVE_PHONE,
						PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT,
						PURCHASE.SHIPPING_CONTACT, PURCHASE.SHIPPING_PERIOD,
						PURCHASE.CREATION_USER, PURCHASE.CREATION_DATE,
						PURCHASE.MODIFICATION_USER, PURCHASE.MODIFICATION_DATE)
				.values(purchase.getDomain(), purchase.getProject(),
						purchase.getSupplier(), purchase.getSeries(),
						purchase.getNumber(), purchase.getPurchaseReference(),
						purchase.getAddress(), purchase.getDiscountExpr(),
						purchase.getIssueDate(), purchase.getPayMethod(),
						purchase.getDocumentType().ordinal(),
						purchase.getSecurityLevel(),
						purchase.getStatus().ordinal(), purchase.getComments(),
						purchase.getRemarks(), purchase.getWorkplace(),
						purchase.getWarehouse(), purchase.getScope(),
						purchase.getNumberOfPymnts(),
						purchase.getDaysToFirstPymnt(),
						purchase.getDaysBetweenPymnts(),
						purchase.getPymntDays(), purchase.getBankAccount(),
						purchase.getBankAlias(), purchase.getBic(),
						purchase.isEmailCommunication(), purchase.getCarrier(),
						purchase.getShippingAlternativeAddress(),
						purchase.getShippingAlternativeAddress2(),
						purchase.getShippingAlternativeZip(),
						purchase.getShippingAlternativeCity(),
						purchase.getShippingAlternativePhone(),
						purchase.getShippingAlternativeRecipient(),
						purchase.getShippingContact(),
						purchase.getShippingPeriod(), ctx.getUser(),
						creationDate, ctx.getUser(), modificationDate)
						.returning(PURCHASE.ID).fetchOne().getId();
	}
	
	public static int insertPurchaseDetail(AONContext ctx, PurchaseDetail detail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.insertInto(PURCHASE_DETAIL, PURCHASE_DETAIL.DOMAIN,
						PURCHASE_DETAIL.PURCHASE, PURCHASE_DETAIL.PROJECT,
						PURCHASE_DETAIL.LINE, PURCHASE_DETAIL.ITEM,
						PURCHASE_DETAIL.DESCRIPTION, PURCHASE_DETAIL.QUANTITY,
						PURCHASE_DETAIL.PRICE, PURCHASE_DETAIL.DISCOUNT_EXPR,
						PURCHASE_DETAIL.TAXES, PURCHASE_DETAIL.STATUS,
						PURCHASE_DETAIL.PROPOSAL_DETAIL,
						PURCHASE_DETAIL.DELIVERED,
						PURCHASE_DETAIL.CREATION_USER,
						PURCHASE_DETAIL.CREATION_DATE,
						PURCHASE_DETAIL.MODIFICATION_USER,
						PURCHASE_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getPurchase(),
						detail.getProject(), detail.getLine().shortValue(),
						detail.getItem(), detail.getDescription(),
						detail.getQuantity(), detail.getPrice(),
						detail.getDiscountExpression(), detail.getTaxes(),
						(byte) detail.getStatus().ordinal(),
						detail.getProposalDetail(), detail.getDelivered(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate).execute();
	}
	
	private static Integer createCompanySupplier(AONContext ctx, int domain,
			int registry, int scope) {
		return ctx
				.getDslContext()
				.insertInto(SUPPLIER, SUPPLIER.DOMAIN, SUPPLIER.REGISTRY,
						SUPPLIER.SCOPE, SUPPLIER.WITHHOLDING,
						SUPPLIER.WITHHOLDING_FARMER,
						SUPPLIER.VAT_ACCRUAL_PAYMENT, SUPPLIER.STATUS,
						SUPPLIER.PURCHASE_VALUATED, SUPPLIER.CREATION_DATE,
						SUPPLIER.MODIFICATION_DATE)
				.values(domain, registry, scope, (byte) 0, (byte) 0, (byte) 0,
						(byte) 0, (byte) 0,
						new java.sql.Timestamp(new Date().getTime()),
						new java.sql.Timestamp(new Date().getTime())).execute();
	}

	
	private static Integer obtainSupplier(AONContext ctx, Integer registryId) {
		Record1<Integer> result = ctx.getDslContext().select(SUPPLIER.REGISTRY).from(SUPPLIER)
				.where(SUPPLIER.REGISTRY.eq(registryId)).fetchAny();
		return result!=null?result.value1():null;
	}
	
	private static Integer obtainEnterpriseId(AONContext ctx, int domain) {
		return ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domain)).fetchAny().value1();
	}
	
	private static Integer obtainEnterpriseScope(AONContext ctx, int domain) {
		return ctx.getDslContext().select(ENTERPRISE.SCOPE).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domain)).fetchAny().value1();
	}

	private static Integer obtainManufactureMaxNumber(AONContext ctx, Integer domain, String series) {
		return ctx.getDslContext().select(DSL.max(PURCHASE.NUMBER)).from(PURCHASE)
				.where(PURCHASE.DOMAIN.eq(domain))
				.and(PURCHASE.SERIES.eq(series))
				.fetchAny().value1();
	}
	
	
}
