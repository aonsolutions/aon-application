package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.CHILD_GEOZONE;
import static com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.PARENT_GEOZONE;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.validation.WorkplaceAutoComplete;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class WorkplaceDAO {
	
	private WorkplaceDAO() {
	
	}
	
	private static final WorkplacePropertiesDAO WORKPLACE_PROPERTIES = new WorkplacePropertiesDAO();

	private static class WorkplacePropertiesDAO implements WorkplaceProperties {
		protected Condition[] getConditions(WorkplaceFilter filter) {
			if (filter == null) return new Condition[0];
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
	
	// ----- SELECT

	private static SelectConditionStep<Record> select(AONContext ctx, WorkplaceFilter filter) {
		 return ctx.getDslContext().select()
			.from(WORKPLACE)
			.leftOuterJoin(PAYROLL_WORKPLACE).on(PAYROLL_WORKPLACE.WORKPLACE.eq(WORKPLACE.ID))
			.where(WORKPLACE_PROPERTIES.getConditions(filter));
	}
	
	private static SelectConditionStep<Record> selectFull(AONContext ctx, WorkplaceFilter filter) {
		 return ctx.getDslContext().select()
			.from(WORKPLACE)
			.leftOuterJoin(PAYROLL_WORKPLACE).on(PAYROLL_WORKPLACE.WORKPLACE.eq(WORKPLACE.ID))
			.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(WORKPLACE.ADDRESS))
			.leftOuterJoin(DOMAIN).on(DOMAIN.ID.eq(RADDRESS.DOMAIN))
			.leftOuterJoin(CHILD_GEOZONE).on(CHILD_GEOZONE.ID.eq(RADDRESS.GEOZONE))
			.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE).and(GEOTREE.DOMAIN.eq(DOMAIN.ID).or(GEOTREE.DOMAIN.eq(DOMAIN.PARENT))))
			.leftOuterJoin(PARENT_GEOZONE).on(PARENT_GEOZONE.ID.eq(GEOTREE.PARENT))
			.where(WORKPLACE_PROPERTIES.getConditions(filter));
	}
	
	private static SelectConditionStep<Record> withSecurity(AONContext ctx, SelectConditionStep<Record> select) {
		 return select.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE));
	}
	
	// ----- GET

	public static Workplace get(AONContext ctx, Integer workplaceId, Options... options){ 
		return get(ctx, f -> f.getIdProperty().eq(workplaceId), options);
	}
	
	public static Workplace get(AONContext ctx, WorkplaceFilter filter, Options... options){ 
		Options opts = options.length > 0 ? options[0] : new Options();
		return get(ctx, filter, opts);
	}
	
	public static Workplace get(AONContext ctx, WorkplaceFilter filter, Options options){ 
		ctx.checkRead();
		SelectConditionStep<Record> select = options.isFull() 
			? selectFull(ctx, filter) : select(ctx, filter);
		if(options.isSecurity()) select = withSecurity(ctx, select);
		return select.limit(1).fetch().stream().map(new WorkplaceFiller())
			.findFirst().orElse(null);
	}
	
	// ----- GET STREAM
	
	public static Stream<Workplace> getStream(AONContext ctx, WorkplaceFilter filter, Options... options){ 
		Options opts = options.length > 0 ? options[0] : new Options();
		return getStream(ctx, filter, opts);
	}
	
	public static Stream<Workplace> getStream(AONContext ctx, WorkplaceFilter filter, Options options){ 
		ctx.checkRead();
		SelectConditionStep<Record> select = options.isFull() 
			? selectFull(ctx, filter) : select(ctx, filter);
		if(options.isSecurity()) select = withSecurity(ctx, select);
		return select.fetch().stream().map(new WorkplaceFiller());
	}
		
	@Deprecated
	public static Optional<Workplace> getWorkplace(AONContext ctx, Integer domainId, Integer worplaceId){
		return Optional.ofNullable(get(ctx, 
			f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(worplaceId)),
			new Options().setFull(true)));		
	}

	@Deprecated
	public static Stream<Workplace> getWorkplaces(AONContext ctx, Integer domainId){
		return getStream(ctx, f -> f.getDomainProperty().eq(domainId));
	}

	@Deprecated
	public static Stream<Workplace> getWorkplacesNoScope(AONContext ctx, Integer domainId){
		return getStream(ctx, f -> f.getDomainProperty().eq(domainId), new Options().setSecurity(false));
	}
	
	public static Workplace save(AONContext ctx, Workplace workplace) {
		ctx.checkWrite();
		WorkplaceAutoComplete.completeWorkplace(ctx, workplace);
		Workplace savedWorkplace = workplace.getId() != null 
			? update(ctx, workplace)
			: insert(ctx, workplace);
		
		if(null != workplace.getPayrollWorkplace()) {
			workplace.getPayrollWorkplace().setWorkplace(savedWorkplace.getId());
			PayrollWorkplaceDAO.save(ctx, workplace.getPayrollWorkplace());
		}
		
		return savedWorkplace;
	}
	
	private static Workplace insert(AONContext ctx, Workplace workplace) {
		Integer id = ctx.getDslContext().insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, workplace.getDomain())
			.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
			.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
			.set(WORKPLACE.ADDRESS, workplace.getAddress() != null ? workplace.getAddress().getId() : null)
			.set(WORKPLACE.CUSTOMER, workplace.getCustomer())
			.set(WORKPLACE.SCOPE, workplace.getScope())
			.set(WORKPLACE.ECONOMICAGREEMENT, workplace.getEconomicAgreement().value())
			.set(WORKPLACE.ACTIVE, AonEnumUtils.getByte( workplace.isActive() ) )
			.returning(WORKPLACE.ID)
			.fetchOne()
			.getValue(WORKPLACE.ID);
		workplace.setId(id);
		ctx.log().debug("INSERT WORKPLACE id: " + workplace.getId());	
		return workplace;
	}
	
	private static Workplace update(AONContext ctx, Workplace workplace) {
		ctx.getDslContext().update(WORKPLACE)
		.set(WORKPLACE.ENTERPRISE, workplace.getEnterprise())
		.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
		.set(WORKPLACE.ADDRESS, workplace.getAddress() != null ? workplace.getAddress().getId() : null)
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
				.setAddress(hasValue(r, RADDRESS.ID) 
					? RegistryAddressFiller.build(r)  
					: (isNull(r, WORKPLACE.ADDRESS) ? null : new RegistryAddress().setId(getValue(r, WORKPLACE.ADDRESS))))
				.setCustomer(getValue(r, WORKPLACE.CUSTOMER))
				.setDescription(getValue(r, WORKPLACE.DESCRIPTION))
				.setEconomicAgreement(Administration.safeValueOf(getValue(r, WORKPLACE.ECONOMICAGREEMENT)))
				.setEnterprise(getValue(r, WORKPLACE.ENTERPRISE))
				.setScope(getValue(r, WORKPLACE.SCOPE))
				.setPayrollWorkplace(hasValue(r, PAYROLL_WORKPLACE.ID) 
					? PayrollWorkplaceDAO.PayrollWorkplaceFiller.build(r) : null);
		}
	}

}
