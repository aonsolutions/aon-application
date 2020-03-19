package com.esferalia.aon.occam.jooq.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;

import net.aonsolutions.core.pool.AonConnectionException;

public class AbstractOccamTest {

	protected static AONContext ctx;
	protected static String DOMAIN_NAME = "occamTest.aonsolutions.test";
	protected static int DOMAIN_ID = 3049;
	protected static String USER = "montse";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

	private static void shutUp() {
		PrintStream devnull = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
			}
		});
		System.setOut(devnull);
		System.setErr(devnull);
	}

}
