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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLLabelElement;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.DataDoesNotExist;
import solutions.aon.seg.social.exceptions.invalidData.LiquidationDoesNotExist;
import solutions.aon.seg.social.exceptions.invalidData.NoMoreDataException;
import solutions.aon.seg.social.exceptions.invalidData.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.invalidData.WrongRegimeException;
import solutions.aon.seg.social.exceptions.invalidData.invalidCccException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Idc;
import solutions.aon.seg.social.objects.Liquidation;
import solutions.aon.seg.social.objects.Liquidation.LiquidationBuilder;
import solutions.aon.seg.social.objects.SituacionEmpresa;
import solutions.aon.seg.social.objects.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.objects.WorkerLiquidation.WorkerLiquidationBuilder;
import solutions.aon.seg.social.objects.WorkerLiquidation;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRED_I {

	public static SituacionEmpresa getSituacionEmpresa(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws MalformedURLException, IOException, InterruptedException, SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
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
			StatusCodeException.HandleStatusCodeException(fhsce);
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;

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

	public static byte[] getContributionInformationCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime,
			String contributionAccount, Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, null, regime,
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
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage = htmlPage.getAnchorByHref(href).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			
			try {
				jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
				jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			} catch (ElementNotFoundException enfe) {
			}
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
			try {
				jacadaform.getInputByName("txt_SDFDIA").setValueAttribute("" + calendar.get(Calendar.DAY_OF_MONTH));
			} catch (ElementNotFoundException enfe) {
			}
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
			Page page = jacadaform.getInputByValue("Continuar").click();
			if ( !page.isHtmlPage() ) {
				InputStream is = page.getWebResponse().getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;
			}
			
			htmlPage = (HtmlPage) page;
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
			StatusCodeException.HandleStatusCodeException(e);

		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}
	
	public static Collection<byte[]> getTACertificatePDFs (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException{
		Object[] arr_fields= {affiliationNumber, regime, contributionAccount, fecha};
		Toolkit.verifyData(arr_fields);
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, contributionAccount, fecha);
	}
	
	public static Collection<byte[]> getContributionPDFs (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException{
		Object[] arr_fields= {affiliationNumber, regime, contributionAccount, fecha};
		Toolkit.verifyData(arr_fields);
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, contributionAccount, fecha);
	}
	
	public static Collection<byte[]> getPdfsInfo(final String href, final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha)
			throws SegSocialException, InterruptedException {
		Object[] arr_fields= {affiliationNumber, regime, contributionAccount, fecha};
		Toolkit.verifyData(arr_fields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			ArrayList<byte[]> ret=new ArrayList<byte[]>();
			boolean found=false;
			int indTab=2;
 			
 			String dia="";
 			String mes="";
 			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(fecha);
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
			
			
 			boolean fin=false;
 			int clicks=0;
 			HtmlPage htmlPage=getPageForPdfs(href, webClient, affiliationNumber, regime, contributionAccount, fecha, clicks);
 			while(!fin) {
 				DomNodeList<DomNode> iter=htmlPage.querySelectorAll("#Sub0900112079>tbody>tr");
 	 			if(iter.size()==0) {
 	 				iter=htmlPage.querySelectorAll("#Sub0900112078>tbody>tr");
 	 				indTab=1;
 	 			}
 	 			
				for (int i=0;i<iter.size();i++) {
					DomNodeList<DomNode> dn2=iter.get(i).querySelectorAll("td");
					
					if(dn2.get(indTab).getVisibleText().equalsIgnoreCase(strDate)){
						found=true;
						HtmlLabel htmlLabel=dn2.get(indTab).querySelector("label");
						InputStream is=htmlLabel.dblClick().getWebResponse().getContentAsStream();
						ret.add(is.readAllBytes());
						is.close();
					}
					else if(found==true) {
						fin=true;
						break;
					}
					
				}
				clicks++;
				if(!fin) {
					try {
						htmlPage=getPageForPdfs(href, webClient, affiliationNumber, regime, contributionAccount, fecha, clicks);
						HtmlUnitToolkit.manageStatusCode(htmlPage);
					}
					catch(NoMoreDataException nmde) {
						fin=true;
					}
				}
 			}
			return ret;
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);

		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static HtmlPage getPageForPdfs(final String href, final WebClient webClient, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date fecha, final int clicks)
			throws SegSocialException, InterruptedException {

		try{

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
			for(int i=0;i<clicks;i++) {
				List<HtmlInput> htmlInputList=htmlPage.getByXPath("//input[@value='Pág. Sig.']");
					htmlPage=htmlInputList.get(0).click();
					HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
			return htmlPage;
			
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);

		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static byte[] getObligationAwarenessCertificate(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String contributionAccount)
			throws SegSocialException {
		Object[] arr_fields= {regime, contributionAccount};
		Toolkit.verifyData(arr_fields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
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
			StatusCodeException.HandleStatusCodeException(e);

		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}

	// RETURNS A COLLECTION OF OBJECTS WITH THE DATE AND THE DESCRIPTION OF ALL TA
	// CERTIFICATES
	public static Collection<Idc> getIDCDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount) throws SegSocialException, InterruptedException {
		Object[] arr_fields= {affiliationNumber, regime, contributionAccount};
		Toolkit.verifyData(arr_fields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
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
			StatusCodeException.HandleStatusCodeException(e);
		} catch (ElementNotFoundException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}

	public static Collection<Date> getDischargeDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
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
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}
	
	public static byte[] getContributionSettlementReport(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Optional<Date> settlementPeriod) throws SegSocialException {
		Object[] arr_fields= {affiliationNumber, regime, contributionAccount, settlementPeriod};
		Toolkit.verifyData(arr_fields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			htmlPage=htmlPage.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR").click();
			HtmlForm jacadaform=htmlPage.getFormByName("jacadaform");
			//NSS
			jacadaform.getInputByName("txt_SDFTESNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			//REGIME
			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValueAttribute(regime);
			//CCC
			jacadaform.getInputByName("txt_SDFTESCTA").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			//Settlement period
			if(!settlementPeriod.isEmpty()) {
				Calendar c=Calendar.getInstance();
				c.setTime(settlementPeriod.get());
				String month=""+c.get(Calendar.MONTH)+1;
				String year=""+c.get(Calendar.YEAR);
				jacadaform.getInputByName("txt_SDFMES").setValueAttribute(month);
				jacadaform.getInputByName("txt_SDFAO").setValueAttribute(year);
			}
			//PRINTING METHOD
			HtmlSelect printSelect=jacadaform.getSelectByName("cbo_ListaTipoImpresion");
			printSelect.getOptionByText("OnLine").setSelected(true);
			//GETTING THE PDF
			if(jacadaform.getInputByValue("Continuar").click() instanceof com.gargoylesoftware.htmlunit.UnexpectedPage) {
				InputStream is=jacadaform.getInputByValue("Continuar").click().getWebResponse().getContentAsStream();
				byte[] ret=is.readAllBytes();
				is.close();
				return ret;
			}
			else {
				htmlPage=jacadaform.getInputByValue("Continuar").click();
				try {
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					return null;
				}catch (DataDoesNotExist ddne){
					throw ddne;
				}
			}
			
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
		
	}
	
	public static enum LiquidationType{
		L00_NORMAL("L00"),
		C02_COMP_SALARIOS_TRAMITACION_NO_CONCERTADOS("C02"),
		C03_COMP_SALARIOS_RETROACTIVOS_NO_CONCERTADO("C03"),
		C13_COMP_VACAC_RETRIBUIDAS_NO_CONCERTADOS("C13"),
		C90_COMP_POR_INCREMENTO_BASES_NO_CONCERTADOS("C90"),
		C91_COMP_NUEVOS_TRAB_Y_O_TRAMOS_NO_CONCERTA("C91"),
		L02_COMPLEMENTARIA_POR_SALARIOS_TRAM_NORMAL("L02"),
		L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV("L03"),
		L13_VACACIONES_RETRIBUIDAS("L13"),
		L90_COMPLEMENTARIA_POR_INCREMENTO_DE_BASES("L90"),
		L91_COMP_NUEVOS_TRABAJADORES_Y_O_TRAMOS("L91"),
		L92_COMP_SALARIOS_TRAMITACIÓN_DE_OFICIO("L92"),
		L93_COMP_VAC_RETR_Y_NO_DISFR_DE_OFICIO("L93"),
		V03_COMP_ABONO_SALARIOS_RETROACTIVOS_DE_L13("V03"),
		V90_COMP_POR_INCREMENTO_DE_BASES_DE_L13("V90"),
		TODAS("T");
		
		private String value;
		
		
		private LiquidationType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	//Origen de la liquidación
	public static enum LiquidationOrigin{
		PRESENTADAS_POR_LA_EMPRESA("E"),
		GENERADAS_POR_LA_TGSS("G"),
		TODAS("T");
		private String value;
		private LiquidationOrigin(String value) {
			this.value=value;
		}
		public String getValue() {
			return value;
		}
	}
	
	public static enum Regime{
		GENERAL("0111"),
		GENERAL_ARTISTAS("0112"),
		GENERAL_CONSERVAS_VEGETALES("0132"),
		GENERAL_HOSTELERIA("0135"),
		GENERAL_CINEMATOG("0136"),
		GENERAL_OPINION_PUBLICA("0137"),
		GENERAL_AGRARIO("0163"),
		ESPECIAL_MAR_GRUPO_1("0811"),
		ESPECIAL_MAR_GRUPO_2A("0812"),
		ESPECIAL_MAR_GRUPO_2B("0813"),
		ESPECIAL_MAR_GRUPO_3("0814"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_1("0821"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_2A("0822"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_2B("0823");
		private String value;
		private Regime(String value) {
			this.value=value;
		}
		public String getValue() {
			return value;
		}
		
	}
	
	
	
	public static Collection<Liquidation> CalculationQueryByCCC(final InputStream certificateInputStream,
		final String certificatePassword, final String certificateType, final String ccc,
		final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
		final LiquidationOrigin liqOrigin) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arr_fields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
		Toolkit.verifyData(arr_fields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			HtmlForm formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
			formDatos.getInputByName("CCC").setValueAttribute(ccc);
			HtmlSelect selectRegime=formDatos.getSelectByName("REGIMEN");
			selectRegime.setSelectedIndex(selectRegime.getOptionByValue(regime.getValue()).getIndex());
			Calendar cFrom=Calendar.getInstance();
			cFrom.setTime(dateFrom);
			Calendar cTo=Calendar.getInstance();
			cTo.setTime(dateTo);
			String sFrom;
			String sTo;
			if(cFrom.get(Calendar.MONTH)+1<10) {
				sFrom="0"+(cFrom.get(Calendar.MONTH)+1);
			}
			else
				sFrom=""+(cFrom.get(Calendar.MONTH)+1);
			if(cTo.get(Calendar.MONTH)+1<10) {
				sTo="0"+(cTo.get(Calendar.MONTH)+1);
			}
			else
				sTo=""+(cTo.get(Calendar.MONTH)+1);
			HtmlSelect selectMFrom=formDatos.getSelectByName("MES_DESDE");
			selectMFrom.setSelectedIndex(selectMFrom.getOptionByValue(sFrom).getIndex());
			HtmlSelect selectYFrom=formDatos.getSelectByName("ANNIO_DESDE");
			selectYFrom.setSelectedIndex(selectYFrom.getOptionByValue(""+cFrom.get(Calendar.YEAR)).getIndex());
			HtmlSelect selectMTo=formDatos.getSelectByName("MES_HASTA");
			selectMTo.setSelectedIndex(selectMTo.getOptionByValue(sTo).getIndex());
			HtmlSelect selectYTo=formDatos.getSelectByName("ANNIO_HASTA");
			selectYTo.setSelectedIndex(selectYTo.getOptionByValue(""+cTo.get(Calendar.YEAR)).getIndex());
			HtmlSelect selectLiqType=formDatos.getSelectByName("TIPO_LIQUIDACION");
			selectLiqType.setSelectedIndex(selectLiqType.getOptionByValue(liqType.getValue()).getIndex());
			formDatos.getInputByValue(liqOrigin.getValue()).setChecked(true);
			htmlPage=formDatos.getInputByValue("Aceptar").click();
			try {
				checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				Collection<Liquidation> ret= new ArrayList<Liquidation>();
				formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
				DomNodeList<DomNode> liqList=formDatos.querySelectorAll("input[type='radio']");
				for (int i=0;i<liqList.size();i++) {
					HtmlRadioButtonInput radio=(HtmlRadioButtonInput)liqList.get(i);
					radio.click();
					formDatos.getInputByValue("Continuar").setChecked(true);
					htmlPage=formDatos.getInputByValue("Continuar").click();
					LiquidationBuilder lb=new LiquidationBuilder();
					for (DomNode domNode : htmlPage.querySelectorAll("table>tbody>tr")) {
						HtmlTableRow tr=(HtmlTableRow)domNode;
						liquidationDataType(tr, lb);
					}
					ret.add(lb.build());
					htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
					formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
					liqList=formDatos.querySelectorAll("input[type='radio']");
				}
				
				return ret;
			}
			
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;	
	}
	
	//RETURNS A COLLECTION OF HASHMAPS CONTAINING EACH WORKER'S CALCULATION QUERY (WORKERS' NSS AS KEY)
	public static Collection<Map<String, WorkerLiquidation>> WorkersCalculationQueryByCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
			final LiquidationOrigin liqOrigin) throws SegSocialException{
			InvalidCertificateException.checkCertificate(certificateInputStream);
			Object[] arr_fields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
			Toolkit.verifyData(arr_fields);
			try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
				webClient.getOptions().setJavaScriptEnabled(false);
				HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
				HtmlForm formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
				formDatos.getInputByName("CCC").setValueAttribute(ccc);
				HtmlSelect selectRegime=formDatos.getSelectByName("REGIMEN");
				selectRegime.setSelectedIndex(selectRegime.getOptionByValue(regime.getValue()).getIndex());
				Calendar cFrom=Calendar.getInstance();
				cFrom.setTime(dateFrom);
				Calendar cTo=Calendar.getInstance();
				cTo.setTime(dateTo);
				String sFrom;
				String sTo;
				if(cFrom.get(Calendar.MONTH)+1<10) {
					sFrom="0"+(cFrom.get(Calendar.MONTH)+1);
				}
				else
					sFrom=""+(cFrom.get(Calendar.MONTH)+1);
				if(cTo.get(Calendar.MONTH)+1<10) {
					sTo="0"+(cTo.get(Calendar.MONTH)+1);
				}
				else
					sTo=""+(cTo.get(Calendar.MONTH)+1);
				HtmlSelect selectMFrom=formDatos.getSelectByName("MES_DESDE");
				selectMFrom.setSelectedIndex(selectMFrom.getOptionByValue(sFrom).getIndex());
				HtmlSelect selectYFrom=formDatos.getSelectByName("ANNIO_DESDE");
				selectYFrom.setSelectedIndex(selectYFrom.getOptionByValue(""+cFrom.get(Calendar.YEAR)).getIndex());
				HtmlSelect selectMTo=formDatos.getSelectByName("MES_HASTA");
				selectMTo.setSelectedIndex(selectMTo.getOptionByValue(sTo).getIndex());
				HtmlSelect selectYTo=formDatos.getSelectByName("ANNIO_HASTA");
				selectYTo.setSelectedIndex(selectYTo.getOptionByValue(""+cTo.get(Calendar.YEAR)).getIndex());
				HtmlSelect selectLiqType=formDatos.getSelectByName("TIPO_LIQUIDACION");
				selectLiqType.setSelectedIndex(selectLiqType.getOptionByValue(liqType.getValue()).getIndex());
				formDatos.getInputByValue(liqOrigin.getValue()).setChecked(true);
				htmlPage=formDatos.getInputByValue("Aceptar").click();
				try {
					checkLiquidationExceptions(htmlPage);
				}catch(NullPointerException | ElementNotFoundException e) {
					Collection<Map<String, WorkerLiquidation>> ret= new ArrayList<Map<String, WorkerLiquidation>>();
					formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
					DomNodeList<DomNode> liqList=formDatos.querySelectorAll("input[type='radio']");
					for (int h=0;h<liqList.size();h++) {
						
						HtmlRadioButtonInput radio=(HtmlRadioButtonInput)liqList.get(h);
						radio.click();
						htmlPage=formDatos.getInputByValue("Continuar").click();
						htmlPage=htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES").click();
						formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
						List<HtmlRadioButtonInput> listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
						HashMap<String, WorkerLiquidation> map=new HashMap<String, WorkerLiquidation>();
						for (int i=0;i<listRadiosWorkers.size();i++) {
							
							htmlPage=listRadiosWorkers.get(i).click();
							htmlPage=formDatos.getInputByValue("Consultar").click();
							DomNode cafNode=htmlPage.querySelector("abbr[title='Código alfabético (abreviado a partir de nombre y apellidos) del trabajador']").getParentNode();
							String caf=Toolkit.removeWeirdCharacters(cafNode.getVisibleText());
							caf=caf.substring(caf.indexOf(":")+1).trim();
							
							DomNode keyNode=htmlPage.querySelector("abbr[title='Número de afiliación a la Seguridad Social']").getParentNode();
							String key=Toolkit.removeWeirdCharacters(keyNode.getVisibleText());
							key=key.substring(key.indexOf(":")+1).trim();
							WorkerLiquidationBuilder wlb=new WorkerLiquidationBuilder();
							wlb.setCaf(caf);
							wlb.setNss(key);
							DomNodeList<DomNode> rows=htmlPage.querySelectorAll("tbody tr");
							for (DomNode row : rows) {
								workerLiquidationDataType((HtmlTableRow)row, wlb);
							}
							map.put(key, wlb.build());
							htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
							formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
							listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
						}
						ret.add(map);
						htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
						formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
						liqList=formDatos.querySelectorAll("input[type='radio']");
					}	
					return ret;
				}
				
			} catch (FailingHttpStatusCodeException e) {
				StatusCodeException.HandleStatusCodeException(e);
			} catch (MalformedURLException e) {
				throw new SegSocialException(e);
			} catch (IOException e) {
				throw new CertificateNotFoundException();
			}
			return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private static void checkLiquidationExceptions(HtmlPage htmlPage) throws LiquidationDoesNotExist, DataDoesNotExist,
			WrongRegimeException, invalidCccException, UnfilledMandatory, NullPointerException, ElementNotFoundException {
		HtmlDivision divError=(HtmlDivision) htmlPage.getElementById("ARQContenMensaje");
		if(divError.getVisibleText().toUpperCase().contains("NO EXISTE LIQUIDACIÓN"))
			throw new LiquidationDoesNotExist();
		else if(divError.getVisibleText().toUpperCase().contains("NO EXISTEN DATOS"))
			throw new DataDoesNotExist();
		else if(divError.getVisibleText().toUpperCase().contains("CUENTA DE COTIZACIÓN NO EXISTE"))
			throw new WrongRegimeException();
		else if(divError.getVisibleText().toUpperCase().contains("C.C.C. ERRÓNEO"))
			throw new invalidCccException();
		else if(divError.getVisibleText().toUpperCase().contains("DEBE TENER CONTENIDO"))
			throw new UnfilledMandatory();
	}
	private static Float cellToFloat(HtmlTableCell tc) throws SegSocialException {
		String nmbr=Toolkit.removeWeirdCharacters(tc.getVisibleText()).replaceAll("[.]","").replace(',', '.');
		if(nmbr.equals("")) {
			return null;
		}
		if(nmbr.contains(" ")){
			nmbr=nmbr.substring(0, nmbr.indexOf(' '));
		}
		try {
			return Float.parseFloat(nmbr);
		} catch (NumberFormatException e){
			throw new SegSocialException(e);
		}
		
	}
	
	
	private static void liquidationDataType(HtmlTableRow tr, LiquidationBuilder lb) throws SegSocialException {
		String innerText=Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
		if(innerText.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCc_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCc_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCc_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCc_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCcLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setItWorkAccident_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setItWorkAccident_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setItWorkAccident_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setItWorkAccident_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setImsWorkAccident_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setImsWorkAccident_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setImsWorkAccident_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setImsWorkAccident_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setWorkAccidentLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setWorkAccidentLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setWorkAccidentLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setWorkAccidentLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("OTRAS COTIZACIONES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributions_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributions_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributions_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributions_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributionsLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributionsLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributionsLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributionsLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setTotalLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setTotalLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setTotalLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setTotalLiquid_totalFee(nmbr4);
		}
		
	}
	
	
	private static void workerLiquidationDataType(HtmlTableRow tr, WorkerLiquidationBuilder lb) throws SegSocialException {
		String innerText=Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
		if(innerText.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCc_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCc_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCc_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCc_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCcLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setItWorkAccident_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setItWorkAccident_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setItWorkAccident_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setItWorkAccident_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setImsWorkAccident_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setImsWorkAccident_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setImsWorkAccident_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setImsWorkAccident_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setWorkAccidentLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setWorkAccidentLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setWorkAccidentLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setWorkAccidentLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("DESEMPLEO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setUnemployment_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setUnemployment_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setUnemployment_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setUnemployment_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("FOGASA")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setFogasa_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setFogasa_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setFogasa_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setFogasa_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("FORMACIÓN PROFESIONAL")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setJobTraining_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setJobTraining_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setJobTraining_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setJobTraining_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributionsLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributionsLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributionsLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributionsLiquid_totalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setTotalLiquid_base(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setTotalLiquid_businessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setTotalLiquid_workerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setTotalLiquid_totalFee(nmbr4);
		}
		
	}


}