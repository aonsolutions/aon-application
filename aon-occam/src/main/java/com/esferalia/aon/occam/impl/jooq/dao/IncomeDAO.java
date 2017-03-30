package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IncomeDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IncomeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IncomeRegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IncomeDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IncomePropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class IncomeDAO {
	
	private static final IncomePropertiesDAO INCOME_PROPERTIES = new IncomePropertiesDAO();	
	private static final IncomeDetailPropertiesDAO INCOME_DETAIL_PROPERTIES = new IncomeDetailPropertiesDAO();
	
	public static Stream<Income> getIncomeStream(AONContext ctx, IncomeFilter filter){
		return INCOME_PROPERTIES.build(ctx.getDslContext().select()
				.from(INCOME).join(REGISTRY).on(REGISTRY.ID.eq(INCOME.SUPPLIER))
				, filter).fetch().stream().map(new IncomeRegistryFiller());
	}
	
	public static Optional<Income> insertIncome(AONContext ctx, Income income){
		return ctx.getDslContext().insertInto(INCOME, INCOME.DOMAIN, INCOME.PROJECT,
				INCOME.REFERENCE_CODE, INCOME.SUPPLIER, INCOME.ADDRESS, INCOME.ISSUE_TIME,
				INCOME.PAY_METHOD, INCOME.SECURITY_LEVEL, INCOME.STATUS, INCOME.COMMENTS,
				INCOME.REMARKS, INCOME.WORKPLACE, INCOME.SCOPE, INCOME.NUMBER_OF_PYMNTS,
				INCOME.DAYS_TO_FIRST_PYMNT, INCOME.DAYS_BETWEEN_PYMNTS, INCOME.PYMNT_DAYS,
				INCOME.BANK_ACCOUNT, INCOME.BANK_ALIAS, INCOME.BIC, INCOME.CARRIER_PACKING, 
				INCOME.CREATION_USER, INCOME.CREATION_DATE, INCOME.MODIFICATION_USER, INCOME.MODIFICATION_DATE)
			.values(income.getDomain(), income.getProject(),
					income.getReferenceCode(), income.getSupplier(), income.getAddress(), income.getIssueDate(),
					income.getPayMethod(), income.getSecurityLevel(), income.getStatus() != null ? income.getStatus().value() : null, income.getComments(),
					income.getRemarks(), income.getWorkplace(), income.getScope(), income.getNumberOfPymnts(),
					income.getDaysToFirstPymnt(), income.getDaysBetweenPymnt(), income.getPymntDays(),
					income.getBankAccount(), income.getBankAlias(), income.getBic(), income.getCarrierPacking(), 
					ctx.getUser(), new Date(), ctx.getUser(), new Date())
			.returning().fetch().stream().map(new IncomeFiller()).findFirst();
	}
	
	public static Optional<IncomeDetail> insertIncomeDetail(AONContext ctx, IncomeDetail incomeDetail){
		Record1<Short> a = ctx.getDslContext().select(DSL.max(INCOME_DETAIL.LINE))
			.from(INCOME_DETAIL).where(INCOME_DETAIL.INCOME.eq(incomeDetail.getIncome().getId()))
			.fetchOne();
		Integer line = a.value1() != null  ? a.value1().intValue() + 1 : 1;
		return ctx.getDslContext().insertInto(INCOME_DETAIL, INCOME_DETAIL.DOMAIN, INCOME_DETAIL.INCOME, INCOME_DETAIL.ITEM,
				INCOME_DETAIL.LINE, INCOME_DETAIL.PRICE, INCOME_DETAIL.PROJECT, INCOME_DETAIL.PURCHASE_DETAIL,
				INCOME_DETAIL.QUANTITY, INCOME_DETAIL.WAREHOUSE, INCOME_DETAIL.DESCRIPTION, INCOME_DETAIL.DISCOUNT_EXPR, 
				INCOME_DETAIL.CREATION_USER, INCOME_DETAIL.CREATION_DATE, INCOME_DETAIL.MODIFICATION_USER, INCOME_DETAIL.MODIFICATION_DATE)
			.values(incomeDetail.getDomain(), incomeDetail.getIncome() != null ? incomeDetail.getIncome().getId() : null, incomeDetail.getItem() != null ? incomeDetail.getItem().getId() : null,
					line.shortValue(), incomeDetail.getPrice(), incomeDetail.getProject() != null ? incomeDetail.getProject().getId() : null, incomeDetail.getPurchaseDetail(),
					incomeDetail.getQuantity(), incomeDetail.getWarehouse(), incomeDetail.getDescription(), incomeDetail.getDiscountExpression(),
					ctx.getUser(), AonDateUtils.toTimestamp(new Date()), ctx.getUser(), AonDateUtils.toTimestamp(new Date()))
			.returning().fetch().stream().map(new IncomeDetailFiller()).findFirst();
	}
	
	public static Optional<IncomeDetail> updateIncomeDetail(AONContext ctx, IncomeDetail incomeDetail){
		return ctx.getDslContext().update(INCOME_DETAIL)
				.set(INCOME_DETAIL.DOMAIN, incomeDetail.getDomain())
				.set(INCOME_DETAIL.INCOME, incomeDetail.getIncome().getId())
				.set(INCOME_DETAIL.ITEM, incomeDetail.getItem().getId())
				.set(INCOME_DETAIL.LINE, incomeDetail.getLine())
				.set(INCOME_DETAIL.PRICE, incomeDetail.getPrice())
				.set(INCOME_DETAIL.PROJECT, incomeDetail.getProject().getId())
				.set(INCOME_DETAIL.PURCHASE_DETAIL, incomeDetail.getPurchaseDetail())
				.set(INCOME_DETAIL.QUANTITY, incomeDetail.getQuantity())
				.set(INCOME_DETAIL.WAREHOUSE, incomeDetail.getWarehouse())
				.set(INCOME_DETAIL.DESCRIPTION, incomeDetail.getDescription())
				.set(INCOME_DETAIL.DISCOUNT_EXPR, incomeDetail.getDiscountExpression())
				.set(INCOME_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(INCOME_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.where(INCOME_DETAIL.ID.eq(incomeDetail.getId()))
			.returning().fetch().stream().map(new IncomeDetailFiller()).findFirst();
	}
	
	public static Optional<IncomeDetail> deleteIncomeDetail(AONContext ctx, Integer id){
		Optional<IncomeDetail> incomeDetail = getIncomeDetailStream(ctx, f -> f.getIdProperty().eq(id)).findFirst();
		ctx.getDslContext().update(INCOME_DETAIL).set(INCOME_DETAIL.LINE, INCOME_DETAIL.LINE.add(-1))
		.where(INCOME_DETAIL.INCOME.eq(incomeDetail.get().getIncome().getId()).and(INCOME_DETAIL.LINE.greaterThan(incomeDetail.get().getLine())))
		.execute();
		ctx.getDslContext().delete(INCOME_DETAIL).where(INCOME_DETAIL.ID.eq(id)).execute();
		return incomeDetail;
	}
	
	public static Stream<IncomeDetail> getIncomeDetailStream(AONContext ctx, IncomeDetailFilter filter){
		return INCOME_DETAIL_PROPERTIES.build(ctx.getDslContext().select().from(INCOME_DETAIL), filter)
				.fetch().stream().map(new IncomeDetailFiller());
	}
	
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
				.limit(1).fetch().stream().map(new SpecialIncomeDetailFiller())
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
				.limit(1).fetch().stream().map(new SpecialIncomeDetailFiller())
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
				.fetch().stream().map(new SpecialIncomeDetailFiller())
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
				.fetch().stream().map(new SpecialIncomeDetailFiller())
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
				.fetch().stream().map(new SpecialIncomeDetailFiller())
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
				.fetch().stream().map(new SpecialIncomeDetailFiller())
				.filter(f -> list.stream().filter(h -> h.equals(f.getId())).count() == 0)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class SpecialIncomeDetailFiller implements Function<Record, IncomeDetail> {
		
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
