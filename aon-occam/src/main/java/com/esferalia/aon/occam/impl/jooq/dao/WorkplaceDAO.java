package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.impl.jooq.validation.WorkplaceAutoComplete;

public class WorkplaceDAO {
	
	private WorkplaceDAO() {
	
	}
	
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
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(DSL.trim(WORKPLACE.DESCRIPTION));}
		@Override public Property<Byte> getEconomicagreementProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ECONOMICAGREEMENT);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.ENTERPRISE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(WORKPLACE.SCOPE);}	
	}
	
	public static Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter){
		return ctx.getDslContext().select().from(WORKPLACE).where(WORKPLACE_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(WORKPLACE).stream().map(new WorkplaceFiller()).findFirst().orElse(null);	
	}
	
	public static LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter){
		return ctx.getDslContext().select().from(WORKPLACE)
				.where(WORKPLACE_PROPERTIES.getConditions(filter))
				.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE))
				.orderBy(WORKPLACE.DESCRIPTION)
				.fetchInto(WORKPLACE)
				.stream()
				.map(new WorkplaceFiller())
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
			.set(WORKPLACE.ACTIVE, workplace.isActive() ? (byte) 1 : 0)
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
		.set(WORKPLACE.ACTIVE, workplace.isActive() ? (byte) 1 : 0)
		.where(WORKPLACE.ID.eq(workplace.getId()))
		.execute();
		ctx.log().debug("UPDATE WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	protected static class WorkplaceFiller extends Filler implements Function<Record, Workplace> {
		
		@Override
		public Workplace apply(Record r) {
			return build(r);
		}
		
		public static Workplace build(Record r) {
			return new Workplace()
					.setId(getValue(r, WORKPLACE.ID))
					.setDomain(getValue(r, WORKPLACE.DOMAIN))
					.setActive(getBoolean(r, WORKPLACE.ACTIVE))
					.setAddress(getValue(r, WORKPLACE.ADDRESS))
					.setCustomer(getValue(r, WORKPLACE.CUSTOMER))
					.setDescription(getValue(r, WORKPLACE.DESCRIPTION))
					.setEconomicagreement(getValue(r, WORKPLACE.ECONOMICAGREEMENT))
					.setEnterprise(getValue(r, WORKPLACE.ENTERPRISE))
					.setScope(getValue(r, WORKPLACE.SCOPE));
		}
	}
	
}
