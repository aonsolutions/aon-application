package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.function.BiConsumer;
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
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryAddressDAO {
	
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
					.setGeozoneCode(r.getValue(GEOZONE.CODE))
					.setGeozoneName(r.getValue(GEOZONE.NAME))
					.setAlias(r.getValue(RADDRESS.ALIAS))
					.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE))
					.setDirty(false)
					;
		}
	}
	
	private static class RegistryAddressAutoComplete {
		
		public static BiConsumer<AONContext, RegistryAddress> COMPLETE_MAIN_TYPE = (ctx, address) -> {
			if (address.isMain()) {
				RegistryAddressDAO.getStreamByRegistry( ctx, address.getRegistry())
					.filter( adr -> !AonNumberUtils.equals(adr.getId(),address.getId()))
					.filter( adr -> adr.isMain())
					.forEach( adr ->  {
						int count = ctx.getDslContext().update(RADDRESS)
							.set(RADDRESS.TYPE, DELEGATION_ADDRESS)
							.where(RADDRESS.ID.eq(adr.getId()))
							.execute();
						ctx.log().info("\t saving registry address: Setting new Main address, updating olders: " + adr.getId() + ". (" + count + " rows)");				
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
					ctx.log().info("\t saving registry address: autocomplete main flag: " + address.isMain());
				}
				
			}
		};
		
		public static void autoComplete(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
			COMPLETE_MAIN_TYPE
				.accept(ctx,registryAddress);
		}
		
	}
	
	private static class RegistryAddressValidation {
		public static BiConsumer<AONContext,RegistryAddress> EMPTY_DOMAIN = (ctx,registryAddress) -> {
			if (registryAddress.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static BiConsumer<AONContext,RegistryAddress> EMPTY_REGISTRY = (ctx,registryAddress) -> {
			if (registryAddress.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(ADDRESS_REGISTRY_LABEL)) ;
		};
		
		public static BiConsumer<AONContext,RegistryAddress> OVERFLOW_NUMBER = (ctx,registryAddress) -> {
			if (AonStringUtils.length(registryAddress.getNumber()) > RADDRESS.NUMBER.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_NUMBER_LABEL, RADDRESS.NUMBER.getDataType().length() ));
		};
		
		public static BiConsumer<AONContext,RegistryAddress> OVERFLOW_ZIP = (ctx,registryAddress) -> {
			if (AonStringUtils.length(registryAddress.getZip()) > RADDRESS.ZIP.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_ZIP_LABEL, RADDRESS.ZIP.getDataType().length() ));
		};
		

		public static void validate(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_REGISTRY)
				.andThen(OVERFLOW_NUMBER)
				.andThen(OVERFLOW_ZIP)
				.accept(ctx,registryAddress);
		}
		
		public static void validateDeletion(AONContext ctx, Integer id) {
			// TODO Auto-generated method stub
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, RegistryAddressFilter filter) {
		return ctx.getDslContext().select()
				.from(RADDRESS)
				.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(RADDRESS_PROPERTIES.getConditions(filter));
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
		ctx.log().info("INSERT REGISTRY ADDRESS ( registry: "+ address.getRegistry() +") id: " + address.getId());
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
		ctx.log().info("UPDATE REGISTRY ADDRESS ( registry: "+ address.getRegistry() +") id: " + address.getId() + ". (" + count + " rows)");
		address.setDirty(false);
		return address;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RegistryAddressValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RADDRESS)
			.where(RADDRESS.ID.eq(id))
			.execute();
		ctx.log().info("DELETE REGISTRY ADDRESS id:" + id + " ("+count+" rows)");
	}
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		RegistryAddressValidation.validateDeletion(ctx, registry);
		int count = ctx.getDslContext().delete(RADDRESS)
			.where(RADDRESS.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("DELETE REGISTRY ADDRESS registry:" + registry + " ("+count+" rows)");
		return count;
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
