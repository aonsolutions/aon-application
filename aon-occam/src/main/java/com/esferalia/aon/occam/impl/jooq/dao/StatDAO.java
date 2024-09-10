package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Elaboration.ELABORATION;
import static com.esferalia.aon.jooq.tables.ElaborationDetail.ELABORATION_DETAIL;
import static com.esferalia.aon.jooq.tables.ElaborationDetailComposition.ELABORATION_DETAIL_COMPOSITION;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Task.TASK;

import java.math.BigDecimal;
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

import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.SelectSeekStep1;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.OldTask;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.IStatFilterItemVisitor;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType.IStatTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.fee.FeeChartType;
import com.esferalia.aon.occam.api.model.stat.invoice.DirectSalesChartType;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.occam.api.model.stat.task.TaskChartType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO.DeliveryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO.ElaborationPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO.FinancePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IncomePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PurchasePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO.SalesPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.stat.DirectSalesChartTypeVisitor;
import com.esferalia.aon.occam.impl.jooq.dao.stat.FeeChartTypeVisitor;
import com.esferalia.aon.occam.impl.jooq.dao.stat.InvoiceChartTypeVisitor;
import com.esferalia.aon.occam.impl.jooq.dao.stat.TaskChartTypeVisitor;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class StatDAO {

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
		for (ProductCategory pc : ProductOldDAO.getProductCategories(ctx)) {
			params.getFilterItems().add(
					new StatFilterItem().setId(AonNumberUtils.toString(pc.getId()))
					.setLabel(pc.getName())
					.setType(StatFilterType.PRODUCT_CATEGORY));
		}
		
		BrandDAO.getStream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
			.forEach(brand -> params.getFilterItems().add(
				new StatFilterItem().setId(AonNumberUtils.toString(brand.getId()))
				.setLabel(brand.getName())
				.setType(StatFilterType.PRODUCT_BRAND)));
		
		ProductOldDAO.getTags(ctx).forEach( tag -> 
		 	params.getFilterItems().add(new StatFilterItem()
		 			.setId(AonNumberUtils.toString(tag.getId()))
					.setLabel(tag.getName())
					.setType(StatFilterType.PRODUCT_TAG))); 

		RegistryOldDAO.getSegments(ctx).forEach( seg -> 
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
		
		SellerDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()))
			.sorted((a, b) -> a.getName().compareTo(b.getName()))
			.forEach(seller -> 
				params.getFilterItems().add(new StatFilterItem()
					.setId(seller.getId())
					.setLabel(seller.getName())
					.setType(StatFilterType.SELLER)));
		params.setFilterMap(new HashMap<String, String[]>());
		return params;
	}
	
	
	public static Stream<OldTask> getStatTaskStream(AONContext ctx, StatParams params){
		return ctx.getDslContext().select()
			.from(TASK)
			.where(TaskChartTypeVisitor.getTaskCondition(ctx, params))
			.orderBy(TASK.START_DATE.desc())
			.fetchInto(TASK)
			.stream()
			.map(new TaskOldDAO.FullTaskFiller());
	}
	
	public static StatData<String, String, Double> getStatData(final AONContext ctx, final StatParams params){
		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		
		params.getStatType().visit( new IStatTypeVisitor() {

			// --------------------------------------------------- INVOICE
			@Override
			public void visitInvoice() {
				InvoiceChartType.values()[params.getChartType()].visit(new InvoiceChartTypeVisitor(ctx,params,table));
			}

			// --------------------------------------------------- DELIVERY / DIRECT SALES
			@Override
			public void visitDirectSales() {
				DirectSalesChartType.values()[params.getChartType()].visit(new DirectSalesChartTypeVisitor(ctx,params,table));
			}
			
			// --------------------------------------------------- TASK
			@Override
			public void visitTask() {
				TaskChartType.values()[params.getChartType()].visit(new TaskChartTypeVisitor(ctx,params,table) );
			}
			
			// --------------------------------------------------- FEE
			@Override
			public void visitFee() {
				FeeChartType.values()[params.getChartType()].visit(new FeeChartTypeVisitor(ctx,params,table));
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

	
	public static String getInvoicesReport(AONContext ctx, StatParams params, IDAOCallback callback) {
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
		Stream<InvoiceDetail> stream = InvoiceOLDDAO.getInvoiceDetailsExtended(ctx, 
				p -> p.getDomainProperty().eq(ctx.getDomainId())
				.and(scopes==null?null:p.getScopeProperty().in(scopes))							
				.and(params.getFrom()==null?null:p.getStartIssueDateProperty().ge(params.getFrom()))
				.and(params.getTo()==null?null:p.getEndIssueDateProperty().le(params.getTo()))
				.and(params.getRegistry()==null?null:p.getRegistryProperty().eq(params.getRegistry()))
				.and(params.getProduct()==null?null:p.getProductProperty().eq(params.getProduct()))
				.and((types==null||types.isEmpty())?null:p.getTypeProperty().in(types.toArray(new Byte[types.size()])))
				.and((categories==null||categories.isEmpty())?null:p.getProductCategoryProperty().in(categories.toArray(new Integer[categories.size()])))
				.and((brands==null||brands.isEmpty())?null:p.getProductBrandProperty().in(brands.toArray(new Integer[brands.size()])))
				.and((workplaces==null||workplaces.isEmpty())?null:p.getWorkplaceProperty().in(workplaces.toArray(new Integer[workplaces.size()])))
				.and((sellers==null||sellers.isEmpty())?null:p.getSellerProperty().in(sellers.toArray(new Integer[sellers.size()])))
				.and(p.getProductTypeProperty().ne(ProductType.PREPAYMENT.value()))
			,callback)
			.map( d -> d.getDetail() )
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
			.limit(1000);
		String result = InvoiceFormatter.formatInvoices("LISTADO DE FACTURAS", 
				"(M\u00E1x. 1000 Facturas)",
				stream.collect(Collectors.toCollection(LinkedList::new))
				); 
		stream.close();
		return result;
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
	private static final ElaborationPropertiesDAO ELABORATION_PROPERTIES = new ElaborationPropertiesDAO();
	private static final FinancePropertiesDAO FINANCE_PROPERTIES = new FinancePropertiesDAO();
	
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
	
	public static StatData<Integer, String, Double> getElaborationMovements(AONContext ctx,
			ProductFilter productFilter, ItemFilter itemFilter, ElaborationFilter elaborationFilter) {
		
		Collection<Condition> detailConditions = new ArrayList<Condition>();
		detailConditions.addAll(Arrays.asList(ELABORATION_PROPERTIES.getConditions(elaborationFilter)));
		detailConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		detailConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> detailSelect = ctx.getDslContext()
			.select(ITEM.ID, DSL.sum(ELABORATION_DETAIL.QUANTITY).as(ELABORATION_DETAIL.QUANTITY))
			.from(ELABORATION)
				.join(ELABORATION_DETAIL).on(ELABORATION.ID.equal(ELABORATION_DETAIL.ELABORATION))
				.join(ITEM).on(ELABORATION_DETAIL.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
			.where(detailConditions)
			.groupBy(ELABORATION_DETAIL.ID)
			.orderBy(ITEM.PRODUCT);
		
		Collection<Condition> compositionConditions = new ArrayList<Condition>();
		detailConditions.addAll(Arrays.asList(ELABORATION_PROPERTIES.getConditions(elaborationFilter)));
		compositionConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		compositionConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));
		SelectSeekStep1<Record2<Integer, BigDecimal>, Integer> compositionSelect = ctx.getDslContext()
				.select(ITEM.ID, DSL.sum(ELABORATION_DETAIL_COMPOSITION.QUANTITY).as(ELABORATION_DETAIL_COMPOSITION.QUANTITY))
				.from(ELABORATION)
				.join(ELABORATION_DETAIL).on(ELABORATION.ID.equal(ELABORATION_DETAIL.ELABORATION))
				.join(ELABORATION_DETAIL_COMPOSITION).on(ELABORATION_DETAIL.ID.equal(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL))
				.join(ITEM).on(ELABORATION_DETAIL_COMPOSITION.ITEM.equal(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
				.where(compositionConditions)
				.groupBy(ELABORATION_DETAIL_COMPOSITION.ID)
				.orderBy(ITEM.PRODUCT);
		
		Map<Integer, Double> detailMap = new HashMap<>();
		detailSelect
			.forEach(record -> {
				Integer key = record.get(ITEM.ID);
				Double value = record.value2().doubleValue();
				if(!detailMap.containsKey(key))
					detailMap.put(key, 0.0);
				detailMap.put(key, detailMap.get(key)+value);
			});
		Map<Integer, Double> compositionMap = new HashMap<>();
		compositionSelect
			.forEach(record -> {
				Integer key = record.get(ITEM.ID);
				Double value = record.value2().doubleValue();
				if(!compositionMap.containsKey(key))
					compositionMap.put(key, 0.0);
				compositionMap.put(key, compositionMap.get(key)+value);
			});
		
		final StatData<Integer, String, Double> stat = new StatData<>();
		for (Integer key : detailMap.keySet())
			stat.put(key, PRODUCT_INPUTS, detailMap.get(key));
		for (Integer key : compositionMap.keySet())
			stat.put(key, PRODUCT_OUTPUTS, compositionMap.get(key));
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
	
	public static StatData<String, String, Double> getFinanceStat(AONContext ctx, FinanceFilter financeFilter) {
		StatData<String, String, Double> stat = new StatData<>();
		Collection<Condition> whereConditions = new ArrayList<>();
		whereConditions.addAll(Arrays.asList(FINANCE_PROPERTIES.getConditions(financeFilter)));
		ctx.getDslContext().select(FINANCE.PAYMENT, DSL.month(FINANCE.DUE_DATE), DSL.year(FINANCE.DUE_DATE), DSL.sum(FINANCE.AMOUNT).plus(FINANCE.EXPENSES))
		.from(FINANCE)
		.where(whereConditions)
		.groupBy(FINANCE.PAYMENT, DSL.month(FINANCE.DUE_DATE), DSL.year(FINANCE.DUE_DATE))
		.orderBy(FINANCE.DUE_DATE.asc())
		.forEach(record -> {
			stat.put(record.value2().toString() + "/" + record.value3().toString(), record.value1() == 0 ? "cobro" : "pago", record.value4().doubleValue());
		});
		return stat;
	}
	
}
