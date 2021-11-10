package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NotExistingYetException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.invalidCccException;

public class TestServicioREDMov extends SegSocialTest {
	
	private static Logger LOG = Logger.getLogger(TestServicioREDMov.class.getName());
	
//----------------------------------------------------IPF X NAF--------------------------------------------------
	
	@Test
	public void ipfxnaf() throws ParserConfigurationException, SAXException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add("0100227573");
		    nssList.add("0110051859");
		    assertTrue(!ServicioREDMov.ipfxnaf(certificateInputStream, "jg@FNMT", "pkcs12", nssList).isEmpty());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void ipfxnafWrongNaf() throws ParserConfigurationException, SAXException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ArrayList<String> nssList = new ArrayList<>();
			nssList.add("010022757387");
			ServicioREDMov.ipfxnaf(certificateInputStream, "jg@FNMT", "pkcs12", nssList);
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
		
//---------------------------------------------------------------------------------------------------------------
//----------------------------------------------------NAF X IPF--------------------------------------------------
	
	@Test
	public void nafxipf() throws ParserConfigurationException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		   ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "garcia", "perez");
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfIncorrectIpf() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262833H", "garcia", "perez");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfWrongIpfFormat() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "162628336HS", "garcia", "perez");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfWrongName() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "vinicius", "junior");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfWrongNullIpf() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", null, "vinicius", "junior");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfWrongNullFirstSurname() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "", "perez");
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void nafxipfWrongNullSecondSurname() throws ParserConfigurationException, IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "garcia", null);
			fail();
		} catch (InvalidDataException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
//---------------------------------------------------------------------------------------------------------------
//--------------------------------------------REPORT AFFILIATE IN ALTA-------------------------------------------
	
	@Test
	public void testReportAffiliateInAlta() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		   byte[] pdf = ServicioREDMov.getReportAffiliateInAlta(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
		   assertTrue(pdf.length > 100000);
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInAltaWrongRegime() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInAlta(certificateInputStream, "jg@FNMT", "pkcs12", "0113", "01105360062");
		} catch (WrongRegimeException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInAltaNullRegime() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInAlta(certificateInputStream, "jg@FNMT", "pkcs12", null, "01105360062");
		} catch (UnfilledMandatory e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInAltaWrongCCC() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInAlta(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360064");
		} catch (invalidCccException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInAltaNullCCC() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInAlta(certificateInputStream, "jg@FNMT", "pkcs12", "0111", null);
		} catch (UnfilledMandatory e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
//---------------------------------------------------------------------------------------------------------------
//--------------------------------------------REPORT AFFILIATE IN ALTA-------------------------------------------
	
	@Test
	public void testReportAffiliateInMovPrev() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		   byte[] pdf = ServicioREDMov.getReportAffiliateInMovPrev(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
		   assertTrue(pdf.length > 100000);
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInMovPrevWrongRegime() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInMovPrev(certificateInputStream, "jg@FNMT", "pkcs12", "0113", "01105360062");
		} catch (WrongRegimeException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInMovPrevNullRegime() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInMovPrev(certificateInputStream, "jg@FNMT", "pkcs12", null, "01105360062");
		} catch (UnfilledMandatory e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInMovPrevWrongCCC() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInMovPrev(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360064");
		} catch (UnfilledMandatory e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReportAffiliateInMovPrevNullCCC() throws IOException {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			ServicioREDMov.getReportAffiliateInMovPrev(certificateInputStream, "jg@FNMT", "pkcs12", "0111", null);
		} catch (UnfilledMandatory e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
//---------------------------------------------------------------------------------------------------------------
}