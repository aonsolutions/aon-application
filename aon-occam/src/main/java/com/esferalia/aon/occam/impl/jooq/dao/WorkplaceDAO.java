package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(WORKPLACE.ACTIVE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.DOMAIN);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.ADDRESS);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.CUSTOMER);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(WORKPLACE.DESCRIPTION);}
		@Override public Property<Byte> getEconomicagreementProperty() {return new FilterDAO.PropertyDAO<Byte>(WORKPLACE.ECONOMICAGREEMENT);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.ENTERPRISE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(WORKPLACE.SCOPE);}	
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
	
	public static void updateWorkplace(AONContext ctx, Workplace workplace) {
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
	};
	
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
	
}
