package solutions.aon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import solutions.aon.seg.social.SistemaRED_Secondary_User;
import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.StatusCodeException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.objects.SecondaryUser;

public class TestSecondaryUsers {

	@Test
	public void getSecondaryUserCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.getSecondaryUserByIpf(certificateInputStream, "jg@FNMTT", "pkcs12", "x");
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getSecondaryUserIpfTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.getSecondaryUserByIpf(certificateInputStream, "jg@FNMT", "pkcs12", "X");
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getSecondaryUserIpfEmptyTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.getSecondaryUserByIpf(certificateInputStream, "jg@FNMT", "pkcs12", null);
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getSecondaryUserContentTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			assertTrue(SistemaRED_Secondary_User.getSecondaryUserByIpf(certificateInputStream, "jg@FNMT", "pkcs12", "0Y7514970X") instanceof SecondaryUser);
		}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	
	@Test
	public void getSecondaryUsersCertificateTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.getSecondaryUsers(certificateInputStream, "jg@FNMT", "pkcs12_");
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getSecondaryUsersContentTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			assertTrue(SistemaRED_Secondary_User.getSecondaryUsers(certificateInputStream, "jg@FNMT", "pkcs12").size() != 0);
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	
	@Test
	public void registerSecondaryUsersCertificateTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.registerSecondaryUserByNie(certificateInputStream,"jg@FNMT_", "pkcs12","1","x","x");
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void registerSecondaryUsersNieTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.registerSecondaryUserByNie(certificateInputStream,"jg@FNMT", "pkcs12","1","x","x");
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void registerSecondaryUsersNieEmptyTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.registerSecondaryUserByNie(certificateInputStream,"jg@FNMT", "pkcs12","1","","x");
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void registerSecondaryUsersNafEmptyTest(){
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaRED_Secondary_User.registerSecondaryUserByNie(certificateInputStream,"jg@FNMT", "pkcs12","1","x","");
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}	

}
