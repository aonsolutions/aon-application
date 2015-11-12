package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;

import java.util.Date;
import java.util.LinkedList;

import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;

public class IncomeDAO {
	
	
	public static IncomeDetail getLastIncomeDetail(AONContext ctx, Item item){
		
		Record3<java.sql.Date, Double, Integer> data = ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.limit(1)
				.fetchOne();
		
		IncomeDetail incomeDetail = new IncomeDetail();
		if(data != null){
			if(data.value1() != null){
				Income income = new Income();
				income.setIssueDate(data.value1());
				incomeDetail.setIncome(income);
			}
			if(data.value2() != null) incomeDetail.setPrice(data.value2());
			if(data.value3() != null) incomeDetail.setId(data.value3());
		}
		return incomeDetail;
	}
	
	public static LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate){
		java.sql.Date date = new java.sql.Date(startDate.getTime());
		
		Result<Record5<java.sql.Date, Double, Integer, String, Double>> data = ctx.getDslContext()
				.select(INCOME.ISSUE_TIME, INCOME_DETAIL.PRICE, INCOME_DETAIL.ID, INCOME_DETAIL.DISCOUNT_EXPR
						,INCOME_DETAIL.QUANTITY)
				.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.where(INCOME_DETAIL.ITEM.eq(item.getId()))
				.and(INCOME.ISSUE_TIME.greaterOrEqual(date))
				.orderBy(INCOME.ISSUE_TIME.desc())
				.fetch();
		
		LinkedList<IncomeDetail> list = new LinkedList<IncomeDetail>();
		
		data.stream().forEach(r -> {
			IncomeDetail incomeDetail = new IncomeDetail();
			if(r.value1() != null){
				Income income = new Income();
				income.setIssueDate(r.value1());
				incomeDetail.setIncome(income);
				incomeDetail.setQuantity(r.value5() != null ? r.value5() : 0.0);
			}
			if(r.value2() != null) incomeDetail.setPrice(r.value2());
			if(r.value3() != null) incomeDetail.setId(r.value3());
			incomeDetail.setDiscountExpression(r.value4() != null?r.value4():"0.0");
			list.add(incomeDetail);
		});
		return list;
	}
}
