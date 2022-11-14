package com.esferalia.aon.occam.test.ql;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import org.jooq.Condition;
import org.jooq.conf.ParamType;
import org.junit.Test;

import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class AonOccamQLTest extends AbstractOccamTest {

	@Test
	public void test() throws ExpressionException {
		Condition cond = new JOOQRenderer()
			.put(INVOICE.RNAME, "Toled*")
			.put(INVOICE.ID, "2757|2875")
			.getCondition();
		
		System.out.println( 
			ctx.getDslContext()
				.select( INVOICE.ID )
				.from(INVOICE)
				.where(cond)
				.getSQL(ParamType.INLINED)
		);
	}
	
}
