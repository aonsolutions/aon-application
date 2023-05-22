package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.filter.AonFacade.AonFillerBuilder;
import net.aonsolutions.occam.api.filter.GeoZoneFacade.CompositeGeoZoneBuilder;
import net.aonsolutions.occam.api.filter.GeoZoneFacade.GeoZoneBuilder;
import net.aonsolutions.occam.api.filter.GeoZoneFacade.GeoZoneBuilderFactory;
import net.aonsolutions.occam.api.filter.GeoZoneFacade.GeoZoneFilter;
import net.aonsolutions.occam.api.filter.GeoZoneFacade.GeoZoneFilters;
import net.aonsolutions.watson.client.util.AonStringUtils;

public class GeoZoneDAO {

	
	private GeoZoneDAO() {

	}
	
	private static final GeoZoneFilterDAO GEOZONE_FILTER = new GeoZoneFilterDAO();
	private static class GeoZoneFilterDAO implements GeoZoneFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(GEOZONE.ID);}
		@Override public Property<Integer> withDomain() {return new PropertyDAO<>(GEOZONE.DOMAIN);}
		@Override public Property<String> withCode() {return new PropertyDAO<>(GEOZONE.CODE);}
		@Override public Property<String> withName() {return new PropertyDAO<>(GEOZONE.NAME);}
		@Override public Property<Byte> withSystem() {return new PropertyDAO<>(GEOZONE.SYSTEM);}
	}
	
	private static class GeoZoneSelectBuilderDAO extends  CompositeGeoZoneBuilder<Stream<Geozone>> {
		
		private final ResultQuery<Record> query;
		private final FillerBuilder fillerBuilder; 
		
		public GeoZoneSelectBuilderDAO( AONContext ctx, GeoZoneFilter filter ) {
			
			SelectBuilder selectBuilder = new SelectBuilder( ctx );
			FromBuilder fromBuilder = new  FromBuilder( selectBuilder.build() );
			SelectJoinStep<Record> from = fromBuilder.build();
			WhereBuilder whereBuilder = new WhereBuilder(from,filter);
			LimitBuilder limitBuilder = new  LimitBuilder( whereBuilder.build() );
			fillerBuilder = new  FillerBuilder();
			query =  limitBuilder.build();
		
			addBuilder(selectBuilder);
			addBuilder(fromBuilder);
			addBuilder(whereBuilder);
			addBuilder(limitBuilder);
			addBuilder(fillerBuilder);
			 
		}
		
		@Override
		public Stream<Geozone> build() {
			return this.query
				.fetch()
				.stream()
				.map(fillerBuilder::build );
		}
		
	}
	
	private static class SelectBuilder implements GeoZoneBuilder<SelectSelectStep<Record>> {
		
		private SelectSelectStep<Record> select;

		public SelectBuilder(AONContext ctx) {
			this.select = ctx.getDslContext()
					.select( GEOZONE.fields() );
		}

		@Override
		public SelectSelectStep<Record> build() {
			return select;
		}
		
		@Override public SelectBuilder limit(int offest, int rows) {return this;}
	}

	private static class FromBuilder implements GeoZoneBuilder<SelectJoinStep<Record>> {
		private SelectJoinStep<Record> from;
		
		public FromBuilder(SelectSelectStep<Record> select ) {
			from = select.from(GEOZONE);
		}
		
		@Override
		public SelectJoinStep<Record> build() {
			return from;
		}
		
		@Override public FromBuilder limit(int offest, int rows) { return this; }
	}

	private static class WhereBuilder implements GeoZoneBuilder<SelectLimitStep<Record>> {
		private SelectLimitStep<Record> where;
		
		public WhereBuilder(SelectJoinStep<Record> from, GeoZoneFilter filter) {
			where = from.where( getWhere(filter) );
		}
		
		@Override
		public SelectLimitStep<Record> build() {
			return where;
		}
		
		private Condition getWhere(GeoZoneFilter filter) {
			if ( filter.filter(GEOZONE_FILTER) instanceof FilterDAO filterDAO) {
				return filterDAO.getCondition();
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}
		
		@Override public WhereBuilder limit(int offset, int rows) {return this;}
	}

	private static class LimitBuilder implements GeoZoneBuilder<ResultQuery<Record>> {
		private SelectLimitStep<Record> where;
		private SelectWithTiesAfterOffsetStep<Record> limit;
		
		public LimitBuilder(SelectLimitStep<Record> where) {
			this.where = where;
		}
		
		@Override 
		public LimitBuilder limit(int offset, int rows) {
			limit = where.limit(offset,rows);
			return this;
		}
		
		@Override
		public ResultQuery<Record> build() {
			return limit==null?where:limit;
		}
	}

	private static class FillerBuilder implements GeoZoneBuilder<Function<Record, RecordMapper<Geozone>>>,AonFillerBuilder<Geozone> {
		
		private UnaryOperator<RecordMapper<Geozone>> withAudit = t -> t;
		private UnaryOperator<RecordMapper<Geozone>> withParent = t -> t;
		private UnaryOperator<RecordMapper<Geozone>> withBooking = t -> t;
		
		@Override
		public Function<Record, RecordMapper<Geozone>> build() {
			return null;
		}
		
		@Override
		public Geozone build(Record rec) {
			
			Function<Record, RecordMapper<Geozone>> f = a -> new RecordMapper<Geozone>(rec, Geozone::new );
			return f.andThen( mapper -> {
					mapper.get()
					.setId(FillerUtils.getValue(mapper.getRecord(), GEOZONE.ID))
					.setDomain(FillerUtils.getValue(mapper.getRecord(), GEOZONE.DOMAIN))
					.setCode(FillerUtils.getValue(mapper.getRecord(), GEOZONE.CODE))
					.setName(FillerUtils.getValue(mapper.getRecord(), GEOZONE.NAME))
					.setSystem(FillerUtils.getBoolean(mapper.getRecord(), GEOZONE.SYSTEM));
					return mapper;
				})
				.andThen( withBooking )
				.andThen( withAudit )
				.andThen( withParent )
				.apply(rec)
				.get()
				.setDirty(false)
			;
		}
		
		@Override public GeoZoneBuilder<Function<Record, RecordMapper<Geozone>>> limit(int offset, int rows) { return null; }

	}

	private static GeoZoneSelectBuilderDAO getBuilder( AONContext ctx, GeoZoneFilter filter ) {
		return new GeoZoneSelectBuilderDAO(ctx,filter);
	}

	public static Optional<Geozone> get(AONContext ctx, GeoZoneFilter filter, GeoZoneBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Geozone> getStream(AONContext ctx, GeoZoneFilter filter, GeoZoneBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
	
	public static Geozone save(AONContext ctx, Geozone geozone) {
		ctx.checkWrite();
		if (geozone == null) throw new AonCoreException(AonError.SAVE_EMPTY.getMessage());
		if (geozone.isDirty()) { 
			GeoZoneValidation.validate(ctx, geozone);
//			return (geozone.getId() == null)
//				?insert(ctx,geozone)
//				:update(ctx,geozone);
		} else {
			ctx.log().debug(AonError.NOT_DIRTY.format("GeoZone",geozone.getId()));
		}
		return geozone;  
	}
	
//	private static GeoZone insert(AONContext ctx, GeoZone geozone) {
//		Integer id = ctx.getDslContext()
//			.insertInto(GEOZONE)
//			.set(GEOZONE.DOMAIN,geozone.getDomain())
//			.set(GEOZONE.CODE,geozone.getCode())
//			.set(GEOZONE.NAME,geozone.getName())
//			.set(GEOZONE.SYSTEM, AonEnumUtils.getByte(geozone.isSystem()))
//			.returning(GEOZONE.ID)
//			.fetchOne()
//			.getValue(GEOZONE.ID);
//		geozone.setId(id);
//		ctx.log().info("INSERT GEOZONE id: {0} - {1}", geozone.getId(), geozone.getName());	
//		return geozone;
//	}
//	
//	private static GeoZone update(AONContext ctx, GeoZone geozone) {
//		throw new UnsupportedOperationException("Not implemented!");
//	}

//	public static void bind(AONContext ctx, Integer domain, Integer parentId, Integer childId) {
//		ctx.checkWrite();
//		ctx.getDslContext()
//			.insertInto(GEOTREE)
//			.set(GEOTREE.DOMAIN, domain)
//			.set(GEOTREE.PARENT,parentId)
//			.set(GEOTREE.CHILD,childId)
//			.execute();
//	}

	private static class GeoZoneValidation {
		
		public static final BiConsumer<AONContext,Geozone> VALIDATE_DOMAIN = (ctx,geozone) -> {
			if (geozone.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};

		public static final BiConsumer<AONContext,Geozone> VALIDATE_NAME = (ctx,geozone) -> {
			if (AonStringUtils.isBlank(geozone.getName())) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
			if (AonStringUtils.length(geozone.getName()) > GEOZONE.NAME.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre", GEOZONE.NAME.getDataType().length() ));
		};
		
		public static final BiConsumer<AONContext,Geozone> VALIDATE_CODE = (ctx,geozone) -> {
			if (AonStringUtils.isBlank(geozone.getCode())) 
				throw new AonCoreException(AonError.EMPTY_CODE.getMessage());
			if (AonStringUtils.length(geozone.getCode()) > GEOZONE.CODE.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "C\u00F3digo", GEOZONE.CODE.getDataType().length() ));
		};
		
		public static void validate(AONContext ctx, Geozone geozone) throws AonCoreException{
			VALIDATE_DOMAIN
			.andThen(VALIDATE_NAME)
			.andThen(VALIDATE_CODE)
			.accept(ctx, geozone);
		}
		
	}
}




