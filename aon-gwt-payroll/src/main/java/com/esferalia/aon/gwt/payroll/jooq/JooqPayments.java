package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Payment;

public class JooqPayments {

	public static List<Payment> getPaymentConcepts(Connection connection,
			Integer domainId, Integer parentDomainId) throws SQLException {
		return getPaymentConcepts(DSL.using(connection, getDefaultSettings()),
				domainId, parentDomainId);
	}

	public static List<Payment> getPaymentConcepts(Connection connection,
			int offset, int limit, Integer domainId, Integer parentDomainId)
			throws SQLException {
		return getPaymentConcepts(DSL.using(connection, getDefaultSettings()),
				domainId, parentDomainId);
	}
		
	public static List<Payment> getPayments(Connection connection,
			Integer domainId, Integer parentDomainId) throws SQLException {
		return getPayments(DSL.using(connection, getDefaultSettings()),
				domainId, parentDomainId);
	}

	private static List<Payment> getPaymentConcepts(DSLContext context,
			Integer domainId, Integer parentDomainId) throws SQLException {
		Cursor<Record> cursor = null;
		
		Collection<Integer> domains = getDomains(0,domainId, parentDomainId);
		
		try {
		//@formatter:off
		cursor = 
		context.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.ID.greaterThan(0)
		.and(PAYMENT_CONCEPT.DOMAIN.in(domains)))
		.orderBy(PAYMENT_CONCEPT.TYPE)
		.fetchLazy();
		//@formatter:on
		
		List<Payment> concepts = new LinkedList<Payment>();
		for ( Record record: cursor) {
			Payment concept = new Payment();
			
			concept.setId(record.getValue(PAYMENT_CONCEPT.ID));
			concept.setName(record.getValue(PAYMENT_CONCEPT.CODE));
			concept.setType(getType(record.getValue(PAYMENT_CONCEPT.TYPE)));
			concept.setDescription(record.getValue(PAYMENT_CONCEPT.DESCRIPTION));
			concept.setExpression(record.getValue(PAYMENT_CONCEPT.EXPRESSION));
			concept.setIrpfExpression(record.getValue(PAYMENT_CONCEPT.IRPF_EXPRESSION));
			concept.setQuoteExpression(record.getValue(PAYMENT_CONCEPT.QUOTE_EXPRESSION));
			
			concepts.add(concept);
		}
		
		return concepts;
		} finally {
			if ( cursor != null )
				cursor.close();
		}
	}

	private static List<Payment> getPayments(DSLContext context,
			Integer domainId, Integer parentDomainId) throws SQLException {
		Cursor<Record> cursor = null;
		
		Collection<Integer> domains = getDomains(0,domainId, parentDomainId);
		
		try {
		//@formatter:off
		cursor = 
		context.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.ID.greaterThan(0)
		.and(SYSTEM_PAYMENT.DOMAIN.in(domains)))
		.orderBy(SYSTEM_PAYMENT.TYPE)
		.fetchLazy();
		//@formatter:on
		
		List<Payment> concepts = new LinkedList<Payment>();
		for ( Record record: cursor) {
			Payment concept = new Payment();
			
			concept.setId(record.getValue(SYSTEM_PAYMENT.ID));
			concept.setConceptId(record.getValue(SYSTEM_PAYMENT.PAYMENT_CONCEPT));
			concept.setType(getType(record.getValue(SYSTEM_PAYMENT.TYPE)));
			concept.setDescription(record.getValue(SYSTEM_PAYMENT.DESCRIPTION));
			concept.setExpression(record.getValue(SYSTEM_PAYMENT.EXPRESSION));
			concept.setIrpfExpression(record.getValue(SYSTEM_PAYMENT.IRPF_EXPRESSION));
			concept.setQuoteExpression(record.getValue(SYSTEM_PAYMENT.QUOTE_EXPRESSION));
			
			concepts.add(concept);
		}
		
		return concepts;
		} finally {
			if ( cursor != null )
				cursor.close();
		}
	}

	private static Payment.Type getType(Byte ordinal) {
		if (ordinal == null || ordinal < 0) {
			return null;
		}

		Payment.Type types[] = Payment.Type.values();

		if (ordinal >= types.length) {
			return null;
		}

		return types[ordinal];
	}
	
	private static Collection<Integer> getDomains(Integer ...domains){
		Collection<Integer> collection = new ArrayList<Integer>(); 
		for ( Integer domain: domains )
			if( domain != null )
				collection.add(domain);
		return collection;
	}
	

}
