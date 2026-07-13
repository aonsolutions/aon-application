package solutions.aon.seg.social;

import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.Toolkit.parseDate;
import static solutions.aon.seg.social.toolkit.Toolkit.removeExtraZeros;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.xml.transform.TransformerException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class ServicioREDEmployee extends ServicioREDRegeXML{
	
	public static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		
		SSLContext sslContext = null;
		try {				
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			List<Employee> employees = new LinkedList<>();
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			String txtSDFTESO62 = ccc != null && ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFNUM62 = ccc != null && ccc.length() > 2 ? ccc.substring(2) : "";
			
			HttpPost httpPost = new HttpPost(link);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.LIBAFCON, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6201"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair("txt_SDFREG62_ayuda", regime));
			params.add(new BasicNameValuePair("txt_SDFTESO62", txtSDFTESO62));
			params.add(new BasicNameValuePair("txt_SDFNUM62", txtSDFNUM62));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			checkOldSsError(body, 3543);
			
			if (!(Toolkit.getDIL(body) != null && Toolkit.getDIL(body).contains("3543"))) {
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
				
				employees.addAll(ServicioREDRegeXML.getEmployeesFromTable(httpClient, body, link, sessionId, regime, ccc));
				httpPost = new HttpPost(link);
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.LIBAFCON, IServicioRedConstants.LIBAFCON));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6202"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair("btn_Sub2205801004", "P?.+Ant."));
				httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				checkOldSsError(body);
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);	
			}
			
			httpPost = new HttpPost(link);
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.LIBAFCON, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6201"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair("txt_SDFREG62_ayuda", regime));
			params.add(new BasicNameValuePair("txt_SDFTESO62", txtSDFTESO62));
			params.add(new BasicNameValuePair("txt_SDFNUM62", txtSDFNUM62));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			checkOldSsError(body, 3543);
			if (!(Toolkit.getDIL(body) != null && Toolkit.getDIL(body).contains("3543"))) {
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
				
				List<Employee> formerEmployees = ServicioREDRegeXML.getEmployeesFromTable(httpClient, body, link, sessionId, regime, ccc);
				employees.addAll(formerEmployees);
			}
			return employees;
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
	}
	
	/**
	 * 
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param cccs
	 * @return Map<CCC, List<Employee>>
	 * @throws SegSocialException
	 */
	public static Collection<Employee> getTotalEmployees(byte[] certificateData,
			final String certificatePassword, final String certificateType, Map<String, Set<String>> cccs) /*cccs -> Map<REGIME, Set<CCC>>*/
			throws SegSocialException, IOException {
		InvalidCertificateException.checkCertificate(certificateData, certificatePassword);
		return getTotalEmployees(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, cccs);
	}
	
	private static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Map<String, Set<String>> cccs) /*cccs -> Map<REGIME, Set<CCC>>*/
			throws SegSocialException, IOException {
		if (cccs == null) {
			return Collections.emptyList();
		}
		
		byte[] certificateData = certificateInputStream.readAllBytes();
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType)) {
			
			List<Employee> employees = new LinkedList<>();
			
			webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			
			HtmlPage page =  webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
			
			System.out.println("CCCs loaded: " + cccs.size());
			
			for (Entry<String, Set<String>> entry : cccs.entrySet()) {
				
				String regime = entry.getKey();
				Set<String> cccSet = entry.getValue();
				
				for(String ccc : cccSet) {
					((HtmlInput)page.getElementById("SDFREG62_ayuda")).setValue(regime);
					((HtmlInput)page.getElementById("SDFREG62_ayuda")).setValueAttribute(regime);
					
					((HtmlInput)page.getElementById("SDFTESO62")).setValue(ccc.substring(0, 2));
					((HtmlInput)page.getElementById("SDFTESO62")).setValueAttribute(ccc.substring(0, 2));
					
					((HtmlInput)page.getElementById("SDFNUM62")).setValue(ccc.substring(2));
					((HtmlInput)page.getElementById("SDFNUM62")).setValueAttribute(ccc.substring(2));
					
					((HtmlInput)page.getElementById("chkgrupo1_1")).click();
					
					page = ((HtmlInput)page.getElementById("Sub2207601004")).click();
					
					System.out.println("ccc: " + ccc + " - regime: " + regime);
					
					hanleStatusCodeException(page);
					
					getEmployeesTable(page, employees, regime, ccc);
					
					// Prev employees
					page = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
					
					((HtmlInput)page.getElementById("SDFREG62_ayuda")).setValue(regime);
					((HtmlInput)page.getElementById("SDFREG62_ayuda")).setValueAttribute(regime);
					
					((HtmlInput)page.getElementById("SDFTESO62")).setValue(ccc.substring(0, 2));
					((HtmlInput)page.getElementById("SDFTESO62")).setValueAttribute(ccc.substring(0, 2));
					
					((HtmlInput)page.getElementById("SDFNUM62")).setValue(ccc.substring(2));
					((HtmlInput)page.getElementById("SDFNUM62")).setValueAttribute(ccc.substring(2));
					
					((HtmlInput)page.getElementById("chkgrupo1_2")).click();
					
					page = ((HtmlInput)page.getElementById("Sub2207601004")).click();
					
					getEmployeesTable(page, employees, regime, ccc);
					
					// Search again
					page = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
				}
				
			};
			
			return employees;
			
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
			throw new SegSocialException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}
	
	private static final Pattern SEG_SOCIAL_ERROR_PATTERN =
	        Pattern.compile("^\\s*(\\d+)\\*\\s*(.*)$");

	private static void hanleStatusCodeException(HtmlPage page) throws SegSocialException {
	    DomElement dil = page.getElementById("DIL");
	    if (dil == null) {
	        return; // no hay zona de mensajes en esta página
	    }

	    String message = dil.getTextContent();
	    if (message == null || message.isBlank()) {
	        return;
	    }
	    message = message.trim();

	    // Formato de error de la Seg. Social: "<codigo>*  <descripcion>"
	    // p.ej. "3462*    CUENTA DE COTIZACION NO AUTORIZADA"
	    Matcher matcher = SEG_SOCIAL_ERROR_PATTERN.matcher(message);
	    if (matcher.matches()) {
	        String code = matcher.group(1);
	        String description = matcher.group(2).trim();

	        // Códigos informativos de "fin de lista / sin datos": NO son error.
	        // 3252 = NO HAY MAS AFILIADOS A CONSULTAR
	        // 3037 = fin de datos (usado como NoMoreDataException en el toolkit)
	        // 3083 = sin datos
	        if (isEndOfListCode(code)) {
	            return;
	        }

	        throw new SegSocialException(code + " - " + description);
	    }
	}

	/** Códigos informativos de paginación / fin de datos: NO son error. */
	private static boolean isEndOfListCode(String code) {
	    return "3251".equals(code)   // HAY MAS AFILIADOS A CONSULTAR (hay más páginas)
	        || "3252".equals(code)   // NO HAY MAS AFILIADOS A CONSULTAR (última página)
	        || "3037".equals(code)   // fin de datos
	        || "3083".equals(code);  // sin datos
	}
	
	private static void getEmployeesTable(HtmlPage page, List<Employee> employees, String regime, String ccc)
	        throws IOException, TransformerException {

	    HtmlLabel dil = (HtmlLabel) page.getElementById("DIL");
	    if (dil == null) return;

	    String status = dil.getTextContent();

	    // Caso sin datos: no hay nada que leer.
	    if (status.contains("NO EXISTEN DATOS") ) {
	        return;
	    }

	    // Recorremos páginas hasta el fin de lista.
	    // OJO: el literal real es "NO HAY MAS AFILIADOS A CONSULTAR" (código 3252),
	    // no "NO EXISTEN MAS AFILIADOS". Comparamos por código, que es estable.
	    while (true) {
	        dil = (HtmlLabel) page.getElementById("DIL");
	        status = dil != null ? dil.getTextContent() : "";

	        HtmlTable table = (HtmlTable) page.getElementById("Sub1000112079");
	        if (table != null) {
	            for (int i = 1; i < table.getRowCount(); i++) {
	                HtmlTableRow row = table.getRow(i);

	                String nss = removeSpaces(getLabelValue(row.getCell(0)));
	                if (null == nss || nss.isEmpty()) continue;

	                String name = getLabelValue(row.getCell(1));
	                Date date = parseDateWithDashes(getLabelValue(row.getCell(2)));
	                String situation = !getLabelValue(row.getCell(3)).isEmpty()
	                        ? getLabelValue(row.getCell(3)) : "AL";
	                String ipf = Toolkit.removeExtraZeros(removeSpaces(getLabelValue(row.getCell(4))));

	                EmployeeBuilder builder = new EmployeeBuilder();
	                builder.setNss(nss)
	                       .setName(name)
	                       .setFra(date)
	                       .setSituation(situation)
	                       .setIpf(ipf)
	                       .setCtaCti(ccc)
	                       .setRegime(regime);

	                if (!situation.contains("AL")) {
	                    builder.setFrb(date);
	                }

	                final String nssFinal = nss;
	                if (employees.stream().noneMatch(e -> e.getNss().equals(nssFinal))) {
	                    employees.add(builder.build());
	                }
	            }
	        }

	        // Fin de lista: código 3252 (= "NO HAY MAS AFILIADOS A CONSULTAR").
	        if (status.contains("3252") || status.contains("NO HAY MAS AFILIADOS")) {
	            break;
	        }

	        // Pasar a la página siguiente.
	        HtmlInput next = (HtmlInput) page.getElementById("Sub2207801001");
	        if (next == null) break;   // sin botón siguiente -> terminar
	        page = next.click();
	    }
	}

	private static String getLabelValue(HtmlTableCell cell) {
		DomElement span = cell.getFirstElementChild();
        if (span != null) {
            DomElement label = span.getFirstElementChild();
            if (label instanceof HtmlLabel) {
                return label.asNormalizedText();
            }
        }
        return "";
	}

	public static Collection<Employee> getEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
					throws SegSocialException {
		return getEmployeesCommon(certificateInputStream, certificatePassword, certificateType, regime, ccc, false);
	}
	
	public static Collection<Employee> getPrevEmployees(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws SegSocialException {
		return getEmployeesCommon(certificateInputStream, certificatePassword, certificateType, regime, ccc, true);
	}
	
	/**
	 * Se han eliminado los par?etros "regimen" y "ccc" respecto al m?odo original hecho con HTMLUnit, ya que estos no se utilizaban.
	 **/
	public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nss) throws SegSocialException, IOException {
		SSLContext sslContext = null;
		try {				
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			String txtSDFTESORNAF = nss != null && nss.length() > 2 ? nss.substring(0, 2) : "";
			String txtSDFNUMNAF = nss != null && nss.length() > 2 ? nss.substring(2) : "";
			
			HttpPost httpPost = new HttpPost(link);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6101"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair("txt_SDFTESORNAF", txtSDFTESORNAF));
			params.add(new BasicNameValuePair("txt_SDFNUMNAF", txtSDFNUMNAF));
			params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			//PICKING UP INFO
			String ipf = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNUMIPF"), "innerText");
			
			String birthDate1 = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFDIANAC"), "innerText");
			String birthDate2 = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFMESNAC"), "innerText");
			String birthDate3 = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFAONAC"), "innerText");
			Date birthDate = parseDateWithDashes(birthDate1 + "-" + birthDate2 + "-" + birthDate3);
			String sex = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFSEXO"), "innerText");
			String name = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFAPELNOM"), "innerText");
			String tlf = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFMOVIL"), "innerText");
			String ctaCot1 = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTESORCCC"), "innerText");
			String ctaCot2 = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNUMCCC"), "innerText");
			String ctaCot = emptyIfNull(ctaCot1) + emptyIfNull(ctaCot2);
			String regime = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFREGIM"), "innerText");
			String companyId = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFEMPRESARIO3"), "innerText");
			String companyName = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNOMBRE3"), "innerText");
			String situation = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTSITUACAFI"), "innerText");
			String gc = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFCGRUPOAFI"), "innerText");
			String gcDesc = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTGRUPOAFI"), "innerText");
			Boolean agricultPromo = Toolkit.toBoolean(Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFPFEA"), "innerText"));
			Boolean workTimeReduct = Toolkit.toBoolean(Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFDESRJ"), "innerText"));
			String fraStr = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFRAAFI"), "innerText");
			String feaStr = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFEAAFI"), "innerText");
			String contract = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTIPOAFI"), "innerText");
			String coef = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFCOEFAFI"), "innerText");
			String colec = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFCOLECTIVO"), "innerText");
			String epig = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFEPIGAFI"), "innerText");
			String ocup = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFOCUPACION"), "innerText");
			String vinFam = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFVINCULO"), "innerText");
			String profesCat = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFCATEGORIA"), "innerText");
			String reducingCoef = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFCOEFRED"), "innerText");
			String frbStr = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFRBAFI"), "innerText");
			String febStr = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFEBAFI"), "innerText");
			if (ipf != null)
				ipf = removeExtraZeros(ipf);
			if (companyId != null)
				companyId = removeExtraZeros(companyId);
			
			Date fra = parseDate(fraStr, "dd/MM/yyyy");
			Date fea = parseDate(feaStr, "dd/MM/yyyy");
			Date frb = parseDate(frbStr, "dd/MM/yyyy");
			Date feb = parseDate(febStr, "dd/MM/yyyy");
			
			
			if (frb == null) {
				
				httpPost = new HttpPost(link);
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6103"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair("chk_SDFNIVEMPL", "1"));
				params.add(new BasicNameValuePair("chk_SDFDIBA", "1"));
				params.add(new BasicNameValuePair("chk_SDFJORNREAL", "1"));
				params.add(new BasicNameValuePair("chk_SDFDATOSCONTAFI", "1"));
				params.add(new BasicNameValuePair("chk_SDFPECLIQAFI", "1"));
				params.add(new BasicNameValuePair("chk_SDFCONSSUSTITUTO", "1"));
				params.add(new BasicNameValuePair("chk_SDFCES", "1"));
				params.add(new BasicNameValuePair("chk_SDFPECASIMALTA", "1"));
				params.add(new BasicNameValuePair("chk_SDFERE", "1"));
				params.add(new BasicNameValuePair("chk_SDFCONVCOL", "1"));
				params.add(new BasicNameValuePair("chk_SDFSITINCA", "1"));
				params.add(new BasicNameValuePair("chk_SDFSITIT", "1"));
				params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
				httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				checkOldSsError(body);
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
				
				String frEl = Toolkit.getElementByAttribute(body, "id", "SDFFRBAFI");
				if (frEl != null) {
					situation = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTSITUACAFI"), "innerText");
					frbStr = Toolkit.getAttribute(frEl, "innerText");
					febStr = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFEBAFI"), "innerText");
					frb = parseDate(frbStr, "dd/MM/yyyy");
					feb = parseDate(febStr, "dd/MM/yyyy");	
				}
				
				
			}
		
			
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
	}
	
	
	/**
	 * INFORME DE VIDA LABORAL DE UN C. C. C.
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param regime
	 * @param ccc
	 * @param from
	 * @param to
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getCccLaboralLifePOST(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException {
		
		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body1 = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR71&E=I&AP=AFIR");
			link = Toolkit.getLink(body1);
			sessionId = Toolkit.getSessionId(body1);
			
			HttpPost httpPost = new HttpPost(link);
	
			String txtSDFTESCCO = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFNYCCCO = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ACRM7101"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Sub2207001009"));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, "Acr71"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "Acr71"));
			params.add(new BasicNameValuePair("txt_SDFREGCCO", regime));
			params.add(new BasicNameValuePair("txt_SDFTESCCO", txtSDFTESCCO));
			params.add(new BasicNameValuePair("txt_SDFNYCCCO", txtSDFNYCCCO));
			params.add(new BasicNameValuePair("txt_SDFDIADESDEM", String.format("%td", from)));
			params.add(new BasicNameValuePair("txt_SDFMESDESDEM", String.format("%tm", from)));
			params.add(new BasicNameValuePair("txt_SDFAODESDEM", String.format("%tY", from)));
			params.add(new BasicNameValuePair("txt_SDFDIAHASTAM", String.format("%td", to)));
			params.add(new BasicNameValuePair("txt_SDFMESHASTAM", String.format("%tm", to)));
			params.add(new BasicNameValuePair("txt_SDFAOHASTAM", String.format("%tY", to)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("btn_Sub2207001009", IServicioRedConstants.CONTINUE));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body1 = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body1);
			Toolkit.checkTooLong(body1);
			httpPost = Toolkit.reportGenerationForm(body1);
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
		
	}
	
}
