package solutions.aon;


import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Certificates.CertificatesBuilder;
import aon.sepe.objects.Certificates.TypeDuration;
import aon.sepe.objects.QuoteData;
import solutions.aon.sepe.Certificado;
import solutions.aon.sepe.exceptions.SepeException;

public class TestCertificado {
	
	@Test
	@Ignore
	public void testCertEnterprisePdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String nif = "72740703Y";
			@SuppressWarnings("deprecation")
			Date fecha =  new Date("2016/05/06");
			byte[] pdf = Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore("SEND CERTIFICATE SEPE")
	public void testCertEnterprise() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			CertificatesBuilder bd  = new CertificatesBuilder();
			bd.setRegimen("0111")
			.setCtaCti("41108285354")
			.setIpf("28805668P")
			.setIpfManager("J41956632")
			.setName("DEVORA")
			.setSurname("ALVAREZ")
			.setLastSurname("RODRIGUEZ")
			.setTypeContract("502")
			.setGz("09")
			.setDurationContract(2)
			.setTypeDuration(TypeDuration.DIAS)
			.setCatProfessional("9210")
			.setCauseSuspension("11")
			.setfAEd(new Date("2021/09/04"))
			.setfSTd(new Date("2021/09/05"))
			.setDaysCtzVc(0)
			.setBcccVc(0.00)
			.setBcdVc(0.00)
			.setCargo(null)
			.setOfficePublic(null)
			.setPublicPosition(null)
			.setDedicationPer(null);
			
			List<QuoteData> quoteDatas = new LinkedList<>();
			
			quoteDatas.add(parseQuoteData(2021, 9, 2, 22.95, 22.95));
			
			bd.setQuoteData(quoteDatas);
		
			
			Certificado.certEnterprise(certificateInputStream, certificatePassword, certificateType,  bd.build());
		} catch (SepeException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static QuoteData parseQuoteData(Integer anio, Integer month, Integer days, Double bccc, Double bcd) {
		return new QuoteData()
		.setAnio(anio)
		.setMonth(month)
		.setDays(days)
		.setBccc(bccc)
		.setBcd(bcd);
	}
}
