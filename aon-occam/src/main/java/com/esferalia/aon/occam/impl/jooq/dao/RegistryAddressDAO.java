package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO.GeoZoneFiller;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryAddressValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryAddressDAO {
	private RegistryAddressDAO() {
		
	}
	private static final Byte MAIN_ADDRESS = 0;
	private static final Byte DELEGATION_ADDRESS = 1;
	
	public static final String ADDRESS_REGISTRY_LABEL = "Registry";
	public static final String ADDRESS_NUMBER_LABEL = "N\u00fcmero";
	public static final String ADDRESS_ZIP_LABEL = "C\u00F3digo postal";

	
	private static final RAddressPropertiesDAO RADDRESS_PROPERTIES = new RAddressPropertiesDAO();
	private static class RAddressPropertiesDAO implements RegistryAddressProperties {
		private Condition[] getConditions(RegistryAddressFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.REGISTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.TYPE);}
		@Override public Property<String> getRecipientProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.RECIPIENT);}
		@Override public Property<String> getStreetTypeProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.STREET_TYPE);}
		@Override public Property<String> getAddressProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.ADDRESS);}
		@Override public Property<String> getAddress2Property() {return new FilterDAO.PropertyDAO<>(RADDRESS.ADDRESS2);}
		@Override public Property<String> getAddress3Property() {return new FilterDAO.PropertyDAO<>(RADDRESS.ADDRESS3);}
		@Override public Property<String> getNumberProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.NUMBER);}
		@Override public Property<String> getZipProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.ZIP);}
		@Override public Property<String> getCityProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.CITY);}
		@Override public Property<Integer> getGeozoneProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.GEOZONE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.ALIAS);}
		@Override public Property<String> getMunicipalityCodeProperty() {return new FilterDAO.PropertyDAO<>(RADDRESS.MUNICIPALITY_CODE);}
	}

	public static class RegistryAddressFiller implements Function<Record, RegistryAddress> {

		@Override
		public RegistryAddress apply(Record r) {
			return build(r);
		}
		
		public static RegistryAddress build(Record r) {
			com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
			com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");
			return build(r, parent, child);
		}
		
		public static RegistryAddress build(Record r, Geozone parent, Geozone child) {
			return new RegistryAddress()
					.setId(r.getValue(RADDRESS.ID))
					.setDomain(r.getValue(RADDRESS.DOMAIN))
					.setRegistry(r.getValue(RADDRESS.REGISTRY))
					.setMain(MAIN_ADDRESS.equals(r.getValue(RADDRESS.TYPE)))
					.setRecipient(r.getValue(RADDRESS.RECIPIENT))
					.setStreetType(StreetType.getForAeatCode(r.getValue(RADDRESS.STREET_TYPE), AonLanguage.SPANISH))
					.setAddress(r.getValue(RADDRESS.ADDRESS))
					.setNumber(r.getValue(RADDRESS.NUMBER))
					.setAddress2(r.getValue(RADDRESS.ADDRESS2))
					.setAddress3(r.getValue(RADDRESS.ADDRESS3))
					.setZip(r.getValue(RADDRESS.ZIP))
					.setCity(r.getValue(RADDRESS.CITY))
					.setGeozone(r.getValue(RADDRESS.GEOZONE))
					.setGeozoneCode(r.getValue(child.CODE))
					.setGeozoneName(r.getValue(child.NAME))
	
					.setChild(GeoZoneFiller.build(r, child))
					.setParent(GeoZoneFiller.build(r, parent))
					
					.setAlias(r.getValue(RADDRESS.ALIAS))
					.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE))
					.setDirty(false)
					;
		}
	}
	
	private static class RegistryAddressAutoComplete {
		
		public static final BiConsumer<AONContext, RegistryAddress> COMPLETE_MAIN_TYPE = (ctx, address) -> {
			if (address.isMain()) {
				RegistryAddressDAO.getStreamByRegistry( ctx, address.getRegistry())
					.filter( adr -> !AonNumberUtils.equals(adr.getId(),address.getId()))
					.filter( adr -> adr.isMain())
					.forEach( adr ->  {
						int count = ctx.getDslContext().update(RADDRESS)
							.set(RADDRESS.TYPE, DELEGATION_ADDRESS)
							.where(RADDRESS.ID.eq(adr.getId()))
							.execute();
						ctx.log().debug("\t saving registry address: Setting new Main address, updating olders: {0}. ({1} rows)",adr.getId(),count);				
					});
			} else {
				int count = ctx.getDslContext().fetchCount(
						ctx.getDslContext().select( RADDRESS.ID )
							.from(RADDRESS)
							.where(RADDRESS.REGISTRY.eq(address.getRegistry()))
							.and(address.getId() == null
								?DSL.trueCondition()
								:RADDRESS.ID.ne(address.getId()))
							.and(RADDRESS.TYPE.eq( MAIN_ADDRESS ))
					);
				if (count == 0) {
					address.setMain(true);
					ctx.log().debug("\t saving registry address: autocomplete main flag: {0}",address.isMain());
				}
				
			}
		};
		
		public static final BiConsumer<AONContext, RegistryAddress> COMPLETE_GEOZONE = (ctx, address) -> {
			if(address.getGeozone() == null) {
				GeoZone geozone = new GeoZone();
				if(Country.ES.equals(address.getCountry()) && address.getZip() != null
						&& address.getZip().length() > 1) {
					geozone = GeoZoneDAO.get(ctx, f -> f.getCodeProperty().eq(address.getZip().substring(0, 2)));
				}
				
				if((geozone == null || geozone.isEmpty()) && address.getCountry() != null) {
					GeoZone geozoneCountry  = GeoZoneDAO.get(ctx, f -> f.getCodeProperty().eq(address.getCountry().getIso2()));					
					if(geozoneCountry == null || geozoneCountry.isEmpty()) {
						geozoneCountry = new GeoZone()
								.setDomain(address.getDomain())
								.setName(address.getCountry().getName())
								.setCode(address.getCountry().getIso2())
								.setSystem(true);
						geozoneCountry = GeoZoneDAO.insert(ctx, geozoneCountry);
					}

					if(AonStringUtils.isBlank(address.getProvince())) address.setProvince(address.getCity());
					geozone = GeoZoneDAO.getChild(ctx, geozoneCountry.getId(), f -> f.getNameProperty().eq(address.getProvince()));
					if(geozone == null ||  geozone.isEmpty()) {
						geozone = new GeoZone()
	 							.setDomain(address.getDomain())
								.setName(address.getProvince() )
								.setCode("00")
								.setSystem(true);		
						geozone = GeoZoneDAO.insert(ctx, geozone);
					}
					GeoZoneDAO.bind(ctx, address.getDomain(), geozoneCountry.getId(), geozone.getId());
				}
				
				if((geozone == null || geozone.isEmpty()) && !AonStringUtils.isBlank(address.getProvince())) {
					geozone = GeoZoneDAO.get(ctx, f -> f.getNameProperty().eq(address.getProvince()));
				}
				
				if(geozone != null && !geozone.isEmpty()) {
					address.setGeozone(geozone.getId());
				}
			}
		};
		
		public static void autoComplete(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
			COMPLETE_MAIN_TYPE
				.andThen(COMPLETE_GEOZONE)
				.accept(ctx,registryAddress);
		}
		
	}

	private static SelectConditionStep<Record> select(AONContext ctx, RegistryAddressFilter filter) {
		com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
		com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");

		return ctx.getDslContext().select()
				.from(RADDRESS)
				.leftOuterJoin(child).on(child.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(parent).on(parent.ID.eq(GEOTREE.PARENT))
				.where(RADDRESS_PROPERTIES.getConditions(filter));
	}

	public static RegistryAddress get(AONContext ctx, RegistryAddressFilter filter){
		return select(ctx,filter).limit(1)
				.fetch().stream().map(new RegistryAddressFiller())
				.findFirst().orElse(new RegistryAddress());
	}
	
	public static RegistryAddress get(AONContext ctx, Integer id){
		return RegistryAddressDAO.getStream(ctx, f -> f.getIdProperty().eq(id))
				.findFirst()
				.orElse(null);
	}
	
	public static RegistryAddress getMain(AONContext ctx, Integer registry){
		return RegistryAddressDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry)
				.and(f.getTypeProperty().eq( MAIN_ADDRESS )))
				.findFirst()
				.orElse(null);
	}

	public static Stream<RegistryAddress> getStreamByRegistry(AONContext ctx, Integer registryId){
		return RegistryAddressDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registryId));
	}
	
	public static Stream<RegistryAddress> getStream(AONContext ctx, RegistryAddressFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryAddressFiller());
	}
	
	public static RegistryAddress save(AONContext ctx, RegistryAddress registryAddress) {
		ctx.checkWrite();
		if(registryAddress.getId() != null && registryAddress.isRemoved()) { 
			delete(ctx, registryAddress.getId());
			return registryAddress;
		}
		if(!registryAddress.isDirty()) return registryAddress;
		RegistryAddressAutoComplete.autoComplete(ctx, registryAddress);
		RegistryAddressValidation.validate(ctx, registryAddress);
		return (registryAddress.getId() == null)
				?insert(ctx, registryAddress)
				:update(ctx, registryAddress);
	}
	
	private static RegistryAddress insert(AONContext ctx, RegistryAddress address){
		Integer id = ctx.getDslContext().insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN,address.getDomain())
			.set(RADDRESS.REGISTRY,address.getRegistry())
			.set(RADDRESS.TYPE,address.isMain()?MAIN_ADDRESS:DELEGATION_ADDRESS)
			.set(RADDRESS.RECIPIENT,address.getRecipient())
			.set(RADDRESS.STREET_TYPE,address.getStreetType()==null?null:address.getStreetType().getAeatCode())
			.set(RADDRESS.ADDRESS,address.getAddress())	
			.set(RADDRESS.NUMBER,address.getNumber())	
			.set(RADDRESS.ADDRESS2,address.getAddress2())
			.set(RADDRESS.ADDRESS3,address.getAddress3())
			.set(RADDRESS.ZIP,address.getZip())
			.set(RADDRESS.CITY,address.getCity())
			.set(RADDRESS.GEOZONE,address.getGeozone())	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.returning(RADDRESS.ID)
			.fetchOne()
			.getValue(RADDRESS.ID);
		address.setId(id).setDirty(false);
		ctx.log().debug("INSERT REGISTRY ADDRESS ( registry: {0}) id: {1}",address.getRegistry(),address.getId());
		return address;
	}
	private static RegistryAddress update(AONContext ctx, RegistryAddress address){
		int count = ctx.getDslContext().update(RADDRESS)
			.set(RADDRESS.DOMAIN,address.getDomain())
			.set(RADDRESS.REGISTRY,address.getRegistry())
			.set(RADDRESS.TYPE,address.isMain()?MAIN_ADDRESS:DELEGATION_ADDRESS)
			.set(RADDRESS.RECIPIENT,address.getRecipient())
			.set(RADDRESS.STREET_TYPE,address.getStreetType()==null?null:address.getStreetType().getAeatCode())
			.set(RADDRESS.ADDRESS,address.getAddress())	
			.set(RADDRESS.NUMBER,address.getNumber())	
			.set(RADDRESS.ADDRESS2,address.getAddress2())
			.set(RADDRESS.ADDRESS3,address.getAddress3())
			.set(RADDRESS.ZIP,address.getZip())
			.set(RADDRESS.CITY,address.getCity())
			.set(RADDRESS.GEOZONE,address.getGeozone())	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.where(RADDRESS.ID.eq(address.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY ADDRESS ( registry: {0}) id: {1}. ({2} rows)",address.getRegistry(),address.getId(),count);
		address.setDirty(false);
		return address;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RegistryAddressValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RADDRESS)
			.where(RADDRESS.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY ADDRESS id: {0} ({1} rows)",id,count);
	}
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		RegistryAddressValidation.validateDeletion(ctx, registry);
		int count = ctx.getDslContext().delete(RADDRESS)
			.where(RADDRESS.REGISTRY.eq(registry))
			.execute();
		ctx.log().debug("DELETE REGISTRY ADDRESS registry: {0} ({1} rows)",registry,count);
		return count;
	}
	
	public static Integer getMainAddressProvince(AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.select(GEOZONE.CODE)
			.from(RADDRESS)
			.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.where(RADDRESS.REGISTRY.equal(registry))
			.and(RADDRESS.TYPE.equal( MAIN_ADDRESS ))
			.limit(1)
			.fetch()
			.stream()
			.mapToInt(rec -> AonNumberUtils.toint(rec.getValue(GEOZONE.CODE) ))
			.findFirst()
			.orElse(0);
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
