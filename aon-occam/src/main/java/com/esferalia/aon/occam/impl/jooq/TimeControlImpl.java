package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITimeControl;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
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
	public LinkedList<TimeControlDetail> getTimeControlDetailList(AONContext ctx, TimeControlFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.getTimeControlDetailList(ctx, filter));
	}
	
	@Override
	public TimeControlDetail saveTimeControlDetail(AONContext ctx, TimeControlDetail tcd) {
		return ctx.getDslContext().transactionResult(
				configuration -> TimeControlDAO.saveTimeControlDetail(ctx, tcd));
	}
	
	@Override
	public Location saveLocation(AONContext ctx, Location lc) {
		return  ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.saveLocation(ctx, lc));
	}

	@Override
	public Stream<Location> getLocationStream(AONContext ctx, LocationFilter filter) {
		return  ctx.getDslContext().transactionResult(
				configuration -> LocationDAO.getLocationStream(ctx, filter));
	}
	
}
