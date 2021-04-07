package solutions.aon.seg.social;

import static solutions.aon.seg.social.exceptions.InvalidCertificateException.checkCertificate;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getSSCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.manageStatusCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;
import static solutions.aon.seg.social.toolkit.Toolkit.SplitString;
import static solutions.aon.seg.social.toolkit.Toolkit.getDateArray;
import static solutions.aon.seg.social.toolkit.Toolkit.parseDate;
import static solutions.aon.seg.social.toolkit.Toolkit.removeExtraZeros;
import static solutions.aon.seg.social.toolkit.Toolkit.splitStringMultiple;
import static solutions.aon.seg.social.toolkit.Toolkit.verifyData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exceptions.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.StatusCodeException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.NoQueryData;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRED_Employee {

	// GETS BOTH REAL AND PREVIUS EMPLOYEES
	public static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {

		byte[] cert;
		try {
			cert = certificateInputStream.readAllBytes();
			ArrayList<Employee> employees = new ArrayList<>();
			try {
				employees.addAll(getEmployees(new ByteArrayInputStream(cert), certificatePassword, certificateType,
						regimen, ccc));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				employees.addAll(getPrevEmployees(new ByteArrayInputStream(cert), certificatePassword, certificateType,
						regimen, ccc));
			} catch (NoQueryData ignored) {
			}

			return employees;
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}

	}

	// CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIES
	private static Employee employeeFullInfo(String nss, WebClient webClient)
			throws IOException, InterruptedException, SegSocialException {

		HtmlPage page = webClient
				.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR");
		HtmlForm formParts = wait4(page, p -> p.getFormByName("jacadaform")).orElseThrow();
		manageStatusCode(page);

		formParts.getInputByName("txt_SDFTESORNAF").setValueAttribute(nss.substring(0, 2));
		formParts.getInputByName("txt_SDFNUMNAF").setValueAttribute(nss.substring(2));
		formParts.getInputByName("btn_Sub2207601004").focus();
		page = formParts.getInputByName("btn_Sub2207601004").click();
		manageStatusCode(page);

		String ipf = page.getElementById("SDFNUMIPF").getTextContent().trim().replaceAll("^0+", "");
		String birthDateStr = page.getElementById("SDFDIANAC").getTextContent() + "-"
				+ page.getElementById("SDFMESNAC").getTextContent() + "-"
				+ page.getElementById("SDFAONAC").getTextContent();

		String sex = page.getElementById("SDFSEXO").getTextContent();
		String name = page.getElementById("SDFAPELNOM").getTextContent();
		String tlf = page.getElementById("SDFMOVIL").getTextContent();
		String ctaCot = page.getElementById("SDFTESORCCC").getTextContent()
				+ page.getElementById("SDFNUMCCC").getTextContent();
		String regime = page.getElementById("SDFREGIM").getTextContent();
		String companyId = page.getElementById("SDFEMPRESARIO3").getTextContent();
		String companyName = page.getElementById("SDFNOMBRE3").getTextContent();
		String situation = page.getElementById("SDFTSITUACAFI").getTextContent();
		String gc = page.getElementById("SDFCGRUPOAFI").getTextContent();
		String gcDesc = page.getElementById("SDFTGRUPOAFI").getTextContent();
		Boolean agricultPromo = Toolkit.toBoolean(page.getElementById("SDFPFEA").getTextContent());
		Boolean workTimeReduct = Toolkit.toBoolean(page.getElementById("SDFLITRJ").getTextContent());
		String fraStr = page.getElementById("SDFFRAAFI").getTextContent();
		String feaStr = page.getElementById("SDFFEAAFI").getTextContent();
		String frbStr = page.getElementById("SDFFRBAFI").getTextContent();
		String febStr = page.getElementById("SDFFEBAFI").getTextContent();
		String contract = page.getElementById("SDFTIPOAFI").getTextContent();
		String coef = page.getElementById("SDFCOEFAFI").getTextContent();
		String colec = page.getElementById("SDFCOLECTIVO").getTextContent();
		String epig = page.getElementById("SDFEPIGAFI").getTextContent();
		String ocup = page.getElementById("SDFOCUPACION").getTextContent();
		String vinFam = page.getElementById("SDFVINCULO").getTextContent();
		String profesCat = page.getElementById("SDFCATEGORIA").getTextContent();
		String reducingCoef = page.getElementById("SDFCOEFRED").getTextContent();

		ipf = removeExtraZeros(ipf);
		companyId = removeExtraZeros(companyId);

		if (birthDateStr.equals(" / / "))
			birthDateStr = "";

		Date birthDate = parseDate(birthDateStr, "dd-MM-yyyy");
		Date fra = parseDate(fraStr, "dd/MM/yyyy");
		Date fea = parseDate(feaStr, "dd/MM/yyyy");
		Date frb = parseDate(frbStr, "dd/MM/yyyy");
		Date feb = parseDate(febStr, "dd/MM/yyyy");

		// BUILD
		EmployeeBuilder builder = new EmployeeBuilder();

		builder.setNss(nss.replace(" ", "")).setName(name).setSituation(situation).setIpf(ipf.replace(" ", ""))
				.setNss(nss).setIpf(ipf).setBirthDate(birthDate).setSex(sex).setTlf(tlf).setCtaCti(ctaCot)
				.setRegime(regime).setCompanyId(companyId).setCompanyName(companyName).setSituation(situation).setGc(gc)
				.setGcDesc(gcDesc).setAgricultPromo(agricultPromo).setWorkTimeReduct(workTimeReduct).setFra(fra)
				.setFea(fea).setFrb(frb).setFeb(feb).setContract(contract).setCoef(coef).setColec(colec).setEpig(epig)
				.setOcup(ocup).setVinFam(vinFam).setProfesCat(profesCat).setReducingCoefic(reducingCoef);

		return builder.build();
	}

	// HANDLE THE EXCEPTIONS OF GETEMPLOYEE METHOD
	public static Collection<Employee> getEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {

		verifyData(new Object[] { regimen, ccc });
		checkCertificate(certificateInputStream);

		try {
			return getEmployeesImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException | InterruptedException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
	}

	// RETURN ALL THE EMPLOYEES
	private static Collection<Employee> getEmployeesImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws ElementNotFoundException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setJavaScriptEnabled(false);
			ArrayList<Employee> employees = new ArrayList<>();

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
			manageStatusCode(htmlPage);

			HtmlForm formParts = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			formParts.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
			formParts.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
			formParts.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
			formParts.getInputByName("chk_chkgrupo1_1").setChecked(true);
			htmlPage = formParts.getInputByName("btn_Sub2207601004").click();
			manageStatusCode(htmlPage);

			return getEmployeesFromTable(htmlPage, employees, regimen, ccc);
		}
	}

	// HANDLE THE EXCEPTIONS OF GETPREVEMPLOYEEA METHOD
	public static Collection<Employee> getPrevEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {

		Toolkit.verifyData(new Object[] { regimen, ccc });
		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			return getPrevEmployeesImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException | InterruptedException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
	}

	// RETURN ALL THE PREV EMPLOYEES
	private static Collection<Employee> getPrevEmployeesImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws ElementNotFoundException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setJavaScriptEnabled(false);
			ArrayList<Employee> employees = new ArrayList<>();

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
			manageStatusCode(htmlPage);

			HtmlForm formParts = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			formParts.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
			formParts.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
			formParts.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
			formParts.getInputByName("chk_chkgrupo1_2").setChecked(true);
			htmlPage = formParts.getInputByName("btn_Sub2207601004").click();
			manageStatusCode(htmlPage);

			return getEmployeesFromTable(htmlPage, employees, regimen, ccc);
		}
	}

	// COMMON GETEMPLOYEE CODE
	private static ArrayList<Employee> getEmployeesFromTable(HtmlPage htmlPage, ArrayList<Employee> employees,
			String regime, String ccc) throws IOException, SegSocialException {

		int i = 1;
		boolean end = false;

		while (!end) {
			ArrayList<ArrayList<String>> datas = new ArrayList<>();
			ArrayList<String> empData = new ArrayList<>();
			Iterable<DomElement> tableContent = htmlPage.getElementById("Sub1000112079").getLastElementChild()
					.getChildElements();

			for (DomElement tr : tableContent) {
				Iterable<DomElement> rowContent = tr.getChildElements();
				for (DomElement td : rowContent) {
					if (i == 5) {
						empData.add(td.getVisibleText());
						datas.add(empData);
						empData = new ArrayList<>();
						i = 0;
					} else if (i == 1 && td.getVisibleText().equals(""))
						break;
					else
						empData.add(td.getVisibleText());
					i++;
				}
			}

			EmployeeBuilder builder = new EmployeeBuilder();

			for (ArrayList<String> empdata : datas) {
				String nss = empdata.get(0);
				if (nss != null)
					nss = nss.replace(" ", "");
				String name = empdata.get(1);
				Date fra = Toolkit.parseDate(empdata.get(2), "dd-MM-yyyy");
				String situation = empdata.get(3);
				if (situation.equals(""))
					situation = "AL";
				String ipf = empdata.get(4);

				if (ipf != null)
					ipf = Toolkit.removeExtraZeros(ipf.replace(" ", ""));

				Employee employee = builder.setNss(nss).setName(name).setFra(fra).setSituation(situation).setIpf(ipf)
						.setCtaCti(ccc).setRegime(regime).build();

				employees.add(employee);
			}
			HtmlInput btn = htmlPage.querySelector("input[name=btn_Sub2207801001]");
			htmlPage = btn.click();
			if (getSSCode(htmlPage) == 3145)
				end = true;
		}
		return employees;
	}

	// HANDLE GETEMPLOYEE EXCEPTIONS
	public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {

		verifyData(new Object[] { regimen, ccc, nss });
		checkCertificate(certificateInputStream);

		try {
			return getEmployeeImpl(certificateInputStream, certificatePassword, certificateType, nss);
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new StatusCodeException();
			}
		} catch (MalformedURLException | InterruptedException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
	}

	// RETURNS AN EMPLOYEE
	private static Employee getEmployeeImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String nss) throws IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			return employeeFullInfo(nss, webClient);
		}
	}

	// GETS A PDF CCC LIQUIDATION
	public static byte[] getCccLiquidation(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date liquidationPeriod) throws SegSocialException {
		checkCertificate(certificateInputStream);
		try {
			return getCccLiquidationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc,
					liquidationPeriod);
		} catch (FailingHttpStatusCodeException e) {
			throw new StatusCodeException();
		} catch (IOException e) {
			throw new SegSocialException();
		}
	}

	// GET PDF INFO
	public static byte[] getCccLiquidationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date liquidationPeriod)
			throws FailingHttpStatusCodeException, IOException, SegSocialException {

		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR");

			HtmlForm form = htmlPage.getFormByName("jacadaform");
			String[] cccSplit = SplitString(ccc, 2);

			form.getInputByName("txt_SDFREGCTA").setAttribute("value", regime);
			form.getInputByName("txt_SDFTESCTA").setAttribute("value", cccSplit[0]);
			form.getInputByName("txt_SDFCUENTA").setAttribute("value", cccSplit[1]);

			HtmlOption option = (HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1);
			option.click();
			manageStatusCode(htmlPage);

			String[] splitDate = Toolkit.formatDate(liquidationPeriod, "MM-yyyy").get().split("-");
			form.getInputByName("txt_SDFMES").setAttribute("value", splitDate[0]);
			form.getInputByName("txt_SDFAO").setAttribute("value", splitDate[1]);

			HtmlInput submit = form.querySelector("input[name=btn_Sub2207601004]");
			webClient.getOptions().setRedirectEnabled(true);
			InputStream stream = submit.click().getWebResponse().getContentAsStream();

			byte[] ret = stream.readAllBytes();
			stream.close();

			manageStatusCode(htmlPage);
			return ret;
		}
	}

	// GET PDF INFO
	public static byte[] getCccLaboralLife(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to)
			throws FailingHttpStatusCodeException, IOException, SegSocialException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage document = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR71&E=I&AP=AFIR");
			HtmlInput regimeInput = document.querySelector("#SDFREGCCO");
			HtmlInput cccInput1 = document.querySelector("#SDFTESCCO");
			HtmlInput cccInput2 = document.querySelector("#SDFNYCCCO");
			HtmlInput fromDayInput = document.querySelector("#SDFDIADESDEM");
			HtmlInput fromMonthInput = document.querySelector("#SDFMESDESDEM");
			HtmlInput fromYearInput = document.querySelector("#SDFAODESDEM");
			HtmlInput toDayInput = document.querySelector("#SDFDIAHASTAM");
			HtmlInput toMonthInput = document.querySelector("#SDFMESHASTAM");
			HtmlInput toYearIn = document.querySelector("#SDFAOHASTAM");
			HtmlOption onlineOption = document.querySelector("#ListaTipoImpresion option:nth-child(3)");
			HtmlSubmitInput continueButton = document.querySelector("#Sub2207001009");

			Integer[] fromArray = getDateArray(from);
			Integer[] toArray = getDateArray(to);
			ArrayList<String> cccArray = splitStringMultiple(ccc, 2);

			regimeInput.setAttribute("value", regime);
			cccInput1.setAttribute("value", cccArray.get(0));
			cccInput2.setAttribute("value", cccArray.get(1));

			fromDayInput.setAttribute("value", String.valueOf(fromArray[0]));
			fromMonthInput.setAttribute("value", String.valueOf(fromArray[1]));
			fromYearInput.setAttribute("value", String.valueOf(fromArray[2]));

			toDayInput.setAttribute("value", String.valueOf(toArray[0]));
			toMonthInput.setAttribute("value", String.valueOf(toArray[1]));
			toYearIn.setAttribute("value", String.valueOf(toArray[2]));

			onlineOption.click();
			try {
				UnexpectedPage doc = continueButton.click();
				byte[] pdf = doc.getWebResponse().getContentAsStream().readAllBytes();
				return pdf;
			} catch (Exception e) {
				throw new InvalidDataException();
			}
		}
	}
}