package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Location.LOCATION;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlReason;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class LocationValidation {
	
	private LocationValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, Location> NULL = (ctx, location) -> {
		if (location == null)
			throw new AonCoreException(AonError.MARKETING_ACTION_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, Location> EMPTY_DOMAIN = (ctx, location) -> {
		if (location != null && location.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, Location> NULL_DESCRIPTION = (ctx, location) -> {
		if (location != null && AonStringUtils.isBlank(location.getDescription()))
			throw new AonCoreException("La descripci\u00f3n de la ubicaci\u00f3n es nula.");
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, Location> NULL_RADIO = (ctx, location) -> {
		if (location != null && null == location.getRadio())
			throw new AonCoreException("El radio de la ubicaci\u00f3n es nulo.");
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, Location> NULL_TYPE = (ctx, location) -> {
		if (location != null && null == location.getType())
			throw new AonCoreException("El tipo de la ubicaci\u00f3n es nulo.");
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, Location> NULL_REGISTRY_TYPE = (ctx, location) -> {
		if (location != null && (null != location.getType() && !location.getType().equals(TimeControlReason.DISPLACED) && null == location.getRegistry()) )
			throw new AonCoreException("Operario obligatorio si no es desplazado.");
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, Location> NULL_COORDINATES = (ctx, location) -> {
		if (location != null && null == location.getCoordinates())
			throw new AonCoreException("Las coordenadas de la ubicaci\u00f3n son nulas.");
	};
	
	
	public static void validate(AONContext ctx, Location location) throws AonCoreException{
		NULL
		.andThen(EMPTY_DOMAIN)
		.andThen(NULL_DESCRIPTION)
		.andThen(NULL_RADIO)
		.andThen(NULL_TYPE)
		.andThen(NULL_REGISTRY_TYPE)
		.andThen(NULL_COORDINATES)
		.accept(ctx, location);
	}
	
	/**
	 * Checks if an alias is repeated
	 * @param ctx the context
	 * @param question the question
	 * @return true if the alias is repeated, false otherwise
	 */
	public static boolean checkDescription(AONContext ctx, Location location) {
		Condition condition = location.getId() == null ? DSL.trueCondition() : LOCATION.ID.ne(location.getId());
		List<Record> questionRecords = ctx.getDslContext().select().from(LOCATION)
			.where(LOCATION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(LOCATION.DESCRIPTION.eq(location.getDescription()))
		 	.and(condition)
			.fetch();
			
		return !questionRecords.isEmpty();
	}

}
