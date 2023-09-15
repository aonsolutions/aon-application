package solutions.aon.seg.social;

import static java.lang.Long.parseLong;
import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getTrimmedById;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.manageStatusCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;
import static solutions.aon.seg.social.toolkit.Toolkit.parseDate;
import static solutions.aon.seg.social.toolkit.Toolkit.verifyData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exception.NotRespondingException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.object.SecondaryUser.SecondaryUserBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

@Deprecated
class SistemaREDSecondaryUser {

	// HANDLE SECONDARYUSERS EXCEPTIONS
	public static SecondaryUser getSecondaryUserByIpf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ipf)
			throws SegSocialException {
		verifyData(new Object[] { ipf });
		try {
			return getSecondaryUserByIpfImpl(certificateInputStream, certificatePassword, certificateType, ipf);
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
		
		return null;
	}

	// GET THE SECONDARY USER BY IPF
	public static SecondaryUser getSecondaryUserByIpfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ipf)
			throws FailingHttpStatusCodeException, IOException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW67&E=I&AP=AUT");

			emptyUserSecundary(htmlPage);
			
			HtmlCheckBoxInput ch = htmlPage.querySelector("#chkgrupo1_2");
			htmlPage = ch.click();
			manageStatusCode(htmlPage);

			HtmlInput ipfInput = htmlPage.querySelector("#inputgrupo1_2_1");
			ipfInput.setAttribute("value", ipf);
			manageStatusCode(htmlPage);

			HtmlSubmitInput continueButton = htmlPage.querySelector("#Sub2207101004_46");
			htmlPage = continueButton.click();
			manageStatusCode(htmlPage);

			return getSecondaryUserInfo(htmlPage);
		}
	}

	public static SecondaryUser getSecondaryUserInfo(HtmlPage htmlPage) {

		// OBJECT CREATE
		String authoritation = getTrimmedById(htmlPage, "SDFAUTORIZ");
		String authoritationEntity = getTrimmedById(htmlPage, "SDFRAZSOCIALAUT");

		String mainUserName = getTrimmedById(htmlPage, "SDFNOMBREAUT");
		String mainUserIpf = getTrimmedById(htmlPage, "SDFIPFAUT");
		String mainUserNaf = getTrimmedById(htmlPage, "SDFNAFAUT");

		String name = getTrimmedById(htmlPage, "SDFNOMBRESEC");
		String province = getTrimmedById(htmlPage, "SDFPROVSEC");
		String naf = getRealNAF(getTrimmedById(htmlPage, "SDFNAFSEC"));
		String situation = getTrimmedById(htmlPage, "SDFSITSEC");
		Date situationDate = parseDate(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECSITSEC"), "dd/MM/yyyy");
		String telephone = getTrimmedById(htmlPage, "SDFTELEFONOSEC");
		String fax = getTrimmedById(htmlPage, "SDFFAXSEC");
		String mobile = getTrimmedById(htmlPage, "SDFMOVILSEC");
		String mail = getTrimmedById(htmlPage, "txtconcat1_2");

		SecondaryUserBuilder builder = new SecondaryUserBuilder();

		builder.setAuthoritation(authoritation).setAuthoritationEntity(authoritationEntity)
				.setMainUserName(mainUserName).setMainUserIpf(mainUserIpf).setMainUserNaf(mainUserNaf).setName(name)
				.setProvince(province).setNaf(naf).setSituation(situation).setSituationDate(situationDate)
				.setTelephone(telephone).setFax(fax).setMobile(mobile).setMail(mail);

		return builder.build();
	}

	private static String getRealNAF(String naf) {
		if (naf.length() == 9)
			naf = "0" + naf;

		String province = naf.substring(0, 2);
		String nafCenter = naf.substring(2, naf.length());

		String completeNAF = "";

		if (nafCenter.substring(0, 1).equals("0")) {
			Long nafD = parseLong(province + nafCenter.substring(1, nafCenter.length()));
			long mod = (nafD % 97);
			completeNAF = nafD + "" + (mod < 10 ? "0" + mod : mod);
		} else {
			Long nafD = parseLong(province + nafCenter);
			long mod = (nafD % 97);
			completeNAF = nafD + "" + (mod < 10 ? "0" + mod : mod);
		}

		if (completeNAF.length() == 11)
			completeNAF = "0" + completeNAF;

		return completeNAF;
	}

	// HANDLE EXCEPTIONS OF
	public static Collection<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SegSocialException {
		try {
			return getSecondaryUsersImpl(certificateInputStream, certificatePassword, certificateType);
		} catch (FailingHttpStatusCodeException | IOException e) {
			throw new SegSocialException(e);
		}
	}

	// GET SECONDARY USERS
	public static Collection<SecondaryUser> getSecondaryUsersImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType)
			throws FailingHttpStatusCodeException, IOException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW67&E=I&AP=AUT");

			emptyUserSecundary(htmlPage);
			
			HtmlCheckBoxInput ch = htmlPage.querySelector("#chkgrupo1_1");

			// La autorización XXXXX de la que es usted principal 
			// no tiene asociado ningún secundario
			if ( ch == null ) 
				return Collections.emptyList();  
				
			htmlPage = ch.click();
			manageStatusCode(htmlPage);

			HtmlSubmitInput btn = htmlPage.querySelector("#Sub2207101004_46");
			htmlPage = btn.click();

			DomNodeList<DomNode> checkboxes = htmlPage.querySelectorAll("#Sub0800010080>tbody>tr input[type=checkbox]");
			for (DomNode checkbox : checkboxes) {
				HtmlCheckBoxInput check = (HtmlCheckBoxInput) checkbox;
				htmlPage = check.click();
			}

			HtmlSubmitInput queryButton = htmlPage.querySelector("#Sub2205301004_72");
			htmlPage = queryButton.click();
			HtmlSubmitInput continueButton;

			ArrayList<SecondaryUser> users = new ArrayList<>();

			do {
				continueButton = htmlPage.querySelector("#Sub2207101004_92");
				if (continueButton == null)
					break;

				SecondaryUser user = getSecondaryUserInfo(htmlPage);
				users.add(user);
				htmlPage = continueButton.click();
			} while (true);

			return users;
		}
	}

	// HANDLE EXCEPTIONS OF registerSecondaryUserImpl()
	public static void registerSecondaryUserByNie(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String typeIpf, final String nie,
			String naf) throws SegSocialException {
		try {
			registerSecondaryUserImpl(certificateInputStream, certificatePassword, certificateType, typeIpf, nie, naf);
		} catch (FailingHttpStatusCodeException e){
			HandleStatusCodeException(e);
		}
		catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	// REGISTER SECONDARY USER
	public static void registerSecondaryUserImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String typeIpf, final String ipf,
			String naf) throws IOException, SegSocialException {

		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW68&E=I&AP=AUT");

			HtmlCheckBoxInput ch1 = htmlPage.querySelector("#chkgrupo1_1");
			htmlPage = ch1.click();

			HtmlOption opt1 = htmlPage.querySelector("#inputgrupo1_1_1 option:nth-child(" + typeIpf + ")");
			htmlPage = opt1.click();

			HtmlInput ipfTxt = htmlPage.querySelector("#inputgrupo1_1_2");
			ipfTxt.setAttribute("value", ipf);

			HtmlInput nafTxt = htmlPage.querySelector("#inputgrupo1_1_3");
			nafTxt.setAttribute("value", naf);

			HtmlSubmitInput submitButton = htmlPage.querySelector("#Sub2207101004_52");
			htmlPage = submitButton.click();
			manageStatusCode(htmlPage);

			HtmlSubmitInput nextSubmitButton = htmlPage.querySelector("#Sub2207101004_99");
			htmlPage = nextSubmitButton.click();

			HtmlSubmitInput finalSubmitButton = htmlPage.querySelector("#Sub2207101004_99");
			finalSubmitButton.click();

		} catch(FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		}
	}

	// HANDLE EXCEPTIONS OF deleteSecondaryUser
	public static void deleteSecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipfType, final String ipf) throws SegSocialException {
		try {
			deleteSecondaryUserImpl(certificateInputStream, certificatePassword, certificateType, ipfType, ipf);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (RuntimeException e) {
			throw new NotRespondingException();
		} catch (Exception e) {
			throw new SegSocialException(e);
		}

	}

	// DELETE SECONDARY USER
	public static void deleteSecondaryUserImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ipfType, final String ipf)
			throws IOException, SegSocialException, InterruptedException {

		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			webClient.getOptions().setTimeout(15000);
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW68&E=I&AP=AUT");

			HtmlCheckBoxInput ch1 = htmlPage.querySelector("#chkgrupo1_2");
			htmlPage = ch1.click();

			HtmlOption opt1 = htmlPage.querySelector("#inputgrupo1_2_1 option:nth-child(" + ipfType + ")");
			htmlPage = opt1.click();

			HtmlInput ipfTxt = htmlPage.querySelector("#inputgrupo1_2_2");
			ipfTxt.setAttribute("value", ipf);

			HtmlSubmitInput submitButton = htmlPage.querySelector("#Sub2207101004_52");
			htmlPage = submitButton.click();
			wait4(htmlPage, p -> p.querySelector("Sub2207101004_99")).orElseThrow();

			HtmlSubmitInput finalSubmitButton = htmlPage.querySelector("#Sub2207101004_99");
			manageStatusCode(htmlPage);

			finalSubmitButton.click();
		}
	}

	// HANDLE EXCEPTIONS OF
	public void modifySecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) {
		modifySecondaryUserImpl(certificateInputStream, certificatePassword, certificateType, ipf);
	}

	// MODIFY SECONDARY USERS
	public void modifySecondaryUserImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) {

	}
	
	private static void emptyUserSecundary(HtmlPage htmlPage) throws NoQueryData{
		DomNode msgOne = htmlPage.querySelector("#Sub1000401071");
		if(msgOne!=null && !msgOne.getVisibleText().isEmpty()) {
			DomNode msgTwo = htmlPage.querySelector("#Sub1100401071");
			String msgError = msgOne.getVisibleText().trim();
			if(msgTwo!=null && !msgTwo.getVisibleText().isEmpty()) {
				msgError += " ";
				msgError = msgError.concat(msgTwo.getVisibleText().trim());
			}
			throw new NoQueryData(msgError);
		}
	}
}
