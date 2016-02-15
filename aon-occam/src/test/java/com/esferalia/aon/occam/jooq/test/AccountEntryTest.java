package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class AccountEntryTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "inelco-mac.ecastellano.dev";
	private static int DOMAIN_ID = 400;
	private static String USER = "jgarcia";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	
	// ACCOUNT ENTRY	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDomain() {
		AccountEntry accountEntry = new AccountEntry();
		AON.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDate() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		AON.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyPeriod() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		AON.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testWrongDomain() {
		AccountPeriod period = AON.fetchPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			AON.insert(ctx, period);
		}
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(100); // Other
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		accountEntry.setPeriod(period.getId());
		AON.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyTypeInsert() {
		AccountPeriod period = AON.fetchPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			AON.insert(ctx, period);
		}
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		accountEntry.setPeriod(period.getId());
		AON.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
	@Test
	@Ignore
	public void testInsert() {
		AccountPeriod period = AON.fetchPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			AON.insert(ctx, period);
		}
		Date now = new Date();
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId()); // Other
		ae.setEntryDate( AonDateUtils.getSqlDate(1974, 5, 4) );
		ae.setPeriod(period.getId());
		ae.setEntryType( AccountEntryType.MANUAL );
		ae.setConfidential(false);
		ae.addDetail( getAccountEntryDetail(622849,"640000000","Sueldos y salarios","Nominas febrero",3151.49,0,null,null,null,null) );
		ae.addDetail( getAccountEntryDetail(622855,"642000000","Seguridad Social a cargo de la empresa.","Nominas febrero",617.42,0,null,null,null,null) );
		ae.addDetail( getAccountEntryDetail(830539,"476000001","Seguridad social, regimen general","Nominas febrero",0,758.86,null,null,null,null) );
		ae.addDetail( getAccountEntryDetail(830540,"476000002","Seguridad social acreedora, reta","Nominas febrero",0,281.84,null,null,null,null) );
		ae.addDetail( getAccountEntryDetail(622581,"475100000","Hacienda Pública, acreedora por retenciones practicadas.","Nominas febrero",0,79.51,null,null,null,null) );
		ae.addDetail( getAccountEntryDetail(622558,"465000000","Remuneraciones pendientes de pago.","Nominas febrero",0,2648.7,null,null,null,null) );
		for (int i = 0; i < 1000 ; i++) {
			AON.save(DOMAIN_NAME, DOMAIN_ID, USER, ae);
		}
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ");
		
	}
	
//	@Test
//	public void testUpdate() {
//		AccountPeriod period = AON.fetchPeriod(ctx,
//				ACCOUNT_PERIOD.NAME.equal("1974")
//				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
//				);
//		if (period != null) {
//			AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.ID);
//			Record1<Integer> record = ctx.getDslContext()
//				.select(maxFunc)
//				.from(ACCOUNT_ENTRY)
//				.where(ACCOUNT_ENTRY.DOMAIN.equal(period.getDomain()))
//				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period.getId()))
//				.fetchOne();
//			if (record != null) {
//				Integer maxValue = record.getValue(maxFunc);
//				AccountEntry ae = AON.fetchOneAccountEntry(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
//				ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
//				AON.update(ctx, ae);
//			}
//		}
//	}

//	@Test
//	public void testDelete() {
//		AccountPeriod period = AON.fetchPeriod(ctx,
//				ACCOUNT_PERIOD.NAME.equal("1974")
//				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
//				);
//		if (period != null) {
//			AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.ID);
//			Record1<Integer> record = ctx.getDslContext()
//				.select(maxFunc)
//				.from(ACCOUNT_ENTRY)
//				.where(ACCOUNT_ENTRY.DOMAIN.equal(period.getDomain()))
//				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period.getId()))
//				.fetchOne();
//			if (record != null) {
//				Integer maxValue = record.getValue(maxFunc);
//				AccountEntry ae = AON.fetchOneAccountEntry(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
//				ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
//				AON.delete(ctx, ae);
//			}
//		}
//	}

	@Test
	public void testFetch() {
		System.out.println(" ------- testFetch" );
		Stream<AccountEntry> list = AON.getAccountEntries(ctx
				,p -> p.getDomainProperty().eq(ctx.getDomainId())
				,0,10);
		list.forEach(accountEntry -> System.out.println(accountEntry) );
		System.out.println(" -----------------" );
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

	private static AccountEntryDetail getAccountEntryDetail(Integer account, String accountCode,
			String accountDescription, String concept, double debit,
			double credit, Integer balancingAccount,
			String balancingAccountCode, String balancingAccountDescription,
			String documentNumber) {
		return new AccountEntryDetail()
			.setAccount(account)
			.setAccountCode(accountCode)
			.setAccountDescription(accountDescription)
			.setConcept(concept)
			.setDebit(debit)
			.setCredit(credit)
			.setBalancingAccount(account)
			.setBalancingAccountCode(accountCode)
			.setBalancingAccountDescription(accountDescription)
			.setDocumentNumber(documentNumber);
	}	
	
}
