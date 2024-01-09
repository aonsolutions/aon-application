package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.InvoiceDetailRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceTaxRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceSource.IInvoiceSourceVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.EnterpriseActivityFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.offer.OfferDetailDAO;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDAO {
	
	private InvoiceDAO() {

	}
	
	private static final String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy}. ";
	static final Date VAT_ACCRUAL_START_DATE = AonDateUtils.getDate(2014, 0, 1);
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();
	
	private static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	private static class RegistryPropertiesDAO implements RegistryProperties {
		private Condition[] getConditions(RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
	}

	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static final InvoicingGroupPropertiesDAO INVOICING_GROUP_PROPERTIES = new InvoicingGroupPropertiesDAO();
	private static class InvoicingGroupPropertiesDAO implements InvoicingGroupProperties {

		private Condition[] getConditions(InvoicingGroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CREATION_USER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CUSTOMER);}
		@Override public Property<Byte> getCustomerGroupedProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CUSTOMER_GROUPED);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.DESCRIPTION);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.ID);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.MODIFICATION_USER);}
	}

	private static final Registry SELLER_ALIAS = REGISTRY.as("seller");
	public static final com.esferalia.aon.jooq.tables.Seller SELLER_SUPPORT = SELLER.as("seller_support");
    public static final com.esferalia.aon.jooq.tables.Registry SELLER_SUPPORT_ALIAS = REGISTRY.as("registry_support_seller");
	
	
	public static Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter){
		return INVOICE_PROPERTIES.build(ctx.getDslContext().select().from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE)), filter)
				.fetch().stream().map(new InvoiceFiller());
	}
	
	public static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter, ItemFilter iFilter){
		ctx.checkRead();
		
		Collection<Condition> whereConditions = new ArrayList<>();
		whereConditions.addAll(Arrays.asList(INVOICE_PROPERTIES.getConditions(filter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(pFilter)));
		whereConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(iFilter)));

		return ctx.getDslContext()
			.select()
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.where(whereConditions)
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE_DETAIL.LINE)
			.fetch().stream().map(new InvoiceDetailFiller());
	}
	
	public static Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter,Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii){
		Boolean intracomunitaria = "intracomunitarias".equals(sii);
		Boolean bienes = "bienes".equals(sii);
		Boolean cp = "cp_cobros_pagos".equals(sii) || "cp_cobros".equals(sii) || "cp_pagos".equals(sii);
		
		if(pending && !intracomunitaria && !cp && !bienes
				&& !aceptada && !aceptadaErrores && !incorrecta && !anulada){
			FilterDAO d = (FilterDAO) filter.filter(INVOICE_PROPERTIES);			
			return ctx.getDslContext().select()
				.from(INVOICE)
				.leftOuterJoin(DATA_RESPONSE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID)
						.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value())))
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.and(DATA_RESPONSE.SOURCE_ID.isNull())
				.limit(d.getPerPage())
				.offset(d.getPerPage() * (d.getPage() -1))
				.fetch().stream().map(new SiiInvoiceFiller(true));
		} else {
			Condition c = DATA_RESPONSE_DETAIL.DATA_VALUE.eq(""); 
			if(pending) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Pendiente"));
			if(aceptada) c = cp ? c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Parcial")) : c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Correcto"));
			if(aceptadaErrores) c = cp ? c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Pagado")) : c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("AceptadoConErrores"));
			if(incorrecta) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Incorrecto"));
			if(anulada) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Anulada"));
			String status = "status";
			if(intracomunitaria)status =  "status_intra";
			else if(cp) status = "status_cp";
			else if(bienes) status = "status_bienes";
			return INVOICE_PROPERTIES.build(ctx.getDslContext().select().from(INVOICE)
					.join(DATA_RESPONSE).on(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()).and(DATA_RESPONSE.SOURCE_ID.eq(INVOICE.ID)))
					.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq(status).and(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE.ID))
						.and(c))
				, filter)
				.fetch().stream().map(new SiiInvoiceFiller(false));
		}
	}
	
	public static Invoice getInvoice(AONContext ctx, Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
				.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
				.where(INVOICE.DOMAIN.eq(ctx.getDomainId()).and(INVOICE.ID.eq(id)))
				.fetch()
				.stream()
				.map( new InvoiceFiller() )
				.findFirst()
				.orElse(null);
	}

	private static Result<Record14<Integer, java.sql.Date, Integer, String, Integer, Integer, String, String, Short, String, Double, Double, String, Double>> getBoughtProductInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select(INVOICE.ID, DSL.max(INVOICE.ISSUE_DATE), INVOICE.REGISTRY, INVOICE.REFERENCE_CODE
					, INVOICE_DETAIL.PROJECT, INVOICE_DETAIL.ITEM, PRODUCT.CODE, PRODUCT.NAME, INVOICE_DETAIL.LINE
					, INVOICE_DETAIL.DESCRIPTION, INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE
					, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE_DETAIL.TAXABLE_BASE)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
				.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
				.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.groupBy(PRODUCT.CODE)
				.orderBy(INVOICE.ISSUE_DATE)
				.fetch();
	}
	
	public static Field<Integer> getOrderedType() {
		// Field para que salgan ordenado primero 
		// compras,gastos y gastos no .ded y luego ventas.
		// En la select se complementa con invoice.type
		return DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);

	}
	
	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();
		Field<Integer> orderedType = getOrderedType();
		return ctx.getDslContext()
			.selectDistinct(
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
				,PCATEGORY.ID
				,PCATEGORY.NAME
				,BRAND.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,PRODUCT.TYPE
				,PRODUCT.PACKAGED
				,ITEM.DETAIL
				,ITEM.DETAIL2
				,ITEM.DETAIL3
				,ITEM.DESCRIPTION
				,ITEM.PURCHASE_PRICE
				,ITEM.PRICE
				,ITEM.PACK_FORMAT_TAG
				,ITEM.PACK_UNITS
                ,ITEM.PACK_UNITS_TAG
                ,ITEM.PACK_MEASUREMENT
                ,ITEM.PACK_MEASUREMENT_TAG
                ,ITEM.STOCK_UNIT_TAG
				
				,INVOICE_DETAIL.DESCRIPTION
				,INVOICE_DETAIL.DOMAIN
				,INVOICE_DETAIL.QUANTITY
				,INVOICE_DETAIL.PRICE
				,INVOICE_DETAIL.DISCOUNT_EXPR
				,INVOICE_DETAIL.TAXABLE_BASE
				,INVOICE_DETAIL.SELLER
				,INVOICE_DETAIL.PROJECT
				,INVOICE_DETAIL.WAREHOUSE
				,INVOICE_DETAIL.WORKPLACE
				,INVOICE_DETAIL.SOURCE
				,INVOICE_DETAIL.SOURCE_ID
				,INVOICE_DETAIL.INVEST_ASSET
				,INVOICE_DETAIL.PREPAYMENT
				,SELLER_ALIAS.NAME
				,WORKPLACE.DESCRIPTION
				,WAREHOUSE.NAME
				,SCOPE.DESCRIPTION
				,INVOICE_DETAIL.ID
				,PRODUCT.CATEGORY
				,ITEM.ID
				,RSELLER.ID
				,SELLER_ALIAS.ID
				,SELLER_SUPPORT_ALIAS.NAME
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
			.leftOuterJoin(BRAND).on(PRODUCT.BRAND.equal(BRAND.ID))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
			
			.leftOuterJoin(RSELLER).on(RSELLER.REGISTRY.eq(INVOICE.REGISTRY))
			.leftOuterJoin(SELLER_SUPPORT).on(RSELLER.SELLER.eq(SELLER_SUPPORT.REGISTRY))
			.leftOuterJoin(SELLER_SUPPORT_ALIAS).on(SELLER_SUPPORT.REGISTRY.eq(SELLER_SUPPORT_ALIAS.ID))
			
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
	}
	
	
	public static Stream<Invoice> getInvoiceHeaders(AONContext ctx,InvoiceFilter filter, int offset , int numberOfRows) {
		ctx.checkRead();
		Field<Integer> orderedType = getOrderedType();
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.ACTIVITY
				,INVOICE.TYPE
				,INVOICE.TRANSACTION
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
			)
			.from(INVOICE)
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE.desc(),INVOICE.REFERENCE_CODE)
			.limit(offset,numberOfRows)
			.fetch()
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
	
	public static ArrayList<InvoiceDetail> getInvoiceDetailsList(AONContext ctx, InvoiceFilter filter) {
		ArrayList<InvoiceDetail> invoiceDetails = getFullInvoices(ctx, filter)
			.stream()
			.map(new FullInvoiceDetailFiller())
			.collect(Collectors.toCollection(ArrayList::new));
		
		invoiceDetails.forEach(invoiceDetail -> invoiceDetail.setSegments(getRegistrySegments(ctx, invoiceDetail.getInvoice().getRegistry())));
		
		return invoiceDetails;
	}

	private static LinkedList<String> getRegistrySegments(AONContext ctx, Integer id) {
		List<String> segments = ctx.getDslContext().select(SEGMENT.NAME).from(SEGMENT)
			.join(RSEGMENT).on(RSEGMENT.SEGMENT.eq(SEGMENT.ID))
			.where(RSEGMENT.REGISTRY.eq(id))
			.fetch(SEGMENT.NAME);
		
		return segments.isEmpty() ? new LinkedList<>() : segments.stream().collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Invoice getFullInvoice(AONContext ctx, Integer id) {
		Invoice invoice = getInvoice(ctx, id);
		if(invoice != null) {
			invoice.setRegistryData( RegistryDAO.get(ctx, invoice.getRegistry()));
			invoice.setAddress(InvoiceAddressDAO.get(ctx, invoice));
			invoice.setDetails(InvoiceDetailDAO.getFullList(ctx, f -> f.getInvoiceProperty().eq(id)));
//			invoice.setDetails(getInvoiceDetails(ctx, prop -> prop.getIdProperty().eq(id))
//			.collect(Collectors.toCollection(LinkedList::new)));
			for(Integer i = 0; i < invoice.getDetails().size(); i++) {
				InvoiceDetail detail = invoice.getDetails().get(i);
				detail.getSource().visit(detail, new IInvoiceSourceVisitor() {
					
					private static final long serialVersionUID = 1L;
					@Override public void visitTedi(InvoiceDetail detail) {}
					@Override public void visitSales(InvoiceDetail detail) {
						detail.setSalesDetail(SalesDetailDAO.get(ctx, detail.getSourceId()));						
					}
					
					@Override public void visitReservation(InvoiceDetail detail) {}
					
					@Override public void visitPurchase(InvoiceDetail detail) {
					    // TODO PurchaseDetailDAO.get(ctx, detail.getSourceId());
						PurchaseDetail d = PurchaseDAO.getPurchaseDetailStream(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
							.and(f.getIdProperty().eq(detail.getSourceId()))).findFirst().orElse(new PurchaseDetail());
						detail.setPurchaseDetail(d);						
					}
					
					@Override public void visitOffer(InvoiceDetail detail) {
						detail.setOfferDetail(OfferDetailDAO.get(ctx, detail.getSourceId()));						
					}
					@Override public void visitIncome(InvoiceDetail detail) {
						 // TODO IncomeDetailDAO.get(ctx, detail.getSourceId());
						IncomeDetail d = IncomeDAO.getIncomeDetailStream(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
								.and(f.getIdProperty().eq(detail.getSourceId()))).findFirst().orElse(new IncomeDetail());	
						detail.setIncomeDetail(d);						
					}
					@Override public void visitFee(InvoiceDetail detail) {}
					@Override public void visitDirectInvoice(InvoiceDetail detail) {}
					@Override public void visitDirectExpense(InvoiceDetail detail) {}
					@Override public void visitDelivery(InvoiceDetail detail) {
						detail.setDeliveryDetail(DeliveryDetailDAO.getFull(ctx, detail.getSourceId()));						
					}
					@Override public void visitAccount(InvoiceDetail detail) {}
				});
				
				LinkedList<InvoiceTax> taxes = getInvoiceTaxStreamFromDetail(ctx, detail.getId())
				.collect(Collectors.toCollection(LinkedList::new));
				
				invoice.getDetails().get(i).setInvoiceTaxes(taxes);

				Account acc = getInvoiceDetailAccount(ctx, invoice.getDetails().get(i).getId());
				invoice.getDetails().get(i).setAccount(acc.getId());
				invoice.getDetails().get(i).setAccountCode(acc.getCode());
				invoice.getDetails().get(i).setAccountDescription(acc.getDescription());
			}
			
			invoice.setFinances( FinanceDAO.getFinanceStream(ctx, prop -> prop.getInvoiceProperty().eq(id))
					.collect(Collectors.toCollection(LinkedList::new))
					);
			AccountingInvoiceDAO.fillBreakdown(ctx, invoice, true);
		
			if(invoice.isRectifier()) {
				Invoice rectify = getInvoice(ctx, invoice.getRectificationInvoice());
				if(rectify != null) {
					invoice.setRectificationInvoiceSeries(rectify.getSeries());
					invoice.setRectificationInvoiceDate(rectify.getFiscal().getExpDate() != null 
							? rectify.getFiscal().getExpDate() 
							: rectify.getIssueDate());
					invoice.setRectificationInvoiceNumber(rectify.getNumber());
					invoice.setRectificationInvoiceReference(rectify.getReferenceCode());
				}
			}
		}
		return invoice;
	}
	
	public static List<InvoiceSeries> getSalesSeries(AONContext ctx) {
		return ctx.getDslContext()
			.select(INVOICE.SERIES, DSL.max(INVOICE.ISSUE_DATE), DSL.count(INVOICE.ID))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId())
				.and(INVOICE.TYPE.eq(InvoiceType.SALES.value())))
			.groupBy(INVOICE.SERIES)
			.orderBy(INVOICE.ISSUE_DATE)
			.fetch().stream().map(r -> new InvoiceSeries()
					.setSales(true)
					.setSeriesInfo(false)
					.setDescription(r.getValue(INVOICE.SERIES))
					.setCount(r.getValue(DSL.count(INVOICE.ID))))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static Account getInvoiceDetailAccount(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext().select().from(ACCOUNT)
		.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
		.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetailId)).limit(1)
		.fetch().stream().map(new FullAccountFiller()).findFirst().orElse(new Account());
	}
	
	public static Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext().select(INVOICE_TAX.TAX_TYPE, INVOICE_TAX.DOMAIN, INVOICE_TAX.PERCENTAGE, DSL.sum(INVOICE_TAX.BASE),
					DSL.sum(INVOICE_TAX.SURCHARGE), DSL.sum(INVOICE_TAX.QUOTA), DSL.sum(INVOICE_TAX.SURCHARGE_QUOTA))
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.join(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL))
				.where(INVOICE.ID.eq(invoiceId))
				.groupBy(INVOICE_TAX.TAX_TYPE, INVOICE_TAX.PERCENTAGE)
				.fetch().stream().map(new InvoiceTaxFiller());
	}
	
	public static Stream<InvoiceTax> getInvoiceTaxStreamFromDetail(AONContext ctx, Integer id) {
		return ctx.getDslContext().select()
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(id))
			.fetch().stream().map(new InvoiceTaxFiller());
	}

	private static class InvoiceTaxFiller  implements Function<Record, InvoiceTax> {

		@Override
		public InvoiceTax apply(Record record) {
			return new InvoiceTax()
					.setId(record.getValue(INVOICE_TAX.ID))
					.setDomain(record.getValue(INVOICE_TAX.DOMAIN))
					.setTaxType(TaxType.values()[record.getValue(INVOICE_TAX.TAX_TYPE)])
					.setPercentage(record.getValue(INVOICE_TAX.PERCENTAGE))
					.setBase(record.getValue(INVOICE_TAX.BASE))
					.setSurcharge(record.getValue(INVOICE_TAX.SURCHARGE))
					.setQuota(record.getValue(INVOICE_TAX.QUOTA))
					.setSurchargeQuota(record.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
					.setWithholdingType(WithholdingType.safeValueOf(record.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
					.setDeductiblePercent(record.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
					.setDeductibleQuota(record.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA));	
		}
		
	}
	
	public static Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter) {
		return getBoughtProductInvoices(ctx, filter)
			.stream()
			.map(record -> new InvoiceDetail().setId(record.getValue(INVOICE_DETAIL.ID))
					.setInvoice(new Invoice().setId(record.getValue(INVOICE.ID))
						.setIssueDate(record.getValue(DSL.max(INVOICE.ISSUE_DATE)))
						.setRegistry(record.getValue(INVOICE.REGISTRY))
						.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE)))
					.setItem(Filler.checkField(record, ITEM.ID)
							? ItemFiller.build(record)
							: new Item().setId(record.getValue(INVOICE_DETAIL.ITEM)))
					.setDescription(record.getValue( INVOICE_DETAIL.DESCRIPTION ))
					.setQuantity(record.getValue(INVOICE_DETAIL.QUANTITY))
					.setPrice(record.getValue(INVOICE_DETAIL.PRICE))
					.setDiscountExpression(record.getValue(INVOICE_DETAIL.DISCOUNT_EXPR)));
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
					.setActivity(new EnterpriseActivity().setId(record.getValue(INVOICE.ACTIVITY)))
				;
		}
		
	}
	
	
	public static class InvoiceFiller extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return buildInvoice(r);
		}
		
		public static Invoice buildInvoice(Record r) {
			return new Invoice()
				.setId(r.getValue(INVOICE.ID))
				.setDomain(r.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(INVOICE.TYPE)))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
				.setRegistry( r.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setRegistryAddress(r.getValue(INVOICE.RADDRESS))
				.setScope(checkField(r, SCOPE.ID)
						? ScopeFiller.buildScope(r)
						: new Scope().setId(r.getValue(INVOICE.SCOPE)))
				.setActivity(checkField(r, ENTERPRISE_ACTIVITY.ID)
						? EnterpriseActivityFiller.build(r)
						: new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY)))	
				.setInvestAsset(r.getValue(INVOICE.INVEST_ASSET))
				.setProject(r.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class, r.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(r.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(InvoiceTransactionType.safeValueOf(r.getValue(INVOICE.TRANSACTION)))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(r.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(r.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(r.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(r.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(r.getValue(INVOICE.TOTAL))	
				.setComments(r.getValue(INVOICE.COMMENTS))
				.setFiscal(checkField(r, INVOICE_FISCAL.INVOICE)
						? InvoiceFiscalDAO.InvoiceFiscalFiller.buildInvoiceFiscal(r)
						: new InvoiceFiscal())
				.setCreationDate(r.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(r.getValue(INVOICE.CREATION_USER))
				.setModificationDate(r.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(r.getValue(INVOICE.MODIFICATION_USER));
		}
	}
	
	public static class SiiInvoiceFiller extends InvoiceFiller implements Function<Record,Invoice> {
		Boolean pending;
		public SiiInvoiceFiller(Boolean pending) {
			this.pending = pending;
		}
		
		@Override
		public Invoice apply(Record r) {
			return buildInvoice(r)
				.setSiiStatus(pending ? "Pendiente" : r.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE));				
		}	
	}

	private static class FullInvoiceDetailFiller extends Filler implements Function<Record,InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return new InvoiceDetail()
				.setId(r.getValue(INVOICE_DETAIL.ID))
				.setInvoice(new Invoice()
					.setId(r.getValue(INVOICE.ID))
					.setDomain(r.getValue(INVOICE.DOMAIN))
					.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(INVOICE.TYPE)))
					.setSeries(r.getValue(INVOICE.SERIES))
					.setNumber(r.getValue(INVOICE.NUMBER))
					.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
					.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(r.getValue(INVOICE.TAX_DATE))
					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
					.setRegistry(r.getValue(INVOICE.REGISTRY))
					.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class, r.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setRegistryName(r.getValue(INVOICE.RNAME))
					.setAddress(new RegistryAddress()
							.setGeozoneCode(r.getValue(GEOZONE.CODE))
							.setGeozoneName(r.getValue(GEOZONE.NAME))
							.setCity(r.getValue(RADDRESS.CITY))
							.setZip(r.getValue(RADDRESS.ZIP)))
					.setScope(new Scope().setId(r.getValue(SCOPE.ID)).setDescription(r.getValue(SCOPE.DESCRIPTION)))
				)
				.setInvestAsset(r.getValue(INVOICE_DETAIL.INVEST_ASSET))
				.setProject( r.getValue( INVOICE_DETAIL.PROJECT ))
				.setProjectName( r.getValue( PROJECT.NAME ))
				.setLine(r.getValue( INVOICE_DETAIL.LINE ))
				.setDescription(r.getValue( INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(r.getValue(INVOICE_DETAIL.QUANTITY))
				.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
				.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(r.getValue(INVOICE_DETAIL.TAXABLE_BASE))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
				.setSeller(checkField(r, SELLER_ALIAS.ID)
					? new Seller().copy(RegistryFiller.build(r, SELLER_ALIAS))
					: new Seller().setId(r.getValue(INVOICE_DETAIL.SELLER)))
				.setWorkPlace(r.getValue(INVOICE_DETAIL.WORKPLACE))
				.setWorkPlaceName(r.getValue(WORKPLACE.DESCRIPTION))
				.setWarehouse(r.getValue(INVOICE_DETAIL.WAREHOUSE))
				.setWarehouseName(r.getValue(WAREHOUSE.NAME))
				.setSource(InvoiceSource.safeValueOf(r.getValue(INVOICE_DETAIL.SOURCE)))
				.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID))
				.setPrepayment(getBoolean(r, INVOICE_DETAIL.PREPAYMENT))
				.setSellerSupport(checkField(r, SELLER_SUPPORT_ALIAS.NAME)
						? r.get(SELLER_SUPPORT_ALIAS.NAME)
						: "")
				;
		}
		
	}
	
	public static LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter){
		return ctx.getDslContext().select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
				.from(INVOICING_GROUP).where(INVOICING_GROUP_PROPERTIES.getConditions(filter))
				.fetchInto(INVOICING_GROUP).stream().map(new InvoicingGroupFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static InvoicingGroup insert(AONContext ctx, InvoicingGroup invoicingGroup) {
		Integer id = ctx.getDslContext()
			.insertInto(INVOICING_GROUP)
			.set(INVOICING_GROUP.DOMAIN, invoicingGroup.getDomain())
			.set(INVOICING_GROUP.CUSTOMER, invoicingGroup.getCustomer())
			.set(INVOICING_GROUP.CUSTOMER_GROUPED, invoicingGroup.getCustomerGrouped())
			.set(INVOICING_GROUP.DESCRIPTION, invoicingGroup.getDescription())
			.set(INVOICING_GROUP.CREATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.CREATION_USER, ctx.getUser())
			.set(INVOICING_GROUP.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.MODIFICATION_USER, ctx.getUser())
			.returning(INVOICING_GROUP.ID).fetchOne().getValue(INVOICING_GROUP.ID);
		return invoicingGroup.setId(id);
	}
	
	private static InvoicingGroup update(AONContext ctx, InvoicingGroup invoicingGroup) {
		ctx.getDslContext()
			.update(INVOICING_GROUP)
			.set(INVOICING_GROUP.DESCRIPTION, invoicingGroup.getDescription())
			.set(INVOICING_GROUP.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.MODIFICATION_USER, ctx.getUser())
			.where(INVOICING_GROUP.ID.eq(invoicingGroup.getId()))
			.execute();
		return invoicingGroup;
	}
	
	public static InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup) {
		return invoicingGroup.getId() != null 
				? update(ctx, invoicingGroup) 
				: insert(ctx, invoicingGroup) ;
	}
	
	public static LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate){
		Field<Integer> orderedType = getOrderedType();
		AggregateFunction<Integer> min = DSL.min(INVOICE.NUMBER);
		AggregateFunction<Integer> max = DSL.max(INVOICE.NUMBER);
		AggregateFunction<Integer> records = DSL.count();
		LinkedList<InvoiceSeries> list = new LinkedList<>(); 
		ctx.getDslContext()
		.select(orderedType,INVOICE.SERIES,min,max,records)
		.from(INVOICE)
		.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
		.and((INVOICE.ISSUE_DATE).between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)) )
		.and(INVOICE.TYPE.ne( InvoiceType.UNDEDUCTIBLE.value()) )
		.groupBy(orderedType,INVOICE.SERIES)
		.fetch()
		.stream()
		.forEach( rec -> list.add(
			new InvoiceSeries()
				.setSales(rec.getValue(orderedType) == 1)
				.setSeriesInfo(true)
				.setDescription(rec.getValue(INVOICE.SERIES))
				.setFromNumber(rec.getValue(min))
				.setToNumber(rec.getValue(max))
				.setCount(rec.getValue(records)))
				);
		AggregateFunction<Integer> count = DSL.count();
		ctx.getDslContext()
			.select(orderedType,INVOICE.TRANSACTION,count)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and((INVOICE.ISSUE_DATE).between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)) )
			.groupBy(orderedType,INVOICE.TRANSACTION)
			.fetch()
			.stream()
			.forEach( rec -> list.add(
				new InvoiceSeries()
					.setSales(rec.getValue(orderedType) == 1)
					.setSeriesInfo(false)
					.setDescription(InvoiceTransactionType.values()[rec.getValue(INVOICE.TRANSACTION)].getDescription())
					.setFromNumber(rec.getValue(count)))
					);
		return list;
	}
	
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
	
	public static class InvoicingGroupFiller implements Function<Record, InvoicingGroup> {

		@Override
		public InvoicingGroup apply(Record r) {
			return buildInvoicingGroup(r);			
		}
		
		public static InvoicingGroup buildInvoicingGroup(Record r) {
			return new InvoicingGroup()
					.setId(r.getValue(INVOICING_GROUP.ID))
					.setDomain(r.getValue(INVOICING_GROUP.DOMAIN))
					.setCustomer(r.getValue(INVOICING_GROUP.CUSTOMER))
					.setCustomerGrouped(r.getValue(INVOICING_GROUP.CUSTOMER_GROUPED))
					.setDescription(r.getValue(INVOICING_GROUP.DESCRIPTION))
					.setCreationDate(r.getValue(INVOICING_GROUP.CREATION_DATE))
					.setCreationUser(r.getValue(INVOICING_GROUP.CREATION_USER))
					.setModificationDate(r.getValue(INVOICING_GROUP.MODIFICATION_DATE))
					.setModificationUser(r.getValue(INVOICING_GROUP.MODIFICATION_USER));		
		}
	}
	
	public static int getMinNumber(AONContext ctx, InvoiceType type, String series ) {
		return getMinNumber(ctx, new Byte[]{type.value()} , series);
	}
	
	public static int getMinNumber(AONContext ctx, Byte[] types, String series ) {
		Integer min = ctx.getDslContext()
			.select( DSL.min(INVOICE.NUMBER))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in(types))
			.and( AonStringUtils.isBlank(series)
					?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
					:INVOICE.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.min(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.min(INVOICE.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		return --min;
	}
	
	public static Invoice getLastSaleInvoice(AONContext ctx, String series ) {
		return ctx.getDslContext().select()
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
				.and(AonStringUtils.isBlank(series)
					? INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
					: INVOICE.SERIES.eq(series))
				.orderBy(INVOICE.NUMBER.desc())
				.limit(1)
				.fetch().stream().map(new InvoiceFiller()).findFirst().orElse(new Invoice());
	}
	
	public static int getNextNumber(AONContext ctx, InvoiceType type, String series ) {
		return getNextNumber(ctx, new Byte[]{type.value()} , series);
	}
	
	public static int getNextNumber(AONContext ctx, Byte[] types, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(INVOICE.NUMBER))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in(types))
			.and( AonStringUtils.isBlank(series)
					?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
					:INVOICE.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.max(INVOICE.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	public static Invoice accept(AONContext ctx, Invoice invoice, Integer rawdocId) {
		AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx, invoice.getIssueDate());
		InvoiceAutoComplete.completeInvoice2(ctx, aonCtx, invoice);
		InvoiceValidation.validateInvoice(ctx, aonCtx, invoice);
		invoice = insert(ctx, aonCtx, invoice);
		FinanceDAO.insertFinances(ctx, invoice.getFinances());
		if(invoice.isRectifier() && invoice.getRectificationInvoice() != null) {
			updateRectifiedInvoice(ctx, invoice);
		}
		if(rawdocId != null) {
			Rawdoc rawdoc = RawdocDAO.getFull(ctx, rawdocId);
			if(rawdoc.getData() != null) {
				Attach attach = new Attach()
					.setDate(new Date())
					.setDomain(new Domain().setId(invoice.getDomain()))
					.setAttachModule(invoice.getId())
					.setMimeType(rawdoc.getMimeType())
					.setAttachType(AttachType.INVOICE)
					.setType(InvoiceAttachmentType.INVOICE.value())
					.setData(rawdoc.getData());
				AttachmentDAO.insertInvoiceAttach(ctx, attach);
			}
			RawdocDAO.delete(ctx, invoice.getDomain(), rawdocId);	
		}
		return invoice;
	}
	
	private static void updateRectifiedInvoice(AONContext ctx, Invoice rectifierInvoice) {
		ctx.getDslContext().update(INVOICE)
		.set(INVOICE.RECTIFICATION_TYPE, RectificationType.RECTIFIED.value())
		.set(INVOICE.RECTIFICATION_INVOICE, rectifierInvoice.getId())
		.where(INVOICE.ID.eq(rectifierInvoice.getRectificationInvoice()))
		.execute();
	}
	
	public static Invoice save(AONContext ctx, Invoice invoice) {
		invoice = invoice.getId() != null
			? update(ctx, invoice)
			: insert(ctx, invoice);
		invoice.setDetails(InvoiceDetailDAO.save(ctx, invoice.getDetails()));
		return invoice;
	}
	
	public static Invoice insert(AONContext ctx, Invoice invoice) {
		return insert(ctx,ConfigurationDAO.getConfiguration(ctx, invoice.getIssueDate()),invoice); 
	}
	
	public static InvoiceDetail insertInvoiceDetail(AONContext ctx, InvoiceDetail invoiceDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression())
		.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice().getId())
		.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset())
		.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem() != null ? invoiceDetail.getItem().getId() : null)
		.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
		.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
		.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
		.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource() != null ? invoiceDetail.getSource().value(): null)
		.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
		.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
		.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
		.set(INVOICE_DETAIL.PREPAYMENT, (byte)1)
		.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() != null ? invoiceDetail.getSeller().getId(): null)
		.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkPlace())
		.set(INVOICE_DETAIL.WAREHOUSE,  invoiceDetail.getWarehouse())
		.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp(new Date().getTime()))
		.set(INVOICE_DETAIL.CREATION_USER, ctx.getUser())
		.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
		.set(INVOICE_DETAIL.MODIFICATION_USER, ctx.getUser())
		.execute();
		return invoiceDetail.setId(id);
	}
	public static Invoice insert(AONContext ctx, AonConfiguration config, Invoice invoice) {
		ctx.checkWrite();
		InvoiceAutoComplete.completeInvoice(ctx, config, invoice);
		InvoiceValidation.validateInvoice(ctx, config, invoice);
		InvoiceRecord record = ctx.getDslContext()
			.insertInto(INVOICE)
			.set(INVOICE.DOMAIN, invoice.getDomain() )
			.set(INVOICE.ACTIVITY, invoice.getActivity().getId())
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
			.set(INVOICE.CREATION_USER, ctx.getUser()) 
			.set(INVOICE.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE.MODIFICATION_USER, ctx.getUser()) 
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE.ID)
			.fetchOne();
		invoice.setId(record.getValue(INVOICE.ID));
		ctx.log().debug("INSERT INVOICE invoice: {0} Act: {1}",invoice.getId(),invoice.getActivity());
	
//		if(invoice.getAddress() != null && !invoice.getAddress().isEmpty())
//			invoice.setAddress(InvoiceAddressDAO.save(ctx, invoice.getAddress(), invoice.getId()));
		
		insertDetails(ctx, config, invoice);
		InvoiceFiscalDAO.save(ctx, config, invoice);
		return invoice.setCreationDate(new Date()); 
	}
	
	private static void insertDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
		if(invoice.getDetails() != null) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				insertDetail(ctx, config, invoice, detail);
			}
		}
	}
	private static void insertDetail(AONContext ctx, AonConfiguration config, Invoice invoice,InvoiceDetail detail) {
		InvoiceValidation.validateDetail(ctx, config, detail);
		beforeInsertDetail(ctx, config, invoice, detail);
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
						? MessageFormat.format(DETAIL_MSG, invoice.getReferenceCode(), invoice.getIssueDate())
						: detail.getDescription())
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
			.set(INVOICE_DETAIL.CREATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(INVOICE_DETAIL.ID)
			.fetchOne();
		detail.setId(record.getValue(INVOICE_DETAIL.ID));
		detail.setInvoice(invoice);
		ctx.log().debug("\tINSERT INVOICE_DETAIL detalles invoice: {0}",detail.getId());
		afterInsertDetail(ctx, config, invoice, detail);
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
			ctx.log().debug("\t\tINSERT INVOICE_TAX tax: {0}",tax.getTaxType());
		}
	}
	
	public static Invoice update(AONContext ctx, Invoice invoice) {
		return update(ctx,ConfigurationDAO.getConfiguration(ctx, invoice.getIssueDate()),invoice); 
	}
	
	public static Invoice update(AONContext ctx, Invoice invoice, boolean only) {
		return update(ctx,ConfigurationDAO.getConfiguration(ctx, invoice.getIssueDate()),invoice, only); 
	}

	public static Invoice update(AONContext ctx, AonConfiguration config, Invoice invoice) {
		return update(ctx, config, invoice, false);
	}
	
	public static Invoice update(AONContext ctx, AonConfiguration config, Invoice invoice, boolean only) {
		ctx.checkWrite();
		InvoiceValidation.validateInvoice(ctx, config, invoice);
		InvoiceAutoComplete.completeInvoice(ctx, config, invoice);
		int i = ctx.getDslContext()
			.update(INVOICE)
			.set(INVOICE.DOMAIN, invoice.getDomain() )
			.set(INVOICE.ACTIVITY, invoice.getActivity().getId() )
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
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( invoice.getId()))
			.execute();
		ctx.log().debug("UPDATE INVOICE invoice: {0} ({1} rows)",invoice.getId(),i);
		if(!only) {
			InvoiceFiscalDAO.save(ctx, config, invoice);
			updateDetails(ctx, config, invoice);
		}
		return invoice; 
	}
	
	private static void updateDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
		if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				if (detail.isDeleted()) {
					Integer id = detail.getId() * -1;
					detail.setId(id);
					deleteDetail(ctx,config,invoice,detail);
				} else {
					insertDetail(ctx, config, invoice, detail);
				}
			}
		}
	}

	private static void deleteDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		beforeDeleteDetail(ctx, config, invoice, detail);
		
		int count = ctx.getDslContext()
			.delete(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
			.execute();
		ctx.log().debug("DELETE INVOICE_TAX detalles de la factura: {0} ({1} filas)",detail.getId(),count);
		
		// Se borran la linea
		count = ctx.getDslContext()
			.delete(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.equal(detail.getId()))
			.execute();
		ctx.log().debug("DELETE INVOICE_DETAIL detalle de la factura: {0} ({1} filas)",detail.getId(),count);
	}

	public static void delete(AONContext ctx, Integer id) {
		delete(ctx, ConfigurationDAO.getConfiguration(ctx),id);
	}
	
	public static void delete(AONContext ctx, AonConfiguration config, Integer id) {
		ctx.checkWrite();
		
		Invoice inv = getInvoice(ctx, id);
		if (inv == null) throw new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage());
		InvoiceValidation.validateInvoiceDeletion(ctx, config, inv);
		
		if (inv.isRectifier()) {
			if (inv.getRectificationInvoice() != null) {
				final Invoice rectified = getInvoice(ctx, inv.getRectificationInvoice());
				if (rectified == null) throw new AonCoreException(AonError.INVOICE_RECTIFIED_NOT_FOUND.getMessage());
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
					ctx.log().debug("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - SOLO UNA): {0}",rectified.getId());
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
							// Segunda iteracion y sucesivas. Hay mas de una, debe continuar a null.
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
					ctx.log().debug("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - MAS DE UNA): {0}",rectified.getId());
				}
			}
		}
		inv.setDetails(
			ctx.getDslContext()
				.select(INVOICE_DETAIL.ID,INVOICE_DETAIL.DOMAIN,INVOICE_DETAIL.SOURCE) 
				.from( INVOICE_DETAIL )
				.where(INVOICE_DETAIL.INVOICE.eq(inv.getId()))
				.fetch()
				.stream()
				.map( rec -> new InvoiceDetail()
					.setId(rec.getValue(INVOICE_DETAIL.ID))
					.setDomain(rec.getValue(INVOICE_DETAIL.DOMAIN))
					.setSource(AonEnumUtils.enumValue(InvoiceSource.class,rec.getValue(INVOICE_DETAIL.SOURCE))))
				.collect(Collectors.toCollection(LinkedList::new))
				);
		
		deleteDetails(ctx, config, inv);
		
		if ( inv.isDUAAllowed() ) {
			Integer importInvoice = ctx.getDslContext()
				.select(INVOICE_DUA.INVOICE_IMPORT)
				.from(INVOICE_DUA)
				.where(INVOICE_DUA.INVOICE_NATIONAL.equal(id))
				.and(INVOICE_DUA.DOMAIN.eq(inv.getDomain()))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(INVOICE_DUA.INVOICE_IMPORT))				
				.findFirst()
				.orElse(null);
			if (importInvoice != null) {
				ctx.log().debug("\tDUA LINKED");
				ctx.getDslContext()
					.select(INVOICE_DETAIL.TAXABLE_BASE, INVOICE_TAX.ID,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.SURCHARGE)
					.from(INVOICE_DETAIL)
					.innerJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					.where(INVOICE_DETAIL.INVOICE.eq(importInvoice))
					.and(INVOICE_DETAIL.DOMAIN.eq(inv.getDomain()))
					.fetch()
					.stream()
					.forEach(rec -> {
						int taxId = rec.getValue(INVOICE_TAX.ID);
						double base = rec.getValue(INVOICE_DETAIL.TAXABLE_BASE);
						double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
						double surcharge = rec.getValue(INVOICE_TAX.SURCHARGE);
						double quota = AonMathUtils.round( base * percent / 100 );
						double surchargeQuota = AonMathUtils.round( base * surcharge / 100 );
						int count = ctx.getDslContext().update(INVOICE_TAX)
								.set(INVOICE_TAX.BASE, base )
								.set(INVOICE_TAX.QUOTA, quota )
								.set(INVOICE_TAX.SURCHARGE_QUOTA, surchargeQuota)
								.set(INVOICE_TAX.DEDUCTIBLE_PERCENT, 100.0)
								.set(INVOICE_TAX.DEDUCTIBLE_QUOTA, quota)
								.where(INVOICE_TAX.ID.equal( taxId ))
								.execute();
						ctx.log().debug("\tUPDATE INVOICE_TAX (RESTORE PREVIOUS INFO): {0} ({1} filas)",id,count);	
					});
				int count = ctx.getDslContext()
						.delete(INVOICE_DUA)
						.where(INVOICE_DUA.INVOICE_NATIONAL.equal(id))
						.execute();
				ctx.log().debug("\tDELETE INVOICE_DUA: {0} ({1} filas)",id,count);
			}
		}

		int count = ctx.getDslContext()
			.delete(INVOICE_ATTACH)
			.where(INVOICE_ATTACH.INVOICE.equal(id))
			.execute();
		ctx.log().debug("DELETE INVOICE_ATTACH adjuntos de la factura: {0} ({1} filas)",id,count);
		
		FinanceDAO.deleteInvoiceFinances(ctx,id);
		
		InvoiceFiscalDAO.delete(ctx, id);
		
		InvoiceAddressDAO.delete(ctx, id);
		InvoiceBatchDetailDAO.delete(ctx, f-> f.getInvoiceProperty().eq(id));
		InvoiceInfoDAO.delete(ctx, f-> f.getInvoiceProperty().eq(id));
		
		count = ctx.getDslContext()
			.delete(INVOICE)
			.where(INVOICE.ID.equal(id))
			.execute();
		ctx.log().debug("DELETE INVOICE factura: {0} ({1} filas)",id,count);
	}

	private static void deleteDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
		if (invoice.getDetails() != null ) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				deleteDetail(ctx,config,invoice,detail);
			}
		}
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
		if (inv.isSales()) {
			inv.setSeries(data.getSeries());
			inv.setNumber(data.getNumber());
			inv.setReferenceCode(null);
		} else {
			inv.setSeries(null);
			inv.setNumber(0);
			inv.setReferenceCode(data.getReferenceCode());
		}
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
				FinanceTrackingDAO.settle(ctx, oldId    );
				FinanceTrackingDAO.settle(ctx, financeId);
			}
			
		}
	}
	
	private static void beforeInsertDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		
	}
	private static void afterInsertDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		if (detail.getInvoice().getType() != InvoiceType.UNDEDUCTIBLE && !detail.isPrepayment()) {
			if(detail.getDomain() == null) detail.setDomain(invoice.getDomain());
			insertInvoiceTaxes(ctx,detail);
		} else {
			ctx.log().debug("\t\tSKIPPING INVOICE TAX CREATION ({0})",(detail.isPrepayment()? "PREPAYMENT": "UNDEDUCTIBLE INVOICE"));
		}
		detail.getSource().visit(detail, new IInvoiceSourceVisitor() {
			
			private static final long serialVersionUID = -9008741708561768671L;

			@Override public void visitSales(InvoiceDetail detail) {}
			@Override public void visitReservation(InvoiceDetail detail) {}
			@Override public void visitPurchase(InvoiceDetail detail) {}
			@Override public void visitOffer(InvoiceDetail detail) {}
			@Override public void visitIncome(InvoiceDetail detail) {}
			@Override public void visitFee(InvoiceDetail detail) {}
			@Override public void visitDirectInvoice(InvoiceDetail detail) {}
			@Override public void visitDirectExpense(InvoiceDetail detail) {}
			@Override public void visitDelivery(InvoiceDetail detail) {}
			
			@Override public void visitAccount(InvoiceDetail detail) {
				if (detail.getAccount() == null) 
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_EXP_ACCOUNT.getMessage());
				ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
					.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, detail.getDomain())
					.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, detail.getId())
					.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, detail.getAccount())
					.execute();
				if (detail.getInvoice().getType() != InvoiceType.UNDEDUCTIBLE && !detail.isPrepayment()) {
					ctx.log().debug("\tINSERT INVOICE_DETAIL_ACCOUNT");
					for (InvoiceTax tax : detail.getInvoiceTaxes() ) {
						ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
						.set(INVOICE_TAX_ACCOUNT.DOMAIN,detail.getDomain())
						.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, tax.getId())
						.set(INVOICE_TAX_ACCOUNT.ACCOUNT, tax.getAccount()!=null?tax.getAccount():detail.getAccount())
						.execute();
						ctx.log().debug("\t\tINSERT INVOICE_TAX_ACCOUNT");
					}
				} else {
					ctx.log().debug("\t\tSKIPPING INVOICE TAX ACCOUNT CREATION ({0})",(detail.isPrepayment()?"PREPAYMENT":"UNDEDUCTIBLE INVOICE"));
				}
			}
			
			@Override public void visitTedi(InvoiceDetail detail) {
				if (detail.getAccount() != null) { 
					ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
						.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, detail.getDomain())
						.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, detail.getId())
						.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, detail.getAccount())
						.execute();
					if (detail.getInvoice().getType() != InvoiceType.UNDEDUCTIBLE && !detail.isPrepayment()) {
						ctx.log().debug("\tINSERT INVOICE_DETAIL_ACCOUNT");
						for (InvoiceTax tax : detail.getInvoiceTaxes() ) {
						ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
							.set(INVOICE_TAX_ACCOUNT.DOMAIN,detail.getDomain())
							.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, tax.getId())
							.set(INVOICE_TAX_ACCOUNT.ACCOUNT, tax.getAccount()!=null?tax.getAccount():detail.getAccount())
							.execute();
						ctx.log().debug("\t\tINSERT INVOICE_TAX_ACCOUNT");
						}
					} else {
						ctx.log().debug("\t\tSKIPPING INVOICE TAX ACCOUNT CREATION ({0})",(detail.isPrepayment()?"PREPAYMENT":"UNDEDUCTIBLE INVOICE"));
					}
				}
			}
		});
	}
	
	private static void beforeDeleteDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		detail.getSource().visit(detail, new IInvoiceSourceVisitor() {
			
			private static final long serialVersionUID = -715576035920671791L;
			
			@Override public void visitSales(InvoiceDetail detail) {}
			@Override public void visitReservation(InvoiceDetail detail) {}
			@Override public void visitPurchase(InvoiceDetail detail) {}
			@Override public void visitOffer(InvoiceDetail detail) {}
			@Override public void visitIncome(InvoiceDetail detail) {}
			@Override public void visitFee(InvoiceDetail detail) {}
			@Override public void visitDirectInvoice(InvoiceDetail detail) {}
			@Override public void visitDirectExpense(InvoiceDetail detail) {}
			@Override public void visitDelivery(InvoiceDetail detail) {}
			@Override public void visitAccount(InvoiceDetail detail) {
				int count = ctx.getDslContext()
					.delete(INVOICE_DETAIL_ACCOUNT)
					.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(detail.getId()))
					.execute();
				ctx.log().debug("DELETE INVOICE_DETAIL_ACCOUNT ({0} filas.)",count);
				
				ctx.getDslContext().select(INVOICE_TAX.ID)
					.from(INVOICE_TAX)
					.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
					.fetch()
					.stream()
					.mapToInt(rec -> rec.getValue(INVOICE_TAX.ID))
					.forEach(id -> {
						int x = ctx.getDslContext()
								.delete(INVOICE_TAX_ACCOUNT)
								.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(id))
								.execute();
						ctx.log().debug("DELETE INVOICE_TAX_ACCOUNT ({0} filas.)",x);
					});
			}
			
			@Override public void visitTedi(InvoiceDetail detail) {
				int count = ctx.getDslContext()
						.delete(INVOICE_DETAIL_ACCOUNT)
						.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(detail.getId()))
						.execute();
					ctx.log().debug("DELETE INVOICE_DETAIL_ACCOUNT ({0} filas.)",count);
					ctx.getDslContext().select(INVOICE_TAX.ID)
						.from(INVOICE_TAX)
						.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
						.fetch()
						.stream()
						.mapToInt(rec -> rec.getValue(INVOICE_TAX.ID))
						.forEach(id -> {
							int x = ctx.getDslContext()
									.delete(INVOICE_TAX_ACCOUNT)
									.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(id))
									.execute();
							ctx.log().debug("DELETE INVOICE_TAX_ACCOUNT ({0} filas.)",x);
						});
			}
			
		});
		
	}

	public static Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().select(
				 INVOICE.TYPE
				,INVOICE.REGISTRY
				,INVOICE.SCOPE
				,REGISTRY.ALIAS
				,REGISTRY.DOCUMENT
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.NAME
				
			)
			.from(INVOICE)
			.join(REGISTRY).on(REGISTRY.ID.eq(INVOICE.REGISTRY))
			.where(REGISTRY_PROPERTIES.getConditions(filter))
			.and(INVOICE.DOMAIN.eq(ctx.getDomainId())
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),INVOICE.SCOPE)))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), INVOICE.SECURITY_LEVEL))
			.groupBy(INVOICE.TYPE,INVOICE.REGISTRY)
			.orderBy(REGISTRY.NAME)
			.limit(30)
			.fetch()
			.stream()
			.map(rec -> new InvoiceRegistry()
					.setId( rec.getValue(INVOICE.REGISTRY) )
					.setScope(rec.getValue(INVOICE.SCOPE))
					.setAlias(rec.getValue(REGISTRY.ALIAS))
					.setDocument(rec.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(rec.getValue(REGISTRY.NAME))
					.setType( AccountingRegistryType.getFor(InvoiceType.safeValueOf( rec.getValue(INVOICE.TYPE) )))
					);			
	}
	
	public static void saveFacturaeCodeAsignacion(AONContext ctx, Integer invoice, Integer registry, String code) {
		Project project = new Project()
				.setDomain(new Domain().setId(ctx.getDomainId()))
				.setRegistry(new com.esferalia.aon.occam.api.model.registry.Registry().setId(registry))
				.setName(code)
				.setDate(new Date());

		project = ProjectDAO.save(ctx, project);

		ctx.getDslContext().update(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.PROJECT, project.getId())
		.where(INVOICE_DETAIL.DOMAIN.eq(ctx.getDomainId()))
		.and(INVOICE_DETAIL.INVOICE.eq(invoice))
		.execute();
		
	}

	public static void updateWithholdingType(AONContext ctx, Integer invoiceId, WithholdingType newType ) {
		if (invoiceId == null)  throw new AonCoreException("El Identificador de factura no puede estar vacio");
		if (newType == null) throw new AonCoreException("El nuevo tipo de retención no puede estar vacio");
	
		MutableInt sum = new MutableInt();
		ctx.getDslContext().select(INVOICE_TAX.ID)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(INVOICE.ID.eq(invoiceId))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(INVOICE_TAX.ID))
			.forEach(taxId -> sum.add(ctx.getDslContext()
						.update(INVOICE_TAX)
						.set(INVOICE_TAX.WITHHOLDING_TYPE, newType.value())
						.where(INVOICE_TAX.ID.eq(taxId))
						.execute())
			);
		ctx.log().info("UPDATE WITHHOLDING TYPE: {0}: {1} filas.",invoiceId, sum.getValue());
	}

	public static Condition getWhere(AccountingReportParams params) {
		
		Condition condition = INVOICE.DOMAIN.equal( params.getDomain() );
		
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// Sólo las comunes. Los "sin activdad".
				condition = condition.and( INVOICE.ACTIVITY.isNull());
			} else {
				condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
			}
		}
		if(params.getInvoices() != null){
			condition = condition.and( INVOICE.ID.in( params.getInvoices() ));
		}
			
		if(params.getFromDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.ge( AonDateUtils.toSql(params.getFromDate())));
		}
		if(params.getToDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.le( AonDateUtils.toSql(params.getToDate())));
		}

		if(params.getActivity() != null){
			condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity()));
		}

		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			condition = condition.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
		}
		
		if (params.getAccrualRegime() != null) {
			condition = condition.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		
		if (params.getInvestment() != null) {
			condition = condition.and( INVOICE.INVESTMENT.eq( AonEnumUtils.getByte(params.getInvestment())));
		}
		
		if (params.getRectificationType() != null) {
			condition = condition.and( INVOICE.RECTIFICATION_TYPE.eq( params.getRectificationType().value()));
		}
		
		if (params.getService() != null) {
			if ( params.getService().booleanValue() ) {
				condition = condition.and( INVOICE.SERVICE.eq((byte)1).or( INVOICE.TYPE.eq( InvoiceType.EXPENSES.value())));
			} else {
				condition = condition.and( INVOICE.SERVICE.ne((byte)1).and( INVOICE.TYPE.ne( InvoiceType.EXPENSES.value())));
			}
		}
		
		if (params.getOutput() != null) {
			if (params.getOutput().booleanValue()) {
				condition = condition.and( INVOICE.TYPE.eq( InvoiceType.SALES.value() ));
			} else {
				condition = condition.and( INVOICE.TYPE.in( InvoiceType.EXPENSES.value(), InvoiceType.PURCHASE.value() ));
			}
		}
			
		if (params.getVatSummaryType() != null) {
			if (params.getVatSummaryType() == VatSummaryType.NATIONAL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(false)));
			} else if (params.getVatSummaryType() == VatSummaryType.SURCHARGE){	
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.SURCHARGE.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.FARMER) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.INTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.INTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.EXTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.EXTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.CAN_CEU_MEL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.CAN_CEU_MEL.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.OTHER_ISP) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.OTHER_ISP.value() ));
			}
		}
		return condition;
	}
	
	public static Stream<Invoice> getInvoiceHeaders(AONContext ctx, AccountingReportParams params, int offset , int numberOfRows) {
		ctx.checkRead();
		Field<Integer> orderedType = getOrderedType();
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.ACTIVITY
				,INVOICE.TYPE
				,INVOICE.TRANSACTION
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
				
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,IAE.EPIGRAPH				
			)
			.from(INVOICE)
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.where(getWhere(params))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE.desc(),INVOICE.REFERENCE_CODE)
			.limit(offset , numberOfRows )
			.fetch()
			.stream()
			.map(new HeaderInvoiceFiller());
	}

	public static class HeaderInvoiceFiller  implements Function<Record,Invoice> {

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
				.setActivity(new EnterpriseActivity()
					.setId(record.getValue(INVOICE.ACTIVITY))
					.setDescription(record.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
					.setEpigraph(record.getValue(IAE.EPIGRAPH))
					)
			;
		}
		
	}

	public static void updateActivity(AONContext ctx, Integer invoiceId, Integer activity) {
		if (invoiceId == null)  throw new AonCoreException("El Identificador de factura no puede estar vacio");
		int count = ctx.getDslContext().update(INVOICE)
			.set(INVOICE.ACTIVITY, activity)
			.where(INVOICE.ID.eq(invoiceId))
			.execute();
		ctx.log().info("UPDATE ACTIVITY: Invoice {0}: Activity {1}. {2} filas.",invoiceId, activity, count);
	}
}
