package solutions.aon;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.JndType;
import aon.sepe.objects.Contract.OfferType;
import aon.sepe.objects.Contract.SexType;
import aon.sepe.objects.ContractExtension;
import aon.sepe.objects.CopyBasic;
import aon.sepe.objects.CopyBasic.FirmType;
import solutions.aon.sepe.Sepe;

public class TestContrato {

	private final String CERTIFICATE_PASSWORD = "1234";
	private final String CERTIFICATE_TYPE = "pkcs12";
	private final String CERTIFICATE_PATH = System.getProperty("user.home") + "/MARIA_VERA.p12";

	@Test
	@Ignore
	public void sendContrato() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date fnac = new Date("1965/03/13");
			@SuppressWarnings("deprecation")
			Date fini = new Date("2022/03/01");
			ContractBuilder bd = new ContractBuilder().setRegimen("0111").setCtaCti("28231545357")
					.setCifEnterprise("B87812889").setIpf("16262835H").setName("MARCOS").setSurname("SÁNCHEZ")
					.setLastSurname("HERNÁNDEZ").setDateBirth(fnac).setSex(SexType.HOMBRE).setCodNationality(724)
					.setCodPaisDom(724).setCodMunDom("01059").setNss("010022757387").setCodContract("300")
					.setDateIniContract(fini).setCodFormativo(59)// review
					.setCodOccupation("1311")// review
					.setCodPaisWork(724).setCodMunWork("01059").setOffer(OfferType.NO) // review
			;
//			bd.setInterinidad("H");

			String ide = Sepe.sendContract(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, bd.build());
			System.out.println("ide: " + ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void sendContractExtension() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date startDate = new Date("2022/08/01");
			@SuppressWarnings("deprecation")
			Date endDate = new Date("2022/08/26");
			ContractExtension bd = new ContractExtension().setCif("45360684S").setRegime("0111")
					.setCtaCti("35122598846").setSepeId("3520220169060").setStartDate(startDate).setEndDate(endDate)
					.setDiscontinuo(true);
//			bd.setInterinidad("H");

			String sepeId = Sepe.sendContractExtension(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,
					bd);
			System.out.println(sepeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void removeContrato() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			String ide = "0120220026115";
			Sepe.removeContrato(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void sendTransformation() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date fini = new Date("2022/03/01");
			@SuppressWarnings("deprecation")
			Date fcomunicate = new Date("2022/03/02");

			ContractBuilder bd = new ContractBuilder().setCifEnterprise("B01487271").setIpf("16262835H")
					.setCodContract("189") // 189, 109
					.setDateIniContract(fini).setDateComContract(fcomunicate).setJndType(JndType.JORNADA_MENSUAL)
					.setDurationTypeJndHour("40").setDurationTypeJndMin("0").setCodOccupation("1311")
					.setCodPaisWork(724).setCodMunWork("01059").setDiscontinuo(true) // ¿Realiza trabajos fijos
																						// discontinuos o periódicos que
																						// se repiten en fechas ciertas?
			;

			CopyBasic copyBasic = new CopyBasic().setFirmType(FirmType.FIRMADA_REPRESENTANTES_LEGALES)
					.setWorkAddress("CALLE WELLINGM, ALAVA").setRestContract("segun convenio");

			Sepe.sendTransformation(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, bd.build(),
					copyBasic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void RemoveTransformation() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			String ide = "0120220026115";
			Sepe.removeTransformation(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void sendCopyBasic() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			String ipf = "16262835H";
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/01/28");
			Date ffin = fini;
			String workAddress = "CALLE WELLINGM, ALAVA";
			String restContract = "segun convenio";
			Sepe.sendContratoCopyBasic(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, ffin,
					FirmType.NO_FACILITADO_COPIA, workAddress, restContract);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void getContratoPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/10/01");
			String ipf = "71899097Q";
			String sepeId = "3320210207385";
//			byte[] pdf = Sepe.getContratoPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, fini);
			byte[] pdf = Sepe.getContratoPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, sepeId);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void getCopyBasicPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/10/01");
			String ipf = "71899097Q";
			String sepeId = "3320210207385";
//			byte[] pdf = Sepe.getCopyBasicPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf, fini, fini);

			byte[] pdf = Sepe.getCopyBasicPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, sepeId);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void getTransformationsPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {

			String cif = "B72384936";
			Date fini = new Date("2021/04/19");
			String ipf = "73578385M";

			byte[] pdf = Sepe.getTransformationPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, ipf,
					cif, fini);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void getTransformationCopyBasicPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			@SuppressWarnings("deprecation")
			Date fini = new Date("2022/04/01");
			String ipf = "71899097Q";
			String cif = "43443804R";

			String sepeId = "3320210207385";

//			byte[] pdf = Sepe.getTransformationCopyBasicPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,
//					 ipf, cif, fini
//			);

			byte[] pdf = Sepe.getTransformationCopyBasicPdf(certificateInputStream, CERTIFICATE_PASSWORD,
					CERTIFICATE_TYPE, sepeId);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void getContractExtensionPdf() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {

			String cif = "44736467H";
			String ipf = "45776588X";
			Date oldDateIniContract = new Date("2022/07/30");
			Integer nprorroga = 1;
			String sepeId = "352022016907001";

			byte[] pdf = Sepe.getContractExtensionPdf(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,
					ipf, cif, oldDateIniContract, nprorroga, sepeId);
			System.out.println(new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	@SuppressWarnings("deprecation")
	public void getContractData() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			Date fini = new Date("2022/02/18");
			Date fend = new Date("2022/02/18");
			String ipf = "16262835H";
			Contract contract = Sepe.getContractData(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,
					ipf, fini, fend);
			System.out.println(contract.getSepeId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	@SuppressWarnings("deprecation")
	public void getTransformationData() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {

			String cif = "B72384936";
			String ipf = "73578385M";
			Date oldDateIniContract = new Date("2021/04/19");
			Optional<String> sepeId = Optional.empty();

			Contract contract = Sepe.getTransformationData(certificateInputStream, CERTIFICATE_PASSWORD,
					CERTIFICATE_TYPE, ipf, cif, oldDateIniContract, sepeId);

			System.out.println(contract.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	@SuppressWarnings("deprecation")
	public void getContractExtensionData() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {

			String cif = "B72384936";
			String ipf = "73578385M";
			Date oldDateIniContract = new Date("2021/04/19");
			Optional<String> sepeId = Optional.of("352022016907001");

			Contract contract = Sepe.getContractExtensionData(certificateInputStream, CERTIFICATE_PASSWORD,
					CERTIFICATE_TYPE, ipf, cif, oldDateIniContract, sepeId);

			System.out.println(contract.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void validateCert() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH)) {
			Sepe.validateCert(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
