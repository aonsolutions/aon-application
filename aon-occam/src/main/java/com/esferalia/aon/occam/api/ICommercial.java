package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;

public interface ICommercial {
	public CommercialTracking getCommercialTracking(AONContext ctx, CommercialTrackingFilter filter);
	public CommercialTracking save(AONContext ctx, CommercialTracking commercialTracking);
	public Stream<CommercialTracking> getCommercialTrackingStream(AONContext ctx, CommercialTrackingFilter filter);

	
	public void updateEventId(AONContext ctx, Integer ctId, String eventId);
	
	public CommercialActivity getCommercialActivity(AONContext ctx, CommercialActivityFilter filter);
	public LinkedList<CommercialActivity> getCommercialActivityList(AONContext ctx, CommercialActivityFilter filter);
	public CommercialActivity save(AONContext ctx, CommercialActivity commercialActivity);

}
