package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Stock.STOCK;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Properties.StockProperties;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.impl.jooq.validation.ProductOldValidation;

public class StockDAO {
	
	private StockDAO() {
		
	}
	
	private static final StockPropertiesDAO STOCK_PROPERTIES = new StockPropertiesDAO();

	protected static class StockPropertiesDAO implements StockProperties {
		protected Condition[] getConditions(StockFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(STOCK.ID);} 
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(STOCK.WAREHOUSE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(STOCK.DOMAIN);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(STOCK.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(STOCK.QUANTITY);}
	}
	
	public static Stream<Stock> getStream(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().select().from(STOCK).where(STOCK_PROPERTIES.getConditions(filter))
		.fetchInto(STOCK).stream().map(new StockFiller());
	}
	
	public static Stock get(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().select().from(STOCK).where(STOCK_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(STOCK).stream().map(new StockFiller()).findFirst().orElse(new Stock());
	}
	
	public static Optional<Stock> opt(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().select().from(STOCK).where(STOCK_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(STOCK).stream().map(new StockFiller()).findFirst();
	}
	
	public static Stock save(AONContext ctx, Stock stock) {
		return stock.getId() != null 
				? update(ctx, stock).orElse(new Stock())
				: insert(ctx, stock).orElse(new Stock());
	}
	
	public static Optional<Stock> insert(AONContext ctx, Stock stock){
		ctx.checkWrite();
		ProductOldValidation.validateStocking(ctx, stock.getItem());
		return ctx.getDslContext().insertInto(STOCK, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE)
				.values(stock.getDomain(), stock.getItem(), stock.getQuantity(), stock.getWarehouse())
				.returning().fetch().stream().map(new StockFiller()).findFirst();
	}
	
	public static Optional<Stock> update(AONContext ctx, Stock stock){
		ctx.checkWrite();
		return ctx.getDslContext().update(STOCK)
			.set(STOCK.QUANTITY, stock.getQuantity())
			.set(STOCK.DOMAIN, stock.getDomain())
			.set(STOCK.ITEM, stock.getItem())
			.set(STOCK.WAREHOUSE, stock.getWarehouse())
			.where(STOCK.ID.eq(stock.getId()))
			.returning().fetch().stream().map(new StockFiller()).findFirst();
	}
	
	public static Optional<Stock> delete(AONContext ctx, Integer stockId){
		Optional<Stock> stock = getStream(ctx, f -> f.getIdProperty().eq(stockId)).findFirst();
		ctx.getDslContext().delete(STOCK).where(STOCK.ID.eq(stockId)).execute();
		return stock;
	}
	
	public static void add(AONContext ctx, Integer itemId, Integer warehouse, double quantity) {
		Optional<Stock> stockOptional = opt(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getItemProperty().eq(itemId))
			.and(f.getWarehouseProperty().eq(warehouse)));
		Stock stock;
		if(stockOptional.isEmpty()) {
			stock = new Stock()
					.setDomain(ctx.getDomainId())
					.setItem(itemId)
					.setWarehouse(warehouse)
					.setQuantity(quantity);
		} else {
			stock = stockOptional.get();
			stock.setQuantity(stock.getQuantity() + quantity);
		}
		save(ctx, stock);
	}
	
	public static void subtract(AONContext ctx, Integer itemId, Integer warehouse, double quantity) {
		Optional<Stock> stockOptional = opt(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getItemProperty().eq(itemId))
			.and(f.getWarehouseProperty().eq(warehouse)));
		Stock stock;
		if(stockOptional.isEmpty()) {
			stock = new Stock()
					.setDomain(ctx.getDomainId())
					.setItem(itemId)
					.setWarehouse(warehouse)
					.setQuantity(-quantity);
		} else {
			stock = stockOptional.get();
			stock.setQuantity(stock.getQuantity() - quantity);
		}
		save(ctx, stock);
	}
	
	private static class StockFiller extends Filler implements Function<Record, Stock> {
		@Override
		public Stock apply(Record r) {
			return new Stock()
					.setId(getValue(r, STOCK.ID))
					.setDomain(getValue(r, STOCK.DOMAIN))
					.setItem(getValue(r, STOCK.ITEM))
					.setQuantity(getValue(r, STOCK.QUANTITY))
					.setWarehouse(getValue(r, STOCK.WAREHOUSE));
		}
	}
}
