package net.aonsolutions.aon.tedi.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;

import net.aonsolutions.core.pool.AonConnectionException;

public class AbstractTediTest {

	protected static AONContext ctx;
	protected static String DOMAIN_NAME = "queserialascortas.ecastellano.euk";
	protected static Integer DOMAIN_ID = 18539;
	protected static String USER = "admin";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterClass
	public static void afterClass() {
		if (ctx != null) ctx.finalize();
	}

	private static void shutUp() {
		if (mustShutUp()) {
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
	
	protected static boolean mustShutUp() {
		return false;
//		String mustShutUp = System.getProperty("mustShutUp", "true");
//		return "true".equalsIgnoreCase(mustShutUp);
	}

	
}
