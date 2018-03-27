package com.esferalia.aon.occam.impl.jooq.dao.stat;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.task.ITaskChartTypeVisitor;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.AonDayOfWeek;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskChartTypeVisitor implements ITaskChartTypeVisitor {

	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yy");

	private AONContext ctx;
	private StatParams params;
	private StatData<String, String, Double> table;	

	public TaskChartTypeVisitor(AONContext ctx, StatParams params, StatData<String,String,Double> table) {
		this.ctx = ctx;
		this.params = params;
		this.table = table;
	}

	@Override
	public void visitTaskByType() {
		ctx.getDslContext().select(DSL.count(TASK.ID), TAG.NAME)
		.from(TASK).join(TASK_TAG).on(TASK.ID.eq(TASK_TAG.TASK))
			.join(TAG).on(TASK_TAG.TAG.eq(TAG.ID))
		.where(TAG.TYPE.eq(TagType.TASK_TYPE.value()))
			.and(getTaskCondition(ctx, params))
		.groupBy(TAG.NAME).fetch().stream().forEach(r -> 
			table.put(r.getValue(TAG.NAME), "CANTIDAD", r.value1().doubleValue()));
	}
	
	@Override
	public void visitTaskByTag() {
		ctx.getDslContext().select(DSL.count(TASK.ID), TAG.NAME)
		.from(TASK).join(TASK_TAG).on(TASK.ID.eq(TASK_TAG.TASK))
			.join(TAG).on(TASK_TAG.TAG.eq(TAG.ID))
		.where(TAG.TYPE.eq(TagType.TASK_LABEL.value()))
			.and(getTaskCondition(ctx, params))
		.groupBy(TAG.NAME).fetch().stream().forEach(r -> 
			table.put(r.getValue(TAG.NAME), "CANTIDAD", r.value1().doubleValue()));
	}
	
	@Override
	public void visitTaskByStatus() {
		AggregateFunction<Integer> count = DSL.count(TASK.ID);
		ctx.getDslContext().select(TASK.STATUS,  count)
			.from(TASK)
			.where(getTaskCondition(ctx, params))
			.groupBy(TASK.STATUS)
			.orderBy(TASK.STATUS)				
			.fetch().stream().forEach(rec -> {
				double amount = rec.getValue(count).doubleValue();
				table.put(TaskStatus.values()[rec.getValue(TASK.STATUS)].getESName(),"CANTIDAD", amount);
			});
	}
	
	@Override
	public void visitTaskBySchedule() {
		LinkedList<Date> list = ctx.getDslContext().select(TASK.START_DATE)
				.from(TASK)
				.where(getTaskCondition(ctx, params))
				.fetch().stream().map(r -> r.getValue(TASK.START_DATE))
				.collect(Collectors.toCollection(LinkedList::new));
			for(Integer i = 0; i < 24; i++){
				Integer hora = i;
				Long count = list.stream().filter(r -> AonDateUtils.getHour(r) == hora).count();
				table.put("De " + String.format("%02d", hora) + "h a " + String.format("%02d", hora+1) + "h", "CANTIDAD",  count.doubleValue());	
			}
	}
	
	@Override
	public void visitTaskByMonth() {
		final Field<Integer> year = DSL.year(TASK.START_DATE);
		final Field<Integer> month = DSL.month(TASK.START_DATE);
		AggregateFunction<Integer> count = DSL.count(TASK.ID);
		ctx.getDslContext().select(TASK.STATUS, year, month, count)
			.from(TASK)
			.where(getTaskCondition(ctx, params))
			.groupBy(year,month, TASK.STATUS)
			.orderBy(year,month, TASK.STATUS)				
			.fetch()
			.stream()
			.forEach(rec -> {
				String monthKey = rec.getValue(month)+"/"+rec.getValue(year);
				double amount = rec.getValue(count).doubleValue();
				table.put(monthKey, TaskStatus.values()[rec.getValue(TASK.STATUS)].getESName(), amount);
			});
	}
	
	@Override
	public void visitTaskByDayOfWeek() {
		LinkedList<Date> list = ctx.getDslContext().select(TASK.START_DATE)
				.from(TASK)
				.where(getTaskCondition(ctx, params))
				.fetch().stream().map(r -> r.getValue(TASK.START_DATE))
				.collect(Collectors.toCollection(LinkedList::new));
			for(Integer i = 0; i < 7; i++){
				Integer day = i;
				Long count = list.stream().filter(r -> AonDateUtils.getDayOfWeek(r) == day+1).count();
				table.put(AonDayOfWeek.values()[day].getName(), "CANTIDAD",  count.doubleValue());	
			}
	}
	
	@Override
	public void visitTaskByDay() {
		Field<java.sql.Date> date = DSL.date(TASK.START_DATE);
		AggregateFunction<Integer> count = DSL.count(TASK.ID);
		ctx.getDslContext().select(date, TASK.STATUS,  count)
			.from(TASK)
			.where(getTaskCondition(ctx, params))
			.groupBy(date, TASK.STATUS)
			.orderBy(date, TASK.STATUS)				
			.fetch().stream().forEach(rec -> {
				String date1= FMT.format( rec.getValue(date));
				double amount = rec.getValue(count).doubleValue();
				table.put(date1, TaskStatus.values()[rec.getValue(TASK.STATUS)].getESName(), amount);
			});
	}

	@Override
	public void visitTaskByCustomer() {
		ctx.getDslContext().select(DSL.count(TASK.ID), REGISTRY.NAME)
		.from(TASK).join(REGISTRY).on(TASK.REGISTRY.eq(REGISTRY.ID))
		.where(getTaskCondition(ctx, params))
		.groupBy(TASK.REGISTRY).fetch().stream().forEach(r -> 
			table.put(r.getValue(REGISTRY.NAME), "CANTIDAD", r.value1().doubleValue()));
	}
	
	public static Condition getTaskCondition(AONContext ctx, StatParams params) {
		Condition c = TASK.DOMAIN.eq(ctx.getDomainId())
				.and(TASK.NUMBER.isNotNull());
		if (params.getFrom() != null) c = c.and(TASK.START_DATE.ge(new Timestamp(params.getFrom().getTime())));
		if (params.getTo() != null) c = c.and(TASK.START_DATE.le(new Timestamp(params.getTo().getTime())));
		if (params.getIssueFilter() != null && params.getIssueFilter().getState() != null){
			if(params.getIssueFilter().getState().equals("open"))
				c = c.and(TASK.STATUS.eq(TaskStatus.PENDING.value())
					 .or(TASK.STATUS.eq(TaskStatus.IN_PROGRESS.value())));
			else if(params.getIssueFilter().getState().equals("closed"))
				c = c.and(TASK.STATUS.eq(TaskStatus.FINISHED.value()));
			else if(params.getIssueFilter().getState().equals("deleted"))
				c = c.and(TASK.STATUS.eq(TaskStatus.DELETED.value()));
		}
		return c;
	}
	
}
