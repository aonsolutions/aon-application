package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AmortizationTypeFilter;
import com.esferalia.aon.occam.api.model.AmortizationTypeProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AmortizationTypeDAO {
	
	private AmortizationTypeDAO() {}
	
	private static final AmortizationTypePropertiesDAO AMORTIZATION_TYPE_PROPERTIES = new AmortizationTypePropertiesDAO();
	private static class AmortizationTypePropertiesDAO implements AmortizationTypeProperties {
		
		private Condition[] getConditions(AmortizationTypeFilter filter) {
			if (filter==null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.DESCRIPTION);}
		@Override public Property<Double> getPercentageProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.PERCENTAGE);}
		@Override public Property<String> getFixedAssetAccountProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT);}
		@Override public Property<String> getAccumulatedAccountProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT);}
		@Override public Property<String> getAllocationAccountProperty() {return new FilterDAO.PropertyDAO<>(AMORTIZATION_TYPE.ALLOCATION_ACCOUNT);}

	}
	
	public static class AmortizationTypeFiller extends Filler implements Function<Record,AmortizationType> {
		
		@Override
		public AmortizationType apply(Record r) {
			return new AmortizationType()
					.setId(r.get(AMORTIZATION_TYPE.ID))
					.setDomain(DomainFiller.build(r))
					.setDescription(r.get(AMORTIZATION_TYPE.DESCRIPTION))
					.setPercentage(r.get(AMORTIZATION_TYPE.PERCENTAGE))
					.setFixedAssetAccount(r.get(AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT))
					.setAccumulatedAccount(r.get(AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT))
					.setAllocationAccount(r.get(AMORTIZATION_TYPE.ALLOCATION_ACCOUNT));
		}
	}
	
	public static List<AmortizationType> getList(AONContext ctx, AmortizationTypeParams params) {
		Condition condition = createAmortizationTypeCondition(params);
		
		return ctx.getDslContext()
				.select().from(AMORTIZATION_TYPE)
				.join(DOMAIN).on(DOMAIN.ID.eq(AMORTIZATION_TYPE.DOMAIN))
				.where(condition)
				.and(AMORTIZATION_TYPE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(AMORTIZATION_TYPE.DESCRIPTION)
				.offset(params.getOffset())
				.limit(params.getLimit())
				.stream()
				.map(new AmortizationTypeFiller())
				.collect(Collectors.toList());
	}
	
	private static Condition createAmortizationTypeCondition(AmortizationTypeParams params) {
		if(params == null) return DSL.trueCondition(); 
		
		Condition condition = DSL.trueCondition();
		
		if(AonStringUtils.isNotBlank(params.getDescription())) 
			condition = AMORTIZATION_TYPE.DESCRIPTION.like("%" + params.getDescription() + "%");
		
		if(AonStringUtils.isNotBlank(params.getFixedAssetAccount())) 
			condition = AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT.like("%" + params.getFixedAssetAccount() + "%");
		
		if(AonStringUtils.isNotBlank(params.getAccumulatedAccount())) 
			condition = AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT.like("%" + params.getAccumulatedAccount() + "%");
		
		if(AonStringUtils.isNotBlank(params.getAllocationAccount())) 
			condition = AMORTIZATION_TYPE.ALLOCATION_ACCOUNT.like("%" + params.getAllocationAccount() + "%");
		
		return condition;
	}

	public static void save(AONContext ctx, AmortizationType amortizationType) {
		if (amortizationType.getId() == null) insert(ctx, amortizationType);
		else update(ctx, amortizationType);
	}
	
	private static void insert(AONContext ctx, AmortizationType amortizationType) {
		ctx.checkWrite();
		
		Integer insertId = ctx.getDslContext()
				.insertInto(AMORTIZATION_TYPE)
				.set(AMORTIZATION_TYPE.DOMAIN, amortizationType.getDomain().getId())
				.set(AMORTIZATION_TYPE.DESCRIPTION, amortizationType.getDescription())
				.set(AMORTIZATION_TYPE.PERCENTAGE, amortizationType.getPercentage())
				.set(AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT, amortizationType.getFixedAssetAccount())
				.set(AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT, amortizationType.getAccumulatedAccount())
				.set(AMORTIZATION_TYPE.ALLOCATION_ACCOUNT, amortizationType.getAllocationAccount())
				.returning(AMORTIZATION_TYPE.ID)
				.fetchOne()
				.getValue(AMORTIZATION_TYPE.ID);
		
		ctx.log().debug("INSERT AMORTIZATION TYPE id: {0}", insertId);
	}
	
	public static void update(AONContext ctx, AmortizationType amortizationType) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(AMORTIZATION_TYPE)
			.set(AMORTIZATION_TYPE.DESCRIPTION, amortizationType.getDescription())
			.set(AMORTIZATION_TYPE.PERCENTAGE, amortizationType.getPercentage())
			.set(AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT, amortizationType.getFixedAssetAccount())
			.set(AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT, amortizationType.getAccumulatedAccount())
			.set(AMORTIZATION_TYPE.ALLOCATION_ACCOUNT, amortizationType.getAllocationAccount())
			.where(AMORTIZATION_TYPE.ID.eq(amortizationType.getId()))
			.execute();
		
		ctx.log().debug("UPDATE  AMORTIZATION TYPE id: {0}}", amortizationType.getId());
	}

	public static void delete(AONContext ctx, List<Integer> deleteIds) {
		deleteIds.forEach(id -> delete(ctx, id));
	}
	
	private static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(AMORTIZATION_TYPE)
			.where(AMORTIZATION_TYPE.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE AMORTIZATION TYPE id: {0}", id);
	}
	
}




