package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Geozone;
import net.aonsolutions.occam.api.model.RegistryAddress;
import net.aonsolutions.occam.api.model.type.AonLanguage;
import net.aonsolutions.occam.api.model.type.StreetType;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.GeozoneHandler.GeozoneFiller;

public class RegistryAddressHandler {
	private RegistryAddressHandler() {
		
	}
	private static final com.esferalia.aon.jooq.tables.Geozone PARENT = GEOZONE.as("parentGeozone");
	private static final com.esferalia.aon.jooq.tables.Geozone CHILD = GEOZONE.as("childGeozone");
	
	private static final Byte MAIN_ADDRESS = 0;
	private static final Byte DELEGATION_ADDRESS = 1;
	
	static class RegistryAddressFiller extends Filler<RegistryAddress> {

		@Override
		public RegistryAddress apply(Record r) {
			return build(r);
		}
		
		public static RegistryAddress build(Record r) {
			return build(r, PARENT, CHILD);
		}
		
		public static RegistryAddress build(Record r, com.esferalia.aon.jooq.tables.Geozone parent, com.esferalia.aon.jooq.tables.Geozone child) {
			return new RegistryAddress()
				.setId(r.getValue(RADDRESS.ID))
				.setDomain(r.getValue(RADDRESS.DOMAIN))
				.setRegistry(r.getValue(RADDRESS.REGISTRY))
				.setMain(MAIN_ADDRESS.equals(r.getValue(RADDRESS.TYPE)))
				.setRecipient(r.getValue(RADDRESS.RECIPIENT))
				.setStreetType(StreetType.valueOfAeatCode(getValue(r,RADDRESS.STREET_TYPE), AonLanguage.SPANISH).orElse(null))
				.setAddress(r.getValue(RADDRESS.ADDRESS))
				.setNumber(r.getValue(RADDRESS.NUMBER))
				.setAddress2(r.getValue(RADDRESS.ADDRESS2))
				.setAddress3(r.getValue(RADDRESS.ADDRESS3))
				.setZip(r.getValue(RADDRESS.ZIP))
				.setCity(r.getValue(RADDRESS.CITY))
				.setGeozone(GeozoneFiller.build(r, child))
				.setParent(GeozoneFiller.build(r, parent))
				.setAlias(r.getValue(RADDRESS.ALIAS))
				.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE))
			;
		}
	}

	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
			.from(RADDRESS)
			.join(DOMAIN).on(DOMAIN.ID.eq(RADDRESS.DOMAIN))
			.leftOuterJoin(CHILD).on(CHILD.ID.eq(RADDRESS.GEOZONE))
			.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE).and(GEOTREE.DOMAIN.eq(DOMAIN.ID).or(GEOTREE.DOMAIN.eq(DOMAIN.PARENT))))
			.leftOuterJoin(PARENT).on(PARENT.ID.eq(GEOTREE.PARENT))
		;
	}

	static Optional<RegistryAddress> get(AONContext ctx, Integer id){
		return select(ctx)
			.where(RADDRESS.ID.eq(id)) 
			.fetch()
			.stream()
			.map(new RegistryAddressFiller())
			.findFirst()
		;
	}

	static Stream<RegistryAddress> streamByRegistry(AONContext ctx, Integer registryId){
		return select(ctx)
			.where(RADDRESS.REGISTRY.eq(registryId)) 
			.fetch()
			.stream()
			.map(new RegistryAddressFiller())
		;
	}
	
	static RegistryAddress save(AONContext ctx, RegistryAddress registryAddress) {
		ctx.checkWrite();
		if(registryAddress.getId() != null && registryAddress.isDeleted()) { 
			delete(ctx, registryAddress.getId());
			return registryAddress;
		}
		RegistryAddressAutoComplete.autoComplete(ctx, registryAddress);
		RegistryAddressValidation.validate(ctx, registryAddress);
		return (registryAddress.getId() == null)
			?insert(ctx, registryAddress)
			:update(ctx, registryAddress)
		;
	}
	
	private static RegistryAddress insert(AONContext ctx, RegistryAddress address){
		Integer id = ctx.getDslContext().insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN,address.getDomain())
			.set(RADDRESS.REGISTRY,address.getRegistry())
			.set(RADDRESS.TYPE,address.isMain()?MAIN_ADDRESS:DELEGATION_ADDRESS)
			.set(RADDRESS.RECIPIENT,address.getRecipient())
			.set(RADDRESS.STREET_TYPE,	StreetType.value(address.getStreetType()))
			.set(RADDRESS.ADDRESS,address.getAddress())	
			.set(RADDRESS.NUMBER,address.getNumber())	
			.set(RADDRESS.ADDRESS2,address.getAddress2())
			.set(RADDRESS.ADDRESS3,address.getAddress3())
			.set(RADDRESS.ZIP,address.getZip())
			.set(RADDRESS.CITY,address.getCity())
			.set(RADDRESS.GEOZONE,address.getGeozone().map(Geozone::getId).orElse(null))	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.returning(RADDRESS.ID)
			.fetchOne()
			.getValue(RADDRESS.ID);
		address.setId(id);
		ctx.log().debug("INSERT REGISTRY ADDRESS ( registry: {0}) id: {1}",address.getRegistry(),address.getId());
		return address;
	}
	private static RegistryAddress update(AONContext ctx, RegistryAddress address){
		int count = ctx.getDslContext().update(RADDRESS)
			.set(RADDRESS.DOMAIN,address.getDomain())
			.set(RADDRESS.REGISTRY,address.getRegistry())
			.set(RADDRESS.TYPE,address.isMain()?MAIN_ADDRESS:DELEGATION_ADDRESS)
			.set(RADDRESS.RECIPIENT,address.getRecipient())
			.set(RADDRESS.STREET_TYPE,	StreetType.value(address.getStreetType()))
			.set(RADDRESS.ADDRESS,address.getAddress())	
			.set(RADDRESS.NUMBER,address.getNumber())	
			.set(RADDRESS.ADDRESS2,address.getAddress2())
			.set(RADDRESS.ADDRESS3,address.getAddress3())
			.set(RADDRESS.ZIP,address.getZip())
			.set(RADDRESS.CITY,address.getCity())
			.set(RADDRESS.GEOZONE,address.getGeozone().map(Geozone::getId).orElse(null))	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.where(RADDRESS.ID.eq(address.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY ADDRESS ( registry: {0}) id: {1}. ({2} rows)",address.getRegistry(),address.getId(),count);
		return address;
	}

	static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RegistryAddressValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RADDRESS)
			.where(RADDRESS.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY ADDRESS id: {0} ({1} rows)",id,count);
	}
	
	// ****************************************************************
	// ****************************************************************
	// ****************************************************************
	
	
	private class RegistryAddressValidation {
		
		public static final String ADDRESS_REGISTRY_LABEL = "Registry";
		public static final String ADDRESS_NUMBER_LABEL = "N\u00fcmero";
		public static final String ADDRESS_ZIP_LABEL = "C\u00F3digo postal";

		private RegistryAddressValidation() {}
		
		public static final BiConsumer<AONContext,RegistryAddress> EMPTY_DOMAIN = (ctx,registryAddress) -> {
			if (registryAddress.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext,RegistryAddress> EMPTY_REGISTRY = (ctx,registryAddress) -> {
			if (registryAddress.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(ADDRESS_REGISTRY_LABEL)) ;
		};
		
		public static final BiConsumer<AONContext,RegistryAddress> OVERFLOW_NUMBER = (ctx,registryAddress) -> {
			if (AonStringUtils.length(registryAddress.getNumber()) > RADDRESS.NUMBER.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_NUMBER_LABEL, RADDRESS.NUMBER.getDataType().length() ));
		};
		
		public static final BiConsumer<AONContext,RegistryAddress> OVERFLOW_ZIP = (ctx,registryAddress) -> {
			if (AonStringUtils.length(registryAddress.getZip()) > RADDRESS.ZIP.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_ZIP_LABEL, RADDRESS.ZIP.getDataType().length() ));
		};
		
		public static final ObjIntConsumer<AONContext> CHECK_INVOICE = (ctx,registryAddressId) -> {
			Integer invoiceId = ctx.getDslContext().select(INVOICE.ID)
					.from(INVOICE)
					.where(INVOICE.RADDRESS.eq(registryAddressId))
					.fetch()
					.stream()
					.map (rec -> rec.getValue(INVOICE.ID))
					.findAny()
					.orElse(null);
			if (invoiceId != null) {
				throw new AonCoreException(AonError.DELETE_RADDRESS_INVOICE.getMessage());
			}
		};
		
		public static void validate(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_REGISTRY)
				.andThen(OVERFLOW_NUMBER)
				.andThen(OVERFLOW_ZIP)
				.accept(ctx,registryAddress);
		}
		
		public static void validateDeletion(AONContext ctx, Integer id) {
			CHECK_INVOICE
				.accept(ctx,id);
		}
	}
	
	private static class RegistryAddressAutoComplete {
		
		public static final BiConsumer<AONContext, RegistryAddress> COMPLETE_MAIN_TYPE = (ctx, address) -> {
			if (address.isMain()) {
				RegistryAddressHandler.streamByRegistry( ctx, address.getRegistry())
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
		
		public static void autoComplete(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
			COMPLETE_MAIN_TYPE
				.accept(ctx,registryAddress);
		}
		
	}
/*
	public static RegistryAddress get(AONContext ctx, RegistryAddressFilter filter){
		return select(ctx,filter).limit(1)
				.fetch().stream().map(new RegistryAddressFiller())
				.findFirst().orElse(new RegistryAddress());
	}
	
	public static RegistryAddress getMain(AONContext ctx, Integer registry){
		return RegistryAddressHandler.getStream(ctx, f -> f.getRegistryProperty().eq(registry)
				.and(f.getTypeProperty().eq( MAIN_ADDRESS )))
				.findFirst()
				.orElse(null);
	}

	public static Stream<RegistryAddress> getStreamByRegistry(AONContext ctx, Integer registryId){
		return RegistryAddressHandler.getStream(ctx, f -> f.getRegistryProperty().eq(registryId));
	}
	
	public static Stream<RegistryAddress> getStream(AONContext ctx, RegistryAddressFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryAddressFiller());
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
	
*/
}
