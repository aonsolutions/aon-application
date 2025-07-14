package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Date;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class BankStatementDAO {
	
	private BankStatementDAO() {
		
	}

	private static final Field<java.sql.Date> MAX_DATE = DSL.max(BANK_STATEMENT.OPERATION_DATE);
	

	public static Date getLastMovementDate(AONContext ctx, Integer rbankId) {
		Record1<java.sql.Date> bankStatement = ctx.getDslContext()
			.select(MAX_DATE)
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbankId))
			.limit(1)
			.fetchOne();
		
		return null == bankStatement.getValue(MAX_DATE) ? null : new Date(bankStatement.getValue(MAX_DATE).getTime());
	}

	static int getNextLotNumber(AONContext ctx, Integer domainId, RegistryBank rbank) {
		AggregateFunction<Integer> lot = DSL.max(BANK_STATEMENT.LOT_NUMBER);
		int lotNumber = ctx.getDslContext().select( lot )
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.filter( rec -> rec.getValue(lot) != null)
			.mapToInt( rec -> rec.getValue(lot))
			.findFirst()
			.orElse(0);
		return ++lotNumber;  
	}
	
	
	public static List<BankStatement> getIncorrectMovements(AONContext ctx , Integer rbankId , Date operationDate ,  double amount, String concept) {
		return ctx.getDslContext().select().from(BANK_STATEMENT).where(BANK_STATEMENT.RBANK.eq(rbankId))
		.and(BANK_STATEMENT.OPERATION_DATE.eq(new java.sql.Date(operationDate.getTime())))
		.and(BANK_STATEMENT.AMOUNT.eq(amount))
		.and(BANK_STATEMENT.STATUS.eq((byte) 0))
		.and( BANK_STATEMENT.OWN_CONCEPT.likeIgnoreCase("%" + concept + "%")
		.or(BANK_STATEMENT.DESCRIPTION.likeIgnoreCase("%" + concept + "%")))
		.fetchInto(BankStatement.class);
	}
	
	
}
