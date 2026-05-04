package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectHolder.PROJECT_HOLDER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.Rnote;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO.FeeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectHolderDAO.ProjectHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SellerWorkloadDAO {
	
	public static class StatusFilter {
	    public final Condition condition;
	    public final Table<?> lastNote;
	    public final Field<Integer> lnRegistry;

	    public StatusFilter(Condition condition, Table<?> lastNote, Field<Integer> lnRegistry) {
	        this.condition = condition;
	        this.lastNote = lastNote;
	        this.lnRegistry = lnRegistry;
	    }
	}


	private SellerWorkloadDAO() {
	}

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

		@Override
		public Property<Integer> getRegistryProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);
		}

		@Override
		public Property<Byte> getStatusProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.STATUS);
		}

		@Override
		public Property<Integer> getScopeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);
		}

		@Override
		public Property<Integer> getCommissionTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);
		}

		@Override
		public Property<Integer> getTaskHolderProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.TASK_HOLDER);
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ID);
		}

		@Override
		public Property<String> getDocumentProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT);
		}

		@Override
		public Property<Byte> getDocumentTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_TYPE);
		}

		@Override
		public Property<String> getDocumentCountryProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_COUNTRY);
		}

		@Override
		public Property<String> getNameProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NAME);
		}

		@Override
		public Property<String> getAliasProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ALIAS);
		}

		@Override
		public Property<Byte> getTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.TYPE);
		}

		@Override
		public Property<String> getNationalityProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NATIONALITY);
		}

		@Override
		public Property<Byte> getSecurityLevelProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.SECURITY_LEVEL);
		}

		@Override
		public Property<String> getEmailProperty() {
			return new RegistryDAO.RMediaPropertyDAO(MediaType.EMAIL);
		}
	
	}

	public static List<SellerWorkload> getList(CloseableAONContext ctx, SellerWorkloadParams params) {
	    List<SellerWorkload> sellers = new ArrayList<>();

	    Date start = getStartDatePeriod(params.getPeriod());
	    Date end = getEndDatePeriod(params.getPeriod());

	    StatusFilter sf = getCustomerStatusCondition(params, end);

	    if (params.getByProject()) {

	        Condition condition = paramsProjectToCondition(ctx, params);

	        // Construcción base del SELECT
	        SelectJoinStep<?> base = ctx.getDslContext()
	            .select()
	            .from(PROJECT_HOLDER)
	            .join(PROJECT).on(PROJECT.ID.eq(PROJECT_HOLDER.PROJECT))
	            .join(TASK_HOLDER).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER.REGISTRY))
	            .join(TASK_HOLDER_ALIAS).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER_ALIAS.ID))
	            .leftOuterJoin(WORKGROUP).on(PROJECT_HOLDER.WORKGROUP.eq(WORKGROUP.ID))
	            .leftJoin(CUSTOMER_FEE)
	                .on(CUSTOMER_FEE.PROJECT.eq(PROJECT.ID)
	                .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(end)))
	                .and(CUSTOMER_FEE.FINAL_DATE.isNull()
	                    .or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(start)))))
	            .leftJoin(CUSTOMER)
	                .on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

	        // Solo si aplica el caso especial (nota tipo 6)
	        if (sf.lastNote != null) {
	            base = base.leftOuterJoin(sf.lastNote)
	                .on(sf.lnRegistry.eq(CUSTOMER.REGISTRY));
	        }

	        SelectHavingStep<?> select = ((SelectConditionStep<?>) base
	            .where(condition)
	            .and(sf.condition)
	            .and(PROJECT_HOLDER.START_DATE.le(new Timestamp(end.getTime())))
	            .and(PROJECT_HOLDER.END_DATE.isNull()
	                .or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime())))))
	            .groupBy(PROJECT_HOLDER.TASK_HOLDER);

	        applyProjectOrdering(select, params);
	        applyHavingCustomers(select, params);

	        sellers = select.limit(params.getOffset(), params.getLimit())
	            .fetch()
	            .stream()
	            .map(r -> {
	                SellerWorkload sellerWorkload = new SellerWorkload();
	                sellerWorkload.setProjectHolder(ProjectHolderFiller.build(r));
	                return sellerWorkload;
	            })
	            .collect(Collectors.toList());

	        sellers.forEach(seller -> {
	            System.out.println(seller.getName());
	            getProjectCustomerAmount(ctx, seller, seller.getProjectHolder().getTaskHolder().getId(), params);
	            System.out.println(seller.getName() + " -- END");
	        });

	    } else {

	        Condition condition = paramsToCondition(ctx, params);

	        SelectJoinStep<?> base = ctx.getDslContext()
	            .select(SELLER.REGISTRY, SELLER_ALIAS.NAME, SELLER_ALIAS.DOCUMENT, SCOPE.DESCRIPTION)
	            .from(SELLER)
	            .join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
	            .join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
	            .leftJoin(CUSTOMER_FEE)
	                .on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
	                .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(end)))
	                .and(CUSTOMER_FEE.FINAL_DATE.isNull()
	                    .or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(start)))))
	            .leftJoin(CUSTOMER)
	                .on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

	        if (sf.lastNote != null) {
	            base = base.leftOuterJoin(sf.lastNote)
	                .on(sf.lnRegistry.eq(CUSTOMER.REGISTRY));
	        }

	        SelectHavingStep<?> select = ((SelectConditionStep<?>) base
	            .where(condition)
	            .and(sf.condition))
	            .groupBy(SELLER.REGISTRY);

	        applyOrdering(select, params);
	        applyHavingCustomers(select, params);

	        sellers = select.limit(params.getOffset(), params.getLimit())
	            .fetch()
	            .stream()
	            .map(new SellerFiller())
	            .collect(Collectors.toList());

	        sellers.forEach(seller -> {
	            System.out.println(seller.getName());
	            getCustomerAmount(ctx, seller, seller.getId(), params);
	            System.out.println(seller.getName() + " -- END");
	        });
	    }

	    return sellers;
	}


	public static Integer getListCount(CloseableAONContext ctx, SellerWorkloadParams params) {
	    Date start = getStartDatePeriod(params.getPeriod());
	    Date end = getEndDatePeriod(params.getPeriod());

	    StatusFilter sf = getCustomerStatusCondition(params, end);

	    if (params.getByProject()) {

	        Condition condition = paramsProjectToCondition(ctx, params);

	        SelectJoinStep<Record1<Integer>> base = ctx.getDslContext()
	            .selectDistinct(PROJECT_HOLDER.TASK_HOLDER)
	            .from(PROJECT_HOLDER)
	            .join(PROJECT).on(PROJECT.ID.eq(PROJECT_HOLDER.PROJECT))
	            .leftOuterJoin(WORKGROUP).on(PROJECT_HOLDER.WORKGROUP.eq(WORKGROUP.ID))
	            .leftOuterJoin(TASK_HOLDER).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER.REGISTRY))
	            .leftOuterJoin(TASK_HOLDER_ALIAS).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER_ALIAS.ID))
	            .leftJoin(CUSTOMER_FEE)
	                .on(CUSTOMER_FEE.PROJECT.eq(PROJECT.ID)
	                .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(end)))
	                .and(CUSTOMER_FEE.FINAL_DATE.isNull()
	                    .or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(start)))))
	            .leftJoin(CUSTOMER)
	                .on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

	        if (sf.lastNote != null) {
	            base = base.leftOuterJoin(sf.lastNote)
	                .on(sf.lnRegistry.eq(CUSTOMER.REGISTRY));
	        }

	        SelectHavingStep<Record1<Integer>> select = base
	            .where(condition)
	            .and(sf.condition)
	            .and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
	            .and(PROJECT_HOLDER.END_DATE.isNull()
	                .or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
	            .groupBy(PROJECT_HOLDER.TASK_HOLDER);

	        applyProjectOrdering(select, params);
	        applyHavingCustomers(select, params);

	        Result<Record1<Integer>> sellerCount = select.fetch();
	        return sellerCount.isEmpty() ? 0 : sellerCount.size();

	    } else {

	        Condition condition = paramsToCondition(ctx, params);

	        SelectJoinStep<Record1<Integer>> base = ctx.getDslContext()
	            .selectDistinct(SELLER.REGISTRY)
	            .from(SELLER)
	            .join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
	            .join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
	            .leftJoin(CUSTOMER_FEE)
	                .on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
	                .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(AonDateUtils.toSql(end)))
	                .and(CUSTOMER_FEE.FINAL_DATE.isNull()
	                    .or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(AonDateUtils.toSql(start)))))
	            .leftJoin(CUSTOMER)
	                .on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

	        if (sf.lastNote != null) {
	            base = base.leftOuterJoin(sf.lastNote)
	                .on(sf.lnRegistry.eq(CUSTOMER.REGISTRY));
	        }

	        SelectHavingStep<Record1<Integer>> select = base
	            .where(condition)
	            .and(sf.condition)
	            .groupBy(SELLER.REGISTRY);

	        applyOrdering(select, params);
	        applyHavingCustomers(select, params);

	        Result<Record1<Integer>> sellerCount = select.fetch();
	        return sellerCount.isEmpty() ? 0 : sellerCount.size();
	    }
	}


	public static SellerWorkloadContent getSellersWorkloadContent(CloseableAONContext ctx, SellerWorkloadParams params) {
		//Condition condition = createFeeWorkloadCondition(ctx, params);

		Date start = getStartDatePeriod(params.getPeriod());
		Date endIt = AonDateUtils.toSql( AonDateUtils.getMonthLastDay(start) );
		Date end = getEndDatePeriod(params.getPeriod());
		
		StatusFilter sf = getCustomerStatusCondition(params, end); 
		
		HashSet<Integer> customerFeeIds = new HashSet<Integer>();
		HashSet<Integer> invoiceIds = new HashSet<Integer>();

		// Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera
		// compatible con ambos motores de base de datos
		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha
		
		List<Integer> projectIds = ctx.getDslContext().selectDistinct(PROJECT.ID)
				.from(PROJECT)
				.join(PROJECT_HOLDER).on(PROJECT_HOLDER.PROJECT.eq(PROJECT.ID))
				.where(PROJECT_HOLDER.TASK_HOLDER.eq(params.getTaskHolder()))
				.and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
				.and(PROJECT_HOLDER.END_DATE.isNull().or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
				.fetch(PROJECT.ID);

		while (start.before(end)) {
			// Realizamos la consulta con la lógica del ajuste de fechas incorporada
			if(null != params.getSeller()) {
				SelectConditionStep<Record1<Integer>> select = null;
				if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
					select = ctx.getDslContext().selectDistinct(CUSTOMER_FEE.ID)
					    .from(CUSTOMER_FEE)
					    .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
					    .join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
					    .leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
					    .where(CUSTOMER_FEE.SELLER.eq(params.getSeller()))
					    .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
					    .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
					    .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
					    .and(recalculatedBillingDateAdjusted.lessThan(start))
					    .and(sf.condition)
					    ;
				} else {
					
					select = ctx.getDslContext().selectDistinct(CUSTOMER_FEE.ID)
							.from(CUSTOMER_FEE).join(CUSTOMER)
							.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
							.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
							.where(CUSTOMER_FEE.SELLER.eq(params.getSeller()))
							.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
							.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
							.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
							.and(sf.condition);
				}

				applyFeeOrdering(select, params);
	
				Result<Record1<Integer>> result = select.groupBy(CUSTOMER_FEE.ID).offset(params.getOffset())
						.limit(params.getLimit()).fetch();
	
				customerFeeIds.addAll(result.getValues(CUSTOMER_FEE.ID));
			}
			
			if(null != params.getTaskHolder()) {
				SelectConditionStep<Record1<Integer>> select = null;
				if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
					select = ctx.getDslContext().selectDistinct(CUSTOMER_FEE.ID)
				    .from(CUSTOMER_FEE)
				    .join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				    .join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				    .leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
				    .where(CUSTOMER_FEE.PROJECT.in(projectIds))
				    .and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				    .and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
				    .and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
				    .and(recalculatedBillingDateAdjusted.lessThan(start))
				    .and(sf.condition);
				} else {
					select = ctx.getDslContext().selectDistinct(CUSTOMER_FEE.ID)
							.from(CUSTOMER_FEE).join(CUSTOMER)
							.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
							.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
							.where(CUSTOMER_FEE.PROJECT.in(projectIds))
							.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
							.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
							.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
							.and(sf.condition);
				}
				
				applyFeeOrdering(select, params);
	
				Result<Record1<Integer>> result = select.groupBy(CUSTOMER_FEE.ID).offset(params.getOffset())
						.limit(params.getLimit()).fetch();
	
				customerFeeIds.addAll(result.getValues(CUSTOMER_FEE.ID));
			}

			if(null != params.getSeller()) {
				// Realizamos la consulta con la lógica del ajuste de fechas incorporada
				
				SelectConditionStep<Record1<Integer>> invoiceSelect = null;
				
				if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
					invoiceSelect = ctx.getDslContext().selectDistinct(INVOICE_DETAIL.ID)
							.from(INVOICE_DETAIL)
							.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
							.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
							.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
							.where(INVOICE_DETAIL.SELLER.eq(params.getSeller()))
							.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(INVOICE.ISSUE_DATE.between(start, endIt))
							.and(sf.condition);
				} else {
					invoiceSelect = ctx.getDslContext().selectDistinct(INVOICE_DETAIL.ID)
							.from(INVOICE_DETAIL)
							.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
							.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
							.where(INVOICE_DETAIL.SELLER.eq(params.getSeller()))
							.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(INVOICE.ISSUE_DATE.between(start, endIt))
							;
				}
				
				
				Result<Record1<Integer>> resultInvoice = invoiceSelect.groupBy(INVOICE_DETAIL.ID).offset(params.getOffset())
						.limit(params.getLimit()).fetch();
	
				invoiceIds.addAll(resultInvoice.getValues(INVOICE_DETAIL.ID));
			}
			
			if(null != params.getTaskHolder()) {
				// Realizamos la consulta con la lógica del ajuste de fechas incorporada
				SelectConditionStep<Record1<Integer>> invoiceSelect = null;
				
				if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
					invoiceSelect = ctx.getDslContext().selectDistinct(INVOICE_DETAIL.ID)
							.from(INVOICE_DETAIL)
							.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
							.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
							.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
							.where(INVOICE_DETAIL.PROJECT.in(projectIds))
							.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(INVOICE.ISSUE_DATE.between(start, endIt))
							.and(sf.condition);
				} else {
					invoiceSelect = ctx.getDslContext().selectDistinct(INVOICE_DETAIL.ID)
							.from(INVOICE_DETAIL)
							.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
							.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
							.where(INVOICE_DETAIL.PROJECT.in(projectIds))
							.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
							.and(INVOICE.ISSUE_DATE.between(start, endIt))
							.and(sf.condition);
				}
				
				Result<Record1<Integer>> resultInvoice = invoiceSelect.groupBy(INVOICE_DETAIL.ID).offset(params.getOffset())
						.limit(params.getLimit()).fetch();
	
				invoiceIds.addAll(resultInvoice.getValues(INVOICE_DETAIL.ID));
			}
			// Sumamos un mes a la fecha de inicio
			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}

		if (customerFeeIds.isEmpty() && invoiceIds.isEmpty())
			return new SellerWorkloadContent();

		CustomerFeeParams customerFeeParams = new CustomerFeeParams();
		customerFeeParams.setDomain(ctx.getDomainId());
		customerFeeParams.setFeeIds(customerFeeIds.toArray(new Integer[0]));
		customerFeeParams.setInvoiceIds(invoiceIds.toArray(new Integer[0]));
		customerFeeParams.setCustomerName(params.getDescription());
		customerFeeParams.setProductName(params.getDescription());
		customerFeeParams.setOrderBy(params.getOrderBy());
		customerFeeParams.setAsc(params.isAsc());

		LinkedList<Fee> feeList = customerFeeIds.size() > 0 ? getFees(ctx, customerFeeParams) : new LinkedList<Fee>();
		List<InvoiceDetail> invoiceList = InvoiceDetailExtendedDAO.getInvoiceDetails(ctx, f -> f.getDetailIdProperty().in(customerFeeParams.getInvoiceIds())).collect(Collectors.toList());

		System.out.println("Project : " + params.getTaskHolder());
		System.out.println("Seller : " + params.getSeller());
		System.out.println("Offset : " + params.getOffset());
		System.out.println("Limit : " + params.getLimit());
		System.out.println(feeList.size());
		System.out.println(invoiceList.size());
		
		SellerWorkloadContent sellerWorkloadContent = new SellerWorkloadContent()
				.setFees(
					feeList.stream()
					.filter( fee -> {
						return AonStringUtils.isBlank(params.getDescription()) || 
							( AonStringUtils.containsIgnoreCase(fee.getCustomer().getName(), params.getDescription()) ||
							  AonStringUtils.containsIgnoreCase(fee.getCustomer().getDocument(), params.getDescription()) ||
							  AonStringUtils.containsIgnoreCase(fee.getCustomer().getAlias(), params.getDescription())
							);
					})
					.collect(Collectors.toList()))
				
				.setInvoiceDetails(
						invoiceList.stream()
						.filter( invoice -> {
							return AonStringUtils.isBlank(params.getDescription()) || 
								( AonStringUtils.containsIgnoreCase(invoice.getInvoice().getRegistryName(), params.getDescription()) ||
								  AonStringUtils.containsIgnoreCase(invoice.getInvoice().getRegistryDocument(), params.getDescription())
								);
						})
						.collect(Collectors.toList()))
				
				.setInvoiceDetails(invoiceList);

		return sellerWorkloadContent;
	}

	private static StatusFilter getCustomerStatusCondition(
	        SellerWorkloadParams params,
	        Date end
	) {

	    // Caso general: usar flags normales
	    if (!(params.getCustomers() == null || params.getCustomers() == (byte) 1)) {
	        return new StatusFilter(DSL.trueCondition(), null, null);
	    }

	    // Caso especial: solo activos + inactivos/bloqueados válidos
	    if (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) {

	        Rnote r1 = RNOTE.as("r1");
	        Rnote r2 = RNOTE.as("r2");

	        // Subconsulta: última nota tipo 6 por registry
	        Table<?> lastNote = DSL
	            .select(
	                r1.REGISTRY,
	                r1.DESCRIPTION,
	                r1.COMMENTS
	            )
	            .from(r1)
	            .join(
	                DSL.select(
	                        r2.REGISTRY,
	                        DSL.max(r2.ID).as("max_id")
	                )
	                .from(r2)
	                .where(r2.NOTE_TYPE.eq((byte) 6))
	                .groupBy(r2.REGISTRY)
	            ).on(r1.ID.eq(DSL.field("max_id", Integer.class)))
	            .where(r1.NOTE_TYPE.eq((byte) 6))
	            .asTable("lastNote");

	        Field<Integer> lnRegistry = lastNote.field(RNOTE.REGISTRY.getName(), Integer.class);
	        Field<String> lnDescription = lastNote.field(RNOTE.DESCRIPTION.getName(), String.class);
	        Field<String> lnComments = lastNote.field(RNOTE.COMMENTS.getName(), String.class);

	        // Extraer fecha YYYY-MM-DD
	        Field<String> expirationDateStr = DSL.field(
	            "REGEXP_SUBSTR({0}, '([0-9]{4}-[0-9]{2}-[0-9]{2})')",
	            String.class,
	            lnComments
	        );

	        Field<java.sql.Date> expirationDate =
	            DSL.field("STR_TO_DATE({0}, '%Y-%m-%d')", java.sql.Date.class, expirationDateStr);

	        // Condición para inactivos o bloqueados válidos
	        Condition inactiveOrBlockedCondition =
	            CUSTOMER.STATUS.in((byte)1, (byte)2)
	            .and(lnDescription.in("Inactivo", "Bloqueado"))
	            .and(expirationDate.isNotNull())
	            .and(expirationDate.greaterOrEqual(end));

	        // Condición final
	        Condition condition =
	            CUSTOMER.STATUS.eq((byte) 0)   // activos siempre
	            .or(inactiveOrBlockedCondition);

	        return new StatusFilter(condition, lastNote, lnRegistry);
	    }

	    // Caso normal: usar flags
	    List<Byte> customerStatus = new ArrayList<>();
	    if (params.getCustomerActive())   customerStatus.add((byte) 0);
	    if (params.getCustomerInactive()) customerStatus.add((byte) 1);
	    if (params.getCustomerBlocked())  customerStatus.add((byte) 2);

	    return new StatusFilter(CUSTOMER.STATUS.in(customerStatus), null, null);
	}


	
	public static List<Integer> getFeeIdsList(CloseableAONContext ctx, SellerWorkloadParams params) {

		if (params.getSeller() == null && params.getTaskHolder() == null) {

			List<SellerWorkload> sellers = getList(ctx, params);

			HashSet<Integer> customerFeeIds = new HashSet<Integer>();
			
			sellers.forEach(seller -> {
				if(null == seller.getProjectHolder()) {
					params.setTaskHolder(null);
					params.setSeller(seller.getId());
				} else {
					params.setTaskHolder(seller.getProjectHolder().getTaskHolder().getId());
					params.setSeller(null);
				}
				SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
				customerFeeIds.addAll( content.getFees().stream().map(fee -> fee.getId()).distinct().collect(Collectors.toList()) );
			});
			
			return customerFeeIds.stream().collect(Collectors.toList());
			
		} else {
			
			HashSet<Integer> customerFeeIds = new HashSet<Integer>();
			SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
			customerFeeIds.addAll( content.getFees().stream().map(fee -> fee.getId()).distinct().collect(Collectors.toList()) );
			return customerFeeIds.stream().collect(Collectors.toList());
			
		}

	}
	
	public static List<Integer> getInvoiceIdsList(CloseableAONContext ctx, SellerWorkloadParams params) {

		if (params.getSeller() == null && params.getTaskHolder() == null) {

			List<SellerWorkload> sellers = getList(ctx, params);

			HashSet<Integer> invoiceDetailIds = new HashSet<Integer>();
			
			sellers.forEach(seller -> {
				if(null == seller.getProjectHolder()) {
					params.setTaskHolder(null);
					params.setSeller(seller.getId());
				} else {
					params.setTaskHolder(seller.getProjectHolder().getTaskHolder().getId());
					params.setSeller(null);
				}
				SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
				invoiceDetailIds.addAll( content.getInvoiceDetails().stream().map(invoiceDetail -> invoiceDetail.getId()).distinct().collect(Collectors.toList()) );
			});
			
			return invoiceDetailIds.stream().collect(Collectors.toList());
			
		} else {
			
			HashSet<Integer> invoiceDetailIds = new HashSet<Integer>();
			SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
			invoiceDetailIds.addAll( content.getInvoiceDetails().stream().map(invoiceDetail -> invoiceDetail.getId()).distinct().collect(Collectors.toList()) );
			return invoiceDetailIds.stream().collect(Collectors.toList());
			
		}

	}

	public static LinkedList<Fee> getFees(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = createFeeCondition(ctx, customerFeeParams);

		SelectConditionStep<Record> fromCustomerRecords = ctx.getDslContext().selectDistinct().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN)).join(CUSTOMER)
				.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER)).join(CUSTOMER_ALIAS)
				.on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID)).join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM)).join(PRODUCT)
				.on(PRODUCT.ID.eq(ITEM.PRODUCT)).join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY)).leftOuterJoin(INVOICING_GROUP)
				.on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP)).leftOuterJoin(PROJECT)
				.on(PROJECT.ID.eq(CUSTOMER_FEE.PROJECT)).where(condition);

		applyCustomerFeeOrdering(fromCustomerRecords, customerFeeParams);

		Result<Record> feeRecords = fromCustomerRecords.groupBy(CUSTOMER_FEE.ID).fetch();

		System.out.println("Customer Fee size : " + feeRecords.size());

		LinkedList<Fee> fees = feeRecords.stream().map(new FeeFiller())
				.collect(Collectors.toCollection(LinkedList::new));

		return fees;
	}

	private static Condition createFeeCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain());

		User user = SecurityDAO.getUser(ctx);
		if (user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}

		if (null != customerFeeParams.getFeeIds() && customerFeeParams.getFeeIds().length > 0) {
			condition = condition.and(CUSTOMER_FEE.ID.in(customerFeeParams.getFeeIds()));
		}

		if (AonStringUtils.isNotBlank(customerFeeParams.getCustomerName())
				|| AonStringUtils.isNotBlank(customerFeeParams.getProductName())) {
			condition = condition
					.and(CUSTOMER_ALIAS.NAME.likeIgnoreCase("%" + customerFeeParams.getCustomerName() + "%").or(
							CUSTOMER_FEE.DESCRIPTION.likeIgnoreCase("%" + customerFeeParams.getProductName() + "%")));
		}

		return condition;
	}

	private static Condition createFeeWorkloadCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(params.getDomain());

		User user = SecurityDAO.getUser(ctx);
		if (user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}

		return condition;
	}

	private static Condition createFeeWorkloadCondition(AONContext ctx, SellerWorkloadParams params) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(params.getDomain());

		User user = SecurityDAO.getUser(ctx);
		if (user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
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
	
	private static void applyProjectOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
		orderFields.put("name", TASK_HOLDER_ALIAS.NAME);
		orderFields.put("alias", TASK_HOLDER_ALIAS.ALIAS);
		orderFields.put("document", TASK_HOLDER_ALIAS.DOCUMENT);

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
		
		if(period == (byte) 0 || period == (byte) 1)
			start = AonDateUtils.addMonths(start, -1);
		else if(period == (byte) 4)
		start = AonDateUtils.addMonths(start, 1);
		
		return AonDateUtils.toSql(start);
	}

	private static Date getEndDatePeriod(Byte period) {
		java.util.Date start = AonDateUtils.getMonthFirstDay(new java.util.Date());
		java.util.Date end = AonDateUtils.getMonthLastDay(start);
		
		if (period == (byte) 0) {
			end = AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(end, -1) );
		} else if (period == (byte) 3 || period == (byte) 4) {
			end = AonDateUtils.addMonths(start, 1);
			end = AonDateUtils.getMonthLastDay(end);
		} 

		return AonDateUtils.toSql(end);
	}

	private static Condition paramsToCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = SELLER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));

		if (AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(SELLER_ALIAS.NAME.like("%" + params.getDescription() + "%")
					.or(SELLER_ALIAS.DOCUMENT.like("%" + params.getDescription() + "%"))
					.or(SELLER_ALIAS.ALIAS.like("%" + params.getDescription() + "%")));
		}

		if (AonStringUtils.isNotBlank(params.getName()))
			condition = condition.and(SELLER_ALIAS.NAME.like("%" + params.getName() + "%"));

		if (AonStringUtils.isNotBlank(params.getAlias()))
			condition = condition.and(SELLER_ALIAS.ALIAS.like("%" + params.getAlias() + "%"));

		if (AonStringUtils.isNotBlank(params.getDocument()))
			condition = condition.and(SELLER_ALIAS.DOCUMENT.like("%" + params.getDocument() + "%"));

		if (null != params.getScope())
			condition = condition.and(SELLER.SCOPE.eq(params.getScope()));

		if (null != params.getActive())
			condition = condition.and(SELLER.STATUS.eq(params.getActive()));
		
		if (params.getCustomers() == null || params.getCustomers() == (byte)1) {
			
			if(null != params.getCustomerActive() || null != params.getCustomerInactive() || null != params.getCustomerBlocked()) {
				List<Byte> customerStatus = new ArrayList<Byte>();
				
				if(null != params.getCustomerActive() && params.getCustomerActive()) customerStatus.add((byte)0);
				if(null != params.getCustomerInactive() && params.getCustomerInactive()) customerStatus.add((byte)1);
				if(null != params.getCustomerBlocked() && params.getCustomerBlocked()) customerStatus.add((byte)2);
				
				condition = condition.and(CUSTOMER.STATUS.in(customerStatus));
			}
			
		}

		return condition;
	}

	private static Condition paramsProjectToCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = PROJECT_HOLDER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));

		if (AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(TASK_HOLDER_ALIAS.NAME.like("%" + params.getDescription() + "%")
					.or(TASK_HOLDER_ALIAS.DOCUMENT.like("%" + params.getDescription() + "%"))
					.or(TASK_HOLDER_ALIAS.ALIAS.like("%" + params.getDescription() + "%")));
		}

		if (AonStringUtils.isNotBlank(params.getName()))
			condition = condition.and(TASK_HOLDER_ALIAS.NAME.like("%" + params.getName() + "%"));

		if (AonStringUtils.isNotBlank(params.getAlias()))
			condition = condition.and(TASK_HOLDER_ALIAS.ALIAS.like("%" + params.getAlias() + "%"));

		if (AonStringUtils.isNotBlank(params.getDocument()))
			condition = condition.and(TASK_HOLDER_ALIAS.DOCUMENT.like("%" + params.getDocument() + "%"));
		
		if (params.getCustomers() == null || params.getCustomers() == (byte)1) {
			
			List<Byte> customerStatus = new ArrayList<Byte>();
			
			if(params.getCustomerActive()) customerStatus.add((byte)0);
			if(params.getCustomerInactive()) customerStatus.add((byte)1);
			if(params.getCustomerBlocked()) customerStatus.add((byte)2);
			
			condition = condition.and(CUSTOMER.STATUS.in(customerStatus));
		}

		return condition;
	}

	private static void getCustomerAmount(AONContext ctx, SellerWorkload sellerWorkload, Integer seller, SellerWorkloadParams params) {
		Date start = getStartDatePeriod(params.getPeriod());
		Date endIt = AonDateUtils.toSql( AonDateUtils.getMonthLastDay(start) );
		Date end = getEndDatePeriod(params.getPeriod());
		
		Condition condition = createFeeWorkloadCondition(ctx, params);
		
		StatusFilter sf = getCustomerStatusCondition(params, end); 

		// Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera
		// compatible con ambos motores de base de datos
		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

		while (start.before(end)) {
			// Realizamos la consulta con la lógica del ajuste de fechas incorporada
			Result<Record5<Double, Double, String, Short, Integer>> result = null;
			if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
				result = ctx.getDslContext()
						.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.PERIOD,
								CUSTOMER_FEE.CUSTOMER)
						.from(CUSTOMER_FEE).join(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
						.where(CUSTOMER_FEE.SELLER.eq(seller))
						.and(condition)
						.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
						.fetch();
			} else {
				result = ctx.getDslContext()
						.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.PERIOD,
								CUSTOMER_FEE.CUSTOMER)
						.from(CUSTOMER_FEE).join(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.where(CUSTOMER_FEE.SELLER.eq(seller))
						.and(condition)
						.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
						.fetch();
			}
			
			SelectConditionStep<Record4<Double, Double, String, Integer>> invoiceSelect = null;
			if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
				invoiceSelect = ctx.getDslContext()
						.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE.REGISTRY)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
						.where(INVOICE_DETAIL.SELLER.eq(seller))
						.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(INVOICE.ISSUE_DATE.between(start, endIt))
						.and(sf.condition);
			} else {
				invoiceSelect = ctx.getDslContext()
						.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE.REGISTRY)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.where(INVOICE_DETAIL.SELLER.eq(seller))
						.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(INVOICE.ISSUE_DATE.between(start, endIt))
						;
			}
			
			Result<Record4<Double, Double, String, Integer>> invoiceResult = invoiceSelect.fetch();
			
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
			
			for (Record record : invoiceResult) {
				Double quantity = record.get(INVOICE_DETAIL.QUANTITY);
				Double price = record.get(INVOICE_DETAIL.PRICE);
				String discountExpr = record.get(INVOICE_DETAIL.DISCOUNT_EXPR);

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

				// Fórmula: (PRICE - DISCOUNT) * QUANTITY
				double invoiceNetSum = (price - discount) * quantity;
				double invoiceSum = price * quantity;
				netSum += invoiceNetSum;
				totalSum += invoiceSum;
			}
			
			HashSet<Integer> customers = new HashSet<Integer>();
			customers.addAll(result.stream().map(it -> it.get(CUSTOMER_FEE.CUSTOMER)).distinct().collect(Collectors.toList()));
			customers.addAll(invoiceResult.stream().map(it -> it.get(INVOICE.REGISTRY)).distinct().collect(Collectors.toList()));

			// Actualizamos la carga de trabajo con el número de clientes distintos, total
			// de registros y sumatorio
			sellerWorkload.addSellerWorkloadPeriod(
					start,
					customers.size(), 
					result.size(),
					invoiceResult.size(),
					netSum, 
					totalSum);

			// Sumamos un mes a la fecha de inicio
			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}
	}

	private static void getProjectCustomerAmount(AONContext ctx, SellerWorkload sellerWorkload, Integer taskHolderId, SellerWorkloadParams params) {
		Date start = getStartDatePeriod(params.getPeriod());
		Date endIt = AonDateUtils.toSql( AonDateUtils.getMonthLastDay(start) );
		Date end = getEndDatePeriod(params.getPeriod());
		
		Condition condition = createFeeWorkloadCondition(ctx, params);
		
		StatusFilter sf = getCustomerStatusCondition(params, end); 
		
		List<Integer> projectIds = ctx.getDslContext().selectDistinct(PROJECT.ID)
			.from(PROJECT)
			.join(PROJECT_HOLDER).on(PROJECT_HOLDER.PROJECT.eq(PROJECT.ID))
			.where(PROJECT_HOLDER.TASK_HOLDER.eq(taskHolderId))
			.and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
			.and(PROJECT_HOLDER.END_DATE.isNull().or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
			.fetch(PROJECT.ID);

		// Utilizamos JOOQ para recalcular la fecha de facturación ajustada de manera
		// compatible con ambos motores de base de datos
		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE); // Si no hay período definido, no ajustamos la fecha

		while (start.before(end)) {
			// Realizamos la consulta con la lógica del ajuste de fechas incorporada
			Result<Record5<Double, Double, String, Short, Integer>> result = null;
			if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
				result = ctx.getDslContext()
						.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.PERIOD,
								CUSTOMER_FEE.CUSTOMER)
						.from(CUSTOMER_FEE).join(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
						.where(CUSTOMER_FEE.PROJECT.in(projectIds))
						.and(condition)
						.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
						.fetch();
			} else {
				result = ctx.getDslContext()
						.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.PERIOD,
								CUSTOMER_FEE.CUSTOMER)
						.from(CUSTOMER_FEE).join(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.where(CUSTOMER_FEE.PROJECT.in(projectIds))
						.and(condition)
						.and(CUSTOMER_FEE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start)) // Filtro por la fecha ajustada
						.fetch();
			}
			
			SelectConditionStep<Record4<Double, Double, String, Integer>> invoiceSelect = null;
			if ( (params.getCustomers() == null || params.getCustomers() == (byte)1) && (params.getCustomerActive() && !params.getCustomerInactive() && !params.getCustomerBlocked()) && null != sf.lnRegistry ) {
				invoiceSelect = ctx.getDslContext()
						.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE.REGISTRY)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.leftOuterJoin(sf.lastNote).on(sf.lnRegistry.eq(CUSTOMER.REGISTRY))
						.where(INVOICE_DETAIL.PROJECT.in(projectIds))
						.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(INVOICE.ISSUE_DATE.between(start, endIt))
						.and(condition);
			} else {
				invoiceSelect = ctx.getDslContext()
						.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR, INVOICE.REGISTRY)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.where(INVOICE_DETAIL.PROJECT.in(projectIds))
						.and(INVOICE_DETAIL.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
						.and(INVOICE.ISSUE_DATE.between(start, endIt))
						;
			}
			
			Result<Record4<Double, Double, String, Integer>> invoiceResult = invoiceSelect.fetch();
			

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
			
			for (Record record : invoiceResult) {
				Double quantity = record.get(INVOICE_DETAIL.QUANTITY);
				Double price = record.get(INVOICE_DETAIL.PRICE);
				String discountExpr = record.get(INVOICE_DETAIL.DISCOUNT_EXPR);

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

				// Fórmula: (PRICE - DISCOUNT) * QUANTITY
				double invoiceNetSum = (price - discount) * quantity;
				double invoiceSum = price * quantity;
				netSum += invoiceNetSum;
				totalSum += invoiceSum;
			}
			
			HashSet<Integer> customers = new HashSet<Integer>();
			customers.addAll(result.stream().map(it -> it.get(CUSTOMER_FEE.CUSTOMER)).distinct().collect(Collectors.toList()));
			customers.addAll(invoiceResult.stream().map(it -> it.get(INVOICE.REGISTRY)).distinct().collect(Collectors.toList()));

			// Actualizamos la carga de trabajo con el número de clientes distintos, total
			// de registros y sumatorio
			sellerWorkload.addSellerWorkloadPeriod(
					start,
					customers.size(), 
					result.size(),
					invoiceResult.size(),
					netSum, 
					totalSum);

			// Sumamos un mes a la fecha de inicio
			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}
	}
	
	// Método para evaluar el descuento basado en DISCOUNT_EXPR
	private static double evaluateDiscount(String discountExpr, Double price, ScriptEngine engine)
			throws ScriptException {
		double discount = 0.0;
		double discountedPrice = price;

		// Expresión regular que soporta números con o sin decimales (e.g., 10, 10.5)
		String numberPattern = "\\d+(\\.\\d+)?";

		if (discountExpr.matches(numberPattern)) {
			// Si es un número simple como "10" o "10.5", aplicamos ese porcentaje de
			// descuento
			discount = (price * Double.parseDouble(discountExpr)) / 100;
			discountedPrice = price - discount;
		} else {
			// Si es una expresión matemática (e.g., "10 + 10.5"), aplicamos cada descuento
			// secuencialmente
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

	protected static class SellerFiller extends Filler implements Function<Record, SellerWorkload> {

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
