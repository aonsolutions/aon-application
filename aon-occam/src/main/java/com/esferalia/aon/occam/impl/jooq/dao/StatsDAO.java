package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.math.BigDecimal;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.SelectLimitStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class StatsDAO {

	public static StatParams createStatParams(AONContext ctx) {
		StatParams params = new StatParams();
		for (InvoiceType type : InvoiceType.values()) {
			params.getFilterItems().add(
					new StatFilterItem().setId(type.toString())
					.setLabel(type.getDescription())
					.setType(StatFilterType.INVOICE_TYPE));
		}
		for (ProductCategory pc : ProductDAO.getProductCategories(ctx)) {
			params.getFilterItems().add(
					new StatFilterItem().setId(AonNumberUtils.toString(pc.getId()))
					.setLabel(pc.getName())
					.setType(StatFilterType.PRODUCT_CATEGORY));
		}
		for(Workplace wp : WorkplaceDAO.getWorkplaceList(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))){
			params.getFilterItems().add(
			new StatFilterItem()
				.setId(wp.getId())
				.setLabel(wp.getDescription())
				.setType(StatFilterType.WORKPLACE));
		}
			
		return params;
	}
	
		
	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private static SelectLimitStep<Record3<Integer, Byte, BigDecimal>> getSentence(AONContext ctx, StatParams params,
			Field<Integer> when, AggregateFunction<BigDecimal> what) {
		
		//params.setFrom( AonDateUtils.getYearFirstDay(2014)); params.setTo(AonDateUtils.getYearLastDay(2015) );
		 
		Condition c = INVOICE.DOMAIN.eq(ctx.getDomainId());

		//Esto de momento no influye con StatFilterItem...
		if (params.getFrom() != null) {
			// siempre que mayor o igual...
			c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(params.getFrom())));
		}
		if (params.getTo() != null) {
			// siempre que menos o igual...
			c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(params.getTo())));
		}

		// En vez de crear la condicion(la where), como en la siguiente linea,
		// con comillas, se va creando con jooq(Condition c),
		// por si luego se modifica o se cambia un campo, no da error de
		// escritura si no de compilacion y es mas facil de ver
		// String where = "INVOICE.DOMAIN = params.getDomain()";

		boolean mustFilterByItemFields = false;
		Condition productCategoriesCondition = null;
		Condition invoiceTypeCondition = null;
		Condition workplaceCondition = null;
		for (StatFilterItem item : params.getFilterItems() ) {
			//Si todos los item.isSelected() están a false o todos a true, también se pinta
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.INVOICE_TYPE) {
					byte invoiceType = (byte) InvoiceType.valueOf(item.getId()).ordinal();
					if (invoiceTypeCondition == null) {
						invoiceTypeCondition = INVOICE.TYPE.eq(invoiceType);
					} else {
						invoiceTypeCondition = invoiceTypeCondition.or(INVOICE.TYPE.eq(invoiceType));
					}
				}
				if (item.getType() == StatFilterType.PRODUCT_CATEGORY) {
					int productCategoryId = AonNumberUtils.toInteger( item.getId());
					if (productCategoriesCondition == null) {
						productCategoriesCondition = PRODUCT.CATEGORY.eq( productCategoryId );
					} else {
						productCategoriesCondition = productCategoriesCondition.or(PRODUCT.CATEGORY.eq(productCategoryId));
					}
				}
				if (item.getType() == StatFilterType.WORKPLACE) {
					int workplaceId = AonNumberUtils.toInteger( item.getId());
					if (workplaceCondition == null) {
						workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq( workplaceId );
					} else {
						workplaceCondition = workplaceCondition.or(INVOICE_DETAIL.WORKPLACE.eq(workplaceId));
					}
				}
			}
		}
		if (invoiceTypeCondition != null) {
			c = c.and(invoiceTypeCondition);
		}
		if (productCategoriesCondition != null) {
			c = c.and(productCategoriesCondition);
			mustFilterByItemFields = true;
		}
		if (workplaceCondition != null) {
			c = c.and(workplaceCondition);
		}
		
		
		// Selects para devolver ctx en función de c
		if (mustFilterByItemFields) {
			return ctx.getDslContext().select(when, INVOICE.TYPE, what)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where(c)
					.groupBy(when, INVOICE.TYPE)
					.orderBy(when, INVOICE.TYPE);
		}
		return ctx.getDslContext().select(when, INVOICE.TYPE, what)
			.from(INVOICE)
			.join(INVOICE_DETAIL)
			.on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.where(c)
			.groupBy(when, INVOICE.TYPE)
			.orderBy(when, INVOICE.TYPE);
	}
	
	//A partir de aquí, se rellenan las tablas(year, month, day), con la condición y los campos y datos que necesitamos devolver
	//YEAR
	public static StatData<Integer, String, Double> getYearInvoiceTypeData(AONContext ctx, StatParams params) {

		StatData<Integer, String, Double> table = new StatData<Integer, String, Double>();
		final String profit = "Beneficio";

		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
		getSentence(ctx, params, year, sum).fetch().stream().forEach(rec -> {
			// Año, Tipo --> cantidad
			InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
			double amount = rec.getValue(sum).doubleValue();
			Integer y = rec.getValue(year);
			// Beneficio --> cantidad
			Double d = table.get(y, profit);
			d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
			table.put(y, profit, d);

			// Año, Tipo --> cantidad
			table.put(y, type.getDescription(), amount);
		});
		return table;
	}

	// MONTH
	public static StatData<Integer, String, Double> getMonthInvoiceTypeData(AONContext ctx, StatParams params) {

		StatData<Integer, String, Double> table = new StatData<Integer, String, Double>();
		final String profit = "Beneficio";
		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> month = DSL.month(INVOICE.ISSUE_DATE);
		getSentence(ctx, params, month, sum)
			.fetch()
			.stream()
			.forEach(rec -> {
				// Mes, Tipo --> cantidad
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				double amount = rec.getValue(sum).doubleValue();
				Integer m = rec.getValue(month);
				// Beneficio --> cantidad
				Double d = table.get(m, profit);
				d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
				table.put(m, profit, d);
	
				// Month, Tipo --> cantidad
				table.put(m, type.getDescription(), amount);
			});
		return table;
	}

	//DAY
	public static StatData<Integer,  String, Double> getDayInvoiceTypeData(AONContext ctx, StatParams params) {
		StatData<Integer, String, Double> table = new StatData<Integer, String, Double>();
		final String profit = "Beneficio";
		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> day = DSL.day(INVOICE.ISSUE_DATE);
		getSentence(ctx, params, day, sum).fetch().stream().forEach(rec -> {
			// Día, Tipo --> cantidad
			InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
			double amount = rec.getValue(sum).doubleValue();
			Integer d = rec.getValue(day);
			// Beneficio --> cantidad
			Double dou = table.get(d, profit);
			dou = AonMathUtils.round((dou == null ? 0.0 : dou) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
			table.put(d, profit, dou);

			// Month, Tipo --> cantidad
			table.put(d, type.getDescription(), amount);
		});
		return table;
	}

}
