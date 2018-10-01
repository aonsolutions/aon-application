package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Date;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertValuesStep17;
import org.jooq.Record;
import org.jooq.Record17;

import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.impl.jooq.validation.FeeValidation;



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
	}
	
	public static Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter){
		return ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(REGISTRY).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(FEE_PROPERTIES.getConditions(filter))
				.and(CUSTOMER.STATUS.eq(CustomerStatus.ACTIVE.value()))
				.orderBy(CUSTOMER_FEE.LINE)
			.fetch().stream().map(new FeeFiller());
	}
	
	public static class FeeFiller  implements Function<Record, Fee> {

		@Override
		public Fee apply(Record r) {
			return new Fee()
					.setProductCode(r.getValue(PRODUCT.CODE))
					.setId(r.getValue(CUSTOMER_FEE.ID))
					.setDomain(r.getValue(CUSTOMER_FEE.DOMAIN))
					.setDescription(r.getValue(CUSTOMER_FEE.DESCRIPTION))
					.setSecurityLevel(r.getValue(CUSTOMER_FEE.SECURITY_LEVEL))
					.setBillingDate(r.getValue(CUSTOMER_FEE.BILLING_DATE))
					.setBillingGroup(r.getValue(CUSTOMER_FEE.INVOICING_GROUP))
					.setCustomer(r.getValue(CUSTOMER_FEE.CUSTOMER))
					.setCustomerName(r.getValue(REGISTRY.NAME))
					.setDiscountExpr(r.getValue(CUSTOMER_FEE.DISCOUNT_EXPR))
					.setStartDate(r.getValue(CUSTOMER_FEE.INITIAL_DATE))
					.setEndDate(r.getValue(CUSTOMER_FEE.FINAL_DATE))
					.setItemId(r.getValue(CUSTOMER_FEE.ITEM))
					.setLine(r.getValue(CUSTOMER_FEE.LINE))
					.setPeriod(r.getValue(CUSTOMER_FEE.PERIOD))
					.setPrice(r.getValue(CUSTOMER_FEE.PRICE))
					.setProjectId(r.getValue(CUSTOMER_FEE.PROJECT))
					.setQuantity(r.getValue(CUSTOMER_FEE.QUANTITY))
					.setSellerId(r.getValue(CUSTOMER_FEE.SELLER))
					.setWorkplaceId(r.getValue(CUSTOMER_FEE.WORKPLACE));
		}
	}

	public static Fee getFee(AONContext ctx, Integer id){
		ctx.checkRead();
		
		Record17<Integer, Integer, Integer, Short, Integer, String, Double, Double, String, Date, Date, Date, Short, Byte, Integer, Integer, Integer> record = ctx.getDslContext()
			.select(CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE)
			.from(CUSTOMER_FEE)
			.where(CUSTOMER_FEE.ID.eq(id))
			.fetchOne();
		if(record != null){
			Fee fee = new Fee();
			fee.setId(id);
			if(record.value1() != null) fee.setDomain(record.value1());
			if(record.value2() != null) fee.setProjectId(record.value2());
			if(record.value3() != null) fee.setClientId(record.value3());
			if(record.value4() != null) fee.setLine(record.value4());
			if(record.value5() != null) fee.setItemId(record.value5());
			if(record.value6() != null) fee.setDescription(record.value6());
			if(record.value7() != null) fee.setQuantity(record.value7());
			if(record.value8() != null) fee.setPrice(record.value8());
			if(record.value9() != null) fee.setDiscountExpr(record.value9());
			if(record.value10() != null) fee.setStartDate(record.value10());
			if(record.value11() != null) fee.setEndDate(record.value11());
			if(record.value12() != null) fee.setBillingDate(record.value12());
			if(record.value13() != null) fee.setPeriod(record.value13());
			if(record.value14() != null) fee.setConfidential(record.value14() == 1);
			if(record.value15() != null) fee.setBillingGroup(record.value15());
			if(record.value16() != null) fee.setSellerId(record.value16());
			if(record.value17() != null) fee.setWorkplaceId(record.value17());
			return fee;
		}
		return null;
	}
	
	public static void insert(AONContext ctx, Fee f) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			FeeValidation.validate(ctx, f);
			ctx.getDslContext()
				.insertInto(PRODUCT, PRODUCT.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE)
				.values(f.getDomain(), f.getProjectId(), f.getClientId(), f.getLine(), f.getItemId(), f.getDescription(), f.getQuantity(), f.getPrice(), f.getDiscountExpr(), new Date(f.getStartDate().getTime()), new Date(f.getEndDate().getTime()), new Date(f.getBillingDate().getTime()), f.getPeriod(), f.getSecurityLevel(), f.getBillingGroup(), f.getSellerId(), f.getWorkplaceId())
				.execute();
		});
	}

	public static void insert(AONContext ctx, Stream<Fee> fs) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep17<ProductRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, Date, Date, Date, Short, Byte, Integer, Integer, Integer> insertQuery = ctx.getDslContext().insertInto(PRODUCT, PRODUCT.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
			fs.forEach(f ->{
				FeeValidation.validate(ctx, f);
				insertQuery.values(f.getDomain(), f.getProjectId(), f.getClientId(), f.getLine(), f.getItemId(), f.getDescription(), f.getQuantity(), f.getPrice(), f.getDiscountExpr(), new Date(f.getStartDate().getTime()), new Date(f.getEndDate().getTime()), new Date(f.getBillingDate().getTime()), f.getPeriod(), f.getSecurityLevel(), f.getBillingGroup(), f.getSellerId(), f.getWorkplaceId());
			});
			insertQuery.execute();
		});
	}

	public static void update(AONContext ctx, Fee f) {
		
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			FeeValidation.validate(ctx, f);
			ctx.getDslContext()
				.update(CUSTOMER_FEE)
					.set(CUSTOMER_FEE.DOMAIN, f.getDomain())
					.set(CUSTOMER_FEE.PROJECT, f.getProjectId())
					.set(CUSTOMER_FEE.CUSTOMER, f.getClientId())
					.set(CUSTOMER_FEE.LINE, f.getLine())
					.set(CUSTOMER_FEE.ITEM, f.getItemId())
					.set(CUSTOMER_FEE.DESCRIPTION, f.getDescription())
					.set(CUSTOMER_FEE.QUANTITY, f.getQuantity())
					.set(CUSTOMER_FEE.PRICE, f.getPrice())
					.set(CUSTOMER_FEE.DISCOUNT_EXPR, f.getDiscountExpr())
					.set(CUSTOMER_FEE.INITIAL_DATE, new Date(f.getStartDate().getTime()))
					.set(CUSTOMER_FEE.FINAL_DATE, new  Date(f.getEndDate().getTime()))
					.set(CUSTOMER_FEE.BILLING_DATE, new  Date(f.getBillingDate().getTime()))
					.set(CUSTOMER_FEE.PERIOD, f.getPeriod())
					.set(CUSTOMER_FEE.SECURITY_LEVEL, f.getSecurityLevel())
					.set(CUSTOMER_FEE.INVOICING_GROUP, f.getBillingGroup())
					.set(CUSTOMER_FEE.SELLER, f.getSellerId())
					.set(CUSTOMER_FEE.WORKPLACE, f.getWorkplaceId())
					.where(CUSTOMER_FEE.ID.equal(f.getId()))
					.execute();
		});
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
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			Vector<Integer> ids = new Vector<Integer>();
			fs.forEach(f ->{
				ids.add(f.getId());
			});
			ctx.getDslContext()
				.delete(CUSTOMER_FEE)
				.where(CUSTOMER_FEE.ID.in(ids)).execute();
		});
		
	}
	
	
	
	
}
