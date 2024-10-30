package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jooq.Condition;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO.FeeFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SellerWorkloadDAO {
    
    private SellerWorkloadDAO() {}
	
    public static final com.esferalia.aon.jooq.tables.Registry SELLER_ALIAS = REGISTRY.as("registry_seller");
    
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	
	public static class SellerPropertiesDAO implements SellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SELLER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(SELLER.TASK_HOLDER);}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ID);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.SECURITY_LEVEL);}
	}
	
	public static List<SellerWorkload>  getList(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		Date start = getStartDatePeriod(params.getPeriod());
		Date end = getEndDatePeriod(params.getPeriod());
		
		SelectHavingStep<?> select = ctx.getDslContext().select(
	            SELLER.REGISTRY,
	            SELLER_ALIAS.NAME,
	            SELLER_ALIAS.DOCUMENT,
	            SCOPE.DESCRIPTION
	        )
			.from(SELLER)
	        .join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
	        .join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
	        .leftJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
	            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(start)))
	            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(end))))) // Condiciones de fechas
	        .where(condition)
	        .groupBy(SELLER.REGISTRY); // Agrupamos por SELLER
		        
		applyOrdering(select, params);
		applyHavingCustomers(select, params);
				
		List<SellerWorkload> sellers = select
				.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new SellerFiller())
				.collect(Collectors.toList());
		
		sellers.forEach(seller -> {
			System.out.println(seller.getName());
			getCustomerFeeAmount(ctx, seller, seller.getId(), params);
			System.out.println(seller.getName() + " -- END");
		});
		
		return sellers;
	}
	
	public static Integer getListCount(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		Date start = getStartDatePeriod(params.getPeriod());
		Date end = getEndDatePeriod(params.getPeriod());
		
		SelectHavingStep<Record1<Integer>> select = ctx.getDslContext().selectDistinct(SELLER.REGISTRY)
			.from(SELLER)
	        .join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
	        .join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
	        .leftJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
	            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(start)))
	            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(end))))) // Condiciones de fechas
	        .where(condition)
	        .groupBy(SELLER.REGISTRY); // Agrupamos por SELLER
		        
		applyOrdering(select, params);
		applyHavingCustomers(select, params);
				
		Result<Record1<Integer>> sellerCount = select.fetch();
					
		return sellerCount.isEmpty() ? 0 : sellerCount.size();
	}
	
	public static List<Fee>  getFeeList(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = createFeeCondition(ctx, params);
		
		Date start = getStartDatePeriod(params.getPeriod());
	    Date end = getEndDatePeriod(params.getPeriod());
	    
	    HashSet<Integer> customerFeeIds = new HashSet<Integer>();

	    // Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera compatible con ambos motores de base de datos
	    Field<Date> recalculatedBillingDateAdjusted = DSL
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
	        .otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

	    while (start.before(end)) {
	        // Realizamos la consulta con la lógica del ajuste de fechas incorporada
	        SelectConditionStep<Record1<Integer>> select = ctx.getDslContext()
	        	.selectDistinct(CUSTOMER_FEE.ID)
	            .from(CUSTOMER_FEE)
	            .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(CUSTOMER.STATUS.ne((byte)1)))
	            .join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
	            .where(CUSTOMER_FEE.SELLER.eq(params.getSeller()))
	            .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
	            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
	            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(end)))
	            .and(CUSTOMER_FEE.PERIOD.ne((short) 0))
	            .and(recalculatedBillingDateAdjusted.lessOrEqual(start)) // Filtro por la fecha ajustada
	            .and(condition);
	        
	        	applyFeeOrdering(select, params);
	        	
	        	Result<Record1<Integer>> result = select
	        		.groupBy(CUSTOMER_FEE.ID)
	        		.offset(params.getOffset())
					.limit(params.getLimit())
		            .fetch();
	        
	        customerFeeIds.addAll(result.getValues(CUSTOMER_FEE.ID));
	        
	        // Sumamos un mes a la fecha de inicio
	        start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
	    }
	    
	    if(customerFeeIds.isEmpty())
	    	return new LinkedList<Fee>();
	    
	    CustomerFeeParams customerFeeParams = new CustomerFeeParams();
	    customerFeeParams.setDomain(ctx.getDomainId());
	    customerFeeParams.setFeeIds(customerFeeIds.toArray(new Integer[0]));
	    customerFeeParams.setCustomerName(params.getDescription());
	    customerFeeParams.setProductName(params.getDescription());
	    customerFeeParams.setOrderBy(params.getOrderBy());
	    customerFeeParams.setAsc(params.isAsc());
	    
	    LinkedList<Fee> feeList = getFees(ctx, customerFeeParams);
	    
	    System.out.println("Seller : " + params.getSeller());
	    System.out.println("Offset : " + params.getOffset());
	    System.out.println("Limit : " + params.getLimit());
	    System.out.println(feeList.size());
	    
	    return feeList;
	}

	public static List<Integer> getFeeIdsList(CloseableAONContext ctx, SellerWorkloadParams params) {
		
		if(params.getSeller() == null) {
			
			Condition condition = paramsToCondition(ctx, params);
			
			Date start = getStartDatePeriod(params.getPeriod());
			Date end = getEndDatePeriod(params.getPeriod());
			
			SelectHavingStep<?> select = ctx.getDslContext().select(
		            SELLER.REGISTRY,
		            SELLER_ALIAS.NAME,
		            SELLER_ALIAS.DOCUMENT,
		            SCOPE.DESCRIPTION
		        )
				.from(SELLER)
		        .join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
		        .join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
		        .leftJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
		            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(start)))
		            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(end))))) // Condiciones de fechas
		        .where(condition)
		        .groupBy(SELLER.REGISTRY); // Agrupamos por SELLER
			        
			applyOrdering(select, params);
			applyHavingCustomers(select, params);
					
			List<SellerWorkload> sellers = select
					.limit(params.getOffset(), params.getLimit())
					.fetch()
					.stream()
					.map(new SellerFiller())
					.collect(Collectors.toList());
			
			HashSet<Integer> customerFeeAllIds = new HashSet<Integer>();
			
			for(SellerWorkload sellerWorkload : sellers) {
				Condition conditionFee = createFeeCondition(ctx, params);
				
				start = getStartDatePeriod(params.getPeriod());
				end = getEndDatePeriod(params.getPeriod());
			    
			    HashSet<Integer> customerFeeIds = new HashSet<Integer>();

			    // Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera compatible con ambos motores de base de datos
			    Field<Date> recalculatedBillingDateAdjusted = DSL
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
			        .when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
			        .otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

			    while (start.before(end)) {
			        // Realizamos la consulta con la lógica del ajuste de fechas incorporada
			        Result<Record1<Integer>> result = ctx.getDslContext()
			        	.selectDistinct(CUSTOMER_FEE.ID)
			            .from(CUSTOMER_FEE)
			            .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(CUSTOMER.STATUS.ne((byte)1)))
			            .join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			            .where(CUSTOMER_FEE.SELLER.eq(sellerWorkload.getId()))
			            .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
			            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(end)))
			            .and(CUSTOMER_FEE.PERIOD.ne((short) 0))
			            .and(recalculatedBillingDateAdjusted.lessOrEqual(start)) // Filtro por la fecha ajustada
			            .and(conditionFee)
			            .fetch();
			        
			        customerFeeIds.addAll(result.getValues(CUSTOMER_FEE.ID));
			        
			        // Sumamos un mes a la fecha de inicio
			        start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			    }
			    
			    System.out.println("getFeeIdsList size : " + customerFeeIds.size());
			    
			    customerFeeAllIds.addAll(customerFeeIds);
			}
			
			System.out.println("getFeeIdsList ALL size : " + customerFeeAllIds.size());
			
			return customerFeeAllIds.stream().collect(Collectors.toList());
			
		} else {
		
			Condition condition = createFeeCondition(ctx, params);
			
			Date start = getStartDatePeriod(params.getPeriod());
		    Date end = getEndDatePeriod(params.getPeriod());
		    
		    HashSet<Integer> customerFeeIds = new HashSet<Integer>();

		    // Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera compatible con ambos motores de base de datos
		    Field<Date> recalculatedBillingDateAdjusted = DSL
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
		        .when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
		        .otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

		    while (start.before(end)) {
		        // Realizamos la consulta con la lógica del ajuste de fechas incorporada
		        Result<Record1<Integer>> result = ctx.getDslContext()
		        	.selectDistinct(CUSTOMER_FEE.ID)
		            .from(CUSTOMER_FEE)
		            .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(CUSTOMER.STATUS.ne((byte)1)))
		            .join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
		            .where(CUSTOMER_FEE.SELLER.eq(params.getSeller()))
		            .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
		            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
		            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(end)))
		            .and(CUSTOMER_FEE.PERIOD.ne((short) 0))
		            .and(recalculatedBillingDateAdjusted.lessOrEqual(start)) // Filtro por la fecha ajustada
		            .and(condition)
		            .fetch();
		        
		        customerFeeIds.addAll(result.getValues(CUSTOMER_FEE.ID));
		        
		        // Sumamos un mes a la fecha de inicio
		        start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
		    }
		    
		    System.out.println("getFeeIdsList size : " + customerFeeIds.size());
		    
		    return customerFeeIds.stream().collect(Collectors.toList());
		    
		}
		
	}
	
	public static LinkedList<Fee> getFees(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createFeeCondition(ctx, customerFeeParams);
		
		SelectConditionStep<Record> fromCustomerRecords = ctx.getDslContext().selectDistinct().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(INVOICING_GROUP).on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP))
				.where(condition);
			
//		fromCustomerRecords = fromCustomerRecords.leftJoin(RITEM).on(RITEM.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(RITEM.ITEM.eq(CUSTOMER_FEE.ITEM)).and(RITEM.CUSTOMER_FEE.eq(CUSTOMER_FEE.ID)));
		
		applyCustomerFeeOrdering(fromCustomerRecords, customerFeeParams);
		
		Result<Record> feeRecords = fromCustomerRecords	
			.groupBy(CUSTOMER_FEE.ID)
			.fetch();
		
		System.out.println("Customer Fee size : " + feeRecords.size());
		
		LinkedList<Fee> fees = feeRecords.stream().map(new FeeFiller()).collect(Collectors.toCollection(LinkedList::new));
		
		return fees;
	}

	private static Condition createFeeCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
		
		if(null != customerFeeParams.getFeeIds() && customerFeeParams.getFeeIds().length > 0) {
			condition = condition.and(CUSTOMER_FEE.ID.in(customerFeeParams.getFeeIds()));
		}
		
		if(AonStringUtils.isNotBlank(customerFeeParams.getCustomerName()) || AonStringUtils.isNotBlank(customerFeeParams.getProductName())) {
			condition = condition.and(CUSTOMER_ALIAS.NAME.likeIgnoreCase("%" + customerFeeParams.getCustomerName() + "%").or(CUSTOMER_FEE.DESCRIPTION.likeIgnoreCase("%" + customerFeeParams.getProductName() + "%")));
		}
		
		return condition;
	}
	

	
	private static Condition createFeeCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(params.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
		
		if(AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(CUSTOMER_ALIAS.NAME.likeIgnoreCase("%" + params.getDescription() + "%").or(CUSTOMER_FEE.DESCRIPTION.likeIgnoreCase("%" + params.getDescription() + "%")));
		}
		
		return condition;
	}
	
	private static void applyHavingCustomers(SelectHavingStep<?> select, SellerWorkloadParams params) {
		if (params.getCustomers() != null) {
	    	if (params.getCustomers() == 1) // Al menos un customer distinto
	            select.having(DSL.countDistinct(CUSTOMER_FEE.CUSTOMER).gt(0));
	    	else /// Sin customers
	            select.having(DSL.countDistinct(CUSTOMER_FEE.CUSTOMER).eq(0));
	    }
	}
	
	private static void applyOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
	    orderFields.put("name", SELLER_ALIAS.NAME);
	    orderFields.put("alias", SELLER_ALIAS.ALIAS);
	    orderFields.put("document", SELLER_ALIAS.DOCUMENT);

	    Field<?> orderField = orderFields.get(params.getOrderBy());

	    if (orderField != null) {
	        select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
	    }
	}
	
	private static void applyFeeOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
	    orderFields.put("customer", CUSTOMER_ALIAS.NAME);
	    orderFields.put("product", CUSTOMER_FEE.DESCRIPTION);

	    Field<?> orderField = orderFields.get(params.getOrderBy());

	    if (orderField != null) {
	        select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
	    }
	}
	
	private static void applyCustomerFeeOrdering(SelectConditionStep<Record> select, CustomerFeeParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
	    orderFields.put("customer", CUSTOMER_ALIAS.NAME);
	    orderFields.put("product", CUSTOMER_FEE.DESCRIPTION);

	    Field<?> orderField = orderFields.get(params.getOrderBy());

	    if (orderField != null) {
	        select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
	    }
	}

	private static Date getStartDatePeriod(Byte period) {
		java.util.Date start = AonDateUtils.getMonthFirstDay(new java.util.Date());
		return AonDateUtils.toSql(start);
	}
	
	private static Date getEndDatePeriod(Byte period) {
		java.util.Date start = AonDateUtils.getMonthFirstDay(new java.util.Date());
		java.util.Date end = null;
		
		if(period == (byte)0) {
			end = AonDateUtils.getMonthLastDay(new java.util.Date());
		} else if(period == (byte)1) {
			end = AonDateUtils.addMonths(start, 1);
			end = AonDateUtils.getMonthLastDay(end);
		} else if(period == (byte)2) {
			end = AonDateUtils.addMonths(start, 2);
			end = AonDateUtils.getMonthLastDay(end);
		} 
		
		return AonDateUtils.toSql(end);
	}
	
	private static Condition paramsToCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = SELLER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(
					SELLER_ALIAS.NAME.like("%" + params.getDescription() + "%")
					.or(SELLER_ALIAS.DOCUMENT.like("%" + params.getDescription() + "%"))
					.or(SELLER_ALIAS.ALIAS.like("%" + params.getDescription() + "%"))
			);
		}
		
		if(AonStringUtils.isNotBlank(params.getName()))
			condition = condition.and(SELLER_ALIAS.NAME.like("%" + params.getName() + "%"));
		
		if(AonStringUtils.isNotBlank(params.getAlias()))
			condition = condition.and(SELLER_ALIAS.ALIAS.like("%" + params.getAlias() + "%"));
		
		if(AonStringUtils.isNotBlank(params.getDocument()))
			condition = condition.and(SELLER_ALIAS.DOCUMENT.like("%" + params.getDocument() + "%"));
		
		if(null != params.getScope())
			condition = condition.and(SELLER.SCOPE.eq(params.getScope()));
		
		if(null != params.getActive())
			condition = condition.and(SELLER.STATUS.eq(params.getActive()));
		
		return condition;
	}
	
	private static void getCustomerFeeAmount(AONContext ctx, SellerWorkload sellerWorkload, Integer seller, SellerWorkloadParams params) {
	    Date start = getStartDatePeriod(params.getPeriod());
	    Date end = getEndDatePeriod(params.getPeriod());

	    // Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera compatible con ambos motores de base de datos
	    Field<Date> recalculatedBillingDateAdjusted = DSL
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
	        .when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
	        .otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

	    while (start.before(end)) {
	        // Realizamos la consulta con la lógica del ajuste de fechas incorporada
	        Result<Record5<Double, Double, String, Short, Integer>> result = ctx.getDslContext().select(
	                    CUSTOMER_FEE.QUANTITY, 
	                    CUSTOMER_FEE.PRICE, 
	                    CUSTOMER_FEE.DISCOUNT_EXPR, 
	                    CUSTOMER_FEE.PERIOD, 
	                    CUSTOMER_FEE.CUSTOMER)
	            .from(CUSTOMER_FEE)
	            .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(CUSTOMER.STATUS.ne((byte)1)))
	            .where(CUSTOMER_FEE.SELLER.eq(seller))
	            .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
	            .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
	            .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(end)))
	            .and(CUSTOMER_FEE.PERIOD.ne((short) 0))
	            .and(recalculatedBillingDateAdjusted.lessOrEqual(start)) // Filtro por la fecha ajustada
	            .fetch();

	        // Inicializamos los valores para los cálculos
	        double netSum = 0.0;
	        double totalSum = 0.0;
	        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

	        for (Record record : result) {
	            Double quantity = record.get(CUSTOMER_FEE.QUANTITY);
	            Double price = record.get(CUSTOMER_FEE.PRICE);
	            String discountExpr = record.get(CUSTOMER_FEE.DISCOUNT_EXPR);
	            short recordPeriod = record.get(CUSTOMER_FEE.PERIOD);

	            // Calcular el descuento
	            double discount = 0.0;
	            if (discountExpr != null && !discountExpr.isEmpty()) {
	                try {
	                    discount = evaluateDiscount(discountExpr, price, engine);
	                } catch (ScriptException e) {
	                    System.err.println("Error evaluando DISCOUNT_EXPR: " + discountExpr);
	                    e.printStackTrace();
	                }
	            }

	            // Fórmula: (PRICE - DISCOUNT) * QUANTITY / PERIOD
	            double periodValue = getPeriodValue(recordPeriod);
	            double feeNetSum = (price - discount) * quantity / periodValue;
	            double feeSum = price * quantity / periodValue;
	            netSum += feeNetSum;
	            totalSum += feeSum;
	        }

	        // Actualizamos la carga de trabajo con el número de clientes distintos, total de registros y sumatorio
	        sellerWorkload.addSellerWorkloadPeriod(start, 
	            (int)result.stream().map(it -> it.get(CUSTOMER_FEE.CUSTOMER)).distinct().count(), 
	            result.size(), 
	            netSum,
	            totalSum);

	        // Sumamos un mes a la fecha de inicio
	        start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
	    }
	}
	
	// Método para evaluar el descuento basado en DISCOUNT_EXPR
	private static double evaluateDiscount(String discountExpr, Double price, ScriptEngine engine) throws ScriptException {
	    double discount = 0.0;
	    double discountedPrice = price;

	    // Expresión regular que soporta números con o sin decimales (e.g., 10, 10.5)
	    String numberPattern = "\\d+(\\.\\d+)?";

	    if (discountExpr.matches(numberPattern)) { 
	        // Si es un número simple como "10" o "10.5", aplicamos ese porcentaje de descuento
	        discount = (price * Double.parseDouble(discountExpr)) / 100;
	        discountedPrice = price - discount;
	    } else {
	        // Si es una expresión matemática (e.g., "10 + 10.5"), aplicamos cada descuento secuencialmente
	        String[] discountParts = discountExpr.split("\\+");

	        for (String part : discountParts) {
	            part = part.trim();
	            if (part.matches(numberPattern)) {
	                // Aplicamos el porcentaje de descuento al precio actual
	                double percentage = Double.parseDouble(part);
	                discount = (discountedPrice * percentage) / 100;
	                discountedPrice -= discount;
	            } else {
	                // Si por alguna razón el descuento no es numérico, se lanza una excepción
	                throw new ScriptException("Formato de descuento inválido: " + part);
	            }
	        }
	    }

	    return price - discountedPrice; // Devolvemos la cantidad total descontada
	}

    // Método para calcular el valor de división por periodo (Short Period)
    private static double getPeriodValue(short period) {
        switch (period) {
            case 1:
                return 1.0;
            case 2:
                return 2.0;
            case 3:
                return 3.0;
            case 4:
                return 4.0;
            case 5:
                return 6.0;
            case 6:
                return 12.0;
            default:
                return 1.0; // Por defecto, si el periodo es inválido o no se especifica
        }
    }
	
	protected static class SellerFiller extends Filler implements Function<Record,SellerWorkload> {
	
		@Override
		public SellerWorkload apply(Record r) {
			return build(r);
		}

		public static SellerWorkload build(Record r) {
			return build(r, SELLER_ALIAS);			
		}
		
		public static SellerWorkload build(Record r, Registry registry) {
			SellerWorkload sellerWorkload = new SellerWorkload();
			
			sellerWorkload.setId(r.getValue(SELLER.REGISTRY));
			sellerWorkload.setStatus(SellerStatus.safeValueOf(getValue(r, SELLER.STATUS)));
			sellerWorkload.setName(r.getValue(SELLER_ALIAS.NAME));
			sellerWorkload.setDocument(r.getValue(SELLER_ALIAS.DOCUMENT));
			sellerWorkload.setScope(new Scope().setDescription(r.getValue(SCOPE.DESCRIPTION)));

			return sellerWorkload;
		}

	}
	
}
