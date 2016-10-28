package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.GeoZoneFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Properties.GeoZoneProperties;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class GeoZoneDAO {
	private static final GeoZonePropertiesDAO GEOZONE_PROPERTIES = new GeoZonePropertiesDAO();
	private static class GeoZonePropertiesDAO implements GeoZoneProperties {
		private Condition[] getConditions(GeoZoneFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(GEOZONE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(GEOZONE.DOMAIN);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(GEOZONE.CODE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(GEOZONE.NAME);}
		@Override public Property<Byte> getSystemProperty() {return new FilterDAO.PropertyDAO<Byte>(GEOZONE.SYSTEM);}
	}
	
	private static class FullGeoZoneFiller  implements Function<Record,GeoZone> {
		@Override
		public GeoZone apply(Record record) {
			return new GeoZone()
			.setId(record.getValue(GEOZONE.ID))
			.setDomain(record.getValue(GEOZONE.DOMAIN))
			.setCode(record.getValue(GEOZONE.CODE))
			.setName(record.getValue(GEOZONE.NAME))
			.setSystem(AonEnumUtils.getBoolean(record.getValue(GEOZONE.SYSTEM)))
			;
		}
	}

	private static Stream<GeozoneRecord> getGeoZoneStream(AONContext ctx, GeoZoneFilter filter) {
		return ctx.getDslContext()
				.selectFrom(GEOZONE)
				.where(GEOZONE_PROPERTIES.getConditions(filter))
				.and(GEOZONE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(GEOZONE.CODE)
				.fetch()
				.stream();
	}
	public static Stream<GeoZone> getGeoZones(AONContext ctx, GeoZoneFilter filter) {
		ctx.checkRead();
		return getGeoZoneStream(ctx, filter)
			.map(new FullGeoZoneFiller());			
	}
	public static GeoZone get(AONContext ctx, Integer id) {
		Condition condition = GEOZONE.ID.equal(id);
		return get(ctx, condition);
	}
	public static GeoZone get(AONContext ctx, String code) {
		Condition condition = GEOZONE.CODE.equal(code);
		return get(ctx, condition);
	}
	public static GeoZone get(AONContext ctx, Condition condition) {
		ctx.checkRead();
		return ctx.getDslContext() 
			.selectFrom(GEOZONE)
			.where(condition)
			.and(GEOZONE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.fetch()
			.stream()
			.map(new FullGeoZoneFiller())
			.findFirst()
			.orElse(null);
	}

	public static GeoZone insert(AONContext ctx, GeoZone geozone) {
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
		return get(ctx, id);
	}

}




