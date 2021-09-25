package solutions.aon.seg.social;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

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
import solutions.aon.seg.social.exception.invalid.invalidCccException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.Liquidation;
import solutions.aon.seg.social.object.WorkerLiquidation;

//@Ignore
public class TestServicioRED extends SegSocialTest {

//------------------------------------------------CONTRIBUTION INFO---------------------------------------------

	@Test
	public void testContributionInfoPOST() throws IOException {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 23);
			byte[] pdf = ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011017250195"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
//			if (pdf != null) {
				assertTrue(pdf != null && pdf.length > 120000);
//			}
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNFT"
					, "pkcs12"
					, "011017250195"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidCertificateException e) {
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011017250195"
					, "011"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
		} catch (WrongRegimeException e) {
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011017250999"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
		} catch (WrongAffNumber e) {
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011017250195"
					, "0111"
					, "01105577932"
					, calendar.getTime()
					);
			fail();
		} catch (invalidCccException e) {
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			ServicioRED.getIDCPost(certificateInputStream
					, "jg@FNMT"
					, "pkcs12"
					, "011017250195"
					, "0111"
					, "01105577910"
					, calendar.getTime()
					);
			fail();
		} catch (InvalidDateException e) {
		
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
//			}
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
		} catch (invalidCccException e) {
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
			
		} catch (StatusCodeException e) {
			System.err.println(e.getMessage());
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
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			} catch (invalidCccException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			} catch (invalidCccException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//------------------------------------------------TA DUPLICATE---------------------------------------------------
		
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
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			} catch (invalidCccException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
		}
		
//---------------------------------------------------------------------------------------------------------------
		
//----------------------------------------------SITUACIÓN EMPRESA------------------------------------------------
		
		@Test
		@Ignore
		public void testSituacionEmpresaPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				assertEquals("Situación de la empresa: \n" + 
						"	CCC: \"01105360062\"\n" + 
						"	ID Empresario: \"9\"\n" + 
						"	NIF: \"B01487271\"\n" + 
						"	Régimen: \"0111\"\n" + 
						"	UGTGSS: \"010202\"\n" + 
						"	SIT: \"02ALTA\"\n" + 
						"	FSit: \"TUE AUG 27 00:00:00 CEST 2013\"\n" + 
						"	Fecha alta inicial: \"TUE AUG 27 00:00:00 CEST 2013\"\n" + 
						"	Trabajadores de alta: \"10\"\n" + 
						"	Alta primer trabaajdor: \"WED SEP 04 00:00:00 CEST 2013\"\n" + 
						"	Ultima baja ef. cot.: \"SAT JUL 31 00:00:00 CEST 2021\"\n" + 
						"	C.Esp num.: \"9999\"\n" + 
						"	C.Esp cad.: \"OTROS COLECTIVOS SIN ESPE\"\n" + 
						"	C.TA2ALTA: \"0\"\n" + 
						"	TA2BAJA: \"3\"\n" + 
						"	CNAE09 num.: \"6209\"\n" + 
						"	CNAE09 cad.: \"OTROS SERVICIOS RELACIONADOS CON LAS TEC\"\n" + 
						"	Tipos ATyEPIT.: \"0.8\"\n" + 
						"	IMS: \"0.7\"\n" + 
						"	Total: \"1.5\"\n" + 
						"	 Coeficiente jubilación:\"0\"\n" + 
						"	Cad. coef. jubilación: \"COEFICIENTE REDUCTOR\"\n" + 
						"	Esc taller: \"FALSE\"\n" + 
						"	Autorización red: \"228115 AUTORIZADO\"\n" + 
						"	Plazo incorp. red: \"TUE OCT 01 00:00:00 CEST 2013\"\n" + 
						"	Fecha aut. can: \"WED AUG 01 00:00:00 CEST 2018\"\n" + 
						"	Anagrama: \"AON SOLUTIONS, S.L\"\n" + 
						"	Móvil: \"646964199\"\n" + 
						"	Tlf. fijo: \"945121010\"\n" + 
						"	Email: \"JGARCIA@AONSOLUTIONS.ES\"\n" + 
						"	FNotif. domicilio empresa: \"TRUE\"\n" + 
						"	Tipo vía domicilio empresa: \"CL\"\n" + 
						"	Tipo vía dirección actividad: \"CL\"\n" + 
						"	Dir. empresa calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Num. dir. empresa: \"52\"\n" + 
						"	Dir. emp. piso: \"B\"\n" + 
						"	CP empresa: \"01010\"\n" + 
						"	Num municipio empresa: \"010590000\"\n" + 
						"	Municipio empresa: \"VITORIA-GASTEIZ\"\n" + 
						"	Notif. domicilio actividad: \"FALSE\"\n" + 
						"	Act UGTGSS: \"010202\"\n" + 
						"	Dir. actividad calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Dir. actividad calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Dir. act. piso: \"B\"\n" + 
						"	CP actividad: \"01010\"\n" + 
						"	Dir. act. num. municipio: \"010590000\"\n" + 
						"	Dir. act. nom. muni.: \"VITORIA-GASTEIZ\"\n",
						
						ServicioRED.getSituacionEmpresaPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062"
						).toString());
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Ignore
		@Test
		public void testSituacionEmpresaSAX() throws IOException, ParserConfigurationException, SAXException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {		
				assertEquals("Situación de la empresa: \n" + 
						"	CCC: \"01105360062\"\n" + 
						"	ID Empresario: \"9\"\n" + 
						"	NIF: \"B01487271\"\n" + 
						"	Régimen: \"0111\"\n" + 
						"	UGTGSS: \"010202\"\n" + 
						"	SIT: \"02ALTA\"\n" + 
						"	FSit: \"TUE AUG 27 00:00:00 CEST 2013\"\n" + 
						"	Fecha alta inicial: \"TUE AUG 27 00:00:00 CEST 2013\"\n" + 
						"	Trabajadores de alta: \"10\"\n" + 
						"	Alta primer trabaajdor: \"WED SEP 04 00:00:00 CEST 2013\"\n" + 
						"	Ultima baja ef. cot.: \"SAT JUL 31 00:00:00 CEST 2021\"\n" + 
						"	C.Esp num.: \"9999\"\n" + 
						"	C.Esp cad.: \"OTROS COLECTIVOS SIN ESPE\"\n" + 
						"	C.TA2ALTA: \"0\"\n" + 
						"	TA2BAJA: \"3\"\n" + 
						"	CNAE09 num.: \"6209\"\n" + 
						"	CNAE09 cad.: \"OTROS SERVICIOS RELACIONADOS CON LAS TEC\"\n" + 
						"	Tipos ATyEPIT.: \"0.8\"\n" + 
						"	IMS: \"0.7\"\n" + 
						"	Total: \"1.5\"\n" + 
						"	 Coeficiente jubilación:\"0\"\n" + 
						"	Cad. coef. jubilación: \"COEFICIENTE REDUCTOR\"\n" + 
						"	Esc taller: \"FALSE\"\n" + 
						"	Autorización red: \"228115 AUTORIZADO\"\n" + 
						"	Plazo incorp. red: \"TUE OCT 01 00:00:00 CEST 2013\"\n" + 
						"	Fecha aut. can: \"WED AUG 01 00:00:00 CEST 2018\"\n" + 
						"	Anagrama: \"AON SOLUTIONS, S.L\"\n" + 
						"	Móvil: \"646964199\"\n" + 
						"	Tlf. fijo: \"945121010\"\n" + 
						"	Email: \"JGARCIA@AONSOLUTIONS.ES\"\n" + 
						"	FNotif. domicilio empresa: \"TRUE\"\n" + 
						"	Tipo vía domicilio empresa: \"CL\"\n" + 
						"	Tipo vía dirección actividad: \"CL\"\n" + 
						"	Dir. empresa calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Num. dir. empresa: \"52\"\n" + 
						"	Dir. emp. piso: \"B\"\n" + 
						"	CP empresa: \"01010\"\n" + 
						"	Num municipio empresa: \"010590000\"\n" + 
						"	Municipio empresa: \"VITORIA-GASTEIZ\"\n" + 
						"	Notif. domicilio actividad: \"FALSE\"\n" + 
						"	Act UGTGSS: \"010202\"\n" + 
						"	Dir. actividad calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Dir. actividad calle: \"DUQUE DE WELLINGTON\"\n" + 
						"	Dir. act. piso: \"B\"\n" + 
						"	CP actividad: \"01010\"\n" + 
						"	Dir. act. num. municipio: \"010590000\"\n" + 
						"	Dir. act. nom. muni.: \"VITORIA-GASTEIZ\"\n",
						
						ServicioRED.getSituacionEmpresaSAX(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062"
						).toString());
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Ignore
		@Test
		public void testSituacionEmpresaSAXInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getSituacionEmpresaSAX(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"0111",
						"01105360062"
						);
				fail();
			} catch (InvalidCertificateException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Ignore
		@Test
		public void testSituacionEmpresaSAXInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getSituacionEmpresaSAX(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011",
						"01105360062"
						);
				fail();
			} catch (InvalidDataException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Ignore
		@Test
		public void testSituacionEmpresaPOSTInvalidSAX() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				ServicioRED.getSituacionEmpresaSAX(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105577932"
						);
				fail();
			} catch (UnfilledMandatory e) {
				// In this case, when an invalid CCC is inputed, the system works as if there were an unfilled mandatory field
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}

//---------------------------------------------------------------------------------------------------------------

//----------------------------------------------SITUACIÓN EMPRESA------------------------------------------------
		@Test
		public void testgetIdcsPOST() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Collection<Idc> idcs = ServicioRED.getIDCDatesPOST(
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
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testgetIdcsPOSTInvalidCertificate() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getIDCDatesPOST(
						certificateInputStream,
						"jg@FNFT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105360062"
						);
				fail();
			} catch (InvalidCertificateException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testgetIdcsPOSTInvalidRegime() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getIDCDatesPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"011",
						"01105360062"
						);
				fail();
			} catch (InvalidDataException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testgetIdcsPOSTInvalidCCC() throws IOException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				ServicioRED.getIDCDatesPOST(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"011005185924",
						"0111",
						"01105577932"
						);
				fail();
			} catch (invalidCccException e) {
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				byte[] pdf = ServicioRED.getCccLaboralLife(
						certificateInputStream,
						"jg@FNMT",
						"pkcs12",
						"0111",
						"01105360062",
						new Date(),
						new Date()
				);
				assertTrue(pdf.length > 130000);
			} catch (StatusCodeException ignored) {
				ignored.printStackTrace();
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
				ServicioRED.getCccLaboralLife(
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
				System.err.println(e.getMessage());
			} catch (StatusCodeException e) {
				e.printStackTrace();
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
				Collection<Liquidation> liq = ServicioRED.calculationByCCC(
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
				
//				for (Liquidation liquidation : liq) {
//					System.out.println(liquidation);
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryGrantsAndBonuses() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				Collection<Liquidation> liq = ServicioRED.calculationByCCC(
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
				
//				for (Liquidation liquidation : liq) {
//					System.out.println(liquidation);
//				}
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				ServicioRED.calculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationOriginNoData() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.calculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryRegimeCCCNotFound() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.calculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testCalculationQueryNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.calculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				Map<String, Map<String, WorkerLiquidation>> liqs = ServicioRED.workersCalculationByCCC(
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
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryGrantsAndBonuses() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
				Map<String, Map<String, WorkerLiquidation>> liq = ServicioRED.workersCalculationByCCC(
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
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
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
				ServicioRED.workersCalculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationOriginNoData() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
				ServicioRED.workersCalculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryRegimeCCCNotFound() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.workersCalculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		@Test
		public void testWorkersCalculationQueryNullCCC() throws IOException, ParseException {
			try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
				Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
				ServicioRED.workersCalculationByCCC(
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
				
			} catch (StatusCodeException e) {
				System.err.println(e.getMessage());
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		

//---------------------------------------------------------------------------------------------------------------
}