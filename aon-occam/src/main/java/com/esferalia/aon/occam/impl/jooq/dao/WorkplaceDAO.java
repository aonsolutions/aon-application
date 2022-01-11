package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
import com.esferalia.aon.occam.impl.jooq.validation.WorkplaceAutoComplete;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;

public class WorkplaceDAO {
	
	private static final WorkplacePropertiesDAO WORKPLACE_PROPERTIES = new WorkplacePropertiesDAO();

	protected static class WorkplacePropertiesDAO implements WorkplaceProperties {
		protected Condition[] getConditions(WorkplaceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ACTIVE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.DOMAIN);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ADDRESS);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.CUSTOMER);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.DESCRIPTION);}
		@Override public Property<Byte> getEconomicagreementProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ECONOMICAGREEMENT);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ENTERPRISE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.SCOPE);}	
	}
	
	public static Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter){
		return ctx.getDslContext().select().from(WORKPLACE).where(WORKPLACE_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(WORKPLACE).stream().map(new FullWorkplaceFiller()).findFirst().orElse(null);	
	}
	
	public static LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter){
		return ctx.getDslContext().select().from(WORKPLACE)
				.where(WORKPLACE_PROPERTIES.getConditions(filter))
				.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE))
				.orderBy(WORKPLACE.DESCRIPTION)
				.fetchInto(WORKPLACE)
				.stream()
				.map(new FullWorkplaceFiller())
				.collect(Collectors.toCollection(LinkedList::new));	
	}

	public static Workplace save(AONContext ctx, Workplace workplace) {
		WorkplaceAutoComplete.completeWorkplace(ctx, workplace);
		return workplace.getId() != null 
			? update(ctx, workplace)
			: insert(ctx, workplace);
	}

	public static Workplace insert(AONContext ctx, Workplace workplace) {
		Integer id = ctx.getDslContext().insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, workplace.getDomain())
			.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
			.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
			.set(WORKPLACE.ADDRESS, workplace.getAddress())
			.set(WORKPLACE.CUSTOMER, workplace.getCustomer())
			.set(WORKPLACE.SCOPE, workplace.getScope())
			.set(WORKPLACE.ECONOMICAGREEMENT, workplace.getEconomicagreement())
			.set(WORKPLACE.ACTIVE, workplace.getActive())
			.returning(WORKPLACE.ID).fetchOne().getValue(WORKPLACE.ID);
		workplace.setId(id);
		ctx.log().debug("UPDATE WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	public static Workplace update(AONContext ctx, Workplace workplace) {
		ctx.getDslContext().update(WORKPLACE)
		.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
		.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
		.set(WORKPLACE.ADDRESS, workplace.getAddress())
		.set(WORKPLACE.CUSTOMER, workplace.getCustomer())
		.set(WORKPLACE.SCOPE, workplace.getScope())
		.set(WORKPLACE.ECONOMICAGREEMENT, workplace.getEconomicagreement())
		.set(WORKPLACE.ACTIVE, workplace.getActive())
		.where(WORKPLACE.ID.eq(workplace.getId()))
		.execute();
		ctx.log().debug("UPDATE WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	private static class FullWorkplaceFiller implements Function<WorkplaceRecord, Workplace> {
		
		@Override
		public Workplace apply(WorkplaceRecord r) {
			return new Workplace()
					.setActive(r.getActive())
					.setAddress(r.getAddress())
					.setCustomer(r.getCustomer())
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setEconomicagreement(r.getEconomicagreement())
					.setEnterprise(r.getEnterprise())
					.setId(r.getId())
					.setScope(r.getScope());
		}
	}
	
	protected static class WorkplaceFiller implements Function<Record, Workplace> {
		
		@Override
		public Workplace apply(Record r) {
			return buildWorkplace(r);
		}
		
		public static Workplace buildWorkplace(Record r) {
			return new Workplace()
					.setId(r.getValue(WORKPLACE.ID))
					.setDomain(r.getValue(WORKPLACE.DOMAIN))
					.setActive(r.getValue(WORKPLACE.ACTIVE))
					.setAddress(r.getValue(WORKPLACE.ADDRESS))
					.setCustomer(r.getValue(WORKPLACE.CUSTOMER))
					.setDescription(r.getValue(WORKPLACE.DESCRIPTION))
					.setEconomicagreement(r.getValue(WORKPLACE.ECONOMICAGREEMENT))
					.setEnterprise(r.getValue(WORKPLACE.ENTERPRISE))
					.setScope(r.getValue(WORKPLACE.SCOPE));
		}
	}
	
}
