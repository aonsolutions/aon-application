package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Location.LOCATION;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jooq.Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.FullInvoiceFiller;
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
		return lc.getId()!=0
			? update(ctx, lc)
			: insert(ctx, lc);
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
	
	public static Location getLocation(AONContext ctx, Integer id) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select()
				.from(LOCATION)
				.where(LOCATION.ID.eq(id))
				.stream()
				.map( new LocationFiller() )
				.findFirst()
				.orElse(null);
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
