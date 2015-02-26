package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;


public class InvoiceDAO {
	
	private static final Registry SELLER_ALIAS = REGISTRY.as("seller");
	
	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFilter filter) {
		ctx.checkRead();
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
				,GEOZONE.CODE
				,GEOZONE.NAME
				,RADDRESS.ZIP
				,RADDRESS.CITY
				
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
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(INVOICE_DETAIL.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
			.where(getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
		
	}
	
	
	public static void getInvoicesFormatCSV(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatCSV(out,'\t',"");
	}

	public static void getInvoicesFormatHTML(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatHTML(out);
	}

	public static void getInvoiceDetails(AONContext ctx, InvoiceFilter filter,
			Consumer<InvoiceDetail> action) {
		getFullInvoices(ctx, filter)
			.stream()
			.map( new FullInvoiceDetailFiller())
			.forEach(action);
	}
	
	private static class FullInvoiceDetailFiller  implements Function<Record,InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record record) {
			InvoiceDetail detail = new InvoiceDetail(); 
			Invoice invoice = new Invoice();
			invoice.setId( record.getValue( INVOICE.ID ) );
			invoice.setDomain(record.getValue( INVOICE.DOMAIN ));
			invoice.setType(AonEnumUtils.enumValue(InvoiceType.class, record.getValue( INVOICE.TYPE )));
			invoice.setSeries(record.getValue( INVOICE.SERIES ));
			invoice.setNumber(record.getValue( INVOICE.NUMBER ));
			invoice.setReferenceCode(record.getValue( INVOICE.REFERENCE_CODE ));
			invoice.setIssueDate(record.getValue( INVOICE.ISSUE_DATE ));
			invoice.setTaxDate(record.getValue( INVOICE.TAX_DATE ));
			invoice.setRegistry(record.getValue(INVOICE.REGISTRY));
			invoice.setRegistryDocument(record.getValue( INVOICE.RDOCUMENT ));
			invoice.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class, record.getValue( INVOICE.RDOCUMENT_TYPE)));
			invoice.setRegistryDocumentCountry(Country.safeValueOf(record.getValue( INVOICE.RDOCUMENT_COUNTRY )));
			invoice.setRegistryName(record.getValue( INVOICE.RNAME ));
			invoice.setRegistryProvinceCode(record.getValue( GEOZONE.CODE ));
			invoice.setRegistryProvince(record.getValue( GEOZONE.NAME ));
			invoice.setRegistryTown(record.getValue( RADDRESS.CITY ));
			invoice.setRegistryZIP(record.getValue( RADDRESS.ZIP ));
			
			detail.setInvoice(invoice);
			
			detail.setProject( record.getValue( PROJECT.NAME ));
			detail.setLine(record.getValue( INVOICE_DETAIL.LINE ));
			detail.setDescription(record.getValue( INVOICE_DETAIL.DESCRIPTION ));
			detail.setQuantity(record.getValue(INVOICE_DETAIL.QUANTITY));
			detail.setPrice(record.getValue(INVOICE_DETAIL.PRICE));
			detail.setDiscountExpression(record.getValue(INVOICE_DETAIL.DISCOUNT_EXPR));
			detail.setTaxableBase(record.getValue(INVOICE_DETAIL.TAXABLE_BASE));
			
			if (record.getValue(INVOICE_DETAIL.ITEM) != null) {
				Item item = new Item();
				item.setId(record.getValue(INVOICE_DETAIL.ITEM));
				item.setCategory( record.getValue( PCATEGORY.NAME ) );
				item.setProductId( record.getValue( PRODUCT.ID ) );
				item.setName( record.getValue( PRODUCT.NAME ) );
				item.setCode(record.getValue( PRODUCT.CODE ) );
				item.setDetail(record.getValue( ITEM.DETAIL ));
				item.setDetail2(record.getValue( ITEM.DETAIL2 ));
				item.setDetail3(record.getValue( ITEM.DETAIL3 ));
				item.setDescription(record.getValue( ITEM.DESCRIPTION ));
				detail.setItem(item);
			}
			
			if (record.getValue(INVOICE_DETAIL.SELLER) != null) {
				Seller seller = new Seller();
				seller.setId( record.getValue(INVOICE_DETAIL.SELLER) );
				seller.setRegistryName( record.getValue(SELLER_ALIAS.NAME) );
				detail.setSeller(seller);	
			}
			
			detail.setWorkPlace(record.getValue(WORKPLACE.DESCRIPTION));
			detail.setWarehouse(record.getValue(WAREHOUSE.NAME));
			
			return detail;
		}
		
	}
	
	private static Collection<Condition> getConditions(InvoiceFilter filter) {
		LinkedList<Condition> list = new LinkedList<Condition>();
		list.add(INVOICE.DOMAIN.equal(filter.getDomain()));
		if ( !filter.isPurchasesEnabled() ) {
			list.add(INVOICE.TYPE.notEqual((byte) 0));
		}
		if ( !filter.isSalesEnabled() ) {
			list.add(INVOICE.TYPE.notEqual((byte) 1));
		}
		if ( !filter.isExpensesEnabled() ) {
			list.add(INVOICE.TYPE.notEqual((byte) 2));
		}
		if ( !filter.isUndeductibleExpensesEnabled() ) {
			list.add(INVOICE.TYPE.notEqual((byte) 3));
		}
		if ( filter.getFromDate() != null) {
			list.add(INVOICE.ISSUE_DATE.greaterOrEqual(AonDateUtils.toSql( filter.getFromDate())));	
		}
		if ( filter.getToDate() != null) {
			list.add(INVOICE.ISSUE_DATE.lessOrEqual(AonDateUtils.toSql( filter.getToDate())));	
		}
		return list;
	}
}
