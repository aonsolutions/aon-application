package com.esferalia.aon.occam.jooq.test;


import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.impl.DSL;
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
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLine;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class AccountEntryTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "alhymotion-mac.ecastellano.dev";
	private static int DOMAIN_ID = 3034;

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
		AON.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDate() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		AON.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyPeriod() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		AON.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testWrongDomain() {
		AccountPeriod period = AON.fetchPeriod(ctx,
				ACCOUNT_PERIOD.NAME.equal("1974")
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
				);
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
		accountEntry.setAccountPeriod(period.getId());
		AON.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyTypeInsert() {
		AccountPeriod period = AON.fetchPeriod(ctx,
				ACCOUNT_PERIOD.NAME.equal("1974")
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
				);
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
		accountEntry.setAccountPeriod(period.getId());
		AON.insert(ctx, accountEntry);
	}
	
	@Test
	@Ignore
	public void testInsert() {
		AccountPeriod period = AON.fetchPeriod(ctx,
				ACCOUNT_PERIOD.NAME.equal("1974")
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
				);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			AON.insert(ctx, period);
		}
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId()); // Other
		ae.setEntryDate( AonDateUtils.getSqlDate(1974, 5, 4) );
		ae.setAccountPeriod(period.getId());
		ae.setEntryType( AccountEntryType.MANUAL );
		ae.setConfidential(false);
		ae.addDetail( new AccountEntryDetail(622849,"640000000","Sueldos y salarios","Nominas febrero",3151.49,0,null,null,null,null) );
		ae.addDetail( new AccountEntryDetail(622855,"642000000","Seguridad Social a cargo de la empresa.","Nominas febrero",617.42,0,null,null,null,null) );
		ae.addDetail( new AccountEntryDetail(830539,"476000001","Seguridad social, regimen general","Nominas febrero",0,758.86,null,null,null,null) );
		ae.addDetail( new AccountEntryDetail(830540,"476000002","Seguridad social acreedora, reta","Nominas febrero",0,281.84,null,null,null,null) );
		ae.addDetail( new AccountEntryDetail(622581,"475100000","Hacienda Pública, acreedora por retenciones practicadas.","Nominas febrero",0,79.51,null,null,null,null) );
		ae.addDetail( new AccountEntryDetail(622558,"465000000","Remuneraciones pendientes de pago.","Nominas febrero",0,2648.7,null,null,null,null) );
		Date now = new Date();
		AON.insert(ctx, ae);
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ( "+ae.getDetails().size() + " rows ) " + ae.getId());
	}
	
	@Test
	@Ignore
	public void testUpdate() {
		AccountPeriod period = AON.fetchPeriod(ctx,
				ACCOUNT_PERIOD.NAME.equal("1974")
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
				);
		if (period != null) {
			AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.ID);
			Record1<Integer> record = ctx.getDslContext()
				.select(maxFunc)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.equal(period.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period.getId()))
				.fetchOne();
			if (record != null) {
				Integer maxValue = record.getValue(maxFunc);
				AccountEntry ae = AON.fetchOneAccountEntry(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
				ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
				AON.update(ctx, ae);
			}
		}
	}

	@Test
	@Ignore
	public void testDelete() {
		AccountPeriod period = AON.fetchPeriod(ctx,
				ACCOUNT_PERIOD.NAME.equal("1974")
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId()))
				);
		if (period != null) {
			AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.ID);
			Record1<Integer> record = ctx.getDslContext()
				.select(maxFunc)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.equal(period.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period.getId()))
				.fetchOne();
			if (record != null) {
				Integer maxValue = record.getValue(maxFunc);
				AccountEntry ae = AON.fetchOneAccountEntry(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
				ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
				AON.delete(ctx, ae);
			}
		}
	}

	@Test
	@Ignore
	public void testFetch() {
		Condition condition = ACCOUNT_ENTRY.DOMAIN.equal(ctx.getDomainId());
		
		Stream<AccountEntry> list = AON.fetchAccountEntry(ctx, condition,0,10);
		list.forEach(accountEntry -> System.out.println(""+ accountEntry.getId()) );
		System.out.println( "************************************" );
		list = AON.fetchAccountEntry(ctx, condition,10,10);
		list.forEach(accountEntry -> System.out.println(""+ accountEntry.getId()) );
		System.out.println( "************************************" );
	}

	@Test
	@Ignore
	public void testFetchCSV() {
		Condition condition = ACCOUNT_ENTRY.DOMAIN.equal(ctx.getDomainId());
		System.out.println( AON.fetchAccountEntryCSV(ctx, condition,0,10) );
	}

	@Test
	@Ignore
	public void testSalaryAccountEntry() {
		
		SalaryAccountEntry sae = new SalaryAccountEntry();
		sae.setDate( AonDateUtils.getDate(2014, 0, 1));
		sae.setConcept("APUNTE NÓMINA");
		sae.setSecurityLevel(SecurityLevel.CONFIDENTIAL);
		
		// SI RegistryBank --> Se cuadra el apunte contra "Remuneraciones pendientes de pago"
		// en caso contrario contra la cuenta de banco.
		// TODO soporte a CAJA.
		sae.setRegistryBank(null);
		// --------------------------------------------------------------------
		
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.SALARY,null,3151.49));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.SALARY_IN_KIND,null,1000)); 
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.ALLOWANCE,null,2000));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.COMPENSATION,null,1500));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.RETENTION,null,79.51));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.RETENTION_IN_KIND,null,15));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.EMPLOYEE_SOC_INS,null,758.86));
		sae.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.COMPANY_SOC_INS,null,617.42));
		AON.insertSalaryEntry(ctx , sae);
	}

	@Test
	public void testSalaryAccountEntryFromSalary() {
		String domainName = "escolaglobal-equipu.ecastellano.dev";
		Integer domainId = 1051;
		AONContext salaryCTX = AONContext.getAONContext(domainName, domainId);
		try {
			Integer enterprise = 147499;
			Date from = AonDateUtils.getDate(2014, 9, 01);
			Date to = AonDateUtils.getDate(2014, 9, 31);
			String concept = "Nóminas OCTUBRE";
			Integer registryBank = null;
			AON.insertSalaryEntry(salaryCTX,enterprise,from,to,concept,registryBank);
		} finally {
			salaryCTX.finalize();
		}
	}
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
