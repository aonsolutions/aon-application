package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.impl.jooq.PMSImpl;

public class PMS {

	private static IPMS getPMS() {
		return new PMSImpl();
	}

	public static LinkedList<HotelGuestByCountry> getHotelGuestByCountry(String domainName, Integer domainId, String login,
			Integer hotelId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHotelGuestByCountry(ctx, hotelId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
