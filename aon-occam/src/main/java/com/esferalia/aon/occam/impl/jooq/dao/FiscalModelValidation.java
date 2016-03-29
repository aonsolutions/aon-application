package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalModelValidation {

	private static final byte ZERO = 0;
	 
	/**
	 * El dominio no puede estar vacio.
	 */
	public static BiConsumer<FiscalModel,AONContext> EMPTY_DOMAIN = (fm,ctx) -> {
		if (fm.getDomain() == 0) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El ejercicio no puede estar vacio.
	 */
	public static BiConsumer<FiscalModel,AONContext> EMPTY_YEAR = (fm,ctx) -> {
		if (fm.getYear() == 0) 
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
		if (!fm.isReplacement() && !fm.isComplementary()
			&& ctx.getDslContext()
				.select()
				.from(FS_MODEL)
				.where(FS_MODEL.DOMAIN.equal(fm.getDomain()))
					.and(FS_MODEL.MODEL.eq( fm.getModel().getValue()))
					.and(FS_MODEL.YEAR.equal(fm.getYear()))
					.and(FS_MODEL.PERIOD.eq( fm.getPeriod().getValue() ))
					.and( fm.getModel() == FiscalModelType.M130
							?FS_MODEL.DOCUMENT.eq( fm.getDocument() )
							:DSL.trueCondition() )
					.and(FS_MODEL.ADMINISTRATION.eq( fm.getAdministration().getValue() ))
					.and(FS_MODEL.REPLACEMENT.equal( ZERO ))
					.and(FS_MODEL.COMPLEMENTARY.equal( ZERO ))
					.and((fm.isNew())?DSL.trueCondition():FS_MODEL.ID.ne(fm.getId()))
				.fetch()
				.stream()
				.findFirst()
				.isPresent() ) {
			throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	};

	/**
	 * El telefono debe tener nueve caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> CONTACT_CELLULAR = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getContactCellular()) > 9  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Tel\u00E9fono de Contacto", "9" ));
	};

	/**
	 * El telefono debe tener nueve caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> CONTACT_PHONE = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getContactPhone()) > 9  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Tel\u00E9fono de Contacto", "9" ));
	};

	public static void validate(AONContext ctx, FiscalModel fm) {
		EMPTY_DOMAIN
		.andThen(EMPTY_YEAR)
		.andThen(INVALID_YEAR)
		.andThen(EMPTY_PERIOD)
		.andThen(CONTACT_CELLULAR)
		.andThen(CONTACT_PHONE)
		.andThen(SAME_PERIOD_EXISTS_CHECK)
		.accept(fm, ctx);
	}

}
