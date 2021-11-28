package solutions.aon.seg.social;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.object.SecondaryUser;

public class TestServicioREDSecondaryUsers extends SegSocialTest {
	
	private static Logger LOG = Logger.getLogger(TestServicioREDSecondaryUsers.class.getName());
	
//----------------------------------------------------IPF X NAF--------------------------------------------------
	
	@Test
	public void getSecondaryUsersContentTest() throws IOException{
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {		
			List<SecondaryUser> users = ServicioREDSecondaryUser.getSecondaryUsers(certificateInputStream, "jg@FNMT", "pkcs12");
			assertNotEquals(users.size(), 0);
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void getSecondaryUsersContentTestWrongCert() throws IOException{
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {		
			List<SecondaryUser> users = ServicioREDSecondaryUser.getSecondaryUsers(certificateInputStream, "dhdhdh", "pkcs12");
			assertNotEquals(users.size(), 0);
		} catch (InvalidCertificateException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	
	@Test
	public void getSecondaryUsersContentTestAyudaT() throws IOException{
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")) {			
			List<SecondaryUser> users = ServicioREDSecondaryUser.getSecondaryUsers(certificateInputStream, "123456", "pkcs12");
			assertNotEquals(users.size(), 0);
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
		
//---------------------------------------------------------------------------------------------------------------
}