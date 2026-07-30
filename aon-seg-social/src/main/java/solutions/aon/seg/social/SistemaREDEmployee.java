package solutions.aon.seg.social;

import static solutions.aon.seg.social.exception.InvalidCertificateException.checkCertificate;
import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getSSCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.manageStatusCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;
import static solutions.aon.seg.social.toolkit.Toolkit.SplitString;
import static solutions.aon.seg.social.toolkit.Toolkit.getDateArray;
import static solutions.aon.seg.social.toolkit.Toolkit.noSpaces;
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
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.UnexpectedPage;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDEmployee {

	// Toolkit.buildFile(htmlPage.asXml().getBytes(),
	// System.getProperty("user.home")+"/test.html");

	private static final String FORMAT_DATE_ES = "dd/MM/yyyy";

	// DATOS PERSONALES
	// NUSS: 13 1011852553 IPF: 6 0X7028854P F.Nacimiento: 23 10 1987 Sexo: V
	// DELGADO MARULANDA ANDRES FELIPE Movil para SMS: 674498291
	private static final Pattern EMPlOYEE_DATA = Pattern.compile(
			"nuss:\\s+(?<nss>\\d+\\s*\\d+)?.*ipf:\\s+(?<identity>\\d+)\\s+(?<ipf>\\w+).*f.nacimiento:\\s+(?<birthDate>\\d{2}\\s\\d{2}\\s\\d{4})?.*sexo:\\s+(?<sex>\\w+)?\\s+(?<name>.+)\\s+movil.+sms:\\s+(?<tlf>\\d{9})?.*$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	// DATOS EMPRESA
	// Cta. Cotiz.: 28 249788734 R?imen: 0111 Ident.Empr.: 9 0B01991256 Nombre ...:
	// RIKAMBA SL
	private static final Pattern ENTERPRISE_DATA = Pattern.compile(
			"cta.*cotiz.*:\\s+(?<ccc>\\d{2}\\s\\d{9}).*r.gimen:\\s+(?<regime>\\d{4}).*ident.*:\\s+(?<identityCif>\\d{1})?\\s+(?<cif>\\w+)\\s+nombre\\s+...:\\s+(?<enterpriseName>\\w+)\\s+",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static final Pattern CONTRACT_DATA = Pattern.compile(
			"situaci.n:\\s+(?<situation>.+)\\s+g\\.c\\.:\\s+(?<gc>\\d{1,2})?.*f\\.r\\.a\\.:\\s+(?<fra>\\d{2}\\/\\d{2}\\/\\d{4}).*f\\.e\\.a\\.:\\s+(?<fea>\\d{2}\\/\\d{2}\\/\\d{4}).*f\\.r\\.b\\.:\\s+(?<frb>\\d{2}\\/\\d{2}\\/\\d{4})?.*f\\.e\\.b\\.:\\s+(?<feb>\\d{2}\\/\\d{2}\\/\\d{4})?.*contrato:\\s+(?<contract>\\d{3}).*coef\\.:\\s+(?<coef>\\d{1,3}\\,\\d{1,3})?.*colec\\.:\\s+(?<colec>\\d)?.*ocup:\\s+(?<ocup>\\w{1}\\s+)?.*",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

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
			}
			try {
				employees.addAll(getPrevEmployees(new ByteArrayInputStream(cert), certificatePassword, certificateType,
						regimen, ccc));
			} catch (NoQueryData ignored) {
			}

			return employees;
		} catch (IOException e) {
			throw new InvalidCertificateException();
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		}
		return null;

	}

	// CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIEeS
	private static Employee employeeFullInfo(String ccc, String nss, WebClient webClient)
			throws IOException, InterruptedException, SegSocialException {
		HtmlPage page = webClient
				.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR");

		HtmlForm formParts = wait4(page, p -> p.getFormByName("jacadaform")).orElseThrow();
		manageStatusCode(page);

		formParts.getInputByName("txt_SDFTESORNAF").setValue(nss.substring(0, 2));
		formParts.getInputByName("txt_SDFNUMNAF").setValue(nss.substring(2));
		formParts.getInputByName("btn_Sub2207601004").focus();
		page = formParts.getInputByName("btn_Sub2207601004").click();

		HtmlTable table = (HtmlTable) page.querySelector("#Sub1000110078");
		if (table != null) {
			for (final HtmlTableRow row : table.getRows()) {
				HtmlTableCell cell = row.getCell(1);
				if (cell.getVisibleText().replaceAll("\\s", "").indexOf(ccc) >= 0) {
					HtmlLabel label = cell.querySelector("label");
					page = label.dblClick();
					break;
				}
			}
		}

		manageStatusCode(page);

		List<Employee> employees = getEmployeeFullListRegex(page, nss);
		if (!employees.isEmpty()) {
			return employees.get(0);
		}

		throw new DataDoesNotExist();
	}

	// CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIEeS
	private static List<Employee> getEmployeeFullListRegex(HtmlPage page, String nss) throws IOException {

		List<Employee> list = new ArrayList<>();

		while (page.getElementById("Sub0400101079_77") != null) {

			String text = page.asNormalizedText();

//		    	String[] lines = text.split("\\r?\\n");
//		    	for (String line : lines) {
//		    	    System.out.println("line>>>"+line);
//		    	}
			String ipf = "";
			String birthDateStr = "";
			String sex = "";
			String name = "";
			String tlf = "";
			String ctaCot = "";
			String regime = "";
			String companyId = "";
			String companyName = "";
			String situation = "";
			String gc = "";
			String contract = "";
			String coef = "";
			String colec = "";
			String ocup = "";
			String fraStr = "";
			String feaStr = "";
			String frbStr = "";
			String febStr = "";

//				String vinFam = "";
//				String profesCat = "";
//				String reducingCoef = "";
//				String gcDesc = "";
//				Boolean agricultPromo = "";
//				Boolean workTimeReduct = "";
//				String epig = "";

			Matcher employeeMatcher = EMPlOYEE_DATA.matcher(text);

			if (employeeMatcher.find()) {
				System.out.println("---------------EMPLOYEE DATA-----------------");
				System.out.println("nss->" + employeeMatcher.group("nss"));
				System.out.println("identity->" + employeeMatcher.group("identity"));
				System.out.println("ipf->" + employeeMatcher.group("ipf"));
				System.out.println("birthDate->" + employeeMatcher.group("birthDate"));
				System.out.println("sex->" + employeeMatcher.group("sex"));
				System.out.println("name->" + employeeMatcher.group("name"));
				System.out.println("tlf->" + employeeMatcher.group("tlf"));
				System.out.println("--------------------------------");

				ipf = employeeMatcher.group("ipf") != null
						? removeExtraZeros(employeeMatcher.group("ipf").replaceAll("^0+", ""))
						: "";

				birthDateStr = employeeMatcher.group("birthDate") != null ? employeeMatcher.group("birthDate") : "";

				sex = employeeMatcher.group("sex") != null ? employeeMatcher.group("sex") : "";

				name = employeeMatcher.group("name") != null ? employeeMatcher.group("name") : "";

				tlf = employeeMatcher.group("tlf") != null ? employeeMatcher.group("tlf") : "";
			}

			Matcher enterpriseMatcher = ENTERPRISE_DATA.matcher(text);

			if (enterpriseMatcher.find()) {
				System.out.println("---------------ENTERPRISE DATA-----------------");
				System.out.println("ccc->" + enterpriseMatcher.group("ccc"));
				System.out.println("regime->" + enterpriseMatcher.group("regime"));
				System.out.println("identityCif->" + enterpriseMatcher.group("identityCif"));
				System.out.println("cif->" + enterpriseMatcher.group("cif"));
				System.out.println("enterpriseName->" + enterpriseMatcher.group("enterpriseName"));
				System.out.println("--------------------------------");

				regime = enterpriseMatcher.group("regime") != null ? enterpriseMatcher.group("regime") : "";

				ctaCot = enterpriseMatcher.group("ccc") != null ? noSpaces(enterpriseMatcher.group("ccc")) : "";

				companyId = enterpriseMatcher.group("cif") != null ? removeExtraZeros(enterpriseMatcher.group("cif"))
						: "";

				companyName = enterpriseMatcher.group("enterpriseName") != null
						? enterpriseMatcher.group("enterpriseName")
						: "";
			}

			Matcher contractMatcher = CONTRACT_DATA.matcher(text);

			if (contractMatcher.find()) {
				System.out.println("---------------CONTRACT DATA-----------------");
				System.out.println("situation->" + contractMatcher.group("situation"));
				System.out.println("gc->" + contractMatcher.group("gc"));
				System.out.println("fra->" + contractMatcher.group("fra"));
				System.out.println("fea->" + contractMatcher.group("fea"));
				System.out.println("frb->" + contractMatcher.group("frb"));
				System.out.println("feb->" + contractMatcher.group("feb"));
				System.out.println("contract->" + contractMatcher.group("contract"));
				System.out.println("coef->" + contractMatcher.group("coef"));
				System.out.println("colec->" + contractMatcher.group("colec"));
				System.out.println("ocup->" + contractMatcher.group("ocup"));
				System.out.println("--------------------------------");

				situation = contractMatcher.group("situation") != null ? contractMatcher.group("situation") : "";

				gc = contractMatcher.group("gc") != null ? contractMatcher.group("gc") : "";

				fraStr = contractMatcher.group("fra") != null ? contractMatcher.group("fra") : "";

				feaStr = contractMatcher.group("fea") != null ? contractMatcher.group("fea") : "";

				frbStr = contractMatcher.group("frb") != null ? contractMatcher.group("frb") : "";

				febStr = contractMatcher.group("feb") != null ? contractMatcher.group("feb") : "";

				contract = contractMatcher.group("contract") != null ? contractMatcher.group("contract") : "";

				coef = contractMatcher.group("coef") != null ? contractMatcher.group("coef") : "";

				colec = contractMatcher.group("colec") != null ? contractMatcher.group("colec") : "";

				ocup = contractMatcher.group("ocup") != null ? contractMatcher.group("ocup") : "";
			}

			Date birthDate = parseDate(birthDateStr, "dd mm yyyy");
			Date fra = parseDate(fraStr, FORMAT_DATE_ES);
			Date fea = parseDate(feaStr, FORMAT_DATE_ES);
			Date frb = parseDate(frbStr, FORMAT_DATE_ES);
			Date feb = parseDate(febStr, FORMAT_DATE_ES);

//				if(frb==null && fra!=null) {
//					DomNode next = page.querySelector("[value=\"Continuar\"]");
//					if(next!=null) {
//						 page = ((HtmlSubmitInput) next).click();
//						 
//						 Matcher contractTwoMatcher = CONTRACT_DATA.matcher(page.asNormalizedText());
//							
//						 if (contractTwoMatcher.find()) {
//							  System.out.println("---------------CONTRACT DATA-----------------");
//						      System.out.println("situation->"+contractTwoMatcher.group("situation"));
//					          System.out.println("gc->"+contractTwoMatcher.group("gc"));
//					          System.out.println("fra->"+contractTwoMatcher.group("fra"));
//					          System.out.println("fea->"+contractTwoMatcher.group("fea"));
//					          System.out.println("frb->"+contractTwoMatcher.group("frb"));
//					          System.out.println("feb->"+contractTwoMatcher.group("feb"));
//					          System.out.println("contract->"+contractTwoMatcher.group("contract"));
//					          System.out.println("coef->"+contractTwoMatcher.group("coef"));
//					          System.out.println("colec->"+contractTwoMatcher.group("colec"));
//					          System.out.println("ocup->"+contractTwoMatcher.group("ocup"));
//					          System.out.println("--------------------------------");
//					          
//							 frbStr = contractTwoMatcher.group("frb")!=null ? contractTwoMatcher.group("frb") : "";
//						     febStr = contractTwoMatcher.group("feb")!=null ? contractTwoMatcher.group("feb") : "";
//							 if(!frbStr.isEmpty() && !febStr.isEmpty()) {
//								Date endDate = parseDate(frbStr, FORMAT_DATE_ES);
//								if(fra.before(endDate)) {
//									situation = contractTwoMatcher.group("situation")!=null ? contractMatcher.group("situation") : "";
//									frb = endDate;
//									feb = parseDate(febStr, FORMAT_DATE_ES);
//								} 
//							 }
//						 }
//					}
//				}

			// BUILD
			Employee employeeData = new EmployeeBuilder().setIpf(ipf).setNss(nss).setName(name).setSituation(situation)
					.setBirthDate(birthDate).setSex(sex).setTlf(tlf).setCtaCti(ctaCot).setRegime(regime)
					.setCompanyId(companyId).setCompanyName(companyName).setSituation(situation).setGc(gc).setFra(fra)
					.setFea(fea).setFrb(frb).setFeb(feb).setContract(contract).setCoef(coef).setColec(colec)
					.setOcup(ocup)
//				.setEpig(epig)
//				.setVinFam(vinFam)
//				.setProfesCat(profesCat)
//				.setReducingCoefic(reducingCoef)
//				.setGcDesc(gcDesc)
//				.setAgricultPromo(agricultPromo)
//				.setWorkTimeReduct(workTimeReduct)
					.build();

			list.add(employeeData);

//				DomNode next = page.querySelector("[value=\"Continuar\"]");
//				if(next!=null) {
//					 page = ((HtmlSubmitInput) next).click();
//				} else {
			break;
//				}
		}

		return list;
	}

	// CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIEeS
	private static List<Employee> getEmployeeFullListOld(HtmlPage page, String nss) throws IOException {

		List<Employee> list = new ArrayList<>();
		while (page.getElementById("SDFNUMIPF") != null) {

			String ipf = page.getElementById("SDFNUMIPF").getTextContent().trim().replaceAll("^0+", "");
			String birthDateStr = page.getElementById("SDFDIANAC").getTextContent() + "/"
					+ page.getElementById("SDFMESNAC").getTextContent() + "/"
					+ page.getElementById("SDFAONAC").getTextContent();

			if (birthDateStr.equals(" / / ")) {
				birthDateStr = "";
			}

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

			Boolean workTimeReduct = Toolkit.toBoolean(domElementExists(page.getElementById("SDFLITRJ")));
			String contract = domElementExists(page.getElementById("SDFTIPOAFI"));
			String coef = domElementExists(page.getElementById("SDFCOEFAFI"));
			String colec = domElementExists(page.getElementById("SDFCOLECTIVO"));
			String epig = domElementExists(page.getElementById("SDFEPIGAFI"));
			String ocup = domElementExists(page.getElementById("SDFOCUPACION"));
			String vinFam = domElementExists(page.getElementById("SDFVINCULO"));
			String profesCat = domElementExists(page.getElementById("SDFCATEGORIA"));
			String reducingCoef = domElementExists(page.getElementById("SDFCOEFRED"));

			String frbStr = domElementExists(page.getElementById("SDFFRBAFI"));
			String febStr = domElementExists(page.getElementById("SDFFEBAFI"));
			String fraStr = domElementExists(page.getElementById("SDFFRAAFI"));
			String feaStr = domElementExists(page.getElementById("SDFFEAAFI"));

			if (fraStr.isEmpty()) {
				fraStr = domElementExists(page.getElementById("SDFFRASAN1"));
				feaStr = domElementExists(page.getElementById("SDFFEASAN1"));
			}

			Date birthDate = parseDate(birthDateStr, FORMAT_DATE_ES);
			Date fra = parseDate(fraStr, FORMAT_DATE_ES);
			Date fea = parseDate(feaStr, FORMAT_DATE_ES);
			Date frb = parseDate(frbStr, FORMAT_DATE_ES);
			Date feb = parseDate(febStr, FORMAT_DATE_ES);

			ipf = removeExtraZeros(ipf).replace(" ", "");
			companyId = removeExtraZeros(companyId);

			if (frb == null && fra != null) {
				DomNode next = page.querySelector("[value=\"Continuar\"]");
				if (next != null) {
					page = ((HtmlSubmitInput) next).click();
					frbStr = domElementExists(page.getElementById("SDFFRBSAN1"));
					febStr = domElementExists(page.getElementById("SDFFEBSAN1"));
					if (!frbStr.isEmpty() && !febStr.isEmpty()) {
						Date endDate = parseDate(frbStr, FORMAT_DATE_ES);
						if (fra.before(endDate)) {
							situation = page.getElementById("SDFTSITUACAFI").getTextContent();
							frb = endDate;
							feb = parseDate(febStr, FORMAT_DATE_ES);
						}
					}
				}
			}

			// BUILD
			Employee employeeData = new EmployeeBuilder().setIpf(ipf).setNss(nss).setName(name).setSituation(situation)
					.setBirthDate(birthDate).setSex(sex).setTlf(tlf).setCtaCti(ctaCot).setRegime(regime)
					.setCompanyId(companyId).setCompanyName(companyName).setSituation(situation).setGc(gc)
					.setGcDesc(gcDesc).setAgricultPromo(agricultPromo).setWorkTimeReduct(workTimeReduct).setFra(fra)
					.setFea(fea).setFrb(frb).setFeb(feb).setContract(contract).setCoef(coef).setColec(colec)
					.setEpig(epig).setOcup(ocup).setVinFam(vinFam).setProfesCat(profesCat)
					.setReducingCoefic(reducingCoef).build();

			list.add(employeeData);

			DomNode next = page.querySelector("[value=\"Continuar\"]");
			if (next != null) {
				page = ((HtmlSubmitInput) next).click();
			} else {
				break;
			}
		}

		return list;
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
	/**
	 * Use solutions.aon.seg.social.ServicioREDEmployee.getTotalEmployees in order
	 * to use in client and server side.
	 */
	@Deprecated
	private static Collection<Employee> getEmployeesImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws ElementNotFoundException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			ArrayList<Employee> employees = new ArrayList<>();

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");

			Toolkit.checkCertificateRevoked(htmlPage.asXml());

			manageStatusCode(htmlPage);

			HtmlForm formParts = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			formParts.getInputByName("txt_SDFREG62_ayuda").setValue(regimen);
			formParts.getInputByName("txt_SDFTESO62").setValue(ccc.substring(0, 2));
			formParts.getInputByName("txt_SDFNUM62").setValue(ccc.substring(2));
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

			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			ArrayList<Employee> employees = new ArrayList<>();

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
			manageStatusCode(htmlPage);

			HtmlForm formParts = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			formParts.getInputByName("txt_SDFREG62_ayuda").setValue(regimen);
			formParts.getInputByName("txt_SDFTESO62").setValue(ccc.substring(0, 2));
			formParts.getInputByName("txt_SDFNUM62").setValue(ccc.substring(2));
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

				if (ipf != null) {
					if (ipf.contains(" ")) ipf = ipf.split(" ")[1];
					ipf = Toolkit.removeExtraZeros(ipf.replace(" ", ""));
				}

				builder.setNss(nss).setName(name).setFra(fra).setSituation(situation).setIpf(ipf).setCtaCti(ccc)
						.setRegime(regime);

				if (!situation.contains("AL"))
					builder.setFrb(fra);

				employees.add(builder.build());
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
			return getEmployeeImpl(certificateInputStream, certificatePassword, certificateType, ccc, nss);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException | InterruptedException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
	}

	// RETURNS AN EMPLOYEE
	private static Employee getEmployeeImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String ccc, String nss)
			throws IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setUseInsecureSSL(true);
			return employeeFullInfo(ccc, nss, webClient);
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

			Integer[] fromArray = getDateArray(from);
			Integer[] toArray = getDateArray(to);
			ArrayList<String> cccArray = splitStringMultiple(ccc, 2);

			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setDownloadImages(true);
			webClient.setJavaScriptTimeout(10000);
			HtmlPage document = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR71&E=I&AP=AFIR");
			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = document.getElementById("Sub2207001009");
				if (formSubmit != null)
					break;
				synchronized (document) {
					document.wait(500);
				}
			}

			HtmlForm form = HtmlUnitToolkit.wait4(document, p -> p.getFormByName("jacadaform")).orElseThrow();

			form.getInputByName("txt_SDFREGCCO").setValue(regime);
			form.getInputByName("txt_SDFREGCCO").setValueAttribute(regime);
			
			form.getInputByName("txt_SDFTESCCO").setValue(cccArray.get(0));
			form.getInputByName("txt_SDFTESCCO").setValueAttribute(cccArray.get(0));
			
			form.getInputByName("txt_SDFNYCCCO").setValue(cccArray.get(1));
			form.getInputByName("txt_SDFNYCCCO").setValueAttribute(cccArray.get(1));

			HtmlInput fromDayInput = document.querySelector("#SDFDIADESDEM");
			HtmlInput fromMonthInput = document.querySelector("#SDFMESDESDEM");
			HtmlInput fromYearInput = document.querySelector("#SDFAODESDEM");
			HtmlInput toDayInput = document.querySelector("#SDFDIAHASTAM");
			HtmlInput toMonthInput = document.querySelector("#SDFMESHASTAM");
			HtmlInput toYearIn = document.querySelector("#SDFAOHASTAM");
			HtmlOption onlineOption = document.querySelector("#ListaTipoImpresion option:nth-child(3)");

			fromDayInput.setValue(String.valueOf(fromArray[0]));
			fromDayInput.setValueAttribute(String.valueOf(fromArray[0]));
			
			fromMonthInput.setValue(String.valueOf(fromArray[1]));
			fromMonthInput.setValueAttribute(String.valueOf(fromArray[1]));
			
			fromYearInput.setValue(String.valueOf(fromArray[2]));
			fromYearInput.setValueAttribute(String.valueOf(fromArray[2]));

			toDayInput.setValue(String.valueOf(toArray[0]));
			toDayInput.setValueAttribute(String.valueOf(toArray[0]));
			
			toMonthInput.setValue(String.valueOf(toArray[1]));
			toMonthInput.setValueAttribute(String.valueOf(toArray[1]));
			
			toYearIn.setValue(String.valueOf(toArray[2]));
			toYearIn.setValueAttribute(String.valueOf(toArray[2]));

			onlineOption.click();
		
			formSubmit.click();

			return getPDFDocument((HtmlElement) formSubmit);

		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
		return null;
	}

	// GET PDF INFO
	public static byte[] getLaboralLife(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String nss)
			throws FailingHttpStatusCodeException, IOException, SegSocialException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage document = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR63&E=I&AP=AFIR");

			HtmlInput nssInput = document.querySelector("#SDFTESORNAF");
			HtmlInput nssInput1 = document.querySelector("#SDFNUMNAF");

			nssInput.setValue(nss.substring(0, 2));
			nssInput1.setValue(nss.substring(2));

			HtmlOption onlineOption = document.querySelector("#ListaTipoImpresion option:nth-child(3)");
			onlineOption.click();

			HtmlSubmitInput continueButton = document.querySelector("#Sub2207001009");

			// Check if we have more than one CCC for this person
			try {
				document = continueButton.click();
				Integer ssCode = HtmlUnitToolkit.getSSCode(document);
				// Select the current CCC
				if (ssCode == 3710) {
					ArrayList<String> cccArray = splitStringMultiple(ccc, 2);
					String cccStr = regime + " " + cccArray.get(0) + " " + cccArray.get(1);
					Optional<DomNode> domNode = document.querySelectorAll("label").stream()
							.filter(label -> label.getTextContent().equals(cccStr)).findFirst();
					domNode.map(node -> {
						return getPDFDocument((HtmlLabel) node);
					});
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}

			return getPDFDocument(continueButton);
			// DESCOMENTAR LINEAS
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getPDFDocument(HtmlElement linkElement) throws IllegalArgumentException {
		try {
			UnexpectedPage docPage = linkElement.dblClick();
			return docPage.getWebResponse().getContentAsStream().readAllBytes();
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch ( ClassCastException e) {
			throw new IllegalArgumentException(" El informe requerido excede el límite de información de transmisión permitido. Solicítelo en Diferido");
		} 
	}

	private static String domElementExists(DomElement domEl) {
		return domEl != null ? domEl.getTextContent() : "";
	}

}
