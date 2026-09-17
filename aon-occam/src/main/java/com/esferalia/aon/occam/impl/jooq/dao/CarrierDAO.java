package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import  org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.validation.CarrierValidation;

public class CarrierDAO {
	
	public static final com.esferalia.aon.jooq.tables.Registry CARRIER_ALIAS = REGISTRY.as("registry_carrier");
	private static final CarrierPropertiesDAO CARRIER_PROPERTIES = new CarrierPropertiesDAO();
	
	private CarrierDAO() {
	
	}
	
	public static class CarrierPropertiesDAO  extends RegistryPropertiesDAO implements CarrierProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.REGISTRY);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.REGISTRY);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.SCOPE);}
		

	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, CarrierFilter filter) {
		return ctx.getDslContext().select()
				.from(CARRIER)
				.join(CARRIER_ALIAS).on(CARRIER_ALIAS.ID.eq(CARRIER.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(CARRIER.SCOPE))
				.where(CARRIER_PROPERTIES.getConditions(filter));	
	}

	public static Carrier get(AONContext ctx, CarrierFilter filter, Options...options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new CarrierFiller())
			.findFirst()
			.orElse(new Carrier());
	}
	
	public static Carrier get(AONContext ctx, Integer id, Options...options){
		return get(ctx, f -> f.getRegistryProperty().eq(id), options);
	}
	
	public static Carrier getFull(AONContext ctx, CarrierFilter filter){
		Carrier carrier = get(ctx, filter);
		if(carrier.getId() != null) {
			carrier.setMainAddress(RegistryOldDAO.getRAddressStream(ctx,
					f -> f.getRegistryProperty().eq(carrier.getId())
						.and(f.getTypeProperty().eq(RegistryAddressDAO.MAIN_ADDRESS)))
				.findFirst().orElse(null));
		}
		return carrier;
	}
	
	public static Carrier getFull(AONContext ctx, Integer id){
		return getFull(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static Stream<Carrier> getStream(AONContext ctx, CarrierFilter filter, Options...options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter)
			.orderBy(CARRIER_ALIAS.NAME)
			.fetch().stream().map(new CarrierFiller());
	}	
	
	public static Stream<Carrier> getStream(AONContext ctx, CarrierFilter filter, Options options){
		if(options.isPagination()) {
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		} else return getStream(ctx, filter);
	}	
	
	public static Stream<Carrier> getStream(AONContext ctx, CarrierFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.orderBy(CARRIER_ALIAS.NAME)
			.limit(perPage).offset(perPage* (page - 1))
			.fetch().stream().map(new CarrierFiller());
	}
	
	public static Carrier save(AONContext ctx, Carrier carrier) {
		ctx.checkWrite();
		CarrierValidation.autocomplete(ctx, carrier);
		CarrierValidation.validate(ctx, carrier);
		boolean nullId = (carrier.getId() == null); 
		carrier.copy(RegistryDAO.save(ctx, carrier));
		return nullId || get(ctx, carrier.getId()).isEmpty()
			? insert(ctx, carrier) : update(ctx, carrier);
	}

	private static Carrier insert(AONContext ctx, Carrier carrier){
		ctx.getDslContext().insertInto(CARRIER)
			.set(CARRIER.REGISTRY, carrier.getId())
			.set(CARRIER.DOMAIN, carrier.getDomain().getId())
			.set(CARRIER.SCOPE, carrier.getScope().getId())
			.set(CARRIER.STATUS, carrier.getStatus().value())
			.execute();
		return carrier;
	}
	
	private static Carrier update(AONContext ctx, Carrier carrier){
		ctx.checkWrite();
		ctx.getDslContext().update(CARRIER)
			.set(CARRIER.DOMAIN, carrier.getDomain().getId())
			.set(CARRIER.SCOPE, carrier.getScope().getId())
			.set(CARRIER.STATUS, carrier.getStatus().value())
			.where(CARRIER.REGISTRY.eq(carrier.getId()))
			.execute();
		return carrier;
	}
	
	public static void delete(AONContext ctx, Integer id){
		CarrierValidation.validateDeletion(ctx, id);
		delete(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	private static void delete(AONContext ctx, CarrierFilter filter){
		ctx.getDslContext().delete(CARRIER)
		.where(CARRIER_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	protected static class CarrierFiller extends Filler implements Function<Record, Carrier> {
	
		@Override
		public Carrier apply(Record r) {
			return build(r);
		}
		
		public static Carrier build(Record r) {
			return new Carrier()
					.copy(RegistryFiller.build(r, CARRIER_ALIAS))
					.setStatus(CarrierStatus.safeValueOf(r.getValue(CARRIER.STATUS)))
					.setScope(checkField(r, SCOPE.ID)
						? ScopeFiller.buildScope(r)
						: new Scope().setId(r.getValue(CARRIER.SCOPE)));
		}
	}
	
}
