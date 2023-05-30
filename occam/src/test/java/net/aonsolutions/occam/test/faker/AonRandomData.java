package net.aonsolutions.occam.test.faker;

import java.util.Optional;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.dao.GeozoneDAO;

public class AonRandomData {
	
	private static final int REQUIRED = -1;
	
	public static Geozone getGeozone(AONContext ctx) {
		return getGeozone(ctx, REQUIRED).get();
	}
	public static Optional<Geozone> getGeozone(AONContext ctx, int nullThreshold){
		return AonRandom.gt(nullThreshold)
			?GeozoneDAO.get(ctx, null, f -> f.orderByRandom() )
			:Optional.empty() ;
	}
	
}
