package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.Payment;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.payroll.calculator.DomainPayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLAgreementPaymentsFactoryTestCase extends AbstractSQLTestCase {

	@Test
	public void testCreate() throws SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		int parentId =  (int)System.currentTimeMillis();
		int overrideId = parentId +1;
		
		aonContext.getDslContext()
		.insertInto(DOMAIN)
		.set(DOMAIN.ID, parentId)
		.set(DOMAIN.NAME, Integer.toString(parentId))
		.set(DOMAIN.OWNER, Integer.toString(parentId))
		.set(DOMAIN.DESCRIPTION, Integer.toString(parentId))
		.newRecord()
		.set(DOMAIN.ID, overrideId)
		.set(DOMAIN.NAME, Integer.toString(overrideId))
		.set(DOMAIN.OWNER, Integer.toString(overrideId))
		.set(DOMAIN.DESCRIPTION, Integer.toString(overrideId))
		.execute();
		
		
		Agreement agreement = new Agreement();
		agreement.setId(parentId);
		agreement.setDomain(parentId);
		
		Date startDate = AonDateUtils.getYearFirstDay(new Date());
		
		

		agreement.addPayment (new com.esferalia.aon.occam.api.model.Payment()
		.setDomain(parentId)
		.setStartDate(startDate)
		.setExpression("BASE_PARENT")
		.setCode("BASE"));

		agreement.addPayment(new com.esferalia.aon.occam.api.model.Payment()
		.setDomain(parentId)
		.setStartDate(startDate)
		.setCode("EXTRA")
		.setExpression("EXTRA_PARENT")
		.setType(PaymentType.CRA_0004));

		agreement.addPayment(new com.esferalia.aon.occam.api.model.Payment()
		.setDomain(parentId)
		.setStartDate(startDate)
		.setCode("PLUS")
		.setExpression("PLUS_PARENT"));

		agreement.addPayment(new com.esferalia.aon.occam.api.model.Payment()
		.setDomain(overrideId)
		.setStartDate(startDate)
		.setCode("EXTRA")
		.setExpression("EXTRA_OVERRIDE")
		.setType(PaymentType.CRA_0004));
		
		agreement.addPayment(new com.esferalia.aon.occam.api.model.Payment()
		.setDomain(overrideId)
		.setStartDate(AonDateUtils.addDays(startDate, 10))
		.setCode("PLUS")
		.setExpression("PLUS_OVERRIDE"));

		AON.saveAgreement(aonContext, agreement);
		
		Date endDate = AonDateUtils.getMonthLastDay(startDate);

		SQLAgreementPaymentsFactory factory = new SQLAgreementPaymentsFactory(
				connection, startDate, endDate);
		
		AgreementKey agreementKey = new AgreementKey(parentId, parentId);
		Collection<ISystemPayment> parentPayments= factory.create(agreementKey);
		assertEquals(parentPayments.size(), 3);
		for (IContractPayment contractPayment : parentPayments) {
			assertEquals( contractPayment.getName() + "_PARENT" , contractPayment.getExpression());
		}

		agreementKey = new AgreementKey(parentId, overrideId);
		Collection<ISystemPayment> overridePayments= factory.create(agreementKey);
		assertEquals(overridePayments.size(), 2);
		for (IContractPayment contractPayment : overridePayments) {
			assertEquals( contractPayment.getName() + "_OVERRIDE" , contractPayment.getExpression());
		}
		
		
		Collection<ISystemPayment> payments = new DomainPayments(overridePayments, parentPayments );
		for (IContractPayment payment : payments) {
			if ( AonStringUtils.equals(payment.getName(), "BASE")) 
				assertEquals( "BASE_PARENT" , payment.getExpression());
			else if ( AonStringUtils.equals(payment.getName(), "EXTRA")) 
				assertEquals( "EXTRA_OVERRIDE" , payment.getExpression());
			
		}
		
	}
}
