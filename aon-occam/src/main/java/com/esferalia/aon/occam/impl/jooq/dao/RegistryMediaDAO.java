package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.server.AonValidationUtil;

public class RegistryMediaDAO {
	public static final String MEDIA_TYPE_LABEL = "Tipo de contacto";
	public static final String MEDIA_REGISTRY_LABEL = "Registry";
	public static final String MEDIA_VALUE_LABEL = "Valor";
	public static final String MEDIA_EMAIL_LABEL = "Correo Electr\u00F3nico";
	
	private static final RMediaPropertiesDAO RMEDIA_PROPERTIES = new RMediaPropertiesDAO();
	private static class RMediaPropertiesDAO implements RegistryMediaProperties {
		
		private Condition[] getConditions(RegistryMediaFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.REGISTRY);}
		@Override public Property<Byte> getMediaProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.MEDIA);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.VALUE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.COMMENT);}
		@Override public Property<Byte> getAdministrativeProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.ADMINISTRATIVE);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.COMMERCIAL);}
		@Override public Property<Byte> getTechnicalProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.TECHNICAL);}
		@Override public Property<Integer> getRaddressProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.RADDRESS);}
	}

	public static class RegistryMediaFiller  implements Function<Record,RegistryMedia> {

		@Override
		public RegistryMedia apply(Record rec) {
			return new RegistryMedia()
				.setId(rec.getValue(RMEDIA.ID))
				.setDomain(rec.getValue(RMEDIA.DOMAIN))
				.setComment(rec.getValue(RMEDIA.COMMENT))
				.setMedia(MediaType.safeValueOf(rec.getValue(RMEDIA.MEDIA)))
				.setRegistry(rec.getValue(RMEDIA.REGISTRY))
				.setAdministrative(rec.getValue(RMEDIA.ADMINISTRATIVE) == 1)
				.setCommercial(rec.getValue(RMEDIA.COMMERCIAL) == 1)
				.setTechnical(rec.getValue(RMEDIA.TECHNICAL) == 1)
				.setRaddress(rec.getValue(RMEDIA.RADDRESS))
				.setValue(rec.getValue(RMEDIA.VALUE));
		}
	}
	
	private static class RegistryMediaComplete {
		
		public static BiConsumer<AONContext,RegistryMedia> COMPLETE_MEDIA_TYPE = (ctx,media) -> {
			if (media.getMedia() == null) {
				ctx.log().info("\t saving registry media: autocomplete media: " + MediaType.UNKNOWN);
				media.setMedia(MediaType.UNKNOWN);
			}
		};
		
		public static void autoComplete(AONContext ctx, RegistryMedia media) throws AonCoreException {
			COMPLETE_MEDIA_TYPE
			.accept(ctx, media);
		}
		
	}
	
	private static class RegistryMediaValidation {
		public static BiConsumer<RegistryMedia,AONContext> EMPTY_DOMAIN = (media,ctx) -> {
			if (media.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static BiConsumer<RegistryMedia,AONContext> EMPTY_MEDIA = (media,ctx) -> {
			if (media.getMedia() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(MEDIA_TYPE_LABEL)) ;
		};
		
		public static BiConsumer<RegistryMedia,AONContext> EMPTY_REGISTRY = (media,ctx) -> {
			if (media.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(MEDIA_REGISTRY_LABEL)) ;
		};

		public static BiConsumer<RegistryMedia,AONContext> OVERFLOW_VALUE = (media,ctx) -> {
			if (AonStringUtils.length(media.getValue()) > RMEDIA.VALUE.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( MEDIA_VALUE_LABEL, RMEDIA.VALUE.getDataType().length() ));
		};

		public static BiConsumer<RegistryMedia,AONContext> VALID_EMAIL = (media,ctx) -> {
			if (media.getMedia() == MediaType.EMAIL) {
				if (!AonValidationUtil.isValidEmail( media.getValue() )) {
					throw new AonCoreException(AonError.INVALID_FORMAT.format( MEDIA_EMAIL_LABEL,media.getValue()));
				}
			}
		};

		public static void validate(AONContext ctx, RegistryMedia media) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_REGISTRY)
			.andThen(EMPTY_MEDIA)
			.andThen(OVERFLOW_VALUE)
			.andThen(VALID_EMAIL)
			.accept(media, ctx);
		}
		public static void validateDeletion(AONContext ctx, Integer id) {
			// TODO Auto-generated method stub
		}
	}
	
	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistryMediaFilter filter) {
		return ctx.getDslContext().select()
			.from(RMEDIA)
			.where(RMEDIA_PROPERTIES.getConditions(filter));
	}

	
	public static RegistryMedia get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static Stream<RegistryMedia> getStreamByRegistry(AONContext ctx, Integer registry){
		return getStream(ctx, f -> f.getRegistryProperty().eq(registry));
	}
	
	public static Stream<RegistryMedia> getStream(AONContext ctx, RegistryMediaFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryMediaFiller());
	}
	
	public static RegistryMedia insert(AONContext ctx, RegistryMedia media){
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
			.set(RMEDIA.RADDRESS,media.getRaddress())
			.returning(REGISTRY.ID)
			.fetchOne()
			.getValue(REGISTRY.ID);
		media.setId(id);
		ctx.log().info("INSERT REGISTRY MEDIA id: " + media.getId());
		return media; 
	}
	
	public static RegistryMedia update(AONContext ctx, RegistryMedia media){
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
			.set(RMEDIA.RADDRESS,media.getRaddress())
			.where(RMEDIA.ID.eq(media.getId()))
			.execute();
		ctx.log().info("UPDATE MEDIA id: " + media.getId() + ". (" + count + " rows)");
		return media; 
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RegistryMediaValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RMEDIA)
			.where(RMEDIA.ID.eq(id))
			.execute();
		ctx.log().info("DELETE REGISTRY MEDIA id:" + id + " ("+count+" rows)");
	}
	
	public static void deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		RegistryMediaValidation.validateDeletion(ctx, registry);
		int count = ctx.getDslContext().delete(RMEDIA)
			.where(RMEDIA.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("DELETE REGISTRY MEDIA registry:" + registry + " ("+count+" rows)");
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
	

}
