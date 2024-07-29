package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;

public interface ITimeControl {

	public Stream<TimeControl> getTimeControlStream(AONContext ctx, Date startDate, Date endDate);
	public Stream<TimeControl> getTimeControlEmployeeStream(AONContext ctx, Date startDate, Date endDate, TaskHolderFilter filter, Integer page, Integer perPage);
	public Stream<TimeControl> getTaskHolderTimeControlStream(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate, TimeControlGroup group);
	public TimeControl getTaskHolderTimeControl(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate);
	
	public Stream<TimeControlDetail> getTimeControlDetailStream(AONContext ctx, TimeControlFilter filter);
	public Stream<TimeControlDetail> getTimeControlHistoric(AONContext ctx, TimeControlFilter filter);
	public LinkedList<TimeControlDetail> getTimeControlDetailList(AONContext ctx, TimeControlFilter filter);
	public TimeControlDetail saveTimeControlDetail(AONContext ctx, TimeControlDetail tcd);
	public void deleteTimeControlDetail(AONContext ctx, Integer id);
	
	//LOCATION
	public Location saveLocation(AONContext ctx, Location lc);
	public Stream<Location> getLocationStream(AONContext ctx, LocationFilter filter);
	
	public void deleteLocation(AONContext ctx, Integer id);
	
	public Location getLocation(AONContext ctx, LocationFilter filter);
	
	public Location getLocationByCoordinates(AONContext ctx, Coordinates c);
	
}
