package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.validation.WorkplaceAutoComplete;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class WorkplaceDAO {
	
	private WorkplaceDAO() {
	
	}
	
	public static Optional<Workplace> getWorkplace(AONContext ctx, Integer domainId, Integer worplaceId){
		ctx.checkRead();
		return ctx.getDslContext().select()
			.from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domainId))
			.and(WORKPLACE.ID.eq(worplaceId))
			.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE))
			.fetch()
			.stream()
			.map(new WorkplaceFiller())
			.findFirst();
		
	}
	
	public static Stream<Workplace> getWorkplaces(AONContext ctx, Integer domainId){
		ctx.checkRead();
		List<Workplace> workplaces = ctx.getDslContext().select()
			.from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domainId))
			.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE))
			.fetch()
			.stream()
			.map(new WorkplaceFiller())
			.collect(Collectors.toList());
		
		workplaces.forEach(w -> w.setPayrollWorkplace(PayrollWorkplaceDAO.get(ctx, f -> f.getWorkplaceProperty().eq(w.getId()) )));
		
		return workplaces.stream();
	}
	
	public static Stream<Workplace> getWorkplacesNoScope(AONContext ctx, Integer domainId){
		ctx.checkRead();
		return ctx.getDslContext().select()
			.from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.map(new WorkplaceFiller());
	}
	
	public static Workplace save(AONContext ctx, Workplace workplace) {
		ctx.checkWrite();
		WorkplaceAutoComplete.completeWorkplace(ctx, workplace);
		Workplace savedWorkplace = workplace.getId() != null 
			? update(ctx, workplace)
			: insert(ctx, workplace);
		
		workplace.getPayrollWorkplace().setWorkplace(savedWorkplace.getId());
		PayrollWorkplaceDAO.save(ctx, workplace.getPayrollWorkplace());
		
		return savedWorkplace;
	}
	
	private static Workplace insert(AONContext ctx, Workplace workplace) {
		Integer id = ctx.getDslContext().insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, workplace.getDomain())
			.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
			.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
			.set(WORKPLACE.ADDRESS, workplace.getAddress())
			.set(WORKPLACE.CUSTOMER, workplace.getCustomer())
			.set(WORKPLACE.SCOPE, workplace.getScope())
			.set(WORKPLACE.ECONOMICAGREEMENT, workplace.getEconomicAgreement().value())
			.set(WORKPLACE.ACTIVE, AonEnumUtils.getByte( workplace.isActive() ) )
			.returning(WORKPLACE.ID)
			.fetchOne()
			.getValue(WORKPLACE.ID);
		workplace.setId(id);
		ctx.log().debug("UPDATE WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	private static Workplace update(AONContext ctx, Workplace workplace) {
		ctx.getDslContext().update(WORKPLACE)
		.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
		.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
		.set(WORKPLACE.ADDRESS, workplace.getAddress())
		.set(WORKPLACE.CUSTOMER, workplace.getCustomer())
		.set(WORKPLACE.SCOPE, workplace.getScope())
		.set(WORKPLACE.ECONOMICAGREEMENT, workplace.getEconomicAgreement().value())
		.set(WORKPLACE.ACTIVE, AonEnumUtils.getByte( workplace.isActive() ) )
		.where(WORKPLACE.ID.eq(workplace.getId()))
		.execute();
		ctx.log().debug("UPDATE WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	public static void delete(AONContext ctx, Integer workplaceId) {
		
		PayrollWorkplaceDAO.delete(ctx, workplaceId);
		
		ctx.getDslContext().delete(WORKPLACE)
			.where(WORKPLACE.ID.eq(workplaceId))
			.execute();
		
		ctx.log().debug("DELETE WORKPLACE id: " + workplaceId);	
		
	}
	
	static class WorkplaceFiller extends Filler implements Function<Record, Workplace> {
		
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
				.setEconomicAgreement(Administration.safeValueOf(getValue(r, WORKPLACE.ECONOMICAGREEMENT)))
				.setEnterprise(getValue(r, WORKPLACE.ENTERPRISE))
				.setScope(getValue(r, WORKPLACE.SCOPE));
		}
	}
	// ******************************************************************
	// ***************************************************** [OLD] ******
	// ******************************************************************
	// ******************************************************************
	@Deprecated
	private static final WorkplacePropertiesDAO WORKPLACE_PROPERTIES = new WorkplacePropertiesDAO();
	@Deprecated
	private static class WorkplacePropertiesDAO implements WorkplaceProperties {
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
		
		@Override public Property<String> getGeozoneNameProperty() {return new FilterDAO.PropertyDAO<>(GEOZONE.NAME);}
	}
	
	/**
	 * @deprecated Use of domain is mandatory and 
	 * 			Filter can return multiple results. 
	 * 			If nothing is found, null must be returned.
	 * 		 	Use Optional<Workplace> getWorkplace(AONContext ctx, Integer domainId, Integer id) instead.
	 */
	@Deprecated
	public static Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter){
		return ctx.getDslContext().select().from(WORKPLACE)
				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(WORKPLACE.ADDRESS))
				.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(WORKPLACE_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(WORKPLACE).stream().map(new WorkplaceFiller()).findFirst().orElse(null);	
	}
	
	/**
	 * @deprecated Use of domain is mandatory
	 * @user Stream<Workplace> getWorkplaceList(AONContext ctx, Integer domainId) instead.
	 */
	@Deprecated
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

}
