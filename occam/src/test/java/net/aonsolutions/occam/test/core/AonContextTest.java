package net.aonsolutions.occam.test.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Stack;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonLogger;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;

@ExtendWith(TimingExtension.class)	
class AonContextTest extends AbstractOccamTest {
	
	@Test()
	void instanceTest() {
		AONContext aonContext = assertDoesNotThrow(() -> AONContext.getAONContext(DOMAIN_NAME,USER));
		aonContext.close();
		String wrongDomainName = "INVALID_DOMAIN";
		AonCoreException e = assertThrows(AonCoreException.class, () -> AONContext.getAONContext(wrongDomainName,USER));
		assertEquals("No es posible encontrar el dominio: "+wrongDomainName, e.getMessage());
	}

	@Test()
	void canReadTest() {
		Boolean canRead = ctx.canReadForTests();
		try {
			ctx.denyRead();
			assertFalse( ctx.canRead() );
			SecurityException e = assertThrows(SecurityException.class, () -> ctx.checkRead());
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
			
			ctx.setCanReadValueForTests( null );
			assertTrue( ctx.canRead() );
			assertDoesNotThrow(() -> ctx.checkRead());

			ctx.allowRead();
			assertTrue( ctx.canRead() );
			
			assertDoesNotThrow(() -> ctx.checkRead());
			
		} finally {
			ctx.setCanReadValueForTests(  canRead );	
		}
	}

	@Test()
	void canWriteTest() {
		Boolean canWrite = ctx.canWriteForTests();
		try {
			ctx.denyWrite();
			assertFalse( ctx.canWrite() );
			SecurityException e = assertThrows(SecurityException.class, () -> ctx.checkWrite());
			assertEquals(AonError.WRITE_FORBIDDEN.getMessage(), e.getMessage());
			
			ctx.setCanWriteValueForTests( null );
			assertTrue( ctx.canWrite() );
			assertDoesNotThrow(() -> ctx.checkWrite());

			ctx.allowWrite();
			assertTrue( ctx.canWrite() );
			
			assertDoesNotThrow(() -> ctx.checkWrite());
		} finally {
			ctx.setCanWriteValueForTests(  canWrite );	
		}
	}
	
	private static class MyHandler extends ConsoleHandler {
		private Stack<String> stack = new Stack<String>();
		
		@Override
		public void publish(LogRecord record) {
			super.publish(record);
			stack.push(record.getMessage());
		}
		
		public String pop() {
			return stack.pop();
		}
	}
	
	@Test()
	void logTest() {
		AonLogger logger = ctx.log();
		try {
			Logger l = Logger.getLogger(this.getClass().getName());
			MyHandler handler = new MyHandler();
			l.addHandler(handler);
			AonLogger testLogger = new AonLogger(l, DOMAIN_NAME);
			ctx.setLoggerForTests(testLogger);
			Level originalLevel = ctx.getLoggerLevelForTests();
			try {
				setLoggerLevel(Level.FINE);
				if (l.isLoggable(Level.FINE)) {
					ctx.log().debug("Testing Loggers: DEBUG MESSAGE");
					assertEquals(AonLogger.DEB, handler.pop());
					ctx.log().debug("Testing Loggers: DEBUG MESSAGE {0}", "param");
					assertEquals(AonLogger.DEB, handler.pop());
				}
				setLoggerLevel(Level.WARNING);
				if (l.isLoggable(Level.WARNING)) {
					ctx.log().warn("Testing Loggers: WARN MESSAGE");
					assertEquals(AonLogger.WAR, handler.pop());
					ctx.log().warn("Testing Loggers: WARN MESSAGE {0}", "param");
					assertEquals(AonLogger.WAR, handler.pop());
				}
				setLoggerLevel(Level.INFO);
				if (l.isLoggable(Level.INFO)) {
					ctx.log().info("Testing Loggers: INFO MESSAGE");
					assertEquals(AonLogger.INF, handler.pop());
					ctx.log().info("Testing Loggers: INFO MESSAGE {0}", "param");
					assertEquals(AonLogger.INF, handler.pop());
				}
				setLoggerLevel(Level.SEVERE);
				if (l.isLoggable(Level.SEVERE)) {
					ctx.log().error("Testing Loggers: ERROR MESSAGE");
					assertEquals(AonLogger.ERR, handler.pop());
					ctx.log().error("Testing Loggers: ERROR MESSAGE {0}", "param");
					assertEquals(AonLogger.ERR, handler.pop());
				}
			} finally {
				setLoggerLevel(originalLevel);			
			}
		} finally {
			ctx.setLoggerForTests(logger);
		}
	}
}
