package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
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
		if (fm.getStatus() != FiscalStatus.BLOCKED && !fm.isReplacement() && !fm.isComplementary()
			&& ctx.getDslContext()
				.select()
				.from(FS_MODEL)
				.where(FS_MODEL.DOMAIN.equal(fm.getDomain()))
					.and(FS_MODEL.MODEL.eq( fm.getModel().getValue()))
					.and(FS_MODEL.YEAR.equal(fm.getYear()))
					.and(FS_MODEL.PERIOD.eq( fm.getPeriod().getValue() ))
					.and( fm.getModel().isOtherDeponentAllowedInSamePeriod()
							?FS_MODEL.DOCUMENT.eq( fm.getDocument() )
							:DSL.trueCondition() )
					.and(FS_MODEL.ADMINISTRATION.eq( fm.getAdministration().getValue() ))
					.and(FS_MODEL.REPLACEMENT.equal( ZERO ))
					.and(FS_MODEL.COMPLEMENTARY.equal( ZERO ))
					.and(FS_MODEL.STATUS.notEqual( (byte) FiscalStatus.BLOCKED.ordinal() ))
					.and((fm.isNew())?DSL.trueCondition():FS_MODEL.ID.ne(fm.getId()))
				.fetch()
				.stream()
				.findFirst()
				.isPresent() ) {
			throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	};

	public static BiConsumer<FiscalModel,AONContext> SOMETHING_TO_COMPLEMENT = (fm,ctx) -> {
		if (fm.getStatus() != FiscalStatus.BLOCKED 
			&& (fm.isReplacement() || fm.isComplementary())
			&& !(ctx.getDslContext()
				.select()
				.from(FS_MODEL)
				.where(FS_MODEL.DOMAIN.equal(fm.getDomain()))
					.and(FS_MODEL.MODEL.eq( fm.getModel().getValue()))
					.and(FS_MODEL.YEAR.equal(fm.getYear()))
					.and(FS_MODEL.PERIOD.eq( fm.getPeriod().getValue() ))
					.and( fm.getModel().isOtherDeponentAllowedInSamePeriod()
							?FS_MODEL.DOCUMENT.eq( fm.getDocument() )
							:DSL.trueCondition() )
					.and(FS_MODEL.ADMINISTRATION.eq( fm.getAdministration().getValue() ))
					.and(FS_MODEL.REPLACEMENT.equal( ZERO ))
					.and(FS_MODEL.COMPLEMENTARY.equal( ZERO ))
					.and(FS_MODEL.STATUS.notEqual( (byte) FiscalStatus.BLOCKED.ordinal() ))
					.and((fm.isNew())?DSL.trueCondition():FS_MODEL.ID.ne(fm.getId()))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) ) {
			throw new AonCoreException(AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
		}
	};

	/**
	 * El Nombre debe tener 45 caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> NAME_LENGTH = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getName()) > 45  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre o raz\u00F3n social", "45" ));
	};
	
	/**
	 * El Documento debe tener 9 caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> DOCUMENT_LENGTH = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getDocument()) > 45  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Documento", "9" ));
	};

	/**
	 * El apellido debe tener 30 caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> SURNAME_LENGTH = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getSurname()) > 45  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Apellidos", "30" ));
	};

	/**
	 * El Numero de calle debe tener 4 caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> STREET_NUMBER_LENGTH = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getStreetNumber()) > 4  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Direcci\u00F3n. N\u00FAmero", "4" ));
	};

	/**
	 * El codigo postal debe tener 5 caracters como maximo.
	 */
	public static BiConsumer<FiscalModel,AONContext> ZIP_LENGTH = (fm,ctx) -> {
		if (AonStringUtils.length( fm.getZip()) > 5  ) 
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "C\u00F3digo postal", "5" ));
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
		.andThen(DOCUMENT_LENGTH)
		.andThen(NAME_LENGTH)
		.andThen(SURNAME_LENGTH)
		.andThen(STREET_NUMBER_LENGTH)
		.andThen(ZIP_LENGTH)
		.andThen(CONTACT_CELLULAR)
		.andThen(CONTACT_PHONE)
		.andThen(SAME_PERIOD_EXISTS_CHECK)
		.andThen(SOMETHING_TO_COMPLEMENT)
		.accept(fm, ctx);
	}

}
