package com.esferalia.aon.dsi.test;

import java.sql.Connection;

import org.jooq.Configuration;
import org.jooq.InsertQuery;
import org.jooq.impl.DSL;
import org.jooq.util.mysql.MySQLDSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.esferalia.aon.dsi.DSI2AON;
import com.esferalia.aon.dsi.util.DBUtils;

@RunWith(JUnit4.class)
public class MainTest {

	private static final String DB = "aon-dsi";
	private static final String OWNER = "soporte@analize.es";
	private static final String PARENT = "dsigrupo.aonsolutions.net";

	private int parentDomain = 1;

	private Connection dsiConn;
	private Connection aonConn;

	@Before
	public void setupAon() throws Exception {
		aonConn = DBUtils.getAonConnection("jdbc:mysql://localhost/aon-dsi",
				"aon", "40n");
		/*
		 * DBUtils.createDatabase(aonConn, DB); parentDomain =
		 * DBUtils.createParentDomain(aonConn, PARENT, "DSI GRUPO", OWNER);
		 */

	}

	@Before
	public void setupDsi() throws Exception {
		dsiConn = DBUtils
				.getDsiConnection("jdbc:paradox:/target/test-classes/db");
	}

	@After
	public void teardownAon() throws Exception {
		// DBUtils.dropDatabase(aonConn, DB);
		aonConn.close();
	}

	@After
	public void teardownDsi() throws Exception {
		dsiConn.close();
	}

	@Test
	public void testDSI2AON() throws Exception {
		// MADERAS BILBILITANAS
		// FNEMPRES.F20SSCOD.eq("50"), FNEMPRES.F20SSNUM.eq("9874545")
		//@formatter:off
		new DSI2AON(dsiConn, aonConn)
		.setCommit(true)
		.setReplace(true)
		.run(parentDomain, "-"+PARENT ,OWNER
		//, FNEMPRES.F20SSCOD.eq("50"), FNEMPRES.F20SSNUM.eq("9874545")
		);
		//@formatter:on
	}

}
