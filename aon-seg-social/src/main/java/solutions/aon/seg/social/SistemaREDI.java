package solutions.aon.seg.social;

import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.clickAndCheckCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.doubleClickAndCheckCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlParagraph;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.OutOfServiceMotivation;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.exception.invalid.LiquidationDoesNotExist;
import solutions.aon.seg.social.exception.invalid.NoMoreDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.Liquidation;
import solutions.aon.seg.social.object.Liquidation.LiquidationBuilder;
import solutions.aon.seg.social.object.SituacionEmpresa;
import solutions.aon.seg.social.object.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.object.WorkerLiquidation;
import solutions.aon.seg.social.object.WorkerLiquidation.WorkerLiquidationBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDI {

	public static SituacionEmpresa getSituacionEmpresa(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws MalformedURLException, IOException, InterruptedException, SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementsById("SDFREGCTA_ayuda"));
			HtmlForm jacadaform = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			// Separando el ccc
			String ccc1 = ccc.substring(0, 2);
			String ccc2 = ccc.substring(2);

			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValue(regime);
			jacadaform.getInputByName("txt_SDFTESCTA").setValue(ccc1);
			jacadaform.getInputByName("txt_SDFNUMCTA").setValue(ccc2);
			// click en 'Continuar'
			
			htmlPage = clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
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
					.setIdEmpresario(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPO3")).setNifempresa(nif)
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
					.setcEspNum(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOLECTIVO3"))
					.setcEspCad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDESCOL3"))
					.setCnae09Num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV093"))
					.setCnae93Num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV933"))
					.setCnae09Cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV093"))
					.setCnae93Cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV933")).setTa2Alta(trabajador2Alta)
					.setTa2Baja(ta2baja).setTiposATyEPIT(tiposATyEpit).setIms(ims).setTotal(total)
					.setCoeJubNum(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOREJU"))
					.setCoeJubCad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSCOREJU"))
					.setAconExtra(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTMEXTR")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSACONT"))
					.setEscTaller(booltaller)
					.setAutorizacionRed(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFAUTORIDRED"))
					.setPlazoIncorpRed(Toolkit.parseDate(plazoRed, "dd/MM/yyyy"))
					.setFechaAutCan(Toolkit.parseDate(fechaAutRed, "dd/MM/yyyy"));

			// Datos identificativos
			jacadaform = htmlPage.getFormByName("jacadaform");

			htmlPage = clickAndCheckCode(jacadaform.getInputByValue("Datos Iden."));
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("SDFLOCALIDAD4"));

			seb1.setAnagrama(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFANAGR3"))
					.setEmbarcacion(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPEMB")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFEMB")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOMEMBAR"))
					.setTlfMovil(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELMOVIL3"))
					.setTlfFijo(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELFIJO3"))
					.setEmail(HtmlUnitToolkit.getTrimmedById(htmlPage, "txtconcat1_1"))
					.setNotifDomEmpresa(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF3")))
					.setTipoViaDirEmpresa(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA3"))
					.setDirEmpCalle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO3"))
					.setDirEmpNum(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
					.setDirEmpBis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS3"))
					.setDirEmpBloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE3"))
					.setDirEmpEs(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA3"))
					.setDirEmpPiso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO3"))
					.setDirEmpP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA3"))
					.setDirEmpCP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL3"))
					.setDirEmpNumMuni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD13"))
					.setDirEmpNomMuni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD23"))
					.setDirEmpTlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO3"))
					.setNotifDomActividad(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF4")))
					.setActUgtgss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESORERIA3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMON3")
							+ HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFURE3"))
					.setTipoViaDirActividad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA4"))
					.setDirActCalle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO4"))
					.setDirActNum(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
					.setDirActBis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS4"))
					.setDirActBloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE4"))
					.setDirActEs(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA4"))
					.setDirActPiso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO4"))
					.setDirActP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA4"))
					.setDirActCP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL4"))
					.setDirActNumMuni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFMUNICIPIO4"))
					.setDirActNomMuni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD4"))
					.setDirActTlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO4"));

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
			String ccc, Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime,
					ccc, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getContributionInformationCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime,
			String ccc, Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, null, regime,
					ccc, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getContributionInformationNSS(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime,
			String ccc, String nss, Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, nss, regime,
					ccc, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getTADuplicate(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String affiliationNumber, String regime, String ccc,
			Date fecha) throws SegSocialException {
		try {
			return getPdfInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR",
					certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime,
					ccc, fecha);
		} catch (InterruptedException ie) {
			throw new SegSocialException();
		}
	}

	public static byte[] getPdfInfo(final String href, final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date fecha)
			throws SegSocialException, InterruptedException {
		
		Date today = new Date(); 
		Date date = fecha.after(today) ? today : fecha;

		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/" + href);
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			
			try {
				jacadaform.getInputByName("txt_SDFTESNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
				jacadaform.getInputByName("txt_SDFNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			} catch (ElementNotFoundException enfe) {
			}
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValue(regime);
			} catch (ElementNotFoundException enfe) {
				try {
					jacadaform.getInputByName("txt_SDFREGCTA").setValue(regime);
				} catch (ElementNotFoundException oenfe) { 
					jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValue(regime);
				}
			} 
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValue(Toolkit.SplitString(ccc, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValue(Toolkit.SplitString(ccc, 2)[1]);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			try {
				jacadaform.getInputByName("txt_SDFDIA").setValue("" + calendar.get(Calendar.DAY_OF_MONTH));
			} catch (ElementNotFoundException enfe) {
			}
			jacadaform.getInputByName("txt_SDFMES").setValue("" + (calendar.get(Calendar.MONTH) + 1));
			jacadaform.getInputByName("txt_SDFAO").setValue("" + calendar.get(Calendar.YEAR));

			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = clickAndCheckCode(de);
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			Page page = clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
			
			if ( !page.isHtmlPage() ) {
				WebResponse response = HtmlUnitToolkit.wait4(page, p -> p.getWebResponse()).orElseGet(null);
				InputStream is = response.getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;
			}
			
			htmlPage = (HtmlPage) page;
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			// Obtaining the first table registry's label to double-click on it so that it
			// loads the pdf
			List<HtmlLabel> labels = htmlPage.getByXPath("//label[@name='_1_0']");
			page = doubleClickAndCheckCode(labels.get(0));

			if ( page.isHtmlPage() ) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				
				// --------------------Enterprise with loss of benefits-------
				DomElement next = htmlPage.querySelector("[value=\"Continuar\"]"); 
				if(next!=null) {
					page = clickAndCheckCode(next);
				}
			}  
			
			if ( !page.isHtmlPage() ){
				WebResponse response = HtmlUnitToolkit.wait4(page, p -> p.getWebResponse()).orElseGet(null);
				InputStream is = response.getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;
			}
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
			final String regime, final String ccc, final Date fecha)
			throws SegSocialException, InterruptedException{
		Object[] arrFields= {affiliationNumber, regime, ccc, fecha};
		Toolkit.verifyData(arrFields);
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, ccc, fecha);
	}
	
	public static Collection<byte[]> getContributionPDFs (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date fecha)
			throws SegSocialException, InterruptedException{
		Object[] arrfields= {affiliationNumber, regime, ccc, fecha};
		Toolkit.verifyData(arrfields);
		return getPdfsInfo("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR", certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime, ccc, fecha);
	}
	
	public static Collection<byte[]> getPdfsInfo(final String href, final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date fecha)
			throws SegSocialException, InterruptedException {
		Object[] arrfields= {affiliationNumber, regime, ccc, fecha};
		Toolkit.verifyData(arrfields);
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
 			HtmlPage htmlPage=getPageForPdfs(href, webClient, affiliationNumber, regime, ccc, fecha, clicks);
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
						InputStream is=doubleClickAndCheckCode(htmlLabel).getWebResponse().getContentAsStream();
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
						htmlPage=getPageForPdfs(href, webClient, affiliationNumber, regime, ccc, fecha, clicks);
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
			final String regime, final String ccc, final Date fecha, final int clicks)
			throws SegSocialException, InterruptedException {

		try{

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/" + href);
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			jacadaform.getInputByName("txt_SDFTESNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValue(regime);
			} catch (ElementNotFoundException enfe) {
				jacadaform.getInputByName("txt_SDFREGCTA").setValue(regime);
			}
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValue(Toolkit.SplitString(ccc, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValue(Toolkit.SplitString(ccc, 2)[1]);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(fecha);
			jacadaform.getInputByName("txt_SDFDIA").setValue("" + calendar.get(Calendar.DAY_OF_MONTH));
			jacadaform.getInputByName("txt_SDFMES").setValue("" + (calendar.get(Calendar.MONTH) + 1));
			jacadaform.getInputByName("txt_SDFAO").setValue("" + calendar.get(Calendar.YEAR));
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
//			ArrayList<byte[]> ret=new ArrayList<byte[]>();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = clickAndCheckCode(de);
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			for(int i=0;i<clicks;i++) {
				List<HtmlInput> htmlInputList=htmlPage.getByXPath("//input[@value='Pág. Sig.']");
					htmlPage=clickAndCheckCode(htmlInputList.get(0));
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
			final String certificatePassword, final String certificateType, String regime, String ccc, String authCode)
			throws SegSocialException {
		Object[] arrFields= {regime, ccc};
		Toolkit.verifyData(arrFields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuDEUDA-CI.html");
			
			HtmlAnchor certSSRequest = htmlPage.getAnchorByHref("/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21F001");
			XmlPage xXmlPage = certSSRequest.click();
			htmlPage = HtmlUnitToolkit.tranformXmlPage(xXmlPage);
				
			handleSepeExceptions(htmlPage);
			
			// Mirar si necesita autorizacion (Por ejemplo certificados como los de AyudaT)
			DomElement selectAuth = htmlPage.getElementById("TITULO_SECCION_forSelAutori");
			if(selectAuth != null) {
				// Seleccione un Número de Autorización
				String auth = removeLeftZeros(authCode);
				if(auth.length() == 5) {
					auth = '0' + auth;
				} 
				HtmlAnchor authAnchor = (HtmlAnchor) htmlPage.getElementById("enlace_" + auth);
				if(null == authAnchor)
					throw new IllegalArgumentException("No existe el numero de autorizaci\u00f3n: " + auth + ". Reviselo en Configuraci\u00f3n > Parametros > Laborales");	
				
				XmlPage authXmlPage = htmlPage.getElementById("enlace_" + auth).click();
				htmlPage = HtmlUnitToolkit.transformXmlPage(authXmlPage);
			}
			
			htmlPage.getElementById("radio_Opcion3").click();
			HtmlInput criBusCccNaf = (HtmlInput) htmlPage.getElementById("criBusCccNaf") ;
			criBusCccNaf.setValue(regime+ccc);
			XmlPage xmlPage  = htmlPage.getElementById("botBuscar").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);

			xmlPage = htmlPage.getElementById("enlace_" + regime.substring(1, regime.length()) + ccc).click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			xmlPage = htmlPage.getElementById("ENVIO_13").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			xmlPage = htmlPage.getElementById("ENVIO_15").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);

			for (HtmlAnchor anchor : htmlPage.getAnchors()) {
			    if ( "documento".equals(anchor.getAttribute("data-pc_tipo"))) {
				Page pdfPage = anchor.click();
				return pdfPage.getWebResponse().getContentAsStream().readAllBytes();
			    }
			} 
				    
		} catch (FailingHttpStatusCodeException e1) {
		    e1.printStackTrace();
		} catch (MalformedURLException e1) {
		    e1.printStackTrace();
		} catch (IOException e1) {
		    e1.printStackTrace();
		} catch (TransformerException e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	private static String removeLeftZeros(String input) {
		if (input == null || input.isEmpty()) {
            return input;
        }

        // Use regular expression to remove leading zeros
        String result = input.replaceFirst("^0+", "");

        return result;
	}

	private static void handleSepeExceptions(HtmlPage htmlPage) {
		HtmlParagraph error = htmlPage.querySelector("#CONTENEDOR_SECCION_1 > div > div > p");
		if(null != error) throw new IllegalArgumentException(error.getTextContent());		
	}

	public static Collection<Idc> getIDCDates(byte[] certificateData,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateData, certificatePassword);
		return getIDCDates(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, affiliationNumber, regime, ccc);
	}

	// RETURNS A COLLECTION OF OBJECTS WITH THE DATE AND THE DESCRIPTION OF ALL TA
	// CERTIFICATES
	public static Collection<Idc> getIDCDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc) throws SegSocialException {
		Object[] arrFields= {affiliationNumber, regime, ccc};
		Toolkit.verifyData(arrFields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
			Toolkit.checkCertificateRevoked(htmlPage.asXml());
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			// Filling the fields
			jacadaform.getInputByName("txt_SDFTESNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			try {
				jacadaform.getInputByName("txt_SDFREGCTA_NH").setValue(regime);
			} catch (ElementNotFoundException enfe) {
				jacadaform.getInputByName("txt_SDFREGCTA").setValue(regime);
			}
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValue(Toolkit.SplitString(ccc, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValue(Toolkit.SplitString(ccc, 2)[1]);
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = clickAndCheckCode(de);
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			boolean end = false;
			ArrayList<Idc> ret = new ArrayList<Idc>();
			while (!end) {
				DomNodeList<DomNode> dnl = htmlPage.querySelectorAll("#Sub0900112078>tbody>tr");

				for (DomNode e : dnl) {
					DomNodeList<DomNode> registros = e.querySelectorAll("td");
					if (registros.get(1).getTextContent().trim().equals("")) {
						return ret;
					} else {
						String[] arrD = registros.get(1).getTextContent().trim().split(" ");
						GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(arrD[2]),
								Integer.parseInt(arrD[1]) - 1, Integer.parseInt(arrD[0]));
						Date d = gc.getTime();
						ret.add(new Idc("ALTA", d));
						
						if (registros.get(2).getTextContent().trim().equals(""))
							continue;
						arrD = registros.get(2).getTextContent().trim().split(" ");
						gc = new GregorianCalendar(Integer.parseInt(arrD[2]),
								Integer.parseInt(arrD[1]) - 1, Integer.parseInt(arrD[0]));
						d = gc.getTime();
						ret.add(new Idc("BAJA", d));
					}
				}
				
				DomElement pagSig = htmlPage.getElementById("Sub2206301003");
				if ( pagSig != null ) {
					htmlPage = clickAndCheckCode(pagSig);
				}

				try {
					HtmlUnitToolkit.getSSCode(htmlPage);
				} catch (NoMoreDataException nmde) {
					end = true;
				}
			}
			return ret;

			// Obtaining the first table registry's label to double-click on it so that it
			// loads the pdf
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (ElementNotFoundException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return Collections.emptyList();
	}

	public static Collection<Date> getDischargeDates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);) {
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlForm jacadaform = htmlPage.getFormByName("jacadaform");
			jacadaform.getInputByName("txt_SDFTESNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			jacadaform.getInputByName("txt_SDFREGCTA").setValue(regime);
			jacadaform.getInputByName("txt_SDFTESCTA")
					.setValue(Toolkit.SplitString(ccc, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA")
					.setValue(Toolkit.SplitString(ccc, 2)[1]);
			// Selecting document's printing method
			Iterable<DomElement> it = jacadaform.getSelectByName("cbo_ListaTipoImpresion").getChildElements();
			for (DomElement de : it) {
				if (de.getTextContent().trim().equalsIgnoreCase("OnLine")) {
					htmlPage = clickAndCheckCode(de);
					HtmlUnitToolkit.manageStatusCode(htmlPage);
					break;
				}
			}
			htmlPage = clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
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
				htmlPage = clickAndCheckCode(htmlPage.getElementById("Sub2206301003"));
				//System.out.println(htmlPage.asNormalizedText());
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
			final String regime, final String ccc, final Optional<Date> settlementPeriod) throws SegSocialException {
		Object[] arrFields= {affiliationNumber, regime, ccc, settlementPeriod};
		Toolkit.verifyData(arrFields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			HtmlForm jacadaform=htmlPage.getFormByName("jacadaform");
			//NSS
			jacadaform.getInputByName("txt_SDFTESNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
			jacadaform.getInputByName("txt_SDFNAF").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			//REGIME
			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValue(regime);
			//CCC
			jacadaform.getInputByName("txt_SDFTESCTA").setValue(Toolkit.SplitString(ccc, 2)[0]);
			jacadaform.getInputByName("txt_SDFCUENTA").setValue(Toolkit.SplitString(ccc, 2)[1]);
			//Settlement period
			if(!settlementPeriod.isEmpty()) {
				Calendar c=Calendar.getInstance();
				c.setTime(settlementPeriod.get());
				String month=""+c.get(Calendar.MONTH)+1;
				String year=""+c.get(Calendar.YEAR);
				jacadaform.getInputByName("txt_SDFMES").setValue(month);
				jacadaform.getInputByName("txt_SDFAO").setValue(year);
			}
			//PRINTING METHOD
			HtmlSelect printSelect=jacadaform.getSelectByName("cbo_ListaTipoImpresion");
			printSelect.getOptionByText("OnLine").setSelected(true);
			//GETTING THE PDF
			if(clickAndCheckCode(jacadaform.getInputByValue("Continuar")) instanceof org.htmlunit.UnexpectedPage) {
				InputStream is=clickAndCheckCode(jacadaform.getInputByValue("Continuar")).getWebResponse().getContentAsStream();
				byte[] ret=is.readAllBytes();
				is.close();
				return ret;
			}
			else {
				htmlPage=clickAndCheckCode(jacadaform.getInputByValue("Continuar"));
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
	

	static HtmlPage liquidationPageFill(HtmlPage htmlPage, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws ElementNotFoundException, IOException, SegSocialException {
//		CCC
			{
				HtmlInput inputCcc = (HtmlInput) htmlPage.getElementById("idCCC");
				inputCcc.setValue(ccc);
			}
//		REGIME
			{
				HtmlSelect selectRegime = (HtmlSelect) htmlPage.getElementById("idRegimen");
				HtmlOption selectedRegime = selectRegime.getOptionByValue(regime.getValue());
				selectedRegime.setSelected(true);
			}
//		LIQUIDATION PERIOD
//			FROM
			{
				Calendar calendarFrom = Calendar.getInstance();
				calendarFrom.setTime(dateFrom);
				HtmlSelect selectMonthFrom = (HtmlSelect) htmlPage.getElementById("idMesDesde");
				String strMonthFrom = calendarFrom.get(Calendar.MONTH)<9?"0"+(calendarFrom.get(Calendar.MONTH)+1):""+(calendarFrom.get(Calendar.MONTH)+1);
				HtmlOption selectedMonthFrom = selectMonthFrom.getOptionByValue(strMonthFrom);
				selectedMonthFrom.setSelected(true);
				HtmlSelect selectYearFrom = (HtmlSelect) htmlPage.getElementById("idAnioDesde");
				HtmlOption selectedYearFrom = selectYearFrom.getOptionByValue(""+calendarFrom.get(Calendar.YEAR));
				selectedYearFrom.setSelected(true);
			}
//			TO
			{
				Calendar calendarTo = Calendar.getInstance();
				calendarTo.setTime(dateTo);
				HtmlSelect selectMonthTo = (HtmlSelect) htmlPage.getElementById("idMesHasta");
				String strMonthTo = calendarTo.get(Calendar.MONTH)<9?"0"+(calendarTo.get(Calendar.MONTH)+1):""+(calendarTo.get(Calendar.MONTH)+1);
				HtmlOption selectedMonthTo = selectMonthTo.getOptionByValue(strMonthTo);
				selectedMonthTo.setSelected(true);
				HtmlSelect selectYearTo = (HtmlSelect) htmlPage.getElementById("idAnioHasta");
				HtmlOption selectedYearTo = selectYearTo.getOptionByValue(""+calendarTo.get(Calendar.YEAR));
				selectedYearTo.setSelected(true);
			}
//		LIQUIDATION TYPE
			{
				HtmlSelect selectLiquidationType = (HtmlSelect) htmlPage.getElementById("idTipoLiquidacion");
				HtmlOption selectedLiquidationType = selectLiquidationType.getOptionByValue(liqType.getValue());
				selectedLiquidationType.setSelected(true);
			}
//		LIQUIDATION ORIGIN
			{
				DomNodeList<DomNode> liquidationOrigins = htmlPage.querySelectorAll("input[name='ORIGEN_LIQUIDACION']");
				HtmlInput liquidationOriginInput = (HtmlInput) liquidationOrigins.stream()
						.filter(origin -> ((HtmlInput)origin).getValue().equalsIgnoreCase(liqOrigin.getValue())).findFirst().get();
				clickAndCheckCode(liquidationOriginInput);
			}
//		ACCEPT
			{
				htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ACEPTAR"));
			}

		return htmlPage;
	}
	
    	static HtmlPage liquidationPageFill(HtmlPage htmlPage, final String numLiquidation )
    		throws ElementNotFoundException, IOException, SegSocialException {
    	    //  OPCION 2
    	    {
    		
    		HtmlRadioButtonInput inputOpcion2 = (HtmlRadioButtonInput) htmlPage.getElementById("idOpcion2");
		inputOpcion2.click();
    	    }
    	    // NÚMERO LIQUIDACION
    	    {
    		HtmlInput inputNumeroLiquidacion = (HtmlInput) htmlPage.getElementById("idNumeroLiquidacion");
    		inputNumeroLiquidacion.setValue(numLiquidation);
    		
    	    }
    	    //	ACCEPT
    	    {
    		htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ACEPTAR"));
    	    }
    
    	    return htmlPage;
    	}

	//RETURNS A COLLECTION OF ALL LIQUIDATIONS AVAILABLE FOR AN ENTERPRISE WITHIN THE DATE SPECIFIED
	public static Collection<Liquidation> CalculationQueryByCCC(final InputStream certificateInputStream,
		final String certificatePassword, final String certificateType, final String ccc,
		final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
		final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arrFields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
		Toolkit.verifyData(arrFields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			htmlPage=liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
			try {
				checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				Collection<Liquidation> ret= new ArrayList<Liquidation>();
				HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
				DomNodeList<DomNode> liqList=formDatos.querySelectorAll("input[type='radio']");
				for (int i=0;i<liqList.size();i++) {
					HtmlRadioButtonInput radio=(HtmlRadioButtonInput)liqList.get(i);
					radio.click();
					formDatos.getInputByValue("Continuar").setChecked(true);
					htmlPage=clickAndCheckCode(formDatos.getInputByValue("Continuar"));
					LiquidationBuilder lb=new LiquidationBuilder();
					for (DomNode domNode : htmlPage.querySelectorAll("table>tbody>tr")) {
						HtmlTableRow tr=(HtmlTableRow)domNode;
						liquidationDataType(tr, lb);
					}
					ret.add(lb.build());
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
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
	
	//RETURNS A MAP (LIQUIDATION TYPE AS KEY) OF MAPS CONTAINING EACH WORKER'S CALCULATION QUERY (WORKERS' NSS AS KEY)
	public static Map<String,Map<String, WorkerLiquidation>> workersCalculationQueryByCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
			InvalidCertificateException.checkCertificate(certificateInputStream);
			Object[] arrFields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
			Toolkit.verifyData(arrFields);
			try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
				webClient.getOptions().setJavaScriptEnabled(false);
				HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
				HtmlUnitToolkit.checkStatusAndDown(htmlPage);
				htmlPage=liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);

				try {
					checkLiquidationExceptions(htmlPage);
				}catch(NullPointerException | ElementNotFoundException e) {
					Map<String,Map<String, WorkerLiquidation>> ret= new HashMap<String,Map<String, WorkerLiquidation>>();
					HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
					DomNodeList<DomNode> liqList=formDatos.querySelectorAll("input[type='radio']");
					for (int h=0;h<liqList.size();h++) {
						
						HtmlRadioButtonInput radio=(HtmlRadioButtonInput)liqList.get(h);
						radio.click();
						htmlPage=clickAndCheckCode(formDatos.getInputByValue("Continuar"));
						htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES"));
						formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
						DomNode liqNode=htmlPage.querySelector("abbr[title='Tipo de liquidación ']").getNextSibling();
						String liq=Toolkit.removeWeirdCharacters(liqNode.getVisibleText());
						List<HtmlRadioButtonInput> listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
						HashMap<String, WorkerLiquidation> map=new HashMap<String, WorkerLiquidation>();
						for (int i=0;i<listRadiosWorkers.size();i++) {
							
							htmlPage=listRadiosWorkers.get(i).click();
							htmlPage=clickAndCheckCode(formDatos.getInputByValue("Consultar"));
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
							htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
							formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
							listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
						}
						ret.put(liq,map);
						htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
						formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
						liqList=formDatos.querySelectorAll("input[type='radio']");
					}	
					return ret;
				}
				
			} catch (FailingHttpStatusCodeException e) {
				String response = e.getResponse().getContentAsString();
				//response = response.replaceAll("\n", " ");
				Pattern pattern = Pattern.compile("\\s*<p\\s*class=\"p2\">(?<mensaje>.+?)</p>\\s*", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(response);
				ArrayList<String> list = new ArrayList<String>();
				while(matcher.find()) {
					list.add(matcher.group("mensaje"));
				}
				
//				for(String match : list) {
//					System.out.println(match);
//				}
				if(list.get(0).equalsIgnoreCase("Aplicación Cerrada temporalmente.")) {
					throw new OutOfServiceException(list.get(0), new OutOfServiceMotivation(list.get(1)));
				} else {
					StatusCodeException.HandleStatusCodeException(e);
				}
			} catch (MalformedURLException e) {
				throw new SegSocialException(e);
			} catch (IOException e) {
				throw new CertificateNotFoundException();
			}
			return null;
	}
	
	
	
	
	
	
	
	//TAKES A MAP (LIQUIDATION TYPE AS KEY) OF MAPS (NAF AS KEY) OF WORKERS' LIQUIDATIONS PASSING CCC AND NAFS AS ARGUMENTS
	public static Map<String,Map<String, WorkerLiquidation>> workersCalculationQueryByCCCandNAFS(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			try {
				htmlPage.getElementById("autorizacion0").click();
				htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ACEPTAR"));
			} catch (NullPointerException e) {}
//		FILLING THE FIELDS TO GET THE QUERY	
			htmlPage = liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
			try {
				if(((HtmlInput)htmlPage.getElementById("idCCC")).isDisabled()) {
					htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ACEPTAR"));
				}
			} catch (NullPointerException e) {}
			try {
				checkLiquidationExceptions(htmlPage);
			} catch(NullPointerException | ElementNotFoundException e) {
//		ITERATE SELECTS BY LIQUIDATION TYPE
				Map<String,Map<String, WorkerLiquidation>> ret= new HashMap<String,Map<String, WorkerLiquidation>>();
				DomNodeList<DomNode> listOfLiquidationTypes = htmlPage.querySelectorAll("input[type='radio']");
				for(int i=0; i<listOfLiquidationTypes.size(); i++) {
					HashMap<String, WorkerLiquidation> map = new HashMap<String, WorkerLiquidation>();
					HtmlRadioButtonInput radio = (HtmlRadioButtonInput) listOfLiquidationTypes.get(i);
					radio.click();
					htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONTINUAR"));
					htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES"));
					for(String naf : nafs) {
						
						DomNodeList<DomNode> rowNodes = htmlPage.querySelectorAll("tbody tr:not([class='cabecera'])");
						if(!rowNodes.isEmpty()) {
							Optional<DomNode> row = rowNodes.stream().filter(node -> (Toolkit.removeWeirdCharacters(((HtmlTableRow)node).getCell(1).getVisibleText()).equalsIgnoreCase(naf))).findFirst();
							if(row.isPresent()) {
								HtmlTableRow tableRow = (HtmlTableRow) row.get();
								HtmlRadioButtonInput nafRadio = tableRow.getCell(0).querySelector("input[type='radio']");
								nafRadio.click();
							}
						}
						else {
							HtmlInput nafInput = (HtmlInput) htmlPage.getElementById("NAF_TRABAJADOR");
							nafInput.setValue(naf);
						}
						htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTAR"));
						try {
						//Taking the CAF
							DomNode cafNode=htmlPage.querySelector("abbr[title='Código alfabético (abreviado a partir de nombre y apellidos) del trabajador']").getParentNode();
							String caf=Toolkit.removeWeirdCharacters(cafNode.getVisibleText());
							caf=caf.substring(caf.indexOf(":")+1).trim();
							
						//Setting CAF and NAF
							WorkerLiquidationBuilder wlb=new WorkerLiquidationBuilder();
							wlb.setCaf(caf);
							wlb.setNss(naf);
							
						//Putting each worker's data into the inner maps
							DomNodeList<DomNode> rows=htmlPage.querySelectorAll("tbody tr");
							for (DomNode row : rows) {
								workerLiquidationDataType((HtmlTableRow)row, wlb);
							}
							map.put(naf, wlb.build());
							htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
						
						} catch (NullPointerException e1) {}
					}
					DomNode liqNode=htmlPage.querySelector("abbr[title='Tipo de liquidación ']").getNextSibling();
					String liq=Toolkit.removeWeirdCharacters(liqNode.getVisibleText());	
					htmlPage = clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
					if(!map.isEmpty())
						ret.put(liq, map);
					htmlPage= clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
					listOfLiquidationTypes = htmlPage.querySelectorAll("input[type='radio']");
				}
				return ret;
			}
			
		} catch (FailingHttpStatusCodeException e) {
			String response = e.getResponse().getContentAsString();
			//response = response.replaceAll("\n", " ");
			Pattern pattern = Pattern.compile("\\s*<p\\s*class=\"p2\">(?<mensaje>.+?)</p>\\s*", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(response);
			ArrayList<String> list = new ArrayList<String>();
			while(matcher.find()) {
				list.add(matcher.group("mensaje"));
			}
			
//			for(String match : list) {
//				System.out.println(match);
//			}
			if(list.get(0).equalsIgnoreCase("Aplicación Cerrada temporalmente.")) {
				throw new OutOfServiceException(list.get(0), new OutOfServiceMotivation(list.get(1)));
			} else {
				StatusCodeException.HandleStatusCodeException(e);
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {}
		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * GETS THE WORKER LIQUIDATION AVAILABLE IN THE FIRST ENTERPRISE LIQUIDATION SHOWN FOR THE WORKER WHOSE NAF IS INPUTTED
	 */
	public static WorkerLiquidation WorkerCalculationQueryByNAF(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String naf, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arrFields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
		Toolkit.verifyData(arrFields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			htmlPage=liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
			try {
				checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				htmlPage=htmlPage.getElementById("liquidacion0").click();
				htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONTINUAR"));
				htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES"));
				DomNodeList<DomNode> workerRows=htmlPage.querySelectorAll("tbody>tr:not(.cabecera)");
				for (DomNode rowNode : workerRows) {
					HtmlTableRow row=(HtmlTableRow)rowNode;
					String nafta=Toolkit.removeWeirdCharacters(row.getCell(1).getVisibleText()).trim();
					if(nafta.equalsIgnoreCase(naf)) {
						DomNode radNode=row.getCell(0).querySelector("input");
						HtmlRadioButtonInput rad=(HtmlRadioButtonInput) radNode;
						htmlPage=rad.click();
						htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTAR"));
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
						for (DomNode rw : rows) {
							workerLiquidationDataType((HtmlTableRow)rw, wlb);
						}
						return wlb.build();
					}
				}
				throw new DataDoesNotExist();
			}
		} catch (FailingHttpStatusCodeException e) {
			String response = e.getResponse().getContentAsString();
			//response = response.replaceAll("\n", " ");
			Pattern pattern = Pattern.compile("\\s*<p\\s*class=\"p2\">(?<mensaje>.+?)</p>\\s*", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(response);
			ArrayList<String> list = new ArrayList<String>();
			while(matcher.find()) {
				list.add(matcher.group("mensaje"));
			}
			
//			for(String match : list) {
//				System.out.println(match);
//			}
			if(list.get(0).equalsIgnoreCase("Aplicación Cerrada temporalmente.")) {
				throw new OutOfServiceException(list.get(0), new OutOfServiceMotivation(list.get(1)));
			} else {
//				System.out.println(e.getStatusMessage());
				StatusCodeException.HandleStatusCodeException(e);
			}
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
}	
	//GETS ALL AVAILABLE WORKER LIQUIDATIONS FOR THE WORKER WHOSE NAF IS INPUTTED
	public static Collection<WorkerLiquidation> WorkerCalculationQueriesByNAF(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String naf, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arrFields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
		Toolkit.verifyData(arrFields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			HtmlUnitToolkit.checkStatusAndDown(htmlPage);
			htmlPage=liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
			try {
				checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				Collection<WorkerLiquidation> ret=new ArrayList<WorkerLiquidation>();
				
				DomNodeList<DomNode> liquidationNodes=htmlPage.querySelectorAll("tbody>tr:not(.cabecera)");
				
				for (int i=0;i<liquidationNodes.size();i++) {
					
					
					HtmlTableRow rwLiq=(HtmlTableRow)liquidationNodes.get(i);
					DomNode radLiqNode=rwLiq.getCell(0).querySelector("input");
					HtmlRadioButtonInput radLiq=(HtmlRadioButtonInput)radLiqNode;
					htmlPage=radLiq.click();
					
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONTINUAR"));
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES"));
					DomNodeList<DomNode> workerRows=htmlPage.querySelectorAll("tbody>tr:not(.cabecera)");
					for (DomNode rowNode : workerRows) {
						HtmlTableRow row=(HtmlTableRow)rowNode;
						String nafta=Toolkit.removeWeirdCharacters(row.getCell(1).getVisibleText()).trim();
						if(nafta.equalsIgnoreCase(naf)) {
							DomNode radNode=row.getCell(0).querySelector("input");
							HtmlRadioButtonInput rad=(HtmlRadioButtonInput) radNode;
							htmlPage=rad.click();
							htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.CONSULTAR"));
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
							for (DomNode rw : rows) {
								workerLiquidationDataType((HtmlTableRow)rw, wlb);
							}
							ret.add(wlb.build());
						}
					}
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
					htmlPage=clickAndCheckCode(htmlPage.getElementById("SPM.ACC.ATRAS"));
					liquidationNodes=htmlPage.querySelectorAll("tbody>tr:not(.cabecera)");
				}
				
				
				
				
				if(ret.size()==0)
					throw new DataDoesNotExist();
				else
					return ret;
			}
		} catch (FailingHttpStatusCodeException e) {
			String response = e.getResponse().getContentAsString();
			//response = response.replaceAll("\n", " ");
			Pattern pattern = Pattern.compile("\\s*<p\\s*class=\"p2\">(?<mensaje>.+?)</p>\\s*", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(response);
			ArrayList<String> list = new ArrayList<String>();
			while(matcher.find()) {
				list.add(matcher.group("mensaje"));
			}
			
//			for(String match : list) {
//				System.out.println(match);
//			}
			if(list.get(0).equalsIgnoreCase("Aplicación Cerrada temporalmente.")) {
				throw new OutOfServiceException(list.get(0), new OutOfServiceMotivation(list.get(1)));
			} else {
//				System.out.println(e.getStatusMessage());
				StatusCodeException.HandleStatusCodeException(e);
			}
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
	}	

	

	static void checkLiquidationExceptions(HtmlPage htmlPage) throws LiquidationDoesNotExist, DataDoesNotExist,
			WrongRegimeException, InvalidCccException, UnfilledMandatory, NullPointerException, ElementNotFoundException {
		HtmlDivision divError=(HtmlDivision) htmlPage.getElementById("ARQContenMensaje");
		if(divError.getVisibleText().toUpperCase().contains("NO EXISTE LIQUIDACIÓN"))
			throw new LiquidationDoesNotExist();
		else if(divError.getVisibleText().toUpperCase().contains("NO EXISTEN DATOS"))
			throw new DataDoesNotExist();
		else if(divError.getVisibleText().toUpperCase().contains("CUENTA DE COTIZACIÓN NO EXISTE"))
			throw new WrongRegimeException();
		else if(divError.getVisibleText().toUpperCase().contains("C.C.C. ERRÓNEO"))
			throw new InvalidCccException();
		else if(divError.getVisibleText().toUpperCase().contains("DEBE TENER CONTENIDO"))
			throw new UnfilledMandatory();
		else if(divError.getVisibleText().toUpperCase().contains("EL CCC NO PERTENECE AL COLECTIVO DE CLEGIOS CONCERTADOS"))
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
			lb.setCcBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCcLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setItWorkAccidentBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setItWorkAccidentBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setItWorkAccidentWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setItWorkAccidentTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setImsWorkAccidentBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setImsWorkAccidentBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setImsWorkAccidentWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setImsWorkAccidentTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setWorkAccidentLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setWorkAccidentLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setWorkAccidentLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setWorkAccidentLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("OTRAS COTIZACIONES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributionsBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributionsBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributionsWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributionsTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributionsLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributionsLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributionsLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributionsLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setTotalLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setTotalLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setTotalLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setTotalLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("BONIF.Y SUBVENC.CON CARGO AL INEM")) {
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setGrantsAndBonusesBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setGrantsAndBonusesBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setGrantsAndBonusesWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setGrantsAndBonusesTotalFee(nmbr4);
		}
		
		
	}
	
	
	private static void workerLiquidationDataType(HtmlTableRow tr, WorkerLiquidationBuilder lb) throws SegSocialException {
		String innerText=Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
		if(innerText.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setCcDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCcBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setCcLiquidDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setCcLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setCcLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setCcLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setCcLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setItWorkAccidentDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setItWorkAccidentBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setItWorkAccidentusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setItWorkAccidentWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setItWorkAccidentTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setImsWorkAccidentDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setImsWorkAccidentBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setImsWorkAccidentBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setImsWorkAccidentWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setImsWorkAccidentTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setWorkAccidentLiquidDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setWorkAccidentLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setWorkAccidentLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setWorkAccidentLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setWorkAccidentLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("DESEMPLEO")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setUnemploymentDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setUnemploymentBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setUnemploymentBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setUnemploymentWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setUnemploymentTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("FOGASA")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setFogasaDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setFogasaBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setFogasaBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setFogasaWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setFogasaTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("FORMACIÓN PROFESIONAL")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setJobTrainingDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setJobTrainingBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setJobTrainingBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setJobTrainingWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setJobTrainingTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setOtherContributionsLiquidDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setOtherContributionsLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setOtherContributionsLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setOtherContributionsLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setOtherContributionsLiquidTotalFee(nmbr4);
		}
		else if(innerText.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setTotalLiquidDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setTotalLiquidBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setTotalLiquidBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setTotalLiquidWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setTotalLiquidTotalFee(nmbr4);
		}
		else if(innerText != null && innerText.contains("BONIF")) {
			String desc = Toolkit.removeWeirdCharacters(tr.getCell(0).getVisibleText());
			lb.setGrantsAndBonusesDescription(desc);
			Float nmbr1=cellToFloat(tr.getCell(1));
			lb.setGrantsAndBonusesBase(nmbr1);
			Float nmbr2=cellToFloat(tr.getCell(2));
			lb.setGrantsAndBonusesBusinessFee(nmbr2);
			Float nmbr3=cellToFloat(tr.getCell(3));
			lb.setGrantsAndBonusesWorkerFee(nmbr3);
			Float nmbr4=cellToFloat(tr.getCell(4));
			lb.setGrantsAndBonusesTotalFee(nmbr4);
		}
		
	}
	

}