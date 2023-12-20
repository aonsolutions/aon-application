package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Date;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class BankStatementDAO {
	
	private BankStatementDAO() {
		
	}

	private static final Field<java.sql.Date> MAX_DATE = DSL.max(BANK_STATEMENT.OPERATION_DATE);
	

	public static Date getLastMovementDate(AONContext ctx, Integer rbankId) {
		return ctx.getDslContext()
			.select(MAX_DATE)
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbankId))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(MAX_DATE))
			.map(date -> new Date(date.getTime()))
			.findFirst()
			.orElse( null );
	}

	static int getNextLotNumber(AONContext ctx, Integer domainId, RegistryBank rbank) {
		AggregateFunction<Integer> lot = DSL.max(BANK_STATEMENT.LOT_NUMBER);
		return ctx.getDslContext().select( lot )
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.mapToInt( rec -> rec.getValue(lot))
			.findFirst()
			.orElse(1);
	}
	
}
