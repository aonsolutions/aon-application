package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PurchaseDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PurchaseDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PurchasePropertiesDAO;


public class PurchaseDAO {
	
	private static final PurchaseDetailPropertiesDAO PURCHASE_DETAIL_PROPERTIES = new PurchaseDetailPropertiesDAO();
	private static final PurchasePropertiesDAO PURCHASE_PROPERTIES = new PurchasePropertiesDAO();

	


	public static Stream<Purchase> getPurchaseStream(AONContext ctx, PurchaseFilter filter){
		return ctx.getDslContext().select().from(PURCHASE).where(PURCHASE_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new FullPurchaseFiller());
	}
	
	public static Purchase getPurchase(AONContext ctx, Integer id){
		return ctx.getDslContext().select().from(PURCHASE).where(PURCHASE.ID.eq(id)).limit(1).fetchInto(PURCHASE)
			.stream().map(new FullPurchaseFiller()).findFirst().orElse(new Purchase());
	}
	
	public static PurchaseDetail getPurchaseDetail(AONContext ctx, PurchaseDetailFilter filter){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL).where(PURCHASE_DETAIL_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PURCHASE_DETAIL).stream().map(new FullPurchaseDetailFiller(ctx)).findFirst().orElse(new PurchaseDetail());
	}
	
	public static PurchaseDetail getTargetManufactureDetail(AONContext ctx, Integer salesDetailId){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL).leftOuterJoin(PURCHASE).onKey()
				.where(PURCHASE_DETAIL.SOURCE.eq(PurchaseSourceType.SALES.value()))
				.and(PURCHASE_DETAIL.SOURCE_ID.eq(salesDetailId))
				.and(PURCHASE.DOCUMENT_TYPE.eq(PurchaseType.MANUFACTURE.value()))
				.orderBy(PURCHASE_DETAIL.ID.desc())
				.limit(1).fetchInto(PURCHASE_DETAIL).stream().map(new FullPurchaseDetailFiller(ctx)).findFirst().orElse(new PurchaseDetail());
	}
	
	public static PurchaseDetail getTargetPurchaseDetail(AONContext ctx, Integer salesDetailId){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL).leftOuterJoin(PURCHASE).onKey()
				.where(PURCHASE_DETAIL.SOURCE.eq(PurchaseSourceType.SALES.value()))
				.and(PURCHASE_DETAIL.SOURCE_ID.eq(salesDetailId))
				.and(PURCHASE.DOCUMENT_TYPE.eq(PurchaseType.NORMAL.value()))
				.orderBy(PURCHASE_DETAIL.ID.desc())
				.limit(1).fetchInto(PURCHASE_DETAIL).stream().map(new FullPurchaseDetailFiller(ctx)).findFirst().orElse(new PurchaseDetail());
	}
	
	public static List<PurchaseDetail> getTargetManufactureDetails(AONContext ctx, Integer salesId){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL)
					.leftOuterJoin(PURCHASE).onKey()
					.leftOuterJoin(SALES_DETAIL).on(PURCHASE_DETAIL.SOURCE_ID.eq(SALES_DETAIL.ID))
				.where(PURCHASE_DETAIL.SOURCE.eq(PurchaseSourceType.SALES.value()))
					.and(SALES_DETAIL.SALES.eq(salesId))
					.and(PURCHASE.DOCUMENT_TYPE.eq(PurchaseType.MANUFACTURE.value()))
				.fetchInto(PURCHASE_DETAIL).stream().map(new FullPurchaseDetailFiller(ctx))
				.collect(Collectors.toList());
	}
	
	public static List<PurchaseDetail> getTargetPurchaseDetails(AONContext ctx, Integer salesId){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL)
				.leftOuterJoin(PURCHASE).onKey()
				.leftOuterJoin(SALES_DETAIL).on(PURCHASE_DETAIL.SOURCE_ID.eq(SALES_DETAIL.ID))
				.where(PURCHASE_DETAIL.SOURCE.eq(PurchaseSourceType.SALES.value()))
				.and(SALES_DETAIL.ID.eq(salesId))
				.and(PURCHASE.DOCUMENT_TYPE.eq(PurchaseType.NORMAL.value()))
				.fetchInto(PURCHASE_DETAIL).stream().map(new FullPurchaseDetailFiller(ctx))
				.collect(Collectors.toList());
	}

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
						PURCHASE_DETAIL.DELIVERED, PURCHASE_DETAIL.SOURCE,
						PURCHASE_DETAIL.SOURCE_ID,
						PURCHASE_DETAIL.CREATION_USER,
						PURCHASE_DETAIL.CREATION_DATE,
						PURCHASE_DETAIL.MODIFICATION_USER,
						PURCHASE_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getPurchaseId(),
						detail.getProject(), detail.getLine().shortValue(),
						detail.getItem(), detail.getDescription(),
						detail.getQuantity(), detail.getPrice(),
						detail.getDiscountExpression(), detail.getTaxes(),
						(byte) detail.getStatus().ordinal(),
						detail.getProposalDetail(), detail.getDelivered(),
						detail.getSource().value(), detail.getSourceId(),
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
	

	
	private static class FullPurchaseFiller implements Function<Record, Purchase> {
		@Override
		public Purchase apply(Record r) {
			Purchase purchase = new Purchase();
			purchase.setId(r.getValue(PURCHASE.ID));
			purchase.setDomain(r.getValue(PURCHASE.DOMAIN));
			purchase.setProject(r.getValue(PURCHASE.PROJECT));
			purchase.setSupplier(r.getValue(PURCHASE.SUPPLIER));
			purchase.setSeries(r.getValue(PURCHASE.SERIES));
			purchase.setNumber(r.getValue(PURCHASE.NUMBER));
			purchase.setPurchaseReference(r.getValue(PURCHASE.PURCHASE_REFERENCE));
			purchase.setAddress(r.getValue(PURCHASE.ADDRESS));
			purchase.setDiscountExpr(r.getValue(PURCHASE.DISCOUNT_EXPR));
			purchase.setIssueDate(r.getValue(PURCHASE.ISSUE_DATE));
			purchase.setPayMethod(r.getValue(PURCHASE.PAY_METHOD));
			purchase.setDocumentType(PurchaseType.values()[r.getValue(PURCHASE.DOCUMENT_TYPE)]);
			purchase.setSecurityLevel(r.getValue(PURCHASE.SECURITY_LEVEL));
			purchase.setStatus(PurchaseStatus.values()[r.getValue(PURCHASE.STATUS)]);
			purchase.setComments(r.getValue(PURCHASE.COMMENTS));
			purchase.setRemarks(r.getValue(PURCHASE.REMARKS));
			purchase.setWorkplace(r.getValue(PURCHASE.WORKPLACE));
			purchase.setWarehouse(r.getValue(PURCHASE.WAREHOUSE));
			purchase.setScope(r.getValue(PURCHASE.SCOPE));
			purchase.setNumberOfPymnts(r.getValue(PURCHASE.NUMBER_OF_PYMNTS));
			purchase.setDaysToFirstPymnt(r.getValue(PURCHASE.DAYS_TO_FIRST_PYMNT));
			purchase.setDaysBetweenPymnts(r.getValue(PURCHASE.DAYS_BETWEEN_PYMNTS));
			purchase.setPymntDays(r.getValue(PURCHASE.PYMNT_DAYS));
			purchase.setBankAccount(r.getValue(PURCHASE.BANK_ACCOUNT));
			purchase.setBankAlias(r.getValue(PURCHASE.BANK_ALIAS));
			purchase.setBic(r.getValue(PURCHASE.BIC));
			purchase.setEmailCommunication(r.getValue(PURCHASE.EMAIL_COMMUNICATION) == 1);
			purchase.setCarrier(r.getValue(PURCHASE.CARRIER));
			purchase.setShippingAlternativeAddress(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS));
			purchase.setShippingAlternativeAddress2(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2));
			purchase.setShippingAlternativeZip(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_ZIP));
			purchase.setShippingAlternativeCity(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_CITY));
			purchase.setShippingAlternativePhone(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_PHONE));
			purchase.setShippingAlternativeRecipient(r.getValue(PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT));
			purchase.setShippingContact(r.getValue(PURCHASE.SHIPPING_CONTACT));
			purchase.setShippingPeriod(r.getValue(PURCHASE.SHIPPING_PERIOD)!=null?r.getValue(PURCHASE.SHIPPING_PERIOD).intValue():null);
			purchase.setCarrierPacking(r.getValue(PURCHASE.CARRIER_PACKING));
			return purchase;
		}
	}
	
	private static class FullPurchaseDetailFiller implements Function<PurchaseDetailRecord, PurchaseDetail> {
		AONContext ctx;
		public FullPurchaseDetailFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		@Override
		public PurchaseDetail apply(PurchaseDetailRecord r) {
			PurchaseDetail detail = new PurchaseDetail();
			detail.setId(r.getId());
			detail.setDomain(r.getDomain());
			detail.setPurchaseId(r.getPurchase());
			detail.setPurchase(getPurchase(ctx, r.getPurchase()));
			detail.setItem(r.getItem());
			detail.setLine(r.getLine().intValue());
			detail.setDescription(r.getDescription());
			detail.setQuantity(r.getQuantity());
			detail.setPrice(r.getPrice());
			detail.setDiscountExpression(r.getDiscountExpr());
			detail.setTaxes(r.getTaxes());
			detail.setStatus(PurchaseDetailStatus.values()[r.getStatus()]);
			detail.setProposalDetail(r.getProposalDetail());
			detail.setSource(PurchaseSourceType.values()[r.getSource()]);
			detail.setSourceId(r.getSourceId());
			detail.setDelivered(r.getDelivered());
			return detail;
		}
	}
}
