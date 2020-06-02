package com.esferalia.aon.occam.impl.jooq.dao.stat;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
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
import com.esferalia.aon.occam.api.model.stat.invoice.IInvoiceChartTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceChartTypeVisitor implements IInvoiceChartTypeVisitor {
	
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yy");
	private static final String RESULT = "Resultado";
	protected static final String UNKNOWN = "Desconocido"; 

	private AONContext ctx;
	private StatParams params;
	private StatData<String, String, Double> table;	
	
	public InvoiceChartTypeVisitor(AONContext ctx, StatParams params, StatData<String,String,Double> table) {
		this.ctx = ctx;
		this.params = params;
		this.table = table;
	}
	
	@Override
	public void visitInvoiceTypeByYearComboChart() {
		final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,year, INVOICE.TYPE, sum)
			.where( getInvoiceCondition(ctx, params))
			.groupBy(year, INVOICE.TYPE)
			.orderBy(year, DSL.decode()
					   .when(INVOICE.TYPE.equal((byte) 1), 0)
					   .when(INVOICE.TYPE.equal((byte) 0), 1)
					   .when(INVOICE.TYPE.equal((byte) 2), 2)
					   .when(INVOICE.TYPE.equal((byte) 3), 3))				
			.fetch()
			.stream()
			.forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				double amount = rec.getValue(sum).doubleValue();
				String y = AonNumberUtils.toString( rec.getValue(year));
				if (params.isResultVisible()) {
					Double d = table.get(y, RESULT);
					d = AonMathUtils.round( (d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
					table.put(y, RESULT, d);
				}
				table.put(y, type.getDescription(), amount);
			});
	}
	
	private SelectOnConditionStep<Record> getSelect(AONContext ctx, SelectField<?> ... fields) {
		SelectOnConditionStep<Record> sel = ctx.getDslContext()
				.select(fields)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
				.leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				;
			if (params.hasTagFilter()) {
				sel = sel
					.leftOuterJoin(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.eq(PRODUCT.ID));
			}
			if (params.hasSegmentFilter()) {
				sel = sel.leftOuterJoin(RSEGMENT).on(RSEGMENT.REGISTRY.eq(INVOICE.REGISTRY));
			}
		return sel;
	}

	@Override
	public void visitInvoiceTypeByMonthsComboChart() {
		final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
		final Field<Integer> month = DSL.month(INVOICE.ISSUE_DATE);
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx, year,month, INVOICE.TYPE, sum )
			.where( getInvoiceCondition(ctx, params))
		    .groupBy(year,month, INVOICE.TYPE)
			.orderBy(year,month, DSL.decode()
					   .when(INVOICE.TYPE.equal((byte) 1), 0)
					   .when(INVOICE.TYPE.equal((byte) 0), 1)
					   .when(INVOICE.TYPE.equal((byte) 2), 2)
					   .when(INVOICE.TYPE.equal((byte) 3), 3))				
			.fetch()
			.stream()
			.forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				String monthKey = rec.getValue(month)+"/"+rec.getValue(year);
				double amount = rec.getValue(sum).doubleValue();
				if (params.isResultVisible()) {
					Double d = table.get(monthKey, RESULT);
					d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
					table.put(monthKey, RESULT, d);
				}
				table.put(monthKey, type.getDescription(), amount);
			});
	}
	

	@Override
	public void visitInvoiceTypeByWeeksComboChart() {					
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		final Calendar calendar = Calendar.getInstance();
		getSelect(ctx,INVOICE.ISSUE_DATE , INVOICE.TYPE, sum)
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE.ISSUE_DATE, INVOICE.TYPE)
			.orderBy(INVOICE.ISSUE_DATE, DSL.decode()
					   .when(INVOICE.TYPE.equal((byte) 1), 0)
					   .when(INVOICE.TYPE.equal((byte) 0), 1)
					   .when(INVOICE.TYPE.equal((byte) 2), 2)
					   .when(INVOICE.TYPE.equal((byte) 3), 3))				
			.fetch()
			.stream()
			.forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				Date issueDate = rec.getValue(INVOICE.ISSUE_DATE);
				calendar.setTime(issueDate);
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
				String firstDayOfWeek = FMT.format( calendar.getTime() );
				String weekKey = calendar.get(Calendar.WEEK_OF_YEAR) + " ("+firstDayOfWeek+")";
				double amount = rec.getValue(sum).doubleValue();
				if (params.isResultVisible()) {
					Double d = table.get(weekKey, RESULT);
					d = (d == null ? 0.0 : d);
					amount = (amount * (type == InvoiceType.SALES ? 1 : -1));
					d = AonMathUtils.round( d + amount);
					table.put(weekKey, RESULT, d);
				}
				Double acum = table.get(weekKey, type.getDescription());
				acum = AonMathUtils.round( (acum == null ? 0.0 : acum) + amount); 
				table.put(weekKey, type.getDescription(), acum);
			});
	}

	@Override
	public void visitInvoiceTypeByDaysComboChart() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,INVOICE.ISSUE_DATE , INVOICE.TYPE, sum)
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE.ISSUE_DATE, INVOICE.TYPE)
			.orderBy(INVOICE.ISSUE_DATE, DSL.decode()
					   .when(INVOICE.TYPE.equal((byte) 1), 0)
					   .when(INVOICE.TYPE.equal((byte) 0), 1)
					   .when(INVOICE.TYPE.equal((byte) 2), 2)
					   .when(INVOICE.TYPE.equal((byte) 3), 3))				
			.fetch().stream().forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				String date = FMT.format( rec.getValue(INVOICE.ISSUE_DATE));
				double amount = rec.getValue(sum).doubleValue();
				if (params.isResultVisible()) {
					Double dou = table.get(date, RESULT);
					dou = AonMathUtils.round((dou == null ? 0.0 : dou) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
					table.put(date, RESULT, dou);
				}
				table.put(date, type.getDescription(), amount);
			});
	}
	
	@Override
	public void visitGeoProvince() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,GEOZONE.NAME, sum)
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(INVOICE.REGISTRY).and(RADDRESS.TYPE.eq((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
			.where( getInvoiceCondition(ctx, params))
			.groupBy(GEOZONE.NAME)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					table.put( "CHART"
							, AonStringUtils.defaultIfBlank(rec.getValue(GEOZONE.NAME), UNKNOWN)
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcInvoiceWorkplace() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,INVOICE_DETAIL.WORKPLACE,WORKPLACE.DESCRIPTION, INVOICE.TYPE, sum)
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INVOICE_DETAIL.WORKPLACE))
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE_DETAIL.WORKPLACE, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
					table.put( AonStringUtils.defaultIfBlank(rec.getValue(WORKPLACE.DESCRIPTION), UNKNOWN)
							, type.getDescription()
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcInvoiceTitular() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,INVOICE.REGISTRY, INVOICE.RNAME , INVOICE.TYPE, sum)
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE.REGISTRY, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
					table.put(rec.getValue(INVOICE.RNAME)
							, type.getDescription()
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcInvoiceTitularAddress() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,INVOICE.REGISTRY, INVOICE.RNAME , RADDRESS.ALIAS, RADDRESS.ADDRESS, INVOICE.TYPE, sum)
			.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(INVOICE.RADDRESS))						
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE.REGISTRY, INVOICE.RADDRESS, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					String name = rec.getValue(RADDRESS.ALIAS);
					if ( AonStringUtils.isBlank(name)) {
						name = rec.getValue(RADDRESS.ADDRESS);	
					}
					if ( AonStringUtils.isBlank(name)) {
						name = rec.getValue(INVOICE.RNAME); 
					} else {
						name = rec.getValue(INVOICE.RNAME) + " [" + name + "]"; 
					}
					InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
					table.put(name , type.getDescription(), d);
				}
			});
	}

	@Override
	public void visitAbcInvoiceSeller() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,INVOICE_DETAIL.SELLER,
				DSL.nvl(REGISTRY.NAME, UNKNOWN)
				, INVOICE.TYPE, sum)
			.leftOuterJoin(REGISTRY).on(INVOICE_DETAIL.SELLER.eq(REGISTRY.ID))
			.where( getInvoiceCondition(ctx, params))
			.groupBy(INVOICE_DETAIL.SELLER, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
					table.put(rec.getValue(DSL.nvl(REGISTRY.NAME, UNKNOWN))
							, type.getDescription()
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcInvoiceProduct() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,PRODUCT.ID,PRODUCT.NAME, INVOICE.TYPE, sum)
			.where( getInvoiceCondition(ctx, params))
			.groupBy(PRODUCT.ID, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				double d = rec.getValue(sum).doubleValue();
				if (d >= 0) {
					InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
					table.put(AonStringUtils.defaultIfBlank(rec.getValue(PRODUCT.NAME), UNKNOWN)
							, type.getDescription()
							, d);
				}
			});
	}
	
	@Override
	public void visitAbcInvoiceCategory() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,PCATEGORY.ID,PCATEGORY.NAME, INVOICE.TYPE, sum)
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
			.where( getInvoiceCondition(ctx, params))
			.groupBy(PCATEGORY.ID, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				table.put(AonStringUtils.defaultIfBlank(rec.getValue(PCATEGORY.NAME), UNKNOWN)
						, type.getDescription()
						, rec.getValue(sum).doubleValue());
			});
	}
	
	@Override
	public void visitAbcProductBrand() {
		final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
		getSelect(ctx,BRAND.ID,BRAND.NAME, INVOICE.TYPE, sum)
			.leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
			.where( getInvoiceCondition(ctx, params))
			.groupBy(BRAND.ID, INVOICE.TYPE)
			.orderBy(sum.desc())
			.fetch().stream().forEach(rec -> {
				InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
				table.put(AonStringUtils.defaultIfBlank(rec.getValue(BRAND.NAME), UNKNOWN)
						, type.getDescription()
						, rec.getValue(sum).doubleValue());
			});
	}

	private AggregateFunction<BigDecimal> getInvoiceSelectField(final StatParams params) {
		return params.isViewAmounts()
				?DSL.sum(INVOICE_DETAIL.QUANTITY)
				:DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
	}

	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private Condition getInvoiceCondition(AONContext ctx, StatParams params) {
		Condition c = INVOICE.DOMAIN.eq(ctx.getDomainId());
		c = c.and(SecurityDAO.getUserScopesCondition(ctx, INVOICE.SCOPE));
		c = c.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), INVOICE.SECURITY_LEVEL));		
		if (params.getFrom() != null) {
			c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(params.getFrom())));
		}
		if (params.getTo() != null) {
			c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(params.getTo())));
		}
		if (params.getRegistry() != null) {
			c = c.and(INVOICE.REGISTRY.eq(params.getRegistry()));
		}
		if (params.getProduct() != null) {
			c = c.and(PRODUCT.ID.eq(params.getProduct()));
		}
		// Se ignoran los suplidos.
		c = c.and(PRODUCT.TYPE.isNull().or(PRODUCT.TYPE.ne(ProductType.PREPAYMENT.value())));
		// -----------------------
		StatDAOInvoiceFilterItemVisitor visitor = new StatDAOInvoiceFilterItemVisitor();		
		for (StatFilterItem item : params.getFilterItems() ) {
			if (item.isSelected()) {
				System.out.println("TRUE");
			}
			item.getType().visit(visitor,item);
		}
		c = visitor.appendCondition(c);
		return c;
	}
	
	
	private static class StatDAOInvoiceFilterItemVisitor implements IStatFilterItemVisitor {
		
		private Condition productCategoriesCondition = null;
		private Condition productBrandsCondition = null;
		private Condition productTagCondition = null;
		private Condition invoiceTypeCondition = null;
		private Condition workplaceCondition = null;
		private Condition sellerCondition = null;
		private Condition segmentCondition = null;

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
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.INVOICE_TYPE) {
					byte invoiceType = (byte) InvoiceType.valueOf(item.getId()).ordinal();
					if (invoiceTypeCondition == null) {
						invoiceTypeCondition = INVOICE.TYPE.eq(invoiceType);
					} else {
						invoiceTypeCondition = invoiceTypeCondition.or(INVOICE.TYPE.eq(invoiceType));
					}
				}
			}
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
			if (productBrandsCondition != null) {
				condition = condition.and(productBrandsCondition);
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
	
}
