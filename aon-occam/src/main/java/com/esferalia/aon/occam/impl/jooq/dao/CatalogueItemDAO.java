package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.CatalogueItemFilter;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CatalogueItemPropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CatalogueItemDAO {

	private CatalogueItemDAO() {}

	private static final CatalogueItemPropertiesDAO CATALOGUE_ITEM_PROPERTIES = new CatalogueItemPropertiesDAO();

	public static class CatalogueItemFiller extends Filler implements Function<Record, CatalogueItem> {

		@Override
		public CatalogueItem apply(Record r) {
			return build(r);
		}

		public static CatalogueItem build(Record r) {
			return new CatalogueItem()
				.setId(r.getValue(CATALOGUE_ITEM.ID))
				.setDomain(r.getValue(CATALOGUE_ITEM.DOMAIN))
				.setCatalogue(r.getValue(CATALOGUE_ITEM.CATALOGUE))
				.setProduct(r.getValue(CATALOGUE_ITEM.PRODUCT))
				.setItem(r.getValue(CATALOGUE_ITEM.ITEM))
				.setQuantity(r.getValue(CATALOGUE_ITEM.QUANTITY))
				.setPrice(r.getValue(CATALOGUE_ITEM.PRICE))
				.setDiscount(r.getValue(CATALOGUE_ITEM.DISCOUNT));
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, CatalogueItemFilter filter) {
		return ctx.getDslContext().select()
			.from(CATALOGUE_ITEM)
			.where(CATALOGUE_ITEM_PROPERTIES.getConditions(filter))
			.and(CATALOGUE_ITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
	}

	public static Stream<CatalogueItem> getStream(AONContext ctx, CatalogueItemFilter filter) {
		return select(ctx, filter)
			.fetch()
			.stream()
			.map(new CatalogueItemFiller());
	}

	public static CatalogueItem get(AONContext ctx, Integer id) {
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static Stream<CatalogueItem> getByCatalogue(AONContext ctx, Integer catalogueId) {
		return getStream(ctx, p -> p.getCatalogueProperty().eq(catalogueId));
	}

	public static CatalogueItem save(CloseableAONContext ctx, CatalogueItem catalogueItem) {
		return catalogueItem.getId() == null ? insert(ctx, catalogueItem) : update(ctx, catalogueItem);
	}

	public static CatalogueItem insert(AONContext ctx, CatalogueItem catalogueItem) {
		ctx.checkWrite();
		validate(ctx, catalogueItem);

		Integer id = ctx.getDslContext().insertInto(CATALOGUE_ITEM)
			.set(CATALOGUE_ITEM.DOMAIN, catalogueItem.getDomain())
			.set(CATALOGUE_ITEM.CATALOGUE, catalogueItem.getCatalogue())
			.set(CATALOGUE_ITEM.PRODUCT, catalogueItem.getProduct())
			.set(CATALOGUE_ITEM.ITEM, catalogueItem.getItem())
			.set(CATALOGUE_ITEM.QUANTITY, catalogueItem.getQuantity())
			.set(CATALOGUE_ITEM.PRICE, catalogueItem.getPrice())
			.set(CATALOGUE_ITEM.DISCOUNT, catalogueItem.getDiscount())
			.returning(CATALOGUE_ITEM.ID)
			.fetchOne()
			.getValue(CATALOGUE_ITEM.ID);

		catalogueItem.setId(id);

		ctx.log().debug("INSERT CATALOGUE_ITEM id: {0}", catalogueItem.getId());

		return catalogueItem;
	}

	public static CatalogueItem update(AONContext ctx, CatalogueItem catalogueItem) {
		ctx.checkWrite();
		validate(ctx, catalogueItem);

		int count = ctx.getDslContext().update(CATALOGUE_ITEM)
			.set(CATALOGUE_ITEM.PRODUCT, catalogueItem.getProduct())
			.set(CATALOGUE_ITEM.ITEM, catalogueItem.getItem())
			.set(CATALOGUE_ITEM.QUANTITY, catalogueItem.getQuantity())
			.set(CATALOGUE_ITEM.PRICE, catalogueItem.getPrice())
			.set(CATALOGUE_ITEM.DISCOUNT, catalogueItem.getDiscount())
			.where(CATALOGUE_ITEM.ID.eq(catalogueItem.getId()))
			.execute();

		ctx.log().debug("UPDATE CATALOGUE_ITEM id: {0}. ({1} rows)", catalogueItem.getId(), count);

		return catalogueItem;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();

		int count = ctx.getDslContext().delete(CATALOGUE_ITEM)
			.where(CATALOGUE_ITEM.ID.eq(id))
			.execute();

		ctx.log().debug("DELETE CATALOGUE_ITEM id: {0} ({1} rows)", id, count);
	}

	public static void deleteByCatalogue(AONContext ctx, Integer catalogueId) {
		ctx.checkWrite();

		int count = ctx.getDslContext().delete(CATALOGUE_ITEM)
			.where(CATALOGUE_ITEM.CATALOGUE.eq(catalogueId))
			.execute();

		ctx.log().debug("DELETE CATALOGUE_ITEM catalogue: {0} ({1} rows)", catalogueId, count);
	}

	// ***************************************
	// ********** VALIDATION *****************
	// ***************************************

	private static BiConsumer<CatalogueItem, AONContext> EMPTY_DOMAIN = (item, ctx) -> {
		if (item.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};

	private static BiConsumer<CatalogueItem, AONContext> EMPTY_CATALOGUE = (item, ctx) -> {
		if (item.getCatalogue() == null)
			throw new AonCoreException("El catálogo es obligatorio");
	};

	private static BiConsumer<CatalogueItem, AONContext> EMPTY_PRODUCT = (item, ctx) -> {
		if (item.getProduct() == null)
			throw new AonCoreException("El producto es obligatorio");
	};

	private static void validate(AONContext ctx, CatalogueItem catalogueItem) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_CATALOGUE)
			.andThen(EMPTY_PRODUCT)
			.accept(catalogueItem, ctx);
	}
}
