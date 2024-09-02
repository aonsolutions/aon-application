package com.esferalia.aon.occam.test.ql;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jooq.Condition;
import org.jooq.conf.ParamType;
import org.junit.jupiter.api.Test;

import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.occam.impl.jooq.ql.JOOQRenderer;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class AonOccamQLTest extends AbstractOccamTest {

	@Test
	public void test() throws ExpressionException {
		Condition cond = new JOOQRenderer()
			.put(INVOICE.RNAME, "Toled*")
			.put(INVOICE.ID, "2757|2875")
			.getCondition();
		assertEquals(
			"select `invoice`.`id` from `invoice` where (`invoice`.`rname` like 'Toled%' and (`invoice`.`id` = 2757 or `invoice`.`id` = 2875))"
			,ctx.getDslContext()
				.select( INVOICE.ID )
				.from(INVOICE)
				.where(cond)
				.getSQL(ParamType.INLINED)
		);
	}
	
}
