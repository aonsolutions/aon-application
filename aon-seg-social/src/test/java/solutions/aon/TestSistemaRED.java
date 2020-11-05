package solutions.aon;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.invalidData.SyntaxException;

public class TestSistemaRED {

	@Test
	@Ignore("Not Yet")
	public void testWrongPassword() {
		try {
			SistemaRED.getEmployee(
					TestSistemaRED.class.getResourceAsStream("FNMT.p12"), 
					"error", 
					"pkcs12", 
					"0111", 
					"", 
					"");
		} 
//		catch ( PasswordIncorrect e ) {
//			
//		}
		catch (SegSocialException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	@Ignore("Not Yet")
	public void testPasswordCCC() {
		try {
			SistemaRED.getEmployee(
					TestSistemaRED.class.getResourceAsStream("FNMT.p12"), 
					"jg@FNMT", 
					"pkcs12", 
					"0111", 
					"888888888888888", 
					"4444444444444");
		} 
		catch (SyntaxException e) {
			System.out.println(e.getMessage());
		}
		catch ( Throwable e ) {
			Assert.fail(e.getMessage());

		}
	}
}
