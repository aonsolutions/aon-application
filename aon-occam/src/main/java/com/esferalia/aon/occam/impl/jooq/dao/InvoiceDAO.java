package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.InvoicingGroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceDAO {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static class InvoicePropertiesDAO implements InvoiceProperties {

		private Condition[] getConditions(InvoiceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<Integer>(INVOICE.DOMAIN);}
		@Override public Property<Date> getStartIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Date> getEndIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.TYPE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.WORKPLACE);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.SELLER);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PRODUCT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.ITEM);}
		@Override public Property<Integer> getProductCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.ID);}
			
	}
	
	private static final InvoicingGroupPropertiesDAO INVOICING_GROUP_PROPERTIES = new InvoicingGroupPropertiesDAO();
	private static class InvoicingGroupPropertiesDAO implements InvoicingGroupProperties {

		private Condition[] getConditions(InvoicingGroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(INVOICING_GROUP.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(INVOICING_GROUP.CREATION_USER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICING_GROUP.CUSTOMER);}
		@Override public Property<Byte> getCustomerGroupedProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICING_GROUP.CUSTOMER_GROUPED);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(INVOICING_GROUP.DESCRIPTION);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICING_GROUP.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICING_GROUP.ID);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(INVOICING_GROUP.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(INVOICING_GROUP.MODIFICATION_USER);}
	}

	private static final Registry SELLER_ALIAS = REGISTRY.as("seller");
	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();

		// Field para que salgan ordenado primero 
		// compras,gastos y gastos no .ded y luego ventas.
		// En la select se complementa con invoice.type
		Field<Integer> orderedType = DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);
		
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.TYPE
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.REGISTRY
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.SECURITY_LEVEL
				,GEOZONE.CODE
				,GEOZONE.NAME
				,RADDRESS.ZIP
				,RADDRESS.CITY
				,SCOPE.ID
				,SCOPE.DESCRIPTION
				,PROJECT.NAME
				,INVOICE_DETAIL.LINE
				
				,INVOICE_DETAIL.ITEM
				,PCATEGORY.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,ITEM.DETAIL
				,ITEM.DETAIL2
				,ITEM.DETAIL3
				,ITEM.DESCRIPTION
				,ITEM.PURCHASE_PRICE
				,ITEM.PRICE
				
				,INVOICE_DETAIL.DESCRIPTION
				,INVOICE_DETAIL.QUANTITY
				,INVOICE_DETAIL.PRICE
				,INVOICE_DETAIL.DISCOUNT_EXPR
				,INVOICE_DETAIL.TAXABLE_BASE
				,INVOICE_DETAIL.SELLER
				,SELLER_ALIAS.NAME
				,WORKPLACE.DESCRIPTION
				,WAREHOUSE.NAME
			)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID).and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(INVOICE_DETAIL.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
		
	}
	
	
	public static Stream<Invoice> getInvoiceHeaders(AONContext ctx,InvoiceFilter filter) {
		return getFullInvoices(ctx, filter)
			.stream()
			.map(new MinimalInvoiceFiller());
	}

	public static void getInvoicesFormatCSV(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatCSV(out,'\t',"");
	}

	public static void getInvoicesFormatHTML(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatHTML(out);
	}

	public static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
		return getFullInvoices(ctx, filter)
			.stream()
			.map(new FullInvoiceDetailFiller());
	}
	
	public static class MinimalInvoiceFiller  implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record record) {
			return new Invoice()
					.setId(record.getValue(INVOICE.ID))
					.setDomain(record.getValue(INVOICE.DOMAIN))
					.setType(AonEnumUtils.enumValue(InvoiceType.class,record.getValue(INVOICE.TYPE)))
					.setSeries(record.getValue(INVOICE.SERIES))
					.setNumber(record.getValue(INVOICE.NUMBER))
					.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE))
					.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(record.getValue(INVOICE.TAX_DATE))
					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,record.getValue(INVOICE.SECURITY_LEVEL)))
					.setRegistry(record.getValue(INVOICE.REGISTRY))
					.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setRegistryName(record.getValue(INVOICE.RNAME))
					.setScope(new Scope().setId(record.getValue(SCOPE.ID)))
				;
		}
		
	}
	
	
	
	
	
	private static class FullInvoiceDetailFiller  implements Function<Record,InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record record) {
			return new InvoiceDetail()
				.setInvoice(new Invoice()
					.setId(record.getValue(INVOICE.ID))
					.setDomain(record.getValue(INVOICE.DOMAIN))
					.setType(
							AonEnumUtils.enumValue(InvoiceType.class,
									record.getValue(INVOICE.TYPE)))
					.setSeries(record.getValue(INVOICE.SERIES))
					.setNumber(record.getValue(INVOICE.NUMBER))
					.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE))
					.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(record.getValue(INVOICE.TAX_DATE))
					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,
							record.getValue(INVOICE.SECURITY_LEVEL)))
					.setRegistry(record.getValue(INVOICE.REGISTRY))
					.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(
							AonEnumUtils.enumValue(DocumentType.class,
									record.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(
							Country.safeValueOf(record
									.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setRegistryName(record.getValue(INVOICE.RNAME))
					.setRegistryProvinceCode(record.getValue(GEOZONE.CODE))
					.setRegistryProvince(record.getValue(GEOZONE.NAME))
					.setRegistryTown(record.getValue(RADDRESS.CITY))
					.setRegistryZIP(record.getValue(RADDRESS.ZIP))
					.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				)
				.setProject( record.getValue( PROJECT.NAME ))
				.setLine(record.getValue( INVOICE_DETAIL.LINE ))
				.setDescription(record.getValue( INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(INVOICE_DETAIL.QUANTITY))
				.setPrice(record.getValue(INVOICE_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(record.getValue(INVOICE_DETAIL.TAXABLE_BASE))
				.setItem((record.getValue(INVOICE_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(INVOICE_DETAIL.ITEM))
						.setCategory( record.getValue( PCATEGORY.NAME ) )
						.setProductId( record.getValue( PRODUCT.ID ) )
						.setName( record.getValue( PRODUCT.NAME ) )
						.setCode(record.getValue( PRODUCT.CODE ) )
						.setDetail(record.getValue( ITEM.DETAIL ))
						.setDetail2(record.getValue( ITEM.DETAIL2 ))
						.setDetail3(record.getValue( ITEM.DETAIL3 ))
						.setPurchasePrice(record.getValue( ITEM.PURCHASE_PRICE ))
						.setPrice(record.getValue( ITEM.PRICE ))
						.setDescription(record.getValue( ITEM.DESCRIPTION )))
				.setSeller((record.getValue(INVOICE_DETAIL.SELLER) == null)
					? null
					: new Seller()
					.setId( record.getValue(INVOICE_DETAIL.SELLER) )
					.setRegistryName( record.getValue(SELLER_ALIAS.NAME) ))
				.setWorkPlace(record.getValue(WORKPLACE.DESCRIPTION))
				.setWarehouse(record.getValue(WAREHOUSE.NAME));
		}
		
	}
	
	public static InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId){
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId()))
				.and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.orderBy(INVOICE.ISSUE_DATE.desc())
				.limit(1).fetch().stream().map(new InvoiceDetailFiller())
				.findFirst().orElse(new InvoiceDetail());
	}
	
	public static InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date){
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
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
	
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate)))
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
								INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())
						.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate))))
				
				.orderBy(INVOICE.ISSUE_DATE.desc()
						,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate)))
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
								INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())
						.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
						.and(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql(startDate))))
						
				
				.orderBy(INVOICE.ISSUE_DATE.desc()
						,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);

		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
								INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull()))
				
				
				.orderBy(INVOICE.ISSUE_DATE.desc()
						,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date) {
		Condition workplaceCondition = INVOICE_DETAIL.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq(workplaceId);

		return ctx.getDslContext()
				.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
						INVOICE_DETAIL.QUANTITY)
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.where(INVOICE_DETAIL.ITEM.eq(item.getId())).and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(workplaceCondition)
				.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.union(ctx.getDslContext()
						.select(INVOICE.ISSUE_DATE, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.ID, INVOICE_DETAIL.DISCOUNT_EXPR,
								INVOICE_DETAIL.QUANTITY)
						.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
							.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(INVOICE_DETAIL.SOURCE_ID))
						.where(INVOICE_DETAIL.SOURCE.eq((byte) 4))
						.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
						.and(workplaceCondition)
						.and(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql(date)))
						.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull()))
				
				
				.orderBy(INVOICE.ISSUE_DATE.desc()
						,INVOICE_DETAIL.ID.desc())
				.fetch().stream().map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	
	public static LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter){
		return ctx.getDslContext().select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
				.from(INVOICING_GROUP).where(INVOICING_GROUP_PROPERTIES.getConditions(filter))
				.fetchInto(INVOICING_GROUP).stream().map(new FullInvoicingGroupFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate){
		Field<Integer> type = DSL.decode()
				   .when(INVOICE.TYPE.equal((byte) 0), 0)
				   .when(INVOICE.TYPE.equal((byte) 1), 1)
				   .when(INVOICE.TYPE.equal((byte) 2), 0)
				   .when(INVOICE.TYPE.equal((byte) 3), 0);
		AggregateFunction<Integer> min = DSL.min(INVOICE.NUMBER);
		AggregateFunction<Integer> max = DSL.max(INVOICE.NUMBER);
		LinkedList<InvoiceSeries> list = new LinkedList<InvoiceSeries>(); 
		ctx.getDslContext()
		.select(type,INVOICE.SERIES,min,max)
		.from(INVOICE)
		.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
		.and((INVOICE.ISSUE_DATE).between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)) )
		.groupBy(type,INVOICE.SERIES)
		.fetch()
		.stream()
		.forEach( rec -> list.add(
			new InvoiceSeries()
				.setSales(rec.getValue(type) == 1)
				.setSeriesInfo(true)
				.setDescription(rec.getValue(INVOICE.SERIES))
				.setFromNumber(rec.getValue(min))
				.setToNumber(rec.getValue(max)))
				);
		AggregateFunction<Integer> count = DSL.count();
		ctx.getDslContext()
			.select(type,INVOICE.TRANSACTION,count)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and((INVOICE.ISSUE_DATE).between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)) )
			.groupBy(type,INVOICE.TRANSACTION)
			.fetch()
			.stream()
			.forEach( rec -> list.add(
				new InvoiceSeries()
					.setSales(rec.getValue(type) == 1)
					.setSeriesInfo(false)
					.setDescription(InvoiceTransactionType.values()[rec.getValue(INVOICE.TRANSACTION)].getDescription())
					.setFromNumber(rec.getValue(count)))
					);
		return list;
	}
	
	private static class InvoiceDetailFiller implements Function<Record, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return new InvoiceDetail().setInvoice(new Invoice().setIssueDate(r.getValue(INVOICE.ISSUE_DATE)))
					.setPrice(r.getValue(INVOICE_DETAIL.PRICE)).setId(r.getValue(INVOICE_DETAIL.ID))
					.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR) != null
							? r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR) : "0.0")
					.setQuantity(
							r.getValue(INVOICE_DETAIL.QUANTITY) != null ? r.getValue(INVOICE_DETAIL.QUANTITY) : 0.0);
		}

	}
	
	private static class FullInvoicingGroupFiller implements Function<InvoicingGroupRecord, InvoicingGroup> {
		@Override
		public InvoicingGroup apply(InvoicingGroupRecord r) {
			return new InvoicingGroup()
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setCustomer(r.getCustomer())
					.setCustomerGrouped(r.getCustomerGrouped())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser());			
		}
	}
}
