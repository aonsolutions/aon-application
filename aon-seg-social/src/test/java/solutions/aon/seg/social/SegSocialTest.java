package solutions.aon.seg.social;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;

import solutions.aon.seg.social.toolkit.Toolkit;

public abstract class SegSocialTest {
	public static final String PASSED = "PASSED - ";
	
	@BeforeClass
	public static void notDown() {
		testDisponibility("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
	}
	
	public static void testDisponibility(String url) {
		try (final InputStream is = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			assumeTrue(Toolkit.checkSiteDisponibility(is
				, "jg@FNMT"
				, "pkcs12"
				, url));
		} catch (IOException e1) {
			fail();
		}
	}
	
}
