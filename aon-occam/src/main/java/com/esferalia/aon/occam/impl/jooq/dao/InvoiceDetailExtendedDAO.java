package com.esferalia.aon.occam.impl.jooq.dao;

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
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetailExtended;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceDetailExtendedDAO {
	
	private InvoiceDetailExtendedDAO() {
	}
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	
	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();
		Field<Integer> orderedType = InvoiceDAO.getOrderedType();
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
				,BRAND.ID
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
				,SellerDAO.SELLER_ALIAS.ID
				,SellerDAO.SELLER_ALIAS.NAME
				,WORKPLACE.DESCRIPTION
				,WAREHOUSE.NAME
				,SCOPE.DESCRIPTION
				,INVOICE_DETAIL.ID
				,PRODUCT.CATEGORY
				,ITEM.ID
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
			.leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
	}

	public static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
		return getFullInvoices(ctx, filter)
				.stream()
				.map(new FullInvoiceDetailFiller());
	}

	public static Stream<InvoiceDetailExtended> getInvoiceDetailsExtended(AONContext ctx, InvoiceFilter filter, IDAOCallback callback) {
		return getFullInvoices(ctx, filter)
			.stream()
			.onClose(() -> { if (callback != null) callback.onFinish();})
			.map(new FullInvoiceDetailFiller())
			.map( d -> new InvoiceDetailExtended()
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
				.setSeller(checkField(r, SellerDAO.SELLER_ALIAS.ID)
					? new Seller().copy(RegistryFiller.build(r, SellerDAO.SELLER_ALIAS))
					: new Seller().setId(r.getValue(INVOICE_DETAIL.SELLER)))
				.setWorkplace( new Workplace()
					.setId( r.getValue(INVOICE_DETAIL.WORKPLACE) )
					.setDescription(getValue(r,WORKPLACE.DESCRIPTION)))
				.setWarehouse(r.getValue(INVOICE_DETAIL.WAREHOUSE))
				.setWarehouseName(r.getValue(WAREHOUSE.NAME))
				.setSource(InvoiceSource.safeValueOf(r.getValue(INVOICE_DETAIL.SOURCE)))
				.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID))
				.setPrepayment(getBoolean(r, INVOICE_DETAIL.PREPAYMENT))
				;
			
		}
		
	}
}

