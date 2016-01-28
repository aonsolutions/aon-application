package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPMS;
import com.esferalia.aon.occam.impl.jooq.dao.PMSDAO;

public class PMSImpl implements IPMS {
	
	@Override
	public void deleteReservationCreditCard(AONContext ctx, Integer reservationId) {
		ctx.getDslContext().transaction(configuration -> 
			PMSDAO.deleteReservationCreditCard(ctx, reservationId));
	}
}
