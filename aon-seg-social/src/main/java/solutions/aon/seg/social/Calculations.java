package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.TextPage;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.util.WebConnectionWrapper;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.OutOfServiceMotivation;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class Calculations {
	
    @FunctionalInterface
    static interface LiquidationPageFill {
	    HtmlPage fill(HtmlPage htmlPage) throws IOException, SegSocialException;
	}

    @FunctionalInterface
    static interface CalcCallback {
	    void accept(String liquidation, String naf, Map<Period, Map<String, Calc>> calcs);
	}
    
    private static void trace(String liquidation, String naf, Map<Period, Map<String, Calc>> calcs) {
    	
    }

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationQueryByCCC(
			final InputStream certificateInputStream,
			final String certificatePassword, 
			final String certificateType, 
			final String ccc,
			final SistemaRED.Regime regime, 
			final Date dateFrom, 
			final Date dateTo, 
			final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		return workersCalculationQueryByCCC(
				certificateInputStream, 
				certificatePassword, 
				certificateType, 
				ccc, 
				regime, 
				dateFrom, 
				dateTo, 
				liqType, 
				liqOrigin, 
				Calculations::trace
				);
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationQueryByCCC(
			final InputStream certificateInputStream,
			final String certificatePassword, 
			final String certificateType, 
			final String ccc,
			final SistemaRED.Regime regime, 
			final Date dateFrom, 
			final Date dateTo, 
			final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin,
			final CalcCallback callback) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arrFields= {ccc, regime, dateFrom, dateTo, liqType, liqOrigin};
		Toolkit.verifyData(arrFields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			try {
				htmlPage.getElementById("autorizacion0").click();
				htmlPage = htmlPage.getElementById("SPM.ACC.ACEPTAR").click();
			} catch (NullPointerException e) {}
			htmlPage=SistemaREDI.liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
			try {
				SistemaREDI.checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				Map<String, Map<String,Map<Period, Map<String, Calc>>>> ret=  new LinkedHashMap<String, Map<String,Map<Period, Map<String, Calc>>>>();
				HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
				DomNodeList<DomNode> liqList=formDatos.querySelectorAll("input[type='radio']");
				DomNodeList<DomNode> trowList = htmlPage.querySelectorAll("table>tbody>tr:not(.cabecera)");
				
				for (int h=0;h<liqList.size();h++) {
					String liquidationType = Toolkit.removeWeirdCharacters(((HtmlTableRow)trowList.get(h)).getCells().get(3).getVisibleText());
					Map<String,Map<Period, Map<String, Calc>>> nafMap = new LinkedHashMap<String,Map<Period, Map<String, Calc>>>();
					HtmlRadioButtonInput radio=(HtmlRadioButtonInput)liqList.get(h);
					radio.click();
					htmlPage=formDatos.getInputByValue("Continuar").click();
					htmlPage = htmlPage.getElementById("SPM.ACC.CONSULTA_TRABAJADORES").click();
					formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
					List<HtmlRadioButtonInput> listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
					for (int i=0;i<listRadiosWorkers.size();i++) {
						htmlPage=listRadiosWorkers.get(i).click();
						htmlPage=formDatos.getInputByValue("Consultar").click();
						DomNode nafElement = htmlPage.querySelector("abbr[title='Número de afiliación a la Seguridad Social']").getNextSibling();
						String naf = Toolkit.removeWeirdCharacters(nafElement.getVisibleText());
						htmlPage = htmlPage.getElementById("SPM.ACC.RELACION_TRAMOS").click();
						
						List<DomElement> radios = htmlPage.getElementsByName("TRAMO");
						
						Map<Period, Map<String, Calc>> periods = new LinkedHashMap<Period, Map<String,Calc>>();
						
						for (int k=0; k<radios.size(); k++) {
							DomElement rad = radios.get(k);
							htmlPage = rad.click();
							htmlPage = htmlPage.getElementById("SPM.ACC.CALCULOS_TRAMO").click();
							
							DomNode element = htmlPage.querySelector("div>abbr[title='Número de afiliado.']");
							element = element.getParentNode();
							element = element.getNextElementSibling();
							String fromDateStr = Toolkit.removeWeirdCharacters(element.getVisibleText());
							Pattern datePattern = Pattern.compile("\\s*Fecha\\s*(Desde|Hasta)\\s*:\\s*(?<date>(?<day>\\d{1,2})\\/(?<month>\\d{1,2})\\/(?<year>\\d{2,4}))\\s*", Pattern.CASE_INSENSITIVE);
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							Matcher matcher = datePattern.matcher(fromDateStr);
							Date fromDate = null;
							if (matcher.matches()) {
								try {
									fromDate = df.parse(matcher.group("date"));
								} catch (ParseException e1) {}
							}
							element = element.getNextElementSibling();
							String toDateStr = Toolkit.removeWeirdCharacters(element.getVisibleText());
							matcher = datePattern.matcher(toDateStr);
							Date toDate = null;
							if (matcher.matches()) {
								try {
									toDate = df.parse(matcher.group("date"));
								} catch (ParseException e1) {}
							}
							
							Period period = new Period(fromDate, toDate);
							
							HtmlTableBody firstTable = htmlPage.querySelector("table>tbody");
							
							DomNodeList<DomNode> trList = firstTable.querySelectorAll("tr:not(.cabecera)");
							
							LinkedHashMap<String, Calc> calcs = new LinkedHashMap<String, Calc>();
							
							trList.forEach(trNode -> {
								HtmlTableRow tr = (HtmlTableRow) trNode;
								List<HtmlTableCell> cells = tr.getCells();
								
								String regExp = "\\s*(-?(\\d*\\.?)*\\d+\\,?\\d*).*";
								
								String description = Toolkit.removeWeirdCharacters(cells.get(0).getVisibleText());
								
								String baseStr = Toolkit.removeWeirdCharacters(cells.get(1).getVisibleText());
								baseStr = baseStr.replaceAll(regExp, "$1");
								Double base = baseStr != null && !baseStr.isEmpty() ? Double.parseDouble(baseStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;
								
								String enterprisePercentStr = Toolkit.removeWeirdCharacters(cells.get(2).getVisibleText());
								enterprisePercentStr = enterprisePercentStr.replaceAll(regExp, "$1");
								Double enterprisePercent = enterprisePercentStr != null && !enterprisePercentStr.isEmpty() ? Double.parseDouble(enterprisePercentStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;

								String enterpriseStr = Toolkit.removeWeirdCharacters(cells.get(3).getVisibleText());
								enterpriseStr = enterpriseStr.replaceAll(regExp, "$1");
								Double enterprise = enterpriseStr != null && !enterpriseStr.isEmpty() ? Double.parseDouble(enterpriseStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;
								
								String employeePercentStr = Toolkit.removeWeirdCharacters(cells.get(4).getVisibleText());
								employeePercentStr = employeePercentStr.replaceAll(regExp, "$1");
								Double employeePercent = employeePercentStr != null && !employeePercentStr.isEmpty() ? Double.parseDouble(employeePercentStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;

								String employeeStr = Toolkit.removeWeirdCharacters(cells.get(5).getVisibleText());
								employeeStr = employeeStr.replaceAll(regExp, "$1");
								Double employee = employeeStr != null && !employeeStr.isEmpty() ? Double.parseDouble(employeeStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;
								
								String totalStr = Toolkit.removeWeirdCharacters(cells.get(6).getVisibleText());
								totalStr = totalStr.replaceAll(regExp, "$1");
								Double total = totalStr != null && !totalStr.isEmpty() ? Double.parseDouble(totalStr.replaceAll("\\.", "").replaceAll(",", ".")) : null;
								
								Calc calc = new Calc()
										.setBase(base)
										.setTotal(total)
										.setEmployee(employee)
										.setEnterprise(enterprise)
										.setEmployeePercent(employeePercent)
										.setEnterprisePercent(enterprisePercent)
										;
								calcs.put(description, calc);
								
							});
							
							periods.put(period, calcs);
							callback.accept(liquidationType, naf, periods);
							
							htmlPage = htmlPage.getElementById("SPM.ACC.ATRAS").click();
							radios = htmlPage.getElementsByName("TRAMO");
						}
						nafMap.put(naf, periods);
						
						htmlPage = htmlPage.getElementByName("SPM.ACC.ATRAS").click();
						
						htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
						formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
						listRadiosWorkers=formDatos.getRadioButtonsByName("NAF");
					}
					ret.put(liquidationType, nafMap);
					htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
					htmlPage=htmlPage.getElementById("SPM.ACC.ATRAS").click();
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
			
//			for(String match : list) {
//				System.out.println(match);
//			}
			if(list.get(0).equalsIgnoreCase("Aplicación Cerrada temporalmente.")) {
				throw new OutOfServiceException(list.get(0), new OutOfServiceMotivation(list.get(1)));
			} else {
				StatusCodeException.HandleStatusCodeException(e);
			}
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CertificateNotFoundException();
		} catch (ElementNotFoundException e) {
			if(e.getAttributeValue() != null && e.getAttributeValue().length() == 4) {
				try {
					Integer.parseInt(e.getAttributeValue());
					throw new InvalidDateException(e.getAttributeValue());
				} catch (NumberFormatException e1) {
				}
			}
			throw new solutions.aon.seg.social.exception.ElementNotFoundException(e.getMessage());
		}
		return Collections.emptyMap();
	}
	
	
	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFS(
			final InputStream certificateInputStream,
			final String certificatePassword, 
			final String certificateType, 
			String numLiquidation, 
			String authorized, 
			String[] nafs
			) throws SegSocialException{
		
		return workersCalculationByCCCandNAFS(
			    certificateInputStream, 
			    certificatePassword, 
			    certificateType,
			    numLiquidation,
			    authorized,
			    nafs,
			    Calculations::trace
				);
		
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFS(
		final InputStream certificateInputStream,
		final String certificatePassword, 
		final String certificateType, 
		String numLiquidation, 
		String authorized, 
		String[] nafs,
		final CalcCallback callback) throws SegSocialException{
	    
	    Object[] arrFields = { numLiquidation };
	    Toolkit.verifyData(arrFields);

	    return workersCalculationByCCCandNAFS(
		    certificateInputStream, 
		    certificatePassword, 
		    certificateType, 
		    authorized, 
		    htmlPage -> SistemaREDI.liquidationPageFill(htmlPage, numLiquidation), 
		    nafs,
		    callback);
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFS(
			final InputStream certificateInputStream,
			final String certificatePassword, 
			final String certificateType, 
			final String ccc,
			final SistemaRED.Regime regime, 
			final Date dateFrom, 
			final Date dateTo, 
			final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, 
			String authorized, 
			String[] nafs) throws SegSocialException{
		return workersCalculationByCCCandNAFS(
				certificateInputStream, 
				certificatePassword, 
				certificateType, 
				ccc, 
				regime, 
				dateFrom, 
				dateTo, 
				liqType, 
				liqOrigin, 
				authorized, 
				nafs,
				Calculations::trace);
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFS(
		final InputStream certificateInputStream,
		final String certificatePassword, 
		final String certificateType, 
		final String ccc,
		final SistemaRED.Regime regime, 
		final Date dateFrom, 
		final Date dateTo, 
		final SistemaRED.LiquidationType liqType,
		final SistemaRED.LiquidationOrigin liqOrigin, 
		String authorized, 
		String[] nafs,
		final CalcCallback callback) throws SegSocialException{
	    
	    Object[] arrFields = { ccc, regime, dateFrom, dateTo, liqType, liqOrigin };
	    Toolkit.verifyData(arrFields);

	    return workersCalculationByCCCandNAFS(
		    certificateInputStream, 
		    certificatePassword, 
		    certificateType, 
		    authorized, 
		    htmlPage -> SistemaREDI.liquidationPageFill(htmlPage, ccc, regime, dateFrom, dateTo, liqType, liqOrigin), 
		    nafs,
		    callback);
	}
	
	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFS(
			final InputStream certificateInputStream,
			final String certificatePassword, 
			final String certificateType, 
			String authorized,
			LiquidationPageFill liquidationPageFill,  
			String[] nafs,
			final CalcCallback callback) throws SegSocialException{
		
		byte [] certificateData = null;
		try {
			certificateData = certificateInputStream.readAllBytes();
		} catch ( IOException e ) {
			
		}
		
		InvalidCertificateException.checkCertificate(certificateData, certificatePassword);
		
		Map<String,String> variables = new HashMap<>();
		variables.put("coVgRutaLocalJS", "/ControlRecaudacion/js");
		variables.put("skVgSubtituloAplicacion", "ServicioConsultadeCalculos");

		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType);
			WebConnectionWrapper wrapper =HtmlUnitToolkit.transformXmlPage(webClient, certificateData, certificatePassword, certificateType, variables) ){
			

    		HtmlUnitToolkit.transformXmlPage(webClient, certificateData, certificatePassword, certificateType, variables);
			
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			try {
				authorized = authorized.trim().replaceFirst("^0*", "");
				for ( DomElement autorizacionAnchor : htmlPage.getElementsByTagName(HtmlAnchor.TAG_NAME) ) {
						String href = ((HtmlAnchor) autorizacionAnchor).getHrefAttribute();
						if ( href.endsWith("NUM_AUTORIZADO="+authorized) ) {
							htmlPage = ((HtmlAnchor) autorizacionAnchor).click();
							break;
						}
				}
			} catch (NullPointerException e) {}
			
			htmlPage=liquidationPageFill.fill(htmlPage);
			try {
				SistemaREDI.checkLiquidationExceptions(htmlPage);
			}catch(NullPointerException | ElementNotFoundException e) {
				Map<String, Map<String,Map<Period, Map<String, Calc>>>> ret= new LinkedHashMap<String, Map<String,Map<Period, Map<String, Calc>>>>();
				HtmlForm liqForm = (HtmlForm) htmlPage.getElementById("idFormularioSeleccionLiquidaciones");
				DomNodeList<HtmlElement> liqAnchors = liqForm.getElementsByTagName(HtmlAnchor.TAG_NAME);
				DomNodeList<DomNode> liqTRList = liqForm.querySelectorAll("table>tbody>tr:not(.cabecera)");
				
				for (int h = 0; h < liqAnchors.size(); h++) {
					String liquidationType = Toolkit.removeWeirdCharacters(((HtmlTableRow)liqTRList.get(h)).getCells().get(3).getVisibleText());
					Map<String,Map<Period, Map<String, Calc>>> nafMap = new LinkedHashMap<String,Map<Period, Map<String, Calc>>>();
					htmlPage = liqAnchors.get(h).click();
					htmlPage = htmlPage.getElementByName("SPM.ACC.CONSULTA_TRABAJADORES").click();
					
					for(String naf : nafs) {
						HtmlForm nafForm = (HtmlForm) htmlPage.getElementById("idFormularioSeleccionPorNAF");
						
						if ( nafForm != null ) {
							DomNodeList<DomNode> nafRows = nafForm.querySelectorAll("tbody tr:not([class='cabecera'])");
	
							if(!nafRows.isEmpty()) {
								Optional<DomNode> nafRow = nafRows.stream().filter(node -> (Toolkit.removeWeirdCharacters(((HtmlTableRow)node).getCell(0).getVisibleText()).equalsIgnoreCase(naf))).findFirst();
								if(nafRow.isPresent()) {
									HtmlTableRow nafTableRow = (HtmlTableRow) nafRow.get();
									htmlPage = nafTableRow.getCell(0).getElementsByTagName(HtmlAnchor.TAG_NAME).getFirst().click();
								}
								else {
									System.out.println("NOT FOUND : " + naf + " " + liquidationType);
									continue;
								}
							}
						}
						else {
							HtmlInput nafInput = (HtmlInput) htmlPage.getElementByName("NAF_TRABAJADOR");
							nafInput.setValue(naf);
							htmlPage = htmlPage.getElementByName("SPM.ACC.CONSULTAR").click();
						}
						
						if (htmlPage.querySelector("li[title='Error']") == null) {

							Map<Period, Map<String, Calc>> periods = new LinkedHashMap<>();
							
							HtmlForm nafMonthForm = (HtmlForm) htmlPage.getElementById("idformularioDatosGlobalesTrabajadorMes");
							if ( nafMonthForm == null ) {
								System.out.println("ERROR: " + naf);
								continue;
							}
								
							HtmlTableBody firstTable = nafMonthForm.querySelector("table>tbody");
							
							if ( firstTable == null ) {
								htmlPage = htmlPage.getElementById("paginaVolver").click();
								liqForm = (HtmlForm) htmlPage.getElementById("idFormularioSeleccionLiquidaciones");
								continue; //CR61(7106) .
							}
							 
							DomNodeList<DomNode> trList = firstTable.querySelectorAll("tr:not(.cabecera)");
							
							LinkedHashMap<String, Calc> employeeCalcs = new LinkedHashMap<>();
							
							trList.forEach(trNode -> {
								HtmlTableRow tr = (HtmlTableRow) trNode;
								List<HtmlTableCell> cells = tr.getCells();
								
								String description = Toolkit.removeWeirdCharacters(cells.get(0).getVisibleText());
								
								Double base = getDoubleValue(cells.get(1));
								Double enterprise = getDoubleValue(cells.get(2));
								Double employee = getDoubleValue(cells.get(3));
								Double total = getDoubleValue(cells.get(4));
								
								Calc calc = new Calc()
										.setBase(base)
										.setTotal(total)
										.setEmployee(employee)
										.setEnterprise(enterprise)
										;
								
								employeeCalcs.put(description, calc);
								
							});
							
							periods.put(null, employeeCalcs);

							htmlPage = htmlPage.getElementByName("SPM.ACC.RELACION_TRAMOS").click();

							
							DomNodeList<DomNode> anchors = htmlPage.querySelectorAll("a[href*=\"CALCULOS_TRAMO\"]");
							
							for (int i=0; i<anchors.size(); i++) {
								
								HtmlAnchor rad = (HtmlAnchor) anchors.get(i);
								
								HtmlTableRow tableRow = getParentHtmlTableRow(rad);
								Double quoteDays = getDoubleValue(tableRow.getCell(3));
								Double hours = getDoubleValue(tableRow.getCell(4));
								Double baseCC = getDoubleValue(tableRow.getCell(5));
								Double baseAT = getDoubleValue(tableRow.getCell(6));
								
								String fromDateStr  = getStringValue(tableRow.getCell(1));
								String toDateStr  = getStringValue(tableRow.getCell(2));
								
								htmlPage = rad.click();
								
								DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
								Date fromDate = null;
								try {
									fromDate = df.parse(fromDateStr);
								} catch (ParseException e1) {
									
								}
								Date toDate = null;
								try {
									toDate = df.parse(toDateStr);
								} catch (ParseException e1) {
									
								}
								
								Period period = 
								new Period(fromDate, toDate)
								.setHours(hours)
								.setBaseAT(baseAT)
								.setBaseCC(baseCC)
								.setQuoteDays(quoteDays)
								;
								
								firstTable = htmlPage.querySelector("table>tbody");
								trList = firstTable.querySelectorAll("tr:not(.cabecera)");
								
								LinkedHashMap<String, Calc> calcs = new LinkedHashMap<String, Calc>();
								
								trList.forEach(trNode -> {
									HtmlTableRow tr = (HtmlTableRow) trNode;
									List<HtmlTableCell> cells = tr.getCells();
									
									if ( cells.size() < 7 ) 
										return;
									
									String description = Toolkit.removeWeirdCharacters(cells.get(0).getVisibleText());
									
									Double base = getDoubleValue(cells.get(1));
									Double enterprisePercent = getDoubleValue(cells.get(2));
									Double enterprise = getDoubleValue(cells.get(3));
									Double employeePercent = getDoubleValue(cells.get(4));
									Double employee = getDoubleValue(cells.get(5));
									Double total = getDoubleValue(cells.get(6));
									
									Calc calc = new Calc()
											.setBase(base)
											.setTotal(total)
											.setEmployee(employee)
											.setEnterprise(enterprise)
											.setEmployeePercent(employeePercent)
											.setEnterprisePercent(enterprisePercent)
											;
									calcs.put(description, calc);
									
								});
								
								if ( calcs.isEmpty() ) {
									calcs.putAll(employeeCalcs);
								}
								
								periods.put(period, calcs);
								
								htmlPage = htmlPage.getElementById("paginaVolver").click();
								anchors = htmlPage.querySelectorAll("a[href*=\"CALCULOS_TRAMO\"]");
							}
							nafMap.put(naf, periods);
							callback.accept(liquidationType, naf, periods);
							
							Page page = htmlPage.getElementById("paginaVolver").click();
							if ( page.isHtmlPage() ) {
								htmlPage = (HtmlPage ) page;
							}
							
							page = htmlPage.getElementById("paginaVolver").click();
							if ( page.isHtmlPage() ) { 
								htmlPage = (HtmlPage ) page;
							}
							 
							liqForm =(HtmlForm) htmlPage.getElementById("idFormularioSeleccionLiquidaciones");
						}
						
					}
					
					ret.put(liquidationType, nafMap);
					htmlPage = htmlPage.getElementById("paginaVolver").click();
					htmlPage = htmlPage.getElementById("paginaVolver").click();
					liqForm =(HtmlForm) htmlPage.getElementById("idFormularioSeleccionLiquidaciones");
					liqAnchors = liqForm.getElementsByTagName(HtmlAnchor.TAG_NAME);
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
			throw new SegSocialException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CertificateNotFoundException();
		} catch (ElementNotFoundException e) {
			if(e.getAttributeValue() != null && e.getAttributeValue().length() == 4) {
				try {
					Integer.parseInt(e.getAttributeValue());
					throw new InvalidDateException(e.getAttributeValue());
				} catch (NumberFormatException e1) {
				}
			}
			throw new solutions.aon.seg.social.exception.ElementNotFoundException(e.getMessage());
		} catch ( Exception e ) {
			e.printStackTrace();
			throw e;
		}
		return Collections.emptyMap();
	}


	private static String getStringValue(HtmlTableCell cell) {
		String str = Toolkit.removeWeirdCharacters(cell.getVisibleText());
		
		if ( str == null )
			return null;
		if ( str.isEmpty())
			return null;
		
		return str;
	}

	private static Double getDoubleValue(HtmlTableCell cell) {
		String regExp = "\\s*(-?(\\d*\\.?)*\\d+\\,?\\d*).*";
		String str = Toolkit.removeWeirdCharacters(cell.getVisibleText());
		str = str.replaceAll(regExp, "$1");
		
		if ( str == null )
			return null;
		if ( str.isEmpty())
			return null;
		
		str = str.replaceAll("\\.", "").replaceAll(",", ".");
		
		try {
			return Double.parseDouble(str);
		} catch ( NumberFormatException e) {
			return null;
		}
	}
	
	
	
	private static HtmlTableRow getParentHtmlTableRow(DomNode el) {
		for ( DomNode parent = el.getParentNode(); parent != null; parent = parent.getParentNode() ) {
			if ( HtmlTableRow.TAG_NAME.equalsIgnoreCase(parent.getLocalName())) {
				return (HtmlTableRow) parent;
			}
		}
		return null;
	}
	

	private static HtmlTableRow getParentHtmlTableRow(DomElement el) {
		for ( DomNode parent = el.getParentNode(); parent != null; parent = parent.getParentNode() ) {
			if ( HtmlTableRow.TAG_NAME.equalsIgnoreCase(parent.getLocalName())) {
				return (HtmlTableRow) parent;
			}
		}
		return null;
	}
	
	
	
}
