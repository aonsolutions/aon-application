package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITimeControl;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.impl.jooq.dao.LocationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TimeControlDAO;

public class TimeControlImpl implements ITimeControl {
	
	@Override
	public Stream<TimeControl> getTimeControlStream(AONContext ctx, Date startDate, Date endDate) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlStream(ctx, startDate, endDate));
	}
	
	@Override
	public Stream<TimeControl> getTimeControlEmployeeStream(AONContext ctx, Date startDate, Date endDate, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlEmployeeStream(ctx, startDate, endDate, page, perPage));
	}

	@Override
	public Stream<TimeControl> getTaskHolderTimeControlStream(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate, TimeControlGroup group) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTaskHolderTimeControlStream(ctx, taskHolderId, startDate, endDate, group));
	}
	
	@Override
	public TimeControl getTaskHolderTimeControl(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTaskHolderTimeControl(ctx, taskHolderId, startDate, endDate));
	}
	
	@Override
	public Stream<TimeControlDetail> getTimeControlDetailStream(AONContext ctx, TimeControlFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlDetailStream(ctx, filter));
	}
	
	@Override
	public Stream<TimeControlDetail> getTimeControlHistoric(AONContext ctx, TimeControlFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlHistoric(ctx, filter));
	}

	@Override
	public LinkedList<TimeControlDetail> getTimeControlDetailList(AONContext ctx, TimeControlFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlDetailList(ctx, filter));
	}
	
	@Override
	public TimeControlDetail saveTimeControlDetail(AONContext ctx, TimeControlDetail tcd) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.save(ctx, tcd));
	}
	
	@Override
	public void deleteTimeControlDetail(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> TimeControlDAO.delete(ctx, id)
		);
	}
	
	@Override
	public Location saveLocation(AONContext ctx, Location lc) {
		return  ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.save(ctx, lc));
	}
	
	
	@Override
	public Location getLocation(AONContext ctx, LocationFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.get(ctx, filter));
	}


	@Override
	public Stream<Location> getLocationStream(AONContext ctx, LocationFilter filter) {
		return  ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.getStream(ctx, filter));
	}

	@Override
	public void deleteLocation(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> LocationDAO.delete(ctx, id)
		);
	}
	
	@Override
	public Location getLocationByCoordinates(AONContext ctx, Coordinates c) {
		return ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.getByCoordinates(ctx, c));
	}
	
}
