package solutions.aon.seg.social;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exception.SegSocialException;

public class AonSegSocialJuanmaTest {

	
	@Test
	public void testSetCnoWrongCCC() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		try (final InputStream certificateInputStream = new FileInputStream(file)) {
			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, "123456", "pkcs12", "461052988590", // NSS OK
					"173578385M", // DNI OK
					"0111", // REGIMEN OK
					"1112253430", // CCC ERRONEO
					"0020" // CNO OK
			);
			Assert.fail();
		} catch (SegSocialException e) {
			Assert.assertEquals("3823* CUENTA DE COTIZACION ERRONEA", e.getMessage());
		}
	}

	@Test
	private static void testSetCnoWrongIdent() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		try (final InputStream certificateInputStream = new FileInputStream(file)) {
			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, "123456", "pkcs12", "461052988590", // NSS OK
					"17357838", // DNI ERRONEO
					"0111", // REGIMEN OK
					"11122534302", // CCC OK
					"0020" // CNO OK
			);
			Assert.fail();
		} catch (SegSocialException e) {
			Assert.assertEquals("3595* IDENTIFICADOR ERRONEO", e.getMessage());
		}

	}

	@Test
	private static void testSetCnoWrongRegimen() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		try (final InputStream certificateInputStream = new FileInputStream(file)) {
			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, "123456", "pkcs12", "461052988590", // NSS OK
					"173578385M", // DNI OK
					"011", // REGIMEN ERRONEO
					"11122534302", // CCC OK
					"0020" // CNO OK

			);
			Assert.fail();
		} catch (SegSocialException e) {
			Assert.assertEquals("3605* REGIMEN INCOMPATIBLE", e.getMessage());
		}

	}

	@Test
	private static void testSetCnoWrongNSS() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		try (final InputStream certificateInputStream = new FileInputStream(file)) {
			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, "123456", "pkcs12", "4610529885", // NSS
																											// ERRONEO
					"173578385M", // DNI OK
					"0111", // REGIMEN OK
					"11122534302", // CCC OK
					"0020" // CNO OK

			);
			Assert.fail();
		} catch (SegSocialException e) {
			Assert.assertEquals("3820* NUMERO DE AFILIADO INCORRECTO", e.getMessage());
		}

	}

	@Test
	private static void testSetCnoWrongCno() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		try (final InputStream certificateInputStream = new FileInputStream(file)) {
			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, "123456", "pkcs12", "461052988590", // NSS OK
					"173578385M", // DNI OK
					"0111", // REGIMEN OK
					"11122534302", // CCC OK
					"9999" // CNO ERRONEO
			);

			Assert.fail();
		} catch (SegSocialException e) {
			Assert.assertEquals("3820* CNO INCORRECTO", e.getMessage());
		}
	}
	
	@Test
	private static void testOnlineSettlementCccWrongCcc(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception{
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage, 
					"123", 
					"0111",
					"05",
					"2019",
					"05",
					"2022",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());

		}
		
	}
	
	@Test
	public void testOnlineSettlementCccWrongRegimen(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"000",
					"05",
					"2019",
					"05",
					"2022",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementCccWrongStartMonth(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"0111",
					"13",
					"2019",
					"05",
					"2022",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementCccWrongStartYear(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"0111",
					"05",
					"20",
					"05",
					"2022",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementCccWrongEndMonth(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"0111",
					"05",
					"2019",
					"00",
					"2022",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementCccWrongEndYear(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"0111",
					"05",
					"2019",
					"05",
					"20226",
					"L90");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementCccWrongLiquidationNumber(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption1(webClient,
					htmlPage,
					"11122534302",
					"0111",
					"05",
					"2019",
					"00",
					"2022",
					"L9045");
			Assert.fail();
		} catch (SegSocialException e) {
			//Comprobar mensajes de error
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
	}
	
	@Test
	public void testOnlineSettlementLiquidationNumberWrongLiquidationNumber(WebClient webClient, HtmlPage htmlPage,
			String liquidationNumber, String rnt) throws Exception {
		
		try {
			AonSegSocialJuanma.onlineSettlementOption2(webClient,
					htmlPage,
					"02",
					"S");
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
		
	}
	
	@Test
	public void testOnlineSettlementLiquidationNumberWrongRnt(WebClient webClient, HtmlPage htmlPage,
			String liquidationNumber, String rnt) throws Exception {
		
		try {
			AonSegSocialJuanma.onlineSettlementOption2(webClient,
					htmlPage,
					"12345678912345678",
					"f");
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals("Wrong data :", e.getMessage());
		}
		
	}
	
	
	
}
