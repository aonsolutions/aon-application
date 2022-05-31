package solutions.aon.seg.social;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.exception.invalid.SyntaxException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.object.Employee;

//@Ignore
public class TestServicioREDEmployee extends SegSocialTest {
	
	private static Logger LOG = Logger.getLogger(TestServicioREDEmployee.class.getName());

//-------------------------------------------------TOTAL EMPLOYEES-----------------------------------------------

	@Test
	public void testTotalEmployeesPOST() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getTotalEmployees(certificateInputStream,
					"jg@FNMT",
					"pkcs12",
					"0111",
					"01105360062");
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testTotalEmployeesPOSTMultiCCC() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")) {
			
			Map<String, Set<String>> cccMap = new LinkedHashMap<>();
			Set<String> cccSet = new LinkedHashSet<>();
			cccSet.add(null);
			cccSet.add("11122534303");
			cccSet.add("11122534302");
			cccMap.put("0111", cccSet);
			
			ServicioREDEmployee.getTotalEmployees(certificateInputStream.readAllBytes(),
					"123456",
					"pkcs12",
					cccMap);
			
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testTotalEmployeesMultiScreenPOST() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")) {
			ServicioREDEmployee.getTotalEmployees(certificateInputStream,
					"123456",
					"pkcs12",
					"0111",
					"11122534302");
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
		
//---------------------------------------------------------------------------------------------------------------

//----------------------------------------------------EMPLOYEES--------------------------------------------------
	
	@Test 
	public void getEmployeesRegimeTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062");
			fail();
		} catch (WrongRegimeException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test 
	public void getEmployeesRegimeEmptyTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12",null,"01105360062");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}	
	
	@Test
	public void getEmployeesCccTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0111","011205360062");
			fail();
		} catch (InvalidCccException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void getEmployeesCcc2PagesTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMTI.p12")) {
			ServicioREDEmployee.getEmployees(certificateInputStream,"123456","pkcs12","0111","11122534302");
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void getEmployeesCcc2PagesPrevTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMTI.p12")) {
			ServicioREDEmployee.getPrevEmployees(certificateInputStream,"123456","pkcs12","0111","11122534302");
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch(NoQueryData e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void getEmployeesCccEmptyTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211",null);
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
//---------------------------------------------------------------------------------------------------------------
	
//----------------------------------------------------EMPLOYEES--------------------------------------------------
	
	@Test
	public void getEmployeeTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12", "011005185924");			
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void getEmployeeNssOtherNafTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12", "011017250195");
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());			
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void getEmployeeNssTest() throws IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ServicioREDEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12", "011z005185924");					
		}
		catch (SyntaxException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
//---------------------------------------------------------------------------------------------------------------
}