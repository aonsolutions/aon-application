package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.object.Liquidation.LiquidationBuilder;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.object.SecondaryUser.SecondaryUserBuilder;
import solutions.aon.seg.social.object.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.object.WorkerLiquidation.WorkerLiquidationBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

public abstract class ServicioREDRegeXML {
	protected static final String DATE_FORMAT = "dd/MM/yyyy";
	protected static final String DATE_FORMAT_DASHES = "dd-MM-yyyy";
	/**
	 * Not reliable, it sometimes does not pick up some values properly
	 * @param xml The xml String
	 * @return a map containing all fields with id and their innerTexts
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Deprecated
	protected static Map<String, String> extractSituacionEmpresaInfo (String xml) throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, String> values = new HashMap<String, String>();
		
		DefaultHandler handler = new DefaultHandler() {
			String id = null;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (attributes.getValue("id") != null) {
					id = attributes.getValue("id");
				}
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				id = null;
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (id != null) {
					String value = new String(ch, start, length);
					if (id.equals("SDFFECHASIT")) {
						System.out.println();
						System.out.println(new String(ch, 0, ch.length));
						System.out.println(value);
						System.out.println();
					}
					value = value != null ? Toolkit.removeNBSP(value) : null;
					value = value != null ? value.trim() : value;
					values.put(id, value);
				}
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return values;
	}
	
	protected static List<Employee> extractIpfXNafInfo (String xml) throws SegSocialException, ParserConfigurationException, SAXException, IOException {
		List<Employee> employees = new LinkedList<>();
		
		DefaultHandler handler = new DefaultHandler() {
			private StringBuilder data;
			EmployeeBuilder employeeBuilder;
			String errMsg = null;
			boolean bNss;
			boolean bIpf;
			boolean bName;
			boolean bMessage;
			boolean bTipo;
			boolean bTexto;
			boolean empty;
			boolean error;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("TRABAJADOR")) {
					empty = true;
					employeeBuilder = new EmployeeBuilder();
				} else if (qName.equalsIgnoreCase("NA5NumSegSocialCompleto")) {
					bNss = true;
				} else if (qName.equalsIgnoreCase("IP9NumDoc")) {
					bIpf = true;
				} else if (qName.equalsIgnoreCase("NOMBRE_COMPLETO")) {
					bName = true;
				} else if (qName.equalsIgnoreCase("MESSAGE")) {
					bMessage = true;
				} else if (bMessage && qName.equalsIgnoreCase("TIPO")) {
					bTipo = true;
				} else if (bMessage && qName.equalsIgnoreCase("TEXTO")) {
					bTexto = true;
				}
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bNss) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					employeeBuilder.setNss(data.toString());
					bNss = false;
				} else if (bIpf) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					employeeBuilder.setIpf(data.toString());
					bIpf = false;
				} else if (bName) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					String name = data.toString();
					name = name != null ? name.trim() : null;
					employeeBuilder.setName(name);
					bName = false;
				} else if (bTipo) {
					error = data.toString() != null && data.toString().equalsIgnoreCase("ERROR");
					bTipo = false;
				} else if (bTexto) {
					errMsg = data.toString();
					bTexto = false;
				}
				if (qName.equalsIgnoreCase("TRABAJADOR") && !empty) {
					employees.add(employeeBuilder.build());
				} else if (qName.equalsIgnoreCase("MESSAGE")) {
					bMessage = false;
					if (error) {
						throw new SAXException(errMsg != null ? errMsg : "");
					}
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return employees;
	}
	
	protected static List<SecondaryUser> extractSecondaryUsers(String xml) throws SegSocialException, ParserConfigurationException, SAXException, IOException {
		List<SecondaryUser> users = new LinkedList<>();
		
		DefaultHandler handler = new DefaultHandler() {
			private StringBuilder data;
			SecondaryUserBuilder builder;
			boolean empty;
			boolean error;
			boolean bSituation;
			boolean tbaSituation;
			boolean bNaf;
			boolean bName;
			boolean bFecha;
			boolean bIpf;
			boolean bPhone;
			boolean bMobile;
			boolean bFax;
			boolean bProvince;
			boolean bMail;
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("usuario")) {
					empty = true;
					builder = new SecondaryUserBuilder();
				} else if (qName.equalsIgnoreCase("tbaSituacionUsuario")) {
					tbaSituation = true;
				} else if (tbaSituation && qName.equalsIgnoreCase("descripcion")) {
					bSituation = true;
				} else if (qName.equalsIgnoreCase("naf")) {
					bNaf = true;
				} else if (qName.equalsIgnoreCase("nombreApellidos")) {
					bName = true;
				} else if (qName.equalsIgnoreCase("fechaSituacion")) {
					bFecha = true;
				} else if (qName.equalsIgnoreCase("IP3DocumentoAutorizado")) {
					bIpf = true;
				} else if (qName.equalsIgnoreCase("telefono")) {
					bPhone = true;
				} else if (qName.equalsIgnoreCase("telefonoMovil")) {
					bMobile = true;
				} else if (qName.equalsIgnoreCase("fax")) {
					bFax = true;
				} else if (qName.equalsIgnoreCase("provincia")) {
					bProvince = true;
				} else if (qName.equalsIgnoreCase("mail")) {
					bMail = true;
				} 
				
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bSituation) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setSituation(data.toString());
					bSituation = false;
					tbaSituation = false;
				} else if (bNaf) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setNaf(data.toString().replace(" ", ""));
					bNaf = false;
				} else if (bName) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setName(data.toString());
					bName = false;
				} else if (bFecha) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setSituationDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bFecha = false;
				} else if (bIpf) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setIpf(data.toString());
					bIpf = false;
				} else if (bPhone) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setTelephone(data.toString());
					bPhone = false;
				} else if (bMobile) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setMobile(data.toString());
					bMobile = false;
				} else if (bFax) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setFax(data.toString());
					bFax = false;
				} else if (bProvince) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setProvince(data.toString());
					bProvince = false;
				} else if (bMail) {
					if (data.toString() != null && !data.toString().isEmpty())
						empty = false;
					builder.setMail(data.toString());
					bMail = false;
				} else if (qName.equalsIgnoreCase("usuario")) {
					users.add(builder.build());
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return users;
	}
	
	protected static void checkErrors(String xml) throws SegSocialException, ParserConfigurationException, SAXException, IOException {
		DefaultHandler handler = new DefaultHandler() {
			StringBuilder data;
			boolean error;
			boolean bTextoError;

			String errorText = null;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (error && qName.contentEquals("TEXTO")) {
					bTextoError = true;
				} else if (bTextoError) {
					errorText = data.toString();
					bTextoError = false;
				}
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (data.toString().equalsIgnoreCase("ERROR")) {
					error = true;
				} else if (error && qName.contentEquals("MESSAGE")) {
					throw new SAXException(errorText != null ? errorText : "");
				}		
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
	}
	
	protected static List<NameValuePair> extractSecondaryFormValues(String xml) throws SegSocialException, ParserConfigurationException, SAXException, IOException {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
		params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
		DefaultHandler handler = new DefaultHandler() {
			StringBuilder data;
			String session;
			String urlFrom;
			boolean dataABM;
			boolean dataES;
			boolean bTicket;
			boolean bCodeSituation;
			boolean bCodeProvince;
			boolean textProvince;
			boolean bSession;
			boolean bUrl;
			boolean urlFull;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("SPM.TICKET")) 
					bTicket = true;
				else if (qName.equalsIgnoreCase("datosABM")) 
					dataABM = true;
				else if (qName.equalsIgnoreCase("datosEntradaSalida")) 
					dataES = true;
				else if (dataABM && qName.equalsIgnoreCase("codigoStr"))  //mal
					bCodeSituation = true;
				else if (qName.equalsIgnoreCase("provincia"))  //mal
					bCodeProvince = true;
				else if (qName.equalsIgnoreCase("SPM.IDSESSION")) 
					bSession = true;
				else if (qName.equalsIgnoreCase("SPM.URLFRONTEND")) 
					bUrl = true;
				else if (qName.equalsIgnoreCase("desProvincia")) 
					textProvince = true;
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bTicket) {
					params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, data.toString()));
					bTicket = false;
				} else if (bCodeSituation && dataABM) {
					params.add(new BasicNameValuePair("seleccionSituacion", data.toString()));
					bCodeSituation = false;
					dataABM = false;
				} else if (bCodeProvince && dataES) {
					params.add(new BasicNameValuePair("seleccionProvincia", data.toString()));
					bCodeProvince = false;
					dataES = false;
				} else if (textProvince) {
					params.add(new BasicNameValuePair("ARQ_descseleccionProvincia", data.toString()));
					textProvince = false;
				} else if (bSession) {
					session = data.toString();
					bSession = false;
				} else if (bUrl) {
					urlFrom = data.toString();
					bUrl = false;
				} 
				if(session!=null && urlFrom!=null && !urlFull) {
					params.add(new BasicNameValuePair("url", urlFrom+session));
					urlFull = true;
				} 
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return params;
	}
	
	protected static Employee extractNafXIpfInfo (String xml) throws SegSocialException, ParserConfigurationException, SAXException, IOException {
		//builder.setNss(nss).setName(name).setIpf(ipf1).setIdent(Integer.parseInt(ident1));
		EmployeeBuilder employeeBuilder = new EmployeeBuilder();
		DefaultHandler handler = new DefaultHandler() {
			StringBuilder data;
			boolean bNss;
			boolean bIpf;
			boolean bName;
			boolean bCodeType;
			boolean bTipo;
			boolean bTextoError;
			boolean error;
			String errorText = null;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("NA5NumSegSocialCompleto")) {
					bNss = true;
				} else if (qName.equalsIgnoreCase("IP6NUMERO_DOCUMENTO")) {
					bIpf = true;
				} else if (qName.equalsIgnoreCase("NOMBRE_COMPLETO")) {
					bName = true;
				} else if (qName.contentEquals("CODIGO_TIPO")) {
					bCodeType = true;
				} else if (qName.contentEquals("TIPO")) {
					bTipo = true;
				} else if (error && qName.contentEquals("TEXTO")) {
					bTextoError = true;
				}
					data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bNss) {
					employeeBuilder.setNss(data.toString());
					bNss = false;
				} else if (bIpf) {
					employeeBuilder.setIpf(data.toString());
					bIpf = false;
				} else if (bName) {
					employeeBuilder.setName(data.toString());
					bName = false;
				} else if (bCodeType) {
					try {						
						employeeBuilder.setIdent(Integer.parseInt(data.toString()));
					} catch (NumberFormatException e) {
					} finally {
						bCodeType = false;
					}
				} else if (bTipo) {
					if (data.toString().equalsIgnoreCase("ERROR")) {
						error = true;
					}
					bTipo = false;
				} else if (bTextoError) {
					errorText = data.toString();
					bTextoError = false;
				}
				
				if (error && qName.contentEquals("MESSAGE")) {
					throw new SAXException(errorText != null ? errorText : "");
				}
				
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return employeeBuilder.build();
	}
	
	protected static void fillManagementData (SituacionEmpresaBuilder seb, Map<String, String> values) {
		String sdfEmpresario3 = values.get("SDFEMPRESARIO3");
		sdfEmpresario3 = sdfEmpresario3 != null && sdfEmpresario3.length() > 9 ?
				Toolkit.removeExtraZeros(values.get("SDFEMPRESARIO3")) : sdfEmpresario3;
				
		seb.setCcc(values.get("SDFCPROVINCIA3") + values.get("SDFCNISS3"))
		.setIdEmpresario(values.get("SDFTIPO3"))
		.setNifempresa(sdfEmpresario3)
		.setRegimen(values.get("SDFREGIMEN3"))
		.setNss(values.get("SDFPRONAF3") + values.get("SDFNUMNAF30"))
		.setCccp(values.get("SDFPROVINCIACP3"))
		.setUgtgss(values.get("SDFTESORERIA3") + values.get("SDFADMON3") + values.get("SDFURE3"))
		.setUgtgsscccp(values.get("SDFTESPPAL") + values.get("SDFADMPPAL") + values.get("SDFUREPPAL"))
		.setUgcentral(values.get("SDFUNIDAD"))
		.setOgism(values.get("SDFPROUGISM") + values.get("SDFLOCUGISM"))
		.setCccAnt(values.get("SDFPROVCANT3") + values.get("SDFNUMCANT3"))
		.setCccSuc(values.get("SDFPROSUC3") + values.get("SDFNUMSUC3") + values.get("SDFREGSUC3") + values.get("SDFCCOASUC3"))
		.setSit(values.get("SDFSITUACION3") + values.get("SDFTSITUACION3"))
		.setfSit(Toolkit.parseDate(values.get("SDFFECHASIT"), DATE_FORMAT))
		.setfAltaInicial(Toolkit.parseDate(values.get("SDFFECHAALTA"), DATE_FORMAT))
		.setTrabajadorAlta(Toolkit.strToInteger(values.get("SDFNROTRA3")))
		.setAltaPrTrab(Toolkit.parseDate(values.get("SDFFECHAALTA13"), DATE_FORMAT))
		.setUltBajaEfCot(Toolkit.parseDate(values.get("SDFFECHABAJA93"), DATE_FORMAT))
		.setTrl(values.get("SDFTIPCON") + values.get("SDFDESTIPCON"))
		.setcEspNum(values.get("SDFCOLECTIVO3"))
		.setcEspCad(values.get("SDFDESCOL3"))
		.setCnae09Num(values.get("SDFACTIV093"))
		.setCnae93Num(values.get("SDFACTIV933"))
		.setCnae09Cad(values.get("SDFTACTIV093"))
		.setCnae93Cad(values.get("SDFTACTIV933"))
		.setTa2Alta(Toolkit.strToInteger(values.get("SDFCCONDIAS3")))
		.setTa2Baja(Toolkit.strToInteger(values.get("SDFCCONDPPB3")))
		.setTiposATyEPIT(Toolkit.strToFloat(values.get("SDFTITN12")))
		.setIms(Toolkit.strToFloat(values.get("SDFTIMSN12")))
		.setTotal(Toolkit.strToFloat(values.get("SDFTTOTN12")))
		.setCoeJubNum(values.get("SDFCOREJU"))
		.setCoeJubCad(values.get("SDFDSCOREJU"))
		.setAconExtra(values.get("SDFACTMEXTR") + values.get("SDFDSACONT"))
		.setEscTaller(Toolkit.toBoolean(values.get("SDFCESTAL")))
		.setAutorizacionRed(values.get("SDFAUTORIDRED"))
		.setPlazoIncorpRed(Toolkit.parseDate(values.get("SDFPLAZORED"), DATE_FORMAT))
		.setFechaAutCan(Toolkit.parseDate(values.get("SDFFECHAAUTRED"), DATE_FORMAT));
			
	}
	
	protected static void fillIdentifyingData (SituacionEmpresaBuilder seb, Map<String, String> values) {
		seb.setAnagrama(values.get("SDFANAGR3"))
		.setEmbarcacion(values.get("SDFTIPEMB") + values.get("SDFEMB") + values.get("SDFNOMEMBAR"))
		.setTlfMovil(values.get("SDFTELMOVIL3"))
		.setTlfFijo(values.get("SDFTELFIJO3"))
		.setEmail(values.get("txtconcat1_1"))
		.setNotifDomEmpresa(Toolkit.toBoolean(values.get("SDFNOTIF3")))
		.setTipoViaDirEmpresa(values.get("SDFVIA3"))
		.setDirEmpCalle(values.get("SDFDOMICILIO3"))
		.setDirEmpNum(values.get("SDFNUMERO3"))
		.setDirEmpBis(values.get("SDFBIS3"))
		.setDirEmpBloq(values.get("SDFBLOQUE3"))
		.setDirEmpEs(values.get("SDFESCALERA3"))
		.setDirEmpPiso(values.get("SDFPISO3"))
		.setDirEmpP(values.get("SDFPUERTA3"))
		.setDirEmpCP(values.get("SDFPOSTAL3"))
		.setDirEmpNumMuni(values.get("SDFLOCALIDAD13"))
		.setDirEmpNomMuni(values.get("SDFLOCALIDAD23"))
		.setDirEmpTlf(values.get("SDFNUM9TELEFONO3"))
		.setNotifDomActividad(Toolkit.toBoolean(values.get("SDFNOTIF4")))
		.setActUgtgss(values.get("SDFTESORERIA3") + values.get("SDFADMON3") + values.get("SDFURE3"))
		.setTipoViaDirActividad(values.get("SDFVIA4"))
		.setDirActCalle(values.get("SDFDOMICILIO4"))
		.setDirActNum(values.get("SDFNUMERO3"))
		.setDirActBis(values.get("SDFBIS4"))
		.setDirActBloq(values.get("SDFBLOQUE4"))
		.setDirActEs(values.get("SDFESCALERA4"))
		.setDirActPiso(values.get("SDFPISO4"))
		.setDirActP(values.get("SDFPUERTA4"))
		.setDirActCP(values.get("SDFPOSTAL4"))
		.setDirActNumMuni(values.get("SDFMUNICIPIO4"))
		.setDirActNomMuni(values.get("SDFLOCALIDAD4"))
		.setDirActTlf(values.get("SDFNUM9TELEFONO4"));
	}
	
	protected static void liquidationDataType(LiquidationBuilder lb, Collection<String> trs) throws SegSocialException {
		for (String tr : trs) {
			List<String> rows = Toolkit.getTdsTexts(tr);
			String rowConcept=Toolkit.safeRemoveWeirdCharacters(Toolkit.safeGet(rows, 0));
			if (rowConcept != null) {
				if(rowConcept.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
					Float nmbr1= Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setCcBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setCcBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setCcWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setCcTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setCcLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setCcLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setCcLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setCcLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setItWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setItWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setItWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setItWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setImsWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setImsWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setImsWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setImsWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setWorkAccidentLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setWorkAccidentLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setWorkAccidentLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setWorkAccidentLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("OTRAS COTIZACIONES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setOtherContributionsBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setOtherContributionsBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setOtherContributionsWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setOtherContributionsTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setOtherContributionsLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setOtherContributionsLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setOtherContributionsLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setOtherContributionsLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setTotalLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setTotalLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setTotalLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setTotalLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("BONIF.Y SUBVENC.CON CARGO AL INEM")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setGrantsAndBonusesBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setGrantsAndBonusesBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setGrantsAndBonusesWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setGrantsAndBonusesTotalFee(nmbr4);
				}
			}
						
		}
		
	}
	
	
	
	
	public static void workerLiquidationDataType(WorkerLiquidationBuilder wlb, Collection<String> trs) throws SegSocialException {
		
		for (String tr : trs) {
			List<String> rows = Toolkit.getTdsTexts(tr);
			String rowConcept=Toolkit.safeRemoveWeirdCharacters(Toolkit.safeGet(rows, 0));
			if (rowConcept != null) {
				
				if(rowConcept.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setCcDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setCcBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setCcBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setCcWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setCcTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setCcLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setCcLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setCcLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setCcLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setCcLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setItWorkAccidentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setItWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setItWorkAccidentusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setItWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setItWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setImsWorkAccidentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setImsWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setImsWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setImsWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setImsWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setWorkAccidentLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setWorkAccidentLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setWorkAccidentLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setWorkAccidentLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setWorkAccidentLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("DESEMPLEO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setUnemploymentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setUnemploymentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setUnemploymentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setUnemploymentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setUnemploymentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("FOGASA")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setFogasaDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setFogasaBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setFogasaBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setFogasaWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setFogasaTotalFee(nmbr4);
				}
				else if(rowConcept.toUpperCase().contains("FORMACI") && rowConcept.toUpperCase().contains("N PROFESIONAL")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setJobTrainingDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setJobTrainingBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setJobTrainingBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setJobTrainingWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setJobTrainingTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setOtherContributionsLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setOtherContributionsLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setOtherContributionsLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setOtherContributionsLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setOtherContributionsLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setTotalLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setTotalLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setTotalLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setTotalLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setTotalLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.contains("BONIF")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setGrantsAndBonusesDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setGrantsAndBonusesBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setGrantsAndBonusesBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setGrantsAndBonusesWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setGrantsAndBonusesTotalFee(nmbr4);
				}
				
				
			}
		}
		
	}

	public static void checkOldSsError(String body) throws SegSocialException {
		String error = Toolkit.getDIL(body);
		if (Toolkit.getErrCode(error) != null)
			InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
		else {
			String notAuthorized = Toolkit.getElementByAttribute(body, "class", "cuerpo_noautorizado");
			if(notAuthorized!=null) {
				notAuthorized = Toolkit.getElementByAttribute(body, "class", "cabMensaje");
				if(notAuthorized!=null && notAuthorized.toLowerCase().contains("no autorizado")) {
					InvalidDataException.checkCode(0, "Certificado no autorizado");
				}
			}
		}
	}

	
	public static void checkOldSsError(String body, Integer... exceptions) throws SegSocialException {
		String error = Toolkit.getDIL(body);
		if (Toolkit.getErrCode(error) != null) {
			if (exceptions == null || exceptions.length == 0 || !Arrays.stream(exceptions).anyMatch(code -> code.equals(Toolkit.getErrCode(error))))
				InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
		}
	}
	
	public static String removeSpaces(String text) {
		if (text == null || text.isEmpty())
			return text;
		else
			return text.replace(" ", "");
	}

	public static String emptyIfNull(String text) {
		if (text == null)
			return "";
		else
			return text;
	}
	
	public static Date parseDateWithDashes(String strDate) {
		if (strDate == null || strDate.isEmpty())
			return null;
		String d = strDate.trim();
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_DASHES);
		try {
			return df.parse(d);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parseDateWithSlashes(String strDate) {
		if (strDate == null || strDate.isEmpty())
			return null;
		String d = strDate.trim();
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		try {
			return df.parse(d);
		} catch (ParseException e) {
			return null;
		}
	}

	public static final String DEFAULT_ENCODING = IServicioRedConstants.ISO_8859_1;
	static List<Employee> getEmployeesFromTable(CloseableHttpClient httpClient, String body, String link,
			String sessionId, String regime, String ccc) throws SegSocialException, IOException {
		String dil = Toolkit.getDIL(body);
		boolean endOfData = false;
		List<Employee> employees = new ArrayList<>();
		while (!endOfData) {
			String table = Toolkit.getTable(body);
			Collection<String> trs = Toolkit.getTrs(table);
			for (String tr : trs) {
				List<String> tds = Toolkit.getTdsTexts(tr);
				if (tds != null && !tds.isEmpty()) {
					String nss = removeSpaces(tds.get(0));
					if (nss == null || nss.isEmpty())
						break;
					EmployeeBuilder builder = new EmployeeBuilder();
					String name = tds.get(1);
					Date date = parseDateWithDashes(tds.get(2));
					String situation = tds.get(3) != null && !tds.get(3).isEmpty() ? tds.get(3) : "AL";
					String ipf = Toolkit.removeExtraZeros(removeSpaces(tds.get(4)));
					builder.setNss(nss)
					.setName(name)
					.setFra(date)
					.setSituation(situation)
					.setIpf(ipf)
					.setCtaCti(ccc)
					.setRegime(regime);
					if(!situation.contains("AL")) 
						builder.setFrb(date);
				
					employees.add(builder.build());
				}
			}
			
			if (dil == null ||(dil != null && dil.contains("3252")))
				endOfData = true;
			else {
				HttpPost httpPost = new HttpPost(link);
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.LIBAFCON, IServicioRedConstants.LIBAFCON));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6202"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair("btn_Sub2207801001", "Pág.+Sig."));
				httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				checkOldSsError(body);
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
				dil = Toolkit.getDIL(body);
			}
		}	
		
		return employees;
	}

	static Collection<Employee> getEmployeesCommon(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc, boolean prev)
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
			params.add(new BasicNameValuePair("chk_chkgrupo1_" + (prev ? "2" : "1"), "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODING));
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			return getEmployeesFromTable(httpClient, body, link, sessionId, regime, ccc);
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
	}

	public static String identity(String ipf) {
		ipf = Toolkit.removeExtraZeros(ipf);
		Pattern nif  = Pattern.compile(
				//  -------- LEGAL_PERSON_NIF PATTERN  
				// -------- (1) --> X00000000
					"^[A-JUV]"
					+"[\\s-_/]?"
					+"[0-9]{2}"
					+"[-_/\\.]?"
					+"[0-9]{3}"
					+"[-_/\\.]?"
					+"[0-9]{3}$"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Pattern dni  = Pattern.compile(
					"[0-9]?"
					+"[0-9]"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/]?"
					+"[A-Z]"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				//  -------- NIE PATTERN 
				// -------- (1) --> X0000000X
		Pattern nie  = Pattern.compile(
					"[XYZ]"
					+"[\\s-_/]?"
					+"[0-9]{7}"
					+"[\\s-_/]?"
					+"[A-HJ-NP-TV-Z]"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Map<Pattern, Integer> patterns = new HashMap<Pattern, Integer>();
		patterns.put(nif, 1);
		patterns.put(dni, 1);
		patterns.put(nie, 6);
		
		String identity = "";
		for (Entry<Pattern, Integer> entry : patterns.entrySet()) {
			if ( entry.getKey().matcher(ipf).matches()) { identity = entry.getValue().toString(); break; }
		}
		return identity;
	}
	
	
	
	
}
