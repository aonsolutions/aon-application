package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.CommissionType.COMMISSION_TYPE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import  org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.occam.impl.jooq.validation.SellerValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SellerDAO {
    
    private SellerDAO() {

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
	
	private static SelectConditionStep<Record> select(AONContext ctx, SellerFilter filter) {
		return ctx.getDslContext().select()
				.from(SELLER)
				.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
				.leftOuterJoin(COMMISSION_TYPE).on(COMMISSION_TYPE.ID.eq(SELLER.COMMISSION_TYPE))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(SELLER.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.where(SELLER_PROPERTIES.getConditions(filter));	
	}

	public static Seller get(AONContext ctx, SellerFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new SellerFiller())
			.findFirst()
			.orElse(new Seller());
	}
	
	public static Seller get(AONContext ctx, Integer id){
		return get(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static List<Seller>  getList(CloseableAONContext ctx, SellerParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select()
			.from(SELLER)
			.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
			.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
			.leftOuterJoin(COMMISSION_TYPE).on(COMMISSION_TYPE.ID.eq(SELLER.COMMISSION_TYPE))
			.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(SELLER.TASK_HOLDER))
			.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(SELLER_ALIAS.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "alias"))
				select.orderBy(SELLER_ALIAS.ALIAS);
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(SELLER_ALIAS.DOCUMENT);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(SELLER_ALIAS.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "alias"))
				select.orderBy(SELLER_ALIAS.ALIAS.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(SELLER_ALIAS.DOCUMENT.desc());
		}
				
		List<Seller> sellers = select
				.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new SellerFiller())
				.collect(Collectors.toList());
		
		sellers.forEach(seller -> System.out.println(seller.getName()));
			
		return sellers;
	}
	
	public static Integer getListCount(CloseableAONContext ctx, SellerParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record1<Integer>> select = ctx.getDslContext().selectCount()
			.from(SELLER)
			.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
			.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
			.leftOuterJoin(COMMISSION_TYPE).on(COMMISSION_TYPE.ID.eq(SELLER.COMMISSION_TYPE))
			.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(SELLER.TASK_HOLDER))
			.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(SELLER_ALIAS.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "alias"))
				select.orderBy(SELLER_ALIAS.ALIAS);
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(SELLER_ALIAS.DOCUMENT);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(SELLER_ALIAS.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "alias"))
				select.orderBy(SELLER_ALIAS.ALIAS.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(SELLER_ALIAS.DOCUMENT.desc());
		}
				
		Record1<Integer> sellerCount = select.fetchOne();
					
		return sellerCount == null ? 0 : sellerCount.value1();
	}
	
	private static Condition paramsToCondition(CloseableAONContext ctx, SellerParams params) {
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

	public static Stream<Seller> getStream(AONContext ctx, SellerFilter filter){
		return select(ctx, filter)
			.orderBy(SELLER_ALIAS.NAME)
			.fetch().stream().map(new SellerFiller());
	}	
	
	public static Stream<Seller> getStream(AONContext ctx, SellerFilter filter, int offset, int limit){
		return select(ctx, filter)
				.orderBy(SELLER_ALIAS.NAME)
				.offset(offset)
				.limit(limit)
				.fetch().stream().map(new SellerFiller());
	}	
	
	public static Seller save(AONContext ctx, Seller seller) {
		ctx.checkWrite();
		SellerValidation.validate(ctx, seller);
		boolean nullId = (seller.getId() == null); 
		seller.copy(RegistryDAO.save(ctx, seller));
		return nullId || get(ctx, seller.getId()).isEmpty()
			? insert(ctx, seller) : update(ctx, seller);
	}

	private static Seller insert(AONContext ctx, Seller seller){
		ctx.getDslContext().insertInto(SELLER)
			.set(SELLER.REGISTRY, seller.getId())
			.set(SELLER.DOMAIN, seller.getDomain().getId())
			.set(SELLER.COMMISSION_TYPE, seller.getCommissionType().getId())
			.set(SELLER.SCOPE, seller.getScope().getId())
			.set(SELLER.STATUS, seller.getStatus().value())
			.set(SELLER.TASK_HOLDER, seller.getTaskHolder().getRegistry())
			.execute();
		return seller;
	}
	
	private static Seller update(AONContext ctx, Seller seller){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(SELLER)
			.set(SELLER.DOMAIN, seller.getDomain().getId())
			.set(SELLER.COMMISSION_TYPE, seller.getCommissionType().getId())
			.set(SELLER.SCOPE, seller.getScope().getId())
			.set(SELLER.STATUS, seller.getStatus().value())
			.set(SELLER.TASK_HOLDER, seller.getTaskHolder().getRegistry())
			.where(SELLER.REGISTRY.eq(seller.getId()))
			.execute();
		ctx.log().info("UPDATE SUPPLIER id: " + seller.getId() + ". (" + count + " rows)");		
		return seller;
	}
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, SellerFilter filter){
		ctx.getDslContext().delete(SELLER)
		.where(SELLER_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	
	public static class SellerFiller extends Filler implements Function<Record,Seller> {
	
		@Override
		public Seller apply(Record r) {
			return build(r);
		}

		public static Seller build(Record r) {
			return build(r, SELLER_ALIAS);			
		}
		
		public static Seller build(Record r, Registry registry) {
			return new Seller()
				.copy(RegistryFiller.build(r, registry))
				.setId(getValue(r, SELLER.REGISTRY))
				.setDomain(getValue(r, SELLER.DOMAIN))
				.setStatus(SellerStatus.safeValueOf(getValue(r, SELLER.STATUS)))
				.setCommissionType(checkField(r, SCOPE.ID)
						? new CommissionType().setId(getValue(r, COMMISSION_TYPE.ID)).setDomain(getValue(r, COMMISSION_TYPE.DOMAIN)).setName(getValue(r, COMMISSION_TYPE.NAME))
						: new CommissionType().setId(getValue(r, SELLER.COMMISSION_TYPE)))
				.setScope(checkField(r, SCOPE.ID)
					? ScopeFiller.buildScope(r)
					: new Scope().setId(getValue(r, SELLER.SCOPE)))
				.setTaskHolder(checkField(r, TASK_HOLDER.REGISTRY) 
					? TaskHolderFiller.build(r, TASK_HOLDER_ALIAS)
					: new TaskHolder().setRegistry(getValue(r, SELLER.TASK_HOLDER)))
				;
		}

	}
	
}
