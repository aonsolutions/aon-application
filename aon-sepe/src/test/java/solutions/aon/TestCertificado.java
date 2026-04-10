package solutions.aon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import aon.sepe.objects.Certificates.CertificatesBuilder;
import aon.sepe.objects.Certificates.TypeDuration;
import aon.sepe.objects.QuoteData;
import solutions.aon.sepe.Certificado;
import solutions.aon.sepe.exceptions.SepeException;

public class TestCertificado {

	@Test
	@Disabled
	public void testCertEnterprisePdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String nif = "72740703Y";
			@SuppressWarnings("deprecation")
			Date fecha = new Date("2016/05/06");
			byte[] pdf = Certificado.getCertEnterprisePdf(certificateInputStream, certificatePassword, certificateType,
					nif, fecha);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Disabled("SEND CERTIFICATE SEPE")
	public void testCertEnterprise() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("AYUDAT.p12")) {
			String certificatePassword = "SLLENsMMGPAkm3jF";
			String certificateType = "pkcs12";
			CertificatesBuilder bd = new CertificatesBuilder();
			bd.setRegimen("0111").setCtaCti("43117353701").setIpf("Y4092896Z").setIpfManager("B55741847")
					.setName("MATTHEW").setSurname("NEAL").setLastSurname(null).setTypeContract("402").setGz("07")
					.setDurationContract(15).setTypeDuration(TypeDuration.DIAS).setCatProfessional("4500")
					.setCauseSuspension("11").setfAEd(new Date("2021/11/15")).setfSTd(new Date("2021/11/29"))
					.setDaysCtzVc(0).setBcccVc(0.00).setBcdVc(0.00).setCargo(null).setOfficePublic(null)
					.setPublicPosition(null).setDedicationPer(null);

			List<QuoteData> quoteDatas = new LinkedList<>();

			quoteDatas.add(parseQuoteData(2021, 11, 15, 806.31, 806.31));

			bd.setQuoteData(quoteDatas);

			Certificado.sendCertEnterprise(certificateInputStream, certificatePassword, certificateType, bd.build());
		} catch (SepeException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static QuoteData parseQuoteData(Integer anio, Integer month, Integer days, Double bccc, Double bcd) {
		return new QuoteData().setAnio(anio).setMonth(month).setDays(days).setBccc(bccc).setBcd(bcd);
	}
}
