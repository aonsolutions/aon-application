package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import org.jooq.Condition;
import org.jooq.Field;

import com.esferalia.aon.occam.api.AONContext;

public class DAOUtilities {

	public static Condition getHeritableDomainCondition(AONContext ctx, Field<Integer> field, int domain) {
		return field.eq(domain)
			.or(field.eq(ctx.getDslContext()
				.select(DOMAIN.PARENT)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.and(DOMAIN.ENABLEHEREDITY.eq((byte)1))));
		
	}

}
