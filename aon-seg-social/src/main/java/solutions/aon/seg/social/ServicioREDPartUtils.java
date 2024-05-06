package solutions.aon.seg.social;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.ITPartPage;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.It.ItBuilder;
import solutions.aon.seg.social.object.ItPartId;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public abstract class ServicioREDPartUtils extends ServicioREDRegeXML {
	
	protected static ITPartPage getInfoPartsByXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		Map<Integer, ITPart> map = new HashMap<>();
		ITPartPage itPartPage = new ITPartPage().setData(map);
	
		DefaultHandler handler = new DefaultHandler() {
			private StringBuilder data;
			ITPart part = new ITPart();
			boolean bajaLogicaDesc;
			int position = 0;
			boolean bNss;
			boolean bTypePart;
			boolean bWorkLeaveDate;
			boolean bReceptionDate;	
			boolean bWorkRestartDate;
			boolean bConfirmationDate;
			boolean bPartNum;
			boolean bCcc;
		
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("ARQ.EstPag")) {
					String id = attributes.getValue("id");
					String indbot = attributes.getValue("INDBOT");
					
					boolean next = id!=null && indbot!=null 
							       && id.contains("ARQ.ESTPAG.IDPAGINACION_CONSULTA") 
							       && (indbot.equals("01") || indbot.equals("11"));
					
					itPartPage.setNext(next);
				} else if (qName.equalsIgnoreCase("DatosParte")) {
					part = new ITPart();
				}  else if (qName.equalsIgnoreCase("naf")) {
					bNss = true;
				}  else if (qName.equalsIgnoreCase("cccEmpresa")) {
					bCcc = true;
				} else if (qName.equalsIgnoreCase("fechaBaja")) {
					bWorkLeaveDate = true;
				} else if (qName.equalsIgnoreCase("fechaRecepcion")) {
					bReceptionDate = true;
				}  else if (qName.equalsIgnoreCase("fechaAlta")) {
					bWorkRestartDate = true;
				} else if (qName.equalsIgnoreCase("tipoParteDesc")) {
					bTypePart = true;
				} else if (qName.equalsIgnoreCase("bajaLogicaDesc")) {
					bajaLogicaDesc = true;
				} else if (qName.equalsIgnoreCase("fechaParteConf")) { // descripcionCausaAlta
					bConfirmationDate = true;
				} else if (qName.equalsIgnoreCase("numParteConf")) { // descripcionCausaAlta
					bPartNum = true;
				}
		
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				 if (bajaLogicaDesc) {
					boolean isAnulado = data.indexOf("S")>-1;
					if(!isAnulado) 
						map.put(position, part);

					bajaLogicaDesc = false;
					position++;
				} else if (bNss) {
					part.setNaf(data.toString());
					bNss = false;
				} else if (bCcc) {
					part.setCcc(data.toString());
					bCcc = false;
				}else if (bTypePart) {
					part.setPartType(data.toString());
					bTypePart = false;
				}  else if (bWorkLeaveDate) {
					part.setWorkLeaveDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkLeaveDate = false;
				} else if (bReceptionDate) {
					part.setReceptionDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bReceptionDate = false;
				}  else if (bWorkRestartDate) {
					part.setWorkRestartDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkRestartDate = false;
				}  else if (bConfirmationDate) {
					part.setConfirmationDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bConfirmationDate = false;
				} else if (bPartNum) {
					part.setPartNum(Integer.parseInt(data.toString()));
					bPartNum = false;
				} 
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		newInstanceXml(xml, handler);
		
		return itPartPage;
	}
	
	protected static void setInfoPart(String xml, ITPart part) throws ParserConfigurationException, SAXException, IOException {
		DefaultHandler handler = new DefaultHandler() {
			StringBuilder data;
			boolean dataEmployee;
			boolean bIpf;
			boolean bNss;
			boolean bEmployeeName;
			boolean bEmployeeDirection;
			boolean bTypePart;
			boolean bContingency;
			boolean bWorkLeaveDate;
			boolean bReceptionDate;
			boolean bWorkRestartDate;
			boolean bCauseRestart;
			boolean bCcc;
			boolean bRelapse;
			boolean bBaseCtz;
			boolean bSumBCtz;
			boolean bHoursCtzExtr;
			boolean bHoursCrzOther;
			boolean bCollegiateNumber;
			boolean bTypeCto;
			boolean bGpCtz;
			boolean bDaysCtz;
			boolean bLack;
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("DatosTrabajador")) {
					dataEmployee = true;
				} else if (qName.equalsIgnoreCase("certificado_entidad")) {
					dataEmployee = false;
				} else if (dataEmployee) {
					if(qName.equalsIgnoreCase("ipf")) {						
						bIpf = true;
					} else if(qName.equalsIgnoreCase("naf")) {						
						bNss = true;
					} else if(qName.equalsIgnoreCase("nombre")) {						
						bEmployeeName = true;
					} else if(qName.equalsIgnoreCase("direccion")) {						
						bEmployeeDirection = true;
					} else if (qName.equalsIgnoreCase("cccEmpresa")) {
						bCcc = true;
					} 
				} else if (qName.equalsIgnoreCase("tipoParteDesc")) {
					bTypePart = true;
				} else if (qName.equalsIgnoreCase("contingencia")) { // contingenciaDesc
					bContingency = true;
				} else if (qName.equalsIgnoreCase("fechaBaja")) {
					bWorkLeaveDate = true;
				} else if (qName.equalsIgnoreCase("fechaRecepcion")) {
					bReceptionDate = true;
				}  else if (qName.equalsIgnoreCase("fechaAlta")) {
					bWorkRestartDate = true;
				} else if (qName.equalsIgnoreCase("causaAlta")) { // descripcionCausaAlta
					bCauseRestart = true;
				} else if (qName.equalsIgnoreCase("recaidaDesc")) { 
					bRelapse = true;
				}  else if (qName.equalsIgnoreCase("baseCotizacion")) { 
					bBaseCtz = true;
				} else if (qName.equalsIgnoreCase("sumaBaseCotizacion")) { 
					bSumBCtz = true;
				} else if (qName.equalsIgnoreCase("cotizacionHorasExtra")) { 
					bHoursCtzExtr = true;
				} else if (qName.equalsIgnoreCase("cotizacionOtrosConceptos")) { 
					bHoursCrzOther = true;
				} else if (qName.equalsIgnoreCase("numColegiado")) { 
					bCollegiateNumber = true;
				} else if (qName.equalsIgnoreCase("tipoContrato")) { 
					bTypeCto = true;
				} else if(qName.equalsIgnoreCase("grupoCotizacion")) { 
					bGpCtz = true;
				} else if(qName.equalsIgnoreCase("diasCotizados")) { 
					bDaysCtz = true;
				} else if(qName.equalsIgnoreCase("carencia")) { 
					bLack = true;
				}
				
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bIpf) {
					part.setIpf(data.toString());
					bIpf = false;
				} else if (bNss) {
					part.setNaf(data.toString());
					bNss = false;
				} else if (bCcc) {
					part.setCcc(data.toString());
					bCcc = false;
				} else if (bEmployeeName) {
					part.setNameEmployee(data.toString());
					bEmployeeName = false;
				} else if (bEmployeeDirection) {
					part.setDirectionEmployee(data.toString());
					bEmployeeDirection = false;
				} else if (bTypePart) {
					part.setPartType(data.toString());
					bTypePart = false;
				}  else if (bContingency) {
					part.setContingency(data.toString());
					bContingency = false;
				} else if (bWorkLeaveDate) {
					part.setWorkLeaveDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkLeaveDate = false;
				} else if (bReceptionDate) {
					part.setReceptionDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bReceptionDate = false;
				}  else if (bWorkRestartDate) {
					part.setWorkRestartDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkRestartDate = false;
				} else if (bCauseRestart) {
					part.setCauseRestart(data.toString());
					bCauseRestart = false;
				} else if (bRelapse) {
					part.setRelapse(!data.toString().contains("N"));
					bRelapse = false;
				} else if (bBaseCtz) {
					part.setBaseCtz(Toolkit.parseStringToFloat(data.toString()));
					bBaseCtz = false;
				} else if (bSumBCtz) {
					part.setSumBCtz(Toolkit.parseStringToFloat(data.toString()));
					bSumBCtz = false;
				} else if (bHoursCtzExtr) {
					part.setHoursCtzExtr(Toolkit.parseStringToFloat(data.toString()));
					bHoursCtzExtr = false;
				} else if (bHoursCrzOther) {
					part.setHoursCrzOther(Toolkit.parseStringToFloat(data.toString()));
					bHoursCrzOther = false;
				} else if (bCollegiateNumber) {
					part.setCollegiateNumber(Toolkit.noSpaces(data.toString()));
					bCollegiateNumber = false;
				} else if (bTypeCto) {
					part.setTypeCto(data.toString());
					bTypeCto = false;
				} else if (bGpCtz) {
					part.setGpCtz(data.toString());
					bGpCtz = false;
				} else if (bDaysCtz) {
					part.setDaysCtz(parseInt(data.toString()));
					bDaysCtz = false;
				} else if (bLack) {
					part.setLack(parseInt(data.toString()));
					bLack = false;
				}
				
				part.setGpCtz(xml);
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		newInstanceXml(xml, handler);
	}
	
	protected static List<It> orderByIT(List<ITPart> itParts) {
		HashMap<ItPartId, List<ITPart>> orderedItParts = new HashMap<>();
		
		// Recorremos la lista de ITPart y agrupamos cada una en el mapa seg�n su identificador ItPartId
	    for (ITPart itp : itParts) {
	        Optional<Date> workLeaveDate = itp.getWorkLeaveDate();
	        Optional<String> naf = itp.getNaf();

	        // Si workLeaveDate y naf est�n presentes, a�adimos la ITPart al mapa con su identificador ItPartId
	        if (workLeaveDate.isPresent() && naf.isPresent()) {
	            ItPartId idPartId = new ItPartId(workLeaveDate.get(), naf.get());
	            orderedItParts.computeIfAbsent(idPartId, k -> new ArrayList<>()).add(itp);
	        }
	    }	    
	    
	    // Creamos una lista para almacenar las instancias de It que se creen a partir de las ITPart agrupadas
	    List<It> its = new ArrayList<>();
	    ItBuilder builder = new ItBuilder();

	    for (List<ITPart> values : orderedItParts.values()) {
	    	ArrayList<ITPart> confirmations = new ArrayList<>();
			ITPart end = null;
			ITPart start = null;
		
			// Recorremos la lista de ITPart y buscamos las ITPart de inicio, confirmaci�n y fin
	        for (ITPart itp : values) {
	        	start = itp;
//	            String partType = itp.getPartType().toLowerCase();
//	            if (partType.contains("baja") || partType.contains("pb")) {
//	                start = itp;
//	            } else if ((partType.contains("confirmaci\u00f3n") || partType.contains("pc")) && !confirmations.contains(itp)) {
//	                confirmations.add(itp);
//	            } else if (partType.contains("alta") || partType.contains("pa")) {
//	                end = itp;
//	            }
	         }
			
	        // Si se ha encontrado una ITPart de inicio, creamos una instancia de It a partir de las ITPart encontradas
			if(null!=start) {
				Optional<String> typeProcess = start.getTypeProcess();
				if (end == null && typeProcess.isPresent() && typeProcess.get().toLowerCase().contains("muy corto")) {
					ITPart tmp = new ITPart();
					tmp.setReceptionDate(start.getReceptionDate());
					tmp.setCauseRestart("6 Mejor\u00EDa permite trabajar");
					tmp.setPartType("Alta");
					start.getNaf().ifPresent(tmp::setNaf);
					start.getWorkLeaveDate().ifPresent(workDate->{
						tmp.setWorkLeaveDate(workDate);
						tmp.setWorkRestartDate(Toolkit.addDays(workDate, 1));
					});
					end = tmp;
				}
				
				its.add(builder.setStart(start).setConfirmations(confirmations).setEnd(end).build());
			}
		}
		return its.stream().sorted((o1, o2)-> o1.getStart().getWorkLeaveDate().get().compareTo(o2.getStart().getWorkLeaveDate().get())).collect(Collectors.toList());
	}
	
	
	
	protected static HtmlPage setUrlParseRemoveXml(HtmlPage htmlPage, HtmlAnchor link) throws IOException {
		link = HtmlUnitToolkit.setUrlParse(htmlPage, link);
		link.setAttribute("href", link.getHrefAttribute().replace("&ARQ.SPM.OUT=XML_STYLESHEET", ""));
		htmlPage = link.click();
		return htmlPage;
	}
	
}
