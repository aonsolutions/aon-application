package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.function.BiConsumer;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class FiscalModelValidation {

	/**
	 * El dominio no puede estar vacio.
	 */
	public static BiConsumer<FiscalModel,AONContext> EMPTY_DOMAIN = (fm,ctx) -> {
		if (fm.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El ejercicio no puede estar vacio.
	 */
	public static BiConsumer<FiscalModel,AONContext> EMPTY_YEAR = (fm,ctx) -> {
		if (fm.getYear() == null) 
			throw new AonCoreException(AonError.EMPTY_YEAR.getMessage());
	};

	/**
	 * El ejercicio debe ser válido.
	 */
	public static BiConsumer<FiscalModel,AONContext> INVALID_YEAR = (fm,ctx) -> {
		if (fm.getYear() < 2005 || fm.getYear() > 2025) 
			throw new AonCoreException(AonError.INVALID_YEAR.getMessage());
	};

	/**
	 * El periodo no puede estar vacio.
	 */
	public static BiConsumer<FiscalModel,AONContext> EMPTY_PERIOD = (fm,ctx) -> {
		if (fm.getPeriod() == null) 
			throw new AonCoreException(AonError.EMPTY_PERIOD.getMessage());
	};

	public static BiConsumer<FiscalModel,AONContext> SAME_PERIOD_EXISTS_CHECK = (fm,ctx) -> {
		if (fm.isReplacement()) {
			// Se comprueba que no exista una decl. sustitutiva.
			SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(FS_MODEL)
				.where(FS_MODEL.DOMAIN.equal(fm.getDomain()))
				.and(FS_MODEL.MODEL.eq( fm.getModel().getValue()))
				.and(FS_MODEL.YEAR.equal(fm.getYear()))
				.and(FS_MODEL.PERIOD.eq( fm.getPeriod().getValue() ))
				.and(FS_MODEL.REPLACEMENT.equal((byte) 1));
			if (fm.getId() != null) {
				select.and(FS_MODEL.ID.ne(fm.getId()));	
			}
			if (ctx.getDslContext().fetchCount(select) == 0) {
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
			}
		} else {
			
			// Se comprueba que no exista ya una decl.
			SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(FS_MODEL)
				.where(FS_MODEL.DOMAIN.equal(fm.getDomain()))
				.and(FS_MODEL.MODEL.eq( fm.getModel().getValue()))
				.and(FS_MODEL.YEAR.equal(fm.getYear())
				.and(FS_MODEL.PERIOD.eq( fm.getPeriod().getValue() ))
				.and(FS_MODEL.REPLACEMENT.equal((byte) 0)));
			if (fm.getId() != null) {
				select.and(FS_MODEL.ID.ne(fm.getId()));	
			}
			if (ctx.getDslContext().fetchCount(select) > 0 )  
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	};

	public static void validate(AONContext ctx, FiscalModel fm) {
		EMPTY_DOMAIN
		.andThen(EMPTY_YEAR)
		.andThen(INVALID_YEAR)
		.andThen(EMPTY_PERIOD)
		.andThen(SAME_PERIOD_EXISTS_CHECK)
		.accept(fm, ctx);
	}

}
