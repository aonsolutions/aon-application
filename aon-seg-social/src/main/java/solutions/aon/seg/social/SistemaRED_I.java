package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLLabelElement;

import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.NoMoreDataException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.objects.Idc;
import solutions.aon.seg.social.objects.SituacionEmpresa;
import solutions.aon.seg.social.objects.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRED_I {

	public static SituacionEmpresa getSituacionEmpresa(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws MalformedURLException, IOException, InterruptedException, SegSocialException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");

			htmlPage = HtmlUnitToolkit
					.wait4(htmlPage,
							p -> p.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR"))
					.orElseThrow().click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementsById("SDFREGCTA_ayuda"));
			HtmlForm jacadaform = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			// Separando el ccc
			String ccc1 = ccc.substring(0, 2);
			String ccc2 = ccc.substring(2);

			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValueAttribute(regime);
			jacadaform.getInputByName("txt_SDFTESCTA").setValueAttribute(ccc1);
			jacadaform.getInputByName("txt_SDFNUMCTA").setValueAttribute(ccc2);

			// click en 'Continuar'
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			// control de campos
			String nif = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFEMPRESARIO3");
			String cadFecha = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHASIT");
			String fechaAlta = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAALTA");
			String fechaAlta13 = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAALTA13");
			String fechaBaja93 = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHABAJA93");
			String esctaller = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCESTAL");
			Boolean booltaller = Toolkit.toBoolean(esctaller);
			String plazoRed = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPLAZORED");
			String fechaAutRed = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAAUTRED");
			Integer trabajadorAlta = null;
			Integer trabajador2Alta = null;
			Integer ta2baja = null;
			Float tiposATyEpit = null;
			Float ims = null;
			Float total = null;

			try {
				trabajadorAlta = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNROTRA3"));
			} catch (NumberFormatException e) {
			}
			try {
				trabajador2Alta = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCONDIAS3"));
			} catch (NumberFormatException e) {
			}
			try {
				ta2baja = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCONDPPB3"));
			} catch (NumberFormatException e) {
			}
			try {
				tiposATyEpit = Float
						.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTITN12").replace(",", "."));
			} catch (NumberFormatException e) {
			}
			try {
				ims = Float.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIMSN12").replace(",", "."));
			} catch (NumberFormatException e) {
			}
			try {
				total = Float.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTTOTN12").replace(",", "."));
			} catch (NumberFormatException e) {
			}

			if (nif.length() > 9)
				nif = Toolkit.removeExtraZeros(nif);

			// Pasando los valores al objeto
			SituacionEmpresaBuilder seb1 = new SituacionEmpresaBuilder();

			// DATOS IDENTIFICATIVOS
			seb1.setCcc(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVINCIA3")
					+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNISS3"))
					.setIdEmpresario(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPO3")).setNif_empresa(nif)
					.setRegimen(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFREGIMEN3"))
					.setNss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPRONAF3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMNAF30"))
					.setCccp(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVINCIACP3"))
					.setUgtgss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESORERIA3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMON3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFURE3"))
					.setUgtgsscccp(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESPPAL")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMPPAL")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFUREPPAL"))
					.setUgcentral(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFUNIDAD"))
					.setOgism(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROUGISM")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCUGISM"))
					.setCccAnt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVCANT3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMCANT3"))
					.setCccSuc(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROSUC3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMSUC3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFREGSUC3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCOASUC3"))
					.setSit(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFSITUACION3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTSITUACION3"))
					.setfSit(Toolkit.parseDate(cadFecha, "dd/MM/yyyy"))
					.setfAltaInicial(Toolkit.parseDate(fechaAlta, "dd/MM/yyyy")).setTrabajadorAlta(trabajadorAlta)
					.setAltaPrTrab(Toolkit.parseDate(fechaAlta13, "dd/MM/yyyy"))
					.setUltBajaEfCot(Toolkit.parseDate(fechaBaja93, "dd/MM/yyyy"))
					.setTrl(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPCON")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDESTIPCON"))
					.setcEsp_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOLECTIVO3"))
					.setcEsp_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDESCOL3"))
					.setCnae09_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV093"))
					.setCnae93_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV933"))
					.setCnae09_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV093"))
					.setCnae93_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV933")).setTa2Alta(trabajador2Alta)
					.setTa2Baja(ta2baja).setTiposATyEPIT(tiposATyEpit).setIms(ims).setTotal(total)
					.setCoeJub_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOREJU"))
					.setCoeJub_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSCOREJU"))
					.setAconExtra(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTMEXTR")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSACONT"))
					.setEscTaller(booltaller)
					.setAutorizacionRed(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFAUTORIDRED"))
					.setPlazoIncorpRed(Toolkit.parseDate(plazoRed, "dd/MM/yyyy"))
					.setFechaAutCan(Toolkit.parseDate(fechaAutRed, "dd/MM/yyyy"));

			// Datos identificativos
			jacadaform = htmlPage.getFormByName("jacadaform");

			htmlPage = jacadaform.getInputByValue("Datos Iden.").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("SDFLOCALIDAD4"));

			seb1.setAnagrama(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFANAGR3"))
					.setEmbarcacion(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPEMB")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFEMB")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOMEMBAR"))
					.setTlfMovil(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELMOVIL3"))
					.setTlfFijo(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELFIJO3"))
					.setEmail(HtmlUnitToolkit.getTrimmedById(htmlPage, "txtconcat1_1"))
					.setNotif_dom_empresa(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF3")))
					.setTipo_via_dir_empresa(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA3"))
					.setDir_emp_calle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO3"))
					.setDir_emp_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
					.setDir_emp_bis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS3"))
					.setDir_emp_bloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE3"))
					.setDir_emp_es(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA3"))
					.setDir_emp_piso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO3"))
					.setDir_emp_p(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA3"))
					.setDir_emp_CP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL3"))
					.setDir_emp_num_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD13"))
					.setDir_emp_nom_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD23"))
					.setDir_emp_tlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO3"))
					.setNotif_dom_actividad(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF4")))
					.setAct_ugtgss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESORERIA3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMON3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFURE3"))
					.setTipo_via_dir_actividad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA4"))
					.setDir_act_calle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO4"))
					.setDir_act_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
					.setDir_act_bis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS4"))
					.setDir_act_bloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE4"))
					.setDir_act_es(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA4"))
					.setDir_act_piso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO4"))
					.setDir_act_p(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA4"))
					.setDir_act_CP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL4"))
					.setDir_act_num_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFMUNICIPIO4"))
					.setDir_act_nom_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD4"))
					.setDir_act_tlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO4"));

			return seb1.build();
		} catch (FailingHttpStatusCodeException fhsce) {
			switch (fhsce.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}
		}

	}

	public static byte[] getContributionInformation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String affiliationNumber, String regime,
			String contributionAccount, Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime,
					contributionAccount, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getTADuplicate(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String affiliationNumber, String regime, String contributionAccount,
			Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime,
					contributionAccount, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getPdfInfo(final String href, final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage = htmlPage.getAnchorByHref(href).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValueAttribute(regime);
			} catch (ElementNotFoundException enfe) {
				jacadaform.getInputByName("txt_SDFREGCTA").setValueAttribute(regime);
			}
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(fecha);
			jacadaform.getInputByName("txt_SDFDIA").setValueAttribute("" + calendar.get(Calendar.DAY_OF_MONTH));
			jacadaform.getInputByName("txt_SDFMES").setValueAttribute("" + (calendar.get(Calendar.MONTH) + 1));
			jacadaform.getInputByName("txt_SDFAO").setValueAttribute("" + calendar.get(Calendar.YEAR));
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = de.click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			// REVISAR SPLIT
			// Obtaining the first table registry's label to double-click on it so that it
			// loads the pdf
			List<HtmlLabel> labels = htmlPage.getByXPath("//label[@name='_1_0']");
			InputStream is = labels.get(0).dblClick().getWebResponse().getContentAsStream();
			byte[] ret = is.readAllBytes();
			is.close();
			return ret;
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}

		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static Collection<byte[]> getTACertificatePDFs (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException{
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, contributionAccount, fecha);
	}
	
	public static Collection<byte[]> getContributionPDFs (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException{
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, contributionAccount, fecha);
	}
	
	public static Collection<byte[]> getPdfsInfo(final String href, final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage = htmlPage.getAnchorByHref(href).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValueAttribute(regime);
			} catch (ElementNotFoundException enfe) {
				jacadaform.getInputByName("txt_SDFREGCTA").setValueAttribute(regime);
			}
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(fecha);
			jacadaform.getInputByName("txt_SDFDIA").setValueAttribute("" + calendar.get(Calendar.DAY_OF_MONTH));
			jacadaform.getInputByName("txt_SDFMES").setValueAttribute("" + (calendar.get(Calendar.MONTH) + 1));
			jacadaform.getInputByName("txt_SDFAO").setValueAttribute("" + calendar.get(Calendar.YEAR));
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			ArrayList<byte[]> ret=new ArrayList<byte[]>();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = de.click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			// REVISAR SPLIT
			// Obtaining the first table registry's label to double-click on it so that it
			// loads the pdf
			
			boolean found=false;
			int indTab=2;
 			DomNodeList<DomNode> iter=htmlPage.querySelectorAll("#Sub0900112079>tbody>tr");
 			if(iter.size()==0) {
 				iter=htmlPage.querySelectorAll("#Sub0900112078>tbody>tr");
 				indTab=1;
 			}
 			String dia="";
 			String mes="";
 			if(calendar.get(Calendar.DATE)<10) {
 				dia="0"+calendar.get(Calendar.DATE);
 			}
 			else {
 				dia=""+calendar.get(Calendar.DATE);
 			}
 			
 			if((calendar.get(Calendar.MONTH)+1)<10) {
 				mes="0"+(calendar.get(Calendar.MONTH)+1);
 			}
 			else {
 				mes=""+(calendar.get(Calendar.MONTH)+1);
 			}
 			
			String strDate=""+dia+" "+mes+" "+calendar.get(Calendar.YEAR);
			for (DomNode domNode : iter) {
				DomNodeList<DomNode> dn2=domNode.querySelectorAll("td");
				
				if(dn2.get(indTab).getVisibleText().equalsIgnoreCase(strDate)){
					found=true;
					HtmlLabel htmlLabel=dn2.get(indTab).querySelector("label");
					InputStream is=htmlLabel.dblClick().getWebResponse().getContentAsStream();
					ret.add(is.readAllBytes());
					is.close();
				}
				else if(found==true) {
					break;
				}
				
			}
			
			
			
			//List<HtmlLabel> labels = htmlPage.getByXPath("//label[@name='_1_0']");
			/*InputStream is = labels.get(0).dblClick().getWebResponse().getContentAsStream();
			byte[] ret = is.readAllBytes();
			is.close();*/
			return ret;
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}

		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	

	public static byte[] getObligationAwarenessCertificate(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String contributionAccount)
			throws SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuDEUDA.html");
			htmlPage = htmlPage.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR")
					.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Inputting contribution account and regime
			jacadaform.getInputByName("txt_SDFWMIDENT").setValueAttribute(contributionAccount);
			jacadaform.getInputByName("txt_SDFWMRESU").setValueAttribute(regime);
			// Selecting document's printing method
			Iterable<DomElement> itOptions = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement option : itOptions) {
				if (option.getTextContent().equalsIgnoreCase("OnLine")) {
					htmlPage = option.click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			// Doing click, first on Continuar button and, then, on confirm button
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			InputStream is = htmlPage.getElementById("Sub2204801005_7").click().getWebResponse().getContentAsStream();
			byte[] ret = is.readAllBytes();
			is.close();
			return ret;
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}

		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	// RETURNS A COLLECTION OF OBJECTS WITH THE DATE AND THE DESCRIPTION OF ALL TA
	// CERTIFICATES
	public static Collection<Idc> getIDCDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount) throws SegSocialException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage = htmlPage.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR")
					.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValueAttribute(regime);
			} catch (ElementNotFoundException enfe) {
				jacadaform.getInputByName("txt_SDFREGCTA").setValueAttribute(regime);
			}
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = de.click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			boolean end = false;
			ArrayList<Idc> ret = new ArrayList<Idc>();
			while (!end) {
				DomNodeList<DomNode> dnl = htmlPage.querySelectorAll("#Sub0900112079>tbody>tr");

				for (DomNode e : dnl) {
					DomNodeList<DomNode> registros = e.querySelectorAll("td");
					if (registros.get(2).getTextContent().trim().equals("")) {
						return ret;
					} else {
						String[] arrD = registros.get(2).getTextContent().trim().split(" ");
						GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(arrD[2]),
								Integer.parseInt(arrD[1]) - 1, Integer.parseInt(arrD[0]));
						Date d = gc.getTime();
						String desc = Toolkit.removeNBSP(registros.get(1).querySelector("span>label").getTextContent());
						ret.add(new Idc(desc, d));
					}
				}
				htmlPage = htmlPage.getElementById("Sub2206501001").click();

				try {
					HtmlUnitToolkit.getSSCode(htmlPage);
				} catch (NoMoreDataException nmde) {
					end = true;
				}
			}
			return ret;

			// Obtaining the first table registry's label to double-click on it so that it
			// loads the pdf
			/*
			 * List<HtmlLabel> labels = htmlPage.getByXPath("//label[@name='_1_0']");
			 * InputStream
			 * is=labels.get(0).dblClick().getWebResponse().getContentAsStream(); byte[]
			 * ret=is.readAllBytes();
			 */
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}
		} catch (ElementNotFoundException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<Date> getDischargeDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount) throws SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage = htmlPage.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR")
					.click();
			HtmlUnitToolkit.getSSCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			jacadaform.getInputByName("txt_SDFREGCTA").setValueAttribute(regime);
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = de.click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			boolean end = false;
			ArrayList<Date> ret = new ArrayList<Date>();
			while (!end) {
				DomNodeList<DomNode> dnl = htmlPage.querySelectorAll("#Sub0900112078>tbody>tr");

				for (DomNode e : dnl) {
					DomNodeList<DomNode> registros = e.querySelectorAll("td");
					if (registros.get(1).getTextContent().trim().equals("")) {
						if (dnl.indexOf(e) != 0)
							return ret;
					} else {
						String[] arrD = registros.get(1).getTextContent().trim().split(" ");
						GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(arrD[2]),
								Integer.parseInt(arrD[1]) - 1, Integer.parseInt(arrD[0]));
						ret.add(gc.getTime());
					}
				}
				htmlPage = htmlPage.getElementById("Sub2206301003").click();
				//System.out.println(htmlPage.asText());
				try {
					HtmlUnitToolkit.getSSCode(htmlPage);
				} catch (NoMoreDataException nmde) {
					end = true;
				}
			}

			return ret;

		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException,
			InterruptedException, ParseException, SegSocialException {
		/*
		  try (final InputStream certificateInputStream = new FileInputStream(args[0]))
		  { System.out.println(getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs12", "0111", "01105360062"));
		  
		  }*/
		  /*try (final InputStream certificateInputStream = new FileInputStream(args[0])){
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			  byte[] pdf=getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  System.out.println(pdf.length+" Bytes downloaded");
		  Toolkit.buildPdf(pdf,"DuplicadoTA");
		  }*/
		 /* try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			  byte[] pdf=getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  System.out.println(pdf.length+" Bytes downloaded");
		  Toolkit.buildPdf(pdf,"InfoCotizacion");
		  }
		  try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			  byte[] pdf=getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT","pkcs12", "0111", "01105360062");
		  System.out.println(pdf.length+" Bytes downloaded");
		  Toolkit.buildPdf(pdf,"ObligationAwarenessCertificate");
		  }
		  try (final InputStream certificateInputStream = new FileInputStream(args[0])) { 
			  //Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			  Collection<Idc> r=getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924","0111", "01105360062"); Toolkit.log(r.toArray());
		  }
		 
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			// Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<Date> i = getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111",
					"01105360062");
			Toolkit.log(i.toArray());
		}*/
		  try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			  Collection<byte[]> col=getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
			  String nom="a";
			  for (byte[] bs : col) {
				Toolkit.buildPdf(bs, nom);
				System.out.println(nom+".pdf CREATED");
				nom+=1;
			}
		  }
		  try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			  Collection<byte[]> col=getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
			  String nom="b";
			  for (byte[] bs : col) {
				Toolkit.buildPdf(bs, nom);
				System.out.println(nom+".pdf CREATED");
				nom+=1;
			}
		  }
	}

}
