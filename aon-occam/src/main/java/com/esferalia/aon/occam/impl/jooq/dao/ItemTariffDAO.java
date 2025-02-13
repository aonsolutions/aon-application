package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ItemTariff.ITEM_TARIFF;
import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.ItemTariffFilter;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.ItemTariffType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ItemTariffPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO.TariffFiller;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ItemTariffDAO {
	
	private ItemTariffDAO() {}
	
	private static final ItemTariffPropertiesDAO ITEM_TARIFF_PROPERTIES = new ItemTariffPropertiesDAO();

	public static class ItemTariffFiller extends Filler implements Function<Record, ItemTariff> {

		@Override
		public ItemTariff apply(Record r) {
			return build(r);
		}
		
		public static ItemTariff build(Record r) {
			return new ItemTariff()
				.setId(r.getValue(ITEM_TARIFF.ID))
				.setDomain(r.getValue(ITEM_TARIFF.DOMAIN))
				.setItem(r.getValue(ITEM_TARIFF.ITEM))
				.setTariff(TariffFiller.build(r))
				.setType(ItemTariffType.safeValueOf(r.getValue(ITEM_TARIFF.TYPE)))
				.setProfitPercent(r.getValue(ITEM_TARIFF.PROFIT_PERCENT))
				.setPrice(r.getValue(ITEM_TARIFF.PRICE))
				;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, ItemTariffFilter filter) {
		return ctx.getDslContext().select()
				.from(ITEM_TARIFF)
				.join(TARIFF).on(TARIFF.ID.eq(ITEM_TARIFF.TARIFF))
				.where(ITEM_TARIFF_PROPERTIES.getConditions(filter))
				.and(ITEM_TARIFF.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
		
	}
	
	public static Stream<ItemTariff> getStream(AONContext ctx, ItemTariffFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new ItemTariffFiller());
	}
	
	public static ItemTariff get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	public static ItemTariff save(CloseableAONContext ctx, ItemTariff itemTariff) {
		return null == itemTariff.getId() 
				? insert(ctx, itemTariff)
				: update(ctx, itemTariff);
	}
	
	public static ItemTariff insert(AONContext ctx, ItemTariff itemTariff) {
		ctx.checkWrite();
		
		validate(ctx, itemTariff);
		
		Integer id = ctx.getDslContext().insertInto(ITEM_TARIFF)
			.set(ITEM_TARIFF.DOMAIN, itemTariff.getDomain())
			.set(ITEM_TARIFF.ITEM, itemTariff.getItem())
			.set(ITEM_TARIFF.TARIFF, itemTariff.getTariff().getId())
			.set(ITEM_TARIFF.TYPE, (byte) itemTariff.getType().ordinal())
			.set(ITEM_TARIFF.PROFIT_PERCENT, itemTariff.getProfitPercent())
			.set(ITEM_TARIFF.PRICE, itemTariff.getPrice())
			.returning(ITEM_TARIFF.ID)
			.fetchOne()
			.getValue(ITEM_TARIFF.ID);
		
		itemTariff.setId(id);
		
		ctx.log().debug("INSERT ITEM TARIFF id: {0}", itemTariff.getId());		
		
		return itemTariff;
	}

	public static ItemTariff update(AONContext ctx, ItemTariff itemTariff) {
		ctx.checkWrite();
		
		validate(ctx, itemTariff);
		
		int count = ctx.getDslContext().update(ITEM_TARIFF)
			.set(ITEM_TARIFF.ITEM, itemTariff.getItem())
			.set(ITEM_TARIFF.TARIFF, itemTariff.getTariff().getId())
			.set(ITEM_TARIFF.TYPE, (byte) itemTariff.getType().ordinal())
			.set(ITEM_TARIFF.PROFIT_PERCENT, itemTariff.getProfitPercent())
			.set(ITEM_TARIFF.PRICE, itemTariff.getPrice())
			.where(ITEM_TARIFF.ID.eq(itemTariff.getId()))
			.execute();
		
		ctx.log().debug("UPDATE ITEM TARIFF id: {0}. ({1} rows)", itemTariff.getId(), count);
		
		return itemTariff;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		int count = ctx.getDslContext().delete(ITEM_TARIFF)
			.where(ITEM_TARIFF.ID.eq(id))
			.execute();
		
		ctx.log().debug("DELETE ITEM TARIFF id: {0} ({1} rows)", id, count);
	}

	// ***************************************
	// ********** VALIDATION *****************
	// ***************************************

	private static BiConsumer<ItemTariff,AONContext> EMPTY_DOMAIN = (itemTariff,ctx) -> {
		if (itemTariff.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	private static BiConsumer<ItemTariff,AONContext> EMPTY_ITEM  = (itemTariff,ctx) -> {
		if (itemTariff.getItem() == null) 
			throw new AonCoreException("El item es un dato obligatorio, no puede estar vac\u00EDo");
	};
	
	private static BiConsumer<ItemTariff,AONContext> EMPTY_TARIFF  = (itemTariff,ctx) -> {
		if (itemTariff.getTariff() == null || itemTariff.getTariff().getId() == null) 
			throw new AonCoreException("La tarifa es un dato obligatorio, no puede estar vac\u00EDo");
	};
	
	private static void validate(AONContext ctx, ItemTariff itemTariff) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_ITEM)
		.andThen(EMPTY_TARIFF)
		.accept(itemTariff, ctx);
	}

	

}
