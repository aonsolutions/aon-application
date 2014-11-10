package com.esferalia.aon.test;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.esferalia.aon.master.impl.client.AccountEntry;
import com.esferalia.aon.master.impl.client.AccountEntryDetail;
import com.esferalia.aon.master.impl.client.AccountPeriod;
import com.esferalia.aon.master.impl.server.jooq.AccountEntryDAO;
import com.esferalia.aon.master.impl.server.jooq.AccountPeriodDAO;
import com.esferalia.aon.master.impl.server.jooq.DAOContext;
import com.esferalia.aon.master.impl.server.jooq.DAOSecurityContext;
import com.esferalia.aon.master.impl.server.sql.AonDAOException;
import com.esferalia.aon.shared.commons.AonDatabaseUtil;
import com.esferalia.aon.shared.commons.AonDateUtils;


public class AccountingTest {

	private static Connection conn;
	private static Settings SETTINGS = null;
	private static DAOContext ctx;
	private static int DOMAIN_ID = 3034;

	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			SETTINGS.setParamType( ParamType.INLINED );
		}
		return SETTINGS;
	}

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net"
			,"aonsolutions","40ns0lut10ns");
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		DAOSecurityContext securityContext = new DAOSecurityContext( DOMAIN_ID );
		ctx = new DAOContext(dslContext, securityContext);
	}
	
	// ACCOUNT PERIOD
	@Test(expected=AonDAOException.class)
	public void testPeriodEmptyInitiaionDate() {
		AccountPeriod period = new AccountPeriod();
		ctx.getDslContext().transaction( configuration -> {
			AccountPeriodDAO.insert(ctx, period);
		});
	}

	@Test(expected=AonDAOException.class)
	public void testPeriodEmptyDeadline() {
		AccountPeriod period = new AccountPeriod();
		period.setInitiationDate( AonDateUtils.getDate(2014, 0, 1));
		try {
			AccountPeriodDAO.insert(ctx, period);
		} catch (AonDAOException e) {
			throw e;
		}
	}

	@Test(expected=AonDAOException.class)
	public void testPeriodInitialDateOverlap() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974").and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
					);
			if (period == null) {
				period = new AccountPeriod();
				period.setName("1974");
				period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
				period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
				period.setDomain(ctx.getSecurityContext().getDomainId());
				period.setStatus( AccountPeriodStatus.ACTIVE );
				AccountPeriodDAO.insert( ctx.getNested(configuration) , period);
			}
			period = new AccountPeriod();
			period.setName("1975");
			period.setInitiationDate( AonDateUtils.getDate(1974, 5, 1));
			period.setDeadline( AonDateUtils.getDate(1975, 4, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getSecurityContext().getDomainId());
			AccountPeriodDAO.insert(ctx.getNested(configuration), period);
		});
	}

	@Test(expected=AonDAOException.class)
	public void testPeriodDeadlineOverlap() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974")
					.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
					);
			if (period == null) {
				period = new AccountPeriod();
				period.setName("1974");
				period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
				period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
				period.setStatus( AccountPeriodStatus.ACTIVE );
				period.setDomain(ctx.getSecurityContext().getDomainId());
				AccountPeriodDAO.insert(ctx.getNested(configuration), period);
			}
			period = new AccountPeriod();
			period.setName("1973");
			period.setInitiationDate( AonDateUtils.getDate(1973, 5, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 4, 31));
			period.setDomain(ctx.getSecurityContext().getDomainId());
			AccountPeriodDAO.insert(ctx.getNested(configuration), period);
		});
	}
	
	// ACCOUNT ENTRY	
	@Test(expected=AonDAOException.class)
	public void testEmptyDomain() {
		AccountEntry accountEntry = new AccountEntry();
		AccountEntryDAO.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonDAOException.class)
	public void testEmptyDate() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getSecurityContext().getDomainId());
		AccountEntryDAO.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonDAOException.class)
	public void testEmptyPeriod() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getSecurityContext().getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		AccountEntryDAO.insert(ctx, accountEntry);
	}
	
	@Test(expected=AonDAOException.class)
	public void testWrongDomain() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974")
					.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
					);
			if (period == null) {
				period = new AccountPeriod();
				period.setName("1974");
				period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
				period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
				period.setStatus( AccountPeriodStatus.ACTIVE );
				period.setDomain(ctx.getSecurityContext().getDomainId());
				AccountPeriodDAO.insert(ctx.getNested(configuration), period);
			}
			AccountEntry accountEntry = new AccountEntry();
			accountEntry.setDomain(100); // Other
			accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
			accountEntry.setAccountPeriod(period.getId());
			AccountEntryDAO.insert(ctx.getNested(configuration), accountEntry);
		});
	}
	
	@Test(expected=AonDAOException.class)
	public void testEmptyTypeInsert() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974")
					.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
					);
			if (period == null) {
				period = new AccountPeriod();
				period.setName("1974");
				period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
				period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
				period.setStatus( AccountPeriodStatus.ACTIVE );
				period.setDomain(ctx.getSecurityContext().getDomainId());
				AccountPeriodDAO.insert(ctx.getNested(configuration), period);
			}
			AccountEntry accountEntry = new AccountEntry();
			accountEntry.setDomain(ctx.getSecurityContext().getDomainId());
			accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
			accountEntry.setAccountPeriod(period.getId());
			AccountEntryDAO.insert(ctx.getNested(configuration), accountEntry);
		});
	}
	
	@Test
	public void testInsert() {
			ctx.getDslContext().transaction(configuration -> {
				try {
					AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
							ACCOUNT_PERIOD.NAME.equal("1974")
							.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
							);
					if (period == null) {
						period = new AccountPeriod();
						period.setName("1974");
						period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
						period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
						period.setStatus( AccountPeriodStatus.ACTIVE );
						period.setDomain(ctx.getSecurityContext().getDomainId());
						AccountPeriodDAO.insert(ctx.getNested(configuration), period);
					}
					AccountEntry ae = new AccountEntry();
					ae.setDomain(ctx.getSecurityContext().getDomainId()); // Other
					ae.setEntryDate( AonDateUtils.getSqlDate(1974, 5, 4) );
					ae.setAccountPeriod(period.getId());
					ae.setEntryType( AccountEntryType.MANUAL );
					ae.setConfidential(false);
					ae
						.addDetail( new AccountEntryDetail(622849,"640000000","Sueldos y salarios","Nominas febrero",3151.49,0,null,null,null,null) )
						.addDetail( new AccountEntryDetail(622855,"642000000","Seguridad Social a cargo de la empresa.","Nominas febrero",617.42,0,null,null,null,null) )
						.addDetail( new AccountEntryDetail(830539,"476000001","Seguridad social, regimen general","Nominas febrero",0,758.86,null,null,null,null) )
						.addDetail( new AccountEntryDetail(830540,"476000002","Seguridad social acreedora, reta","Nominas febrero",0,281.84,null,null,null,null) )
						.addDetail( new AccountEntryDetail(622581,"475100000","Hacienda Pública, acreedora por retenciones practicadas.","Nominas febrero",0,79.51,null,null,null,null) )
						.addDetail( new AccountEntryDetail(622558,"465000000","Remuneraciones pendientes de pago.","Nominas febrero",0,2648.7,null,null,null,null) )
					;
					AccountEntryDAO.insert(ctx.getNested(configuration), ae);
				} catch (AonDAOException e) {
					e.printStackTrace();
					throw e;
				}
			});
	}
	
	@Test
	public void testUpdate() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974")
					.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
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
					AccountEntry ae = AccountEntryDAO.fetchOne(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
					ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
					AccountEntryDAO.update(ctx, ae);
				}
			}
		});
	}

	@Test
	@Ignore
	public void testDelete() {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
					ACCOUNT_PERIOD.NAME.equal("1974")
					.and(ACCOUNT_PERIOD.DOMAIN.equal(ctx.getSecurityContext().getDomainId()))
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
					AccountEntry ae = AccountEntryDAO.fetchOne(ctx,ACCOUNT_ENTRY.ID.equal(maxValue));	
					ae.setEntryDate( AonDateUtils.getSqlDate(1974, 4, 8) );
					AccountEntryDAO.delete(ctx, ae);
				}
			}
		});
	}

	@Test
	public void testFetch() {
		Condition condition = ACCOUNT_ENTRY.DOMAIN.equal(ctx.getSecurityContext().getDomainId());
		
		Stream<AccountEntry> list = AccountEntryDAO.fetch(ctx, condition,0,10);
		list.forEach(accountEntry -> System.out.println(""+ accountEntry.getId()) );
		System.out.println( "************************************" );
		list = AccountEntryDAO.fetch(ctx, condition,10,10);
		list.forEach(accountEntry -> System.out.println(""+ accountEntry.getId()) );
		System.out.println( "************************************" );
	}

	@Test
	public void testFetchCSV() {
		Condition condition = ACCOUNT_ENTRY.DOMAIN.equal(ctx.getSecurityContext().getDomainId());
		System.out.println( AccountEntryDAO.fetchCSV(ctx, condition,0,10) );
	}

	@AfterClass
	public static void afterClass() {
		AonDatabaseUtil.closeQuietly(conn);
	}
	
}
