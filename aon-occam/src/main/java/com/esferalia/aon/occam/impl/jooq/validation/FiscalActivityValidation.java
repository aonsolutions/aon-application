package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.FsActivity.FS_ACTIVITY;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalActivityValidation {

	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<FiscalActivity,AONContext> EMPTY_DOMAIN = (fa,ctx) -> {
		if (fa.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};

	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<FiscalActivity,AONContext> EMPTY_YEAR = (fa,ctx) -> {
		if (fa.getYear() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};

	/**
	 * La fecha del asiento es un dato obligatorio.
	 */
	public static BiConsumer<FiscalActivity,AONContext> EMPTY_EPIGRAPH = (fa,ctx) -> {
		if (AonStringUtils.isBlank(fa.getEpigraph()))
			throw new AonCoreException(AonError.EMPTY_EPIGRAPH.getMessage());
	};

	/**
	 * No puede haber dos ep?grafes iguales en el mismo a?o.
	 */
	public static BiConsumer<FiscalActivity,AONContext> DUPLICATE_EPIGRAPH = (fa,ctx) -> {
		int count = ctx.getDslContext().selectCount()
			.from(FS_ACTIVITY)
			.where(FS_ACTIVITY.DOMAIN.eq(fa.getDomain()))
			.and(FS_ACTIVITY.YEAR.eq(fa.getYear()))
			.and(FS_ACTIVITY.EPIGRAPH.eq(fa.getEpigraph()))
			.and( fa.getId()!=null
				?FS_ACTIVITY.ID.ne(fa.getId())
				:FS_ACTIVITY.ID.eq(FS_ACTIVITY.ID))
			.fetchOne(0,int.class);
		if (count>0) {
			throw new AonCoreException(AonError.DUPLICATE_EPIGRAPH.format(fa.getYear(),fa.getEpigraph()));
		}
	};

	public static void validate(AONContext ctx, FiscalActivity fa)
			throws AonCoreException {
		
		
		EMPTY_DOMAIN
			.andThen(EMPTY_EPIGRAPH)
			.andThen(EMPTY_YEAR)
			.andThen(DUPLICATE_EPIGRAPH)
			.accept(fa, ctx);

	}

}
