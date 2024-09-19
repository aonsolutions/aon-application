package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.InvoiceFlat;
import com.esferalia.aon.occam.api.model.finance.InvoiceFlatExtended;
import com.esferalia.aon.occam.api.model.finance.InvoiceFlatFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceFlatProperties;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO.WarehouseFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceFlatDAO {
	private static final Field<?>[] INVOICE_FIELDS;
	static {
		INVOICE_FIELDS = AonCollectionUtils.stream(INVOICE.fields())
			.filter(f -> f != INVOICE.COMMENTS)
			.filter(f -> f != INVOICE.REMARKS)
			.toArray(Field<?>[]::new);
		 ;
	}
	
	
	private static final InvoiceFlatPropertiesDAO INVOICE_PROPERTIES = new InvoiceFlatPropertiesDAO();
	
	// Used in StatDAO
	public static class InvoiceFlatPropertiesDAO implements InvoiceFlatProperties {
		
		private static final long serialVersionUID = 5739904988666744000L;
		
//		private Select<Record> build(SelectJoinStep<Record> select, InvoiceFlatFilter filter) {
//			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
//			return filterDAO.build(select);
//		}
		
		public Condition[] getConditions(InvoiceFlatFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.DOMAIN);}
		@Override public Property<Integer> getActivityProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.ACTIVITY);}
		@Override public Property<Integer> getRegistryProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<String> getRegistryDocumentProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RDOCUMENT);}
		@Override public Property<String> getRegistryNameProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RNAME);}
		@Override public Property<java.util.Date> getIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<Byte> getRectificationTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Integer> getRectificationInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_INVOICE);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WORKPLACE);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SELLER);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PRODUCT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ITEM);}
		@Override public Property<Integer> getProductCategoryProperty() {return new FilterDAO.PropertyDAO<>(PCATEGORY.ID);}
		@Override public Property<Integer> getProductBrandProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.BRAND);}
		@Override public Property<String> getProductCodeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CODE);}	
		@Override public Property<Byte> getProductTypeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.TYPE);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TRANSACTION);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.INVESTMENT);}
		@Override public Property<java.util.Date> getTaxDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.TAX_DATE);}
		@Override public Property<Integer> getPosShiftroperty() {return new FilterDAO.PropertyDAO<>(INVOICE.POS_SHIFT);}
		@Override public Property<Byte> getVatAccrualPayment() {return new FilterDAO.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.WITHHOLDING);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERIES);}
 		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.NUMBER);}
 		@Override public Property<String> getReferenceCodeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.REFERENCE_CODE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_USER);}
		@Override public Property<Double> getTotalProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TOTAL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.STATUS);}
	}
	
	private InvoiceFlatDAO() {

	}
	
	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFlatFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(INVOICE_FIELDS)
			.select(InvoiceDAO.ORDERED_TYPE)
			.select(SCOPE.fields())
			.select(PROJECT.fields())
			.select(INVEST_ASSET.fields())
			.select(IAE.fields())
			.select(RADDRESS.fields())
			.select(GEOZONE.fields())
			.select(INVOICE_DETAIL.fields())
			.select(ITEM.fields())
			.select(PRODUCT.fields())
			.select(BRAND.fields())
			.select(PCATEGORY.fields())
			.select(SellerDAO.SELLER_ALIAS.fields())
			.select(WORKPLACE.fields())
			.select(WAREHOUSE.fields())
			
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID).and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(INVOICE_DETAIL.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(BRAND).on(PRODUCT.BRAND.equal(BRAND.ID))
			.leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
			.leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.equal(INVOICE_DETAIL.INVEST_ASSET))
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(InvoiceDAO.ORDERED_TYPE,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
	}
	
	public static Stream<InvoiceFlat> getInvoiceFlats(AONContext ctx, InvoiceFlatFilter filter) {
		return getFullInvoices(ctx, filter)
			.stream()
			.map(new InvoiceFlatFiller());
	}
	
	public static Stream<InvoiceFlatExtended> getInvoiceFlatsExtended(AONContext ctx, InvoiceFlatFilter filter, IDAOCallback callback) {
		return getFullInvoices(ctx, filter)
			.stream()
			.onClose(() -> { if (callback != null) callback.onFinish();})
			.map(new InvoiceFlatFiller())
			.map( d -> new InvoiceFlatExtended()
				.setDetail(d)
				.setSegments(
					RegistryDAO.getRegistrySegmentNames(ctx, d.getInvoice().getRegistry())
						.collect(Collectors.toCollection(LinkedList::new)
					)
				)
				.setSellerSupport(
					RegistryDAO.getRegistrySellerNames(ctx, d.getInvoice().getRegistry(), d.getInvoice().getIssueDate())
						.collect(Collectors.joining(", ")))
			);
	}

	private static class InvoiceFlatFiller extends Filler implements Function<Record,InvoiceFlat> {

		@Override
		public InvoiceFlat apply(Record r) {
			return new InvoiceFlat()
				.setId(getValue(r,INVOICE_DETAIL.ID))
				.setInvoice( InvoiceFiller.build(r))
				.setGeozoneCode(getValue(r,GEOZONE.CODE))
				.setGeozoneName(getValue(r,GEOZONE.NAME))
				.setCity(getValue(r,RADDRESS.CITY))
				.setZip(getValue(r,RADDRESS.ZIP))
				.setScope(getValue(r,SCOPE.ID))
				.setScopeName(getValue(r,SCOPE.DESCRIPTION))
				.setInvestAsset(getOpt(r, INVEST_ASSET.ID).map(i -> InvestAssetFiller.build(r)).orElse(null))
				.setProject( getValue( r, INVOICE_DETAIL.PROJECT ))
				.setProjectName( getValue( r, PROJECT.NAME ))
				.setLine(getValue(r, INVOICE_DETAIL.LINE ))
				.setDescription(getValue( r, INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(getValue(r, INVOICE_DETAIL.QUANTITY))
				.setPrice(getValue(r, INVOICE_DETAIL.PRICE))
				.setDiscountExpression(getValue(r, INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(getValue(r, INVOICE_DETAIL.TAXABLE_BASE))
				.setItem(getOpt(r, ITEM.ID).map(i -> ItemFiller.build(r)).orElse(null))
				.setSeller( getOpt(r, SELLER.REGISTRY).map(i -> SellerFiller.build(r, SellerDAO.SELLER_ALIAS)).orElse(null))
				.setWorkplace(getOpt(r, WORKPLACE.ID).map(w -> WorkplaceFiller.build(r)).orElse(null))
				.setWarehouse(getOpt(r, WAREHOUSE.ID).map(w -> WarehouseFiller.build(r)).orElse(null))
				.setSource(InvoiceSource.safeValueOf(r.getValue(INVOICE_DETAIL.SOURCE)))
				.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID))
				.setPrepayment(getBoolean(r, INVOICE_DETAIL.PREPAYMENT))
				;
		}
		
	}

}




