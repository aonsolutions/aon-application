package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommercial;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.impl.jooq.dao.CommercialDAO;

public class CommercialImpl implements ICommercial {

	
	@Override
	public CommercialTracking getCommercialTracking(AONContext ctx, CommercialTrackingFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CommercialDAO.getCommercialTracking(ctx, filter));
	}
	
	@Override
	public Stream<CommercialTracking> getCommercialTrackingStream(AONContext ctx, CommercialTrackingFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CommercialDAO.getCommercialTrackingStream(ctx, filter));
	}
	
	@Override
	public void updateEventId(AONContext ctx, Integer ctId, String eventId) {
		ctx.getDslContext().transaction(
				configuration -> CommercialDAO.updateEventId(ctx, ctId, eventId));
	}
	
	
	@Override
	public CommercialActivity getCommercialActivity(AONContext ctx, CommercialActivityFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CommercialDAO.getCommercialActivity(ctx, filter));
	}
	
	@Override
	public LinkedList<CommercialActivity> getCommercialActivityList(AONContext ctx, CommercialActivityFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CommercialDAO.getCommercialActivityList(ctx, filter));
	}

	@Override
	public Seller getSeller(AONContext ctx, Integer sellerId){
		return ctx.getDslContext().transactionResult(
				congiguration -> CommercialDAO.getSeller(ctx, sellerId));
	}

}
