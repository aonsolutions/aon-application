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

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PurchaseDetailRecord;
import com.esferalia.aon.jooq.tables.records.PurchaseRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailProperties;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseType;


public class PurchaseDAO {
	
	private static final PurchaseDetailPropertiesDAO PURCHASE_DETAIL_PROPERTIES = new PurchaseDetailPropertiesDAO();
	
	protected static class PurchaseDetailPropertiesDAO implements PurchaseDetailProperties {
		protected Condition[] getConditions(PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.DOMAIN);}
		@Override public Property<Integer> getPurchaseProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PURCHASE);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PROJECT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<Short>(PURCHASE_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<String>(PURCHASE_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE_DETAIL.STATUS);}
		@Override public Property<Integer> getProposalDetailProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.PROPOSAL_DETAIL);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(PURCHASE_DETAIL.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PURCHASE_DETAIL.SOURCE_ID);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<Double>(PURCHASE_DETAIL.DELIVERED);}
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
	

	
	private static class FullPurchaseFiller implements Function<PurchaseRecord, Purchase> {
		@Override
		public Purchase apply(PurchaseRecord r) {
			Purchase purchase = new Purchase();
			purchase.setId(r.getId());
			purchase.setDomain(r.getDomain());
			purchase.setProject(r.getProject());
			purchase.setSupplier(r.getSupplier());
			purchase.setSeries(r.getSeries());
			purchase.setNumber(r.getNumber());
			purchase.setPurchaseReference(r.getPurchaseReference());
			purchase.setAddress(r.getAddress());
			purchase.setDiscountExpr(r.getDiscountExpr());
			purchase.setIssueDate(r.getIssueDate());
			purchase.setPayMethod(r.getPayMethod());
			purchase.setDocumentType(PurchaseType.values()[r.getDocumentType()]);
			purchase.setSecurityLevel(r.getSecurityLevel());
			purchase.setStatus(PurchaseStatus.values()[r.getStatus()]);
			purchase.setComments(r.getComments());
			purchase.setRemarks(r.getRemarks());
			purchase.setWorkplace(r.getWorkplace());
			purchase.setWarehouse(r.getWarehouse());
			purchase.setScope(r.getScope());
			purchase.setNumberOfPymnts(r.getNumberOfPymnts());
			purchase.setDaysToFirstPymnt(r.getDaysToFirstPymnt());
			purchase.setDaysBetweenPymnts(r.getDaysBetweenPymnts());
			purchase.setPymntDays(r.getPymntDays());
			purchase.setBankAccount(r.getBankAccount());
			purchase.setBankAlias(r.getBankAlias());
			purchase.setBic(r.getBic());
			purchase.setEmailCommunication(r.getEmailCommunication()==1);
			purchase.setCarrier(r.getCarrier());
			purchase.setShippingAlternativeAddress(r.getShippingAlternativeAddress());
			purchase.setShippingAlternativeAddress2(r.getShippingAlternativeAddress2());
			purchase.setShippingAlternativeZip(r.getShippingAlternativeZip());
			purchase.setShippingAlternativeCity(r.getShippingAlternativeCity());
			purchase.setShippingAlternativePhone(r.getShippingAlternativePhone());
			purchase.setShippingAlternativeRecipient(r.getShippingAlternativeRecipient());
			purchase.setShippingContact(r.getShippingContact());
			purchase.setShippingPeriod(r.getShippingPeriod()!=null?r.getShippingPeriod().intValue():null);
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
