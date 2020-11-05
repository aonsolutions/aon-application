package solutions.aon;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import solutions.aon.seg.social.SistemaREDITParts;
import solutions.aon.seg.social.SistemaRedEmployee;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestItParts {

	@Test
	public void testGetIts() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), Toolkit.parseDate("1-1-2020", "dd-MM-yyyy"));
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}

	}

}
