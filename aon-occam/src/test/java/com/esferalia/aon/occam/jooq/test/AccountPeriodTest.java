package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class AccountPeriodTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "alhymotion-mac.ecastellano.dev";
	private static int DOMAIN_ID = 3034;
	private static String USER = "mac";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	@Test(expected=AonCoreException.class)
	public void testPeriodEmptyInitiaionDate() {
		AccountPeriod period = new AccountPeriod();
		ACCOUNTING.insert(ctx, period);
	}

	@Test(expected=AonCoreException.class)
	public void testPeriodEmptyDeadline() {
		AccountPeriod period = new AccountPeriod();
		period.setInitiationDate( AonDateUtils.getDate(2014, 0, 1));
		ACCOUNTING.insert(ctx, period);
	}

	@Test(expected=AonCoreException.class)
	public void testPeriodInitialDateOverlap() {
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setDomain(ctx.getDomainId());
			period.setStatus( AccountPeriodStatus.ACTIVE );
			ACCOUNTING.insert( ctx , period);
		}
		period = new AccountPeriod();
		period.setName("1975");
		period.setInitiationDate( AonDateUtils.getDate(1974, 5, 1));
		period.setDeadline( AonDateUtils.getDate(1975, 4, 31));
		period.setStatus( AccountPeriodStatus.ACTIVE );
		period.setDomain(ctx.getDomainId());
		ACCOUNTING.insert(ctx, period);
	}

	@Test(expected=AonCoreException.class)
	public void testPeriodDeadlineOverlap() {
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			ACCOUNTING.insert(ctx, period);
		}
		period = new AccountPeriod();
		period.setName("1973");
		period.setInitiationDate( AonDateUtils.getDate(1973, 5, 1));
		period.setDeadline( AonDateUtils.getDate(1974, 4, 31));
		period.setDomain(ctx.getDomainId());
		ACCOUNTING.insert(ctx, period);
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
