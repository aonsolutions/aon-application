package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectField;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.IStatFilterItemVisitor;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType.IStatTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.fee.FeeChartType;
import com.esferalia.aon.occam.api.model.stat.fee.IFeeChartTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.invoice.IInvoiceChartTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.occam.api.model.stat.task.ITaskChartTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.task.TaskChartType;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IncomePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PurchasePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO.SalesPropertiesDAO;
import com.esferalia.aon.watson.AonDayOfWeek;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class StatDAO {
	private static final String RESULT = "Resultado";
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yy");
	protected static final String UNKNOWN = "Desconocido"; 

	private static final String CATEGORY_STR = "category";
	private static final String CUSTOMER_STR = "customer";
	private static final String SELLER_STR = "seller";
	private static final String WORKPLACE_STR = "workplace";
	private static final String PERIOD_STR = "period";
	
	public static StatParams createStatParams(AONContext ctx) {
		StatParams params = new StatParams();
		params.setDomain(ctx.getDomainId());
		params.setChartType(InvoiceChartType.INVOICE_TYPE_BY_MONTHS_COMBO_CHART.value());
		// Se entra con fecha hasta igual a hoy y fecha desde trece meses menos.
		Date today = new Date();
		params.setTo(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.MONTH, -13);
		params.setFrom(c.getTime());
		
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
		
		ProductDAO.getBrandStream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
			.forEach(brand -> params.getFilterItems().add(
				new StatFilterItem().setId(AonNumberUtils.toString(brand.getId()))
				.setLabel(brand.getName())
				.setType(StatFilterType.PRODUCT_BRAND)));
		
		ProductDAO.getTags(ctx).forEach( tag -> 
		 	params.getFilterItems().add(new StatFilterItem()
		 			.setId(AonNumberUtils.toString(tag.getId()))
					.setLabel(tag.getName())
					.setType(StatFilterType.PRODUCT_TAG))); 

		RegistryDAO.getSegments(ctx).forEach( seg -> 
	 		params.getFilterItems().add(new StatFilterItem()
	 				.setId(AonNumberUtils.toString(seg.getId()))
	 				.setLabel(seg.getName() + " ("+seg.getId()+")")
	 				.setType(StatFilterType.SEGMENT)));
		
		for(Workplace wp : WorkplaceDAO.getWorkplaceList(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))){
			params.getFilterItems().add(
			new StatFilterItem()
				.setId(wp.getId())
				.setLabel(wp.getDescription())
				.setType(StatFilterType.WORKPLACE));
		}
		
		RegistryDAO.getSellers(ctx).forEach(
				seller -> params.getFilterItems().add(new StatFilterItem()
							.setId(seller.getId())
							.setLabel(seller.getRegistryName())
							.setType(StatFilterType.SELLER))
						);
		params.setFilterMap(new HashMap<String, String[]>());
		return params;
	}
	
	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private static  Condition getInvoiceCondition(AONContext ctx, StatParams params) {
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
			item.getType().visit(visitor,item);
		}
		c = visitor.appendCondition(c);
		return c;
	}
	
	private static  Condition getTaskCondition(AONContext ctx, StatParams params) {
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
	
	public static Stream<Task> getStatTaskStream(AONContext ctx, StatParams params){
		return ctx.getDslContext().select()
			.from(TASK)
			.where(getTaskCondition(ctx, params))
			.orderBy(TASK.START_DATE.desc())
			.fetchInto(TASK)
			.stream()
			.map(new TaskDAO.FullTaskFiller());
	}
	
	private static AggregateFunction<BigDecimal> getInvoiceSelectField(final StatParams params) {
		return params.isViewAmounts()
				?DSL.sum(INVOICE_DETAIL.QUANTITY)
				:DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
	}
	
	public static StatData<String, String, Double> getStatData(final AONContext ctx, final StatParams params){
		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		
		params.getStatType().visit( new IStatTypeVisitor() {

			// --------------------------------------------------- INVOICE
			@Override
			public void visitInvoice() {
				InvoiceChartType.values()[params.getChartType()].visit(new IInvoiceChartTypeVisitor() {
					
					@Override
					public void visitInvoiceTypeByYearComboChart() {
						final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
						final AggregateFunction<BigDecimal> sum = getInvoiceSelectField(params);
						getSelect(ctx,year, INVOICE.TYPE, sum)
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
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
							.where( StatDAO.getInvoiceCondition(ctx, params))
							.groupBy(BRAND.ID, INVOICE.TYPE)
							.orderBy(sum.desc())
							.fetch().stream().forEach(rec -> {
								InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
								table.put(AonStringUtils.defaultIfBlank(rec.getValue(BRAND.NAME), UNKNOWN)
										, type.getDescription()
										, rec.getValue(sum).doubleValue());
							});
					}
					
				});
			}

			// --------------------------------------------------- TASK
			@Override
			public void visitTask() {
				TaskChartType.values()[params.getChartType()].visit(new ITaskChartTypeVisitor() {
					
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
				});
			}
			
			// --------------------------------------------------- FEE
			@Override
			public void visitFee() {
				FeeChartType.values()[params.getChartType()].visit(new IFeeChartTypeVisitor() {
					
					@Override
					public void visitFeeType() {
						Date from = AonDateUtils.getDate(AonDateUtils.getYear(params.getFrom()),
								AonDateUtils.getMonth(params.getFrom()), 1);
						Date to = AonDateUtils.addDays(AonDateUtils.addYears(from, 1), -1);
						HashMap<String, Double> map = new HashMap<String, Double>();
						
						LinkedList<Fee> list = FeeDAO.getFeeStream(ctx, f -> getFeeFilter(from, to, ctx.getDomainId(), params.getFilterMap(), f))
								.collect(Collectors.toCollection(LinkedList::new));
						
						for(Integer i = 0; i < 12 ; i++){
							Date date = AonDateUtils.addMonths(from, i);
							String monthKey = AonDateUtils.getYear(date)+ "/" 
									+ (AonDateUtils.getMonth(date) < 9 ? "0" : "") 
									+(AonDateUtils.getMonth(date)+ 1);	
							Double d = list.stream()
							.filter(r -> {
								int fromMonth = AonDateUtils.getMonth(date) + 1;
								int billingMonth = AonDateUtils.getMonth(r.getBillingDate()) +1;
								int period = BillingPeriod.values()[r.getPeriod()].getValue();
								Boolean endDate = r.getEndDate() == null || (r.getEndDate() != null && date.compareTo(r.getEndDate()) <= 0);
								if(period == 0) return endDate && fromMonth == billingMonth && 
										AonDateUtils.getYear(r.getBillingDate()) == AonDateUtils.getYear(date);
								return endDate && r.getBillingDate().compareTo(date) <= 0 
										&& (fromMonth % period) == (billingMonth % period);
							})
							.mapToDouble(r -> r.getPrice() * r.getQuantity() * (r.getDiscount() != null ? ((100 - r.getDiscount())/100) :1.0)
								* getPercent(r.getStartDate(), r.getEndDate(), date, BillingPeriod.values()[r.getPeriod()].getValue())).sum();						
							map.put(monthKey, d);
						}
						map.keySet().stream().sorted((n1,n2)-> n1.compareTo(n2))
						.forEach(key -> table.put(key, "Cuotas", map.get(key)));
					}
				});
			}
		});
		return table;
	}
	
	public static Filter getFeeFilter(Date from, Date to, Integer domainId,
			HashMap<String, String[]> filterMap, FeeProperties f) {
		Filter filter = f.getFinalDateProperty().ge(AonDateUtils.toSql(from))
				.or(f.getFinalDateProperty().isNull())
			.and(f.getBillingDateProperty().lt(AonDateUtils.toSql(to)))
			.and(f.getDomainProperty().eq(domainId));
		
		if(filterMap.containsKey(CATEGORY_STR)){
			Filter fcategory = f.getCategoryProperty().eq(Integer.parseInt(filterMap.get(CATEGORY_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(CATEGORY_STR).length ; i++){
				fcategory = fcategory.or(f.getCategoryProperty().eq(Integer.parseInt(filterMap.get(CATEGORY_STR)[i])));
			}
			filter = filter.and(fcategory);
		}
		
		if(filterMap.containsKey(CUSTOMER_STR)){
			Filter fcustomer = f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(CUSTOMER_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(CUSTOMER_STR).length ; i++){
				fcustomer = fcustomer.or(f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(CUSTOMER_STR)[i])));
			}
			filter = filter.and(fcustomer);
		}
		
		if(filterMap.containsKey(SELLER_STR)){
			Filter fseller = f.getSellerProperty().eq(Integer.parseInt(filterMap.get(SELLER_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(SELLER_STR).length ; i++){
				fseller = fseller.or(f.getSellerProperty().eq(Integer.parseInt(filterMap.get(SELLER_STR)[i])));
			}
			filter = filter.and(fseller);
		}
		
		if(filterMap.containsKey(WORKPLACE_STR)){
			Filter fworkplace = f.getWorkplaceProperty().eq(Integer.parseInt(filterMap.get(WORKPLACE_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(WORKPLACE_STR).length ; i++){
				fworkplace = fworkplace.or(f.getWorkplaceProperty().eq(Integer.parseInt(filterMap.get(WORKPLACE_STR)[i])));
			}
			filter = filter.and(fworkplace);
		}
		
		if(filterMap.containsKey(PERIOD_STR)){
			Filter fperiod = f.getPeriodProperty().eq((short) Integer.parseInt(filterMap.get(PERIOD_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(PERIOD_STR).length ; i++){
				fperiod = fperiod.or(f.getPeriodProperty().eq((short) Integer.parseInt(filterMap.get(PERIOD_STR)[i])));
			}
			filter = filter.and(fperiod);
		}
		return filter;
	}
	public static Double getPercent(Date startDate, Date endDate, Date date, int period){
		if(period == 0 || (startDate.compareTo(date) < 0 && (endDate == null 
				|| AonDateUtils.addMonths(date, period).compareTo(endDate) <= 0 )))
			return 1.0;
		
		int sMonth = AonDateUtils.getMonth(startDate);
		int sYear = AonDateUtils.getYear(startDate);
		int eMonth = endDate != null ? AonDateUtils.getMonth(endDate) : -1;
		int eYear = endDate != null ? AonDateUtils.getYear(endDate) : -1;
		
		Integer d1 = 0;
		Integer d2 = 0;
		for(Integer i = 0; i < period; i++){
			Date d = AonDateUtils.addMonths(date, i);
			int month = AonDateUtils.getMonth(d);
			int year = AonDateUtils.getYear(d);
			Integer lastDay = AonDateUtils.getDay(AonDateUtils.getMonthLastDay(d));
			Integer start = lastDay;
			Integer end = 0;
			if(sMonth == month && sYear == year)
				start = lastDay - (AonDateUtils.getDay(startDate) - 1);
			if(eMonth == month && eYear == year)
				end = lastDay - AonDateUtils.getDay(endDate);
			
			d1 = d1 + start - end;
			d2 = d2 + lastDay;
		}
		return d1.doubleValue()/d2.doubleValue();
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
	
	public static String getInvoicesReport(AONContext ctx, StatParams params) {
		final Integer[] scopes = SecurityDAO.getUserScopes(ctx);
		final LinkedList<Byte> types = new LinkedList<Byte>();
		final LinkedList<Integer> categories = new LinkedList<Integer>();
		final LinkedList<Integer> tags = new LinkedList<Integer>();
		final LinkedList<Integer> brands = new LinkedList<Integer>();
		final LinkedList<Integer> workplaces = new LinkedList<Integer>();
		final LinkedList<Integer> sellers = new LinkedList<Integer>();
		final LinkedList<Integer> segments = new LinkedList<Integer>();
		
		IStatFilterItemVisitor visitor = new IStatFilterItemVisitor() {

			@Override
			public void visitInvoiceTypeCondition(StatFilterItem item) {
				if (item.isSelected() ) types.add( (byte) InvoiceType.valueOf(item.getId()).ordinal() );
			}

			@Override
			public void visitProductCategoryCondition(StatFilterItem item) {
				if (item.isSelected() ) categories.add( AonNumberUtils.toInteger( item.getId() ));
			}

			@Override
			public void visitProductTagCondition(StatFilterItem item) {
				if (item.isSelected() ) tags.add( AonNumberUtils.toInteger( item.getId() ));
			}

			@Override
			public void visitProductBrandCondition(StatFilterItem item) {
				if (item.isSelected() ) brands.add( AonNumberUtils.toInteger( item.getId() ));
			}

			@Override
			public void visitWorkplaceCondition(StatFilterItem item) {
				if (item.isSelected() ) workplaces.add( AonNumberUtils.toInteger( item.getId() ));
			}

			@Override
			public void visitSellerCondition(StatFilterItem item) {
				if (item.isSelected() ) sellers.add( AonNumberUtils.toInteger( item.getId() ));
			}
			@Override
			public void visitSegmentCondition(StatFilterItem item) {
				if (item.isSelected() ) segments.add( AonNumberUtils.toInteger( item.getId() ));
			}
		};
		
		for (StatFilterItem item : params.getFilterItems() ) {
			item.getType().visit(visitor,item);
		}
		
		return InvoiceFormatter.formatInvoices("LISTADO DE FACTURAS", 
				"(M\u00E1x. 1000 Facturas)",
				InvoiceDAO.getInvoiceDetails(ctx, 
						p -> p.getDomainProperty().eq(ctx.getDomainId())
							.and(scopes==null?null:p.getScopeProperty().in(scopes))							
							.and(params.getFrom()==null?null:p.getStartIssueDateProperty().ge(params.getFrom()))
							.and(params.getTo()==null?null:p.getEndIssueDateProperty().le(params.getTo()))
							.and(params.getRegistry()==null?null:p.getRegistryProperty().eq(params.getRegistry()))
							.and(params.getProduct()==null?null:p.getProductProperty().eq(params.getProduct()))
							.and((types==null||types.size()==0)?null:p.getTypeProperty().in(types.toArray(new Byte[types.size()])))
							.and((categories==null||categories.size()==0)?null:p.getProductCategoryProperty().in(categories.toArray(new Integer[categories.size()])))
							.and((brands==null||brands.size()==0)?null:p.getProductBrandProperty().in(brands.toArray(new Integer[brands.size()])))
							.and((workplaces==null||workplaces.size()==0)?null:p.getWorkplaceProperty().in(workplaces.toArray(new Integer[workplaces.size()])))
							.and((sellers==null||sellers.size()==0)?null:p.getSellerProperty().in(sellers.toArray(new Integer[sellers.size()])))
							.and(p.getProductTypeProperty().ne(ProductType.PREPAYMENT.value()))
					)
					// Filtro de product TAGS
					.filter( det ->  tags.isEmpty() 
							|| det.getItem() == null 
							|| det.getItem().getProduct() == null 
							|| ctx.getDslContext().fetchExists( ctx.getDslContext()
								.select()
								.from(PRODUCT_TAG)
								.where(PRODUCT_TAG.PRODUCT.eq(det.getItem().getProduct().getId()))
								.and(PRODUCT_TAG.TAG.in(tags)))
							)
					// Filtro de Registry Segments
					.filter( det ->  segments.isEmpty() 
							|| ctx.getDslContext().fetchExists( ctx.getDslContext()
								.select()
								.from(RSEGMENT)
								.where(RSEGMENT.REGISTRY.eq(det.getInvoice().getRegistry() )) 
								.and(RSEGMENT.SEGMENT.in(segments)))
							)
					// TODO Implementar mecanismo offset limit. Se capa seguridad de memoria en el servidor.
					.limit(1000)	// Modificar subtitulo 
					// ---------------
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}

	
	/*
	 * PRODUCT STATS
	 */
	public static final String PRODUCT_OUTPUTS = "Salidas";
	public static final String PRODUCT_INPUTS = "Entradas";
	public static final String PRODUCT_STOCK = "Stock";
	public static final String PRODUCT_PENDING_PURCHASES = "Recibir";
	public static final String PRODUCT_PENDING_SALES = "Servir";
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static final DeliveryPropertiesDAO DELIVERY_PROPERTIES = new DeliveryPropertiesDAO();
	private static final IncomePropertiesDAO INCOME_PROPERTIES = new IncomePropertiesDAO();
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();
	private static final SalesPropertiesDAO SALES_PROPERTIES = new SalesPropertiesDAO();
	private static final PurchasePropertiesDAO PURCHASE_PROPERTIES = new PurchasePropertiesDAO();
	
	public static StatData<Integer, String, Double> getProductStat(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter,
			SalesFilter salesFilter, PurchaseFilter purchaseFilter) {
		
		Map<Integer, Double> outputs = getOutputs(ctx, ITEM.PRODUCT, productFilter, itemFilter, invoiceFilter, deliveryFilter);
		Map<Integer, Double> pendingSales = getPendingSales(ctx, ITEM.PRODUCT, productFilter, salesFilter);
		Map<Integer, Double> pendingPurchases = getPendingPurchases(ctx, ITEM.PRODUCT, productFilter, purchaseFilter);
		Map<Integer, Double> stock = getProductStock(ctx, productFilter);
		
		final StatData<Integer, String, Double> stat = new StatData<>();
		for(Integer key: outputs.keySet()) {
			stat.put(key, PRODUCT_OUTPUTS, outputs.get(key));
			stat.put(key, PRODUCT_STOCK, stock.get(key));
			stat.put(key, PRODUCT_PENDING_PURCHASES, pendingPurchases.get(key));
			stat.put(key, PRODUCT_PENDING_SALES, pendingSales.get(key));
		}
		
		return stat;
	}
	
	public static StatData<Integer, String, Double> getWarehouseProductMovements(AONContext ctx,
			ProductFilter productFilter, ItemFilter itemFilter, InvoiceFilter invoiceFilter,
			DeliveryFilter deliveryFilter, IncomeFilter incomeFilter) {

		Map<Integer, Double> outputs = getOutputs(ctx, ITEM.PRODUCT, productFilter, itemFilter, invoiceFilter, deliveryFilter);
		Map<Integer, Double> inputs = getInputs(ctx, ITEM.PRODUCT, productFilter, itemFilter, invoiceFilter, incomeFilter);

		final StatData<Integer, String, Double> stat = new StatData<>();
		for (Integer key : outputs.keySet())
			stat.put(key, PRODUCT_OUTPUTS, outputs.get(key));
		for (Integer key : inputs.keySet())
			stat.put(key, PRODUCT_INPUTS, inputs.get(key));

		return stat;
	}
	
	public static StatData<Integer, String, Double> getWarehouseItemMovements(AONContext ctx,
			ProductFilter productFilter, ItemFilter itemFilter, InvoiceFilter invoiceFilter,
			DeliveryFilter deliveryFilter, IncomeFilter incomeFilter) {

		Map<Integer, Double> outputs = getOutputs(ctx, ITEM.ID, productFilter, itemFilter, invoiceFilter, deliveryFilter);
		Map<Integer, Double> inputs = getInputs(ctx, ITEM.ID, productFilter, itemFilter, invoiceFilter, incomeFilter);

		final StatData<Integer, String, Double> stat = new StatData<>();
		for (Integer key : outputs.keySet())
			stat.put(key, PRODUCT_OUTPUTS, outputs.get(key));
		for (Integer key : inputs.keySet())
			stat.put(key, PRODUCT_INPUTS, inputs.get(key));

		return stat;
	}
	
	private static Map<Integer, Double> getOutputs(AONContext ctx, TableField<ItemRecord, Integer> selectField,
			ProductFilter productFilter, ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter) {
		Collection<Condition> deliveryConditions = new ArrayList<Condition>();
		deliveryConditions.addAll(Arrays.asList(DELIVERY_PROPERTIES.getConditions(deliveryFilter)));
		deliveryConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		deliveryConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> deliverySelect = ctx.getDslContext()
			.select(selectField, DSL.sum(DELIVERY_DETAIL.QUANTITY).as(DELIVERY_DETAIL.QUANTITY))
			.from(DELIVERY)
				.join(DELIVERY_DETAIL).on(DELIVERY.ID.equal(DELIVERY_DETAIL.DELIVERY))
				.join(ITEM).on(DELIVERY_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
			.where(deliveryConditions)
			.groupBy(selectField)
			.orderBy(ITEM.PRODUCT);
		
		Collection<Condition> invoiceConditions = new ArrayList<Condition>();
		invoiceConditions.addAll(Arrays.asList(INVOICE_PROPERTIES.getConditions(invoiceFilter)));
		invoiceConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		invoiceConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> invoiceSelect = ctx.getDslContext()
				.select(selectField, DSL.sum(INVOICE_DETAIL.QUANTITY).as(INVOICE_DETAIL.QUANTITY))
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.join(ITEM).on(INVOICE_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
				.where(invoiceConditions)
				.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
				.and(INVOICE_DETAIL.SOURCE.ne(InvoiceSource.DELIVERY.value()))
				.groupBy(selectField)
				.orderBy(ITEM.PRODUCT);
		
		Map<Integer, Double> map = new HashMap<>();
		deliverySelect
			.union(invoiceSelect)
			.forEach(record -> {
				Integer key = record.get(selectField);
				Double value = record.value2().doubleValue();
				if(!map.containsKey(key))
					map.put(key, 0.0);
				map.put(key, map.get(key)+value);
			});
		return map;
	}
	
	private static Map<Integer, Double> getInputs(AONContext ctx, TableField<ItemRecord, Integer> selectField,
			ProductFilter productFilter, ItemFilter itemFilter, InvoiceFilter invoiceFilter, IncomeFilter incomeFilter) {
		Collection<Condition> deliveryConditions = new ArrayList<Condition>();
		deliveryConditions.addAll(Arrays.asList(INCOME_PROPERTIES.getConditions(incomeFilter)));
		deliveryConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		deliveryConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> deliverySelect = ctx.getDslContext()
			.select(selectField, DSL.sum(INCOME_DETAIL.QUANTITY).as(INCOME_DETAIL.QUANTITY))
			.from(INCOME)
				.join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
				.join(ITEM).on(INCOME_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
			.where(deliveryConditions)
			.groupBy(selectField)
			.orderBy(ITEM.PRODUCT);
		
		Collection<Condition> invoiceConditions = new ArrayList<Condition>();
		invoiceConditions.addAll(Arrays.asList(INVOICE_PROPERTIES.getConditions(invoiceFilter)));
		invoiceConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		invoiceConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> invoiceSelect = ctx.getDslContext()
				.select(selectField, DSL.sum(INVOICE_DETAIL.QUANTITY).as(INVOICE_DETAIL.QUANTITY))
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.join(ITEM).on(INVOICE_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
				.where(invoiceConditions)
				.and(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()))
				.and(INVOICE_DETAIL.SOURCE.ne(InvoiceSource.INCOME.value()))
				.groupBy(selectField)
				.orderBy(ITEM.PRODUCT);
		
		Map<Integer, Double> map = new HashMap<>();
		deliverySelect
			.union(invoiceSelect)
			.forEach(record -> {
				Integer key = record.get(selectField);
				Double value = record.value2().doubleValue();
				if(!map.containsKey(key))
					map.put(key, 0.0);
				map.put(key, map.get(key)+value);
			});
		return map;
	}
	
	private static Map<Integer, Double> getPendingSales(AONContext ctx, TableField<ItemRecord, Integer> selectField,
			ProductFilter productFilter, SalesFilter salesFilter) {
		Collection<Condition> whereConditions = new ArrayList<Condition>();
		whereConditions.addAll(Arrays.asList(SALES_PROPERTIES.getConditions(salesFilter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		
		Map<Integer, Double> map = new HashMap<>();
		ctx.getDslContext()
			.select(selectField, DSL.sum(SALES_DETAIL.QUANTITY).as(SALES_DETAIL.QUANTITY))
			.from(SALES)
				.join(SALES_DETAIL).on(SALES.ID.equal(SALES_DETAIL.SALES))
				.join(ITEM).on(SALES_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
			.where(whereConditions)
			.and(SALES_DETAIL.STATUS.ne(SalesDetailStatus.SETTLED.value()))
			.groupBy(selectField)
			.orderBy(ITEM.PRODUCT)
			.forEach(record -> {
				Integer key = record.get(selectField);
				Double value = record.value2().doubleValue();
				if(!map.containsKey(key))
					map.put(key, 0.0);
				map.put(key, map.get(key)+value);
			});
		return map;
	}
	
	private static Map<Integer, Double> getPendingPurchases(AONContext ctx, TableField<ItemRecord, Integer> selectField,
			ProductFilter productFilter, PurchaseFilter purchaseFilter) {
		Collection<Condition> whereConditions = new ArrayList<Condition>();
		whereConditions.addAll(Arrays.asList(PURCHASE_PROPERTIES.getConditions(purchaseFilter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		
		Map<Integer, Double> map = new HashMap<>();
		ctx.getDslContext()
			.select(selectField, DSL.sum(PURCHASE_DETAIL.QUANTITY).as(PURCHASE_DETAIL.QUANTITY))
			.from(PURCHASE)
				.join(PURCHASE_DETAIL).on(PURCHASE.ID.equal(PURCHASE_DETAIL.PURCHASE))
				.join(ITEM).on(PURCHASE_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
			.where(whereConditions)
			.and(PURCHASE_DETAIL.STATUS.ne(PurchaseDetailStatus.SETTLED.value()))
			.groupBy(selectField)
			.orderBy(ITEM.PRODUCT)
			.forEach(record -> {
				Integer key = record.get(selectField);
				Double value = record.value2().doubleValue();
				if(!map.containsKey(key))
					map.put(key, 0.0);
				map.put(key, map.get(key)+value);
			});
		return map;
	}
	
	private static Map<Integer, Double> getProductStock(AONContext ctx, ProductFilter productFilter) {
		Map<Integer, Double> map = new HashMap<>();
		
		// TODO method::getProductStock
		
		return map;
	}
	
}
