package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IGroupware;
import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.impl.jooq.dao.GroupwareDAO;

public class GroupwareImpl implements IGroupware {

	@Override
	public Notice getNotice(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(configuration ->
				GroupwareDAO.getNotice(ctx, id));
	}
	
	@Override
	public Integer insertNotice(AONContext ctx, Notice notice) {
		return ctx.getDslContext().transactionResult(configuration ->
				GroupwareDAO.insertNotice(ctx, notice));
	}
	
	@Override
	public Alarm getAlarm(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
				GroupwareDAO.getAlarm(ctx, id));
	}
	
	@Override
	public Integer insertAlarm(AONContext ctx, Alarm alarm) {
		return ctx.getDslContext().transactionResult(configuration ->
				GroupwareDAO.insertAlarm(ctx, alarm));
	}

}
