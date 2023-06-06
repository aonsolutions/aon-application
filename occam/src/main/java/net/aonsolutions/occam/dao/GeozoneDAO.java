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
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectSeekStepN;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.filter.AonFacade.AonFillerBuilder;
import net.aonsolutions.occam.api.filter.GeozoneFacade.CompositeGeozoneBuilder;
import net.aonsolutions.occam.api.filter.GeozoneFacade.GeozoneBuilder;
import net.aonsolutions.occam.api.filter.GeozoneFacade.GeozoneBuilderFactory;
import net.aonsolutions.occam.api.filter.GeozoneFacade.GeozoneFilter;
import net.aonsolutions.occam.api.filter.GeozoneFacade.GeozoneFilters;
import net.aonsolutions.watson.client.util.AonStringUtils;

public class GeozoneDAO {

	
	private GeozoneDAO() {

	}
	
	private static final GeoZoneFilterDAO GEOZONE_FILTER = new GeoZoneFilterDAO();
	private static class GeoZoneFilterDAO implements GeozoneFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(GEOZONE.ID);}
		@Override public Property<Integer> withDomain() {return new PropertyDAO<>(GEOZONE.DOMAIN);}
		@Override public Property<String> withCode() {return new PropertyDAO<>(GEOZONE.CODE);}
		@Override public Property<String> withName() {return new PropertyDAO<>(GEOZONE.NAME);}
		@Override public Property<Byte> withSystem() {return new PropertyDAO<>(GEOZONE.SYSTEM);}
	}
	
	private static class GeoZoneSelectBuilderDAO extends  CompositeGeozoneBuilder<Stream<Geozone>> {
		
		private final ResultQuery<Record> query;
		private final FillerBuilder fillerBuilder; 
		
		public GeoZoneSelectBuilderDAO( AONContext ctx, GeozoneFilter filter ) {
			
			SelectBuilder selectBuilder = new SelectBuilder( ctx );
			FromBuilder fromBuilder = new  FromBuilder( selectBuilder.build() );
			SelectJoinStep<Record> from = fromBuilder.build();
			WhereBuilder whereBuilder = new WhereBuilder(from,filter);
			OrderByBuilder orderByBuilder = new OrderByBuilder( whereBuilder.build() );
			LimitBuilder limitBuilder = new  LimitBuilder( orderByBuilder.build() );
			fillerBuilder = new  FillerBuilder();
			query =  limitBuilder.build();
		
			addBuilder(selectBuilder);
			addBuilder(fromBuilder);
			addBuilder(whereBuilder);
			addBuilder(orderByBuilder);
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
	
	private static class SelectBuilder implements GeozoneBuilder<SelectSelectStep<Record>> {
		
		private SelectSelectStep<Record> select;

		public SelectBuilder(AONContext ctx) {
			this.select = ctx.getDslContext()
					.select( GEOZONE.fields() );
		}

		@Override
		public SelectSelectStep<Record> build() {
			return select;
		}
		@Override public SelectBuilder orderByCode() {return this;}
		@Override public SelectBuilder orderByName() {return this;}
		@Override public SelectBuilder orderByRandom() {return this;}
		@Override public SelectBuilder limit(int offest, int rows) {return this;}
	}

	private static class FromBuilder implements GeozoneBuilder<SelectJoinStep<Record>> {
		private SelectJoinStep<Record> from;
		
		public FromBuilder(SelectSelectStep<Record> select ) {
			from = select.from(GEOZONE);
		}
		
		@Override
		public SelectJoinStep<Record> build() {
			return from;
		}
		
		@Override public FromBuilder orderByCode() {return this;}
		@Override public FromBuilder orderByName() {return this;}
		@Override public FromBuilder orderByRandom() {return this;}
		@Override public FromBuilder limit(int offest, int rows) { return this; }
	}

	private static class WhereBuilder implements GeozoneBuilder<SelectConditionStep<Record>> {
		private SelectConditionStep<Record> where;
		
		public WhereBuilder(SelectJoinStep<Record> from, GeozoneFilter filter) {
			where = from.where( getWhere(filter) );
		}
		
		@Override
		public SelectConditionStep<Record> build() {
			return where;
		}
		
		private Condition getWhere(GeozoneFilter filter) {
			if ( filter.filter(GEOZONE_FILTER) instanceof FilterDAO filterDAO) {
				return filterDAO.getCondition();
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}
		
		@Override public WhereBuilder orderByCode() {return this;}
		@Override public WhereBuilder orderByName() {return this;}
		@Override public WhereBuilder orderByRandom() {return this;}
		@Override public WhereBuilder limit(int offset, int rows) {return this;}
	}

	private static class OrderByBuilder implements GeozoneBuilder<SelectLimitStep<Record>> {
		private SelectConditionStep<Record> where;
		private SelectSeekStepN<Record> orderBy;
		
		public OrderByBuilder(SelectConditionStep<Record> where) {
			this.where = where;
		}

		@Override
		public SelectLimitStep<Record> build() {
			return orderBy == null ? where : orderBy; 
		}
		
		@Override 
		public OrderByBuilder orderByCode() {
			orderBy = where.orderBy( Stream.of(GEOZONE.CODE).toList() );
			return this;
		}
		
		@Override public OrderByBuilder orderByName() {
			orderBy = where.orderBy( Stream.of(GEOZONE.NAME).toList() );
			return this;
		}
		
		@Override public OrderByBuilder orderByRandom() {
			orderBy = where.orderBy( Stream.of(DSL.rand()).toList() );
			return this;
		}
		
		@Override public OrderByBuilder limit(int offest, int rows) { return this; }
	}

	private static class LimitBuilder implements GeozoneBuilder<ResultQuery<Record>> {
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
		
		@Override public LimitBuilder orderByCode() {return this;}
		@Override public LimitBuilder orderByName() {return this;}
		@Override public LimitBuilder orderByRandom() {return this;}
	}

	private static class FillerBuilder implements GeozoneBuilder<Function<Record, RecordMapper<Geozone>>>,AonFillerBuilder<Geozone> {
		
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
				.markAsClean()
			;
		}
		
		@Override public FillerBuilder orderByCode() {return this;}
		@Override public FillerBuilder orderByName() {return this;}
		@Override public FillerBuilder orderByRandom() {return this;}
		@Override public FillerBuilder limit(int offset, int rows) { return null; }

	}

	private static GeoZoneSelectBuilderDAO getBuilder( AONContext ctx, GeozoneFilter filter ) {
		return new GeoZoneSelectBuilderDAO(ctx,filter);
	}

	public static Optional<Geozone> get(AONContext ctx, GeozoneFilter filter, GeozoneBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Geozone> getStream(AONContext ctx, GeozoneFilter filter, GeozoneBuilderFactory factory){
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




