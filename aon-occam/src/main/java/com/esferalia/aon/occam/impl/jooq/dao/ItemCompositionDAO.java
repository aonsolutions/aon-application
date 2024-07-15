package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemComposition.ITEM_COMPOSITION;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ItemCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ItemCompositionProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;

public class ItemCompositionDAO {
	
	private ItemCompositionDAO() {
		
	}
	
	public static final com.esferalia.aon.jooq.tables.Item COMPOSITION_ALIAS = ITEM.as("composition_item");
	public static final com.esferalia.aon.jooq.tables.Product COMPOSITION_PRODUCT_ALIAS = PRODUCT.as("composition_product");
	
	private static final ItemCompositionPropertiesDAO ITEM_COMPOSITION_PROPERTIES = new ItemCompositionPropertiesDAO();

	protected static class ItemCompositionPropertiesDAO implements ItemCompositionProperties {
		protected Condition[] getConditions(ItemCompositionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.DOMAIN);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.ITEM);}
		@Override public Property<Integer> getCompositionItemProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.COMPOSITION_ITEM);}
		@Override public Property<Short> getSequenceProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.SEQUENCE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.QUANTITY);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(ITEM_COMPOSITION.DISCOUNT_EXPR);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, ItemCompositionFilter filter) {
		return ctx.getDslContext().select().from(ITEM_COMPOSITION)
				.leftOuterJoin(COMPOSITION_ALIAS).on(ITEM_COMPOSITION.COMPOSITION_ITEM.eq(COMPOSITION_ALIAS.ID))
				.leftOuterJoin(COMPOSITION_PRODUCT_ALIAS).on(COMPOSITION_PRODUCT_ALIAS.ID.eq(COMPOSITION_ALIAS.PRODUCT))
				.where(ITEM_COMPOSITION_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ItemComposition> getStream(AONContext ctx, ItemCompositionFilter filter){
		return select(ctx, filter).fetch().stream().map(new ItemCompositionFiller());
	}

	public static List<ItemComposition> getList(AONContext ctx, ItemCompositionFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ItemComposition get(AONContext ctx, ItemCompositionFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new ItemCompositionFiller()).findFirst().orElse(new ItemComposition());
	}
	
	public static ItemComposition save(AONContext ctx, ItemComposition itemComposition) {
		return itemComposition.getId() != null
				? update(ctx, itemComposition)
				: insert(ctx, itemComposition);
	}
	
	
	public static ItemComposition insert(AONContext ctx, ItemComposition itemComposition) {
		Integer id = ctx.getDslContext().insertInto(ITEM_COMPOSITION)
				.set(ITEM_COMPOSITION.DOMAIN, itemComposition.getDomain())
				.set(ITEM_COMPOSITION.ITEM, itemComposition.getItemId())
				.set(ITEM_COMPOSITION.COMPOSITION_ITEM, itemComposition.getCompositionItemId())
				.set(ITEM_COMPOSITION.SEQUENCE, itemComposition.getSequence().shortValue())
				.set(ITEM_COMPOSITION.DESCRIPTION, itemComposition.getDescription())
				.set(ITEM_COMPOSITION.QUANTITY, itemComposition.getQuantity())
				.set(ITEM_COMPOSITION.DISCOUNT_EXPR, itemComposition.getDiscountExpression())
				.returning(ITEM_COMPOSITION.ID).fetchOne().getValue(ITEM_COMPOSITION.ID);
		ctx.log().debug("INSERT ITEM COMPOSITION ID: " +id);	
		return itemComposition.setId(id);
	}
	
	public static ItemComposition update(AONContext ctx, ItemComposition itemComposition) {
		ctx.getDslContext().update(ITEM_COMPOSITION)
			.set(ITEM_COMPOSITION.DOMAIN, itemComposition.getDomain())
			.set(ITEM_COMPOSITION.ITEM, itemComposition.getItemId())
			.set(ITEM_COMPOSITION.COMPOSITION_ITEM, itemComposition.getCompositionItemId())
			.set(ITEM_COMPOSITION.SEQUENCE, itemComposition.getSequence().shortValue())
			.set(ITEM_COMPOSITION.DESCRIPTION, itemComposition.getDescription())
			.set(ITEM_COMPOSITION.QUANTITY, itemComposition.getQuantity())
			.set(ITEM_COMPOSITION.DISCOUNT_EXPR, itemComposition.getDiscountExpression())
			.where(ITEM_COMPOSITION.ID.eq(itemComposition.getId())).execute();
		ctx.log().debug("UPDATE ITEM COMPOSITION id:" + itemComposition.getId());
		return itemComposition;
	}

	public static void delete(AONContext ctx, ItemCompositionFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(ITEM_COMPOSITION)
			.where(ITEM_COMPOSITION_PROPERTIES.getConditions(filter))
			.and(ITEM_COMPOSITION.DOMAIN.eq(ctx.getDomainId()))
			.execute();
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(ITEM_COMPOSITION).where(ITEM_COMPOSITION.ID.eq(id)).execute();
		ctx.log().debug("DELETE ITEM COMPOSITION id:" + id);
	}

	public static class ItemCompositionFiller extends Filler  implements Function<Record, ItemComposition> {

		@Override
		public ItemComposition apply(Record r) {
			return build(r);
		}

		public static ItemComposition build(Record r) {
			return new ItemComposition()
				.setId(getValue(r, ITEM_COMPOSITION.ID))
				.setDomain(getInteger(r, ITEM_COMPOSITION.DOMAIN))
				.setItemId(getInteger(r, ITEM_COMPOSITION.ITEM))
				.setCompositionItemId(getInteger(r, ITEM_COMPOSITION.COMPOSITION_ITEM))
				.setSequence(getShort(r, ITEM_COMPOSITION.SEQUENCE))
				.setDescription(getValue(r, ITEM_COMPOSITION.DESCRIPTION))
				.setQuantity(getDouble(r, ITEM_COMPOSITION.QUANTITY))
				.setDiscountExpression(getValue(r, ITEM_COMPOSITION.DISCOUNT_EXPR))
				.setComposition(checkField(r, COMPOSITION_ALIAS.ID)
						? ItemFiller.build(r, COMPOSITION_ALIAS, COMPOSITION_PRODUCT_ALIAS)
						: new Item().setId(getValue(r, ITEM_COMPOSITION.COMPOSITION_ITEM)));
		}
	}
	
}
