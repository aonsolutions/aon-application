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
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.OutputStream;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
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

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(INVOICE.ID);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(INVOICE.DOMAIN);
		}

		@Override
		public Property<Date> getStartIssueDateProperty() {
			return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);
		}

		@Override
		public Property<Date> getEndIssueDateProperty() {
			return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);
		}

		@Override
		public Property<Byte> getTypeProperty() {
			return new FilterDAO.PropertyDAO<Byte>(INVOICE.TYPE);
		}

		@Override
		public Property<Integer> getScopeProperty() {
			return new FilterDAO.PropertyDAO<Integer>(INVOICE.SCOPE);
		}

		@Override
		public Property<Byte> getConfidentialProperty() {
			return new FilterDAO.PropertyDAO<Byte>(INVOICE.SECURITY_LEVEL);
		}
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
				,GEOZONE.CODE
				,GEOZONE.NAME
				,RADDRESS.ZIP
				,RADDRESS.CITY
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
	
	
	public static void getInvoicesFormatCSV(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatCSV(out,'\t',"");
	}

	public static void getInvoicesFormatHTML(AONContext ctx,
			InvoiceFilter filter, OutputStream out) {
		getFullInvoices(ctx, filter).formatHTML(out);
	}

	public static void getInvoiceDetails(AONContext ctx,
			Consumer<InvoiceDetail> action, InvoiceFilter filter) {
		getFullInvoices(ctx, filter)
			.stream()
			.map(new FullInvoiceDetailFiller())
			.forEach(action);
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
					.setScope(record.getValue(SCOPE.DESCRIPTION))						
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
	
}
