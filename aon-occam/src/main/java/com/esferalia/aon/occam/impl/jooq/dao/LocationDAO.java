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
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.LocationPropertiesDAO;


public class LocationDAO {

	private static final LocationPropertiesDAO LOCATION_PROPERTIES = new LocationPropertiesDAO();
//	 LocationFilter filter
	public static Stream<Location> getLocationStream(AONContext ctx, LocationFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(LOCATION)
			.where(LOCATION_PROPERTIES.getConditions(filter))
			.orderBy(LOCATION.ID.desc())
			.fetch().stream().map(new LocationFiller());
	}
	
	public static Location saveLocation(AONContext ctx, Location lc) {
		Location location  = lc.getId() !=0 ? update(ctx, lc) : insert(ctx, lc);
		updateLocationUser(ctx, location);
		return location;
	}
	private static Location insert(AONContext ctx, Location lc) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(LOCATION, LOCATION.DOMAIN, LOCATION.DESCRIPTION, LOCATION.RADIO, LOCATION.LATITUDE, LOCATION.LONGITUDE)
			.values(lc.getDomain().getId(), lc.getDescription(), lc.getRadio(), lc.getCoordinates().getLatitude(), lc.getCoordinates().getLongitude())
			.returning(LOCATION.ID).fetchOne().getValue(LOCATION.ID);
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
			.where(LOCATION.ID.eq(lc.getId()))
			.execute();		
		return lc;
	}
	
	
	public static void deleteLocation(AONContext ctx, Location lc) {
		ctx.checkWrite();
		ctx.getDslContext().delete(LOCATION).where(LOCATION.ID.eq(lc.getId())).execute();	
	}
	
	public static Location getLocation(AONContext ctx, LocationFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select()
				.from(LOCATION)
				.where(LOCATION_PROPERTIES.getConditions(filter))
				.stream()
				.map( new LocationFiller() )
				.findFirst()
				.orElse(null);
	}
	
	public static Location getLocation(AONContext ctx, Coordinates coordinates) {
		ctx.checkRead();
		Param<Double> lt = DSL.val(coordinates.getLatitude());
		Param<Double> lg = DSL.val(coordinates.getLongitude());
		Field<BigDecimal> pi = DSL.pi();
		
		Field<BigDecimal> f1 = DSL.sin(lt.mul(pi).div(180))
				.mul(DSL.sin(LOCATION.LATITUDE.mul(pi).div(180)));
		
		Field<BigDecimal> f2 = DSL.cos(lt.mul(pi).div(180))
				.mul(DSL.cos(LOCATION.LATITUDE.mul(pi).div(180)))
				.mul(DSL.cos(lg.sub(LOCATION.LONGITUDE).mul(pi.div(180))));
		
		Field<BigDecimal> f3 = DSL.acos( f1.add(f2)).mul(DSL.val(180).div(pi));

		Field<BigDecimal> distance = f3.mul(DSL.val(60).mul(1.1515).mul(1609.344)).as("distance");
		
		return ctx.getDslContext().select(
				LOCATION.ID, LOCATION.DOMAIN, LOCATION.RADIO, LOCATION.DESCRIPTION, 
				LOCATION.LATITUDE, LOCATION.LONGITUDE, distance)
		.from(LOCATION)
		.where(LOCATION.DOMAIN.eq(ctx.getDomainId()))
		.having(distance.le(LOCATION.RADIO.cast(BigDecimal.class)))
		.orderBy(distance.asc()).limit(1).fetch().stream().map( new LocationFiller() )
		.findFirst().orElse(new Location());
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
					.setRadio(record.getValue(LOCATION.RADIO));
		}
	}
	
}
