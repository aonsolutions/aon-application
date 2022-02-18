package solutions.aon;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.OfferType;
import aon.sepe.objects.Contract.SexType;
import solutions.aon.sepe.Contrata;
import solutions.aon.sepe.Contrata.FirmType;

public class TestContrato {
	
	private static final String CERTIFICATE_PASSWORD = "1234"; // "aon@FNMT";
	private static final String CERTIFICATE_TYPE = "pkcs12"; 
	private static final String CERTIFICATE_PATH =  System.getProperty("user.home")+"/CERT.pfx"; 

	@Test
	@Ignore
	public void testContrato() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fnac = new Date("1965/03/13");
			@SuppressWarnings("deprecation")
			Date fini = new Date("2022/02/18");
			ContractBuilder bd = new ContractBuilder();
			bd.setRegimen("0111")
			.setCtaCti("01105360062")
			.setCifEnterprise("B01487271")
			.setIpf("16262835H")
			.setName("JULIO")
			.setSurname("GARCIA")
			.setLastSurname("PEREZ")
			.setDateBirth(fnac)
			.setSex(SexType.HOMBRE)
			.setCodNationality(724)
			.setCodPaisDom(724)
			.setCodMunDom("01059") 
			.setNss("010022757387")
			.setCodContract("410")
			.setDateIniContract(fini)
			.setCodFormativo(59)//review
			.setCodOccupation("1311")//review
			.setCodPaisWork(724)
			.setCodMunWork("01059")
			.setOffer(OfferType.NO) //review
			;
			bd.setInterinidad("H");


			String ide = Contrata.sendContrata(certificateInputStream, CERTIFICATE_PASSWORD, certificateType, bd.build());
			System.out.println("ide: "+ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void removeContrato() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {		
			String ide = "0120220024287";
			Contrata.removeContrato(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testSendTransformation() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {			
			@SuppressWarnings("deprecation")
			Date fini = new Date("2022/02/18");
			@SuppressWarnings("deprecation")
			Date fcomunicate = new Date("2022/02/19");
			
			ContractBuilder bd = new ContractBuilder();
			bd
//			.setRegimen("0111")
//			.setCtaCti("01105360062")
			.setCifEnterprise("B01487271")
			.setIpf("16262835H")
//			.setName("JULIO")
//			.setSurname("GARCIA")
//			.setLastSurname("PEREZ")
//			.setDateBirth(fnac)
//			.setSex(SexType.HOMBRE)
//			.setCodNationality(862)
//			.setCodPaisDom(724)
//			.setCodMunDom("01059") 
//			.setNss("010022757387")
			.setCodContract("209")
			.setDateIniContract(fini)
			.setDateComContract(fcomunicate)
//			.setCodFormativo(59)//review
//			.setCodOccupation("1311")//review
//			.setCodPaisWork(724)
//			.setCodMunWork("01059")
//			.setOffer(OfferType.NO) //review
			;

			String ide = Contrata.sendTransformation(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,  bd.build());
//			System.out.println("ide: "+ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testCopyBasic() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			String ipf = "16262835H";
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/01/28");
			Date ffin = fini;
			String workAddress = "CALLE WELLINGM, ALAVA";
			String restContract = "segun convenio";
			Contrata.contratoCopyBasic(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, ffin, FirmType.NO_FACILITADO_COPIA, workAddress, restContract);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testContratoPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {		
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2022/02/18");
			@SuppressWarnings("deprecation")
			Date ffin =  new Date("2022/02/18");
			String ipf = "16262835H";
			byte[] pdf = Contrata.contratoPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, ffin);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testGetCopyBasicPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2020/09/09");
			@SuppressWarnings("deprecation")
			Date ffin =  new Date("2021/01/01");
			String ipf = "16262835H";
			byte[] pdf = Contrata.getCopyBasicPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, ffin);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testTransformacionsPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {			
			@SuppressWarnings("deprecation")
			Date fecha =  new Date("2016/05/06");
			String ipf = "16262835H";
			byte[] pdf = Contrata.transformacionsPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testGetContratoData() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {			
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2022/02/18");
//			@SuppressWarnings("deprecation")
			Date fend =  new Date("2022/02/18");
			String ipf = "16262835H";
			Contract contract = Contrata.getContractData(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, fend);
			System.out.println(contract.getSepeId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testValidateCert() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			Contrata.validateCert(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
