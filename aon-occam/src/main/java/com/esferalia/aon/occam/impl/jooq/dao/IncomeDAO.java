package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class IncomeDAO {
	
	
	public static IncomeDetail getLastIncomeDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId){
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(workplaceCondition)
				.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.limit(1).fetch().stream().map(new IncomeDetailFiller())
				.findFirst().orElse(new IncomeDetail());
	}
	
	public static IncomeDetail getLastIncomeDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date){
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(workplaceCondition)
				.and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(INCOME.ISSUE_TIME.lessOrEqual(AonDateUtils.toSql(date)))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.limit(1).fetch().stream().map(new IncomeDetailFiller())
				.findFirst().orElse(new IncomeDetail());
	}
	
	public static LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId){
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(workplaceCondition).and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(INCOME.ISSUE_TIME.greaterOrEqual(AonDateUtils.toSql(startDate)))
				.and(INCOME_DETAIL.ID.notIn(ctx.getDslContext().select(INVOICE_DETAIL.SOURCE_ID)
						.from(INVOICE_DETAIL)															
						.where(INVOICE_DETAIL.SOURCE.eq((byte)4))
						.and(INVOICE_DETAIL.WORKPLACE.eq(workplaceId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())))
				.orderBy(INCOME.ISSUE_TIME.desc()
						,INCOME_DETAIL.ID.desc())
				.fetch().stream().map(new IncomeDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<IncomeDetail> getLastIncomeDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date){
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(workplaceCondition).and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(INCOME.ISSUE_TIME.lessOrEqual(AonDateUtils.toSql(date)))
				.and(INCOME.ISSUE_TIME.greaterOrEqual(AonDateUtils.toSql(startDate)))
				.and(INCOME_DETAIL.ID.notIn(ctx.getDslContext().select(INVOICE_DETAIL.SOURCE_ID)
						.from(INVOICE_DETAIL)															
						.where(INVOICE_DETAIL.SOURCE.eq((byte)4))
						.and(INVOICE_DETAIL.WORKPLACE.eq(workplaceId))
						.and(INVOICE_DETAIL.WAREHOUSE.isNull())))
				.orderBy(INCOME.ISSUE_TIME.desc()
						,INCOME_DETAIL.ID.desc())
				.fetch().stream().map(new IncomeDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<IncomeDetail> getIncomeDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId){
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(workplaceCondition).and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.and(INCOME_DETAIL.ID.notIn(ctx.getDslContext().select(INVOICE_DETAIL.SOURCE_ID)
															.from(INVOICE_DETAIL)															
															.where(INVOICE_DETAIL.SOURCE.eq((byte)4))
															.and(INVOICE_DETAIL.WORKPLACE.eq(workplaceId))
															.and(INVOICE_DETAIL.WAREHOUSE.isNull())))
				.orderBy(INCOME.ISSUE_TIME.desc()
						,INCOME_DETAIL.ID.desc())
				.fetch().stream().map(new IncomeDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedList<IncomeDetail> getIncomeDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date){
		LinkedList<Integer> list = ctx.getDslContext().select(INVOICE_DETAIL.SOURCE_ID)
		.from(INVOICE_DETAIL)															
		.where(INVOICE_DETAIL.SOURCE.eq((byte)4))
		.and(INVOICE_DETAIL.WORKPLACE.eq(workplaceId))
		.and(INVOICE_DETAIL.ITEM.eq(item.getId()))
		.and(INVOICE_DETAIL.WAREHOUSE.isNull())
		.fetch().stream().map(r -> r.getValue(INVOICE_DETAIL.SOURCE_ID))
		.collect(Collectors.toCollection(LinkedList::new));
		
		Condition workplaceCondition = INCOME.WORKPLACE.isNull();
		if(workplaceId != null) workplaceCondition = INCOME.WORKPLACE.eq(workplaceId);
		
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(INCOME.ISSUE_TIME.lessOrEqual(AonDateUtils.toSql(date)))
				.and(workplaceCondition).and(INCOME_DETAIL.WAREHOUSE.eq(warehouseId))
				.orderBy(INCOME.ISSUE_TIME.desc()
						,INCOME_DETAIL.ID.desc())
				.fetch().stream().map(new IncomeDetailFiller())
				.filter(f -> list.stream().filter(h -> h.equals(f.getId())).count() == 0)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class IncomeDetailFiller implements Function<Record, IncomeDetail> {
		
		@Override
		public IncomeDetail apply(Record r) {
			return new IncomeDetail().setIncome(new Income().setIssueDate(r.getValue(INCOME.ISSUE_TIME)))
					.setPrice(r.getValue(INCOME_DETAIL.PRICE)).setId(r.getValue(INVOICE_DETAIL.ID))
					.setDiscountExpression(r.getValue(INCOME_DETAIL.DISCOUNT_EXPR) != null
							? r.getValue(INCOME_DETAIL.DISCOUNT_EXPR) : "0.0")
					.setQuantity(
							r.getValue(INCOME_DETAIL.QUANTITY) != null ? r.getValue(INCOME_DETAIL.QUANTITY) : 0.0);
		}

	}
}
