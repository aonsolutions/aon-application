package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.InsertValuesStepN;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PurchaseDetailRecord;
import com.esferalia.aon.jooq.tables.records.PurchaseRecord;
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
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PurchaseDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PurchaseDetailItemFiller;
import com.esferalia.aon.watson.server.AonDateUtils;


public class PurchaseDAO {
	
	private static final PurchaseDetailPropertiesDAO PURCHASE_DETAIL_PROPERTIES = new PurchaseDetailPropertiesDAO();
	private static final PurchasePropertiesDAO PURCHASE_PROPERTIES = new PurchasePropertiesDAO();

	// ------------------- PURCHASE
	
	public static Stream<Purchase> getPurchaseStream2(AONContext ctx, PurchaseFilter filter){
		return ctx.getDslContext().select().from(PURCHASE)
				.join(REGISTRY).on(REGISTRY.ID.eq(PURCHASE.SUPPLIER))
				.where(PURCHASE_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new FullPurchaseFiller());
	}
	
	public static Stream<Purchase> getPurchaseStream(AONContext ctx, PurchaseFilter filter){
		return ctx.getDslContext().select().from(PURCHASE)
				.join(REGISTRY).on(REGISTRY.ID.eq(PURCHASE.SUPPLIER))
				.join(PURCHASE_DETAIL).on(PURCHASE_DETAIL.PURCHASE.eq(PURCHASE.ID))
				.where(PURCHASE_PROPERTIES.getConditions(filter))
				.orderBy(PURCHASE.ISSUE_DATE.desc())
			.fetch().stream().map(new FullPurchaseFiller()).filter(distinctByKey(p -> p.getId()));
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static Purchase getPurchase(AONContext ctx, Integer id){
		return ctx.getDslContext().select().from(PURCHASE).where(PURCHASE.ID.eq(id)).limit(1).fetchInto(PURCHASE)
			.stream().map(new FullPurchaseFiller()).findFirst().orElse(new Purchase());
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
	
	public static InsertValuesStepN<PurchaseRecord> insertPurchase2(AONContext ctx, Purchase purchase) {
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
						PURCHASE.CARRIER, PURCHASE.CARRIER_PACKING,
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
						purchase.isEmailCommunication(),
						purchase.getCarrier(), purchase.getCarrierPacking(),
						purchase.getShippingAlternativeAddress(),
						purchase.getShippingAlternativeAddress2(),
						purchase.getShippingAlternativeZip(),
						purchase.getShippingAlternativeCity(),
						purchase.getShippingAlternativePhone(),
						purchase.getShippingAlternativeRecipient(),
						purchase.getShippingContact(),
						purchase.getShippingPeriod(), ctx.getUser(),
						creationDate, ctx.getUser(), modificationDate);
	}

	public static int insertPurchase(AONContext ctx, Purchase purchase) {
		return insertPurchase2(ctx, purchase).returning(PURCHASE.ID).fetchOne().getId();
	}
	
	public static Purchase insertPurchase3(AONContext ctx, Purchase purchase) {
		return insertPurchase2(ctx, purchase).returning().fetch().stream()
				.map(new FullPurchaseFiller()).findFirst().orElse(new Purchase());
	}
	
	public static Purchase updatePurchase(AONContext ctx, Purchase purchase, PurchaseFilter filter) {
		ctx.checkWrite();
		purchase.setModificationDate(new Date());
		purchase.setModificationUser(ctx.getUser());
		if(purchase.getNumber()==null || purchase.getNumber()==0){
			Integer number = obtainManufactureMaxNumber(ctx, purchase.getDomain(), purchase.getSeries());
			purchase.setNumber(number!=null?++number:1);
		}
		return ctx.getDslContext()
				.update(PURCHASE)
				.set(PURCHASE.DOMAIN,purchase.getDomain())
				.set(PURCHASE.PROJECT, purchase.getProject())
				.set(PURCHASE.SUPPLIER, purchase.getSupplier())
				.set(PURCHASE.SERIES, purchase.getSeries())
				.set(PURCHASE.NUMBER, purchase.getNumber())
				.set(PURCHASE.PURCHASE_REFERENCE, purchase.getPurchaseReference())
				.set(PURCHASE.ADDRESS, purchase.getAddress())
				.set(PURCHASE.DISCOUNT_EXPR, purchase.getDiscountExpr())
				.set(PURCHASE.ISSUE_DATE, AonDateUtils.toSql(purchase.getIssueDate()))
				.set(PURCHASE.PAY_METHOD, purchase.getPayMethod())
				.set(PURCHASE.DOCUMENT_TYPE, purchase.getDocumentType().value())
				.set(PURCHASE.SECURITY_LEVEL, (byte) purchase.getSecurityLevel())
				.set(PURCHASE.STATUS, purchase.getStatus().value())
				.set(PURCHASE.COMMENTS, purchase.getComments())
				.set(PURCHASE.REMARKS, purchase.getRemarks())
				.set(PURCHASE.WORKPLACE, purchase.getWorkplace())
				.set(PURCHASE.WAREHOUSE, purchase.getWarehouse())
				.set(PURCHASE.SCOPE, purchase.getScope())
				.set(PURCHASE.NUMBER_OF_PYMNTS, (short) purchase.getNumberOfPymnts())
				.set(PURCHASE.DAYS_TO_FIRST_PYMNT, (short) purchase.getDaysToFirstPymnt())
				.set(PURCHASE.DAYS_BETWEEN_PYMNTS, (short) purchase.getDaysBetweenPymnts())
				.set(PURCHASE.PYMNT_DAYS, purchase.getPymntDays())
				.set(PURCHASE.BANK_ACCOUNT, purchase.getBankAccount())
				.set(PURCHASE.BANK_ALIAS, purchase.getBankAlias())
				.set(PURCHASE.BIC, purchase.getBic())
				.set(PURCHASE.EMAIL_COMMUNICATION, purchase.isEmailCommunication() ? (byte) 1 :(byte) 0)
				.set(PURCHASE.CARRIER, purchase.getCarrier())
				.set(PURCHASE.CARRIER_PACKING, purchase.getCarrierPacking())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS, purchase.getShippingAlternativeAddress())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2, purchase.getShippingAlternativeAddress2())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_ZIP, purchase.getShippingAlternativeZip())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_CITY, purchase.getShippingAlternativeCity())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_PHONE, purchase.getShippingAlternativePhone())
				.set(PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT, purchase.getShippingAlternativeRecipient())
				.set(PURCHASE.SHIPPING_CONTACT, purchase.getShippingContact())
				.set(PURCHASE.SHIPPING_PERIOD, purchase.getShippingPeriod() != null ? purchase.getShippingPeriod().byteValue() : null)
				.set(PURCHASE.CREATION_USER, purchase.getCreationUser())
				.set(PURCHASE.CREATION_DATE, AonDateUtils.toTimestamp(purchase.getCreationDate()))
				.set(PURCHASE.MODIFICATION_USER, purchase.getModificationUser())
				.set(PURCHASE.MODIFICATION_DATE, AonDateUtils.toTimestamp(purchase.getModificationDate()))
			.where(PURCHASE_PROPERTIES.getConditions(filter))
			.returning().fetch().stream().map(new FullPurchaseFiller()).findFirst().orElse(new Purchase());
	}
	
	public static void deletePurchase(AONContext ctx, PurchaseFilter filter) {
		Integer[] ids = getPurchaseStream(ctx, filter).map(r -> r.getId()).toArray(Integer[]::new);
		deletePurchaseDetail(ctx, f -> f.getPurchaseProperty().in(ids));
		ctx.getDslContext().delete(PURCHASE).where(PURCHASE_PROPERTIES.getConditions(filter));
	}
	
	// ------------------- PURCHASE DETAIL
	
	public static Stream<PurchaseDetail> getPurchaseDetailStream(AONContext ctx, PurchaseDetailFilter filter){
		return ctx.getDslContext().select().from(PURCHASE_DETAIL)
				.join(ITEM).on(PURCHASE_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.join(PURCHASE).on(PURCHASE_DETAIL.PURCHASE.eq(PURCHASE.ID))
			.where(PURCHASE_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new PurchaseDetailItemFiller());
	}
	
	public static int insertPurchaseDetail(AONContext ctx, PurchaseDetail detail) {
		ctx.checkWrite();
		return ctx.getDslContext()
			.insertInto(PURCHASE_DETAIL, 
					PURCHASE_DETAIL.DOMAIN, PURCHASE_DETAIL.PURCHASE, 
					PURCHASE_DETAIL.PROJECT,PURCHASE_DETAIL.LINE,
					PURCHASE_DETAIL.ITEM, PURCHASE_DETAIL.DESCRIPTION, 
					PURCHASE_DETAIL.QUANTITY, PURCHASE_DETAIL.PRICE,
					PURCHASE_DETAIL.DISCOUNT_EXPR, PURCHASE_DETAIL.TAXES, 
					PURCHASE_DETAIL.STATUS, PURCHASE_DETAIL.PROPOSAL_DETAIL,
					PURCHASE_DETAIL.DELIVERED, PURCHASE_DETAIL.DELIVERY_DATE, 
					PURCHASE_DETAIL.SOURCE, PURCHASE_DETAIL.SOURCE_ID,
					PURCHASE_DETAIL.CREATION_USER, PURCHASE_DETAIL.CREATION_DATE,
					PURCHASE_DETAIL.MODIFICATION_USER, PURCHASE_DETAIL.MODIFICATION_DATE)
			.values(detail.getDomain(), detail.getPurchaseId(),
					detail.getProject(), detail.getLine().shortValue(),
					detail.getItem(), detail.getDescription(),
					detail.getQuantity(), detail.getPrice(),
					detail.getDiscountExpression(), detail.getTaxes(),
					detail.getStatus().value(), detail.getProposalDetail(), 
					detail.getDelivered(), AonDateUtils.toSql(detail.getDeliveryDate()),
					detail.getSource() != null ? detail.getSource().value() : null, detail.getSourceId(),
					ctx.getUser(), AonDateUtils.toTimestamp(new Date()),
					ctx.getUser(), AonDateUtils.toTimestamp(new Date()))
			.execute();
	}
	
	public static PurchaseDetail updatePurchaseDetail(AONContext ctx, PurchaseDetail purchaseDetail, PurchaseDetailFilter filter) {
		ctx.checkWrite();
		purchaseDetail.setModificationDate(new Date());
		purchaseDetail.setModificationUser(ctx.getUser());
		
		return ctx.getDslContext()
				.update(PURCHASE_DETAIL)
				.set(PURCHASE_DETAIL.DOMAIN,purchaseDetail.getDomain())
				.set(PURCHASE_DETAIL.PURCHASE, purchaseDetail.getPurchaseId())
				.set(PURCHASE_DETAIL.PROJECT, purchaseDetail.getProject())
				.set(PURCHASE_DETAIL.LINE, purchaseDetail.getLine().shortValue())
				.set(PURCHASE_DETAIL.ITEM, purchaseDetail.getItem())
				.set(PURCHASE_DETAIL.DESCRIPTION, purchaseDetail.getDescription())
				.set(PURCHASE_DETAIL.QUANTITY, purchaseDetail.getQuantity())
				.set(PURCHASE_DETAIL.PRICE, purchaseDetail.getPrice())
				.set(PURCHASE_DETAIL.DISCOUNT_EXPR, purchaseDetail.getDiscountExpression())
				.set(PURCHASE_DETAIL.TAXES, purchaseDetail.getTaxes())
				.set(PURCHASE_DETAIL.STATUS, purchaseDetail.getStatus().value())
				.set(PURCHASE_DETAIL.SOURCE, purchaseDetail.getSource() != null ? purchaseDetail.getSource().value() : null)
				.set(PURCHASE_DETAIL.SOURCE_ID, purchaseDetail.getSourceId())
				.set(PURCHASE_DETAIL.PROPOSAL_DETAIL, purchaseDetail.getProposalDetail())
				.set(PURCHASE_DETAIL.DELIVERED, purchaseDetail.getDelivered())
				.set(PURCHASE_DETAIL.DELIVERY_DATE, AonDateUtils.toSql(purchaseDetail.getDeliveryDate()))
				.set(PURCHASE_DETAIL.CREATION_USER, purchaseDetail.getCreationUser())
				.set(PURCHASE_DETAIL.CREATION_DATE, AonDateUtils.toTimestamp(purchaseDetail.getCreationDate()))
				.set(PURCHASE_DETAIL.MODIFICATION_USER, purchaseDetail.getModificationUser())
				.set(PURCHASE_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(purchaseDetail.getModificationDate()))
				.set(PURCHASE_DETAIL.CARRIER, purchaseDetail.getCarrier())
				.set(PURCHASE_DETAIL.CARRIER_PACKING, purchaseDetail.getCarrierPacking())
			.where(PURCHASE_DETAIL_PROPERTIES.getConditions(filter))
			.returning().fetch().stream().map(new PurchaseDetailFiller()).findFirst().orElse(new PurchaseDetail());
	}
	
	public static void deletePurchaseDetail(AONContext ctx, PurchaseDetailFilter filter) {
		ctx.getDslContext().delete(PURCHASE_DETAIL).where(PURCHASE_DETAIL_PROPERTIES.getConditions(filter)).execute();
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

	// -------------------- SUPPLIER
	
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
			purchase.setSupplierName(r.getValue(REGISTRY.NAME));
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
