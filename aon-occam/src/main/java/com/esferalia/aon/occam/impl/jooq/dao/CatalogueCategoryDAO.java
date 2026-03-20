package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CatalogueCategory.CATALOGUE_CATEGORY;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.CatalogueCategoryFilter;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueCategory;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CatalogueCategoryPropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CatalogueCategoryDAO {

	private CatalogueCategoryDAO() {}

	private static final CatalogueCategoryPropertiesDAO CATALOGUE_CATEGORY_PROPERTIES = new CatalogueCategoryPropertiesDAO();

	public static class CatalogueCategoryFiller extends Filler implements Function<Record, CatalogueCategory> {

		@Override
		public CatalogueCategory apply(Record r) {
			return build(r);
		}

		public static CatalogueCategory build(Record r) {
			return new CatalogueCategory()
				.setId(r.getValue(CATALOGUE_CATEGORY.ID))
				.setDomain(r.getValue(CATALOGUE_CATEGORY.DOMAIN))
				.setCatalogue(r.getValue(CATALOGUE_CATEGORY.CATALOGUE))
				.setCategory(r.getValue(CATALOGUE_CATEGORY.CATEGORY))
				.setQuantity(r.getValue(CATALOGUE_CATEGORY.QUANTITY))
				.setDiscount(r.getValue(CATALOGUE_CATEGORY.DISCOUNT));
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, CatalogueCategoryFilter filter) {
		return ctx.getDslContext().select()
			.from(CATALOGUE_CATEGORY)
			.where(CATALOGUE_CATEGORY_PROPERTIES.getConditions(filter))
			.and(CATALOGUE_CATEGORY.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
	}

	public static Stream<CatalogueCategory> getStream(AONContext ctx, CatalogueCategoryFilter filter) {
		return select(ctx, filter)
			.fetch()
			.stream()
			.map(new CatalogueCategoryFiller());
	}

	public static CatalogueCategory get(AONContext ctx, Integer id) {
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static Stream<CatalogueCategory> getByCatalogue(AONContext ctx, Integer catalogueId) {
		return getStream(ctx, p -> p.getCatalogueProperty().eq(catalogueId));
	}

	public static CatalogueCategory save(CloseableAONContext ctx, CatalogueCategory catalogueCategory) {
		ctx.checkWrite();
		validate(ctx, catalogueCategory);
		
		return catalogueCategory.getId() == null ? insert(ctx, catalogueCategory) : update(ctx, catalogueCategory);
	}

	public static CatalogueCategory insert(AONContext ctx, CatalogueCategory catalogueCategory) {
		Integer id = ctx.getDslContext().insertInto(CATALOGUE_CATEGORY)
			.set(CATALOGUE_CATEGORY.DOMAIN, catalogueCategory.getDomain())
			.set(CATALOGUE_CATEGORY.CATALOGUE, catalogueCategory.getCatalogue())
			.set(CATALOGUE_CATEGORY.CATEGORY, catalogueCategory.getCategory())
			.set(CATALOGUE_CATEGORY.QUANTITY, catalogueCategory.getQuantity())
			.set(CATALOGUE_CATEGORY.DISCOUNT, catalogueCategory.getDiscount())
			.returning(CATALOGUE_CATEGORY.ID)
			.fetchOne()
			.getValue(CATALOGUE_CATEGORY.ID);

		catalogueCategory.setId(id);

		ctx.log().debug("INSERT CATALOGUE_CATEGORY id: {0}", catalogueCategory.getId());

		return catalogueCategory;
	}

	public static CatalogueCategory update(AONContext ctx, CatalogueCategory catalogueCategory) {
		int count = ctx.getDslContext().update(CATALOGUE_CATEGORY)
			.set(CATALOGUE_CATEGORY.CATEGORY, catalogueCategory.getCategory())
			.set(CATALOGUE_CATEGORY.QUANTITY, catalogueCategory.getQuantity())
			.set(CATALOGUE_CATEGORY.DISCOUNT, catalogueCategory.getDiscount())
			.where(CATALOGUE_CATEGORY.ID.eq(catalogueCategory.getId()))
			.execute();

		ctx.log().debug("UPDATE CATALOGUE_CATEGORY id: {0}. ({1} rows)", catalogueCategory.getId(), count);

		return catalogueCategory;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();

		int count = ctx.getDslContext().delete(CATALOGUE_CATEGORY)
			.where(CATALOGUE_CATEGORY.ID.eq(id))
			.execute();

		ctx.log().debug("DELETE CATALOGUE_CATEGORY id: {0} ({1} rows)", id, count);
	}

	public static void deleteByCatalogue(AONContext ctx, Integer catalogueId) {
		ctx.checkWrite();

		int count = ctx.getDslContext().delete(CATALOGUE_CATEGORY)
			.where(CATALOGUE_CATEGORY.CATALOGUE.eq(catalogueId))
			.execute();

		ctx.log().debug("DELETE CATALOGUE_CATEGORY catalogue: {0} ({1} rows)", catalogueId, count);
	}

	// ***************************************
	// ********** VALIDATION *****************
	// ***************************************

	private static BiConsumer<CatalogueCategory, AONContext> EMPTY_DOMAIN = (category, ctx) -> {
		if (category.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};

	private static BiConsumer<CatalogueCategory, AONContext> EMPTY_CATALOGUE = (category, ctx) -> {
		if (category.getCatalogue() == null)
			throw new AonCoreException("El catalogo es obligatorio");
	};

	private static BiConsumer<CatalogueCategory, AONContext> EMPTY_CATEGORY = (category, ctx) -> {
		if (category.getCategory() == null)
			throw new AonCoreException("La categoria es obligatoria");
	};

	private static void validate(AONContext ctx, CatalogueCategory catalogueCategory) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_CATALOGUE)
			.andThen(EMPTY_CATEGORY)
			.accept(catalogueCategory, ctx);
	}
}
