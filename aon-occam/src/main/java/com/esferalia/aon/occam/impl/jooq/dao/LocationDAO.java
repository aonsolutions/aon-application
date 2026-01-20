package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Location.LOCATION;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlReason;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.LocationPropertiesDAO;

public class LocationDAO {

	private LocationDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final LocationPropertiesDAO LOCATION_PROPERTIES = new LocationPropertiesDAO();

	//	 LocationFilter filter
	public static Stream<Location> getStream(AONContext ctx, LocationFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(LOCATION)
			.where(LOCATION_PROPERTIES.getConditions(filter))
			.orderBy(LOCATION.ID.desc())
			.fetch().stream().map(new LocationFiller());
	}
	
	public static Location save(AONContext ctx, Location lc) {
		Location location  = lc.getId() !=0 ? update(ctx, lc) : insert(ctx, lc);
		updateLocationUser(ctx, location);
		return location;
	}
	
	private static Location insert(AONContext ctx, Location lc) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
				.insertInto(LOCATION)
				.set(LOCATION.DOMAIN, lc.getDomain().getId())
				.set(LOCATION.DESCRIPTION, lc.getDescription())
				.set(LOCATION.RADIO, lc.getRadio())
				.set(LOCATION.LATITUDE, lc.getCoordinates().getLatitude())	
				.set(LOCATION.LONGITUDE,lc.getCoordinates().getLongitude())
				.set(LOCATION.TYPE,lc.getType() == null ? null : lc.getType().value())
				.set(LOCATION.REGISTRY,lc.getRegistry())
			.returning(LOCATION.ID).fetchOne().getId();
		ctx.log().debug("INSERT LOCATION id: " + id);		
		return lc.setId(id);
	}
	
	private static Location update(AONContext ctx, Location lc) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(LOCATION)
			.set(LOCATION.DESCRIPTION, lc.getDescription())
			.set(LOCATION.RADIO, lc.getRadio())
			.set(LOCATION.LATITUDE, lc.getCoordinates().getLatitude())
			.set(LOCATION.LONGITUDE, lc.getCoordinates().getLongitude())
			.set(LOCATION.TYPE,lc.getType() == null ? null : lc.getType().value())
			.set(LOCATION.REGISTRY,lc.getRegistry())
			.where(LOCATION.ID.eq(lc.getId()))
			.execute();		
		ctx.log().debug("UPDATE LOCATION id: " + lc.getId());		
		return lc;
	}
	
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(LOCATION).where(LOCATION.ID.eq(id)).execute();	
		ctx.log().debug("DELETE LOCATION id: " + id);		
	}
	
	public static Location get(AONContext ctx, LocationFilter filter) {
		ctx.checkRead();
		
		return getStream(ctx, filter).findFirst().orElse(null);
	}
	
	public static Location getByCoordinates(AONContext ctx, Coordinates coordinates) {
	    ctx.checkRead();
	
	    // Parámetros de entrada
	    Param<Double> lat = DSL.val(coordinates.getLatitude());
	    Param<Double> lng = DSL.val(coordinates.getLongitude());
	
	    /*
	     * Distancia en metros usando Haversine
	     * Radio medio de la Tierra = 6.371.000 m
	     */
	    Field<BigDecimal> distanceExpr = DSL.field(
	        """
	        6371000 * acos(
	            cos(radians({0})) * cos(radians({1})) *
	            cos(radians({2}) - radians({3})) +
	            sin(radians({0})) * sin(radians({1}))
	        )
	        """,
	        BigDecimal.class,
	        lat,                    // {0} lat punto buscado
	        LOCATION.LATITUDE,      // {1} lat tabla
	        LOCATION.LONGITUDE,     // {2} lng tabla
	        lng                     // {3} lng punto buscado
	    );
	
	    // Alias solo para SELECT / ORDER BY
	    Field<BigDecimal> distance = distanceExpr.as("distance");
	
	    return ctx.getDslContext()
	        .select(
	            LOCATION.ID,
	            LOCATION.DOMAIN,
	            LOCATION.RADIO,
	            LOCATION.DESCRIPTION,
	            LOCATION.LATITUDE,
	            LOCATION.LONGITUDE,
	            distance
	        )
	        .from(LOCATION)
	        .where(LOCATION.DOMAIN.eq(ctx.getDomainId()))
	        // Dentro del radio (metros)
	        .and(distanceExpr.le(LOCATION.RADIO.cast(BigDecimal.class)))
	        .orderBy(distance.asc())
	        .limit(1)
	        .fetch()
	        .stream()
	        .map(new LocationFiller())
	        .findFirst()
	        .orElse(new Location());
	}
	
	private static void updateLocationUser(AONContext ctx, Location lc) {
		TimeControlDAO.updateTimeControDetailLocation(ctx, lc);
	}
	
	public static class LocationFiller  implements Function<Record, Location> {
		@Override
		public Location apply(Record record) {
			return new Location()
					.setId(record.getValue(LOCATION.ID))
					.setDomain(new Domain().setId(record.getValue(LOCATION.DOMAIN)))
					.setCoordinates(new Coordinates(record.getValue(LOCATION.LATITUDE), record.getValue(LOCATION.LONGITUDE)))
					.setDescription(record.getValue(LOCATION.DESCRIPTION))
					.setRadio(record.getValue(LOCATION.RADIO))
					.setRegistry(record.getValue(LOCATION.REGISTRY))
					.setType(null == record.getValue(LOCATION.TYPE) ? null : TimeControlReason.safeValueOf(record.getValue(LOCATION.TYPE)))
					;
		}
	}
	
}
