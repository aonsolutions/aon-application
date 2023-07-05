package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.CustomerRecord;
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
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoicingGroupFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO.ProjectFiller;
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
		@Override public Property<Integer> getMonthBillingDateProperty() {return new FilterDAO.PropertyDAO<Integer>(DSL.month(CUSTOMER_FEE.BILLING_DATE));}
		@Override public Property<Integer> getProductCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.ID);}
		@Override public Property<Integer> getProductTagProperty() {return new FilterDAO.PropertyDAO<Integer>(TAG.ID);}
	}
	
	public static LinkedList<Fee> getFeeList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createFeeCondition(ctx, customerFeeParams);
		SelectOnConditionStep<Record> fromCustomerRecords = ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID))
				.leftOuterJoin(TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(SELLER).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP));
		if (customerFeeParams != null && customerFeeParams.getSegment() != null) {
			if(customerFeeParams.getSegment() == -1)
				fromCustomerRecords = fromCustomerRecords 	
				.leftJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
			else fromCustomerRecords = fromCustomerRecords 	
				.join(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
		}
				
		Result<Record> feeRecords = fromCustomerRecords 
				.where(condition)
				.orderBy(CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Customer Fee size : " + feeRecords.size());
		
		return feeRecords.stream().map(new FeeFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static Condition createFeeCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
		
		if(null != customerFeeParams.getMonth() && null == customerFeeParams.getYear()) {
			condition = condition.and(DSL.month(CUSTOMER_FEE.BILLING_DATE).eq(customerFeeParams.getMonth() + 1));
		} else if(null == customerFeeParams.getMonth() && null != customerFeeParams.getYear()) {
			Date startBillingDate = new Date(customerFeeParams.getYear(), 0, 1);
			Date endBillingDate = new Date(customerFeeParams.getYear(), 11, 31);
			
			condition = condition.and(CUSTOMER_FEE.BILLING_DATE.between(startBillingDate, endBillingDate));	
		} else if(null != customerFeeParams.getMonth() && null != customerFeeParams.getYear()) {
			Date billingDate = new Date(customerFeeParams.getYear(), customerFeeParams.getMonth(), 1);
			
			condition = condition.and(CUSTOMER_FEE.BILLING_DATE.eq(parseSQLDate(billingDate)));
		}
			
		if(null != customerFeeParams.getPeriodicity())
			condition = condition.and(CUSTOMER_FEE.PERIOD.eq(customerFeeParams.getPeriodicity().shortValue()));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getCustomer())) 
			condition = condition.and(CUSTOMER_ALIAS.NAME.eq(customerFeeParams.getCustomer()));
		
		if(null != customerFeeParams.getCustomerStatus())
			condition = condition.and(CUSTOMER.STATUS.eq(customerFeeParams.getCustomerStatus()));
		
		if(null != customerFeeParams.getSegment() && customerFeeParams.getSegment() != -1) 
			condition = condition.and(RSEGMENT.SEGMENT.eq(customerFeeParams.getSegment()));
		
		if(null != customerFeeParams.getStartDate()){
			switch (customerFeeParams.getStartCompare()) {
			case (byte) 1:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.le(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.ge(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			default:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.eq(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			}
		}
		
		if(null != customerFeeParams.getEndDate()){
			switch (customerFeeParams.getEndCompare()) {
			case (byte) 1:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.le(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.ge(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			default:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.eq(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			}
		}
		
		if(null != customerFeeParams.getProduct())
			condition = condition.and(CUSTOMER_FEE.ITEM.eq(customerFeeParams.getProduct()));
		
		if(null != customerFeeParams.getProductCategory())
			condition = condition.and(PCATEGORY.ID.eq(customerFeeParams.getProductCategory()));
		
		if(null != customerFeeParams.getProductTag())
			condition = condition.and(TAG.ID.eq(customerFeeParams.getProductTag()));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getQuantity())) {
			if(AonStringUtils.containsIgnoreCase(customerFeeParams.getQuantity(), ":")) {
				Double quantityStart = Double.parseDouble(customerFeeParams.getQuantity().split(":")[0].trim());
				Double quantityEnd = Double.parseDouble(customerFeeParams.getQuantity().split(":")[1].trim());
				condition = condition.and(CUSTOMER_FEE.QUANTITY.ge(quantityStart));
				condition = condition.and(CUSTOMER_FEE.QUANTITY.le(quantityEnd));
			} else if(AonStringUtils.containsIgnoreCase(customerFeeParams.getQuantity(), ">")) {
				String quantityStr = customerFeeParams.getQuantity().split(">")[1].trim();
				if(AonStringUtils.containsIgnoreCase(quantityStr, "=")) {
					quantityStr = quantityStr.split("=")[1].trim();
					Double quantity = Double.parseDouble(quantityStr);
					condition = condition.and(CUSTOMER_FEE.QUANTITY.ge(quantity));
				} else {
					Double quantity = Double.parseDouble(quantityStr);
					condition = condition.and(CUSTOMER_FEE.QUANTITY.gt(quantity));
				}
			} else if(AonStringUtils.containsIgnoreCase(customerFeeParams.getQuantity(), "<")) {
				String quantityStr = customerFeeParams.getQuantity().split("<")[1].trim();
				if(AonStringUtils.containsIgnoreCase(quantityStr, "=")) {
					quantityStr = quantityStr.split("=")[1].trim();
					Double quantity = Double.parseDouble(quantityStr);
					condition = condition.and(CUSTOMER_FEE.QUANTITY.le(quantity));
				} else {
					Double quantity = Double.parseDouble(quantityStr);
					condition = condition.and(CUSTOMER_FEE.QUANTITY.lt(quantity));
				}
			} else {
				Double quantity = Double.parseDouble(customerFeeParams.getQuantity());
				condition = condition.and(CUSTOMER_FEE.QUANTITY.eq(quantity));
			}
		}
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getPrice())) {
			if(AonStringUtils.containsIgnoreCase(customerFeeParams.getPrice(), ":")) {
				Double priceStart = Double.parseDouble(customerFeeParams.getPrice().split(":")[0].trim());
				Double priceEnd = Double.parseDouble(customerFeeParams.getPrice().split(":")[1].trim());
				condition = condition.and(CUSTOMER_FEE.PRICE.ge(priceStart));
				condition = condition.and(CUSTOMER_FEE.PRICE.le(priceEnd));
			} else if(AonStringUtils.containsIgnoreCase(customerFeeParams.getPrice(), ">")) {
				String priceStr = customerFeeParams.getPrice().split(">")[1].trim();
				if(AonStringUtils.containsIgnoreCase(priceStr, "=")) {
					priceStr = priceStr.split("=")[1].trim();
					Double price = Double.parseDouble(priceStr);
					condition = condition.and(CUSTOMER_FEE.PRICE.ge(price));
				} else {
					Double price = Double.parseDouble(priceStr);
					condition = condition.and(CUSTOMER_FEE.PRICE.gt(price));
				}
			} else if(AonStringUtils.containsIgnoreCase(customerFeeParams.getPrice(), "<")) {
				String priceStr = customerFeeParams.getPrice().split("<")[1].trim();
				if(AonStringUtils.containsIgnoreCase(priceStr, "=")) {
					priceStr = priceStr.split("=")[1].trim();
					Double price = Double.parseDouble(priceStr);
					condition = condition.and(CUSTOMER_FEE.PRICE.le(price));
				} else {
					Double price = Double.parseDouble(priceStr);
					condition = condition.and(CUSTOMER_FEE.PRICE.lt(price));
				}
			} else {
				Double price = Double.parseDouble(customerFeeParams.getPrice());
				condition = condition.and(CUSTOMER_FEE.PRICE.eq(price));
			}
		}
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getDiscount()))
			condition = condition.and(CUSTOMER_FEE.DISCOUNT_EXPR.eq(customerFeeParams.getDiscount()));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getSeller())) 
			condition = condition.and(SELLER_ALIAS.NAME.eq(customerFeeParams.getSeller()));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getWorkplace())) 
			condition = condition.and(WORKPLACE.DESCRIPTION.eq(customerFeeParams.getWorkplace()));
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getInvoicingGroup())) 
			condition = condition.and(INVOICING_GROUP.DESCRIPTION.eq(customerFeeParams.getInvoicingGroup()));
		
		if(null != customerFeeParams.getProject()) 
			condition = condition.and(CUSTOMER_FEE.PROJECT.eq(customerFeeParams.getProject()));
		
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
				.leftOuterJoin(RSEGMENT).on(CUSTOMER.REGISTRY.eq(RSEGMENT.ID))
				.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID))
				.leftOuterJoin(TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
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
	
	public static Integer saveList(AONContext ctx, LinkedList<Fee> feeList) {
		feeList.stream()
			.filter(fee -> fee.isModify())
			.forEach(fee -> {
				FeeValidation.validate(ctx, fee);
				
				ctx.getDslContext()
					.update(CUSTOMER)
						.set(CUSTOMER.STATUS, fee.getCustomer().getStatus().value())
						.where(CUSTOMER.REGISTRY.eq(fee.getCustomer().getId()))
						.execute();
				
				if(fee.getItem().getProduct().isModify()) {
					ctx.getDslContext()
					.update(PRODUCT)
						.set(PRODUCT.NAME, fee.getItem().getProduct().getName())
						.where(PRODUCT.ID.eq(fee.getItem().getProduct().getId()))
						.execute();
				}
				
				update(ctx, fee);
			});
				
		return (int) feeList.stream().filter(fee -> fee.isModify()).count();
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

	private static Fee update(AONContext ctx, Fee f) {
		Date startDate = f.getStartDate() != null ? new Date(f.getStartDate().getTime()) : null;
		Date endDate = f.getEndDate() != null ? new Date(f.getEndDate().getTime()) : null;
		Date billingDate = f.getBillingDate() != null ? new Date(f.getBillingDate().getTime()) : null;
		
		ctx.getDslContext()
			.update(CUSTOMER_FEE)
				.set(CUSTOMER_FEE.DOMAIN, f.getDomain().getId())
				.set(CUSTOMER_FEE.PROJECT, null == f.getProject() ? null : f.getProject().getId())
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
	
	public static void delete(AONContext ctx, CustomerFeeParams customerFeeParams) {
		ctx.checkWrite();
		customerFeeParams.setOffset(0);
		customerFeeParams.setLimit(Integer.MAX_VALUE);
		LinkedList<Fee> paramsFeeList = getFeeList(ctx, customerFeeParams);
		paramsFeeList.forEach(fee -> ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
			.delete(CUSTOMER_FEE)
			.where(CUSTOMER_FEE.ID.equal(fee.getId())).execute();
		}));
		
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

	public static Map<String, OldItem> getProductsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, OldItem> productsSuggestion = new TreeMap<>();
		
		// Condition
		Condition condition = PRODUCT.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) 
			condition = condition.and(PRODUCT.CODE.isNotNull().and(PRODUCT.CODE.containsIgnoreCase(query)))
					.or(PRODUCT.NAME.isNotNull().and(PRODUCT.NAME.containsIgnoreCase(query)));

		Result<Record> customerRecords = ctx.getDslContext().select()
			.from(PRODUCT)
			.join(ITEM)
			.on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.where(condition)
			.fetch();
		
		customerRecords.forEach(r -> {
			OldItem item = ItemFiller.buildItem(r);
			productsSuggestion.put(r.get(PRODUCT.NAME) + " (" + r.get(PRODUCT.CODE) + ")", item);
		});
		
		System.out.println("getProductsSuggestion size : " + productsSuggestion.size());
		
		return productsSuggestion;
	}
	
	public static Map<String, Integer> getProductCategoriesSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, Integer> productCategoriesSuggestion = new TreeMap<>();
		
		// Condition
		Condition condition = PCATEGORY.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) 
			condition = condition.and(PCATEGORY.NAME.isNotNull().and(PCATEGORY.NAME.containsIgnoreCase(query)));

		Result<Record> productCategoriesRecords = ctx.getDslContext().select()
			.from(PCATEGORY)
			.where(condition)
			.fetch();
		
		productCategoriesRecords.forEach(r -> {
			productCategoriesSuggestion.put(r.get(PCATEGORY.NAME), r.get(PCATEGORY.ID));
		});
		
		System.out.println("getProductCategoriesSuggestion size : " + productCategoriesSuggestion.size());
		
		return productCategoriesSuggestion;
	}
	
	public static Map<String, Integer> getProductTagsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, Integer> productTagsSuggestion = new TreeMap<>();
		
		// Condition
		Condition condition = PRODUCT.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) 
			condition = condition.and(TAG.NAME.isNotNull().and(TAG.NAME.containsIgnoreCase(query)));

		Result<Record> customerRecords = ctx.getDslContext().select()
			.from(PRODUCT)
			.join(PRODUCT_TAG)
			.on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID))
			.join(TAG)
			.on(TAG.ID.eq(PRODUCT_TAG.TAG))
			.where(condition)
			.fetch();
		
		customerRecords.forEach(r -> {
			productTagsSuggestion.put(r.get(TAG.NAME), r.get(TAG.ID));
		});
		
		System.out.println("getProductTagsSuggestion size : " + productTagsSuggestion.size());
		
		return productTagsSuggestion;
	}

	public static Map<String, Customer> getCustomersSuggestion(CloseableAONContext ctx, int domainId, String query) {
		// Condition
		Condition condition = CUSTOMER.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) 
			condition = condition.and(REGISTRY.NAME.isNotNull())
				.and(REGISTRY.NAME.containsIgnoreCase(query)
						.or(REGISTRY.DOCUMENT.containsIgnoreCase(query))
						.or(REGISTRY.ALIAS.isNotNull().and(REGISTRY.ALIAS.containsIgnoreCase(query)))
				);
		
		Result<Record> feeRecords = ctx.getDslContext().select().from(CUSTOMER)
				.join(REGISTRY).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
				.where(condition)
				.orderBy(REGISTRY.NAME)
				.fetch();
		
		Map<String, Customer> customerSuggestion = new TreeMap<>();
		
		feeRecords.forEach(r -> {
			Customer customer = CustomerFiller.buildCustomer(r, REGISTRY);
			customerSuggestion.put(r.get(REGISTRY.NAME) + " ( " + r.get(REGISTRY.DOCUMENT) + " )" + (AonStringUtils.isBlank(r.get(REGISTRY.ALIAS)) ? "" : " - " + r.get(REGISTRY.ALIAS)), customer);
		});
		
		System.out.println("getCustomersSuggestion size : " + customerSuggestion.size());
		
		return customerSuggestion;
	}
	
	public static Map<String, Workplace> getWorkplacesSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, Workplace> suggestions = new HashMap<>();
		
		// Condition
		Condition condition = WORKPLACE.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) condition = condition.and(WORKPLACE.DESCRIPTION.containsIgnoreCase(query));

		Result<Record> workplaceRecords = ctx.getDslContext()
				.select().from(WORKPLACE)
				.where(condition)
				.fetch();
		
		workplaceRecords.forEach(r -> {
			Workplace workplace = WorkplaceFiller.build(r);
			suggestions.put(r.get(WORKPLACE.DESCRIPTION), workplace);
		});
		
		System.out.println("getWorkplacesSuggestion size : " + suggestions.size());
		
		return suggestions;
	}
	
	public static Map<String, Seller> getSellersSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, Seller> suggestions = new HashMap<>();
		
		// Condition
		Condition condition = SELLER.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) condition = condition.and(REGISTRY.NAME.containsIgnoreCase(query));
		
		Result<Record> sellerRecords = ctx.getDslContext()
				.select().from(SELLER)
				.join(REGISTRY)
				.on(REGISTRY.ID.eq(SELLER.REGISTRY))
				.where(condition)
				.fetch();
		
		sellerRecords.forEach(r -> {
			Seller seller = SellerFiller.build(r);
			suggestions.put(r.get(REGISTRY.NAME), seller);
		});
		
		System.out.println("getSellersSuggestion size : " + suggestions.size());
		
		return suggestions;
	}
	
	public static Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		Map<String, InvoicingGroup> suggestions = new HashMap<>();
		
		// Condition
		Condition condition = INVOICING_GROUP.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query)) condition = condition.and(INVOICING_GROUP.DESCRIPTION.containsIgnoreCase(query));
		
		Result<Record> sellerRecords = ctx.getDslContext()
				.select().from(INVOICING_GROUP)
				.where(condition)
				.fetch();

		sellerRecords.forEach(r -> {
			InvoicingGroup invoicingGroup = InvoicingGroupFiller.buildInvoicingGroup(r);
			suggestions.put(r.get(INVOICING_GROUP.DESCRIPTION), invoicingGroup);
		});
		
		System.out.println("getInvoicingGroupsSuggestion size : " + suggestions.size());
		
		return suggestions;
	}
	
	public static Map<String, Project> getProjectsSuggestion(CloseableAONContext ctx, int domainId, Integer customerId, String query) {
		Map<String, Project> suggestions = new HashMap<>();
		
		// Condition
		Condition condition = PROJECT.DOMAIN.eq(domainId);
		if(AonStringUtils.isNotBlank(query))
			condition = condition
					.and(REGISTRY.NAME.isNotNull())
					.and(REGISTRY.NAME.containsIgnoreCase(query)
							.or(REGISTRY.DOCUMENT.containsIgnoreCase(query))
							.or(REGISTRY.ALIAS.isNotNull().and(REGISTRY.ALIAS.containsIgnoreCase(query)))
					);
		if(null != customerId) condition = condition.and(PROJECT.REGISTRY.eq(customerId));
		
		Result<Record> projectRecords = ctx.getDslContext()
				.select().from(PROJECT)
				.join(DOMAIN).on(PROJECT.DOMAIN.eq(DOMAIN.ID))
				.join(REGISTRY).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
				.where(condition)
				.fetch();

		projectRecords.forEach(r -> {
			Project project = ProjectFiller.build(r);
			suggestions.put(r.get(PROJECT.NAME), project);
		});
		
		System.out.println("getProjectsSuggestion size : " + suggestions.size());
		
		return suggestions;
	}
	
	public static void createCustomerFeeList(AONContext ctx, Fee fee) {
		insert(ctx, fee);
	}

	public static Integer saveMassiveFees(AONContext ctx, Fee fee, CustomerFeeParams customerFeeParams) {
		Condition condition = createFeeCondition(ctx, customerFeeParams);
		SelectOnConditionStep<Record1<Integer>> fromCustomerRecords = ctx.getDslContext().select(CUSTOMER_FEE.ID).from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID))
				.leftOuterJoin(TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(SELLER).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP));
		if (customerFeeParams != null && customerFeeParams.getSegment() != null) {
			if(customerFeeParams.getSegment() == -1)
				fromCustomerRecords = fromCustomerRecords 	
				.leftJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
			else fromCustomerRecords = fromCustomerRecords 	
				.join(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
		}
		Result<Record1<Integer>> customerFeeRecords = fromCustomerRecords	
			.where(condition)
			.orderBy(CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE)
		.fetch();
		
		// Update Product name if is modified
		if(null != fee.getItem() && null != fee.getItem().getProduct() && fee.getItem().getProduct().isModify()) {
			ctx.getDslContext().update(PRODUCT)
				.set(PRODUCT.NAME, fee.getItem().getProduct().getName())
				.where(PRODUCT.ID.eq(fee.getItem().getProduct().getId()))
				.execute();
		}
		
		UpdateSetMoreStep<CustomerRecord> updateQuery = (UpdateSetMoreStep) ctx.getDslContext().update(CUSTOMER_FEE);
		
		if(null != fee.getItem() && null != fee.getItem().getId()) updateQuery.set(CUSTOMER_FEE.ITEM, fee.getItem().getId());
		if(null != fee.getPeriod()) updateQuery.set(CUSTOMER_FEE.PERIOD, (short) fee.getPeriod().value());
		if(null != fee.getPrice()) updateQuery.set(CUSTOMER_FEE.PRICE, fee.getPrice());
		if(AonStringUtils.isNotBlank(fee.getDiscountExpr()) && !AonStringUtils.equalsIgnoreCase(fee.getDiscountExpr(), "0.0")) updateQuery.set(CUSTOMER_FEE.DISCOUNT_EXPR, fee.getDiscountExpr());
		
		if(null != fee.getBillingDate()) updateQuery.set(CUSTOMER_FEE.BILLING_DATE, parseSQLDate(fee.getBillingDate()));
		if(null != fee.getStartDate()) updateQuery.set(CUSTOMER_FEE.INITIAL_DATE, parseSQLDate(fee.getStartDate()));
		if(null != fee.getEndDate()) updateQuery.set(CUSTOMER_FEE.FINAL_DATE, parseSQLDate(fee.getEndDate()));
		
		updateQuery.where(CUSTOMER_FEE.ID.in(customerFeeRecords));
		
		return updateQuery.execute();
	}

	public static Map<Integer, Integer> getMinMaxCustomerFeeYear(CloseableAONContext ctx, int domainId) {
		Date maxDate = (Date) ctx.getDslContext().select(DSL.max(CUSTOMER_FEE.BILLING_DATE)).from(CUSTOMER_FEE).where(CUSTOMER_FEE.DOMAIN.eq(domainId)).fetchOne().get(0);
		Date minDate = (Date) ctx.getDslContext().select(DSL.min(CUSTOMER_FEE.BILLING_DATE)).from(CUSTOMER_FEE).where(CUSTOMER_FEE.DOMAIN.eq(domainId)).fetchOne().get(0);
		Map<Integer, Integer> datesMap = new HashMap<Integer, Integer>();
		if(null != maxDate && null != minDate)
			datesMap.put(minDate.getYear() + 1900,  maxDate.getYear() + 1900);
		
		return datesMap;
	}

	public static Map<Integer, Integer> getCustomerProductsUpdates(CloseableAONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = createFeeCondition(ctx, customerFeeParams);
		
		SelectOnConditionStep<Record1<Integer>> fromCustomerRecords = ctx.getDslContext().selectDistinct(CUSTOMER_FEE.CUSTOMER).from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID))
				.leftOuterJoin(TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(SELLER).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY))
				.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP));
		if (customerFeeParams != null && customerFeeParams.getSegment() != null) {
			if(customerFeeParams.getSegment() == -1)
				fromCustomerRecords = fromCustomerRecords 	
				.leftJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
			else fromCustomerRecords = fromCustomerRecords 	
				.join(RSEGMENT).on(RSEGMENT.REGISTRY.eq(CUSTOMER.REGISTRY));
		}
		Result<Record1<Integer>> customerRecords = fromCustomerRecords 
				.where(condition)
				.orderBy(CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE)
			.fetch();
		
		System.out.println("Customer Updates : " + customerRecords.size());
		
		Result<Record1<Integer>> customerFeeRecords = ctx.getDslContext().select(CUSTOMER_FEE.ID).from(CUSTOMER_FEE)
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
			.fetch();
		
		System.out.println("Customer Fee Updates : " + customerFeeRecords.size());
		
		Map<Integer, Integer> result = new HashMap<>();
		result.put(customerRecords.size(), customerFeeRecords.size());
		return result;
	}

	public static Integer getItemIdByProductCode(CloseableAONContext ctx, int domainId, String productCode) {
		List<Integer> itemRecords = ctx.getDslContext().select(ITEM.ID).from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.where(PRODUCT.CODE.eq(productCode))
			.and(ITEM.DOMAIN.eq(domainId))
			.fetch(ITEM.ID);
		
		return itemRecords.isEmpty() ? null : itemRecords.get(0);
	}
	
}
