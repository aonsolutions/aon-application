package com.esferalia.aon.occam.test.accounting.period;


import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class DeleteTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriodDAO.getPeriods(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID))
		.forEach(p -> {
			if (ctx.getDslContext().select( DSL.count(ACCOUNT_ENTRY.ID) )
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(p.getId()))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(DSL.count(ACCOUNT_ENTRY.ID)) )
				.findFirst()
				.orElse(0) > 0) {
				AccountPeriod ap = p;
				assertThrows(AonCoreException.class, () -> AccountPeriodDAO.delete(ctx, ap));
			} else {
				AccountPeriodDAO.delete(ctx, p);
				p = ACCOUNTING.getPeriod(ctx, year);
				assertNull(p);
			}
		});
	}

}
