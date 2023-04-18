package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoicingGroupFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.occam.impl.jooq.validation.FeeValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FeeDAO {
	
	private static final FeePropertiesDAO FEE_PROPERTIES = new FeePropertiesDAO();
	private static class FeePropertiesDAO implements FeeProperties {
		private Condition[] getConditions(FeeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.DOMAIN);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.CUSTOMER);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(CUSTOMER_FEE.DESCRIPTION);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.PROJECT);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<Short>(CUSTOMER_FEE.LINE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(CUSTOMER_FEE.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<Double>(CUSTOMER_FEE.PRICE);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<String>(CUSTOMER_FEE.DISCOUNT_EXPR);}
		@Override public Property<Date> getInitialDateProperty() {return new FilterDAO.PropertyDAO<Date>(CUSTOMER_FEE.INITIAL_DATE);}
		@Override public Property<Date> getFinalDateProperty() {return new FilterDAO.PropertyDAO<Date>(CUSTOMER_FEE.FINAL_DATE);}
		@Override public Property<Date> getBillingDateProperty() {return new FilterDAO.PropertyDAO<Date>(CUSTOMER_FEE.BILLING_DATE);}
		@Override public Property<Short> getPeriodProperty() {return new FilterDAO.PropertyDAO<Short>(CUSTOMER_FEE.PERIOD);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER_FEE.SECURITY_LEVEL);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.INVOICING_GROUP);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.SELLER);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER_FEE.WORKPLACE);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.CATEGORY);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(CUSTOMER.SCOPE);}
		@Override public Property<Integer> getSegmentProperty() {return new FilterDAO.PropertyDAO<Integer>(RSEGMENT.SEGMENT);}
	}
	
	public static LinkedList<Fee> getFeeList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createFeeCondition(customerFeeParams);
		
		Result<Record> feeRecords = ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(SELLER).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP))
				.where(condition)
				.orderBy(CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Customer Fee size : " + feeRecords.size());
		
		return feeRecords.stream().map(new FeeFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static Condition createFeeCondition(CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain())
				.and(CUSTOMER_FEE.BILLING_DATE.eq(parseSQLDate(customerFeeParams.getBillingDate())))
				.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.ge(parseSQLDate(customerFeeParams.getBillingDate()))));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getCustomer())) 
			condition = condition.and(CUSTOMER_ALIAS.NAME.eq(customerFeeParams.getCustomer()));
		if(AonStringUtils.isNotBlank(customerFeeParams.getProductCode()))
			condition = condition.and(PRODUCT.CODE.eq(customerFeeParams.getProductCode()));
		if(null != customerFeeParams.getCustomerStatus())
			condition = condition.and(CUSTOMER.STATUS.eq(customerFeeParams.getCustomerStatus()));
		
		return condition;
	}

	public static Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter){
		Result<Record> feeRecords = ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(SELLER).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP))
				.where(FEE_PROPERTIES.getConditions(filter))
				.orderBy(CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE)
			.fetch();
		
		System.out.println("Customer Fee size : " + feeRecords.size());
		
		return feeRecords.stream().map(new FeeFiller());
	}
	
	protected static class FeeFiller extends Filler implements Function<Record, Fee> {

		@Override
		public Fee apply(Record r) {
			return buildFee(r);
		}
		
		public static Fee buildFee(Record r) {			
			return new Fee()
				.setId(r.getValue(CUSTOMER_FEE.ID))
				.setDomain(checkField(r, DOMAIN.ID) 
					? DomainFiller.build(r) 
					: new Domain().setId(r.getValue(CUSTOMER_FEE.DOMAIN)) )
				.setCustomer(checkField(r, CUSTOMER.REGISTRY)
					? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
					: new Customer().copy(new Registry().setId(r.getValue(CUSTOMER_FEE.CUSTOMER))))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.buildItem(r)
					: new OldItem().setId(r.getValue(CUSTOMER_FEE.ITEM)))			
				.setDescription(r.getValue(CUSTOMER_FEE.DESCRIPTION))
				.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(CUSTOMER_FEE.SECURITY_LEVEL)))
				.setStartDate(r.getValue(CUSTOMER_FEE.INITIAL_DATE))
				.setEndDate(r.getValue(CUSTOMER_FEE.FINAL_DATE))
				.setBillingDate(r.getValue(CUSTOMER_FEE.BILLING_DATE))
				.setInvoicingGroup(checkField(r, INVOICING_GROUP.ID)
					? InvoicingGroupFiller.buildInvoicingGroup(r)
					: new InvoicingGroup().setId(r.getValue(CUSTOMER_FEE.INVOICING_GROUP)))
				.setDiscountExpr(r.getValue(CUSTOMER_FEE.DISCOUNT_EXPR))
				.setLine(r.getValue(CUSTOMER_FEE.LINE))
				.setPeriod(BillingPeriod.values()[r.getValue(CUSTOMER_FEE.PERIOD)])
				.setPrice(r.getValue(CUSTOMER_FEE.PRICE))
				.setProject(new Project().setId(r.getValue(CUSTOMER_FEE.PROJECT)))
				.setQuantity(r.getValue(CUSTOMER_FEE.QUANTITY))
				.setSeller(new Seller().setId(r.getValue(CUSTOMER_FEE.SELLER)))
				.setSeller(checkField(r, SELLER.REGISTRY)
					? SellerFiller.build(r)
					: new Seller().setId(r.getValue(CUSTOMER_FEE.SELLER)))
				.setWorkplace(checkField(r, WORKPLACE.ID)
					? WorkplaceFiller.build(r)
					: new Workplace().setId(r.getValue(CUSTOMER_FEE.WORKPLACE)));
		}
	}

	public static Fee getFee(AONContext ctx, Integer id){
		ctx.checkRead();
		return getFeeStream(ctx, f -> f.getIdProperty().eq(id)).findFirst().orElse(new Fee());
	}
	
	public static Fee getFee(AONContext ctx, FeeFilter filter){
		ctx.checkRead();
		return getFeeStream(ctx, filter).findFirst().orElse(new Fee());
	}
	
	public static Fee save(AONContext ctx, Fee fee) {
		ctx.checkWrite();
		FeeValidation.validate(ctx, fee);
		if(fee.getLine() != null) {
			Fee uf = getFee(ctx, f -> f.getDomainProperty().eq(fee.getDomain().getId())
				.and(f.getCustomerProperty().eq(fee.getCustomer().getId()))
				.and(f.getLineProperty().eq(fee.getLine())));
			fee.setId(uf.getId());
		} else {
			Result<Record1<Short>> n = ctx.getDslContext().select(DSL.max(CUSTOMER_FEE.LINE))
					.from(CUSTOMER_FEE)
					.where(CUSTOMER_FEE.DOMAIN.eq(ctx.getDomainId()).and(CUSTOMER_FEE.CUSTOMER.eq(fee.getCustomer().getId()))).fetch();
			fee.setLine((n.isEmpty() || n.get(0).value1()==null) ? (short) 1 :  (short) (n.get(0).value1() + 1));
		}
		return fee.getId() != null ? update(ctx, fee) : insert(ctx, fee);
	}
	
	public static void saveList(AONContext ctx, LinkedList<Fee> feeList) {
		feeList.stream()
			.filter(fee -> fee.isModify())
			.forEach(fee -> {
				FeeValidation.validate(ctx, fee);
				
				ctx.getDslContext()
					.update(CUSTOMER)
						.set(CUSTOMER.STATUS, fee.getCustomer().getStatus().value())
						.where(CUSTOMER.REGISTRY.eq(fee.getCustomer().getId()))
						.execute();
				
				update(ctx, fee);
			});
	}
	
	private static Fee insert(AONContext ctx, Fee fee) {
		Date startDate = fee.getStartDate() != null ? new Date(fee.getStartDate().getTime()) : null;
		Date endDate = fee.getEndDate() != null ? new Date(fee.getEndDate().getTime()) : null;
		Date billingDate = fee.getBillingDate() != null ? new Date(fee.getBillingDate().getTime()) : null;
		
		Integer id = ctx.getDslContext()
			.insertInto(CUSTOMER_FEE, CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE)
			.values(fee.getDomain().getId(), fee.getProject().getId(), fee.getCustomer().getId(), fee.getLine(), fee.getItem().getId(), fee.getDescription(), fee.getQuantity(), fee.getPrice(), fee.getDiscountExpr(), startDate, endDate, billingDate, (short) fee.getPeriod().value(), fee.getSecurityLevel().value(), fee.getInvoicingGroup().getId(), fee.getSeller().getId(), fee.getWorkplace().getId())
			.returning(CUSTOMER_FEE.ID).fetchOne().getValue(CUSTOMER_FEE.ID);
		return fee.setId(id);
	}

//	MULTIPLE FEE INSERT
//	
//	private static void insert(AONContext ctx, Stream<Fee> fs) {
//		ctx.checkWrite();
//		ctx.getDslContext().transaction(configuration -> {
//			InsertValuesStep17<ProductRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, Date, Date, Date, Short, Byte, Integer, Integer, Integer> insertQuery = ctx.getDslContext().insertInto(PRODUCT, PRODUCT.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
//			fs.forEach(f ->{
//				FeeValidation.validate(ctx, f);
//				insertQuery.values(f.getDomain().getId(), f.getProject().getId(), f.getCustomer().getId(), f.getLine(), f.getItem().getId(), f.getDescription(), f.getQuantity(), f.getPrice(), f.getDiscountExpr(), new Date(f.getStartDate().getTime()), new Date(f.getEndDate().getTime()), new Date(f.getBillingDate().getTime()), (short) f.getPeriod().value(), f.getSecurityLevel().value(), f.getInvoicingGroup().getId(), f.getSeller().getId(), f.getWorkplace().getId());
//			});
//			insertQuery.execute();
//		});
//	}

	private static Fee update(AONContext ctx, Fee f) {
		Date startDate = f.getStartDate() != null ? new Date(f.getStartDate().getTime()) : null;
		Date endDate = f.getEndDate() != null ? new Date(f.getEndDate().getTime()) : null;
		Date billingDate = f.getBillingDate() != null ? new Date(f.getBillingDate().getTime()) : null;
		
		ctx.getDslContext()
			.update(CUSTOMER_FEE)
				.set(CUSTOMER_FEE.DOMAIN, f.getDomain().getId())
				.set(CUSTOMER_FEE.PROJECT, f.getProject().getId())
				.set(CUSTOMER_FEE.CUSTOMER, f.getCustomer().getId())
				.set(CUSTOMER_FEE.LINE, f.getLine())
				.set(CUSTOMER_FEE.ITEM, f.getItem().getId())
				.set(CUSTOMER_FEE.DESCRIPTION, f.getDescription())
				.set(CUSTOMER_FEE.QUANTITY, f.getQuantity())
				.set(CUSTOMER_FEE.PRICE, f.getPrice())
				.set(CUSTOMER_FEE.DISCOUNT_EXPR, f.getDiscountExpr())
				.set(CUSTOMER_FEE.INITIAL_DATE, startDate)
				.set(CUSTOMER_FEE.FINAL_DATE, endDate)
				.set(CUSTOMER_FEE.BILLING_DATE, billingDate)
				.set(CUSTOMER_FEE.PERIOD, (short) f.getPeriod().value())
				.set(CUSTOMER_FEE.SECURITY_LEVEL, f.getSecurityLevel().value())
				.set(CUSTOMER_FEE.INVOICING_GROUP, f.getInvoicingGroup().getId())
				.set(CUSTOMER_FEE.SELLER, f.getSeller().getId())
				.set(CUSTOMER_FEE.WORKPLACE, f.getWorkplace().getId())
				.where(CUSTOMER_FEE.ID.equal(f.getId()))
				.execute();
		return f;
	}

	public static void delete(AONContext ctx, Fee f) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(CUSTOMER_FEE)
				.where(CUSTOMER_FEE.ID.equal(f.getId())).execute();
		});
	}

	public static void delete(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			Integer[] ids = fs.map(f -> f.getId()).toArray(Integer[]::new);
			delete(ctx, f -> f.getIdProperty().in(ids));
		});
	}
	
	public static void delete(AONContext ctx, FeeFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(CUSTOMER_FEE)
			.where(FEE_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	private static java.sql.Date parseSQLDate(java.util.Date date){
		return null == date ? null : new java.sql.Date(date.getTime());
	}
	
	// --------------------------------------------------------------------
	// 					CUSTOMER / PRODUCT SUGGESTIONS
	// --------------------------------------------------------------------

	public static LinkedList<String> getProductsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		List<String> customerRecords = ctx.getDslContext().selectDistinct(PRODUCT.CODE)
			.from(PRODUCT)
			.where(PRODUCT.DOMAIN.eq(domainId))
			.and(PRODUCT.CODE.containsIgnoreCase(query))
			.and(PRODUCT.CODE.isNotNull())
			.fetch(PRODUCT.CODE);
		
		return new LinkedList<>(customerRecords);
	}

	public static LinkedList<String> getCustomersSuggestion(CloseableAONContext ctx, int domainId, String query) {
		List<String> customerRecords = ctx.getDslContext().selectDistinct(REGISTRY.NAME)
			.from(REGISTRY)
			.join(CUSTOMER)
			.on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
			.where(CUSTOMER.DOMAIN.eq(domainId))
			.and(REGISTRY.NAME.isNotNull())
			.and(REGISTRY.NAME.containsIgnoreCase(query)
					.or(REGISTRY.DOCUMENT.containsIgnoreCase(query))
					.or(REGISTRY.ALIAS.isNotNull().and(REGISTRY.ALIAS.containsIgnoreCase(query)))
			).fetch(REGISTRY.NAME);
		
		return new LinkedList<>(customerRecords);
	}
	
}
