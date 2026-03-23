package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.CatalogueFilter;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CataloguePropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CatalogueDAO {
	
	private CatalogueDAO() {}
	
	private static final CataloguePropertiesDAO CATALOGUE_PROPERTIES = new CataloguePropertiesDAO();

	public static class CatalogueFiller extends Filler implements Function<Record, Catalogue> {

		@Override
		public Catalogue apply(Record r) {
			return build(r);
		}
		
		public static Catalogue build(Record r) {
			return new Catalogue()
				.setId(r.getValue(CATALOGUE.ID))
				.setDomain(r.getValue(CATALOGUE.DOMAIN))
				.setName(r.getValue(CATALOGUE.NAME))
				.setPurchase(r.getValue(CATALOGUE.PURCHASE) == (byte)1)
				.setStart(r.getValue(CATALOGUE.START_DATE))
				.setEnd(r.getValue(CATALOGUE.END_DATE));
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, CatalogueFilter filter) {
		return ctx.getDslContext().select()
				.from(CATALOGUE)
				.where(CATALOGUE_PROPERTIES.getConditions(filter))
				.and(CATALOGUE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
		
	}
	
	public static Stream<Catalogue> getStream(AONContext ctx, CatalogueFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new CatalogueFiller());
	}
	
	public static Catalogue get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	public static Catalogue save(CloseableAONContext ctx, Catalogue catalogue) {
		return catalogue.getId() == null ? insert(ctx, catalogue) : update(ctx, catalogue);
	}
	
	public static Catalogue insert(AONContext ctx, Catalogue catalogue) {
		ctx.checkWrite();
		validate(ctx, catalogue);
		
		Integer id = ctx.getDslContext().insertInto(CATALOGUE)
			.set(CATALOGUE.DOMAIN,catalogue.getDomain())
			.set(CATALOGUE.NAME,catalogue.getName())
			.set(CATALOGUE.PURCHASE, AonEnumUtils.getByte( catalogue.isPurchase()))
			.set(CATALOGUE.START_DATE, AonDateUtils.toSql(catalogue.getStart()))
			.set(CATALOGUE.END_DATE, AonDateUtils.toSql(catalogue.getEnd()))
			.returning(CATALOGUE.ID)
			.fetchOne()
			.getValue(CATALOGUE.ID);
		
		catalogue.setId(id);
		
		ctx.log().debug("INSERT TARIFF id: {0}", catalogue.getId());		
		
		return get(ctx, id);
	}

	public static Catalogue update(AONContext ctx, Catalogue catalogue) {
		ctx.checkWrite();
		validate(ctx, catalogue);
		
		int count = ctx.getDslContext().update(CATALOGUE)
				.set(CATALOGUE.NAME,catalogue.getName())
				.set(CATALOGUE.PURCHASE, AonEnumUtils.getByte( catalogue.isPurchase()))
				.set(CATALOGUE.START_DATE, AonDateUtils.toSql(catalogue.getStart()))
				.set(CATALOGUE.END_DATE, AonDateUtils.toSql(catalogue.getEnd()))
				.where(CATALOGUE.ID.eq(catalogue.getId()))
				.execute();
		
		ctx.log().debug("UPDATE CATALOGUE id: {0}. ({1} rows)", catalogue.getId(), count);
		
		return get(ctx, catalogue.getId());
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		validateDeletion(ctx, id);
		
		int count = ctx.getDslContext().delete(CATALOGUE)
			.where(CATALOGUE.ID.eq(id))
			.execute();
		
		ctx.log().debug("DELETE CATALOGUE id: {0} ({1} rows)",id,count);
	}

	// ***************************************
	// ********** VALIDATION *****************
	// ***************************************

	private static BiConsumer<Catalogue, AONContext> EMPTY_DOMAIN = (catalogue, ctx) -> {
		if (catalogue.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	private static BiConsumer<Catalogue, AONContext> EMPTY_NAME  = (catalogue, ctx) -> {
		if (AonStringUtils.isBlank(catalogue.getName())) 
			throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
	};
	
	private static BiConsumer<Catalogue, AONContext> EMPTY_START_DATE = (catalogue, ctx) -> {
		if (catalogue.getStart() == null) 
			throw new AonCoreException("La fecha de incio es obligatoria");
	};
	
	private static void validate(AONContext ctx, Catalogue catalogue) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_NAME)
		.andThen(EMPTY_START_DATE)
		.accept(catalogue, ctx);
	}
	
	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}

}
