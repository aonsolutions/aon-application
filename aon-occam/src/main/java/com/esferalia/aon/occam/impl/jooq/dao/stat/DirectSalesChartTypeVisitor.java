package com.esferalia.aon.occam.impl.jooq.dao.stat;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.stat.IStatFilterItemVisitor;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.invoice.IDirectSalesChartTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DirectSalesChartTypeVisitor implements IDirectSalesChartTypeVisitor {
	private static final Field<Integer>  MONTH_FIELD = DSL.field("month_field", Integer.class);
	private static final Field<Integer>   YEAR_FIELD = DSL.field("year_field" , Integer.class);
	private static final Field<BigDecimal> SUM_FIELD = DSL.sum(DSL.field("sum_field"  , BigDecimal.class));
	private static final Field<java.sql.Date> DATE_FIELD = DSL.field("date_field"  , java.sql.Date.class);
	private static final Field<Byte>  TYPE_FIELD = DSL.field("in_type_field", Byte.class);
	
	private static final Field<Byte>  INVOICE_TYPE_FIELD = INVOICE.TYPE.as("in_type_field");
	private static final Field<Byte>  SALES_TYPE_FIELD = DSL.inline(InvoiceType.SALES.value()).as("in_type_field");
	private static final Field<Byte>  PURCHASE_TYPE_FIELD = DSL.inline(InvoiceType.PURCHASE.value()).as("in_type_field");
	
	private static final Field<Integer> INV_YEAR = DSL.year(INVOICE.ISSUE_DATE).as(YEAR_FIELD);
	private static final Field<Integer> INV_MONTH = DSL.month(INVOICE.ISSUE_DATE).as(MONTH_FIELD);
	
	private static final Field<Integer> DEL_YEAR = DSL.year(DELIVERY.ISSUE_TIME).as(YEAR_FIELD);
	private static final Field<Integer> DEL_MONTH = DSL.month(DELIVERY.ISSUE_TIME).as(MONTH_FIELD);

	private static final Field<Integer> INC_YEAR = DSL.year(INCOME.ISSUE_TIME).as(YEAR_FIELD);
	private static final Field<Integer> INC_MONTH = DSL.month(INCOME.ISSUE_TIME).as(MONTH_FIELD);

	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yy");
	protected static final String UNKNOWN = "Desconocido";
	public static final String AMOUNT_LABEL = "Importe"; 
	@Deprecated
	private String periodLabel = "Periodo seleccionado";

	private AONContext ctx;
	private StatParams params;
	private StatData<String, String, Double> table;
	
	public DirectSalesChartTypeVisitor(AONContext ctx, StatParams params, StatData<String,String,Double> table) {
		this.ctx = ctx;
		this.params = params;
		this.table = table;
		this.params.setViewPreviousPeriod(false);
	}
	
	private SelectOnConditionStep<Record> getInvoiceSelect(SelectField<?> ... fields) {
		SelectOnConditionStep<Record> sel = ctx.getDslContext()
				.select(fields)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
				.leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				;
			if (params.hasTagFilter()) {
				sel = sel.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID));
			}
			if (params.hasSegmentFilter()) {
				sel = sel.leftOuterJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(INVOICE.REGISTRY));
			}
		return sel;
	}
	
	private SelectOnConditionStep<Record> getDeliverySelect(SelectField<?> ... fields) {
		SelectOnConditionStep<Record> sel = ctx.getDslContext()
				.select(fields)
				.from(DELIVERY)
				.join(DELIVERY_DETAIL).on(DELIVERY.ID.eq(DELIVERY_DETAIL.DELIVERY))
				.leftOuterJoin(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
				.leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				;
			if (params.hasTagFilter()) {
				sel = sel.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID));
			}
			if (params.hasSegmentFilter()) {
				sel = sel.leftOuterJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(DELIVERY.CUSTOMER));
			}
		return sel;
	}
	
	private SelectOnConditionStep<Record> getIncomeSelect(SelectField<?> ... fields) {
		SelectOnConditionStep<Record> sel = ctx.getDslContext()
				.select(fields)
				.from(INCOME)
				.join(INCOME_DETAIL).on(INCOME.ID.eq(INCOME_DETAIL.INCOME))
				.leftOuterJoin(ITEM).on(INCOME_DETAIL.ITEM.eq(ITEM.ID))
				.leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				;
			if (params.hasTagFilter()) {
				sel = sel.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID));
			}
			if (params.hasSegmentFilter()) {
				sel = sel.leftOuterJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(INCOME.SUPPLIER));
			}
		return sel;
	}

	@Override
	public void visitDirectSalesTypeByYearComboChart() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		ctx.getDslContext().select(TYPE_FIELD,YEAR_FIELD,SUM_FIELD)
			.from(
				getInvoiceSelect(INVOICE_TYPE_FIELD,INV_YEAR, invSum )
					.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD,INV_YEAR)
				.orderBy(INVOICE_TYPE_FIELD,INV_YEAR)
			.unionAll(
				getDeliverySelect( SALES_TYPE_FIELD, DEL_YEAR, delSum ) 
					.where( getDeliveryCondition())
				.groupBy(DEL_YEAR)
				.orderBy(DEL_YEAR))
			.unionAll(
				getIncomeSelect(PURCHASE_TYPE_FIELD,INC_YEAR, incSum ) 
					.where( getIncomeCondition())
				.groupBy(INC_YEAR)
				.orderBy(INC_YEAR))
		)
		.groupBy(TYPE_FIELD,YEAR_FIELD)
		.orderBy(TYPE_FIELD,YEAR_FIELD)
		.fetch()
		.stream()
		.forEach(rec -> {
			InvoiceType type = InvoiceType.safeValueOf(rec.getValue(TYPE_FIELD));
			double amount = rec.getValue(SUM_FIELD).doubleValue();
			String y = AonNumberUtils.toString( rec.getValue(YEAR_FIELD) );
			table.put(y, type.getDescription() , amount);
		});
	}

	@Override
	public void visitDirectSalesTypeByMonthsComboChart() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		
		ctx.getDslContext().select(TYPE_FIELD,YEAR_FIELD,MONTH_FIELD,SUM_FIELD)
			.from(
				getInvoiceSelect(INVOICE_TYPE_FIELD,INV_YEAR,INV_MONTH, invSum )
					.where( getInvoiceCondition())
					.groupBy(INVOICE_TYPE_FIELD,INV_YEAR,INV_MONTH)
					.orderBy(INVOICE_TYPE_FIELD,INV_YEAR,INV_MONTH)
			.unionAll(
				 getDeliverySelect(SALES_TYPE_FIELD,DEL_YEAR,DEL_MONTH, delSum ) 
					.where( getDeliveryCondition())
					.groupBy(DEL_YEAR,DEL_MONTH)
					.orderBy(DEL_YEAR,DEL_MONTH))
			.unionAll(
					getIncomeSelect(PURCHASE_TYPE_FIELD,INC_YEAR,INC_MONTH, incSum ) 
						.where( getIncomeCondition())
						.groupBy(INC_YEAR)
						.orderBy(INC_YEAR))
			)
			.groupBy(TYPE_FIELD,YEAR_FIELD,MONTH_FIELD)
			.orderBy(TYPE_FIELD,YEAR_FIELD,MONTH_FIELD)
			.fetch()
			.stream()
			.forEach(rec -> {
				InvoiceType type = InvoiceType.safeValueOf(rec.getValue(TYPE_FIELD));
				String monthKey = rec.getValue(MONTH_FIELD)+AonStringUtils.SLASH+rec.getValue(YEAR_FIELD);
				double amount = rec.getValue(SUM_FIELD).doubleValue();
				table.put(monthKey, type.getDescription() , amount);
			});
	}

	@Override
	public void visitDirectSalesTypeByWeeksComboChart() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");

		final Calendar calendar = Calendar.getInstance();
		ctx.getDslContext().select(TYPE_FIELD,DATE_FIELD,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE.as(DATE_FIELD), invSum )
				.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE)
				.orderBy(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE)
		.unionAll(
			 getDeliverySelect(SALES_TYPE_FIELD,DSL.date( DELIVERY.ISSUE_TIME).as(DATE_FIELD) , delSum ) 
				.where( getDeliveryCondition())
				.groupBy(DSL.date( DELIVERY.ISSUE_TIME))
				.orderBy(DSL.date( DELIVERY.ISSUE_TIME)))
		.unionAll(
				getIncomeSelect(PURCHASE_TYPE_FIELD,INCOME.ISSUE_TIME, incSum ) 
					.where( getIncomeCondition())
				.groupBy(INCOME.ISSUE_TIME)
				.orderBy(INCOME.ISSUE_TIME))
		)
		.groupBy(TYPE_FIELD,DATE_FIELD)
		.orderBy(TYPE_FIELD,DATE_FIELD)
		.fetch()
		.stream()
		.forEach(rec -> {
			InvoiceType type = InvoiceType.safeValueOf(rec.getValue(TYPE_FIELD));
			Date date = rec.getValue(DATE_FIELD);
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
			String firstDayOfWeek = FMT.format( calendar.getTime() );
			String weekKey = calendar.get(Calendar.WEEK_OF_YEAR) + " ("+firstDayOfWeek+")";
			double amount = rec.getValue(SUM_FIELD).doubleValue();
			Double acum = table.get(weekKey, type.getDescription() );
			acum = AonMathUtils.round( (acum == null ? 0.0 : acum) + amount); 
			table.put(weekKey, type.getDescription() , acum);
		});
	}

	@Override
	public void visitDirectSalesTypeByDaysComboChart() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		ctx.getDslContext().select(TYPE_FIELD,DATE_FIELD,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE.as(DATE_FIELD), invSum )
				.where( getInvoiceCondition())
			.groupBy(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE)
			.orderBy(INVOICE_TYPE_FIELD,INVOICE.ISSUE_DATE)
		.unionAll(
			 getDeliverySelect(SALES_TYPE_FIELD, DSL.date( DELIVERY.ISSUE_TIME).as(DATE_FIELD) , delSum ) 
				.where( getDeliveryCondition())
			.groupBy(DSL.date( DELIVERY.ISSUE_TIME))
			.orderBy(DSL.date( DELIVERY.ISSUE_TIME)))
		.unionAll(
			getIncomeSelect(PURCHASE_TYPE_FIELD,INCOME.ISSUE_TIME, incSum ) 
				.where( getIncomeCondition())
			.groupBy(INCOME.ISSUE_TIME)
			.orderBy(INCOME.ISSUE_TIME))
		)
		.groupBy(TYPE_FIELD,DATE_FIELD)
		.orderBy(TYPE_FIELD,DATE_FIELD)
		.fetch()
		.stream()
		.forEach(rec -> {
			InvoiceType type = InvoiceType.safeValueOf(rec.getValue(TYPE_FIELD));
			String date = FMT.format( rec.getValue(DATE_FIELD) );
			double amount = rec.getValue(SUM_FIELD).doubleValue();
			table.put(date, type.getDescription(), amount);
		});
	}
	
//	@Override
//	public void visitGeoProvince() {
//		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
//		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
//		final Field<String> geozone = DSL.field("geozone", String.class);
//		ctx.getDslContext().select(geozone,SUM_FIELD)
//			.from(
//				getInvoiceSelect(GEOZONE.NAME.as("geozone"), invSum)
//					.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(INVOICE.REGISTRY).and(RADDRESS.TYPE.eq((byte) 0)))
//					.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
//					.where( getInvoiceCondition())
//					.groupBy(GEOZONE.NAME)
//			.unionAll(
//				 getDeliverySelect(GEOZONE.NAME.as("geozone"), delSum ) 
//					.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(DELIVERY.CUSTOMER).and(RADDRESS.TYPE.eq((byte) 0)))
//					.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
//					.where( getDeliveryCondition())
//					.groupBy(GEOZONE.NAME)
//			 ))
//			.groupBy(geozone)
//			.orderBy(SUM_FIELD.desc())
//			.fetch()
//			.stream()
//			.forEach(rec -> {
//				double d = rec.getValue(SUM_FIELD).doubleValue();
//				if (d >= 0) {
//					table.put( "CHART"
//							, AonStringUtils.defaultIfBlank(rec.getValue(geozone), UNKNOWN)
//							, d);
//				}
//			});
//	}
	
	@Override
	public void visitAbcDirectSalesWorkplace() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> workplaceId = DSL.field("workplaceId", Integer.class);
		final Field<String> workplace = DSL.field("workplace", String.class);
		
		ctx.getDslContext().select(TYPE_FIELD,workplaceId,workplace,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,INVOICE_DETAIL.WORKPLACE.as(workplaceId),WORKPLACE.DESCRIPTION.as(workplace), invSum)
				.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INVOICE_DETAIL.WORKPLACE))
			.where( getInvoiceCondition())
			.groupBy(INVOICE_TYPE_FIELD,INVOICE_DETAIL.WORKPLACE)
		.unionAll(
			getDeliverySelect(SALES_TYPE_FIELD,DELIVERY.WORKPLACE.as(workplaceId),WORKPLACE.DESCRIPTION.as(workplace), delSum)
				.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(DELIVERY.WORKPLACE))
			.where( getDeliveryCondition())
			.groupBy(DELIVERY.WORKPLACE))
		.unionAll(
			getIncomeSelect(PURCHASE_TYPE_FIELD,INCOME.WORKPLACE.as(workplaceId),WORKPLACE.DESCRIPTION.as(workplace), incSum)
				.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INCOME.WORKPLACE))
			.where( getIncomeCondition())
			.groupBy(INCOME.WORKPLACE))
		)
		.groupBy(TYPE_FIELD,workplaceId)
		.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
		.fetch()
		.stream()
		.forEach(rec -> {
			InvoiceType type = InvoiceType.safeValueOf(rec.getValue(TYPE_FIELD));
			double d = rec.getValue(SUM_FIELD).doubleValue();
			if (d >= 0) {
				table.put( AonStringUtils.defaultIfBlank(rec.getValue(workplace), UNKNOWN)
						, AMOUNT_LABEL
						, d);
			}
		});
	}
	
	@Override
	public void visitAbcDirectSalesTitular() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> registryId = DSL.field("registryId", Integer.class);
		final Field<String> registry = DSL.field("registry", String.class);
		
		ctx.getDslContext().select(TYPE_FIELD,registryId,registry,SUM_FIELD)
			.from(
				getInvoiceSelect(INVOICE_TYPE_FIELD,INVOICE.REGISTRY.as(registryId), REGISTRY.NAME.as(registry) , invSum)
					.innerJoin(REGISTRY).on(REGISTRY.ID.eq(INVOICE.REGISTRY))
				.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD,INVOICE.REGISTRY)
			.unionAll(
				getDeliverySelect(SALES_TYPE_FIELD,DELIVERY.CUSTOMER.as(registryId), REGISTRY.NAME.as(registry) , delSum)
					.innerJoin(REGISTRY).on(REGISTRY.ID.eq(DELIVERY.CUSTOMER))
				.where( getDeliveryCondition())
				.groupBy(DELIVERY.CUSTOMER))
			.unionAll(
				getIncomeSelect(PURCHASE_TYPE_FIELD,INCOME.SUPPLIER.as(registryId), REGISTRY.NAME.as(registry) , incSum)
					.innerJoin(REGISTRY).on(REGISTRY.ID.eq(INCOME.SUPPLIER))
				.where( getIncomeCondition())
				.groupBy(INCOME.WORKPLACE))
			)
			.groupBy(TYPE_FIELD,registryId)
			.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
			.fetch()
			.stream()
			.forEach(rec -> {
				double d = rec.getValue(SUM_FIELD).doubleValue();
				if (d >= 0) {
					table.put(rec.getValue(registry)
							, AMOUNT_LABEL
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcDirectSalesTitularAddress() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> registryId = DSL.field("registryId", Integer.class);
		final Field<String> registry = DSL.field("registry", String.class);
		final Field<Integer> addressId = DSL.field("addressId", Integer.class);
		final Field<String> addressAlias = DSL.field("addressAlias", String.class);
		final Field<String> address = DSL.field("address", String.class);
		
		ctx.getDslContext().select(TYPE_FIELD,registryId,registry,addressId,addressAlias,address,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,INVOICE.REGISTRY.as(registryId), INVOICE.RNAME.as(registry)
				, INVOICE.RADDRESS.as(addressId)
				, RADDRESS.ALIAS.as(addressAlias), RADDRESS.ADDRESS.as(address), invSum)
				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(INVOICE.RADDRESS))
				.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD, INVOICE.REGISTRY, INVOICE.RADDRESS)
		.unionAll(
			getDeliverySelect(SALES_TYPE_FIELD,DELIVERY.CUSTOMER.as(registryId), REGISTRY.NAME.as(registry)
				, DELIVERY.ADDRESS.as(addressId)
				, RADDRESS.ALIAS.as(addressAlias), RADDRESS.ADDRESS.as(address), delSum)
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(DELIVERY.CUSTOMER))
				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(DELIVERY.ADDRESS))
				.where( getDeliveryCondition())
				.groupBy(DELIVERY.CUSTOMER,DELIVERY.ADDRESS))
		.unionAll(
			getIncomeSelect(PURCHASE_TYPE_FIELD,INCOME.SUPPLIER.as(registryId), REGISTRY.NAME.as(registry)
				, INCOME.ADDRESS.as(addressId)
				, RADDRESS.ALIAS.as(addressAlias), RADDRESS.ADDRESS.as(address), incSum)
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(INCOME.SUPPLIER))
				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(INCOME.ADDRESS))
				.where( getIncomeCondition())
				.groupBy(INCOME.SUPPLIER,INCOME.ADDRESS))
		)
		.groupBy(TYPE_FIELD,registryId,addressId)
		.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
		.fetch()
		.stream()
		.forEach(rec -> {
			double d = rec.getValue(SUM_FIELD).doubleValue();
			if (d >= 0) {
				String name = rec.getValue(addressAlias);
				if ( AonStringUtils.isBlank(name)) {
					name = rec.getValue(address);	
				}
				if ( AonStringUtils.isBlank(name)) {
					name = rec.getValue(registry); 
				} else {
					name = rec.getValue(registry) + " [" + name + "]"; 
				}
				table.put(name ,AMOUNT_LABEL, d);
			}
		});
	}

//	@Override
//	public void visitAbcDirectSalesSeller() {
//	}
	
	@Override
	public void visitAbcDirectSalesProduct() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> productId = DSL.field("productId", Integer.class);
		final Field<String> product = DSL.field("product", String.class);

		ctx.getDslContext().select(TYPE_FIELD,productId,product,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,PRODUCT.ID.as(productId),PRODUCT.NAME.as(product), invSum)
				.where( getInvoiceCondition())
			.groupBy(INVOICE_TYPE_FIELD,PRODUCT.ID)
		.unionAll(
			getDeliverySelect(SALES_TYPE_FIELD,PRODUCT.ID.as(productId),PRODUCT.NAME.as(product), delSum)
				.where( getDeliveryCondition())
			.groupBy(PRODUCT.ID))
		.unionAll(
			getIncomeSelect(PURCHASE_TYPE_FIELD,PRODUCT.ID.as(productId),PRODUCT.NAME.as(product), incSum)
				.where( getIncomeCondition())
			.groupBy(PRODUCT.ID))
		)
		.groupBy(TYPE_FIELD,productId)
		.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
		.fetch()
		.stream()
		.forEach(rec -> {
			double d = rec.getValue(SUM_FIELD).doubleValue();
			if (d >= 0) {
				table.put(AonStringUtils.defaultIfBlank(rec.getValue(product), UNKNOWN), AMOUNT_LABEL, d);
			}
		});
	}
	
	@Override
	public void visitAbcDirectSalesCategory() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> categoryId = DSL.field("categoryId", Integer.class);
		final Field<String> category = DSL.field("category", String.class);
		
		ctx.getDslContext().select(TYPE_FIELD,categoryId,category,SUM_FIELD)
			.from(
				getInvoiceSelect(INVOICE_TYPE_FIELD,PCATEGORY.ID.as(categoryId),PCATEGORY.NAME.as(category), invSum)
					.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
					.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD,PCATEGORY.ID)
			.unionAll(
				getDeliverySelect(SALES_TYPE_FIELD,PCATEGORY.ID.as(categoryId),PCATEGORY.NAME.as(category), delSum)
					.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
					.where( getDeliveryCondition())
				.groupBy(PCATEGORY.ID))
			.unionAll(
				getIncomeSelect(PURCHASE_TYPE_FIELD,PCATEGORY.ID.as(categoryId),PCATEGORY.NAME.as(category), incSum)
					.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
					.where( getIncomeCondition())
				.groupBy(PCATEGORY.ID))
			)
			.groupBy(TYPE_FIELD,categoryId)
			.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
			.fetch()
			.stream()
			.forEach(rec -> {
				double d = rec.getValue(SUM_FIELD).doubleValue();
				if (d >= 0) {
					table.put(AonStringUtils.defaultIfBlank(rec.getValue(category), UNKNOWN), AMOUNT_LABEL, d);
				}
			});
	}
	
	@Override
	public void visitAbcProductBrand() {
		final Field<BigDecimal> invSum = getInvoiceSelectField(params).as("sum_field");
		final Field<BigDecimal> delSum = getDeliverySelectField(params).as("sum_field");
		final Field<BigDecimal> incSum = getIncomeSelectField(params).as("sum_field");
		final Field<Integer> brandId = DSL.field("brandId", Integer.class);
		final Field<String> brand = DSL.field("brand", String.class);
		
		ctx.getDslContext().select(TYPE_FIELD,brandId,brand,SUM_FIELD)
		.from(
			getInvoiceSelect(INVOICE_TYPE_FIELD,BRAND.ID.as(brandId),BRAND.NAME.as(brand), invSum)
				.leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
				.where( getInvoiceCondition())
				.groupBy(INVOICE_TYPE_FIELD,BRAND.ID)
		.unionAll(
			getDeliverySelect(SALES_TYPE_FIELD,BRAND.ID.as(brandId),BRAND.NAME.as(brand), delSum)
				.leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
				.where( getDeliveryCondition())
			.groupBy(BRAND.ID))
		.unionAll(
			getIncomeSelect(PURCHASE_TYPE_FIELD,BRAND.ID.as(brandId),BRAND.NAME.as(brand), incSum)
				.leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
				.where( getIncomeCondition())
			.groupBy(BRAND.ID))
		)
		.groupBy(TYPE_FIELD,brandId)
		.orderBy(TYPE_FIELD.asc(),SUM_FIELD.desc())
		.fetch()
		.stream()
		.forEach(rec -> {
			table.put(AonStringUtils.defaultIfBlank(rec.getValue(brand), UNKNOWN), AMOUNT_LABEL, rec.getValue(SUM_FIELD).doubleValue());
		});
	}

	private AggregateFunction<BigDecimal> getInvoiceSelectField(final StatParams params) {
		return params.isViewAmounts()
				?DSL.sum(INVOICE_DETAIL.QUANTITY)
				:DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
	}

	private AggregateFunction<BigDecimal> getDeliverySelectField(final StatParams params) {
		return params.isViewAmounts()
				?DSL.sum(DELIVERY_DETAIL.QUANTITY)
				:DSL.sum(DELIVERY_DETAIL.QUANTITY.mul(DELIVERY_DETAIL.PRICE));
	}
	private Condition getDeliveryCondition() {
		Condition c = DELIVERY.DOMAIN.eq(this.ctx.getDomainId());
//		c = c.and(DELIVERY.STATUS.eq(DeliveryStatus.PENDING.value()));
		if (params.isSalesSelected()) {
			c = c.and(SecurityDAO.getUserScopesCondition(this.ctx, DELIVERY.SCOPE));
			c = c.and(SecurityDAO.getSecurityLevelCondition(this.ctx, this.ctx.getUser(), DELIVERY.SECURITY_LEVEL));
			if (this.params.getFrom() != null) {
				c = c.and(DELIVERY.ISSUE_TIME.ge(AonDateUtils.toTimestamp(this.params.getFrom())));
			}
			if (this.params.getTo() != null) {
				c = c.and(DELIVERY.ISSUE_TIME.le(AonDateUtils.toTimestamp(this.params.getTo())));
			}
			if (this.params.getRegistry() != null) {
				c = c.and(DELIVERY.CUSTOMER.eq(this.params.getRegistry()));
			}
			if (this.params.getProduct() != null) {
				c = c.and(PRODUCT.ID.eq(this.params.getProduct()));
			}
			// Se ignoran los suplidos.
			c = c.and(PRODUCT.TYPE.isNull().or(PRODUCT.TYPE.ne(ProductType.PREPAYMENT.value())));
			// -----------------------
			StatDAOInvoiceFilterItemVisitor visitor = new StatDAODeliveryFilterItemVisitor();		
			for (StatFilterItem item : this.params.getFilterItems() ) {
				item.getType().visit(visitor,item);
			}
			c = visitor.appendCondition(c);
		} else {
			c = c.and(DSL.falseCondition());	
		}
		return c;
	}

	private AggregateFunction<BigDecimal> getIncomeSelectField(final StatParams params) {
		return params.isViewAmounts()
				?DSL.sum(INCOME_DETAIL.QUANTITY)
						:DSL.sum(INCOME_DETAIL.QUANTITY.mul(INCOME_DETAIL.PRICE));
	}
	private Condition getIncomeCondition() {
		Condition c = INCOME.DOMAIN.eq(this.ctx.getDomainId());
		if (params.isPurchaseSelected()) {
			c = c.and(SecurityDAO.getUserScopesCondition(this.ctx, INCOME.SCOPE));
			c = c.and(SecurityDAO.getSecurityLevelCondition(this.ctx, this.ctx.getUser(), INCOME.SECURITY_LEVEL));
			if (this.params.getFrom() != null) {
				c = c.and(INCOME.ISSUE_TIME.ge(AonDateUtils.toSql(this.params.getFrom())));
			}
			if (this.params.getTo() != null) {
				c = c.and(INCOME.ISSUE_TIME.le(AonDateUtils.toSql(this.params.getTo())));
			}
			if (this.params.getRegistry() != null) {
				c = c.and(INCOME.SUPPLIER.eq(this.params.getRegistry()));
			}
			if (this.params.getProduct() != null) {
				c = c.and(PRODUCT.ID.eq(this.params.getProduct()));
			}
			// Se ignoran los suplidos.
			c = c.and(PRODUCT.TYPE.isNull().or(PRODUCT.TYPE.ne(ProductType.PREPAYMENT.value())));
			// -----------------------
			StatDAOInvoiceFilterItemVisitor visitor = new StatDAODeliveryFilterItemVisitor();		
			for (StatFilterItem item : this.params.getFilterItems() ) {
				item.getType().visit(visitor,item);
			}
			c = visitor.appendCondition(c);
		} else {
			c = c.and(DSL.falseCondition());	
		}
		return c;
	}
	
	
	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private Condition getInvoiceCondition() {
		Condition c = INVOICE.DOMAIN.eq(this.ctx.getDomainId());
		Condition typeCondition = null;
		if (params.isSalesSelected()) {
			typeCondition = INVOICE.TYPE.eq(InvoiceType.SALES.value()); 	
		}
		if (params.isPurchaseSelected()) {
			Condition pCondition = INVOICE.TYPE.eq(InvoiceType.PURCHASE.value());
			typeCondition = (typeCondition == null) ? pCondition : typeCondition.or(pCondition);
		}
		c = c.and(typeCondition);
		c = c.and(
			INVOICE_DETAIL.SOURCE.eq(InvoiceSource.DIRECT_INVOICE.value())
		);
		c = c.and(SecurityDAO.getUserScopesCondition(this.ctx, INVOICE.SCOPE));
		c = c.and(SecurityDAO.getSecurityLevelCondition(this.ctx, this.ctx.getUser(), INVOICE.SECURITY_LEVEL));
		if (this.params.getFrom() != null) {
			c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(this.params.getFrom())));
			periodLabel = FMT.format(this.params.getFrom());
		}
		if (this.params.getTo() != null) {
			c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(this.params.getTo())));
			periodLabel = periodLabel + " al " +  FMT.format(this.params.getTo());
		}
		if (this.params.getRegistry() != null) {
			c = c.and(INVOICE.REGISTRY.eq(this.params.getRegistry()));
		}
		if (this.params.getProduct() != null) {
			c = c.and(PRODUCT.ID.eq(this.params.getProduct()));
		}
		// Se ignoran los suplidos.
		c = c.and(PRODUCT.TYPE.isNull().or(PRODUCT.TYPE.ne(ProductType.PREPAYMENT.value())));
		// -----------------------
		StatDAOInvoiceFilterItemVisitor visitor = new StatDAOInvoiceFilterItemVisitor();		
		for (StatFilterItem item : this.params.getFilterItems() ) {
			item.getType().visit(visitor,item);
		}
		c = visitor.appendCondition(c);
		return c;
	}
	
	
	private static class StatDAOInvoiceFilterItemVisitor implements IStatFilterItemVisitor {
		
		protected Condition productCategoriesCondition = null;
		protected Condition productBrandsCondition = null;
		protected Condition productTagCondition = null;
		protected Condition invoiceTypeCondition = null;
		protected Condition workplaceCondition = null;
		protected Condition sellerCondition = null;
		protected Condition segmentCondition = null;

		@Override
		public void visitWorkplaceCondition(StatFilterItem item) {
			if (item.isSelected() ) {
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
		
		@Override
		public void visitProductCategoryCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.PRODUCT_CATEGORY) {
					int productCategoryId = AonNumberUtils.toInteger( item.getId());
					if (productCategoriesCondition == null) {
						productCategoriesCondition = PRODUCT.CATEGORY.eq( productCategoryId );
					} else {
						productCategoriesCondition = productCategoriesCondition.or(PRODUCT.CATEGORY.eq(productCategoryId));
					}
				}
			}
		}
		@Override
		public void visitProductBrandCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.PRODUCT_BRAND) {
					int productBrandId = AonNumberUtils.toInteger( item.getId());
					if (productBrandsCondition == null) {
						productBrandsCondition = PRODUCT.BRAND.eq( productBrandId );
					} else {
						productBrandsCondition = productBrandsCondition.or(PRODUCT.BRAND.eq(productBrandId));
					}
				}
			}
		}
		
		@Override
		public void visitProductTagCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.PRODUCT_TAG) {
					int productTagId = AonNumberUtils.toInteger( item.getId());
					if (productTagCondition == null) {
						productTagCondition = PRODUCT_TAG.TAG.eq( productTagId );
					} else {
						productTagCondition = productTagCondition.or(PRODUCT_TAG.TAG.eq(productTagId));
					}
				}
			}
		}

		@Override
		public void visitInvoiceTypeCondition(StatFilterItem item) {
			// Noithing only Sales.
		}
		
		@Override
		public void visitSellerCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.SELLER) {
					int sellerId = AonNumberUtils.toInteger( item.getId());
					if (sellerCondition == null) {
						sellerCondition = INVOICE_DETAIL.SELLER.eq( sellerId );
					} else {
						sellerCondition = sellerCondition.or(INVOICE_DETAIL.SELLER.eq(sellerId));
					}
				}
			}
		}
		
		@Override
		public void visitSegmentCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.SEGMENT) {
					int sellerId = AonNumberUtils.toInteger( item.getId());
					if (segmentCondition == null) {
						segmentCondition = RSEGMENT.SEGMENT.eq( sellerId );
					} else {
						segmentCondition = segmentCondition.or(RSEGMENT.SEGMENT.eq(sellerId));
					}
				}
			}
		}

		public Condition appendCondition(Condition condition) {
			if (invoiceTypeCondition != null) {
				condition = condition.and(invoiceTypeCondition);
			}
			if (productCategoriesCondition != null) {
				condition = condition.and(productCategoriesCondition);
			}
			if (productTagCondition != null) {
				condition = condition.and(productTagCondition);
			}
			if (workplaceCondition != null) {
				condition = condition.and(workplaceCondition);
			}
			if (sellerCondition != null) {
				condition = condition.and(sellerCondition);
			}
			if (segmentCondition != null) {
				condition = condition.and(segmentCondition);
			}
			return condition;
		}

	}
	

	private static class StatDAODeliveryFilterItemVisitor extends StatDAOInvoiceFilterItemVisitor {
		
		@Override
		public void visitWorkplaceCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.WORKPLACE) {
					int workplaceId = AonNumberUtils.toInteger( item.getId());
					if (workplaceCondition == null) {
						workplaceCondition = DELIVERY.WORKPLACE.eq( workplaceId );
					} else {
						workplaceCondition = workplaceCondition.or(DELIVERY.WORKPLACE.eq(workplaceId));
					}
				}
			}
		}
		
		@Override
		public void visitSellerCondition(StatFilterItem item) {
			// Nothing. Delivery has no seller.
		}
		
	}
}
