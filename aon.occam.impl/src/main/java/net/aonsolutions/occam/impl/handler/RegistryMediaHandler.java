package net.aonsolutions.occam.impl.handler;


import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonValidationUtil;
import com.esferalia.aon.watson.util.AonEnumUtils;

import net.aonsolutions.occam.api.model.RegistryMedia;
import net.aonsolutions.occam.api.model.type.MediaType;
import net.aonsolutions.occam.impl.AONContext;

class RegistryMediaHandler extends AbsHandler {
	
	private RegistryMediaHandler() {
		
	}
	
	private static SelectJoinStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
			.from(RMEDIA);
	}
	
	static Stream<RegistryMedia> streamByRegistry(AONContext ctx, Integer registryId ){
		return select(ctx)
			.where( RMEDIA.REGISTRY.eq( registryId ))
			.fetch()
			.stream()
			.map(new RegistryMediaFiller());
		
	}
	
	
	static class RegistryMediaFiller extends Filler<RegistryMedia> {

		@Override
		public RegistryMedia apply(Record rec) {
			return new RegistryMedia()
				.setId(rec.getValue(RMEDIA.ID))
				.setDomain(rec.getValue(RMEDIA.DOMAIN))
				.setComment(rec.getValue(RMEDIA.COMMENT))
				.setMedia(MediaType.value(rec.getValue(RMEDIA.MEDIA)).orElse(null))
				.setRegistry(rec.getValue(RMEDIA.REGISTRY))
				.setAdministrative(rec.getValue(RMEDIA.ADMINISTRATIVE) != null && rec.getValue(RMEDIA.ADMINISTRATIVE) == 1)
				.setCommercial(rec.getValue(RMEDIA.COMMERCIAL) != null && rec.getValue(RMEDIA.COMMERCIAL) == 1)
				.setTechnical(rec.getValue(RMEDIA.TECHNICAL) != null && rec.getValue(RMEDIA.TECHNICAL) == 1)
				.setValue(rec.getValue(RMEDIA.VALUE))
			;
		}
	}
	
	static RegistryMedia save(AONContext ctx, RegistryMedia media){
		ctx.checkWrite();
		if(media.getId() != null && media.isDeleted()) {
			delete(ctx, media.getId());
			return media;
		}
		return (media.getId() == null)
				?insert(ctx, media)
				:update(ctx, media);
	}
	
	static RegistryMedia insert(AONContext ctx, RegistryMedia media){
		ctx.checkWrite();
		RegistryMediaComplete.autoComplete(ctx, media);
		RegistryMediaValidation.validate(ctx, media);
		Integer id = ctx.getDslContext()
			.insertInto(RMEDIA)
			.set(RMEDIA.DOMAIN, media.getDomain())
			.set(RMEDIA.REGISTRY,media.getRegistry())
			.set(RMEDIA.MEDIA, media.getMedia().value())
			.set(RMEDIA.VALUE, media.getValue())
			.set(RMEDIA.COMMENT, media.getComment())
			.set(RMEDIA.ADMINISTRATIVE, AonEnumUtils.getByte( media.isAdministrative()))
			.set(RMEDIA.COMMERCIAL, AonEnumUtils.getByte( media.isCommercial()))
			.set(RMEDIA.TECHNICAL, AonEnumUtils.getByte( media.isTechnical()))
			.returning(RMEDIA.ID)
			.fetchOne()
			.getValue(RMEDIA.ID);
		media.setId(id);
		ctx.log().debug("INSERT REGISTRY MEDIA ( registry: {0}) id: {1}",media.getRegistry(),media.getId());
		return media; 
	}
	
	static RegistryMedia update(AONContext ctx, RegistryMedia media){
		ctx.checkWrite();
		RegistryMediaComplete.autoComplete(ctx, media);
		RegistryMediaValidation.validate(ctx, media);
		int count = ctx.getDslContext().update(RMEDIA)
			.set(RMEDIA.DOMAIN, media.getDomain())
			.set(RMEDIA.REGISTRY,media.getRegistry())
			.set(RMEDIA.MEDIA, media.getMedia().value())
			.set(RMEDIA.VALUE, media.getValue())
			.set(RMEDIA.COMMENT, media.getComment())
			.set(RMEDIA.ADMINISTRATIVE, AonEnumUtils.getByte( media.isAdministrative()))
			.set(RMEDIA.COMMERCIAL, AonEnumUtils.getByte( media.isCommercial()))
			.set(RMEDIA.TECHNICAL, AonEnumUtils.getByte( media.isTechnical()))
			.where(RMEDIA.ID.eq(media.getId()))
			.execute();
		ctx.log().debug("UPDATE MEDIA ( registry: {0}) id: {1}. ({2} rows)",media.getRegistry(),media.getId(),count);
		return media; 
	}
	
	static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RMEDIA)
			.where(RMEDIA.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY MEDIA id: {0} ({1} rows)",id,count);
	}
	
	
	private static class RegistryMediaComplete {
		
		static final BiConsumer<AONContext,RegistryMedia> COMPLETE_MEDIA_TYPE = (ctx,media) -> {
			if (media.getMedia() == null) {
				ctx.log().debug("\t saving registry media: autocomplete media: {0}",MediaType.UNKNOWN);
				media.setMedia(MediaType.UNKNOWN);
			}
		};
		
		static void autoComplete(AONContext ctx, RegistryMedia media) throws AonCoreException {
			COMPLETE_MEDIA_TYPE
			.accept(ctx, media);
		}
		
	}

	private static class RegistryMediaValidation {

		static final BiConsumer<RegistryMedia,AONContext> EMPTY_DOMAIN = (media,ctx) -> {
			if (media.getDomain() == null) throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		static final BiConsumer<RegistryMedia,AONContext> EMPTY_MEDIA = (media,ctx) -> {
			if (media.getMedia() == null)  throw new AonCoreException(AonError.EMPTY_DATA.format("Tipo de contacto")) ;
		};
		
		static final BiConsumer<RegistryMedia,AONContext> EMPTY_REGISTRY = (media,ctx) -> {
			if (media.getRegistry() == null) throw new AonCoreException(AonError.EMPTY_DATA.format("Registry"));
		};

		static final BiConsumer<RegistryMedia,AONContext> OVERFLOW_VALUE = (media,ctx) -> {
			if (overflows(RMEDIA.VALUE , media.getValue()))
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Valor", RMEDIA.VALUE.getDataType().length() ));
		};

		static final BiConsumer<RegistryMedia,AONContext> VALID_EMAIL = (media,ctx) -> {
			if (media.getMedia() == MediaType.EMAIL) {
				if (!AonValidationUtil.isValidEmail( media.getValue() )) {
					throw new AonCoreException(AonError.INVALID_FORMAT.format( "Correo Electr\u00F3nico",media.getValue()));
				}
			}
		};
		
		static void validate(AONContext ctx, RegistryMedia media) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_REGISTRY)
			.andThen(EMPTY_MEDIA)
			.andThen(OVERFLOW_VALUE)
			.andThen(VALID_EMAIL)
			.accept(media, ctx);
		}
	}

	// **********************************************************************
	// **********************************************************************
	
/*
	
	public static RegistryMedia get(AONContext ctx, RegistryMediaFilter filter){
		return select(ctx,filter)
			.limit(1)
			.fetch()
			.stream()
			.map(new RegistryMediaFiller())
			.findFirst()
			.orElse(new RegistryMedia());
	}
	
	public static RegistryMedia get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		RegistryMediaValidation.validateDeletion(ctx, registry);
		int count = ctx.getDslContext().delete(RMEDIA)
			.where(RMEDIA.REGISTRY.eq(registry))
			.execute();
		ctx.log().debug("DELETE REGISTRY MEDIA registry: {0} ({1} rows)",registry,count);
		return count;
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static RegistryMedia getRandom(AONContext ctx, RegistryMediaFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryMediaFiller())
			.findFirst()
			.orElse(null);
	}
	
*/
}
