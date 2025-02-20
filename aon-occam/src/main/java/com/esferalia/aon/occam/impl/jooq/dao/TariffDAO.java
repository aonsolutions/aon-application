package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;
import static com.esferalia.aon.jooq.tables.TariffAddinfo.TARIFF_ADDINFO;
import static com.esferalia.aon.jooq.tables.TariffCatalogue.TARIFF_CATALOGUE;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import  org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.TariffFilter;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO.CatalogueFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TariffPropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TariffDAO {
	
	private TariffDAO() {}
	
	private static final TariffPropertiesDAO TARIFF_PROPERTIES = new TariffPropertiesDAO();

	public static class TariffFiller extends Filler implements Function<Record, Tariff> {

		@Override
		public Tariff apply(Record r) {
			return build(r);
		}
		
		public static Tariff build(Record r) {
			return new Tariff()
				.setId(r.getValue(TARIFF.ID))
				.setDomain(r.getValue(TARIFF.DOMAIN))
				.setCode(r.getValue(TARIFF.CODE))
				.setName(r.getValue(TARIFF.NAME))
				.setPurchase(r.getValue(TARIFF.PURCHASE)==1)
				.setDiscount(r.getValue(TARIFF.DISCOUNT))
				.setActive(r.getValue(TARIFF.ACTIVE)==1);
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
	
	public static List<Tariff> getTariffList(CloseableAONContext ctx, TariffParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select()
			.from(TARIFF)
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(TARIFF.CODE);
			else  if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(TARIFF.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "type"))
				select.orderBy(TARIFF.PURCHASE);
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(TARIFF.ACTIVE);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(TARIFF.CODE.desc());
			else  if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(TARIFF.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "type"))
				select.orderBy(TARIFF.PURCHASE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(TARIFF.ACTIVE.desc());
		}
		
		List<Tariff> tariffs = select.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new TariffFiller())
			.collect(Collectors.toList());
			
		return tariffs;
	}
	

	private static Condition paramsToCondition(CloseableAONContext ctx, TariffParams params) {
		Condition condition = TARIFF.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(
					TARIFF.NAME.like("%" + params.getDescription() + "%")
					.or(TARIFF.CODE.like("%" + params.getDescription() + "%"))
			);
		
		if(null != params.getPurchase())
			condition = condition.and(TARIFF.PURCHASE.eq(params.getPurchase()));
		
		if(null != params.getStatus())
			condition = condition.and(TARIFF.ACTIVE.eq(params.getStatus()));
		
		return condition;
	}
	
	public static Tariff save(CloseableAONContext ctx, Tariff tariff) {
		return tariff.getId() == null ? insert(ctx, tariff) : update(ctx, tariff);
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
		ctx.log().debug("INSERT TARIFF id: {0}", tariff.getId());		
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
		ctx.log().debug("UPDATE TARIFF id: {0}. ({1} rows)",tariff.getId(),count);
		return tariff;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(TARIFF)
			.where(TARIFF.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE TARIFF id: {0} ({1} rows)",id,count);
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

	public static List<TariffAddInfo> getTariffAddInfoList(CloseableAONContext ctx, Integer tariffId) {
		List<TariffAddInfo> tariffAddInfoList = ctx.getDslContext().select()
			.from(TARIFF_ADDINFO)
			.join(TARIFF).on(TARIFF.ID.eq(TARIFF_ADDINFO.TARIFF))
			.where(TARIFF_ADDINFO.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(TARIFF_ADDINFO.TARIFF.eq(tariffId))
			.fetch()
			.stream()
			.map(new TariffAddInfoFiller())
			.collect(Collectors.toList());
		
		return tariffAddInfoList;
	}

	public static TariffAddInfo saveTariffAddInfo(CloseableAONContext ctx, TariffAddInfo tariffAddInfo) {
		return tariffAddInfo.getId() == null ? insert(ctx, tariffAddInfo) : update(ctx, tariffAddInfo);
	}
	
	public static TariffAddInfo insert(AONContext ctx, TariffAddInfo tariffAddInfo) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(TARIFF_ADDINFO)
			.set(TARIFF_ADDINFO.DOMAIN, tariffAddInfo.getDomain())
			.set(TARIFF_ADDINFO.TARIFF, tariffAddInfo.getTariff().getId())
			.set(TARIFF_ADDINFO.ATTRIBUTE, tariffAddInfo.getAttribute())
			.set(TARIFF_ADDINFO.VALUE, tariffAddInfo.getValue())
			.set(TARIFF_ADDINFO.VALUE_DATE, AonDateUtils.toSql(tariffAddInfo.getDate()))
			.returning(TARIFF_ADDINFO.ID)
			.fetchOne()
			.getValue(TARIFF_ADDINFO.ID);
		tariffAddInfo.setId(id);
		ctx.log().debug("INSERT TARIFF_ADDINFO id: {0}", tariffAddInfo.getId());		
		return tariffAddInfo;
	}
	
	public static TariffAddInfo update(AONContext ctx, TariffAddInfo tariffAddInfo) {
		ctx.checkWrite();
		int count = ctx.getDslContext().update(TARIFF_ADDINFO)
			.set(TARIFF_ADDINFO.TARIFF, tariffAddInfo.getTariff().getId())
			.set(TARIFF_ADDINFO.ATTRIBUTE, tariffAddInfo.getAttribute())
			.set(TARIFF_ADDINFO.VALUE, tariffAddInfo.getValue())
			.set(TARIFF_ADDINFO.VALUE_DATE, AonDateUtils.toSql(tariffAddInfo.getDate()))
			.where(TARIFF_ADDINFO.ID.eq(tariffAddInfo.getId()))
			.execute();
		ctx.log().debug("UPDATE TARIFF_ADDINFO id: {0}. ({1} rows)", tariffAddInfo.getId(), count);
		return tariffAddInfo;
	}

	public static void deleteTariffAddInfo(CloseableAONContext ctx, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(TARIFF_ADDINFO)
			.where(TARIFF_ADDINFO.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE TARIFF_ADDINFO id: {0} ({1} rows)", id, count);
	}
	
	public static class TariffAddInfoFiller extends Filler implements Function<Record, TariffAddInfo> {

		@Override
		public TariffAddInfo apply(Record r) {
			return build(r);
		}
		
		public static TariffAddInfo build(Record r) {
			return new TariffAddInfo()
				.setId(r.getValue(TARIFF_ADDINFO.ID))
				.setDomain(r.getValue(TARIFF_ADDINFO.DOMAIN))
				.setTariff(TariffFiller.build(r))
				.setAttribute(r.getValue(TARIFF_ADDINFO.ATTRIBUTE))
				.setValue(r.getValue(TARIFF_ADDINFO.VALUE))
				.setDate(r.getValue(TARIFF_ADDINFO.VALUE_DATE))
				;
		}
	}

	public static List<TariffCatalogue> getTariffCatalgueList(CloseableAONContext ctx, Integer tariffId) {
		List<TariffCatalogue> tariffCatalogueList = ctx.getDslContext().select()
				.from(TARIFF_CATALOGUE)
				.join(TARIFF).on(TARIFF.ID.eq(TARIFF_CATALOGUE.TARIFF))
				.join(CATALOGUE).on(CATALOGUE.ID.eq(TARIFF_CATALOGUE.CATALOGUE))
				.where(TARIFF_CATALOGUE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.and(TARIFF_CATALOGUE.TARIFF.eq(tariffId))
				.fetch()
				.stream()
				.map(new TariffCatalogueFiller())
				.collect(Collectors.toList());
			
			return tariffCatalogueList;
	}

	public static TariffCatalogue saveTariffCatalogue(CloseableAONContext ctx, TariffCatalogue tariffCatalogue) {
		return tariffCatalogue.getId() == null ? insert(ctx, tariffCatalogue) : update(ctx, tariffCatalogue);
	}
	
	public static TariffCatalogue insert(AONContext ctx, TariffCatalogue tariffCatalogue) {
		ctx.checkWrite();
		validate(ctx, tariffCatalogue);
		Integer id = ctx.getDslContext().insertInto(TARIFF_CATALOGUE)
			.set(TARIFF_CATALOGUE.DOMAIN, tariffCatalogue.getDomain())
			.set(TARIFF_CATALOGUE.TARIFF, tariffCatalogue.getTariff().getId())
			.set(TARIFF_CATALOGUE.CATALOGUE, tariffCatalogue.getCatalogue().getId())
			.returning(TARIFF_CATALOGUE.ID)
			.fetchOne()
			.getValue(TARIFF_CATALOGUE.ID);
		tariffCatalogue.setId(id);
		ctx.log().debug("INSERT TARIFF_CATALOGUE id: {0}", tariffCatalogue.getId());		
		return tariffCatalogue;
	}
	
	public static TariffCatalogue update(AONContext ctx, TariffCatalogue tariffCatalogue) {
		ctx.checkWrite();
		validate(ctx, tariffCatalogue);
		int count = ctx.getDslContext().update(TARIFF_CATALOGUE)
			.set(TARIFF_CATALOGUE.TARIFF, tariffCatalogue.getTariff().getId())
			.set(TARIFF_CATALOGUE.CATALOGUE, tariffCatalogue.getCatalogue().getId())
			.where(TARIFF_CATALOGUE.ID.eq(tariffCatalogue.getId()))
			.execute();
		ctx.log().debug("UPDATE TARIFF_CATALOGUE id: {0}. ({1} rows)", tariffCatalogue.getId(), count);
		return tariffCatalogue;
	}

	public static void deleteTariffCatalogue(CloseableAONContext ctx, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(TARIFF_CATALOGUE)
			.where(TARIFF_CATALOGUE.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE TARIFF_CATALOGUE id: {0} ({1} rows)", id, count);
	}	
	
	private static BiConsumer<TariffCatalogue, AONContext> EMPTY_CATALOGUE = (tariffCatalogue, ctx) -> {
		if (tariffCatalogue.getCatalogue() == null || tariffCatalogue.getCatalogue().getId() == null) 
			throw new AonCoreException("El campo cat\u00e1logo es obligatorio");
	};
	
	private static void validate(AONContext ctx, TariffCatalogue tariffCatalogue) throws AonCoreException{
		EMPTY_CATALOGUE.accept(tariffCatalogue, ctx);
	}
	
	public static class TariffCatalogueFiller extends Filler implements Function<Record, TariffCatalogue> {

		@Override
		public TariffCatalogue apply(Record r) {
			return build(r);
		}
		
		public static TariffCatalogue build(Record r) {
			return new TariffCatalogue()
				.setId(r.getValue(TARIFF_CATALOGUE.ID))
				.setDomain(r.getValue(TARIFF_CATALOGUE.DOMAIN))
				.setTariff(TariffFiller.build(r))
				.setCatalogue(CatalogueFiller.build(r))
				;
		}
	}

}
