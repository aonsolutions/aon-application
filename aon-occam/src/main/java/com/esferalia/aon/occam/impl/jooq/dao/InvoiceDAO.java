package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
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
import java.text.MessageFormat;
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
import com.esferalia.aon.jooq.tables.records.InvoiceDetailRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceTaxRecord;
import com.esferalia.aon.jooq.tables.records.InvoicingGroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDAO {
	
	private static String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy} ";
	static final Date VAT_ACCRUAL_START_DATE = AonDateUtils.getDate(2014, 0, 1);
	
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
		@Override public Property<Integer> getRegistryProperty(){return new FilterDAO.PropertyDAO<Integer>(INVOICE.REGISTRY);}
		@Override public Property<Date> getStartIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Date> getEndIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.TYPE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<Byte> getRectificationTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Integer> getRectificationInvoiceProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE.RECTIFICATION_INVOICE);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.WORKPLACE);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.SELLER);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PRODUCT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_DETAIL.ITEM);}
		@Override public Property<Integer> getProductCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.ID);}
		@Override public Property<String> getProductCodeProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CODE);}	
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
	
	public static Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter){
		return ctx.getDslContext()
				.select()
				.from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE))
				.where(INVOICE_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new FullInvoiceFiller());
	}
	
	public static Invoice getInvoice(AONContext ctx, Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
				.where(INVOICE.ID.eq(id))
				.fetch()
				.stream()
				.map( new FullInvoiceFiller() )
				.findFirst()
				.orElse(null);
	}
	
	private static Result<Record> getBoughtProductInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
				.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
				.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.where(INVOICE_DETAIL.ID.in(
					ctx.getDslContext()
					.select(DSL.max(INVOICE_DETAIL.ID))
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
					.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
					.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
					.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
					.where(INVOICE_PROPERTIES.getConditions(filter))
					.groupBy(PRODUCT.CODE)
					.orderBy(INVOICE.ISSUE_DATE.desc())
			))	
			.fetch();
	}
	
	private static Result<Record> getOldBoughtProductInvoices(AONContext ctx, Integer id, InvoiceFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.and(INVOICE_DETAIL.ID.ne(id))
			.orderBy(INVOICE.ISSUE_DATE.desc())
			.fetch();
	}
	
	
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
				,INVOICE.ACTIVITY
				,IAE.EPIGRAPH
				,INVOICE.INVEST_ASSET
				,INVOICE.PROJECT
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
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.RECTIFICATION_INVOICE
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
				,INVOICE_DETAIL.PROJECT
				,INVOICE_DETAIL.WAREHOUSE
				,INVOICE_DETAIL.WORKPLACE
				,SELLER_ALIAS.NAME
				,WORKPLACE.DESCRIPTION
				,WAREHOUSE.NAME
				,SCOPE.DESCRIPTION
			)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.equal(INVOICE.INVEST_ASSET))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID).and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
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
	
	public static Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter) {
		return getBoughtProductInvoices(ctx, filter)
			.stream()
			.map(new BoughtProductInvoiceDetailFiller());
	}
	
	public static Stream<InvoiceDetail> getOldBoughtProductStream(AONContext ctx, Integer id, InvoiceFilter filter) {
		return getOldBoughtProductInvoices(ctx, id, filter)
			.stream()
			.map(new BoughtProductInvoiceDetailFiller());
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
					.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				;
		}
		
	}
	
	public static class FullInvoiceFiller  implements Function<Record,Invoice> {

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
				.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				.setActivity(record.getValue(INVOICE.ACTIVITY))	
				.setActivity(record.getValue(INVOICE.INVEST_ASSET))
				.setActivity(record.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,record.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(record.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,record.getValue(INVOICE.TRANSACTION)))
				.setRecorded(record.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(record.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(record.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(record.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(record.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(record.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(record.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(record.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(record.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(record.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(record.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(record.getValue(INVOICE.TOTAL))	
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
					.setAddressProvinceCode(record.getValue(GEOZONE.CODE))
					.setAddressProvince(record.getValue(GEOZONE.NAME))
					.setAddressTown(record.getValue(RADDRESS.CITY))
					.setAddressZIP(record.getValue(RADDRESS.ZIP))
					.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				)
				.setProject( record.getValue( INVOICE_DETAIL.PROJECT ))
				.setProjectName( record.getValue( PROJECT.NAME ))
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
				.setWorkPlace(record.getValue(INVOICE_DETAIL.WORKPLACE))
				.setWorkPlaceName(record.getValue(WORKPLACE.DESCRIPTION))
				.setWarehouse(record.getValue(INVOICE_DETAIL.WAREHOUSE))
				.setWarehouseName(record.getValue(WAREHOUSE.NAME));
		}
		
	}
	
	private static class BoughtProductInvoiceDetailFiller  implements Function<Record,InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record record) {
			return new InvoiceDetail()
				.setId(record.getValue(INVOICE_DETAIL.ID))
				.setInvoice(new Invoice()
					.setId(record.getValue(INVOICE.ID))
					.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
					.setRegistry(record.getValue(INVOICE.REGISTRY)))
				.setProject( record.getValue( INVOICE_DETAIL.PROJECT ))
				.setItem((record.getValue(INVOICE_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(INVOICE_DETAIL.ITEM))
						.setCode(record.getValue( PRODUCT.CODE )))	
				.setLine(record.getValue( INVOICE_DETAIL.LINE ))
				.setDescription(record.getValue( INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(INVOICE_DETAIL.QUANTITY))
				.setPrice(record.getValue(INVOICE_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(record.getValue(INVOICE_DETAIL.TAXABLE_BASE))
				;
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

	public static int getNextNumber(AONContext ctx, Byte[] types, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(INVOICE.NUMBER))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in(types))
			.and( AonStringUtils.isBlank(series)
					?INVOICE.SERIES.isNull()
					:INVOICE.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.max(INVOICE.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		return ++next;
	}
	
	public static Invoice insert(AONContext ctx, Invoice invoice) {
		return insert(ctx,ConfigurationDAO.getConfiguration(ctx, invoice.getIssueDate()),invoice); 
	}
	
	public static Invoice insert(AONContext ctx, AonConfiguration config, Invoice invoice) {
		ctx.checkWrite();
		InvoiceValidation.validateInvoice(ctx, config, invoice);
		InvoiceAutoComplete.completeInvoice(ctx, config, invoice);
		InvoiceRecord record = ctx.getDslContext()
			.insertInto(INVOICE)
			.set(INVOICE.DOMAIN, invoice.getDomain() )
			.set(INVOICE.ACTIVITY, invoice.getActivity() )
			.set(INVOICE.INVEST_ASSET, invoice.getInvestAsset() )
			.set(INVOICE.PROJECT, invoice.getProject() )
			.set(INVOICE.SERIES, invoice.getSeries() )
			.set(INVOICE.NUMBER, invoice.getNumber() )
			.set(INVOICE.REFERENCE_CODE, invoice.getReferenceCode() )
			.set(INVOICE.REGISTRY, invoice.getRegistry() )
			.set(INVOICE.RDOCUMENT, invoice.getRegistryDocument() )
			.set(INVOICE.RDOCUMENT_TYPE, AonEnumUtils.getByte( invoice.getRegistryDocumentType()) )
			.set(INVOICE.RDOCUMENT_COUNTRY, Country.safeIso2( invoice.getRegistryDocumentCountry()))
			.set(INVOICE.RNAME, invoice.getRegistryName() )
			.set(INVOICE.RADDRESS, invoice.getRegistryAddress() )
			.set(INVOICE.ISSUE_DATE, AonDateUtils.toSql( invoice.getIssueDate()) )
			.set(INVOICE.TAX_DATE, AonDateUtils.toSql(invoice.getTaxDate()) )
			.set(INVOICE.SECURITY_LEVEL, AonEnumUtils.getByte( invoice.isConfidential() ) )
			.set(INVOICE.STATUS, AonEnumUtils.getByte( invoice.isRecorded() ) )
			.set(INVOICE.TYPE, AonEnumUtils.getByte( invoice.getType() ) )
			.set(INVOICE.SURCHARGE, AonEnumUtils.getByte( invoice.isSurcharge() ))
			.set(INVOICE.WITHHOLDING, AonEnumUtils.getByte( invoice.isWithholding() ))
			.set(INVOICE.WITHHOLDING_FARMER, AonEnumUtils.getByte( invoice.isWithholdingFarmer() ))
			.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invoice.isVatAccrualPayment() ))
			.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( invoice.isInvestment() ) )
			.set(INVOICE.TRANSACTION, AonEnumUtils.getByte( invoice.getTransaction() ) )
			.set(INVOICE.SCOPE, invoice.getScope().getId() )
			.set(INVOICE.SERVICE, AonEnumUtils.getByte(  invoice.isService() ) )
			.set(INVOICE.RECTIFICATION_TYPE, AonEnumUtils.getByte( invoice.getRectificationType()) )
			.set(INVOICE.RECTIFICATION_INVOICE, invoice.getRectificationInvoice() )
			.set(INVOICE.ADVANCE, AonEnumUtils.getByte( invoice.isAdvance()) )
			.set(INVOICE.SIGNED, AonEnumUtils.getByte( invoice.isSigned()) )
			.set(INVOICE.TAXABLE_BASE, invoice.getTaxableBase() )
			.set(INVOICE.VAT_QUOTA, invoice.getVatQuota() )
			.set(INVOICE.RETENTION_QUOTA, invoice.getRetentionQuota() )
			.set(INVOICE.TOTAL, invoice.getTotal() )
			.set(INVOICE.POS_SHIFT, invoice.getPosShift() )
			.set(INVOICE.SELLER, invoice.getSeller() )
			.set(INVOICE.COMMENTS, invoice.getComments() )
			.set(INVOICE.REMARKS, invoice.getRemarks() )
			.set(INVOICE.CREATION_USER, invoice.getCreationUser() )
			.set(INVOICE.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(INVOICE.ID)
			.fetchOne();
		invoice.setId(record.getValue(INVOICE.ID));
		ctx.log().info("INSERT INVOICE invoice: " + invoice.getId());
		insertDetails(ctx, config, invoice);
		return invoice; 
	}
	
	private static void insertDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
		for (InvoiceDetail detail : invoice.getDetails()) {
			InvoiceValidation.validateDetail(ctx, config, detail);
			InvoiceDetailRecord record = ctx.getDslContext()
				.insertInto(INVOICE_DETAIL)
				.set(INVOICE_DETAIL.DOMAIN,invoice.getDomain())
				.set(INVOICE_DETAIL.INVOICE,invoice.getId())
				.set(INVOICE_DETAIL.INVEST_ASSET,detail.getInvestAsset())
				.set(INVOICE_DETAIL.PROJECT,detail.getProject())
				.set(INVOICE_DETAIL.LINE,detail.getLine())
				.set(INVOICE_DETAIL.ITEM,detail.getItem()==null?null : detail.getItem().getId())
				.set(INVOICE_DETAIL.DESCRIPTION,
						(AonStringUtils.isBlank(detail.getDescription()) && detail.getSource() == InvoiceSource.ACCOUNT)
							?MessageFormat.format(DETAIL_MSG, invoice.getReferenceCode(), invoice.getIssueDate())
							:detail.getDescription())
				.set(INVOICE_DETAIL.QUANTITY,detail.getQuantity())
				.set(INVOICE_DETAIL.PRICE,detail.getPrice())
				.set(INVOICE_DETAIL.DISCOUNT_EXPR,detail.getDiscountExpression())
				.set(INVOICE_DETAIL.SOURCE,detail.getSource().value() )
				.set(INVOICE_DETAIL.SOURCE_ID,detail.getSourceId())
				.set(INVOICE_DETAIL.TAXABLE_BASE,detail.getTaxableBase())
				.set(INVOICE_DETAIL.TAXES,detail.getTaxes())
				.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte( detail.isPrepayment() )) 
				.set(INVOICE_DETAIL.SELLER,detail.getSeller() == null ? null : detail.getSeller().getId() )
				.set(INVOICE_DETAIL.WORKPLACE,detail.getWorkPlace() )
				.set(INVOICE_DETAIL.WAREHOUSE,detail.getWarehouse())
				.set(INVOICE_DETAIL.CREATION_USER,ctx.getUser())
				.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(INVOICE_DETAIL.ID)
				.fetchOne();
			detail.setId(record.getValue(INVOICE_DETAIL.ID));
			ctx.log().info("\tINSERT INVOICE_DETAIL detalles invoice: " + detail.getId());
			insertInvoiceTaxes(ctx,detail);
		}
	}
	
	private static void insertInvoiceTaxes(AONContext ctx, InvoiceDetail detail) {
		for (InvoiceTax tax : detail.getInvoiceTaxes()) {
			InvoiceTaxRecord record =  ctx.getDslContext()
				.insertInto(INVOICE_TAX)
				.set(INVOICE_TAX.DOMAIN,detail.getDomain())
				.set(INVOICE_TAX.INVOICE_DETAIL,detail.getId())
				.set(INVOICE_TAX.TAX_TYPE, tax.getTaxType().value())
				.set(INVOICE_TAX.BASE,tax.getBase())
				.set(INVOICE_TAX.PERCENTAGE,tax.getPercentage())
				.set(INVOICE_TAX.QUOTA,tax.getQuota())
				.set(INVOICE_TAX.SURCHARGE,tax.getSurcharge())
				.set(INVOICE_TAX.SURCHARGE_QUOTA,tax.getSurchargeQuota())
				.set(INVOICE_TAX.VAT_DEDUCTION_TYPE,tax.getVatDeductionType() == null
						? VatDeductionType.WITH_RIGHT.value() 
						: tax.getVatDeductionType().value())
				.set(INVOICE_TAX.WITHHOLDING_TYPE,tax.getWithholdingType() == null 
						? WithholdingType.PROFESSIONAL.value() 
						: tax.getWithholdingType().value())
				.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,tax.getDeductiblePercent())
				.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,tax.getDeductibleQuota())
				.returning(INVOICE_TAX.ID)
				.fetchOne();
			tax.setId(record.getValue(INVOICE_TAX.ID));
			ctx.log().info("\t\tINSERT INVOICE_TAX tax: " + tax.getTaxType());
		}
	}
	
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();

		Invoice inv = getInvoice(ctx, id);
		if (inv == null) throw new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage());
		
		InvoiceValidation.validateInvoiceDeletion(ctx, null, inv);
		
		if (inv.isRectifier()) {
			if (inv.getRectificationInvoice() != null) {
				final Invoice rectified = getInvoice(ctx, inv.getRectificationInvoice());
				if (rectified == null) throw new AonCoreException(AonError.INVOICE_RECTIFIED_NOT_FOUND.getMessage());
				System.out.println("rectified.getRectificationInvoice() ..: " + rectified.getRectificationInvoice());
				System.out.println("inv.getId() ..........................: " + rectified.getRectificationInvoice());
				if (rectified.getRectificationInvoice() != null &&
						AonNumberUtils.equals(inv.getId(), rectified.getRectificationInvoice())) {
					// La factura rectificada, solo lo esta una vez, y es por la factura que estamos borrando.
					// Luego marcamos la factura rectificada como "NO RECTIFICADA".
					ctx.getDslContext().update(INVOICE)
						.set(INVOICE.RECTIFICATION_TYPE, RectificationType.NONE.value())
						.set(INVOICE.RECTIFICATION_INVOICE, (Integer) null)
						.set(INVOICE.MODIFICATION_USER,ctx.getUser())
						.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.where(INVOICE.ID.equal( rectified.getId() ))
						.execute();
					ctx.log().info("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - SOLO UNA): " + rectified.getId());
				} else {
					// En la factura rectificada no hay constancia de cual es la factura que la 
					// rectifica, por lo tanto puede haber mas de una.
					
					rectified.setRectificationType(RectificationType.NONE);  // Si no entra en el buble, no quedan facturas rectificativas.
					
					getInvoiceStream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId())
												.and(p.getRectificationInvoiceProperty().eq(inv.getRectificationInvoice()))
												.and(p.getIdProperty().ne(inv.getId()))
												)
					.forEach( rectifier -> {
						if (rectified.getRectificationInvoice() == null) {
							// Primera iteracion.
							rectified.setRectificationType(RectificationType.RECTIFIED);							
							rectified.setRectificationInvoice(rectifier.getId());
						} else {
							// Segunda iteracion. Hay mas de una, debe continuar a null.
							rectified.setRectificationInvoice(null);
						}
					});
					
					ctx.getDslContext().update(INVOICE)
						.set(INVOICE.RECTIFICATION_TYPE, rectified.getRectificationType().value())
						.set(INVOICE.RECTIFICATION_INVOICE, rectified.getRectificationInvoice())
						.set(INVOICE.MODIFICATION_USER,ctx.getUser())
						.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.where(INVOICE.ID.equal( rectified.getId() ))
						.execute();
				ctx.log().info("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - MAS DE UNA): " + rectified.getId());
					
				}
			}
		}
		
		int count = ctx.getDslContext()
			.delete(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.in( 
				ctx.getDslContext().select(INVOICE_DETAIL.ID)
					.from(INVOICE_DETAIL)
					.where(INVOICE_DETAIL.INVOICE.equal(id)) ))
			.execute();
		ctx.log().info("DELETE INVOICE_DETAIL detalles de la factura: " + id + " ("+count+" filas)");
		// Se borran las lineas
		count = ctx.getDslContext()
			.delete(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.INVOICE.equal(id))
			.execute();
		ctx.log().info("DELETE INVOICE_DETAIL detalles de la factura: " + id + " ("+count+" filas)");
		// Se borra la cabecera
		ctx.getDslContext()
			.delete(INVOICE)
			.where(INVOICE.ID.equal(id))
			.execute();
		ctx.log().info("DELETE INVOICE factura: " + id);
	}

	
	public static Invoice rectify(AONContext ctx, Integer invoiceId, InvoiceRectificationData data)  {
		Invoice inv = getInvoice(ctx, invoiceId);
		if (inv == null) {
			throw new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage());
		}
		if (data == null || data.getRectificationtype() == null) {
			throw new AonCoreException(AonError.INVOICE_INVALID_RECTIFICATION_DATA.getMessage());
		}
		if (data.getRectificationtype() != RectificationType.SPECIAL_RECTIFIER) {
			throw new AonCoreException("Las facturas rectificativas especiales, no se encuentran implementadas.");
		}
		if (data.getRectificationtype() != RectificationType.NORMAL_RECTIFIER) {
			throw new AonCoreException(AonError.INVOICE_INVALID_RECTIFICATION_TYPE
					.format(data.getRectificationtype().getDescription()));
		}
		
		RectificationType oldRectificationType = inv.getRectificationType();
		mergeRecitificationData(inv,data);
		rectifyInvoiceDetails(inv);
		inv = InvoiceDAO.insert(ctx, inv);
		LinkedList<Finance> finances = FinanceDAO.getInvoiceFinances(ctx, invoiceId);
		rectifyInvoiceFinances(ctx,finances, inv, data);
		rectifyInvoiceUpdate(ctx,invoiceId, inv.getId(), oldRectificationType); 
		return inv;
	}
	
	public static void mergeRecitificationData(Invoice inv, InvoiceRectificationData data) {
		Integer invoiceId = inv.getId();
		inv.setId(null);
		inv.setSeries(data.getSeries());
		inv.setNumber(data.getNumber());
		inv.setComments((AonStringUtils.isBlank(inv.getComments())
			?""
			:(inv.getComments() + " "))
			+ data.getCause());
		inv.setIssueDate(data.getIssueDate());
		inv.setTaxDate(data.getIssueDate());
		inv.setRectificationType(data.getRectificationtype());
		inv.setRectificationInvoice(invoiceId);
		inv.setRecorded(false);
		inv.setTaxableBase( AonMathUtils.round(inv.getTaxableBase() * (-1)));
		inv.setVatQuota(AonMathUtils.round(inv.getVatQuota() * (-1)));
		inv.setRetentionQuota(AonMathUtils.round(inv.getRetentionQuota() * (-1)));
		inv.setTotal(AonMathUtils.round(inv.getTotal() * (-1)));
	}

	public static void rectifyInvoiceDetails(Invoice inv) {
		for (InvoiceDetail detail : inv.getDetails()) {
			detail.setId(null);
			detail.setQuantity( AonMathUtils.round(detail.getQuantity() * (-1),3));	
			detail.setSource((detail.getSource() == InvoiceSource.RESERVATION) ? detail.getSource() : InvoiceSource.DIRECT_INVOICE);
			detail.setSourceId((detail.getSource() == InvoiceSource.RESERVATION) ? detail.getSourceId() : null);
			detail.setTaxableBase(AonMathUtils.round(detail.getTaxableBase() * (-1), 4));
			
			for (InvoiceTax tax : detail.getInvoiceTaxes()) {
				tax.setId(null);
				tax.setBase( AonMathUtils.round(tax.getBase() * (-1),4));
				tax.setQuota(AonMathUtils.round(tax.getQuota() * (-1)));
				tax.setSurchargeQuota(AonMathUtils.round(tax.getSurchargeQuota() * (-1)));
				tax.setDeductibleQuota(AonMathUtils.round(tax.getDeductibleQuota() * (-1)));
			}
		}
	}
	
	public static void rectifyInvoiceUpdate(AONContext ctx, Integer invoiceId, Integer rectifierInvoice, RectificationType oldRectificationType) {
		ctx.getDslContext().update(INVOICE)
			.set(INVOICE.RECTIFICATION_TYPE, RectificationType.RECTIFIED.value())
			.set(INVOICE.RECTIFICATION_INVOICE, (oldRectificationType == null || oldRectificationType == RectificationType.NONE)
				?rectifierInvoice
				:null)
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( invoiceId))
			.execute();
	}
	
	private static void rectifyInvoiceFinances(AONContext ctx, LinkedList<Finance> finances, Invoice newInvoice,
			InvoiceRectificationData data) {
		for (Finance finance : finances) {
			Integer oldId = finance.getId();
			
			finance.setAmount(AonMathUtils.round(finance.getAmount() * (-1)));
			finance.setFinanceStatus(FinanceStatus.PENDING);
			finance.setInvoice(newInvoice);
			finance.setId( null );
			Integer financeId = FinanceDAO.insert(ctx, finance);
			
			if (data.isSettleFinances() && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				FinanceDAO.settle(ctx, oldId    ,finance.getAmount());
				FinanceDAO.settle(ctx, financeId,finance.getAmount());
			}
			
		}
	}
}
