package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.security.Booking;

public class BookingDAO {

	private BookingDAO() {
	
	}
	
	public static Booking save(AONContext ctx, Booking booking) {
		if(booking.getNumberOfUsers() != null) {
			SecurityDAO.saveDomainMaxDefinedUser(ctx, booking.getNumberOfUsers());
		}
		
		AonApp.getValues().stream().forEach(app -> {
			DomainApp domainApp = SecurityDAO.getDomainAppStream(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getAppProperty().eq(app.value())))
				.findFirst().orElse(new DomainApp());
	
			if(booking.getApps().contains(app)) {
				domainApp.setDomain(ctx.getDomainId())
					.setApp(app)
					.setActive(true);
			} else if(!domainApp.isEmpty()) {
				domainApp.setActive(false);
			}
			
			if(!domainApp.isEmpty())
				SecurityDAO.saveDomainApp(ctx, domainApp);
		});
		return booking;
	}

}
