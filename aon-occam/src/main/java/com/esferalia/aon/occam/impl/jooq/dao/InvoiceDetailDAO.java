package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
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
import org.jooq.Record;
import org.jooq.SelectSeekStep1;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDetailProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceDetailDAO {
   
    private InvoiceDetailDAO() {
     
    }
	
	private static final InvoiceDetailPropertiesDAO INVOICE_DETAIL_PROPERTIES = new InvoiceDetailPropertiesDAO();
	public static class InvoiceDetailPropertiesDAO implements InvoiceDetailProperties {
		
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
	
	
	
	public static SelectSeekStep1<Record, Short> select(AONContext ctx, InvoiceDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_DETAIL)
				.where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
				.orderBy(INVOICE_DETAIL.LINE);
	}
	
	public static SelectSeekStep1<Record, Short> selectFull(AONContext ctx, InvoiceDetailFilter filter){  
        return ctx.getDslContext()
                .select()
                .from(INVOICE_DETAIL)
                .leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
                .leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
                .leftOuterJoin(PROJECT).on(INVOICE_DETAIL.PROJECT.eq(PROJECT.ID))
                .leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
                .leftOuterJoin(BRAND).on(PRODUCT.BRAND.equal(BRAND.ID))
                .leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
                .leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
                .leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
                .where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
                .orderBy(INVOICE_DETAIL.LINE);
    }
	
	public static Stream<InvoiceDetail> getStream(AONContext ctx, InvoiceDetailFilter filter){	
		return select(ctx, filter).fetch().stream().map(new InvoiceDetailFiller());
	}
	
	public static Stream<InvoiceDetail> getFullStream(AONContext ctx, InvoiceDetailFilter filter){ 
        return selectFull(ctx, filter).fetch().stream().map(new InvoiceDetailFiller());
    }
	
	public static Stream<InvoiceDetail> getStream(AONContext ctx, InvoiceDetailFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new InvoiceDetailFiller());
	}
	
	public static Stream<InvoiceDetail> getFullStream(AONContext ctx, InvoiceDetailFilter filter, Integer page, Integer perPage){   
	    return selectFull(ctx, filter)
	        .limit(perPage)
	        .offset(perPage * (page -1))
	        .fetch().stream().map(new InvoiceDetailFiller());
	}
	
	public static List<InvoiceDetail> getList(AONContext ctx, InvoiceDetailFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<InvoiceDetail> getList(AONContext ctx, InvoiceDetailFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<InvoiceDetail> getFullList(AONContext ctx, InvoiceDetailFilter filter) {  
        return getFullStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
    }
	
	public static List<InvoiceDetail> getFullList(AONContext ctx, InvoiceDetailFilter filter, Integer page, Integer perPage){  
        return getFullStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
    }
	
	public static InvoiceDetail get(AONContext ctx, InvoiceDetailFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceDetailFiller())
			.findFirst().orElse(new InvoiceDetail());
	}
	
	
	public static List<InvoiceDetail> save(AONContext ctx, List<InvoiceDetail> invoiceDetails) {
		LinkedList<InvoiceDetail> list = new LinkedList<>();
		invoiceDetails.stream().forEach(invoiceDetail -> 
			list.add(save(ctx, invoiceDetail)));
		return list;
	}
	
	public static InvoiceDetail save(AONContext ctx, InvoiceDetail invoiceDetail) {
		// TODO AUTOCOMPLETE && VALIDATE.
		invoiceDetail = invoiceDetail.getId() != null 
			? update(ctx, invoiceDetail)
			: insert(ctx, invoiceDetail);
		InvoiceTaxDAO.save(ctx, invoiceDetail.getInvoiceTaxes());	
		return invoiceDetail;
	}
	
	public static List<InvoiceDetail> update(AONContext ctx, List<InvoiceDetail> invoiceDetails) {
		invoiceDetails.stream().forEach(invoiceDetail -> update(ctx, invoiceDetail));
		return invoiceDetails;
	}
	
	public static InvoiceDetail update(AONContext ctx, InvoiceDetail invoiceDetail) {
		ctx.getDslContext().update(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getId())
		.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset())
		.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
		.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
		.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
		.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression())
		.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
		.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
		.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
		.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
		.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
		.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
		.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkPlace() )
		.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
		.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
		.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
		.execute();
		return invoiceDetail;
	}
	
	public static InvoiceDetail insert(AONContext ctx, InvoiceDetail invoiceDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getId())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset())
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression())
			.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkPlace() )
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
			.set(INVOICE_DETAIL.CREATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_DETAIL.ID).fetchOne().getId();
		return invoiceDetail.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, InvoiceDetailFilter filter){
		ctx.getDslContext().delete(INVOICE_DETAIL)
		.where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class InvoiceDetailFiller extends Filler implements Function<Record, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDetail build(Record r) {
			return new InvoiceDetail()
					.setId(r.getValue(INVOICE_DETAIL.ID))
					.setInvoice(checkField(r, INVOICE.ID)
						? InvoiceFiller.buildInvoice(r)
						: new Invoice().setId(r.getValue(INVOICE_DETAIL.ID)))
					.setProject(r.getValue(INVOICE_DETAIL.PROJECT))
					.setProjectName(getValue(r, PROJECT.NAME))
					.setLine(r.getValue(INVOICE_DETAIL.LINE))
					.setDescription(r.getValue(INVOICE_DETAIL.DESCRIPTION ))
					.setQuantity(r.getValue(INVOICE_DETAIL.QUANTITY))
					.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
					.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
					.setTaxableBase(r.getValue(INVOICE_DETAIL.TAXABLE_BASE))
					.setItem(checkField(r, ITEM.ID)
							? ItemFiller.build(r)
							: new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
					.setSeller(checkField(r, SELLER.REGISTRY) 
							? SellerFiller.build(r)
							: new Seller().setId(r.getValue(INVOICE_DETAIL.SELLER)))
					.setWorkplace(checkField(r, WORKPLACE.ID) 
							? WorkplaceFiller.build(r)
							: new Workplace().setId(r.getValue(INVOICE_DETAIL.WORKPLACE)))
					.setWarehouse(r.getValue(INVOICE_DETAIL.WAREHOUSE))
					.setWarehouseName(getString(r, WAREHOUSE.NAME))
					.setAccount(getValue(r,ACCOUNT.ID))
					.setAccountCode(getValue(r, ACCOUNT.CODE))
					.setAccountDescription(getValue(r, ACCOUNT.DESCRIPTION))
					.setSource(InvoiceSource.safeValueOf(r.getValue(INVOICE_DETAIL.SOURCE)))
					.setSourceId(r.getValue(INVOICE_DETAIL.SOURCE_ID));
		}
	}
}
