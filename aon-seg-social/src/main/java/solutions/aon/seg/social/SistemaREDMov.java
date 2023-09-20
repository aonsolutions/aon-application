
package solutions.aon.seg.social;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.javascript.JavaScriptErrorListener;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDMov {
	
	//	Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");
	private static final String MESSAGE_ERROR = "Error: No se acepta la comunicaci\u00f3n";
	
	private SistemaREDMov() {
	    throw new IllegalStateException("Utility class");
	}
	 
	// HANDLE THE EXCEPTIONS OF ALTA METHOD
	public static byte[] sendAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			return sendAltaImpl(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	public static void validateCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SegSocialException {
		try {
			validateCertImpl(certificateInputStream, certificatePassword, certificateType);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static byte[] getReportAffiliateInMovPrev(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			return getReportAffiliateInMovPrevImpl(certificateInputStream, certificatePassword, certificateType, regime,
					ccc);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	public static byte[] getReportAffiliateInAlta(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			return getReportAffiliateInAltaImpl(certificateInputStream, certificatePassword, certificateType, regime,
					ccc);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	// HANDLE THE EXCEPTIONS OF ALTA METHOD
	public static byte[] sendBaja(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			return sendBajaImpl(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	public static Collection<Employee> ipfxnaf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, List<String> nssList)
			throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			return ipfxnafImpl(certificateInputStream, certificatePassword, certificateType, nssList);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	public static Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			return nafxipfImpl(certificateInputStream, certificatePassword, certificateType, ipf, apellido1, apellido2);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}

	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, SituationType sit, String regimen, String ctaCti, String nss, Date fecha)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			movPrevDeleteImpl(certificateInputStream, certificatePassword, certificateType, sit, regimen, ctaCti, nss,
					fecha);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static void updateContractCoef(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, Date fechaCambio,
			Optional<String> contract, String coef) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			updateContractCoefImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti,
					nss, fechaCambio, contract, coef);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static void removeMovConsolidated(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, SituationType situationType, String regimen, String ctaCti, String nss,
			String ipf, Date date) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try {
			removeMovConsolidatedImpl(certificateInputStream, certificatePassword, certificateType, situationType,
					regimen, ctaCti, nss, ipf, date);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static void updateQuoteGroup(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String grupCtz,
			Date fecha) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			updateQuoteGroupImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
					grupCtz, fecha);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static void updateOccupation(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String ocup,
			Date fecha) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			updateOccupationImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
					ocup, fecha);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}
	
	public static void updateCno(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cno,
			Date fecha) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			updateCnoImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
					cno, fecha);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}

	public static void updateCatProf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {updateCatProfImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, cat, fecha);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}	
	
	private static byte[] sendAltaImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws SegSocialException, FailingHttpStatusCodeException, IOException, InterruptedException  {
		String situation = employee.getSituacion()!=null ? employee.getSituacion() : "01";
    	Integer mov = 0;
		String ident = Toolkit.getIdentityType(employee.getIpf());
		String dni =  Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
		String[] fra = formatDate(employee.getFra()); //fecha [dia,mes,año]
		WebClient webclient = getWebClient(certificateInputStream,certificatePassword, certificateType);
		webclient.getOptions().setUseInsecureSSL(true);
		
		Optional<String> colect = employee.getColec();
		Optional<String> mdCtz = employee.getMdctz();
		Optional<Double> factor = employee.getFactor(); //COEFICIENTE
    	
		HtmlPage htmlPage = firstPageAltaBaja(
				webclient, mov, employee.getNss(), employee.getCtaCti(),
				employee.getRegime(), dni, ident
    	);

		HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
		
		employee.getRlce().ifPresent(rlce-> {
			form.getInputByName("txt_SDFRLCE_ayuda").setValue(rlce);
			form.getInputByName("txt_SDFRLCE_ayuda").setValueAttribute(rlce);
		});
		
		form.getInputByName("txt_SDFSITAFI_ayuda").setValue(situation); 
		form.getInputByName("txt_SDFFREALDD").setValue(fra[0]); 
		form.getInputByName("txt_SDFFREALMM").setValue(fra[1]); 
		form.getInputByName("txt_SDFFREALAA").setValue(fra[2]); 
		form.getInputByName("txt_SDFFREALDD").setValueAttribute(fra[0]); 
		form.getInputByName("txt_SDFFREALMM").setValueAttribute(fra[1]); 
		form.getInputByName("txt_SDFFREALAA").setValueAttribute(fra[2]); 
		//GRUPO DE COTIZACION
		employee.getGc().ifPresent(gc-> {
			form.getInputByName("txt_SDFGRUCOT_ayuda").setValue(gc);
			form.getInputByName("txt_SDFGRUCOT_ayuda").setValueAttribute(gc);
		});
		//CONTRATO
		employee.getContract().ifPresent(contract-> {
			form.getInputByName("txt_SDFTICO_ayuda").setValue(contract);
			form.getInputByName("txt_SDFTICO_ayuda").setValueAttribute(contract);
		});
		
		//COLLECTIVE
		employee.getCollective().ifPresent(collective-> {
			form.getInputByName("txt_SDFCOLTRA_ayuda").setValue(collective);
			form.getInputByName("txt_SDFCOLTRA_ayuda").setValueAttribute(collective);
		});
		
		//CONVENIO
		if(form.getInputByName("txt_SDFCONVCOL_ayuda").getValue().isEmpty()) {
		    String convenio = colect.isPresent() ? colect.get() : "60888888888888";
			form.getInputByName("txt_SDFCONVCOL_ayuda").setValue(convenio); 
			form.getInputByName("txt_SDFCONVCOL_ayuda").setValueAttribute(convenio); 
		}
		//OCUPACION
		if (employee.getOcup() != null) {
			form.getInputByName("txt_SDFOCUPACION_ayuda").setValue(employee.getOcup().toUpperCase());
			form.getInputByName("txt_SDFOCUPACION_ayuda").setValueAttribute(employee.getOcup().toUpperCase());
		}
		
		if (employee.getRegime().equals("0163") && !mdCtz.isEmpty()) {
			form.getInputByName("txt_SDFMODCOTI_ayuda").setValue(mdCtz.get());
			form.getInputByName("txt_SDFMODCOTI_ayuda").setValueAttribute(mdCtz.get());
		} else {
			//COEFICIENTE
			if(!factor.isEmpty()) {
				Integer coefInt =  (int) Math.round(factor.get() * 1000);
				form.getInputByName("txt_SDFCOEFCO_ayuda").setValue(Integer.toString(coefInt)); 
				form.getInputByName("txt_SDFCOEFCO_ayuda").setValueAttribute(Integer.toString(coefInt)); 
			} else if(!employee.getCoef().isEmpty()) {
				String coef = Integer.toString(employee.getCoef().get().intValue());
				form.getInputByName("txt_SDFCOEFCO_ayuda").setValue(coef); 
				form.getInputByName("txt_SDFCOEFCO_ayuda").setValueAttribute(coef); 
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////// ALTAS PARA REGIMEN DE ARTISTA Y COND DE DESEMPLEADO
		if (employee.getRegime().equals("0112")) {
			employee.getArtistRegimen().ifPresent(artistRegimen ->{
				form.getInputByName("txt_SDFCATPROF_ayuda").setValue(artistRegimen);
				form.getInputByName("txt_SDFCATPROF_ayuda").setValueAttribute(artistRegimen);
			});	
		}
		
		employee.getUnemployedStatus().ifPresent(unemployedStatus ->{
			form.getInputByName("txt_SDFDESEMP_ayuda").setValue(unemployedStatus);
			form.getInputByName("txt_SDFDESEMP_ayuda").setValueAttribute(unemployedStatus);
		});
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		employee.getQuoteMonth().ifPresent(quote->{
			DomNode quoteMonthNode = form.querySelector("#SDFINDGCMENSUAL"); 
			if(quoteMonthNode!=null && Boolean.TRUE.equals(quote) ) {
				((HtmlCheckBoxInput)quoteMonthNode).setChecked(true);
			}
		});
		
		//CNO
		employee.getCno().ifPresent(cno-> {
			form.getInputByName("txt_SDFCNOCUP_ayuda").setValue(cno);
			form.getInputByName("txt_SDFCNOCUP_ayuda").setValueAttribute(cno);
		});
		
		//------------GET TA
		DomNode printDoc = form.querySelector("select[name=\"cbo_ListaSiNo\"]");
		if(printDoc!=null) {
			((HtmlSelect)printDoc).setSelectedAttribute("SI", true);
		}
		
		DomNode printType = null;
		if(employee.getRegime().contains("0163")) {
			printType = form.querySelector("select[name=\"cbo_ListaTipoImpresion\"]");
		} else {
			printType = form.querySelector("select[name=\"cbo_ListaTipoImpresion001\"]");
		}

		if(printType!=null) {
			((HtmlSelect)printType).setSelectedAttribute("OnLine", true);
		}
		
//		Toolkit.buildFile(htmlPage.asXml().getBytes(), "/Users/svaldepenas/Desktop/altaTgss.html");

		Page pageResult = ((HtmlSubmitInput) form.querySelector("input[value=Continuar]")).click();
		
//		try {
//			if (pageResult.isHtmlPage()) {
//				Toolkit.buildFile(((HtmlPage) pageResult).asXml().getBytes(), "/Users/svaldepenas/Desktop/altaTgss_2.html");
//			} else {
//				Toolkit.buildFile(pageResult.getWebResponse().getContentAsStream().readAllBytes(), "/Users/svaldepenas/Desktop/ta.pdf");
//			}
//		} catch (Exception e) {
//			System.out.println("PARSER PAGE : " + e.getMessage());
//		}
		
		if (pageResult.isHtmlPage()) {
//			htmlPage = (HtmlPage) htmlPage;
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = ((HtmlPage) pageResult);
			
			DomNode msg1 = htmlPage.querySelector("#Sub0000201056");
			if (msg1 != null && msg1.getTextContent().trim().toLowerCase().contains("la mecanizacion de este tipo de registros puede implicar")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();
			}

			DomNode msg2 = htmlPage.querySelector("#Sub0600401054");
			if (msg2 != null && msg2.getTextContent().trim().toLowerCase().contains("revise el contenido del coeficiente a tiempo parcial")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
			}
			
			DomNode msg3 = htmlPage.querySelector("#Frame");
			if(msg3!=null && msg3.getTextContent().trim().toLowerCase().contains("aplicarse beneficios en materia")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
			}
		}

		if (pageResult.isHtmlPage()) {
//			htmlPage = (HtmlPage) htmlPage;
			DomNode message = ((HtmlPage) pageResult).querySelector("#DIL"); 
			if(message!=null) {
				throw new SegSocialException(message.getTextContent().trim());
			}
		} else {
			try {
				return pageResult.getWebResponse().getContentAsStream().readAllBytes();
			} catch (Exception e) {
				throw new InvalidDataException();
			}
		}

		throw new SegSocialException(MESSAGE_ERROR);
	}

	private static byte[] sendBajaImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException, FailingHttpStatusCodeException, IOException, InterruptedException {
		String situation = employee.getSituacion() != null ? employee.getSituacion() : "93";
		Integer mov = 1;
		String ident = Toolkit.getIdentityType(employee.getIpf());
		String dni = Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
		Optional<Date> frbOpt = employee.getFrb();
		Optional<Date> frvOpt = employee.getFrv(); // fecha de vacaciones OPTIONAL

		WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType);
		webClient.getOptions().setUseInsecureSSL(true);
		HtmlPage htmlPage = firstPageAltaBaja(
    			webClient,
				mov, employee.getNss(), employee.getCtaCti(),
				employee.getRegime(),  dni, ident
    	);

		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

		if (!frbOpt.isEmpty()) {
			String[] frb = formatDate(frbOpt.get()); // fecha [dia,mes,anio]
			form.getInputByName("txt_SDFSITAFI_ayuda").setValue(situation);
			form.getInputByName("txt_SDFFREALDD").setValue(frb[0]);
			form.getInputByName("txt_SDFFREALMM").setValue(frb[1]);
			form.getInputByName("txt_SDFFREALAA").setValue(frb[2]);
			form.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situation);
			form.getInputByName("txt_SDFFREALDD").setValueAttribute(frb[0]);
			form.getInputByName("txt_SDFFREALMM").setValueAttribute(frb[1]);
			form.getInputByName("txt_SDFFREALAA").setValueAttribute(frb[2]);
		}

		if (!frvOpt.isEmpty()) { // FECHA DE VACACIONES
			String[] fvac = formatDate(frvOpt.get()); 
			form.getInputByName("txt_SDFFFINVDD").setValue(fvac[0]);
			form.getInputByName("txt_SDFFFINVMM").setValue(fvac[1]);
			form.getInputByName("txt_SDFFFINVAA").setValue(fvac[2]);
			form.getInputByName("txt_SDFFFINVDD").setValueAttribute(fvac[0]);
			form.getInputByName("txt_SDFFFINVMM").setValueAttribute(fvac[1]);
			form.getInputByName("txt_SDFFFINVAA").setValueAttribute(fvac[2]);
			
			//------------- Indicativo SAA       
			employee.getAsociativeSA().ifPresent(form.getInputByName("txt_SDFINDSAA")::setValue); 
			employee.getAsociativeSA().ifPresent(form.getInputByName("txt_SDFINDSAA")::setValueAttribute); 
		}

		//------------GET TA
		DomNode printDoc = form.querySelector("select[name=\"cbo_ListaSiNo\"]");
		if(printDoc!=null) {
			((HtmlSelect)printDoc).setSelectedAttribute("SI", true);
		}
		
		DomNode printType = form.querySelector("select[name=\"cbo_ListaTipoImpresion001\"]");
		if(printType!=null) {
			((HtmlSelect)printType).setSelectedAttribute("OnLine", true);
		}
        
		Page page = ((HtmlSubmitInput) form.querySelector("input[value=Continuar]")).click();
		if (page.isHtmlPage()) {
			htmlPage = (HtmlPage) page;
			HtmlUnitToolkit.manageStatusCode(htmlPage);
	
			DomNode msg1 = htmlPage.querySelector("#Sub0000201056");
			if (msg1 != null && msg1.getTextContent().trim().toLowerCase().contains("la mecanizacion de este tipo de registros puede implicar")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();
			}
	
			DomNode msg2 = htmlPage.querySelector("#Sub0600401054");
			if (msg2 != null && msg2.getTextContent().trim().toLowerCase().contains("revise el contenido del coeficiente a tiempo parcial")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
			}
			
			DomNode msg3 = htmlPage.querySelector("#Frame");
			if(msg3!=null && msg3.getTextContent().trim().toLowerCase().contains("aplicarse beneficios en materia")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
			}
		}
					
		if (page.isHtmlPage()) {
			htmlPage = (HtmlPage) page;
			DomNode message = htmlPage.querySelector("#DIL"); 
			if(message!=null) {
				throw new SegSocialException(message.getTextContent().trim());
			}
		} else {
			try {
				return page.getWebResponse().getContentAsStream().readAllBytes();
			} catch (Exception e) {
				throw new InvalidDataException();
			}
		}
			
		throw new SegSocialException(MESSAGE_ERROR);
	}

	private static void movPrevDeleteImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, SituationType situation, String regimen, String ctaCti, String nss,
			Date fecha) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			Integer mov = SituationType.ALTA.equals(situation) ? 0 : 1;
			// Date
			String[] fr = formatDate(fecha); // date [day,month,year]

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			// form fist
			jacadaForm.getInputByName("txt_SDFTESORNAF").setValue(nss.substring(0, 2));
			jacadaForm.getInputByName("txt_SDFNUMNAF").setValue(nss.substring(2));
			jacadaForm.getInputByName("txt_SDFREGCC_ayuda").setValue(regimen);
			jacadaForm.getInputByName("txt_SDFTESCC").setValue(ctaCti.substring(0, 2));
			jacadaForm.getInputByName("txt_SDFNUMCC").setValue(ctaCti.substring(2));
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas001]>option")
					.get(mov);
			option.click();
			jacadaForm.getInputByName("txt_SDFDIAB").setValue(fr[0]);
			jacadaForm.getInputByName("txt_SDFMESB").setValue(fr[1]);
			jacadaForm.getInputByName("txt_SDFAOB").setValue(fr[2]);
			HtmlOption option1 = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas]>option")
					.get(1);
			option1.click();
			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207101004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			// second screen
			HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207101004");
			htmlPage = btnSubmit1.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		}
	}

	private static void removeMovConsolidatedImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, SituationType situationType, String regimen,
			String ctaCti, String nss, String ipf, Date date) throws Exception {

		if (SituationType.ALTA.equals(situationType)) {
			altaConsolidadaDeleteImpl(certificateInputStream, certificatePassword, certificateType, regimen, ctaCti,
					nss);
		} else if (SituationType.BAJA.equals(situationType)) {
			removeBajaConsolidatedImpl(certificateInputStream, certificatePassword, certificateType, regimen, ctaCti,
					nss, ipf, date);
		}
	}

	private static void altaConsolidadaDeleteImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ctaCti, String nss)
			throws SegSocialException, FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00E");
			handleSegSocialExceptions(htmlPage);

			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1"))
					.orElseThrow();
			formDatos.getInputByName("NA5NumSegSocialCompleto").setValue(nss);
			formDatos.getInputByName("CC1EmpresaAut").setValue(regimen + ctaCti);

			HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipoImpresion]>option").get(2);
			option.click();

			Page pageAux = ((HtmlButton) htmlPage.querySelector("#ENVIO_3")).click();
			if (pageAux instanceof XmlPage) {
				handleSegSocialExceptions((XmlPage) pageAux);
			} else
				handleSegSocialExceptions((HtmlPage) pageAux);
		}
	}

	private static void removeBajaConsolidatedImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ctaCti, String nss,
			String ipf, Date date) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR02&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			String ident = Toolkit.getIdentityType(ipf);
			String dni = Toolkit.fillStringLeft(ipf, "0", 10);
			String[] fra = formatDate(date); // date [day,month,year]

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFIDPRONAF").setValue(nss.substring(0, 2));
			form.getInputByName("txt_SDFIDNAFCON").setValue(nss.substring(2));

			form.getInputByName("txt_SDFIDTIPOPF_ayuda").setValue(ident);
			form.getInputByName("txt_SDFIDNIDEPF").setValue(dni);

			form.getInputByName("txt_SDFIDREGIMEN_ayuda").setValue(regimen);
			form.getInputByName("txt_SDFIDTESCTA").setValue(ctaCti.substring(0, 2));
			form.getInputByName("txt_SDFIDCTACON").setValue(ctaCti.substring(2));

			form.getInputByName("txt_SDFIDFREALDD").setValue(fra[0]);
			form.getInputByName("txt_SDFIDFREALMM").setValue(fra[1]);
			form.getInputByName("txt_SDFIDFREALAA").setValue(fra[2]);
			htmlPage = ((HtmlSubmitInput) form.querySelector("input[value=Continuar]")).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		}
	}

	private static Collection<Employee> ipfxnafImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, List<String> nssList)
			throws Exception {

		if (nssList.size() >= 7) {			
			throw new Exception("nss max 7");
		}

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00C");
			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1"))
					.orElseThrow();

			for (int i = 0; i < nssList.size(); i++) {
				String naf = nssList.get(i);
				naf = naf.length() > 10 ? naf.substring(0, 10) : naf;
				HtmlInput inpt = formDatos.getInputByName("NA1NumSegSocialSinDC" + Integer.toString((i + 1)));
				inpt.setValue(naf);
			}

			ArrayList<Employee> employees = new ArrayList<>();

			Page pageAux = ((HtmlButton) formDatos.querySelector("#ENVIO_3")).click();

			if (pageAux instanceof XmlPage) {
				XmlPage xmlPage = (XmlPage) pageAux;
				DomNodeList<DomNode> employeesHtml = xmlPage.querySelectorAll("trabajador");
				if(null == employeesHtml || employeesHtml.isEmpty()) employeesHtml = xmlPage.querySelectorAll("TRABAJADOR");
				EmployeeBuilder builder = new EmployeeBuilder();
				for (DomNode employee : employeesHtml) {
					DomNode ipfNode = null != employee.querySelector("ip9numdoc") ? employee.querySelector("ip9numdoc") : employee.querySelector("IP9NumDoc");
					String ipf = ipfNode.getTextContent();
					
					DomNode fieldNameNode = null != employee.querySelector("nombre_completo") ? employee.querySelector("nombre_completo") : employee.querySelector("NOMBRE_COMPLETO");
					String fieldName = fieldNameNode.getTextContent();
					
					if (!ipf.isEmpty()) {
						DomNode nssNode = null != employee.querySelector("na5numsegsocialcompleto") ? employee.querySelector("na5numsegsocialcompleto") : employee.querySelector("NA5NumSegSocialCompleto");
						String nss = nssNode.getTextContent();
						
						Employee empl = builder.setNss(nss).setName(fieldName.trim()).setIpf(ipf).build();
						employees.add(empl);
					} else if (!fieldName.isEmpty()) {
						throw new Exception(fieldName.trim());
					}
				}
			} else {
				htmlPage = (HtmlPage) pageAux;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
			return employees;
		}
	}

	private static Employee nafxipfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException, FailingHttpStatusCodeException, IOException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			String message = null;
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00D");
			Integer ident = 1; // NIF DEFAULT
			if (Toolkit.getIdentityType(ipf).equals("6")) {
				ident = 3; // NIE
			}
	
			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1")).orElseThrow();

			((HtmlOption) formDatos.querySelectorAll("select[name=tipo]>option").get(ident)).click();

			formDatos.getInputByName("ipf6NumeroDocumento").setValue(ipf);
			formDatos.getInputByName("primerApellido").setValue(apellido1);
			if (apellido2 != null && !apellido2.trim().isEmpty()) {
				formDatos.getInputByName("segundoApellido").setValue(apellido2);
			} else {
				formDatos.getInputByName("checkApellido2").setChecked(true);
			}

			EmployeeBuilder builder = new EmployeeBuilder();

			Page pageAux = ((HtmlButton) formDatos.querySelector("#ENVIO_2")).click();
			if (pageAux instanceof XmlPage) {
				XmlPage xmlPage = (XmlPage) pageAux;
				DomNode employeeHtml = xmlPage.querySelector("usuario_red");
				if(employeeHtml == null)
					employeeHtml = xmlPage.querySelector("USUARIO_RED");
				System.out.println(xmlPage.asXml());
				if(employeeHtml!=null) {
					String nss = getStringNode(employeeHtml.querySelector("na5numsegsocialcompleto"));
					if(nss == null) nss = getStringNode(employeeHtml.querySelector("NA5NumSegSocialCompleto"));
					String doc = getStringNode(employeeHtml.querySelector("ip6numero_documento"));
					if(doc == null) doc = getStringNode(employeeHtml.querySelector("IP6NUMERO_DOCUMENTO"));
					String name = getStringNode(employeeHtml.querySelector("nombre_completo"));
					if(name == null) name = getStringNode(employeeHtml.querySelector("NOMBRE_COMPLETO"));
					String identNew = getStringNode(employeeHtml.querySelector("codigo_tipo"));
					if(identNew == null) identNew = getStringNode(employeeHtml.querySelector("CODIGO_TIPO"));
					if (nss!=null && doc!=null && name!=null && identNew!=null) {
						builder
						.setNss(nss)						
						.setName(name)
						.setIpf(doc)
						.setIdent(Integer.parseInt(identNew));
					} else {
						message = "Sin datos para la consulta";
						String text = getStringNode(xmlPage.querySelector("texto"));
						if (text != null) {
							message = text;
						}
					}
				} else {
					message = "Sin datos para la consulta";
				}
			} else {
				htmlPage = (HtmlPage) pageAux;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
			
			if(message!=null) {
				throw new SegSocialException(message);
			}

			return builder.build();
		}
	}

	private static void updateQuoteGroupImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String grupCtz,
			Date fecha) throws Exception {

		String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00B";
		String fieldValue = "codNuevoGrupo";
		String fieldDate = "fechaSituacion";

		updateCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
				fecha, grupCtz, fieldValue, fieldDate, url);
	}

	private static void updateOccupationImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String ocup,
			Date fecha) throws Exception {

		String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00G";
		String fieldValue = "NueOcu";
		String fieldDate = "fecSit";
		String newOcu = ocup.toUpperCase();

		updateCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
				fecha, newOcu, fieldValue, fieldDate, url);
	}
	
	private static void updateCatProfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha)
			throws Exception {

		String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00I";
		String fieldValue = "NueCat";
		String fieldDate = "fecSit";

		updateCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss,
				fecha, cat, fieldValue, fieldDate, url);
	}
	
	private static void updateCnoImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cno,
			Date fecha) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);

			Integer ident = Integer.parseInt(Toolkit.getIdentityType(ipf));

			// Date
			String[] fr = formatDate(fecha); // date [day,month,year]

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR55&E=I&AP=AFIR");

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			// form fist
			form.getInputByName("txt_SDFPROAFI").setValue(nss.substring(0, 2));
			form.getInputByName("txt_SDFCODAFI").setValue(nss.substring(2));

			form.getInputByName("txt_SDFTIPPFI_ayuda").setValue(ident.toString());
			form.getInputByName("txt_SDFNUMPFI").setValue(ipf);

			HtmlInput regimenInput = htmlPage.querySelector("#SDFREGAFI");
			regimenInput.setValue(regimen);
			
			form.getInputByName("txt_SDFTESCTACOT").setValue(ctaCti.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValue(ctaCti.substring(2));

			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207001004_42");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			form.getInputByName("txt_SDFDREAL").setValue(fr[0]);
			form.getInputByName("txt_SDFMREAL").setValue(fr[1]);
			form.getInputByName("txt_SDFAREAL").setValue(fr[2]);
			
			if(cno != null) form.getInputByName("txt_SDFCNOCUP_ayuda").setValue(cno);

			btnSubmit = htmlPage.querySelector("#Sub2207001004_85");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		}
	}

	

	private static void updateContractCoefImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String regimen, String ctaCti,
			String nss, Date fecha, Optional<String> contract, String coef) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);

			Integer ident = Integer.parseInt(Toolkit.getIdentityType(ipf));

			// Date
			String[] fr = formatDate(fecha); // date [day,month,year]

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR45&E=I&AP=AFIR");

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			// form fist
			form.getInputByName("txt_SDFPROAFI").setValue(nss.substring(0, 2));
			form.getInputByName("txt_SDFCODAFI").setValue(nss.substring(2));

			form.getInputByName("txt_SDFTIPPFI_ayuda").setValue(ident.toString());
			form.getInputByName("txt_SDFNUMPFI").setValue(ipf);

			form.getInputByName("txt_SDFREGAFI_ayuda").setValue(regimen);
			form.getInputByName("txt_SDFTESCTACOT").setValue(ctaCti.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValue(ctaCti.substring(2));

			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207601004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			form.getInputByName("txt_SDFFREALDD").setValue(fr[0]);
			form.getInputByName("txt_SDFFREALMM").setValue(fr[1]);
			form.getInputByName("txt_SDFFREALAA").setValue(fr[2]);

			if (!contract.isEmpty()) {
				form.getInputByName("txt_SDFTICO_ayuda").setValue(contract.get()); // tipo de contrato				
			}

			if (coef == null || coef.isEmpty()) {				
				coef = "0";
			}
			
			form.getInputByName("txt_SDFCOEFCO_ayuda").setValue(coef); // coef 3 digits

			btnSubmit = htmlPage.querySelector("#Sub2207401004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
	
			DomNode msg2 = htmlPage.querySelector("#Sub0600401054");
			if (msg2 != null && msg2.getTextContent().trim().contains("Revise el contenido del coeficiente a tiempo parcial")) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
		}
	}

	private static void updateCatOcupGc(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, Date fecha,
			String newValue, String fieldValue, String fieldDate, String url) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClientExplorer(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
			HtmlPage htmlPage = webClient.getPage(url);
			
			Integer ident = Integer.parseInt(Toolkit.getIdentityType(ipf));

			String[] fr = formatDate(fecha); // date [day,month,year]

			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_6"))
					.orElseThrow();

			HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipo]>option").get(ident);
			option.click();

			formDatos.getInputByName("NA5NumSegSocialCompleto").setValue(nss);
			formDatos.getInputByName("IP9NumDoc").setValue(ipf);
			formDatos.getInputByName("CC1EmpresaAut").setValue(regimen + ctaCti);
			((HtmlInput) htmlPage.querySelector("#PR_CAMPO_ORIGEN")).setValue("FORM");

			htmlPage = ((HtmlButton) htmlPage.querySelector("#ENVIO_10")).click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
	
			formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1")).orElseThrow();
			formDatos.getInputByName(fieldValue).setValue(newValue);
			formDatos.getInputByName(fieldDate).setValue(fr[0] + "/" + fr[1] + "/" + fr[2]);

			htmlPage = ((HtmlButton) htmlPage.querySelector("#ENVIO_7")).click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

			String message = HtmlUnitToolkit.getMessageSuccess(htmlPage);
			if (!message.isEmpty()) {				
				System.out.println(message);
			}
		}
	}

	private static void handleSegSocialExceptions(HtmlPage htmlPage) throws InvalidDataException {
		try {
			DomNode error = htmlPage.querySelector("#ARQContenMensaje>ul >.mensajeError");
			if (error != null && !error.getVisibleText().isEmpty())
				throw new InvalidDataException(error.getVisibleText());
		} catch (NullPointerException e) {
		}
	}

	private static void handleSegSocialExceptions(XmlPage xmlPage) throws InvalidDataException {
		try {
			DomNode error = xmlPage.querySelector("#MESSAGES");
			if (error != null && !error.getVisibleText().isEmpty() && error.getVisibleText().indexOf("realizada correctamente") < 0) {				
				throw new InvalidDataException(error.getVisibleText());
			}
		} catch (NullPointerException e) {}
	}
	
	private static String getStringNode(DomNode el) {
		return el!=null && !el.getTextContent().trim().isEmpty() ? el.getTextContent().trim() : null;
	}
	
	private static HtmlPage firstPageAltaBaja(WebClient webClient,
			Integer mov, String nss, Optional<String> ctaCti, String regimen, String dni, String ident) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		
		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
		// form fist
		HtmlOption option = (HtmlOption) form.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(mov);
		option.click();
		form.getInputByName("txt_SDFPROAFI").setValue(nss.substring(0, 2));
		form.getInputByName("txt_SDFCODAFI").setValue(nss.substring(2));
		form.getInputByName("txt_SDFREGAFI_ayuda").setValue(regimen);
		form.getInputByName("txt_SDFTIPPFI_ayuda").setValue(ident);
		form.getInputByName("txt_SDFNUMPFI").setValue(dni);
		ctaCti.ifPresent(cta->{
			form.getInputByName("txt_SDFTESCTACOT").setValue(cta.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValue(cta.substring(2));
		});

		HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");
		htmlPage = continueIn.click();
		
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		return htmlPage;
	}

	private static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws InvalidCertificateException {
		return HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType);
	}

	private static String[] formatDate(Date fecha) {
		String[] arr = new String[3];
		String dia = "";
		String mes = "";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = "" + (calendar.get(Calendar.YEAR));
		if (calendar.get(Calendar.DATE) < 10)
			dia = "0" + calendar.get(Calendar.DATE);
		else
			dia = "" + calendar.get(Calendar.DATE);
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			mes = "0" + (calendar.get(Calendar.MONTH) + 1);
		else
			mes = "" + (calendar.get(Calendar.MONTH) + 1);
		arr[0] = dia;
		arr[1] = mes;
		arr[2] = anio;
		return arr;
	}

	private static byte[] getReportAffiliateInAltaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		Object[] arrFields= {regime, ccc};
		Toolkit.verifyData(arrFields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			ArrayList<String> cccArr = Toolkit.splitStringMultiple(ccc, 2);

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR64&E=I&AP=AFIR");

			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFREG62_ayuda").setValue(regime);
			form.getInputByName("txt_SDFTESO62").setValue(cccArr.get(0));
			form.getInputByName("txt_SDFNUM62").setValue(cccArr.get(1));

			((HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1)).click();

			HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");

			Page page = continueIn.click();
			if (page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			} else {
				try {
					return page.getWebResponse().getContentAsStream().readAllBytes();
				} catch (Exception e) {
					throw new InvalidDataException();
				}
			}
		}
		return null;
	}

	private static byte[] getReportAffiliateInMovPrevImpl(InputStream certificateInputStream,
			String certificatePassword, String certificateType, String regime, String ccc)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		
		Object[] arrFields= {regime, ccc};
		Toolkit.verifyData(arrFields);
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			ArrayList<String> cccArr = Toolkit.splitStringMultiple(ccc, 2);

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR74&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFREGENT_ayuda").setValue(regime);
			form.getInputByName("txt_SDFTESCCCENT").setValue(cccArr.get(0));
			form.getInputByName("txt_SDFCODCCCENT").setValue(cccArr.get(1));

			((HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1)).click();

			HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");

			Page page = continueIn.click();
			if (page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			} else {
				try {
					return page.getWebResponse().getContentAsStream().readAllBytes();
				} catch (Exception e) {
					throw new InvalidDataException();
				}
			}
		}
		return null;
	}

	private static void validateCertImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SegSocialException, IOException {
		byte[] certByte = certificateInputStream.readAllBytes();
		try (WebClient webClient = HtmlUnitToolkit.getWebClientCert(new ByteArrayInputStream(certByte),
				certificatePassword, certificateType)) {
			InvalidCertificateException.checkCertificate(certByte, certificatePassword);
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");

			Toolkit.checkCertificateRevoked(htmlPage.asXml());
		} catch (Exception e) {
			throw new SegSocialException(e.getMessage());
		}
	}

	private static JavaScriptErrorListener jascriptFunctionExceptionError() {
		return new JavaScriptErrorListener() {
			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
			}

			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
			}

			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {
			}

			@Override
			public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
			}

			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
			}
		};
	}

}
