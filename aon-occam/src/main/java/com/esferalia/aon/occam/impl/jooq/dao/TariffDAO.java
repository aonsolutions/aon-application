package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.TariffFilter;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TariffPropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TariffDAO {
	
	private static final TariffPropertiesDAO TARIFF_PROPERTIES = new TariffPropertiesDAO();

	private static class TariffFiller  implements Function<Record, Tariff> {

		@Override
		public Tariff apply(Record r) {
			return new Tariff()
				.setId(r.getValue(TARIFF.ID))
				.setDomain(r.getValue(TARIFF.DOMAIN))
				.setCode(r.getValue(TARIFF.CODE))
				.setName(r.getValue(TARIFF.NAME))
				.setPurchase(r.getValue(TARIFF.PURCHASE)==1)
				.setDiscount(r.getValue(TARIFF.DISCOUNT))
				.setActive(r.getValue(TARIFF.ACTIVE)==1)
				;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, TariffFilter filter) {
		return ctx.getDslContext().select()
				.from(TARIFF)
				.where(TARIFF_PROPERTIES.getConditions(filter))
				.and(TARIFF.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
		
	}
	
	public static Stream<Tariff> getStream(AONContext ctx, TariffFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new TariffFiller());
	}
	
	public static Tariff get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	public static Tariff insert(AONContext ctx, Tariff tariff) {
		ctx.checkWrite();
		validate(ctx, tariff);
		Integer id = ctx.getDslContext().insertInto(TARIFF)
			.set(TARIFF.DOMAIN,tariff.getDomain())
			.set(TARIFF.CODE,tariff.getCode())
			.set(TARIFF.NAME,tariff.getName())
			.set(TARIFF.PURCHASE, AonEnumUtils.getByte( tariff.isPurchase()))
			.set(TARIFF.DISCOUNT,tariff.getDiscount())
			.set(TARIFF.ACTIVE, AonEnumUtils.getByte( tariff.isActive()))
			.returning(TARIFF.ID)
			.fetchOne()
			.getValue(TARIFF.ID);
		tariff.setId(id);
		ctx.log().info("INSERT TARIFF id: " + tariff.getId());		
		return tariff;
	}

	public static Tariff update(AONContext ctx, Tariff tariff) {
		ctx.checkWrite();
		validate(ctx, tariff);
		int count = ctx.getDslContext().update(TARIFF)
			.set(TARIFF.CODE,tariff.getCode())
			.set(TARIFF.NAME,tariff.getName())
			.set(TARIFF.PURCHASE, AonEnumUtils.getByte( tariff.isPurchase()))
			.set(TARIFF.DISCOUNT,tariff.getDiscount())
			.set(TARIFF.ACTIVE, AonEnumUtils.getByte( tariff.isActive()))
			.where(TARIFF.ID.eq(tariff.getId()))
			.execute();
		ctx.log().info("UPDATE TARIFF id: " + tariff.getId() + ". (" + count + " rows)");
		return tariff;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(TARIFF)
			.where(TARIFF.ID.eq(id))
			.execute();
		ctx.log().info("DELETE TARIFF id:" + id + " ("+count+" rows)");
	}

	// ***************************************
	// ********** VALIDATION *****************
	// ***************************************

	private static BiConsumer<Tariff,AONContext> EMPTY_DOMAIN = (tariff,ctx) -> {
		if (tariff.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	private static BiConsumer<Tariff,AONContext> EMPTY_NAME  = (tariff,ctx) -> {
		if (tariff.getName() == null) 
			throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
	};
	
	private static BiConsumer<Tariff,AONContext> OVERFLOW_CODE = (tariff,ctx) -> {
		if (AonStringUtils.length(tariff.getCode()) > TARIFF.CODE.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "C\u00F3digo", TARIFF.CODE.getDataType().length() ));
	};
	
	private static BiConsumer<Tariff,AONContext> OVERFLOW_NAME = (tariff,ctx) -> {
		if (AonStringUtils.length(tariff.getName()) > TARIFF.NAME.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre", TARIFF.NAME.getDataType().length() ));
	};
	
	private static void validate(AONContext ctx, Tariff tariff) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_NAME)
		.andThen(OVERFLOW_CODE)
		.andThen(OVERFLOW_NAME)
		.accept(tariff, ctx);
	}
	
	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Tariff getRandom(AONContext ctx, TariffFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new TariffFiller())
			.findFirst()
			.orElse(null);
	}

	

}
