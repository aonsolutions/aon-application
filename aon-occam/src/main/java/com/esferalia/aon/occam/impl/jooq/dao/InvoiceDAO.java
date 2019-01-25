package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.InvoiceDetailRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceTaxRecord;
import com.esferalia.aon.jooq.tables.records.InvoicingGroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
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
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;;

public class InvoiceDAO {
	
	private static String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy}. ";
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NATIONALITY);}
	}

//	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
//	private static class ProductPropertiesDAO implements ProductProperties {
//		private Condition[] getConditions(ProductFilter filter) {
//			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
//			if (filterDAO == null) return new Condition[0];
//			return new Condition[] { filterDAO.getCondition() };
//		}
//		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.ID);} 
//		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.DOMAIN);}
//		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
//		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CODE);}
//		@Override public Property<Byte> getKindProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.KIND);}
//		@Override public Property<Integer> getBrandProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.BRAND);}
//		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.CATEGORY);}
//		@Override public Property<Byte> getInventoriableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.INVENTORIABLE);}
//		@Override public Property<Byte> getSerializableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.SERIALIZABLE);}
//		@Override public Property<Byte> getLotableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.LOTABLE);}
//		@Override public Property<Byte> getStatusProperty() { return new FilterDAO.PropertyDAO<Byte>(PRODUCT.STATUS);}
//		@Override public Property<Integer> getVatProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.VAT);}
//		@Override public Property<Integer> getRetentionProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.RETENTION);}
//		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.TYPE);}
//		@Override public Property<Byte> getManufacturedProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.MANUFACTURED);}
//		@Override public Property<Byte> getCompositionProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION);}
//		@Override public Property<Byte> getCompositionPriceProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION_PRICE);}
//		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.SALES_ACCOUNT);}
//		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.PURCHASE_ACCOUNT);}
//		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CREATION_USER);}
//		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.CREATION_DATE);}
//		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
//		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.MODIFICATION_DATE);}
//	}

	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
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
		return INVOICE_PROPERTIES.build(ctx.getDslContext().select().from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE)), filter)
				.fetch().stream().map(new FullInvoiceFiller());
	}
	
	public static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter, ItemFilter iFilter){
		ctx.checkRead();
		
		Collection<Condition> whereConditions = new ArrayList<Condition>();
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
				.from(INVOICE).join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE))
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.and(INVOICE.ID.notIn(
						ctx.getDslContext().select(DATA_RESPONSE.SOURCE_ID)
							.from(DATA_RESPONSE)
							.where(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()))
							.and(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()))
						))
				.limit(d.getPerPage())
				.offset(d.getPerPage() * (d.getPage() -1))
				.fetch().stream().map(new SiiInvoiceFiller(true));
		/*	Condition c = DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Pendiente");
			if(aceptada) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Correcto"));
			if(aceptadaErrores) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("AceptadoConErrores"));
			if(incorrecta) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Incorrecto"));
			if(anulada) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Anulada"));
								
			Field[] f = new Field[INVOICE.fields().length + 2];
			for(Integer i = 0 ; i < INVOICE.fields().length; i++)
				f[i] = INVOICE.fields()[i];
			f[INVOICE.fields().length] = SCOPE.DESCRIPTION;
			f[INVOICE.fields().length + 1] = DATA_RESPONSE_DETAIL.DATA_VALUE;
			
			return ctx.getDslContext().select(f)
					.from(INVOICE).join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE))
					.leftOuterJoin(DATA_RESPONSE).on(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()).and(DATA_RESPONSE.SOURCE_ID.eq(INVOICE.ID)))
					.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("status")
															.and(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE.ID))
															.and(c)
													)
					.where(INVOICE_PROPERTIES.getConditions(filter))
					.limit(d.getPerPage())
					.offset(d.getPerPage() * (d.getPage() -1))
					.fetch().stream().map(new SiiInvoiceFiller(false));
*/
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
					.join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE))
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
				.where(INVOICE.ID.eq(id))
				.fetch()
				.stream()
				.map( new FullInvoiceFiller() )
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
				,BRAND.NAME
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
				, INVOICE_DETAIL.ID
				,PRODUCT.CATEGORY
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

	public static Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext().select(INVOICE_TAX.TAX_TYPE,  INVOICE_TAX.PERCENTAGE, DSL.sum(INVOICE_TAX.BASE),
					DSL.sum(INVOICE_TAX.SURCHARGE), DSL.sum(INVOICE_TAX.QUOTA), DSL.sum(INVOICE_TAX.SURCHARGE_QUOTA))
				.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.join(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL))
				.where(INVOICE.ID.eq(invoiceId))
				.groupBy(INVOICE_TAX.TAX_TYPE, INVOICE_TAX.PERCENTAGE)
				.fetch().stream().map(new InvoiceTaxFiller());
	}

	private static class InvoiceTaxFiller  implements Function<Record6<Byte, Double, BigDecimal, BigDecimal, BigDecimal, BigDecimal>,InvoiceTax> {

		@Override
		public InvoiceTax apply(Record6<Byte, Double, BigDecimal, BigDecimal, BigDecimal, BigDecimal> record) {
			return new InvoiceTax()
					.setTaxType(TaxType.values()[record.value1()])
					.setPercentage(record.value2())
					.setBase(record.value3().doubleValue())
					.setSurcharge(record.value4().doubleValue())
					.setQuota(record.value5().doubleValue())
					.setSurchargeQuota(record.value6().doubleValue());	
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
					.setItem((record.getValue(INVOICE_DETAIL.ITEM) == null)? null
						: new Item().setId(record.getValue(INVOICE_DETAIL.ITEM))
							.setCode(record.getValue(PRODUCT.CODE ))
							.setName(record.getValue(PRODUCT.NAME)))	
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
				.setInvestAsset(record.getValue(INVOICE.INVEST_ASSET))
				.setProject(record.getValue(INVOICE.PROJECT))
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
				.setComments(record.getValue(INVOICE.COMMENTS))
				.setStatus(record.getValue(INVOICE.STATUS))
				.setCreationDate(record.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(record.getValue(INVOICE.CREATION_USER))
				.setModificationDate(record.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(record.getValue(INVOICE.MODIFICATION_USER));
		}
		
	}
	
	public static class SiiInvoiceFiller  implements Function<Record,Invoice> {
		Boolean pending;
		public SiiInvoiceFiller(Boolean pending) {
			this.pending = pending;
		}
		
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
				.setScope(new Scope().setId(record.getValue(INVOICE.SCOPE)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				.setActivity(record.getValue(INVOICE.ACTIVITY))	
				.setInvestAsset(record.getValue(INVOICE.INVEST_ASSET))
				.setProject(record.getValue(INVOICE.PROJECT))
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
				.setComments(record.getValue(INVOICE.COMMENTS))
				.setStatus(record.getValue(INVOICE.STATUS))
				.setSiiStatus(pending ? "Pendiente" : record.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE))
				.setCreationDate(record.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(record.getValue(INVOICE.CREATION_USER))
				.setModificationDate(record.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(record.getValue(INVOICE.MODIFICATION_USER))
				;
		}
		
	}

	private static class FullInvoiceDetailFiller  implements Function<Record,InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record record) {
			return new InvoiceDetail()
				.setId(record.getValue(INVOICE_DETAIL.ID))
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
						.setProduct(new Product().setCategory(record.getValue(PRODUCT.CATEGORY)))
						.setBrand(record.getValue(BRAND.NAME))
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
	
	public static InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId){
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
	
	public static InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date){
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
	
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId) {
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
	
	public static LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
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
	
	public static LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId) {
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

	public static LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date) {
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

	
	public static LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter){
		return ctx.getDslContext().select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
				.from(INVOICING_GROUP).where(INVOICING_GROUP_PROPERTIES.getConditions(filter))
				.fetchInto(INVOICING_GROUP).stream().map(new FullInvoicingGroupFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate){
		Field<Integer> orderedType = getOrderedType();
		AggregateFunction<Integer> min = DSL.min(INVOICE.NUMBER);
		AggregateFunction<Integer> max = DSL.max(INVOICE.NUMBER);
		AggregateFunction<Integer> records = DSL.count();
		LinkedList<InvoiceSeries> list = new LinkedList<InvoiceSeries>(); 
		ctx.getDslContext()
		.select(orderedType,INVOICE.SERIES,min,max,records)
		.from(INVOICE)
		.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
		.and((INVOICE.ISSUE_DATE).between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)) )
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
	
	private static class InvoiceDetailFiller implements Function<Record, InvoiceDetail> {

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
					.setItem((r.getValue(INVOICE_DETAIL.ITEM) == null)? null
							: new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
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
					?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
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
	
	public static InvoiceDetail insertInvoiceDetail(AONContext ctx, InvoiceDetail invoiceDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression())
		.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice().getId())
		.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset())
		.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem().getId())
		.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
		.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
		.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
		.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
		.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
		.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
		.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
		.set(INVOICE_DETAIL.PREPAYMENT, (byte)1)
		.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller().getId())
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
		ctx.log().info("INSERT INVOICE invoice: " + invoice.getId() + " Act: " + invoice.getActivity());
		insertDetails(ctx, config, invoice);
		return invoice; 
	}
	
	private static void insertDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
		for (InvoiceDetail detail : invoice.getDetails()) {
			insertDetail(ctx, config, invoice, detail);
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
			ctx.log().info("\t\tINSERT INVOICE_TAX tax: " + tax.getTaxType());
		}
	}
	
	public static Invoice update(AONContext ctx, AonConfiguration config, Invoice invoice) {
		ctx.checkWrite();
		InvoiceValidation.validateInvoice(ctx, config, invoice);
		InvoiceAutoComplete.completeInvoice(ctx, config, invoice);
		int i = ctx.getDslContext()
			.update(INVOICE)
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
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( invoice.getId()))
			.execute();
		ctx.log().info("UPDATE INVOICE invoice: " + invoice.getId() + "("+i+" rows)");
		updateDetails(ctx, config, invoice);
		return invoice; 
	}
	
	private static void updateDetails(AONContext ctx, AonConfiguration config, Invoice invoice) {
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

	private static void deleteDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		beforeDeleteDetail(ctx, config, invoice, detail);
		
		int count = ctx.getDslContext()
			.delete(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
			.execute();
		ctx.log().info("DELETE INVOICE_TAX detalles de la factura: " + detail.getId() + " ("+count+" filas)");
		
		// Se borran la linea
		count = ctx.getDslContext()
			.delete(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.equal(detail.getId()))
			.execute();
		ctx.log().info("DELETE INVOICE_DETAIL detalle de la factura: " + detail.getId() + " ("+count+" filas)");
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
		
		ctx.getDslContext()
			.delete(INVOICE)
			.where(INVOICE.ID.equal(id))
			.execute();
		ctx.log().info("DELETE INVOICE factura: " + id);
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
				FinanceDAO.settle(ctx, oldId    ,finance.getAmount());
				FinanceDAO.settle(ctx, financeId,finance.getAmount());
			}
			
		}
	}
	
	private static void beforeInsertDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		
	}
	private static void afterInsertDetail(AONContext ctx, AonConfiguration config, Invoice invoice, InvoiceDetail detail) {
		insertInvoiceTaxes(ctx,detail);
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
				ctx.log().info("INSERT INVOICE_DETAIL_ACCOUNT");
				for (InvoiceTax tax : detail.getInvoiceTaxes() ) {
//					if (tax.getAccount() == null) 
//						throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_TAX_ACCOUNT.getMessage());
					ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
						.set(INVOICE_TAX_ACCOUNT.DOMAIN,detail.getDomain())
						.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, tax.getId())
						.set(INVOICE_TAX_ACCOUNT.ACCOUNT, tax.getAccount()!=null?tax.getAccount():detail.getAccount())
						.execute();
					ctx.log().info("INSERT INVOICE_TAX_ACCOUNT");
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
				ctx.log().info("DELETE INVOICE_DETAIL_ACCOUNT ("+count+" filas.)");
				
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
						ctx.log().info("DELETE INVOICE_TAX_ACCOUNT ("+x+" filas.)");
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
	
	public static Stream<Product> getInvoiceProducts(AONContext ctx, ProductFilter filter) {
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
			.map(rec -> new Product()
					.setId( rec.getValue(PRODUCT.ID) )
					.setCode( rec.getValue(PRODUCT.CODE) )
					.setName(rec.getValue(PRODUCT.NAME))
					);			
	}
	
}
