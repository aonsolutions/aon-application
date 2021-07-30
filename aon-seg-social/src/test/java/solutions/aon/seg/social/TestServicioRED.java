package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.exception.invalid.WrongAffNumber;
import solutions.aon.seg.social.exception.invalid.WrongIdentifierException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.invalidCccException;

@Ignore
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
			if (pdf != null) {
				assertTrue(pdf.length > 120000);
			}
		} catch (StatusCodeException e) {
			e.printStackTrace();
		} catch (SegSocialException e) {
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			if (pdf != null) {
				assertTrue(pdf.length > 12000);
			}
		} catch (StatusCodeException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
				if (pdf != null) {
					assertTrue(pdf.length > 12000);
				}
			} catch (StatusCodeException e) {
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				if (pdf != null) {
					assertTrue(pdf.length > 120000);
				}
			} catch (StatusCodeException e) {
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				if (pdf != null) {
					assertTrue(pdf.length > 110000);
				}
			} catch (StatusCodeException e) {
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
			} catch (SegSocialException e) {
				e.printStackTrace();
				fail();
			}
		}
		
//---------------------------------------------------------------------------------------------------------------		
}
