package solutions.aon.seg.social;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.ReportTooLongException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.exception.invalid.LiquidationDoesNotExist;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongAffNumber;
import solutions.aon.seg.social.exception.invalid.WrongIdentifierException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.Period;

//@Ignore
public class TestServicioRED extends SegSocialTest {
	
	private static Logger LOG = Logger.getLogger(TestServicioRED.class.getName());

//------------------------------------------------CONTRIBUTION INFO---------------------------------------------

	@Test
	public void testContributionInfoPOST() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			byte[] pdf = SistemaRED.getIDC(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, "011017250195"
					, calendar.getTime()
					);
//			if (pdf != null) {
				assertTrue(pdf != null && pdf.length > 120000);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//			}
		} catch (StatusCodeException e) {
			LOG.severe(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContributionInfoPOSTInvalidCertificate() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			SistemaRED.getIDC(certificateInputStream
					, "jg@FNFT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, "011017250195"
					, calendar.getTime()
					);
			fail();
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
	public void testContributionInfoPOSTInvalidRegime() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			SistemaRED.getIDC(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011"
					, "01105577910"
					, "011017250195"
					, calendar.getTime()
					);
			fail();
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
	public void testContributionInfoPOSTInvalidNAF() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			SistemaRED.getIDC(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, "011017250999"
					, calendar.getTime()
					);
			fail();
		} catch (WrongAffNumber e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContributionInfoPOSTInvalidCCC() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			SistemaRED.getIDC(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577932"
					, "011017250195"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidCccException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContributionInfoPOSTInvalidDate() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			SistemaRED.getIDC(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, "011017250195"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidDateException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
//---------------------------------------------------------------------------------------------------------------
	
//------------------------------------------------CONTRIBUTION INFO CCC------------------------------------------
	@Test
	public void testContributionInfoCccPOST() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			
			byte[] pdf = ServicioRED.getIDCCccPOST(
					certificateInputStream,
					"jg@FNMT",
					"pkcs12",
					"0111",
					"01105360062",
					new Date());
//			if (pdf != null) {
				assertTrue(pdf != null && pdf.length > 12000);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//			}
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContributionInfoCccPOSTInvalidCertificate() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			ServicioRED.getIDCCccPOST(certificateInputStream
					, "jg@FNFT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
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
	public void testContributionInfoCccPOSTInvalidRegime() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			byte[] pdf = ServicioRED.getIDCCccPOST(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011"
					, "01105577910"
					, calendar.getTime()
					);
			fail(pdf.length+"");
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
	public void testContributionInfoCccPOSTInvalidCCC() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			ServicioRED.getIDCCccPOST(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577932"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidCccException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContributionInfoCccPOSTInvalidDate() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			ServicioRED.getIDCCccPOST(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidDateException e) {
			LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
		} catch (StatusCodeException e) {
			LOG.warning(e.getMessage());
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
//---------------------------------------------------------------------------------------------------------------
	
//------------------------------------------------CONTRIBUTION INFO NAF------------------------------------------
		@Test
		public void testContributionInfoNafPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				byte[] pdf = ServicioRED.getIDCNAfPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062",
						new Date());
//				if (pdf != null) {
					assertTrue(pdf != null && pdf.length > 12000);
					LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//				}
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionInfoNafPOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getIDCNAfPOST(
						certificateInputStream
						, "jg@FNFT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
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
		public void testContributionInfoNafPOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getIDCNAfPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "011"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
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
		public void testContributionInfoNafPOSTInvalidNAF() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getIDCNAfPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250999"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
			} catch (WrongAffNumber e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionInfoNafPOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getIDCNAfPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577932"
						, calendar.getTime()
						);
				fail();
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionInfoNafPOSTInvalidDate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getIDCNAfPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
			} catch (InvalidDateException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//------------------------------------------------TA DUPLICATE---------------------------------------------------

		@Test
		public void testGetTADuplicatePOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				byte[] pdf = ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						"0111",
						"011005185924",
						calendar.getTime());
//				if (pdf != null) {
					assertTrue(pdf != null && pdf.length > 120000);
					LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//				}
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testGetTADuplicatePOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"01105360062",
						"0111",
						"011005185924",
						calendar.getTime()
						);
				fail();
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
		public void testGetTADuplicatePOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						"011",
						"011005185924",
						calendar.getTime()
						);
				fail();
			} catch (NotAllowedContributionAccount e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testGetTADuplicatePOSTInvalidNAF() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						"0111",
						"011005185999",
						calendar.getTime()
						);
				fail();
			} catch (WrongAffNumber e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testGetTADuplicatePOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105577932",
						"0111",
						"011005185924",
						calendar.getTime()
						);
				fail();
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testGetTADuplicatePOSTInvalidDate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
				calendar.set(Calendar.MONTH, Calendar.AUGUST);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				
				ServicioRED.getTADuplicatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						"0111",
						"011005185924",
						calendar.getTime()
						);
				fail();
			} catch (DataDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//--------------------------------------------OBLIGATION AWARENESS-----------------------------------------------
		
		@Test
		public void testObligationAwarenessCertificatePOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				byte[] pdf = ServicioRED.getObligationAwarenessCertificatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062"
						);
//				if (pdf != null) {
					assertTrue(pdf != null && pdf.length > 110000);
					LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//				}
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testObligationAwarenessCertificatePOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getObligationAwarenessCertificatePOST(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"0111",
						"01105360062"
						);
				fail();
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
		public void testObligationAwarenessCertificatePOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getObligationAwarenessCertificatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011",
						"01105360062"
						);
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
		public void testObligationAwarenessCertificatePOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				ServicioRED.getObligationAwarenessCertificatePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105577932"
						);
				fail();
			} catch (WrongIdentifierException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//---------------------------------------CONTRIBUTION SETTLEMENT REPORT------------------------------------------
		
		@Test
		public void testContributionSettlementReportPOST() throws IOException {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				byte[] pdf = ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011017250195",
						"0111",
						"01105577910",
						calendar.getTime());
//				if (pdf != null) {
					assertTrue(pdf != null && pdf.length > 130000);
					LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
//				}
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionSettlementReportPOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream
						, "jg@FNFT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
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
		public void testContributionSettlementReportPOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "011"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
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
		public void testContributionSettlementReportPOSTInvalidNAF() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250999"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
			} catch (WrongAffNumber e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionSettlementReportPOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2020);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577932"
						, calendar.getTime()
						);
				fail();
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionSettlementReportPOSTInvalidDate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH, 23);
				ServicioRED.getContributionSettlementReportPOST(
						certificateInputStream
						, "jg@FNMT"
						, "pkcs12"
						, "011017250195"
						, "0111"
						, "01105577910"
						, calendar.getTime()
						);
				fail();
			} catch (InvalidDateException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//----------------------------------------------SITUACIÓN EMPRESA------------------------------------------------
		@Test
		public void testSituacionEmpresaPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				assertNotNull(ServicioRED.getSituacionEmpresaPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062"
						));
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testSituacionEmpresaPOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getSituacionEmpresaPOST(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"0111",
						"01105360062"
						);
				fail();
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
		public void testSituacionEmpresaPOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getSituacionEmpresaPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011",
						"01105360062"
						);
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
		public void testSituacionEmpresaPOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				ServicioRED.getSituacionEmpresaPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105577932"
						);
				fail();
			} catch (UnfilledMandatory e) {
				// In this case, when an invalid CCC is inputed, the system works as if there were an unfilled mandatory field
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

//----------------------------------------------SITUACIÓN EMPRESA------------------------------------------------
		@Test
		public void testgetIdcsPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Collection<Idc> idcs = SistemaREDI.getIDCDates(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062"
						);
				for (Idc idc : idcs) {
					if (idc.getFecha() == null) {
						fail("Not taking well idc dates");
					}
				}
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testgetIdcsPOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				SistemaREDI.getIDCDates(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062"
						);
				fail();
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
		public void testgetIdcsPOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				SistemaREDI.getIDCDates(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"011",
						"01105360062"
						);
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
		public void testgetIdcsPOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				SistemaREDI.getIDCDates(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105577932"
						);
				fail();
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

//---------------------------------------------------------------------------------------------------------------
		
//-----------------------------------------------DISCHARGE DATES-------------------------------------------------				
		
		@Test
		public void testDischargeDatesPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Collection<Date> dates = ServicioRED.getDischargeDatesPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062"
						);
				for (Date date : dates) {
					if (date == null) {
						fail("Not picking up some dates");
					}
				}
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
//---------------------------------------------------------------------------------------------------------------
		
		
//-----------------------------------------------CCC LABORAL LIFE------------------------------------------------
		
		@Test
		public void getCccLaboralLifeTest() throws IOException {
			try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
				byte[] pdf = ServicioREDEmployee.getCccLaboralLifePOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062",
						new Date(),
						new Date()
				);
				assertTrue(pdf.length > 130000);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				fail("unexpected SegSocialException");
			}

		}
		
		@Test
		public void getCccLaboralLifeTestTooLong() throws IOException {
			try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, 10);
				cal.set(Calendar.MONTH, Calendar.AUGUST);
				cal.set(Calendar.YEAR, 2020);
				Date from = cal.getTime();
				cal.set(Calendar.YEAR, 2021);
				ServicioREDEmployee.getCccLaboralLifePOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"0111",
						"11122534302",
						from,
						cal.getTime()
				);
				fail();
			} catch (ReportTooLongException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				fail("unexpected SegSocialException");
			}
			
		}

//---------------------------------------------------------------------------------------------------------------
		
//----------------------------------------------CALCULATION BY CCC-----------------------------------------------
		
		@Test
		public void testCalculationByCCCPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryGrantsAndBonuses() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105577910",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryDateNotFound() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar c=Calendar.getInstance();
				c.add(Calendar.MONTH, 1);
				Date d=c.getTime();
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				
				fail();
			} catch (DataDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationOriginNoData() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.GENERADAS_POR_LA_TGSS
						);
				
				fail();
			} catch (LiquidationDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryRegimeCCCNotFound() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL_ARTISTAS,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				fail();
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
		public void testCalculationQueryNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.calculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
				);
				fail();
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

//-----------------------------------------WORKERS' CALCULATION BY CCC-------------------------------------------

		@Test
		public void testWorkersCalculationByCCCPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
				);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryGrantsAndBonuses() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105577910",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryDateNotFound() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar c=Calendar.getInstance();
				c.add(Calendar.MONTH, 1);
				Date d=c.getTime();
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				
				fail();
			} catch (DataDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationOriginNoData() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.GENERADAS_POR_LA_TGSS
						);
				fail();
			} catch (LiquidationDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryRegimeCCCNotFound() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"01105360062",
						SistemaRED.Regime.GENERAL_ARTISTAS,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
						);
				fail();
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
		public void testWorkersCalculationQueryNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.workersCalculationByCCCPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.L00_NORMAL,
						SistemaRED.LiquidationOrigin.TODAS
				);
				fail();
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
		
//------------------------------------WORKERS' CALCULATION BY CCC AND NAFS---------------------------------------
		
		@Test
		public void testWorkersCalculationByCCCandNAFsPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				Map<String, Map<String, Map<Period, Map<String, Calc>>>> map = ServicioRED.workersCalculationByCCCandNAFsPOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"11122534302",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.TODAS,
						SistemaRED.LiquidationOrigin.TODAS,
						"gwt354", "111016467058", "111008520536");
				LOG.info(map.toString());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongDatePOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-"+(Calendar.getInstance().get(Calendar.YEAR)+2));
				ServicioRED.workersCalculationByCCCandNAFsPOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"11122534302",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.TODAS,
						SistemaRED.LiquidationOrigin.TODAS,
						"111016467058", "111008520536", "gwt354");
				fail("Shouldn't end");
			} catch (DataDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationByCCCandNAFsMultpleNafsDateWithNoDataPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2006");
				ServicioRED.workersCalculationByCCCandNAFsPOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"11122534302",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.TODAS,
						SistemaRED.LiquidationOrigin.TODAS,
						"gwt354", "111016467058", "111008520536");
				fail("Shouldn't end");
			} catch (DataDoesNotExist e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongRegimePOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				ServicioRED.workersCalculationByCCCandNAFsPOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"11122534302",
						SistemaRED.Regime.ESPECIAL_MAR_ASIMILADOS_GRUPO_1,
						d,
						d,
						SistemaRED.LiquidationType.TODAS,
						SistemaRED.LiquidationOrigin.TODAS,
						"111016467058", "111008520536", "gwt354");
				fail("Shouldn't end");
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
		public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongCCCPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				ServicioRED.workersCalculationByCCCandNAFsPOST(
						certificateInputStream,
						"123456",
						"pkcs12",
						"11177534302",
						SistemaRED.Regime.GENERAL,
						d,
						d,
						SistemaRED.LiquidationType.TODAS,
						SistemaRED.LiquidationOrigin.TODAS,
						"111016467058", "111008520536", "gwt354");
				fail("Shouldn't end");
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		
//---------------------------------------------------------------------------------------------------------------

//--------------------------------------------TA CERTIFICATE PDFS------------------------------------------------
		
		@Test
		public void testTACertificatePDFsPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				List<byte[]> pdfs = ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062",
						d);
				assertTrue(!pdfs.isEmpty());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testTACertificatePdfsPOSTWrongCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105368062",
						d);
				fail("Should have returned a pdf");
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testTACertificatePdfsPOSTNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"",
						d);
				fail("Should have returned a pdf");
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
		public void testTACertificatePdfsPOSTNullRegime() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"",
						"01105360062",
						d);
				fail("Should have returned a pdf");
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
		public void testTACertificatePdfsPOSTWrongRegime() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0181",
						"01105360062",
						d);
				fail("Should have returned a pdf");
			} catch (NotAllowedContributionAccount e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testTACertificatePdfsPOSTWrongDate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.YEAR, 4);
				Date d = c.getTime();
				ServicioRED.getTACertificatePDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062",
						d);
				fail("Should have thrown an exception");
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
		
//---------------------------------------------CONTRIBUTION PDFS-------------------------------------------------
		
		@Test
		public void testContributionPDFsPOST() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				List<byte[]> pdfs = ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062",
						d);
				
				assertTrue(!pdfs.isEmpty());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testContributionPdfsWrongCCC() throws ParseException, IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105368062",
						d);
				fail("Should have returned a pdf");
			} catch (InvalidCccException e) {
				LOG.info(PASSED + new Object(){}.getClass().getEnclosingMethod().getName());
			} catch (StatusCodeException e) {
				LOG.warning(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testContributionPdfsNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"",
						d);
				fail("Should have returned a pdf");
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
		public void testContributionPdfsNullRegime() throws ParseException, IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"",
						"01105360062",
						d);
				fail("Should have returned a pdf");
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
		public void testContributionPdfsWrongRegime() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
				ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0181",
						"01105360062",
						d);
				fail("Should have returned a pdf");
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
		public void testContributionPdfsWrongDate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.YEAR, 1);
				Date d = c.getTime();
				ServicioRED.getContributionPDFsPOST(certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062",
						d);
				fail("Should have thrown an exception");
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
}