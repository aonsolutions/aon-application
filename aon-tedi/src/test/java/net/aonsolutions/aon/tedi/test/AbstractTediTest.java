package net.aonsolutions.aon.tedi.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

import net.aonsolutions.core.pool.AonConnectionException;

public class AbstractTediTest {

	protected static CloseableAONContext ctx;
	protected static String DOMAIN_NAME = "occamtest.aonsolutions.test";
	protected static Integer DOMAIN_ID = 1;
	protected static String USER = "admin";
	
	@BeforeAll
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterAll
	public static void afterClass() {
		if (ctx != null) ctx.close();
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
