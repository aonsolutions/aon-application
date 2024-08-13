package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectSeekStep1;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDetailProperties;
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
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceDetailDAO {
   
    private InvoiceDetailDAO() {
     
    }
	
	private static final InvoiceDetailPropertiesDAO INVOICE_DETAIL_PROPERTIES = new InvoiceDetailPropertiesDAO();
	private static class InvoiceDetailPropertiesDAO implements InvoiceDetailProperties {
		
		public Condition[] getConditions(InvoiceDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DOMAIN);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.MODIFICATION_DATE);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.INVOICE);}
		@Override public Property<Integer> getInvestAssetProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.INVEST_ASSET);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.LINE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SOURCE_ID);}
		@Override public Property<Double> getTaxableBaseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.TAXABLE_BASE);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.TAXES);}
		@Override public Property<Byte> getPrepaymentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.PREPAYMENT);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SELLER);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WORKPLACE);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WAREHOUSE);}
	}
	
	
	
	private static SelectSeekStep1<Record, Short> select(AONContext ctx, InvoiceDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_DETAIL)
				.where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
				.orderBy(INVOICE_DETAIL.LINE);
	}
	
	private static SelectSeekStep1<Record, Short> selectFull(AONContext ctx, InvoiceDetailFilter filter){  
        return ctx.getDslContext()
                .select()
                .from(INVOICE_DETAIL)
                .leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
                .leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
                .leftOuterJoin(PROJECT).on(INVOICE_DETAIL.PROJECT.eq(PROJECT.ID))
                .leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
                .leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
                .leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.eq(INVOICE_DETAIL.SELLER))
                .leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.eq(INVOICE_DETAIL.WAREHOUSE))
                .leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INVOICE_DETAIL.WORKPLACE))
                .leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.eq(INVOICE_DETAIL.INVEST_ASSET))
                .where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
                .orderBy(INVOICE_DETAIL.LINE);
    }
	
	private static Stream<InvoiceDetail> getFullStream(AONContext ctx, InvoiceDetailFilter filter){ 
        return selectFull(ctx, filter).fetch().stream().map(new InvoiceDetailFiller());
    }
	
	static List<InvoiceDetail> getFullList(AONContext ctx, InvoiceDetailFilter filter) {  
        return getFullStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
    }
	
	static Stream<InvoiceDetail> getDetails(AONContext ctx, Integer invoiceId) {  
        return getFullStream(ctx, p -> p.getInvoiceProperty().eq(invoiceId));
    }

	public static InvoiceDetail get(AONContext ctx, InvoiceDetailFilter filter) {
		return select(ctx, filter)
				.limit(1)
				.fetch()
				.stream()
				.map(new InvoiceDetailFiller())
				.findFirst()
				.orElse(new InvoiceDetail());
	}
	
	static List<InvoiceDetail> save(AONContext ctx, List<InvoiceDetail> invoiceDetails) {
		LinkedList<InvoiceDetail> list = new LinkedList<>();
		invoiceDetails.stream().forEach(invoiceDetail -> 
			list.add(save(ctx, invoiceDetail)));
		return list;
	}
	
	static InvoiceDetail save(AONContext ctx, InvoiceDetail invoiceDetail) {
		invoiceDetail = invoiceDetail.getId() != null 
			? update(ctx, invoiceDetail)
			: insert(ctx, invoiceDetail);
		
		if (invoiceDetail.getInvoice().getType() != InvoiceType.UNDEDUCTIBLE && !invoiceDetail.isPrepayment()) {
			InvoiceTaxDAO.save(ctx, invoiceDetail.getInvoiceTaxes(), invoiceDetail);	
		} else {
			ctx.log().debug("\t\tSKIPPING INVOICE TAX CREATION ({0})",(invoiceDetail.isPrepayment()? "PREPAYMENT": "UNDEDUCTIBLE INVOICE"));
		}

		return invoiceDetail;
	}
	
	private static InvoiceDetail update(AONContext ctx, InvoiceDetail invoiceDetail) {
		ctx.getDslContext().update(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice().getId())
		.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(ias -> ias.getId()).orElse(null))
		.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
		.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
		.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
		.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().getDiscountExpr())
		.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
		.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
		.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
		.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
		.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
		.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
		.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace() == null ? null : invoiceDetail.getWorkplace().getId())
		.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
		.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
		.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
		.where(INVOICE_DETAIL.ID.eq(invoiceDetail.getId()))
		.execute();
		return invoiceDetail;
	}
	
	private static InvoiceDetail insert(AONContext ctx, InvoiceDetail invoiceDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice().getId())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(ias -> ias.getId()).orElse(null))
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().getDiscountExpr())
			.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace() == null ? null : invoiceDetail.getWorkplace().getId())
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
			.set(INVOICE_DETAIL.CREATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_DETAIL.ID).fetchOne().getId();
		return invoiceDetail.setId(id);
	}	

	public static class InvoiceDetailFiller extends Filler implements Function<Record, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDetail build(Record r) {
			return new InvoiceDetail()
					.setId(getValue(r, INVOICE_DETAIL.ID))
					.setDomain(getValue(r, INVOICE_DETAIL.DOMAIN))
					.setInvoice(checkField(r, INVOICE.ID)
						? InvoiceFiller.build(r)
						: new Invoice().setId(r.getValue(INVOICE_DETAIL.INVOICE)))
					.setProject(getValue(r, INVOICE_DETAIL.PROJECT))
					.setProjectName(getValue(r, PROJECT.NAME))
					.setLine(getValue(r, INVOICE_DETAIL.LINE))
					.setDescription(getValue(r, INVOICE_DETAIL.DESCRIPTION ))
					.setQuantity(getValue(r, INVOICE_DETAIL.QUANTITY))
					.setPrice(getValue(r, INVOICE_DETAIL.PRICE))
					.setDiscountExpression(getValue(r, INVOICE_DETAIL.DISCOUNT_EXPR))
					.setTaxableBase(getValue(r, INVOICE_DETAIL.TAXABLE_BASE))
					.setItem(checkField(r, ITEM.ID)
							? ItemFiller.build(r)
							: new Item().setId(getValue(r, INVOICE_DETAIL.ITEM)))
					.setSeller(checkField(r, SELLER.REGISTRY) 
							? SellerFiller.build(r)
							: new Seller().setId(getValue(r, INVOICE_DETAIL.SELLER)))
					.setWorkplace(checkField(r, WORKPLACE.ID) 
							? WorkplaceFiller.build(r)
							: new Workplace().setId(getValue(r, INVOICE_DETAIL.WORKPLACE)))
					.setWarehouse(getValue(r, INVOICE_DETAIL.WAREHOUSE))
					.setWarehouseName(getString(r, WAREHOUSE.NAME))
					.setExpAccount( checkField(r, ACCOUNT.ID)
						? FullAccountFiller.build(r)
						: null )
					.setInvestAsset(checkField(r, INVEST_ASSET.ID)
							? InvestAssetFiller.build(r)
							: null )
					.setSource(InvoiceSource.safeValueOf(getValue(r, INVOICE_DETAIL.SOURCE)))
					.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID));
		}
	}
	
	
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
	//	 Los siguientes métodos (hasta final de fichero) deben pasar por refactor.
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------

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
			.where(InvoiceDAO.INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
			.fetch();
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
				.setInvestAsset(checkField(r, INVEST_ASSET.ID)
					? InvestAssetFiller.build(r)
					: null )
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
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
	// 						FIN REFACTOR
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
}
