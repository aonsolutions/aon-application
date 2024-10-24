package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.model.Filter.GeozoneFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Geozone;
import net.aonsolutions.occam.api.model.Properties.GeozoneProperties;
import net.aonsolutions.occam.impl.AONContext;

class GeozoneHandler {

	
	private GeozoneHandler() {
	}
	
	private static final GeozonePropertiesHandler GEOZONE_PROPERTIES = new GeozonePropertiesHandler();
	private static class GeozonePropertiesHandler implements GeozoneProperties {
		private Condition getConditions(GeozoneFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterImpl filterHandler = (FilterImpl) filter.filter(this);
			if (filterHandler == null) return DSL.trueCondition();
			return filterHandler.getCondition();
		}
		
		@Override public Property<Integer> 	getIdProperty() 	{return new FilterImpl.PropertyDAO<>(GEOZONE.ID);}
		@Override public Property<Integer> 	getDomainProperty() {return new FilterImpl.PropertyDAO<>(GEOZONE.DOMAIN);}
		@Override public Property<String> 	getCodeProperty() 	{return new FilterImpl.PropertyDAO<>(GEOZONE.CODE);}
		@Override public Property<String> 	getNameProperty() 	{return new FilterImpl.PropertyDAO<>(GEOZONE.NAME);}
		@Override public Property<Byte> 	getSystemProperty() {return new FilterImpl.PropertyDAO<>(GEOZONE.SYSTEM);}
	}
	
	static class GeozoneFiller extends Filler<Geozone> {
		
		@Override
		public Geozone apply(Record r) {
			return build(r);
		}
		
		public static Geozone build(Record r) {
			return build(r, GEOZONE);
		}
		
		public static Geozone build(Record r, com.esferalia.aon.jooq.tables.Geozone table) {
			if (isNull(r, table.ID)) return null;
			return new Geozone()
				.setId(getValue(r, table.ID))
				.setDomain(getValue(r, table.DOMAIN))
				.setCode(getValue(r, table.CODE))
				.setName(getValue(r, table.NAME))
				.setSystem(getBoolean(r, table.SYSTEM))
			;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext()
			.select()
			.from(GEOZONE)
			.where(GEOZONE.DOMAIN.in(ctx.getInheritanceDomainIds(domain)))
		;
	}
	
	static Geozone insert(AONContext ctx, Geozone geozone) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(GEOZONE)
			.set(GEOZONE.DOMAIN,geozone.getDomain())
			.set(GEOZONE.CODE,geozone.getCode())
			.set(GEOZONE.NAME,geozone.getName())
			.set(GEOZONE.SYSTEM, AonEnumUtils.getByte(geozone.isSystem()))
			.returning(GEOZONE.ID)
			.fetchOne()
			.getValue(GEOZONE.ID);
		return geozone.setId(id);
	}
	
	static void bind(AONContext ctx, Integer domain, Integer parentId, Integer childId) {
		ctx.checkWrite();
		long count = ctx.getDslContext().select()
			.from(GEOTREE)
			.where(GEOTREE.DOMAIN.eq(domain))
			.and(GEOTREE.PARENT.eq(parentId))
			.and(GEOTREE.CHILD.eq(childId))
			.fetch().stream().count();
		if(count <= 0) {
			ctx.getDslContext()
				.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT,parentId)
				.set(GEOTREE.CHILD,childId)
				.execute();
		}
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Geozone getRandom(AONContext ctx, int domain, GeozoneFilter filter) {
	
		return select(ctx,domain)
			.and(GEOZONE_PROPERTIES.getConditions(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new GeozoneFiller())
			.findFirst()
			.orElse(null);
	}
	
	
	/*
	public static GeoZone get(AONContext ctx, Integer id) {
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	public static GeoZone get(AONContext ctx, String code) {
		return get(ctx, p -> p.getCodeProperty().eq(code));
	}

	public static GeoZone getParent(AONContext ctx, Integer id) {
		return selectParent(ctx, id)
			.fetch()
			.stream()
			.map(new GeoZoneFiller()).findFirst().orElse(new GeoZone());
	}
	
	public static GeoZone getChild(AONContext ctx, Integer parent, GeoZoneFilter filter) {
		return selectChild(ctx, parent, filter)
			.fetch()
			.stream()
			.map(new GeoZoneFiller()).findFirst().orElse(new GeoZone());
	}
	
	public static GeoZone get(AONContext ctx, GeoZoneFilter filter) {
		ctx.checkRead();
		return getStream(ctx,filter)
			.findFirst()
			.orElse(null);
	}

	*/
}




