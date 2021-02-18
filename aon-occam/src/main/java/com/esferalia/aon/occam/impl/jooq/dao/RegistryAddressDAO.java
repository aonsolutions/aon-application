package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.StreetType;

public class RegistryAddressDAO {
	
	private static final Byte MAIN_ADDRESS = 0;
	
	private static final RAddressPropertiesDAO RADDRESS_PROPERTIES = new RAddressPropertiesDAO();
	private static class RAddressPropertiesDAO implements RegistryAddressProperties {
		private Condition[] getConditions(RegistryAddressFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.REGISTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RADDRESS.TYPE);}
		@Override public Property<String> getRecipientProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.RECIPIENT);}
		@Override public Property<String> getStreetTypeProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.STREET_TYPE);}
		@Override public Property<String> getAddressProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS);}
		@Override public Property<String> getAddress2Property() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS2);}
		@Override public Property<String> getAddress3Property() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS3);}
		@Override public Property<String> getNumberProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.NUMBER);}
		@Override public Property<String> getZipProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ZIP);}
		@Override public Property<String> getCityProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.CITY);}
		@Override public Property<Integer> getGeozoneProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.GEOZONE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ALIAS);}
		@Override public Property<String> getMunicipalityCodeProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.MUNICIPALITY_CODE);}
	}

	public static class RegistryAddressFiller implements Function<Record, RegistryAddress> {

		@Override
		public RegistryAddress apply(Record r) {
			return new RegistryAddress()
					.setId(r.getValue(RADDRESS.ID))
					.setDomain(r.getValue(RADDRESS.DOMAIN))
					.setRegistry(r.getValue(RADDRESS.REGISTRY))
					.setMain(r.getValue(RADDRESS.TYPE)==MAIN_ADDRESS)
					.setRecipient(r.getValue(RADDRESS.RECIPIENT))
					.setStreetType(StreetType.safeValueOf(r.getValue(RADDRESS.STREET_TYPE)))
					.setAddress(r.getValue(RADDRESS.ADDRESS))
					.setNumber(r.getValue(RADDRESS.NUMBER))
					.setAddress2(r.getValue(RADDRESS.ADDRESS2))
					.setAddress3(r.getValue(RADDRESS.ADDRESS3))
					.setZip(r.getValue(RADDRESS.ZIP))
					.setCity(r.getValue(RADDRESS.CITY))
					.setGeozone(r.getValue(RADDRESS.GEOZONE))
					.setGeozoneName(r.getValue(GEOZONE.NAME))
					.setAlias(r.getValue(RADDRESS.ALIAS))
					.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE))
					;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistryAddressFilter filter) {
		return ctx.getDslContext().select()
				.from(RADDRESS)
				.join(REGISTRY).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
				.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(RADDRESS_PROPERTIES.getConditions(filter));
	}

	public static RegistryAddress getMain(AONContext ctx, Integer registry){
		return RegistryAddressDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry)
				.and(f.getTypeProperty().eq( MAIN_ADDRESS )))
				.findFirst()
				.orElse(null);
	}

	public static Stream<RegistryAddress> getStreamByRegistry(AONContext ctx, Integer registry){
		return RegistryAddressDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry));
	}
	
	public static Stream<RegistryAddress> getStream(AONContext ctx, RegistryAddressFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryAddressFiller());
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static RegistryAddress getRandom(AONContext ctx, RegistryAddressFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryAddressFiller())
			.findFirst()
			.orElse(null);
	}
	

}
