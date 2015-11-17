package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class IncomeDAO {
	
	
	public static IncomeDetail getLastIncomeDetail(AONContext ctx, Item item){
		
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.limit(1).fetch().stream().map(new IncomeDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new)).getFirst();
	}
	
	public static LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate){
		return ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(INCOME.ISSUE_TIME.greaterOrEqual(AonDateUtils.toSql(startDate)))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.fetch().stream().map(new IncomeDetailFiller())
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
