package solutions.aon;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaRedSecondaryUser;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;

public class TestSecondaryUsers {

	@Test
	@Ignore
	public void test() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMTT.p12")) {			
			SistemaRedSecondaryUser.getSecondaryUserByIpf(certificateInputStream, "jg@FNMT", "pkcs12", "x");
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}

}
